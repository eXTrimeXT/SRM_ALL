
export default {
  path: '/demoQaBuyer',
  name: 'demoQaBuyer',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetBuyerQa1'
  },
  children: [
    {
      path: 'demoSetBuyerQa1',
      component: () => import('@/modulesQa/buyer/demoQa/views/demoSet'),
      name: 'demoSetBuyerQa1',
      meta: {
        title: '标准示例-qa',
        requiresAuth: true
      }
    }
  ]
}
