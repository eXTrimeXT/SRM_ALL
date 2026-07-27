export default {
  path: '/queryComparePriceManagement',
  name: 'queryComparePriceManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'expertDatabase'
  },
  children: [
    // 询价流程配置
    {
      path: 'inquiryByProjectFlowSetting',
      component: () => import('modb@/inquiryByProjectBuyer/views/inquiryByProjectFlowSetting'),
      name: 'inquiryByProjectFlowSetting',
      meta: {
        title: 'route.biddingFlowSetting1',
        requiresAuth: true
      }
    },
    {
      path: 'inquiryByProjectListBuyer',
      component: () => import('modb@/inquiryByProjectBuyer/views/inquiryByProjectListBuyer'),
      name: 'inquiryByProjectListBuyer',
      meta: {
        title: 'route.biddingProject',
        defaultActive: 'biddingProject_new',
        requiresAuth: true
      }
    },
    {
      path: 'inquiryByProjectChClBuyer',
      component: () => import('modb@/inquiryByProjectBuyer/views/inquiryByProjectChClBuyer'),
      name: 'inquiryByProjectChClBuyer',
      meta: {
        title: 'route.challengeClarification',
        defaultActive: 'challengeClarification_new',
        requiresAuth: true
      }
    },
    // 询价技术评分
    {
      path: 'inquiryTechnologyScore',
      component: () => import('modb@/inquiryByProjectBuyer/views/technologyScore'),
      name: 'inquiryTechnologyScore',
      meta: {
        title: 'route.inquiryTechnologyScore',
        requiresAuth: true
      }
    }
  ]
}
