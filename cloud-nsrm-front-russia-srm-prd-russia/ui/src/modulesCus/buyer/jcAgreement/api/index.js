import http from '@/utils/axios/http'

const basePath = '/api-sou/jcAgreement/'

const priceAdjustApply = {
  save: data =>
    http({
      url: `/api-sou/price/adjustment/apply/saveOrUpdatePaaAdjust`,
      method: 'POST',
      data,
      loading: true
    }),
}

const jcAccount = {
  save: data =>
    http({
      url: `/api-sou/design/plan/ledger/saveOrUpdateChLedgerInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  getDetail: params =>
    http({
      url: `/api-sou/design/plan/ledger/getChLedgerInfo`,
      method: 'get',
      params,
      loading: true
    })
}

const centralizedAgree = {
  listPageUrl: `${basePath}getJcAgreementPageList`,
  getJcAgreementInfo: params =>
    http({
      url: `${basePath}getJcAgreementInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  saveOrUpdateJcAgreement: data =>
    http({
      url: `${basePath}saveOrUpdateJcAgreement`,
      method: 'POST',
      data,
      loading: true
    }),
  changeStatus: params =>
    http({
      url: `${basePath}changeStatusJcAgreementInfo`,
      method: 'POST',
      params,
      loading: true
    }),
  getChangeJcAgreementList: params =>
    http({
      url: `${basePath}getChangeJcAgreementList`,
      method: 'POST',
      params,
      loading: true
    }),
  changeJcAgreement: data =>
    http({
      url: `${basePath}changeJcAgreement`,
      method: 'POST',
      data,
      loading: true
    }),
  getChangeJcAgreementInfo: params =>
    http({
      url: `${basePath}getChangeJcAgreementInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  abandon: data =>
    http({
      url: 'api-sou/jcAgreement/discardAgreementLine',
      method: 'POST',
      data,
      loading: true
    })
}

const contractAgree = {
  listPageUrl: '/api-sou/jcAgreement/getJcAgreementPageList',
  get: data =>
    http({
      url: '/api-sou/jcAgreement/getJcAgreementPageList',
      method: 'POST',
      data,
      loading: true
    })
}

const chDesignPlan = {
  listPageUrl: '/api-sou/design/plan/getChDesignPlanPageList',
  getDesignPlanInfo: params =>
    http({
      url: '/api-sou/design/plan/getDesignPlanInfo',
      method: 'GET',
      params,
      loading: true
    }),
  delete: params =>
    http({
      url: '/api-sou/design/plan/delete',
      method: 'GET',
      params,
      loading: true
    }),
  savaOrUpdateDesignPlan: data =>
    http({
      url: '/api-sou/design/plan/savaOrUpdateDesignPlan',
      method: 'POST',
      data,
      loading: true
    }),
  pullOrder: data =>
    http({
      url: '/api-sou/design/plan/pullOrder',
      method: 'POST',
      data,
      loading: true
    }),
  mergeOrderData: params =>
    http({
      url: '/api-sou/design/plan/mergeOrderData',
      method: 'GET',
      params,
      loading: true
    }),
  saveOrUpdateDemandProjPlan: data =>
    http({
      url: '/api-sou/design/plan/saveOrUpdateDemandProjPlan',
      method: 'POST',
      data,
      loading: true
    }),
  getDemandProjPlanList: params =>
    http({
      url: '/api-sou/design/plan/getDemandProjPlanList',
      method: 'POST',
      params,
      loading: true
    }),
  // 获取上年和上上年数据
  getPullOrder: data =>
    http({
      url: '/api-sou/design/plan/getPullOrder',
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商信息-保存
  saveOrUpdateDemandSup: data =>
    http({
      url: '/api-sou/design/plan/saveOrUpdateDemandSup',
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商信息-查询
  getReqSupInfoList: params =>
    http({
      url: '/api-sou/design/plan/getReqSupInfoList',
      method: 'GET',
      params,
      loading: true
    }),
  // 删除明细
  deleteDetail: data =>
    http({
      url: 'api-sou/design/plan/deleteSomePullOrderData',
      method: 'POST',
      data,
      loading: true
    })
}

export {
  priceAdjustApply,
  jcAccount,
  centralizedAgree as centralHttp,
  contractAgree as contractHttp,
  chDesignPlan as designPlanHttp
}

