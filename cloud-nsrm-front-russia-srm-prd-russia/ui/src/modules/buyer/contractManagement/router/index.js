export default {
  path: '/contractManagement',
  name: 'contractManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'contractTemplate'
  },
  children: [
    // 合同模板
    {
      path: 'contractTemplate',
      component: () =>
        import('modb@/contractManagement/views/contractModeManager'),
      name: 'contractTemplateManagement',
      meta: {
        title: 'route.contractTemplate',
        requiresAuth: true
      }
    },
    // {
    //   path: 'onlyofficeContractModeManager',
    //   component: () => import('modb@/contractManagement/views/onlyofficeContractModeManager'),
    //   name: 'onlyofficeContractModeManager',
    //   meta: {
    //     title: 'route.contractTemplate',
    //     requiresAuth: true
    //   }
    // },
    // {
    //   path: 'onlyofficeContractMaintainList',
    //   component: () => import('modb@/contractManagement/views/onlyofficeContractManager'),
    //   name: 'onlyofficeContractMaintainList',
    //   meta: {
    //     title: 'route.contractMaintainList',
    //     requiresAuth: true
    //   }
    // },
    {
      path: 'conditionFactor',
      component: () => import('modb@/contractManagement/views/conditionFactor'),
      name: 'conditionFactor',
      meta: {
        title: 'route.conditionFactor',
        requiresAuth: true
      }
    },
    {
      path: 'contractElement',
      component: () => import('modb@/contractManagement/views/contractElement'),
      name: 'contractElement',
      meta: {
        title: 'route.contractElement',
        requiresAuth: true
      }
    },
    {
      path: 'contractTypeElement',
      component: () =>
        import('modb@/contractManagement/views/contractTypeElement'),
      name: 'contractTypeElement',
      meta: {
        title: 'route.contractTypeElement',
        requiresAuth: true
      }
    },
    {
      path: 'contractPaymentType',
      component: () =>
        import('modb@/contractManagement/views/contractPaymentType'),
      name: 'contractPaymentType',
      meta: {
        title: 'route.contractPaymentType',
        requiresAuth: true
      }
    },
    {
      path: 'contractMaintainList',
      component: () => import('modb@/contractManagement/views/contractManager'),
      name: 'contractMaintainList',
      meta: {
        title: 'route.contractMaintainList',
        requiresAuth: true
      }
    },
    {
      path: 'inspectionBill',
      component: () => import('modb@/contractManagement/views/inspectionBill'),
      name: 'inspectionBill',
      meta: {
        title: 'route.inspectionBill',
        requiresAuth: true
      }
    },
    {
      path: 'inspectionApplyBill',
      component: () =>
        import('modb@/contractManagement/views/inspectionApplyBill'),
      name: 'inspectionApplyBill',
      meta: {
        title: 'route.inspectionApplyBill',
        requiresAuth: true
      }
    },
    {
      path: 'supInspectionBillList',
      component: () =>
        import('modb@/contractManagement/views/supInspectionBill'),
      name: 'supInspectionBillList',
      meta: {
        title: 'route.supInspectionBill',
        requiresAuth: true
      }
    },
    {
      path: 'gradingRules',
      component: () => import('modb@/contractManagement/views/gradingRules'),
      name: 'gradingRules',
      meta: {
        title: 'route.gradingRules',
        requiresAuth: true
      }
    }
  ]
}
