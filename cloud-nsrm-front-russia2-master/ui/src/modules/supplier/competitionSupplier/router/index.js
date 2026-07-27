export default {
  path: '/competitionSupplier',
  name: 'competitionSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'competitionSupplier'
  },
  children: [
    // 竞价项目协同
    {
      path: 'competitionProject',
      component: () => import('mods@/competitionSupplier/views/competitionProject'),
      name: 'competitionProject',
      meta: {
        title: 'route.biddingItems',
        requiresAuth: true
      }
    },
    // 竞价单（新）
    {
      path: 'competitionManageVendor',
      component: () => import('mods@/competitionSupplier/views/competitionManageVendor'),
      name: 'competitionManageVendor',
      meta: {
        title: 'route.biddingItems',
        requiresAuth: true
      }
    },
  ]
}
