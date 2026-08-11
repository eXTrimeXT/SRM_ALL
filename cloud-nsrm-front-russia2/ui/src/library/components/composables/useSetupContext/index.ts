import { computed, getCurrentInstance } from 'vue-demi'

export function useSetupContext () {
  const instance = getCurrentInstance()
  const root = instance?.proxy

  if (!instance || !root || !instance.proxy) {
    throw new Error('This must be called within a setup function.')
  }

  return {
    app: root,
    currentInstance: instance.proxy,
    router: root.$router,
    route: computed(() => root.$route),
    query: computed(() => root.$route.query),
    params: computed(() => root.$route.params)
  }
}
