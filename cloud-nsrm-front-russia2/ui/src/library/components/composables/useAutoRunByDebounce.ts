import { Reaction } from '@meicloud/render-engine'
import { useDebounceFn } from '@vueuse/core'
import { useAutoRun } from './useAutoRun'

export const useAutoRunByDebounce = (
  tracker: Reaction,
  fn: (...args: any[]) => void,
  delay = 66,
) => {
  const debounceFn = useDebounceFn(fn, delay)

  useAutoRun(() => {
    const result = tracker()

    debounceFn(result)
  })
}
