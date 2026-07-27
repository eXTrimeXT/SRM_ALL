/**
 * 寻源核心 字典/枚举/魔法值
 * 请注意，使用时候跟实际的数据确认下，这里冗余是为了在页面进行业务逻辑判断，没有使用于业务逻辑判断的不要冗余进来
 */
// 用户类型魔法值，冗余在此
const USER_TYPE_ENUM = {
  BUYER: 'BUYER',
  VENDOR: 'VENDOR'
}

// 业务类型魔法值 简易询价 招标 项目式询价 竞价 技术交流 价格管理
const BUSINESS_TYPE_ENUM = {
  // 简易询价
  INQUIRY: 'INQUIRY',
  // 简易询价[LTS]
  INQUIRY_LTS: 'INQUIRY_LTS',
  // 招标
  BIDING: 'BIDING',
  // 招标[LTS]
  BIDDING_LTS: 'BIDDING_LTS',
  // 项目式询价
  BARGAIN: 'BARGAIN',
  // 项目式询价[LTS]
  BARGAIN_LTS: 'BARGAIN_LTS',
  // 竞价
  COMPETITION: 'COMPETITION',
  // 技术交流
  TECH_EXCHANGE: 'TECH_EXCHANGE',
  // 价格管理
  PRICE: 'PRICE',
  // 寻源需求
  SOURCING: 'SOURCING',
  // 新版竞价
  AUCT: 'AUCT'
}

// 寻源核心-寻源类型 SOU_TYPE
const SOU_TYPE_ENUM = {
  // 研发非生产
  RDNP: 'rdnp',
  // 零部件寻源
  PART: 'part',
  // 项目式询价
  BRG: 'brg',
  // 简易询价
  INQ: 'inq',
  // 招投标
  BID: 'bid'
}

// 寻源定点 - 寻源类型 SOU_CAR_SOU_TYPE
export const SOU_CAR_SOU_TYPE_ENUM = {
  // 研发非生产
  DEV_NOT_PRD: 'DEV_NOT_PRD',
  // 零部件寻源
  PARTS: 'PARTS',
  // 竞价
  COMP: 'COMP'
}

// 寻源核心 - 供应商报价状态魔法值 SOU_ORDER_STATUS
const SOU_ORDER_STATUS_ENUM = {
  // 未投标
  DRAFT: 'DRAFT',
  // 已投标
  SUBMISSION: 'SUBMISSION',
  // 已撤回
  WITHDRAW: 'WITHDRAW',
  // 作废
  CANCEL: 'CANCEL'
}

// 寻源核心 - 询价单据状态 SOU_PROJECT_STATUS
const SOU_PROJECT_STATUS_ENUM = {
  // 拟定
  DRAFT: 'DRAFT',
  // 已作废
  CANCEL: 'CANCEL',
  // 接受报名中(简易询价暂无)
  ACCEPT_SIGN_UP: 'ACCEPT_SIGN_UP',
  // 报名截止(简易询价暂无)
  SIGN_UP_END: 'SIGN_UP_END',
  // 报价未开始
  ORDER_NOT_START: 'ORDER_NOT_START',
  // 接收报价中
  ACCEPT_ORDER: 'ACCEPT_ORDER',
  // 已截止报价
  ORDER_END: 'ORDER_END',
  // 技术评标(简易询价暂无)
  TECH_EVAL: 'TECH_EVAL',
  // 商务评标(简易询价暂无)
  BUSINESS_EVAL: 'BUSINESS_EVAL',
  // 评选中
  EVALUATING: 'EVALUATING',
  // 定价中
  PRICING: 'PRICING',
  // 定价驳回
  PRICE_REJECT: 'PRICE_REJECT',
  // 已定价
  PRICE_END: 'PRICE_END'
}

// 寻源核心 - 邀标方式 SOU_PUBLISH_SCOPE
const SOU_PUBLISH_SCOPE_ENUM = {
  // 邀请招标
  INVITE_TENDER: 'INVITE_TENDER',
  // 公开招标
  OPEN_TENDER: 'OPEN_TENDER'
}

