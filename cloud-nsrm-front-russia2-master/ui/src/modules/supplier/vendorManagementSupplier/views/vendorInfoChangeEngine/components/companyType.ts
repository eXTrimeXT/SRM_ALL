import {expression, i18nExpression, methodExpression} from '@meicloud/render-engine'

import {
  radioGroupByYOrNSegment, requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export const companyType = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'formClassAllChange'
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      companyTypeBefore: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          overseasRelation: {
            type: 'string',
            title: i18nExpression('vendorMod.overseasRelation'), // 变更单号
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION',
              disabled: true
            },
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyType: {
            type: 'string',
            title: i18nExpression('vendorMod.companyType'), // 企业性质
            'x-component': 'DictSelect',
            'x-hidden': expression(`$form.query('.companyTypeBefore.overseasRelation').take()?.value != 'INSIDE'`),
            'x-component-props': {
              code: 'COMPANY_NATURE',
              disabled: true
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: expression(`$form.query('.overseasRelation').take()?.value == 'INSIDE'`),
              message: i18nExpression('common.requiredField')
            }
          },
          supplierType: {
            type: 'string',
            title: i18nExpression('supplierRating.supplierType'), // 供应商业务类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_TYPE',
              disabled: true
            },
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          ceeaBusinessModel: {
            type: 'string',
            title: i18nExpression('vendorMod.bizModel'), // 商业模式
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BIZ_MODEL',
              disabled: true
            },
            'x-decorator': 'FormItem'
          }
        }
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'formClassAllChange'
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      companyTypeAfter: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          overseasRelation: {
            type: 'string',
            title: i18nExpression('vendorMod.overseasRelation'), // 变更单号
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION'
            },
            'x-reactions': methodExpression(`() => {
                const newData = $form.query('.companyTypeAfter.overseasRelation')?.take().value || null
                const oldData = $form.query('.companyTypeBefore.overseasRelation')?.take().value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyType: {
            type: 'string',
            title: i18nExpression('vendorMod.companyType'), // 企业性质
            'x-component': 'DictSelect',
            'x-hidden': expression(`$form.query('.companyTypeAfter.overseasRelation').take()?.value != 'INSIDE'`),
            'x-component-props': {
              code: 'COMPANY_NATURE'
            },
            'x-reactions': methodExpression(`() => {
                const newData = $form.query('.companyTypeAfter.companyType')?.take().value || null
                const oldData = $form.query('.companyTypeBefore.companyType')?.take().value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            'x-validator': {
              required: expression(`$form.query('.overseasRelation').take()?.value == 'INSIDE'`),
              message: i18nExpression('common.requiredField')
            }
          },
          supplierType: {
            type: 'string',
            title: i18nExpression('supplierRating.supplierType'), // 供应商业务类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_TYPE'
            },
            'x-reactions': methodExpression(`() => {
                const newData = $form.query('.companyTypeAfter.supplierType')?.take().value || null
                const oldData = $form.query('.companyTypeBefore.supplierType')?.take().value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          ceeaBusinessModel: {
            type: 'string',
            title: i18nExpression('vendorMod.bizModel'), // 商业模式
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BIZ_MODEL'
            },
            'x-reactions': methodExpression(`() => {
                const newData = $form.query('.companyTypeAfter.ceeaBusinessModel')?.take().value || null
                const oldData = $form.query('.companyTypeBefore.ceeaBusinessModel')?.take().value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          }
        }
      }
    }
  }
}
