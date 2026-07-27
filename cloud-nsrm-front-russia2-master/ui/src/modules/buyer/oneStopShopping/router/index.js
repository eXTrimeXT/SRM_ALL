
import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/oneStopShopping',
  name: 'oneStopShopping',
  component: () => import('@/layout'),
  redirect: {
    name: 'purchaseCatalog'
  },
  children: [
    {
      path: 'purchaseCatalog',
      component: meiqlCtrl.purchaseCatalog === 'Y'
            ? () => import('modb@/oneStopShopping/views/purchaseCatalogEngine')
            : () => import('modb@/oneStopShopping/views/purchaseCatalog'),
      name: 'purchaseCatalog',
      meta: {
        title: 'route.purchaseCatalog',
        requiresAuth: true
      }

    },
    {
      path: 'purchaseCatalogOnOrOff',
      component: meiqlCtrl.purchaseCatalogOnOrOff === 'Y'
            ? () => import('modb@/oneStopShopping/views/purchaseCatalogOnOrOffEngine')
            : () => import('modb@/oneStopShopping/views/purchaseCatalogOnOrOff'),
      name: 'purchaseCatalogOnOrOff',
      meta: {
        title: 'route.purchaseCatalogOnOrOff', // 采购目录上下架
        requiresAuth: true
      }
    },
    {
      path: 'shoppingCart',
      component: meiqlCtrl.shoppingCart === 'Y'
            ? () => import('modb@/oneStopShopping/views/shoppingCartEngine')
            : () => import('modb@/oneStopShopping/views/shoppingCart'),
      name: 'shoppingCart',
      meta: {
        title: 'route.shoppingCart',
        requiresAuth: true
      }
    },
    {
      path: 'purchaseMaterialMaintain',
      component: () => import('modb@/oneStopShopping/views/purchaseMaterialMaintain'),
      name: 'purchaseMaterialMaintain',
      meta: {
        title: 'route.purchaseMaterialMaintain',
        requiresAuth: true
      }
    },
    {
      path: 'priceLibrary',
      component: () => import('modb@/oneStopShopping/views/priceLibrary'),
      name: 'priceLibrary',
      meta: {
        title: 'route.priceLibrary',
        requiresAuth: true
      }
    },
    {
      path: 'vendorPurchaseMaterialMaintain',
      component: () => import('modb@/oneStopShopping/views/vendorPurchaseMaterialMaintain'),
      name: 'vendorPurchaseMaterialMaintain',
      meta: {
        title: 'route.vendorPurchaseMaterialMaintain',
        requiresAuth: true
      }
    }
  ]
}
