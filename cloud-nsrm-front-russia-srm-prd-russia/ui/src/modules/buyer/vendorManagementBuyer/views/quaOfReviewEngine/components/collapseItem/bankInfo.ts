import {
    expression,
    i18nExpression,
    generateXindexInOrder
} from '@meicloud/render-engine'

import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.bankInfo') // 银行信息
    },
    'x-visible': expression('$form.query(\'ReviewForm\').get(\'data\').changeDim?.bankInfo === \'Y\''),
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`),
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                    $self.query('.bankJournals').take().componentProps.componentInstance.addRow()
                  }`)
            }
          }
        }
      },
      bankJournals: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          // 联表主键的 key
          primaryKey: 'bankJournalId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'bankJournals:*',
        properties: generateXindexInOrder({
          bankJournalId: {
            type: 'string',
            'x-hidden': true

          },
          bankCode: {
            type: 'string',
            title: i18nExpression('components.bank.bankCode'), // 银行代码
            'x-render-table-column': {
              minWidth: 180
            },
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              readPretty: '{{$form.readPretty}}',
              showKey: 'bankNum',
              propKey: 'bankNum',
              'preQueryData': expression(`{'t.attr1': 'Y'}`),
              'name': 'ceea_base_erp_branch_bank_info',
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                $getBankObj(val,row)
              }`)
            }
          },
          bankName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.bank.bankName'), // 银行名称
              minWidth: 150
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          unionCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.bank.unionCode'), // 分行编码
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          openingBank: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.bank.branchBankName'), // 开户行名称
              minWidth: 120
            },
            'x-read-pretty': true,
            ...editTableFormItemValid
          },
          bankAccountName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.bank.accountName'), // 账户名称
              minWidth: 120
            },
            ...editTableFormItemValid
          },
          bankAccount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.bank.bankAccount'), // 银行账号
              minWidth: 120
            },
            ...editTableFormItemValid
          },
          currencyCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('vendorMod.currencyCode'), // 币种
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BID_TENDER_CURRENCY'
            },
            ...editTableFormItemValid
          },
          ceeaMainAccount: {
            type: 'string',
            title: i18nExpression('components.bank.isMain'), // 是否主账户
            'x-render-table-column': {
              minWidth: 120
            },
            default: 'Y',
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },
          ceeaEnabled: {
            type: 'string',
            title: i18nExpression('components.bank.isActive'), // 启用
            'x-render-table-column': {
              minWidth: 120
            },
            default: 'Y',
            'x-component': 'Checkbox',
            'x-component-props': {
              trueLabel: 'Y',
              falseLabel: 'N'
            }
          },

          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 60,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-reactions': expression(`(field) => {
                  field.visible = !$form.readPretty
                }`),
            properties: {
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`
                        () => {
                          $table.remove($self.index)
                        }
                      `)
                }
              }
            }
          }
        })
      }
    }
}
