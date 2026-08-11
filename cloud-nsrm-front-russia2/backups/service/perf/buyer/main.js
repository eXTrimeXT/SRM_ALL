/**
 * 绩效管理API
 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

const perf = {
  // 绩效评分项目
// 综合绩效明细里面的详情
   scoreManScoringV1: async data =>
    http({
      url: getUrl('/api-pef/perf/score-man-scoring-v1/list'),
      method: 'POST',
      data,
      loading: true
    }),

// 异常问题处理通知
// 查询业务实体
   listOrgByUserAndCompany: async companyId =>
    http({
      url: getUrl('/api-sup/info/orgCategory/listOrgByUserAndCompany'),
      method: 'GET',
      params: { companyId },
      loading: true
    }),

// 考核&改善预警
// 列表
   performanceWarningList: async data =>
    http({
      url: getUrl('/api-pef/scoring/perfOverallScore/listPageScoreUnder60'),
      method: 'POST',
      data,
      loading: true
    }),

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
// 审批
   submitWithFlow: async data =>
    http({
      url: getUrl('/api-pef/vendorAsses/submitWithFlow'),
      method: 'POST',
      data,
      loading: true
    }),
// 审批
   scoreprojectWithFlow: async data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/submitProcessScoreItemsWithFlow'),
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

// 绩效模型
// 列表
   listPefTemplateHeaderPage: async data =>
    http({
      url: getUrl('/api-pef/template/listPefTemplateHeaderPage'),
      method: 'POST',
      data,
      loading: true
    }),
// 启用 |禁用 模型
   enableOrDisPefTemplateHeader: async data =>
    http({
      url: getUrl('/api-pef/template/enablePefTemplateHeader'),
      method: 'POST',
      data,
      loading: true
    }),
// 删除 模型
   delPefTemplate: async data =>
    http({
      url: getUrl('/api-pef/template/delPefTemplateHeader'),
      method: 'POST',
      data,
      loading: true
    }),
// 复制 模型
   copyTemplate: async data =>
    http({
      url: getUrl('/api-pef/template/copyPerfTemplateHeader'),
      method: 'POST',
      data,
      loading: true
    }),
// 根据模型id 查询模型数据详情
   getPefTemplateDetail: async params =>
    http({
      url: getUrl('/api-pef/template/findPerTemplateByTemplateHeadId'),
      method: 'GET',
      params,
      loading: true
    }),

// 获取绩效维度和类型
   getPefTempDimAndOrgCat: async params =>
    http({
      url: getUrl('/api-pef/template/findIndicatorsHeaderDimensionList'),
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
// 删除绩效行信息
   delPefTemplateLine: async data =>
    http({
      url: getUrl('/api-pef/template/delPefTemplateLine'),
      method: 'POST',
      data,
      loading: true
    }),
// 保存绩效模型
   savePerfTemplate: async data =>
    http({
      url: getUrl('/api-pef/template/savePerfTemplate'),
      method: 'POST',
      data,
      loading: true
    }),
// 修改绩效模型
   updatePerfTemplate: async data =>
    http({
      url: getUrl('/api-pef/template/updatePerfTemplate'),
      method: 'POST',
      data,
      loading: true
    }),

// 根据绩效模型指标ID或指标库行ID获取绩效指标详情信息
   findPerfTemplateLineAndIndsLine: async params =>
    http({
      url: getUrl('/api-pef/template/findPerfTemplateLineAndIndsLine'),
      method: 'GET',
      params,
      loading: false
    }),
// 根据绩效模型维度Id删除记录
   delPefTemplateDimWeight: async data =>
    http({
      url: getUrl('/api-pef/template/delPefTemplateDimWeight'),
      method: 'POST',
      data,
      loading: false
    }),

// 绩效等级
// 列表
   listPerfLevelPage: async data =>
    http({
      url: getUrl('/api-pef/perfLevel/listPerfLevelPage'),
      method: 'POST',
      data,
      loading: true
    }),
// 保存
   savePerfLevel: async data =>
    http({
      url: getUrl('/api-pef/perfLevel/savePerfLevel'),
      method: 'POST',
      data,
      loading: true
    }),
// 修改
   updatePerfLevel: async data =>
    http({
      url: getUrl('/api-pef/perfLevel/updatePerfLevel'),
      method: 'POST',
      data,
      loading: true
    }),
// 删除
   deletePerfLevel: async data =>
    http({
      url: getUrl('/api-pef/perfLevel/deletePerfLevel'),
      method: 'POST',
      data,
      loading: true
    }),
// 启用|禁用
   enablePerfLevel: async data =>
    http({
      url: getUrl('/api-pef/perfLevel/enablePerfLevel'),
      method: 'POST',
      data,
      loading: true
    }),
// 查询等级
   getPerfLevelById: async params =>
    http({
      url: getUrl('/api-pef/perfLevel/getPerfLevel'),
      method: 'GET',
      params,
      loading: false
    }),

// 绩效评分项目
// 列表
   listPerfScoreItemsPage: async data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/listPerfScoreItemsPage'),
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
   confirmBeforeCalculate: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/confirmBeforeCalculate'),
      method: 'POST',
      data
    }),
// 根据 项目ID获取详细信息接口NEW
   getScoreItemsDetailByScoreItemsId: params =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/getScoreItemsDetailByScoreItemsId'),
      method: 'GET',
      params
    }),
// 获取绩效模型集合接口
   getValidTemplateHeader: params =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/getValidTemplateHeader'),
      method: 'GET',
      params
    }),

// 根据 项目ID获取详细信息接口
   findPerfScoreItemsById: params =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/findPerfScoreItemsById'),
      method: 'GET',
      params
    }),
// 根据绩效模型ID获取绩效指标接口
   getPerfTemplateLineByHeaderId: params =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/getPerfTemplateLineByHeaderId'),
      method: 'GET',
      params
    }),
// 提交评分项目
   scoreItemsSubmit: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/scoreItemsSubmit'),
      method: 'POST',
      data
    }),
// 保存评分项目-2
   scoreItemsSaveTemporary: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/scoreItemsSaveTemporary'),
      method: 'POST',
      data
    }),
// 保存评分项目
   savePerfScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/savePerfScoreItems'),
      method: 'POST',
      data
    }),
// 更新
   updatePerfScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/updatePerfScoreItems'),
      method: 'POST',
      data
    }),
// 更新绩效评分
   saveOrUpdatePerfScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/saveOrUpdatePerfScoreItems'),
      method: 'POST',
      data
    }),
// 根据删供应商
   delPerfScoreItemsSupById: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/delPerfScoreItemsSupById'),
      method: 'POST',
      data
    }),
// 根据删除评分人
   delPerfScoreItemsManById: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/delPerfScoreItemsManById'),
      method: 'POST',
      data
    }),

// 发布
   publishScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/publishScoreItems'),
      method: 'POST',
      data
    }),
// 通知评分人
   notifyScorers: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/notifyScorers'),
      method: 'POST',
      data
    }),
// 废弃项目
   abandonScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/abandonScoreItems'),
      method: 'POST',
      data
    }),
// 删除项目
   delScoreItemsAndSon: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/delScoreItemsAndSon'),
      method: 'POST',
      data
    }),

// 计算评分
   calculateScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/calculateScoreItems'),
      method: 'POST',
      data
    }),
// 提交流程
   submitProcessScoreItems: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/submitProcessScoreItems'),
      method: 'POST',
      data
    }),
// 评分人任务分配 点击查询： 根据绩效模型头ID获取供应商指标配置信息集合
   getScoreItemsSupIndicatorList: data =>
    http({
      url: getUrl('/api-pef/scoreproject/scoreItems/getScoreItemsSupIndicatorList'),
      method: 'POST',
      data
    }),
// 评分人任务分配 点击查询： 根据绩效模型头ID获取供应商指标配置信息集合1-2
   getScoreItemsSupIndicatorTemplate: params =>
    http({
      url: getUrl('/api-pef/template/listTemplateLinesByTemplateHeaderId'),
      method: 'GET',
      params
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

// --------------------------------------------------------------------------------------------------
// -------------------------------------------- 检验标准 --------------------------------------------
// --------------------------------------------------------------------------------------------------
   inspectionStandard: {
    // 暂存
    tempSave: async data =>
      http({
        url: getUrl('/api-pef/inspection-standard/tempSave'),
        method: 'POST',
        data,
        loading: true
      }),
    // 提交
    submit: async data =>
      http({
        url: getUrl('/api-pef/inspection-standard/submit'),
        method: 'POST',
        data,
        loading: true
      }),
    // 获取详情
    getInfo: async params =>
      http({
        url: getUrl('/api-pef/inspection-standard/getInfo'),
        method: 'GET',
        params,
        loading: true
      }),
    // 获取详情
    listByIspId: async params =>
      http({
        url: getUrl('/api-pef/inspection-standard-project/listByIspId'),
        method: 'GET',
        params,
        loading: true
      }),
    // 删除
    delete: async data =>
      http({
        url: getUrl('/api-pef/inspection-standard/delete'),
        method: 'PUT',
        data,
        loading: true
      }),
    // 升级
    upgrade: async inspectionStandardId =>
      http({
        url: getUrl(
          `/api-pef/inspection-standard/versionUpgrade?inspectionStandardId=${inspectionStandardId}`,
        ),
        method: 'PUT',
        params: {},
        loading: true
      }),
    // 生效
    takeEffect: async inspectionStandardId =>
      http({
        url: getUrl(`/api-pef/inspection-standard/take-effect/${inspectionStandardId}`),
        method: 'PUT',
        params: {},
        loading: true
      }),
    // 失效
    loseEffect: async inspectionStandardId =>
      http({
        url: getUrl(`/api-pef/inspection-standard/lose-effect/${inspectionStandardId}`),
        method: 'PUT',
        params: {},
        loading: true
      }),
    getExcHandlingNoticeDetail: async (url, params) =>
      http({
        url: getUrl(url),
        method: 'GET',
        params,
        loading: true
      }),
    noticeAdd: async data =>
      http({
        url: getUrl('/api-pef/quasupplierenotice/add'),
        method: 'POST',
        data,
        loading: true
      }),
    noticeModify: async data =>
      http({
        url: getUrl('/api-pef/quasupplierenotice/modify'),
        method: 'POST',
        data,
        loading: true
      }),
    noticeDelete: async params =>
      http({
        url: getUrl('/api-pef/quasupplierenotice/delete'),
        method: 'GET',
        params,
        loading: true
      }),
    incomingExceptionAdd: async data =>
      http({
        url: getUrl('/api-pef/itemExceptionHandle/add'),
        method: 'POST',
        data,
        loading: true
      }),
    incomingExceptionModify: async data =>
      http({
        url: getUrl('/api-pef/itemExceptionHandle/modify'),
        method: 'POST',
        data,
        loading: true
      }),
    incomingExceptionPublish: async data =>
      http({
        url: getUrl('/api-pef/itemExceptionHandle/publish'),
        method: 'POST',
        data,
        loading: true
      }),
    incomingExceptionDetail: async params =>
      http({
        url: getUrl('/api-pef/itemExceptionHandle/get'),
        method: 'GET',
        params,
        loading: true
      }),
    incomingExceptionDelete: async params =>
      http({
        url: getUrl('/api-pef/itemExceptionHandle/delete'),
        method: 'GET',
        params,
        loading: true
      }),
    inspectionItemDetail: async params =>
      http({
        url: getUrl('/api-pef/perf/inspectionproject/get'),
        method: 'GET',
        params,
        loading: true
      }),
    inspectionItemModify: async (url, data) =>
      http({
        url: getUrl(url),
        method: 'POST',
        data,
        loading: true
      }),
    inspectionItemDelete: async params =>
      http({
        url: getUrl('/api-pef/perf/inspectionproject/delete'),
        method: 'GET',
        params,
        loading: true
      }),
    processExceptionAdd: async data =>
      http({
        url: getUrl('/api-pef/quaProcessException/add'),
        method: 'POST',
        data,
        loading: true
      }),
    processExceptionModify: async data =>
      http({
        url: getUrl('/api-pef/quaProcessException/modify'),
        method: 'POST',
        data,
        loading: true
      }),
    processExceptionPublish: async data =>
      http({
        url: getUrl('/api-pef/quaProcessException/publish'),
        method: 'POST',
        data,
        loading: true
      }),
    processExceptionDetail: async params =>
      http({
        url: getUrl('/api-pef/quaProcessException/getById'),
        method: 'GET',
        params,
        loading: true
      }),
    processExceptionDetailList: async params =>
      http({
        url: getUrl('/api-pef/quaProcessExceptionHandle/listByBillCode'),
        method: 'GET',
        params,
        loading: true
      }),
    processExceptionDelete: async params =>
      http({
        url: getUrl('/api-pef/quaProcessException/delete'),
        method: 'GET',
        params,
        loading: true
      }),
    reportDetail: async params =>
      http({
        url: getUrl('/api-pef/report8D/getReportById'),
        method: 'GET',
        params,
        loading: true
      }),
    reportSave: async (url, data) =>
      http({
        url: getUrl(url),
        method: 'POST',
        data,
        loading: true
      }),
    reportClose2: async data =>
      http({
        url: getUrl('/api-pef/report8D/close8dReport2'),
        method: 'POST',
        data,
        loading: true
      }),
    reportClose: async reportId =>
      http({
        url: getUrl(`/api-pef/report8D/close/${reportId}`),
        method: 'PUT',
        loading: true
      }),
    reportDelete: async params =>
      http({
        url: getUrl('/api-pef/report8D/delete'),
        method: 'GET',
        params,
        loading: true
      })
  },

  /**
   * 供应商分级
   * */
// 分级评级明细---》查询
  addOrUpdateVendorLevel: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/listPageByParam',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》保存按钮
  saveOrUpdateScoreList: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/saveOrUpdateScoreList',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》删除/批量删除
  bathDelete: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/bathDelete',
      method: 'POST',
      data
    })
  },

  // 分级评级明细---》绩效明细弹窗
  listPageOverallScoreByParam: (data) => {
    return http({
      url: '/api-pef/perf/leveloverallscore/listPageOverallScoreByParam',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》提交
  submitScoreList: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/submitScoreList',
      method: 'POST',
      data
    })
  },

// 分级评级明细---》失效
  inValid: (data) => {
    return http({
      url: '/api-pef/perf/levellinescore/inValid',
      method: 'POST',
      data
    })
  }
}

export default {
  perf
}
