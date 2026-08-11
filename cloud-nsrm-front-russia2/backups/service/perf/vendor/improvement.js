/**
 * @description 供应商 - 供应商改善 /api-perf/vendor/improvement
 * @description 路径：$api.perf.vendor.improvement
 * @author 伟龙
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const perf = {
  // 列表
  vendorImproveListPage: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/listPage'),
      method: 'POST',
      data,
      loading: true
    }),
  // 通知供应商
  vendorImproveNotifySupplier: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/notifySupplier'),
      method: 'POST',
      data,
      loading: true
    }),
  // 删除
  vendorImproveDel: async params =>
    http({
      url: getUrl('/api-pef/vendorImprove/delete'),
      method: 'GET',
      params,
      loading: true
    }),
  // 综合绩效查询
  listPerfOverallScorePage: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/listPerfOverallScorePage'),
      method: 'POST',
      data
    }),
  // 查询
  vendorImproveQueryById: async params =>
    http({
      url: getUrl('/api-pef/vendorImprove/queryById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 供应商反馈
  vendorImproveVendorFeedback: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/vendorFeedback'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购商评价处理
  vendorImproveBuyersProcess: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/buyersProcess'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增
  vendorImproveAdd: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // 更新
  vendorImproveModify: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/modify'),
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  perf
}
