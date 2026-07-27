
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
        title: '供应商报价单', // 供应商报价单
        requiresAuth: true
      }
    },
    {
      path: 'jcPricing',
      component: () => import('modcb@/jcManagement/views/pricing'),
      name: 'JcPricing',
      meta: {
        title: '定价单', // 定价单
        requiresAuth: true
      }
    },
    {
      path: 'jcViewQuotation',
      component: () => import('modcb@/jcManagement/views/viewQuotation'),
      name: 'jcViewQuotation',
      meta: {
        title: '查看报价', // 查看报价
        requiresAuth: true
      }
    }
  ]
}
