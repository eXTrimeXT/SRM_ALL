export default {
  path: '/supplierRecommend',
  name: 'supplierRecommend',
  component: () => import('@/layout'),
  redirect: {
    name: 'recommendVendor'
  },
  children: [
    // 推荐供应商 /supplierRecommend/recommendVendor
    {
      path: 'recommendVendor',
      component: () => import('modcb@/supplierRecommend/views/recommendVendor'),
      name: 'recommendVendor',
      meta: {
        // title: '推荐供应商',
        title: () => "dataConfMod.ifRecommendVendor",
        requiresAuth: true
      }
    }
  ]
}
