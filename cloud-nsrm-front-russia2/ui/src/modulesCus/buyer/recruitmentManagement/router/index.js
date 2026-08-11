
export default {
  path: '/recruitmentManagement',
  name: 'recruitmentManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'recruitment'
  },
  children: [
    {
      path: 'recruitment',
      component: () => import('@/modulesCus/buyer/recruitmentManagement/views/recruitment'),
      name: 'recruitment',
      meta: {
        title: 'cusEntry.route.recruitment', // 招募管理
        requiresAuth: true
      }
    }
  ]
}
