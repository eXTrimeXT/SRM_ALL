
export default {
  path: '/demo',
  name: 'demo',
  component: () => import('@/layout'),
  redirect: {
    name: 'dashboardDemo'
  },
  children: [
    {
      path: 'dashboardDemo',
      component: () => import('modb@/demo/views/dashboard'),
      name: 'dashboardDemo',
      meta: {
        title: "base.navbar.home",  //工作台
        requiresAuth: true
      }
    },
    {
      path: 'demoSet',
      component: () => import('modb@/demo/views/demoSet'),
      name: 'demoSet',
      meta: {
        title: "cusEntry.supplement20250211.standardExample", // '标准示例'
        requiresAuth: true
      }
    },
    {
      path: 'demoOrderTab',
      component: () => import('modb@/demo/views/demoOrderTab'),
      name: 'demoOrderTab',
      meta: {
        title: "cusEntry.supplement20250211.orderHeaderDemoTab",  // '订单头demo-tab'
        requiresAuth: true
      }
    },
    {
      path: 'demoOrderPop',
      component: () => import('modb@/demo/views/demoOrderPop'),
      name: 'demoOrderPop',
      meta: {
        title: "cusEntry.supplement20250211.orderHeaderDialogMode",  // '订单头-弹框模式'
        requiresAuth: true
      }
    },
    {
      path: 'demoOrderAppend',
      component: () => import('modb@/demo/views/demoOrderAppend'),
      name: 'demoOrderAppend',
      meta: {
        title: "cusEntry.supplement20250211.orderDataAppend",  // 'order数据追加'
        requiresAuth: true
      }
    },
    {
      path: 'demoOrderMultiple',
      component: () => import('modb@/demo/views/demoOrderMultiple'),
      name: 'demoOrderMultiple',
      meta: {
        title: "cusEntry.supplement20250211.orderHeaderStructure",  // '订单头行结构'
        requiresAuth: true
      }
    },
    {
      path: 'studyDemo',
      component: () => import('modb@/demo/views/studyDemo'),
      name: 'studyDemo',
      meta: {
        title: "cusEntry.supplement20250211.studyDemo", // '学习demo'
        requiresAuth: true
      }
    },
    {
      path: 'studyDemoA',
      component: () => import('modb@/demo/views/studyDemoA'),
      name: 'studyDemoA',
      meta: {
        title: "cusEntry.supplement20250211.studyDemo1",  // '学习demo1'
        requiresAuth: true
      }
    },
    {
      path: 'studyDemoC',
      component: () => import('modb@/demo/views/studyDemoC'),
      name: 'studyDemoC',
      meta: {
        title: "cusEntry.supplement20250211.studyDemoC",  // '学习demoC'
        requiresAuth: true
      }
    },
    {
      path: 'vxeTableDemo',
      name: 'vxeTableDemo',
      component: () => import('modb@/demo/views/vxeTableDemo'),
      mera: {
        title: 'vxe-table demo',
        requiresAuth: true
      }
    },
    {
      path: 'categorySourcing',
      name: 'categorySourcing',
      component: () => import('modb@/demo/views/categorySourcing'),
      mera: {
        title: "cusEntry.supplement20250211.categorySourceTracingDemo",  // '品类寻源-演示'
        requiresAuth: true
      }
    },
    {
      path: 'cateReport',
      name: 'cateReport',
      component: () => import('modb@/demo/views/categorySourcing/cateReport'),
      mera: {
        title: "cusEntry.supplement20250211.sourceMonitoringReport",  // '寻源监控报表'
        requiresAuth: true
      }
    },
    {
      path: 'orderProgressReport',
      name: 'orderProgressReport',
      component: () => import('modb@/demo/views/orderProgressReport'),
      mera: {
        title: "route.orderProgressReport",  // '采购计划执行报表'
        requiresAuth: true
      }
    },
    {
      path: 'flowDemo',
      name: 'flowDemo',
      component: () => import('modb@/demo/views/flowDemo'),
      mera: {
        title: "cusEntry.supplement20250211.processDemo",  // '流程demo'
        requiresAuth: true
      }
    },
    {
      path: 'conditionDemo',
      name: 'conditionDemo',
      component: () => import('modb@/demo/views/flowDemo/confIndex'),
      mera: {
        title:"cusEntry.supplement20250211.conditionConfigDemo", //  '条件配置demo'
        requiresAuth: true
      }
    },
    {
      path: 'meiqlVue',
      name: 'meiqlVue',
      component: () => import('modb@/demo/views/meiqlVue'),
      mera: {
        title: 'meiqlVue-demo',
        requiresAuth: true
      }
    }
  ]
}
