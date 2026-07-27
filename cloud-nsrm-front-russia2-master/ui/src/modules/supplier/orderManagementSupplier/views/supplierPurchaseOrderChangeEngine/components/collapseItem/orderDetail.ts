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
    title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailsList') // 订单明细
  },
  properties: {
    orderChangeDetails: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        preColumns: 'seq'
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'orderChangeDetails:*',
      properties: generateXindexInOrder({
        categoryName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemName'), // 物料名称
          'x-render-table-column': {
            minWidth: 150
          }
        },
        orderDetailStatus: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OrderDetailStatus'
          },
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
            minWidth: 100
          }
        },
        unit: {
          type: 'string',
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('purchaseDemand.unitCode') // 单位
          }
        },
        requirementQuantity: {
          type: 'string',
          title: i18nExpression('purchaseDemand.requirementQuantity'), // 需求数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        deliveryNoticeQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量 TODO
          'x-render-table-column': {
            minWidth: 100
          }
        },
        maxOrderQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.maxOrderQuantity'), // 采购申请可用数量 TODO
          'x-render-table-column': {
            minWidth: 140
          }
        },
        originOrderNum: {
          type: 'string',
          title: i18nExpression('orderMod.oldOrderNum'), // 原订单数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.orderChangeAfterNum'), // 变更后数量
            minWidth: 120
          }
        },
        requirementDate: {
          title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.requirementDate, '{y}-{m}-{d}')
            }`)
          }
        },
        originPlanReceiveDate: {
          title: i18nExpression('orderMod.oldPlanReceiveDate'), // 原要求到货日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.originPlanReceiveDate, '{y}-{m}-{d}')
            }`)
          }
        },
        planReceiveDate: {
          title: i18nExpression('orderMod.changeAfterReceiveDate'), // 变更后要求到货日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.planReceiveDate, '{y}-{m}-{d}')
            }`)
          }
        },
        promiseReceiveDate: {
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.promiseReceiveDate, '{y}-{m}-{d}')
            }`)
          }
        },
        originUsedContractQuantity: {
          type: 'string',
          default: 0,
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewPreContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 140,
            title: i18nExpression('orderMod.originUsedContractQuantity') // 变更前合同关联数量
          },
          'x-read-pretty': false
        },
        usedContractQuantity: {
          type: 'string',
          default: 0,
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewAfterContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 140,
            title: i18nExpression('orderMod.orderChangeUsedContractQuantity') // 变更后合同关联数量
          },
          'x-read-pretty': false
        },
        comments: {
          type: 'string',
          title: i18nExpression('purchaseDemand.comments'), // 明细备注
          'x-component-props': {
            maxlength: 80
          },
          'x-render-table-column': {
            minWidth: 150
          }
        }
      })
    }
  }
}
