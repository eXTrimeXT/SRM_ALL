import http from '@/utils/axios/http'
// 采购商 - 集采询比价
const buyerPath = '/api-sou/api/v1/buyer/ext_pur_inq'
const list = {
  pageList: `${buyerPath}/init/pageProjects`,
  /* 删除 */
  delete: id =>
    http({
      url: `${buyerPath}/init/remove/${id}`,
      method: 'DELETE',
      loading: true
    }),
  /* 废弃 */
  abandon: data =>
    http({
      url: `${buyerPath}init/cancel`,
      method: 'POST',
      data,
      loading: true
    })
}
const init = {
  /* 获取立项详情 */
  getDetail: id =>
    http({
      url: `${buyerPath}/init/getDetails/${id}`,
      method: 'GET',
      loading: true
    }),
  /* 暂存/提交 */
  saveOrSubmit: data =>
    http({
      url: `${buyerPath}/init/editInitInfo`,
      method: 'POST',
      data,
      loading: true
    }),
  /* 删除供应商 */
  deleteVendor: data =>
    http({
      url: '/api-sou/npm/buyer/ext_pur_inq/init/removeVendor',
      method: 'POST',
      data,
      loading: true
    }),
  /* 查看删除供应商 */
  readDeleteSupplier: data =>
    http({
      url: '/api-sou/npm/buyer/ext_pur_inq/init/queryVendorDel',
      method: 'POST',
      data,
      loading: true
    })
}
/**
 * select 评选 Controlller
 */
const selectPath = `${buyerPath}/select`
const select = {
  /* 获取改变中标供应商数据 */
  getChangeSuppilers: params =>
    http({
      url: '/api-sou/npm/buyer/ext_pur_inq/select/queryOrderItemVendors',
      method: 'GET',
      params,
      loading: true
    }),
  /* 关闭物料需求 */
  closeRows: data =>
    http({
      url: '/api-sou/npm/buyer/ext_pur_inq/select/closeSouItems',
      method: 'POST',
      data,
      loading: true
    }),
  /* 结束询价 */
  stopInquiry: projectId =>
    http({
      url: `/api-sou/npm/buyer/ext_pur_inq/select/finishSou/${projectId}`,
      method: 'POST',
      loading: true
    }),
  /* 修改中标供应商 */
  changeBidSupplier: data =>
    http({
      url: '/api-sou/api/v1/buyer/ext_pur_inq/select/changeWinStatus',
      method: 'POST',
      data,
      loading: true
    }),
  /* 总价比价 */
  compareTotalAmount: projectId =>
    http({
      url: `/api-sou/npm/buyer/ext_pur_inq/select/totalPriceCompare/${projectId}`,
      method: 'POST',
      loading: true
    }),
  /* 获取评选数据 */
  getSelectListByProjectId: data =>
    http({
      url: '/api-sou/npm/buyer/ext_pur_inq/select/queryItemSelectInfo',
      method: 'POST',
      data,
      loading: true
    }),
  // 获取询价管理详情
  getManagementDetail: id =>
    http({
      url: `/api-sou/npm/buyer/ext_pur_inq/select/getInqSelectManagementInfo/${id}`,
      method: 'GET',
      loading: true
    }),
  // 修改报价截止时间
  changeDeadline: data =>
    http({
      url: '/api-sou/api/v1/buyer/ext_pur_inq/control/changeOrderEndTime',
      method: 'POST',
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
      url: '/api-sou/api/v1/buyer/ext_pur_inq/control/startNewRound',
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
export default {
  init,
  list,
  select
}
