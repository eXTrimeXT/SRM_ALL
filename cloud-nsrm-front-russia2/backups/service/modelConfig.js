/**
 * 模板配置API
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
  getModelConfig: async pageCode =>
    http({
      url: getUrl('/api-base/base/form_page/getModelConfig'),
      method: 'GET',
      params: { pageCode },
      loading: true
    }),
  deleteModel: async id =>
    http({
      url: getUrl('/api-base/base/form_page/deleteModel'),
      method: 'GET',
      params: { id },
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
    }),
  copyModel: async data =>
    http({
      url: getUrl('/api-base/base/form_page/copyModel'),
      method: 'POST',
      data,
      loading: true
    })
}
