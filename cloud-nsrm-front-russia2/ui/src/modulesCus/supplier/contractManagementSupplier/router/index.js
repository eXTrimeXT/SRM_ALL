export default {
  path: '/contractManagementSup',
  name: 'contractManagementSup',
  component: () => import('@/layout'),
  redirect: {
    name: 'contractManagerSupplier'
  },
  children: [
    // 合同列表(协同)
    {
      path: 'contractManagerSupplier',
      // 此处供应商直接引用采购商的页面，页面内部做权限控制
      component: () => import('modcb@/contractManagement/views/contractManager'),
      name: 'contractManagerSupplier',
      meta: {
        title: 'route.contractMaintainList',
        requiresAuth: true
      }
    }
  ]
}
