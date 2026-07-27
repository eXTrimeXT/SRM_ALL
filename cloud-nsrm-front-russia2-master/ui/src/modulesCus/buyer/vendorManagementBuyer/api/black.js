/**
 * 供应商 API
 * 黑名单
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 黑名单
export const blackApi = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/black/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async companyId =>
    http({
      url: getUrl('/api-sup/sup/black/delete'),
      method: 'GET',
      params: { companyId },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-sup/sup/black/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/black/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  checkSubmitData: async data =>
    http({
      url: getUrl('/api-sup/sup/black/checkSubmitData'),
      method: 'POST',
      data,
      loading: true
    })
}

// 黑名单临时
export const blackTemporaryApi = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/blacktemporary/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/blacktemporary/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-base/base/blacktemporary/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/blacktemporary/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

// 其他功能调用黑名单的通用接口

export const blackComApi = {
  findByCompanyIdAndStatus: async companyId =>
    http({
      url: getUrl('/api-sup/sup/black/findByCompanyIdAndStatus'),
      method: 'GET',
      params: { companyId },
      loading: true
    })
}
