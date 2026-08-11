import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/inquiryVendor',
  name: 'inquiryVendor',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryOrders'
  },
  children: [
    // 询价单 /inquiryVendor/orders
    {
      path: 'orders',
      component: () => meiqlCtrl.inquiryVendorOrders === 'Y'
        ? import('mods@/inquirySupplier/views/inquiryOrdersEngine')
        : import('mods@/inquirySupplier/views/inquiryOrders'),
      name: 'inquiryOrders',
      meta: {
        title: 'route.inquiryOrders',
        defaultActive: 'inquiryVendorOrders',
        requiresAuth: true
      }
    }
    // FIXME 调试使用，加入白名单，后续删除 询价单 /inquiryVendor/orders-engine
    // {
    //   path: 'orders-engine',
    //   // 渲染引擎版
    //   component: () => import('mods@/inquirySupplier/views/inquiryOrdersEngine'),
    //   name: 'inquiryOrdersEngine',
    //   meta: {
    //     title: 'route.inquiryOrders',
    //     defaultActive: 'inquiryVendorOrders',
    //     requiresAuth: true
    //   }
    // }
  ]
}
