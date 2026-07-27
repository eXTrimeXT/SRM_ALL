export default {
  path: '/baseSettingCommon',
  name: 'baseSettingCommon',
  component: () => import('@/layout'),
  redirect: {
    name: 'outsourcingBom'
  },
  children: [
    // 物料维护
    {
      path: 'materialMaintenance',
      component: () => import('mod@/common/baseSettingCommon/views/materialMaintenance'),
      name: 'materialMaintenance',
      meta: {
        title: 'route.materialMaintenance',
        requiresAuth: true
      }
    },
    // 物料维护共用相同文件、区分入口页
    {
      path: 'materialMaintenanceSupplier',
      component: () => import('mod@/common/baseSettingCommon/views/materialMaintenance/supplierIndex'),
      name: 'materialMaintenanceSupplier',
      meta: {
        title: 'route.materialMaintenance',
        requiresAuth: true
      }
    },
    // 动态报表 /baseSetting/dynamicReportPage
    // 采购商和供应商都需要用到动态报表
    {
      path: 'dynamicReportPage/:sqlCode',
      component: () => import('modb@/basicSetting/views/dynamicReportPage'),
      name: 'dynamicReportPage',
      meta: {
        title: 'route.dynamicReportPage',
        requiresAuth: true
      }
    }
  ]
}
