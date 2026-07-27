export default {
  path: '/vendorBiddingManagement',
  name: 'vendorBiddingManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'vendorBiddingList'
  },
  children: [
    // /vendorBiddingManagement/vendorBiddingList
    {
      path: 'vendorBiddingList',
      component: () => import('mods@/biddingManagementSupplier/views/vendorBiddingList'),
      name: 'vendorBiddingList',
      meta: {
        title: 'route.vendorBiddingList',
        requiresAuth: true
      }
    },
    // 招标-质疑澄清 /vendorBiddingManagement/vendorChallengeClarification
    {
      path: 'vendorChallengeClarification',
      component: () => import('mods@/biddingManagementSupplier/views/vendorBiddingList/challengeClarification'),
      name: 'vendorChallengeClarification',
      meta: {
        title: 'route.vendorChallengeClarification',
        requiresAuth: true
      }
    },
    {
      path: 'vendorBiddingSignUp',
      component: () => import('mods@/biddingManagementSupplier/views/vendorBiddingList/vendorBiddingSignUp'),
      name: 'vendorBiddingSignUp',
      meta: {
        title: '招标报名',
        requiresAuth: true
      }
    },
    {
      path: 'doBidingDetail',
      component: () => import('mods@/biddingManagementSupplier/views/vendorBiddingList/doBidingDetail'),
      name: 'doBidingDetail',
      meta: {
        title: '投标',
        requiresAuth: true
      }
    }
  ]
}
