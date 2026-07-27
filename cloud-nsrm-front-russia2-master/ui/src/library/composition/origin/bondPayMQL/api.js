import http from '@/utils/axios/http'

/**
 * 竞价 - 报价接口
 *
 */

const basePath = '/api-sou/api-ql'

const competion = {
  // 采购商查看保证金缴纳列表（分页）
  listVendorBonds: data =>
    http({
      url: `${basePath}/AuctSouProjectForBuyer/listVendorBonds`,
      method: 'POST',
      data,
      loading: true
    }),
  // 采购商确认/驳回保证金
  confirmBonds: data =>
    http({
      url: `${basePath}/AuctSouProjectForBuyer/confirmBonds`,
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商查看保证金缴纳信息
  listBonds: data =>
    http({
      url: `${basePath}/AuctSouProjectForVendor/listVendorBonds`,
      method: 'POST',
      data,
      loading: true
    }),
  // 供应商暂存/提交保证金
  submitBond: data =>
    http({
      url: `${basePath}/AuctSouProjectForVendor/submitBond`,
      method: 'POST',
      data,
      loading: true
    })
}

export {
  competion as compHttp
}

