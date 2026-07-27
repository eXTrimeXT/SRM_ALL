import {
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"

export const specialControls = {
  specialControls: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.specialControls'),
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
          isBacklist: {
            type: 'string',
            title: i18nExpression('vendorMod.isBacklist'), // 是否黑名单
            'x-component': 'DictSelect',
            'x-decorator': 'FormItem',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          focusFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifFocus'),
            'x-component': 'DictSelect',
            'x-decorator': 'FormItem',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          positionLimitFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifLimitUnit'),
            'x-component': 'DictSelect',
            'x-decorator': 'FormItem',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          categoryLimitFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifLimitCategory'),
            'x-component': 'DictSelect',
            'x-decorator': 'FormItem',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          timeLimitFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifLimitTime'),
            'x-component': 'DictSelect',
            'x-decorator': 'FormItem',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          limitDate: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.limitDate'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true
            }
          },
          contractVerification: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifVertity'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          allowClearWithoutSealFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.allowClearWithoutSealFlag'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          allowBidWithoutSealFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.allowBidWithoutSealFlag'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          allowQuotationWithoutSealFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.allowQuotationWithoutSealFlag'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          biddingFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.isCompetition'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          keySupervisionFlag: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.keySupervisionFlag'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              disabled: true
            }
          },
          accountGroup: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.accountGroup'),
            'x-component-props': {
              disabled: true
            }
          },
          partner: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.tradePartner'),
            'x-component-props': {
              disabled: true
            }
          },
          gscpStatus: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.gscpStatus'),
            'x-component-props': {
              disabled: true
            }
          }
        }
      }
    }
  }
}