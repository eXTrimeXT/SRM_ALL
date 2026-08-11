import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/SouReqHeadBuyer'
const hisBasePath = '/api-sou/api-ql/SouInfoHistoryBuyer'
const applyBasePath = '/api-sou/api-ql/SouReqApplyBuyer'
const refundBasePath = '/api-sou/api-ql/SouIntDepositRefundBuyer'
const invoiceBasePath = '/api-sou/api-ql/SouIntDepositInvoiceBuyer'
const method = 'POST'

export default {
  listPageUrl: `${basePath}/query`,
  delete: data =>
    http({
      url: `${basePath}/delete`,
      method,
      data,
      loading: true
    }),
  save: data =>
    http({
      url: `${basePath}/save`,
      method,
      data,
      loading: true
    }),
  submit: data =>
    http({
      url: `${basePath}/submit`,
      method,
      data,
      loading: true
    }),
  read: data =>
    http({
      url: `${basePath}/read`,
      method,
      data,
      loading: true
    }),
  updatePublicEndTime: data =>
    http({
      url: `${basePath}/updatePublicEndTime`,
      method,
      data,
      loading: true
    }),
  publicHisSubmit: data =>
    http({
      url: `${hisBasePath}/submit`,
      method,
      data,
      loading: true
    }),
  publicHisInfo: data =>
    http({
      url: `${hisBasePath}/query`,
      method,
      data,
      loading: true
    }),
  publicHisRead: data =>
    http({
      url: `${hisBasePath}/read`,
      method,
      data,
      loading: true
    }),
  cancelPublic: data =>
    http({
      url: `${basePath}/cancelPublic`,
      method,
      data,
      loading: true
    }),
  // 查询意向金退款
  applyDepositQuery: data =>
    http({
      url: `${applyBasePath}/query`,
      method,
      data,
      loading: true
    }),
  // 意向金退款提交
  refundSubmit: data =>
    http({
      url: `${refundBasePath}/submit`,
      method,
      data,
      loading: true
    }),
  // 意向金退款历史
  refundQuery: data =>
    http({
      url: `${refundBasePath}/query`,
      method,
      data,
      loading: true
    }),
  // 报名处理
  handleApply: data =>
    http({
      url: `${applyBasePath}/handleApply`,
      method,
      data,
      loading: true
    }),
  // 查看开票详情
  invocieQuery: data =>
    http({
      url: `${invoiceBasePath}/query`,
      method,
      data,
      loading: true
    }),
  // 发起供应商推荐
  createVendorRecommend: data =>
    http({
      url: `${applyBasePath}/createVendorRecommend`,
      method,
      data,
      loading: true
    }),
  // 查询报名详情
  getApplyInfo: data =>
    http({
      url: `${applyBasePath}/getApplyInfo`,
      method,
      data,
      loading: true
    })

}
