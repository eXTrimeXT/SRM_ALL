export default {
  path: '/expertLibrary',
  name: 'expertLibrary',
  component: () => import('@/layout'),
  redirect: {
    name: 'expertInfo'
  },
  children: [
    // 专家信息 /expertLibrary/expertInfo
    {
      path: 'expertInfo',
      name: 'expertInfo',
      component: () => import('modcb@/expertLibrary/views/expertInfo'),
      meta: {
        // title: '专家信息',
        title: 'cusEntry.supplement20250205.expertInformation',
        requiresAuth: true
      }
    },
    // 专家申请列表 /expertLibrary/expertApply
    {
      path: 'expertApply',
      name: 'expertApply',
      component: () => import('modcb@/expertLibrary/views/expertApply'),
      meta: {
        // title: '专家申请列表',
        title: 'cusEntry.supplement20250205.expertApplicationList',
        requiresAuth: true
      }
    },
    // 专家库列表 /expertLibrary/expertDatabase
    {
      path: 'expertDatabase',
      name: 'expertDatabase',
      component: () => import('modcb@/expertLibrary/views/expertDatabase'),
      meta: {
        defaultActive: 'expertDatabase1',
        title: 'bid_mod.expertDataBaseList',
        requiresAuth: true
      }
    },
    // 专家评审详情 /expertLibrary/expertReview
    {
      path: 'expertReview',
      name: 'expertReview',
      component: () => import('modcb@/expertLibrary/views/expertReview'),
      meta: {
        title: 'cusEntry.supplement20250205.expertReviewDetails',
        requiresAuth: true
      }
    }
  ]
}
