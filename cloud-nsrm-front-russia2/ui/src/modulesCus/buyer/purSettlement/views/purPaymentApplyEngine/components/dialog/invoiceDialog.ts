import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.invoice'), // 开票单
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: true,
    beforeClose: expression(`(done, type) => {
            if ( type === 'ok') {
              $setInvoiceDetailData ($form, $message)  
            }
            done()
      }`)
  },
  properties: {
    invoiceDialogTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        orgName: {
          type: 'string',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-render-table-column': {
            minWidth: 100
          }
        },
        organizationName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
          'x-render-table-column': {
            minWidth: 100
          }
        },
        onlineInvoiceNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.billingNumber'), // 开票单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorCode: {
          type: 'string',
          title: i18nExpression('common.vendorCode'), // 供应商编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendor'), // 供应商
          'x-render-table-column': {
            minWidth: 100
          }
        },
        actualInvoiceAmountY: {
          type: 'string',
          title: i18nExpression('purSettlementMod.actualInvoiceAmountY3'), // 发票含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unPaidAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unPaidAmount'), // 未付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        payMethod: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_MODE'
          },
          'x-render-table-column': {
            title: i18nExpression('paymentType.paymentWay'), // 付款方式
            minWidth: 100
          }
        },
        payAccountPeriodCode: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_PERIOD'
          },
          'x-render-table-column': {
            title: i18nExpression('paymentType.paymentDay1'), // 付款账期
            minWidth: 100
          }
        },
        currencyName: {
          type: 'string',
          title: i18nExpression('quota.currency'), // 币种
          'x-render-table-column': {
            minWidth: 100
          }
        },
        taxRate: {
          type: 'string',
          title: i18nExpression('purchaseDemand.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedBy: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('common.creator'), // 创建人
            minWidth: 100
          }
        },
        appliedDate: {
          title: i18nExpression('quota.createdDate'), // 创建日期
          'x-render-table-column': {
            minWidth: 160
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.appliedDate, '{y}-{m}-{d}')
            }`)
          }
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'PaymentApply\').get(\'data\').invoiceDialogPageNum'),
        pageSize: expression('$form.query(\'PaymentApply\').get(\'data\').invoiceDialogPageSize'),
        total: expression('$form.query(\'PaymentApply\').get(\'data\').invoiceDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
            $form.query('PaymentApply').get('data').invoiceDialogPageNum = num
            $getInvoiceDialogData($form)
          }`),
        '@size-change': expression(`(size) => {
            $form.query('PaymentApply').get('data').invoiceDialogPageSize = size
            $getInvoiceDialogData($form)
          }`)
      }
    }

  }
}
