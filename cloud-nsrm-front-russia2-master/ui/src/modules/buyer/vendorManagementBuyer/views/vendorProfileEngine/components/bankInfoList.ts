import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";



export const bankInfoList = {
  bankInfoList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.bankInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      bankInfos: {
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
          branchBankId: {
            type:"number",
            'x-hidden': true
          },
          // 银行代码
          bankCode: {
            type: 'string',
            title: i18nExpression('components.bank.bankCode'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'bankNum',
              propKey: 'bankNum',
              name: 'ceea_base_erp_branch_bank_info',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 银行名称
          bankName: {
            type: 'string',
            title: i18nExpression('components.bank.bankName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 开户行名称
          openingBank: {
            type: 'string',
            title: i18nExpression('components.bank.branchBankName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 账户名称
          bankAccountName: {
            type: 'string',
            title: i18nExpression('components.bank.accountName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 银行账号
          bankAccount: {
            type: 'string',
            title: i18nExpression('components.bank.bankAccount'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 币种
          currencyCode: {
            type: 'string',
            title: i18nExpression('vendorMod.currencyCode'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'currency',
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 是否主账户
          ceeaMainAccount: {
            type: 'string',
            title: i18nExpression('components.bank.isMain'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$disabled`)
            }
          },
          // 启用
          ceeaEnabled: {
            type: 'string',
            title: i18nExpression('components.bank.isActive'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$disabled`)
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  'disabled': expression(`$disabled`),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)
                }
              }
            }}
        })
      }
    }
  }
}
