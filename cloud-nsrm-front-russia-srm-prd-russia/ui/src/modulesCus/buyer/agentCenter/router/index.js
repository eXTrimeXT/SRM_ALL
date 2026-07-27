export default {
  path: '/agentCenter',
  name: 'agentCenter',
  component: () => import('@/layout'),
  redirect: {
    name: 'taskCenter'
  },
  children: [
    {
      path: 'approvalFlowCenter',
      component: () => import('modcb@/agentCenter/views/approvalFlowCenter'),
      name: 'approvalFlowCenter',
      meta: {
        title: 'route.approvalFlowCenter',
        requiresAuth: true
      }
    },
    {
      path: 'flowTask', // 流程代办任务
      component: () => import('modcb@/agentCenter/views/flowTask'),
      name: 'flowTask',
      meta: {
        title: 'route.flowTask',
        requiresAuth: true
      }
    },
    {
      path: 'flowTaskView', // 流程代办任务
      component: () => import('modcb@/agentCenter/views/flowTask/flowTaskView'),
      name: 'flowTaskView',
      meta: {
        title: 'route.flowTaskView', // 流程详情
        requiresAuth: true
      }
    }
  ]
}
