export default {
  path: '/inquiryManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryBySimpleListBuyer'
  },
  children: [
    // 询价管理
    {
      path: 'inquiryBySimpleListBuyer',
      component: () => import('modb@/inquiryBySimpleBuyer/views/inquiryBySimpleListBuyer'),
      name: 'inquiryBySimpleListBuyer',
      meta: {
        title: 'route.inquiry',
        defaultActive: 'inqment',
        requiresAuth: true
      }
    }
  ]
}
