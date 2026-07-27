/**
 * 寻源核心 字典/枚举/魔法值
 * 请注意，使用时候跟实际的数据确认下，这里冗余是为了在页面进行业务逻辑判断，没有使用于业务逻辑判断的不要冗余进来
 */

// 业务类型魔法值 简易询价 招标 项目式询价 竞价 技术交流 价格管理
export enum BUSINESS_TYPE_ENUM {
  // 简易询价
  INQUIRY = 'INQUIRY',
  // 简易询价[LTS]
  INQUIRY_LTS = 'INQUIRY_LTS',
  // 招标
  BIDING = 'BIDING',
  // 招标[LTS]
  BIDDING_LTS = 'BIDDING_LTS',
  // 项目式询价
  BARGAIN = 'BARGAIN',
  // 项目式询价[LTS]
  BARGAIN_LTS = 'BARGAIN_LTS',
  // 竞价
  COMPETITION = 'COMPETITION',
  // 技术交流
  TECH_EXCHANGE = 'TECH_EXCHANGE',
  // 价格管理
  PRICE = 'PRICE',
  // 寻源需求
  SOURCING = 'SOURCING'
}

// 寻源核心-寻源类型 SOU_TYPE
export enum SOU_TYPE_ENUM {
  // 研发非生产
  RDNP = 'rdnp',
  // 零部件寻源
  PART = 'part',
  // 项目式询价
  BRG = 'brg',
  // 简易询价
  INQ = 'inq',
  // 招投标
  BID = 'bid'
}

// 寻源核心 - 评分规则 SOU_SCORE_RULE_TYPE
export enum SOU_SCORE_RULE_TYPE_ENUM {
  // 合理低价
  MIN_PRICE = 'MIN_PRICE',
  // 合理高价
  MAX_PRICE = 'MAX_PRICE',
  // 综合评分
  COMPOSITE_PRICE = 'COMPOSITE_PRICE'
}

// 寻源核心 - 报价类型 SOU_ORDER_TYPE
export enum SOU_ORDER_TYPE_ENUM {
  // 普通
  SIMPLE = 'SIMPLE',
  // 公式
  FORMULA = 'FORMULA',
  // 模型 准备废弃
  MODEL = 'MODEL',
  // 模版
  TEMPLATE = 'TEMPLATE'
}

// 寻源核心 - 审批状态 SOU_APPROVAL_STATUS
export enum SOU_APPROVAL_STATUS_ENUM {
  // 拟定
  DRAFT = 'DRAFT',
  // 已提交
  SUBMITTED = 'SUBMITTED',
  // 已驳回
  REJECTED = 'REJECTED',
  // 已撤回
  WITHDRAW = 'WITHDRAW',
  // 已废弃
  ABANDONED = 'ABANDONED',
  // 已审批
  APPROVED = 'APPROVED'
}

// 寻源核心 - 询价单据状态 SOU_PROJECT_STATUS
export enum SOU_PROJECT_STATUS_ENUM {
  // 拟定
  DRAFT = 'DRAFT',
  // 已作废
  CANCEL = 'CANCEL',
  // 接受报名中(简易询价暂无)
  ACCEPT_SIGN_UP = 'ACCEPT_SIGN_UP',
  // 报名截止(简易询价暂无)
  SIGN_UP_END = 'SIGN_UP_END',
  // 报价未开始
  ORDER_NOT_START = 'ORDER_NOT_START',
  // 接收报价中
  ACCEPT_ORDER = 'ACCEPT_ORDER',
  // 已截止报价
  ORDER_END = 'ORDER_END',
  // 技术评标(简易询价暂无)
  TECH_EVAL = 'TECH_EVAL',
  // 商务评标(简易询价暂无)
  BUSINESS_EVAL = 'BUSINESS_EVAL',
  // 评选中
  EVALUATING = 'EVALUATING',
  // 定价中
  PRICING = 'PRICING',
  // 定价驳回
  PRICE_REJECT = 'PRICE_REJECT',
  // 已定价
  PRICE_END = 'PRICE_END'
}

// 寻源核心 - 供应商报价状态 SOU_ORDER_STATUS
export enum SOU_ORDER_STATUS_ENUM {
  // 未投标
  DRAFT = 'DRAFT',
  // 已投标
  SUBMISSION = 'SUBMISSION',
  // 已撤回
  WITHDRAW = 'WITHDRAW',
  // 作废
  CANCEL = 'CANCEL'
}

// 寻源核心 - 邀标方式 SOU_PUBLISH_SCOPE
export enum SOU_PUBLISH_SCOPE_ENUM {
  // 邀请招标
  INVITE_TENDER = 'INVITE_TENDER',
  // 公开招标
  OPEN_TENDER = 'OPEN_TENDER'
}
