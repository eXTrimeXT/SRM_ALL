export default {
  path: '/centralizedPurchase',
  name: 'centralizedPurchase',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiry'
  },
  children: [
    /* 询比价 */
    {
      path: 'inquiry',
      name: 'inquiry',
      component: () => import('modcb@/centralizedPurchase/views/inquiry'),
      meta: {
        title: 'cusEntry.route.inquiry',
        requiresAuth: true
      }
    }
  ]
}
