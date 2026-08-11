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
import router, { init, resetRouter, constantRoutes } from './router'
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
import preventReClick from '@/directive/preventReClick/preventReClick'
import http from '@/utils/axios/http' // 网关请求
import httpWeb from '@/utils/httpWeb' // 非网关请求
import { systemUrl } from '@/config/sysConfig'
// 根据lov的value值获取显示值 , getLovValues
import {
  getLabelByValue,
  getDictLabelByValue,
  getStatusClass,
  tabsAddFormTitle,
  focusAndAlterErrorEngine,
  formatDatePicker,
  parseTime, formatDatePickerTime
} from '@/utils'
import 'polyfill-object.fromentries'

// 加载动画loading
import loadingInstance from '@/utils/loading'
import AuthorityButton from 'lib@/components/AuthorityButton'
import DictSelect from 'lib@/components/c-select/index.js'
import validate from 'lib@/mixins/validate'
import addStarToColumn from 'lib@/mixins/addStarToColumn'
import flowJudgmentVariable from 'lib@/mixins/flowJudgmentVariable'

import { DictClass } from '@/library/utils/dict/dict-utils'

import Postmate from 'postmate'
import { getAssistantToken } from '@/api/user'
import { saveOrderFileCheck } from 'modcb@/biddingBuyer/api/analysis'

// 引入大数据表格以及xe-utils工具类方法
import 'lib@/plugins/vxe-table'

// 引入全局UI组件
import 'lib@/components/srm-ui/packages/index'

import service from './service'

import { IndustryPackage } from '@meicloud/industry-package'

import { RenderEngine } from 'lib@/components/render-engine'

import { Workbook } from 'web-editor-onlyoffice'

init(store)

Vue.prototype.$systemUrl = systemUrl
Vue.config.devtools = true
Vue.prototype.$http = http
Vue.prototype.$httpWeb = httpWeb
Vue.prototype.$bus = new Vue()

Vue.prototype.$getLabelByValue = getLabelByValue
Vue.prototype.$getDictLabelByValue = getDictLabelByValue
Vue.prototype.$getDictLabel = DictClass.getDictLabel // svg组件
Vue.prototype.$parseTime = parseTime
Vue.prototype.$formatDatePicker = formatDatePicker
Vue.prototype.$formatDatePickerTime = formatDatePickerTime

Vue.prototype.$getStatusClass = getStatusClass // 获取状态样式

Vue.directive('el-drag-dialog', drag)
Vue.directive('input-format', inputFormat)
Vue.directive('drag-div', dragDiv)
Vue.directive('preventReClick', preventReClick)
Vue.use(service)

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.use(DictSelect)

// 日期库
Vue.use(dayjs)

Vue.use(AuthorityButton)

Vue.use(Workbook)

// 定位到表单验证错误位置、定位到页面某一元素
Vue.mixin(validate)
// 给必填表格表头加星号
Vue.mixin(addStarToColumn)
// 流程判断变量
Vue.mixin(flowJudgmentVariable)

// eslint-disable-next-line func-call-spacing
Vue.config.productionTip = false // get token from cookie

// 多语言获取
let vm
// UI 挂载
const initVue = () => {
  // 接口报错也设置默认语言
  Vue.use(Element, {
    size: Cookies.get('size') || 'small', // medium / small / mini set element-ui default size
    i18n: (key, value) => i18n.t(key, value),
    isMessageBg: true,
    input: {
      trim: false // 优先级高于外层设置值
      // maxlength: 50,
      // showWordLimit: false
    },
    textarea: {
      trim: false, // input 框去掉前后空白
      maxlength: 500,
      showWordLimit: true
    }
  })
}
initVue()
store.dispatch('app/setSysOpenConfig')

Vue.prototype.$pageLoading = loadingInstance // global filters
// 默认设置表格所有列显示Tooltip
// Element.TableColumn.props.showOverflowTooltip = { type: Boolean, default: true }

Vue.prototype.$handleFrameClick = (type, params) => {
  const isDrag = document.getElementById('assistant').getAttribute('drag-flag')
  if (isDrag === 'true') {
    // eslint-disable-next-line no-useless-return
    return
  }
  Vue.prototype.$getAssistantToken(type, params)
}
Vue.prototype.$getAssistantToken = (type, params) => {
  if (store.state.user.isFirstClick) {
    store.commit('user/SET_LOAD_FLAG', true)
    getAssistantToken().then(res => {
      if (res.code + '' === '0') {
        Vue.prototype.$handleHandshake(res.data, type, params)
        store.commit('user/SET_FIRST_CLICK', false)
      }
    })
  } else {
    store.commit('user/SET_LOAD_FLAG', false)
    store.commit('user/SET_FRAME', true)
  }
}

