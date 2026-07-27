export default {
  path: '/competition',
  name: 'competition',
  component: () => import('@/layout'),
  redirect: {
    name: 'competitionManagement'
  },
  children: [
    // 竞价项目管理
    {
      path: 'competitionManagement',
      component: () => import('modb@/competition/views/competitionManagement'),
      name: 'competitionManagement',
      meta: {
        title: 'route.competitionManagement',
        requiresAuth: true
      }
    },
    // 竞价项目管理(新)
    {
      path: 'competitionManageBuyer',
      component: () => import('modcb@/competition/views/competitionManageBuyer'),
      name: 'competitionManageBuyer',
      meta: {
        title: 'route.competitionManagement',
        requiresAuth: true
      }
    }
  ]
}
