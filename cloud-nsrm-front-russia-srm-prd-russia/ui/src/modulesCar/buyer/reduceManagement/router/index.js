export default {
  path: '/reduceManagement',
  name: 'reduceManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'reduceBasePrice'
  },
  children: [
    // 基准价格维护
    {
      path: 'reduceBasePrice',
      component: () => import('modcarb@/reduceManagement/views/reduceBasePrice'),
      name: 'reduceBasePrice',
      meta: {
        title: 'route.reduceBasePrice',
        defaultActive: '-',
        requiresAuth: true
      }
    },
    // 车型月度量纲维护
    {
      path: 'reduceCarDimension',
      component: () => import('modcarb@/reduceManagement/views/reduceCarDimension'),
      name: 'reduceCarDimension',
      meta: {
        title: 'route.reduceCarDimension',
        defaultActive: 'reduceCarDimension',
        requiresAuth: true
      }
    },
    // 车型年降目标维护
    {
      path: 'reduceCarTarget',
      component: () => import('modcarb@/reduceManagement/views/reduceCarTarget'),
      name: 'reduceCarTarget',
      meta: {
        title: 'route.reduceCarTarget',
        defaultActive: 'reduceCarTarget',
        requiresAuth: true
      }
    },
    // 零件年降目标维护
    {
      path: 'reduceMaterialTarget',
      component: () => import('modcarb@/reduceManagement/views/reduceMaterialTarget'),
      name: 'reduceMaterialTarget',
      meta: {
        title: 'route.reduceMaterialTarget',
        defaultActive: 'reduceMaterialTarget',
        requiresAuth: true
      }
    },
    // 年降进展维护
    {
      path: 'reduceProgress',
      component: () => import('modcarb@/reduceManagement/views/reduceProgress'),
      name: 'reduceProgress',
      meta: {
        title: 'route.reduceProgress',
        defaultActive: 'reduceProgress',
        requiresAuth: true
      }
    },
    // 年降洽谈记录
    {
      path: 'reduceNegotiation',
      component: () => import('modcarb@/reduceManagement/views/reduceNegotiation'),
      name: 'reduceNegotiation',
      meta: {
        title: 'route.reduceNegotiation',
        defaultActive: 'reduceNegotiation',
        requiresAuth: true
      }
    },
    // 返利维护
    {
      path: 'reduceRebate',
      component: () => import('modcarb@/reduceManagement/views/reduceRebate'),
      name: 'reduceRebate',
      meta: {
        title: 'route.reduceRebate',
        defaultActive: 'reduceRebate',
        requiresAuth: true
      }
    }
  ]
}
