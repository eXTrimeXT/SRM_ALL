
export default {
  path: '/buyerPlanSynergy',
  name: 'buyerPlanSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'buyerMarchRollingForecast'
  },
  children: [
    {
      path: 'buyerMarchRollingForecast',
      component: () =>
        import('modb@/planManagementBuyer/views/buyerMarchRollingForecast'),
      name: 'buyerMarchRollingForecast',
      meta: {
        title: 'route.buyerMarchRollingForecast',
        requiresAuth: true
      }
    },
    {
      path: 'buyerPlanOrders',
      component: () => import('modb@/planManagementBuyer/views/buyerPlanOrders'),
      name: 'buyerPlanOrders',
      meta: {
        title: 'route.buyerPlanOrders',
        requiresAuth: true
      }
    },
    // 供方库存管理
    {
      path: 'supplierInventory',
      component: () => import('modb@/planManagementBuyer/views/supplierInventory'),
      name: 'supplierInventory',
      meta: {
        title: 'route.supplierInventory',
        defaultActive: 'buyersupplierinventory',
        requiresAuth: true
      }
    },
    // 供方库存历史查询
    {
      path: 'supplierInventoryLog',
      component: () => import('modb@/planManagementBuyer/views/supplierInventoryLog'),
      name: 'supplierInventoryLog',
      meta: {
        title: 'route.supplierInventoryLog',
        defaultActive: 'buyersupplierinventorylog',
        requiresAuth: true
      }
    },
    // 供方产能提报
    {
      path: 'capacityreport',
      component: () => import('modb@/planManagementBuyer/views/capacityreport'),
      name: 'capacityreport',
      meta: {
        title: '供方产能提报',
        defaultActive: 'buyercapacityreport',
        requiresAuth: true
      }
    },
    // 供方产能历史记录
    {
      path: 'capacityreporthistory',
      component: () => import('modb@/planManagementBuyer/views/capacityreporthistory'),
      name: 'capacityreporthistory',
      meta: {
        title: '供方产能历史记录',
        defaultActive: 'buyercapacityreporthistory',
        requiresAuth: true
      }
    }

  ]
}
