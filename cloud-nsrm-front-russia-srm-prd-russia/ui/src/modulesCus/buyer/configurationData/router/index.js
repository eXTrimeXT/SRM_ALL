export default {
  path: '/configurationData',
  name: 'configurationData',
  component: () => import('@/layout'),
  redirect: {
    name: 'recentProcurement'
  },
  children: [
    {
      path: 'recentProcurement',
      component: () => import('modcb@/configurationData/views/recentProcurement'),
      name: 'recentProcurement',
      meta: {
        title: '近期采购类参数配置', // 近期采购类参数配置
        requiresAuth: true
      }
    },
    {
      path: 'foundation',
      component: () => import('modcb@/configurationData/views/recentProcurement/foundation'),
      name: 'foundation',
      meta: {
        title: '近期采购类基础数据', // 近期采购类基础数据
        requiresAuth: true
      }
    },
    {
      path: 'historicalSuppliers',
      component: () => import('modcb@/configurationData/views/historicalSuppliers'),
      name: 'historicalSuppliers',
      meta: {
        title: '历史供应商', // 历史供应商
        requiresAuth: true
      }
    },
    {
      path: 'autoOrderSplit',
      component: () => import('modcb@/configurationData/views/autoOrderSplit'),
      name: 'autoOrderSplit',
      meta: {
        title: '自动分单', // 自动分单
        requiresAuth: true
      }
    }
  ]
}
