/* 价格管理 */

import http from '@/utils/axios/http'
const getUrl = path => `${path}`

/* purCat */
export const priceManagementApi = {
  // 上架接口 价格目录列表勾选数据列表数组(价格管理 价格目录)
  putOnShelves: async data => {
    return http({
      url: getUrl('/api-inq/price/priceLibrary/putOnShelves'),
      method: 'POST',
      data
    })
  },
  // 获取上架合同
  getOnShelvesContractList: async data =>
    http({
      url: getUrl('/api-cm/contract/contractHead/getOnShelvesContractList'),
      method: 'POST',
      data,
      loading: true
    })
}
