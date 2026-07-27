/**
 * @description 报价详情
 */
import {
  generateXindexInOrder,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import LadderPriceSegment from 'lib@/compositionEngine/inquiry/ladderPrice'

const QuoteDetailDialogSegment: Record<any, any> = {
  quoteDetailDialog: {
    type: 'object',
    title: '查看报价',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'dialogMain',
      size: 'large',
      footer: false,
      beforeClose: generateCharFunctionExpression(({ $form, $self, $message, $queryEngine, $projectId }, done, type) => {
        done()
        // 清空查询数据
        $self.value.quoteDetailDialogInfo = {}
        $self.value.quoteTableList = []
      }),
      '@opened': generateCharFunctionExpression(async ({ $values, $self, $queryEngine }) => {
        const state = $self.data
        if (state.viewRow) {
          const state = $self.data
          if (state.viewRow) {
            $self.value.quoteDetailDialogInfo = {
              souNo: $values.header.souNo,
              orderNo: state.viewRow.orderNo,
              vendorCode: state.viewRow.vendorCode,
              vendorName: state.viewRow.vendorName,
              standardCurrency: $values.header.standardCurrency,
              pricePrecision: $values.header.pricePrecision
            }

            $queryEngine.request.baseRequest({
              type: 'InqSouOrder',
              action: 'getVendorOrderDetails',
              query: { '*': {} },
              payload: [state.viewRow.orderId],
              tree: true
            }).then((response: any) => {
              $self.value.quoteTableList = response.data
            })
          }
        }
      }),

      'x-data': {
        // 用于弹窗交互
        viewRow: null
      }
    },
    properties: {
      quoteDetailDialogInfo: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          colon: false,
          feedbackLayout: 'terse'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          minColumns: 1,
          maxColumns: 3,
          columnGap: 32
        },
        'x-read-pretty': true,
        properties: {
          // 询价单号
          souNo: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryNo'),
            'x-decorator': 'FormItem'
          },
          // 报价单号
          orderNo: {
            type: 'string',
            title: i18nExpression('bidMod.quoteNo'),
            'x-decorator': 'FormItem'
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            title: i18nExpression('bidMod.vendorCode'),
            'x-decorator': 'FormItem'
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('bidMod.vendorName'),
            'x-decorator': 'FormItem'
          },
          // 报价币种
          standardCurrency: {
            type: 'string',
            title: i18nExpression('bidMod.currency'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'currency'
            }
          },
          // 价格精度
          pricePrecision: {
            type: 'string',
            title: i18nExpression('bidMod.pricePrecision'),
            'x-decorator': 'FormItem'
          }
        }
      },

      // 表格
      quoteTableList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: false,
          sortable: false,
          editMode: true,
          pagination: false
        },
        'x-read-pretty': true,
        properties: generateXindexInOrder({
          // 业务实体
          orgOuName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.affairsEntity'),
              minWidth: 100
            }
          },
          // 库存组织
          orgInvName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.affairsInventoryOrg'),
              minWidth: 100
            }
          },
          // 物料编码
          itemCode: {
            type: 'string',
            // TODO 兼容无料号显示空
            'x-render-table-column': {
              title: i18nExpression('bidMod.itemCode'),
              minWidth: 100
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
          // 单位
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.unit'),
              minWidth: 100
            },
            'x-read-pretty': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'unit'
            }
          },
          // 采购分类
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.purcategoryName'),
              minWidth: 100
            }
          },
          // 组合
          itemGroup: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.affairsCombination'),
              minWidth: 100
            }
          },
          // 预计采购量
          requireQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: '预计采购量',
              minWidth: 100
            }
          },
          // 行类型
          itemType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.itemType'),
              minWidth: 100
            },
            'x-read-pretty': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DMAND_LINE_TYPE'
            }
          },
          // 未税单价
          orderNotaxPrice: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.quotenotaxPrice2'),
              minWidth: 100
            }
          },
          // 未税总金额
          standardNotaxTotalPrice: {
            type: 'string',
            'x-render-table-column': {
              title: '未税总金额',
              minWidth: 100
            }
          },
          // 含税单价
          orderTaxPrice: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.quotetaxPrice2'),
              minWidth: 100
            }
          },
          // 税率
          taxKey: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.taxRate2'),
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'tax'
            }
          },
          // 币种
          orderCurrency: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.currency_price'),
              minWidth: 130
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'currency'
            }
          },
          // 阶梯价类型
          ladderType: {
            type: 'string',
            // 普通式报价才显示
            'x-visible': generateCharExpressionByFunction(({ $values }) => {
              return $values.header?.orderType === 'SIMPLE'
            }),
            'x-render-table-column': {
              title: '阶梯价类型',
              minWidth: 100
            },
            'x-reactions': generateCharFunctionExpression(({ $self, $t }) => {
              $self.value = $self.value === 'standard' ? $t('bidMod.standardladderPrice') : $t('bidMod.sumladderPrice')
            })
          },
          // 阶梯价明细
          isLadder: {
            type: 'string',
            title: '查看阶梯价明细',
            // 普通式报价才显示
            'x-visible': generateCharExpressionByFunction(({ $values }) => {
              return $values.header?.orderType === 'SIMPLE'
            }),
            'x-render-table-column': {
              title: '阶梯价明细',
              minWidth: 125
            },
            'x-component': 'RButton',
            'x-component-props': {
              disabled: generateCharExpressionByFunction(({ $self }) => {
                return $self.value === 'N'
              }),
              type: 'text',
              '@click': generateCharFunctionExpression(({ $table, $self, $getFieldParentFieldFormPath }) => {
                const row = $table.getRowByIndex($self.index)
                // 只触发当前的
                const takeObj: any = $self.query($getFieldParentFieldFormPath($self, 3).concat('ladderPriceDialog')).take()
                takeObj.setComponentProps({ visible: true })
                takeObj.setData({
                  editRow: {
                    souItem: {
                      itemCode: row.itemCode,
                      itemDesc: row.itemDesc,
                      requireQuantity: row.requireQuantity,
                      unit: row.unit
                    },
                    ladderList: row.ladderList
                  },
                  editRowIndex: $self.index
                })
              })
            }
          },
          // 公式报价明细
          isFormula: {
            type: 'void',
            title: '查看公式报价明细',
            // 公式报价才显示
            'x-visible': generateCharExpressionByFunction(({ $values }) => {
              return $values.header?.orderType === 'FORMULA'
            }),
            'x-render-table-column': {
              title: '公式报价明细',
              minWidth: 125
            },
            'x-component': 'RButton',
            'x-component-props': {
              type: 'text',
              // TODO 待实现
              '@click': generateCharFunctionExpression(() => {
                // openFormulaQuotaDialog(row)
              }, true)
            }
          },
          // 备注
          remark: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.remark'),
              minWidth: 100
            }
          }
        })
      },

      // 阶梯价弹窗
      ...LadderPriceSegment({ mode: 'quote', readonly: true })

      // 公式报价
    }
  }
}

export default QuoteDetailDialogSegment
