/**
 * 询比价API
 */

import http from '@/utils/axios/http'

export const bidding = {
  getCurrencyByBidingId: async bidingId =>
    http({
      url: '/api-brg/bidInitiating/biding/getCurrencyByBidingId',
      method: 'GET',
      params: { bidingId }
    })
}

const getQuotUrl = path => '/api-sup/sup/quotadetail/' + path
export const quotadetail = {
  list: async data =>
    http({
      url: getQuotUrl('listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getQuotUrl('batchSave'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getQuotUrl('modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getQuotUrl('delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getQuotUrl('batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const sourcingResultReport = async (type, biddingNum) =>
  http({
    url: `/api-${type === 'RFQ' ? 'brg' : 'bid'}/bidingResult/sourcingResultReport/generate`,
    method: 'GET',
    params: { biddingNum },
    loading: true
  })

export const getInqBidingAuditStatus = async bidingId =>
  http({
    url: '/api-comp/competition/bidInitiating/getBidingAuditStatus',
    method: 'GET',
    params: { bidingId },
    loading: true
  })

// 查询询价单立项信息
export const getInqInfoById = id =>
  http({
    url: `/api-inq/inquiry/header/getInqInfo/${id}`,
    method: 'GET',
    loading: true
  })

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
      url: '/api-inq/inq/reqhead/listAll',
      method: 'POST',
      data,
      loading: true
    })
}

// 报价
export const quote = {
  // 批量查询阶梯价
  batchGetQuoteLadderPrices: data =>
    http({
      url: '/api-inq/quote/quoteHeader/batchGetQuoteLadderPrices',
      method: 'POST',
      data,
      loading: true
    })
}

// 简易询价
export const inquiryBySimple = {
  inquiryDelete: async data =>
    http({
      url: '/api-inq/inquiry/header/delete',
      method: 'POST',
      data,
      loading: true
    }),
  inquiryCancel: async data =>
    http({
      url: '/api-inq/inquiry/header/cancel',
      method: 'POST',
      data,
      loading: true
    }),
  inquiryCopy: async inquiryId =>
    http({
      url: `/api-inq/inquiry/header/copy/${inquiryId}`,
      method: 'POST',
      loading: true
    }),
  inquiryTempSave: async data =>
    http({
      url: '/api-inq/inquiry/header/tempSaveInq',
      method: 'POST',
      data,
      loading: true
    }),
  inquirySave: async data =>
    http({
      url: '/api-inq/inquiry/header/submitInq',
      method: 'POST',
      data,
      loading: true
    }),
  inquiryDetail: async params =>
    http({
      url: '/api-inq/inquiry/header/getHeadById',
      method: 'GET',
      params,
      loading: true
    }),
  setPermission: async data =>
    http({
      url: '/api-inq/inquiry/quoteAuth/getAuth',
      method: 'POST',
      data,
      loading: true
    }),
  ladderPriceList: async data =>
    http({
      url: '/api-inq/inquiry/ladderPrice/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  passAudit: async data =>
    http({
      url: '/api-inq/inquiry/header/passAudit',
      method: 'POST',
      data,
      loading: true
    }),
  rejectAudit: async data =>
    http({
      url: '/api-inq/inquiry/header/rejectAudit',
      method: 'POST',
      data,
      loading: true
    }),
  getQuoteSelectionDetail: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/management/${inquiryId}`,
      method: 'GET',
      loading: true
    }),
  changeBeginQuote: async data =>
    http({
      url: '/api-inq/quote/selection/changeBeginQuote',
      method: 'POST',
      data,
      loading: true
    }),
  changeDeadline: async data =>
    http({
      url: '/api-inq/quote/selection/changeDeadline',
      method: 'POST',
      data,
      loading: true
    }),
  getInquiryRule: async data =>
    http({
      url: '/api-bid/evaluation/ruleLineConfig/listPage',
      method: 'POST',
      data,
      loading: true
    }),
  getOuDetail: async params =>
    http({
      url: '/api-base/base/base-ou-group/queryById',
      method: 'GET',
      params,
      loading: true
    }),
  cancelQuote: async data =>
    http({
      url: '/api-inq/quote/selection/cancelQuote',
      method: 'POST',
      data,
      loading: true
    }),
  getSearchInfo: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/selecting/searchInfo/${inquiryId}`,
      method: 'GET',
      loading: true
    }),
  getQuoteSelectionDetailData: async (data, inquiryId) =>
    http({
      url: `/api-inq/quote/selection/selecting/${inquiryId}`,
      method: 'POST',
      data,
      loading: true
    }),
  getEvaluateOne: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/autoSelecting/${inquiryId}`,
      method: 'POST',
      loading: true
    }),
  changeSelectSubmit: async (data, apiName) =>
    http({
      url: `/api-inq/quote/selection/${apiName}`,
      method: 'POST',
      data,
      loading: true
    }),
  openResult: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/openResult/${inquiryId}`,
      method: 'POST',
      loading: true
    }),
  createPricingApproval: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/createPricingApproval/${inquiryId}`,
      method: 'POST',
      loading: true
    }),
  batchUpdatePayment: async data =>
    http({
      url: '/api-inq/quote/selection/batchUpdatePayment',
      method: 'POST',
      data,
      loading: true
    }),
  getPriceCompareInfos: async params =>
    http({
      url: '/api-inq/quote/selection/getPriceCompareInfos',
      method: 'GET',
      params,
      loading: false
    }),
  changeQuoteQuantity: async data =>
    http({
      url: '/api-inq/quote/selection/changeQuoteQuantity',
      method: 'POST',
      data,
      loading: true
    }),
  getTrackingDetail: async quoteId =>
    http({
      url: `/api-inq/quote/selection/getVendorQuoteDetail/${quoteId}`,
      method: 'GET',
      loading: true
    }),
  getTargetPriceData: async inquiryId =>
    http({
      url: `/api-inq/quote/selection/getTargetPrice/${inquiryId}`,
      method: 'GET',
      loading: true
    }),
  saveTargetPrice: async (data, inquiryId) =>
    http({
      url: `/api-inq/quote/selection/setTargetPrice/${inquiryId}`,
      method: 'POST',
      data,
      loading: true
    }),
  startNewRound: async data =>
    http({
      url: '/api-inq/quote/selection/startNewRound',
      method: 'POST',
      data,
      loading: true
    }),
  getBidingResultData: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/getInqQuoteHistory',
      method: 'POST',
      data,
      loading: true
    }),
  getInqQuoteInfo: async params =>
    http({
      url: '/api-inq/quote/quoteHeader/getInqQuoteInfo',
      method: 'GET',
      params,
      loading: true
    }),
  batchGetQuoteLadderPrices: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/batchGetQuoteLadderPrices',
      method: 'POST',
      data,
      loading: true
    }),
  batchGetQuoteItemPayments: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/batchGetQuoteItemPayments',
      method: 'POST',
      data,
      loading: true
    }),
  saveQuote: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/saveQuote',
      method: 'POST',
      data,
      loading: true
    }),
  submitQuote: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/submitQuote',
      method: 'POST',
      data,
      loading: true
    }),
  rollback: async data =>
    http({
      url: '/api-inq/quote/quoteHeader/rollback',
      method: 'POST',
      data,
      loading: true
    }),
  readQuote: async params =>
    http({
      url: '/api-inq/quote/item',
      method: 'GET',
      params,
      loading: true
    }),
  computeFormulaPrice: async params =>
    http({
      url: '/api-inq/quote/quoteHeader/computeFormulaPrice',
      method: 'GET',
      params,
      loading: true
    }),
  getQuoteFormulaPrices: async params =>
    http({
      url: '/api-inq/quote/quoteHeader/getQuoteFormulaPrices',
      method: 'GET',
      params,
      loading: true
    })
}

// 报价模板报价应用
export const templatePrice = {
  // 获取 / 保存 报价模板预览数据
  getOrSaveTempDataUrl: '/api-inq/quote/quoteHeader/quote/temp'
}
