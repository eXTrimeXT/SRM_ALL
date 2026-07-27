
export default {
  path: '/demoCarBuyer',
  name: 'demoCarBuyer',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetBuyerCar1'
  },
  children: [
    {
      path: 'demoSetBuyerCar1',
      component: () => import('@/modulesCar/buyer/demoCar/views/demoSet'),
      name: 'demoSetBuyerCar1',
      meta: {
        title: '标准示例-car',
        requiresAuth: true
      }
    }
  ]
}
