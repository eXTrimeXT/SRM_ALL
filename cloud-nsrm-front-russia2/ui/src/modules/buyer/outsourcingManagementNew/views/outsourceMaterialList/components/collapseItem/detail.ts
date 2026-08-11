import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('outsourceMaterialHead.detail') // 组件物料明细
  },
  properties: {
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: false,
        preColumns: 'seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      'x-read-pretty': true,
      properties: generateXindexInOrder({
        materialLineId: {
          type: 'string',
          'x-hidden': true
        },
        rowNum: {
          type: 'string',
          title: i18nExpression('outsourceMaterialHead.rowNum'), // 委外用料单行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        baseMaterialCode: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.baseMaterialCode'), // 组件物料编码
            minWidth: 120
          }
        },
        baseMaterialName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.baseMaterialName'), // 组件物料名称
            minWidth: 120
          }
        },
        baseMaterialUnit: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
            minWidth: 120
          }
        },
        bomQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.bomQuantity'), // BOM数量
            minWidth: 120,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.bomQuantityTip') } // BOM数量：生产一个总成物料所需组件物料数量；值来源于BOM清单维护的数量
          }
        },
        orderQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.orderQuantity'), // 组件订单数量
            minWidth: 130,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.orderQuantityTip') } // 组件订单数量：订单数量*BOM数量
          }
        },
        receivedQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.receivedQuantity'), //  已领数量
            minWidth: 120,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.receivedQuantityTip') } // 已领数量=此委外用料单行累计供方签收数量-累计退料数量（更新未领数量的退料数量）
          }
        },
        returnQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.returnQuantity'), // 退料数量
            minWidth: 120
          }
        },
        unreceivedQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.unreceivedQuantity'), // 未领数量
            minWidth: 120,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.unreceivedQuantityTip') } // 未领数量：组件订单数量-已领数量+退料数量（更新未领数量的退料数量）

          }
        },
        detailComments: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.detailComments'), // 明细备注
            minWidth: 120
          }
        }
      })
    }
  }
}
