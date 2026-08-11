import { meiqlCtrl } from '@/config/meiqlConfig'
export default {
  path: '/demoCusCom',
  name: 'demoCusCom',
  component: () => import('@/layout'),
  redirect: {
    name: 'demoSetCommon1'
  },
  children: [
    {
      path: 'demoSetCommon1',
      component: () => import('@/modulesCus/common/demoCus/views/demoSet'),
      name: 'demoSetCommon1',
      meta: {
        title: 'cusEntry.supplement20250205.standardExampleNew1',  // '标准示例-新1'
        requiresAuth: true
      }
    }
  ]
}
