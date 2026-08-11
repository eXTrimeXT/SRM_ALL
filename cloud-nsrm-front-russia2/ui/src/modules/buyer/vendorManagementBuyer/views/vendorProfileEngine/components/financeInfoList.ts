import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const financeInfoList = {
  financeInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.financeInfo')
    },
    'x-query-engine-skip': true,
    properties: {
      financeInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 引入组织
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            title: i18nExpression('vendorMod.ceeaOrgName2'),
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 工厂代码
          factoryCode: {
            type: 'number',
            title: i18nExpression('vendorMod.factoryCode'),
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 结算币种
          clearCurrency: {
            type: 'string',
            title: i18nExpression('vendorMod.clearCurrency'),
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BID_TENDER_CURRENCY',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款方式
          paymentMethod: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentMethod'),
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_METHOD',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 付款账期
          paymentTerms: {
            type: 'string',
            title: i18nExpression('vendorMod.paymentTerms'),
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_TERMS',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          }
        })
      }
    }
  }
}
