/**
 * 合同验收API
 */

import http from '@/utils/axios/http'
const getUrl = (path) => `${path}`

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

export default {
  performAcceptance
}
