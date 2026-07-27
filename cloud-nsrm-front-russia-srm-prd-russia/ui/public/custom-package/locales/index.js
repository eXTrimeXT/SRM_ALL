import zhCN from './zh-CN/index.js'

export const install = (i18n) => {
  // @see https://kazupon.github.io/vue-i18n/zh/api/#mergelocalemessage-locale-message
  i18n.mergeLocaleMessage('zh-CN', zhCN)
}
