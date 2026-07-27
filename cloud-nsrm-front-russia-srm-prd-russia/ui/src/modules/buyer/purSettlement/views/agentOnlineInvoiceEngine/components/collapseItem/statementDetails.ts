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
    title: i18nExpression('accountMod.statementDetail1') // 对账单明细
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
            '@click': expression(`
                () => {
                  $openStatementDialog($form, $message)
              }`)
          }
        }
      }
    },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        maxHeight: 400,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        editMode: true,
        primaryKey: 'onlineInvoiceDetailId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      'x-read-pretty': true,
      properties: generateXindexInOrder({
        onlineInvoiceDetailId: {
          type: 'string',
          'x-hidden': true
        },
        invoiceNoticeNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.statementNumber'), // 对账单号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceDetailNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoiceDetailNum'), // 对账行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveOrderNo: {
          type: 'string',
          title: i18nExpression('accountMod.inboundReturnOrderNo'), // 入库/退货单号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveOrderLineNo: {
          type: 'string',
          title: i18nExpression('accountMod.inboundReturnLineNo'), // 入库/退货行号
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveDate: {
          type: 'string',
          title: i18nExpression('orderMod.transactionDate'), // 事务处理日期
          'x-render-table-column': {
            minWidth: 120
          }
        },
        type: {
          type: 'string',
          title: i18nExpression('purSettlementMod.type'), // 事务类型
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'WAREHOURING_RETURN_DETAIL'
          }
        },
        orderNumber: {
          type: 'string',
          title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
          'x-render-table-column': {
            minWidth: 120
          }
        },

        lineNum: {
          type: 'string',
          title: i18nExpression('orderMod.orderLineNum'), // 订单行号
          'x-render-table-column': {
            minWidth: 120
          }
        },

        itemCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 120
          }
        },

        itemName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        unit: {
          type: 'string',
          title: i18nExpression('dataConfMod.unit'), // 单位
          'x-render-table-column': {
            minWidth: 120
          }
        },
        receiveNum: {
          type: 'string',
          title: i18nExpression('purSettlementMod.reconciliationQuantity'), // 对账数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        notInvoiceQuantity: {
          type: 'string',
          title: i18nExpression('purSettlementMod.invoicesAvailable'), // 可开票数量
          'x-render-table-column': {
            minWidth: 120
          }
        },
        invoiceQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.invoiceQuantity'), // 本次开票数量
            minWidth: 120
          },
          'x-component-props': {
            '@change': expression(`
              () => {
                $setAmountCal($form)
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
                if(value > $self.query('.notInvoiceQuantity').get('value')){
                  return $t('purchaseDemand.lessThanInvoiceNumber') // 本次开票数量不可大于未开票数量
                }
              }`)
          }
        },
        unitPriceExcludingTax: {
          type: 'string',
          title: i18nExpression('purSettlementMod.unitPriceNoTax'), // 未税单价
          'x-render-table-column': {
            minWidth: 120
          }
        },
        taxRate: {
          type: 'string',
          title: i18nExpression('bidMod.taxRate'), // 税率
          'x-render-table-column': {
            minWidth: 120
          }
        },
        taxAmount: {
          type: 'number',
          title: i18nExpression('contractMod.amount2'), // 含税金额
          'x-render-table-column': {
            minWidth: 120
          },
          'x-reactions': expression(`(field) => {
              // 对账单明细行含税金额：含税单价 * 本次开票数量
              let row = $table.getRowByIndex($self.index)
              $self.value =  Number(row?.unitPriceContainingTax || 0) * Number(row?.invoiceQuantity || 0)
            }`)
        },
        noTaxAmount: {
          type: 'number',
          title: i18nExpression('contractMod.unAmount'), // 未税金额
          'x-render-table-column': {
            minWidth: 120
          },
          'x-reactions': expression(`(field) => {
              // 对账单明细行未税金额：未税单价 * 本次开票数量
              let row = $table.getRowByIndex($self.index)
              $self.value =  Number(row?.unitPriceExcludingTax || 0) * Number(row?.invoiceQuantity || 0)
            }`)
        },
        currencyName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.currency'), // 币种
          'x-render-table-column': {
            minWidth: 120
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
