export default {
  path: '/biddingManagement',
  name: 'biddingManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'expertDatabase'
  },
  children: [
    // 专家库
    {
      path: 'expertDatabase',
      component: () => import('modb@/biddingManagementBuyer/views/expertDatabase'),
      name: 'expertDatabase',
      meta: {
        title: 'route.expertDatabase',
        requiresAuth: true
      }
    },
    // 价格公式定义
    {
      path: 'formula',
      component: () => import('modb@/biddingManagementBuyer/views/formula'),
      name: 'formula',
      meta: {
        title: 'route.formula',
        requiresAuth: true
      }
    },
    // 要素定义
    {
      path: 'elementDefinition',
      component: () => import('modb@/biddingManagementBuyer/views/elementDefinition'),
      name: 'elementDefinition',
      meta: {
        title: 'route.elementDefinition',
        requiresAuth: true
      }
    },
    // 基材档案
    {
      path: 'baseMaterial',
      component: () => import('modb@/biddingManagementBuyer/views/baseMaterial'),
      name: 'baseMaterial',
      meta: {
        title: 'route.baseMaterial',
        requiresAuth: true
      }
    },
    // 基材价格
    {
      path: 'basicPrice',
      component: () => import('modb@/biddingManagementBuyer/views/basicPrice'),
      name: 'basicPrice',
      meta: {
        title: 'route.basicPrice',
        requiresAuth: true
      }
    },
    // 物料价格公式定义
    {
      path: 'materialMainData',
      component: () => import('modb@/biddingManagementBuyer/views/materialMainData'),
      name: 'materialMainData',
      meta: {
        title: 'route.materialMainData',
        requiresAuth: true
      }
    },
    // 物料属性清单
    {
      path: 'attribute',
      component: () => import('modb@/biddingManagementBuyer/views/attribute'),
      name: 'attribute',
      meta: {
        title: 'route.attribute',
        requiresAuth: true
      }
    }
  ]
}
