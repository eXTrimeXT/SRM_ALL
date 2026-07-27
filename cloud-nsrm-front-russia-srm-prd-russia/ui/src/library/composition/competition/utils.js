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
    label: '竞价项目立项',
    isSubmenu: true,
    children: [
      // 项目信息
      {
        id: 'projectInfo',
        label: '基础信息',
        key: 'projectInfo'
      },
      // 项目需求
      {
        id: 'requireInfo',
        label: '竞价需求',
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
        label: '报名详情',
        key: 'signUpManagement'
      }
    ]
  },
  // 保证金管理
  {
    id: 'bondManagementParent',
    label: '保证金管理',
    isSubmenu: true,
    children: [
      // 保证金详情
      {
        id: 'bondManagement',
        label: '保证金详情',
        key: 'bondManagement'
      }
    ]
  },
  // 商务管理
  {
    id: 'businessManagementParent',
    label: '商务管理',
    isSubmenu: true,
    children: [
      // 商务详情
      {
        id: 'businessManagement',
        label: '商务详情',
        key: 'businessManagement'
      }
    ]
  },
  // 竞/评标
  {
    id: 'competitiveTender',
    label: '竞/评标',
    isSubmenu: true,
    children: [
      // 竞价大厅
      {
        id: 'auctHall',
        label: '竞价大厅',
        key: 'auctHall'
      },
      // 评选
      {
        id: 'evaluation',
        label: '评选',
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
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MAX_PRICE, '涨幅'],
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MIN_PRICE, '降幅']
  ])
  return enumMap.get(type) || ''
}

const getEvaluateMethodFlagAmount = type => {
  const enumMap = new Map([
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MAX_PRICE, '涨额'],
    [SOU_AUCT_SCORE_RULE_TYPE_ENUM.MIN_PRICE, '降额']
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

