import {
  expression,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
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
      'x-component': 'Space',
      'x-component-props': {
        style: 'margin-bottom: 16px'
      },
      'x-reactions': expression(`(field) => {
             field.visible = !$form.readPretty
         }`),
      properties: {
        // 批量维护
        openBatchSetDialog: {
          type: 'void',
          title: i18nExpression('purchaseOrder.batchSet'), // 批量维护
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $openBatchSetDialog($self,$form,$message)
            }`)
          }
        },
        exportExcel: {
          type: 'void',
          title: i18nExpression('purchaseOrder.exportExcel'), // 自定义导出
          'x-component': 'RButton',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $exportExcel($form, $message)
            }`)
          }
        },
        importUpdate: {
          type: 'void',
          'x-component': 'el-tooltip',
          'x-component-props': {
            effect: 'dark',
            content: ' {{$t(\'purchaseOrder.importTip\') }}', // '批量导入时建议先导出订单明细，再按导出的订单明细仅修改供方承诺到货日期、确认意见，供方确认订单数量页面可修改才可修改；其余内容请勿修改，可删除行数据进行导入不可新增',
            placement: 'top-start'
          },
          properties: {
            importExcel: {
              type: 'void',
              'x-component': 'ImportExcel',
              'x-component-props': {
                title: '批量更新',
                showTemplate: false,
                type: 'default',
                extraData: {
                  orderId: '{{$attrs.params.row.orderId}}',
                  uploadType: 'PAAS_MINIO',
                  sourceType: 'WEB_APP',
                  fileModular: 'sup-ce',
                  fileFunction: '采购订单明细维护',
                  fileType: 'excel'
                },
                upLoadUrl: '/api-sup-ce/po/orderDetail/importByOrder',
                '@handleSuccess': expression(`() => {
                  $queryEngine.request.read()
                }`)
              },
              'x-slot': 'default'
            }
          }
        }
      }
    },
    detailList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        style: 'flex: 1',
        maxHeight: 400,
        pagination: false,
        preColumns: 'checkbox',
        sortable: false,
        checkboxConfig: { trigger: 'defatult' }
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
          'x-render-table-column': {
            minWidth: 150
          }
        },
        unit: {
          type: 'string',
          'x-render-table-column': {
            minWidth: 100,
            title: i18nExpression('purchaseDemand.unitCode') // 单位
          }
          // 'x-component': 'DictSelect',
          // 'x-component-props': {
          //   code: 'unit'
          // }
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
          }
        },
        requirementDate: {
          title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
          'x-render-table-column': {
            minWidth: 150
          },
          ...yearMonthDaySelectorSegment
        },
        ceeaPlanReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
            minWidth: 150
          }
        },
        confirmNum: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('purchaseOrder.confirmNum'), // 供方确认订单数量
            minWidth: 160,
            customRender: true,
            titlePrefix: '{{$form.query(\'OrderVendor\').get(\'data\').configValue?.vendorRefuse ? { content: \'供方确认数量填0则是拒绝该订单行\' } : null}}'
          },
          'x-component-props': {
            disabled: expression('(!$form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply && !$form.query(\'OrderVendor\').get(\'data\').configValue?.vendorRefuse && !$form.query(\'OrderVendor\').get(\'data\').configValue?.vendorModifyCount) ||  ($form.values.demandType === \'NONPRODUCTIVE_DEMAND\' && !$form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply)')
          },
          'x-reactions': expression(`(field) => {
            const row = $table.getRowByIndex($self.index)
            let total = $form.values?.detailList?.filter(item => item.parentLineNum === row?.parentLineNum)?.reduce((prev, cur) => prev + Number(cur.confirmNum || 0), 0)
            setTimeout(()=>{
              field.setComponentProps({
                class: (row?.orderNum > total || total === 0)  ? 'high-light' :''
              })
            })
          }`),
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              const { vendorRefuse, vendorModifyCount,vendorSplitReply } = $form.query('OrderVendor').get('data').configValue
              const row = $table.getRowByIndex($self.index)
              if(vendorSplitReply){
                if (value < 0) {
                  return $t('purchaseOrder.prompt11') //'供方确认订单数量不能小于0，请检查！'
                }
              }else{
                if($form.values.demandType === 'NONPRODUCTIVE_DEMAND'){
                  // 非生产性需求
                  if (value !== row.orderNum && value !== 0) {
                    return $t('purchaseOrder.prompt9') // '供方确认订单数量若更改只可改为0，请检查！'
                  }
                } else if (vendorRefuse && vendorModifyCount) {
                  // 允许供应商更改订单数量和拒绝订单时
                  if (value > row.orderNum) {
                    return $t('purchaseOrder.prompt10') // '供方确认订单数量不能大于订单数量，请检查！'
                  }
              
                  if (value < 0) {
                    return $t('purchaseOrder.prompt11') //'供方确认订单数量不能小于0，请检查！'
                  }
                } else if (vendorModifyCount) {
                  // 允许供应商更改订单数量时
                  if (value > row.orderNum) {
                    return $t('purchaseOrder.prompt12') //'供方确认订单数量不能大于订单数量，请检查！'
                  }
              
                  if (value <= 0) {
                    return $t('purchaseOrder.prompt13')  // '供方确认订单数量必须大于0，请检查！'
                  }
                } else if (vendorRefuse) {
                  // 允许供应商拒绝时
                  if (value !== row.orderNum && value !== 0) {
                    return $t('purchaseOrder.prompt14') //'供方确认订单数量若更改只可改为0，请检查！'
                  }
                } else if (row?.ladderPriceFlag === 'Y') {
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
              }
              
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        ceeaPromiseReceiveDate: {
          ...yearMonthDaySelectorSegment,
          'x-render-table-column': {
            title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
            minWidth: 150,
            customRender: true
          },
          'x-component-props': {
            placeholder: i18nExpression('common.pleaseSelectDate'),
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
              if(new Date(value) < (new Date() - 24*60*60*1000)){
                return $t('purchaseOrder.prompt15')// 供方承诺到货日期不能小于当前日期！
              }
            }`)
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        refusedReason: {
          type: 'string',
          title: i18nExpression('purchaseOrder.refusedReason'), // 确认意见
          'x-component-props': {
            maxlength: 50
          },
          'x-render-table-column': {
            minWidth: 150,
            customRender: true
          },
          'x-read-pretty': '{{$form.readPretty}}'
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
          'x-render-table-column': {
            minWidth: 150
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
          'x-render-table-column': {
            minWidth: 100
          }
        },
        ceeaUnitTaxPrice: {
          type: 'string',
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.taxPrice') // 含税单价
          }
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
            readPretty: true,
            showKey: 'currencyName',
            propKey: 'currencyName',
            name: 'scc_base_purchase_currency_info'
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.currency') // 币种
          }
        },
        // 税率
        ceeaTaxKey: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax'
          },
          'x-render-table-column': {
            minWidth: 120,
            title: i18nExpression('purchaseDemand.taxRate')
          }
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
          'x-visible': '{{orderConfig.showContractInfor === \'Y\'}}',
          'x-render-table-column': {
            title: i18nExpression('orderMod.contractInfor'), // 合同信息
            minWidth: 80,
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
          'x-render-table-column': {
            title: i18nExpression('common.operation'),
            width: 80,
            fixed: 'right',
            sortable: false
          },
          'x-component': 'RenderTableButtonList',
          'x-visible': expression('!$form.readPretty && $form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply'),
          properties: {
            split: {
              type: 'void',
              title: i18nExpression('purchaseOrder.split'), // 拆分
              'x-reactions': changeFieldVisibleByDeps(
                [],
                // 待供方确认
                '$table.getRowByIndex($self.index)?.isParentLine'
              ),
              'x-component-props': {
                '@click': expression('({ row }) => $opentSplitDialog(row, $form)')
              }
            },
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'), // 删除
              'x-reactions': changeFieldVisibleByDeps(
                [],
                // 待供方确认
                '!$table.getRowByIndex($self.index)?.isParentLine'
              ),
              'x-component-props': {
                '@click': expression(`
                     ({ rowIndex }) => {
                        // $table.remove(rowIndex)
                        $form.values.detailList.splice(rowIndex,1)
                        $form.values.detailList.forEach((item, index) => {
                          item.lineNum = index + 1 + ''
                        })
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
