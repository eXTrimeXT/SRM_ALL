
export default {
  path: '/demoCusSup',
  name: 'demoCusSup',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetNewSup1'
  },
  children: [
    {
      path: 'demoSetNewSup1',
      component: () => import('@/modulesCus/supplier/demoCus/views/demoSet'),
      name: 'demoSetNewSup1',
      meta: {
        title: '标准示例-新',
        requiresAuth: true
      }
    }
  ]
}
