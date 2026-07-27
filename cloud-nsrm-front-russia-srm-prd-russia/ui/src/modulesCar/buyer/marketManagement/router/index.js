export default {
  path: '/marketManagement',
  name: 'marketManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'marketAndMaterial'
  },
  children: [
    // 市况类型与原材料关系维护
    {
      path: 'marketAndMaterialCar',
      component: () => import('modcarb@/marketManagement/views/marketAndMaterial'),
      name: 'marketAndMaterial',
      meta: {
        // title: 'route.marketAndMaterial',
        title: '市况类型与原材料关系维护',
        defaultActive: 'marketAndMaterialCar',
        requiresAuth: true
      }
    },
    // 零件与原材料联动关系维护
    {
      path: 'partsMaterialCar',
      component: () => import('modcarb@/marketManagement/views/partsMaterial'),
      name: 'partsMaterial',
      meta: {
        title: '零件与原材料联动关系维护',
        defaultActive: 'partsMaterialCar',
        requiresAuth: true
      }
    },
    // 市况联动要素定义
    {
      path: 'marketElementCar',
      component: () => import('modcarb@/marketManagement/views/marketElement'),
      name: 'marketElement',
      meta: {
        title: 'route.marketElement',
        defaultActive: 'marketElementCar',
        requiresAuth: true
      }
    },
    // 要素扩展API配置
    {
      path: 'quoteFunConfigCar',
      component: () => import('modcarb@/marketManagement/views/quoteFunConfig'),
      name: 'quoteFunConfig',
      meta: {
        title: 'route.quoteFunConfig',
        defaultActive: 'quoteFunConfigCar',
        requiresAuth: true
      }
    },
    // 市况联动公式定义
    {
      path: 'marketFormulaCar',
      component: () => import('modcarb@/marketManagement/views/marketFormula'),
      name: 'marketFormula',
      meta: {
        title: 'route.marketFormula',
        defaultActive: 'marketFormulaCar',
        requiresAuth: true
      }
    },
    // 原材料行情维护
    {
      path: 'materialQuotationCar',
      component: () => import('modcarb@/marketManagement/views/materialQuotation'),
      name: 'materialQuotation',
      meta: {
        title: '原材料行情维护',
        defaultActive: 'materialQuotationCar',
        requiresAuth: true
      }
    },
    // 市况联动零件价格列表
    {
      path: 'marketPartsPriceCar',
      component: () => import('modcarb@/marketManagement/views/marketPartsPrice'),
      name: 'marketPartsPrice',
      meta: {
        title: '市况联动零件价格列表',
        defaultActive: 'marketPartsPriceCar',
        requiresAuth: true
      }
    }

  ]
}
