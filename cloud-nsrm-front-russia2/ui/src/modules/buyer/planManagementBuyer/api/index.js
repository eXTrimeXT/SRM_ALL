/* 计划管理 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

/* 供方库存管理 */
export const supplierInventory = {
  updateSupplierInventoryByBuyer: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventory/updateSupplierInventoryByBuyer'),
      method: 'POST',
      data,
      loading: true
    }),
  update: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventory/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventory/listPage'),
      method: 'POST',
      data,
      loading: true
    })
}

/* 供方产能提报 */
export const capacityreport = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/capacityreport/listAll'),
      method: 'POST',
      data,
      loading: true
    }),
  confirmHandle: async id =>
    http({
      url: getUrl('/api-sup/sup/capacityreport/confirm'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  add: async data =>
    http({
      url: getUrl('/api-sup/sup/capacityreport/addCapacityReport'),
      method: 'POST',
      data,
      loading: true
    }),
  // 更新
  update: async data =>
    http({
      url: getUrl('/api-sup/sup/capacityreport/updateCapacityReport'),
      method: 'POST',
      data,
      loading: true
    })
}

// 供方历史库存
export const supplierInventoryLogApi = {
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/add'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/modify'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/batchSaveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

export const orderForecastBuyerApi = {
  // 采购商批量发布计划
  submitBatch: data => http({
    url: getUrl('/api-sup-ce/plan/orderForecast/submitBatch'),
    method: 'POST',
    data
  }),

  // 采购商批量保存计划检查(是否会废弃掉已确认的计划)
  checkSaveBatch: data => http({
    url: getUrl('/api-sup-ce/plan/orderForecast/checkSaveBatch'),
    method: 'POST',
    data
  }),

  // 采购商批量保存计划
  saveBatch: data => http({
    url: getUrl('/api-sup-ce/plan/orderForecast/saveBatch'),
    method: 'POST',
    data
  }),

  // 采购商批量删除计划
  deleteBatch: data => http({
    url: getUrl('/api-sup-ce/plan/orderForecast/deleteBatch'),
    method: 'POST',
    data
  }),

  // 采购商批量删除计划
  abandonBatch: data => http({
    url: getUrl('/api-sup-ce/plan/orderForecast/abandonBatch'),
    method: 'POST',
    data
  })
}

// 供方产能历史记录
export const capacityreporthistory = {
  list: async data =>
    http({
      url: '/api-sup/sup/capacityreport/listAllHistory',
      method: 'POST',
      data,
      loading: true
    }),

  add: async data =>
    http({
      url: '/api-sup/sup/capacityreport/add',
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: '/api-sup/sup/capacityreport/modify',
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: '/api-sup/sup/capacityreport/delete',
      method: 'GET',
      params: { id },
      loading: true
    }),

  batchSaveOrUpdate: async data =>
    http({
      url: '/api-sup/sup/capacityreport/batchSaveOrUpdate',
      method: 'POST',
      data,
      loading: true
    })
}
