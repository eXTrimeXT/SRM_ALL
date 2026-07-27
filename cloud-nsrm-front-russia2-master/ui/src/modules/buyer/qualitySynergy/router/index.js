export default {
  path: '/qualitySynergy',
  name: 'qualitySynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'incomingException'
  },
  children: [
    {
      path: 'incomingException',
      component: () => import('modb@/qualitySynergy/views/incomingException'),
      name: 'incomingException',
      meta: {
        title: 'route.incomingException',
        requiresAuth: true
      }
    },
    {
      path: 'report8D',
      component: () => import('modb@/qualitySynergy/views/report8D'),
      name: 'report8D',
      meta: {
        title: 'route.report8D',
        requiresAuth: true,
        defaultActive: '8D'
      }
    },
    {
      path: 'processException',
      component: () => import('modb@/qualitySynergy/views/processException'),
      name: 'processException',
      meta: {
        title: 'route.processException',
        requiresAuth: true
      }
    },
    {
      path: 'processException',
      component: () => import('modb@/qualitySynergy/views/processException'),
      name: 'processException',
      meta: {
        title: 'route.processException',
        requiresAuth: true
      }
    },
    {
      path: 'excHandlingNotice',
      component: () => import('modb@/qualitySynergy/views/excHandlingNotice'),
      name: 'excHandlingNotice',
      meta: {
        title: 'route.excHandlingNotice',
        requiresAuth: true,
        defaultActive: 'exceptionHandlingNotice'
      }
    },
    {
      // 成品通用标准
      path: 'inspectionStandard',
      component: () => import('modb@/qualitySynergy/views/inspectionStandard'),
      name: 'inspectionStandard',
      meta: {
        title: 'route.inspectionStandard',
        requiresAuth: true
      }
    },
    // 检验项目
    {
      path: 'inspectionItem',
      component: () => import('modb@/qualitySynergy/views/inspectionItem'),
      name: 'inspectionItem',
      meta: {
        title: 'route.inspectionItem',
        requiresAuth: true
      }
    }
  ]
}
