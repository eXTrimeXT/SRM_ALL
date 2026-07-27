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
  title: i18nExpression('purSettlementMod.selPrepayApplyDetails'),
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: true,
    beforeClose: expression(`(done, type) => {
              if ( type === 'ok') {
                $setPrepayApplyDetailsData($form, $message)  
              }
              done()
        }`)
  },
  properties: {
    prepayApplyDialogTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        advanceApplyNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.advanceApplyNum'), // 预付申请单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.appliedDate, '{y}-{m}-{d}')
            }`)
          },
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.appliedDate'), // 单据创建日期
            minWidth: 100
          }
        },
        includeTaxAmount: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('accountMod.advancePaymentAmount'), // 预付款金额
            minWidth: 100
          }
        },
        unWrittenOffAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unWrittenOffAmount2'), // 可核销金额
          'x-render-table-column': {
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
          title: i18nExpression('bidMod.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedFullName: {
          type: 'string',
          title: i18nExpression('purSettlementMod.prepaymentCreator'), // 预付款创建人
          'x-render-table-column': {
            minWidth: 100
          }
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'OnlineInvoice\').get(\'data\').prepayApplyDialogPageNum'),
        pageSize: expression('$form.query(\'OnlineInvoice\').get(\'data\').prepayApplyDialogPageSize'),
        total: expression('$form.query(\'OnlineInvoice\').get(\'data\').prepayApplyDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
              $form.query('OnlineInvoice').get('data').prepayApplyDialogPageNum = num
              $getPrepayApplyDialogData($form)
            }`),
        '@size-change': expression(`(size) => {
              $form.query('OnlineInvoice').get('data').prepayApplyDialogPageSize = size
              $getPrepayApplyDialogData($form)
            }`)
      }
    }

  }
}
