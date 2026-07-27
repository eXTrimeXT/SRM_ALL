import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/bidding',
  name: 'bidding',
  component: () => import('@/layout'),
  redirect: {
    name: 'biddingManagementLTS'
  },
  children: [
    // 招标项目管理 /bidding/management
    {
      path: 'management',
      name: 'biddingManagementLTS',
      component: () => import('modb@/bidding/views/biddingManagement'),
      meta: {
        title: 'route.biddingManagement',
        requiresAuth: true
      }
    },
    // 招标技术评分 /bidding/techScore
    {
      path: 'techScore',
      name: 'biddingTechScore',
      component: () => import('modb@/bidding/views/biddingTechScore'),
      meta: {
        title: 'route.biddingTechScore',
        requiresAuth: true
      }
    },
    // 质疑澄清 /bidding/qa
    {
      path: 'qa',
      component: meiqlCtrl.bidQa === 'Y'
        ? () => import('modb@/bidding/views/biddingQaEngine')
        : () => import('modb@/bidding/views/biddingQa'),
      name: 'biddingQaBuyer',
      meta: {
        title: 'route.challengeClarification',
        requiresAuth: true
      }
    }
  ]
}
