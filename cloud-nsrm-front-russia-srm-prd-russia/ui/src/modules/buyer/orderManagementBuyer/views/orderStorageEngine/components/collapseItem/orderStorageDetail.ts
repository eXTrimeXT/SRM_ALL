import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.inboundDetail') // 入库明细信息
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
          title: i18nExpression('common.add'),
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
        // 联表主键的 key
        primaryKey: 'warehouseReceiptDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      // 'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        // 来源表:detailList
        warehouseReceiptDetailId: {
          type: 'string',
          'x-hidden': true
        },
        // 来源表:detailList
        warehouseReceiptRowNum: {
          type: 'string',
          title: i18nExpression('purchaseDemand.lineNum'), // 行号
          'x-render-table-column': {
            minWidth: 60
          }
        },
        // deliveryNumber来源表：detailList.deliveryNoteDetailId.deliveryNoteId
        // lineNum来源表：detailList.deliveryNoteDetailId
        deliveryNumberAndLineNum: {
          type: 'string',
          title: `{{$t('orderMod.buyerOrderSynergy.deliveryNumber') +
          '|' +
          $t('purchaseDemand.lineNum')}}`, // 送货单号|行号
          'x-render-table-column': {
            minWidth: 150
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            $self.value = row?.deliveryNumber + '|' + row?.deliveryNoteLineNum
          }`)
        },
        // orderNumber来源表：detailList.orderDetailId.orderId
        // lineNum来源表：detailList.orderDetailId
        orderNumberAndOrderLineNum: {
          type: 'string',
          title: `{{$t('orderMod.buyerOrderSynergy.orderNumber') +
          '|' +
          $t('purchaseDemand.lineNum')}}`, // 采购订单编号|行号
          'x-render-table-column': {
            minWidth: 150
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            $self.value = row?.orderNumber + '|' + row?.orderLineNum
          }`)
        },
        // 来源表：detailList.orderDetailId
        categoryName: {
          type: 'string',
          title: i18nExpression('vendorMod.category'), // 采购品类
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.orderDetailId
        materialCode: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.orderDetailId
        materialName: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList.deliveryNoteDetailId
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryQuantity'), // 送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList
        notWarehouseQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.abledWarehouseNum'), // 可入库数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        // 来源表：detailList
        warehouseQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'), // 入库数量
            minWidth: 150
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value > $self.query('.notWarehouseQuantity').get('value')){
                return $t('orderStorage.prompt2') // 入库数量不可大于可入库数量
              }
              if(value <= 0){
                return $t('orderStorage.prompt3') // 入库数量必须大于0
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'), // 操作
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
