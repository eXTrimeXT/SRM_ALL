import { useFieldSchema, RecursionField, h } from '@meicloud/render-engine'
import { defineComponent } from 'vue-demi'

const alignMap = {
  left: 'flex-start',
  center: 'center',
  right: 'flex-end',
} as const

export const FixedFooter = defineComponent({
  name: 'FixedFooter',
  props: {
    width: {
      type: String,
      default: '100%',
    },
    align: {
      type: String,
      default: 'right',
    },
    gap: {
      type: String,
      default: '8px',
    },
  },
  setup(props) {
    const fieldSchema = useFieldSchema()

    return () => {
      return h(
        'div',
        {
          class: 'render-form-container__fixed-footer',
        },
        {
          default: () =>
            h(
              'div',
              {
                style: {
                  width: props.width,
                },
                class: 'render-form-container__fixed-footer-content',
                staticStyle: {
                  gap: props.gap,
                  justifyContent: alignMap[props.align as 'left'],
                },
              },
              {
                default: () =>
                  h(
                    RecursionField,
                    {
                      props: {
                        schema: fieldSchema.value.items,
                        onlyRenderProperties: true,
                      },
                    },
                    {},
                  ),
              },
            ),
        },
      )
    }
  },
})
