import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL, // url = base url + request url
  withCredentials: true, // send cookies when cross-domain requests
  timeout: 35000, // request timeout
  ContentType: 'application/json'
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    const token = getToken()
    if (token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // config.headers['X-Token'] = getToken()
      config.headers.Authorization = token
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
service.interceptors.response.use(
  response => {
    const res = response.data
    // if the custom code is not 000, it is judged as an error.
    if (res.retCode !== '000') {
      //  002: Token expired;
      if (res.retCode === '002') {
        // to re-login
        MessageBox.confirm('您已经退回登录，您可以取消继续停留在这个页面，或者再次登录', '确认注销', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 重置token 刷新页面
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      Message({
        message: res.retMsg || 'Error',
        type: 'error'
      })
      return Promise.reject(new Error(res.retMsg || 'Error'))
    } else { // success
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
