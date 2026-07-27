export default {
  path: '/techExchange',
  name: 'techExchange',
  component: () => import('@/layout'),
  redirect: {
    name: 'techExchangeManagement'
  },
  children: [
    // 技术交流管理
    {
      path: 'techExchangeManagement',
      component: () => import('modb@/techExchange/views/techExchangeManagement'),
      name: 'techExchangeManagement',
      meta: {
        title: 'route.techExchangeManagement',
        requiresAuth: true
      }
    }
  ]
}
