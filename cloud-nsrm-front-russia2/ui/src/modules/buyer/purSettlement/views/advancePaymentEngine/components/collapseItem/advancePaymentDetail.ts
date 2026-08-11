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
  'x-visible': expression('$form.values.billType == \'CONTRACT\''),
  'x-component-props': {
    title: i18nExpression('accountMod.advancePaymentDetail2') // 合同履约预付款明细
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
                  $openAdvancePaymentDialog($form, $message)
               }`)
          }
        }
      }
    },
    perAdvanceApplyDetails: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'perAdvanceApplyDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-read-pretty': true,
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'perAdvanceApplyDetails:*',
      properties: generateXindexInOrder({
        perAdvanceApplyDetailId: {
          type: 'string',
          'x-hidden': true
        },
        contractName: {
          type: 'string',
          title: i18nExpression('vendorMod.contractName'), // 合同名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        milestoneType: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'MILESTONE_SCHEDULE'
          },
          'x-render-table-column': {
            title: i18nExpression('contract_mod.processNodeName'), // 里程碑名称
            minWidth: 100
          }
        },
        paymentStage: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PAYMENT_STAGE'
          },
          'x-render-table-column': {
            title: i18nExpression('bidMod.payStage'), // 付款阶段
            minWidth: 100
          }
        },
        invoiceNo: {
          type: 'string',
          title: i18nExpression('contract_mod.processNum2'), // 合同履约开票单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        vendorName: {
          type: 'string',
          title: i18nExpression('common.vendorName'), // 供应商名称
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
        invName: {
          type: 'string',
          title: i18nExpression('vendorMod.organization'), // 组织
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('vendorMod.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('vendorMod.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        payableTaxedAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.payableTax'), // 应付含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        invoicedTaxedAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.payableTax2'), // 开票含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        stayPaymentAmount: {
          type: 'string',
          title: i18nExpression('contract_mod.paymentAmount'), // 待付款金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        currentPaymentAmount: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.payingAmount'), // 本次付款金额
            minWidth: 120
          },
          'x-read-pretty': '{{$form.readPretty}}',
          'x-component-props': {
            '@change': expression(`
              () => {
                $setAmountCal($form)
              }
            `)
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value <= 0){
                return $t('advancePayment.prompt4') // 本次付款金额必须大于0
              }else if(value > Number($self.query('.stayPaymentAmount').get('value'))){
                return $t('advancePayment.prompt5') // 本次付款金额不可大于待付款金额
              }
            }`)
          }
        },
        alreadyPaymentAmount: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('contract_mod.amountPaid'), // 已付款金额
            minWidth: 100
          }
        },
        createdFullName: {
          type: 'string',
          title: i18nExpression('sourcingBuyer.createdFullName'), // 创建人
          'x-render-table-column': {
            minWidth: 100
          }
        },
        creationDate: {
          title: i18nExpression('sourcingBuyer.creationDate'), // 创建时间
          'x-render-table-column': {
            minWidth: 100
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.creationDate, '{y}-{m}-{d}')
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
