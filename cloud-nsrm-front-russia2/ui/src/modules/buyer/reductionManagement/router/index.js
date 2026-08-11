export default {
    path: '/reductionManagement',
    name: 'reductionManagement',
    component: () => import('@/layout'),
    redirect: {
        name: 'salePlan'
    },
    children: [
      {
        path: 'salePlan',
        component: () =>
            import('modb@/reductionManagement/views/salePlan'),
        name: 'salePlan',
        meta: {
            title: "cusEntry.supplement20250211.productionSalesPlan",
            requiresAuth: true
        }
      }
    ]
  }
