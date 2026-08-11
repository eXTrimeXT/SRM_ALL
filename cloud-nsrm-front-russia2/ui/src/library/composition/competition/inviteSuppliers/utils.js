// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价
  INQUIRY: {
    // 预计数量
    quantity: 'demandQuantity',
    // 物料ID
    targetId: 'itemId',
    // 物料编码
    targetNum: 'itemCode',
    // 物料名称
    targetDesc: 'itemDesc',
    // 权限list
    authList: 'quoteAuthList',
    // 物料需求ID
    requirementLineId: 'inquiryItemId',
    // 联系人
    linkManName: 'linkMan'
  },
  // 简易询价[LTS]
  INQUIRY_LTS: {
    // 预计数量
    quantity: 'requireQuantity',
    // 物料ID
    targetId: 'itemId',
    // 物料编码
    targetNum: 'itemCode',
    // 物料名称
    targetDesc: 'itemDesc',
    // 物料需求ID
    requirementLineId: 'souItemId',
    // 联系人
    linkManName: 'linkmanName',
    // 禁止报价
    quoteForbid: 'forbidPrice'
  },
  // 招标
  BIDING: {},
  // 招标[LTS]
  BIDDING_LTS: {
    // 预计数量
    quantity: 'requireQuantity',
    // 物料ID
    targetId: 'itemId',
    // 物料编码
    targetNum: 'itemCode',
    // 物料名称
    targetDesc: 'itemDesc',
    // 物料需求ID
    requirementLineId: 'souItemId',
    // 联系人
    linkManName: 'linkmanName',
    // 禁止报价
    quoteForbid: 'forbidPrice'
  },
  // 项目式询价
  BARGAIN: {},
  // 项目式询价[LTS]
  BARGAIN_LTS: {
    // 预计数量
    quantity: 'requireQuantity',
    // 物料ID
    targetId: 'itemId',
    // 物料编码
    targetNum: 'itemCode',
    // 物料名称
    targetDesc: 'itemDesc',
    // 物料需求ID
    requirementLineId: 'souItemId',
    // 联系人
    linkManName: 'linkmanName',
    // 禁止报价
    quoteForbid: 'forbidPrice'
  },
  // 竞价
  COMPETITION: {
    // 预计数量
    quantity: 'requireQuantity',
    // 物料ID
    targetId: 'itemId',
    // 物料编码
    targetNum: 'itemCode',
    // 物料名称
    targetDesc: 'itemDesc',
    // 物料需求ID
    requirementLineId: 'souItemId',
    // 联系人
    linkManName: 'linkmanName',
    // 禁止报价
    quoteForbid: 'forbidPrice'
  },
  // 技术交流
  TECH_EXCHANGE: {
    // 联系人
    linkManName: 'linkMan'
  },
  // 寻源需求
  SOURCING: {
    // 联系人
    linkManName: 'contactName'
  }
}

// 根据业务类型以及key，返回映射字段
export const mappingPropByBusinessTypeAndKey = (type, key) => {
  // 如果找不到配置就返回key
  return businessTypeKeyMap[type][key] || key
}
