import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/baseSetting',
  name: 'baseSetting',
  component: () => import('@/layout'),
  redirect: {
    name: 'settingGuide'
  },
  children: [
    // 业务操作日志 /baseSetting/businessOperationLog
    {
      path: 'businessOperationLog',
      component: () => import('modb@/basicSetting/views/businessOperationLog'),
      name: 'businessOperationLog',
      meta: {
        title: 'route.businessOperationLog',
        requiresAuth: true
      }
    },
    {
      path: 'sysThemeConfig',
      component: () => import('modb@/basicSetting/views/sysThemeConfig'),
      name: 'sysThemeConfig',
      meta: {
        title: 'route.sysThemeConfig', // 系统主题设置
        requiresAuth: true
      }
    },
    // 水晶球报表开发 /baseSetting/crystalBallReport
    {
      path: 'crystalBallReport',
      component: () => import('modb@/basicSetting/views/crystalBallReport'),
      name: 'crystalBallReport',
      meta: {
        title: 'route.crystalBallReport',
        requiresAuth: true
      }
    },
    // 消息提示管理
    {
      path: 'messageManagement',
      component: () => import('modb@/basicSetting/views/messageManagement'),
      name: 'messageManagement',
      meta: {
        title: 'route.messageManagement',
        requiresAuth: true
      }
    },
    // 模板配置 /baseSetting/modelConfig
    {
      path: 'modelConfig',
      component: () => import('modb@/basicSetting/views/modelConfig'),
      name: 'modelConfig',
      meta: {
        title: 'route.modelConfig',
        requiresAuth: true
      }
    },
    // 导出中心 /baseSetting/exportCenter
    {
      path: 'exportCenter',
      component: () => import('modb@/basicSetting/views/exportCenter'),
      name: 'exportCenter',
      meta: {
        title: 'route.exportCenter',
        requiresAuth: true
      }
    },
    // 动态报表配置 /baseSetting/dynamicReportConfig
    {
      path: 'dynamicReportConfig',
      component: () => import('modb@/basicSetting/views/dynamicReportConfig'),
      name: 'dynamicReportConfig',
      meta: {
        title: 'route.dynamicReportConfig',
        requiresAuth: true
      }
    },
    // 配置引导 /baseSetting/settingGuide
    {
      path: 'settingGuide',
      component: () => import('modb@/basicSetting/views/settingGuide'),
      name: 'settingGuide',
      meta: {
        title: 'route.settingGuide',
        requiresAuth: true
      }
    },
    // 定时任务绑定 /baseSetting/timedTaskBind
    {
      path: 'timedTaskBind',
      component: () => import('modb@/basicSetting/views/timedTaskBind'),
      name: 'timedTaskBind',
      meta: {
        title: 'route.timedTaskBind',
        requiresAuth: true
      }
    },
    // 定时任务配置 /baseSetting/timedTaskConfig
    {
      path: 'timedTaskConfig',
      component: () => import('modb@/basicSetting/views/timedTaskConfig'),
      name: 'timedTaskConfig',
      meta: {
        title: 'route.timedTaskConfig',
        requiresAuth: true
      }
    },
    // 组织设置 /baseSetting/organizationSetting
    {
      path: 'organizationSetting',
      component: () => import('modb@/basicSetting/views/organizationSetting'),
      name: 'organizationSetting',
      meta: {
        title: 'route.organizationSetting',
        requiresAuth: true
      }
    },
    // 准入流程配置 /baseSetting/accessFlowSetting
    {
      path: 'accessFlowSetting',
      component: () => meiqlCtrl.accessFlowSetting === 'Y'
        ? import('modb@/basicSetting/views/accessFlowSetting')
        : import('modb@/basicSetting/views/accessFlowSettingOldOne'),
      name: 'accessFlowSetting',
      meta: {
        title: 'route.accessFlowSetting',
        requiresAuth: true
      }
    },
    // 准入流程配置 /baseSetting/accessFlowSetting
    {
      path: 'accessFlowSetting_backups',
      component: () => import('modb@/basicSetting/views/accessFlowSetting_backups'),
      name: 'accessFlowSetting_backups',
      meta: {
        title: 'route.accessFlowSetting_backups',
        requiresAuth: true
      }
    },
    // 附件管理（新） /baseSetting/sceneFileManagement
    {
      path: 'sceneFileManagement',
      component: () => import('modb@/basicSetting/views/sceneFileManagement'),
      name: 'sceneFileManagement',
      meta: {
        title: 'route.sceneFileManagement',
        requiresAuth: true
      }
    },
    // 管理层级设置 /baseSetting/manageLevelSetting
    {
      path: 'manageLevelSetting',
      component: () => import('modb@/basicSetting/views/manageLevelSetting'),
      name: 'manageLevelSetting',
      meta: {
        title: 'route.manageLevelSetting',
        requiresAuth: true
      }
    },
    // 字典维护 /baseSetting/dictionaryMaintenance
    {
      path: 'dictionaryMaintenance',
      component: () => import('modb@/basicSetting/views/dictionaryMaintenance'),
      name: 'dictionaryMaintenance',
      meta: {
        title: 'route.dictionaryMaintenance',
        requiresAuth: true
      }
    },
    // 用户登录统计 /baseSetting/userRegisterCount
    {
      path: 'userRegisterCount',
      component: () => import('modb@/basicSetting/views/userRegisterCount'),
      name: 'userRegisterCount',
      meta: {
        title: 'route.userRegisterCount',
        requiresAuth: true
      }
    },
    // 用户操作日志 /baseSetting/userULog
    {
      path: 'userULog',
      component: () => import('modb@/basicSetting/views/userULog'),
      name: 'userULog',
      meta: {
        title: 'route.userULog',
        requiresAuth: true
      }
    },
    // 用户行为分析 /baseSetting/electronicReport
    {
      path: 'electronicReport',
      component: () => import('modb@/basicSetting/views/electronicReport'),
      name: 'electronicReport',
      meta: {
        title: 'route.electronicReport',
        requiresAuth: true
      }
    },
    // 消息定义 /baseSetting/messageMaintenance
    {
      path: 'messageMaintenance',
      component: () => import('modb@/basicSetting/views/messageMaintenance'),
      name: 'messageMaintenance',
      meta: {
        title: 'route.messageMaintenance',
        requiresAuth: true
      }
    },
    // 采购基础设置 /baseSetting/purchaseBaseSetting
    {
      path: 'purchaseBaseSetting',
      component: () => import('modb@/basicSetting/views/purchaseBaseSetting'),
      name: 'purchaseBaseSetting',
      meta: {
        title: 'route.purchaseBaseSetting',
        requiresAuth: true
      }
    },
    // 供应商属性模板配置 /baseSetting/vendorAttributeSetting
    {
      path: 'vendorAttributeSetting',
      component: () => import('modb@/basicSetting/views/vendorAttributeSetting'),
      name: 'vendorAttributeSetting',
      meta: {
        title: 'route.vendorAttributeSetting',
        requiresAuth: true
      }
    },
    // 供应商属性管理 /baseSetting/vendorAttributeControl
    {
      path: 'vendorAttributeControl',
      component: () => import('modb@/basicSetting/views/vendorAttributeControl'),
      name: 'vendorAttributeControl',
      meta: {
        title: 'route.vendorAttributeControl',
        requiresAuth: true
      }
    },
    // 流程模板配置 /baseSetting/workflowSetting
    {
      path: 'workflowSetting',
      component: () => import('modb@/basicSetting/views/workflowSetting'),
      name: 'workflowSetting',
      meta: {
        title: 'route.workflowSetting',
        requiresAuth: true
      }
    },
    // 采购分类维护 /baseSetting/purchaseCategoryMaintenance
    {
      path: 'purchaseCategoryMaintenance',
      component: () =>
        import('modb@/basicSetting/views/purchaseCategoryMaintenance'),
      name: 'purchaseCategoryMaintenance',
      meta: {
        title: 'route.purchaseCategoryMaintenance',
        requiresAuth: true
      }
    },
    // 快速查询配置 /baseSetting/quickSearchConfig
    {
      path: 'quickSearchConfig',
      component: () => import('modb@/basicSetting/views/quickSearchConfig'),
      name: 'quickSearchConfig',
      meta: {
        title: 'route.quickSearchConfig',
        requiresAuth: true
      }
    },
    // 系统组件示例 /baseSetting/quickSearchDemo
    {
      path: 'quickSearchDemo',
      component: () => import('modb@/demo/views/componentDoc'),
      name: 'quickSearchDemo',
      meta: {
        title: 'route.quickSearchDemo',
        requiresAuth: true
      }
    },
    // 单据号规则配置 /baseSetting/orderNoConfig
    {
      path: 'orderNoConfig',
      component: () => import('modb@/basicSetting/views/orderNoConfig'),
      name: 'orderNoConfig',
      meta: {
        title: 'route.orderNoConfig',
        requiresAuth: true
      }
    },
    // 供应商对接人 /baseSetting/categoryResponsibility
    {
      path: 'categoryResponsibility',
      component: meiqlCtrl.siteReviewPlanConfirm === 'Y'
        ? () => import('modb@/basicSetting/views/categoryResponsibilityEngine')
        : () => import('modb@/basicSetting/views/categoryResponsibility'),
      name: 'categoryResponsibility',
      meta: {
        title: 'route.supplierLeader',
        requiresAuth: true
      }
    },
    // 接口重推 /baseSetting/interfaceRepushList
    {
      path: 'interfaceRepushList',
      component: () => import('modb@/basicSetting/views/interfaceRepushList'),
      name: 'interfaceRepushList',
      meta: {
        title: 'route.interfaceRepush',
        requiresAuth: true
      }
    },
    // redis缓存管理 /baseSetting/redisCacheList
    {
      path: 'redisCacheList',
      component: () => import('modb@/basicSetting/views/redisCacheList'),
      name: 'redisCacheList',
      meta: {
        title: 'route.redisCache',
        requiresAuth: true
      }
    },
    // 采购报表参数（业务用） /baseSetting/reportSetting
    {
      path: 'reportSetting',
      component: () => import('modb@/basicSetting/views/reportSetting'),
      name: 'reportSetting',
      meta: {
        title: 'route.reportSetting',
        requiresAuth: true
      }
    },
    // 物料配额比例 /baseSetting/quotaSetting
    {
      path: 'quotaSetting',
      component: () => import('modb@/basicSetting/views/quotaSetting'),
      name: 'quotaSetting',
      meta: {
        title: 'route.quotaSetting',
        requiresAuth: true
      }
    },
    // 系统接入配置 /baseSetting/systemDock
    {
      path: 'systemDock',
      component: () => import('modb@/basicSetting/views/systemDock'),
      name: 'systemDock',
      meta: {
        title: 'route.systemDock',
        requiresAuth: true
      }
    },
    // 业务监控配置 /baseSetting/monitorBizConfig
    {
      path: 'monitorBizConfig',
      component: () => import('modb@/basicSetting/views/monitorBizConfig'),
      name: 'monitorBizConfig',
      meta: {
        title: 'route.monitorBizConfig',
        requiresAuth: true
      }
    },
    // 业务监控日志 /baseSetting/monitorBizLog
    {
      path: 'monitorBizLog',
      component: () => import('modb@/basicSetting/views/monitorBizLog'),
      name: 'monitorBizLog',
      meta: {
        title: 'route.monitorBizLog',
        requiresAuth: true
      }
    },
    // 通知模板 /baseSetting/noticetemplate
    {
      path: 'noticetemplate',
      component: () => import('modb@/basicSetting/views/noticetemplate'),
      name: 'noticetemplate',
      meta: {
        title: 'route.noticetemplate',
        requiresAuth: true
      }
    },
    // 多语言导入导出 /baseSetting/i18nSetting
    {
      path: 'i18nSetting',
      component: () => import('modb@/basicSetting/views/i18nSetting'),
      name: 'i18nSetting',
      meta: {
        title: 'route.i18nSetting',
        requiresAuth: true
      }
    },
    // 物料图纸 /baseSetting/drawingshead
    {
      path: 'drawingshead',
      component: meiqlCtrl.drawingshead === 'Y'
        ? () => import('modb@/basicSetting/views/drawingsheadEngine')
        : () => import('modb@/basicSetting/views/drawingshead'),
      name: 'drawingshead',
      meta: {
        title: 'route.materialDrawings',
        requiresAuth: true
      }
    },
    // 表单页面 /baseSetting/formPage
    {
      path: 'formPage',
      component: () => import('modb@/basicSetting/views/formPage'),
      name: 'formPage',
      meta: {
        title: 'route.formPage',
        requiresAuth: true
      }
    },
    // 场景附件列配置 /baseSetting/formPageScene
    {
      path: 'formPageScene',
      component: () => import('modb@/basicSetting/views/formPage/FormPageScene'),
      name: 'formPageScene',
      meta: {
        title: 'route.formPageScene',
        requiresAuth: true
      }
    },
    // 品类状态配置 /baseSetting/vendorStateSetting
    {
      path: 'vendorStateSetting',
      component: () => import('modb@/basicSetting/views/vendorStateSetting'),
      name: 'vendorStateSetting',
      meta: {
        title: 'route.vendorStateSetting',
        requiresAuth: true
      }
    },
    // 动态页面 /baseSetting/formPageDynamic/:pageCode
    {
      path: 'formPageDynamic/:pageCode',
      component: () => import('modb@/basicSetting/views/formPage/dynamic'),
      name: 'formPageDynamic',
      meta: {
        title: 'route.formPageDynamic',
        requiresAuth: true
      }
    },
    // 功能数据权限 /baseSetting/dataPermission
    {
      path: 'dataPermission',
      component: () => import('modb@/basicSetting/views/dataPermission'),
      name: 'dataPermission',
      meta: {
        title: 'route.dataPermission',
        requiresAuth: true
      }
    },
    // 功能数据权限变量可选 /baseSetting/dataPermissionOption
    {
      path: 'dataPermissionOption',
      component: () => import('modb@/basicSetting/views/dataPermissionOption'),
      name: 'dataPermissionOption',
      meta: {
        title: 'route.dataPermissionOption',
        requiresAuth: true
      }
    },
    // 系统参数配置 /baseSetting/systemConfigure
    {
      path: 'systemConfigure',
      component: () => import('modb@/basicSetting/views/systemConfigure'),
      name: 'systemConfigure',
      meta: {
        title: 'systemConfigure',
        requiresAuth: true
      }
    },
    // 状态流转配置 /baseSetting/statusConfig
    {
      path: 'statusConfig',
      component: () => import('../views/statusSetting/index'),
      name: 'statusConfig',
      meta: {
        title: 'route.statusConfig',
        requiresAuth: true
      }
    },
    // 待办列表配置 /baseSetting/todolistConfig
    {
      path: 'todolistConfig',
      component: () => import('modb@/basicSetting/views/todolistConfig'),
      name: 'todolistConfig',
      meta: {
        title: 'route.todolistConfig',
        requiresAuth: true
      }
    },
    // 业务规则配置 /baseSetting/businessRuleConfig
    {
      path: 'businessRuleConfig',
      component: () => import('modb@/basicSetting/views/businessRuleConfig'),
      name: 'businessRuleConfig',
      meta: {
        title: 'route.businessRuleConfig',
        requiresAuth: true
      }
    }
  ]
}
