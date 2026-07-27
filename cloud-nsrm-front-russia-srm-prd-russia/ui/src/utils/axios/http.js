import { Message, MessageBox } from '@meicloud/element-ui'
import { getToken, clearCookie, setRedirectUrl } from '@/utils/auth'
import { isShowTraceInfo } from '@/config/sysConfig'
import { sysPrefix, singlePointDev } from '@/config/ipConfig'
import { stopRepeatRequest, allowRequestUrl } from './debounce'
import { getMenuInfo, urlWhiteList } from '@/utils/menu-auth'
import { checkStrIsJSON } from '@/utils/validate'
import { getObjectKeyValue } from '@/utils'
import { messageConfig } from '@/utils/message'
import axios from 'axios'
import loadingInstance from '@/utils/loading'
import store from '@/store'

let loadInstance // 页面加载效果
const hideLoading = function () {
  loadInstance && loadInstance.close()
  loadInstance = null
}

// create an axios instance
const http = axios.create({
  withCredentials: true, // send cookies when cross-domain requests
  timeout: 350000, // request timeout
  ContentType: 'application/json',
  baseURL: sysPrefix() // 添加网关前缀
})

const CancelToken = axios.CancelToken
const source = CancelToken.source()
let showTraceInfo = isShowTraceInfo // 是否开启显示 TraceInfo

// request interceptor
http.interceptors.request.use(
  config => {
    if (config.debounce) {
      // 防抖。自定义cancelToken对象
      let cancel
      config.cancelToken = new axios.CancelToken(c => {
        cancel = c
      })
      stopRepeatRequest(config.url, config.data || config.params, cancel, config.debounceMessage)
    } else {
      config.cancelToken = source.token
    }

    // 显示页面加载效果
    if (config.loading && !loadInstance) {
      loadInstance = loadingInstance.open()
    }
    const token = getToken()
    if (token) {
      if (!config.unToken) {
        config.headers.Authorization = 'Bearer ' + token
      }
    }
    if (config.check) { // 招标启用后台校验标识
      config.headers.check = config.check
    }
    // 本地调试iam单点
    let singlePointType = singlePointDev()
    if (singlePointType) {
      config.headers['service-tag'] = singlePointType
    }
    // 菜单ID加入请求头 用于后端统计当前使用接口
    let menuInfo = getMenuInfo()
    config.headers['X-Fun-Info'] = menuInfo.secretKey
    if (urlWhiteList.includes(config.url)) {
      config.headers.menuid = '-1'
    } else {
      if (menuInfo.menuId) {
        config.headers.menuid = menuInfo.menuId
      }
    }
    return config
  },
  error => {
    // do something with request error
    console.log('request-error', error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
http.interceptors.response.use(
  response => {
    if (response.config.debounce) {
      // 防抖移除
      allowRequestUrl(response.config.url)
    }

    //  直接返回
    if (response.config.returnDirectly) {
      hideLoading()
      if (response.data && response.data.code === 'SRM_COMMON_00002') {
        // 当前所在的页面href
        let href = window.location.href.split('#')[1]
        source.cancel() // 取消其他正在进行的请求
        MessageBox.confirm('会话失效，请重新登录！！', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          showClose: false,
          showCancelButton: false,
          closeOnClickModal: false
        }).then(() => {
          clearCookie()
          setRedirectUrl(decodeURI(href))
          location.reload()
        }).catch(() => {})
        // 消息提示
        // Message({
        //   ...messageConfig,
        //   message: response.data.message || 'Error',
        //   type: 'error',
        //   duration: 5 * 1000
        // })
        return Promise.reject(new Error(response.data.message))
      } else {
        return response
      }
    }

    hideLoading()
    // 跟踪ID显示
    let traceInfo = response.headers['x-b3-traceid']
    if (traceInfo) {
      // 这里不支持多语言，暂时用中文
      traceInfo = ' [错误代码:' + traceInfo + ']'
    } else {
      traceInfo = ''
    }
    const res = response.data
    // if the custom code is not 000, it is judged as an error.
    if (res.code !== '0') {
      //  002: Token expired;
      if (res.code === 'SRM_COMMON_00002') {
        // 当前所在的页面href
        let href = window.location.href.split('#')[1]
        source.cancel() // 取消其他正在进行的请求
        MessageBox.confirm('会话失效，请重新登录！！', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          showClose: false,
          showCancelButton: false,
          closeOnClickModal: false
        }).then(() => {
          clearCookie()
          setRedirectUrl(decodeURI(href))
          location.reload()
        }).catch(() => {})
        return Promise.reject(new Error(res.message))
      }
      // 应用过期
      if (res.code === 'SRM_COMMON_00060') {
        store.dispatch('app/appRegisterFn', res.message)
        return Promise.reject('应用过期')
      }

      // let msg = (showTraceInfo === 'Y' ? res.message + traceInfo : res.message) || 'Error'
      let msg = ''
      if (showTraceInfo === 'Y') {
        msg = res.message + traceInfo
      } else {
        let messIsIsJson = checkStrIsJSON(res.message)
        if (messIsIsJson) {
          let mesObj = JSON.parse(res.message)
          msg = getObjectKeyValue(mesObj)
        } else {
          msg = res.message || 'Error'
        }
      }
      Message({
        ...messageConfig,
        message: msg,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(msg))
    } else { // success
      return res
    }
  },
  error => {
    hideLoading()
    let isCanceled = error.message == 'canceled'
    let message = error.message == 'canceled' ? '会话失效，已取消请求！' : error.message
    let errorMessage = message
    if (axios.isCancel(error)) {
      // 鉴定为取消的报错
      if (error.message && typeof error.message === 'string' && error.message.indexOf('debounce-') === 0) {
        // 触发防抖的需要报错 移除错误提示标识
        errorMessage = error.message.replace('debounce-', '')
      } else {
        // 请求被CODE SRM_COMMON_00002取消触发不需要报错
        return Promise.reject(error)
      }
    }
    // 接口请求超时提示
    let originalRequest = error.config
    if (error.message.indexOf('timeout') !== -1 && !originalRequest._retry) {
      errorMessage = '接口请求超时！！'
      Message({
        ...messageConfig,
        message: errorMessage,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }

    console.log('err' + errorMessage) // for debug
    if (!isCanceled) {
      console.log(error.code) // for debug
      if (error.code == 'ERR_BAD_RESPONSE') {
        Message({
          ...messageConfig,
          message: '系统网络异常，请稍后重试！',
          type: 'error',
          duration: 5 * 1000
        })
      } else {
        Message({
          ...messageConfig,
          message: error.message,
          type: 'error',
          duration: 5 * 1000
        })
      }
    }
    return Promise.reject(error)
  }
)
export default http
