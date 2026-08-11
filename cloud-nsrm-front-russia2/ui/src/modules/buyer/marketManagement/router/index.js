export default {
    path: '/marketManagement',
    name: 'marketManagement',
    component: () => import('@/layout'),
    redirect: {
      name: 'marketManagement'
    },
    children: [
      {
        path: 'materialQuotation',
        component: () => import('modb@/marketManagement/views/materialQuotation.vue'),
        name: 'materialQuotation',
        meta: {
          // '原材料行情维护'
          title: "cusEntry.supplement20250211.rawMaterialMarketMaintenance",
          requiresAuth: true
        }
      }

    ]
  }
