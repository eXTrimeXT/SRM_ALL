/**
 * @description 采购商 - 询价管理 /api-sou/buyer/inq
 * @description 路径：$api.sou.buyer.inq
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 采购商 - 询价管理
const buyerPath = '/api-sou/buyer/inq'

/**
 * init 立项 Controlller
 */
const initPath = `${buyerPath}/init`
const init = {
  /* 获取明细列表 */
  detailPage: '/api-sou/npm/buyer/inq/init/querySouItems',
  /* 获取智能推荐供应商 */
  getRecommendVendor: id =>
    http({
      url: `/api-sou/npm/buyer/inq/init/getVendorAiRecommend/${id}`,
      method: 'GET',
      loading: true
    }),
  /* 删除供应商 */
  deleteSupplier: data =>
    http({
      url: '/api-sou/npm/buyer/inq/init/removeVendor',
      method: 'POST',
      data,
      loading: true
    }),
  /* 查看删除供应商 */
  readDeleteSupplier: data =>
    http({
      url: '/api-sou/npm/buyer/inq/init/queryVendorDel',
      method: 'POST',
      data,
      loading: true
    }),
  // 审批流
  renderEngine: data =>
    http({
      url: '/api-base/flow/event/submitEngine',
      method: 'POST',
      data,
      loading: true
    }),
  // 列表查询
  listPageUrl: `${initPath}/page`,
  // 删除
  delete: id =>
    http({
      url: `${initPath}/delete/${id}`,
      method: 'DELETE',
      loading: true
    }),
  // 取消
  cancel: data =>
    http({
      url: `${initPath}/cancel`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 复制
  copy: id =>
    http({
      url: `${initPath}/copy/${id}`,
      method: 'POST',
      loading: true
    }),
  // 暂存
  editInq: data =>
    http({
      url: `${initPath}/editInq`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交
  submitInq: data =>
    http({
      url: '/api-pj/pj/buyer/inq/init/editInq',
      method: 'POST',
      data,
      loading: true
    }),
  // 获取询价单详情信息
  getInqInfo: id =>
    http({
      url: `${initPath}/getInqInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 取消明细
  cancelRows: data =>
    http({
      url: '/api-sou/npm/buyer/inq/select/draft/closeSouItems',
      method: 'POST',
      data,
      loading: true
    }),
  // 判断新增供应商和已邀请供应商是否有关联
  supplierRelation: data =>
    http({
      url: '/api-sou/npm/buyer/inq/init/checkVendor',
      method: 'POST',
      data,
      loading: true
    })
}

/**
 * order 询价单 Controlller
 */
const orderPath = `${buyerPath}/order`
const order = {
  // 获取询价单详情信息
  getInqOrderInfo: params =>
    http({
      url: `${orderPath}/getInqOrderInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  // 作废供应商报价
  cancelOrder: data =>
    http({
      url: `${orderPath}/cancelOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交代理报价
  submitOrder: data =>
    http({
      url: `${orderPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询阶梯价报价
  batchGetOrderLadderPrices: data =>
    http({
      url: `${orderPath}/batchGetOrderLadderPrices`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询付款账期
  getOrderItemPaymentsUrl: `${orderPath}/getOrderItemPayments`,
  // 查询公式报价
  getOrderFormulaPricesUrl: `${orderPath}/getOrderFormulaPrices`,
  // 计算公式报价
  getComputeFormulaPriceUrl: `${orderPath}/computeFormulaPrice`
}

/**
 * select 评选 Controlller
 */
const selectPath = `${buyerPath}/select`
const select = {
  /* 获取改变中标供应商数据 */
  getChangeSuppilers: params =>
    http({
      url: '/api-sou/npm/buyer/inq/select/queryOrderItemVendors',
      method: 'GET',
      params,
      loading: true
    }),
  /* 取消物料需求 */
  cancelRow: data =>
    http({
      url: '/api-sou/npm/buyer/inq/select/closeSouItems',
      method: 'POST',
      data,
      loading: true
    }),
  /* 关闭物料需求 */
  closeRow: data =>
    http({
      url: '/api-sou/npm/buyer/inq/select/closeSouItemsWithReason',
      method: 'POST',
      data,
      loading: true
    }),
  /* 结束询价 */
  stopInquiry: projectId =>
    http({
      url: `/api-sou/npm/buyer/inq/select/finishSou/${projectId}`,
      method: 'POST',
      loading: true
    }),
  /* 修改中标供应商 */
  changeBidSupplier: data =>
    http({
      url: '/api-sou/buyer/inq/select/changeSelectStatus',
      method: 'PUT',
      data,
      loading: true
    }),
  /* 总价比价 */
  compareTotalAmount: projectId =>
    http({
      url: `/api-sou/npm/buyer/inq/select/totalPriceCompare/${projectId}`,
      method: 'POST',
      loading: true
    }),
  /* 获取评选数据 */
  getSelectListByProjectId: data =>
    http({
      url: '/api-sou/npm/buyer/inq/select/queryItemSelectInfo',
      method: 'POST',
      data,
      loading: true
    }),
  // 获取询价管理详情
  getManagementDetail: id =>
    http({
      url: `/api-sou/npm/buyer/inq/select/getInqSelectManagementInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 修改报价截止时间
  changeDeadline: data =>
    http({
      url: `${selectPath}/changeDeadline`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 修改报价开始时间
  changeBeginQuote: data =>
    http({
      url: `${selectPath}/changeBeginQuote`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 查询目标价
  getTargetPrice: id =>
    http({
      url: `${selectPath}/getTargetPrice/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询供应商报价详情
  getVendorQuoteDetail: id =>
    http({
      url: `${selectPath}/getVendorQuoteDetail/${id}`,
      method: 'GET',
      loading: true
    }),
  // 保存设定目标价
  setTargetPrice: (id, data) =>
    http({
      url: `${selectPath}/setTargetPrice/${id}`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 查询评选的筛选条件下拉框信息
  getSearchInfo: id =>
    http({
      url: `${selectPath}/selecting/searchInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 查询评选列表
  getSelectingPage: data =>
    http({
      url: `${selectPath}/selecting`,
      method: 'POST',
      data,
      loading: true
    }),
  // 根据物料查询比价则先图数据
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
  getFormulaPriceCompareUrl: `${selectPath}/getFormulaPriceCompare`,
  // 智能评选
  autoSelecting: id =>
    http({
      url: `${selectPath}/autoSelecting/${id}`,
      method: 'PUT',
      loading: true
    }),
  // 评选 - 入围 / 淘汰
  changeSelectStatus: data =>
    http({
      url: `${selectPath}/changeSelectStatus`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 评选 - 中标 / 落标
  changeSelectResult: data =>
    http({
      url: `${selectPath}/changeSelectResult`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 评选 - 修改中标数量
  changeQuoteQuantity: data =>
    http({
      url: `${selectPath}/changeQuoteQuantity`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 评选 - 批量修改账期信息
  batchUpdatePayment: data =>
    http({
      url: `${selectPath}/batchUpdatePayment`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 评选 - 公开本轮结果
  openResult: id =>
    http({
      url: `${selectPath}/openResult/${id}`,
      method: 'PUT',
      loading: true
    }),
  // 评选 - 发起新一轮
  startNewRound: data =>
    http({
      url: '/api-sou/si/buyer/inq/select/startNewRound',
      method: 'POST',
      data,
      loading: true
    }),
  // 评选 - 生成价格审批单
  getCreatePricingApprovalUrl: id => `${selectPath}/createPricingApproval/${id}`,
  // 评选 - 生成价格审批单（新）
  createPricingApprovalNew: id => `${selectPath}/createPricingApprovalNew/${id}`,
  // 评选 - 导出比价信息
  getExportPriceCompareInfoUrl: id => `${selectPath}/exportPriceCompareInfos/pdf/${id}`
}
// 根据后端不同的 Controlller 区分不同的对象拆分
/* 定价单 */
const price = {
  /* 关闭 */
  close: data =>
    http({
      url: '/api-sou/npm/fix-price/buyer/closeFixPriceLine',
      method: 'POST',
      data,
      loading: true
    }),
  /* 流程撤回 */
  undo: data =>
    http({
      url: '/api-pj/external/bpm/rollBackAll',
      method: 'POST',
      data,
      loading: true
    }),
  /* 获取行报价明细 */
  getQuoteDetail: id =>
    http({
      url: `/api-sou/npm/fix-price/buyer/listSouInqOrderItemsForPriceLine?orderItemId=${id}`,
      method: 'GET',
      loading: true
    }),
  /* 查询列表 */
  listPageUrl: '/api-sou/npm/fix-price/buyer/pageFixPrices',
  /* 删除 */
  delete: id =>
    http({
      url: `/api-sou/npm/fix-price/buyer/deleteFixPrice/${id}`,
      method: 'POST',
      loading: true
    }),
  /* 编辑 */
  save: data =>
    http({
      url: '/api-sou/npm/fix-price/buyer/editFixPrice',
      method: 'POST',
      data,
      loading: true
    }),
  /* 获取详情 */
  getDetail: id =>
    http({
      url: `/api-sou/npm/fix-price/buyer/getFixPrice/${id}`,
      method: 'GET',
      loading: true
    }),
  /* 查询询价 */
  inquiry: data =>
    http({
      url: '/api-sou/npm/fix-price/buyer/listSouInqOrderItems',
      method: 'POST',
      data,
      loading: true
    }),
  /* 查询近期采购 */
  recentPurchase: data =>
    http({
      url: '/api-sou/npm/fix-price/buyer/listReqLines',
      method: 'POST',
      data,
      loading: true
    }),
  /* 提交审批 */
  submitEngine: data =>
    http({
      url: '/api-base/flow/event/submitEngine',
      method: 'POST',
      data,
      loading: true
    })
}
export default {
  init,
  order,
  select,
  price
}
