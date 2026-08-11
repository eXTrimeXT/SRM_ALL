export default {
  path: '/sourcing',
  name: 'sourcing',
  component: () => import('@/layout'),
  redirect: {
    name: 'sourcingRequireBuyer'
  },
  children: [
    // 寻源需求列表
    {
      path: 'sourcingRequireBuyer',
      component: () => import('modcb@/sourcing/views/sourcingRequireBuyer'),
      name: 'sourcingRequireBuyer',
      meta: {
        
        // title: '寻源需求列表',
        title: () => "route.sourcingApplicationBuyer",
        requiresAuth: true
      }
    }
  ]
}
