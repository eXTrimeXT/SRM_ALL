import Cookies from 'js-cookie'
import Sortable from 'sortablejs'
import './index.scss'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { systemUrl } from '@/config/sysConfig'
import { getMenuInfo } from '@/utils/menu-auth'

const EXPORT_TYPE = {
  CURRENT_PAGE: 'CURRENT_PAGE',
  ALL: 'ALL'
}

export default {
  name: 'ExportExcel',
  props: {
    propUrl: {
      type: String,
      default: null
    },
    exprotUrl: {
      type: String,
      default: null
    },
    isCustomUrl: { // 自定义url，如果是前端定义导出字段的时候而且url也需要自定义的时候，将后端自定的url放在这里就可以了 by 伟龙
      type: String,
      default: null
    },
    filterParams: {
      type: Object,
      default: () => { }
    },
    // 字段导出方式
    exportMode: {
      type: String,
      default: 'service' // front 前端定义导出字段 | service 服务端定义导出字段
    },
    tableHeader: {
      type: Array
    },
    pageUrl: {
      type: String
    },
    dictCodes: {
      type: Object,
      default: () => { }
    },
    timeout: {
      type: [Number, String],
      default: 350000
    },
    exportSize: { // 导出最大条数大小
      type: Number,
      default: 2000
    },
    title: {// 按钮名字
      type: String,
      default: ''
    },
    code: { // 权限控制按钮显示
      type: String
    },
    type: { // 按钮类型
      type: String,
      default: function () {
        return 'primary'
      }
    },
    baseUrl: { // 开发环境使用本地配置
      type: String,
      default: () => {
        return '$' + '{srm}' // systemUrl // config.baseUrl
      }
    },
    // 禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 多sheet
    multiple: {
      type: Boolean,
      default: false
    },
    // 头行明细配置
    // [ { tableHeader: [], pageUrl: '', dictCodes: {} } ]
    lineArray: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 导出方式
    exportType: {
      type: String,
      default: function () {
        return 'webApi' // webApi 传统接口 | meiqlApi meiql接口
      }
    },
    // meiql接口的列表查询payload
    generateMeiQLExportRequest: {
      type: Function
    },
    fileName: {
      type: String,
      default: function () {
        return this.$t(this.$route.meta.title) // 导出文件
      }
    }
  },
  data () {
    return {
      exportSelect: [
        {
          label: () => this.$t('customTable.currentPage'),
          value: EXPORT_TYPE.CURRENT_PAGE
        },
        { label: () => this.$t('customTable.all'), value: EXPORT_TYPE.ALL }
      ],
      // 配置项是否可见
      modalVisible: false,
      // 导出字段列表
      tableConfig: [],
      // 当前选中的条目
      currentSelectedRows: [],
      exprotType: EXPORT_TYPE.ALL,
      pageSize: 15,
      pageNum: 1,
      rules: {
        fileName: [
          { required: true, message: this.$t('customTable.filenameTip') }
        ],
        pageSize: [
          { required: true, message: this.$t('customTable.pageSizeTip') }
        ],
        pageNum: [
          { required: true, message: this.$t('customTable.pageNumTip') }
        ]
      },
      // 行配置
      lineConfig: [],
      fileNameValue: this.fileName
    }
  },
  watch: {
    tableConfig (n, o) {
      if (n && n.length) {
        this.$nextTick(() => {
          this.initSortable()
        })
      }
    },
    lineConfig: {
      handler (val) {
        this.$nextTick(() => {
          for (let i = 0; i < this.lineConfig.length; i++) {
            const lineItem = this.lineConfig[i]
            this.initSortableLine(lineItem)
          }
        })
      },
      deep: true
    }
  },
  methods: {
    initSortable () {
      if (this.multiple) {
        return
      }
      const tbody = document.querySelector(
        '.custom-table-dialog .el-table__body-wrapper tbody'
      )
      const _this = this
      Sortable.create(tbody, {
        animation: 180,
        delay: 0,
        onEnd ({ newIndex, oldIndex }) {
          const currRow = _this.tableConfig.splice(oldIndex, 1)[0]
          _this.tableConfig.splice(newIndex, 0, currRow)
        }
      })
    },
    initSortableLine (lineItem) {
      if (!this.multiple) {
        return
      }
      const tbody = document.querySelector(
        '.custom-table-dialog .custom-pane-' + lineItem.indexName + ' .el-table__body-wrapper tbody'
      )
      Sortable.create(tbody, {
        animation: 180,
        delay: 0,
        onEnd ({ newIndex, oldIndex }) {
          const currRow = lineItem.tableConfig.splice(oldIndex, 1)[0]
          lineItem.tableConfig.splice(newIndex, 0, currRow)
        }
      })
    },
    closeModal () {
      this.modalVisible = false
    },
    openModal () {
      this.fileNameValue = this.fileName
      // 前置事件 添加回调判断，如果存在回调就直接中断执行方法, 回调方法不能写异步
      let flag = true
      this.$emit('before-open', true, () => {
        flag = false
      })
      if (!flag) {
        return false
      }

      this.modalVisible = true
      this.pageSize = 15
      this.pageNum = 1
      this.exprotType = EXPORT_TYPE.ALL
      // 前端定义导出字段
      if (this.exportMode === 'front') {
        if (this.multiple) {
          if (this.lineArray && this.lineArray.length > 0) {
            const tableConfigLineArray = []
            for (let lineIndex = 0; lineIndex < this.lineArray.length; lineIndex++) {
              const lineItem = this.lineArray[lineIndex]
              let lineHeader = lineItem.tableHeader

              if (!lineItem.pageUrl) {
                this.$message.error(this.$t('components.eio.msgPageUrl')) // 请检查是否定义pageUrl
                return
              }

              if (lineHeader && lineHeader.length) {
                const tableConfigItem = lineHeader.map(item => {
                  const { label, prop } = item
                  let realLabel = label
                  if (typeof label === 'function') {
                    realLabel = label()
                  }
                  return { label: prop, prop: realLabel }
                })

                let dictCodes = ''
                try {
                  if (lineItem.dictCodes) {
                    dictCodes = JSON.stringify(lineItem.dictCodes)
                  }
                } catch (e) {
                  console.error(e)
                }
                tableConfigLineArray.push({
                  tableConfig: tableConfigItem,
                  dictCodes: dictCodes,
                  exportType: lineItem.exportType,
                  currentSelectedRows: [],
                  pageNum: lineItem.pageNum,
                  pageSize: lineItem.pageSize,
                  pageUrl: lineItem.pageUrl,
                  indexName: 'line' + lineIndex,
                  fileName: lineItem.fileName
                })
              } else {
                throw new Error(this.$t('components.eio.notDfTableHeader')) // 未定义tableHeader
              }
            }
            this.lineConfig = tableConfigLineArray
          } else {
            throw new Error(this.$t('components.eio.notDfTableHeader')) // 未定义tableHeader
          }

          this.$nextTick(() => {
            for (let tableIndex = 0; tableIndex < this.lineConfig.length; tableIndex++) {
              const lineItem = this.lineConfig[tableIndex]
              this.$refs['tableLine' + lineItem.indexName].toggleAllSelection()
            }
            // this.$refs.tableLine.toggleAllSelection()
          })
        } else {
          if (!this.pageUrl) {
            this.$message.error(this.$t('components.eio.msgPageUrl')) // 请检查是否定义pageUrl
            return
          }
          if (this.tableHeader && this.tableHeader.length) {
            let resTableHeader = this.tableHeader.filter(i => (i.prop && i.prop != 'operation')) // 过滤掉操作列
            this.tableConfig = resTableHeader.map(item => {
              const { label, prop } = item
              let realLabel = label
              if (typeof label === 'function') {
                realLabel = label()
              }
              return { label: prop, prop: realLabel }
            })
          } else {
            throw new Error(this.$t('components.eio.notDfTableHeader')) // 未定义tableHeader
          }
          this.$nextTick(() => {
            this.$refs.table && this.$refs.table.toggleAllSelection()
          })
        }
      }
      // 服务端定义导出字段
      if (this.exportMode === 'service') {
        if (this.propUrl) {
          this.$http({
            url: this.propUrl,
            method: 'POST',
            loading: true
          }).then(({ data }) => {
            this.tableConfig = Object.entries(data).map(([label, prop]) => ({
              label,
              prop
            }))
            this.$nextTick(() => {
              this.$refs.table && this.$refs.table.toggleAllSelection()
            })
          })
        } else {
          throw new Error(this.$t('components.eio.msgPropUrl'))// 未定义导出字段接口 - propUrl
        }
      }
    },
    submitByCommon () {
      // 如果是多文件导出，则操作完成后退出
      if (this.multiple) {
        const excelParamList = []
        for (let i = 0; i < this.lineConfig.length; i++) {
          const lineItem = this.lineConfig[i]

          const showItems = lineItem.currentSelectedRows.map(i => i.label)
          lineItem.tableConfig = lineItem.tableConfig.map(item => {
            if (showItems.findIndex(j => j === item.label) > -1) { return { ...item, show: true } }
            return { ...item, show: false }
          })
          let queryParam = this.filterParams || {}
          if (lineItem.exportType === EXPORT_TYPE.CURRENT_PAGE) {
            queryParam = {
              ...queryParam,
              pageSize: lineItem.pageSize,
              pageNum: lineItem.pageNum
            }
          }

          const data = {
            queryParam,
            fileName: lineItem.fileName,
            titleList: lineItem.tableConfig.filter(i => i.show).map(i => i.label),
            languageList: lineItem.tableConfig.filter(i => i.show).map(i => i.prop),
            dictCodes: lineItem.dictCodes,
            url: this.baseUrl + sysPrefix() + lineItem.pageUrl
          }
          const title = this.$t(this.$route.meta.title)
          if (title.indexOf('.') > -1) {
            data.permissionName = this.$t(title)
          } else {
            data.permissionName = title
          }
          excelParamList.push(data)
        }

        const url = '/api-file/common-export/exportExcelLine'
        this.$pageLoading.open()
        if (this.isCustomUrl) {
          this.doExport(this.isCustomUrl, excelParamList)
        } else {
          this.doExport(url, excelParamList)
        }
        return
      }
      const queryParam = this.getQueryParams()
      let dictCodes = ''
      try {
        if (this.dictCodes) {
          dictCodes = JSON.stringify(this.dictCodes)
        }
      } catch (e) {
        console.error(e)
      }
      const data = {
        queryParam,
        fileName: this.fileNameValue,
        titleList: this.tableConfig.filter(i => i.show).map(i => i.label),
        languageList: this.tableConfig.filter(i => i.show).map(i => i.prop),
        dictCodes,
        url: this.baseUrl + sysPrefix() + this.pageUrl
      }
      const title = this.$t(this.$route.meta.title)
      if (title.indexOf('.') > -1) {
        data.permissionName = this.$t(title)
      } else {
        data.permissionName = title
      }
      console.log('[export excel common]', data)
      const url = '/api-file/common-export/exportExcel'
      this.$pageLoading.open()
      if (this.isCustomUrl) {
        this.doExport(this.isCustomUrl, data)
      } else {
        this.doExport(url, data)
      }
    },
    getQueryParams () {
      const showItems = this.currentSelectedRows.map(i => i.label)
      this.tableConfig = this.tableConfig.map(item => {
        if (showItems.findIndex(j => j === item.label) > -1) { return { ...item, show: true } }
        return { ...item, show: false }
      })

      let queryParam = this.filterParams || {}
      if (this.exprotType === EXPORT_TYPE.CURRENT_PAGE) {
        queryParam = {
          ...queryParam,
          pageSize: Number(this.pageSize),
          pageNum: Number(this.pageNum)
        }
      } else {
        queryParam = {
          ...queryParam,
          pageSize: Number(this.exportSize) // 全部导出条数
        }
      }
      // meiqlApi 接口导出方式构造入参
      if (this.exportType == 'meiqlApi') {
        queryParam['meiqlPayload'] = this.generateMeiQLExportRequest().body
        queryParam['meiqlPayload'].tree = false
      }
      return queryParam
    },
    submitHandle () {
      if (this.exportMode === 'front') {
        return this.submitByCommon()
      }
      console.log(this.currentSelectedRows)
      if (!this.exprotUrl) {
        throw new Error(this.$t('components.eio.msgExportUrl')) // 未定义导出文件接口 - exprotUrl
      }
      const queryParam = this.getQueryParams()
      const data = {
        queryParam,
        fileName: this.fileNameValue,
        titleList: this.tableConfig.filter(i => i.show).map(i => i.label)
      }
      this.doExport(this.exprotUrl, data)
    },
    doExport (url, data) {
      let menuInfo = getMenuInfo()
      axios({
        method: 'POST',
        url: `${sysPrefix()}${url}`,
        timeout: this.timeout,
        headers: {
          Authorization: 'Bearer ' + getToken(),
          'X-Fun-Info': menuInfo.secretKey
        },
        data: data,
        responseType: 'arraybuffer'
      })
        .then(response => {
          this.$pageLoading.close()
          this.closeModal()

          const blob1 = new Blob([response])
          console.log(blob1)
          console.log(response)
          const { data } = response
          if (response.headers['content-type'].startsWith('application/json')) {
            let enc = new TextDecoder('utf-8')
            let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
            throw new Error(res.message)
          }
          const blob = new Blob([data])
          const disposition = response.headers['content-disposition'] || ''
          const filename = decodeURIComponent(disposition.split('=')[1])
          const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
          let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
          dom.style.display = 'none'
          dom.href = url
          dom.rel = 'noopener'
          dom.setAttribute('download', `${this.fileNameValue}.xlsx` || filename) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
        })
        .catch(error => {
          console.log(error)
          this.$pageLoading.close()
          this.$message({ type: 'error', message: error.message })
        })
    },
    tableSelectHandle (selection) {
      this.currentSelectedRows = selection
    },
    renderFooter (h) {
      return (
        <div class="custom-footer">
          {
            this.multiple
              ? (
                this.lineConfig.map((lineItem, index) => {
                  return (
                    <ElForm label-width="60px" style="width:75%; float:left">
                      <ElRow type="flex" gutter={16}>
                        <ElCol>
                          <ElFormItem
                            label={this.$t('customTable.fileName')}
                            prop="fileName"
                          >
                            <ElInput vModel={lineItem.fileName} />
                          </ElFormItem>
                        </ElCol>
                        <ElCol>
                          <ElFormItem
                            label={this.$t('customTable.exportType')}
                            prop="exportType"
                          >
                            <ElSelect vModel={lineItem.exportType}>
                              {this.exportSelect.map(({ value, label }) => (
                                <ElOption key={value} value={value} label={label()} />
                              ))}
                            </ElSelect>
                          </ElFormItem>
                        </ElCol>
                      </ElRow>
                      {lineItem.exportType === EXPORT_TYPE.CURRENT_PAGE ? (
                        <ElRow type="flex" gutter={16}>
                          <ElCol>
                            <ElFormItem
                              label={this.$t('customTable.pageSize')}
                              prop="pageSize"
                            >
                              <ElInput type="number" vModel={lineItem.pageSize} onChange={this.pageSizeChange} />
                            </ElFormItem>
                          </ElCol>
                          <ElCol>
                            <ElFormItem
                              label={this.$t('customTable.pageNum')}
                              prop="pageNum"
                            >
                              <ElInput type="number" min="1" vModel={lineItem.pageNum} onChange={this.pageNumChange} />
                            </ElFormItem>
                          </ElCol>
                        </ElRow>
                      ) : null}
                    </ElForm>
                  )
                })
              ) : (
                <ElForm label-width="60px" style="width:75%; float:left">
                  <ElRow type="flex" gutter={16}>
                    <ElCol>
                      <ElFormItem
                        label={this.$t('customTable.fileName')}
                        prop="fileName"
                      >
                        <ElInput vModel={this.fileNameValue} />
                      </ElFormItem>
                    </ElCol>
                    <ElCol>
                      <ElFormItem
                        label={this.$t('customTable.exportType')}
                        prop="exportType"
                      >
                        <ElSelect vModel={this.exprotType}>
                          {this.exportSelect.map(({ value, label }) => (
                            <ElOption key={value} value={value} label={label()} />
                          ))}
                        </ElSelect>
                      </ElFormItem>
                    </ElCol>
                  </ElRow>
                  {this.exprotType === EXPORT_TYPE.CURRENT_PAGE ? (
                    <ElRow type="flex" gutter={16}>
                      <ElCol>
                        <ElFormItem
                          label={this.$t('customTable.pageSize')}
                          prop="pageSize"
                        >
                          <ElInput type="number" vModel={this.pageSize} onChange={this.pageSizeChange} />
                        </ElFormItem>
                      </ElCol>
                      <ElCol>
                        <ElFormItem
                          label={this.$t('customTable.pageNum')}
                          prop="pageNum"
                        >
                          <ElInput type="number" min="1" vModel={this.pageNum} onChange={this.pageNumChange} />
                        </ElFormItem>
                      </ElCol>
                    </ElRow>
                  ) : null}
                </ElForm>
              )
          }
          <ElButton onClick={this.closeModal}>
            {this.$t('common.cancel')}
          </ElButton>
          <ElButton
            type="primary"
            class="submit-button"
            onClick={this.submitHandle}
          >
            {this.$t('common.confirm')}
          </ElButton>
        </div>
      )
    },
    renderEditTableModal (h) {
      return (
        <SrmDialog
          appendToBody
          title={this.$t('customTable.exprotTitle')}
          size="middle"
          show-close={true}
          ref="table"
          visible={this.modalVisible}
          class="custom-table-dialog"
          onClose={this.closeModal}
        >
          <div class="tips">{this.$t('customTable.exportTip')}</div>
          {this.multiple
            ? (
              <ElTabs type="border-card">
                {this.lineConfig.map((lineItem) => {
                  return (
                    <ElTabPane label={lineItem.fileName} class={'custom-pane-' + lineItem.indexName}>
                      <ElTable
                        max-height="356px"
                        border
                        ref={'tableLine' + lineItem.indexName}
                        on-selection-change={(selection) => { this.selectionChange(lineItem, selection) }}
                        data={lineItem.tableConfig}
                        row-key="label"
                      >
                        <ElTableColumn type="selection" />
                        <ElTableColumn
                          label={this.$t('customTable.colName')}
                          prop="label"
                        />
                        <ElTableColumn
                          label={this.$t('customTable.propName')}
                          prop="prop"
                        />
                      </ElTable>
                    </ElTabPane>
                  )
                })}
              </ElTabs>
            ) : (
              <ElTable
                max-height="356px"
                ref="table"
                border
                on-selection-change={this.tableSelectHandle}
                data={this.tableConfig}
                row-key="label"
              >
                <ElTableColumn type="selection" />
                <ElTableColumn
                  label={this.$t('customTable.colName')}
                  prop="label"
                />
                <ElTableColumn
                  label={this.$t('customTable.propName')}
                  prop="prop"
                />
              </ElTable>
            )}
          <div slot="footer">{this.renderFooter(h)}</div>
        </SrmDialog>
      )
    },
    pageNumChange (value) {
      if (value < 1) {
        this.pageNum = 1
      }
    },
    // 最大导出数值
    pageSizeChange (value) {
      if (value > this.exportSize) {
        this.pageSize = this.exportSize
      }
    },
    // 判断权限
    hasPermission () {
      const userInfo = this.$store.getters.user.userInfo
      const { buttonPermission = {} } = userInfo
      if (!this.code) return true
      if (buttonPermission[this.code]) {
        return buttonPermission[this.code] == 'Y'
      } else {
        return true
      }
    },
    selectionChange (lineItem, selection) {
      lineItem.currentSelectedRows = selection
    }
  },
  render (h) {
    const isShow = this.hasPermission()
    return isShow ? (
      <div class="export-excel">
        {this.modalVisible ? this.renderEditTableModal(h) : null}
        <ElButton
          onClick={this.openModal}
          type={this.type}
          disabled={this.disabled}
        >
          {this.title || this.$t('customTable.exprotTitle')}
        </ElButton>
      </div>
    ) : null
  }
}
