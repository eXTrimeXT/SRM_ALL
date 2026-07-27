export default {
  path: '/bomManagement',
  name: 'bomManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'completeBomData'
  },
  children: [
    // BOM
    {
      path: 'completeBomData',
      component: () => import('modcarb@/bomManagement/views/completeBomData'),
      name: 'completeBomData',
      meta: {
        title: 'route.completeBomData',
        defaultActive: 'completeBomData',
        requiresAuth: true
      }
    }

  ]
}
