export default {
  path: '/priceModel',
  name: 'priceModel',
  component: () => import('@/layout'),
  redirect: {
    name: 'costElement'
  },
  children: [
    {
      path: 'costElement',
      component: () => import('modb@/priceModel/views/costElement'),
      name: 'costElement',
      meta: {
        title: 'route.costElement',
        requiresAuth: true
      }
    },
    {
      path: 'basePrice',
      component: () => import('modb@/priceModel/views/basePrice'),
      name: 'basePrice',
      meta: {
        title: 'route.basePrice',
        requiresAuth: true
      }
    },
    {
      path: 'priceModel',
      component: () => import('modb@/priceModel/views/priceModel'),
      name: 'priceModel',
      meta: {
        title: 'route.priceModel',
        requiresAuth: true,
        defaultActive: 'priceModels'
      }
    },
    {
      path: 'estimatingPrice',
      component: () => import('modb@/priceModel/views/estimatingPrice'),
      name: 'estimatingPrice',
      meta: {
        title: 'route.estimatingPrice',
        requiresAuth: true
      }
    },
    {
      path: 'material',
      component: () => import('modb@/priceModel/views/material'),
      name: 'material',
      meta: {
        title: 'route.material',
        requiresAuth: true
      }
    },
    {
      path: 'materialPrice',
      component: () => import('modb@/priceModel/views/materialPrice'),
      name: 'materialPrice',
      meta: {
        title: 'route.materialPrice',
        requiresAuth: true
      }
    },
    {
      path: 'elementDefinition',
      component: () => import('modb@/priceModel/views/elementDefinition'),
      name: 'priceElementDefinition',
      meta: {
        title: 'route.elementDefinition',
        requiresAuth: true
      }
    },
    {
      path: 'formula',
      component: () => import('modb@/priceModel/views/formula'),
      name: 'priceFormula',
      meta: {
        title: 'route.priceFormula',
        requiresAuth: true
      }
    }
  ]
}
