export default {
  path: '/biddingSupplier',
  name: 'biddingSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'biddingList'
  },
  children: [
    {
      path: 'biddingList',
      name: 'biddingList',
      component: () => import('modcs@/biddingSupplier/views/biddingList'),
      meta: {
        title: 'cusEntry.route.biddingList', // 招标协同 /biddingSupplier/biddingList
        requiresAuth: true
      }
    },
    {
      path: 'billInfo',
      name: 'billInfo',
      component: () => import('modcs@/biddingSupplier/views/billInfo'),
      meta: {
        title: 'cusEntry.route.billInfo', // 开票信息 /biddingSupplier/billInfo
        requiresAuth: true
      }
    },
    {
      path: 'sourcingCooperation',
      name: 'sourcingCooperation',
      component: () => import('modcs@/biddingSupplier/views/sourcingCooperation'),
      meta: {
        title: 'cusEntry.route.sourcingCooperation', // 寻源需求协同 /biddingSupplier/sourcingCooperation
        requiresAuth: true
      }
    }
  ]
}