Vue.prototype.$handleHandshake = (token, type, params) => {
  const handshake = new Postmate({
    container: document.getElementById('helperFrame'), // Element to inject frame into
    url: 'https://gwkb-helper.gwm.cn/#/embed', // Page to load, must have postmate.js. This will also be the origin used for communication.
    name: 'my-iframe-name', // Set Iframe name attribute. Useful to get `window.name` in the child.
    classListArray: ['helper-frame']
  })
  handshake.then(child => {
    store.commit('user/SET_LOAD_FLAG', false)
    store.commit('user/SET_FRAME', true)
    Vue.prototype.embedHelper = child
    setTimeout(() => {
      child.frame.style.height = '100%'
      child.frame.style.width = '100%'
      child.frame.style.border = 'none'
      child.frame.style.background = '#fff'
      child.frame.style.borderRadius = '20px'
      child.frame.style.boxShadow = '0px 10px 20px 0px rgba(143, 115, 156, 0.13)'
    })
    child.get('msg').then(msg => { })
    child.on('expand', status => { // 接收 关闭折叠展开消息
      store.commit('user/SET_EXPAND', status)
      // this.expand = status
    })
    child.on('close', data => { // 接收 关闭
      store.commit('user/SET_FRAME', false)
    })
    child.on('preview', status => { // 接收 关闭打开预览展开消息
      // this.showPreview = status
      store.commit('user/SET_SHOW_PREVIEW', status)
    })
    child.on('authExpire', status => {
      getAssistantToken().then(res => {
        if (res.code + '' === '0') {
          child.call('token', res.data)
        }
      })
    })
    child.on('taskId', status => {
      if (status == 0) {
        store.commit('user/SET_IDENTIFY_LOAD', false)
      }
      const compareData = JSON.parse(store.state.user.compareData)
      if (status) {
        const param = {
          type: 'OrderFileCheck',
          action: 'save',
          lang: 'zh-cn',
          payload: [
            {
              filePartType: compareData.compareWordSize,
              projectId: compareData.projectId,
              serialNum: status
            }
          ],
          query: {
            '*': {}
          }
        }
        saveOrderFileCheck(param).then(res => {})
      }
    })
    child.on('compareStatus', taskInfo => {
      if (taskInfo.status == 2 || taskInfo.status == 3) {
        store.commit('user/SET_IDENTIFY_LOAD', false)
      }
    })
    child.on('openComparePdf', status => {
      store.commit('user/SET_FULLSIZE', status)
    })
    child.call('token', token)
    child.call('embedClient', 'SRMclient')
    setTimeout(() => {
      child.call('compareData', store.state.user.compareData)
    }, 900)
    if (type === 'file' || type === 'detail') {
      setTimeout(() => {
        child.call('navTabIndex', 1)
      }, 600)
    }
    if (type === 'detail') {
      child.call('pdfData', JSON.stringify(params))
    }
    if (type === 'dialogue') {
      setTimeout(() => {
        child.call('navTabIndex', 2)
      }, 100)
      child.call('biddingData', params)
    }
  })
}

// 行业包、插件包 文件放本地形式
let packagesHost = import.meta.env.MODE == 'development' ? 'https://127.0.0.1:7899' : ''
const packages = {
  // 项目二开定制插件包
  'plugins': [
    {
      url: `${packagesHost}/custom-package/index.js`,
      enable: false // 默认关闭，项目上再开启
    }
  ],
  // 行业包
  'industries': []
}
new IndustryPackage(packages)
  .registerRoutes(constantRoutes)
  .bootstrap()
  .then(({ createApp, routes }) => {
    return createApp(App, {
      i18n,
      store,
      router: resetRouter(routes)
    })
  })
  .then((app) => {
    // 先全局注册，等改造成微模块就可以按需了
    app.component('RenderEngine', RenderEngine)

    app.mount('#app')

    vm = app
  })

export const getVm = () => vm
export const getStore = () => store
export const getRouter = () => router
export const geti18n = () => i18n

export default vm
