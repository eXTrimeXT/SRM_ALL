import { defineComponent, computed } from 'vue-demi'
import { h, connect, mapProps } from '@meicloud/render-engine'
// @ts-ignore
import { usePlaceholder, useHandleClick } from '@meicloud/render-pix'

import type { Button as ElButtonProps } from '@meicloud/element-ui'
import { Button as ElButton } from '@meicloud/element-ui'

export const RButton = connect(
  defineComponent<ElButtonProps & { text?: string; wait?: number }>({
    name: 'RButton',
    props: {
      wait: Number,
      loading: Boolean,
      text: String
    },
    setup (props, { attrs, slots, listeners }) {
      const value = computed(() => props.text ?? attrs.value)
      const placeholder = usePlaceholder(value)

      const { innerLoading, clickDebounce } = useHandleClick(props)

      const handleClick = (e: any) => {
        clickDebounce(e, {
          click: listeners.click
        })
      }

      return () => {
        return h(
          ElButton,
          {
            props: {
              ...props,
              type: attrs.type || 'primary',
              loading: props.loading || innerLoading.value
            },
            attrs,
            on: {
              ...listeners,
              click: handleClick
            }
          },
          {
            default: () => placeholder.value,
            ...slots
          },
        )
      }
    }
  }),
  mapProps({ title: 'text' }),
)
