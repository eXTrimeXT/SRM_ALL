export default {
  path: '/priceManagementSupplier',
  name: 'priceManagementSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'priceCatalog'
  },
  children: [
    // 价格目录
    {
      path: 'priceCatalog',
      component: () => import('../views/priceCatalog'),
      name: 'priceCatalogSupplier',
      meta: {
        title: 'route.priceCatalog',
        requiresAuth: true
      }
    }
  ]
}
