import {
  expression,
  i18nExpression,
} from "@meicloud/render-engine";

export const ceeaAfterSalesAbility = {
  // 售后服务
  ceeaAfterSalesAbilityList: {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.afterSalesService')
  },
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
        saleService: {
          type: 'string',
          'x-decorator': 'FormItem',
          'x-component': 'Input',
          title: i18nExpression('vendorMod.afterSalesService'),
          'x-component-props': {
            'disabled': expression(`$disabled`),
            type: 'textarea'
          }
        }
      }
    }
  }
  }
}
