
export default {
  path: '/demoCarSupplier',
  name: 'demoCarSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetCarSupNew'
  },
  children: [
    {
      path: 'demoSetCarSupNew',
      component: () => import('@/modulesCar/supplier/demoCar/views/demoSet'),
      name: 'demoSetCarSupNew',
      meta: {
        title: '标准示例-新',
        requiresAuth: true
      }
    }
  ]
}
