import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/vendorManagement',
  name: 'vendorManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'quaOfReview'
  },
  children: [
    // 银行信息维护
    {
      path: 'bankInformation',
      component: () => import('modb@/vendorManagementBuyer/views/bankInformationEngine'),
      name: 'bankInformation',
      meta: {
        title: 'route.bankInformation',
        requiresAuth: true,
        defaultActive: 'bankEdit'
      }
    },
    // 黑名单解除
    {
      path: 'blackSecure',
      component: () => import('modcb@/vendorManagementBuyer/views/blackSecure'),
      name: 'blackSecure',
      meta: {
        title: 'route.blackSecure',
        requiresAuth: true
      }
    },
    {
      path: 'quaOfReview',
      component: meiqlCtrl.quaOfReview === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/quaOfReviewEngine')
        : () => import('modb@/vendorManagementBuyer/views/quaOfReview'),
      name: 'quaOfReview',
      meta: {
        title: 'route.quaOfReview', // 资质审查
        requiresAuth: true
      }
    },
    {
      path: 'financialInforChanges',
      component: meiqlCtrl.financialInforChanges === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/financialInforChangesEngine')
        : () => import('modb@/vendorManagementBuyer/views/financialInforChanges'),
      name: 'financialInforChanges',
      meta: {
        title: 'route.financialInforChanges', // 财务信息变更
        requiresAuth: true
      }
    },
    {
      path: 'complaintReview',
      component: meiqlCtrl.complaintReview === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/complaintReviewEngine')
        : () => import('modb@/vendorManagementBuyer/views/complaintReview'),
      name: 'complaintReview',
      meta: {
        title: 'route.complaintReview', // 供应商投诉处理
        requiresAuth: true
      }
    },
    {
      path: 'siteAssessment',
      component: meiqlCtrl.siteAssessment === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/siteAssessmentEngine')
        : () => import('modb@/vendorManagementBuyer/views/siteAssessment'),
      name: 'siteAssessment',
      meta: {
        title: 'route.siteAssessment', // 供应商评审
        requiresAuth: true
      }
    },
    {
      path: 'sampleConfirmed',
      component: () => meiqlCtrl.sampleConfirmed === 'Y'
        ? import('modb@/vendorManagementBuyer/views/sampleConfirmedEngine')
        : import('modb@/vendorManagementBuyer/views/sampleConfirmed'),
      name: 'sampleConfirmed',
      meta: {
        title: 'route.sampleConfirmed', // 样品确认
        requiresAuth: true
      }
    },
    {
      path: 'materialTrial',
      component: () => meiqlCtrl.materialTrial === 'Y'
        ? import('modb@/vendorManagementBuyer/views/materialTrialEngine')
        : import('modb@/vendorManagementBuyer/views/materialTrial'),
      name: 'materialTrial',
      meta: {
        title: 'route.materialTrial', // 物料试用
        requiresAuth: true
      }
    },
    {
      path: 'purchaseDirectory',
      component: () => meiqlCtrl.purchaseDirectory === 'Y'
        ? import('modb@/vendorManagementBuyer/views/purchaseDirectoryEngine')
        : import('modb@/vendorManagementBuyer/views/purchaseDirectory'),
      name: 'purchaseDirectory',
      meta: {
        title: 'route.purchaseDirectory', // 货源清单
        requiresAuth: true
      }
    },
    {
      path: 'purchaseDirectoryChange',
      component: () => meiqlCtrl.purchaseDirectoryChange === 'Y'
        ? import('modb@/vendorManagementBuyer/views/purchaseDirectoryChangeEngine')
        : import('modb@/vendorManagementBuyer/views/purchaseDirectoryChange'),
      name: 'purchaseDirectoryChange',
      meta: {
        title: 'route.purchaseDirectoryChange', // 采购目录变更
        requiresAuth: true
      }
    },
    {
      path: 'vendorOrgAndCatRel',
      component: () => import('modb@/vendorManagementBuyer/views/vendorOrgAndCatRel'),
      name: 'vendorOrgAndCatRel',
      meta: {
        title: 'route.vendorOrgAndCatRel', // 供应商组织与品类关系
        requiresAuth: true
      }
    },
    {
      path: 'vendorProfile',
      component: meiqlCtrl.vendorProfile === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/vendorProfileEngine')
        : () => import('modb@/vendorManagementBuyer/views/vendorProfile'),
      name: 'vendorProfile',
      meta: {
        title: 'route.vendorProfileList', // 供应商清单
        requiresAuth: true
      }
    },
    {
      path: 'vendorGreenChannel',
      component: meiqlCtrl.vendorGreenChannel === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine')
        : () => import('modb@/vendorManagementBuyer/views/vendorGreenChannel'),
      name: 'vendorGreenChannel',
      meta: {
        title: 'route.vendorGreenChannel', // 供应商绿色通道
        requiresAuth: true
      }
    },
    {
      path: 'vendorInfoChange',
      component: meiqlCtrl.vendorInfoChange === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine')
        : () => import('modb@/vendorManagementBuyer/views/vendorInfoChange'),
      name: 'vendorInfoChange',
      meta: {
        title: 'route.vendorInfoChange', // 供应商信息变更
        requiresAuth: true
      }
    },
    {
      path: 'cooperationEnded',
      component: meiqlCtrl.cooperationEnded === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/cooperationEndedEngine')
        : () => import('modb@/vendorManagementBuyer/views/cooperationEnded'),
      name: 'cooperationEnded',
      meta: {
        title: 'cusEntry.route.cooperationEnded', // 合作终止
        requiresAuth: true
      }
    },
    {
      path: 'categoryRelationship',
      component: meiqlCtrl.categoryRelationship === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/categoryRelationshipEngine')
        : () => import('modb@/vendorManagementBuyer/views/categoryRelationship'),
      name: 'categoryRelationship',
      meta: {
        title: 'route.vendorOrgAndCatRel', // 供应商组织与品类关系
        requiresAuth: true
      }
    },
    {
      path: 'expirationReminder',
      component: () => import('modcb@/vendorManagementBuyer/views/expirationReminder'),
      name: 'expirationReminder',
      meta: {
        title: 'route.expirationReminder', // 证件到期提醒
        requiresAuth: true
      }
    },
    {
      path: 'crossOrgImport',
      component: () => meiqlCtrl.crossOrgImport === 'Y'
        ? import('modb@/vendorManagementBuyer/views/crossOrgImportEngine')
        : import('modb@/vendorManagementBuyer/views/crossOrgImport'),
      name: 'crossOrgImport',
      meta: {
        title: 'route.crossOrgImport', // 跨组织引入
        requiresAuth: true
      }
    },
    {
      path: 'questTemplate',
      component: () => import('modb@/vendorManagementBuyer/views/questTemplate'),
      name: 'questTemplate',
      meta: {
        title: 'route.questTemplate', // 调查表模板管理
        requiresAuth: true
      }
    },
    {
      path: 'questManagement',
      component: () => import('modb@/vendorManagementBuyer/views/questManagement'),
      name: 'questManagement',
      meta: {
        title: 'route.questManagement', // 调查表管理（采购商）
        requiresAuth: true
      }
    },
    {
      path: 'questSupplier',
      component: () => import('modb@/vendorManagementBuyer/views/questSupplier'),
      name: 'questSupplierBuyer',
      meta: {
        title: 'route.questSupplier', // 供应商填写调查
        requiresAuth: true
      }
    },
    {
      path: 'inviteSupplier',
      component: meiqlCtrl.inviteSupplier === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/inviteSupplierEngine')
        : () => import('modb@/vendorManagementBuyer/views/inviteSupplier'),
      name: 'inviteSupplier',
      meta: {
        title: 'route.inviteSupplier', // 邀请供应商
        requiresAuth: true
      }
    },
    {
      path: 'reviewFormStandard',
      component: () => import('modb@/vendorManagementBuyer/views/reviewFormStandard'),
      name: 'reviewFormStandard',
      meta: {
        title: 'route.reviewFormStandard', // 资质审查标准管理
        requiresAuth: true
      }
    },
    {
      path: 'siteReviewPlan',
      component: meiqlCtrl.siteReviewPlan === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/siteReviewPlanEngine')
        : () => import('modb@/vendorManagementBuyer/views/siteReviewPlan'),
      name: 'siteReviewPlan',
      meta: {
        title: 'route.siteReviewPlan', // 现场评审计划管理
        requiresAuth: true
      }
    },
    {
      path: 'siteReviewPlanConfirm',
      component: meiqlCtrl.siteReviewPlanConfirm === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/siteReviewPlanConfirmEngine')
        : () => import('modb@/vendorManagementBuyer/views/siteReviewPlanConfirm'),
      name: 'siteReviewPlanConfirm',
      meta: {
        title: 'route.siteReviewPlanConfirm', // 计划落实管理
        requiresAuth: true
      }
    },
    {
      path: 'siteReviewModel',
      component: () => import('modb@/vendorManagementBuyer/views/siteReviewModel'),
      name: 'siteReviewModel',
      meta: {
        title: 'route.siteReviewModel', // 供应商计划落实
        requiresAuth: true
      }
    },
    {
      path: 'potentialSupplier',
      component: meiqlCtrl.potentialSupplierMaterial === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/potentialSupplierEngine')
        : () => import('modb@/vendorManagementBuyer/views/potentialSupplier'),
      name: 'potentialSupplier',
      meta: {
        title: 'route.potentialSupplier', // 潜在供应商
        requiresAuth: true
      }
    },
    {
      path: 'potentialSupplierMaterial',
      component: meiqlCtrl.potentialSupplierMaterial === 'Y'
        ? () => import('modb@/vendorManagementBuyer/views/potentialSupplierMaterialEngine')
        : () => import('modb@/vendorManagementBuyer/views/potentialSupplierMaterial'),
      name: 'potentialSupplierMaterial',
      meta: {
        title: 'route.potentialSupplier', // 潜在供应商-材料类
        requiresAuth: true
      }
    },
    {
      path: 'nonQuaOfReview',
      component: () => import('modb@/vendorManagementBuyer/views/nonQuaOfReview'),
      name: 'nonQuaOfReview',
      meta: {
        title: 'route.nonQuaOfReview', // 非材供方资质认证
        requiresAuth: true
      }
    },
    {
      path: 'nonSiteAssessment',
      component: () => import('modb@/vendorManagementBuyer/views/nonSiteAssessment'),
      name: 'nonSiteAssessment',
      meta: {
        title: 'route.nonSiteAssessment', // 非材供方资质认证
        requiresAuth: true
      }
    },
    {
      path: 'nonCategoryRelationship',
      component: () => import('modb@/vendorManagementBuyer/views/nonCategoryRelationship'),
      name: 'nonCategoryRelationship',
      meta: {
        title: 'route.nonCategoryRelationship', // 非材供方生效
        requiresAuth: true
      }
    },
    // 黑名单
    {
      path: 'black',
      name: 'black',
      component: meiqlCtrl.black === 'Y'
        ? () => import('modcb@/vendorManagementBuyer/views/blackEngine')
        : () => import('modb@/vendorManagementBuyer/views/black'),
      meta: {
        title: 'route.blacklist',
        requiresAuth: true
      }
    },
    // 黑名单报表
    {
      path: 'blackReport',
      name: 'blackReport',
      component: () => import('modb@/vendorManagementBuyer/views/blackReport'),
      meta: {
        title: 'route.blackReport',
        requiresAuth: true
      }
    },
    // 黑名单临时业务申请
    {
      path: 'blacktemporary',
      component: () => import('modb@/vendorManagementBuyer/views/blacktemporary'),
      name: 'blacktemporary',
      meta: {
        title: 'route.blacktemporary',
        requiresAuth: true
      }
    },
    // 问卷调查 - 采购商
    {
      path: 'questionnaireSurvey',
      name: 'questionnaireSurvey',
      component: () => import('modb@/vendorManagementBuyer/views/questionnaireSurvey'),
      meta: {
        title: 'vendor.questionnaireSurvey',
        requiresAuth: true
      }
    },
    // 近期采购供应商
    {
      path: 'recentPurchaseSuppliers',
      name: 'recentPurchaseSuppliers',
      component: () => import('modcb@/vendorManagementBuyer/views/recentPurchaseSuppliers'),
      meta: {
        title: 'cusEntry.route.recentPurchaseSuppliers',
        requiresAuth: true
      }
    },
    // 关联供应商
    {
      path: 'relationSuppliers',
      name: 'relationSuppliers',
      component: () => import('modcb@/vendorManagementBuyer/views/relationSuppliers'),
      meta: {
        title: 'cusEntry.route.relationSuppliers',
        requiresAuth: true
      }
    },
    // 供方生效
    {
      path: 'supplierEffective',
      name: 'supplierEffective',
      component: () => import('modcb@/vendorManagementBuyer/views/supplierEffective'),
      meta: {
        title: 'cusEntry.route.supplierEffective',
        requiresAuth: true
      }
    }
  ]
}
