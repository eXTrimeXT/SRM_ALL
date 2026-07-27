import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.detailSelect'), // 明细选择
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
    formWrapper: {
      type: 'void',
      'x-component': 'RFormWrapper',
      'x-component-props': {
        colLength: 2,
        'form-array': expression('$form.query(\'AdvanceApply\').get(\'data\').invoiceDialogQueryForm'),
        '@getFormData': expression(`(obj) => {
            $getInvoiceQuerydata(obj,$form)
          }`)
      }
    },
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
        orderNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderDetailLineNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderLineNum'), // 订单行号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        amountIncludingTax: {
          type: 'string',
          title: i18nExpression('contractMod.amount2'), // 含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentAmountAppliedN: {
          type: 'number',
          title: i18nExpression('purSettlementMod.paymentAmountAppliedN'), // 未申请付款金额
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
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unitNoTaxPrice: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.unitPriceNoTax'), // 未税单价
            minWidth: 100
          }
        },
        taxRate: {
          type: 'string',
          title: i18nExpression('bidMod.taxRate'), // 税率
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
        pageNum: expression('$form.query(\'AdvanceApply\').get(\'data\').invoiceDialogPageNum'),
        pageSize: expression('$form.query(\'AdvanceApply\').get(\'data\').invoiceDialogPageSize'),
        total: expression('$form.query(\'AdvanceApply\').get(\'data\').invoiceDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
          $form.query('AdvanceApply').get('data').invoiceDialogPageNum = num
          $getInvoiceDialogData($form)
        }`),
        '@size-change': expression(`(size) => {
          $form.query('AdvanceApply').get('data').invoiceDialogPageSize = size
          $getInvoiceDialogData($form)
        }`)
      }
    }

  }
}
