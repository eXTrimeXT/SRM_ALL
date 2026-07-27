/* eslint-disable no-unused-expressions */
import { defineComponent, provide, computed, ref } from 'vue-demi'
import { Dialog } from '@meicloud/element-ui'
import { ButtonList as RenderButtonList } from '@meicloud/render-pix'
// @ts-ignore
import { t } from '@meicloud/element-ui/src/locale'
import {
  h,
  RecursionField,
  useFieldSchema,
  observer,
  useField,
  observable,
  lazyMerge,
  SchemaExpressionScopeSymbol,
  useExpressionScope,
  Schema,
  useAutoRun
} from '@meicloud/render-engine'
import { resolveComponent } from '@meicloud/render-pix/dist/esm/__builtins__'
import { useAutoRunByDebounce } from '@/library/components/composables/useAutoRunByDebounce'

const sizes: Record<string, string> = {
  small: '420px',
  middle: '650px',
  large: '1080px',
  xLarge: '1200px'
}

export const RDialog = observer(
  defineComponent({
    name: 'SchemaDialog',
    inheritAttrs: false,
    setup (_, { attrs, listeners }) {
      const field = useField()
      const fieldSchema = useFieldSchema()

      const visible = observable.computed(() => field.value.componentProps.visible ?? false)

      const width: string = sizes[field.value.componentProps.size] ?? '650px'

      const innerClosed = () => {
        field.value.componentProps.visible = false
      }

      /**
       *
       * @param {scope|ok|cancel} type
       * cancel 点击取消按钮
       * ok 点击确定按钮
       * scope 通过作用域方法进行关闭
       */
      const closed = (type = 'scope') => {
        if (attrs.beforeClose) {
          // @ts-ignore
          attrs.beforeClose(innerClosed, type)
          return
        }

        innerClosed()
      }

      const scopeRef = useExpressionScope()

      const expressionScopeRef = computed(() =>
        // @ts-ignore
        lazyMerge(scopeRef.value, {
          $closed: closed
        }),
      )

      provide(SchemaExpressionScopeSymbol, expressionScopeRef)

      const listRef = ref([])

      // TODO cancelButton、okButton 是否也需要响应？
      const cancelButton =
        attrs.cancelButtonProps == false
          ? null
          : {
              text: resolveComponent(attrs.cancelText || t('el.popconfirm.cancelButtonText')),
              ...(attrs.cancelButtonProps ?? {}),
              click: (e: MouseEvent) => {
                // @ts-ignore
                listeners.cancel?.(e)
                closed('cancel')
              }
            }

      const okButton =
        attrs.okButtonProps == false
          ? null
          : {
              type: 'primary',
              text: resolveComponent(attrs.okText || t('el.popconfirm.confirmButtonText')),
              ...(attrs.okButtonProps ?? {}),
              click: (e: MouseEvent) => {
                // @ts-ignore
                listeners.ok?.(e)
                closed('ok')
              }
            }

      useAutoRunByDebounce(
        () => {
           // @ts-ignore
          const footerButtonList = Schema.shallowCompile(attrs.footerButtonList, scopeRef.value)

          return footerButtonList?.(closed, { cancelButton, okButton })
        },
        footerButtonList => {
          listRef.value = footerButtonList ?? [cancelButton, okButton].filter(Boolean)
        },
        66,
      )

      
      // 内部实现 el-dialog 的 destroyOnClose
      const rerender = ref(true)
      useAutoRun(() => {
        const destroyOnClose = attrs['destroy-on-close'] ?? attrs.destroyOnClose

        if (destroyOnClose) {
          rerender.value = field.value.componentProps.visible
        }
      })

      return () => {
        return h(
          Dialog,
          {
            props: {
              width: width,
              title: field.value.title,
              appendToBody: true,
              ...attrs,
              visible: visible.value,
              destroyOnClose: undefined
            },
            on: {
              ...listeners,
              'update:visible': (val: boolean) => {
                field.value.componentProps.visible = val
              }
            }
          },
          {
            default: () =>
              rerender.value ? h(
                RecursionField,
                {
                  props: {
                    schema: fieldSchema.value,
                    basePath: field.value.address,
                    onlyRenderProperties: true
                  }
                },
                {},
              ) : null,

            ...(attrs.footer === false
              ? {}
              : {
                  footer: () =>
                    h(
                      'div',
                      {},
                      {
                        default: () => {
                          return h(
                            RenderButtonList,
                            {
                              props: {
                                ...(attrs.footerButtonListProps ?? {}),
                                list: listRef.value
                              }
                            },
                            {},
                          )
                        }
                      },
                    )
                })
          },
        )
      }
    }
  }),
)
