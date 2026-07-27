import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-visible': expression('$form.values.billType == \'ORDER\''),
  'x-component-props': {
    title: i18nExpression('purSettlementMod.invoice') // 开票单
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
                $openInvoiceDialog($form, $message)
             }`)
          }
        }
      }
    },
    invoices: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'paymentApplyDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-read-pretty': true,
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'invoices:*',
      properties: generateXindexInOrder({
        paymentApplyDetailId: {
          type: 'string',
          'x-hidden': true
        },
        orgName: {
          type: 'string',
          title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
          'x-render-table-column': {
            minWidth: 100
          }
        },
        organizationName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
          'x-render-table-column': {
            minWidth: 100
          }
        },
        onlineInvoiceNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.billingNumber'), // 开票单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorCode: {
          type: 'string',
          title: i18nExpression('common.vendorCode'), // 供应商编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendor'), // 供应商
          'x-render-table-column': {
            minWidth: 100
          }
        },
        actualInvoiceAmountY: {
          type: 'string',
          title: i18nExpression('purSettlementMod.actualInvoiceAmountY3'), // 发票含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unPaidAmount: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unPaidAmount'), // 未付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        payingAmount: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.payingAmount'), // *本次付款金额
            minWidth: 120
          },
          'x-component-props': {
            '@change': expression(`
              () => {
                $setAmountCal($form)
              }
            `)
          },
          'x-read-pretty': '{{$form.readPretty}}',
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value <= 0){
                return $t('purPaymentApply.prompt11') // 本次付款金额必须大于0
              }else if(value > Number($self.query('.unPaidAmount').get('value'))){
                return $t('purPaymentApply.prompt12') // 本次付款金额不可大于未付款金额
              }
              
            }`)
          }
        },
        payMethod: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_MODE'
          },
          'x-render-table-column': {
            title: i18nExpression('paymentType.paymentWay'), // 付款方式
            minWidth: 100
          }
        },
        payAccountPeriodCode: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_PERIOD'
          },
          'x-render-table-column': {
            title: i18nExpression('paymentType.paymentDay1'), // 付款账期
            minWidth: 100
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
          title: i18nExpression('purchaseDemand.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 100
          }
        },
        appliedBy: {
          'x-render-table-column': {
            title: i18nExpression('common.creator'), // 创建人
            minWidth: 160
          }
        },
        appliedDate: {
          title: i18nExpression('quota.createdDate'), // 创建日期
          'x-render-table-column': {
            minWidth: 100
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.appliedDate, '{y}-{m}-{d}')
            }`)
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
                        $setAmountCal($form)
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
