import {
  expression, i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"
import {radioGroupByYOrNSegment} from "lib@/components/render-engine";

export const companyType = {
  companyType: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.companyType')
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
          status: {
            type: 'string',
            'x-hidden': true
          },
          // 境内外关系
          overseasRelation: {
            type: 'string',
            'x-hidden': generateCharExpressionByFunction(({ $form }) => {
              return $form.query('state').get('data').activeStep === 'companyNature'
            }),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': true,
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
              code: 'SUPPLIER_USE'
            }
          }
          // 供应商类型
          // 'supplierType': {
          //   type: 'string',
          //   'x-decorator': 'FormItem',
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     'disabled': expression(`$form.query('state').get('data').$disabled`),
          //     code: 'SUPPLIER_TYPE'
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
          //     dependencies: ['overseasRelation'],
          //     fulfill: {
          //       state: {
          //         visible: expression('$deps[0] == "OUT"')
          //       }
          //     }
          //   },
          //   'x-component-props': {
          //     'disabled': expression(`$disabled`)
          //   }
          // }
        }
      }
    }
  },
}
