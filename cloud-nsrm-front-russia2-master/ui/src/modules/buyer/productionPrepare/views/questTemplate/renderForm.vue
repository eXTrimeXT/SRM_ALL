<template>
  <div>
    <el-tabs
      v-model="activeTab"
      type="card"
      class="renderForm"
    >
      <el-tab-pane
        v-for="item in configData"
        :key="item.questTemplatePropGroupCode"
        :label="item.questTemplatePropGroupName"
        :name="item.questTemplatePropGroupCode"
        :lazy="true"
      >
        <div class="fileInfoDiv">
          <!-- 表单 -->
          <el-form
            v-if="item.questTemplatePropGroupType==='form'"
            :model="resData[item.questTemplatePropGroupCode]"
          >
            <el-row :gutter="32">
              <el-col
                v-for="formItem in item.formItems"
                :key="formItem.fieldConfigId"
                :span="6"
              >
              <!-- '必填' -->
                <el-form-item
                  :prop="formItem.questTemplatePropField"
                  :label="formItem.questTemplatePropFieldDesc"
                  :rules="[{required: formItem.emptyFlag==='Y' ? true:false,message:$t('vendorMod.required')}]"
                >
                  <!-- 文本 -->
                  <el-input
                    v-if="formItem.questTemplatePropType==='text'"
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    :disabled="disabled"
                    clearable
                  />
                  <!-- 数字 -->
                  <el-input
                    v-else-if="formItem.questTemplatePropType==='number'"
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    v-input-format="{ type: 'float' }"
                    :disabled="disabled"
                    clearable
                  />
                  <!-- 多行文本 -->
                  <el-input
                    v-else-if="formItem.questTemplatePropType==='mText'"
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    type="textarea"
                    :rows="2"
                    :disabled="disabled"
                    clearable
                  />
                  <!-- 日期 -->
                  <el-date-picker
                    v-else-if="formItem.questTemplatePropType==='date'"
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    :disabled="disabled"
                    clearable
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                  <!-- 开关 -->
                  <div
                    v-else-if="formItem.questTemplatePropType==='switch'"
                    style="margin-top: 28px;height: 32px;"
                  >
                    <el-switch
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      :disabled="disabled"
                      active-value="Y"
                      inactive-value="N"
                    />
                  </div>
                  <DictSelect
                    v-else-if="formItem.questTemplatePropType==='select'"
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    :disabled="disabled"
                    :code="formItem.questTemplatePropDict"
                  />
                  <div
                    v-else-if="formItem.questTemplatePropType==='file'"
                    class="formRenderFile"
                  >
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: (resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]).split('#')[0],
                        fileName: (resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]).split('#')[1]
                      }"
                      :readonly="disabled"
                      @on-change="({file}) => formUploadSuccess(file,item.questTemplatePropGroupCode,formItem.questTemplatePropField)"
                    />
                  </div>
                  <el-input
                    v-else
                    v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                    :disabled="disabled"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <!-- 表格 -->
          <div v-if="item.questTemplatePropGroupType==='table'">
            <!-- 编辑模式下才需要新增按钮 -->
            <div
              v-if="optType==='edit'"
              class="optDiv"
            >
              <el-button
                type="primary"
                @click="addFiledItem(item.questTemplatePropGroupCode,item.rowObj)"
              >
                <!-- 新增 -->
                {{ $t("common.add") }}
              </el-button>
            </div>
            <div
              v-if="optType === 'questNew'"
              style="margin-bottom:10px;"
            >
              <el-button
                style="margin-left:10px;"
                type="primary"
                @click="exportHandle"
              >
                <!-- 导出 -->
                {{ $t("common.export") }}
              </el-button>
              <!-- 导入 -->
              <MImport
                v-if="showImport"
                style="display: inline-block;margin: 0 10px;"
                :title="$t('common.import')"
                :disabled="importDisabled"
                upLoadUrl="/api-ppap/quest/questSupplier/importDetailData"
                :extraData="{
                  fileModular: 'suplier',
                  fileFunction: 'accountAccess',
                  fileType: 'excel',
                  rangType:rangType,
                  questTemplatePropGroupIdForQuery:item.questTemplatePropGroupId,
                  questTemplateId:questTemplateId,
                  questNo:questNo
                }"
                @downloadTemplate="downloadTemplate(item)"
                @handleSuccess="(res) => { handleSuccess(res,item.questTemplatePropGroupCode) }"
              />
              <span style="color:#1890ff;margin-left:20px;">
                <!-- 导出按钮调查表 -> 填写内容 -> 导入按钮提交上传 -->
                {{ $t("cusEntry.supplement20250211.exportButtonSurveyFormFillContentImportSubmitUpload") }}
              </span>
            </div>
            <el-table
              :data="(frontPaging && resData[item.questTemplatePropGroupCode]) ? resData[item.questTemplatePropGroupCode].slice((item.pageNum-1)*item.pageSize,item.pageSize*item.pageNum) : resData[item.questTemplatePropGroupCode]"
              style="width: 100%"
              border
              :cell-class-name="getCellClass"
            >
              <el-table-column
                type="index"
                width="50"
              />

              <el-table-column
                v-for="(col) in computedItems(item.colItems)"
                :key="col.questTemplatePropField"
                sortable
                :prop="col.questTemplatePropField"
                :label="col.questTemplatePropFieldDesc"
                min-width="110px"
              >
                <template
                  v-if="col.emptyFlag==='Y'"
                  slot="header"
                >
                  <i class="toRequired">*</i>
                  {{ col.questTemplatePropFieldDesc }}
                </template>
                <template
                  slot-scope="scope"
                >
                  <!-- 文本 -->
                  <el-input
                    v-if="col.questTemplatePropType==='text'"
                    v-model="scope.row[col.questTemplatePropField]"
                    clearable
                    :disabled="disabled"
                  />
                  <!-- 数字 -->
                  <el-input
                    v-else-if="col.questTemplatePropType==='number'"
                    v-model="scope.row[col.questTemplatePropField]"
                    v-input-format="{ type: 'float' }"
                    clearable
                    :disabled="disabled"
                  />
                  <!-- 多行文本 -->
                  <el-input
                    v-else-if="col.questTemplatePropType==='mText'"
                    v-model="scope.row[col.questTemplatePropField]"
                    type="textarea"
                    :rows="2"
                    clearable
                    :disabled="disabled"
                  />
                  <!-- 日期 -->
                  <el-date-picker
                    v-else-if="col.questTemplatePropType==='date'"
                    v-model="scope.row[col.questTemplatePropField]"
                    :disabled="disabled"
                    clearable
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                  <!-- 开关 -->
                  <el-switch
                    v-else-if="col.questTemplatePropType==='switch'"
                    v-model="scope.row[col.questTemplatePropField]"
                    :disabled="disabled"
                    active-value="Y"
                    inactive-value="N"
                  />
                  <DictSelect
                    v-else-if="col.questTemplatePropType==='select'"
                    v-model="scope.row[col.questTemplatePropField]"
                    :disabled="disabled"
                    :code="col.questTemplatePropDict"
                  />
                  <div
                    v-else-if="col.questTemplatePropType==='file'"
                    class="formRenderFile"
                  >
                    <SrmCommonFile
                      :default-file="{
                        fileId: (scope.row[col.questTemplatePropField]).split('#')[0],
                        fileName: (scope.row[col.questTemplatePropField]).split('#')[1]
                      }"
                      :extra-data="fileInfo"
                      @on-change="file => tableUploadSuccess(file, scope.$index,col.questTemplatePropField,(frontPaging && resData[item.questTemplatePropGroupCode]) ? resData[item.questTemplatePropGroupCode].slice((item.pageNum-1)*item.pageSize,item.pageSize*item.pageNum) : resData[item.questTemplatePropGroupCode])"
                    />
                  </div>
                  <el-input
                    v-else
                    v-model="scope.row[col.questTemplatePropField]"
                    clearable
                    :disabled="disabled"
                  />
                </template>
              </el-table-column>
            </el-table>
            <CPagination
              v-if="frontPaging"
              ref="queryPagination"
              style="margin: 0"
              class="c-query-table-pagination"
              :total="resData[item.questTemplatePropGroupCode] ? resData[item.questTemplatePropGroupCode].length : 0"
              :page-num="item.pageNum"
              :page-size="item.pageSize"
              :page-sizes="viewSizes"
              @current-change="(current) => (item.pageNum = current)"
              @size-change="(size) => (item.pageSize = size)"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import DictSelect from 'lib@/components/c-select/dict-select'
