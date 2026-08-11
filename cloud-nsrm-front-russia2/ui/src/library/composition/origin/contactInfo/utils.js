// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  // 简易询价
  INQUIRY: {},
  // 简易询价[LTS]
  INQUIRY_LTS: {},
  // 招标
  BIDING: {
    // 姓名
    linkman: 'bidContactName',
    // 手机号码
    tel: 'bidMobilePhone',
    // 电子邮箱
    email: 'bidEmail'
  },
  // 招标[LTS]
  BIDDING_LTS: {},
  // 项目式询价
  BARGAIN: {
    // 姓名
    linkman: 'brgContactName',
    // 手机号码
    tel: 'brgMobilePhone',
    // 电子邮箱
    email: 'brgEmail'
  },
  // 项目式询价[LTS]
  BARGAIN_LTS: {},
  // 竞价
  COMPETITION: {},
  // 技术交流
  TECH_EXCHANGE: {
    // 姓名
    linkman: 'linkMan',
    // 手机号码
    tel: 'phone'
  },
  // 寻源需求
  SOURCING: {
    // 姓名
    linkman: 'contactName',
    // 手机号码
    tel: 'phone',
    // 电子邮箱
    email: 'email'
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
