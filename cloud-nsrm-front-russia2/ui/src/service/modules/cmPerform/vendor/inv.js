/**
 * 合同履约开票协同API
 */

import http from '@/utils/axios/http'
const getUrl = (path) => `${path}`

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
  performInvoice
}
