/**
 * 计划管理 API
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`
// 采购商
export const buyer = {
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

// 供应商端
export const supplier = {
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
