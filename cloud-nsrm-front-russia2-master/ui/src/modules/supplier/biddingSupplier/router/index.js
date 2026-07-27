import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/biddingVendor',
  name: 'biddingVendor',
  component: () => import('@/layout'),
  redirect: {
    name: 'biddingOrders'
  },
  children: [
    // 招标单 /biddingVendor/orders
    {
      path: 'orders',
      name: 'biddingOrders',
      component: () => import('mods@/biddingSupplier/views/biddingOrders'),
      meta: {
        title: 'route.biddingOrders',
        requiresAuth: true
      }
    },
    // 质疑澄清 /biddingVendor/qa
    {
      path: 'qa',
      component: meiqlCtrl.bidQa === 'Y'
        ? () => import('mods@/biddingSupplier/views/biddingQaEngine')
        : () => import('mods@/biddingSupplier/views/biddingQa'),
      name: 'biddingVendorQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    }
  ]
}
