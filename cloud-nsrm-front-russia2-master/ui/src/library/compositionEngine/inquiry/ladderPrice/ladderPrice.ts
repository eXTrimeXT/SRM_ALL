/**
 * @description 阶梯价
 */
import {
  i18nExpression,
  generateXindexInOrder,
  generateCharFunctionExpression,
  expression, generateCharReactionExpression
} from '@meicloud/render-engine'

interface LadderPriceProps {
  // 是否显示行详情
  showInfo?: boolean;
  // 只读
  readonly?: boolean;
  // 模式 [set设置, quote报价]
  mode?: 'set' | 'quote';
}

/**
 * 保存阶梯价，需要在页面入口作用域注入该方法，如果没用到保存功能就不需要注入
 * @param $form
 * @param $self
 * @param $getFieldParentFieldFormPath
 * @param $message
 * @param done
 */
const saveQuoteOrLadderPrice = ($form: any, $self: any, $getFieldParentFieldFormPath: any, $message: any, done: any) => {
  const {
    ladderPriceForm,
    ladderList
  } = $self.value

  if ($self.data.mode === 'quote') {
    // 报价
    for (const [index, item] of ladderList.entries()) {
      if (!item.orderNotaxPrice) {
        // 第${index + 1}行 请输入未税单价
        $message.warning(i18nExpression('common.theNum') + (index + 1) + i18nExpression('common.row') + i18nExpression('bidMod.common.ladderMsg8'))
        return
      }
    }

    $form.values.itemList[$self.data.editRowIndex].ladderPriceList = ladderList.concat()
    $form.values.itemList[$self.data.editRowIndex].orderNotaxPrice = ladderList[0].orderNotaxPrice
    $form.values.itemList[$self.data.editRowIndex].orderTaxPrice = ladderList[0].orderTaxPrice

    // 清空弹窗数据
    $self.value.ladderPriceForm = {}
    $self.value.ladderList = []
    done()
    return
  }

  // 立项
  if (!$self.value.ladderPriceForm.ladderType) {
    // 请选择阶梯价类型!
    $message.warning(i18nExpression('bidMod.common.ladderMsg1'))
    return
  }

  if (ladderList.length === 0) {
    // 请设置阶梯报价!
    $message.warning(i18nExpression('bidMod.common.ladderMsg2'))
    return
  }

  for (const [index, item] of ladderList.entries()) {
    const [beginQuantity, endQuantity] = [item.beginQuantity, item.endQuantity]

    if (beginQuantity === 0 || endQuantity === 0) {
      // 第${index + 1}行 数量不为0！
      $message.warning(i18nExpression('common.theNum') + (index + 1) + i18nExpression('common.row') + i18nExpression('bidMod.common.ladderMsg3'))
      return
    }

    if (
      !beginQuantity ||
      // 最后一行的至允许为空
      (!endQuantity && index !== (ladderList.length - 1))
    ) {
      // 第${index + 1}行 请输入[数量从]和[数量至]!
      $message.warning(i18nExpression('common.theNum') + (index + 1) + i18nExpression('common.row') + i18nExpression('bidMod.common.ladderMsg4'))
      return
    }

    if (beginQuantity && endQuantity && Number(beginQuantity) > Number(endQuantity)) {
      // 第${index + 1}行 [数量从]不能大于[数量至]!
      $message.warning(i18nExpression('common.theNum') + (index + 1) + i18nExpression('common.row') + i18nExpression('bidMod.common.ladderMsg5'))
      return
    }

    if (index !== 0 && beginQuantity !== ladderList[index - 1].endQuantity) {
      // 第${index + 1}行 阶梯价不允许断层报价，接下来的一层只能从上一层的终点数量开始算!
      $message.warning(i18nExpression('common.theNum') + (index + 1) + i18nExpression('common.row') + i18nExpression('bidMod.common.ladderMsg6'))
      return
    }
  }

  // 阶梯报价未包含预计采购量区间
  const quantity = ladderPriceForm.requireQuantity
  const endQuantity = ladderList[ladderList.length - 1].endQuantity
  if (
    endQuantity &&
    (
      (Number(quantity) < Number(ladderList[0].beginQuantity)) ||
      (Number(quantity) >= Number(endQuantity))
    )
  ) {
    // 阶梯报价未包含预计采购量区间!
    $message.warning(i18nExpression('bidMod.common.ladderMsg7'))
    return
  }

  // 立项
  $form.values.inqItemList[$self.data.editRowIndex].ladderType = ladderPriceForm.ladderType
  $form.values.inqItemList[$self.data.editRowIndex].ladderList = ladderList.concat()

  // 清空弹窗数据
  $self.value.ladderPriceForm = {}
  $self.value.ladderList = []
  done()
}

