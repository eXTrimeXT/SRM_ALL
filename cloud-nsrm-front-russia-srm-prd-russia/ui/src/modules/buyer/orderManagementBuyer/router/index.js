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
      component: meiqlCtrl.buyerPurchaseOrder === 'Y'
        ? () => import('modb@/orderManagementBuyer/views/buyerPurchaseOrderNewEngine')
        : () => import('modb@/orderManagementBuyer/views/buyerPurchaseOrder'),
      name: 'buyerPurchaseOrder',
      meta: {
        title: 'route.buyerPurchaseOrder', // 采购订单
        requiresAuth: true
      }
    },
    {
      path: 'purchaseOrderChange',
      component: meiqlCtrl.purchaseOrderChangeBuyer === 'Y'
        ? () => import('modb@/orderManagementBuyer/views/purchaseOrderChangeEngine')
        : () => import('modb@/orderManagementBuyer/views/purchaseOrderChange'),
      name: 'purchaseOrderChange',
      meta: {
        title: 'route.purchaseOrderChange', // 采购订单变更
        requiresAuth: true
      }
    },
    {
      path: 'warehousingAndReturnGoods',
      component:
        meiqlCtrl.warehousingAndReturnGoods === 'Y'
          ? () => import('modb@/orderManagementBuyer/views/warehousingAndReturnGoodsEngine')
          : () => import('modb@/orderManagementBuyer/views/warehousingAndReturnGoods'),
      name: 'warehousingAndReturnGoods',
      meta: {
        title: 'route.warehousingAndReturnGoods', // 订单入库明细
        requiresAuth: true
      }
    },
    {
      path: 'orderStorage',
      component:
        meiqlCtrl.orderStorage === 'Y'
          ? () => import('modb@/orderManagementBuyer/views/orderStorageEngine')
          : () => import('modb@/orderManagementBuyer/views/orderStorage'),
      name: 'orderStorage',
      meta: {
        title: 'route.orderStorage', // 订单入库
        requiresAuth: true
      }
    },
    {
      path: 'buyerDeliveryNotice',
      component:
        meiqlCtrl.buyerDeliveryNotice === 'Y'
          ? orderConfig.deliveryNoticeByRow === 'Y'
            ? () => import('modb@/orderManagementBuyer/views/buyerDeliveryNoticeByRowEngine')
            : () => import('modb@/orderManagementBuyer/views/buyerDeliveryNoticeEngine')

          : () => import('modb@/orderManagementBuyer/views/buyerDeliveryNotice'),
      name: 'buyerDeliveryNotice',
      meta: {
        title: 'route.buyerDeliveryNotice', // 送货通知单
        requiresAuth: true
      }
    },
    {
      path: 'buyerDeliveryOrder',
      component:
        meiqlCtrl.buyerDeliveryOrder === 'Y'
          ? () => import('modb@/orderManagementBuyer/views/buyerDeliveryOrderEngine')
          : () => import('modb@/orderManagementBuyer/views/buyerDeliveryOrder'),
      name: 'buyerDeliveryOrder',
      meta: {
        title: 'route.buyerDeliveryOrder', // 送货单
        requiresAuth: true
      }
    },
    {
      path: 'deliveryAppointment',
      component: meiqlCtrl.deliveryAppointment === 'Y'
        ? () => import('modb@/orderManagementBuyer/views/deliveryAppointmentEngine')
        : () => import('modb@/orderManagementBuyer/views/deliveryAppointment'),
      name: 'deliveryAppointment',
      meta: {
        title: 'route.deliveryAppointment', // 送货预约
        requiresAuth: true
      }
    },
    {
      path: 'receivedGoodDetail',
      component: () =>
        import('modb@/orderManagementBuyer/views/receivedGoodDetail'),
      name: 'receivedGoodDetail',
      meta: {
        title: 'route.receivedGoodDetail',
        requiresAuth: true
      }
    },
    {
      path: 'returnedGoodsNotice',
      component:
        meiqlCtrl.returnedGoodsNoticeBuyer === 'Y'
          ? () => import('modb@/orderManagementBuyer/views/returnedGoodsNoticeEngine')
          : () => import('modb@/orderManagementBuyer/views/returnedGoodsNotice'),
      name: 'returnedGoodsNotice',
      meta: {
        title: 'route.returnedGoodsNotice', // 退货单
        requiresAuth: true
      }
    },
    {
      path: 'carInfoMaintenance',
      component: meiqlCtrl.buyerCarInfo === 'Y'
        ? () => import('modb@/orderManagementBuyer/views/carInfoMaintenanceEngine')
        : () => import('modb@/orderManagementBuyer/views/carInfoMaintenance'),
      name: 'carInfoMaintenance',
      meta: {
        title: 'route.carInfoMaintenance',
        requiresAuth: true
      }
    },
    {
      path: 'returnGoodsBill',
      component: () => import('modb@/contractManagement/views/returnGoodsBill'),
      name: 'returnGoodsBill',
      meta: {
        title: 'route.returnGoodsBill',
        requiresAuth: true
      }
    },
    {
      path: 'purchaseAnalysis',
      component: () => import('modb@/orderManagementBuyer/views/purchaseAnalysis'),
      name: 'purchaseAnalysis',
      meta: {
        title: 'route.purchaseAnalysis',
        requiresAuth: true
      }
    },
    { // 演示用，后期拉通再调整
      path: 'orderProgressReport',
      name: 'orderProgressReport',
      component: () => import('modb@/orderManagementBuyer/views/orderProgressReport'),
      mera: {
        title: '采购计划执行报表',
        requiresAuth: true
      }
    }
  ]
}
