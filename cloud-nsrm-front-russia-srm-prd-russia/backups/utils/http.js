import axios from 'axios'
import { Message } from '@midea/element-ui' // 'element-ui'
import store from '@/store'
import { getToken, getEntranceType } from '@/utils/auth'
import { isShowTraceInfo } from '@/config/sysConfig'
import { sysPrefix } from '@/config/ipConfig'
import { getVm } from '@/main'

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
    config.cancelToken = source.token
    // 显示页面加载效果
    if (config.loading === true && !loadInstance) {
      const vm = getVm()
      loadInstance = vm.$pageLoading.open()
    }
    const token = getToken()
    if (token) {
      // let each request carry token
      config.headers.Authorization = 'Bearer ' + token
    }
    if (config.check) { // 招标启用后台校验标识
      config.headers.check = config.check
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
http.interceptors.response.use(
  response => {
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
    const { code, status, payload } = res
    // if the custom code is not 000, it is judged as an error.
    if (code) { // SRM 返回code
      if (res.code !== '0') {
        //  002: Token expired;
        if (res.code === 'SRM_COMMON_00002') {
          source.cancel() // 取消其他正在进行的请求
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        }
        let msg = (showTraceInfo === 'Y' ? res.message + traceInfo : res.message) || 'Error'
        Message({
          message: msg,
          type: 'error'
        })
        return Promise.reject(new Error(msg))
      } else { // success
        return res
      }
    } else { // iam token失效 返回 status, payload
      source.cancel() // 取消其他正在进行的请求
      // store.dispatch('user/resetToken').then(() => {
      //   location.reload()
      // })
      Message({
        message: res.message,
        type: 'error'
      })
      return Promise.reject(new Error(res.message))
    }
  },
  error => {
    hideLoading()
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    // 单点登录返回token失效
    let entrance = getEntranceType()
    if (entrance == 'singlePoint') {
      // store.dispatch('user/resetToken').then(() => {
      //   location.reload()
      // })
    }
    return Promise.reject(error)
  }
)
export default http
