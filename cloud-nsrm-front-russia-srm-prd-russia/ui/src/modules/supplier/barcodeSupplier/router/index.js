// 条码管理协同
export default {
  path: '/barcodeSupplier',
  name: 'barcodeSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'innerboxcodeSupplier'
  },
  children: [
    {
      path: 'innerboxcodeSupplier',
      component: () =>
        import('mods@/barcodeSupplier/views/innerboxcodeSupplier'),
      name: 'innerboxcodeSupplier',
      meta: {
        title: '内箱条码',
        requiresAuth: true
      }
    },
    {
      path: 'outerboxcodeSupplier',
      component: () =>
        import('mods@/barcodeSupplier/views/outerboxcodeSupplier'),
      name: 'outerboxcodeSupplier',
      meta: {
        title: '外箱条码',
        requiresAuth: true
      }
    },
    {
      path: 'innerouterrelationSupplier',
      component: () =>
        import('mods@/barcodeSupplier/views/innerouterrelationSupplier'),
      name: 'innerouterrelationSupplier',
      meta: {
        title: '内外箱关联管理',
        requiresAuth: true
      }
    }
  ]
}
