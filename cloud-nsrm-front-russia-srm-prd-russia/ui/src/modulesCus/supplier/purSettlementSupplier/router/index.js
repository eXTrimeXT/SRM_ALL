import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/purSettlementSupplier',
  name: 'purSettlementSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'purStatementBillSupplier'
  },
  children: [
    {
      path: 'purStatementBillSupplier',
      component: () => import('modcs@/purSettlementSupplier/views/purStatementBillSupplier'),
      name: 'purStatementBillSupplier',
      meta: {
        title: 'route.statementCoordination', // 对账单协同
        requiresAuth: true
      }
    }
  ]
}
