/**
 * @description 供应商 - 供方绩效考核 /api-perf/vendor/assessment
 * @description 路径：$api.perf.vendor.assessment
 * @author 伟龙
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const perf = {
// 查询考核单
  vendorAssesQueryById: async params =>
    http({
      url: getUrl('/api-pef/vendorAsses/queryById'),
      method: 'GET',
      params,
      loading: true
    }),
  // 根基类型和维度获取指标
  getPefTempLineByDim: async params =>
    http({
      url: getUrl('/api-pef/template/findIndicatorsHeaderByDimension'),
      method: 'GET',
      params,
      loading: false
    }),
  // 根据绩效模型指标ID或指标库行ID获取绩效指标详情信息
  findPerfTemplateLineAndIndsLine: async params =>
    http({
      url: getUrl('/api-pef/template/findPerfTemplateLineAndIndsLine'),
      method: 'GET',
      params,
      loading: false
    }),
  // 更新
  vendorAssesModify: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  // 通知供应商 支持批量
  notifySupplier: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/notifySupplier'),
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商反馈
  vendorFeedback: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/vendorFeedback'),
      method: 'POST',
      data,
      loading: true
    }),
  // 采购商处理
  buyersProcess: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/buyersProcess'),
      method: 'POST',
      data,
      loading: true
    }),
  // 新增
  vendorAssesAdd: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/add'),
      method: 'POST',
      data,
      loading: true
    })
}

export default {
  perf
}
