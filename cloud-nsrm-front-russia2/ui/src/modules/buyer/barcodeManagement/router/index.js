// 条码管理
export default {
  path: '/barcodeManagement',
  name: 'barcodeManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'innerBoxCode'
  },
  children: [
    {
      path: 'innerBoxCode',
      component: () =>
        import('modb@/barcodeManagement/views/innerboxcode'),
      name: 'innerBoxCode',
      meta: {
        title: 'orderMod.innerBoxBarcode',  // 内箱条码
        requiresAuth: true
      }
    },
    {
      path: 'boxTagConfig',
      component: () =>
        import('modb@/barcodeManagement/views/boxtagconfig'),
      name: 'boxTagConfig',
      meta: {
        title: 'cusEntry.supplement20250211.cartonBarcodeLabelConfig',  // '装箱条码标签配置'
        requiresAuth: true
      }
    },
    {
      path: 'boxBarcodeRule',
      component: () =>
        import('modb@/barcodeManagement/views/boxbarcoderule'),
      name: 'boxBarcodeRule',
      meta: {
        title: 'route.barcodeGenerateRule',  // '条码生成规则配置'
        requiresAuth: true
      }
    },
    {
      path: 'outerBoxCode',
      component: () =>
        import('modb@/barcodeManagement/views/outerboxcode'),
      name: 'outerBoxCode',
      meta: {
        title: 'hierarchical.Outerboxbarcodemanagement', // '外箱条码管理'
        requiresAuth: true
      }
    },
    {
      path: 'innerOuterRelation',
      component: () =>
        import('modb@/barcodeManagement/views/innerouterrelation'),
      name: 'innerOuterRelation',
      meta: {
        title: 'route.barcodeRelation',  // '内外箱关联'
        requiresAuth: true
      }
    }
  ]
}
