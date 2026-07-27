
export default {
  path: '/biddingSettings',
  name: 'biddingSettings',
  component: () => import('@/layout'),
  redirect: {
    name: 'sourcingPublicityConfig'
  },
  children: [
    {
      path: 'sourcingPublicityConfig',
      component: () => import('@/modulesCus/buyer/biddingSettings/views/sourcingPublicityConfig'),
      name: 'sourcingPublicityConfig',
      meta: {
        title: 'cusEntry.route.sourcingPublicityConfig', // 寻源公示配置
        requiresAuth: true
      }
    },
    {
      path: 'sourcingScoreConfig',
      component: () => import('@/modulesCus/buyer/biddingSettings/views/sourcingScoreConfig'),
      name: 'sourcingScoreConfig',
      meta: {
        title: 'cusEntry.route.sourcingScoreConfig', // 评分配置
        requiresAuth: true
      }
    }
  ]
}
