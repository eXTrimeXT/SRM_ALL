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
    title: i18nExpression('supRisk.baseInfo')
  },
  'x-query-engine-skip': true,
  'x-read-pretty': true,
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        //  是否手动创建
        isManual: {
          type: 'string',
          default: 'Y',
          'x-hidden': true
        },
        orderNumber: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
          'x-decorator': 'FormItem'
        },
        orderStatus: {
          type: 'string',
          default: 'DRAFT',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PURCHASE_ORDER'
          }
        },
        storageStatus: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'STORAGE_STATUS'
          }
        },
        orderChangeVersion: {
          type: 'string',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.version') // 版本号
        },
        ceeaEmpUsername: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.buyerName') // 采购员
        },
        ceeaDepartmentName: {
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
            code: 'PURCHASE_TYPE'
          },
          ...requiredValidatorSegment
        },
        ifSample: {
          type: 'string',
          default: 'N',
          'x-decorator': 'FormItem',
          title: i18nExpression('bidMod.ifSampleSmallOrder'), // 是否样品小批量订单
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          }
        },
        ceeaIfSupplierConfirm: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          },
          ...requiredValidatorSegment
        },
        ceeaPurchaseOrderDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.orderDate'), // 订单日期,
          ...yearMonthDaySelectorSegment,
          default: '{{parseTime(new Date(), \'{y}-{m}-{d}\', true)}}'
        },
        ceeaOrgId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: true,
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect')
          },
          ...requiredValidatorSegment
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.organizationId'), // 库存组织
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: true,
            'node-type': 'INV',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            'parent-id': expression('$form.values.ceeaOrgId || -1')
          },
          ...requiredValidatorSegment
        },
        receiveAddressName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
          ...requiredValidatorSegment
        },
        receiveContact: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveContacts') // 收货联系人
        },
        receiveTelephone: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.receiveTelephone') // 收货联系电话
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendor'), // 供应商
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: true,
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info_all'
          },
          ...requiredValidatorSegment
        },
        ceeaSupplierContacts: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('contractMod.linkMan') // 供应商联系人
        },
        ceeaReceiveOrderTelephone: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('contractMod.supplierContactPhone') // 供方联系人电话
        },
        budgetManagementNum: {
          type: 'string',
          'x-decorator': 'FormItem',
          // 生产需求不显示，非生产需求显示
          'x-hidden': `{{
            $values.demandType !== 'NONPRODUCTIVE_DEMAND'
          }}`,
          title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: true,
            showKey: 'budgetManagementNumber',
            propKey: 'budgetManagementNumber',
            autoQuery: true,
            name: 'scc_pb_budget_management_effective'
          },
          ...requiredValidatorSegment
        },
        ceeaTotalNum: {
          type: 'number',
          default: 0,
          'x-hidden': true

        },
        ceeaTaxAmount: {
          type: 'number',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.totalAmountIncludingTax') // 合计金额含税
        },
        ceeaNoTaxAmount: {
          type: 'number',
          default: 0,
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.totalAmountExcludingTax') // 合计金额不含税
        },
        ceeaCostType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.costType') // 成本类型
        },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('contractMod.remark'), // 备注
          'x-component-props': {
            type: 'textarea',
            maxlength: 80,
            autosize: { minRows: 2, maxRows: 4 },
            showWordLimit: true
          }
        }
      }
    }
  }
}
