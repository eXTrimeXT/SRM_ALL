export default {
  path: '/techExchangeSupplier',
  name: 'techExchangeSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'techExchangeManagementSupplier'
  },
  children: [
    // 技术交流管理
    {
      path: 'techExchangeManagementSupplier',
      component: () => import('mods@/techExchangeSupplier/views/techExchangeManagementSupplier'),
      name: 'techExchangeManagementSupplier',
      meta: {
        title: 'route.techExchangeManagement',
        requiresAuth: true
      }
    }
  ]
}
