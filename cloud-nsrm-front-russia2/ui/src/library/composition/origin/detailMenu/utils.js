import i18n from '@/lang'

// 根据业务类型，映射字段
export const businessTypeKeyMap = {
  COMPETITION: {}
}

export const treeMenuData = {
  COMPETITION: [
    {
      id: 't1',
      label: i18n.t('competition.comPriceManagement'),
      disabled: false,
      tag: 'view',
      children: [
        // 项目信息
        {
          id: 't1-1',
          key: 'projectInformation',
          label: i18n.t('bidMod.projectInformation'),
          iconClass: 'el-icon-circle-check',
          tag: 'view',
          disabled: false
        },
        // 项目需求
        {
          id: 't1-2',
          key: 'projectRequirement',
          label: i18n.t('bidMod.projectRequire'),
          iconClass: 'el-icon-circle-check',
          tag: 'view',
          disabled: false
        },
        // 邀请供应商
        {
          id: 't1-3',
          key: 'inviteSupplier',
          label: i18n.t('bidMod.bidVendorCount'),
          iconClass: 'el-icon-circle-check',
          tag: 'view',
          disabled: false
        }
      ]
    },
    // 报名管理 t2
    {
      id: 't2',
      key: 'entryManagement',
      label: i18n.t('bidMod.registManagement'),
      iconClass: 'el-icon-circle-check',
      disabled: false
    },
    // 报价控制 t5
    {
      id: 't3',
      key: 'compControl',
      label: i18n.t('bidMod.priceControl'),
      iconClass: 'el-icon-circle-check',
      disabled: false
    },
    // 开/评标
    {
      id: 't4',
      label: i18n.t('bidMod.evaluationBid'),
      disabled: false,
      children: [
        // 商务标管理
        {
          id: 't4-1',
          key: 'businessManagement',
          label: i18n.t('bidMod.businessManagement'),
          iconClass: 'el-icon-circle-check',
          disabled: false
        },
        // 竞价大厅
        {
          id: 't4-2',
          key: 'compHall',
          label: i18n.t('bidMod.hall'),
          iconClass: 'el-icon-circle-check',
          disabled: false
        },
        // 评选
        {
          id: 't4-3',
          key: 'compEvaluation',
          label: i18n.t('bidMod.appraise'),
          iconClass: 'el-icon-circle-check',
          disabled: false
        }
      ]
    }
  ]
}

// 根据业务类型以及key，返回映射字段
export const mappingPropByBusinessTypeAndKey = (type, key) => {
  // 如果找不到配置就返回key
  return businessTypeKeyMap[type][key] || key
}
