import { defineComponent } from 'vue-demi'
import {
  useFieldSchema,
  useField,
  RecursionField,
  h,
  FragmentComponent
} from '@meicloud/render-engine'

// TODO 优化渲染
export const Steps = defineComponent({
  name: 'RenderSteps',
  setup (_, { attrs }) {
    const fieldRef = useField()
    const fieldSchema = useFieldSchema()

    return () => {
      const steps = fieldSchema.value.mapProperties((schema: any, name: string) => {
        const field = fieldRef.value.query(fieldRef.value.address.concat(name)).take()

        if (field?.display === 'none' || field?.display === 'hidden') return

        return {
          name: schema.name,
          props: field?.componentProps ?? {}
        }
      }).filter(Boolean)

      return h(
        FragmentComponent,
        {},
        {
          default: () => [
            h(
              'el-steps',
              {
                attrs
              },
              {
                default: () =>
                  steps.map((item: Record<string, any>) => {
                    return h(
                      'el-step',
                      {
                        key: item.name,
                        props: item.props
                      },
                      {}
                    )
                  })
              },
            ),
            ...steps.map((item: Record<string, any>) =>
              h(
                RecursionField,
                {
                  props: {
                    name: item.name,
                    schema: {
                      ...fieldSchema.value.properties[item.name],
                      'x-component': 'Fragment'
                    }
                  }
                },
                {},
              ),
            )
          ]
        },
      )
    }
  }
})
