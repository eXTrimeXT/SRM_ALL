import { defineComponent, Ref, shallowRef, ref, h } from 'vue-demi'
import {
  useFieldSchema,
  Schema,
  observer,
  useField,
  FieldModel,
  expression,
  lazyMerge,
  SchemaExpressionScopeSymbol,
  useExpressionScope,
  i18nExpression,
} from '@meicloud/render-engine'
import { FormGridProps, FormLayout } from '@meicloud/render-pix'
import { ToolbarBlock } from './toolbar-block'
import { FieldsBlock } from './fields-block'
import { useEventListenerByRefName } from '@/library/components/composables/useEventListenerByRefName'
import { useAutoRun } from '@/library/components/composables/useAutoRun'

const defaultToolbarSchema = new Schema({
  name: 'toolbar',
  type: 'void',
  'x-component': 'ButtonList',
  'x-query-form-toolbar': true,
  properties: {
    query: {
      title: i18nExpression('components.common.search'),
      'x-component-props': {
        type: 'primary',
        '@click': expression(`(e) => {
          $emits('query', e)
        }`),
      },
    },
    reset: {
      title: i18nExpression('components.common.reset'),
      'x-component-props': {
        '@click': expression(`(e) => {
          $emits('reset', e)
        }`),
      },
    },
  },
})

export const QueryFormProps = {
  ...FormGridProps,
  /**
   * 自动给下级 schema 添加 x-decorator=FormItem 减少配置
   */
  autoAddDecoratorToField: {
    type: Boolean,
    default: true,
  },
  maxColumns: {
    ...FormGridProps.maxColumns,
    default: 3,
  },
  minColumns: {
    default: 3,
  },
  columnGap: {
    ...FormGridProps.columnGap,
    default: 16,
  },
  rowGap: {
    ...FormGridProps.columnGap,
    default: 0,
  },
  collapsible: {
    type: Boolean,
    default: true,
  },
  labelCol: {
    type: Number,
    default: 7,
  },
  labelWidth: Number,
}

export const BaseQueryForm = observer(
  defineComponent({
    name: 'BaseQueryForm',
    inheritAttrs: false,
    props: QueryFormProps,
    setup(props, { listeners }) {
      const fieldRef = useField() as unknown as FieldModel
      const fieldSchema = useFieldSchema() as Ref<Schema>
      const scopeRef = useExpressionScope()

      const fieldsBlockRef = shallowRef()
      const collapsibleRef = ref(props.collapsible)

      // @ts-ignore
      const emits = (eventName: string, ...args: any[]) => listeners[eventName]?.(...args)

      // 用于监听重新计算
      // TODO 监听容器变化会更好
      fieldRef.value.setData({
        reCalcContainerHeight: 0,
      })

      useEventListenerByRefName('queryForm', 'keydown', event => {
        if (event.key === 'Enter') {
          emits('query', event)
        }
      })

      const properties = fieldSchema.value.reduceProperties(
        (acc, schema) => {
          if (schema['x-query-form-toolbar']) {
            acc.toolbar.push(schema)
          } else {
            if (!schema['x-decorator'] && props.autoAddDecoratorToField) {
              schema['x-decorator'] = 'FormItem'
              schema['x-decorator-props'] = {
                labelCol: props.labelCol,
                labelWidth: props.labelWidth,
                ...(schema['x-decorator-props'] ?? {}),
              }
            }

            acc.fields.push(schema)
          }

          return acc
        },
        {
          fields: [] as Schema[],
          toolbar: [] as Schema[],
        },
      )

      useAutoRun(() => {
        const reCalcContainerHeight = fieldRef.value?.data?.reCalcContainerHeight
        if (reCalcContainerHeight) {
          fieldsBlockRef.value?.initContainerHeight()
        }
      })

      // 如果没有传递，那么就使用默认
      if (properties.toolbar.length === 0) {
        properties.toolbar.push(defaultToolbarSchema)
      }

      // 简单通讯
      const handleCollapsToggle = (bool: boolean) => {
        fieldsBlockRef.value?.heightToggle(bool)
      }

      const handleEqualHeight = (bool: boolean) => {
        collapsibleRef.value = !bool
      }

      // TODO 简化 expressionScope 传递
      const expressionScopeRef = computed(() =>
        lazyMerge(scopeRef!.value, {
          $emits: emits,
        }),
      )

      provide(SchemaExpressionScopeSymbol, expressionScopeRef)

      return () => {
        return h(
          FormLayout,
          {
            props: {
              colon: false,
              labelWrap: true,
              labelAlign: 'right',
              layout: 'horizontal',
            },
          },
          [
            h(
              'div',
              {
                style: 'display: inline-flex; gap: 16px; width: 100%',
                ref: 'queryForm',
              },
              [
                h(FieldsBlock, {
                  ref: fieldsBlockRef,
                  props: {
                    schema: properties.fields,
                    grid: props,
                  },
                  on: {
                    equalHeight: handleEqualHeight,
                  },
                }),
                h(ToolbarBlock, {
                  props: {
                    schema: properties.toolbar,
                    collapsible: collapsibleRef.value,
                  },
                  on: {
                    toggle: handleCollapsToggle,
                  },
                }),
              ],
            ),
          ],
        )
      }
    },
  }),
)