import MImport from 'lib@/components/import'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import { questTemplate } from 'modb@/productionPrepare/api'

export default {
  name: 'RenderForm',
  components: { DictSelect, MImport, CPagination },
  props: {
    showEnabledFlagArr: {
      type: Array,
      default () {
        return []
      }
    },
    frontPaging: {
      type: Boolean,
      default: false
    },
    questTemplateId: {
      // 模板ID
      type: [String, Number],
      default: ''
    },
    questSupId: {
      // 供应商ID
      type: [String, Number],
      default: ''
    },
    disabled: {
      // 预览都是禁用
      type: Boolean,
      default: false
    },
    optType: {
      type: String,
      default: 'view'
    },
    comData: {
      type: Array,
      default () {
        return []
      }
    },
    rangType: {
      type: String,
      default: ''
    },
    tableHeader: {
      type: Array,
      default () {
        return []
      }
    },
    importDisabled: {
      type: Boolean,
      default: false
    },
    showImport: {
      type: Boolean,
      default: true
    },
    questNo: {
      type: String,
      default: ''
    },
    viewSizes: {
      type: Array,
      default: () => {
        return [10, 15, 30, 60, 120, 300, 600, 1000, 1500]
      }
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'questManagement',
        fileType: 'images'
      },
      activeTab: '',
      configData: [],
      mergeForm: {},
      sourceList: [],
      resData: {},
      resFormateData: {}, // 提交给后端最后数据
      questTemplateTabArr: []
    }
  },
  computed: {
    computedItems (items) {
      return (items) => {
        return items.filter(item => item.enabledFlag !== 'N') // 调查表管理enabledFlag有可能为空
      }
    }
  },
  watch: {
    questTemplateId (nVal, oVal) {
      if (nVal && nVal !== oVal) {
        this.getTempDetail(nVal)
      }
    }
  },
  created () {
    if (this.questTemplateId) {
      this.getTempDetail(this.questTemplateId)
    }
  },
  mounted () {
    this.$forceUpdate()
  },
  methods: {
    getCellClass ({ row, column, rowIndex, columnIndex }) {
      for (let key in row) {
        if (
          key.includes('Error') &&
          row[key] === 'Y' &&
          key.split('Error')[0] === column.property
        ) {
          return 'red-color'
        }
      }
    },
    // 通过id查询模板数据
    getTempDetail (questTemplateId) {
      questTemplate.questTemplateData({ questTemplateId }).then((res) => {
          let questTemplateTabArr = res.data.questTemplateTabArr
          if (this.tableHeader.length) {
            questTemplateTabArr = this.changeColumns(questTemplateTabArr)
          }
          this.questTemplateTabArr = questTemplateTabArr
          console.log('questTemplateTabArr', questTemplateTabArr)
          this.configData = this.adaptConfigData(questTemplateTabArr) // 配置信息
          console.log('configData', this.configData)
          if (this.configData && this.configData.length) {
            this.activeTab = this.configData[0].questTemplatePropGroupCode
          }
          this.resFormateData = this.formateResData(res.data)
          this.$emit('getTempData', questTemplateTabArr)
        })
        .catch((err) => {
          console.log(err)
        })
    },
    changeColumns (questTemplateTabArr) {
      let newArr = JSON.parse(JSON.stringify(questTemplateTabArr))
      let newTable = JSON.parse(JSON.stringify(this.tableHeader)).reverse()
      newArr.forEach((item) => {
        for (let innerItem of newTable) {
          item.questTemplatePropArr.unshift({
            questTemplatePropField: innerItem.prop,
            questTemplatePropFieldDesc: innerItem.label
          })
        }
      })
      console.log('newArr', newArr)
      return newArr
    },
    // 适配数据
    adaptConfigData (data) {
      let resArr = []
      let resData = {}
      let TableRowObj = {}
      if (data && data.length > 0) {
        data.forEach((elm) => {
          // if(elm.showFlag==='Y'){// 显示的配置
          if (elm.questTemplatePropGroupType === 'form') {
            let colConfig = []
            resData[elm.questTemplatePropGroupCode] = {}
            elm.questTemplatePropArr.forEach((item) => {
              // 如果是禁用的列，不显示
              if (item.enabledFlag === 'Y') {
                colConfig.push({
                  questTemplatePropField: item.questTemplatePropField, // 字段名
                  questTemplatePropFieldDesc: item.questTemplatePropFieldDesc, // 字段描述
                  questTemplatePropId: item.questTemplatePropId, // 配置ID
                  questTemplatePropSort: item.questTemplatePropSort, // 字段排序
                  questTemplatePropType: item.questTemplatePropType, // 组件类型
                  emptyFlag: item.emptyFlag, // 是否必填
                  questTemplatePropDict: item.questTemplatePropDict // 字典编码
                })
                resData[elm.questTemplatePropGroupCode][item.questTemplatePropField] = '' // 初始字段值
              }
            })
            resArr.push({
              questTemplatePropGroupType: elm.questTemplatePropGroupType,
              questTemplatePropGroupName: elm.questTemplatePropGroupName,
              questTemplatePropGroupId: elm.questTemplatePropGroupId,
              questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
              formItems: colConfig
            })
          } else {
            // 表格显示
            let colConfig = []
            let rowObj = {}
            let pageObj = {}
            if (this.frontPaging) pageObj = { pageNum: 1, pageSize: 10 }
            resData[elm.questTemplatePropGroupCode] = []
            elm.questTemplatePropArr.forEach((colItem) => {
              rowObj[colItem.questTemplatePropField] = ''
              TableRowObj[colItem.questTemplatePropField] = ''
              colConfig.push({
                questTemplatePropField: colItem.questTemplatePropField, // 字段名
                questTemplatePropFieldDesc: colItem.questTemplatePropFieldDesc, // 字段描述
                questTemplatePropId: colItem.questTemplatePropId, // 配置ID
                questTemplatePropSort: colItem.questTemplatePropSort, // 字段排序
                questTemplatePropType: colItem.questTemplatePropType, // 组件类型
                emptyFlag: colItem.emptyFlag, // 是否必填
                enabledFlag: colItem.enabledFlag, // 是否启用
                questTemplatePropDict: colItem.questTemplatePropDict // 字典编码
              })
            })
            colConfig.sort(
              (a, b) => a.questTemplatePropSort - b.questTemplatePropSort
            )
            resArr.push({
              questTemplatePropGroupType: elm.questTemplatePropGroupType,
              questTemplatePropGroupName: elm.questTemplatePropGroupName,
              questTemplatePropGroupId: elm.questTemplatePropGroupId,
              questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
              rowObj: rowObj, // 行字段信息
              colItems: colConfig,
              ...pageObj
            })
          }
          // }
        })
      }
      if (this.optType !== 'questNew') {
        this.resData = resData
      }
      // this.$set()
      // 数据回显
      if (this.comData.length > 0) {
        // 存在数据显示
        this.comData.forEach((elm) => {
          if (elm.questTemplatePropGroupType === 'form') {
            // 表单数据回显
            elm.fieldInfoList.forEach((item) => {
              this.resData[elm.questTemplatePropGroupCode][item.questTemplatePropField] = item.questTemplatePropFieldData // 初始字段值
            })
          } else {
            // 表格数据回显
            let firstRow = elm.fieldInfoList[0]
            let questTemplatePropFieldData = firstRow.questTemplatePropFieldData
              ? firstRow.questTemplatePropFieldData.split(',')
              : []
            let rowLength = questTemplatePropFieldData.length
            for (let t = 0; t < rowLength; t++) {
              const obj = cloneDeep(TableRowObj)
              this.resData[elm.questTemplatePropGroupCode].push(obj)
            }

            elm.fieldInfoList.forEach((item) => {
              let fieldData = item.questTemplatePropFieldData
                ? item.questTemplatePropFieldData.split(',')
                : []
              fieldData.forEach((val, index) => {
                this.resData[elm.questTemplatePropGroupCode][index][item.questTemplatePropField] = val // 初始字段值
              })
            })
          }
        })
      }
      this.$forceUpdate()
      console.log('resArr:::', resArr)
      console.log('resData::', this.resData)
      return resArr
    },
    addFiledItem (code, rowObj) {
      let row = cloneDeep(rowObj)
      for (let i in row) {
        row[i] = ''
      }
      this.resData[code].push(row)
      console.log(this.resData)
      this.$forceUpdate()
    },
    // 提交给后台数据转化
    formateResData (data) {
      let fData = {
        questSupId: this.questSupId, // 供应商ID
        questTemplateId: this.questTemplateId, // 模板ID
        groupInfoList: [] // 配置数据
      }
      if (data && data.questTemplateTabArr) {
        data.questTemplateTabArr.forEach((elm) => {
          let fieldInfo = []
          elm.questTemplatePropArr.forEach((item) => {
            fieldInfo.push({
              questTemplatePropId: item.questTemplatePropId, // 字段名
              questTemplatePropField: item.questTemplatePropField, // 字段描述
              questTemplatePropFieldData: null, // 字段数据
              questTemplatePropFieldLable: item.questTemplatePropFieldLable // 字段额外字段
            })
          })
          fData.groupInfoList.push({
            questTemplatePropGroupId: elm.questTemplatePropGroupId,
            questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
            questTemplatePropGroupType: elm.questTemplatePropGroupType,
            fieldInfoList: fieldInfo // 配置信息
          })
        })
      }
      return fData
    },
    // 表单文件上传
    formUploadSuccess (file, code, fieldId) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        this.resData[code][fieldId] = fileId.toString() + '#' + fileName
      } else {
        this.resData[code][fieldId] = ''
      }
    },
    // 表格文件上传
    tableUploadSuccess ({ file }, index, fieldId, list) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        list[index][fieldId] = fileId + '#' + fileName
      } else {
        list[index][fieldId] = ''
      }
    },
    getData () {
      console.log('this.resData')
      console.log(this.resData)
      console.log('this.resFormateData')
      console.log(this.resFormateData)
      let resFormateData = this.resFormateData // 预设格式
      let resData = this.resData // 填写对应组件数据
      if (resFormateData.groupInfoList.length > 0) {
        resFormateData.groupInfoList.forEach((elm) => {
          if (elm.fieldInfoList.length > 0) {
            elm.fieldInfoList.forEach((i) => {
              if (elm.questTemplatePropGroupType === 'form') {
                // 表单配置
                // 取对应值
                let propValue = resData[elm.questTemplatePropGroupCode][i.questTemplatePropField] // 表单取值
                  i.questTemplatePropFieldData = propValue
              } else {
                // 表格
                let propArr = resData[elm.questTemplatePropGroupCode].map(
                  (row) => row[i.questTemplatePropField]
                )
                i.questTemplatePropFieldData = propArr.toString()
              }
            })
          }
        })
      }
      console.log('resFormateData')
      console.log(resFormateData)
      return resFormateData
    },
    // 燕豪改造,增加导出导入功能
    downloadTemplate (item) {
      console.log(item)
      let name = item.questTemplatePropGroupName
      let params = {
        questTemplateId: this.questTemplateId,
        questTemplatePropGroupIdForQuery: item.questTemplatePropGroupId,
        rangType: this.rangType,
        questNo: this.questNo
      }
      downloadFileLinkByPost(
        '/api-ppap/quest/questSupplier/getDetailModel',
        `${name}模板.xlsx`,
        params
      ).catch(() => {
        // this.$message.Error('下载失败')
        this.$message.Error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess (res, questTemplatePropGroupCode) {
      this.getTempDetail(this.questTemplateId)
      this.$emit('after-import')
      // this.$set(this.resData,[questTemplatePropGroupCode],res.data.data)
      // console.log('resData############',this.resData)
    },
    exportHandle () {
      console.log('resData############', this.resData)
      console.log('questTemplateTabArr############', this.questTemplateTabArr)
      let { questTemplatePropGroupId, questTemplatePropGroupName } =
        this.questTemplateTabArr.find(
          (item) => item.questTemplatePropGroupCode === this.activeTab
        )
      const params = {
        rangType: this.rangType,
        questTemplateId: this.questTemplateId,
        questTemplatePropGroupIdForQuery: questTemplatePropGroupId,
        progressDetails: [
          {
            questTemplatePropGroupId,
            detailData: this.resData[this.activeTab]
          }
        ]
      }
      console.log('params:::', params)
      downloadFileLinkByPost(
        '/api-ppap/quest/questSupplier/exportDetailData',
        `${questTemplatePropGroupName}模板导出${parseTime(new Date())}.xlsx`,
        params
      )
    }
  }
}
</script>
<style scoped lang="scss">
.optDiv {
  padding: 5px 0;
}
.fileInfoDiv {
  padding: 10px 0;
}
.toRequired {
  color: red;
  margin-right: 3px;
}
.formRenderFile {
  padding-top: 28px;
  .c-upload-file {
    justify-content: left !important;
  }
}
.renderForm :deep(.red-color) {
  input,
  select {
    color: red !important;
  }
}
</style>
