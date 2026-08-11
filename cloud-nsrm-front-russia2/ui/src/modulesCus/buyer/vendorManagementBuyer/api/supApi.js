/**
 * 供应商 API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 供应商投诉
export const complaintInfo = {
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

// 合作终止
export const orgCatForm = {
  /* 组织和品类受限解除 */
  orgOrCategoryLimitRemove: async data =>
    http({
      url: getUrl('/api-sup/pj/orgCatForm/listOrgCategoryInfoByVendorId'),
      method: 'POST',
      data,
      loading: true
    }),
  /* 组织和品类受限 */
  orgOrCategoryLimit: async data =>
    http({
      url: getUrl('/api-sup/pj/orgCatForm/listOrgCategoryInfoByVendorIdForLimt'),
      method: 'POST',
      data,
      loading: true
    }),
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

// 非材
export const nonMaterialApi = {
  potentialList: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageForRegistered'),
      method: 'POST',
      data
    }),
    // 项目下拉
  findCalculatedScoreItemsList: params =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/findCalculatedScoreItemsList'),
      method: 'GET',
      params
    })
}

// 查询供应商联系人信息
export const getContactInfoByCompanyId = async id =>
  http({
    url: getUrl(`/api-sup/info/contactInfo/listContactInfoByCompanyId?companyId=${id}`),
    method: 'GET',
    loading: true
  })

// 证件到期提醒
export const expirationReminder = {
  // 保存
  modify: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  blockUpOrStartUpReminder: async data =>
    http({
      url: getUrl('/api-sup/info/vendorInformation/blockUpOrStartUpReminder'),
      method: 'POST',
      data,
      loading: true
    })
}

// 跨组织引入
export const crossOrgImport = {
  // 审批
  approvalOne: async importId =>
    http({
      url: getUrl('/api-sup/vendorImport/approve'),
      method: 'GET',
      params: { importId },
      loading: true
    }),
  // 删除
  delRowData: async data =>
    http({
      url: getUrl('/api-sup/vendorImport/delete'),
      method: 'POST',
      data,
      loading: true
    }),

  // 废弃
  abandon: async importId =>
    http({
      url: getUrl('/api-sup/vendorImport/abandon'),
      method: 'GET',
      params: { importId },
      loading: true
    }),

  vendorImportSubmit: async data =>
    http({
      url: getUrl('/api-sup/vendorImport/submit'),
      method: 'POST',
      data,
      loading: true
    }),

  saveTemporary: async data =>
    http({
      url: getUrl('/api-sup/vendorImport/saveTemporary'),
      method: 'POST',
      data,
      loading: true
    }),

  getBuByOrgId: async organizationId =>
    http({
      url: getUrl('/api-base/organization/organization/getBuByOrgId'),
      method: 'GET',
      params: { organizationId },
      loading: true
    }),

  listOrgCategoryByParam: async data =>
    http({
      url: getUrl('/api-sup/vendorImport/listOrgCategoryByParam'),
      method: 'POST',
      data,
      loading: true
    }),

  getOrgByVendorId: async vendorId =>
    http({
      url: getUrl('/api-sup/vendorImport/getOrgByVendorId'),
      method: 'GET',
      params: { vendorId },
      loading: true
    }),

  deleteOneList: async importDetailId =>
    http({
      url: getUrl('/api-sup/vendorImport/vendor-import-detail/delete'),
      method: 'GET',
      params: { importDetailId },
      loading: true
    }),

  listAllOrganization: async data =>
    http({
      url: getUrl('/api-base/organization/organization/listAllOrganization'),
      method: 'POST',
      data,
      loading: true
    }),

  getVendorImportDetail: async importId =>
    http({
      url: getUrl('/api-sup/vendorImport/getVendorImportDetail'),
      method: 'GET',
      params: { importId },
      loading: true
    })
}

// 多个功能公用sup
export const supCommonApi = {
  // 供应商清单，查询合同详情功能
  listContractDetail: async data =>
    http({
      url: getUrl('/api-sup/info/orgCategory/listContractDetail'),
      method: 'POST',
      data,
      loading: true
    }),
  // 看查品类
  getCategoryList: async id =>
    http({
      url: getUrl('/api-sup/sup/sitereviewplan/getCategoryList'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  // 查询品类信息
  findCategory: async companyId =>
    http({
      url: getUrl('/api-sup/info/companyInfo/getInfoByParam'),
      method: 'POST',
      params: { companyId }
    })
}
