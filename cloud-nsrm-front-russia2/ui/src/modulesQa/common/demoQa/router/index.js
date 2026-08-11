
export default {
  path: '/demoQaCommon',
  name: 'demoQaCommon',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetQaCommon'
  },
  children: [
    {
      path: 'demoSetQaCommon',
      component: () => import('@/modulesQa/common/demoQa/views/demoSet'),
      name: 'demoSetQaCommon',
      meta: {
        title: '标准示例-新',
        requiresAuth: true
      }
    }
  ]
}
