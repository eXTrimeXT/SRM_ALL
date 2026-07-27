
export default {
  path: '/vendorSourceSynergy',
  name: 'vendorSourceSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryBySimpleListVendor'
  },
  children: [
    {
      path: 'inquiryBySimpleListVendor',
      component: () => import('mod@/inquiryBySimpleSupplier/views/inquiryBySimpleListVendor/inquiryBySimpleListVendor.vue'),
      name: 'inquiryBySimpleListVendor',
      meta: {
        title: 'route.inquiryOrders',
        defaultActive: 'inqOrderVendor',
        requiresAuth: true
      }
    },
    {
      path: 'materialSourceOrders',
      component: () => import('mod@/inquiryBySimpleSupplier/views/materialSourceOrders'),
      name: 'materialSourceOrders',
      meta: {
        title: 'route.materialSourceOrders',
        requiresAuth: true
      }
    },
    {
      path: 'categorySourceOrders',
      component: () => import('mod@/inquiryBySimpleSupplier/views/categorySourceOrders'),
      name: 'categorySourceOrders',
      meta: {
        title: 'route.categorySourceOrders',
        requiresAuth: true
      }
    },
    {
      path: 'quotationPrices',
      component: () => import('mod@/inquiryBySimpleSupplier/views/quotationPrices'),
      name: 'quotationPrices',
      meta: {
        title: 'route.quotationPrices',
        requiresAuth: true
      }
    },
    {
      path: 'priceInquiry',
      component: () => import('mod@/inquiryBySimpleSupplier/views/priceInquiry'),
      name: 'priceInquiry',
      meta: {
        title: 'route.priceInquiry',
        requiresAuth: true
      }
    },
    {
      path: 'vendorCategoryManagement',
      component: () =>
        import('mod@/inquiryBySimpleSupplier/views/vendorCategoryManagement'),
      name: 'vendorCategoryManagement',
      meta: {
        title: 'route.vendorCategoryManagement',
        requiresAuth: true
      }
    }
  ]
}
