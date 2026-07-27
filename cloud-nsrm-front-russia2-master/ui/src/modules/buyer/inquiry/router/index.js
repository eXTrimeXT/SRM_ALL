import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/inquiry',
  name: 'inquiry',
  component: () => import('@/layout'),
  redirect: {
    name: 'inquiryManagement'
  },
  children: [
    // 询价管理 /inquiry/management
    {
      path: 'management',
      name: 'inquiryManagement',
      component: () => meiqlCtrl.inquiryManagement === 'Y'
        ? import('modb@/inquiry/views/inquiryManagementEngine')
        : import('modb@/inquiry/views/inquiryManagement'),
      meta: {
        title: 'route.inquiry',
        defaultActive: 'inquiryManagementLTS',
        requiresAuth: true
      }
    }
    // FIXME 调试使用，加入白名单，后续删除 询价单 /inquiry/management-engine
    // {
    //   path: 'management-engine',
    //   name: 'inquiryManagementEngine',
    //   // 渲染引擎版
    //   component: () => import('modb@/inquiry/views/inquiryManagementEngine'),
    //   meta: {
    //     title: 'route.inquiry',
    //     defaultActive: 'inquiryManagementLTS',
    //     requiresAuth: true
    //   }
    // }
  ]
}
