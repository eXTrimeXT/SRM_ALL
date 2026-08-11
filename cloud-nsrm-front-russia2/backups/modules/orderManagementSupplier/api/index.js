import http from '@/utils/http'
const getUrl = path => `${path}`

// 订单查询
export const orderList = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/listPage'),
    method: 'POST',
    data
  })

// 订单明细查询
export const orderDetailList = data =>
  http({
    url: getUrl('/api-sup-ce/order/orderDetail/listPage'),
    method: 'POST',
    data
  })

// 订单明细查询
export const listUnDeliveryPage = data =>
  http({
    url: getUrl('/api-sup-ce/order/orderDetail/listUnDeliveryPage'),
    method: 'POST',
    data
  })

// 采购商新增订单
export const buyerAddOrder = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/save'),
    method: 'POST',
    data
  })

// 采购商更改订单
export const buyerUpdateOrder = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/update'),
    method: 'POST',
    data
  })

// 采购商批量提交订单
export const buyerSubmitBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/submitBatch'),
    method: 'POST',
    data
  })

// 供应商批量确认订单
export const vendorSubmitBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/comfirmBatch'),
    method: 'POST',
    data
  })

// 供应商批量拒绝订单
export const vendorRefuseBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/refuseBatch'),
    method: 'POST',
    data
  })

// 采购商批量删除订单明细
export const buyerDeleteBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/orderDetail/deleteBatch'),
    method: 'POST',
    data
  })

// 供应商编辑送货单
export const deliveryNoteUpdate = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNote/update'),
    method: 'POST',
    data
  })

// 供应商新增送货单
export const deliveryNoteSave = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNote/save'),
    method: 'POST',
    data
  })

// 分页查询送货单
export const deliveryNoteList = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNote/listInDeliveryAppoint'),
    method: 'POST',
    data
  })

// 分页查询送货单明细
export const deliveryNoteDetailListPage = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNoteDetail/listPage'),
    method: 'POST',
    data
  })

// 供应商批量提交送货单
export const vendorDeliveryNoteSubmitBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNote/submitBatch'),
    method: 'POST',
    data
  })

// 供应商批量提交送货单
export const deliveryAppointList = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/listPage'),
    method: 'POST',
    data
  })

// 供应商新增送货预约单
export const deliveryAppointSave = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/save'),
    method: 'POST',
    data
  })

// 供应商编辑送货预约单
export const deliveryAppointUpdate = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/update'),
    method: 'POST',
    data
  })

// 供应商提交送货预约单
export const deliveryAppointSubmit = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/submitBatch'),
    method: 'POST',
    data
  })

// 通过送货预约ID获取预约单及明细
export const getDeliveryAppointById = params =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/getDeliveryAppointById'),
    method: 'GET',
    params
  })

// 根据供应商公司ID查询合作组织
export const getInfoByParam = params =>
  http({
    url: getUrl('/api-sup/info/companyInfo/getInfoByParam'),
    method: 'POST',
    params
  })

// 分页查询收货单明细列表
export const receiveListPage = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryNoteDetail/receiveListPage'),
    method: 'POST',
    data
  })

// 供应商批量确认退货单
export const returnOrderComfirmBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/returnOrder/comfirmBatch'),
    method: 'POST',
    data
  })

// 供应商确认订单（630版本）
export const comfirm = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/comfirm'),
    method: 'POST',
    data
  })

// 供应商拒绝订单（630版本）
export const refuse = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/refuse'),
    method: 'POST',
    data
  })
