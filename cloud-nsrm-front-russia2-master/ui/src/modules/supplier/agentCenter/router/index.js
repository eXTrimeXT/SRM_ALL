export default {
  path: '/agentCenterVendor',
  name: 'agentCenter',
  component: () => import('@/layout'),
  redirect: {
    name: 'approvalFlowCenterVendor'
  },
  children: [
    {
      path: 'approvalFlowCenterVendor',
      component: () => import('mods@/agentCenter/views/approvalFlowCenter'),
      name: 'approvalFlowCenterVendor',
      meta: {
        title: 'route.approvalFlowCenterVendor',
        requiresAuth: true
      }
    }
  ]
}
