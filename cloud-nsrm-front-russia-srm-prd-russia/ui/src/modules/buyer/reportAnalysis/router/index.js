export default {
  path: '/report',
  name: 'report',
  component: () => import('@/layout'),
  redirect: {
    name: 'qualityAnalysis'
  },
  children: [
    {
      path: 'qualityAnalysis',
      component: () => import('modb@/reportAnalysis/views/qualityAnalysis'),
      name: 'qualityAnalysis',
      meta: {
        title: 'reportMod.qualityAnalysis', // 质量分析
        requiresAuth: true
      }
    },
    {
      path: 'costReductionAnalysis',
      component: () => import('modb@/reportAnalysis/views/costReductionAnalysis'),
      name: 'costReductionAnalysis',
      meta: {
        title: 'reportMod.costReductionAnalysis', // 采购降本
        requiresAuth: true
      }
    },
    {
      path: 'supplierAnalysis',
      component: () => import('modb@/reportAnalysis/views/supplierAnalysis'),
      name: 'supplierAnalysis',
      meta: {
        title: 'reportMod.supplierAnalysis', // 供应商分析
        requiresAuth: true
      }
    },
    {
      path: 'deliverRateVendor',
      component: () => import('modb@/reportAnalysis/views/deliverRateVendor'),
      name: 'deliverRateVendor',
      meta: {
        title: 'reportMod.deliverRateVendor', // 交货达成率-供应商
        requiresAuth: true
      }
    },
    {
      path: 'deliverRateCategoryOrg',
      component: () => import('modb@/reportAnalysis/views/deliverRateCategoryOrg'),
      name: 'deliverRateCategoryOrg',
      meta: {
        title: 'reportMod.deliverRateCategoryOrg', // 交货达成率-(品类+组织)
        requiresAuth: true
      }
    },
    {
      path: 'priceCycle',
      component: () => import('modb@/reportAnalysis/views/priceCycle'),
      name: 'priceCycle',
      meta: {
        title: 'reportMod.priceCycle', // 定价周期
        requiresAuth: true
      }
    },
    {
      path: 'statementPrescriptionStatistics',
      component: () =>
        import('modb@/reportAnalysis/views/statementPrescriptionStatistics'),
      name: 'statementPrescriptionStatistics',
      meta: {
        title: 'reportMod.statementPrescriptionStatistics', // 对账时效
        requiresAuth: true
      }
    },
    {
      path: 'qualificationSupply',
      component: () => import('modb@/reportAnalysis/views/qualificationSupply'),
      name: 'qualificationSupply',
      meta: {
        title: 'reportMod.qualificationSupply', // 供应商资质证件预警
        requiresAuth: true
      }
    },
    {
      path: 'contractReport',
      component: () => import('modb@/reportAnalysis/views/contractReport'),
      name: 'contractReport',
      meta: {
        title: 'reportMod.contractReport', // 合同预警
        requiresAuth: true
      }
    },
    {
      path: 'orderOverdue',
      component: () => import('modb@/reportAnalysis/views/orderOverdue'),
      name: 'orderOverdue',
      meta: {
        title: 'reportMod.orderOverdue', // 超期未关闭订单
        requiresAuth: true
      }
    },
    {
      path: 'orderOverduezb',
      component: () => import('modb@/reportAnalysis/views/orderOverduezb'),
      name: 'orderOverduezb',
      meta: {
        title: 'reportMod.orderOverduezb', // 超期未关闭订单占比
        requiresAuth: true
      }
    },
    {
      path: 'contractPlanAction',
      component: () => import('modb@/reportAnalysis/views/contractPlanAction'),
      name: 'contractPlanAction',
      meta: {
        title: 'reportMod.contractPlanAction', // 合同执行计划
        requiresAuth: true
      }
    },
    {
      path: 'bidAction',
      component: () => import('modb@/reportAnalysis/views/bidAction'),
      name: 'bidAction',
      meta: {
        title: 'reportMod.bidAction', // 招标执行计划
        requiresAuth: true
      }
    },
    {
      path: 'yearPlanAction',
      component: () => import('modb@/reportAnalysis/views/yearPlanAction'),
      name: 'yearPlanAction',
      meta: {
        title: 'reportMod.yearPlanAction', // 年度采购计划执行
        requiresAuth: true
      }
    },
    {
      path: 'soleSupply',
      component: () => import('modb@/reportAnalysis/views/soleSupply'),
      name: 'soleSupply',
      meta: {
        title: 'reportMod.soleSupply', // 独家供货供应商
        requiresAuth: true
      }
    },
    {
      path: 'vendorOptimize',
      component: () => import('modb@/reportAnalysis/views/vendorOptimize'),
      name: 'vendorOptimize',
      meta: {
        title: 'reportMod.vendorOptimize', // 供方优化
        requiresAuth: true
      }
    },
    {
      path: 'vendorAvgSupply',
      component: () => import('modb@/reportAnalysis/views/vendorAvgSupply'),
      name: 'vendorAvgSupply',
      meta: {
        title: 'reportMod.vendorAvgSupply', // 供应商平均供货规模
        requiresAuth: true
      }
    },
    {
      path: 'soleSupplyAnalysis',
      component: () => import('modb@/reportAnalysis/views/soleSupplyAnalysis'),
      name: 'soleSupplyAnalysis',
      meta: {
        title: 'reportMod.soleSupplyAnalysis', // 独家供货分析
        requiresAuth: true
      }
    },
    {
      path: 'purchaseCostReductionAnalysis',
      component: () =>
        import('modb@/reportAnalysis/views/purchaseCostReductionAnalysis'),
      name: 'purchaseCostReductionAnalysis',
      meta: {
        title: 'reportMod.purchaseCostReductionAnalysis', // 采购降本分析
        requiresAuth: true
      }
    },
    {
      path: 'deliveryRateWerks',
      component: () => import('modb@/reportAnalysis/views/deliveryRateWerks'),
      name: 'deliveryRateWerks',
      meta: {
        title: 'reportMod.deliveryRateWerks', // 交货达成率-工厂
        requiresAuth: true
      }
    }
  ]
}
