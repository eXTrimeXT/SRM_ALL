import http from '@/utils/axios/http'

// 采购商 - 竞价管理
const buyerPath = '/api-sou/buyer/comp'

/**
 * process 流程配置 Controlller
 */
const processPath = `${buyerPath}/process`
const process = {
  // 查询流程配置信息
  getProcessConfig: id =>
    http({
      url: `${processPath}/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询单据节点信息
  getNodesByProject: id =>
    http({
      url: `${processPath}/nodes/${id}`,
      method: 'GET',
      loading: true
    })
}

/**
 * init 立项 Controlller
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
  // 查询竞价单详情
  getProjectInfo: id =>
    http({
      url: `${initPath}/getProjectInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交 项目信息
  editProjectInfo: data =>
    http({
      url: `${initPath}/editProjectInfo`,
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
  // 暂存/提交 项目需求
  editRequireInfo: data =>
    http({
      url: `${initPath}/editRequireInfo`,
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
  // 暂存/提交 邀请供应商信息
  editInviteSupplier: data =>
    http({
      url: `${initPath}/editInviteSupplier`,
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * signUp 报名 Controlller
 */
const signUpPath = `${buyerPath}/sign-up`
const signUp = {
  // 列表查询
  listPageUrl: `${signUpPath}/page`,
  // 立即结束报名
  changeSignUpEndTime: data =>
    http({
      url: `${signUpPath}/changeSignUpEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询供应商报名详情
  getSignUpInfo: params =>
    http({
      url: `${signUpPath}/getSignUpInfo`,
      method: 'GET',
      params,
      loading: true
    })
}

/**
 * order 报价 Controlller
 */
const orderPath = `${buyerPath}/order`
const order = {
  // 改变截止时间
  changeOrderEndTime: data =>
    http({
      url: `${orderPath}/changeOrderEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 改变开始时间
  changeOrderStartTime: data =>
    http({
      url: `${orderPath}/changeOrderStartTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 竞价大厅报价查询
  getCompHallProject: data =>
    http({
      url: `${orderPath}/getCompHallProject`,
      method: 'POST',
      data,
      loading: false
    }),
  // 查询报价单详情
  getOrderInfo: data =>
    http({
      url: `${orderPath}/orderInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询物料明细排行 / 总价排行
  getPageOrderRanking: data =>
    http({
      url: `${orderPath}/pageOrderRanking`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询物料明细报价记录
  getPageOrderItemInfo: data =>
    http({
      url: `${orderPath}/pageOrderItemInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询物料比价历史
  getPriceCompareInfos: params =>
    http({
      url: `${orderPath}/getPriceCompareInfos`,
      method: 'GET',
      params,
      loading: true
    })
}

/**
 * control 报价控制 Controlller
 */
const controlPath = `${buyerPath}/control`
const control = {
  // 查询列表
  getOrders: id =>
    http({
      url: `${controlPath}/orders/${id}`,
      method: 'GET',
      loading: true
    }),
  // 修改报价开始时间
  changeOrderStartTime: data =>
    http({
      url: `${controlPath}/changeOrderStartTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 修改报价截止时间
  changeOrderEndTime: data =>
    http({
      url: `${controlPath}/changeOrderEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询报价单详情
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
  // 智能决标
  intelligent: data =>
    http({
      url: `${selectPath}/intelligent`,
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
  // 生成价格审批单
  createPricingApproval: id =>
    http({
      url: `${selectPath}/createPricingApproval/${id}`,
      method: 'POST',
      loading: true
    }),
  // 生成价格审批单（新）
  createPricingApprovalNew: id =>
    http({
      url: `${selectPath}/createPricingApprovalNew/${id}`,
      method: 'POST',
      loading: true
    }),
}

/**
 * hall 竞价大厅 Controlller
 */
const hallPath = `${buyerPath}/hall`
const hall = {
  // 查询竞价大厅数据
  getHallInfo: id =>
    http({
      url: `${hallPath}/getHallInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询竞价大厅数据
  getItemDetails: id =>
    http({
      url: `${hallPath}/getItemDetails/${id}`,
      method: 'GET',
      loading: true
    })
}

export default {
  process,
  init,
  signUp,
  control,
  select,
  hall,
  order
}
