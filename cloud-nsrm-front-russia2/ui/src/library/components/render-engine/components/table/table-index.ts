/* eslint-disable no-unused-expressions */
import { defineComponent, watch } from 'vue-demi'
import { h, useField } from '@meicloud/render-engine'

const prefixCls = 'render-table-'

export const RenderTableIndex = defineComponent({
  name: 'RenderTableIndex',
  setup(_, { attrs, listeners }) {
    const fieldRef = useField()

    watch(
      () => fieldRef.value.index,
      () => {
        ;(listeners.changeIndex as undefined | Function)?.(fieldRef.value.index)
      },
      { immediate: true },
    )

    return () => {
      return h(
        'span',
        {
          class: `${prefixCls}-index`,
          attrs,
        },
        {
          default: () => [`${Number(fieldRef.value.index) + 1}`],
        },
      )
    }
  },
})
