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
      component:
        meiqlCtrl.vendorPurchaseOrder === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/vendorPurchaseOrderEngine')
          : () => import('mods@/orderManagementSupplier/views/vendorPurchaseOrder'),
      name: 'vendorPurchaseOrder',
      meta: {
        title: 'route.vendorPurchaseOrder', // 采购订单
        requiresAuth: true
      }
    },
    {
      path: 'warehousReturnGoodsVendor',
      component:
        meiqlCtrl.warehousReturnGoodsVendor === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/warehousReturnGoodsVendorEngine')
          : () => import('mods@/orderManagementSupplier/views/warehousReturnGoodsVendor'),
      name: 'warehousReturnGoodsVendor',
      meta: {
        title: 'route.warehousingAndReturnGoods', // 入库退货明细
        requiresAuth: true
      }
    },
    {
      path: 'supplierPurchaseOrderChange',
      component:
        meiqlCtrl.purchaseOrderChangeSupplier === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/supplierPurchaseOrderChangeEngine')
          : () => import('mods@/orderManagementSupplier/views/supplierPurchaseOrderChange'),
      name: 'supplierPurchaseOrderChange',
      meta: {
        title: 'route.purchaseOrderChange', // 采购订单变更
        requiresAuth: true
      }
    },
    {
      path: 'vendorDeliveryOrder',
      component:
        meiqlCtrl.vendorDeliveryOrder === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/vendorDeliveryOrderEngine')
          : () => import('mods@/orderManagementSupplier/views/vendorDeliveryOrder'),
      name: 'vendorDeliveryOrder',
      meta: {
        title: 'route.vendorDeliveryOrder', // 送货单
        requiresAuth: true
      }
    },
    {
      path: 'vendorDeliveryOrderOrigin',
      component: () => import('mods@/orderManagementSupplier/views/vendorDeliveryOrder'),
      name: 'vendorDeliveryOrderOrigin',
      meta: {
        title: 'route.vendorDeliveryOrder',
        requiresAuth: true
      }
    },
    {
      path: 'deliveryAppointments',
      component:
        meiqlCtrl.deliveryAppointmentSupplier === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/deliveryAppointmentEngine')
          : () => import('mods@/orderManagementSupplier/views/deliveryAppointments'),
      name: 'deliveryAppointments',
      meta: {
        title: 'route.deliveryAppointments', // 送货预约
        requiresAuth: true
      }
    },
    {
      path: 'receivedGoodDetails',
      component: () => import('mods@/orderManagementSupplier/views/receivedGoodDetails'),
      name: 'receivedGoodDetails',
      meta: {
        title: 'route.receivedGoodDetails',
        requiresAuth: true
      }
    },
    {
      path: 'orderStorageVendor',
      component: () => import('mods@/orderManagementSupplier/views/orderStorageVendor'),
      name: 'orderStorageVendor',
      meta: {
        title: 'route.orderStorageVendor',
        requiresAuth: true
      }
    },
    {
      path: 'vendorDeliveryNotice',
      component:
        meiqlCtrl.vendorDeliveryNotice === 'Y'
          ? orderConfig.deliveryNoticeByRow === 'Y'
            ? () => import('mods@/orderManagementSupplier/views/vendorDeliveryNoticeByRowEngine')
            : () => import('mods@/orderManagementSupplier/views/vendorDeliveryNoticeEngine')
          : () => import('mods@/orderManagementSupplier/views/vendorDeliveryNotice'),
      name: 'vendorDeliveryNotice',
      meta: {
        title: 'route.vendorDeliveryNotice', // 送货通知单
        requiresAuth: true
      }
    },
    {
      path: 'returnedGoodsNotices',
      component:
        meiqlCtrl.returnedGoodsNoticeSupplier === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/returnedGoodsNoticesEngine')
          : () => import('mods@/orderManagementSupplier/views/returnedGoodsNotices'),
      name: 'returnedGoodsNotices',
      meta: {
        title: 'route.returnedGoodsNotices', // 退货单
        requiresAuth: true
      }
    },
    {
      path: 'carInfoMaintenances',
      component:
        meiqlCtrl.vendorCarInfo === 'Y'
          ? () => import('mods@/orderManagementSupplier/views/carInfoMaintenancesEngine')
          : () => import('mods@/orderManagementSupplier/views/carInfoMaintenances'),
      name: 'carInfoMaintenances',
      meta: {
        title: 'route.carInfoMaintenances',
        requiresAuth: true
      }
    },
    {
      path: 'materialPlan',
      component: () => import('mods@/orderManagementSupplier/views/materialPlan'),
      name: 'materialPlan',
      meta: {
        title: 'route.materialPlanMaintain',
        requiresAuth: true
      }
    },
    {
      path: 'vendorDeliverPlan',
      component: () => import('mods@/orderManagementSupplier/views/vendorDeliverPlan'),
      name: 'vendorDeliverPlan',
      meta: {
        title: 'route.arrivalPlanList',
        requiresAuth: true
      }
    },
    {
      path: 'orderDeliverList',
      component: () => import('mods@/orderManagementSupplier/views/orderDeliverList'),
      name: 'orderDeliverList',
      meta: {
        title: 'route.orderDeliveryDetail',
        requiresAuth: true
      }
    }
  ]
}
