
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
      path: 'purchaseCatalogOnOrOff',
      component: () => import('modcb@/oneStopShopping/views/purchaseCatalogOnOrOff'),
      name: 'purchaseCatalogOnOrOff',
      meta: {
        title: 'route.purchaseCatalogOnOrOff', // 采购目录上下架
        requiresAuth: true
      }
    },
    {
      path: 'purchaseCatalog',
      component: meiqlCtrl.purchaseCatalog === 'Y'
        ? () => import('modcb@/oneStopShopping/views/purchaseCatalogEngine')
        : () => import('modb@/oneStopShopping/views/purchaseCatalog'),
      name: 'purchaseCatalog',
      meta: {
        title: 'route.purchaseCatalog',
        requiresAuth: true
      }
    },
    {
      path: 'shoppingCart',
      component: meiqlCtrl.shoppingCart === 'Y'
        ? () => import('modcb@/oneStopShopping/views/shoppingCartEngine')
        : () => import('modb@/oneStopShopping/views/shoppingCart'),
      name: 'shoppingCart',
      meta: {
        title: 'route.shoppingCart',
        requiresAuth: true
      }
    }
    ,{
      path: 'platformMapping',
      component: () => import('modcb@/oneStopShopping/views/platformMapping'),
      name: 'platformMapping',
      meta: {
        title: '平台映射关系',
        requiresAuth: true
      }
    }
  ]
}
