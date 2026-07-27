export default {
  path: '/queryComparePriceSynergy',
  name: 'queryComparePriceSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryByProjectListVendor'
  },
  children: [
    // 项目询价项目列表
    {
      path: 'inquiryByProjectListVendor',
      component: () => import('mods@/inquiryByProjectSupplier/views/inquiryByProjectListVendor'),
      name: 'inquiryByProjectListVendor',
      meta: {
        title: 'route.vendorBiddingList1',
        defaultActive: 'vendorBiddingList_new',
        requiresAuth: true
      }
    },
    // 质疑/澄清
    {
      path: 'inquiryByProjectChClVendor',
      component: () => import('mods@/inquiryByProjectSupplier/views/inquiryByProjectChClVendor'),
      name: 'inquiryByProjectChClVendor',
      meta: {
        title: 'route.vendorChallengeClarification',
        defaultActive: 'vendorChallengeClarification_new',
        requiresAuth: true
      }
    }
  ]
}
