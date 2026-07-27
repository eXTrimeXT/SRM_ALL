import http from '@/utils/axios/http'
const basePath = '/api-sou/api-ql/SouReqHead'

const souIntDepositBasePath = '/api-sou/api-ql/SouIntDepositRefund'
const souIntInvoiceBasePath = '/api-sou/api-ql/SouIntDepositInvoice'
const applyBasePath = '/api-sou/api-ql/SouReqApply'
const method = 'POST'

export default {
  listPageUrl: `${basePath}/query`,
  // 查看意向金退款详情
  SouIntDeposit: data =>
    http({
      url: `${souIntDepositBasePath}/query`,
      method,
      data,
      loading: true
    }),
  // 意向金申请
  submitInvoice: data =>
    http({
      url: `${souIntInvoiceBasePath}/submit `,
      method,
      data,
      loading: true
    }),
  // 意向金开票申请
  queryInvoice: data =>
    http({
      url: `${souIntInvoiceBasePath}/query `,
      method,
      data,
      loading: true
    }),
  // 报名
  apply: data =>
    http({
      url: `${applyBasePath}/query `,
      method,
      data,
      loading: true
    }),
  // 报名详情
  getApplyInfo: data =>
    http({
      url: `${applyBasePath}/getApplyInfo`,
      method,
      data,
      loading: true
    }),
  // 报名暂存
  save: data =>
    http({
      url: `${applyBasePath}/save`,
      method,
      data,
      loading: true
    }),
  // 报名提交
  applySubmit: data =>
    http({
      url: `${applyBasePath}/apply`,
      method,
      data,
      loading: true
    }),
  // 撤回报名
  withdraw: data =>
    http({
      url: `${applyBasePath}/withdraw`,
      method,
      data,
      loading: true
    })
}
