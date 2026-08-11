import http from '@/utils/axios/http'

const basePath = '/api-sou/api-ql/SouInviteHeadBuyer'
const histBasePath = '/api-sou/api-ql/SouInviteHistoryBuyer'
const itemBasePath = '/api-sou/api-ql/SouInviteItemBuyer'

const method = 'POST'

export default {
  listPageUrl: `${basePath}/query`,
  irListPageUrl: `${basePath}/listPage`,
  publicInvite: data =>
    http({
      url: `${histBasePath}/publicInvite`,
      method,
      data,
      loading: true
    }),
  notPublicInvite: data =>
    http({
      url: `${histBasePath}/notPublicInvite`,
      method,
      data,
      loading: true
    }),
  hisListPageUrl: `${histBasePath}/query`,
  itemListPageUrl: `${itemBasePath}/query`
}
