export default {
  path: '/reportManagement',
  name: 'reportManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'protocolQuery'
  },
  children: [
    {
      path: 'protocolQuery',
      component: () => import('modcb@/reportManagement/views/protocolQuery'),
      name: 'recruitment',
      meta: {
        title: 'cusEntry.route.protocolQuery', // 协议查询报表
        requiresAuth: true
      }
    },
    {
      path: 'suppliers',
      component: () => import('modcb@/reportManagement/views/suppliers'),
      name: 'suppliers',
      meta: {
        title: 'cusEntry.route.suppliers', // 供应商报表
        requiresAuth: true
      }
    },
    {
      path: 'ontimeRatioArrivalReport',
      component: () => import('modcb@/reportManagement/views/ontimeRatioArrivalReport'),
      name: 'ontimeRatioArrivalReport',
      meta: {
        title: 'cusEntry.route.ontimeRatioArrivalReport', // 供应商分析报表（到货及时率汇总）
        requiresAuth: true
      }
    },
    {
      path: 'procurementProgress',
      component: () => import('modcb@/reportManagement/views/procurementProgress'),
      name: 'procurementProgress',
      meta: {
        title: 'cusEntry.route.procurementProgress', // 采购进度报表
        requiresAuth: true
      }
    },
    {
      path: 'procurementProgressRUB',
      component: () => import('modcb@/reportManagement/views/procurementProgressRUB'),
      name: 'procurementProgressRUB',
      meta: {
        title: 'cusEntry.route.procurementProgressRUB', // 采购进度报表（RUB）
        requiresAuth: true
      }
    },
    {
      path: 'materialPriceTrends',
      component: () => import('modcb@/reportManagement/views/materialPriceTrends'),
      name: 'materialPriceTrends',
      meta: {
        title: 'cusEntry.route.materialPriceTrends', // 物料区域价格
        requiresAuth: true
      }
    },
    {
      path: 'materialAreaDetail',
      component: () => import('modcb@/reportManagement/views/materialAreaDetail'),
      name: 'materialAreaDetail',
      meta: {
        title: 'cusEntry.route.materialAreaDetail', // 物料区域明细
        requiresAuth: true
      }
    },
    {
      path: 'supplierQuantity',
      component: () => import('modcb@/reportManagement/views/supplierQuantity'),
      name: 'supplierQuantity',
      meta: {
        title: 'cusEntry.route.supplierQuantity', // 供应商数量
        requiresAuth: true
      }
    },
    {
      path: 'blackSupplierQuantity',
      component: () => import('modcb@/reportManagement/views/blackSupplierQuantity'),
      name: 'blackSupplierQuantity',
      meta: {
        title: 'cusEntry.route.blackSupplierQuantity', // 供应商黑名单数量
        requiresAuth: true
      }
    },
    {
      path: 'limitSupplierQuantity',
      component: () => import('modcb@/reportManagement/views/limitSupplierQuantity'),
      name: 'limitSupplierQuantity',
      meta: {
        title: 'cusEntry.route.limitSupplierQuantity', // 受限供应商数量
        requiresAuth: true
      }
    },
    {
      path: 'supplierActivity',
      component: () => import('modcb@/reportManagement/views/supplierActivity'),
      name: 'supplierActivity',
      meta: {
        title: 'cusEntry.route.supplierActivity', // 供应商活跃情况
        requiresAuth: true
      }
    },
    {
      path: 'deductWarrantyDeposit',
      component: () => import('modcb@/reportManagement/views/deductWarrantyDeposit'),
      name: 'deductWarrantyDeposit',
      meta: {
        title: 'cusEntry.route.deductWarrantyDeposit', // 扣除质保金
        requiresAuth: true
      }
    },
    {
      path: 'supplierPerformanceStatus',
      component: () => import('modcb@/reportManagement/views/supplierPerformanceStatus'),
      name: 'supplierPerformanceStatus',
      meta: {
        title: 'cusEntry.route.supplierPerformanceStatus', // 供应商履约情况
        requiresAuth: true
      }
    },
    {
      path: 'projectProgress',
      component: () => import('modcb@/reportManagement/views/projectProgress'),
      name: 'projectProgress',
      meta: {
        title: 'cusEntry.route.projectProgress', // 项目进度
        requiresAuth: true
      }
    },
    {
      path: 'budgetAndExecutionAmount',
      component: () => import('modcb@/reportManagement/views/budgetAndExecutionAmount'),
      name: 'budgetAndExecutionAmount',
      meta: {
        title: 'cusEntry.route.budgetAndExecutionAmount', // 预算与执行金额
        requiresAuth: true
      }
    },
    {
      path: 'specialBidding',
      component: () => import('modcb@/reportManagement/views/specialBidding'),
      name: 'specialBidding',
      meta: {
        title: 'cusEntry.route.specialBidding', // 特殊招标
        requiresAuth: true
      }
    },
    {
      path: 'expertsNumber',
      component: () => import('modcb@/reportManagement/views/expertsNumber'),
      name: 'expertsNumber',
      meta: {
        title: 'cusEntry.route.expertsNumber', // 专家总数量
        requiresAuth: true
      }
    },
    {
      path: 'partyProjectNumber',
      component: () => import('modcb@/reportManagement/views/partyProjectNumber'),
      name: 'partyProjectNumber',
      meta: {
        title: 'cusEntry.route.partyProjectNumber', // 参与项目数量
        requiresAuth: true
      }
    },
    {
      path: 'planImplementation',
      component: () => import('modcb@/reportManagement/views/planImplementation'),
      name: 'planImplementation',
      meta: {
        title: 'cusEntry.route.planImplementation', // 计划实施情况
        requiresAuth: true
      }
    },
    {
      path: 'bondAndRefund',
      component: () => import('modcb@/reportManagement/views/bondAndRefund'),
      name: 'bondAndRefund',
      meta: {
        title: 'cusEntry.route.bondAndRefund',
        requiresAuth: true
      }
    },
    {
      path: 'depositCollectAndRefund',
      component: () => import('modcb@/reportManagement/views/depositCollectAndRefund'),
      name: 'depositCollectAndRefund',
      meta: {
        title: 'cusEntry.route.depositCollectAndRefund',
        requiresAuth: true
      }
    },
    {
      path: 'priceRatio',
      component: () => import('modcb@/reportManagement/views/priceRatio'),
      name: 'priceRatio',
      meta: {
        title: 'cusEntry.route.priceRatio',
        requiresAuth: true
      }
    }
  ]
}