// 寻源核心 - 审批状态 SOU_APPROVAL_STATUS
const SOU_APPROVAL_STATUS_ENUM = {
  // 拟定
  DRAFT: 'DRAFT',
  // 已提交
  SUBMITTED: 'SUBMITTED',
  // 已驳回
  REJECTED: 'REJECTED',
  // 已撤回
  WITHDRAW: 'WITHDRAW',
  // 已废弃
  ABANDONED: 'ABANDONED',
  // 已审批
  APPROVED: 'APPROVED'
}

// 寻源核心 - 评分规则 SOU_SCORE_RULE_TYPE
const SOU_SCORE_RULE_TYPE_ENUM = {
  // 合理低价
  MIN_PRICE: 'MIN_PRICE',
  // 合理高价
  MAX_PRICE: 'MAX_PRICE',
  // 综合评分
  COMPOSITE_PRICE: 'COMPOSITE_PRICE'
}

// 寻源核心 - 报价类型 SOU_ORDER_TYPE
const SOU_ORDER_TYPE_ENUM = {
  // 普通
  SIMPLE: 'SIMPLE',
  // 公式
  FORMULA: 'FORMULA',
  // 模型 准备废弃
  MODEL: 'MODEL',
  // 模版
  TEMPLATE: 'TEMPLATE',
  // 料费分离
  MATERIAL_COST_SEPARATION: 'MATERIAL_COST_SEPARATION'
}

// 寻源核心 - 寻源单据来源的上游类别 SOU_SOURCE_FROM_TYPE
const SOU_SOURCE_FROM_TYPE_ENUM = {
  // 手工创建
  HAND_MAKE: 'HAND_MAKE',
  // 寻源需求
  SOU_REQ: 'SOU_REQ',
  // 采购需求
  PURCHASE_REQ: 'PURCHASE_REQ'
}

// 寻源核心 - 报名状态 SOU_SIGN_UP_STATUS
const SOU_SIGN_UP_STATUS_ENUM = {
  // 未报名
  NO_SIGN_UP: 'NO_SIGN_UP',
  // 确认中
  CONFIRM_ING: 'CONFIRM_ING',
  // 已报名
  SIGN_UP_DONE: 'SIGN_UP_DONE',
  // 已驳回
  REJECTED: 'REJECTED'
}

// 寻源核心 - 决标方式 SOU_ORDER_WAY
const SOU_ORDER_WAY_ENUM = {
  // 单项
  SINGLE: 'SINGLE',
  // 组合
  COMBINED: 'COMBINED'
}

// 寻源核心 - 文件类型 SOU_FILE_CONFIG_TYPE
const SOU_FILE_CONFIG_TYPE_ENUM = {
  // 技术标
  TECH_FILE: 'TECH_FILE',
  // 商务标
  BUSINESS_FILE: 'BUSINESS_FILE'
}

// 寻源核心-技术评分状态
const SOU_TECH_SCORE_STATUS_ENUM = {
  // 未完成
  UNFINISHED: 'UNFINISHED',
  // 已完成
  FINISHED: 'FINISHED'
}

// 寻源核心-评分维度 SOU_SCORE_RULE_DIMENSION
const SOU_SCORE_RULE_DIMENSION_ENUM = {
  // 价格
  PRICE: 'PRICE',
  // 技术
  TECHNOLOGY: 'TECHNOLOGY',
  // 绩效
  ACHIEVEMENT: 'ACHIEVEMENT',
  // 综合
  COMPOSITE: 'COMPOSITE'
}

export {
  USER_TYPE_ENUM,
  BUSINESS_TYPE_ENUM,
  SOU_TYPE_ENUM,
  SOU_ORDER_STATUS_ENUM,
  SOU_PROJECT_STATUS_ENUM,
  SOU_PUBLISH_SCOPE_ENUM,
  SOU_APPROVAL_STATUS_ENUM,
  SOU_SCORE_RULE_TYPE_ENUM,
  SOU_ORDER_TYPE_ENUM,
  SOU_SOURCE_FROM_TYPE_ENUM,
  SOU_SIGN_UP_STATUS_ENUM,
  SOU_ORDER_WAY_ENUM,
  SOU_FILE_CONFIG_TYPE_ENUM,
  SOU_TECH_SCORE_STATUS_ENUM,
  SOU_SCORE_RULE_DIMENSION_ENUM
}

