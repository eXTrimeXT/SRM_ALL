import http from '@/utils/axios/http'
// 供应商 - 报价单
const quotation = {
  pageList: '/api-sou/api/v1/vendor/ext_pur_inq/order/page',
  getOrderDetail: (projectId, vendorId) =>
    http({
      url: `/api-sou/api/v1/vendor/ext_pur_inq/order/getOrderDetail?projectId=${projectId}&vendorId=${vendorId}`,
      method: 'GET',
      loading: true
    }),
  editOrder: data =>
    http({
      url: '/api-sou/api/v1/vendor/ext_pur_inq/order/editOrder',
      method: 'POST',
      loading: true,
      data
    }),
  // 撤回报价
  rollback: data =>
    http({
      url: '/api-sou/api/v1/vendor/ext_pur_inq/order/rollback',
      method: 'PUT',
      data,
      loading: true
    })
}
/* 定价 */
const prefix = '/api-sou/npm/pur_fix_price/buyer'
const price = {
  /* 列表 */
  pageList: `${prefix}/page`,
  /* 删除定价单 */
  delete: id =>
    http({
      url: `${prefix}/remove/${id}`,
      method: 'POST',
      loading: true
    }),
  /* 查询项目策划 */
  queryProjectInfo: data =>
    http({
      url: `${prefix}/queryPurInq`,
      method: 'POST',
      data,
      loading: true
    }),
  /* 查询中标明细 */
  queryBidDetail: data =>
    http({
      url: `${prefix}/queryPurInq/orderItemList`,
      data,
      method: 'POST',
      loading: true
    }),
  /* 暂存、提交定价单 */
  saveOrSubmit: data =>
    http({
      url: `${prefix}/edit`,
      method: 'POST',
      data,
      loading: true
    }),
  /* 查看详情 */
  getDetail: id =>
    http({
      url: `${prefix}/getFixPrice/${id}`,
      method: 'GET',
      loading: true
    })
}
export default {
  quotation,
  price
}
