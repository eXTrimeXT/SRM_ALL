import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/purSettlementSupplier',
  name: 'purSettlementSupplier',
  component: () => import('@/layout'),
  redirect: {
    name: 'purStatementBillSupplier'
  },
  children: [
    // 对账单协同
    {
      path: 'purStatementBillSupplier',
      component: meiqlCtrl.purStatementBillSupplier === 'Y'
        ? () => import('mods@/purSettlementSupplier/views/purStatementBillSupplierEngine')
        : () => import('mods@/purSettlementSupplier/views/purStatementBillSupplier'),
      name: 'purStatementBillSupplier',
      meta: {
        title: 'route.statementCoordination',
        requiresAuth: true
      }
    },
    // 开票协同
    {
      path: 'purInvoiceSupplier',
      component:
        meiqlCtrl.purInvoiceSupplier === 'Y'
          ? () => import('mods@/purSettlementSupplier/views/purInvoiceSupplierEngine')
          : () => import('mods@/purSettlementSupplier/views/purInvoiceSupplier'),
      name: 'purInvoiceSupplier',
      meta: {
        title: 'cusEntry.supplement20250211.purInvoiceSupplier',
        requiresAuth: true
      }
    }
  ]
}
