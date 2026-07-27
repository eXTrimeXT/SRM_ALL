import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('purSettlementMod.deductionRebateDetails') // 扣罚&返利明细
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
            '@click': expression(`
                () => {
                  $openPerformanceDialog($form, $message)
                }
              `)
          }
        }
      }
    },
    punishList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        maxHeight: 400,
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        primaryKey: 'onlineInvoicePunishId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'punishList:*',
      properties: generateXindexInOrder({
        onlineInvoicePunishId: {
          type: 'string',
          'x-hidden': true
        },
        assessmentNo: {
          type: 'string',
          title: i18nExpression('perfMod.assessmentNo'), // 考核单号
          'x-render-table-column': {
            minWidth: 100
          }
        },
        assessmentDate: {
          type: 'string',
          title: i18nExpression('perfMod.assessmentDate'), // 考核时间
          'x-render-table-column': {
            minWidth: 100
          }
        },
        assessmentType: {
          type: 'string',
          title: i18nExpression('purSettlementMod.assessmentEvent'), // 考核事件
          'x-render-table-column': {
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'ASSESSMENT_BILL_TYPE'
          }
        },
        actualAssessmentAmountN: {
          type: 'string',
          title: i18nExpression('purSettlementMod.actualAssessmentAmountN2'), // 实际考核未税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        tax: {
          type: 'string',
          title: i18nExpression('contractMod.taxQuota'), // 税额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        actualAssessmentAmountY: {
          type: 'string',
          title: i18nExpression('purSettlementMod.actualAssessmentAmountY2'), // 实际考核含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        currencyCode: {
          type: 'string',
          title: i18nExpression('quota.currency'), // 币种
          'x-render-table-column': {
            minWidth: 100
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('bidMod.categoryName'), // 物料分类
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        comment: {
          type: 'string',
          title: i18nExpression('common.remark'), // 备注
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
                      $setRowAmount($form)
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
