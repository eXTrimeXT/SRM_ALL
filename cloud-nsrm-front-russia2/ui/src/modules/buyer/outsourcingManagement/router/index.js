export default {
  path: '/outsourcingManagement',
  name: 'outsourcingManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'outsourcingBom'
  },
  children: [
    {
      path: 'outsourcingBom',
      component: () => import('modb@/outsourcingManagement/views/outsourcingBom'),
      name: 'outsourcingBom',
      meta: {
        title: "route.outsourcingBomNew",  // '委外BOM维护'
        requiresAuth: true
      }
    },
    {
      path: 'osmaterialrequisition',
      component: () => import('modb@/outsourcingManagement/views/osmaterialrequisition'),
      name: 'osmaterialrequisition',
      meta: {
        title: "route.outsourceMaterials",  // '委外领料单'
        requiresAuth: true
      }
    },
    {
      path: 'realTimeInv',
      component: () => import('modb@/outsourcingManagement/views/osVendorInv/realTimeInv'),
      name: 'realTimeInv',
      meta: {
        title: "cusEntry.supplement20250211.supplierOutsourcedInventoryDetails",  // '供方委外库存明细'
        requiresAuth: true
      }
    },
    {
      path: 'osVendorInv',
      component: () => import('modb@/outsourcingManagement/views/osVendorInv/vendorInv'),
      name: 'vendorInv',
      meta: {
        title: "route.osVendorInvBuyer",  //'供方委外库存盘点'
        requiresAuth: true
      }
    }
  ]
}
