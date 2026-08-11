import VueI18n from 'vue-i18n'
import { useSetupContext } from '../useSetupContext'

/**
 * @deprecated 模拟 vue-i18n-next 的 useI18n
 */
export const useI18n = (): any => {
  const { app, currentInstance } = useSetupContext()

  // @see https://github.com/kazupon/vue-i18n/issues/259
  const t = app.$t.bind(currentInstance) as typeof VueI18n.prototype.t

  return {
    t,
    i18n: app.$i18n,
  }
}
