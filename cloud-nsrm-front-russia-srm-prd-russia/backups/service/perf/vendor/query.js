/**
 * @description 供应商 - 绩效结果 /api-perf/vendor/query
 * @description 路径：$api.perf.vendor.query
 * @author 伟龙
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const perf = {
  // 查看明细 overallScoreId
  findOverallScorelById: params =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/findOverallScorelById'),
      method: 'GET',
      params
    }),
  // 综合绩效查询
  listPerfOverallScorePage: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/listPerfOverallScorePage'),
      method: 'POST',
      data
    }),
  // 综合绩效查询 查询等级下拉
  findDistinctLevelNameList: params =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/findDistinctLevelNameList'),
      method: 'GET',
      params
    }),
  // 项目下拉
  findCalculatedScoreItemsList: params =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/findCalculatedScoreItemsList'),
      method: 'GET',
      params
    }),
  // 综合绩效供应商反馈说明上传
  vendorConfirm: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/vendorConfirm'),
      method: 'POST',
      data
    }),
  // 综合绩效查询上传附件
  listPerfceeaUploadFile: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/uploadFile'),
      method: 'POST',
      data
    })
}

export default {
  perf
}
