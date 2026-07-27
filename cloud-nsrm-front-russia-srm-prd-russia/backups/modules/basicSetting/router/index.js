
export default {
  path: '/baseSetting',
  name: 'baseSetting',
  component: () => import('@/layout'),
  redirect: {
    name: 'settingGuide'
  },
  children: [
    {
      path: 'crystalBallReport',
      component: () => import('mod@/basicSetting/views/crystalBallReport'),
      name: 'crystalBallReport',
      meta: {
        title: '水晶球报表开发',
        requiresAuth: true
      }
    },
    {
      path: 'modelConfig',
      component: () => import('mod@/basicSetting/views/modelConfig'),
      name: 'modelConfig',
      meta: {
        title: 'route.modelConfig',
        requiresAuth: true
      }
    },
    {
      path: 'tableDemo',
      component: () => import('mod@/basicSetting/views/tableDemo'),
      name: 'tableDemo',
      meta: {
        title: 'route.multipleVideo', // table模板
        requiresAuth: true
      }
    },
    // {
    //   path: "multipleVideo",
    //   component: () => import("mod@/basicSetting/views/multipleVideo"),
    //   name: "multipleVideo",
    //   meta: {
    //     title: 'route.multipleVideo',   // 多人视频
    //     requiresAuth: true
    //   }
    // },
    {
      path: 'exportCenter',
      component: () => import('mod@/basicSetting/views/exportCenter'),
      name: 'exportCenter',
      meta: {
        title: 'route.exportCenter', // 导出中心
        requiresAuth: true
      }
    },
    {
      path: 'dynamicReportConfig',
      component: () => import('mod@/basicSetting/views/dynamicReportConfig'),
      name: 'dynamicReportConfig',
      meta: {
        title: 'route.dynamicReportConfig', // 动态报表配置
        requiresAuth: true
      }
    },
    {
      path: 'dynamicReportPage/:sqlCode',
      component: () => import('mod@/basicSetting/views/dynamicReportPage'),
      name: 'dynamicReportPage',
      meta: {
        title: 'route.dynamicReportPage', // 动态报表
        requiresAuth: true
      }
    },
    {
      path: 'settingGuide',
      component: () => import('mod@/basicSetting/views/settingGuide'),
      name: 'settingGuide',
      meta: {
        title: 'route.settingGuide', // 配置引导
        requiresAuth: true
      }
    },
    {
      path: 'timedTaskBind',
      component: () => import('mod@/basicSetting/views/timedTaskBind'),
      name: 'timedTaskBind',
      meta: {
        title: 'route.timedTaskBind', // 定时任务绑定
        requiresAuth: true
      }
    },
    {
      path: 'timedTaskConfig',
      component: () => import('mod@/basicSetting/views/timedTaskConfig'),
      name: 'timedTaskConfig',
      meta: {
        title: 'route.timedTaskConfig', // 定时任务配置
        requiresAuth: true
      }
    },
    {
      path: 'organizationSetting',
      component: () => import('mod@/basicSetting/views/organizationSetting'),
      name: 'organizationSetting',
      meta: {
        title: 'route.organizationSetting', // 组织设置
        requiresAuth: true
      }
    },
    {
      path: 'accessFlowSetting',
      component: () => import('mod@/basicSetting/views/accessFlowSetting'),
      name: 'accessFlowSetting',
      meta: {
        title: 'route.accessFlowSetting', // 准入流程配置
        requiresAuth: true
      }
    },
    {
      path: 'businessStateControl',
      component: () => import('mod@/basicSetting/views/businessStateControl'),
      name: 'businessStateControl',
      meta: {
        title: 'route.businessStateControl', // 业务状态控制
        requiresAuth: true
      }
    },
    {
      path: 'fileManagement',
      component: () => import('mod@/basicSetting/views/fileManagement'),
      name: 'fileManagement',
      meta: {
        title: 'route.fileManagement', // 附件管理
        requiresAuth: true
      }
    },
    {
      path: 'sceneFileManagement',
      component: () => import('mod@/basicSetting/views/sceneFileManagement'),
      name: 'sceneFileManagement',
      meta: {
        title: 'route.sceneFileManagement', // 附件管理（新）
        requiresAuth: true
      }
    },
    {
      path: 'categoryDivision',
      component: () => import('mod@/basicSetting/views/categoryDivision'),
      name: 'categoryDivision',
      meta: {
        title: 'route.categoryDivision', // 品类分工
        requiresAuth: true
      }
    },
    {
      path: 'modeDemo',
      component: () => import('mod@/basicSetting/views/modeDemo'),
      name: 'modeDemo',
      meta: {
        title: 'router.modeDemo', // 合同模板管理demo
        requiresAuth: true
      }
    },
    {
      path: 'contractDemo',
      component: () => import('mod@/basicSetting/views/contractDemo'),
      name: 'contractDemo',
      meta: {
        title: 'router.contractDemo', // 合同管理demo
        requiresAuth: true
      }
    },
    {
      path: 'manageLevelSetting',
      component: () => import('mod@/basicSetting/views/manageLevelSetting'),
      name: 'manageLevelSetting',
      meta: {
        title: 'route.manageLevelSetting', // 管理层级设置
        requiresAuth: true
      }
    },
    {
      path: 'dictionaryMaintenance',
      component: () => import('mod@/basicSetting/views/dictionaryMaintenance'),
      name: 'dictionaryMaintenance',
      meta: {
        title: 'route.dictionaryMaintenance', // 字典维护
        requiresAuth: true
      }
    },
    {
      path: 'userRegisterCount',
      component: () => import('mod@/basicSetting/views/userRegisterCount'),
      name: 'userRegisterCount',
      meta: {
        title: 'route.userRegisterCount', // 用户登录统计
        requiresAuth: true
      }
    },
    {
      path: 'userULog',
      component: () => import('mod@/basicSetting/views/userULog'),
      name: 'userULog',
      meta: {
        title: 'route.userULog', // 用户操作日志
        requiresAuth: true
      }
    },
    {
      path: 'electronicReport',
      component: () => import('mod@/basicSetting/views/electronicReport'),
      name: 'electronicReport',
      meta: {
        title: 'route.electronicReport', // 用户操作日志
        requiresAuth: true
      }
    },
    {
      path: 'messageMaintenance',
      component: () => import('mod@/basicSetting/views/messageMaintenance'),
      name: 'messageMaintenance',
      meta: {
        title: 'route.messageMaintenance', // 消息定义
        requiresAuth: true
      }
    },
    {
      path: 'materialMaintenance',
      component: () => import('mod@/basicSetting/views/materialMaintenance'),
      name: 'materialMaintenance',
      meta: {
        title: 'route.materialMaintenance', // 物料维护
        requiresAuth: true
      }
    },
    {
      path: 'purchaseBaseSetting',
      component: () => import('mod@/basicSetting/views/purchaseBaseSetting'),
      name: 'purchaseBaseSetting',
      meta: {
        title: 'route.purchaseBaseSetting', // 采购基础设置
        requiresAuth: true
      }
    },
    {
      path: 'vendorAttributeSetting',
      component: () => import('mod@/basicSetting/views/vendorAttributeSetting'),
      name: 'vendorAttributeSetting',
      meta: {
        title: 'route.vendorAttributeSetting', // 供应商属性设置
        requiresAuth: true
      }
    },
    {
      path: 'vendorAttributeControl',
      component: () => import('mod@/basicSetting/views/vendorAttributeControl'),
      name: 'vendorAttributeControl',
      meta: {
        title: 'route.vendorAttributeControl', //  供应商属性管理
        requiresAuth: true
      }
    },
    {
      path: 'vendorDataImport',
      component: () => import('mod@/basicSetting/views/vendorDataImport'),
      name: 'vendorDataImport',
      meta: {
        title: 'route.vendorDataImport', // 供应商数据导入
        requiresAuth: true
      }
    },
    {
      path: 'workflowSetting',
      component: () => import('mod@/basicSetting/views/workflowSetting'),
      name: 'workflowSetting',
      meta: {
        title: 'route.workflowSetting', // 流程模板配置
        requiresAuth: true
      }
    },
    {
      path: 'purchaseCategoryMaintenance',
      component: () =>
        import('mod@/basicSetting/views/purchaseCategoryMaintenance'),
      name: 'purchaseCategoryMaintenance',
      meta: {
        title: 'route.purchaseCategoryMaintenance', // 采购分类维护
        requiresAuth: true
      }
    },
    {
      path: 'sitePeriodSetting',
      component: () => import('mod@/basicSetting/views/sitePeriodSetting'),
      name: 'sitePeriodSetting',
      meta: {
        title: 'route.sitePeriodSetting', // 现场评审周期设置
        requiresAuth: true
      }
    },
    {
      path: 'quickSearchConfig',
      component: () => import('mod@/basicSetting/views/quickSearchConfig'),
      name: 'quickSearchConfig',
      meta: {
        title: 'route.quickSearchConfig', // 快速查询配置
        requiresAuth: true
      }
    },
    {
      path: 'quickSearchDemo',
      component: () => import('@/componentDoc'),
      name: 'quickSearchDemo',
      meta: {
        title: 'route.quickSearchDemo', // 系统组件示例
        requiresAuth: true
      }
    },
    {
      path: 'orderNoConfig',
      component: () => import('mod@/basicSetting/views/orderNoConfig'),
      name: 'orderNoConfig',
      meta: {
        title: 'route.orderNoConfig', // 单据号规则配置
        requiresAuth: true
      }
    },
    {
      path: 'categoryResponsibility',
      component: () => import('mod@/basicSetting/views/categoryResponsibility'),
      name: 'categoryResponsibility',
      meta: {
        title: 'route.supplierLeader', // 供应商supplier leader
        requiresAuth: true
      }
    },
    {
      path: 'materialCategoryMaintain',
      component: () =>
        import('mod@/basicSetting/views/materialCategoryMaintain'),
      name: 'materialCategoryMaintain',
      meta: {
        title: 'route.materialCategoryMaintain', // 物料小类业务小类维护
        requiresAuth: true
      }
    },
    {
      path: 'bussinessTypeConfig',
      component: () => import('mod@/basicSetting/views/bussinessTypeConfig'),
      name: 'bussinessTypeConfig',
      meta: {
        title: 'route.bussinessTypeConfig', // 业务类型配置
        requiresAuth: true
      }
    },
    {
      path: 'interfaceRepushList',
      component: () => import('mod@/basicSetting/views/interfaceRepushList'),
      name: 'interfaceRepushList',
      meta: {
        title: 'route.interfaceRepush', // 接口重推
        requiresAuth: true
      }
    },
    {
      path: 'redisCacheList',
      component: () => import('mod@/basicSetting/views/redisCacheList'),
      name: 'redisCacheList',
      meta: {
        title: 'route.redisCache', // redis缓存管理
        requiresAuth: true
      }
    },
    {
      path: 'reportSetting',
      component: () => import('mod@/basicSetting/views/reportSetting'),
      name: 'reportSetting',
      meta: {
        title: 'route.reportSetting', // 报表配置
        requiresAuth: true
      }
    },
    {
      path: 'quotaSetting',
      component: () => import('mod@/basicSetting/views/quotaSetting'),
      name: 'quotaSetting',
      meta: {
        title: 'route.quotaSetting', // 配额配置
        requiresAuth: true
      }
    },
    {
      path: 'systemDock',
      component: () => import('mod@/basicSetting/views/systemDock'),
      name: 'systemDock',
      meta: {
        title: 'route.systemDock', // 系统对接配置
        requiresAuth: true
      }
    },
    {
      path: 'monitorBizConfig',
      component: () => import('mod@/basicSetting/views/monitorBizConfig'),
      name: 'monitorBizConfig',
      meta: {
        title: 'route.monitorBizConfig', // 业务监控配置
        requiresAuth: true
      }
    },
    {
      path: 'monitorBizLog',
      component: () => import('mod@/basicSetting/views/monitorBizLog'),
      name: 'monitorBizLog',
      meta: {
        title: 'route.monitorBizLog', // 业务监控日志
        requiresAuth: true
      }
    },
    {
      path: 'noticetemplate',
      component: () => import('mod@/basicSetting/views/noticetemplate'),
      name: 'noticetemplate',
      meta: {
        title: 'route.noticetemplate', // 通知模板
        requiresAuth: true
      }
    }, {
      path: 'i18nSetting',
      component: () => import('mod@/basicSetting/views/i18nSetting'),
      name: 'i18nSetting',
      meta: {
        title: 'route.i18nSetting', // 多语言导入导出
        requiresAuth: true
      }
    },
    {
      path: 'drawingshead',
      component: () =>
        import('mod@/basicSetting/views/drawingshead'),
      name: 'drawingshead',
      meta: {
        title: 'route.materialDrawings',
        requiresAuth: true
      }
    },
    {
      path: 'reportStatistics',
      component: () =>
        import('mod@/basicSetting/views/reportStatistics'),
      name: 'reportStatistics',
      meta: {
        title: '报表统计',
        requiresAuth: true
      }
    },
    {
      path: 'formPage',
      component: () =>
        import('mod@/basicSetting/views/formPage'),
      name: 'formPage',
      meta: {
        title: '表单页面',
        requiresAuth: true
      }
    },
    {
      path: 'formPageScene',
      component: () =>
        import('mod@/basicSetting/views/formPage/FormPageScene'),
      name: 'formPageScene',
      meta: {
        title: '表单页面',
        requiresAuth: true
      }
    },
    {
      path: 'vendorStateSetting',
      component: () =>
        import('mod@/basicSetting/views/vendorStateSetting'),
      name: 'vendorStateSetting',
      meta: {
        title: '品类状态配置',
        requiresAuth: true
      }
    },
    {
      path: 'formPageDynamic/:pageCode',
      component: () => import('@/modules/basicSetting/views/formPage/dynamic'),
      name: 'formPageDynamic',
      meta: {
        title: 'route.formPageDynamic', // 动态页面
        requiresAuth: true
      }
    },
    {
      path: 'dataPermission',
      component: () => import('mod@/basicSetting/views/dataPermission'),
      name: 'dataPermission',
      meta: {
        title: 'route.dataPermission',
        requiresAuth: true
      }
    },
    {
      path: 'dataPermissionOption',
      component: () => import('mod@/basicSetting/views/dataPermissionOption'),
      name: 'dataPermissionOption',
      meta: {
        title: 'route.dataPermissionOption',
        requiresAuth: true
      }
    },
    {
      path: 'systemConfigure',
      component: () => import('mod@/basicSetting/views/systemConfigure'),
      name: 'systemConfigure',
      meta: {
        title: 'systemConfigure',
        requiresAuth: true
      }
    }
  ]
}
