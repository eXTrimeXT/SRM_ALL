/**
 * @description 采购商 - 项目式询价
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 采购商 - 询价管理
const buyerPath = '/api-sou/buyer/bid'

/**
 * process 流程 Controlller
 */
const processPath = `${buyerPath}/process`
const process = {
  // 列表查询
  listPageUrl: `${processPath}/page`,
  // 列表查询
  page: data =>
    http({
      url: `${processPath}/page`,
      method: 'POST',
      data,
      loading: true
    }),
  // 编辑保存
  editProcessConfig: data =>
    http({
      url: `${processPath}/editProcessConfig`,
      method: 'POST',
      data,
      loading: true
    }),
  // 生效
  valid: id =>
    http({
      url: `${processPath}/valid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 失效
  invalid: id =>
    http({
      url: `${processPath}/invalid/${id}`,
      method: 'POST',
      loading: true
    }),
  // 删除
  remove: id =>
    http({
      url: `${processPath}/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 查询询价单关联的流程节点信息
  projectNodes: id =>
    http({
      url: `${processPath}/nodes/${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * init 项目立项 Controlller
 */
const initPath = `${buyerPath}/init`
const init = {
  // 列表查询
  listPageUrl: `${initPath}/page`,
  // 废弃
  cancel: data =>
    http({
      url: `${initPath}/cancel`,
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  remove: id =>
    http({
      url: `${initPath}/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 保存项目信息
  editProjectInfo: data =>
    http({
      url: `${initPath}/editProjectInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询项目信息
  getProjectInfo: id =>
    http({
      url: `${initPath}/getProjectInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交项目需求
  editRequireInfo: data =>
    http({
      url: `${initPath}/editRequireInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询项目需求
  getRequireInfo: id =>
    http({
      url: `${initPath}/getRequireInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交邀请供应商信息
  editInviteSupplier: data =>
    http({
      url: `${initPath}/editInviteSupplier`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询邀请供应商信息
  getInviteSupplier: id =>
    http({
      url: `${initPath}/getInviteSupplier/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交评分规则
  editScoreRule: data =>
    http({
      url: `${initPath}/editScoreRule`,
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * sign-up 报名 Controlller
 */
const signUpPath = `${buyerPath}/sign-up`
const signUp = {
  // 列表查询
  page: `${signUpPath}/page`,
  //  查询供应商报名详情
  getSignUpInfo: params =>
    http({
      url: `${signUpPath}/getSignUpInfo`,
      method: 'get',
      params,
      loading: true
    }),
  // 立即截止报名/延长报名时间
  changeSignUpEndTime: data =>
    http({
      url: `${signUpPath}/changeSignUpEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 确认/驳回报名
  confirmSignUp: data =>
    http({
      url: `${signUpPath}/confirmSignUp`,
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * order 报价单 Controlller
 */
const orderPath = `${buyerPath}/order`
const order = {
  // 查询询价单详情
  getOrderInfo: params =>
    http({
      url: `${orderPath}/getOrderInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  // 查询询价单详情
  cancelOrder: data =>
    http({
      url: `${orderPath}/cancelOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 代理报价
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
  computeFormulaPriceUrl: `${orderPath}/computeFormulaPrice`
}

/**
 * control 报价控制 Controlller
 */
const controlPath = `${buyerPath}/control`
const control = {
  // 报价信息查询
  orders: id =>
    http({
      url: `${controlPath}/orders/${id}`,
      method: 'GET',
      loading: true
    }),
  // 立即开始报价("接收报价中")、延迟报价开始时间("报价未开始")
  changeOrderStartTime: data =>
    http({
      url: `${controlPath}/changeOrderStartTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 立即截止报价("报价已截止")、延长报价时间("接收报价中")
  changeOrderEndTime: data =>
    http({
      url: `${controlPath}/changeOrderEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 发起新一轮
  startNewRound: data =>
    http({
      url: `${controlPath}/startNewRound`,
      method: 'POST',
      data,
      loading: true
    }),
  // 商务开标
  businessOpen: id =>
    http({
      url: `${controlPath}/businessOpen/${id}`,
      method: 'POST',
      loading: true
    }),
  // 报价解密
  decryptPrice: id =>
    http({
      url: `${controlPath}/decryptPrice/${id}`,
      method: 'POST',
      loading: true
    }),
  // 查询报价单想起
  getOrderInfo: id =>
    http({
      url: `${controlPath}/getOrderInfo/${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * select 评选 Controlller
 */
const selectPath = `${buyerPath}/select`
const select = {
  // 列表查询
  listPageUrl: `${selectPath}/page`,
  // 报价信息查询
  orders: id =>
    http({
      url: `${selectPath}/orders/${id}`,
      method: 'GET',
      loading: true
    }),
  // 智能评选
  intelligent: data =>
    http({
      url: `${selectPath}/intelligent`,
      method: 'POST',
      data,
      loading: true
    }),
  // 入围 / 淘汰
  changeWinStatus: data =>
    http({
      url: `${selectPath}/changeWinStatus`,
      method: 'POST',
      data,
      loading: true
    }),
  // 中标 / 落标
  changeSelectStatus: data =>
    http({
      url: `${selectPath}/changeSelectStatus`,
      method: 'POST',
      data,
      loading: true
    }),
  // 修改中标数量
  changeWinAmount: data =>
    http({
      url: `${selectPath}/changeWinAmount`,
      method: 'POST',
      data,
      loading: true
    }),
  // 公开本轮结果
  openResult: id =>
    http({
      url: `${selectPath}/openResult/${id}`,
      method: 'POST',
      loading: true
    }),
  // 评选 - 生成价格审批单
  getCreatePricingApprovalUrl: id => `${selectPath}/createPricingApproval/${id}`,
  // 评选 - 生成价格审批单（新）
  createPricingApprovalNew: id => `${selectPath}/createPricingApprovalNew/${id}`,
  // 查看物料历史比价信息
  getPriceCompareInfos: params =>
    http({
      url: `${selectPath}/getPriceCompareInfos`,
      method: 'GET',
      params,
      loading: true
    }),
  // 查看普通报价比价信息
  getSimplePriceCompareUrl: `${selectPath}/getSimplePriceCompare`,
  // 查看公式报价比价信息
  getFormulaPriceCompareUrl: `${selectPath}/getFormulaPriceCompare`
}

/**
 * tech 技术标 Controller
 */
const techPath = `${buyerPath}/tech`
const tech = {
  // 查询技术报价进度
  techProgress: data =>
    http({
      url: `${techPath}/techProgress`,
      method: 'POST',
      data,
      loading: true
    }),
  // 技术开标
  openTech: id =>
    http({
      url: `${techPath}/openTech/${id}`,
      method: 'POST',
      loading: true
    }),
  // 工作小组成员: 查询需要技术评分的寻源单信息
  techProgressReviewUrl: `${techPath}/techProgressReview`,
  // 工作小组成员: 查询询价单技术评分详情
  techProgressReviewDetail: id =>
    http({
      url: `${techPath}/techProgressReviewDetail/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询评委的技术评分详情
  techProgressInfoDetails: data =>
    http({
      url: `${techPath}/techProgressInfoDetails`,
      method: 'POST',
      data,
      loading: true
    }),
  // 工作小组成员/采购商端: 技术评分/代理评分
  techScore: data =>
    http({
      url: `${techPath}/techScore`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询供应商技术评分信息
  techProgressInfo: params =>
    http({
      url: `${techPath}/techProgressInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  // 查询技术标供应商详情
  vendorTechOrder: id =>
    http({
      url: `${techPath}/vendorTechOrder/${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * bond 保证金 Controller
 */
const bondPath = `${buyerPath}/bond`
const bond = {
  // 查看保证金缴纳情况
  getQueryBondsUrl: id => `${bondPath}/queryBonds/${id}`,
  // 确认缴纳情况
  confirmUrl: `${bondPath}/confirm`
}

const formula = {
  // 查询公式列表 入参：[{ materialId, categoryId, orgOuId }]
  getMaterialFormulaRelateInfos: async payload =>
    http({
      url: '/api-base/bid/pricing-formula/getMaterialFormulaRelateInfos',
      method: 'POST',
      data: payload,
      loading: true
    })
}

// 根据后端不同的 Controlller 区分不同的对象拆分，内容过多，多加一个层级
export default {
  process,
  init,
  signUp,
  order,
  control,
  select,
  tech,
  bond,
  formula
}
