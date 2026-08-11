/**
 * 模板配置API
 * 每个功能接入都会用到，属于公共
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const modelConfig = {
  listPage: async data =>
    http({
      url: getUrl('/api-base/base/form_page/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  copyModel: async data =>
    http({
      url: getUrl('/api-base/base/form_page/copyModel'),
      method: 'POST',
      data,
      loading: true
    }),
  deleteModel: async id =>
    http({
      url: getUrl('/api-base/base/form_page/deleteModel'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

export const modelConfigApi = {
  getModelConfig: async pageCode =>
    http({
      url: getUrl('/api-base/base/form_page/getModelConfig'),
      method: 'GET',
      params: { pageCode },
      loading: true
    }),
  saveFormResutlForBusiness: async data =>
    http({
      url: getUrl('/api-base/base/form_page/saveFormResutlForBusiness'),
      method: 'POST',
      data,
      loading: true
    }),
  getDimDataById: async id =>
    http({
      url: getUrl('/api-base/base/form_page/getDimDataById'),
      method: 'GET',
      params: { id },
      loading: true
    })
}
