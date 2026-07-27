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
  // 获取询价单详情信息
  getInqInfo: id =>
    http({
      url: `${initPath}/getInqInfo/${id}`,
      method: 'GET',
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
  // 获取询价管理详情
  getManagementDetail: id =>
    http({
      url: `${selectPath}/management/${id}`,
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
      url: `${selectPath}/startNewRound`,
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
export default {
  init,
  order,
  select
}
