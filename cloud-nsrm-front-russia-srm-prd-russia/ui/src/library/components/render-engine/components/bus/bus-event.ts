import { FragmentComponent, h, useExpressionScope, useFieldSchema, SchemaOptionsSymbol } from '@meicloud/render-engine'
import { onUnmounted } from 'vue-demi'
import { useDebounceFn } from '@vueuse/core'

export const BusEvent = defineComponent({
  name: 'BusEvent',
  props: {
    eventName: {
      type: String,
    },
    componentTag: {
      type: [String, Object],
    }
  },
  setup(props, { listeners, slots }) {
    const fieldSchema = useFieldSchema()
    const scopeRef = useExpressionScope()
    const optionsRef = inject(SchemaOptionsSymbol, ref(null))

    const innerEventName = computed(() => props.eventName ?? fieldSchema.value.name)

    const handleBusEvent = useDebounceFn((...args: any[]) => {
      listeners.listener?.(...args)
    }, 116)

    onMounted(() => {
      scopeRef!.value.$bus.$on(innerEventName.value, handleBusEvent)
    })

    onUnmounted(() => {
      scopeRef!.value.$bus.$off(innerEventName.value, handleBusEvent)
    })


    return () => {
      return h(optionsRef.value?.components[props.componentTag] ?? FragmentComponent, {}, slots)
    }
  },
})
