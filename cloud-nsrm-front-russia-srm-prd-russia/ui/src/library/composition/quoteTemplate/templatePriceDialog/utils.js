import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { templatePrice as inqTemplatePrice } from '@/service/modules/inq'
import { templatePrice as bidTemplatePrice } from '@/service/modules/bid'

// 根据业务类型和报价类型，返回API
export const getApiByBusinessType = type => {
  const map = new Map([
    // 简易询价
    [
      BUSINESS_TYPE_ENUM.INQUIRY,
      inqTemplatePrice.getOrSaveTempDataUrl
    ],
    // 招标
    [
      BUSINESS_TYPE_ENUM.BIDING,
      bidTemplatePrice.getOrSaveTempDataUrl
    ]
  ])
  return map.get(type)
}
