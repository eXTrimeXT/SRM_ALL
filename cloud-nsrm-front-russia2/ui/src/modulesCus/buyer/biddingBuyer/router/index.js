
export default {
  path: '/biddingBuyer',
  name: 'biddingBuyer',
  component: () => import('@/layout'),
  redirect: {
    name: 'biddingManagementNew'
  },
  children: [
    {
      path: 'biddingManagementNew',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/biddingManagement'),
      name: 'biddingManagementNew',
      meta: {
        title: 'cusEntry.route.biddingManagement', // 招标管理
        requiresAuth: true
      }
    },
    {
      path: 'annualMargin',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/annualMargin'),
      name: 'annualMargin',
      meta: {
        title: 'cusEntry.route.annualMargin', // 年度保证金
        requiresAuth: true
      }
    },
    {
      path: 'biddingScore',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/biddingScore'),
      name: 'biddingScore',
      meta: {
        title: 'cusEntry.route.biddingScore', // 招标评分
        requiresAuth: true
      }
    },
    {
      path: 'bidPriceLibrary',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/bidPriceLibrary'),
      name: 'bidPriceLibrary',
      meta: {
        title: 'cusEntry.supplement20250121.bidPriceLibrary', // 招标价格库
        requiresAuth: true
      }
    },
    {
      path: 'supplierAnalysisReport',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/supplierAnalysisReport'),
      name: 'supplierAnalysisReport',
      meta: {
        title: 'cusEntry.route.supplierAnalysisReport', // 供应商参与报价率报表
        requiresAuth: true
      }
    },
    {
      path: 'projectManagementReport',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/projectManagementReport'),
      name: 'projectManagementReport',
      meta: {
        title: 'cusEntry.route.projectManagementReport', // 项目进度管理报表
        requiresAuth: true
      }
    },
    {
      path: 'supervisionReport',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/supervisionReport'),
      name: 'supervisionReport',
      meta: {
        title: 'cusEntry.route.supervisionReport', // 上报监察报表
        requiresAuth: true
      }
    },
    {
      path: 'technicalFlow',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/biddingManagement/mobile/technical'),
      name: 'technicalFlow',
      meta: {
        title: 'cusEntry.route.technicalFlow', // 技术开标审批流
        requiresAuth: false
      }
    },
    {
      path: 'businessFlow',
      component: () => import('@/modulesCus/buyer/biddingBuyer/views/biddingManagement/mobile/business'),
      name: 'businessFlow',
      meta: {
        title: 'cusEntry.route.businessFlow', // 商务开标审批流
        requiresAuth: false
      }
    },
  ]
}
