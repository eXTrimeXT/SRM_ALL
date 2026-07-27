import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/vendorManagementSynergy',
  name: 'vendorManagementSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'vendorSampleConfirmed'
  },
  children: [
    {
      path: 'complaintinfo',
      component: meiqlCtrl.complaintinfo === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/complaintinfoEngine')
        : () => import('mods@/vendorManagementSupplier/views/complaintinfo'),
      name: 'complaintinfo',
      meta: {
        title: 'route.complaintinfo', // 供应商投诉
        requiresAuth: true
      }
    },
    {
      path: 'vendorArchivalInfo',
      component: () => import('mods@/vendorManagementSupplier/views/vendorArchivalInfo'),
      name: 'vendorArchivalInfo',
      meta: {
        title: 'route.vendorArchivalInfo', // 供方档案信息
        requiresAuth: true
      }
    },
    {
      path: 'questSupplier',
      component: () => import('mods@/vendorManagementSupplier/views/questSupplier'),
      name: 'questSupplier',
      meta: {
        title: 'route.questSupplier', // 填写调查
        requiresAuth: true
      }
    },
    {
      path: 'questManagement',
      component: () => import('mods@/vendorManagementSupplier/views/questManagement'),
      name: 'questManagement',
      meta: {
        title: 'route.questManagement', // 调查表管理
        requiresAuth: true
      }
    },
    {
      path: 'vendorInfoChange',
      component: meiqlCtrl.vendorInfoChange === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/vendorInfoChangeEngine')
        : () => import('mods@/vendorManagementSupplier/views/vendorInfoChange'),
      name: 'vendorInfoChange',
      meta: {
        title: 'route.vendorInfoChange', // 供应商信息变更
        requiresAuth: true
      }
    },
    {
      path: 'drawingshead',
      component: meiqlCtrl.drawingshead === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/drawingsheadEngine')
        : () => import('mods@/vendorManagementSupplier/views/drawingshead'),
      name: 'drawingshead',
      meta: {
        title: 'route.materialDrawings',
        requiresAuth: true
      }
    },
    {
      path: 'purchaseDirectoryChange',
      component: meiqlCtrl.purchaseDirectoryChange === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/purchaseDirectoryChangeEngine')
        : () => import('mods@/vendorManagementSupplier/views/purchaseDirectoryChange'),
      name: 'purchaseDirectoryChangeSupplier',
      meta: {
        title: 'route.purchaseDirectoryChange', // 采购目录变更
        requiresAuth: true
      }
    },
    {
      path: 'purchaseDirectory',
      component: meiqlCtrl.vendorPurchaseDirectory === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/purchaseDirectoryEngine')
        : () => import('mods@/vendorManagementSupplier/views/purchaseDirectory'),
      name: 'purchaseDirectory',
      meta: {
        title: 'route.purchaseDirectory', // 采购目录
        requiresAuth: true
      }
    },
    {
      path: 'vendorSampleConfirmed',
      component: meiqlCtrl.vendorSampleConfirmed === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/vendorSampleConfirmedEngine')
        : () => import('mods@/vendorManagementSupplier/views/vendorSampleConfirmed'),
      name: 'vendorSampleConfirmed',
      meta: {
        title: 'route.vendorSampleConfirmed',
        requiresAuth: true
      }
    },
    {
      path: 'vendorMaterialTrial',
      component: meiqlCtrl.vendorMaterialTrial === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/vendorMaterialTrialEngine')
        : () => import('mods@/vendorManagementSupplier/views/vendorMaterialTrial'),
      name: 'vendorMaterialTrial',
      meta: {
        title: 'route.vendorMaterialTrial',
        requiresAuth: true
      }
    },
    {
      path: 'siteAssessment',
      component: meiqlCtrl.siteAssessment === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/siteAssessmentEngine')
        : () => import('mods@/vendorManagementSupplier/views/siteAssessment'),
      name: 'siteAssessment',
      meta: {
        title: 'route.siteAssessmentVendor',
        defaultActive: 'siteAssessmentVendor',
        requiresAuth: true
      }
    },
    {
      path: 'vendorSiteReviewPlanConfirm',
      component: meiqlCtrl.siteReviewPlanConfirm === 'Y'
        ? () => import('mods@/vendorManagementSupplier/views/vendorSiteReviewPlanConfirmEngine')
        : () => import('mods@/vendorManagementSupplier/views/vendorSiteReviewPlanConfirm'),
      name: 'vendorSiteReviewPlanConfirm',
      meta: {
        title: 'route.vendorSiteReviewPlanConfirm', // 供应商计划落实
        requiresAuth: true
      }
    },
    {
      path: 'questionnaireSurveySupplier',
      component: () => import('mods@/vendorManagementSupplier/views/questionnaireSurveySupplier'),
      name: 'questionnaireSurveySupplier',
      meta: {
        title: 'route.questionnaireSurveySupplier', // 问卷调查 - 供应商
        requiresAuth: true
      }
    }
  ]
}
