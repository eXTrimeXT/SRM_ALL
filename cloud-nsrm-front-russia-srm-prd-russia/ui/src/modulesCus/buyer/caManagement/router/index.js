
export default {
  path: '/caManagement',
  name: 'caManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'calibrationApply'
  },
  children: [
    {
      path: 'calibrationApply',
      component: () => import('@/modulesCus/buyer/caManagement/views/calibrationApply'),
      name: 'calibrationApply',
      meta: {
        title: 'cusEntry.route.calibrationApply', // 定标审批单
        requiresAuth: true
      }
    },
    {
      path: 'calibrationApplyAbandon',
      component: () => import('@/modulesCus/buyer/caManagement/views/calibrationApplyAbandon'),
      name: 'calibrationApplyAbandon',
      meta: {
        title: 'cusEntry.route.calibrationApplyAbandon', // 定标废弃申请
        requiresAuth: true
      }
    }
  ]
}
