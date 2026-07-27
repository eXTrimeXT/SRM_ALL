/**
 * 项目式询价API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const formula = {
  // 查询公式列表 入参：[{ materialId, categoryId, orgOuId }]
  getMaterialFormulaRelateInfos: async payload =>
    http({
      url: getUrl('/api-base/brg/pricing-formula/getMaterialFormulaRelateInfos'),
      method: 'POST',
      data: payload,
      loading: true
    })
}

export const bargain = {
  expertListPage: async data =>
    http({
      url: getUrl('/api-sou/bidExpert/listPage'),
      method: 'POST',
      data
    })
}

export const sourcingTemplate = {
  listPage: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/sourcingTemplate/findSourcingTemplatesWithoutTemplateData'),
      method: 'POST',
      data
    })
}

// 寻源和询价比公用接口 根据寻源模板ID快捷创建招投标或者询比价
export const generateSourceForm = async id =>
  http({
    url: getUrl('/api-brg/brgInitiating/sourcingTemplate/generateSourceForm'),
    method: 'POST',
    params: { id }
  })

export const getBargainAuditStatus = async bargainId =>
  http({
    url: getUrl('/api-brg/brgInitiating/bargain/getBargainAuditStatus'),
    method: 'GET',
    params: { bargainId },
    loading: true
  })

/* 获取招标物料列表 */
export const getRequireInfoByBargainId = async id =>
  http({
    url: getUrl(`/api-brg/brgInitiating/bargain/getRequireInfo/${id}`),
    method: 'GET',
    loading: true
  })

// 查询供应商公式报价
export const getQuoteFormulaPrices = async params =>
  http({
    url: getUrl('/api-brg/supplierCooperate/orderHead/getQuoteFormulaPrices'),
    method: 'GET',
    params,
    loading: true
  })

export const modelQuote = {
  // 寻源需求行【模型报价】查询明细
  find: async lineId =>
    http({
      url: getUrl('/api-brg/brgInitiating/brgRequirementLineTemplatePrice/findDetailsByLineId'),
      method: 'GET',
      params: { lineId }
    }),
  // 寻源需求行【模型报价】查询明细
  findByVendor: async lineId =>
    http({
      url: getUrl('/api-brg/techProposal/brgOrderLineTemplatePriceDetails/findDetailsByLineId'),
      method: 'GET',
      params: { lineId }
    }),
  // 寻源需求行【模型报价】保存明细集-采购商
  saveByBuyer: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/brgRequirementLineTemplatePrice/saveDetails'),
      method: 'POST',
      data
    }),
  // 采购商-评选查看报价模型横向对比
  generateTemplatePriceReport: async data =>
    http({
      url: getUrl('/api-brg/bargainResult/generateTemplatePriceReport'),
      method: 'POST',
      data
    }),
  // 寻源需求行【模型报价】删除明细头集-采购商
  deleteDetails: async detailIds =>
    http({
      url: getUrl('/api-brg/brgInitiating/brgRequirementLineTemplatePrice/deleteDetails'),
      method: 'DELETE',
      params: { detailIds }
    }),
  // 寻源需求行【模型报价】保存明细集-供应商
  saveByVendor: async data =>
    http({
      url: getUrl('/api-brg/techProposal/brgOrderLineTemplatePriceDetails/saveDetails'),
      method: 'POST',
      data
    }),
  // 查询模型报价接口，采购商和供应商同一个
  getOrderModelPrices: async params =>
    http({
      url: getUrl('/api-brg/supplierCooperate/orderHead/getOrderModelPrices'),
      method: 'GET',
      params,
      loading: true
    })
}

