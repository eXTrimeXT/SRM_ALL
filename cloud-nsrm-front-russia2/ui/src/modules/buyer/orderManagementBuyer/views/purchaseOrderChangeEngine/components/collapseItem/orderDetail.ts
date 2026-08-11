import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  editTableFormItemValid,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailsList') // 订单明细
  },
  properties: {
    orderChangeDetails: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        preColumns: 'seq'
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'orderChangeDetails:*',
      properties: generateXindexInOrder({
        categoryName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemName'), // 物料名称
          'x-render-table-column': {
            minWidth: 150
          }
        },
        orderDetailStatus: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OrderDetailStatus'
          },
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
            minWidth: 100
          }
        },
        unit: {
          type: 'string',
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('purchaseDemand.unitCode') // 单位
          }
        },
        requirementQuantity: {
          type: 'string',
          title: i18nExpression('purchaseDemand.requirementQuantity'), // 需求数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        deliveryNoticeQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        maxOrderQuantity: {
          type: 'string',
          title: i18nExpression('orderMod.maxOrderQuantity'), // 采购申请可用数量
          'x-render-table-column': {
            minWidth: 140
          }
        },
        originOrderNum: {
          type: 'string',
          title: i18nExpression('orderMod.oldOrderNum'), // 原订单数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.orderChangeAfterNum'), // 变更后数量
            minWidth: 120,
            customRender: true
          },
          'x-component-props': {
            disabled: expression('$self.query(\'.orderDetailStatus\').get(\'value\') === \'CLOSED\'')
            // '@change': expression(`() => {
            //   $getLadderPrice($form, $table.getRowByIndex($self.index))
            // }`)
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField')
            // validator: expression(`(value, rule) => {
            //   const row = $table.getRowByIndex($self.index)
            //   if(row?.ladderPriceFlag === 'Y'){
            //     const ladderPrice = row.ladderPrices.find(item => {
            //       if(item.endQuantity){
            //         return value >= item.beginQuantity && value < item.endQuantity
            //       }else{
            //         return value >= item.beginQuantity
            //       }

            //     })
            //     if (!ladderPrice) {
            //       return $t('purchaseOrderChange.prompt1') // 订单数量无对应区间阶梯价，请修改！
            //     }
            //   }
            // }`)
          },
          'x-reactions': expression(`(field) => {
            field.setComponentProps({
              class: $self.query('.originOrderNum').get('value') !== field.value ? 'high-light' :''
            })
          }`)
        },
        requirementDate: {
          title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.requirementDate, '{y}-{m}-{d}')
              }`)
          }
        },
        originPlanReceiveDate: {
          title: i18nExpression('orderMod.oldPlanReceiveDate'), // 原要求到货日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.originPlanReceiveDate, '{y}-{m}-{d}')
              }`)
          }
        },
        planReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            disabled: expression('$self.query(\'.orderDetailStatus\').get(\'value\') === \'CLOSED\''),
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.planReceiveDate, '{y}-{m}-{d}')
            }`),
            'picker-options': {
              disabledDate: (time:any) => {
                const start = new Date()
                return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
              }
            }
          },
          'x-render-table-column': {
            title: i18nExpression('orderMod.changeAfterReceiveDate'), // 变更后要求到货日期
            minWidth: 150,
            customRender: true
          },
          ...editTableFormItemValid,
          'x-reactions': expression(`(field) => {
            let originPlanReceiveDate = parseTime($self.query('.originPlanReceiveDate').get('value'),'{y}-{m}-{d}', true)
            let planReceiveDate = field.value ? parseTime(field.value,'{y}-{m}-{d}', true) : ''
            field.setComponentProps({
              class: originPlanReceiveDate !== planReceiveDate ? 'high-light' :''
            })
          }`)
        },
        promiseReceiveDate: {
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.promiseReceiveDate, '{y}-{m}-{d}')
              }`)
          }
        },
        originUsedContractQuantity: {
          type: 'string',
          default: 0,
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewPreContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 140,
            title: i18nExpression('orderMod.originUsedContractQuantity') // 变更前合同关联数量
          },
          'x-read-pretty': false
        },
        usedContractQuantity: {
          type: 'string',
          default: 0,
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            '@click': expression('() => $viewAfterContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 140,
            title: i18nExpression('orderMod.orderChangeUsedContractQuantity') // 变更后合同关联数量
          },
          'x-read-pretty': false
        },
        comments: {
          type: 'string',
          title: i18nExpression('purchaseDemand.comments'), // 明细备注
          'x-component-props': {
            disabled: expression('$self.query(\'.orderDetailStatus\').get(\'value\') === \'CLOSED\''),
            maxlength: 80
          },
          'x-render-table-column': {
            minWidth: 150,
            customRender: true
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 120,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty
            }`),
          properties: {
            concatContract: {
              type: 'void',
              title: i18nExpression('orderMod.editConcatContract'), // 修改关联合同
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                  ({ rowIndex }) => {
                    $concatContract($table.getRowByIndex($self.index), $form)
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
