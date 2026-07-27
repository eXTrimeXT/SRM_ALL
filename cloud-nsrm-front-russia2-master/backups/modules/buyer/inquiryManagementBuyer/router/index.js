export default {
  path: '/inquiryManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'partMapping'
  },
  children: [
    {
      path: 'partMapping',
      component: () =>
        import('modb@/inquiryManagementBuyer/views/partMapping'),
      name: 'partMapping',
      meta: {
        title: '零件映射维护',
        requiresAuth: true
      }
    }
  ]
}
