export default {
  path: '/souConfiguration',
  name: 'souConfiguration',
  component: () => import('@/layout'),
  redirect: {
    name: 'souConfigurationScoreRule'
  },
  children: [
    // 评分规则 /souConfiguration/scoreRule
    {
      path: 'scoreRule',
      name: 'souConfigurationScoreRule',
      component: () => import('modb@/souConfiguration/views/scoreRule'),
      meta: {
        title: 'route.scoreRule',
        requiresAuth: true
      }
    },
    // 项目式询价流程配置 /souConfiguration/bargainFlowConfigure
    {
      path: 'bargainFlowConfigure',
      name: 'bargainFlowConfigure',
      component: () => import('modb@/souConfiguration/views/bargainFlowConfigure'),
      meta: {
        title: 'route.bargainFlowConfigure',
        requiresAuth: true
      }
    },
    // 招标流程配置 /souConfiguration/biddingFlowConfigure
    {
      path: 'biddingFlowConfigure',
      name: 'biddingFlowConfigure',
      component: () => import('modb@/souConfiguration/views/biddingFlowConfigure'),
      meta: {
        title: 'route.biddingFlowConfigure',
        requiresAuth: true
      }
    },
    // 竞价流程配置 /souConfiguration/competitionFlowConfigure
    {
      path: 'competitionFlowConfigure',
      name: 'competitionFlowConfigure',
      component: () => import('modb@/souConfiguration/views/competitionFlowConfigure'),
      meta: {
        title: 'route.competitionFlowConfigure',
        requiresAuth: true
      }
    }
  ]
}
