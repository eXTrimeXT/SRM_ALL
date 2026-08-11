import { useEventListener, Arrayable } from '@vueuse/core'
import { ref, onMounted } from 'vue-demi'
import { useSetupContext } from './useSetupContext'

export const useEventListenerByRefName = <E extends keyof WindowEventMap>(
  refName: string,
  event: Arrayable<E>,
  listener: Arrayable<(this: Window, ev: WindowEventMap[E]) => any>,
  options?: boolean | AddEventListenerOptions,
) => {
  const { currentInstance } = useSetupContext()
  const element = ref<HTMLDivElement>()

  useEventListener(element, event, listener, options)

  onMounted(() => {
    element.value = currentInstance.$refs?.[refName] as unknown as HTMLDivElement
  })
}
