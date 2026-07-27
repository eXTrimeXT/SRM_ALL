// 条码管理协同（新）
export default {
  path: '/barcodeNewSupplier',
  name: 'barcodeNewSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'barcodeInnerPrint'
  },
  children: [
  // 内箱条码打印 路径：/barcodeNewSupplier/barcodeInnerPrint
    {
      path: 'barcodeInnerPrint',
      name: 'barcodeInnerPrint',
      component: () => import('mods@/barcodeNewSupplier/views/barcodeInnerPrint'),
      meta: {
        title: 'route.barcodeInnerPrint',
        requiresAuth: true
      }
    },
    // 外箱条码打印 路径：/barcodeNewSupplier/barcodeOuterPrint
      {
        path: 'barcodeOuterPrint',
        name: 'barcodeOuterPrint',
        component: () => import('mods@/barcodeNewSupplier/views/barcodeOuterPrints'),
        meta: {
          title: 'route.barcodeOuterPrint',
          requiresAuth: true
        }
      },
  // 内外箱关联 路径：/barcodeNewSupplier/barcodeRelation
    {
      path: 'barcodeRelation',
      name: 'barcodeRelation',
      component: () => import('mods@/barcodeNewSupplier/views/barcodeRelation'),
      meta: {
        title: 'route.barcodeRelation',
        requiresAuth: true
      }
    }
  ]
}
