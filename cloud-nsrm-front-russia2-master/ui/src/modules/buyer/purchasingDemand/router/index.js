import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/purchasingDemand',
  name: 'purchasingDemand',
  component: () => import('@/layout'),
  redirect: {
    name: 'purchaseApplication'
  },
  children: [
    {
      path: 'purchaseApplication',
      component: meiqlCtrl.purchaseApplication === 'Y'
        ? () => import('modb@/purchasingDemand/views/purchaseApplicationEngine')
        : () => import('modb@/purchasingDemand/views/purchaseApplication'),
      name: 'purchaseApplication',
      meta: {
        title: 'route.purchaseApplyMmanagement', // 采购申请管理
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
      component: meiqlCtrl.demandPoolManagement === 'Y'
        ? () => import('modb@/purchasingDemand/views/demandPoolManagementEngine')
        : () => import('modb@/purchasingDemand/views/demandPoolManagement'),
      name: 'demandPoolManagement',
      meta: {
        title: 'route.demandPoolManagement', // 需求池
        requiresAuth: true
      }
    },
    {
      path: 'categoryAssignRule',
      component: () => import('modb@/purchasingDemand/views/categoryAssignRule'),
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
    }
  ]
}
