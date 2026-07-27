import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/RecommvendorProject'

const method = 'POST'

export default {
  listPageUrl: `${basePath}/query`,
  queryRisk: data =>
    http({
      url: '/api-sou/api-ql/Recommvendor/queryRisk',
      method,
      data,
      loading: true
    }),
  delete: data =>
    http({
      url: `${basePath}/delete`,
      method,
      data,
      loading: true
    }),
  save: data =>
    http({
      url: `${basePath}/save`,
      method,
      data,
      loading: true
    }),
  submit: data =>
    http({
      url: `${basePath}/submit`,
      method,
      data,
      loading: true
    }),
  read: data =>
    http({
      url: `${basePath}/read`,
      method,
      data,
      loading: true
    }),
  chartInfo: data =>
    http({
      url: '/api-sou/api-ql/Recommvendor/queryHisScore',
      method,
      data,
      loading: true
    }),
  vendorUpdate: data =>
    http({
      url: '/api-sou/api-ql/Recommvendor/vendorUpdate',
      method,
      data,
      loading: true
    }),
  countRecomm: (data = {}) =>
    http({
      url: '/api-sou/require/countRecomm',
      method,
      data,
      loading: true
    }),
  // 不公示 - 发起供应商推荐
  createVendorRecommend: async data =>
    http({
      url: '/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/createVendorRecommend',
      method: 'POST',
      data,
      loading: true
    }),
  // 公示 - 发起供应商推荐
  createSouVendorRecommend: data =>
    http({
      url: '/api-sou/api-ql/SouReqApplyBuyer/createVendorRecommend',
      method,
      data,
      loading: true
    }),
  // 从标前交流获取
  vendorUpdateAsPreBid: data =>
    http({
      url: '/api-sou/api-ql/Recommvendor/vendorUpdateAsPreBid',
      method,
      data,
      loading: true
    })
}
