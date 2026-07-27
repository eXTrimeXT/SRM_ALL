/* 订单协同 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

/* 采购订单 */
export const purchaseOrderApi = {
  // 查询订单详情
  queryOrderById: async orderId =>
    http({
      url: getUrl('/api-sup-ce/order/orderDetail/queryOrderById'),
      method: 'GET',
      params: { orderId },
      loading: true
    })
}

/* 车辆信息维护 */
export const carInfoMaintenancesApi = {
  // 车辆信息批量失效
  carInfoInvalidBatch: async data =>
    http({
      url: getUrl('/api-sup-ce/order/carInfo/invalidBatch'),
      method: 'POST',
      data
    }),
  // 车辆信息批量提交
  carInfoSubmitBatch: async data =>
    http({
      url: getUrl('/api-sup-ce/order/carInfo/submitBatch'),
      method: 'POST',
      data
    }),
  // 新增车辆信息
  carInfoSave: async data =>
    http({
      url: getUrl('/api-sup-ce/order/carInfo/save'),
      method: 'POST',
      data
    }),
  // 修改车辆信息
  carInfoUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/order/carInfo/update'),
      method: 'POST',
      data
    })
}

/* 送货预约 */
export const deliveryAppointmentsApi = {
  // 通过送货预约ID获取预约单及明细
  getDeliveryAppointById: async params =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryAppoint/getDeliveryAppointById'),
      method: 'GET',
      params
    }),
  // 供应商提交送货预约单
  deliveryAppointSubmit: async data =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryAppoint/submitBatch'),
      method: 'POST',
      data
    }),
  // 分页查询送货单
  deliveryNoteList: async data =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryNote/listInDeliveryAppoint'),
      method: 'POST',
      data
    })
}

/* 退货单 */
export const returnedGoodsNoticesApi = {
  // 分页查询退货单明细
  returnDetailListPage: async data =>
    http({
      url: getUrl('/api-sup-ce/order/returnDetail/listPage'),
      method: 'POST',
      data
    }),
  listPageByOrgIdAndKeyWord: async data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageByOrgIdAndKeyWord'),
      method: 'POST',
      data
    }),
  // 采购商添加退货单
  returnOrderSave: async data =>
    http({
      url: getUrl('/api-sup-ce/order/returnOrder/save'),
      method: 'POST',
      data
    }),
  // 采购商修改退货单
  returnOrderUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/order/returnOrder/update'),
      method: 'POST',
      data
    }),
  // 分页查询收货单明细列表
  receiveListPage: async data =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryNoteDetail/receiveListPage'),
      method: 'POST',
      data
    })
}

/* 到货计划 */
export const deliverPlanApi = {
  // 发布
  publish: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/publish'),
      method: 'POST',
      data,
      loading: true
    }),
  // 发布订单
  publishBatch: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/publishBatch'),
      method: 'POST',
      data,
      loading: true
    }),
  // 订单查询
  orderList: async data =>
    http({
      url: getUrl('/api-sup-ce/order/order/listPage'),
      method: 'POST',
      data
    })
}

/* 送货单 */
export const deliveryOrderApi = {
  updateInnerBoxCodeList: async data =>
    http({
      url: '/api-base/base/innerboxcode/updateInnerBoxCodeList',
      method: 'POST',
      data,
      loading: true
    }),
  generateAutoCode: async data =>
    http({
      url: '/api-base/base/outerboxcode/generateAutoCode',
      method: 'POST',
      data,
      loading: true
    }),
  boxRelationDelete: async data =>
    http({
      url: '/api-base/base/innerouterrelation/delete',
      method: 'POST',
      data,
      loading: true
    }),
  getByCategoryIdAndBusinessBoxType: async data =>
    http({
      url: '/api-base/base/boxtagconfig/listByCategoryIdAndBusinessBoxType',
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商新增送货单
  deliveryNoteSave: async data =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryNote/save'),
      method: 'POST',
      loading: true,
      data
    }),
  // 获取品类信息
  getByCategoryId: async categoryId =>
    http({
      url: '/api-base/base/boxtagconfig/listByCategoryId',
      method: 'POST',
      params: { categoryId },
      loading: true
    }),
  // 内箱解绑
  innerDelete: async data =>
    http({
      url: '/api-base/base/innerouterrelation/delete',
      method: 'POST',
      data,
      loading: true
    }),
  // 生成外箱条码
  generateOuterBoxCode: async data =>
    http({
      url: '/api-base/base/outerboxcode/generateOuterBoxCode',
      method: 'POST',
      data,
      loading: true
    }),
  // 生成内箱条码
  generateInnerBoxCode: async data =>
    http({
      url: '/api-base/base/innerboxcode/generateInnerBoxCode',
      method: 'POST',
      data,
      loading: true
    }),
  // 删除外箱条码
  outerboxcodedelete: async id =>
    http({
      url: '/api-base/base/outerboxcode/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),
  // 解绑外箱条码
  unbindByDelivery: async id =>
    http({
      url: '/api-base/base/outerboxcode/unbindByDelivery',
      method: 'GET',
      params: { id },
      loading: true
    }),
  // 供应商批量提交送货单
  vendorDeliveryNoteSubmitBatch: async data =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryNote/submitBatch'),
      method: 'POST',
      data
    }),
  // 订单明细查询
  listUnDeliveryPage: async data =>
    http({
      url: getUrl('/api-sup-ce/order/orderDetail/listUnDeliveryPage'),
      method: 'POST',
      data
    })
}

/* 入库退货明细 */
export const warehousReturnGoodsApi = {
  // 订单查询
  orderList: async data =>
    http({
      url: getUrl('/api-sup-ce/order/order/listPage'),
      method: 'POST',
      data
    })
}

export const orderStorageApi = {
  // 订单入库列表（供应商）
  listPageVendor: async data =>
    http({
      url: getUrl('/api-sup-ce/order/warehouseReceipt/warehouseReceiptPage'),
      method: 'POST',
      data,
      loading: true
    })
}
