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
        // 采购申请新增
        addPurchase: {
          type: 'void',
          title: i18nExpression('purchaseDemand.addPurchaseApp'),
          'x-component-props': {
            type: 'primary',
            disabled: '{{$attrs?.params?.row?.sourceSystem  === \'DEMAND\' || ($form.values.detailList?.length > 0 && !$form.values.detailList[0].ceeaRequirementHeadNum)}}',
            '@click': expression(`() => {
              $openPurchaseDialog($form,$message)
            }`)
          }
        },
        // 物料维护新增
        addMaterial: {
          type: 'void',
          title: i18nExpression('purchaseDemand.addMaterial'),
          'x-component-props': {
            type: 'primary',
            disabled: '{{$attrs?.params?.row?.sourceSystem  === \'DEMAND\' || $form.values.demandType === \'NONPRODUCTIVE_DEMAND\'  || ($form.values.detailList?.length > 0 && !!$form.values.detailList[0].ceeaRequirementHeadNum)}}',
            '@click': expression(`() => {
              $openMaterialDialog($form,$message)
            }`)
          }
        }
      }
    },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'orderDetailId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        orderDetailId: {
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
          default: null,
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
            minWidth: 150,
            customRender: true
          }
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
            minWidth: 120,
            customRender: true
          },
          'x-component-props': {
            '@change': expression(`() => {
              $setRowAmount($table.getRowByIndex($self.index), $form)
            }`)
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value < $self.query('.usedContractQuantity').get('value')){
                return $t('purchaseOrder.prompt6') // '订单数量不能小于合同已关联数量'
              }

              const row = $table.getRowByIndex($self.index)
              if(row?.ladderPriceFlag === 'Y'){
                const ladderPrice = row.ladderPrices.find(item => {
                  if(item.endQuantity){
                    return value >= item.beginQuantity && value < item.endQuantity
                  }else{
                    return value >= item.beginQuantity
                  }
                  
                })
                if (!ladderPrice) {
                  return $t('purchaseOrder.prompt7') // '订单数量无对应区间阶梯价，请修改！'
                }
              }
            }`)
          }
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
        ceeaPlanReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.ceeaPlanReceiveDate, '{y}-{m}-{d}')
            }`),
            'picker-options': {
              disabledDate: (time:any) => {
                const start = new Date()
                return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
              }
            },
            '@change': expression(`() => {
              let row = $table.getRowByIndex($self.index)
              row.ceeaPromiseReceiveDate = row.ceeaPlanReceiveDate
            }`)
          },
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
            minWidth: 150,
            customRender: true
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            triggerType: 'onBlur',
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(new Date(value) < new Date($values.ceeaPurchaseOrderDate)){
                return $t('orderMod.dateValidate') // 要求到货日期及供方承诺到货日期,应晚于订单日期！
              }
            }`)
          }
        },
        confirmNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purchaseOrder.confirmNum'), // 供方确认订单数量
            minWidth: 140
          },
          'x-reactions': expression(`(field) => {
            const row = $table.getRowByIndex($self.index)
            if(row?.confirmNum || row?.confirmNum === 0){
              let statusFlag = ['PART_ACCEPT','APPROVED','REFUSED'].includes($form.values?.orderStatus)
              let total = $form.values?.detailList?.filter(item => item.parentLineNum === row?.parentLineNum)?.reduce((prev, cur) => prev + Number(cur.confirmNum || 0), 0)
        
              setTimeout(()=>{
                field.setComponentProps({
                  class: (statusFlag && (row?.orderNum > total || total === 0))  ? 'high-light' :''
                })
              })
            }
          }`)
        },
        ceeaPromiseReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.ceeaPromiseReceiveDate, '{y}-{m}-{d}')
              }`)
          },
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150
          }
        },
        bomVersion: {
          type: 'string',
          'x-visible': '{{orderConfig.showBom === \'Y\' && $form.values.orderType === \'OUTSOURCING\'}}',
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseOrder.bomVersion'), // Bom版本
            customRender: true
          },

          'x-component': 'BomVersionSearch',
          'x-component-props': {
            disabled: '{{$form.readPretty}}',
            inputModel: '{{$self.value}}',
            '@clear': expression(`value => {
                $form.query('Order').get('data').detailListCurrentIndex = $self.index
                $form.values.detailList[$self.index].bomVersion = ''
                $form.values.detailList[$self.index].bomId = null
              }`),
            '@openDialog': expression(`() => {
                $form.query('Order').get('data').detailListCurrentIndex = $self.index
                $openBomVersionDialog($form, $queryEngine,$table.getRowByIndex($self.index))
              }`)
          },
          ...editTableFormItemValid
        },
        bomDetail: {
          type: 'void',
          'x-visible': '{{orderConfig.showBom === \'Y\' && $form.values.orderType === \'OUTSOURCING\'}}',
          'x-render-table-column': {
            title: i18nExpression('purchaseApplication.bomDetail'), // 'BOM明细'
            minWidth: 100,
            sortable: false
          },
          properties: {
            layout: {
              type: 'void',
              'x-component': 'Space',
              properties: {
                viewFollowUp: {
                  type: 'void',
                  title: i18nExpression('purchaseApplication.detail'), // 详情
                  'x-component': 'TableButton',
                  'x-hidden': '{{!$table.getRowByIndex($self.index).bomVersion}}',
                  'x-component-props': {
                    type: 'text',
                    '@click': expression('({row}) => {$openBomVDetailDialog($form,row, $queryEngine)}')
                  }
                }
              }
            }
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
            maxlength: 50,
            showWordLimit: true,
            customRender: true
          },
          'x-render-table-column': {
            minWidth: 150
          }
        },
        refusedReason: {
          type: 'string',
          title: i18nExpression('purchaseOrder.refusedReason'), // 确认意见
          'x-render-table-column': {
            minWidth: 120
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
          }
        },
        isladderPriceFlag: {
          type: 'string',
          'x-component': 'TableButton',
          'x-component-props': {
            type: 'text',
            disabled: expression('$self.value !== \'是\''),
            '@click': expression('({row}) => $openLadderPriceDialog($form,$table.getRowByIndex($self.index))')
          },
          'x-render-table-column': {
            title: i18nExpression('bidMod.isLadders'), // 是否阶梯价
            minWidth: 120,
            customRender: true
          },
          'x-reactions': expression(`(field) => {
            $self.value = $table.getRowByIndex($self.index)?.ladderPriceFlag === 'Y' ? '是'  : '否'
          }`)
        },
        ceeaUnitTaxPrice: {
          type: 'string',
          'x-component-props': {
            disabled: `{{
              $attrs?.params?.row?.sourceSystem === 'DEMAND'
            }}`,
            '@change': expression(`() => {
              $setRowAmount($table.getRowByIndex($self.index), $form)
            }`)
          },
          'x-render-table-column': {
            minWidth: 120,
            customRender: true,
            title: i18nExpression('purchaseDemand.taxPrice') // 含税单价
          },
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
            disabled: '{{$attrs?.params?.row?.sourceSystem === \'DEMAND\'}}',
            '@close-quicksearch': expression(`(val) => {
              let row = $table.getRowByIndex($self.index)
              row.currencyId = val ? val.currencyId : ''
              row.currencyCode = val ? val.currencyCode : ''
              row.currencyName = val ? val.currencyName : ''
            }`)
          },
          'x-render-table-column': {
            minWidth: 120,
            customRender: true,
            title: i18nExpression('purchaseDemand.currency') // 币种
          },
          ...editTableFormItemValid
        },
        // 税率
        ceeaTaxKey: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax',
            disabled: '{{$attrs?.params?.row?.sourceSystem === \'DEMAND\'}}',
            '@change-value': expression(`(val, dictItem) => {
              $table.getRowByIndex($self.index).ceeaTaxRate = dictItem ? dictItem.key : '' // 税率值
            }`)
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.taxRate'),
            customRender: true
          },
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
        usedContractQuantity: {
          type: 'string',
          default: 0,
          'x-component': 'RButton',
          'x-component-props': {
            type: 'text',
            disabled: '{{!$table.getRowByIndex($self.index).orderDetailId}}',
            '@click': expression('() => $viewContract($table.getRowByIndex($self.index), $form)')
          },
          'x-render-table-column': {
            minWidth: 140,
            fixed: 'right',
            title: i18nExpression('orderMod.contractQuantity') // 合同已关联数量
          },
          'x-read-pretty': false
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
                type: 'text',
                '@click': expression(`
                  ({ rowIndex }) => {
                    $concatContract($table.getRowByIndex($self.index), $form)
                  }
                `)
              }
            },
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
