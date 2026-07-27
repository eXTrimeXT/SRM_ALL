/**
 * @description 采购商 - 技术方案分析 /api-sou/api-ql
 */
import http from '@/utils/axios/http'

// 查询流水
export function getOrderFileCheck (params) {
  return http({
    url: '/api-sou/api-ql/OrderFileCheck/query',
    method: 'POST',
    data: params,
    loading: true
  })
}

// 保存流水
export function saveOrderFileCheck (params) {
  return http({
    url: '/api-sou/api-ql/OrderFileCheck/save',
    method: 'POST',
    data: params
  })
}

// 流水结果
export function getOrderFileCheckDetail (params) {
  return http({
    url: '/api-sou/api-ql/OrderFileCheck/detail',
    method: 'POST',
    data: params
  })
}

// 供应商及文件列表
export function getFileList (params) {
  return http({
    url: `/api-sou/ext/buyer/bid/init/getTechPlan?projectId=${params.projectId}`,
    method: 'GET',
    loading: true
  })
}

// 查询文件是否对比完成
export function getFileResult (params) {
  return http({
    url: `/api-pj/external/ai/bidReview/doneFlag?projectId=${params.projectId}`,
    method: 'GET',
    loading: true
  })
}
