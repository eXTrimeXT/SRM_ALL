import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/performanceManagement',
  name: 'performanceManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'graderRating'
  },
  children: [
    {
      path: 'vendorDemotion',
      component: meiqlCtrl.vendorDemotion === 'Y'
      ? () => import('modb@/performanceManagement/views/vendorDemotionEngine')
      : () => import('modb@/performanceManagement/views/vendorDemotion'),
      name: 'vendorDemotion',
      meta: {
        title: 'route.vendorDemotion',
        requiresAuth: true
      }
    },
    {
      path: 'graderRating',
      component: () => import('modb@/performanceManagement/views/graderRating'),
      name: 'graderRating',
      meta: {
        title: 'route.graderRating',
        requiresAuth: true
      }
    },
    {
      path: 'indicators',
      component: () => import('modb@/performanceManagement/views/indicators'),
      name: 'indicators',
      meta: {
        title: 'route.indicators',
        requiresAuth: true
      }
    },
    {
      path: 'performanceAssessment',
      component: () =>
        import('modb@/performanceManagement/views/performanceAssessment'),
      name: 'performanceAssessment',
      meta: {
        title: 'route.performanceAssessment',
        requiresAuth: true
      }
    },
    {
      path: 'performanceModel',
      component: () =>
        import('modb@/performanceManagement/views/performanceModel'),
      name: 'performanceModel',
      meta: {
        title: 'route.performanceModel',
        requiresAuth: true
      }
    },
    {
      path: 'performanceQuery',
      component: () =>
        import('modb@/performanceManagement/views/performanceQuery'),
      name: 'performanceQuery',
      meta: {
        title: 'route.performanceQuery',
        requiresAuth: true
      }
    },
    {
      path: 'performanceRating',
      component: () =>
        import('modb@/performanceManagement/views/performanceRating'),
      name: 'performanceRating',
      meta: {
        title: 'route.performanceRating',
        requiresAuth: true
      }
    },
    {
      path: 'performanceScoreItems',
      component: () =>
        import('modb@/performanceManagement/views/performanceScoreItems'),
      name: 'performanceScoreItems',
      meta: {
        title: 'route.performanceScoreItems',
        requiresAuth: true
      }
    },
    {
      path: 'vendorImprovement',
      component: () =>
        import('modb@/performanceManagement/views/vendorImprovement'),
      name: 'vendorImprovement',
      meta: {
        title: 'route.vendorImprovement',
        requiresAuth: true
      }
    },
    {
      path: 'performanceWarning',
      component: () =>
        import('modb@/performanceManagement/views/performanceWarning'),
      name: 'performanceWarning',
      meta: {
        title: 'route.performanceWarning',
        requiresAuth: true
      }
    }
  ]
}
