import http from '@/utils/axios/http'

// 采购商 - 竞价管理(最新MQL改造版)
const buyerPath = '/api-sou/api-ql'

/**
 * process 流程配置 Controlller
 */
const processPath = `${buyerPath}/process`
const process = {
  // 流程撤回
  undo: data =>
    http({
      url: '/api-pj/external/bpm/rollBackAll',
      method: 'POST',
      data,
      loading: true
    }),
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
const initPath = `${buyerPath}/AuctSouProjectForBuyer`
const init = {
  // 列表查询
  listPageUrl: '/api-pj/buyer/comp/init/page',
  // 废弃
  cancel: data =>
    http({
      url: '/api-pj/buyer/comp/init/cancel',
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  remove: data =>
    http({
      url: `${initPath}/removeSou`,
      method: 'POST',
      data,
      loading: true
    }),
  // 复制
  copy: data =>
    http({
      url: `${initPath}/copySou`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询竞价单详情 - 目前使用这个
  getInitInfo: id =>
    http({
      url: `/api-pj/buyer/comp/init/getProjectInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询竞价单详情
  getProjectInfo: data =>
    http({
      url: `${initPath}/getProjectInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 暂存/提交 项目信息
  editProjectInfo: data =>
    http({
      url: '/api-pj/buyer/comp/init/editProjectInfo',
      method: 'POST',
      data,
      loading: true
    }),
  // 查询项目需求
  listRequireInfo: id =>
    http({
      url: `/api-pj/buyer/comp/init/getRequireInfo/${id}`,
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
  // mql竞价流程配置流程节点查询
  listProcessNodes: data =>
    http({
      url: `${initPath}/listProcessNodes`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询邀请供应商信息
  getInviteSupplier: id =>
    http({
      url: `/api-pj/buyer/comp/init/getInviteSupplier/${id}`,
      method: 'GET',
      loading: true
    }),
  // 暂存/提交 邀请供应商信息
  editInviteSupplier: data =>
    http({
      url: '/api-pj/buyer/comp/init/editInviteSupplier',
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * signUp 报名 Controlller
 */
const signUpPath = `${buyerPath}/AuctSouProjectForBuyer`
const signUp = {
  // 列表查询供应商报名信息
  listPageUrl: '/api-pj/buyer/comp/sign-up/page',
  // 立即结束报名
  changeSignUpEndTime: data =>
    http({
      url: `${signUpPath}/changeSignUpEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询供应商报名详情
  getSignUpInfo: data =>
    http({
      url: `${signUpPath}/getVendorSignUpDetail`,
      method: 'POST',
      data,
      loading: true
    }),
  // 采购商确认/驳回供应商报名
  confirmSignUp: data =>
    http({
      url: '/api-pj/buyer/comp/sign-up/confirmSignUp',
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * order 报价 Controlller
 */
const orderPath = `${buyerPath}/AuctSouOrderForBuyer`
const order = {
  // 改变竞价截止时间
  changeOrderEndTime: data =>
    http({
      url: `${initPath}/changeOrderEndTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 改变竞价开始时间
  changeOrderStartTime: data =>
    http({
      url: `${initPath}/changeOrderStartTime`,
      method: 'POST',
      data,
      loading: true
    }),
  // 竞价大厅报价查询
  getCompHallProject: data =>
    http({
      url: `${orderPath}/getSouOrderInfo`,
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
  getPriceCompareInfos: data =>
    http({
      url: `${initPath}/generatePriceReport`,
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * control 报价控制 Controlller
 */
const controlPath = `${buyerPath}/AuctSouProjectForBuyer`
const control = {
  // 查询商务详情列表 1
  getOrders: data =>
    http({
      url: `${controlPath}/getControlInfo`,
      method: 'POST',
      data,
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
  // 查询商务详情-报价单详情 1
  getOrderInfo: data =>
    http({
      url: `${controlPath}/getVendorOrderInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  getControlInfo: data =>
    http({
      url: `${controlPath}/getControlInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  // 评选-发起新一轮
  startNewRound: data =>
    http({
      url: `${controlPath}/startNewRound`,
      method: 'POST',
      data,
      loading: true
    }),
  //评选 - 生成价格审批单
  createPricingApproval: data =>
    http({
      url: `${controlPath}/createPricingApproval`,
      method: 'POST',
      data,
      loading: true
    }),
}

/**
 * select 评选 Controlller
 */
const selectPath = `${buyerPath}/AuctSouProjectForBuyer`
const select = {
  /* 获取历史报价 */
  getHistoryPrice: data =>
    http({
      url: '/api-pj/buyer/comp/select/page',
      method: 'POST',
      data,
      loading: true
    }),
  /* 查看报价结果 */
  getQuoteResult: data =>
    http({
      url: '/api-pj/buyer/comp/select/listOrderResult',
      method: 'POST',
      data,
      loading: true
    }),
  /* 查看定价审批 */
  getPriceApproval: data =>
    http({
      url: '/api-pj/buyer/comp/select/listOrderResult',
      method: 'POST',
      data,
      loading: true
    }),
  /* 定价审批提交 */
  priceApprovalSubmit: data =>
    http({
      url: '/api-pj/buyer/comp/select/changeWinStatus',
      method: 'POST',
      data,
      loading: true
    }),
  // 列表查询
  listPageUrl: `${selectPath}/listEvaluations`,
  changeSelectStatus: data =>
    http({
      url: `${selectPath}/changeSelectStatus `,
      method: 'POST',
      data,
      loading: true
    }),
  // 生成定点通知单 - 结果公式
  openResult: data =>
    http({
      url: `${selectPath}/openResult`,
      method: 'POST',
      data,
      loading: true
    }),
  evaluationSave: data =>
    http({
      url: '',
      method: 'POST',
      data,
      loading: true
    }),
  evaluationSubmit: data =>
    http({
      url: '',
      method: 'POST',
      data,
      loading: true
    })
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

/* 定标 */
const calibrate = {
  /* 中标通知查询 */
  bidNotice: data =>
    http({
      url: '/api-pj/buyer/comp/select/listWinNotice',
      method: 'POST',
      data,
      loading: true
    }),
  /* 中标通知提交 */
  bidNoticeSubmit: data =>
    http({
      url: '/api-pj/buyer/comp/select/selectWinNotice',
      method: 'POST',
      data,
      loading: true
    }),
  calibrateResultSubmit: data =>
    http({
      url: '/api-pj/buyer/comp/select/changeSelectStatus',
      method: 'POST',
      data,
      loading: true
    }),
  /* 归档查询 */
  getArchiveList: data =>
    http({
      url: '/api-pj/buyer/comp/select/getPlaceOnFileList',
      method: 'POST',
      data,
      loading: true
    }),
  archiveSubmit: data =>
    http({
      url: '/api-pj/buyer/comp/select/placeOnFile',
      method: 'POST',
      data,
      loading: true
    }),
  submitEngine: data =>
    http({
      url: '/api-base/flow/event/submitEngine',
      method: 'POST',
      data,
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
  order,
  calibrate
}
