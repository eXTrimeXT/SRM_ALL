import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 委外管理
export const outsourcing = {
  // 供方库存管理
  vendorInv: {
    // 查询供方实时库存
    realTimelistPage: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/realTime/listPage'),
        method: 'POST',
        data
      }),
    // 查询供方库存盘点记录
    stockListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/stock/listPage'),
        method: 'POST',
        data
      }),
    // 查询供方库存变动详情
    stockDetail: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/stock/detail'),
        method: 'POST',
        data
      }),
    // 查询供方最新的库存盘点信息
    latestStockDetailListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/stock-detail/task/listPage'),
        method: 'POST',
        data
      }),
    // 查询供方历史库存盘点信息
    stockDetailHisListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/stock-detail/history/listPage'),
        method: 'POST',
        data
      }),
    // 库存同步
    syncInv: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/syncInv'),
        method: 'POST',
        data,
        loading: true
      }),
    // 库存同步重试
    retrySyncInv: async taskId =>
      http({
        url: getUrl(`/api-sup-ce/os/inv/syncInv/retry/${taskId}`),
        method: 'POST',
        data: {},
        loading: true
      }),
    // 发送供应商
    sendInvToVendor: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/buyer/sendInv'),
        method: 'POST',
        data,
        loading: true
      }),
    // 供方确认库存
    vendorConfirm: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/vendor/confirmInv'),
        method: 'POST',
        data,
        loading: true
      }),
    // 结束盘点
    endInv: async data =>
      http({
        url: getUrl(`/api-sup-ce/os/inv/endInv/${data.id}`),
        method: 'POST',
        loading: true
      }),
    // 驳回盘点
    rejectInv: async data =>
      http({
        url: getUrl('/api-sup-ce/os/inv/buyer/rejectInv'),
        method: 'POST',
        data,
        loading: true
      })
  }
}
// 订单管理
export const orderManagement = {
  // 订单查询
    orderList: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/listPage'),
        method: 'POST',
        data
      }),
    // 订单明细查询
    orderDetailList: async data =>
      http({
        url: getUrl('/api-sup-ce/order/orderDetail/listPage'),
        method: 'POST',
        data
      }),
    // 采购商新增订单
    buyerAddOrder: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/save'),
        method: 'POST',
        data
      }),
    // 采购商更改订单
    buyerUpdateOrder: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/update'),
        method: 'POST',
        data
      }),
    // 采购商批量提交订单
    buyerSubmitBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/submitBatch'),
        method: 'POST',
        data
      }),
    // 供应商批量确认订单
    vendorSubmitBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/comfirmBatch'),
        method: 'POST',
        data
      }),
    // 供应商批量拒绝订单
    vendorRefuseBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/refuseBatch'),
        method: 'POST',
        data
      }),
    // 采购商批量删除订单明细
    buyerDeleteBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/orderDetail/deleteBatch'),
        method: 'POST',
        data
      }),
    // 批量导入订单
    exportOrderBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/saveByExcel'),
        method: 'POST',
        data
      }),
    // 根据公司ID和合作组织ID获取财务信息
    getByCompanyIdAndOrgId: async params =>
      http({
        url: getUrl('/api-sup/info/financeInfo/getByCompanyIdAndOrgId'),
        method: 'GET',
        params
      }),
    // 分页查询送货单明细
    deliveryNoteDetailListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/po/deliveryNoteDetail/listPage'),
        method: 'POST',
        data
      }),
    // 采购商批量审核
    buyerBatchReview: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/comfirmBatch'),
        method: 'POST',
        data
      }),
    // 采购商批量驳回
    buyerBatchTurnDown: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/refuseBatch'),
        method: 'POST',
        data
      }),
    listPageByOrgIdAndKeyWord: async data =>
      http({
        url: getUrl('/api-sup/info/companyInfo/listPageByOrgIdAndKeyWord'),
        method: 'POST',
        data
      }),
    // 分页查询退货单明细
    returnDetailListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/order/returnDetail/listPage'),
        method: 'POST',
        data
      }),
    // 采购商批量提交退货单 只能提交拟态退货单
    returnOrderBatchSubmit: async data =>
      http({
        url: getUrl('/api-sup-ce/order/returnOrder/submitBatch'),
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
    // 分页查询车辆信息
    carInfoListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/order/carInfo/listPage'),
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
      }),
    // 车辆信息批量提交
    carInfoSubmitBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/carInfo/submitBatch'),
        method: 'POST',
        data
      }),
    // 车辆信息批量失效
    carInfoInvalidBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/carInfo/invalidBatch'),
        method: 'POST',
        data
      }),
    // 订单明细查询
    listUnDeliveryPage: async data =>
      http({
        url: getUrl('/api-sup-ce/order/orderDetail/listUnDeliveryPage'),
        method: 'POST',
        data
      }),
    // 供应商编辑送货单
    deliveryNoteUpdate: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryNote/update'),
        method: 'POST',
        data
      }),
    // 供应商新增送货单
    deliveryNoteSave: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryNote/save'),
        method: 'POST',
        data
      }),
    // 分页查询送货单
    deliveryNoteList: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryNote/listInDeliveryAppoint'),
        method: 'POST',
        data
      }),
    // 供应商批量提交送货单
    vendorDeliveryNoteSubmitBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryNote/submitBatch'),
        method: 'POST',
        data
      }),
    deliveryAppointList: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/listPage'),
        method: 'POST',
        data
      }),
    // 供应商新增送货预约单
    deliveryAppointSave: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/save'),
        method: 'POST',
        data
      }),
    // 供应商编辑送货预约单
    deliveryAppointUpdate: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/update'),
        method: 'POST',
        data
      }),
    // 供应商提交送货预约单
    deliveryAppointSubmit: async data =>
      http({
         url: getUrl('/api-sup-ce/order/deliveryAppoint/submitBatch'),
        method: 'POST',
        data
      }),
    // 通过送货预约ID获取预约单及明细
    getDeliveryAppointById: async params =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryAppoint/getDeliveryAppointById'),
        method: 'GET',
        params
      }),
    // 根据供应商公司ID查询合作组织
    getInfoByParam: async params =>
      http({
        url: getUrl('/api-sup/info/companyInfo/getInfoByParam'),
        method: 'POST',
        params
      }),
    // 分页查询收货单明细列表
    receiveListPage: async data =>
      http({
        url: getUrl('/api-sup-ce/order/deliveryNoteDetail/receiveListPage'),
        method: 'POST',
        data
      }),
    // 供应商批量确认退货单
    returnOrderComfirmBatch: async data =>
      http({
        url: getUrl('/api-sup-ce/order/returnOrder/comfirmBatch'),
        method: 'POST',
        data
      }),
    // 供应商确认订单（630版本）
    comfirm: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/comfirm'),
        method: 'POST',
        data
      }),
    // 供应商拒绝订单（630版本）
    refuse: async data =>
      http({
        url: getUrl('/api-sup-ce/order/order/refuse'),
        method: 'POST',
        data
      })
}
