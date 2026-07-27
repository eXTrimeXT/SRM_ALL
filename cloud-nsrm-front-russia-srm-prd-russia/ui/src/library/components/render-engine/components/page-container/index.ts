import './index.scss'

import { isStr, h } from '@meicloud/render-engine'
import { defineComponent } from 'vue-demi'

export const PageContainer = defineComponent({
  name: 'RenderPageContainer',
  props: {
    direction: {
      type: String,
      default: 'vertical'
    }
  },
  setup (props, { attrs, slots }) {
    return () => {
      const flexDirection = props.direction === 'vertical' ? 'column' : 'row'

      return h(
        'section',
        {
          class: 'render-page-container'
        },
        {
          default: () =>
            h(
              'div',
              {
                class: 'render-page-container__body',
                ...attrs,
                style: isStr(attrs.style)
                  ? `flex-direction: ${flexDirection};` + attrs.style
                  // @ts-ignore
                  : { ...attrs.style, flexDirection }
              },
              slots,
            )
        },
      )
    }
  }
})
