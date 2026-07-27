/**
 * 工具类 - 公共接口
 */

import http from '@/utils/axios/http'

/**
 * @desc 公共发起请求方法，主要用于动态请求
 * @author donghf3
 * @param url 请求URL
 * @param queryParams 请求body or url param
 * @param method 请求方法
 * @param loading 是否loading
 * @returns {*}
 */
export const common = (url, { queryParams = null, method = 'GET', loading = true }) => {
  const payload = {
    url,
    method,
    loading
  }
  // 存在请求参数
  if (queryParams) {
    if (method.toUpperCase() === 'GET') {
      // get方法 => params
      payload.params = queryParams
    } else {
      // 其他 => data
      payload.data = queryParams
    }
  }
  return http(payload)
}
