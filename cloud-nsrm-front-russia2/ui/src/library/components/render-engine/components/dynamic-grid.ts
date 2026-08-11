import { RecursionField, h, useField, transformSchemas, isEqual } from '@meicloud/render-engine'
import { defineComponent, watch, computed } from 'vue-demi'
import { FormGrid, FormGridProps } from '@meicloud/render-pix'

export const DynamicGrid = defineComponent({
  name: 'RenderDynamicGrid',
  props: {
    ...FormGridProps,
    properties: Object
  },
  setup (props) {
    const fieldRef = useField()

    watch(
      () => props.properties,
      (newVal, oldVal) => {
        console.log(props.properties, 'props.properties')
        if (isEqual(newVal, oldVal)) return
        fieldRef.value.form.clearFormGraph(`${fieldRef.value.address}.*`)
      },
    )

    const propertiesRef = computed(() => {
      if (!props.properties) {
        return []
      }

      const schemas = transformSchemas({ schemas: props.properties })

      return Object.keys(schemas).map(name => ({ name, schema: schemas[name] }))
    })

    return () => {
      return h(
        FormGrid,
        {
          props
        },
        {
          default: () =>
            propertiesRef.value.map(({ name, schema }) =>
              h(
                RecursionField,
                {
                  key: name,
                  props: {
                    name,
                    schema,
                    basePath: fieldRef.value.address,
                    onlyRenderSelf: true
                  }
                },
                {},
              ),
            )
        },
      )
    }
  }
})
