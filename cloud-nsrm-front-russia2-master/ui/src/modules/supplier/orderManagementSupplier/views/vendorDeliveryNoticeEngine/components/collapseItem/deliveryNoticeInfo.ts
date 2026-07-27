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
    title: i18nExpression('vendorMod.companyBaseInfo2') // 基础信息
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      ...formGridSegment,
      properties: {
        deliveryNoticeNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNoticeNum') // 送货通知单号

        },
        status: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DELIVERY_NOTICE_STATUS_NEW'
          }
        },
        createdFullName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('orderMod.buyerOrderSynergy.createdBy') // 创建人
        },

        creationDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.creationDate2'), // 创建时间
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
            'parent-id': expression('$form.values.orgId || -1'),
            placeholder: i18nExpression('common.pleaseSelect')
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
        comments: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.buyerOrderSynergy.comments'), // 备注
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 4 },
            maxlength: 80,
            showWordLimit: true
          }
        },
        refuseReason: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('orderMod.vendorRejectDesc'), // 供方拒绝说明
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 4 },
            showWordLimit: true
          },
          'x-hidden': '{{$form.values.status !== \'REFUSE\'}}'
        }
      }
    }
  }
}
