
export default {
  path: '/vendorHierarchicalManagement',
  name: 'vendorHierarchicalManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'hierarchicalProject'
  },
  children: [
    {
      path: 'hierarchicalProject',
      component: () => import('modb@/vendorHierarchicalManagement/views/hierarchicalProject'),
      name: 'hierarchicalProject',
      meta: {
        title: 'route.hierarchicalProject', // 分级项目管理
        requiresAuth: true
      }
    },
    {
      path: 'hierarchicalRating',
      component: () =>
      import('modb@/vendorHierarchicalManagement/views/hierarchicalRating'),
      name: 'hierarchicalRating',
      meta: {
        title: 'route.hierarchicalRating', // 分级评级明细
        requiresAuth: true
      }
    },
    {
      path: 'hierarchicalReview',
      component: () =>
      import('modb@/vendorHierarchicalManagement/views/hierarchicalReview'),
      name: 'hierarchicalReview',
      meta: {
        title: 'route.hierarchicalReview', // 分级评审
        requiresAuth: true
      }
    },
    {
      path: 'hierarchicalResults',
      component: () =>
      import('modb@/vendorHierarchicalManagement/views/hierarchicalResults'),
      name: 'hierarchicalResults',
      meta: {
        title: 'route.gradingResults', // 分级结果
        requiresAuth: true
      }
    }
  ]
}
