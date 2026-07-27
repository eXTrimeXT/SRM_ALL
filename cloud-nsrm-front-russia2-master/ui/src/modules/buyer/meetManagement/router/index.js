export default {
  path: '/meetManagement',
  name: 'meetManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'meetModel'
  },
  children: [
    // 议题模板
    {
      path: 'meetModel',
      component: () => import('modb@/meetManagement/views/meetModel'),
      name: 'meetModel',
      meta: {
        title: 'route.meetManagementModel',
        defaultActive: 'meetManagementModel',
        requiresAuth: true
      }
    },
    // 议题管理
    {
      path: 'meetTopics',
      component: () => import('modb@/meetManagement/views/meetTopics'),
      name: 'meetTopics',
      meta: {
        title: 'route.meetManagementTopics',
        defaultActive: 'meetManagementTopics',
        requiresAuth: true
      }
    },
    // 待办事项
    {
      path: 'meetTodo',
      component: () => import('modb@/meetManagement/views/meetTodo'),
      name: 'meetTodo',
      meta: {
        title: 'route.meetManagementTodo',
        defaultActive: 'meetManagementTodo',
        requiresAuth: true
      }
    },
    // 会议列表
    {
      path: 'meetManage',
      component: () => import('modb@/meetManagement/views/meetManage'),
      name: 'meetManage',
      meta: {
        title: 'route.meetManagementManage',
        defaultActive: 'meetManagementManage',
        requiresAuth: true
      }
    }
  ]
}
