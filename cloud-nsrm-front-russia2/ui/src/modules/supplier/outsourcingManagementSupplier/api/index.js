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
  update: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisitionForVendor'),
      method: 'POST',
      data,
      loading: true
    }),
  refuse: async data =>
    http({
      url: getUrl('/api-sup-ce/sup/osmaterialrequisition/refuse'),
      method: 'POST',
      data,
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
export const vendorInvSupApi = {
   // 查询供方最新的库存盘点信息
  latestStockDetailListPage: async data =>
    http({
      url: getUrl('/api-sup-ce/os/inv/stock-detail/task/listPage'),
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
  // 供方确认库存
  vendorConfirm: async data =>
    http({
      url: getUrl('/api-sup-ce/os/inv/vendor/confirmInv'),
      method: 'POST',
      data,
      loading: true
    })
}
