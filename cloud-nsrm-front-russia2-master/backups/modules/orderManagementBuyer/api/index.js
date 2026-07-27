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

// 批量导入订单
export const exportOrderBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/order/saveByExcel'),
    method: 'POST',
    data
  })

// 根据公司ID和合作组织ID获取财务信息
export const getByCompanyIdAndOrgId = params =>
  http({
    url: getUrl('/api-sup/info/financeInfo/getByCompanyIdAndOrgId'),
    method: 'GET',
    params
  })

// 分页查询送货单明细
export const deliveryNoteDetailListPage = data =>
  http({
    url: getUrl('/api-sup-ce/po/deliveryNoteDetail/listPage'),
    method: 'POST',
    data
  })

// 采购商批量审核
export const buyerBatchReview = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/comfirmBatch'),
    method: 'POST',
    data
  })

// 采购商批量驳回
export const buyerBatchTurnDown = data =>
  http({
    url: getUrl('/api-sup-ce/order/deliveryAppoint/refuseBatch'),
    method: 'POST',
    data
  })

// 采购商批量驳回
export const listPageByOrgIdAndKeyWord = data =>
  http({
    url: getUrl('/api-sup/info/companyInfo/listPageByOrgIdAndKeyWord'),
    method: 'POST',
    data
  })

// 分页查询退货单明细
export const returnDetailListPage = data =>
  http({
    url: getUrl('/api-sup-ce/order/returnDetail/listPage'),
    method: 'POST',
    data
  })

// 采购商批量提交退货单 只能提交拟态退货单
export const returnOrderBatchSubmit = data =>
  http({
    url: getUrl('/api-sup-ce/order/returnOrder/submitBatch'),
    method: 'POST',
    data
  })

// 采购商添加退货单
export const returnOrderSave = data =>
  http({
    url: getUrl('/api-sup-ce/order/returnOrder/save'),
    method: 'POST',
    data
  })

// 采购商修改退货单
export const returnOrderUpdate = data =>
  http({
    url: getUrl('/api-sup-ce/order/returnOrder/update'),
    method: 'POST',
    data
  })

// 分页查询车辆信息
export const carInfoListPage = data =>
  http({
    url: getUrl('/api-sup-ce/order/carInfo/listPage'),
    method: 'POST',
    data
  })

// 新增车辆信息
export const carInfoSave = data =>
  http({
    url: getUrl('/api-sup-ce/order/carInfo/save'),
    method: 'POST',
    data
  })

// 修改车辆信息
export const carInfoUpdate = data =>
  http({
    url: getUrl('/api-sup-ce/order/carInfo/update'),
    method: 'POST',
    data
  })

// 车辆信息批量提交
export const carInfoSubmitBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/carInfo/submitBatch'),
    method: 'POST',
    data
  })

// 车辆信息批量失效
export const carInfoInvalidBatch = data =>
  http({
    url: getUrl('/api-sup-ce/order/carInfo/invalidBatch'),
    method: 'POST',
    data
  })
