import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  feedbackLayoutIsPopover
} from 'lib@/components/render-engine/schema-segments'

export default {

  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('purSettlementMod.prepayApplyDetails') // 预付款申请明细
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty && !$form.query('OnlineInvoice').get('data').isFirstApproveShow
           }`),
      properties: {
        add: {
          type: 'void',
          title: i18nExpression('common.add'),
          'x-component-props': {
            type: 'primary',
            '@click': expression('() =>  $openPrepayApplyDialog($form, $message)')
          }
        }
      }
    },
    advanceApplyList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        maxHeight: 400,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        editMode: true,
        primaryKey: 'onlineInvoiceAdvanceId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'advanceApplyList:*',
      'x-read-pretty': true,
      properties: generateXindexInOrder({
        onlineInvoiceAdvanceId: {
          type: 'string',
          'x-hidden': true
        },
        advanceApplyNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.advanceApplyNum'), // 预付申请单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedDate: {
          type: 'string',
          title: i18nExpression('purSettlementMod.appliedDate'), // 单据创建日期
          'x-render-table-column': {
            minWidth: 100
          }
        },
        includeTaxAmount: {
          type: 'string',
          title: i18nExpression('accountMod.advancePaymentAmount'), // 预付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unWrittenOffAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unWrittenOffAmount2'), // 可核销金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        curWrittenOffAmount: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.chargeOffAmount'), // 本次核销金额
            minWidth: 100
          },
          'x-component-props': {
            '@change': expression(`
                () => {
                  $setRowAmount($form)
                }
              `)
          },
          'x-read-pretty': expression('$form.readPretty || $form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
                if(value > $self.query('.unWrittenOffAmount').get('value')){
                  return $t('purchaseDemand.amountCheck') // 本次核销金额不可大于未核销金额！
                }
              }`)
          }
        },
        currencyName: {
          type: 'string',
          title: i18nExpression('quota.currency'), // 币种
          'x-render-table-column': {
            minWidth: 100
          }
        },
        taxRate: {
          type: 'string',
          title: i18nExpression('bidMod.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedFullName: {
          type: 'string',
          title: i18nExpression('purSettlementMod.prepaymentCreator'), // 预付款创建人
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
                disabled: expression('$form.query(\'OnlineInvoice\').get(\'data\').isFirstApproveShow'),
                type: 'text',
                '@click': expression(` ({ rowIndex }) => {
                      $table.remove(rowIndex)
                      $setRowAmount($form)
                    }`)
              }
            }
          }
        }
      })
    }
  }
}
