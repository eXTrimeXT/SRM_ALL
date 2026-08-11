export default {
  path: '/contractPerformanceSupplier',
  name: 'contractPerformanceSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'contractPerformancePlanVendor'
  },
  children: [
    {
      path: 'contractPerformancePlanVendor',
      component: () => import('mods@/contractPerformanceSupplier/views/contractPerformancePlanVendor'),
      name: 'contractPerformancePlanVendor',
      meta: {
        title: 'route.contractPerformancePlan',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceCheckVendor',
      component: () => import('mods@/contractPerformanceSupplier/views/contractPerformanceCheckVendor'),
      name: 'contractPerformanceCheckVendor',
      meta: {
        title: 'route.contractPerformanceCheck',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceInvoiceVendor',
      component: () => import('mods@/contractPerformanceSupplier/views/contractPerformanceInvoiceVendor'),
      name: 'contractPerformanceInvoiceVendor',
      meta: {
        title: 'route.contractPerformanceInvoice',
        requiresAuth: true
      }
    }
  ]
}
