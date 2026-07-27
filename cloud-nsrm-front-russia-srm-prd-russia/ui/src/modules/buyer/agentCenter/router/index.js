export default {
  path: '/agentCenter',
  name: 'agentCenter',
  component: () => import('@/layout'),
  redirect: {
    name: 'taskCenter'
  },
  children: [
    {
      path: 'taskCenter',
      component: () => import('modb@/agentCenter/views/taskCenter'),
      name: 'taskCenter',
      meta: {
        title: 'route.taskCenter',
        requiresAuth: true
      }
    },
    {
      path: 'approvalFlowCenter',
      component: () => import('modb@/agentCenter/views/approvalFlowCenter'),
      name: 'approvalFlowCenter',
      meta: {
        title: 'route.approvalFlowCenter',
        requiresAuth: true
      }
    },
    {
      path: 'approval',
      component: () => import('modb@/agentCenter/views/approval'),
      name: 'approval',
      meta: {
        title: 'route.approval',
        requiresAuth: true
      }
    },
    {
      path: 'flowTask', // 流程代办任务
      component: () => import('modb@/agentCenter/views/flowTask'),
      name: 'flowTask',
      meta: {
        title: 'route.flowTask',
        requiresAuth: true
      }
    },
    {
      path: 'flowTaskView', // 流程代办任务
      component: () => import('modb@/agentCenter/views/flowTask/flowTaskView'),
      name: 'flowTaskView',
      meta: {
        title: 'route.flowTaskView', // 流程详情
        requiresAuth: true
      }
    },
    {
      path: 'flowModel', // 流程模型
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowModel'),
      name: 'flowModel',
      meta: {
        title: 'route.flowModel',
        requiresAuth: true
      }
    },
    {
      path: 'flowCenter', // 流程中心
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowCenter'),
      name: 'flowCenter',
      meta: {
        title: 'route.flowCenter',
        requiresAuth: true
      }
    },
    {
      path: 'flowTemplate', // 流程模板
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowTemplate'),
      name: 'flowTemplate',
      meta: {
        title: 'route.flowTemplate',
        requiresAuth: true
      }
    },
    {
      path: 'flowExternalScript', // 流程脚本
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowExternalScript'),
      name: 'flowExternalScript',
      meta: {
        title: 'route.flowExternalScript',
        requiresAuth: true
      }
    },
    {
      path: 'flowFlowEntrust', // 流程委托
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowFlowEntrust'),
      name: 'flowFlowEntrust',
      meta: {
        title: 'route.flowFlowEntrust',
        requiresAuth: true
      }
    },
    {
      path: 'flowInstance', // 流程实例
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowInstance'),
      name: 'flowInstance',
      meta: {
        title: 'route.flowInstance',
        requiresAuth: true
      }
    },
    {
      path: 'flowApproverChange', // 流程审批人变更
      component: () => import('modb@/agentCenter/views/ihrFlowConfig/flowApproverChange'),
      name: 'flowApproverChange',
      meta: {
        title: 'route.flowApproverChange',
        requiresAuth: true
      }
    },
    {
      path: 'IAMOrgSetting', // 修改密码
      component: () => import('modb@/agentCenter/views/iamSetting/iamOrgSetting'),
      name: 'IAMOrgSetting',
      meta: {
        title: 'route.IAMOrgSetting', // 修改密码
        requiresAuth: true
      }
    }

  ]
}
