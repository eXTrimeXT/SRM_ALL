/**
 * @description 供应商查看中标结果
 */
import {
  expression,
  methodExpression,
  generateXindexInOrder,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateCharExpressionByFunction,
  generateCharFunctionExpression,
  generateCharReactionExpression
} from '@meicloud/render-engine'
import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
import PaymentTypeSegment from 'lib@/compositionEngine/sourcing/paymentType'
import LadderPriceSegment from 'lib@/compositionEngine/inquiry/ladderPrice'
import TechnicalDocumentsSegment from 'lib@/compositionEngine/sourcing/technicalDocuments'
import FormulaPriceDialog from '@/library/compositionEngine/inquiry/formulaPrice'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/compositionEngine/sourcing/enum'

/* 公式报价 */
const $handleFormulaQuote = (index: any, row: any, $form: any, $message: any) => {
  const data = $form.query('orderItemList').get('data')
  if (!row.orderCurrency) {
    // 必须先选币种才知道价格精度
    $message.warning('请先选择币种！')
    return
  }
  if (!row.taxKey) {
    // 请先选择税率！
    $message.warning('请先选择税率！')
    return
  }
  data.priceEditRow = {
    ...row,
    currency: row.orderCurrency,
    taxKey: row.taxKey
  }
  data.editIndex = index
  data.formulaPriceQueryParams = {
    souItemId: row.souItemId || '',
    orderItemId: row.orderItemId || '',
    // 币种，用于基材价格根据汇率转换
    currencyCode: row.orderCurrency
  }
  $form.query('.FormulaPriceDialog').take().setComponentProps({
    visible: true
  })
}

// 公式报价保存
const $saveFormulaInFormation = (val: any, $form: any) => {
  const data = $form.query('orderItemList').get('data')
  $form.values.orderItemList[data.editIndex].formulaAttrValues = val.formulaAttrValues
  $form.values.orderItemList[data.editIndex].orderNotaxPrice = val.orderNotaxPrice
  $form.values.orderItemList[data.editIndex].orderTaxPrice = val.orderTaxPrice
}

