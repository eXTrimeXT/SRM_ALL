import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/inquiry',
  name: 'inquiry',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryManagement'
  },
  children: [
    // 询价管理 /inquiry/management
    {
      path: 'management',
      name: 'inquiryManagement',
      component: () => meiqlCtrl.inquiryManagement === 'Y'
        ? import('modb@/inquiry/views/inquiryManagementEngine')
        : import('modcb@/inquiry/views/inquiryManagement'),
      meta: {
        title: 'route.inquiry',
        defaultActive: 'inquiryManagementLTS',
        requiresAuth: true
      }
    },
    // 历史报价单
    {
      path: 'historyQuotePriceBuyer',
      component: () => import('modcs@/inquirySupplier/views/historyQuotePrice'),
      name: 'historyQuotePriceBuyer',
      meta: {
        title: 'cusEntry.route.historyQuotePrice',
        requiresAuth: true
      }
    },
    {
      path: 'priceOrders',
      component: () => import('modcb@/inquiry/views/priceOrders'),
      name: 'priceOrders',
      meta: {
        title: 'cusEntry.route.priceOrders',
        requiresAuth: true
      }
    }
    // FIXME 调试使用，加入白名单，后续删除 询价单 /inquiry/management-engine
    // {
    //   path: 'management-engine',
    //   name: 'inquiryManagementEngine',
    //   // 渲染引擎版
    //   component: () => import('modb@/inquiry/views/inquiryManagementEngine'),
    //   meta: {
    //     title: 'route.inquiry',
    //     defaultActive: 'inquiryManagementLTS',
    //     requiresAuth: true
    //   }
    // }
  ]
}
