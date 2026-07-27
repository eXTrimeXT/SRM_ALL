import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.buyerOrderSynergy.vendorDeliveryList') // 送货单明细
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
           field.visible = !$form.readPretty
       }`),
      properties: {
        add: {
          type: 'void',
          title: i18nExpression('orderMod.addOrderDetail'),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
               $openDialog($form, $message)
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
        preColumns: expression('$form.readPretty ? \'seq\' : \'checkbox, seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'deliveryNoteDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true,
        maxHeight: 400
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      // 'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        // 来源表：detailList
        deliveryNoteDetailId: {
          type: 'string',
          'x-hidden': true

        },
        // 来源表：detailList.orderDetailId.orderId
        orderNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList
        orderLineNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderLineNum'), // 订单行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.deliveryNoticeDetailId.deliveryNoticeId
        deliveryNoticeNumber: {
          type: 'string',
          'x-hidden': '{{$form.values.orderSource !== \'DELIVERY_NOTICE\'}}',
          title: i18nExpression('orderMod.deliveryNoticeNumber'), // 送货通知单编号
          'x-render-table-column': {
            minWidth: 120
          }

        },
        // 来源表：detailList.deliveryNoticeDetailId
        deliveryNoticeLineNum: {
          type: 'string',
          'x-hidden': '{{$form.values.orderSource !== \'DELIVERY_NOTICE\'}}',
          title: i18nExpression('orderMod.deliveryLineNum'), // 送货通知单行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.orderDetailId
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          },
          'x-query-engine-relation': 'detailList.orderDetailId'
        },
        // 来源表：detailList.orderDetailId
        materialName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList
        ceeaBatchNum: {
          type: 'string',
          title: i18nExpression('orderMod.batchNum'), // 批次号
          'x-render-table-column': {
            minWidth: 120
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        // 来源表：detailList.orderDetailId
        unit: {
          type: 'string',
          title: i18nExpression('bid_mod.unit'), // 单位
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.orderDetailId
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList
        numberRemaining: {
          type: 'number',
          title: i18nExpression('orderMod.remainUndeliveryQuantity'), // 剩余未送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.deliveryNoticeDetailId
        noticeSum: {
          type: 'string',
          'x-hidden': '{{$form.values.orderSource !== \'DELIVERY_NOTICE\'}}',
          title: i18nExpression('orderMod.surplusDeliveryQuantity1'), // 本次通知送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        deliveryQuantityBackup: {
          type: 'number',
          'x-hidden': true
        },
        // 来源表：detailList
        deliveryQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.thisDeliveryQuantity'), // 本次送货数量
            minWidth: 150
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value <= 0){
                return $t('buyerDeliveryOrder.prompt28')
              }

              let { numberRemaining, deliveryQuantityBackup } = $table.getRowByIndex($self.index)
              if(value > (numberRemaining+deliveryQuantityBackup)){
                return $t('purchaseDemand.deliveryQuantityGreater')
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        // 来源表：detailList.deliveryNoticeDetailId
        ceeaPromiseReceiveDate: {
          title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          'x-render-table-column': {
            minWidth: 100
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.ceeaPromiseReceiveDate, '{y}-{m}-{d}')
            }`)
          },
          default: undefined
        },
        // 来源表：detailList.deliveryNoticeDetailId
        comments: {
          type: 'string',
          title: i18nExpression('purchaseDemand.comments'), // 明细备注
          'x-render-table-column': {
            minWidth: 100
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 60,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty && $form.values.deliveryNoteStatus === 'CREATE'
            }`),
          properties: {
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                       ({ rowIndex }) => {
                          $table.remove(rowIndex)
                       }
                   `)
              }
            }
          }
        }
      })
    }
  }
}
