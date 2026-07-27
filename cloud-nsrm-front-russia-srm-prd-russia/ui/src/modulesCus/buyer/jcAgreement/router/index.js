
export default {
  path: '/jcAgreement',
  name: 'jcAgreement',
  component: () => import('@/layout'),
  redirect: {
    name: 'centralizedAgree'
  },
  children: [
    {
      path: 'centralizedAgree',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/centralizedAgree'),
      name: 'centralizedAgree',
      meta: {
        title: '集采协议',
        requiresAuth: true
      }
    },
    {
      path: 'contractAgree',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/contractAgree'),
      name: 'contractAgree',
      meta: {
        title: '合同协议',
        requiresAuth: true
      }
    },
    {
      path: 'chDesignPlan',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/chDesignPlan'),
      name: 'chDesignPlan',
      meta: {
        title: '提报策划方案',
        requiresAuth: true
      }
    },
    {
      path: 'priceAdjustApply',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/priceAdjustApply'),
      name: 'priceAdjustApply',
      meta: {
        title: '调价申请',
        requiresAuth: true
      }
    },
    {
      path: 'jcAccount',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/jcAccount'),
      name: 'jcAccount',
      meta: {
        title: '集采台账',
        requiresAuth: true
      }
    },
    {
      path: 'jcProgressReport',
      component: () => import('@/modulesCus/buyer/jcAgreement/views/jcProgressReport'),
      name: 'jcProgressReport',
      meta: {
        title: '集采进度',
        requiresAuth: true
      }
    }
  ]
}
