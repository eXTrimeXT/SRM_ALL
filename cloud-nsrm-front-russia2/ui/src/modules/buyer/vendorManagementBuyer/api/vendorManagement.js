/**
 * 供应商 API
 * 注册、绿色通道、信息变更、供应商清单
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 多个功能调用相同方法
// post类型，单据保存操作传入url,data操作
export function saveOrUpdateOrderByUrl (url, data) {
  return http({
    url: getUrl(url),
    method: 'POST',
    data,
    loading: true
  })
}

// 绿色通道
export const vendorGreenApi = {
   // 绿色通道供应商删除
  companyGreenChannelDel: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/companyGreenChannelDelete'),
      method: 'POST',
      data,
      loading: true
    }),
    // 删除组织品类信息
  orgCategoryDel: async params =>
    http({
      url: getUrl('/api-sup/info/orgCategory/delete'),
      method: 'GET',
      params,
      loading: true
    })
}

// 信息变更
export const vendorChangeApi = {
  // 删除附件
  deleteAttachById: async params =>
    http({
      url: getUrl('/api-sup/info/companyInfo/deleteAttachById'),
      method: 'POST',
      params,
      loading: true
    }),
    // 获取变更单据详情
  getInfoByChangeId: async params =>
    http({
      url: getUrl('/api-sup/change/infoChange/getInfoByChangeId'),
      method: 'GET',
      params,
      loading: true
    }),
    // 变更单据删除
  changeInfoDel: async params =>
    http({
      url: getUrl('/api-sup/change/infoChange/deleteChangeInfo'),
      method: 'GET',
      params,
      loading: true
    }),
    // 供应商点击编辑时触发
  ifAddInfoChange: async companyId =>
    http({
      url: getUrl('/api-sup/change/infoChange/ifAddInfoChange'),
      method: 'GET',
      params: { companyId },
      loading: true
    })
}

// 样品确认API
export const quaSampleApi = {
  // 样品确认单据删除(批量)
  quaSampleBathDel: async data =>
    http({
      url: getUrl('/api-sup/qua/quaSample/bathDeleteByList'),
      method: 'POST',
      data,
      loading: true
    }),
  getEntryConfigRecord: async params =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/getEntryConfigRecord'),
      method: 'GET',
      params,
      loading: true
    })
}
// 物料试用
export const materialTrialApi = {
  // 通过id查询物料试用详情
  getMaterialTrialById: async params =>
    http({
      url: getUrl('/api-sup/materialTrial/get'),
      method: 'GET',
      params,
      loading: true
    }),
  // 物料试用删除单据
  materialTrialOrderDel: async data =>
    http({
      url: getUrl('/api-sup/materialTrial/bathDeleteByList'),
      method: 'POST',
      data,
      loading: true
    })
}

// 合作终止
export const cooperationEndedApi = {
  // 合作终止拟定单据删除
  orgCatFormDel: async params =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/delete'),
      method: 'GET',
      params,
      loading: true
    })
}

// 现场评审
export const siteAssessmentApi = {
  // 删除现场评审单
  siteFormDel: async params =>
    http({
      url: getUrl('/api-sup/review/siteForm/delete'),
      method: 'GET',
      params,
      loading: true
    })
}

// 准入多个功能调用相同接口
export const accessCommonApi = {
  // 查询供应商列表
  getVendorDataList: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageByDTO'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询单据信息
  getQuaSampleData: async params =>
    http({
      url: getUrl('/api-sup/qua/quaSample/getQualifiedSample'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据资质审查单ID查询供应商档案组织与品类状态
  getOrgCatByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateServiceStatusByReviewId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据资质审查单ID查询银行信息
  getBankJournaByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/bankJournal/listBankJournal'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据供应商ID获取上一次评审信息
  getLastSiteFormMessage: async params =>
    http({
      url: getUrl('/api-sup/review/siteForm/getLastSiteFormMessage'),
      method: 'GET',
      params,
      loading: true
    }),
  // 判断资质审类型返回准入流程类型
  getEntryConfigByQuaReviewType: async params =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/getEntryConfigByQuaReviewType'),
      method: 'GET',
      params,
      loading: true
    })
}

// 资质类
export const quaApi = {
  // 资质审查单据删除
  reviewFormhDel: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 非材资质审查单据删除
  serviceReviewFormhDel: async params =>
    http({
      url: getUrl('/api-sup/review/serviceReviewForm/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 查询资质审查附件配置信息
  // quaReviewType 准入类型
  // categoryId 品类ID

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  getByCategoryId: async categoryId =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/getByCategoryId'),
      method: 'GET',
      params: { categoryId }
    }),

  getCategoryInfoById: async categoryId =>
    http({
      url: getUrl('/api-sup/review/reviewForm/getCategoryInfoById'),
      method: 'GET',
      params: { categoryId }
    }),

  getTemplateFilesByReviewCreate: async params =>
    http({
      url: getUrl('/api-sup/entry/fileconfig/getTemplateFilesByReviewCreate'),
      method: 'GET',
      params
    }),
  // reviewFormId  type: AUTH:供应商评审,SAMPLE:样品确认,MATERIAL:物料试用
  getTemplateFilesByReviewFormId: async params =>
    http({
      url: getUrl('/api-sup/entry/fileconfig/getTemplateFilesByReviewFormId'),
      method: 'GET',
      params
    }),
  // sampleId
  getTemplateFilesBySampleId: async params =>
    http({
      url: getUrl('/api-sup/entry/fileconfig/getTemplateFilesBySampleId'),
      method: 'GET',
      params
    }),
  // reviewFormId
  listOrgCateJournalByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateJournalByReviewId'),
      method: 'GET',
      params
    })
}

// 供应商信息
export const vendorProfileApi = {
  // 供应商清单驳回
  companyGreenChannelDeleteNotDelUser: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/rejectInformation'),
      method: 'POST',
      data,
      loading: true
    }),

  // logger页面运用的 证件到期提醒
  getReminderRecord: async (data) =>
    http({
      url: getUrl('/api-sup/expireReminder/getReminderRecord'),
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商审批通过
  vendorInformationApprove: async params =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/vendorInformationApprove'),
      method: 'GET',
      params,
      loading: true
    })
}

// 财务信息变更
export const financeInfoChangeApi = {
  saveCategoryList: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/saveCategoryList'),
      method: 'POST',
      data
    }),

  list: async data =>
    http({
      url: getUrl('/api-sup/info/financeInfoChangeHeader/listPage'),
      method: 'POST',
      data
    }),

  listByCompanyIdAndOrgId: async data =>
    http({
      url: getUrl('/api-sup/info/financeInfo/listByCompanyIdAndOrgId'),
      method: 'POST',
      data
    }),

  saveOrUpdateFinanceInfoChangeHeader: async data =>
    http({
      url: getUrl('/api-sup/info/financeInfoChangeHeader/saveOrUpdateFinanceInfoChangeHeader'),
      method: 'POST',
      data
    }),

  getDetail: async id =>
    http({
      url: getUrl('/api-sup/info/financeInfoChangeHeader/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  delete: async changeHeaderId =>
    http({
      url: getUrl('/api-sup/info/financeInfoChangeHeader/delete'),
      method: 'GET',
      params: { changeHeaderId },
      loading: true
    })
}

// 资质审查标准管理
export const reviewFormStandard = {
  saveCategoryList: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/saveCategoryList'),
      method: 'POST',
      data,
      loading: true
    }),

  getCategoryList: async id =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/getCategoryList'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  listByCompanyIdAndOrgId: async data =>
    http({
      url: getUrl('/api-sup/info/financeInfo/listByCompanyIdAndOrgId'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/update'),
      method: 'POST',
      data,
      loading: true
    }),

  modify: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  list: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  listAll: async data =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/listAll'),
      method: 'POST',
      data,
      loading: true
    }),

  getDetail: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getDetail2: async id =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/delete'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

// 现场评审
export const siteReviewPlan = {
  recall: async siteFormId =>
    http({
      url: getUrl('/api-sup/review/siteForm/withdraw'),
      method: 'GET',
      params: { siteFormId },
      loading: true
    }),

  planList: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  planAdd: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/add'),
      method: 'POST',
      data,
      loading: true
    }),

  planDelete: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  planUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/modify'),
      method: 'POST',
      data,
      loading: true
    })
}

// 供应商现场评审模板管理
export const siteReviewModel = {
  address: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/getBySiteReviewPlanId'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  list: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  listAll: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/listAll'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/update'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getDetail: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  modify: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewmodel/modify'),
      method: 'POST',
      data,
      loading: true
    })
}

// 计划落实管理
export const siteReviewPlanConfirm = {
  planAdd: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/add'),
      method: 'POST',
      data,
      loading: true
    }),
  planModify: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  planList: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  planGet: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  planDelete: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  planUpdateStatus: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/updateStatus'),
      method: 'POST',
      data,
      loading: true
    })
}
