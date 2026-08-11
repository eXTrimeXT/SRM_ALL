export default {
  path: '/outsourcingManagementSup',
  name: 'outsourcingManagementSup',
  component: () => import('@/layout'),
  redirect: {
    name: 'outsourcingBom'
  },
  children: [
    {
      path: 'vendorOsmaterialrequisition',
      component: () => import('mods@/outsourcingManagementSupplier/views/vendorOsmaterialrequisition'),
      name: 'vendorOsmaterialrequisition',
      meta: {
        title: 'route.outsourceMaterialsSup', // 供方委外领料单
        requiresAuth: true
      }
    },
    {
      path: 'osVendorInvForVendor',
      component: () => import('mods@/outsourcingManagementSupplier/views/osVendorInv/vendorInvForVendor'),
      name: 'vendorInvForVendor',
      meta: {
        title: 'outsource.countingCollaboration', // 供方库存盘点协同
        requiresAuth: true
      }
    }
  ]
}
