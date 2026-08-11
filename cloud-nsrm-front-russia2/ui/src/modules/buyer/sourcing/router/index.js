export default {
  path: '/sourcing',
  name: 'sourcing',
  component: () => import('@/layout'),
  redirect: {
    name: 'sourcingApplicationBuyer'
  },
  children: [
    // 寻源需求列表
    {
      path: 'sourcingApplicationBuyer',
      component: () => import('modb@/sourcing/views/sourcingApplicationBuyer'),
      name: 'sourcingApplicationBuyer',
      meta: {
        title: 'route.sourcingApplicationBuyer',
        requiresAuth: true
      }
    },
    // 寻源需求详情
    {
      path: 'sourcingApplicationDetail',
      component: () => import('modb@/sourcing/views/sourcingApplicationBuyer/detail'),
      name: 'sourcingApplicationDetail',
      meta: {
        title: 'route.sourcingApplicationDetail',
        requiresAuth: true
      }
    },
    // 报价查询报表
    {
      path: 'quoteReport',
      component: () => import('modb@/sourcing/views/quoteReport'),
      name: 'quoteReport',
      meta: {
        title: 'route.quoteReport',
        requiresAuth: true
      }
    },
    // 需求监控报表
    {
      path: 'requirementMonitorReport',
      component: () => import('modb@/sourcing/views/requirementMonitorReport'),
      name: 'requirementMonitorReport',
      meta: {
        title: 'route.requirementMonitorReport',
        requiresAuth: true
      }
    },
    // 寻源监控报表
    {
      path: 'sourcingMonitorReport',
      component: () => import('modb@/sourcing/views/sourcingMonitorReport'),
      name: 'sourcingMonitorReport',
      meta: {
        title: 'cusEntry.supplement20250211.sourceMonitoringReport',
        requiresAuth: true
      }
    }
  ]
}
