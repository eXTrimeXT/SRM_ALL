/**
 * 技术交流
 */
import http from '@/utils/axios/http'
export const techExchangeBuyerApi = {
  // 列表查询
  listPageUrl: '/api-sou/technical/exchange/listPage',
  feedbackListPageUrl: '/api-sou/technical/exchange/feedback/listPage',
  // 查看供应商反馈
  feedbackListPage: data =>
    http({
      url: `/api-sou/technical/exchange/feedback/listPage?tecExcId=${data.tecExcId}`,
      method: 'POST',
      data,
      loading: true
    }),
  // 删除单据
  delete: id =>
    http({
      url: `/api-sou/technical/exchange/delete?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 取消单据
  cancel: id =>
    http({
      url: `/api-sou/technical/exchange/cancel?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 结束单据
  closure: id =>
    http({
      url: `/api-sou/technical/exchange/closure?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 获取单据详情
  getExcInfo: id =>
    http({
      url: `/api-sou/technical/exchange/getExcInfo?tecExcId=${id}`,
      method: 'POST',
      loading: true
    }),
  // 保存单据
  save: data =>
    http({
      url: '/api-sou/technical/exchange/save',
      method: 'POST',
      data,
      loading: true
    }),
  // 提交单据
  submit: data =>
    http({
      url: '/api-sou/technical/exchange/submit',
      method: 'POST',
      data,
      loading: true
    })
}
