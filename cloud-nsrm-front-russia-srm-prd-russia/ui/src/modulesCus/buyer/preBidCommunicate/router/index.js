
export default {
  path: '/preBidCommunicate',
  name: 'preBidCommunicate',
  component: () => import('@/layout'),
  redirect: {
    name: 'communicateNoticeBuyer'
  },
  children: [
    {
      path: 'communicateNoticeBuyer',
      component: () => import('@/modulesCus/buyer/preBidCommunicate/views/communicateNoticeBuyer'),
      name: 'communicateNoticeBuyer',
      meta: {
        title: '标前交流通知',
        requiresAuth: true
      }
    },
    {
      path: 'communicateFeedbackBuyer',
      component: () => import('@/modulesCus/buyer/preBidCommunicate/views/communicateFeedbackBuyer'),
      name: 'communicateFeedbackBuyer',
      meta: {
        title: '标前交流反馈',
        requiresAuth: true
      }
    }
  ]
}
