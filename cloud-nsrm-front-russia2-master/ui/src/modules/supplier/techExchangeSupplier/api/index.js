/**
 * 技术交流
 */
import http from '@/utils/axios/http'
export const techExchangeSupApi = {
  // 列表查询
  listPageUrl: '/api-sou/technical/exchange/sup/listPage',
  // 撤回反馈
  cancel: id =>
    http({
      url: `/api-sou/technical/exchange/sup/feedback/cancel?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 获取单据详情
  getInfo: id =>
    http({
      url: `/api-sou/technical/exchange/sup/feedback/getInfo?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 保存反馈
  save: data =>
    http({
      url: '/api-sou/technical/exchange/sup/feedback/save',
      method: 'POST',
      data,
      loading: true
    }),
  // 提交反馈
  submit: data =>
    http({
      url: '/api-sou/technical/exchange/sup/feedback/submit',
      method: 'POST',
      data,
      loading: true
    })
}
