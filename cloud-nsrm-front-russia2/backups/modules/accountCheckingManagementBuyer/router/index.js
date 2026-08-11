export default {
  path: '/buyerAccountCheckingSynergy',
  name: 'buyerAccountCheckingSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'buyerNotSettlementAmount'
  },
  children: [
    {
      path: 'buyerAccountsPayableUnbilled',
      component: () =>
        import(
          'mod@/accountCheckingManagementBuyer/views/buyerAccountsPayableUnbilled'
        ),
      name: 'buyerAccountsPayableUnbilled',
      meta: {
        title: 'route.buyerAccountsPayableUnbilled',
        requiresAuth: true
      }
    },
    {
      path: 'buyerAccountsPayable',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/buyerAccountsPayable'),
      name: 'buyerAccountsPayable',
      meta: {
        title: 'route.buyerAccountsPayable',
        requiresAuth: true
      }
    },
    {
      path: 'buyerNotSettlementAmount',
      component: () =>
        import(
          'mod@/accountCheckingManagementBuyer/views/buyerNotSettlementAmount'
        ),
      name: 'buyerNotSettlementAmount',
      meta: {
        title: 'route.buyerNotSettlementAmount',
        requiresAuth: true
      }
    },
    {
      path: 'buyerPaymentPlan',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/buyerPaymentPlan'),
      name: 'buyerPaymentPlan',
      meta: {
        title: 'route.buyerPaymentPlan',
        requiresAuth: true
      }
    },
    {
      path: 'buyerSelfHelpBilling',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/buyerSelfHelpBilling'),
      name: 'buyerSelfHelpBilling',
      meta: {
        title: 'route.buyerSelfHelpBilling',
        requiresAuth: true
      }
    },
    {
      path: 'buyerTicketOrders',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/buyerTicketOrders'),
      name: 'buyerTicketOrders',
      meta: {
        title: 'route.buyerTicketOrders',
        requiresAuth: true
      }
    },
    {
      path: 'penaltyDeductionOrder',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/penaltyDeductionOrder'),
      name: 'penaltyDeductionOrder',
      meta: {
        title: 'route.penaltyDeductionOrder',
        requiresAuth: true
      }
    },
    {
      path: 'statementBill',
      component: () =>
        import('mod@/accountCheckingManagementBuyer/views/statementBill'),
      name: 'statementBill',
      meta: {
        title: 'route.statementBill',
        requiresAuth: true
      }
    }
  ]
}
