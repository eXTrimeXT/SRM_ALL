import http from '@/utils/axios/http'
import wrapper from 'lib@/utils/axios-cache-plugin'

const $http = wrapper(http, {
  ttl: 60000 * 5
})

// export default function getCompanyDetail(organizationId) {
//   return $http({
//     url: "/api-base/organization/organization/get",
//     method: "GET",
//     params: { organizationId },
//     loading: true
//   });
// }

export default function getCompanyDetail (ouId) {
  return $http({
    url:
      '/api-base/base/org_company/queryContractPartnerByOuId',
    method: 'GET',
    params: { ouId },
    loading: true
  })
}
