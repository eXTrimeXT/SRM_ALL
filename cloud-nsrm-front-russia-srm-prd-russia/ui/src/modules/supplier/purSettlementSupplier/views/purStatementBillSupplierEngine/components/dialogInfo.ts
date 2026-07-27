/* eslint-disable quotes */
import { expression, generateXindexInOrder } from '@meicloud/render-engine'

export default {
  type: 'void',
  title: "{{$t('purSettlementMod.stockInReturnDetailsSel')}}",
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: false
  },
  properties: {
    dialogForm: {
      type: 'object',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 3,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        receiveOrderNo: {
          type: 'string',
          title: "{{$t('purSettlementMod.inboundReturnNumber')}}",
          'x-decorator': 'FormItem'
        },
        orderNumber: {
          type: 'string',
          title: "{{$t('purSettlementMod.orderNumber')}}",
          'x-decorator': 'FormItem'
        },
        materialName: {
          type: 'string',
          title: "{{$t('purSettlementMod.materialId')}}",
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            name: 'scc_base_material_item',
            'showKey': 'materialName',
            '@close-quicksearch': expression(`(val) => {
              let arr = ['materialId','materialCode','materialName']
              for(let key of arr){
                $form.query('DialogInfo.dialogForm').take().value[key] = val ? val[key] : null
              }
            }`)
          }
        },
        materialId: {
          type: 'string',
          'x-hidden': true
        },
        materialCode: {
          type: 'string',
          'x-hidden': true
        }
      })
    },
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar',
        style: 'margin-bottom:10px;'
      },
      properties: {
        query: {
          type: 'void',
          title: "{{$t('common.search')}}",
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $invoiceDialogQuery($form)
            }`)
          }
        },
        confirm: {
          type: 'void',
          title: "{{$t('common.confirm')}}",
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $invoiceDialogConfirm($form,$message)
            }`)
          }
        }
      }
    },
    table: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        type: {
          type: 'string',
          title: "{{$t('purSettlementMod.type')}}",
          'x-render-table-column': {
            width: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'WAREHOURING_RETURN_DETAIL'
          }
        },
        receiveDate: {
          type: 'string',
          title: "{{$t('orderMod.transactionDate')}}",
          'x-render-table-column': {
            width: 150
          }
        },
        receiveOrderNo: {
          type: 'string',
          title: "{{$t('accountMod.inboundReturnOrderNo')}}",
          'x-render-table-column': {
            width: 150
          }
        },
        receiveOrderLineNo: {
          type: 'string',
          title: "{{$t('accountMod.inboundReturnLineNo')}}",
          'x-render-table-column': {
            width: 120
          }
        },
        orderNumber: {
          type: 'string',
          title: "{{$t('purSettlementMod.orderNumber')}}",
          'x-render-table-column': {
            width: 150
          }
        },
        lineNum: {
          type: 'string',
          title: "{{$t('purSettlementMod.orderLineNumber')}}",
          'x-render-table-column': {
            width: 100
          }
        },
        itemCode: {
          type: 'string',
          title: "{{$t('common.materialCode')}}",
          'x-render-table-column': {
            width: 100
          }
        },
        itemName: {
          type: 'string',
          title: "{{$t('common.materialName')}}",
          'x-render-table-column': {
            width: 150
          }
        },
        unit: {
          type: 'string',
          title: "{{$t('dataConfMod.unit')}}",
          'x-render-table-column': {
            width: 60
          }
        },
        receiveNum: {
          type: 'string',
          title: "{{$t('purSettlementMod.receiveNum')}}",
          'x-render-table-column': {
            width: 120
          }
        },
        // 对账数量 与事务处理数量取值一样
        invoiceQuantity: {
          type: 'string',
          title: "{{$t('purSettlementMod.reconciliationQuantity')}}",
          'x-render-table-column': {
            width: 100
          }
        },
        unitPriceExcludingTax: {
          type: 'string',
          title: "{{$t('purSettlementMod.unitPriceNoTax')}}",
          'x-render-table-column': {
            width: 110
          }
        },
        taxRate: {
          type: 'string',
          title: "{{$t('bidMod.taxRate')}}",
          'x-render-table-column': {
            width: 80
          }
        },
        unitPriceContainingTax: {
          type: 'string',
          title: "{{$t('quota.taxPrice')}}",
          'x-render-table-column': {
            width: 80
          }
        },
        taxAmount: {
          type: 'string',
          title: "{{$t('contractMod.amount2')}}",
          'x-render-table-column': {
            width: 110
          }
        },
        currencyName: {
          type: 'string',
          title: "{{$t('purchaseDemand.currency')}}",
          'x-render-table-column': {
            width: 100
          }
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression(`$form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageNum`),
        pageSize: expression(`$form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageSize`),
        total: expression(`$form.query('InvoiceNoticeVendor').get('data').invoiceDialogTotal`),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
          $form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageNum = num
          $getInvoiceDialogData($form)
        }`),
        '@size-change': expression(`(size) => {
          console.log('size:::',size)
          $form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageSize = size
          $getInvoiceDialogData($form)
        }`)
      }
    }
  }
}
