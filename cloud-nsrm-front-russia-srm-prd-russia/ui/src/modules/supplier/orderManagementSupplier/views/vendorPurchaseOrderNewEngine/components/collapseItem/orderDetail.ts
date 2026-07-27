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
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'requirementLineId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        requirementLineId: {
          type: 'string',
          'x-hidden': true
        },
        lineNum: {
          type: 'string',
          title: i18nExpression('purchaseDemand.lineNum'), // 行号
          'x-render-table-column': {
            minWidth: 80
          }
        },
        ceeaRequirementHeadNum: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.purRequisitionNum'), // 采购申请单号
            minWidth: 120
          }
        },
        ceeaRowNum: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.rowNum'), // 申请行号
            minWidth: 100
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
        materialCode: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        // 物料名称
        materialName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.itemName'),
          'x-component-props': {
            disabled: `{{
              !(['20', '40', '60'].includes($table.getRowByIndex($self.index).bigCategoryCode) ||
                ($table.getRowByIndex($self.index).bigCategoryCode === '70' &&
                ($table.getRowByIndex($self.index).materialCode.startsWith(61) ||
                $table.getRowByIndex($self.index).materialCode.startsWith(78))))
            }}`
          },
          'x-render-table-column': {
            minWidth: 150
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        requirementQuantity: {
          type: 'string',
          title: i18nExpression('purchaseDemand.requirementQuantity'), // 需求数量
          'x-render-table-column': {
            minWidth: 100
          }
        },
        orderNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
            minWidth: 120
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        requirementDateBuff: {
          title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment
        },
        ceeaPlanReceiveDate: {
          type: 'date',
          default: null,
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
            minWidth: 150
          },
          'x-component-props': {
            placeholder: i18nExpression('common.pleaseSelectDate'),
            format: 'yyyy-MM-dd',
            'value-format': 'yyyy-MM-dd HH:mm:ss',
            'picker-options': {
              disabledDate: (time:any) => {
                const start = new Date()
                return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
              }
            }
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(new Date(value) < new Date($values.ceeaPurchaseOrderDate)){
                return $t('orderMod.dateValidate') // 要求到货日期及供方承诺到货日期,应晚于创建订单日期！
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        ceeaPromiseReceiveDate: {
          type: 'date',
          default: null,
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150
          },
          'x-component-props': {
            placeholder: i18nExpression('common.pleaseSelectDate'),
            format: 'yyyy-MM-dd',
            'value-format': 'yyyy-MM-dd HH:mm:ss'
          }
        },
        purchaseProject: {
          type: 'string',
          title: i18nExpression('purchaseDemand.purchaseItem'), // 采购项目
          'x-render-table-column': {
            minWidth: 100
          }
        },
        comments: {
          type: 'string',
          title: i18nExpression('purchaseDemand.comments'), // 明细备注
          'x-component-props': {
            maxlength: 80
          },
          'x-render-table-column': {
            minWidth: 150
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        refusedReason: {
          type: 'string',
          title: i18nExpression('purchaseDemand.refusedReason'), // 供方拒绝原因
          'x-render-table-column': {
            minWidth: 100
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
          'x-render-table-column': {
            minWidth: 100
          }
        },
        unit: {
          type: 'string',
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('purchaseDemand.unitCode') // 单位
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'unit'
          }
        },
        ceeaUnitTaxPrice: {
          type: 'string',
          'x-component-props': {
            disabled: `{{
              $attrs?.params?.row?.sourceSystem === 'DEMAND'
            }}`
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.taxPrice') // 含税单价
          },
          'x-read-pretty': '{{$form.readPretty}}',
          ...editTableFormItemValid
        },
        ceeaUnitNoTaxPrice: {
          type: 'string',
          title: i18nExpression('contractMod.notaxPrice'), // 不含税单价
          'x-render-table-column': {
            minWidth: 100
          }
        },
        currencyName: {
          type: 'string',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'currencyName',
            propKey: 'currencyName',
            name: 'scc_base_purchase_currency_info',
            disabled: '{{$attrs?.params?.row?.sourceSystem === \'DEMAND\'}}'
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.currency') // 币种
          },
          'x-read-pretty': '{{$form.readPretty}}',
          ...editTableFormItemValid
        },
        // 税率
        ceeaTaxKey: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax',
            disabled: '{{$attrs?.params?.row?.sourceSystem === \'DEMAND\'}}'
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.taxRate')
          },
          'x-read-pretty': '{{$form.readPretty}}',
          ...editTableFormItemValid
        },
        ceeaAmountIncludingTax: {
          type: 'string',
          title: i18nExpression('contractMod.amount2'), // 含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        ceeaAmountExcludingTax: {
          type: 'string',
          title: i18nExpression('contractMod.excludeTaxPayAmount'), // 不含税金额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        ceeaTaxAmount: {
          type: 'string',
          title: i18nExpression('contractMod.taxQuota'), // 税额
          'x-render-table-column': {
            minWidth: 100
          }
        },
        contractInfor: {
          type: 'void',
          'x-read-pretty': false,
          'x-render-table-column': {
            title: i18nExpression('orderMod.contractInfor'), // 合同信息
            minWidth: 100,
            fixed: 'right',
            sortable: false
          },
          properties: {
            layout: {
              type: 'void',
              'x-component': 'Space',
              properties: {
                viewContract: {
                  type: 'void',
                  title: i18nExpression('common.view'), // 查看
                  'x-component': 'TableButton',
                  'x-component-props': {
                    type: 'text',
                    '@click': expression('() => $viewContract($table.getRowByIndex($self.index), $form)')
                  }
                }
              }
            }
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 160,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty
            }`),
          properties: {
            concatContract: {
              type: 'void',
              title: i18nExpression('orderMod.relationshipAgreement'),
              'x-component-props': {
                type: 'text'
              }
            },
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                type: 'text'
              }
            }
          }
        }
      })
    }
  }
}
