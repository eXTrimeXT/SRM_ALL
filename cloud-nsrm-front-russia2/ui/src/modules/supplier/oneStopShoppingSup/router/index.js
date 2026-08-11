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
      component: meiqlCtrl.purchaseCatalogOnOrOff === 'Y'
            ? () => import('mods@/oneStopShoppingSup/views/purchaseCatalogOnOrOffEngine')
            : () => import('mods@/oneStopShoppingSup/views/purchaseCatalogOnOrOffSupplier'),
      name: 'purchaseCatalogOnOrOffSupplier',
      meta: {
        title: 'route.purchaseCatalogOnOrOffSupplier',
        requiresAuth: true
      }
    }
  ]
}
