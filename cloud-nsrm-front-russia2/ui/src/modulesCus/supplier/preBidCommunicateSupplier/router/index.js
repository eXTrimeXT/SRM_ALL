
export default {
  path: '/preBidCommunicateSupplier',
  name: 'preBidCommunicateSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'communicateNotice'
  },
  children: [
    {
      path: 'communicateNotice',
      component: () => import('@/modulesCus/supplier/preBidCommunicateSupplier/views/communicateNotice'),
      name: 'communicateNotice',
      meta: {
        // title: '标前通知查询',
        title: "cusEntry.supplement20250205.preBidNotificationInquiry",
        requiresAuth: true
      }
    },
    {
      path: 'communicateFeedback',
      component: () => import('@/modulesCus/supplier/preBidCommunicateSupplier/views/communicateFeedback'),
      name: 'communicateFeedback',
      meta: {
        // title: '标前交流反馈',
        title: "cusEntry.supplement20250205.preBidCommunicationFeedback",
        requiresAuth: true
      }
    }
  ]
}
