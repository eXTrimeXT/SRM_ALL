export default {
  path: '/vendorPlanSynergy',
  name: 'vendorPlanSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'marchRollingForecast'
  },
  children: [
    // 供方产能历史记录
    {
      path: 'capacityreporthistory',
      component: () => import('mods@/planManagementSupplier/views/capacityreporthistory'),
      name: 'capacityreporthistory',
      meta: {
        title: 'route.capacityreporthistory',
        requiresAuth: true
      }
    },
    // 供方产能提报
    {
      path: 'capacityreport',
      component: () => import('mods@/planManagementSupplier/views/capacityreport'),
      name: 'capacityreport',
      meta: {
        title: 'route.capacityreport',
        requiresAuth: true
      }
    },
    // 供方库存历史查询
    {
      path: 'supplierInventoryLog',
      component: () => import('mods@/planManagementSupplier/views/supplierInventoryLog'),
      name: 'supplierInventoryLog',
      meta: {
        title: 'route.supplierInventoryLog',
        defaultActive: 'supplierinventorylog',
        requiresAuth: true
      }
    },
    // 供方库存管理
    {
      path: 'supplierInventorySup',
      component: () => import('mods@/planManagementSupplier/views/supplierInventory'),
      name: 'supplierInventorySup',
      meta: {
        title: 'route.supplierInventory',
        defaultActive: 'supplierInventoryV',
        requiresAuth: true
      }
    },
    {
      path: 'marchRollingForecast',
      component: () =>
        import('mods@/planManagementSupplier/views/marchRollingForecast'),
      name: 'marchRollingForecast',
      meta: {
        title: 'route.buyerMarchRollingForecast',
        requiresAuth: true
      }
    },
    {
      path: 'materialReport',
      component: () =>
        import('mods@/planManagementSupplier/views/materialReport'),
      name: 'materialReport',
      meta: {
        title: 'route.materialReport', // 物料明细报表
        requiresAuth: true
      }
    },
    {
      path: 'reportsPurchaseApplication',
      component: () =>
        import('mods@/planManagementSupplier/views/reportsPurchaseApplication'),
      name: 'reportsPurchaseApplication',
      meta: {
        title: 'planMod.purchaseApplyReport', // 采购申请报表
        requiresAuth: true
      }
    },
    {
      path: 'planOrders',
      component: () => import('mods@/planManagementSupplier/views/planOrders'),
      name: 'planOrders',
      meta: {
        title: 'route.buyerPlanOrders',
        requiresAuth: true
      }
    }
  ]
}
