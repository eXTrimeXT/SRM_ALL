import Sortable from 'sortablejs'
import drag from '@/assets/table/drag.svg'
import './index.scss'
const localStorageKeyPerfix = 'custom_table_key'

export default {
  name: 'CustomTable',
  props: {
    pageViewConfigCode: {
      type: String,
      default: ''
    },
    needInit: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      dragIcon: drag,
      // 配置项是否可见
      modalVisible: false,
      // 默认配置
      defaultConfig: [],
      // 弹框表格配置数据
      tableConfig: []
    }
  },
  inject: ['context'],
  watch: {
    tableConfig: {
      deep: true,
      handler: function (n, o) {
        if (n) {
          // console.log('tableConfigchange')
          // console.log(n)
        }
      }
    }
  },
  mounted () {
    if (this.needInit) this.initConfig() // 去掉因为会重复调用 pageConfig.getCurrentConfig接口
  },
  methods: {
    // localstore 保存key
    getParams () {
      const userId = this.$store.getters.user.userId
      const key = `${localStorageKeyPerfix}_${userId}_${this.pageViewConfigCode}`
      return key
    },
    // 查询接口配置信息
    async fatchConfig () {
      let query = { pageViewConfigCode: this.pageViewConfigCode }
      const { data = {} } = await this.$api.base.pageConfig.getCurrentConfig(query)
      let tableConfig = data.tableConfig || ''
      return tableConfig
    },
    // 保存后台
    async saveConfig (tableConfig) {
      let query = {
        pageViewConfigCode: this.pageViewConfigCode,
        configType: 'TABLE',
        tableConfig: tableConfig
      }
      let res = await this.$api.base.pageConfig.saveUserConfig(query).then(res => { })
      return res
    },
    // 还原配置
    async clearConfig () {
      let query = {
        pageViewConfigCode: this.pageViewConfigCode,
        configType: 'TABLE'
      }
      await this.$api.base.pageConfig.removeUserConfig(query)
    },
    // 查询配置
    async queryConfig () {
      const key = this.getParams()
      const JSON_CONFIG = localStorage.getItem(key) // 本地
      const JSON_CONFIG_SERVICE = await this.fatchConfig() // 获取后台配置信息
      const CONFIG_RES = JSON_CONFIG_SERVICE || JSON_CONFIG
      const config = CONFIG_RES ? JSON.parse(CONFIG_RES) : undefined
      const defaultTConfig = ((this.context || {}).defaultTableHeader || [])
      let meta = []
      if (config) {
        meta = config.map(({ prop, show, width, minWidth, fixed, lockLeft, lockRight }) => {
          const target = defaultTConfig.find(i => i.prop === prop)
          return { ...target, show, width, minWidth, fixed, lockLeft, lockRight }
        })
      } else {
        meta = defaultTConfig.map((item) => ({ ...item }))
      }
      return meta
    },
    // 初始化配置
    async initConfig () {
      this.defaultConfig = ((this.context || {}).defaultTableHeader || [])
      const config = await this.queryConfig()
      this.tableConfig = config || this.defaultConfig
    },
    // 拖拽培训初始化
    initSortable () {
      const tbody = document.querySelector(
        '.custom-table-dialog .el-table__body-wrapper tbody'
      )
      const _this = this
      if (tbody) {
        Sortable.create(tbody, {
          handle: '.drag-block',
          animation: 180,
          delay: 0,
          filter: '.el-table__row .el-input', // 过滤器，不需要进行拖动的元素
          preventOnFilter: false,
          onEnd ({ newIndex, oldIndex }) {
            const currRow = _this.tableConfig.splice(oldIndex, 1)[0]
            _this.tableConfig.splice(newIndex, 0, currRow)
          }
        })
      }
    },
    // 保存配置数据
    async setConfig () {
      let version = (new Date()).getTime()
      this.tableConfig.forEach(element => {
        element['minWidth'] = element.width // 保存时把配置的 width 赋值给 minWidth
        element.version = version
        element.fixed = element.lockLeft === 'Y' ? 'left' : element.lockRight === 'Y' ? 'right' : undefined
        if (element.prop === 'operation') { // 操作列默认右边固定
          element.fixed = 'right'
        }
      })
      let configRes = this.tableConfig // .filter(i => i.show)
      const key = this.getParams()
      const config = JSON.stringify(configRes)
      localStorage.setItem(key, config)
      await this.saveConfig(config) // 保存到后台
    },
    // 打开配置
    async openModal () {
      this.modalVisible = true
      await this.initConfig() // 初始化配置
      this.$nextTick(() => this.initSortable())
    },
    // 配置完成后确定按钮事件
    async submitHandle () {
      await this.setConfig()
      this.$emit('updataConfig', true)
      this.modalVisible = false
    },
    // 重置
    async resetModal () {
      const key = this.getParams()
      localStorage.removeItem(key) // 清空本地
      await this.clearConfig() // 清除后台配置数据
      this.$emit('updataConfig', true) // 更新列表
      this.initConfig() // 重置重新初始化
    },
    // 取消按钮
    closeModal () {
      this.modalVisible = false
      this.$emit('updataConfig', false)
    },
    labelFormatter (row, prop, cellValue) {
      if (typeof cellValue === 'function') {
        return cellValue()
      }
      return cellValue
    },
    lockLeftChange (scope) {
      scope.lockRight = 'N'
    },
    lockRightChange (scope) {
      scope.lockLeft = 'N'
    },
    // sortDown(scope) {
    //   const { $index } = scope;
    //   if ($index === this.tableConfig.length - 1) return;
    //   const temp1 = this.tableConfig[$index];
    //   const temp2 = this.tableConfig[$index + 1];
    //   this.$set(this.tableConfig, $index + 1, temp1)
    //   this.$set(this.tableConfig, $index, temp2);
    // },
    // sortUp(scope) {
    //   const { $index } = scope;
    //   if ($index === 0) return;
    //   const temp1 = this.tableConfig[$index];
    //   const temp2 = this.tableConfig[$index - 1];
    //   this.$set(this.tableConfig, $index - 1, temp1)
    //   this.$set(this.tableConfig, $index, temp2);
    // },

    renderFooter (h) {
      return (
        <div class="custom-table-footer">
          <ElButton onClick={this.closeModal}>
            {/* 取 消 */}
            {this.$t('common.cancel')}
          </ElButton>
          <ElButton onClick={this.resetModal}>
            {/* 重置 */}
            {this.$t('common.reset')}
          </ElButton>
          <ElButton
            type="primary"
            onClick={this.submitHandle}
          >
            {/* 确 定 */}
            {this.$t('common.confirm')}
          </ElButton>
        </div>
      )
    },
    renderEditTableModal (h) {
      return (
        // 自定义表格
        <SrmDialog
          title={this.$t('customTable.tableTitle')}
          show-close={true}
          size="middle"
          append-to-body
          visible={this.modalVisible}
          class="custom-table-dialog"
          onClose={this.closeModal}
        >
          {/* * 按住行拖动可调整表格的列顺序 */}
          <div class="tips">{this.$t('customTable.tableTip')}</div>
          <ElTable
            class="custom-table"
            border
            stripe
            max-height="400px"
            ref="table"
            data={this.tableConfig}
            row-key="prop"
          >
            <ElTableColumn
              label=""
              align="left"
              width="38px"
              {
              ...{
                scopedSlots: {
                  default: props => {
                    return (
                      <div class={props.row.prop !== 'operation' ? 'drag-block' : ''}>
                        { (props.row.prop !== 'operation' ? <img src={this.dragIcon} height="16" width="16"/> : null)}
                      </div>
                    )
                  }
                }
              }
              }
            />
            <ElTableColumn
              width="100px"
              label={this.$t('customTable.showCol')}
              align="center"
              {
              ...{
                scopedSlots: {
                  default: props => {
                    return (
                      <div>
                        <ElCheckbox v-model={props.row.show} disabled={props.row.prop === 'operation'} />
                      </div>
                    )
                  }
                }
              }
              }
            />
            {/* 表格列名 */}
            <ElTableColumn
              label={this.$t('customTable.colName')}
              prop="label"
              align="left"
              formatter={this.labelFormatter}
            />
            {/* 属性名 */}
            {/* <ElTableColumn align="left" label={this.$t('customTable.propName')} prop="prop" /> */}
            <ElTableColumn
              label={this.$t('customTable.colWidth')}
              align="left"
              {
              ...{
                scopedSlots: {
                  default: props => {
                    return (
                      <div>
                        <ElInput
                          v-model={props.row.width}
                          disabled={props.row.prop === 'operation'}
                          v-input-format={{ type: 'integer', negative: false }}
                        />
                      </div>
                    )
                  }
                }
              }
              }
            />
            {/* 冻结到左列 */}
            <ElTableColumn
              label={this.$t('customTable.fixedLeft')}
              align="left"
              {
              ...{
                scopedSlots: {
                  default: props => {
                    return (
                      <div>
                        <ElSwitch
                          v-model={props.row.lockLeft}
                          active-value="Y"
                          inactive-value="N"
                          disabled={props.row.prop === 'operation'}
                          onChange={() => this.lockLeftChange(props.row)}
                        />
                      </div>
                    )
                  }
                }
              }
              }
            />
            {/* 冻结到右列 */}
            <ElTableColumn
              label={this.$t('customTable.fixedRight')}
              align="left"
              {
              ...{
                scopedSlots: {
                  default: props => {
                    return (
                      <div>
                        <ElSwitch
                          v-model={props.row.lockRight}
                          active-value="Y"
                          inactive-value="N"
                          disabled={props.row.prop === 'operation'}
                          onChange={() => this.lockRightChange(props.row)}
                        />
                      </div>
                    )
                  }
                }
              }
              }
            />
            {/* <ElTableColumn
              label={$t('common.operation')}
              {
                ...{
                  scopedSlots: {
                    default: props => {
                      return (
                        <div>
                          <ElButton type="primary" icon="el-icon-bottom" onClick={() => this.sortDown(props)} />
                          <ElButton type="primary" icon="el-icon-top" onClick={() => this.sortUp(props)} />
                        </div>
                      );
                    }
                  }
                }
              }
            /> */}
          </ElTable>
          <div slot="footer">{this.renderFooter(h)}</div>
        </SrmDialog>
      )
    }
  },
  render (h) {
    return (
      <div>
        {this.modalVisible ? this.renderEditTableModal(h) : null}
        <div onClick={this.openModal} class="setting-button">
          {/* 表头配置 */}
          <i class="iconfont iconiconchilun setting-icon-operation"></i>
          {this.$t('customTable.tableTitle')}
        </div>
      </div>
    )
  }
}
