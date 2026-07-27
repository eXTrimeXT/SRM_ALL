/* 供应商质量质量 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

// 过程PDCA
export const pdcaPage = {
  // PCDA查询条件获取
  pcdaPageCondition: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcPdcaRecord/pageCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  // 提交PDCA填写记录
  pcdaSubmit: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcPdcaReport/submit'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取PDCA填写记录
  findReportByRecordId: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcPdcaReport/findReportByRecordId'),
      method: 'POST',
      data,
      loading: true
    }),
  // 保存PDCA填写记录
  saveOrUpdate: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcPdcaReport/saveOrUpdate'),
      method: 'POST',
      data,
      loading: true
    })
}

// 监控项目管理
export const qualityProject = {
  // 初始化条件查询
  pageCondition: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcMonitorList/pageCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  // 项目管理列表
  listMyMonitors: async data =>
    http({
      url: getUrl('/api-qc/member/monitor/listMyMonitors'),
      method: 'POST',
      data,
      loading: true
    }),
  // 项目管理新增数据
  addMonitors: async data =>
    http({
      url: getUrl('/api-qc/member/monitor/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // 项目管理修改数据
  monitorModify: async data =>
    http({
      url: getUrl('/api-qc/member/monitor/modify'),
      method: 'POST',
      data,
      loading: true
    }),
  // 项目管理删除数据
  deleteMonitors: async data =>
    http({
      url: getUrl('/api-qc/member/monitor/deleteMonitors'),
      method: 'POST',
      data,
      loading: true
    }),
  // 判异规则获取
  getMonitorSpcParam: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcRuleParam/query'),
      method: 'POST',
      data,
      loading: true
    })
}

// SPC数据管理
export const spcData = {
  // spc历史分析界面查询参数获取
  historyPageCondition: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/historyPageCondition'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc历史界面绘图数据获取
  historyGraphDatas: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/historyGraphDatas'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc历史界面分析数据获取
  historyAnalysisDatas: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/historyAnalysisDatas'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc历史界面选点分析数据
  historyAnalysisDatasFromIds: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/historyAnalysisDatasFromIds'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc数据录入
  spcInputAdd: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc录入界面展示数据获取
  spcListPageData: async data =>
    http({
      url: getUrl('/api-qc/api-ql/spcDataInput/inputPageAnalysisData'),
      method: 'POST',
      data,
      loading: true
    })
}

// SPC标准维护
export const spcStandard = {
  // spc数据删除
  deleteStandards: async data =>
    http({
      url: getUrl('/api-qc/member/standard/deleteStandards'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc标准数据新增
  spcStandardAdd: async data =>
    http({
      url: getUrl('/api-qc/member/standard/add'),
      method: 'POST',
      data,
      loading: true
    }),
  // spc标准数据修改
  spcStandardModify: async data =>
    http({
      url: getUrl('/api-qc/member/standard/modify'),
      method: 'POST',
      data,
      loading: true
    })
}

// 公司物料
export const companyMaterial = {
  deleteById: async data =>
    http({
      url: getUrl('/api-qc/api-ql/CompanyItem/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  save: async data =>
    http({
      url: getUrl('/api-qc/api-ql/CompanyItem/save'),
      method: 'POST',
      data,
      loading: true
    })
}

// 客户关系
export const customRelation = {
  deleteById: async data =>
    http({
      url: getUrl('/api-qc/api-ql/Customer/delete'),
      method: 'POST',
      data,
      loading: true
    }),
  save: async data =>
    http({
      url: getUrl('/api-qc/api-ql/Customer/save'),
      method: 'POST',
      data,
      loading: true
    })
}

// 产线管理
export const lineCode = {
  listAllMyMonitors: async params =>
    http({
      url: getUrl('/api-qc/member/monitor/listAllMyMonitors'),
      method: 'GET',
      params,
      loading: true
    }),
  // 保存
  save: async data =>
    http({
      url: getUrl('/api-qc/api-ql/LineCode/save'),
      method: 'POST',
      data,
      loading: true
    })
}

// 客户料号对应关系
export const relationMaterial = {
  save: async data =>
    http({
      url: getUrl('/api-qc/api-ql/ItemRelation/save'),
      method: 'POST',
      data,
      loading: true
    }),
  deleteById: async data =>
    http({
      url: getUrl('/api-qc/api-ql/ItemRelation/delete'),
      method: 'POST',
      data,
      loading: true
    })
}
