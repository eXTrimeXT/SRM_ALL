/**
 * 供应商 风险画像API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 供应商风险
export const riskApi = {
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
    })
}

// 供应商画像
export const portraitApi = {
  // 分页
  listPage: async ({ vendorId }) =>
    http({
      url: getUrl('/api-sup/info/companyInfo/getVendorImage'),
      method: 'GET',
      params: { vendorId }
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
