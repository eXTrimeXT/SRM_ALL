import { defineComponent } from 'vue-demi'
import { RecursionField, h, useField, useFieldSchema } from '@meicloud/render-engine'

export const HTMLElement = defineComponent({
  name: 'HTMLElementWrapper',
  props: {
    componentTag: {
      type: String,
      default: 'div'
    }
  },
  setup (props, { attrs }) {
    const field = useField()
    const fieldSchema = useFieldSchema()

    return () => {
      return h(
        props.componentTag,
        {
          attrs
        },
        {
          default: () =>
            h(
              RecursionField,
              {
                props: {
                  schema: fieldSchema.value,
                  basePath: field.value.address,
                  onlyRenderProperties: true
                }
              },
              {},
            )
        },
      )
    }
  }
})
