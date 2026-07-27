import { meiqlCtrl } from '@/config/meiqlConfig'
import { orderConfig } from '@/config/orderConfig'

export default {
  path: '/buyerOrderSynergy',
  name: 'buyerOrderSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'buyerPurchaseOrder'
  },
  children: [
    {
      path: 'buyerPurchaseOrder',
      component: () => import('modcb@/orderManagementBuyer/views/buyerPurchaseOrder'),
      name: 'buyerPurchaseOrder',
      meta: {
        title: 'route.buyerPurchaseOrder', // 采购订单
        requiresAuth: true
      }
    },
    {
      path: 'buyerDeliveryOrder',
      component: () => import('modcb@/orderManagementBuyer/views/buyerDeliveryOrder'),
      name: 'buyerDeliveryOrder',
      meta: {
        title: 'route.buyerDeliveryOrder', // 送货单
        requiresAuth: true
      }
    },
    {
      path: 'warehousingAndReturnGoods',
      component: () => import('modcb@/orderManagementBuyer/views/warehousingAndReturnGoods'),
      name: 'warehousingAndReturnGoods',
      meta: {
        title: 'route.warehousingAndReturnGoods', // 订单入库明细
        requiresAuth: true
      }
    },
    {
      path: 'orderAcceptance',
      component: () => import('modcb@/orderManagementBuyer/views/orderAcceptance'),
      name: 'orderAcceptance',
      meta: {
        title: 'cusEntry.route.orderAcceptance', // 订单验收单列表
        requiresAuth: true
      }
    },
    {
      path: 'autoOrderConfig',
      component: () => import('modcb@/orderManagementBuyer/views/autoOrderConfig'),
      name: 'autoOrderConfig',
      meta: {
        title: 'cusEntry.route.autoOrderConfig', // 自动转订单配置规则
        requiresAuth: true
      }
    }
  ]
}
