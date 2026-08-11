import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('paymentType.payment') // 付款信息
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
            '@click': expression(`() => {
              // $self.query('.paymentProvisionList').take().componentProps.componentInstance.addRow('unshift')
              $self.query('.paymentProvisionList').take().componentProps.componentInstance.addRow()
             }`)
          }
        }
      }
    },
    paymentProvisionList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'orderPaymentProvisionId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'paymentProvisionList:*',
      properties: generateXindexInOrder({
        orderPaymentProvisionId: {
          type: 'string',
          'x-hidden': true
        },
        paymentPeriodsNumber: {
          type: 'string',
          title: i18nExpression('contractMod.paymentPeriod'), // 付款期数
          'x-component': 'RenderTableIndex',
          'x-render-table-column': {
            minWidth: 80
          }
        },
        paymentStage: {
          type: 'string',
          title: i18nExpression('contractMod.payStage'), // 付款阶段
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_STAGE'
          },
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentTerm: {
          type: 'string',
          'x-component': 'Select',
          enum: expression('$form.query(\'Order\').get(\'data\').paymentTermOptions'),
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('contractMod.termOfPayment') // 付款条件
          }
        },
        paymentPeriod: {
          type: 'string',
          title: i18nExpression('paymentType.paymentDay1'), // 付款帐期
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_PERIOD'
          },
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentRadio: {
          type: 'number',
          title: i18nExpression('contractMod.payRatio'), // 付款比例
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentWay: {
          type: 'string',
          title: i18nExpression('paymentType.paymentWay'), // 付款方式
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_MODE'
          },
          'x-render-table-column': {
            minWidth: 100
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
                     ({ rowIndex }) => {
                        $table.remove(rowIndex)
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
