export default {
  path: '/performanceManagementSup',
  name: 'performanceManagementSup',
  component: () => import('@/layout'),
  redirect: {
    name: 'graderRating'
  },
  children: [
    {
      path: 'performanceAssessmentVendor',
      component: () =>
        import('mods@/performanceManagementSupplier/views/performanceAssessmentVendor'),
      name: 'performanceAssessmentVendor',
      meta: {
        title: 'route.performanceAssessment', // 供方绩效考核
        requiresAuth: true
      }
    },
    {
      path: 'performanceQueryVendor',
      component: () =>
        import('mods@/performanceManagementSupplier/views/performanceQueryVendor'),
      name: 'performanceQueryVendor',
      meta: {
        title: 'route.performanceQuery', // 绩效结果(供应商)
        requiresAuth: true
      }
    },
    {
      path: 'vendorImprovementVendor',
      component: () =>
        import('mods@/performanceManagementSupplier/views/vendorImprovementVendor'),
      name: 'vendorImprovementVendor',
      meta: {
        title: 'route.vendorImprovement', // 供应商改善(供应商端)
        requiresAuth: true
      }
    }
  ]
}
