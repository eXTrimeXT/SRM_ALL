// import 'babel-polyfill'
import Vue from 'vue'
import Cookies from 'js-cookie'
import Element from '@meicloud/element-ui' // 'element-ui'
// import 'element-ui/lib/theme-chalk/index.css'
import '@meicloud/element-ui/lib/theme-chalk/index.css'
import 'normalize.css/normalize.css'
import './styles/element-variables.scss'
import '@/styles/index.scss'

import App from '@/views/app'
import store from './store'
import router, { init } from './router'
import i18n from './lang'
import dayjs from 'lib@/plugins/dayjs'
// icon 全局注册 svg-icon 组件
import './icons'
// iconfont 引入阿里字体图标
import '@/assets/iconfont/iconfont.css'

// error log
import './utils/error-log'

import * as filters from './filters'
import drag from '@/directive/el-drag-dialog/drag'
import dragDiv from '@/directive/drag/drag'
import inputFormat from '@/directive/input-format/input-format'
import http from '@/utils/axios/http' // 网关请求
import httpWeb from '@/utils/httpWeb' // 非网关请求

// 根据lov的value值获取显示值 , getLovValues
import { getLabelByValue, getDictLabelByValue, getStatusClass } from '@/utils'

// 加载动画loading
import loadingInstance from '@/utils/loading'
import AuthorityButton from 'lib@/components/AuthorityButton'
import DictSelect from 'lib@/components/c-select/index.js'
import logger from 'lib@/utils/logger'
import validate from 'lib@/mixins/validate'
import addStarToColumn from 'lib@/mixins/addStarToColumn'
import { DictClass } from '@/library/utils/dict/dict-utils'

// 引入大数据表格以及xe-utils工具类方法
import 'lib@/plugins/vxe-table'

// 引入全局UI组件
import 'lib@/components/srm-ui/packages/index'

import service from './service'
init(store)

Vue.config.devtools = true
Vue.prototype.$http = http
Vue.prototype.$httpWeb = httpWeb
Vue.prototype.$bus = new Vue()

Vue.prototype.$getLabelByValue = getLabelByValue
Vue.prototype.$getDictLabelByValue = getDictLabelByValue
Vue.prototype.$getDictLabel = DictClass.getDictLabel // svg组件
Vue.prototype.$getStatusClass = getStatusClass // 获取状态样式

Vue.directive('el-drag-dialog', drag)
Vue.directive('input-format', inputFormat)
Vue.directive('drag-div', dragDiv)
Vue.use(service)

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.use(DictSelect)

// 日期库
Vue.use(dayjs)

// 日志装饰器
Vue.use(logger)

Vue.use(AuthorityButton)
// 定位到表单验证错误位置、定位到页面某一元素
Vue.mixin(validate)
// 给必填表格表头加星号
Vue.mixin(addStarToColumn)

// eslint-disable-next-line func-call-spacing
Vue.config.productionTip = false // get token from cookie

// 多语言获取
let vm
store.dispatch('app/getServeLang').then(result => {
  if (result) {
    Vue.use(Element, {
      size: Cookies.get('size') || 'small', // medium / small / mini set element-ui default size
      i18n: (key, value) => i18n.t(key, value)
    })

    Vue.prototype.$pageLoading = loadingInstance // global filters

    vm = new Vue({
      el: '#app',
      router,
      store,
      i18n,
      render: h => h(App)
    })
  }
})

export const getVm = () => vm
export const getStore = () => store
export const getRouter = () => router
export const geti18n = () => i18n

export default vm
