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
    title: i18nExpression('supRisk.baseInfo') // 基础信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        returnOrderNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.returnOrderNumber'), // 退货单号
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        returnStatus: {
          type: 'string',
          'x-decorator': 'FormItem',

          title: i18nExpression('orderMod.buyerOrderSynergy.status'), // 状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'RETURN_ORDER_STATUS',
            disabled: expression('$form.readPretty ? undefined : true')
          },
          ...requiredValidatorSegment
        },
        createdFullName: {
          type: 'string',

          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'), // 创建人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        creationDate: {

          'x-decorator': 'FormItem',
          title: i18nExpression('common.creationTime'), // 创建时间,
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        lastUpdatedFullName: {
          type: 'string',

          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'), // 最后更新人
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        lastUpdateDate: {

          'x-decorator': 'FormItem',
          title: i18nExpression('dataConfMod.lastUpdateDate'), // 最后更新时间
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')

          }
        },
        organizationId: {
          type: 'number',
          'x-decorator': 'FormItem',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-component': 'OrganizationSelector',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            'parent-id': -1,
            'node-type': 'OU',
            'select-type': 'input',
            placeholder: i18nExpression('common.pleaseSelect'),
            multiple: false,
            '@select': expression(`(node) => {
                $form.values.organizationId = node ? node.organizationId : null
                $form.values.organizationCode = node ? node.organizationCode : null
                $form.values.organizationName = node ? node.organizationName : null
                
              }`)
          },
          ...requiredValidatorSegment
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
            'name': 'scc_sup_company_info_all',
            '@close-quicksearch': expression(`(val, scope) => {
              $form.values.vendorId = val ? val.companyId : ''
              $form.values.vendorCode = val ? val.companyCode : ''
              $form.values.vendorName = val ? val.companyName : ''
            }`)
          },
          ...requiredValidatorSegment
        },
        vendorCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        badType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.badType'), // 不良类型
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'RETURN_ORDER_BAD_TYPE'
          },
          ...requiredValidatorSegment
        },
        returnDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.returnDate'), // 退货时间
          ...yearMonthDaySelectorSegment,
          ...requiredValidatorSegment
        },
        returnReason: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.returnReason'), // 退货原因
          'x-component-props': {
            type: 'textarea',
            rows: 2,
            maxlength: 50,
            showWordLimit: true
          }
        },
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('common.remark'), // 备注
          'x-component-props': {
            type: 'textarea',
            rows: 2,
            maxlength: 200,
            showWordLimit: true
          }
        }
      }
    }
  }
}
