import Sortable from 'sortablejs'
import drag from '@/assets/table/drag.svg'
import './index.scss'

export default {
  name: 'CustomTable',
  props: {
    pageViewConfigCode: {
      type: String,
      default: ''
    },
    tableColumn: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      dragIcon: drag,
      // 配置项是否可见
      modalVisible: false,
      // 弹框表格配置数据
      tableConfig: []
    }
  },
  methods: {
    // 初始化配置
    async initConfig () {
      this.tableConfig = this.tableColumn.map(({ field, title, width, hidden, fixed }) => {
        let obj = { show: !hidden, lockLeft: fixed === 'left' ? 'Y' : 'N', lockRight: fixed === 'right' ? 'Y' : 'N' }

        return { prop: field, label: title, width, ...obj }
      })
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
    // 打开配置
    openModal () {
      this.modalVisible = true
      this.initConfig() // 初始化配置
      this.$nextTick(() => this.initSortable())
    },
    // 配置完成后确定按钮事件
    submitHandle () {
      this.modalVisible = false
      this.$emit('updataConfig', this.tableConfig)
    },
    // 重置
    resetModal () {
      this.modalVisible = false
      this.$emit('resetConfig')
    },
    // 取消按钮
    closeModal () {
      this.modalVisible = false
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
