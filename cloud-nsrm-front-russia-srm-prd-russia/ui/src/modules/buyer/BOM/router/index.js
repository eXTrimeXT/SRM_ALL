export default {
    path: '/BOM',
    name: 'BOM',
    component: () => import('@/layout'),
    redirect: {
        name: 'BOM'
    },
    children: [
      {
        path: 'BOM',
        component: () =>
            import('modb@/BOM/views'),
        name: 'BOM',
        meta: {
            title: 'BOM',
            requiresAuth: true
        }
      }
    ]
  }
