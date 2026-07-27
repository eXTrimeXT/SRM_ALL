
export default {
  path: '/quotaManagement',
  name: 'quotaManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'quotaConfig'
  },
  children: [
    {
      path: 'quotaConfig',
      component: () => import('modb@/quotaManagement/views/quotaConfig'),
      name: 'quotaConfig',
      meta: {
        title: 'quota.quotaConfig', // 配额配置
        requiresAuth: true
      }
    },
    {
      path: 'quotaModulation',
      component: () => import('modb@/quotaManagement/views/quotaModulation'),
      name: 'quotaModulation',
      meta: {
        title: 'quota.quotaModulation', // 配额调整
        requiresAuth: true
      }
    },
    {
      path: 'quotaList',
      component: () => import('modb@/quotaManagement/views/quotaList'),
      name: 'quotaList',
      meta: {
        title: 'quota.quotaList', // 配额清单
        requiresAuth: true
      }
    },
    {
      path: 'quotaFlow',
      component: () => import('modb@/quotaManagement/views/quotaFlow'),
      name: 'quotaFlow',
      meta: {
        title: 'quota.quotaFlow', // 配额审批
        requiresAuth: true
      }
    },
    {
      path: 'quotaoffset',
      component: () => import('modb@/quotaManagement/views/quotaoffset'),
      name: 'quotaoffset',
      meta: {
        title: 'quota.quotaoffset', // 配额执行偏差报表
        requiresAuth: true
      }
    }
  ]
}
