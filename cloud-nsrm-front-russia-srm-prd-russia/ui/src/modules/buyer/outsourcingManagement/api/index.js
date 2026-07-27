/**
 * 供应商 委外BOM API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 委外的
export const osMaterialRequisitionApi = {
  vendorList: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  getDtoByParam: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisitiondetail/getDtoByParam'),
      method: 'POST',
      data,
      loading: true
    }),
  getById: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),
  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/delete'),
      method: 'GET',
      params: { id },
      loading: true
    })
}

// bom
export const bomApi = {
  list: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  delete: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/delete'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  getById: async id =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/get'),
      method: 'GET',
      params: { id },
      loading: true
    }),

  addOrUpdate: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/bom/addOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

 // 供方库存管理
export const vendorInvApi = {
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
