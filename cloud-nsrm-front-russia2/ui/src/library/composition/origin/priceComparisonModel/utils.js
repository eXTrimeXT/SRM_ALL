import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'

export const getApiByBusinessType = (businessType) => {
  const map = new Map([
    [
      // 简易询价
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      '/api-sou/buyer/inq/select/souTempSelectReport'
    ],
    [
      // 招标
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      '/api-sou/buyer/bid/select/souTempSelectReport'
    ]
  ])
  return map.get(businessType)
}

export const getFeildByBuinessTypeAndField = (businessType,field) => {
  const map = new Map([
    [
      // 简易询价
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      {
        'notaxTargetPrice': 'notaxTargetPrice'
      }
    ],
    [
      // 招标
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        'notaxTargetPrice': 'targetPrice'
      }
    ]
  ])
  return map.get(businessType)[field]
}