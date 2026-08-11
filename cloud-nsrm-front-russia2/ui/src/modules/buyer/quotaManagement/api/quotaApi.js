/**
 * 供应商 API
 * 配额模块
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 配额清单
export const quotaDetailApi = {
  list: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  calcQuota: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/calcQuota'),
      method: 'POST',
      data,
      loading: true
    }),

  createQuotaFlow: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/createQuotaFlow'),
      method: 'POST',
      data,
      loading: true
    }),

  update: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  batchUpdate: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/batchUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

// 配额审批
export const quotaApproveApi = {
  listFlowPage: async data =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/listFlowPage'),
      method: 'POST',
      data,
      loading: true
    }),

  detailFlowPage: async ({ id }) =>
    http({
      url: getUrl('/api-sup/sup/quotadetail/getQuotaFlowListById'),
      method: 'GET',
      params: { id },
      loading: true
    })
}
// 配额执行偏差报表
export const quotaOffsetApi = {
  listPage: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

  save: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/save'),
      method: 'POST',
      data,
      loading: true
    }),

  detailList: async data =>
    http({
      url: getUrl('/api-sup/sup/quotaoffset/listDetailPage'),
      method: 'POST',
      data,
      loading: true
    })
}
