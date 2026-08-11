/**
 * 寻源方式枚举ENUM
*/
export const PRICE_APPROVAL_FROM_TYPE_ENUM = {
  INQ: 'inq', // 简易询价
  BID: 'bid', // 招标
  BRG: 'brg', //  项目式询价
  COMP: 'comp', // 竞价
  AUCT: 'auct', // 竞价(MQL)
  HANDMAKE: 'hand_make' // 手工创建
}

// 寻源方式新到旧的映射方式
export const mapToSourceType = {
  'inq': 'INQUIRY',
  'bid': 'BIDING',
  'brg': 'BARGAIN',
  'comp': 'COMPETITION',
  'auct': 'AUCT',
  'hand_make': 'HAND_MAKE'
}
