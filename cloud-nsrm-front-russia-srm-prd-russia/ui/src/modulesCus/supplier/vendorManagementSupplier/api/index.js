/* 供应商分级管理 */

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

/* 供方样品确认 */
export const sampleConfirmed = {
  // 查询供应商列表
  getVendorDataList: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageByDTO'),
      method: 'POST',
      data,
      loading: true
    }),
  // reviewFormId  type: AUTH:供应商评审,SAMPLE:样品确认,MATERIAL:物料试用
  getTemplateFilesByReviewFormId: async params =>
    http({
      url: getUrl('/api-sup/entry/fileconfig/getTemplateFilesByReviewFormId'),
      method: 'GET',
      params
    }),
  // 根据资质审查单ID查询供应商档案组织与品类状态
  getOrgCatByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateServiceStatusByReviewId'),
      method: 'GET',
      params,
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
  saveOrUpdatuOrderByStatus: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    }),
  // 样品确认单据删除(批量)
  quaSampleBathDel: async data =>
    http({
      url: getUrl('/api-sup/qua/quaSample/bathDeleteByList'),
      method: 'POST',
      data,
      loading: true
    }),
  // reviewFormId
  listOrgCateJournalByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateJournalByReviewId'),
      method: 'GET',
      params
    }),
  getEntryConfigRecord: async params =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/getEntryConfigRecord'),
      method: 'GET',
      params,
      loading: true
    })
}

// 供方物料试用
export const materialTrial = {
  // 物料试用删除单据
  materialTrialOrderDel: async data =>
    http({
      url: getUrl('/api-sup/materialTrial/bathDeleteByList'),
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
  // sampleId
  getTemplateFilesBySampleId: async params =>
    http({
      url: getUrl('/api-sup/entry/fileconfig/getTemplateFilesBySampleId'),
      method: 'GET',
      params
    }),
  // 根据资质审查单ID查询供应商档案组织与品类状态
  getOrgCatByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateServiceStatusByReviewId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除附件
  deleteAttachById: async params =>
    http({
      url: getUrl('/api-sup/info/companyInfo/deleteAttachById'),
      method: 'POST',
      params,
      loading: true
    }),
  // 通过id查询物料试用详情
  getMaterialTrialById: async params =>
    http({
      url: getUrl('/api-sup/materialTrial/get'),
      method: 'GET',
      params,
      loading: true
    }),
  saveOrUpdatuOrderByStatus: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    })
}

// 供方计划落实管理
export const siteReviewPlanConfirm = {
  planList: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/listPage'),
      method: 'POST',
      data,
      loading: false
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
    }),
  getCategoryList: async id =>
    http({
      url: getUrl('/api-sup/sup/reviewformstandard/getCategoryList'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

// 供应商投诉
export const complaintInfo = {
  // 查询供应商投诉清单
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/listPage'),
      method: 'POST',
      data,
      loading: false
    }),
  // 提交供应商投诉清单
  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  // 删除供应商投诉清单
  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  // 投诉单的回复
  review: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintreview/review'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取投诉单的回复
  getByCompliantInfoId: async compliantInfoId =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintreview/getByCompliantInfoId'),
      method: 'POST',
      params: { compliantInfoId }
    }),
  // 发动请求修改投诉单状态
  requireComplaint: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/require'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购商设置责任人
  setPersonLiable: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/setPersonLiable'),
      method: 'POST',
      data
    })
}

// 供方档案信息
export const vendorArchival = {
  // 获取下一级节点
  getCatChildrenData: async params =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listChildren'),
      method: 'POST',
      params,
      loading: true
    }),
  // 公司性质以及境外关系查询配置信息
  getConfigByTemplate: async data =>
    http({
      url: '/api-sup/dim/dimTemplate/getConfigByTemplate',
      method: 'POST',
      data
    }),
  // 删除附件
  deleteAttachById: async params =>
    http({
      url: getUrl('/api-sup/info/companyInfo/deleteAttachById'),
      method: 'POST',
      params,
      loading: true
    }),
  // 删除财务信息
  financeInfoDel: async params =>
    http({
      url: getUrl('/api-sup/info/financeInfo/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除银行信息
  bankInfoDel: async params =>
    http({
      url: getUrl('/api-sup/info/bankInfo/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除联系人信息
  contactInfoDel: async params =>
    http({
      url: getUrl('/api-sup/info/contactInfo/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除合作组织信息
  orgInfoDel: async params =>
    http({
      url: getUrl('/api-sup/info/orgInfo/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除组织品类信息
  orgCategoryDel: async params =>
    http({
      url: getUrl('/api-sup/info/orgCategory/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 查询公司信息
  getCompanyForEdit: async data =>
    http({
      url: '/api-sup/info/companyInfo/getInfoByParam',
      method: 'POST',
      params: data,
      loading: true
    }),
  // 绿色通道暂存 提交
  // /api-sup/info/companyInfo/saveCompanyGreenChannel 暂存
  // /api-sup/info/companyInfo/companyGreenChannelSubmit 提交
  saveOrUpdatuGreenChannelInfo: async (url, data) =>
    http({
      url: getUrl(url),
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

// 现场评审
export const siteReviewPlan = {
  recall: async siteFormId =>
    http({
      url: getUrl('/api-sup/review/siteForm/withdraw'),
      method: 'GET',
      params: { siteFormId },
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
