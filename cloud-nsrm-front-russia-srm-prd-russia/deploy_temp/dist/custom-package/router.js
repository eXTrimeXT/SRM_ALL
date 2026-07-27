// TODO auto import
const pageLangModules = {
  // 也支持直接输入
  home: {
    'zh-CN': {
      profession: '职业',
    },
  },
  // 支持懒加载
  login: {
    'zh-CN': () => import('./pages/login/lang/zh-CN.js'),
  },
}

const loadPageLanguage = async (i18n, pageLocaleKey) => {
  // 懒加载页面级 i18n message
  // 用 path or name 甚至是 meta.xxx 作为 key 都可以
  const languageMessage = i18n.messages[i18n.locale][pageLocaleKey]
  if (!languageMessage) {
    const pageLangModule = pageLangModules[pageLocaleKey]
    if (pageLangModule) {
      const localeKeys = Object.keys(pageLangModule)
      for (let i = 0; i < localeKeys.length; i += 1) {
        const locale = localeKeys[i]
        let localeMessages = pageLangModule[locale]
        // 判断是否是方法
        if (typeof localeMessages === 'function') {
          localeMessages = await localeMessages().then(res => res.default)
        }

        i18n.mergeLocaleMessage(locale, {
          [pageLocaleKey]: localeMessages,
        })
      }
    }
  }
}

export const install = (router, appInstances) => {
  // @see https://router.vuejs.org/zh/guide/advanced/navigation-guards.html#%E5%85%A8%E5%B1%80%E5%89%8D%E7%BD%AE%E5%AE%88%E5%8D%AB
  router.beforeEach((to, _, next) => {
    loadPageLanguage(appInstances.i18n, to.name || to.path).finally(() => {
      next()
    })
  })
}
