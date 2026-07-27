/**
 * @description 供应商 - 招标
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 供应商 - 询价协同
const vendorPath = '/api-sou/vendor/bid'

// order 流程 Controlller
const orderPath = `${vendorPath}/order`
const order = {
  // 列表查询
  listPageUrl: `${orderPath}/pageOrder`,
  // 撤回报价
  withdraw: data =>
    http({
      url: `${orderPath}/withdraw`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询询价单详情
  getOrderInfo: id =>
    http({
      url: `${orderPath}/getOrderInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交报价
  editOrder: data =>
    http({
      url: `${orderPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询付款账期信息
  getOrderPaymentsUrl: `${orderPath}/getOrderPayments`,
  // 查询公式报价
  getOrderFormulaPricesUrl: `${orderPath}/getOrderFormulaPrices`,
  // 计算公式报价
  computeFormulaPriceUrl: `${orderPath}/computeFormulaPrice`,
  // 查询单据想起
  projectInfo: id =>
    http({
      url: `${orderPath}/projectInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查看项目需求
  requireInfo: id =>
    http({
      url: `${orderPath}/requireInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查看结果
  pageOrderResultUrl: `${orderPath}/pageOrderResult`,
  // 查看报名信息
  signUpInfo: id =>
    http({
      url: `${orderPath}/signUpInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查看投标详情
  getOrderDetails: data =>
    http({
      url: `${orderPath}/getOrderDetails`,
      method: 'POST',
      data,
      loading: true
    }),
  // 获取保证金退款详情
  getBondRefundDetail: id =>
    http({
      url: `/api-sou/ext/vendor/bid/getSouMarginRecord?projectId=${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * bond 保证金 Controller
 */
const bondPath = `${vendorPath}/bond`
const bond = {
  // 查看保证金缴纳情况
  getBondUrl: id => `${bondPath}/getBond/${id}`,
  // 缴纳保证金
  submitBond: `${bondPath}/submitBond`
}

/**
 * signUp 报名 Controller
 */
const signUpPath = `${vendorPath}/sign-up`
const signUp = {
  // 查询供应商报名附件信息
  getSignUpInfo: id =>
    http({
      url: `${signUpPath}/getSignUpInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 供应商报名
  vendorSignUp: data =>
    http({
      url: `${signUpPath}/vendorSignUp`,
      method: 'POST',
      data,
      loading: true
    })
}

const sourcing = {
  getInfo: async params =>
  http({
    url: getUrl('/api-sou/api-ql/SouIntDepositInvoice/getInfo'),
    method: 'POST',
    data,
    loading: true
  }),
}

export default {
  order,
  bond,
  signUp,
  sourcing
}
