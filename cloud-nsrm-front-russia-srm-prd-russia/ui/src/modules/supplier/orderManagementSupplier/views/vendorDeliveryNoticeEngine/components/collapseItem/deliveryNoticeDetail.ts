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
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.deliveryNoticeDetail')
  },
  properties: {
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        preColumns: 'seq',
        editMode: false,
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
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
            disabled: false,
            type: 'text',
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
        noticeSum: {
          type: 'string',
          title: i18nExpression('orderMod.surplusDeliveryQuantity1'), // 本次通知送货数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        deliveryQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.deliveryQuantityHeader'), // 已发货数量
          'x-render-table-column': {
            minWidth: 120,
            titlePrefix: { content: i18nExpression('orderMod.deliveryQuantityHeaderDesc') } // 已发货数量=送货通知单创建送货单已发货数量
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
          title: i18nExpression('contractMod.deliveryDate1'), // 到货日期
          'x-render-table-column': {
            minWidth: 160
          },
          ...yearMonthDaySelectorSegment
        },
        promiseReceiveDate: {
          title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          'x-render-table-column': {
            minWidth: 160
          },
          ...yearMonthDaySelectorSegment
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
        }
      })
    }
  }
}
