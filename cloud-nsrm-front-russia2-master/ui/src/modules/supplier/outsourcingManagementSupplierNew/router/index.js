export default {
  path: '/outsourcingManagementSupNew',
  name: 'outsourcingManagementSupNew',
  component: () => import('@/layout'),
  redirect: {
    name: 'outsourceMaterialsSup'
  },
  children: [
    {
      path: 'outsourceMaterialsSup',
      component: () => import('mods@/outsourcingManagementSupplierNew/views/outsourceMaterialsSup'),
      name: 'outsourceMaterialsSup',
      meta: {
        title: 'route.outsourceMaterialsSup', // 供方委外领料单
        requiresAuth: true
      }
    },
    {
      path: 'outsourceReturnMaterialsSup',
      component: () => import('mods@/outsourcingManagementSupplierNew/views/outsourceReturnMaterialsSup'),
      name: 'outsourceReturnMaterialsSup',
      meta: {
        title: 'route.outsourceReturnMaterialsSup', // 供方委外退料单
        requiresAuth: true
      }
    },
    {
      path: 'osVendorInv',
      component: () => import('mods@/outsourcingManagementSupplierNew/views/osVendorInv'),
      name: 'osVendorInv',
      meta: {
        title: 'route.osVendorInv', // 供方委外盘点协同
        requiresAuth: true
      }
    }
  ]
}
