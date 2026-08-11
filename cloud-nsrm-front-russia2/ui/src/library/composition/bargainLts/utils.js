/**
 * @description 项目式询价 composition
 * @author donghf3
 */
import {
  SOU_PROJECT_STATUS_ENUM,
  SOU_ORDER_STATUS_ENUM,
  SOU_APPROVAL_STATUS_ENUM,
  SOU_SIGN_UP_STATUS_ENUM
} from 'lib@/composition/origin/enum'

// 绑定的流程模板
export const WORKFLOW_MODEL_ID = 'SOUBRGCREATE'

// 招标类型 SOU_BRG_TYPE
export const SOU_BRG_TYPE_ENUM = {
  // 商务
  BUSINESS: 'BUSINESS',
  // 技术+商务
  TECHNOLOGY_BUSINESS: 'TECHNOLOGY_BUSINESS'
}

/**
 * @description 采购商根据参数判断能否废弃单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeAbandonProject = projectStatus => {
  // 非
  return ![
    // 拟定
    SOU_PROJECT_STATUS_ENUM.DRAFT,
    // 定价中
    SOU_PROJECT_STATUS_ENUM.PRICING,
    // 已定价
    SOU_PROJECT_STATUS_ENUM.PRICE_END,
    // 已废弃
    SOU_PROJECT_STATUS_ENUM.CANCEL
  ].includes(projectStatus)
}

/**
 * @description 采购商根据参数判断能否删除单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeDeleteProject = projectStatus => {
  // 拟定
  return projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT
}

/**
 * @description 采购商根据参数判断能否代理报价
 * @author donghf3
 * @param orderStatus 报价状态
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeProxyQuote = (orderStatus, projectStatus) => {
  // 未投标、撤回
  return [SOU_ORDER_STATUS_ENUM.DRAFT, SOU_ORDER_STATUS_ENUM.WITHDRAW].includes(orderStatus) &&
    // 接受报价中
    projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER
}

/**
 * @description 采购商根据参数判断能否操作页面内容
 * @author donghf3
 * @param projectStatus 单据状态
 * @param createApprovalStatus 审批状态
 * @returns {boolean}
 */
export const judgeManagement = (projectStatus, createApprovalStatus) => {
  // 项目状态=='拟定'
  return projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT &&
    // 审批状态=='草稿、审批中'
    [SOU_APPROVAL_STATUS_ENUM.DRAFT, SOU_APPROVAL_STATUS_ENUM.SUBMITTED].includes(createApprovalStatus)
}

/**
 * @description 供应商根据参数判断能否报名
 * @author donghf3
 * @param projectStatus 单据状态
 * @param signUpStatus 报名状态
 * @param signUpEndTime 报名截止时间
 * @returns {boolean}
 */
export const judgeSignUp = ({ projectStatus, signUpStatus, signUpEndTime }) => {
  // 项目状态：接受报名中
  return projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP &&
    // 报名状态：未报名/已驳回
    [SOU_SIGN_UP_STATUS_ENUM.NO_SIGN_UP, SOU_SIGN_UP_STATUS_ENUM.REJECTED].includes(signUpStatus) &&
    // 报名截止时间大于当前时间
    new Date(signUpEndTime) > new Date()
}

/**
 * @description 供应商根据参数判断能否进行报价
 * @author donghf3
 * @param projectStatus 单据状态
 * @param orderStatus 投标状态
 * @param canOrder 是否允许投标
 * @returns {boolean}
 */
export const judgeQuote = ({ projectStatus, orderStatus, canOrder }) => {
  // 项目状态：接受投标中
  return projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER &&
    // 投标状态：未投标/已驳回
    [SOU_ORDER_STATUS_ENUM.DRAFT, SOU_ORDER_STATUS_ENUM.WITHDRAW].includes(orderStatus) &&
    // 允许投标
    canOrder === 'Y'
}

/**
 * @description 供应商根据参数判断能否进行撤回报价
 * @author donghf3
 * @param projectStatus 单据状态
 * @param orderStatus 投标状态
 * @param canOrder 是否允许投标
 * @param allowWithdraw 是否允许撤回
 * @returns {boolean}
 */
export const judgeAllowWithdraw = ({ projectStatus, orderStatus, canOrder, allowWithdraw }) => {
  // 项目状态：接受投标中
  return projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER &&
    // 投标状态：已投标
    orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION &&
    // 允许投标
    canOrder === 'Y' &&
    // 允许撤回
    allowWithdraw === 'Y'
}
