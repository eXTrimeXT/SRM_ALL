import http from '@/utils/axios/http'
// 流程审批拟稿-进入新建流程页面
// functionId string 功能Id
// businessId string 业务表单ID
// subject string 流程标题
export function initWorkFlow (data) {
  return http({
    url: '/api-base/flow/workFlow/initWorkFlow',
    method: 'POST',
    data: data
  })
}

// 流程审批拟稿-获取审批记录
// fdId string fdId(流程ID)
export function getProcessNodesInfo (params) {
  return http({
    url: '/api-base/flow/workFlow/getProcessNodesInfo',
    method: 'GET',
    params: params,
    loading: true
  })
}

// 获取当前流程信息(包括流程标题、流程状态、流程状态Code，流程可操作列表和)
// fdId string fdId(流程ID)
export function getCurrentProcessInfo (params) {
  return http({
    url: '/api-base/flow/workFlow/getCurrentProcessInfo',
    method: 'GET',
    params: params,
    loading: true
  })
}
// 获取上一次审批人
// businessKey IFlowID(流程模板ID)
export function getPrevProcessApprovers (params) {
  return http({
    url: '/api-base/flow/workFlow/getPrevProcessApprovers',
    method: 'GET',
    params: params,
    loading: true
  })
}

// 获取流程审批意见
// fdId
export function getAuditeNoteList (params) {
  return http({
    url: '/api-base/flow/workFlow/getAuditeNoteList',
    method: 'GET',
    params: params
  })
}

// 驳回节点选择
export function getProcessRefuseNode (params) {
  return http({
    url: '/api-base/flow/workFlow/getProcessRefuseNode',
    method: 'GET',
    params: params
  })
}

// 保存草稿 /api-base/flow/workFlow/saveDraftDirectly
// 保存提交 /api-base/flow/workFlow/approveProcess
export function workFlowHandel (url, data) {
  return http({
    url: url,
    method: 'POST',
    data: data,
    loading: true
  })
}

// 工作台审批 代办
// 待处理 /api-base/workbench/process/findMyRunningProcess?page=1&pageSize=10
// 已处理 /api-base/workbench/process/findMyWorkedProcess?page=1&pageSize=10
// 我启动的流程  /api-base/workbench/process/findMyStartProcess?page=1&pageSize=10
// 抄送给我的流程 /api-base/workbench/process/findSendNodesToMe?page=1&pageSize=10
export function getMyProcess (url, params) {
  return http({
    url: url,
    method: 'GET',
    params: params,
    loading: true
  })
}
export function getMyTaskProcess (url, params) {
  return http({
    url: url,
    method: 'POST',
    data: params
  })
}

// 流程预处理，获取iframe地址，token等等
export function beforeProcess (data) {
  return http({
    url: '/api-base/flow/event/beforeProcess',
    method: 'POST',
    data: data
  })
}
