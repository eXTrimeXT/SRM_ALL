export const install = (appInstances) => {
  appInstances.app.provide('RenderEngineGlobalScope', {
    // $t: appInstances.i18n.t.bind(appInstances.i18n),
  })
}
