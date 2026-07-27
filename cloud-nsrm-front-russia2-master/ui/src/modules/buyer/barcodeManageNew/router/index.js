// 条码管理（新）
export default {
  path: '/barcodeManageNew',
  name: 'barcodeManageNew',
  component: () => import('@/layout'),
  redirect: {
    name: 'barcodeRuleConfig'
  },
  children: [
  // 条码规则配置 路径：/barcodeManageNew/barcodeRuleConfig
    {
      path: 'barcodeRuleConfig',
      name: 'barcodeRuleConfig',
      component: () => import('modb@/barcodeManageNew/views/barcodeRuleConfig'),
      meta: {
        title: 'route.barcodeRuleConfig',
        requiresAuth: true
      }
    },
  // 条码生成规则配置 路径：/barcodeManageNew/barcodeGenerateRule
    {
      path: 'barcodeGenerateRule',
      name: 'barcodeGenerateRule',
      component: () => import('modb@/barcodeManageNew/views/barcodeGenerateRule'),
      meta: {
        title: 'route.barcodeGenerateRule',
        requiresAuth: true
      }
    },
  // 打印模板 路径：/barcodeManageNew/barcodePrintTemp
    {
      path: 'barcodePrintTemp',
      name: 'barcodePrintTemp',
      component: () => import('modb@/barcodeManageNew/views/barcodePrintTemp'),
      meta: {
        title: 'route.barcodePrintTemp',
        requiresAuth: true
      }
    },
    // 内箱条码打印 路径：/barcodeManageNew/barcodeInnerPrint
    {
      path: 'barcodeInnerPrint',
      name: 'barcodeInnerPrintBuyer',
      component: () => import('modb@/barcodeManageNew/views/barcodeInnerPrint'),
      meta: {
        title: 'route.barcodeInnerPrint',
        requiresAuth: true
      }
    },
    // 内外箱关联 路径：/barcodeManageNew/barcodeRelationBuyer
    {
      path: 'barcodeRelationBuyer',
      name: 'barcodeRelationBuyer',
      component: () => import('modb@/barcodeManageNew/views/barcodeRelationBuyer'),
      meta: {
        title: 'route.barcodeRelation',
        requiresAuth: true
      }
    },
    // 外箱条码打印 路径：/barcodeManageNew/barcodeOuterPrint
    {
      path: 'barcodeOuterPrint',
      name: 'barcodeOuterPrintBuyer',
      component: () => import('modb@/barcodeManageNew/views/barcodeOuterPrints'),
      meta: {
        title: 'route.barcodeOuterPrint',
        requiresAuth: true
      }
    }
  ]
}
