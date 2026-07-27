
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 品类状态配置
export const costReductionApi = {
  // 供应商分析
  supplierAnalysis: async data =>
    http({
      url: getUrl('/api-report/supplier/getPurchaseAnalysis'),
      method: 'POST',
      data,
      loading: true
    }),
    // 采购降本
  costReductionAnalysis: async data =>
    http({
      url: getUrl('/api-report/costReduction/getCostReductionAnalysis'),
      method: 'POST',
      data,
      loading: true
    }),
  getPurchaseAnalysisCategory: async data =>
    http({
      url: getUrl('/api-report/order/getPurchaseAnalysisCategory'),
      loading: true,
      method: 'POST',
      data
    })
}
