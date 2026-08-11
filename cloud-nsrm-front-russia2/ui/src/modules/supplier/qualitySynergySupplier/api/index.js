/* 供应商质量质量 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const excHandlingNotice = {
  incomingExceptionDelete: async params =>
    http({
      url: getUrl('/api-pef/itemExceptionHandle/delete'),
      method: 'GET',
      params,
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
  // 异常问题处理通知
// 查询业务实体
  listOrgByUserAndCompany: async companyId =>
    http({
      url: getUrl('/api-sup/info/orgCategory/listOrgByUserAndCompany'),
      method: 'GET',
      params: { companyId },
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
    })
}

// 检验标准
export const inspectionStandard = {
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
}
