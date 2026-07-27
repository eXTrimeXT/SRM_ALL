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
        import('mod@/barcodeManagement/views/innerboxcode'),
      name: 'innerBoxCode',
      meta: {
        title: '内箱条码',
        requiresAuth: true
      }
    },
    {
      path: 'boxTagConfig',
      component: () =>
        import('mod@/barcodeManagement/views/boxtagconfig'),
      name: 'boxTagConfig',
      meta: {
        title: '装箱条码标签配置',
        requiresAuth: true
      }
    },
    {
      path: 'boxBarcodeRule',
      component: () =>
        import('mod@/barcodeManagement/views/boxbarcoderule'),
      name: 'boxBarcodeRule',
      meta: {
        title: '条码生成规则配置',
        requiresAuth: true
      }
    },
    {
      path: 'outerBoxCode',
      component: () =>
        import('mod@/barcodeManagement/views/outerboxcode'),
      name: 'outerBoxCode',
      meta: {
        title: '外箱条码管理',
        requiresAuth: true
      }
    },
    {
      path: 'innerOuterRelation',
      component: () =>
        import('mod@/barcodeManagement/views/innerouterrelation'),
      name: 'innerOuterRelation',
      meta: {
        title: '内外箱关联',
        requiresAuth: true
      }
    }
  ]
}
