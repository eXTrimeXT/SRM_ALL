import { Collapse as ElCollapse, CollapseItem as ElCollapseItem, Badge } from '@meicloud/element-ui'
import {
  h,
  useField,
  useFieldSchema,
  Schema,
  SchemaKey,
  RecursionField,
  FragmentDom,
  GeneralField,
} from '@meicloud/render-engine'
import { stylePrefix } from '@meicloud/render-pix/dist/esm/__builtins__'
import { defineComponent, ref, toRaw  } from 'vue-demi'
import { useAutoRun } from '../../composables/useAutoRun'
import { useAutoRunByDebounce } from '../../composables/useAutoRunByDebounce'

type Panels = { name: SchemaKey; props: any; schema: Schema }[]
export const resolveComponent = (
  child?: any,
  props?: Record<string, any>
) => {
  if (child) {
    if (typeof child === 'string' || typeof child === 'number') {
      return child
    } else {
      return h(child, { props }, {})
    }
  }

  return null
}
const usePanels = (
  collapseField: GeneralField,
  schema: Schema,
) => {
  const panels: Panels = []
  schema.mapProperties((schema, name) => {
    const field = collapseField.query(collapseField.address.concat(name)).take()

    if (field?.display === 'none' || field?.display === 'hidden') return

    if (schema['x-component']?.indexOf('CollapseItem') > -1) {
      panels.push({
        name,
        props: {
          ...schema?.['x-component-props'],
          ...field?.componentProps,
          key: field?.componentProps.key || name,
          title: field?.title ?? field?.componentProps.title,
        },
        schema,
      })
    }
  })
  return panels
}

export const Collapse = defineComponent({
  name: 'RenderCollapse',
  props: {
    expandAll: Boolean,
    errorBadge: {
      type: Boolean,
      default: true,
    },
    activeKey: {
      type: [String, Number, Array],
    },
  },
  setup(props, { attrs }) {
    const field = useField()
    const fieldSchema = useFieldSchema()
    const prefixCls = `${stylePrefix}-form-collapse`

    const takeActiveKeys = (panels: Panels) => {
      if (props.activeKey) return props.activeKey
      if (attrs.accordion) return panels[0]?.name
      return panels.map(item => item.name)
    }

    const errorsRef = shallowRef<Record<string, any[]>>({})

    if (props.errorBadge) {
      useAutoRunByDebounce(
        () =>
          field.value.form.queryFeedbacks({
            type: 'error',
            address: field.value.address.concat('*').toString(),
          }),
        errors => {
          const basePath = field.value.address + '.'

          errorsRef.value = errors.reduce((acc: Record<string, any[]>, item) => {
            const panelName = item.address.replace(basePath, '').split('.')[0]
            if (!acc[panelName]) {
              acc[panelName] = []
            }

            acc[panelName].push(item)

            return acc
          }, {})
        },
        316,
      )
    }

    const badgedHeader = (key: SchemaKey, props: any) => {
      return h(
        Badge,
        {
          class: [`${prefixCls}-errors-badge`],
          props: {
            value: errorsRef.value[key]?.length,
          },
        },
        { default: () => resolveComponent(props.title) },
      )
    }

    const hiddenFieldNamesRef = ref<string[]>([])

    // TODO 更精准的渲染
    useAutoRun(() => {
      hiddenFieldNamesRef.value = []
      const names = fieldSchema.value.mapProperties((_, name) => name)

      field.value
        .query(`${field.value.address.toString()}.*(${names.join(',')})`)
        .forEach(cField => {
          if (!cField.visible) {
            hiddenFieldNamesRef.value.push(
              cField.address.segments[cField.address.segments.length - 1] as unknown as string,
            )
          }
        })
    })

    return () => {
      const panels = usePanels(field.value, fieldSchema.value)
      const activeKey = takeActiveKeys(panels)

      return h(
        ElCollapse,
        {
          props: {
            value: activeKey,
          },
        },
        {
          default: () =>
            panels
              .filter(({ name }) => !hiddenFieldNamesRef.value.includes(name))
              .map(({ props: _props, schema, name }, index) => {
                return h(
                  ElCollapseItem,
                  {
                    key: name + String(index),
                    attrs: {
                      id: `collapse_${name}`,
                    },
                    style: props.style,
                    class: props.class,
                    props: {
                      ..._props,
                      title: undefined,
                      name,
                    },
                  },
                  {
                    default: () => [h(RecursionField, { props: { schema, name } }, {})],
                    title: () => props.errorBadge ? badgedHeader(name, _props) : resolveComponent(_props.title),
                  },
                )
              }),
        },
      )
    }
  },
})

export const CollapseItem = defineComponent({
  name: 'CollapseItem',
  setup(_props, { slots }) {
    return () => h(FragmentDom, {}, slots)
  },
})
