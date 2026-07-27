/**
 * 合同履约API
 */

import http from '@/utils/axios/http'
const getUrl = (path) => `${path}`

const performanceTpl = {
  listPage: async data =>
    http({
      url: getUrl('/api-cm/contract/performTempl/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  bathDelete: async data =>
    http({
      url: getUrl('/api-cm/contract/performTempl/bathDelete'),
      method: 'POST',
      data,
      loading: true
    }),
  failure: async performTemplHeadId =>
    http({
      url: getUrl('/api-cm/contract/performTempl/failure'),
      method: 'POST',
      params: { performTemplHeadId },
      loading: true
    }),
  addOrUpdate: async data =>
    http({
      url: getUrl('/api-cm/contract/performTempl/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  submit: async data =>
    http({
      url: getUrl('/api-cm/contract/performTempl/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  getDetailById: async performTemplHeadId =>
    http({
      url: getUrl('/api-cm/contract/performTempl/getDetailById'),
      method: 'GET',
      params: { performTemplHeadId },
      loading: true
    })
}

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

const performAcceptance = {
  getByPerPlanMilestoneId: async perPlanMilestoneId =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/getByPerPlanMilestoneId'),
      method: 'GET',
      params: { perPlanMilestoneId },
      loading: true
    }),
  addOrUpdate: async data =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  getByPerAcceptanceId: async perAcceptanceId =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/getByPerAcceptanceId'),
      method: 'GET',
      params: { perAcceptanceId },
      loading: true
    }),
  vendorSubmit: async data =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/vendorSubmit'),
      method: 'POST',
      data,
      loading: true
    }),
  approvePass: async data =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/approvePass'),
      method: 'POST',
      data,
      loading: true
    }),
  rejected: async data =>
    http({
      url: getUrl('/api-cm/contract/performAcceptance/rejected'),
      method: 'POST',
      data,
      loading: true
    })
}

const performInvoice = {
  addOrUpdate: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    }),
  abandon: async id =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/abandoned'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  approved: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/approved'),
      method: 'POST',
      data,
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  get: async id =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  listPage: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/listPerInvoiceDetailPage'),
      method: 'POST',
      data,
      loading: true
    }),
  rejected: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/rejected'),
      method: 'POST',
      data,
      loading: true
    }),
  vendorSubmit: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/vendorSubmit'),
      method: 'POST',
      data,
      loading: true
    }),
  queryContractMaterial: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/queryContractMaterialTow'),
      method: 'POST',
      data,
      loading: true
    }),
  getPerPayPlanByContractNo: async contractNo =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/getPerPayPlanByContractNo'),
      method: 'GET',
      params: { contractNo },
      loading: true
    }),
  getMileByContractNo: async contractNo =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/getPerPlanMilestoneByContractNo'),
      method: 'GET',
      params: { contractNo },
      loading: true
    }),
  deletePerInvoiceDetailById: async perInvoiceDetailId =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/deletePerInvoiceDetailById'),
      method: 'GET',
      params: { perInvoiceDetailId },
      loading: true
    })
}

export default {
  performanceTpl,
  performPlan,
  performAcceptance,
  performInvoice
}
