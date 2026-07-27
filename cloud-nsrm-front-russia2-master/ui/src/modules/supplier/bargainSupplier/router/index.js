import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/bargainVendor',
  name: 'bargainVendor',
  component: () => import('@/layout'),
  redirect: {
    name: 'bargainOrders'
  },
  children: [
    // 询价单 /bargainVendor/orders
    {
      path: 'orders',
      name: 'bargainOrders',
      component: () => import('mods@/bargainSupplier/views/bargainOrders'),
      meta: {
        title: 'route.inquiryOrders',
        requiresAuth: true
      }
    },
    // 质疑澄清 /bargainVendor/qa
    {
      path: 'qa',
      component: meiqlCtrl.bargainQa ==='Y'
        ? () => import('mods@/bargainSupplier/views/bargainQaEngine')
        : () => import('mods@/bargainSupplier/views/bargainQa'),
      name: 'bargainVendorQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    }
  ]
}
