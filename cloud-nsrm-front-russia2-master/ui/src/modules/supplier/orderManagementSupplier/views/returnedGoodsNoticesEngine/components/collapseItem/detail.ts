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
    title: i18nExpression('orderMod.returnDetail') // 退货明细信息
  },
  properties: {
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
        primaryKey: 'returnDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true,
        scrollY: { gt: 20 }
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      // 'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        // 来源表:detailList
        returnDetailId: {
          type: 'string',
          'x-hidden': true
        },
        //  来源表:detailList.deliveryNoteDetailId.deliveryNoteId
        deliveryNumber: {
          type: 'string',
          'x-hidden': true
        },
        //  来源表:detailList.deliveryNoteDetailId
        lineNum: {
          type: 'string',
          'x-hidden': true
        },
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
            $self.value = row?.deliveryNumber + '|' + row?.lineNum
          }`)
        },
        //  来源表:detailList.deliveryNoteDetailId.orderDetailId.orderId
        orderNumber: {
          type: 'string',
          'x-hidden': true
        },
        //  来源表:detailList.deliveryNoteDetailId.orderDetailId,实际字段是lineNum
        orderLineNum: {
          type: 'string',
          'x-hidden': true
        },
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
        //  来源表:detailList.deliveryNoteDetailId.orderDetailId
        categoryName: {
          type: 'string',
          title: i18nExpression('vendorMod.category'), // 采购品类
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  来源表:detailList.deliveryNoteDetailId.orderDetailId
        materialCode: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  来源表:detailList.deliveryNoteDetailId.orderDetailId
        materialName: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  来源表:detailList.deliveryNoteDetailId
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryQuantity'), // 送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  来源表:detailList.deliveryNoteDetailId
        warehouseQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'), // 入库数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  计算出来的
        notReturnedNum: {
          type: 'string',
          title: i18nExpression('orderMod.notReturnedNum'), // 可退货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        //  来源表:detailList
        returnNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.returnNum'), // 退货数量
            minWidth: 150
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField')
          },
          'x-read-pretty': '{{$form.readPretty}}'
        }
      })
    }
  }
}
