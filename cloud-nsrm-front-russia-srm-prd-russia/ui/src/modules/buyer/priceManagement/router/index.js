import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/priceManagement',
  name: 'priceManagement',
  component: () => import('@/layout'),
  redirect: {
    name: 'priceApproval'
  },
  children: [
    // 价格审批单 /priceManagement/priceApproval
    {
      path: 'priceApproval',
      component: meiqlCtrl.priceApproval === 'Y' ?
        () => import('../views/priceApprovalEngine') :
        () => import('../views/priceApproval'),
      name: 'priceApproval',
      meta: {
        title: 'route.priceApproval',
        requiresAuth: true
      }
    },
    // 价格目录 /priceManagement/priceCatalog
    {
      path: 'priceCatalog',
      component: () => import('../views/priceCatalog'),
      name: 'priceCatalog',
      meta: {
        title: 'route.priceCatalog',
        requiresAuth: true
      }
    },
    // 价格有效期查询报表 /priceManagement/priceValidityReport
    {
      path: 'priceValidityReport',
      component: () => import('../views/priceValidityReport'),
      name: 'priceValidityReport',
      meta: {
        title: 'route.priceValidityReport',
        requiresAuth: true
      }
    }
  ]
}
