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
    title: i18nExpression('vendorMod.financeInfo') // 财务信息
  },
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
            'disabled': expression('$values.orgJournals.length === 0'),
            '@click': expression(`() => {
                  $addFinanceInfoRow($form)
             }`)
          }
        }
      }
    },
    financeInfoList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'financeInfoReviewFormId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'financeInfoList:*',
      properties: generateXindexInOrder({
        financeInfoReviewFormId: {
          type: 'string',
          'x-hidden': true
        },
        orgId: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.ceeaOrgName2'), // 引入组织
            minWidth: 180
          },
          enum: expression('$form.query(\'orgJournals\').get(\'value\').map(item => ({ label: item.orgName, value: item.orgId, ...item }))'),
          'x-component': 'Select',
          'x-component-props': {
            disabled: expression('$self.query(\'.enableFlag\').get(\'value\') === \'Y\' || $form.query(\'ReviewForm\').get(\'data\').isReadOnly'),
            '@change': expression(`(val, item) => {
               if (!val) return

               const option = $self.dataSource.find(item => item.value === val)
               let row = $table.getRowByIndex($self.index)
               row.orgCode = option.orgCode
               row.orgName = option.label
              }`)
          },
          ...editTableFormItemValid
        },
        enableFlag: {
          type: 'string',
          'x-hidden': true
        },
        factoryCode: {
          type: 'string',
          title: i18nExpression('vendorMod.factoryCode'), // 工厂代码
          'x-render-table-column': {
            minWidth: 150
          },
          'x-component-props': {
            maxlength: 50,
            'show-word-limit': true,
            disabled: expression('$self.query(\'.enableFlag\').get(\'value\') === \'Y\' || $form.query(\'ReviewForm\').get(\'data\').isReadOnly'),
            '@onKeyUp': expression(`()=>{
              $self.value= $self.value.replace(/[^\w\\/]/ig,'')
            }`)
          }
        },

        clearCurrency: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.clearCurrency'), // 结算币种
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'BID_TENDER_CURRENCY',
            disabled: expression('$self.query(\'.enableFlag\').get(\'value\') === \'Y\' || $form.query(\'ReviewForm\').get(\'data\').isReadOnly')
          },
          ...editTableFormItemValid
        },
        paymentMethod: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.paymentMethod'), // 付款方式
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            disabled: expression('$self.query(\'.enableFlag\').get(\'value\') === \'Y\' || $form.query(\'ReviewForm\').get(\'data\').isReadOnly'),
            code: 'PAYMENT_METHOD'
          },
          ...editTableFormItemValid
        },
        paymentTerms: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('vendorMod.paymentTerms'), // 付款账期
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            disabled: expression('$self.query(\'.enableFlag\').get(\'value\') === \'Y\' || $form.query(\'ReviewForm\').get(\'data\').isReadOnly'),
            code: 'PAYMENT_TERMS'
          },
          ...editTableFormItemValid
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
                disabled: expression('$form.query(\'ReviewForm\').get(\'data\').isReadOnly'),
                type: 'text',
                '@click': expression(`
                      () => {
                        const index = $self.index
                        const row = $table.getRowByIndex(index)
                        $financeDel($form,row,index,$message)
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
