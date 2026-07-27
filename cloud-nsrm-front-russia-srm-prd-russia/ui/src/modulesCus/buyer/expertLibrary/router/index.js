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
        title: '专家信息',
        requiresAuth: true
      }
    },
    // 专家申请列表 /expertLibrary/expertApply
    {
      path: 'expertApply',
      name: 'expertApply',
      component: () => import('modcb@/expertLibrary/views/expertApply'),
      meta: {
        title: '专家申请列表',
        requiresAuth: true
      }
    },
    // 专家库列表 /expertLibrary/expertDatabase
    {
      path: 'expertDatabase',
      name: 'expertDatabase',
      component: () => import('modcb@/expertLibrary/views/expertDatabase'),
      meta: {
        title: '专家库列表',
        requiresAuth: true
      }
    },
    // 专家评审详情 /expertLibrary/expertReview
    {
      path: 'expertReview',
      name: 'expertReview',
      component: () => import('modcb@/expertLibrary/views/expertReview'),
      meta: {
        title: '专家评审详情',
        requiresAuth: true
      }
    }
  ]
}
