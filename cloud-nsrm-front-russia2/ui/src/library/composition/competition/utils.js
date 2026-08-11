/**
 * @description 竞价 composition
 * @author chengwei
 */
import i18n from '@/lang'

// 竞价流程模板
export const WORKFLOW_MODEL_ID = 'SOUAUCTCREATE'

// 菜单
const menuData = [
  // 询价立项
  {
    id: 'projectInitiation',
    label: i18n.t('competition.projectInitiation'),
    isSubmenu: true,
    children: [
      // 项目信息
      {
        id: 'projectInfo',
        label: i18n.t('competition.projectInfo'),
        key: 'projectInfo'
      },
      // 项目需求
      {
        id: 'requireInfo',
        label: i18n.t('competition.requireInfoCom'),
        key: 'requireInfo'
      },
      // 邀请供应商
      {
        id: 'inviteVendor',
        label: i18n.t('bidMod.inviteSupplier'),
        key: 'inviteVendor'
      }
      // 流程审批
      // {
      //   id: 'createApproval',
      //   label: i18n.t('bidMod.processApproval'),
      //   key: 'createApproval'
      // }
    ]
  },
  // 报名管理
  {
    id: 'signUpManagementParent',
    label: i18n.t('bidMod.entryManagement'),
    isSubmenu: true,
    children: [
      // 报名详情
      {
        id: 'signUpManagement',
        label: i18n.t('competition.signUpManagementDetail'),
        key: 'signUpManagement'
      }
    ]
  },
  // 保证金管理
  {
    id: 'bondManagementParent',
    label: i18n.t('bidMod.bondManagement'),
    isSubmenu: true,
    children: [
      // 保证金详情
      {
        id: 'bondManagement',
        label: i18n.t('bidMod.competitionLts.bondManagement'),
        key: 'bondManagement'
      }
    ]
  },
  // 商务管理
  {
    id: 'businessManagementParent',
    label: i18n.t('competition.businessManagementParent'),
    isSubmenu: true,
    children: [
      // 商务详情
      {
        id: 'businessManagement',
        label: i18n.t('bidMod.competitionLts.businessManagement'),
        key: 'businessManagement'
      }
    ]
  },
  // 竞/评标
  {
    id: 'competitiveTender',
    label: i18n.t('competition.competitiveTender'),
    isSubmenu: true,
    children: [
      // 竞价大厅
      {
        id: 'auctHall',
        label: i18n.t('bidMod.hall'),
        key: 'auctHall'
      },
      // 评选
      {
        id: 'evaluation',
        label: i18n.t('bidMod.bidEvaluation'),
        key: 'evaluation'
      }
    ]
  }
  // 定点会签
  // {
  //   id: 'evaluation',
  //   label: '定点会签',
  //   key: 'evaluation'
  // }
]

// 默认展开的节点
const menuDefaultOpeneds = ['projectInitiation', 'signUpManagementParent', 'bondManagementParent', 'businessManagementParent', 'competitiveTender']

// 竞价状态 SOU_AUCT_PROJECT_STATUS
const SOU_AUCT_PROJECT_STATUS_ENUM = {
  // 拟定
  DRAFT: 'DRAFT',
  // 已作废
  CANCEL: 'CANCEL',
  // 竞价未开始
  ORDER_NOT_START: 'ORDER_NOT_START',
  // 接受报名中
  ACCEPT_SIGN_UP: 'ACCEPT_SIGN_UP',
  // 报名截止
  SIGN_UP_END: 'SIGN_UP_END',
  // 接受竞价中
  ACCEPT_ORDER: 'ACCEPT_ORDER',
  // 竞价截止
  ORDER_END: 'ORDER_END',
  // 定价中
  PRICING: 'PRICING',
  // 定价驳回
  PRICE_REJECT: 'PRICE_REJECT',
  // 已定价
  PRICE_END: 'PRICE_END'
}

// 竞价评分规则 SOU_AUCT_SCORE_RULE_TYPE
const SOU_AUCT_SCORE_RULE_TYPE_ENUM = {
  // 正向竞价
  MAX_PRICE: 'MAX_PRICE',
  // 反向竞价
  MIN_PRICE: 'MIN_PRICE'
}

/**
 * 根据评分规则返回标识字符
 * @param type 字典
 * @returns {string}
 */
const getEvaluateMethodFlag = type => {
  const enumMap = new Map([
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MAX_PRICE, i18n.t('bidMod.increase')],
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MIN_PRICE, i18n.t('bidMod.amplitude')]
  ])
  return enumMap.get(type) || ''
}

const getEvaluateMethodFlagAmount = type => {
  const enumMap = new Map([
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MAX_PRICE, i18n.t('bidMod.amountIncrease')],
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MIN_PRICE, i18n.t('bidMod.amountDecrease')]
  ])
  return enumMap.get(type) || ''
}

export {
  menuData,
  menuDefaultOpeneds,
  SOU_AUCT_PROJECT_STATUS_ENUM,
  SOU_AUCT_SCORE_RULE_TYPE_ENUM,
  getEvaluateMethodFlag,
  getEvaluateMethodFlagAmount
}

