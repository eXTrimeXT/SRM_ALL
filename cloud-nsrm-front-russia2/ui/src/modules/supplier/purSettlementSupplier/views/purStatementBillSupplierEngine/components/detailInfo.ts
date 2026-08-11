/* eslint-disable quotes */
import { generateXindexInOrder, expression } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: "{{$t('route.warehousingAndReturnGoods')}}"
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
        field.visible = ['add','edit'].includes($attrs.params.flag)
      }`),
      properties: {
        add: {
          type: 'void',
          title: "{{$t('common.add')}}",
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $openInvoiceDialog($form, $message)
            }`)
          }
        }
      }
    },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'invoiceDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-read-pretty': true,
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        invoiceDetailId: {
          type: 'string',
          'x-hidden': true
        },
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
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.receiveDate, '{y}-{m}-{d}')
            }`)
          },
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
            width: 130
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
            minWidth: 130
          }
        },
        unit: {
          type: 'string',
          title: "{{$t('dataConfMod.unit')}}",
          'x-render-table-column': {
            width: 100
          }
        },
        receiveNum: {
          type: 'string',
          title: "{{$t('purSettlementMod.receiveNum')}}",
          'x-render-table-column': {
            width: 80
          }
        },
        invoiceQuantity: {
          type: 'string',
          title: "{{$t('purSettlementMod.reconciliationQuantity')}}",
          'x-render-table-column': {
            width: 80
          },
          'x-component-props': {
            class: expression(`$table.getRowByIndex($self.index).type === 'RETURN' ? 'invoice_colorBold' : ''`)

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
        },
        operation: {
          type: 'void',
          title: "{{$t('common.operation')}}",
          'x-render-table-column': {
            width: 60,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
            field.visible = ['add','edit'].includes($attrs.params.flag)
          }`),
          properties: {
            delete: {
              type: 'void',
              title: "{{$t('common.delete')}}",
              'x-component-props': {
                type: 'text',
                '@click': expression(`({rowIndex}) => {
                  $detailTableRemove($table,rowIndex,$form)
                }`)
              }
            }
          }
        }
      })
    }
  }
}
