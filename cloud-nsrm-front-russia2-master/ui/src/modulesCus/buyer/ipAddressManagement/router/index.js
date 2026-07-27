
export default {
  path: '/ipAddressManagement',
  name: 'ipAddressManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'ipAddress'
  },
  children: [
    {
      path: 'ipAddress',
      component: () => import('@/modulesCus/buyer/ipAddressManagement/views/ipAddress'),
      name: 'ipAddress',
      meta: {
        title: 'cusEntry.route.ipAddress', // IP地址管理
        requiresAuth: true
      }
    }
  ]
}
