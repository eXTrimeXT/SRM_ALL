export default {
  path: '/marketBudget',
  name: 'marketBudget',
  component: () => import('@/layout'),
  redirect: {
    name: 'singleMaterial'
  },
  children: [
    // 单台原材料
    {
      path: 'singleMaterial',
      component: () => import('modb@/marketBudget/views/singleMaterial'),
      name: 'singleMaterial',
      meta: {
        title: 'route.singleMaterial',
        defaultActive: 'singleMaterial',
        requiresAuth: true
      }
    },
    // 零件原材料
    {
      path: 'partsMaterial',
      component: () => import('modb@/marketBudget/views/partsMaterial'),
      name: 'partsMaterial',
      meta: {
        title: 'route.partsMaterial',
        defaultActive: 'partsMaterial',
        requiresAuth: true
      }
    },
    // 市况类型与材料关系维护
    {
      path: 'marketAndMaterial',
      component: () => import('modb@/marketBudget/views/marketAndMaterial'),
      name: 'marketAndMaterial',
      meta: {
        title: 'route.marketAndMaterial',
        defaultActive: 'marketAndMaterial',
        requiresAuth: true
      }
    },
    // 市况预算行情维护
    {
      path: 'marketQuotations',
      component: () => import('modb@/marketBudget/views/marketQuotations'),
      name: 'marketQuotations',
      meta: {
        title: 'route.marketQuotations',
        defaultActive: 'marketQuotations',
        requiresAuth: true
      }
    },
    // 市况联动要素定义
    {
      path: 'marketElement',
      component: () => import('modb@/marketBudget/views/marketElement'),
      name: 'marketElement',
      meta: {
        title: 'route.marketElement',
        defaultActive: 'marketElement',
        requiresAuth: true
      }
    },
    // 单台市况重量报表
    {
      path: 'singleMarketWeightReport',
      component: () => import('modb@/marketBudget/views/singleMarketWeightReport'),
      name: 'singleMarketWeightReport',
      meta: {
        title: 'route.singleMarketWeightReport',
        defaultActive: 'singleMarketWeightReport',
        requiresAuth: true
      }
    },
    // 市况预算编制
    {
      path: 'budgetMaking',
      component: () => import('modb@/marketBudget/views/budgetMaking'),
      name: 'budgetMaking',
      meta: {
        title: 'route.budgetMaking',
        defaultActive: 'budgetMaking',
        requiresAuth: true
      }
    },
    // 市况联动公式定义
    {
      path: 'marketFormula',
      component: () => import('modb@/marketBudget/views/marketFormula'),
      name: 'marketFormula',
      meta: {
        title: 'route.marketFormula',
        defaultActive: 'marketFormula',
        requiresAuth: true
      }
    }
  ]
}
