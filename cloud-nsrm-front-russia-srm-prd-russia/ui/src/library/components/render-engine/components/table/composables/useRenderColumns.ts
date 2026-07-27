import { Ref } from 'vue-demi'
import { RecursionField, h, uid, useField, ArrayFieldModel, observable } from '@meicloud/render-engine'
import { ArrayBase } from '@meicloud/render-pix'
import { Tooltip } from '@meicloud/element-ui'
import { resolveComponent } from '@meicloud/render-pix/dist/esm/__builtins__'
import type { ColumnCellRenderParams } from 'vxe-table'
import { parseTableColumnsFromSchema, getRowLocalKey, setRowLocalKey } from '../helper'

// eslint-disable-next-line no-undef
export const useRenderColumns = (
  columns: Ref<ReturnType<typeof parseTableColumnsFromSchema>>,
  performanceMode = false,
) => {
  const arrayField = useField<ArrayFieldModel>()

  const rowKeyMap = new WeakMap()
  const getRowKey = (record: any, index?: number) => {
    if (rowKeyMap instanceof WeakMap) {
      if (!getRowLocalKey(record)) {
        setRowLocalKey(record)
      }
      return `${rowKeyMap.get(record)}-${index}`
    }

    // @ts-ignore
    if (!rowKeyMap?.[index]) {
      // @ts-ignore
      rowKeyMap[index] = uid()
    }

    // @ts-ignore
    return `${rowKeyMap[index]}-${index}`
  }

  const renderColumns = observable.computed(() =>
    columns.value
      .filter(col => col.display === 'visible')
      .map((col, idx) =>
        h(
          'vxe-column',
          { key: `${col.schema.name}_${col.index ?? idx}`, props: col.columnProps },
          {
            header: () => {
              return h(
                'div',
                {
                  style: 'display: flex; align-items: center;'
                },
                {
                  default: () =>
                    [
                      col.required &&
                        h(
                          'i',
                          {
                            staticClass: 'vxe-cell--required-icon',
                            style: 'top: -2px; position: relative;'
                          },
                          {},
                        ),
                      col.columnProps.title,
                      col.columnProps.description &&
                        h(
                          Tooltip,
                          {
                            props: {
                              placement: 'top'
                            }
                          },
                          {
                            default: () =>
                              h('i', { 
                                staticClass: (!!col.columnProps.icon ? col.columnProps.icon : 'el-icon-warning')  + ' description-tips'
                              }, {}),
                            content: () =>
                              h(
                                'div',
                                {},
                                {
                                  default: () => col.columnProps.description
                                },
                              )
                          },
                        )
                    ].filter(Boolean)
                },
              )
            },
            default: (props: ColumnCellRenderParams) => {
              // 内部自己维护的 key
              const currentRowKey = `${props.column.field}_${getRowLocalKey(props.row)}`

              if (performanceMode && col.columnProps.performanceMode !== false) {
                return h(
                  'span',
                  {
                    key: currentRowKey
                  },
                  {
                    default: () => props.row[props.column.field]
                  },
                )
              }

              return h(
                ArrayBase.Item,
                {
                  props: { index: props.$rowIndex, record: props.row },
                  key: currentRowKey
                },
                {
                  default: () =>
                    h(
                      RecursionField,
                      {
                        key: props.rowIndex,
                        props: {
                          schema: col.schema,
                          name: col.schema.name,
                          basePath: arrayField.value.address.concat(props.rowIndex)
                        }
                      },
                      {},
                    )
                },
              )
            }
          },
        ),
      ),
  )

  return {
    rowKeyMap,
    getRowKey,
    renderColumns
  }
}
