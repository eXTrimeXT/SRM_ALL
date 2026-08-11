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
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL' && !$form.query('state').get('data').isSimple
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
            default: 'DRAFT',
            'x-hidden': true
          },
          // 境内外关系
          domesticAndForeignRelations: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW',
              disabled: expression(`$form.query('state').get('data').$disabled`)
            },
            title: expression(`$t('cusEntry.vendorMod.domesticAndForeignRelations')`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 企业性质
          'companyType': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE_NEW',
              disabled: expression(`$form.query('state').get('data').$disabled`)
            },
            title: expression(`$t('cusEntry.vendorMod.vendorType')`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          }
        }
      }
    }
  },
}
