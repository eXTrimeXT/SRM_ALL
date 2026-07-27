import { meiqlCtrl } from '@/config/meiqlConfig'

export default {
  path: '/purSettlement',
  name: 'purSettlement',
  component: () => import('@/layout'),
  redirect: {
    name: 'buyerNotSettlementAmount'
  },
  children: [
    // {
    //   path: 'purStatementBill',
    //   component: () => import('modb@/purSettlement/views/purStatementBill'),
    //   name: 'purStatementBill',
    //   meta: {
    //     title: 'route.purStatementBill', // 对账管理列表
    //     requiresAuth: true
    //   }
    // },
    {
      path: 'purInvoice',
      component: meiqlCtrl.purInvoice === 'Y'
        ? () => import('modb@/purSettlement/views/purInvoiceEngine')
        : () => import('modb@/purSettlement/views/purInvoice'),
      name: 'purInvoice',
      meta: {
        title: 'route.purInvoiceNotice',
        requiresAuth: true
      }
    },
    {
      path: 'purPaymentPlan',
      component: () => import('modb@/purSettlement/views/purPaymentPlan'),
      name: 'purPaymentPlan',
      meta: {
        title: 'route.purPaymentPlan',
        requiresAuth: true
      }
    },
    {
      path: 'purPaymentApply',
      component:
        meiqlCtrl.purPaymentApply === 'Y'
          ? () => import('modb@/purSettlement/views/purPaymentApplyEngine')
          : () => import('modb@/purSettlement/views/purPaymentApply'),
      name: 'purPaymentApply',
      meta: {
        title: 'route.purPaymentApply', // 付款申请单
        requiresAuth: true
      }
    },
    {
      path: 'advancePayment',
      component: meiqlCtrl.advancePayment === 'Y'
        ? () => import('modb@/purSettlement/views/advancePaymentEngine')
        : () => import('modb@/purSettlement/views/advancePayment'),
      name: 'advancePayment',
      meta: {
        title: 'route.advancePayment', // 预付款申请
        requiresAuth: true
      }
    },
    // {
    //   path: 'onlineInvoice',
    //   component: () => import('modb@/purSettlement/views/onlineInvoice'),
    //   name: 'onlineInvoice',
    //   meta: {
    //     title: 'route.onlineInvoice', // 发票审核
    //     requiresAuth: true
    //   }
    // },
    // {
    //   path: 'supOnlineInvoice',
    //   component: () => import('modb@/purSettlement/views/supOnlineInvoice'),
    //   name: 'supOnlineInvoice',
    //   meta: {
    //     title: 'route.supOnlineInvoice', // 供方网上开票
    //     requiresAuth: true
    //   }
    // },
    {
      path: 'agentOnlineInvoice',
      component:
        meiqlCtrl.agentOnlineInvoice === 'Y'
          ? () => import('modb@/purSettlement/views/agentOnlineInvoiceEngine')
          : () => import('modb@/purSettlement/views/agentOnlineInvoice'),
      name: 'agentOnlineInvoice',
      meta: {
        title: 'route.agentOnlineInvoice', // 开票管理
        requiresAuth: true
      }
    }
  ]
}
