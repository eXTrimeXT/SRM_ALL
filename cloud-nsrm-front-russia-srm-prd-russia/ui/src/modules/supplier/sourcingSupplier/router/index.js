export default {
  path: '/sourcingSup',
  name: 'sourcing',
  component: () => import('@/layout'),
  redirect: {
    name: 'sourcingApplicationSupplier'
  },
  children: [
    // 寻源需求报名列表 /sourcingSup/sourcingApplicationSupplier
    {
      path: 'sourcingApplicationSupplier',
      component: () => import('mods@/sourcingSupplier/views/sourcingApplicationSupplier'),
      name: 'sourcingApplicationSupplier',
      meta: {
        title: 'route.sourcingApplicationSupplier',
        requiresAuth: true
      }
    }
  ]
}
