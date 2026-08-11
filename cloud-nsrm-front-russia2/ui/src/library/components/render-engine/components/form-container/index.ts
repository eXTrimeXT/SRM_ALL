import './index.scss'

import { isStr, h } from '@meicloud/render-engine'
import { defineComponent, onMounted, ref } from 'vue-demi'
import { useSetupContext } from '../../../composables/useSetupContext'

import { FixedFooter } from './fixed-footer'

export const FormContainer = defineComponent({
  name: 'FormContainer',
  props: {
    direction: {
      type: String,
      default: 'vertical'
    },
    footerAlign: String
  },
  setup (props, { attrs, slots }) {
    return () => {
      const flexDirection = props.direction === 'vertical' ? 'column' : 'row'

      const { currentInstance } = useSetupContext()

      const containerWidthRef = ref('100%')

      onMounted(() => {
        containerWidthRef.value = `${currentInstance.$el.clientWidth}px`
      })

      return h(
        'div',
        {
          class: 'render-form-container',
          ...attrs,
          style: isStr(attrs.style)
            ? `flex-direction: ${flexDirection};` + attrs.style
            // @ts-ignore
            : { ...attrs.style, flexDirection }
        },
        {
          default: () =>
            [
              slots.default?.(),
              h(
                FixedFooter,
                { props: { width: containerWidthRef.value, align: props.footerAlign } },
                {},
              )
            ].filter(Boolean)
        },
      )
    }
  }
})
