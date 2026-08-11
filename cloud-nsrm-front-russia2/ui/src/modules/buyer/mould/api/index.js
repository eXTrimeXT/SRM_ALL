import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const mouldheader = {
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/listPage'),
      method: 'POST',
      data
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getHistory: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/getHistory'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  calcOrderNumberAndWarehousingNum: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/calcOrderNumberAndWarehousingNum'),
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
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/update'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/add'),
      method: 'POST',
      data,
      loading: true
    }),

  updateMouldHistory: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/updateMouldHistory'),
      method: 'POST',
      data,
      loading: true
    }),

  addMouldHistory: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/addMouldHistory'),
      method: 'POST',
      data,
      loading: true
    }),

  getChangeInfoByFlowId: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/getChangeInfoByFlowId'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  updateChangeMoulds: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/updateChangeMoulds'),
      method: 'POST',
      data,
      loading: true
    }),

  saveChangeMoulds: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/saveChangeMoulds'),
      method: 'POST',
      data,
      loading: true
    }),

  listLogPage: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/listLogPage'),
      method: 'POST',
      data,
      loading: true
    }),

  getScrapInfoByFlowId: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/getScrapInfoByFlowId'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  updateScrapMoulds: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/updateScrapMoulds'),
      method: 'POST',
      data,
      loading: true
    }),

  saveScrapMoulds: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/saveScrapMoulds'),
      method: 'POST',
      data,
      loading: true
    })
}

export const mouldflow = {
  batchPassMould: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/batchPassMould'),
      method: 'POST',
      data,
      loading: true
    }),

  deleteFlow: async id =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/deleteFlow/' + id),
      method: 'DELETE',
      loading: true
    }),

  listFlowPage: async data =>
    http({
      url: getUrl('/api-sup-ce/mould/mouldheader/listFlowPage'),
      method: 'POST',
      data
    })
}

export const mouldline = {
  list: async data =>
    http({
      url: '/api-sup-ce/mould/mouldline/listPage',
      method: 'POST',
      data
    })
}
