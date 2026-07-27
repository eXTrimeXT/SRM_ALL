
export default {
  path: '/inspectManagement',
  name: 'inspectManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'inspectManage'
  },
  children: [
    {
      path: 'inspectManage',
      component: () => import('@/modulesCus/buyer/inspectManagement/views/inspectManage'),
      name: 'inspectManage',
      meta: {
        title: 'cusEntry.route.inspectManage', // 考察管理列表
        requiresAuth: true
      }
    }
  ]
}
