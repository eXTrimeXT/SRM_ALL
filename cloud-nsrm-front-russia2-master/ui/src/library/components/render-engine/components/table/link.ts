import './index.scss'

import { defineComponent, computed } from 'vue-demi'
import { h, connect, mapProps, useField, useExpressionScope } from '@meicloud/render-engine'
// @ts-ignore
import { usePlaceholder, ArrayBase, useHandleClick } from '@meicloud/render-pix'

import type { Button as ElButtonProps } from '@meicloud/element-ui'

export const RenderTableLink = connect(
  defineComponent<ElButtonProps & { text?: string; tips?: string; wait?: number }>({
    name: 'RenderTableLink',
    props: {
      wait: Number,
      text: String,
    },
    setup(props, { attrs, slots, listeners }) {
      const value = computed(() => props.text ?? attrs.value)
      const placeholder = usePlaceholder(value)

      const fieldRef = useField()
      const scopeRef = useExpressionScope()!

      const { clickDebounce } = useHandleClick(props)

      const handleClick = (e: any) => {
        e.preventDefault()

        clickDebounce(e, {
          click: () => {
            if (!value.value) {
              return
            }

            ;(listeners.click as undefined | Function)?.(
              {
                row: scopeRef.value.$table.getRowByIndex(fieldRef.value.index),
                rowIndex: fieldRef.value.index,
              },
              e,
            )
          },
        })
      }

      return () => {
        // TODO 使用 route-link
        return h(
          'a',
          {
            staticClass: 'render-table__link',
            attrs: {
              href: 'javascript:;',
              ...attrs,
            },
            on: {
              click: handleClick,
            },
          },
          {
            default: () => placeholder.value,
            ...slots,
          },
        )
      }
    },
  }),
  mapProps({ title: 'text' }),
)
