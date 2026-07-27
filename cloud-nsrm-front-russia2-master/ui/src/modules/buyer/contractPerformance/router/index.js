export default {
  path: '/contractPerformance',
  name: 'contractPerformance',
  component: () => import('@/layout'),
  redirect: {
    name: 'contractPerformanceProcessConfig'
  },
  children: [
    {
      path: 'contractPerformanceProcessConfig',
      component: () => import('modb@/contractPerformance/views/contractPerformanceProcessConfig'),
      name: 'contractPerformanceProcessConfig',
      meta: {
        title: 'route.contractPerformanceProcessConfig',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceBill',
      component: () => import('modb@/contractPerformance/views/contractPerformanceBill'),
      name: 'contractPerformanceBill',
      meta: {
        title: 'route.contractPerformanceBill',
        requiresAuth: true
      }
    },
    {
      path: 'vendorContractPerformanceBill',
      component: () => import('modb@/contractPerformance/views/vendorContractPerformanceBill'),
      name: 'vendorContractPerformanceBill',
      meta: {
        title: 'route.vendorContractPerformanceBill',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceReport',
      component: () => import('modb@/contractPerformance/views/contractPerformanceReport'),
      name: 'contractPerformanceReport',
      meta: {
        title: 'route.contractPerformanceReport',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformancePlan',
      component: () => import('modb@/contractPerformance/views/contractPerformancePlan'),
      name: 'contractPerformancePlan',
      meta: {
        title: 'route.contractPerformancePlan',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceCheck',
      component: () => import('modb@/contractPerformance/views/contractPerformanceCheck'),
      name: 'contractPerformanceCheck',
      meta: {
        title: 'route.contractPerformanceCheck',
        requiresAuth: true
      }
    },
    {
      path: 'contractPerformanceInvoice',
      component: () => import('modb@/contractPerformance/views/contractPerformanceInvoice'),
      name: 'contractPerformanceInvoice',
      meta: {
        title: 'route.contractPerformanceInvoice',
        requiresAuth: true
      }
    }
  ]
}
