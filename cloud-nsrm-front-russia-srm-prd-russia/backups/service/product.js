/**
 * 自动生成的API
 */

import http from '@/utils/axios/http'
const prefix = '/api-sup/sup/capacityreport'
const getUrl = path => prefix + '/' + path

export const capacityreport = {
  list: async data =>
    http({
      url: getUrl('listAll'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('addCapacityReport'),
      method: 'POST',
      data,
      loading: true
    }),

  // 更新
  update: async data =>
    http({
      url: getUrl('updateCapacityReport'),
      method: 'POST',
      data,
      loading: true
    }),

  confirmHandle: async id =>
    http({
      url: getUrl('confirm'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}

// 历史记录
export const capacityreporthistory = {
  list: async data =>
    http({
      url: getUrl('listAllHistory'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })

}
