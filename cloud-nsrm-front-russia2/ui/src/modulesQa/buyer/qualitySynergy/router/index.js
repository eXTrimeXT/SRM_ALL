export default {
  path: '/qualitySynergy',
  name: 'qualitySynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'incomingException'
  },
  children: [
    {
      path: 'qualityProjectBuyer',
      component: () => import('@/modulesQa/buyer/qualitySynergy/views/qualityProjectBuyer'),
      name: 'qualityProjectBuyer',
      meta: {
        title: 'route.qualityProject',
        requiresAuth: true
      }
    },
    {
      path: 'spcStandardBuyer',
      component: () => import('@/modulesQa/buyer/qualitySynergy/views/spcStandardBuyer'),
      name: 'spcStandardBuyer',
      meta: {
        title: 'route.spcStandard',
        requiresAuth: true
      }
    },
    {
      path: 'spcDataBuyer',
      component: () => import('@/modulesQa/buyer/qualitySynergy/views/spcDataBuyer'),
      name: 'spcDataBuyer',
      meta: {
        title: 'route.spcData',
        requiresAuth: true
      }
    },
    {
      path: 'pdcaPageBuyer',
      component: () => import('@/modulesQa/buyer/qualitySynergy/views/pdcaPageBuyer'),
      name: 'pdcaPageBuyer',
      meta: {
        title: 'route.pdcaPage',
        requiresAuth: true
      }
    }
  ]
}
