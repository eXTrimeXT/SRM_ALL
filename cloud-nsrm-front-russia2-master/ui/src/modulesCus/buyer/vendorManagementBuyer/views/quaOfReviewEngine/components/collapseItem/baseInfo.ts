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
    title: i18nExpression('vendorMod.quaOrderInfo') // 资质审查单
  },
  'x-query-engine-skip': true,
  'x-read-pretty': expression('$form.readPretty'),
  properties: {
    layout: {
      type: 'void',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      ...formGridSegment,
      properties: {
        reviewFormId: {
          type: 'string',
          'x-hidden': true
        },
        // 资质审查类型
        quaReviewType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.quaType'),
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'QUA_REVIEW_TYPE',
            '@change': expression('(value) => $getQuaReviewType(value, $form)')
          },
          ...requiredValidatorSegment
        },
        // 供应商名称
        vendorName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.vendorName'),
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_sup_company_info2',
            '@close-quicksearch': expression(`(value) => {
              $getCompanyObj(value, $form)
              $form.values.cateJournals = []
            }`)
          },
          ...requiredValidatorSegment
        },
        vendorId: {
          type: 'string',
          'x-hidden': true
        },
        vendorCode: {
          type: 'string',
          'x-hidden': true
        },
        // 资质审查单号
        reviewFormNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.quaNum'),
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 审批状态
        approveStatus: {
          type: 'string',
          'x-decorator': 'FormItem',
          default: 'DRAFT',
          title: i18nExpression('vendorMod.approveStatus'),
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PJ_APPROVE_STATUS_TYPE',
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 创建人
        createdFullName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'),
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
        },
        // 部门
        ceeaDeptName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('vendorMod.department'),
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          }
        },
        // 创建时间
        creationDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creationTime'),
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: true
          }
        },
        ifSiteForm: {
          type: 'string',
          title: i18nExpression('cusEntry.vendorMod.ifBid'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO',
            '@change': expression(`(value) => {
              if (value === 'Y') {
                $getAllOrg($form, $message)
              } else {
                $form.values.orgJournals = []
                $form.query('ReviewForm').get('data').allOrg = []
                $form.query('ReviewForm').get('data').orgLength = 0
              }
            }`)
          },
          default: 'N',
          ...requiredValidatorSegment
        },
        extUseType: {
          type: 'string',
          title: i18nExpression('cusEntry.vendorMod.extUseType'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'SUPPLIER_USE'
          }
        },
        // 开发原因分析
        reviewExplain: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            gridSpan: 4
          },
          title: i18nExpression('vendorMod.reviewExplain'),
          'x-component-props': {
            type: 'textarea',
            maxlength: '1000',
            'show-word-limit': true,
            autosize: { minRows: 2, maxRows: 4 }
          },
          ...requiredValidatorSegment
        }
      }
    }
  }
}
