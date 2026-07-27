import http from '@/utils/axios/http'

// 供应商 - 竞价单（新）
const vendorPath = '/api-sou/api-ql'

/**
 * order 报价 Controlller
 */
const orderPath = `${vendorPath}/AuctSouProjectForVendor`
const order = {
  // 列表查询
  listPageUrl: `${orderPath}/listOrders`,
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
  // 查询寻源项目基本信息
  getProjectInfo: data =>
    http({
      url: `${orderPath}/getProjectInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询项目需求
  getRequireInfo: data =>
    http({
      url: `${orderPath}/getRequireInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询报名详情
  getSignUpInfo: data =>
    http({
      url: `${orderPath}/getVendorSignUpDetail`,
      method: 'POST',
      data,
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
const orderQuoPath = `${vendorPath}/AuctSouOrderForVendor`
const orderQuotation = {
  // 列表查询
  listPageUrl: `${orderQuoPath}/page`,
  // 查询详情 （报价）
  getInitDetail: data =>
    http({
      url: `${orderQuoPath}/getSouOrderInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交报价
  editOrder: data =>
    http({
      url: `${orderQuoPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询实时排名
  getRankingData: data =>
    http({
      url: `${orderQuoPath}/getRankingProject`,
      method: 'POST',
      data,
      loading: false
    }),
  // 查询详情
  getInitDetails: params =>
    http({
      url: `${orderQuoPath}/getInitDetails`,
      method: 'GET',
      params,
      loading: true
    }),
  // 查询报价明细
  getPageOrderResult: data =>
    http({
      url: `${orderQuoPath}/pageOrderResult`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询报价明细
  getOrderItemHis: data =>
    http({
      url: `${orderQuoPath}/pageOrderItemHis`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询历史排名
  getHisRanking: data =>
    http({
      url: `${vendorPath}/AuctSouOrderItemHis/query`,
      method: 'POST',
      data,
      loading: true
    }),
  listVendorOrderItemHis: data =>
    http({
      url: `${orderQuoPath}/listVendorOrderItemHis`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查看中标结果
  listOrderItemSelectStatus: `${orderQuoPath}/listOrderItemSelectStatus`
}

/**
 * signUp 报名 Controlller
 */
const signUpPath = `${vendorPath}/AuctSouProjectForVendor`
const signUp = {
  // 查询报名信息
  getSignUpInfo: data =>
    http({
      url: `${signUpPath}/getVendorSignUpDetail`,
      method: 'POST',
      data,
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
