export default {
  path: '/mould',
  name: 'mould',
  component: () => import('@/layout'),
  redirect: {
    name: 'moudleHeader'
  },
  children: [
    {
      path: 'moudleHeader',
      component: () =>
        import('modb@/mould/views/mouldheader'),
      name: 'moudleHeader',
      meta: {
        title: 'route.moudleHeader', // 模具台账
        requiresAuth: true
      }
    },
    {
      path: 'moudleLine',
      component: () =>
        import('modb@/mould/views/mouldline'),
      name: 'moudleLine',
      meta: {
        title: 'route.moudleLine', // 模具与物料对应关系
        requiresAuth: true
      }
    },
    {
      path: 'moudleFlow',
      component: () =>
        import('modb@/mould/views/mouldflow'),
      name: 'moudleFlow',
      meta: {
        title: 'route.moudleFlow', // 模具台账审批汇总
        requiresAuth: true
      }
    }
  ]
}
