
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
      component: () => import('mods@/inquiryBySimpleSupplier/views/inquiryBySimpleListVendor/inquiryBySimpleListVendor.vue'),
      name: 'inquiryBySimpleListVendor',
      meta: {
        title: 'route.inquiryOrders',
        defaultActive: 'inqOrderVendor',
        requiresAuth: true
      }
    }
  ]
}
