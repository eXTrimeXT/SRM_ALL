// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价
  INQUIRY: {
    // 本位币
    standardCurrency: 'currency',
    // 价格精度
    pricePrecision: 'priceNum',
    // 汇率转换日期
    currencyChangeDate: 'currencyExchangeDate',
    // 描述
    currencyDesc: 'comments'
  },
  // 简易询价[LTS]
  INQUIRY_LTS: {
    // 汇率转换日期
    currencyChangeDate: 'currencyExchangeDate',
    // 描述
    currencyDesc: 'comments'
  },
  // 招标
  BIDING: {},
  // 项目式询价[LTS]
  BIDDING_LTS: {
    // 汇率转换日期
    currencyChangeDate: 'currencyExchangeDate',
    // 描述
    currencyDesc: 'comments'
  },
  // 项目式询价
  BARGAIN: {},
  // 项目式询价[LTS]
  BARGAIN_LTS: {
    // 汇率转换日期
    currencyChangeDate: 'currencyExchangeDate',
    // 描述
    currencyDesc: 'comments'
  },
  // 竞价
  COMPETITION: {
    // 汇率转换日期
    currencyChangeDate: 'currencyExchangeDate'
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
