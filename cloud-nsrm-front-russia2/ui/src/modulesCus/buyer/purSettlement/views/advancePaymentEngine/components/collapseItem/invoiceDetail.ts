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
  'x-visible': expression('$form.values.billType == \'ORDER\''),
  'x-component-props': {
    title: i18nExpression('accountMod.advancePaymentDetail') // 订单预付款明细
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
    advanceApplyDetailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'advanceApplyDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-read-pretty': true,
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'advanceApplyDetailList:*',
      properties: generateXindexInOrder({
        advanceApplyDetailId: {
          type: 'string',
          'x-hidden': true
        },
        orderNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderDetailLineNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderLineNum'), // 订单行号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unit: {
          type: 'string',
          title: i18nExpression('dataConfMod.unit'), // 单位
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderNum: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unitNoTaxPrice: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unitPriceNoTax'), // 未税单价
          'x-render-table-column': {
            minWidth: 100
          }
        },
        taxRate: {
          type: 'number',
          title: i18nExpression('bidMod.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 100
          }
        },
        amountIncludingTax: {
          type: 'string',
          title: i18nExpression('contractMod.amount2'), // 含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentAmountAppliedN: {
          type: 'number',
          title: i18nExpression('purSettlementMod.paymentAmountAppliedN'), // 未申请付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        paymentAmountApply: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.paymentAmountApply'), // 本次申请付款金额
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
                return $t('advancePayment.prompt6') // 本次申请付款金额必须大于0
              }else if(value > Number($self.query('.paymentAmountAppliedN').get('value'))){
                return $t('advancePayment.prompt7') // 本次申请付款金额不可大于未申请付款金额
              }
              
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