export default function (props?: LadderPriceProps): Record<any, any> {
  const {
    showInfo = true,
    readonly = false,
    mode = 'set'
  } = props || {}

  const selfReadonly = (flag?: boolean) => expression(`${flag ? '!' : ''}(${readonly} || $readonly)`)

  const isQuote = () => mode === 'quote'

  return {
    ladderPriceDialog: {
      type: 'object',
      title: i18nExpression('bidMod.ladderPrice'),
      'x-component': 'RDialog',
      'x-component-props': {
        class: 'dialogMain',
        size: 'large',
        footer: selfReadonly(true),
        beforeClose: generateCharFunctionExpression(({ $form, $self, $getFieldParentFieldFormPath, $message, $saveQuoteOrLadderPrice }, done, type) => {
          if (!type || type === 'cancel') {
            done()
            return
          }

          // 执行保存
          $saveQuoteOrLadderPrice($form, $self, $getFieldParentFieldFormPath, $message, done)
        }),

        '@opened': generateCharFunctionExpression(({ $self }) => {
          const state = $self.data
          if (state.editRow) {
            $self.value.ladderPriceForm = {
              itemCode: state.editRow.itemCode,
              itemDesc: state.editRow.itemDesc,
              requireQuantity: state.editRow.requireQuantity,
              ladderType: state.editRow.ladderType || 'standard'
            }
            const list = state.mode === 'quote' ? state.editRow.ladderPriceList : state.editRow.ladderList
            $self.value.ladderList = (list || []).map((item: any) => {
              return {
                ...item,
                // 写入单位
                unit: state.editRow.unit
              }
            })
          }
        })
      },

      'x-data': {
        // 用于弹窗交互
        editRow: null,
        editRowIndex: -1,
        // 如果只读，不需要设置这个，这个mode用于数据交互，跟props.mode同步
        mode: 'set'
      },

      properties: {
        // 展示表单
        ladderPriceForm: {
          type: 'object',
          'x-visible': showInfo,
          'x-decorator': 'FormLayout',
          'x-decorator-props': {
            colon: false,
            layout: 'vertical'
          },
          'x-component': 'FormGrid',
          'x-component-props': {
            minColumns: 1,
            maxColumns: 3,
            columnGap: 32,
            rowGap: 0,
            colWrap: true
          },
          'x-read-pretty': true,
          properties: {
            // 物料编码
            itemCode: {
              type: 'string',
              title: i18nExpression('bidMod.itemCode'),
              'x-decorator': 'FormItem'
              // TODO 处理无料号
            },
            // 物料名称
            itemDesc: {
              type: 'string',
              title: i18nExpression('bidMod.itemName'),
              'x-decorator': 'FormItem'
            },
            // 预计数量
            requireQuantity: {
              type: 'string',
              title: i18nExpression('bidMod.demandQuantity'),
              'x-decorator': 'FormItem'
            },
            // 阶梯价类型
            ladderType: {
              type: 'string',
              title: i18nExpression('bidMod.ladderType'),
              'x-read-pretty': selfReadonly() && isQuote(),
              'x-decorator': 'FormItem',
              'x-component': 'Radio.Group',
              enum: [
                {
                  label: i18nExpression('bidMod.standardladderPrice'),
                  value: 'standard'
                },
                {
                  label: i18nExpression('bidMod.sumladderPrice'),
                  value: 'sum'
                }
              ]
            }
          }
        },

        toolbar: {
          type: 'void',
          'x-visible': selfReadonly(true) && !isQuote(),
          'x-component': 'Space',
          'x-component-props': {
            style: 'margin-bottom: 16px'
          },
          properties: {
            // 新增
            add: {
              type: 'void',
              title: i18nExpression('common.add'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('ladderList'))
                    .take(field => {
                      const takeObj: any = $self.query($getFieldParentFieldFormPath(field, 2).concat('ladderPriceDialog')).take()
                      const ladderList = takeObj.value.ladderList

                      field.componentProps.componentInstance.addRow('push', {
                        beginQuantity: ladderList.length ? ladderList[ladderList.length - 1].endQuantity : '',
                        unit: takeObj.data.editRow.unit
                      })
                    })
                })
              }
            }
          }
        },

        // 表格
        ladderList: {
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
          'x-read-pretty': selfReadonly() && isQuote(),
          properties: generateXindexInOrder({
            // 数量从（>=）
            beginQuantity: {
              type: 'string',
              'x-read-pretty': selfReadonly() && isQuote(),
              // TODO 数字输入
              'x-render-table-column': {
                title: i18nExpression('bidMod.beginQuantity'),
                minWidth: 150
              }
            },
            // 数量至（<）
            endQuantity: {
              type: 'string',
              'x-read-pretty': selfReadonly() && isQuote(),
              // TODO 数字输入
              'x-render-table-column': {
                title: i18nExpression('bidMod.endQuantity'),
                minWidth: 150
              }
            },
            // 单位
            unit: {
              type: 'string',
              'x-read-pretty': true,
              'x-render-table-column': {
                title: i18nExpression('bidMod.unit'),
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'unit'
              }
            },
            // 未税单价
            orderNotaxPrice: {
              type: 'string',
              'x-visible': isQuote(),
              'x-read-pretty': selfReadonly(),
              // TODO 数字输入 digits: pricePrecision
              'x-render-table-column': {
                title: i18nExpression('bidMod.quotenotaxPrice2'),
                minWidth: 100
              }
            },
            // 含税单价
            orderTaxPrice: {
              type: 'string',
              'x-visible': isQuote(),
              'x-read-pretty': true,
              'x-render-table-column': {
                title: i18nExpression('bidMod.quotetaxPrice2'),
                minWidth: 100
              },
              'x-reactions': generateCharReactionExpression(({ $table, $self, $getFieldParentFieldFormPath, $readonly, $bigCalcTaxPrice }) => {
                $self.query($getFieldParentFieldFormPath($self, 3).concat('ladderPriceDialog'))
                  .take(field => {
                    const row = $table.getRowByIndex($self.index)
                    if (row && field.data.editRow.taxRate && !$readonly) {
                      // FIXME 价格精度取币种的精度，这里先写死2位
                      row.orderTaxPrice = $bigCalcTaxPrice(row.orderNotaxPrice, field.data.editRow.taxRate, 2)
                    }
                  })
              })
            },
            operation: {
              type: 'void',
              'x-render-table-column': {
                title: i18nExpression('common.operation'),
                width: 150,
                fixed: 'right'
              },
              'x-visible': selfReadonly(true) && !isQuote(),
              'x-component': 'RenderTableButtonList',
              properties: {
                // comment
                delete: {
                  type: 'void',
                  title: i18nExpression('common.delete'),
                  'x-component-props': {
                    '@click': generateCharFunctionExpression(({ $table, $self }) => {
                      $table.remove($self.index)
                    })
                  }
                }
              }
            }
          })
        }
      }
    }
  }
}

export {
  saveQuoteOrLadderPrice
}
