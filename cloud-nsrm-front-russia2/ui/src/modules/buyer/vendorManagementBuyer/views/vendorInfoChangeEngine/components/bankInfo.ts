import {expression, generateXindexInOrder, i18nExpression, methodExpression} from '@meicloud/render-engine'

import {
  checkboxByYOrNSegment,
  formGridSegment, radioGroupByYOrNSegment, requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export const bankInfo = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      bankInfoBefore: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
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
              'disabled': true,
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
          // 分行编码
          unionCode: {
            type: 'string',
            title: i18nExpression('components.bank.unionCode'),
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
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
              'disabled': true
            }
          }
        })
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: ''
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                 $self.query('bankInfoAfter')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      bankInfoAfter: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankCode || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              showKey: 'bankNum',
              propKey: 'bankNum',
              name: 'ceea_base_erp_branch_bank_info',
              'disabled': expression(`$form.readPretty`),
              'preQueryData': expression(`{'t.attr1': 'Y'}`),
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankName || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.openingBank || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-read-pretty': true,
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          // 分行编码
          unionCode: {
            type: 'string',
            title: i18nExpression('components.bank.unionCode'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.unionCode || null
                let className = redFunction(oldData, $self?.value)
                $self.setComponentProps({ class: className })
            }`),
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankAccountName || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.bankAccount || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.currencyCode || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'currency',
              'disabled': expression(`$form.readPretty`)
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
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaMainAccount || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$form.readPretty`)
            }
          },
          // 启用
          ceeaEnabled: {
            type: 'string',
            title: i18nExpression('components.bank.isActive'),
            'x-render-table-column': {
              minWidth: 100
            },
            'x-reactions': expression(`() => {
                const oldData = $form.query('bankInfoBefore').get('value')[$self.index]?.ceeaEnabled || null
                let className = redFunction(oldData, $self.value)
                $self.setComponentProps({ class: className })
            }`),
            'x-component': 'Checkbox',
            'x-component-props': {
              'true-label': "Y",
              'false-label': "N",
              'disabled': expression(`$form.readPretty`)
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
                  'disabled': expression(`$form.readPretty`),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
}
