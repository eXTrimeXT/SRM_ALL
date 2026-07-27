export default {
  path: '/qualitySynergySupplier',
  name: 'qualitySynergySupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'incomingException'
  },
  children: [
    {
      path: 'incomingException',
      component: () => import('mods@/qualitySynergySupplier/views/incomingException'),
      name: 'incomingException',
      meta: {
        title: 'route.incomingException',
        defaultActive: 'incomingExceptionV',
        requiresAuth: true
      }
    },
    {
      path: 'report8DSup',
      component: () => import('mods@/qualitySynergySupplier/views/report8D'),
      name: 'report8DSup',
      meta: {
        title: 'route.report8D',
        requiresAuth: true
      }
    },
    {
      path: 'processException',
      component: () => import('mods@/qualitySynergySupplier/views/processException'),
      name: 'processException',
      meta: {
        title: 'route.processException',
        defaultActive: 'processExceptionVendor',
        requiresAuth: true
      }
    },
    {
      path: 'excHandlingNotice',
      component: () => import('mods@/qualitySynergySupplier/views/excHandlingNotice'),
      name: 'excHandlingNotice',
      meta: {
        title: 'route.excHandlingNotice',
        requiresAuth: true,
        defaultActive: 'exceptionHandlingNoticeVendor'
      }
    },
    {
      // 成品通用标准
      path: 'inspectionStandard',
      component: () => import('mods@/qualitySynergySupplier/views/inspectionStandard'),
      name: 'inspectionStandardSupplier',
      meta: {
        title: 'route.inspectionStandard',
        requiresAuth: true,
        defaultActive: 'inspectionStandardV'
      }
    }
  ]
}
