
export default {
  path: '/purchasingDemand',
  name: 'purchasingDemand',
  component: () => import('@/layout'),
  redirect: {
    name: 'purchaseApplication'
  },
  children: [
    {
      path: 'sharedInventory',
      component: () => import('modc@/buyer/purchasingDemand/views/sharedInventory'),
      name: 'sharedInventory',
      meta: {
        title: 'route.sharedInventory',
        requiresAuth: true
      }
    },
    {
      path: 'qa',
      component: () => import('modc@/buyer/purchasingDemand/views/biddingQa'),
      name: 'biddingQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'biddingVendorQa',
      component: () => import('modc@/buyer/purchasingDemand/views/biddingQa'),
      name: 'biddingQas',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'bargainQa',
      component: () => import('modc@/buyer/purchasingDemand/views/biddingQa'),
      name: 'bargainQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'bargainVendorQa',
      component: () => import('modc@/buyer/purchasingDemand/views/biddingQa'),
      name: 'bargainVendorQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'withdrawZhaobiao',
      component: () => import('modc@/buyer/purchasingDemand/views/withdrawZhaobiao'),
      name: 'withdrawZhaobiao',
      meta: {
        title: 'cusEntry.supplement20250121.demandCancel', // 需求申请取消
        requiresAuth: true
      }
    },
    {
      path: 'biddingDocuments',
      component: () => import('modc@/buyer/purchasingDemand/views/biddingDocuments'),
      name: 'biddingDocuments',
      meta: {
        title: 'cusEntry.supplement20250121.materialSubmit', // 招标资料提交
        requiresAuth: true
      }
    },
    {
      path: 'purchaseApplication',
      component: () => import('modc@/buyer/purchasingDemand/views/purchaseApplication'),
      name: 'purchaseApplication',
      meta: {
        title: 'route.purchaseApplyMmanagement', // 采购申请管理
        requiresAuth: true
      }
    },
    {
      path: 'projectPlan',
      component: () => import('modc@/buyer/purchasingDemand/views/projectPlan'),
      name: 'projectPlan',
      meta: {
        title: 'route.projectPlan', // 项目计划
        requiresAuth: true
      }
    },
    {
      path: 'demandPoolManagementZhaobiao',
      component: () => import('modc@/buyer/purchasingDemand/views/demandPoolManagementZhaobiao'),
      name: 'demandPoolManagementZhaobiao',
      meta: {
        title: 'route.demandPoolManagement', // 需求池/招标计划池
        requiresAuth: true
      }
    },
    {
      path: 'applicationAndAudit',
      component: () =>
        import('modb@/purchasingDemand/views/applicationAndAudit'),
      name: 'applicationAndAudit',
      meta: {
        title: 'route.applicationAndAudit',
        requiresAuth: true
      }
    },
    {
      path: 'demandPoolManagement',
      component: () => import('modc@/buyer/purchasingDemand/views/demandPoolManagement'),
      name: 'demandPoolManagement',
      meta: {
        title: 'route.demandPoolManagement', // 需求池
        requiresAuth: true
      }
    },
    {
      path: 'categoryAssignRule',
      component: () => import('modcb@/purchasingDemand/views/categoryAssignRule'),
      name: 'categoryAssignRule',
      meta: {
        title: 'route.categoryDivisionRules', // 品类分工规则
        requiresAuth: true
      }
    },
    {
      path: 'materialAssignRule',
      component: () => import('modb@/purchasingDemand/views/materialAssignRule'),
      name: 'materialAssignRule',
      meta: {
        title: 'route.materialDivisionRules', // 物料分工规则
        requiresAuth: true
      }
    },
    {
      path: 'abnormal',
      component: () => import('modcb@/purchasingDemand/views/abnormal'),
      name: 'AbnormalList',
      meta: {
        title: 'route.abnormal', // 异常登记
        requiresAuth: true
      }
    }
  ]
}
