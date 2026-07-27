import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";
import {editTableFormItemValid} from "lib@/components/render-engine";



export const bankInfoList = {
  bankInfoList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.bankInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component-props': {
              type: 'primary',
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@click': expression(`() => {
                 $self.query('bankInfos')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      bankInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'bankInfoId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
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
              'preQueryData': expression(`{'t.attr1': 'Y'}`),
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              '@close-quicksearch': expression(`(val) => {
                let row = $table.getRowByIndex($self.index)
                row.branchBankId = val ? val.branchBankId : ''
                row.bankCode = val ? val.bankNum : ''
                row.bankName = val ? val.bankName : '' // 银行名称
                row.unionCode = val ? val.branchBankNum : '' // 分行编号
                row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
              }
              `)
            },
            ...editTableFormItemValid
          },
          // 银行名称
          bankName: {
            type: 'string',
            title: i18nExpression('components.bank.bankName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          // 分行编码
          unionCode: {
            type: 'string',
            title: i18nExpression('components.bank.unionCode'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          // 开户行名称
          openingBank: {
            type: 'string',
            title: i18nExpression('components.bank.branchBankName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          // 账户名称
          bankAccountName: {
            type: 'string',
            title: i18nExpression('components.bank.accountName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
          },
          // 银行账号
          bankAccount: {
            type: 'string',
            title: i18nExpression('components.bank.bankAccount'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
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
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
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
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            ...editTableFormItemValid
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
              'disabled': expression(`$form.query('state').get('data').$disabled`)
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
                  'disabled': expression(`$form.query('state').get('data').$disabled`),
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
