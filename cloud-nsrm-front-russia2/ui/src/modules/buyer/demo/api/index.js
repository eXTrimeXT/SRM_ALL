/**
 * 基础设置新增API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const demoOrderApi = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/demoorder/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-base/base/demoorder/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-base/base/demoorder/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/demoorder/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/demoorder/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

export const demoOrderMulApi = {
  list: async data =>
    http({
      url: getUrl('/api-base/base/multiple/demoorder/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-base/base/multiple/demoorder/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-base/base/multiple/demoorder/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-base/base/multiple/demoorder/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
  }

  export const payTypeApi = {
    // 付款条件新增
    create: async data =>
      http({
        url: getUrl('/api-cm/api-ql/PayType/create'),
        method: 'POST',
        data
      }),
    // 付款条件保存
    save: async data =>
      http({
        url: getUrl('/api-cm/api-ql/PayType/save'),
        method: 'POST',
        data
      }),
    // 付款条件修改
    update: async data =>
      http({
        url: getUrl('/api-cm/api-ql/PayType/update'),
        method: 'POST',
        data
      }),
    // 付款条件分页查询
    query: async data =>
      http({
        url: getUrl('/api-cm/api-ql/PayType/query'),
        method: 'POST',
        data
      })
  }
