/* 绩效模块 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const perVendorApi = {
  // 指标库
// 取值方式取值
  valueDataGet: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/listByTye'),
      method: 'POST',
      data,
      loading: true
    }),
// 指标列表
  listIndicatorsPage: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/findIndicatorsHeaderPage'),
      method: 'POST',
      data,
      loading: true
    }),
// 删除指标
  delIndication: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/delIndicationHeaderAndLine'),
      method: 'POST',
      data,
      loading: true
    }),

// 启用|禁用 指标
  enableOrDisabledIndication: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/updateIndicatorHeadAndLineEnable'),
      method: 'POST',
      data,
      loading: true
    }),

// 查询指标详情
  getIndicatorsDetail: async params =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/getIndicatorsHeaderById'),
      method: 'GET',
      params,
      loading: true
    }),
// 保存指标信息
  saveIndicator: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/saveIndicatorHeadAndLine'),
      method: 'POST',
      data,
      loading: true
    }),
  updateIndicator: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsHeader/updateIndicatorHeadAndLine'),
      method: 'POST',
      data,
      loading: true
    }),
// 删除指标行
  delIndicatorsLine: async data =>
    http({
      url: getUrl('/api-pef/perf/indicatorsLine/deleteIndicatorsLine'),
      method: 'POST',
      data,
      loading: true
    }),

  // 评分人评分
// 列表
  listScoreManScoringPage: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/listScoreManScoringPage'),
      method: 'POST',
      data,
      loading: true
    }),
  ceeaUploadFile: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/uploadFile'),
      method: 'POST',
      data
      // loading:true,
    }),
// 复制项目
  ceeaCopyProject: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/copyScoreItems'),
      method: 'POST',
      data
      // loading:true,
    }),
// 附件删除
  ceeaDeleteFile: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/deleteFile'),
      method: 'POST',
      data
      // loading:true,
    }),
// 评分人绩效评分提交前前调用
  getScoreItemsAfterSubmit: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/getScoreItemsAfterSubmit'),
      method: 'POST',
      data
    }),
// 评分人绩效评分提交前调用
  confirmBeforeScoreManScoringSubmit: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/confirmBeforeScoreManScoringSubmit'),
      method: 'POST',
      data
    }),
// 保存
  saveScoreManScoring: data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/saveScoreManScoring'),
      method: 'POST',
      data
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
// 查看明细 overallScoreId
  findOverallScorelById: params =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/findOverallScorelById'),
      method: 'GET',
      params
    }),
// 综合绩效查询上传附件
  listPerfceeaUploadFile: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/uploadFile'),
      method: 'POST',
      data
    }),
// 综合绩效删除上传附件
  listPerfceeaDeleteFile: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/deleteFile'),
      method: 'POST',
      data
    }),
// 综合绩效供应商反馈说明上传
  vendorConfirm: data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/vendorConfirm'),
      method: 'POST',
      data
    }),

  // 考核
// 列表
  listAssessmentPage: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/listPage'),
      method: 'POST',
      data,
      loading: true
    }),

// 列表删除数据
  vendorAssesDel: async params =>
    http({
      url: getUrl('/api-pef/vendorAsses/delete'),
      method: 'GET',
      params,
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
    }),
// 更新
  vendorAssesModify: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/modify'),
      method: 'POST',
      data,
      loading: true
    }),
// 查询考核单
  vendorAssesQueryById: async params =>
    http({
      url: getUrl('/api-pef/vendorAsses/queryById'),
      method: 'GET',
      params,
      loading: true
    }),

  // 供应商改善
// 列表
  vendorImproveListPage: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/listPage'),
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
// 通知供应商
  vendorImproveNotifySupplier: async data =>
    http({
      url: getUrl('/api-pef/vendorImprove/notifySupplier'),
      method: 'POST',
      data,
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
// 查询
  vendorImproveQueryById: async params =>
    http({
      url: getUrl('/api-pef/vendorImprove/queryById'),
      method: 'GET',
      params,
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
    }),
    // 供应商根据关键字和组织ID查询
  listPageByOrgIdAndKeyWord: data =>
    http({
      url: getUrl('/api-sup/info/companyInfo/listPageByOrgIdAndKeyWord'),
      method: 'POST',
      data
    }),
  // 根据绩效模型指标ID或指标库行ID获取绩效指标详情信息
  findPerfTemplateLineAndIndsLine: params =>
    http({
      url: getUrl('/api-pef/template/findPerfTemplateLineAndIndsLine'),
      method: 'GET',
      params,
      loading: false
    }),
  // 根基类型和维度获取指标
  getPefTempLineByDim: params =>
    http({
      url: getUrl('/api-pef/template/findIndicatorsHeaderByDimension'),
      method: 'GET',
      params,
      loading: false
    })
}
