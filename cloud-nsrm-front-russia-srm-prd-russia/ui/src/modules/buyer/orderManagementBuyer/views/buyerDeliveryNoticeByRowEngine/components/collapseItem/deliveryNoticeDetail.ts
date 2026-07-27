import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  editTableFormItemValid,
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.deliveryNoticeDetail') // 送货通知单明细
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
          title: i18nExpression('orderMod.createdOrderDetail'),
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
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        primaryKey: 'deliveryNoticeDetailId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      'x-read-pretty': true,
      properties: generateXindexInOrder({
        deliveryNoticeDetailId: {
          type: 'string',
          'x-hidden': true

        },
        lineNum: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryLineNum'), // 送货通知行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        status: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
          },
          'x-render-table-column': {
            title: i18nExpression('orderMod.rowStatus'), // 行状态
            minWidth: 120
          }
        },
        orderNumberAndOrderDetailLineNum: {
          type: 'string',
          'x-component': 'TableButton',
          'x-component-props': {
            type: 'text',
            disabled: false,
            '@click': expression('({ row }) => $readOrder(row)')
          },
          'x-render-table-column': {
            title: `{{$t('orderMod.buyerOrderSynergy.orderNumber') +
          '|' + $t('vendorMod.relegation.lineNumber')}}`, // 采购订单编号|行号
            minWidth: 150,
            customRender: true
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            $self.value = row?.orderNumber + '|' + row?.orderDetailLineNum
          }`)
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        unit: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'unit'
          },
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.unitCode'), // 单位
            minWidth: 120
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('common.category'), // 品类
          'x-render-table-column': {
            minWidth: 120
          }
        },
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        remainingNoticeQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryNotice.remainingNoticeQuantity'), // 剩余可通知数量
          'x-render-table-column': {
            minWidth: 130,
            titlePrefix: { content: i18nExpression('buyerDeliveryNotice.remainingNoticeQuantityTip') } // 剩余可通知数量=订单数量-订单累计通知数量
          }
        },
        noticeSum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.surplusDeliveryQuantity1'), // 本次通知送货数量
            minWidth: 150
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              // if(value > $self.query('.remainingNoticeQuantity').get('value')){
              //   return $t('buyerDeliveryNotice.prompt3') // 本次通知送货数量不可大于剩余可通知送货数量
              // }
              if(value < 0){
                return $t('buyerDeliveryNotice.prompt4') // 本次通知送货数量不能小于0
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        deliveryNoticeQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('buyerDeliveryNotice.deliveryQuantity'), // 已送货数量
          'x-render-table-column': {
            minWidth: 120,
            titlePrefix: { content: i18nExpression('buyerDeliveryNotice.deliveryQuantityTip') } // 已送货数量：送货通知单创建送货单累计已送货数量
          }
        },
        warehouseQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.warehouseQuantity'), // 已入库数量
          'x-render-table-column': {
            minWidth: 120,
            titlePrefix: { content: i18nExpression('orderMod.warehouseQuantityDesc') } // 已入库数量=送货通知单创建送货单已入库数量
          }
        },
        returnedQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.returnedQuantity'), // 已退货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveDate: {
          'x-render-table-column': {
            title: i18nExpression('contractMod.deliveryDate1'), // 到货日期
            minWidth: 160
          },
          ...editTableFormItemValid,
          ...yearMonthDaySelectorSegment,
          'x-read-pretty': '{{$form.readPretty}}'
        },
        confirmNum: {
          type: 'string',
          title: i18nExpression('buyerDeliveryNotice.confirmNoticeNum'), // 供方确认通知数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        promiseReceiveDate: {
          title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          'x-render-table-column': {
            minWidth: 120
          },
          ...yearMonthDaySelectorSegment
        },
        refusedReason: {
          type: 'string',
          'x-hidden': '{{$form.values.status !== \'REFUSE\'}}',
          title: i18nExpression('orderMod.vendorRejectDesc'), // 供方拒绝说明
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveContact: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveContacts'), // 收货联系人
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveTelephone: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveTelephone'), // 收货联系电话
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveAddress: {
          type: 'string',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          'x-render-table-column': {
            minWidth: 120
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
              field.visible = !$form.readPretty
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
