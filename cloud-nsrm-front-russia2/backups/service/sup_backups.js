/**
 * 供应商 API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 准入流程配置
export const accessProcessConfiguration = {
  listConfigNodeById: async id =>
    http({
      url: getUrl('api-sup/entry/entryConfig/listConfigNodeById/' + id),
      method: 'GET',
      loading: true
    }),

  saveOrUpdateNode: async data =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/saveOrUpdateNode'),
      method: 'POST',
      data
    })
}

// 非材
export const nonMaterial = {
  potentialList: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageForRegistered'),
      method: 'POST',
      data
    })
}

// 财务信息变更
export const financeInfoChange = {
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

// 供应商现场评审模板管理
export const siteReviewModel = {
  address: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/getBySiteReviewPlanId'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  address2: async companyId =>
    http({
      url: getUrl('/api-sup/info/companyInfo/getInfoByParam'),
      method: 'POST',
      params: { companyId }
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
  planList: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  planAdd: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/add'),
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

  planModify: async data =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplanconfirm/modify'),
      method: 'POST',
      data,
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

// 资质审查标准管理
export const reviewFormStandard = {
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

// 品类状态配置
export const vendorStateSetting = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/categoryState/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/categoryState/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  // 供应商清单，查询合同详情功能
  listContractDetail: async data =>
    http({
      url: getUrl('/api-sup/info/orgCategory/listContractDetail'),
      method: 'POST',
      data,
      loading: true
    })
}

// 合作终止
export const orgCatForm = {
  listOrgCategory: async data =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/listOrgCategoryInfoByVendorId'),
      method: 'POST',
      data,
      loading: true
    }),
  listForzenOrgCategory: async data =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/listForzenOrgCategoryInfoByVendorId'),
      method: 'POST',
      data,
      loading: true
    }),
  listByTime: async data =>
    http({
      url: getUrl('/api-sup/sup/categoryState/listByTime'),
      method: 'POST',
      data,
      loading: true
    }),

  getDetail: async id =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/get'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

// 配额清单
export const quotadetail = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  calcQuota: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/calcQuota'),
      method: 'POST',
      data,
      loading: true
    }),

  createQuotaFlow: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/createQuotaFlow'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  batchUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/batchUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

// 配额审批
export const quotaApprove = {
  listFlowPage: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/listFlowPage'),
      method: 'POST',
      data,
      loading: true
    }),

  detailFlowPage: async ({ id }) =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/getQuotaFlowListById'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

// 配额执行偏差报表
export const quotaoffset = {
  listPage: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  save: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/save'),
      method: 'POST',
      data,
      loading: true
    }),

  detailList: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/listDetailPage'),
      method: 'POST',
      data,
      loading: true
    })
}

// 供应商画像
export const portrait = {
  // 分页
  listPage: async ({ vendorId }) =>
    http({
      url: getUrl('/api-sup/info/companyInfo/getVendorImage'),
      method: 'GET',
      params: { vendorId }
    }),
  aggregateAmount: async ({ vendorId, categoryId }) =>
    http({
      url: getUrl('/api-sup/info/companyInfo/aggregateAmount'),
      method: 'GET',
      params: { vendorId, categoryId }
    }),
  findCategory: async companyId =>
    http({
      url: getUrl('/api-sup/info/companyInfo/getInfoByParam'),
      method: 'POST',
      params: { companyId }
    }),
  radar: async ({ vendorId }) =>
    http({
      url: getUrl('/api-sup/risk-rating/getRiskRadarDto'),
      method: 'GET',
      params: { vendorId }
    })
}

// 供应商风险
export const risk = {
  // 分页
  listPage: async data =>
    http({
      url: getUrl('/api-sup/risk/monitoring/listPage'),
      method: 'POST',
      data
    }),
  // 新增
  add: async data =>
    http({
      url: getUrl('/api-sup/risk/monitoring/add'),
      method: 'POST',
      data
    }),
  // 修改
  modify: async data =>
    http({
      url: getUrl('/api-sup/risk/monitoring/modify'),
      method: 'POST',
      data
    }),
  // 提交
  submit: async data =>
    http({
      url: getUrl('/api-sup/risk/monitoring/submit'),
      method: 'POST',
      data
    }),
  // 关闭
  close: async data =>
    http({
      url: getUrl('/api-sup/risk/monitoring/close'),
      method: 'POST',
      data
    }),
  // 获取详情
  get: async riskMonitoringId =>
    http({
      url: getUrl('/api-sup/risk/monitoring/get'),
      method: 'GET',
      params: { riskMonitoringId }
    }),
  // 删除
  deleteItem: async riskMonitoringId =>
    http({
      url: getUrl('/api-sup/risk/monitoring/delete'),
      method: 'GET',
      params: { riskMonitoringId }
    }),
  // 新建审批通过
  addPass: async riskMonitoringId =>
    http({
      url: getUrl('/api-sup/risk/monitoring/addPass'),
      method: 'GET',
      params: { riskMonitoringId }
    }),
  // 关闭审批通过
  closePass: async riskMonitoringId =>
    http({
      url: getUrl('/api-sup/risk/monitoring/closePass'),
      method: 'GET',
      params: { riskMonitoringId }
    }),
  // 查询供应商上期应对措施
  lastHandle: async ({ vendorId, riskType }) =>
    http({
      url: getUrl('/api-sup/risk/monitoring/queryResponses'),
      method: 'GET',
      params: { vendorId, riskType }
    })
}

export const qua = {
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

  listForMaterialSecByVendor: async data =>
    http({
      url: getUrl('/api-inq/price/priceLibrary/listForMaterialSecByVendor'),
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

export const complaintinfo = {
  // 查询供应商投诉清单
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/complaintinfo/listPage'),
      method: 'POST',
      data,
      loading: true
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

export const bom = {
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const osmaterialrequisition = {
  vendorList: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  getVendorById: async id =>
    http({
      // url: getUrl("/api-sup-ce/sup/osmaterialrequisition/getDetailByIdForVendor"),
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  updateOsMaterialRequisition: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisition'),
      method: 'POST',
      data,
      loading: true
    }),
  updateOsMaterialRequisitionForVendor: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisitionForVendor'),
      method: 'POST',
      data,
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),

  getDtoByParam: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisitiondetail/getDtoByParam'),
      method: 'POST',
      data,
      loading: true
    })
}

// 查询供应商联系人信息
export const getContactInfoByCompanyId = async id =>
  http({
    url: getUrl(`/api-sup/info/contactInfo/listContactInfoByCompanyId?companyId=${id}`),
    method: 'GET',
    loading: true
  })

export const vendorManagement = {
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
  // 删除财务信息
  financeInfoDel: async params =>
    http({
      url: getUrl('/api-sup/info/financeInfo/delete'),
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
  // 删除附件
  deleteAttachById: async params =>
    http({
      url: getUrl('/api-sup/info/companyInfo/deleteAttachById'),
      method: 'POST',
      params,
      loading: true
    }),
  // 绿色通道供应商删除
  companyGreenChannelDel: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/companyGreenChannelDelete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商清单驳回
  companyGreenChannelDeleteNotDelUser: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/rejectInformation'),
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商注册撤回
  companyWithdraw: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/withdrawCompany'),
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商档案驳回
  rejectCompanyInfo: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/rejectCompanyInfo'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询供应商档案变更记录
  getCompanyStatusLog: async data =>
    http({
      url: getUrl('/api-sup/statusLog/companyStatusLog/listAllByParam'),
      method: 'POST',
      data,
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
    }),
  // 查询单据信息
  getQuaSampleData: async params =>
    http({
      url: getUrl('/api-sup/qua/quaSample/getQualifiedSample'),
      method: 'GET',
      params,
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
  // 根据资质审查单ID查询供应商档案组织与品类状态
  getOrgCatByReviewId: async params =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listOrgCateServiceStatusByReviewId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据供应商ID查询供应商档案组织与品类状态
  listOrgCateServiceStatusByCompanyId: async params =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listOrgCateServiceStatusByCompanyId'),
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
  // 更新 修改单据信息
  // 样品
  // /api-sup/qua/quaSample/saveTemporary 暂存
  // /api-sup/qua/quaSample/publish 发布
  // /api-sup/qua/quaSample/confirmed 确认
  // /api-sup/qua/quaSample/refused 拒绝
  // /api-sup/qua/quaSample/submittedSave 提交保存
  // /api-sup/qua/quaSample/submitted 提交
  // /api-sup/qua/quaSample/approved 批准
  // /api-sup/qua/quaSample/rejected 驳回
  // 供方生效
  // /api-sup/review/effectForm/saveTemporary 暂存
  // /api-sup/review/effectForm/submitted 提交
  // 物料试用
  // /api-sup/materialTrial/saveTemporary
  // /api-sup/materialTrial/publish 发布
  // /api-sup/materialTrial/confirmed 确认
  // /api-sup/materialTrial/refused 拒绝
  // /api-sup/materialTrial/submittedSave 提交保存
  // /api-sup/materialTrial/submitted 提交
  // /api-sup/materialTrial/approved 批准
  // /api-sup/materialTrial/rejected 驳回
  // 采购目录
  // /api-sup/saveOrUpdateCatalog 新增 保存
  // 合作终止
  // /api-sup/orgcategory/orgCatForm/saveTemporary
  // /api-sup/orgcategory/orgCatForm/submitted
  saveOrUpdatuOrderByStatus: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询供应商列表
  getVendorDataList: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageByDTO'),
      method: 'POST',
      data,
      loading: true
    }),
  // 查询资质审查单
  getReviewFormDataList: async data =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listPageByParm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 判断资质审类型返回准入流程类型
  getEntryConfigByQuaReviewType: async params =>
    http({
      url: getUrl('/api-sup/entry/entryConfig/getEntryConfigByQuaReviewType'),
      method: 'GET',
      params,
      loading: true
    }),
  // 通过公司id和资质审查类型查询资质审查单
  getReviewFormByParm: async data =>
    http({
      url: getUrl('/api-sup/review/reviewForm/listPageByParm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 根据资质审查ID获取现场评审单
  getSiteFormByReviewFormId: async params =>
    http({
      url: getUrl('/api-sup/review/siteForm/getSiteFormByReviewFormId'),
      method: 'GET',
      params,
      loading: true
    }),
  // 通过ID获取生效单详情 effectFormId
  getEffectFormDTOById: async params =>
    http({
      url: getUrl('/api-sup/review/effectForm/getEffectFormDTOById'),
      method: 'GET',
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
  // 查询样品确认单
  getSampleListPageByParam: async data =>
    http({
      url: getUrl('/api-sup/qua/quaSample/listPageByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  // 物料试用删除单据
  materialTrialOrderDel: async data =>
    http({
      url: getUrl('/api-sup/materialTrial/bathDeleteByList'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购目录
  // 查询采购目录详情信息
  getPurchaseCataLogById: async params =>
    http({
      url: getUrl('/api-sup/purchaseCataLog/get'),
      method: 'GET',
      params,
      loading: true
    }),
  // 删除采购目录
  purchaseCataLogDel: async data =>
    http({
      url: getUrl('/api-sup/purchaseCataLog/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除现场评审单
  siteFormDel: async params =>
    http({
      url: getUrl('/api-sup/review/siteForm/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 供方生效单删除
  vendoeEffectDel: async data =>
    http({
      url: getUrl('/api-sup/review/effectForm/bachDeleteByList'),
      method: 'POST',
      data,
      loading: true
    }),
  // 合作终止拟定单据删除
  orgCatFormDel: async params =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根据控制类型和供应商ID分页查询组织与品类
  getOrgCatFormByParm: async params =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/listOrgCateServiceStatusPageByParm'),
      method: 'GET',
      params,
      loading: true
    }),
  // 通过ID获取合作终止单据明细
  getOrgCatFormDTOById: async params =>
    http({
      url: getUrl('/api-sup/orgcategory/orgCatForm/getOrgCatFormDTO'),
      method: 'GET',
      params,
      loading: true
    }),
  // 供应商变更
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
  // 供应商信息变更暂存 提交
  // /api-sup/change/infoChange/saveTemporary 暂存
  // /api-sup/change/infoChange/submitted 提交
  // /api-sup/change/infoChange/approved 审批
  // /api-sup/change/infoChange/rejected 驳回
  saveOrUpdateChannelInfo: async (url, data) =>
    http({
      url: getUrl(url),
      method: 'POST',
      data,
      loading: true
    })
}
