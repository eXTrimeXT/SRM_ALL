/**
 * 采购订单API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 保存
export const save = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/save'),
    method: 'POST',
    data,
    loading: true
  })

// 更新
export const update = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/update'),
    method: 'POST',
    data,
    loading: true
  })

// 发布
export const publish = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/publish'),
    method: 'POST',
    data,
    loading: true
  })

// 发布(审批流)
export const submitWithFlow = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/submitWithFlow'),
    method: 'POST',
    data,
    loading: true
  })

// 查询订单详情
export const queryOrderById = async orderId =>
  http({
    url: getUrl('/api-sup-ce/po/order/queryOrderById'),
    method: 'GET',
    params: { orderId },
    loading: true
  })

// 发布订单
export const publishBatch = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/publishBatch'),
    method: 'POST',
    data,
    loading: true
  })

// 接受回复
export const acceptReply = async orderId =>
  http({
    url: getUrl('/api-sup-ce/po/order/acceptReply'),
    method: 'GET',
    params: { orderId },
    loading: true
  })

// 采购商驳回
export const returnOrder = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/returnOrder'),
    method: 'POST',
    data,
    loading: true
  })

// 查询未税价
export const queryPrice = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/queryPrice'),
    method: 'POST',
    data,
    loading: true
  })

export const orderStorage = {
  // 订单入库列表（采购商）
  listPage: async data =>
    http({
      url: getUrl('/api-sup-ce/po/warehouseReceipt/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 订单入库列表（供应商）
  listPageVendor: async data =>
    http({
      url: getUrl('/api-sup-ce/order/warehouseReceipt/warehouseReceiptPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 确认入库
  confirm: async data =>
    http({
      url: getUrl('/api-sup-ce/po/warehouseReceipt/confirm'),
      method: 'POST',
      data,
      loading: true
    }),
  // 入库单冲销
  writeOff: async data =>
    http({
      url: getUrl('/api-sup-ce/po/warehouseReceipt/writeOff'),
      method: 'POST',
      data,
      loading: true
    })
}

export const returnedGoods = {
  // 退货订单列表
  listPage: async data =>
    http({
      url: getUrl('/api-sup-ce/po/returnOrder/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 退货订单详情
  getReturnOrderById: async returnOrderId =>
    http({
      url: getUrl('/api-sup-ce/po/returnOrder/getReturnOrderById'),
      method: 'GET',
      params: { returnOrderId },
      loading: true
    })
}

// 查询合同DTO
export const queryContractMaterialPage = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/queryContractMaterialPage'),
    method: 'POST',
    data,
    loading: true
  })

// 查询可选的物料
export const listPagePriceLibrary = async data =>
  http({
    url: getUrl('/api-sup-ce/po/order/listPagePriceLibrary'),
    method: 'POST',
    data,
    loading: true
  })

// 导入 /api-sup-ce/po/orderDetail/importExcel?vendorId=xxx&organizationId=2 POST

export const purchaseAnalysis = {
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
