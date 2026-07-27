/**
 * @description 供应商 - 询价单 /api-sou/vendor/inq
 * @description 路径：$api.sou.vendor.inq
 * @author donghf3
 */
import http from '@/utils/axios/http'

// 供应商 - 询比价
const vendorPath = '/api-sou/vendor/inq'

/**
 * order 报价单 Controlller
 */
const orderPath = `${vendorPath}/order`
const order = {
  // 列表查询
  listPageUrl: `${orderPath}/listInqOrderInfo`,
  // 保存报价
  editOrder: data =>
    http({
      url: `${orderPath}/editOrder`,
      method: 'POST',
      data,
      loading: true
    }),
  // 撤回报价
  rollback: data =>
    http({
      url: `${orderPath}/rollback`,
      method: 'PUT',
      data,
      loading: true
    }),
  // 查看询价单详情
  getInqOrderInfo: params =>
    http({
      url: `${orderPath}/getInqOrderInfo`,
      method: 'GET',
      params,
      loading: true
    }),
  // 批量查询阶梯价报价值
  batchGetOrderLadderPrices: data =>
    http({
      url: `${orderPath}/batchGetOrderLadderPrices`,
      method: 'POST',
      data,
      loading: true
    }),
  // 批量查询阶梯价报价值
  batchGetOrderItemPayments: data =>
    http({
      url: `${orderPath}/batchGetOrderItemPayments`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查看中标结果
  inqOrderHistoryUrl: `${orderPath}/getInqOrderHistory`,
  // 查看公式报价
  getOrderFormulaPricesUrl: `${orderPath}/getOrderFormulaPrices`,
  // 计算公式报价
  getComputeFormulaPriceUrl: `${orderPath}/computeFormulaPrice`
}

export {
  order
}
