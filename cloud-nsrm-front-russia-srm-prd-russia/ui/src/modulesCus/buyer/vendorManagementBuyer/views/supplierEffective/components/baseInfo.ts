import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'

export const baseInfo = {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('cusEntry.vendorMod.baseInfo')
  },
  'x-query-engine-skip': true,
  // 'x-query-engine-relation': '*',
  properties: {
    layoutOne: {
      type: 'void',
      'x-deocrator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      ...formGridSegment,
      properties: {
        reviewFormId: {
          type: 'string',
          'x-hidden': true
        },
        effectFormNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('cusEntry.vendorMod.effectFormNumber'),
          'x-component-props': {
            disabled: true
          }
        },
        approveStatus: {
          type: 'string',
          title: i18nExpression('cusEntry.vendorMod.approveStatus'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'APPROVE_STATUS_TYPE',
            disabled: true
          },
          default: 'DRAFT'
        },
        createdBy: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creator'), // '创建人'
          'x-component-props': {
            disabled: true
          }
        },
        creationDate: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.creationTime'), // '创建时间'
          'x-component-props': {
            disabled: true
          }
        },
        companyName: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.vendorName'), // '供应商名称'
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showKey: 'companyName',
            propKey: 'companyName',
            name: 'scc_sup_company_info_all',
            '@close-quicksearch': expression(`value => {
              $form.values.companyCode = value?.companyCode || ''
              $form.values.companyName = value?.companyName || ''
              $form.values.companyId = value?.companyId || ''
              $form.values.supplierType = value?.supplierType || ''
            }`)
          },
          ...requiredValidatorSegment
        },
        companyCode: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('common.vendorCode'),
          'x-component-props': {
            disabled: true
          }
        },
        supplierType: {
          type: 'string',
          title: i18nExpression('cusEntry.vendorMod.supplierType'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'SUPPLIER_TYPE',
            disabled: true
          }
        },
        reviewFormNumber: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('cusEntry.vendorMod.reviewFormNumber'),
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showKey: 'reviewFormNumber',
            propKey: 'reviewFormNumber',
            name: 'scc_sup_auth_review_form',
            preQueryData: expression(`{'t.vendor_id': $form.values.companyId, 't.approve_status': 'APPROVED'}`),
            '@close-quicksearch': expression(`value => {
              $form.values.companyCode = value?.vendorCode || $form.values.companyCode
              $form.values.companyName = value?.vendorName || $form.values.companyName
              $form.values.companyId = value?.vendorId || $form.values.companyId
              $form.values.quaReviewType = value?.quaReviewType || ''
              $form.values.reviewFormId = value?.reviewFormId || ''
              if ($form.values.reviewFormId) {
                $getDetailByReviewForm($queryEngine, $form)
              }
            }`)
          },
          ...requiredValidatorSegment
        },
        quaReviewType: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: i18nExpression('cusEntry.vendorMod.quaReviewType'),
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'QUA_REVIEW_TYPE',
            disabled: true
          }
        }
      }
    },
    layoutTwo: {
      type: 'void',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      ...formGridSegment,
      properties: {
        importReminder: {
          type: 'string',
          title: i18nExpression('cusEntry.vendorMod.importReminder'),
          'x-decorator': 'FormItem',
          'x-component-props': {
            type: 'textarea',
            autosize: {
              minRows: 2,
              maxRows: 3
            }
          },
          'x-decorator-props': {
            gridSpan: 4
          }
        }
      }
    }
  }
}