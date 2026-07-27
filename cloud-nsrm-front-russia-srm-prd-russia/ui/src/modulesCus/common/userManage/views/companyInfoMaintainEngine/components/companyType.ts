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
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
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
          flowRemark: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-hidden': true
          },
          status: {
            type: 'string',
            default: 'DRAFT',
            'x-hidden': true
          },
          // 境内外关系
          'overseasRelation': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION',
              disabled: true
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
              disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
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
              disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
            }
          }
          // 供应商类型
          // 'supplierType': {
          //   type: 'string',
          //   default: null,
          //   'x-decorator': 'FormItem',
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     disabled: expression('$form.query(\'state\').get(\'data\').$disabled'),
          //     code: 'SUPPLIER_TYPE',
          //     '@change-value': expression(`(val) => {
          //         if (val == '') {
          //           $form.query('.supplierType').take().value = null
          //         }
          //     }`)
          //   },
          //   title: expression(`$t('supplierRating.supplierType')`),
          //   'x-validator': {
          //     required: true,
          //     message: i18nExpression('common.requiredField')
          //   }
          // },
          // DUNS编号
          // 'dunsCode': {
          //   type: 'string',
          //   'x-decorator': 'FormItem',
          //   'x-component': 'Input',
          //   title: expression(`$t('vendorMod.dunsCode')`),
          //   'x-reactions': {
          //     dependencies: ['.overseasRelation'],
          //     fulfill: {
          //       state: {
          //         visible: expression('$deps[0] == "OUT"')
          //       }
          //     }
          //   },
          //   'x-component-props': {
          //     'disabled': expression(`$form.query('state').get('data').$disabled`)
          //   }
          // }
        }
      }
    }
  },
}
