/**
 * 基础设置新增API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 公开寻源的接口 by 伟龙
export const portalSourcing = {
  // 公开寻源列表
  listPage: async data =>
    http({
      url: getUrl('/api-inq/inq-anon/reqhead/listPage'),
      method: 'POST',
      data
    })
}

// 获取指定组织下指定类型的组织列表接口
export const getOrganizationByOrgCode = async data =>
  http({
    url: getUrl('/api-base/organization/organization/getOrganizationByOrgCode'),
    method: 'POST',
    data,
    loading: true
  })
// 获取供应商的组织列表接口
export const getSupplierOrgTree = async data =>
  http({
    url: getUrl('/api-sup/info/orgCategory/supplierTree'),
    method: 'POST',
    data,
    loading: true
  })

export const purchase = {
  // 根据转换的两个币种查询汇率
  purchaseExchangeRate: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseExchangeRate/listByParams'),
      method: 'POST',
      data,
      loading: true
    }),

  // 根据币种和汇率类型批量查询汇率
  getPriceTaxList: async data =>
    http({
      url: '/api-base/purchase/purchaseExchangeRate/getPriceTaxList',
      method: 'POST',
      data,
      loading: true
    }),

  // 查询准入流程配置里面维护品类的列表
  listPageForEntryConfig: async data =>
    http({
      url: getUrl('/api-base/purchase/purchaseCategory/listPageForEntryConfig'),
      method: 'POST',
      data,
      loading: true
    })
}

// 页面配置
export const pageConfig = {
  // 条件配置获取
  getCurrentConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/getCurrent'),
      method: 'POST',
      data
    }),
  // 保存用户设置
  saveUserConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/save'),
      method: 'POST',
      data,
      loading: true
    }),
  // 清除用户配置
  removeUserConfig: async data =>
    http({
      url: getUrl('/api-base/base/page_view_config/removeUserConfig'),
      method: 'POST',
      data,
      loading: true
    })
}

// 审批流相关接口
export const flowAPI = {
  // 流程模式
  getFlowIntegrationMode: async (data, loading = false) =>
    http({
      method: 'POST',
      url: getUrl('/api-base/flow/event/getFlowIntegrationMode'),
      data,
      loading
    }),
  // 查询代办
  queryTodo: async (params, loading = false) =>
    http({
      method: 'GET',
      url: getUrl('/api-base/flow/event/queryTodo'),
      params,
      loading
    }),
  // 列表批量审批获取流程单据ID
  getFlowMainId: async id =>
    http({
      method: 'GET',
      url: getUrl('/api-base/flow/event/flow/mainId'),
      params: { businessId: id }
    })
}

export const themeConfig = {
  // 获取当前用户主题
  getSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/getSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 新增当前用户主题
  addSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/addSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 更新当前用户主题
  updateSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/updateSystemStyle',
      method: 'POST',
      data,
      loading: true
    }),
  // 删除当前用户主题
  deleteSystemStyle: async data =>
    http({
      url: '/api-rbac/systemStyle/deleteSystemStyle',
      method: 'POST',
      data,
      loading: true
    })
}
