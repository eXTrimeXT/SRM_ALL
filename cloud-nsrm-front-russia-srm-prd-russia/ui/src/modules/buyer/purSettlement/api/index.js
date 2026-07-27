/* 采购结算API */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 预付款申请
export const advancePaymentApi = {
  // 查询合同履约预付款明细列表
  prepaymentDetails: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/getPerInvoiceDetailPage'),
      method: 'POST',
      data
    })
}

// 付款申请单
export const purPaymentApplyApi = {
  // 查询合同履约预付款明细列表
  prepaymentDetails: async data =>
    http({
      url: getUrl('/api-cm/contract/performInvoice/getPerInvoiceDetailPage'),
      method: 'POST',
      data
    })
}
