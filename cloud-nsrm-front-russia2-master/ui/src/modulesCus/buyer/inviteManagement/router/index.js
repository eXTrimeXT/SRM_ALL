
export default {
  path: '/inviteManagement',
  name: 'inviteManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'inviteVendor'
  },
  children: [
    {
      path: 'inviteVendor',
      component: () => import('@/modulesCus/buyer/inviteManagement/views/inviteVendor'),
      name: 'inviteVendor',
      meta: {
        title: 'cusEntry.route.inviteVendor', // 邀请供应商
        requiresAuth: true
      }
    }
  ]
}
