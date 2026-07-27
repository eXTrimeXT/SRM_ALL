
export default {
  path: '/jcManagement',
  name: 'jcManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'JcQuotation'
  },
  children: [
    {
      path: 'jcQuotation',
      component: () => import('modcb@/jcManagement/views/quotation'),
      name: 'JcQuotation',
      meta: {
        title:"cusEntry.supplement20250205.supplierQuoteForm", // 供应商报价单
        requiresAuth: true
      }
    },
    {
      path: 'jcPricing',
      component: () => import('modcb@/jcManagement/views/pricing'),
      name: 'JcPricing',
      meta: {
        title: "cusEntry.route.priceOrders", // 定价单
        requiresAuth: true
      }
    },
    {
      path: 'jcViewQuotation',
      component: () => import('modcb@/jcManagement/views/viewQuotation'),
      name: 'jcViewQuotation',
      meta: {
        title: "bidMod.readQuote", // 查看报价
        requiresAuth: true
      }
    }
  ]
}