// 简易询价
export const inquiryByProject = {
  getBargainQuestionById: async params =>
    http({
      url: getUrl('/api-brg/bargainQuestion/getBargainQuestionById'),
      method: 'GET',
      params,
      loading: true
    }),
  getBargainAnswerDetail: async answerId =>
    http({
      url: getUrl(`/api-brg/bargainAnswer/getDetail/${answerId}`),
      method: 'GET',
      loading: true
    }),
  queryCompanyList: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  getProjectInfo: async bargainId =>
    http({
      url: getUrl(`/api-brg/brgInitiating/bargain/getProjectInfo/${bargainId}`),
      method: 'GET',
      loading: true
    }),
  vendorFileDelete: async params =>
    http({
      url: getUrl('/api-brg/supplierCooperate/vendorFile/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  bargainAnswerSave: async data =>
    http({
      url: getUrl('/api-brg/bargainAnswer/tempSaveOrSubmit'),
      method: 'POST',
      data,
      loading: true
    }),
  bargainQuestionReject: async data =>
    http({
      url: getUrl('/api-brg/bargainQuestion/reject'),
      method: 'POST',
      data,
      loading: true
    }),
  bargainAnswerDelete: async answerId =>
    http({
      url: getUrl(`/api-brg/bargainAnswer/delete/${answerId}`),
      method: 'DELETE',
      loading: true
    }),
  bargainAnswerWithDraw: async answerId =>
    http({
      url: getUrl(`/api-brg/bargainAnswer/withDraw/${answerId}`),
      method: 'POST',
      loading: true
    }),
  bargainAnswerPublish: async answerId =>
    http({
      url: getUrl(`/api-brg/bargainAnswer/publish/${answerId}`),
      method: 'POST',
      loading: true
    }),
  processConfigList: async data =>
    http({
      url: getUrl('/api-brg/process-config/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  processConfigDelete: async params =>
    http({
      url: getUrl('/api-brg/process-config/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  processConfigValidOrInvalid: async (data, processConfigId, validFlag) =>
    http({
      url: getUrl(validFlag ? `/api-brg/process-config/valid/${processConfigId}` : `/api-brg/process-config/invalid/${processConfigId}`),
      method: 'POST',
      data,
      loading: true
    }),
  processConfigSave: async data =>
    http({
      url: getUrl('/api-brg/process-config/saveOrUpdateConfig'),
      method: 'POST',
      data,
      loading: true
    }),
  listByBargainId: async params =>
    http({
      url: getUrl('/api-brg/process-node/listByBargainId'),
      method: 'GET',
      params,
      loading: true
    }),
  bargainAbandon: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/abandon'),
      method: 'POST',
      data,
      loading: true
    }),
  bargainDelete: async bargainId =>
    http({
      url: getUrl(`/api-brg/brgInitiating/bargain/delete/${bargainId}`),
      method: 'DELETE',
      loading: true
    }),
  changeSignUpEndTime: async data =>
    http({
      url: getUrl('/api-brg/signUpManagement/management/changeSignUpEndTime'),
      method: 'POST',
      data,
      loading: true
    }),
  confirmVendorSignUpInfo: async data =>
    http({
      url: getUrl('/api-brg/signUpManagement/management/confirmVendorSignUpInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  getBrgControlInfo: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/brgControl/getBrgControlInfo/${scopeBargainId}`),
      method: 'GET',
      loading: true
    }),
  changeBargainEndTime: async data =>
    http({
      url: getUrl('/api-brg/brgControl/changeBargainEndTime'),
      method: 'POST',
      data,
      loading: true
    }),
  startBargain: async data =>
    http({
      url: getUrl('/api-brg/brgControl/startBargain'),
      method: 'POST',
      data,
      loading: true
    }),
  queryBusinessOrders: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/businessProposal/queryBusinessOrders/${scopeBargainId}`),
      method: 'GET',
      loading: true
    }),
  openBrg: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/businessProposal/openBrg/${scopeBargainId}`),
      method: 'POST',
      loading: true
    }),
  decryptBrg: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/businessProposal/decryptBrg/${scopeBargainId}`),
      method: 'POST',
      loading: true
    }),
  withdrawOrder: async data =>
    http({
      url: getUrl('/api-brg/businessProposal/withdrawOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  removeOrderInfo: async params =>
    http({
      url: getUrl('/api-brg/businessProposal/removeOrderInfo'),
      method: 'GET',
      params,
      loading: true
    }),
  getInviteSupplier: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/brgInitiating/bargain/getInviteSupplier/${scopeBargainId}`),
      method: 'GET',
      loading: true
    }),
  tempSaveOrSubmitInviteSupplier: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/tempSaveOrSubmitInviteSupplier'),
      method: 'POST',
      data,
      loading: true
    }),
  getScoreRule: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/brgInitiating/bargain/getScoreRule/${scopeBargainId}`),
      method: 'GET',
      loading: true
    }),
  tempSaveOrSubmitScoreRule: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/tempSaveOrSubmitScoreRule'),
      method: 'POST',
      data,
      loading: true
    }),
  tempSaveOrSubmitProjectInfo: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/tempSaveOrSubmitProjectInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  tempSaveOrSubmitRequireInfo: async data =>
    http({
      url: getUrl('/api-brg/brgInitiating/bargain/tempSaveOrSubmitRequireInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  queryTechProgress: async data =>
    http({
      url: getUrl('/api-brg/techProposal/queryTechProgress'),
      method: 'POST',
      data,
      loading: true
    }),
  techProposalOpenBrg: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/techProposal/openBrg/${scopeBargainId}`),
      method: 'GET',
      loading: true
    }),
  intelligentEvaluation: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/evaluation/intelligentEvaluation/${scopeBargainId}`),
      method: 'POST',
      loading: true
    }),
  enterNextRound: async data =>
    http({
      url: getUrl('/api-brg/evaluation/enterNextRound'),
      method: 'POST',
      data,
      loading: true
    }),
  eliminate: async data =>
    http({
      url: getUrl('/api-brg/evaluation/eliminate'),
      method: 'POST',
      data,
      loading: true
    }),
  winTheBargain: async data =>
    http({
      url: getUrl('/api-brg/evaluation/changeSelectionStatus/win'),
      method: 'POST',
      data,
      loading: true
    }),
  changeWinQuantity: async data =>
    http({
      url: getUrl('/api-brg/evaluation/changeWinQuantity'),
      method: 'POST',
      data,
      loading: true
    }),
  lossTheBargain: async data =>
    http({
      url: getUrl('/api-brg/evaluation/changeSelectionStatus/fail'),
      method: 'POST',
      data,
      loading: true
    }),
  publishBargainResult: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/evaluation/publishResult/${scopeBargainId}`),
      method: 'POST',
      loading: true
    }),
  createPricingApproval: async scopeBargainId =>
    http({
      url: getUrl(`/api-brg/evaluation/createPricingApproval/${scopeBargainId}`),
      method: 'POST',
      loading: true
    }),
  getApprovalDetails: async params =>
    http({
      url: getUrl('/api-inq/price/approval/getApprovalDetails'),
      method: 'GET',
      params,
      loading: true
    }),
  getPriceCompareInfos: async params =>
    http({
      url: getUrl('/api-brg/evaluation/getPriceCompareInfos'),
      method: 'GET',
      params,
      loading: true
    }),
  querySignUpDetailInfo: async params =>
    http({
      url: getUrl('/api-brg/signUpManagement/management/querySignUpDetailInfo'),
      method: 'GET',
      params,
      loading: true
    }),
  getOrderDetails: async orderHeadId =>
    http({
      url: getUrl(`/api-brg/businessProposal/getOrderDetails/${orderHeadId}`),
      method: 'GET',
      loading: true
    }),
  getOrderFiles: async orderHeadId =>
    http({
      url: getUrl(`/api-brg/businessProposal/getOrderFiles/${orderHeadId}`),
      method: 'GET',
      loading: true
    }),
  ruleConfigList: async data =>
    http({
      url: getUrl('/api-bid/evaluation/ruleConfig/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  ruleLineConfigList: async data =>
    http({
      url: getUrl('/api-bid/evaluation/ruleLineConfig/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  queryTechProgressInfo: async params =>
    http({
      url: getUrl('/api-brg/techProposal/queryTechProgressInfo'),
      method: 'GET',
      params,
      loading: true
    }),
  getTechInfo: async params =>
    http({
      url: getUrl('/api-brg/signUpManagement/management/getTechInfo'),
      method: 'GET',
      params,
      loading: true
    }),
  queryTechProgressReviewDetail: async bargainId =>
    http({
      url: getUrl(`/api-brg/techProposal/queryTechProgressReviewDetail/${bargainId}`),
      method: 'GET',
      loading: true
    }),
  techScoreList: async params =>
    http({
      url: getUrl('/api-brg/techScore/listPage'),
      method: 'GET',
      params,
      loading: true
    }),
  getInformation: async params =>
    http({
      url: getUrl('/api-brg/projectManagement/projectPublish/getInformation'),
      method: 'GET',
      params,
      loading: true
    })
}
