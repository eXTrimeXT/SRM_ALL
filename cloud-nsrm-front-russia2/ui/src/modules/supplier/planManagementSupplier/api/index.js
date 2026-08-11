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

// 供方历史库存
export const supplierInventoryLogApi = {
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/inventoryLog/listPage'),
      method: 'POST',
      data,
      loading: false
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

// 供方产能提报
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

// 供应商端
export const orderForecastSupplierApi = {
  // 供应商批量确认计划
  comfirmBatch: data =>
    http({
      url: getUrl('/api-sup-ce/plan/orderForecast/comfirmBatch'),
      method: 'POST',
      data
    }),

  // 供应商批量确认计划
  rejectBatch: data =>
    http({
      url: getUrl('/api-sup-ce/plan/orderForecast/rejectBatch'),
      method: 'POST',
      data
    })
}

// 购物车
export const purCatApi = {
  // 物料保存（保存页面上编辑的供应商）
  ceeaUpdateSupplier: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaUpdateSupplier'),
      method: 'POST',
      data
    }),
  // 通知供应商
  ceeaNotifyVendor: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaNotifyVendor'),
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
//

export const planSupApi = {
  // 报表查询物料列表
  reportsTestItemList: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listPageMaterialItemChart'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除物料
  materialItemDel: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增编辑物料保存
  saveOrUpdateMBatch: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/saveOrUpdateMBatch'),
      method: 'POST',
      data,
      loading: true
    })
}
