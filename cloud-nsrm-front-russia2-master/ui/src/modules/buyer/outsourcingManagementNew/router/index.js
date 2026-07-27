export default {
  path: '/outsourcingManagementNew',
  name: 'outsourcingManagementNew',
  component: () => import('@/layout'),
  redirect: {
    name: 'outsourceMaterials'
  },
  children: [
    {
      path: 'outsourceMaterials',
      component: () => import('modb@/outsourcingManagementNew/views/outsourceMaterials'),
      name: 'outsourceMaterials',
      meta: {
        title: 'route.outsourceMaterials', // 委外领料单
        requiresAuth: true
      }
    },
    {
      path: 'outsourceReturnMaterials',
      component: () => import('modb@/outsourcingManagementNew/views/outsourceReturnMaterials'),
      name: 'outsourceReturnMaterials',
      meta: {
        title: 'route.outsourceReturnMaterials', // 委外退料单
        requiresAuth: true
      }
    },
    {
      path: 'outsourcingBomNew',
      component: () => import('modb@/outsourcingManagementNew/views/outsourcingBom'),
      name: 'outsourcingBomNew',
      meta: {
        title: 'route.outsourcingBomNew', // 委外BOM维护
        requiresAuth: true
      }
    },
    {
      path: 'outsourceMaterialHead',
      component: () => import('modb@/outsourcingManagementNew/views/outsourceMaterialList'),
      name: 'outsourceMaterialHead',
      meta: {
        title: 'route.outsourceMaterialHead', // 委外用料清单
        requiresAuth: true
      }
    },
    {
      path: 'outsourceMaterialChange',
      component: () => import('modb@/outsourcingManagementNew/views/outsourceMaterialListChange'),
      name: 'outsourceMaterialChange',
      meta: {
        title: 'route.outsourceMaterialChange', // 委外用料清单变更
        requiresAuth: true
      }
    },
    {
      path: 'osVendorInvBuyer',
      component: () => import('modb@/outsourcingManagementNew/views/osVendorInvBuyer'),
      name: 'osVendorInvBuyer',
      meta: {
        title: 'route.osVendorInvBuyer', // 供方委外库存盘点
        requiresAuth: true
      }
    }
  ]
}
