/**
 * 合同履约计划API
 */

import http from '@/utils/axios/http'
const getUrl = (path) => `${path}`

const performPlan = {
  listPage: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  vendorListPerOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  rejectedPerOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/rejectedPerOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  approvePerOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/approvePerOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  submitPerOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/submitPerOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-cm/contract/performPlan/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  abandon: async perPlanId =>
    http({
      url: getUrl('/api-cm/contract/performPlan/abandon'),
      method: 'GET',
      params: { perPlanId },
      loading: true
    }),
  deleteById: async perPlanId =>
    http({
      url: getUrl('/api-cm/contract/performPlan/deleteById'),
      method: 'GET',
      params: { perPlanId },
      loading: true
    }),
  getPerOrderById: async perPlanId =>
    http({
      url: getUrl('/api-cm/contract/performPlan/get'),
      method: 'GET',
      params: { perPlanId },
      loading: true
    }),
  getPerDelivOrder: async perOrderPlanId =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/getPerDelivOrder'),
      method: 'GET',
      params: { perOrderPlanId },
      loading: true
    }),
  getPerPlanByContractNo: async contractNo =>
    http({
      url: getUrl('/api-cm/contract/performPlan/getPerPlanByContractNo'),
      method: 'GET',
      params: { contractNo },
      loading: true
    }),
  deletePerDelivOrderAtt: async perDelivOrderAttId =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/deletePerDelivOrderAtt'),
      method: 'GET',
      params: { perDelivOrderAttId },
      loading: true
    }),
  addOrUpdatePerDelivOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/addOrUpdatePerDelivOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  purchaseSubmitPerDelivOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/purchaseSubmitPerDelivOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  vendorSubmitPerDelivOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/vendorSubmitPerDelivOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  rejectedDelivOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/rejectedDelivOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  approveDelivOrder: async data =>
    http({
      url: getUrl('/api-cm/contract/performDelivOrder/approveDelivOrder'),
      method: 'POST',
      data,
      loading: true
    }),
  getPerOrderPlan: async (
    performTemplHeadId, // 根据模板id获取里程碑节点
  ) =>
    http({
      url: getUrl('/api-cm/contract/performPlan/getPerPlanMilestoneById'),
      method: 'GET',
      params: { performTemplHeadId },
      loading: true
    })
}

export default {
  performPlan
}
