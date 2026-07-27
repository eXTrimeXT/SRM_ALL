import { SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { brgBuyerHttp } from 'modb@/bargain/api'
import { bidBuyerHttp } from 'modb@/bidding/api'

// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价[LTS]
  INQUIRY_LTS: {
    // 普通报价
    SIMPLE_PRICING: SOU_ORDER_TYPE_ENUM.SIMPLE,
    // 公式报价
    FORMULA_PRICING: SOU_ORDER_TYPE_ENUM.FORMULA
  },
  // 招标[LTS]
  BIDDING_LTS: {
    // 普通报价
    SIMPLE_PRICING: SOU_ORDER_TYPE_ENUM.SIMPLE,
    // 公式报价
    FORMULA_PRICING: SOU_ORDER_TYPE_ENUM.FORMULA,
    // 模型报价
    MODEL_PRICING: SOU_ORDER_TYPE_ENUM.MODEL
  },
  // 项目式询价[LTS]
  BARGAIN_LTS: {
    // 普通报价
    SIMPLE_PRICING: SOU_ORDER_TYPE_ENUM.SIMPLE,
    // 公式报价
    FORMULA_PRICING: SOU_ORDER_TYPE_ENUM.FORMULA,
    // 模型报价
    MODEL_PRICING: SOU_ORDER_TYPE_ENUM.MODEL
  }
}

/**
 * @description 根据业务类型以及key，返回映射字段
 * @author donghf3
 * @param type
 * @param key
 * @returns {*}
 */
export const mappingPropByBusinessTypeAndKey = (type, key) => businessTypeKeyMap[type][key] || key

/**
 * @description 根据业务类型和报价类型，返回API
 * @author donghf3
 * @param businessType
 * @param pricingType
 * @returns {*}
 */
export const getApiByBusinessTypeAndPricingType = (businessType, pricingType) => {
  const API_MAP = {
    // 简易询价[LTS]
    INQUIRY_LTS: {
      [SOU_ORDER_TYPE_ENUM.SIMPLE]: inqBuyerHttp.select.getSimplePriceCompareUrl,
      [SOU_ORDER_TYPE_ENUM.FORMULA]: inqBuyerHttp.select.getFormulaPriceCompareUrl
    },
    // 招标[LTS]
    BIDDING_LTS: {
      [SOU_ORDER_TYPE_ENUM.SIMPLE]: bidBuyerHttp.select.getSimplePriceCompareUrl,
      [SOU_ORDER_TYPE_ENUM.FORMULA]: bidBuyerHttp.select.getFormulaPriceCompareUrl
    },
    // 项目式询价[LTS]
    BARGAIN_LTS: {
      [SOU_ORDER_TYPE_ENUM.SIMPLE]: brgBuyerHttp.select.getSimplePriceCompareUrl,
      [SOU_ORDER_TYPE_ENUM.FORMULA]: brgBuyerHttp.select.getFormulaPriceCompareUrl
    }
  }
  return API_MAP[businessType][pricingType]
}
