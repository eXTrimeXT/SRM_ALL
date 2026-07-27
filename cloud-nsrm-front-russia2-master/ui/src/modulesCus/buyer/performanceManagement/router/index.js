import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/performanceManagement',
  name: 'performanceManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'graderRating'
  },
  children: [
    // 项目化评分结果预警
    {
      path: 'XMperformanceWarning',
      component: () => import('modc@/buyer/performanceManagement/views/XMperformanceWarning'),
      name: 'XMperformanceWarning',
      meta: {
        title: 'route.XMperformanceWarning',
        requiresAuth: true
      }
    },
    // 供应商预警
    {
      path: 'yujingdan',
      component: () => import('modc@/buyer/performanceManagement/views/yujingdan'),
      name: 'yujingdan',
      meta: {
        title: 'route.yujingdan',
        requiresAuth: true
      }
    },
    // 项目化绩效查询
    {
      path: 'XMperformanceQuery',
      component: () => import('modc@/buyer/performanceManagement/views/XMperformanceQuery'),
      name: 'XMperformanceQuery',
      meta: {
        title: 'route.XMperformanceQuery',
        requiresAuth: true
      }
    },
    // 订单复核
    {
      path: 'orderReview',
      component: () => import('modc@/buyer/performanceManagement/views/orderReview'),
      name: 'orderReview',
      meta: {
        title: 'route.orderReview',
        requiresAuth: true
      }
    },
    // 项目化复核
    {
      path: 'XMorderReview',
      component: () => import('modc@/buyer/performanceManagement/views/XMorderReview'),
      name: 'XMorderReview',
      meta: {
        title: 'route.XMorderReview',
        requiresAuth: true
      }
    },
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
      component: () => import('modc@/buyer/performanceManagement/views/graderRating'),
      name: 'graderRating',
      meta: {
        title: 'route.graderRating',
        requiresAuth: true
      }
    },
    {
      path: 'indicators',
      component: () => import('modc@/buyer/performanceManagement/views/indicators'),
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
        import('modc@/buyer/performanceManagement/views/performanceModel'),
      name: 'performanceModel',
      meta: {
        title: 'route.performanceModel',
        requiresAuth: true
      }
    },
    {
      path: 'performanceQuery',
      component: () =>
        import('modc@/buyer/performanceManagement/views/performanceQuery'),
      name: 'performanceQuery',
      meta: {
        title: 'route.performanceQuery',
        requiresAuth: true
      }
    },
    {
      path: 'performanceRating',
      component: () =>
        import('modc@/buyer/performanceManagement/views/performanceRating'),
      name: 'performanceRating',
      meta: {
        title: 'route.performanceRating',
        requiresAuth: true
      }
    },
    {
      path: 'performanceScoreItems',
      component: () =>
        import('modc@/buyer/performanceManagement/views/performanceScoreItems'),
      name: 'performanceScoreItems',
      meta: {
        title: 'route.performanceScoreItems',
        requiresAuth: true
      }
    },
    {
      path: 'XMperformanceScoreItems',
      component: () =>
        import('modc@/buyer/performanceManagement/views/XMperformanceScoreItems'),
      name: 'XMperformanceScoreItems',
      meta: {
        title: 'route.XMperformanceScoreItems',
        requiresAuth: true
      }
    },
    {
      path: 'XMpingFen',
      component: () =>
        import('modc@/buyer/performanceManagement/views/XMpingFen'),
      name: 'XMpingFen',
      meta: {
        title: 'route.XMpingFen',
        requiresAuth: true
      }
    },
    // {
    //   path: 'fuheOrder',
    //   component: () =>
    //     import('modc@/buyer/performanceManagement/views/fuheOrder'),
    //   name: 'fuheOrder',
    //   meta: {
    //     title: 'route.fuheOrder',
    //     requiresAuth: true
    //   }
    // },
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
        import('modc@/buyer/performanceManagement/views/performanceWarning'),
      name: 'performanceWarning',
      meta: {
        title: 'route.performanceWarning',
        requiresAuth: true
      }
    }
  ]
}
