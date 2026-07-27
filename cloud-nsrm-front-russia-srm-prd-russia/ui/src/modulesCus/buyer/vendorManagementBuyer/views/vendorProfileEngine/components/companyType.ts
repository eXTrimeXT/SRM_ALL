import {
  expression, 
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"
export const companyType = {
  companyTypeAll: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.companyType'),
                value: $form.values.extRejectAttribute1,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute1 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    'x-query-engine-skip': true,
    properties: {
      layout: {
        type: 'void',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          status: {
            type: 'string',
            'x-hidden': true
          },
          // 境内外关系
          'overseasRelation': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': expression(`$disabled`),
              code: 'RELATION_NEW'
            },
            title: expression(`$t('vendorMod.overseasRelation')`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 企业性质
          'companyType': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-reactions': {
              dependencies: ['overseasRelation'],
              fulfill: {
                state: {
                  visible: expression('$deps[0] == "INSIDE"')
                }
              }
            },
            title: expression(`$t('cusEntry.vendorMod.vendorType')`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          extUseType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extUseType'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_USE',
              disabled: expression(`$disabled`)
            }
          }
        }
      }
    }
  },
}
