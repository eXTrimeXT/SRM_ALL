<script lang="jsx">
export default {
  name: 'IPlusTable',
  props: ['columns'],
  methods: {
    getAttrs (h, column) {
      const self = this
      const scopedSlots =
        column.slot || column.render || column.component
          ? {
              scopedSlots: {
                default (scope) {
                  const exportVal = {
                    column: scope.column,
                    $index: scope.$index,
                    row: scope.row
                  }
                  if (column.slot) {
                    return self.$scopedSlots[column.slot](exportVal)
                  } else if (column.render) {
                    return column.render(h, exportVal)
                  } else if (column.component) {
                    return h(column.component)
                  }
                }
              }
            }
          : {}

      return {
        ...scopedSlots,
        attrs: {
          ...column,
          'show-overflow-tooltip': false,
          align: column.align || 'left'
        }
      }
    }
  },
  render (h) {
    const listeners = {
      on: {
        'selection-change': (row) => this.$emit('selection-change', row)
      }
    }

    return (
      <el-table
        {...{ attrs: this.$attrs }}
        {...{ props: this.$props }}
        {...{ listeners: this.$listeners }}
        {...listeners}
        ref="table"
      >
        {this.columns.map((column) => {
          return (
            <el-table-column key={column.prop} {...this.getAttrs(h, column)}>
              {column.children &&
                column.children.map((c) => {
                  return <el-table-column key={c.prop} {...this.getAttrs(h, c)} />
                })}
            </el-table-column>
          )
        })}
      </el-table>
    )
  }
}
</script>
