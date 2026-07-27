import { defineComponent } from 'vue-demi'
import {
  h,
  observer,
  FragmentComponent,
  observable,
  useExpressionScope,
} from '@meicloud/render-engine'
import {
  ButtonList as RenderButtonList,
  useParseButtonListProps,
  ButtonListProps,
} from '@meicloud/render-pix'

export const RenderTableButtonList = observer(
  defineComponent({
    name: 'RenderTableButtonList',
    inheritAttrs: false,
    props: {
      text: {
        type: Boolean,
        default: true,
      },
      size: {
        type: Number,
        default: 12,
      },
      // 最多显示个数，超出将下拉显示
      max: {
        type: Number,
        default: 5,
      },
      trigger: String,
      placement: String,
      loading: Boolean,
    },
    setup(props, { slots, attrs }) {
      const { listDataRef, fieldRef, renderStateManager } = useParseButtonListProps()

      const scopeRef = useExpressionScope()!

      const listRef = observable.computed(() =>
        listDataRef.value.reduce((acc, item) => {
          if (item.display === 'visible') {
            acc.push({
              text: item.field?.title,
              click: () => {
                const clickFN = item.componentProps['@click'] ?? item.componentProps['onClick']
                clickFN?.({
                  row: scopeRef.value.$table.getRowByIndex(fieldRef.value.index),
                  rowIndex: fieldRef.value.index,
                })
              },
              ...item.componentProps,
            })
          }

          return acc
        }, [] as ButtonListProps['list']),
      )

      return () => {
        return h(
          FragmentComponent,
          {},
          {
            default: () => [
              h(
                RenderButtonList,
                {
                  attrs,
                  props: {
                    ...props,
                    list: listRef.value,
                    style: fieldRef.value.component?.[1].style,
                    class: fieldRef.value.component?.[1].class,
                  },
                },
                slots,
              ),
              renderStateManager(),
            ],
          },
        )
      }
    },
  }),
)
