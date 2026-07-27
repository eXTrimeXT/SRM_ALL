import { autorun, Reaction } from '@meicloud/render-engine'
import { onUnmounted } from 'vue-demi'

// 回收到 render-engine
export const useAutoRun = (tracker: Reaction, name?: string) => {
  const dispose = autorun(tracker, name)

  onUnmounted(() => {
    dispose()
  })
}
