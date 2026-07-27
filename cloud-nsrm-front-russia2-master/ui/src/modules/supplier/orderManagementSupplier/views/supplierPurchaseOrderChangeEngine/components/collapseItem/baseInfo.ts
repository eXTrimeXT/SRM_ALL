import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailsForm') // 采购订单单据
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        orderNumber: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
          'x-decorator': 'FormItem'
        },
        orderChangeNumber: {
          type: 'string',
          title: i18nExpression('orderMod.orderChangeCode'), // 采购订单变更单编号
          'x-decorator': 'FormItem'
        },
        orderChangeStatus: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.orderChangeStatus'), // 变更单状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'ORDER_CHANGE_STATUS'
          }
        },
        orderChangeVersion: {
          type: 'string',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.orderVersion') // 订单版本号

        },
        empUsername: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.buyerName') // 采购员

        },
        departmentName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.department') // 采购部门

        },
        demandType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.demandType'), // 需求类型
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DEMAND_TYPE'
          }
        },
        orderType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'ORDER_TYPE'
          }
        },
        purchaseOrderDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
          ...yearMonthDaySelectorSegment
        },
        orgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect')
          }
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.organizationId'), // 库存组织
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'node-type': 'INV',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            'parent-id': expression('$form.values.orgId || -1')
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendor'), // 供应商
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info_all'
          }
        },
        ifSupplierConfirm: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          }
        },
        orderChangeComments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.orderChangeDesc'), // 订单变更说明
          'x-component-props': {
            type: 'textarea',
            maxlength: 2000,
            showWordLimit: true
          }
        },
        rejectReason: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.vendorRejectDesc'), // 供方拒绝说明
          'x-component-props': {
            type: 'textarea',
            maxlength: 2000,
            showWordLimit: true
          }
        }
      }
    }
  }
}
