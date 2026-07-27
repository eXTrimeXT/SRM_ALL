import './index.scss'

import { defineComponent, computed } from 'vue-demi'
import { h, connect, mapProps, useField, useExpressionScope } from '@meicloud/render-engine'
// @ts-ignore
import { usePlaceholder, ArrayBase } from '@meicloud/render-pix'

import type { Button as ElButtonProps } from '@meicloud/element-ui'
import { Popconfirm as ElPopconfirm, Button as ElButton } from '@meicloud/element-ui'

export const TableButton = connect(
  defineComponent<ElButtonProps & { text?: string; tips?: string; showPopconfirm?: Boolean }>({
    name: 'TableButton',
    props: {
      text: String,
      title: {
        type: String,
        default: '',
      },
      showPopconfirm: {
        type: Boolean,
        default: false,
      },
    },
    setup(props, { attrs, slots, listeners }) {
      const value = computed(() => props.text ?? attrs.value)
      const placeholder = usePlaceholder(value)

      const fieldRef = useField()
      const scopeRef = useExpressionScope()!

      return () => {
        if (props.showPopconfirm) {
          return h(
            ElPopconfirm,
            {
              attrs: { ...props, icon: 'el-icon-warning', cancelButtonType: 'default' },
              on: {
                confirm: (e: MouseEvent) => {
                  listeners.confirm?.(
                    {
                      row: scopeRef.value.$table.getRowByIndex(fieldRef.value.index),
                      rowIndex: fieldRef.value.index,
                    },
                    e,
                  )
                },
              },
            },
            {
              reference: () =>
                h(
                  ElButton,
                  {
                    class: !value.value && 'render-table__table-button-readonly',
                    attrs: { type: 'text', ...attrs },
                  },
                  {
                    default: () => placeholder.value,
                    ...slots,
                  },
                ),
            },
          )
        } else {
          return h(
            ElButton,
            {
              class: !value.value && 'render-table__table-button-readonly',
              attrs,
              on: {
                click: (e: MouseEvent) => {
                  if (!value.value) {
                    return
                  }

                  listeners.click?.(
                    {
                      row: scopeRef.value.$table.getRowByIndex(fieldRef.value.index),
                      rowIndex: fieldRef.value.index,
                    },
                    e,
                  )
                },
              },
            },
            {
              default: () => placeholder.value,
              ...slots,
            },
          )
        }
      }
    },
  }),
  mapProps({ title: 'text' }),
)
