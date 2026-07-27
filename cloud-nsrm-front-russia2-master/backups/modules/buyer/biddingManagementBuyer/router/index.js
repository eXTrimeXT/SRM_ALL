export default {
  path: '/biddingManagement',
  name: 'biddingManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'expertDatabase'
  },
  children: [
    {
      path: 'expertDatabase',
      component: () => import('modb@/biddingManagementBuyer/views/expertDatabase'),
      name: 'expertDatabase',
      meta: {
        title: 'route.expertDatabase',
        requiresAuth: true
      }
    },
    {
      path: 'biddingFlowSetting',
      component: () =>
        import('modb@/biddingManagementBuyer/views/biddingFlowSetting'),
      name: 'biddingFlowSetting',
      meta: {
        title: 'route.biddingFlowSetting',
        requiresAuth: true
      }
    },
    {
      path: 'biddingProject',
      component: () => import('modb@/biddingManagementBuyer/views/biddingProject'),
      name: 'biddingProject',
      meta: {
        title: 'route.biddingProject',
        requiresAuth: true
      }
    },
    {
      path: 'challengeClarification',
      component: () =>
        import('modb@/biddingManagementBuyer/views/challengeClarification'),
      name: 'challengeClarification',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'bidingBasicData',
      component: () => import('modb@/biddingManagementBuyer/views/bidingBasicData'),
      name: 'bidingBasicData',
      meta: {
        title: 'route.bidingBasicData',
        requiresAuth: true
      }
    },
    {
      path: 'formula',
      component: () => import('modb@/biddingManagementBuyer/views/formula'),
      name: 'formula',
      meta: {
        title: 'route.formula',
        requiresAuth: true
      }
    },
    {
      path: 'elementDefinition',
      component: () => import('modb@/biddingManagementBuyer/views/elementDefinition'),
      name: 'elementDefinition',
      meta: {
        title: 'route.elementDefinition',
        requiresAuth: true
      }
    },
    {
      path: 'baseMaterial',
      component: () => import('modb@/biddingManagementBuyer/views/baseMaterial'),
      name: 'baseMaterial',
      meta: {
        title: 'route.baseMaterial',
        requiresAuth: true
      }
    },
    {
      path: 'basicPrice',
      component: () => import('modb@/biddingManagementBuyer/views/basicPrice'),
      name: 'basicPrice',
      meta: {
        title: 'route.basicPrice',
        requiresAuth: true
      }
    },
    {
      path: 'materialMainData',
      component: () => import('modb@/biddingManagementBuyer/views/materialMainData'),
      name: 'materialMainData',
      meta: {
        title: 'route.materialMainData',
        requiresAuth: true
      }
    },
    {
      path: 'attribute',
      component: () => import('modb@/biddingManagementBuyer/views/attribute'),
      name: 'attribute',
      meta: {
        title: 'route.attribute',
        requiresAuth: true
      }
    },
    // 招标技术评分
    {
      path: 'technologyScore',
      component: () => import('modb@/biddingManagementBuyer/views/technologyScore'),
      name: 'technologyScore',
      meta: {
        title: 'route.technologyScore',
        requiresAuth: true
      }
    }
  ]
}
