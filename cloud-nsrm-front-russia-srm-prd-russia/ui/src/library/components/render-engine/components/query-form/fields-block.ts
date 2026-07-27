import { defineComponent, shallowRef } from 'vue-demi'
import { h, RecursionField, Schema, useField } from '@meicloud/render-engine'
// @ts-ignore
import { FormGrid, FormGridProps } from '@meicloud/render-pix'

export const FieldsBlock = defineComponent<{ grid: typeof FormGridProps; schema: Schema[] }>({
  name: 'QueryFormFieldsBlock',
  props: {
    schema: {
      type: Array,
      default: [],
    },
    grid: Object,
  },
  setup(props, { expose, emit }) {
    const fieldRef = useField()

    const containerDomRef = shallowRef<HTMLDivElement | undefined>()
    const containerHeightInfoRef = shallowRef({
      min: 0,
      max: 0,
    })

    const setContainerHeight = (num: number) => {
      if (!containerDomRef.value) {
        return
      }

      containerDomRef.value.style.height = num + 'px'
    }

    // TODO 监听容器变动，重新计算 max
    const initContainerHeight = () => {
      setTimeout(() => {
        const contextContainerDom = (containerDomRef.value as HTMLElement)?.querySelector(
          '.render-pix-form-grid',
        )

        if (!contextContainerDom) {
          return
        }

        // targetContainerDomRef.value.children
        const firstNode = contextContainerDom.children[0]
        if (!firstNode) {
          contextContainerDom
          return
        }

        const firstNodeStyle = window.getComputedStyle(firstNode)

        // 默认每一个 col 的高度是相同的
        containerHeightInfoRef.value.min = [
          firstNode.clientHeight,
          parseInt(firstNodeStyle.marginTop),
          parseInt(firstNodeStyle.marginBottom),
        ]
          .filter(num => !Number.isNaN(num))
          .reduce((acc, num) => acc + num, 0)

        setContainerHeight(containerHeightInfoRef.value.min)

        const nodes = contextContainerDom.children as unknown as HTMLDivElement[]
        containerHeightInfoRef.value.max = Array.prototype.reduce.call(
          nodes,
          (count, _, idx) => {
            const prevOffsetTop = nodes[idx - 1]?.offsetTop || 0
            const currentOffsetTop = nodes[idx]?.offsetTop || 0

            if (prevOffsetTop === currentOffsetTop) {
              return count
            }

            return (count as number) + containerHeightInfoRef.value.min
          },
          0,
        ) as number

        emit('equalHeight', containerHeightInfoRef.value.max <= containerHeightInfoRef.value.min)
      }, 16.6)
    }

    onMounted(() => {
      initContainerHeight()
    })

    const heightToggle = (bool: boolean) => {
      setContainerHeight(bool ? containerHeightInfoRef.value.max : containerHeightInfoRef.value.min)
    }

    expose({
      heightToggle,
      initContainerHeight,
    })

    const style =
      'flex: 1; height: 44px; overflow: hidden; transition: height 0.3s cubic-bezier(0.215, 0.61, 0.355, 1) 0s;'

    return () => {
      return h(
        'div',
        {
          style,
          ref: containerDomRef,
        },
        {
          default: () =>
            h(
              FormGrid,
              {
                props: props.grid,
              },
              {
                default: () =>
                  props.schema.map(schema =>
                    h(
                      RecursionField,
                      {
                        key: schema.name,
                        props: {
                          name: schema.name,
                          schema,
                        },
                        basePath: fieldRef.value.address,
                      },
                      {},
                    ),
                  ),
              },
            ),
        },
      )
    }
  },
})
