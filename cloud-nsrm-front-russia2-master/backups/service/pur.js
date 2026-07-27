/**
 * 采购结算 API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`
// 开票
// 供应商暂存
export const saveTemporary = async data =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/saveTemporary'),
    method: 'POST',
    data,
    loading: true
  })
// 供应商暂存
export const invoiceNoticeSubmit = async data =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/submit'),
    method: 'POST',
    data,
    loading: true
  })

// 查询有效对账单
export const listStatementHeadPage = async data =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/listStatementHeadByParm'),
    method: 'POST',
    data,
    loading: true
  })

// 查询单据物料信息
export const getStatementById = async params =>
  http({
    url: getUrl('/api-sup-ce/pm/ps/statementHead/getStatementById'),
    method: 'GET',
    params,
    loading: true
  })
// 获取开票单据详情
export const getInvoiceNoticeSaveDTO = async params =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/getInvoiceNoticeSaveDTO'),
    method: 'GET',
    params,
    loading: true
  })
// 采购员驳回
export const buyerReject = async data =>
  http({
    url: getUrl('/api-sup-ce/ps/invoice/invoiceNotice/buyerReject'),
    method: 'POST',
    data,
    loading: true
  })
  // 采购初审批
export const buyerFirstReview = async data =>
  http({
    url: getUrl('/api-sup-ce/ps/invoice/invoiceNotice/buyerFirstReview'),
    method: 'POST',
    data,
    loading: true
  })
// 财务驳回
export const financeReject = async data =>
  http({
    url: getUrl('/api-sup-ce/ps/invoice/invoiceNotice/financeReject'),
    method: 'POST',
    data,
    loading: true
  })
// 财务终审批
export const financeApprove = async data =>
  http({
    url: getUrl('/api-sup-ce/ps/invoice/invoiceNotice/financeFinalReview'),
    method: 'POST',
    data,
    loading: true
  })
// 供应商废弃
export const vendorabandon = async data =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/abandon'),
    method: 'POST',
    data,
    loading: true
  })
// 供应商删除
export const invoiceNoticeDel = async params =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/deleteByInvoiceNoticeId'),
    method: 'GET',
    params,
    loading: true
  })

// 编辑明细删除
export const batchDeleteByStatementHeadId = async data =>
  http({
    url: getUrl('/api-sup-ce/invoice/invoiceNotice/batchDeleteByStatementHeadId'),
    method: 'POST',
    data,
    loading: true
  })

// 对账单跟踪表分页查询
export const reconciliationTrackList = data => http({
  url: getUrl('/api-sup-ce/reconciliation/reconciliationTrack/listPage'),
  method: 'POST',
  data
})

// 应付款明细表分页查询
export const accountsPayableList = data => http({
  url: getUrl('/api-sup-ce/reconciliation/accountsPayable/listPage'),
  method: 'POST',
  data
})

// 应付未开票表分页查询
export const shouldPayOpenBillList = data => http({
  url: getUrl('/api-sup-ce/reconciliation/shouldPayOpenBill/listPage'),
  method: 'POST',
  data
})
