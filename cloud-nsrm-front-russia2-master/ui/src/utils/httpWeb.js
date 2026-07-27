// 非
import axios from 'axios'
import { Message } from '@meicloud/element-ui' // 'element-ui'
import { getToken } from '@/utils/auth'
import loadingInstance from '@/utils/loading'
import { getMenuInfo } from '@/utils/menu-auth'

let loadInstance // 页面加载效果
const hideLoading = function () {
    loadInstance && loadInstance.close()
    loadInstance = null
}

// create an axios instance
const httpWeb = axios.create({
    withCredentials: true, // send cookies when cross-domain requests
    timeout: 3600000, // request timeout
    ContentType: 'application/json'
})

const CancelToken = axios.CancelToken
const source = CancelToken.source()
// request interceptor
httpWeb.interceptors.request.use(
    config => {
        config.cancelToken = source.token
        // 显示页面加载效果
        if (config.loading === true && !loadInstance) {
            loadInstance = loadingInstance.open()
        }
        const token = getToken()
        let menuInfo = getMenuInfo()
        config.headers['X-Fun-Info'] = menuInfo.secretKey
        if (token) {
            // let each request carry token
            config.headers.Authorization = 'Bearer ' + token
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
httpWeb.interceptors.response.use(
    response => {
        hideLoading()
        const res = response.data
        return res
    },
    error => {
        hideLoading()
        console.log('err' + error) // for debug
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

const axiosConfig = {
  method: 'post',
  // 请求头信息
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
  // 参数
  data: {},
  // 设置超时时间
  timeout: 350000,
  // 携带凭证
  withCredentials: true,
  // 返回数据类型
  responseType: 'json'
}

// 如果需要自定义请求头信息可以用这个方法
export const requestApi = (url, params = {}, options = {}) => {
  const configs = {
    ...axiosConfig,
    url,
    data: params,
    ...options
  }
  return new Promise((resolve, reject) => {
    httpWeb(configs)
      .then(res => {
        resolve(res)
      })
      .catch(err => {
        reject(err)
      })
  })
}

export default httpWeb
