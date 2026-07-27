// 根据业务类型，映射字段
import { BUSINESS_TYPE_ENUM } from '@/library/composition/origin/enum'
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'
import { brgBuyerHttp } from 'modb@/bargain/api'
import { bidBuyerHttp } from 'modb@/bidding/api'

export const businessTypeKeyMap = {
  // 简易询价
  INQUIRY: {},
  // 简易询价[LTS]
  INQUIRY_LTS: {},
  // 招标
  BIDING: {},
  // 招标[LTS]
  BIDDING_LTS: {
    bondEndDatetime: 'bondEndTime'
  },
  // 项目式询价
  BARGAIN: {},
  // 项目式询价[LTS]
  BARGAIN_LTS: {
    bondEndDatetime: 'bondEndTime'
  },
  // 竞价
  COMPETITION: {
    bondEndDatetime: 'bondEndTime'
  }
}

/**
 * @description 根据业务类型以及key，返回映射字段
 * @author donghf3
 * @param type
 * @param key
 * @returns {*}
 */
export const mappingPropByBusinessTypeAndKey = (type, key) => {
  if (!type) {
    return key
  }
  return businessTypeKeyMap[type][key] || key
}

/**
 * @description 保证金管理 根据业务类型，行数据以及单据基础信息，返回api参数
 * @author donghf3
 * @param businessType 业务类型
 * @param baseInfo 单据基础信息
 * @param rowObj 行数据
 * @returns {{submitUrl: string, queryUrl: string, queryParams: {}, submitParams: {vendorIds: string[], hasPay: (*|string)}}|{}}
 */
export const getBondManagementApiParams = (businessType, baseInfo, rowObj) => {
  const paramsMap = new Map([
    // 招标[LTS]
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        queryUrl: bidBuyerHttp.bond.getQueryBondsUrl(baseInfo.id),
        queryParams: {},
        submitUrl: bidBuyerHttp.bond.confirmUrl,
        submitParams: {
          [baseInfo.idKey]: baseInfo.id,
          vendorIds: [rowObj.vendorId || ''],
          hasPay: rowObj.hasPay || ''
        }
      }
    ],
    // 项目式询价[LTS]
    [
      BUSINESS_TYPE_ENUM.BARGAIN_LTS,
      {
        queryUrl: brgBuyerHttp.bond.getQueryBondsUrl(baseInfo.id),
        queryParams: {},
        submitUrl: brgBuyerHttp.bond.confirmUrl,
        submitParams: {
          [baseInfo.idKey]: baseInfo.id,
          vendorIds: [rowObj.vendorId || ''],
          hasPay: rowObj.hasPay || ''
        }
      }
    ]
  ])
  return paramsMap.get(businessType) || {}
}

/**
 * @description 供应商缴纳保证金 根据业务类型以及单据基础信息，返回api参数
 * @author donghf3
 * @param businessType 业务类型
 * @param baseInfo 单据基础信息
 * @returns {{submitUrl: string, queryUrl: string, queryParams: {}, submitParams: {vendorIds: string[], hasPay: (*|string)}}|{}}
 */
export const getBondPayApiParams = (businessType, baseInfo) => {
  const paramsMap = new Map([
    // 招标[LTS]
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        queryUrl: bidSupplierHttp.bond.getBondUrl(baseInfo.id),
        queryParams: {},
        submitUrl: bidSupplierHttp.bond.submitBond,
        submitParams: { [baseInfo.idKey]: baseInfo.id }
      }
    ],
    // 项目式询价[LTS]
    [
      BUSINESS_TYPE_ENUM.BARGAIN_LTS,
      {
        queryUrl: brgSupplierHttp.bond.getBondUrl(baseInfo.id),
        queryParams: {},
        submitUrl: brgSupplierHttp.bond.submitBond,
        submitParams: { [baseInfo.idKey]: baseInfo.id }
      }
    ]
  ])
  return paramsMap.get(businessType) || {}
}
