
export default {
  path: '/accountAccessSupplier',
  name: 'accountAccessSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'functionMaintenance'
  },
  children: [
    // 子账号管理
    {
      path: 'accountManagement',
      component: () => import('modcs@/accountAccessSupplier/views/accountManagement'),
      name: 'accountManagement',
      meta: {
        title: 'route.accountManagement',
        requiresAuth: true
      }
    }
  ]
}
