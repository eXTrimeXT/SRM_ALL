import http from '@/utils/axios/http'

// 供应商 - 竞价项目列表
const vendorPath = '/api-sou/vendor/comp'

/**
 * order 报价 Controlller
 */
const orderPath = `${vendorPath}/order`
const order = {
  // 列表查询
  listPageUrl: `${orderPath}/pageOrder`,
  // 查询报价单详情
  getOrderInfo: id =>
    http({
      url: `${orderPath}/getOrderInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交 报价
  editOrder: data =>
    http({
      url: `${orderPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询项目信息
  getProjectInfo: id =>
    http({
      url: `${orderPath}/projectInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询项目需求
  getRequireInfo: id =>
    http({
      url: `${orderPath}/requireInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询报名详情
  getSignUpInfo: id =>
    http({
      url: `${orderPath}/signUpInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询报价明细
  getOrderDetails: data =>
    http({
      url: `${orderPath}/getOrderDetails`,
      method: 'POST',
      data,
      loading: true
    })
}

// 小鹏迁过来的报价页面用的接口
const orderQuotation = {
  // 列表查询
  listPageUrl: `${orderPath}/page`,
  // 查询详情 （报价）
  getInitDetail: params =>
    http({
      url: `${orderPath}/getInitDetail`,
      method: 'GET',
      params,
      loading: true
    }),
  // 提交报价
  editOrder: data =>
    http({
      url: `${orderPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询实时排名
  getRankingData: data =>
    http({
      url: `${orderPath}/getRankingProject`,
      method: 'POST',
      data,
      loading: false
    }),
  // 查询详情
  getInitDetails: params =>
    http({
      url: `${orderPath}/getInitDetails`,
      method: 'GET',
      params,
      loading: true
    }),
  // 查询报价明细
  getPageOrderResult: data =>
    http({
      url: `${orderPath}/pageOrderResult`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询报价明细
  getOrderItemHis: data =>
    http({
      url: `${orderPath}/pageOrderItemHis`,
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * signUp 报名 Controlller
 */
const signUpPath = `${vendorPath}/sign-up`
const signUp = {
  // 查询报名信息
  getSignUpInfo: id =>
    http({
      url: `${signUpPath}/getSignUpInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交 报名信息
  vendorSignUp: data =>
    http({
      url: `${signUpPath}/vendorSignUp`,
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  order,
  signUp,
  orderQuotation
}
