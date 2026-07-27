
export default {
  path: '/bidNoticeManagement',
  name: 'bidNoticeManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'bidNotice'
  },
  children: [
    {
      path: 'bidNotice',
      component: () => import('@/modulesCus/buyer/bidNoticeManagement/views/bidNotice'),
      name: 'bidNotice',
      meta: {
        title: 'cusEntry.route.bidNotice', // 中/落标通知
        requiresAuth: true
      }
    },
    {
      path: 'bidNoticeAbandon',
      component: () => import('@/modulesCus/buyer/bidNoticeManagement/views/bidNoticeAbandon'),
      name: 'bidNoticeAbandon',
      meta: {
        title: 'cusEntry.route.bidNoticeAbandon', // 中/落标废弃申请
        requiresAuth: true
      }
    }
  ]
}
