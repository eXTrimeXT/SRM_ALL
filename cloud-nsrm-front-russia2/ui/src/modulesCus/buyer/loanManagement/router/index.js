
export default {
  path: '/loanManagement',
  name: 'loanManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'loanApply'
  },
  children: [
    {
      path: 'loanApply',
      component: () => import('@/modulesCus/buyer/loanManagement/views/loanApply'),
      name: 'loanApply',
      meta: {
        title: 'cusEntry.route.loanApply', // 借阅申请
        requiresAuth: true
      }
    }
  ]
}
