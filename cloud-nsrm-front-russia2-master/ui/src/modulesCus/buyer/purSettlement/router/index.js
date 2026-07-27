export default {
  path: '/purSettlement',
  name: 'purSettlement',
  component: () => import('@/layout'),
  redirect: {
    name: 'buyerNotSettlementAmount'
  },
  children: [
    {
      path: 'purInvoice',
      component: () => import('modcb@/purSettlement/views/purInvoice'),
      name: 'purInvoice',
      meta: {
        title: 'route.purInvoiceNotice', // 对账管理
        requiresAuth: true
      }
    },
    {
      path: 'purPaymentApply',
      component: () => import('modcb@/purSettlement/views/purPaymentApplyEngine'),
      name: 'purPaymentApply',
      meta: {
        title: 'route.purPaymentApply', // 付款申请单
        requiresAuth: true
      }
    },
    {
      path: 'advancePayment',
      component: () => import('modcb@/purSettlement/views/advancePaymentEngine'),
      name: 'advancePayment',
      meta: {
        title: 'route.advancePayment', // 预付款申请
        requiresAuth: true
      }
    },
    {
      path: 'agentOnlineInvoice',
      component: () => import('modcb@/purSettlement/views/agentOnlineInvoice'),
      name: 'agentOnlineInvoice',
      meta: {
        title: 'cusEntry.route.agentOnlineInvoice', // 开票管理
        requiresAuth: true
      }
    },
    {
      path: 'comparisonTable',
      component: () => import('modcb@/purSettlement/views/comparisonTable'),
      name: 'comparisonTable',
      meta: {
        title: 'cusEntry.route.comparisonTable', // 业务实体和开票主体对照表
        requiresAuth: true
      }
    }
  ]
}
