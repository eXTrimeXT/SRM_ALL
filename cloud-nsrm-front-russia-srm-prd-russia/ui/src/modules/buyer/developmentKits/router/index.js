export default {
  path: '/developmentKits',
  name: 'developmentKits',
  component: () => import('@/layout'),
  redirect: {
    name: 'codeGenerate'
  },
  children: [
    {
      path: 'codeGenerate',
      component: () =>
        import('modb@/developmentKits/views/codeGenerate'),
      name: 'codeGenerate',
      meta: {
        title: '代码生成',
        requiresAuth: true
      }
    }
  ]
}
