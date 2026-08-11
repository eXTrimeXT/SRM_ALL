
export default {
  path: '/vendorAccountCheckingSynergy',
  name: 'vendorAccountCheckingSynergy',
  component: () => import('@/layout'),
  redirect: {
    name: 'notSettlementAmount'
  },
  children: [
    {
      path: 'vendorAccountsPayableUnbilled',
      component: () =>
        import(
          'mod@/accountCheckingManagementSupplier/views/vendorAccountsPayableUnbilled'
        ),
      name: 'vendorAccountsPayableUnbilled',
      meta: {
        title: 'route.vendorAccountsPayableUnbilled',
        requiresAuth: true
      }
    },
    {
      path: 'vendorAccountsPayable',
      component: () =>
        import('mod@/accountCheckingManagementSupplier/views/vendorAccountsPayable'),
      name: 'vendorAccountsPayable',
      meta: {
        title: 'route.vendorAccountsPayable',
        requiresAuth: true
      }
    },
    {
      path: 'vendorStatementTracking',
      component: () =>
        import(
          'mod@/accountCheckingManagementSupplier/views/vendorStatementTracking'
        ),
      name: 'vendorStatementTracking',
      meta: {
        title: 'route.vendorStatementTracking',
        requiresAuth: true
      }
    },
    {
      path: 'notSettlementAmount',
      component: () =>
        import('mod@/accountCheckingManagementSupplier/views/notSettlementAmount'),
      name: 'notSettlementAmount',
      meta: {
        title: 'route.notSettlementAmount',
        requiresAuth: true
      }
    },
    {
      path: 'paymentPlan',
      component: () =>
        import('mod@/accountCheckingManagementSupplier/views/paymentPlan'),
      name: 'paymentPlan',
      meta: {
        title: 'route.vendorpaymentPlan',
        requiresAuth: true
      }
    },
    {
      path: 'vendorSelfHelpBilling',
      component: () =>
        import('mod@/accountCheckingManagementSupplier/views/vendorSelfHelpBilling'),
      name: 'vendorSelfHelpBilling',
      meta: {
        title: 'route.vendorSelfHelpBilling',
        requiresAuth: true
      }
    },
    {
      path: 'ticketOrders',
      component: () =>
        import('mod@/accountCheckingManagementSupplier/views/ticketOrders'),
      name: 'ticketOrders',
      meta: {
        title: 'route.ticketOrders',
        requiresAuth: true
      }
    }
  ]
}
