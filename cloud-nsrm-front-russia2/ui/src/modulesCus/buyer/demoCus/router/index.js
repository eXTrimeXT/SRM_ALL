
export default {
  path: '/demoCusBuyer',
  name: 'demoCusBuyer',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetNewBuyer'
  },
  children: [
    {
      path: 'demoSetNewBuyer',
      component: () => import('@/modulesCus/buyer/demoCus/views/demoSet'),
      name: 'demoSetNewBuyer',
      meta: {
        title: "cusEntry.supplement20250205.standardExampleNew",  // 标准示例-新
        requiresAuth: true
      }
    }
  ]
}
