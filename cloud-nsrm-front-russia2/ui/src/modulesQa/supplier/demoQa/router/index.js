
export default {
  path: '/demoQaSupplier',
  name: 'demoQaSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetQaSupNew'
  },
  children: [
    {
      path: 'demoSetQaSupNew',
      component: () => import('@/modulesQa/supplier/demoQa/views/demoSet'),
      name: 'demoSetQaSupNew',
      meta: {
        title: '标准示例-新',
        requiresAuth: true
      }
    }
  ]
}
