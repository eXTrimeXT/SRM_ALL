
export default {
  path: '/demoCarCommon',
  name: 'demoCarCommon',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetCarCommon'
  },
  children: [
    {
      path: 'demoSetCarCommon',
      component: () => import('@/modulesCar/common/demoCar/views/demoSet'),
      name: 'demoSetCarCommon',
      meta: {
        title: '标准示例-新',
        requiresAuth: true
      }
    }
  ]
}
