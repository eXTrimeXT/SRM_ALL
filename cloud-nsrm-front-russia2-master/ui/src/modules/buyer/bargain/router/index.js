import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/bargain',
  name: 'bargain',
  component: () => import('@/layout'),
  redirect: {
    name: 'bargainManagement'
  },
  children: [
    // 询价项目管理 /bargain/management
    {
      path: 'management',
      name: 'bargainManagement',
      component: () => import('modb@/bargain/views/bargainManagement'),
      meta: {
        title: 'route.bargainManagement',
        requiresAuth: true
      }
    },
    // 询价技术评分 /bargain/techScore
    {
      path: 'techScore',
      component: () => import('modb@/bargain/views/bargainTechScore'),
      name: 'bargainTechScore',
      meta: {
        title: 'route.inquiryTechnologyScore',
        requiresAuth: true
      }
    },
    // 质疑澄清 /bargain/qa
    {
      path: 'qa',
      component: meiqlCtrl.bargainQa === 'Y'
        ? () => import('modb@/bargain/views/bargainQaEngine')
        : () => import('modb@/bargain/views/bargainQa'),
      name: 'bargainQa',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    }
  ]
}
