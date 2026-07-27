/**
 * @description 简易询价 composition
 * @author donghf3
 */
import {
  SOU_APPROVAL_STATUS_ENUM,
  SOU_ORDER_STATUS_ENUM,
  SOU_PROJECT_STATUS_ENUM
} from 'lib@/composition/origin/enum'

// 绑定的流程模板
export const WORKFLOW_MODEL_ID = 'SOUINQCREATE'

/**
 * @description 采购商根据参数判断能否进行编辑
 * @author donghf3
 * @param projectStatus 单据状态
 * @param createApprovalStatus 审核状态
 * @returns {boolean}
 */
export const judgeEdit = ({ projectStatus, createApprovalStatus }) => {
  // 单据状态拟定
  return projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT &&
  // 审核状态拟定
  createApprovalStatus === SOU_APPROVAL_STATUS_ENUM.DRAFT
}

/**
 * @description 采购商根据参数判断能否删除单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeDelete = ({ projectStatus }) => {
  // 单据状态拟定
  return projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT
}

/**
 * @description 采购商根据参数判断能否进入管理页面
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeManage = ({ projectStatus }) => {
  // 单据状态 非拟定 非取消
  return !([SOU_PROJECT_STATUS_ENUM.DRAFT, SOU_PROJECT_STATUS_ENUM.CANCEL].includes(projectStatus))
}

/**
 * @description 采购商根据参数判断能否进入审批页面
 * @author donghf3
 * @param createApprovalStatus 审批状态
 * @returns {boolean}
 */
export const judgeApproval = ({ createApprovalStatus }) => {
  // 单据状态拟定
  return createApprovalStatus === SOU_APPROVAL_STATUS_ENUM.SUBMITTED
}

/**
 * @description 采购商根据参数判断能否取消单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeCancel = ({ projectStatus }) => {
  // 单据状态 非拟定、非取消、非已定价
  return !([SOU_PROJECT_STATUS_ENUM.DRAFT, SOU_PROJECT_STATUS_ENUM.CANCEL, SOU_PROJECT_STATUS_ENUM.PRICE_END].includes(projectStatus))
}

/**
 * @description 采购商根据参数判断能否查看单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeView = ({ projectStatus }) => {
  // 单据状态 已取消
  return projectStatus === SOU_PROJECT_STATUS_ENUM.CANCEL
}

/**
 * @description 采购商根据参数判断能否复制单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeCopy = ({ projectStatus }) => {
  // 单据状态 非已取消、非拟定
  return ![SOU_PROJECT_STATUS_ENUM.DRAFT, SOU_PROJECT_STATUS_ENUM.CANCEL].includes(projectStatus)
}

/**
 * @description 供应商根据参数判断能否报价
 * @author donghf3
 * @param projectStatus 单据状态
 * @param orderStatus 报价状态
 * @param canOrder 是否允许报价
 * @returns {boolean}
 */
export const judgeQuote = ({ projectStatus, orderStatus, canOrder }) => {
  // 待报价/已撤回
  return [SOU_ORDER_STATUS_ENUM.DRAFT, SOU_ORDER_STATUS_ENUM.WITHDRAW].includes(orderStatus) &&
    // 接受报价中
    projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER &&
    // 允许报价
    canOrder === 'Y'
}

/**
 * @description 供应商根据参数判断能否查看单据
 * @author donghf3
 * @param projectStatus 单据状态
 * @param orderStatus 报价状态
 * @param allowWithdraw 是否允许撤回
 * @returns {boolean}
 */
export const judgeRollback = ({ projectStatus, orderStatus, allowWithdraw }) => {
  // 已报价
  return orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION &&
    // 接受报价中
    projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER &&
    // 允许撤回
    allowWithdraw === 'Y'
}

/**
 * @description 供应商商根据参数判断能否打开中标结果
 * @author donghf3
 * @param projectStatus 单据状态
 * @returns {boolean}
 */
export const judgeResult = ({ orderStatus, currentRound }) => {
  // 已报价 || 当前轮次大于1
  return orderStatus === SOU_ORDER_STATUS_ENUM.SUBMISSION || currentRound > 1
}
