/**
 * 审批流 API
 */
import http from '@/utils/axios/http'
const getUrl = path => `${path}`

export const approvalApi = {
  // 预执行
  nextStep: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/predict'),
      method: 'POST',
      data,
      loading: true
    }),
  // 提交、下一步
  submit: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/submitEngine'),
      method: 'POST',
      data,
      loading: true
    }),
  // 退回、退回直达、退回上一级
  return: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/rollBackTask'),
      method: 'POST',
      data,
      loading: true
    }),
  // 转办
  transfer: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/forwardTask'),
      method: 'POST',
      data,
      loading: true
    }),
  // 废弃
  abandon: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/destory'),
      method: 'POST',
      data,
      loading: true
    }),
  // 审批通过
  pass: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/pass'),
      method: 'POST',
      data,
      loading: true
    }),
  // 获取审批记录
  getApprovalRecord: async params =>
    http({
      url: getUrl('/api-pj/bpmFlow/findTaskListNew'),
      method: 'GET',
      params,
      loading: true
    }),
  /* 流程撤回 */
  recall: data =>
    http({
      url: '/api-pj/external/bpm/rollBackAll',
      method: 'POST',
      data,
      loading: true
    }),
  // 结束
  end: async data =>
    http({
      url: getUrl('/api-pj/bpmFlow/end'),
      method: 'POST',
      data,
      loading: true
    })
}
