import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/userManage',
  name: 'userManage',
  component: () => import('@/layout'),
  redirect: {
    name: 'companyInfoMaintain'
  },
  children: [
    {
      path: 'profile',
      component: () => import('mod@/common/userManage/views/profile'),
      name: 'profile',
      meta: {
        title: 'route.profile', // 个人资料
        requiresAuth: true
      }
    },
    {
      path: 'accountSecurity',
      name: 'accountSecurity',
      component: () => import('@/views/iamPage/accountSecurity/index'),
      hidden: true,
      meta: {
        title: 'route.accountSecurity',
        requiresAuth: true
      }
    },
    {
      path: 'uReport',
      component: () => import('mod@/common/userManage/views/uReport'),
      name: 'uReport',
      meta: {
        title: 'route.uReport', // UReport报表
        requiresAuth: true
      }
    },
    // {
    //   path: 'announcements',
    //   component: () => import('mod@/common/userManage/views/announcements'),
    //   name: 'announcements',
    //   meta: {
    //     title: 'route.announcements', // 公告管理
    //     requiresAuth: true
    //   }
    // },
    // {
    //   path: 'checkAnnouncements',
    //   component: () => import('mod@/common/userManage/views/announcements/list'),
    //   name: 'checkAnnouncements',
    //   meta: {
    //     title: 'route.checkAnnouncements', // 公告查看
    //     requiresAuth: true
    //   }
    // },
    {
      path: 'interfaceLog',
      component: () => import('mod@/common/userManage/views/interfacelog'),
      name: 'interfacelog',
      meta: {
        title: 'route.interfacelog', // 接口日志管理
        requiresAuth: true
      }
    },
    {
      path: 'systemconfig',
      component: () => import('mod@/common/userManage/views/systemconfig'),
      name: 'systemconfig',
      meta: {
        title: 'route.systemconfig', // 接口系统配置
        requiresAuth: true
      }
    },
    {
      path: 'interfaceconfig',
      component: () => import('mod@/common/userManage/views/interfaceconfig'),
      name: 'interfaceconfig',
      meta: {
        title: 'route.interfaceconfig', // 接口配置
        requiresAuth: true
      }
    },
    {
      path: 'resetPwd',
      component: () => import('mod@/common/userManage/views/profile/pwdNew'),
      name: 'resetPwd',
      meta: {
        title: '密码过期，重置密码', // 接口配置
        requiresAuth: false
      }
    },
    {
      path: 'understandBiddingMag',
      component: () => import('modc@/buyer/userManage/views/understandBiddingMag'),
      name: 'understandBiddingMag',
      meta: {
        title: 'cusEntry.route.understandBiddingMag', // 懂招标管理
        requiresAuth: true
      }
    },
    {
      path: 'checkUnderstandBidding',
      component: () => import('modc@/buyer/userManage/views/understandBiddingMag/list'),
      name: 'checkUnderstandBidding',
      meta: {
        title: 'cusEntry.route.understandBiddingMag', // 懂招标查看
        requiresAuth: true
      }
    }
  ]
}
