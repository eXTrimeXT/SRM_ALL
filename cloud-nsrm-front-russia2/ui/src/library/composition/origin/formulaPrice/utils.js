// 根据业务类型，映射字段
import { BUSINESS_TYPE_ENUM, USER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { brgBuyerHttp } from 'modb@/bargain/api'
import { bidBuyerHttp } from 'modb@/bidding/api'
import { inqSupplierHttp } from 'mods@/inquirySupplier/api'
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'
import store from '@/store'

export const businessTypeKeyMap = {
  // 简易询价[LTS]
  [BUSINESS_TYPE_ENUM.INQUIRY_LTS]: {
    formulaResult: 'formulaValue'
  },
  // 招标[LTS]
  [BUSINESS_TYPE_ENUM.BIDDING_LTS]: {
    formulaResult: 'formulaValue'
  },
  // 项目式询价[LTS]
  [BUSINESS_TYPE_ENUM.BARGAIN_LTS]: {
    formulaResult: 'formulaValue'
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
 * @description 根据业务类型，api类型，以及用户角色，返回API
 * @author donghf3
 * @param type
 * @param apiType
 * @returns {*}
 */
export const getApiByBusinessType = (type, apiType) => {
  const map = new Map([
    // 简易询价[LTS]
    [
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      {
        // 查询
        get: {
          [USER_TYPE_ENUM.BUYER]: inqBuyerHttp.order.getOrderFormulaPricesUrl,
          [USER_TYPE_ENUM.VENDOR]: inqSupplierHttp.order.getOrderFormulaPricesUrl
        },
        // 计算
        compute: {
          [USER_TYPE_ENUM.BUYER]: inqBuyerHttp.order.getComputeFormulaPriceUrl,
          [USER_TYPE_ENUM.VENDOR]: inqSupplierHttp.order.getComputeFormulaPriceUrl
        }
      }
    ],
    // 招标[LTS]
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        // 查询
        get: {
          [USER_TYPE_ENUM.BUYER]: bidBuyerHttp.order.getOrderFormulaPricesUrl,
          [USER_TYPE_ENUM.VENDOR]: bidSupplierHttp.order.getOrderFormulaPricesUrl
        },
        // 计算
        compute: {
          [USER_TYPE_ENUM.BUYER]: bidBuyerHttp.order.computeFormulaPriceUrl,
          [USER_TYPE_ENUM.VENDOR]: bidSupplierHttp.order.computeFormulaPriceUrl
        }
      }
    ],
    // 项目式询价[LTS]
    [
      BUSINESS_TYPE_ENUM.BARGAIN_LTS,
      {
        // 查询
        get: {
          [USER_TYPE_ENUM.BUYER]: brgBuyerHttp.order.getOrderFormulaPricesUrl,
          [USER_TYPE_ENUM.VENDOR]: brgSupplierHttp.order.getOrderFormulaPricesUrl
        },
        // 计算
        compute: {
          [USER_TYPE_ENUM.BUYER]: brgBuyerHttp.order.computeFormulaPriceUrl,
          [USER_TYPE_ENUM.VENDOR]: brgSupplierHttp.order.computeFormulaPriceUrl
        }
      }
    ]
  ])
  return map.get(type)[apiType][store.getters.userType]
}
