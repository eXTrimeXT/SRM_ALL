
export default {
  path: '/accountAccess',
  name: 'accountAccess',
  component: () => import('@/layout'),
  redirect: {
    name: 'functionMaintenance'
  },
  children: [
    {
      path: 'functionMaintenance',
      component: () => import('modb@/accountAccess/views/functionMaintenance'),
      name: 'functionMaintenance',
      meta: {
        title: 'route.functionMaintenance',
        requiresAuth: true
      }
    },
    {
      path: 'menuMaintenance',
      component: () => import('modb@/accountAccess/views/menuMaintenance'),
      name: 'menuMaintenance',
      meta: {
        title: 'route.menuMaintenance',
        requiresAuth: true
      }
    },
    {
      path: 'roleMaintenance',
      component: () => import('modb@/accountAccess/views/roleMaintenance'),
      name: 'roleMaintenance',
      meta: {
        title: 'route.roleMaintenance',
        requiresAuth: true
      }
    },
    {
      path: 'usersAccess',
      component: () => import('modcb@/accountAccess/views/usersAccess'),
      name: 'usersAccess',
      meta: {
        title: 'route.usersAccess',
        requiresAuth: true
      }
    },
    {
      path: 'roleFunctionSet',
      component: () => import('modb@/accountAccess/views/roleFunctionSet'),
      name: 'roleFunctionSet',
      meta: {
        title: 'route.roleFunctionSet', // 角色权限配置
        requiresAuth: true
      }
    },
    {
      path: 'reApproval',
      component: () => import('modb@/accountAccess/views/reApproval'),
      name: 'reApproval',
      meta: {
        title: 'route.reApproval',
        requiresAuth: true
      }
    }
  ]
}
