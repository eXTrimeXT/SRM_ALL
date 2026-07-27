/**
 * 自动生成的API
 */

import http from '@/utils/axios/http'
const prefix = '/api-sup-ce/mould/mouldheader'
const getUrl = path => prefix + '/' + path

export const mouldline = {
  list: async data =>
    http({
      url: '/api-sup-ce/mould/mouldline/listPage',
      method: 'POST',
      data,
      loading: true
    })

}

export const mouldheader = {
  list: async data =>
    http({
      url: getUrl('listPage'),
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
  deleteFlow: async id =>
    http({
      url: getUrl('deleteFlow/' + id),
      method: 'DELETE',
      loading: true
    }),
  batchPassMould: async data =>
    http({
      url: getUrl('batchPassMould'),
      method: 'POST',
      data,
      loading: true
    }),
  getById: async id =>
    http({
      url: getUrl('get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getHistory: async id =>
    http({
      url: getUrl('getHistory'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getByIdList: async data =>
    http({
      url: getUrl('getByIdList'),
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
      url: getUrl('update'),
      method: 'POST',
      data,
      loading: true
    }),
  addMouldHistory: async data =>
    http({
      url: getUrl('addMouldHistory'),
      method: 'POST',
      data,
      loading: true
    }),
  updateMouldHistory: async data =>
    http({
      url: getUrl('updateMouldHistory'),
      method: 'POST',
      data,
      loading: true
    }),
  saveScrapMoulds: async data =>
    http({
      url: getUrl('saveScrapMoulds'),
      method: 'POST',
      data,
      loading: true
    }),
  updateScrapMoulds: async data =>
    http({
      url: getUrl('updateScrapMoulds'),
      method: 'POST',
      data,
      loading: true
    }),
  saveChangeMoulds: async data =>
    http({
      url: getUrl('saveChangeMoulds'),
      method: 'POST',
      data,
      loading: true
    }),
  updateChangeMoulds: async data =>
    http({
      url: getUrl('updateChangeMoulds'),
      method: 'POST',
      data,
      loading: true
    }),
  getScrapInfoByFlowId: async id =>
    http({
      url: getUrl('getScrapInfoByFlowId'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getChangeInfoByFlowId: async id =>
    http({
      url: getUrl('getChangeInfoByFlowId'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  listFlowPage: async data =>
    http({
      url: getUrl('listFlowPage'),
      method: 'POST',
      data,
      loading: true
    }),
  listLogPage: async data =>
    http({
      url: getUrl('listLogPage'),
      method: 'POST',
      data,
      loading: true
    }),
  calcOrderNumberAndWarehousingNum: async data =>
    http({
      url: getUrl('calcOrderNumberAndWarehousingNum'),
      method: 'POST',
      data,
      loading: true
    }),
  fileuploadDelete: async params =>
    http({
      url: getUrl('/api-file/file/fileupload/delete'),
      method: 'POST',
      params,
      loading: true
    })
}
