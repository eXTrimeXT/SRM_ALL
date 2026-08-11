/**
 * 技术交流
 */
import http from '@/utils/axios/http'

// 采购商端
export const buyer = {
  techExchangeManagement: {
    // 列表查询
    listPageUrl: '/api-bid/technical/exchange/listPage',
    feedbackListPageUrl: '/api-bid/technical/exchange/feedback/listPage',
    // 查看供应商反馈
    feedbackListPage: data =>
      http({
        url: `/api-bid/technical/exchange/feedback/listPage?tecExcId=${data.tecExcId}`,
        method: 'POST',
        data,
        loading: true
      }),
    // 删除单据
    delete: id =>
      http({
        url: `/api-bid/technical/exchange/delete?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 取消单据
    cancel: id =>
      http({
        url: `/api-bid/technical/exchange/cancel?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 结束单据
    closure: id =>
      http({
        url: `/api-bid/technical/exchange/closure?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 获取单据详情
    getExcInfo: id =>
      http({
        url: `/api-bid/technical/exchange/getExcInfo?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 保存单据
    save: data =>
      http({
        url: '/api-bid/technical/exchange/save',
        method: 'POST',
        data,
        loading: true
      }),
    // 提交单据
    submit: data =>
      http({
        url: '/api-bid/technical/exchange/submit',
        method: 'POST',
        data,
        loading: true
      })
  }
}

// 供应商端
export const supplier = {
  techExchangeManagement: {
    // 列表查询
    listPageUrl: '/api-bid/technical/exchange/sup/listPage',
    // 撤回反馈
    cancel: id =>
      http({
        url: `/api-bid/technical/exchange/sup/feedback/cancel?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 获取单据详情
    getInfo: id =>
      http({
        url: `/api-bid/technical/exchange/sup/feedback/getInfo?tecExcId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 保存反馈
    save: data =>
      http({
        url: '/api-bid/technical/exchange/sup/feedback/save',
        method: 'POST',
        data,
        loading: true
      }),
    // 提交反馈
    submit: data =>
      http({
        url: '/api-bid/technical/exchange/sup/feedback/submit',
        method: 'POST',
        data,
        loading: true
      })
  }
}
