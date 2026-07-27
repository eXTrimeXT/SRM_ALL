import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/BidPrice'

export default {
  listPageUrl: `${basePath}/query`,
  get: data =>
    http({
      url: `${basePath}/edit`,
      method: 'POST',
      data,
      loading: true
    }),
  save: data =>
    http({
      url: `${basePath}/save`,
      method: 'POST',
      data,
      loading: true
    }),
  delete: data =>
    http({
      url: `${basePath}/delete`,
      method: 'POST',
      data,
      loading: true
    })
}
