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
    title: i18nExpression('orderMod.orderChangeReport') // 采购订单变更记录
  },
  properties: {
    orderchangeRecords: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: false,
        maxHeight: 400,
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        orderChangeNumber: {
          type: 'string',
          title: i18nExpression('orderMod.orderChangeNumber'), // 订单变更编号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        orgName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
          'x-render-table-column': {
            minWidth: 100
          }
        },
        organizationName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderChangeVersion: {
          type: 'string',
          title: i18nExpression('orderMod.orderVersion'), // 订单版本号
          'x-render-table-column': {
            minWidth: 110
          }
        },
        lineNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.lineNum'), // 订单行号
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
          title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        originOrderNum: {
          type: 'string',
          title: i18nExpression('orderMod.oldOrderNum'), // 原订单数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderChangeAfterNum'), // 变更后数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        originPlanReceiveDate: {
          title: i18nExpression('orderMod.oldPlanReceiveDate'), // 原要求到货日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment
        },
        planReceiveDate: {
          title: i18nExpression('orderMod.changeAfterReceiveDate'), // 变更后要求到货日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment
        },
        preChangeInfor: {
          type: 'string',
          title: i18nExpression('orderMod.preChangeInfor'), // 变更前合同信息
          'x-component': 'RButton',
          'x-content': '{{$t("common.view")}}',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewPreContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 150
          },
          'x-read-pretty': false
        },
        afterChangeInfor: {
          type: 'string',
          title: i18nExpression('orderMod.afterChangeInfor'), // 变更后合同信息
          'x-component': 'RButton',
          'x-content': '{{$t("common.view")}}',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewAfterContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 150
          },
          'x-read-pretty': false
        },
        submittedBy: {
          type: 'string',
          title: i18nExpression('orderMod.changePerson'), // 变更人
          'x-render-table-column': {
            minWidth: 100
          }
        },
        lastUpdateDate: {
          type: 'string',
          title: i18nExpression('orderMod.changeCompletTime'), // 变更完成时间
          'x-render-table-column': {
            minWidth: 120
          }
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'OrderVendor\').get(\'data\').orderchangeRecordsPageNum'),
        pageSize: expression('$form.query(\'OrderVendor\').get(\'data\').orderchangeRecordsPageSize'),
        total: expression('$form.query(\'OrderVendor\').get(\'data\').orderchangeRecordsTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
                $form.query('OrderVendor').get('data').orderchangeRecordsPageNum = num
                $getOrderchangeRecordsData($form)
              }`),
        '@size-change': expression(`(size) => {
                $form.query('OrderVendor').get('data').orderchangeRecordsPageSize = size
                $getOrderchangeRecordsData($form)
              }`)
      }
    }
  }
}
