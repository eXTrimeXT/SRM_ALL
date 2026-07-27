import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/supplierRisk',
  name: 'supplierRisk',
  component: () => import('@/layout'),
  redirect: {
    name: 'risk'
  },
  children: [
    {
      path: 'risk',
      component: meiqlCtrl.risk === 'Y'
      ? () => import('modb@/supplierPortraitAndRisk/views/riskEngine')
      : () => import('modb@/supplierPortraitAndRisk/views/risk'),
      name: 'risk',
      meta: {
        title: 'route.risk',
        requiresAuth: true
      }
    },
    {
      path: 'portrait',
      component: () => import('modb@/supplierPortraitAndRisk/views/portrait'),
      name: 'portrait',
      meta: {
        title: 'route.portrait',
        requiresAuth: true
      }
    }
  ]
}
