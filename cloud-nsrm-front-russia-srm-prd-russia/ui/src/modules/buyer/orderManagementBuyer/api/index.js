/* 订单管理 */

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
    }),

  // 保存
  save: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/save'),
      method: 'POST',
      data,
      loading: true
    }),

  // 发布
  publish: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/publish'),
      method: 'POST',
      data,
      loading: true
    }),

  // 查询合同DTO
  queryContractMaterialPage: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/queryContractMaterialPage'),
      method: 'POST',
      data,
      loading: true
    }),

  // 查询可选的物料
  listPagePriceLibrary: async data =>
    http({
      url: getUrl('/api-sup-ce/po/order/listPagePriceLibrary'),
      method: 'POST',
      data,
      loading: true
    })
}

/* 送货单 */
export const deliveryOrderApi = {
  // 详情页 - 分页查询送货单明细
  deliveryNoteDetailListPage: async data =>
    http({
      url: getUrl('/api-sup-ce/po/deliveryNoteDetail/listPage'),
      method: 'POST',
      data
    })
}

/* 送货预约 */
export const deliveryAppointmentApi = {
  // 通过送货预约ID获取预约单及明细
  getDeliveryAppointById: async params =>
    http({
      url: getUrl('/api-sup-ce/order/deliveryAppoint/getDeliveryAppointById'),
      method: 'GET',
      params
    })
}

/* 入库单 */
export const orderStorageApi = {
  // 入库单冲销
  writeOff: async data =>
    http({
      url: getUrl('/api-sup-ce/po/warehouseReceipt/writeOff'),
      method: 'POST',
      data,
      loading: true
    })
}

/* 采购分析 */
export const purchaseAnalysisApi = {
  getOrgType: async () =>
    http({
      url: getUrl('/api-base/organization/organizationsType/getOrgTypeByUser'),
      loading: true,
      method: 'GET'
    }),
  getCategory: async () =>
    http({
      url: getUrl('/api-base/dict/base-dict-item/queryProductType'),
      loading: true,
      method: 'GET'
    }),
  getPurchaseAnalysis: async data =>
    http({
      url: getUrl('/api-report/order/getPurchaseAnalysis'),
      loading: true,
      method: 'POST',
      data
    }),
  getPurchaseAnalysisCategory: async data =>
    http({
      url: getUrl('/api-report/order/getPurchaseAnalysisCategory'),
      loading: true,
      method: 'POST',
      data
    }),
  queryOrderWarehousing: async data =>
    http({
      url: getUrl('/api-report/order/queryOrderWarehousing'),
      loading: true,
      method: 'POST',
      data
    }),
  queryOrderPunctuality: async data =>
    http({
      url: getUrl('/api-report/order/queryOrderPunctuality'),
      loading: true,
      method: 'POST',
      data
    }),
  queryOrderConfirm: async data =>
    http({
      url: getUrl('/api-report/order/queryOrderConfirm'),
      loading: true,
      method: 'POST',
      data
    }),
  queryOrderPunctualityYear: async data =>
    http({
      url: getUrl('/api-report/order/queryOrderPunctualityYear'),
      loading: true,
      method: 'POST',
      data
    }),
  queryPurchaseDetailList: async data =>
    http({
      url: getUrl('/api-report/order/queryPurchaseDetailList'),
      loading: true,
      method: 'POST',
      data
    }),
  getPurchaseCategoryDetail: async data =>
    http({
      url: getUrl('/api-report/order/getPurchaseCategoryDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  querySupplierCooperation: async data =>
    http({
      url: getUrl('/api-report/supplier/queryCooperationDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryPurchaseAmount: async data =>
    http({
      url: getUrl('/api-report/supplier/queryPurchaseAmount'),
      loading: true,
      method: 'POST',
      data
    }),
  querySupplierGrade: async data =>
    http({
      url: getUrl('/api-report/supplier/queryPeformanceDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryCategorySupplier: async data =>
    http({
      url: getUrl('/api-report/supplier/queryCategoryDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  querySupplierDetailList: async data =>
    http({
      url: getUrl('/api-report/supplier/querySupplierDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryMonthsDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryMonthsDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryYearCumulativeDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryYearCumulativeDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryCategorRateDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryCategorRateDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryCategorAmountDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryCategorAmountDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryCategorUpAmountDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryCategorUpAmountDetail'),
      loading: true,
      method: 'POST',
      data
    }),
  queryCostReductionDetail: async data =>
    http({
      url: getUrl('/api-report/costReduction/queryCostReductionDetail'),
      loading: true,
      method: 'POST',
      data
    })
}
