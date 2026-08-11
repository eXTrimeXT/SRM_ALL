import { meiqlCtrl } from '@/config/meiqlConfig'
import { orderConfig } from '@/config/orderConfig'

export default {
  path: '/vendorOrderSynergy',
  name: 'vendorOrderSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'vendorPurchaseOrder'
  },
  children: [
    {
      path: 'vendorPurchaseOrder',
      component: () => import('modcs@/orderManagementSupplier/views/vendorPurchaseOrder'),
      name: 'vendorPurchaseOrder',
      meta: {
        title: 'route.vendorPurchaseOrder', // 采购订单
        requiresAuth: true
      }
    },
    {
      path: 'vendorDeliveryOrder',
      component: () => import('modcs@/orderManagementSupplier/views/vendorDeliveryOrder'),
      name: 'vendorDeliveryOrder',
      meta: {
        title: 'route.vendorDeliveryOrder', // 送货单
        requiresAuth: true
      }
    },
    {
      path: 'vendorOrderAcceptance',
      component: () => import('modcs@/orderManagementSupplier/views/vendorOrderAcceptance'),
      name: 'vendorOrderAcceptance',
      meta: {
        title: 'cusEntry.route.orderAcceptance', // 订单验收单列表
        requiresAuth: true
      }
    }
  ]
}
