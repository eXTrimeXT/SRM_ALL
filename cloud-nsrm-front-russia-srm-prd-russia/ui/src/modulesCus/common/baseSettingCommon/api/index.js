import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const commonApi = {
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
    }),
  // 物料详情接口
  materialItemGet: async params =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaGet'),
      method: 'GET',
      params,
      loading: true
    }),
  // 物料信息维护接口
  materialItemModify: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/ceeaSaveOrUpdate'),
      method: 'POST',
      data
    }),
  // 列表查询接口
  listPageByCondition: async data =>
    http({
      url: getUrl('/api-base/material/materialItem/listPageByCondition'),
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