export default function (scope: any): Record<any, any> {
  // 注册事件
  Object.assign(scope, {
    $handleFormulaQuote,
    $saveFormulaInFormation
  })
  return {
    // 公式报价
    FormulaPriceDialog: {
      type: 'void',
      'x-component': FormulaPriceDialog,
      'x-component-props': {
        'businessType': BUSINESS_TYPE_ENUM.INQUIRY_LTS,
        'detailInfo': expression('$form.query("orderItemList").get("data")?.priceEditRow'),
        'queryParams': expression('$form.query("orderItemList").get("data")?.formulaPriceQueryParams'),
        'proxyQuoteParams': expression(`{
          visible: true,
          projectId: $values.projectId,
          souNo: $values.souNo,
          vendorId: $form.query("orderItemList").get("data")?.priceEditRowo?.vendorId
        }`),
        'pricePrecision': expression('$form.query("orderItemList").get("data")?.pricePrecision'),
        'readonly': expression('$form.readPretty'),
        '@save': expression('(val) => $saveFormulaInFormation(val, $form)'),
        '@close': expression(`() => {
          $form.query('.FormulaPriceDialog').take().setComponentProps({
            visible: false
          })
        }`)
      }
    },
    // 物料列表
    orderItemList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-reactions': expression(`() => {
        const baseData = $form.query('InqSouOrderForVendor').get('data')
        const thatData = $form.query("orderItemList").get('data')
        if (baseData?.projectInfo && Object.keys(baseData?.projectInfo).length > 0) {
          const currency = baseData.projectInfo.currencyList.find((item) => {
            return item.currencyCode === $form.values.orderItemList[0].orderCurrency
          })
          thatData.pricePrecision = currency ? currency.pricePrecision : 6
        }
      }`),
      'x-data': {
        priceEditRow: {},
        editIndex: 0,
        formulaPriceQueryParams: {},
        projectInfo: {},
        pricePrecision: null
      },
      'x-component-props': {
        preColumns: 'seq',
        class: 'table-view-vxe-table',
        openCustomTable: false,
        pagination: false,
        sortable: false,
        editMode: generateCharExpressionByFunction(({ $readonly }) => !$readonly)
      },
      'x-read-pretty': true,
      properties: generateXindexInOrder({
        // 业务实体
        orgOuName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bid_mod.businessEntity'),
            minWidth: 150
          }
        },
        // 库存组织
        orgInvName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bid_mod.inv'),
            minWidth: 150
          }
        },
        // 是否无料号寻源
        noCodeItem: {
          type: 'string',
          'x-render-table-column': {
            title: '是否无料号寻源',
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          }
        },
        // 物料编码
        itemCode: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.itemCode'),
            minWidth: 120
          }
        },
        // 物料名称
        itemDesc: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.itemName'),
            minWidth: 150
          }
        },
        // 物料分类
        categoryName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.categoryName'),
            minWidth: 150
          }
        },
        // 组合
        itemGroup: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.itemGroup'),
            minWidth: 150
          }
        },
        // 行类型
        itemType: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.itemType'),
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'DMAND_LINE_TYPE'
          }
        },
        // 预计数量
        requireQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.demandQuantity'),
            minWidth: 100
          }
        },
        // 单位
        unit: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.unit'),
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'unit'
          }
        },
        // 公式值
        formulaValue: {
          type: 'string',
          'x-reactions': generateCharFunctionExpression(({ $form, $self }) => {
            $self.visible = $form.query('orderItemList').get('data')?.projectInfo?.orderType === 'FORMULA'
          }),
          'x-render-table-column': {
            title: i18nExpression('bid_mod.formulaValue'),
            minWidth: 110
          }
        },

        // 币种
        orderCurrency: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.currency_price'),
            minWidth: 150
          },
          'x-read-pretty': false,
          'x-component': 'DictSelect',
          'x-component-props': {
            // TODO 过滤可用币种
            code: 'currency',
            '@change': generateCharFunctionExpression(({ $values }, value) => {
              // 值同步所有行
              $values.orderItemList.forEach((item: Record<string, any>) => {
                item.orderCurrency = value
              })
              // TODO 计算含税单价
            })
          },
          ...editTableFormItemValid
        },

        // 税率
        taxKey: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.taxRate2'),
            minWidth: 150
          },
          'x-read-pretty': false,
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'tax',
            '@change-value': generateCharFunctionExpression(({ $values }, _value, dictItem) => {
              // 值同步所有行
              $values.orderItemList.forEach((item: Record<string, any>) => {
                item.taxKey = _value
                item.taxRate = dictItem.key
              })
              // TODO 计算含税单价
            })
          },
          'x-decorator': 'FormItem',
          ...editTableFormItemValid
        },

        // 未税单价
        orderNotaxPrice: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quotenotaxPrice2'),
            minWidth: 100
          },
          // 阶梯报价 || 公式报价 不能输入
          'x-read-pretty': generateCharExpressionByFunction(({ $table, $form, $self }) => {
            return $table.getRowByIndex($self.index)?.isLadder === 'Y' || $form.query('orderItemList').get('data')?.projectInfo?.orderType === 'FORMULA'
          }),
          'x-component-props': {
            // TODO 数字 只能输入币种的小数位
            // @change="noTaxPriceChange(scope)"
          },
          'x-decorator': 'FormItem',
          ...editTableFormItemValid
        },

        // 含税单价
        orderTaxPrice: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.quotetaxPrice2'),
            minWidth: 100
          },
          'x-reactions': generateCharReactionExpression(({ $table, $self, $readonly, $bigCalcTaxPrice }) => {
            const row = $table.getRowByIndex($self.index)
            if (row && row.taxRate && row.isLadder !== 'Y' && !$readonly) {
              // FIXME 价格精度取币种的精度，这里先写死4位
              row.orderTaxPrice = $bigCalcTaxPrice(row.orderNotaxPrice, row.taxRate, 4)
            }
          })
        },

        // 付款条款
        paymentType: {
          type: 'void',
          'x-content': generateCharExpressionByFunction(({ $t, $readonly }) => $t($readonly ? 'common.view' : 'bidMod.input')),
          'x-component': 'RenderTableLink',
          'x-component-props': {
            type: 'text',
            '@click': generateCharFunctionExpression(({ $table, $self, $getFieldParentFieldFormPath }) => {
              // 只触发当前的
              const row = $table.getRowByIndex($self.index)
              console.log(row, 'row')
              const state = $self.query($getFieldParentFieldFormPath($self, 3).concat('paymentTypeDialog')).get('data')
              state.editIndex = $self.index
              state.mode = 'quote'
              if (row.souItemId && row.orderItemId) {
                state.editRow = { ...row }
              } else {
                state.editRow = {
                  paymentList: row.paymentList && Array.isArray(row.paymentList) ? row.paymentList : []
                }
              }

              $self.query($getFieldParentFieldFormPath($self, 3).concat('paymentTypeDialog')).take(field => {
                field.setComponentProps({ visible: true })
              })
            })
          },
          'x-render-table-column': {
            title: i18nExpression('paymentType.paymentType'),
            width: 110
          }
        },

        // 是否阶梯价
        isLadder: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.isLadder'),
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          }
        },

        // 定价开始日期
        priceStartTime: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.priceBeginDate'),
            minWidth: 160
          }
        },
        // 定价结束日期
        priceEndTime: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('bidMod.priceOverDate'),
            minWidth: 160
          }
        },

        // 技术文件
        itemFiles: {
          type: 'void',
          title: i18nExpression('common.view'),
          'x-component': 'RenderTableLink',
          'x-component-props': {
            type: 'text',
            '@click': generateCharFunctionExpression(({ $table, $self, $getFieldParentFieldFormPath }) => {
              const row = $table.getRowByIndex($self.index)
              // 只触发当前的
              const takeObj: any = $self.query($getFieldParentFieldFormPath($self, 3).concat('technicalDocumentsDialog')).take()
              takeObj.setData({
                editRow: {
                  materialCode: row.noCodeItem === 'Y' ? '' : row.itemCode,
                  businessId: row.souItemId || '',
                  detailData: row.itemFiles || []
                },
                editRowIndex: $self.index
              })
              takeObj.setComponentProps({ visible: true })
            })

          },
          'x-render-table-column': {
            title: i18nExpression('bidMod.technicalDocuments.title'),
            width: 120
          }
        },

        // 备注
        remark: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('common.remark'),
            minWidth: 160
          }
        },
        operation: {
          type: 'void',
          'x-render-table-column': {
            title: i18nExpression('common.operation'),
            width: 150,
            fixed: 'right'
          },
          // 公式报价，或者存在阶梯价行
          'x-visible': generateCharExpressionByFunction(({ $values, $form }) => {
            return $form.query('orderItemList').get('data')?.projectInfo?.orderType === 'FORMULA' || $values.orderItemList.find((item: any) => item.isLadder === 'Y')
          }),
          'x-component': 'RenderTableButtonList',
          properties: {
            // 公式报价
            formula: {
              type: 'void',
              title: generateCharExpressionByFunction(({ $t, $readonly }) => $readonly ? '公式报价明细' : $t('bidMod.formulaQuote')),
              'x-reactions': generateCharFunctionExpression(({ $self, $table }) => {
                $self.visible = $table.getRowByIndex($self.index).isFormula === 'Y'
              }),
              'x-component-props': {
                // TODO 待实现
                '@click': generateCharFunctionExpression(({ $self, $table, $form, $message }) => {
                  const row = $table.getRowByIndex($self.index)
                  $handleFormulaQuote($self.index, row, $form, $message)
                })
              }
            },
            // 阶梯价
            ladder: {
              type: 'void',
              title: generateCharExpressionByFunction(({ $t, $readonly }) => $readonly ? '阶梯报价明细' : $t('bidMod.ladderPrice')),
              'x-reactions': changeFieldVisibleByDeps(
                ['.isLadder'],
                '$deps[0] === "Y"'
              ),
              'x-component-props': {
                '@click': generateCharFunctionExpression(({ $table, $self, $message, $getFieldParentFieldFormPath }) => {
                  const row = $table.getRowByIndex($self.index)
                  if (!row.orderCurrency) {
                    // 必须先选币种才知道价格精度
                    $message.warning('请先选择币种！')
                    return
                  }
                  if (!row.taxRate) {
                    // 请先选择税率！
                    $message.warning('请先选择税率！')
                    return
                  }

                  // 只触发当前的
                  const takeObj: any = $self.query($getFieldParentFieldFormPath($self, 4).concat('ladderPriceDialog')).take()
                  takeObj.setComponentProps({ visible: true })
                  takeObj.setData({
                    editRow: {
                      itemCode: row.itemCode,
                      itemDesc: row.itemDesc,
                      requireQuantity: row.requireQuantity,
                      unit: row.unit,
                      ladderPriceList: row.ladderPriceList
                    },
                    editRowIndex: $self.index,
                    mode: 'quote'
                  })
                })
              }
            }
            // TODO 模板报价待拉通
            // template: {
            //   type: 'void',
            //   title: generateCharExpressionByFunction(({ $t, $readonly }) => $readonly ? '模板报价明细' : $t('templatePrice.label')),
            // 'x-reactions': generateCharFunctionExpression(({ $form, $self }) => {
            //   $self.visible = $form.query('orderItemList').get('data')?.projectInfo?.orderType === 'TEMPLATE'
            // }),
            //   'x-component-props': {
            //     '@click': generateCharFunctionExpression(() => {
            //       // openTemplatePriceDialog($index, row)
            //     })
            //   }
            // }
          }
        }
      })
    },

    // 付款条款
    ...PaymentTypeSegment(),

    // 阶梯价弹窗
    ...LadderPriceSegment({ mode: 'quote' }),

    // 公式报价

    // 技术文件
    ...TechnicalDocumentsSegment({ readonly: true })

    // 模版报价
  }
}
