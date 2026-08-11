/**
 * 自动生成的API
 */

import http from '@/utils/axios/http'
const prefix = '/api-sup-ce/sup/inventoryLog'
const getUrl = path => prefix + '/' + path

export const supplierinventorylog = {
  list: async data =>
    http({
      url: getUrl('listPage'),
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
