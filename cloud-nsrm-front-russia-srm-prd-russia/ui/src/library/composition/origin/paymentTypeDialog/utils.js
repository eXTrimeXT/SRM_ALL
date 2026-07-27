import store from '@/store'
import { BUSINESS_TYPE_ENUM, USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { brgBuyerHttp } from 'modb@/bargain/api'
import { bidBuyerHttp } from 'modb@/bidding/api'
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'

// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价
  [BUSINESS_TYPE_ENUM.INQUIRY]: {},
  // 简易询价[LTS]
  [BUSINESS_TYPE_ENUM.INQUIRY_LTS]: {},
  // 招标
  [BUSINESS_TYPE_ENUM.BIDING]: {},
  // 招标[LTS]
  [BUSINESS_TYPE_ENUM.BIDDING_LTS]: {},
  // 项目式询价
  [BUSINESS_TYPE_ENUM.BARGAIN]: {},
  // 项目式询价[LTS]
  [BUSINESS_TYPE_ENUM.BARGAIN_LTS]: {},
  // 竞价
  [BUSINESS_TYPE_ENUM.COMPETITION]: {},
  // 价格管理
  [BUSINESS_TYPE_ENUM.PRICE]: {},
  // 竞价（新）
  [BUSINESS_TYPE_ENUM.AUCT]: {}
}

/**
 * @description 根据业务类型以及key，返回映射字段
 * @author donghf3
 * @param type 业务类型
 * @param key 可以
 * @returns {*}
 */
export const mappingPropByBusinessTypeAndKey = (type, key) => {
  if (!type) {
    return key
  }
  // 如果找不到配置就返回key
  return businessTypeKeyMap[type][key] || key
}

/**
 * @description 根据业务类型和权限类型返回API
 * @author donghf3
 * @param businessType 业务类型
 * @returns {*|string|string}
 */
export const getApiByBusinessType = (businessType) => {
  const apiMap = new Map([
    // 简易询价
    [
      BUSINESS_TYPE_ENUM.INQUIRY,
      '/api-inq/quote/quoteHeader/getQuoteItemPayments'
    ],
    // 简易询价[LTS]
    [
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      inqBuyerHttp.order.getOrderItemPaymentsUrl
    ],
    // 招标
    [
      BUSINESS_TYPE_ENUM.BIDING,
      '/api-bid/supplierCooperate/orderHead/getOrderLinePayments'
    ],
    // 招标[LTS]
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        [USER_TYPE_ENUM.BUYER]: bidBuyerHttp.order.getOrderPaymentsUrl,
        [USER_TYPE_ENUM.VENDOR]: bidSupplierHttp.order.getOrderPaymentsUrl
      }
    ],
    // 项目式询价
    [
      BUSINESS_TYPE_ENUM.BARGAIN,
      '/api-brg/supplierCooperate/orderHead/getOrderLinePayments'
    ],
    // 项目式询价[LTS]
    [
      BUSINESS_TYPE_ENUM.BARGAIN_LTS,
      {
        [USER_TYPE_ENUM.BUYER]: brgBuyerHttp.order.getOrderPaymentsUrl,
        [USER_TYPE_ENUM.VENDOR]: brgSupplierHttp.order.getOrderPaymentsUrl
      }
    ],
    // 竞价
    [
      BUSINESS_TYPE_ENUM.COMPETITION,
      '/api-comp/supplierCooperate/getComp/orderPayments'
    ],
    // 价格管理
    [
      BUSINESS_TYPE_ENUM.PRICE,
      '/api-inq/quote/quoteHeader/getQuoteItemPayments'
    ]
  ])
  const apiMapData = apiMap.get(businessType)
  // 如果不是字符串，就按当前用户类型判断
  return apiMapData && typeof apiMapData === 'string' ? apiMapData : apiMapData[store.getters.userType]
}

/**
 * @description 根据业务类型和报价类型，以及行数据，返回查询参数
 * @author donghf3
 * @param businessType
 * @param rowObj
 * @returns {{inquiryItemId, quoteItemId: *}|{}}
 */
export const getQueryParamsByBusinessType = (businessType, rowObj = {}) => {
  const paramsMap = new Map([
    // 简易询价
    [
      BUSINESS_TYPE_ENUM.INQUIRY,
      { inquiryItemId: rowObj.inquiryItemId, quoteItemId: rowObj.quoteItemId }
    ],
    // 简易询价[LTS]
    [
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      { souItemId: rowObj.souItemId, orderItemId: rowObj.orderItemId }
    ],
    // 招标
    [
      BUSINESS_TYPE_ENUM.BIDING,
      { requirementLineId: rowObj.requirementLineId, orderLineId: rowObj.orderLineId }
    ],
    // 招标[LTS]
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      { orderItemId: rowObj.orderItemId, souItemId: rowObj.souItemId }
    ],
    // 项目式询价
    [
      BUSINESS_TYPE_ENUM.BARGAIN,
      { requirementLineId: rowObj.requirementLineId, orderLineId: rowObj.orderLineId }
    ],
    // 项目式询价[LTS]
    [
      BUSINESS_TYPE_ENUM.BARGAIN_LTS,
      { orderItemId: rowObj.orderItemId, souItemId: rowObj.souItemId }
    ],
    // 竞价
    [
      BUSINESS_TYPE_ENUM.COMPETITION,
      { orderLineId: rowObj.orderLineId }
    ],
    // 价格管理
    [
      BUSINESS_TYPE_ENUM.PRICE,
      { inquiryItemId: rowObj.inquiryItemId, quoteItemId: rowObj.quoteItemId }
    ]
  ])
  return paramsMap.get(businessType) || {}
}
