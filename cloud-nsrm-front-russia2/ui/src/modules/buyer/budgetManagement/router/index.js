import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/budgetManagement',
  name: 'budgetManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'budgetManagementList'
  },
  children: [
    {
      path: 'budgetManagementList',
      component: meiqlCtrl.budgetManagementList ==='Y'
        ? () => import('modb@/budgetManagement/views/budgetManagementListEngine')
        : () => import('modb@/budgetManagement/views/budgetManagementList'),
      name: 'budgetManagementList',
      meta: {
        title: 'route.budgetManagement',
        requiresAuth: true
      }
    },
    {
      path: 'budgetAdjustmentHistory',
      component: meiqlCtrl.budgetManagementList ==='Y'
        ? () => import('modb@/budgetManagement/views/budgetAdjustmentHistoryEngine')
        : () => import('modb@/budgetManagement/views/budgetAdjustmentHistory'),
      name: 'budgetAdjustmentHistory',
      meta: {
        title: 'route.budgetAdjustmentHistory',
        requiresAuth: true
      }
    }
  ]
}
