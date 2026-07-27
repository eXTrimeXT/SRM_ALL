/**
 * 会议管理API
 */
import http from '@/utils/axios/http'

// 采购商端
export const buyer = {
  // 议题模板
  model: {
    // 列表查询
    listPageUrl: '/api-soucar/meet/meetmodel/listPage',
    // 新增议题
    submit: data =>
      http({
        url: '/api-soucar/meet/meetmodel/submit',
        method: 'POST',
        data,
        loading: true
      }),
    // 查询议题
    getModelInfo: params =>
      http({
        url: '/api-soucar/meet/meetmodel/get',
        method: 'GET',
        params,
        loading: true
      }),
    // 禁用模板
    disabledModel: id =>
      http({
        url: `/api-soucar/meet/meetmodel/disable?modelId=${id}`,
        method: 'POST',
        loading: true
      }),
    // 禁用模板
    enableModel: id =>
      http({
        url: `/api-soucar/meet/meetmodel/enable/?modelId=${id}`,
        method: 'POST',
        loading: true
      })
  },
  // 议题管理
  topics: {
    // 列表查询
    listPageUrl: '/api-soucar/meet/meettopic/listPage',
    // 删除议题
    delete: data =>
      http({
        url: '/api-soucar/meet/meettopic/delete',
        method: 'POST',
        data,
        loading: true
      }),
    // 驳回议题
    reject: data =>
      http({
        url: '/api-soucar/meet/meettopic/rejected',
        method: 'POST',
        data,
        loading: true
      })
  }
}
