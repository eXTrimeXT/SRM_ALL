import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import elementEnLocale from '@meicloud/element-ui/lib/locale/lang/en' // 'element-ui/lib/locale/lang/en' // element-ui lang
import elementZhLocale from '@meicloud/element-ui/lib/locale/lang/zh-CN' // 'element-ui/lib/locale/lang/zh-CN'// element-ui lang
import elementJaLocale from '@meicloud/element-ui/lib/locale/lang/ja' // 'element-ui/lib/locale/lang/ja'// element-ui lang

import renderPIXEn from '@meicloud/render-pix/dist/esm/locale/lang/en'
import renderPIXZh from '@meicloud/render-pix/dist/esm/locale/lang/zh'
import renderPIXJa from '@meicloud/render-pix/dist/esm/locale/lang/ja'
import renderTableZhCN from '@meicloud/render-table/dist/esm/locale/lang/zh-CN'
import renderTableEnUS from '@meicloud/render-table/dist/esm/locale/lang/en-US'
import renderTableJaJP from '@meicloud/render-table/dist/esm/locale/lang/ja-JP'

import enLocale from './en.json'
import zhLocale from './zh.json'
import jaLocale from './ja.json'
import quickSearchEnLocale from './quick-search-en'
import quickSearchZhLocale from './quick-search-zh'
import quickSearchJaLocale from './quick-search-ja'

// 项目二开时修改引入
import zhLocaleCus from './langCus/zh.json'
import enLocaleCus from './langCus/en.json'
import jaLocaleCus from './langCus/ja.json'
import quickSearchZhLocaleCus from './langCus/quick-search-zh'
import quickSearchEnLocaleCus from './langCus/quick-search-en'
import quickSearchJaLocaleCus from './langCus/quick-search-ja'
/**
 * [\u4e00-\u9fa5]
 * 多语言界面调用方法 如：导出
 * 1、模板 属性变量如  :label="$t('common.export')"
 * 2、插值  {{ $t("common.export") }}
 * 3、js消息提示  => this.$t("common.export")
 * 4、如果使用TableView：
  {
    prop: 'vendorCode',
    label: () => this.$t('common.export'),
  }
 * 5、如果使用form-wrapper： 属性入参formArray
    {
    prop: 'vendorCode',
    label: () => this.$t('common.export'),
    }
 * 6、如果使用NavTabs： 参数设置如下
    tabs: [{
      title: () => this.$t('vendorMod.mtTrial'),//物料试用
      name: 'materialTrialList',
      component: materialTrialList,
      closable: false
    }]
* 7、路由多语言
    {
      path: "dashboard",
      component: () => import("@/views/dashboard/index"),
      name: "dashboard",
      meta: { title: "route.dashboard"} //  route.dashboard 对应词条
    }
 */

Vue.use(VueI18n)

const messages = {
  'zh_CN': {
    ...zhLocale,
    ...renderPIXZh,
    ...renderTableZhCN,
    ...elementZhLocale,
    ...quickSearchZhLocale,
    ...zhLocaleCus,
    ...quickSearchZhLocaleCus
  },
  'en_US': {
    ...enLocale,
    ...renderPIXEn,
    ...renderTableEnUS,
    ...elementEnLocale,
    ...quickSearchEnLocale,
    ...enLocaleCus,
    ...quickSearchEnLocaleCus
  },
  'ja_JP': {
    ...jaLocale,
    ...renderPIXJa,
    ...renderTableJaJP,
    ...elementJaLocale,
    ...quickSearchJaLocale,
    ...jaLocaleCus,
    ...quickSearchJaLocaleCus
  }
}
// 用户设置的语言类型|浏览器语言|默认语言
export function initLanguage (userLang) {
  const chooseLanguage = Cookies.get('language')
  if (chooseLanguage) return chooseLanguage

  // if has not choose language
  const navLang = navigator.language || navigator.browserLanguage
  const localLang = (navLang === 'zh-CN' || navLang === 'en-US' || navLang === 'ja-JP') ? navLang.replace(/-/, '_') : false
  const language = localLang || userLang || 'zh_CN'
  const locales = Object.keys(messages)
  for (const locale of locales) {
    if (language.indexOf(locale) > -1) {
      Cookies.set('language', locale)
      return locale
    }
  }
  return 'en-US'
}

const i18n = new VueI18n({
  locale: initLanguage(),
  // set locale messages
  messages
})

export default i18n
