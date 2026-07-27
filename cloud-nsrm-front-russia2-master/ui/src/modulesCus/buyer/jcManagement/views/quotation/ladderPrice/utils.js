// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价
  INQUIRY: {
    targetNum: 'itemCode',
    targetDesc: 'itemDesc',
    quantity: 'demandQuantity',
    taxPrice: 'ldTaxPrice'
  },
  // 简易询价[LTS]
  INQUIRY_LTS: {
    targetNum: 'itemCode',
    targetDesc: 'itemDesc',
    quantity: 'requireQuantity',
    price: 'orderNotaxPrice',
    taxPrice: 'orderTaxPrice',
    ladderPriceTable: 'ladderPriceList'
  },
  // 招标
  BIDING: {},
  // 招标[LTS]
  BIDDING_LTS: {
    targetNum: 'itemCode',
    targetDesc: 'itemDesc',
    quantity: 'requireQuantity',
    price: 'orderNotaxPrice',
    taxPrice: 'orderTaxPrice',
    ladderPriceTable: 'ladderPriceList'
  },
}

/**
 * @description 根据业务类型以及key，返回映射字段
 * @author donghf3
 * @param type
 * @param key
 * @returns {*}
 */
export const mappingPropByBusinessTypeAndKey = (type, key) => businessTypeKeyMap[type][key] || key
