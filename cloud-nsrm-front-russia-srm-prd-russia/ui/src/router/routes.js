import appRouters from './asyncRouter'
import exception from './exception'

export const allRouter = [
  ...appRouters,
  exception,
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index'),
    hidden: true,
    meta: {
      title: 'route.login',
      cached: false,
      visited: false
    }
  },
  {
    path: '/registered',
    name: 'registered',
    component: () => import('@/views/registered/index'),
    hidden: true,
    meta: {
      title: 'route.registered',
      cached: false,
      visited: false
    }
  },
  {
    path: '/forgetPassword',
    name: 'forgetPassword',
    component: () => import('@/views/iamPage/forgetPassword/index'),
    hidden: true,
    meta: {
      title: 'route.forgotPass',
      cached: false,
      visited: false
    }
  },
  {
    path: '/portal',
    name: 'portal',
    component: () => import('@/views/portal/index'),
    hidden: true,
    meta: {
      title: 'route.portal',
      cached: false,
      visited: false
    }
  },
  {
    path: '/portalBidding/vendorBiddingDetail',
    name: 'vendorBiddingDetail',
    component: () => import('@/views/portalBidding/vendorBiddingDetailNew'),
    hidden: true,
    meta: {
      title: '招标详情',
      cached: false,
      visited: false
    }
  },
  {
    path: '/mobileList',
    name: 'mobileList',
    component: () => import('@/views/mobileOa/mobileList/index'),
    hidden: true
  },
  {
    path: '/find',
    name: 'find',
    component: () => import('@/views/find/index'),
    hidden: true
  },
  {
    path: '/mobile',
    name: 'mobile',
    component: () => import('@/views/mobile/index'),
    hidden: true
  },
  {
    path: '/mflowTaskView', // 流程代办任务
    component: () => import('modcb@/agentCenter/views/flowTask/flowTaskView'),
    name: 'mflowTaskView',
    meta: {
      title: '流程详情',
      requiresAuth: true
    }
  },
  // 2023.10.31新加-单据嵌入第三方系统
  {
    path: '/flowTaskViewBase/:id', // 流程代办任务
    component: () => import('modcb@/agentCenter/views/flowTask/flowTaskView'),
    name: 'flowTaskViewBase',
    meta: {
      title: '流程详情',
      requiresAuth: true
    }
  },
  {
    path: '/flowTaskToken/:id', // 流程代办任务
    component: () => import('modcb@/agentCenter/views/flowTask/flowTaskToken'),
    name: 'flowTaskToken',
    meta: {
      title: '流程详情',
      requiresAuth: true
    }
  },
  {
    path: '/document/:id',
    name: 'document',
    component: () => import('@/views/document/index'),
    hidden: true,
    props: true,
    meta: {
      title: 'route.document', // 文件预览
      requiresAuth: true
    }
  },
  {
    path: '/pdfPrint',
    name: 'pdfPrint',
    props: route => ({ query: route.query }),
    component: () => import('@/views/pdfPrint/index'),
    hidden: true,
    meta: {
      title: 'route.pdfPrint', // 订单打印
      requiresAuth: true
    }
  },
  {
    path: '/userProtocol',
    name: 'userProtocol',
    component: () => import('@/views/login/components/UserProtocol'),
    hidden: true,
    meta: { title: 'route.userProtocol' } // 用户服务协议
  },
  {
    path: '/privacyProtocol',
    name: 'privacyProtocol',
    component: () => import('@/views/login/components/PrivacyProtocol'),
    hidden: true,
    meta: { title: 'route.privacyProtocol' } // 用户隐私协议
  },
  {
    path: '/saasUserProtocol',
    name: 'saasUserProtocol',
    component: () => import('@/views/login/components/SaaSUserProtocol'),
    hidden: true,
    meta: { title: 'route.saasUserProtocol' } // 用户使用协议
  },
  {
    path: '/saasPrivacyProtocol',
    name: 'saasPrivacyProtocol',
    component: () => import('@/views/login/components/SaaSPrivacyProtocol'),
    hidden: true,
    meta: { title: 'route.privacyProtocol' } // 用户隐私协议
  },
  {
    path: '/redirect',
    component: () => import('@/layout'),
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/onlyoffice',
    name: 'onlyoffice',
    component: () => import('@/views/onlyoffice/index'),
    hidden: true,
    meta: {
      title: 'onlyoffice',
      noCache: true
    }
  },
  {
    path: '/',
    name: 'app',
    component: () => import('@/layout'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'dashboard',
        meta: { title: 'route.dashboard', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/noUser',
    name: 'noUser',
    component: () => import('@/views/error-page/noUser'),
    meta: {
      title: 'route.noUser',
      noCache: true
    }

  },
  {
    path: '/page401',
    name: 'page401',
    component: () => import('@/views/error-page/401'),
    meta: {
      title: 'page401',
      noCache: true
    }
  },
  {
    path: '*',
    redirect: '/exception/404'
  }
]

export default allRouter
