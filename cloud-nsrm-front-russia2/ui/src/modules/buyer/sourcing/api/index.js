/* 寻源 */

import http from '@/utils/axios/http'

// 寻源需求
export const sourcing = {
  getReportCount: async () =>
    http({
      url: '/api-inq/inq/reqhead/souReqReportCount',
      method: 'POST',
      loading: true
    }),
  tempSaveOrPublishSouReq: async data =>
    http({
      url: '/api-inq/inq/reqhead/tempSaveOrPublishSouReq',
      method: 'POST',
      data,
      loading: true
    }),
  getFormDetail: async params =>
    http({
      url: '/api-inq/inq/reqhead/get',
      method: 'GET',
      params,
      loading: true
    }),
  reqApplyListPage: async data =>
    http({
      url: '/api-inq/inq/reqapply/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  deleteSourcing: async reqHeadId =>
    http({
      url: `/api-inq/inq/reqhead/delete/${reqHeadId}`,
      method: 'DELETE',
      loading: true
    }),
  stopSignUp: async reqHeadId =>
    http({
      url: `/api-inq/inq/reqhead/stopSignUp/${reqHeadId}`,
      method: 'POST',
      loading: true
    }),
  abandonSourcing: async reqHeadId =>
    http({
      url: `/api-inq/inq/reqhead/cancelSouReq/${reqHeadId}`,
      method: 'POST',
      loading: true
    }),
  getMonitorCount: async () =>
    http({
      url: '/api-sup/req/monitor/count',
      method: 'GET',
      loading: true
    }),
  getResult: async params =>
    http({
      url: '/api-inq/inq/reqapply/getResult',
      method: 'GET',
      params,
      loading: true
    }),
  refuseSignUp: async data =>
    http({
      url: '/api-inq/inq/reqapply/refuse',
      method: 'POST',
      data,
      loading: true
    }),
  getDetail: async params =>
    http({
      url: '/api-inq/inq/reqapply/getDetail',
      method: 'GET',
      params,
      loading: true
    }),
  tempSaveOrSubmit: async data =>
    http({
      url: '/api-inq/inq/reqapply/tempSaveOrSubmit',
      method: 'POST',
      data,
      loading: true
    }),
  score: async (data, reqHeadId) =>
    http({
      url: `/api-inq/inq/reqapply/score/${reqHeadId}`,
      method: 'POST',
      data,
      loading: true
    }),
  createReviewForm: async (data, reqHeadId) =>
    http({
      url: `/api-inq/inq/reqhead/createReviewForm/${reqHeadId}`,
      method: 'POST',
      data,
      loading: true
    }),
  createSouInquiry: async reqHeadId =>
    http({
      url: `/api-inq/inq/reqhead/createSouInquiry/${reqHeadId}`,
      method: 'POST',
      loading: true
    }),
  winOrLose: async data =>
    http({
      url: '/api-inq/inq/reqapply/winOrLose',
      method: 'POST',
      data,
      loading: true
    }),
  publish: async reqHeadId =>
    http({
      url: `/api-inq/inq/reqapply/finishSelect/${reqHeadId}`,
      method: 'POST',
      loading: true
    }),
  reqHeadListPage: async data =>
    http({
      url: '/api-inq/inq/reqhead/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  reqHeadListAll: async data =>
    http({
      // url: '/api-inq/inq/reqhead/listAll',
      url: '/api-inq/inq/reqhead/listAllForPage',
      method: 'POST',
      data,
      loading: true
    })
}
