export default {
  path: '/productionPrepare',
  name: 'productionPrepare',
  component: () => import('@/layout'),
  redirect: {
    name: 'toolingConfirmQuery'
  },
  children: [
    {
      path: 'toolingConfirmQuery',
      component: () => import('modb@/productionPrepare/views/toolingConfirmQuery'),
      name: 'toolingConfirmQuery',
      meta: {
        title: 'route.toolingConfirmQuery',
        requiresAuth: true
      }
    },
    {
      path: 'productionApproval',
      component: () => import('modb@/productionPrepare/views/productionApproval'),
      name: 'productionApproval',
      meta: {
        title: 'route.productionApproval',
        requiresAuth: true
      }
    },
    {
      path: 'supplierCategoryResponsibility',
      component: () => import('modb@/productionPrepare/views/categoryResponsibility'),
      name: 'supplierCategoryResponsibility',
      meta: {
        title: 'route.supplierCategoryResponsibility',
        requiresAuth: true
      }
    },
    {
      path: 'questTemplateManage',
      component: () => import('modb@/productionPrepare/views/questTemplate'),
      name: 'questTemplateManage',
      meta: {
        title: 'route.questTemplateManage',
        requiresAuth: true
      }
    },
    {
      path: 'questManagementProd',
      component: () => import('modb@/productionPrepare/views/questManagement'),
      name: 'questManagementProd',
      meta: {
        title: 'route.questManagement',
        requiresAuth: true
      }
    },
    {
      path: 'problemManagement',
      component: () => import('modb@/productionPrepare/views/problemManagement'),
      name: 'problemManagement',
      meta: {
        title: 'route.problemManagement',
        requiresAuth: true
      }
    }
  ]
}
