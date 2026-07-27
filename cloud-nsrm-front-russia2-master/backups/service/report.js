/**
 * 报表分析API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 供应商分析
export const supplierAnalysis = async data =>
  http({
    url: getUrl('/api-report/supplier/getPurchaseAnalysis'),
    method: 'POST',
    data,
    loading: true
  })
// 采购降本
export const costReductionAnalysis = async data =>
  http({
    url: getUrl('/api-report/costReduction/getCostReductionAnalysis'),
    method: 'POST',
    data,
    loading: true
  })
