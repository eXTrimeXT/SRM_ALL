import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/oneStopShoppingSup',
  name: 'oneStopShoppingSup',
  component: () => import('@/layout'),
  redirect: {
    name: 'purchaseCatalog'
  },
  children: [
    {
      path: 'purchaseCatalogOnOrOffSupplier',
      component: () => import('modcs@/oneStopShoppingSup/views/purchaseCatalogOnOrOffSupplier'),
      name: 'purchaseCatalogOnOrOffSupplier',
      meta: {
        title: 'route.purchaseCatalogOnOrOffSupplier', // 内部商城上下架-协同
        requiresAuth: true
      }
    }
  ]
}
