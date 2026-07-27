import axios from 'axios'
import { MessageBox, Message } from '@meicloud/element-ui' // 'element-ui'
import { messageConfig } from '@/utils/message'
import store from '@/store'
import { getToken } from '@/utils/auth'
import routers from '@/router'
import loadingInstance from '@/utils/loading'

let loadInstance // 页面加载效果
const hideLoading = function () {
  loadInstance && loadInstance.close()
  loadInstance = null
}

// create an axios instance
const http = axios.create({
  withCredentials: true, // send cookies when cross-domain requests
  timeout: 3600000, // request timeout
  ContentType: 'application/json'
})

// request interceptor
http.interceptors.request.use(
  config => {
    // 显示页面加载效果
    if (config.loading === true && !loadInstance) {
      loadInstance = loadingInstance.open()
    }
    // do something before request is sent
    const token = getToken()
    if (token) {
      // let each request carry token
      config.headers.Authorization = 'Bearer ' + token
    }
    if (config.check) {
      // 招标启用后台校验标识
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
    const res = response.data
    // if the custom code is not 000, it is judged as an error.
    if (res.code !== '0') {
      //  002: Token expired;
      if (res.code === 'SRM_COMMON_00002') {
        // to re-login
        MessageBox.confirm(
          '您已经退回登录，您可以取消继续停留在这个页面，或者再次登录',
          '确认注销',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          // 重置token 刷新页面
          store.dispatch('user/resetToken').then(() => {
            const router = routers
            // location.reload();
            router.push({ name: 'login' })
          })
        })
      }
      Message({
        ...messageConfig,
        message: res.message || 'Error',
        type: 'error'
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      // success
      return res
    }
  },
  error => {
    hideLoading()
    console.log('err' + error) // for debug
    Message({
      ...messageConfig,
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)
export default http
