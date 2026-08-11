import { findComponentUpwardByProp, proxyProp } from '@/utils/util'

export default {
  name: 'BaseTable',
  props: {
    columns: {
      type: Array,
      required: true
    },
    columnsName: {
      type: String,
      default: 'columns'
    },
    data: {
      type: Array,
      required: true
    }
  },
  computed: {
    emptyText () {
      return this.$attrs['empty-text'] || this.$t('components.noeligiData') // 没有符合条件的数据
    },
    stripe () {
      return this.$attrs.stripe !== false
    }
  },
  mounted () {
    // 代理父组件的columns属性
    const parentComponent = findComponentUpwardByProp(this, this.columnsName)
    if (parentComponent) {
      parentComponent[this.columnsName] = parentComponent[this.columnsName].map(
        column => proxyProp(column)
      )
    } else {
      // console.error('can not find parentComponent')
    }
  },
  methods: {
    // 是否是一个常规的table-column(有以下标签就不是常规table-column)
    isCommonTableColumn (column) {
      const specialColumnList = ['slot', 'operations', 'headerSlot']
      return !specialColumnList.some(option => column[option])
    },
    // 点击操作按钮触发的事件
    handleOperation (event, scope) {
      this.$emit(event, scope)
    },
    renderColumn (h, column, index) {
      let label = column.attrs.label
      if (label && typeof label === 'function') {
        label = label(this)
      }
      const props = {
        ...column.attrs,
        label: label
      }
      // 多级表头
      if (column.children) {
        return (
          <ElTableColumn key={index} {...{ props }}>
            {column.children.map((item, itemIndex) => this.renderColumn(h, item, itemIndex))}
          </ElTableColumn>
        )
      }
      if (this.isCommonTableColumn(column) && !column.hidden) {
        return <ElTableColumn key={index} {...{ props }} />
      }
      if (!column.hidden) {
        if (column.slot || column.headerSlot) {
          const scopedSlots = {}
          if (column.headerSlot) {
            scopedSlots.header = scope => {
              const headerSlot = this.$scopedSlots
                ? this.$scopedSlots[column.headerSlot]
                : null
              return headerSlot ? headerSlot(scope) : null
            }
          }
          if (column.slot) {
            scopedSlots.default = scope => {
              const slot = this.$scopedSlots
                ? this.$scopedSlots[column.slot]
                : null
              return slot ? slot(scope) : null
            }
          }
          return (
            <ElTableColumn key={index} {...{ props }} {...{ scopedSlots }} />
          )
        }
        if (column.operations) {
          const scopedSlots = {
            default: scope => {
              return column.operations.map(operation => {
                let show = true
                if (operation.show && typeof operation.show === 'function') {
                  show = operation.show(scope)
                }
                // class额外处理
                return show ? (
                  <ElButton
                    key={operation.key}
                    class={operation.attrs.class || ''}
                    {...{ props: operation.attrs }}
                    onClick={() => this.handleOperation(operation.event, scope)}
                  >
                    {operation.name}
                  </ElButton>
                ) : null
              })
            }
          }
          return (
            <ElTableColumn key={index} {...{ props }} {...{ scopedSlots }} />
          )
        }
      }
      return null
    }
  },
  render (h) {
    return (
      <ElTable
        on={this.$listeners}
        attrs={this.$attrs}
        data={this.data}
        empty-text={this.emptyText}
        stripe={this.stripe}
      >
        {this.columns.map((column, index) =>
          this.renderColumn(h, column, index)
        )}
      </ElTable>
    )
  }
}
