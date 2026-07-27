
import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/BidNoticeAbandon'

export default {
  // 列表查询
  listPageUrl: `${basePath}/query`,
  // 删除
  delete: data =>
    http({
      url: `${basePath}/delete`,
      method: 'POST',
      data,
      loading: true
    }),
  // 废弃
  abandon: data =>
    http({
      url: `${basePath}/abandon`,
      method: 'POST',
      data,
      loading: true
    }),
  // 查询中落标通知详情
  originalRead: data =>
    http({
      url: '/api-sou/api-ql/BidNotice/read',
      method: 'POST',
      data,
      loading: true
    }),
  // 查询详情
  read: data =>
    http({
      url: `${basePath}/read`,
      method: 'POST',
      data,
      loading: true
    }),
  // 保存
  save: data =>
    http({
      url: `${basePath}/save`,
      method: 'POST',
      data,
      loading: true
    }),
  // 提交
  submit: data =>
    http({
      url: `${basePath}/submit`,
      method: 'POST',
      data,
      loading: true
    })

}
