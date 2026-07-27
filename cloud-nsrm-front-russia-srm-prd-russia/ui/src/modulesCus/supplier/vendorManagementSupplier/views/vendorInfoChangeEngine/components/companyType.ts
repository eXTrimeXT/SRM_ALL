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
            title: i18nExpression('vendorMod.overseasRelation'), // 企业分类
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW',
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          companyType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.vendorType'), // 企业性质
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          extUseType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extUseType'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_USE',
              disabled: true
            }
          }
          // supplierType: {
          //   type: 'string',
          //   title: i18nExpression('supplierRating.supplierType'), // 供应商业务类型
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'SUPPLIER_TYPE',
          //     disabled: true
          //   },
          //   'x-decorator': 'FormItem',
          //   ...requiredValidatorSegment
          // }
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
            title: i18nExpression('vendorMod.overseasRelation'), // 企业分类
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW',
              disabled: true
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyTypeAfter.overseasRelation')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.overseasRelation')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.vendorType'), // 企业性质
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyTypeAfter.companyType')?.take()?.value || null
                const oldData = $form.query('.companyTypeBefore.companyType')?.take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          extUseType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extUseType'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_USE'
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.companyTypeAfter.extUseType')?.take()?.value || null
              const oldData = $form.query('.companyTypeBefore.extUseType')?.take()?.value || null
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          }
          // supplierType: {
          //   type: 'string',
          //   title: i18nExpression('supplierRating.supplierType'), // 供应商业务类型
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'SUPPLIER_TYPE'
          //   },
          //   'x-reactions': expression(`() => {
          //       const newData = $form.query('.companyTypeAfter.supplierType')?.take()?.value || null
          //       const oldData = $form.query('.companyTypeBefore.supplierType')?.take()?.value || null
          //       let className = redFunction(oldData, newData)
          //       $self.setComponentProps({ class: className })
          //   }`),
          //   'x-decorator': 'FormItem',
          //   ...requiredValidatorSegment
          // }
        }
      }
    }
  }
}
