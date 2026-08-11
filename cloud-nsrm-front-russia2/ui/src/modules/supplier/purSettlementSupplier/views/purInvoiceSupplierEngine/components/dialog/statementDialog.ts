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
  title: i18nExpression('accountMod.addStatementDetail'),
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: true,
    beforeClose: expression(`(done, type) => {
              if ( type === 'ok') {
                $setStatementDetailsData ($form, $message)  
              }
              done()
        }`)
  },
  properties: {
    formWrapper: {
      type: 'void',
      'x-component': 'FormWrapper',
      'x-component-props': {
        colLength: 2,
        'form-array': expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').statementDetailsDialogQueryForm'),
        '@getFormData': expression(`(obj) => {
              $getStatementDialogQuerydata(obj,$form)
            }`)
      }
    },
    statementDialogTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        invoiceNoticeNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.statementNumber'), // 对账单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        invoiceDetailNum: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.invoiceDetailNum'), // 对账行号
            minWidth: 100
          }
        },
        receiveOrderLineNo: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('accountMod.inboundReturnLineNo'), // 入库/退货单号
            minWidth: 100
          }
        },
        receiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.receiveDate, '{y}-{m}-{d}')
            }`)
          },
          title: i18nExpression('orderMod.transactionDate'), // 事务处理日期
          'x-render-table-column': {
            minWidth: 100
          }
        },
        type: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'WAREHOURING_RETURN_DETAIL'
          },
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.type'), // 事务类型
            minWidth: 100
          }
        },
        orderNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        lineNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderLineNum'), // 订单行号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unit: {
          type: 'string',
          title: i18nExpression('dataConfMod.unit'), // 单位
          'x-render-table-column': {
            minWidth: 100
          }
        },
        receiveNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.reconciliationQuantity'), // 对账数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        notInvoiceQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.invoicesAvailable'), // 可开票数量
            minWidth: 100
          }
        },
        unitPriceExcludingTax: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unitPriceNoTax'), // 未税单价
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
        unitPriceContainingTax: {
          type: 'string',
          title: i18nExpression('purchaseDemand.taxPrice'), // 含税单价
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
        pageNum: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').statementDialogPageNum'),
        pageSize: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').statementDialogPageSize'),
        total: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').statementDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
              $form.query('OnlineInvoiceVendor').get('data').statementDialogPageNum = num
              $getStatementDialogData($form)
            }`),
        '@size-change': expression(`(size) => {
              $form.query('OnlineInvoiceVendor').get('data').statementDialogPageSize = size
              $getStatementDialogData($form)
            }`)
      }
    }

  }
}
