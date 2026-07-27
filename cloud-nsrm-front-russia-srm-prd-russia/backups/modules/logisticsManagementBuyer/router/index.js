export default {
  path: '/logisticsSourcing',
  name: 'logisticsSourcing',
  component: () => import('@/layout'),
  redirect: {
    name: 'administrativeDivision'
  },
  children: [
    {
      path: 'expenseItem',
      component: () => import('mod@/logisticsManagementBuyer/views/expenseItem'),
      name: 'expenseItem',
      meta: {
        title: 'route.expenseItem', // 费用项定义
        requiresAuth: true
      }
    },
    {
      path: 'tradetermscombination',
      component: () =>
        import('mod@/logisticsManagementBuyer/views/tradetermscombination'),
      name: 'tradetermscombination',
      meta: {
        title: 'route.tradetermscombination', // 贸易术语组合
        requiresAuth: true
      }
    },
    {
      path: 'expenseLevel',
      component: () => import('mod@/logisticsManagementBuyer/views/expenseLevel'),
      name: 'expenseLevel',
      meta: {
        title: 'route.expenseLevel', // 费用级别定义
        requiresAuth: true
      }
    },
    {
      path: 'portInformation',
      component: () => import('mod@/logisticsManagementBuyer/views/portInformation'),
      name: 'portInformation',
      meta: {
        title: 'route.portInformation', // 港口信息维护
        requiresAuth: true
      }
    },
    {
      path: 'administrativeDivision',
      component: () =>
        import('mod@/logisticsManagementBuyer/views/administrativeDivision'),
      name: 'administrativeDivision',
      meta: {
        title: 'route.administrativeDivision', // 行政区域维护
        requiresAuth: true
      }
    },
    {
      path: 'logisticsTemplate',
      component: () => import('mod@/logisticsManagementBuyer/views/logisticsTemplate'),
      name: 'logisticsTemplate',
      meta: {
        title: 'route.logisticsTemplate', // 模板定义
        requiresAuth: true
      }
    },
    {
      path: 'logisticsPurchaseApply',
      component: () =>
        import('mod@/logisticsManagementBuyer/views/logisticsPurchaseApply'),
      name: 'logisticsPurchaseApply',
      meta: {
        title: 'route.logisticsPurchaseApply', // 物流申请
        requiresAuth: true
      }
    },
    {
      path: 'logisticsdemandPool',
      component: () =>
        import('mod@/logisticsManagementBuyer/views/logisticsdemandPool'),
      name: 'logisticsdemandPool',
      meta: {
        title: 'route.logisticsdemandPool', // 物流需求池管理
        requiresAuth: true
      }
    },
    {
      path: 'logisticsPurchaseOrder',
      component: () =>
        import('mod@/logisticsManagementBuyer/views/logisticsPurchaseOrder'),
      name: 'logisticsPurchaseOrder',
      meta: {
        title: 'route.logisticsPurchaseOrder', // 物流采购订单
        requiresAuth: true
      }
    },
    {
      path: 'logisticsProject',
      component: () => import('mod@/logisticsManagementBuyer/views/logisticsProject'),
      name: 'logisticsProject',
      meta: {
        title: 'route.logisticsProject', // route.logisticsProject  // 采购商   物流招标项目
        requiresAuth: true
      }
    },
    {
      path: 'logisticsProjectVendor',
      component: () => import('mod@/logisticsManagementBuyer/views/logisticsProjectVendor'),
      name: 'logisticsProjectVendor',
      meta: {
        title: 'route.logisticsProjectVendor', // route.logisticsProjectVendor  // 供应商  物流招标协同
        requiresAuth: true
      }
    }, // /logisticsManagementBuyer/logisticsProjectVendor
    {
      path: 'logisticsPurchaseOrderVendor',
      component: () => import('mod@/logisticsManagementBuyer/views/logisticsPurchaseOrderVendor'),
      name: 'logisticsPurchaseOrderVendor',
      meta: {
        title: 'route.logisticsPurchaseOrderVendor', // route.logisticsProjectVendor  // 供应商  物流采购订单协同
        requiresAuth: true
      }
    } // /logisticsManagementBuyer/logisticsProjectVendor
  ]
}
