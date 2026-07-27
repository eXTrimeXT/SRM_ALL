/**
 * @description 需求信息
 */
import {
  generateXindexInOrder,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction,
  expression
} from '@meicloud/render-engine'
import {
  cannotLessCurrentTimeSegment,
  requiredFunctionValidatorSegment,
  requiredValidatorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine'
import LadderPriceSegment from 'lib@/compositionEngine/inquiry/ladderPrice'
import TechnicalDocumentsSegment from 'lib@/compositionEngine/sourcing/technicalDocuments'
import { getFormulaValuePreconditions } from 'lib@/compositionEngine/sourcing/composition'
import { formulaHttp } from 'modb@/inquiry/api'

/**
 * 物料变更
 * @param $form
 * @param $message
 * @param row
 * @param value
 */
const $itemCodeChange = ($form: any, $message: any, $t: any, row: any, value: any) => {
  const fullData = $form.query('itemList').take().value || []
  console.log(fullData, 'itemCodeChange fullData')
  if (value && fullData.map((v: any) => v.itemId).includes(value.materialId)) {
    $message.warning('该物料已存在,请勿重复添加!')
    return
  }

  const {
    materialId = '',
    materialCode = '',
    materialName = '',
    unit = '',
    categoryId = '',
    categoryName = ''
  } = value || {}

  row.itemId = materialId
  row.itemCode = materialCode
  row.itemDesc = materialName
  row.unit = unit
  row.categoryId = categoryId
  row.categoryName = categoryName

  $rowHandelFormulaValue(row, true, true, $form, $message, $t)
}

/**
 * 处理单行数据的公式
 * @param row 行数据
 * @param isClear 是否清空公式相关参数
 * @param isOneSet 公式列表只有一个时候是否自动选中
 * @returns {Promise<void>}
 */
const $rowHandelFormulaValue = async (row: any, isClear = true, isOneSet = true, $form: any, $message: any, $t: any) => {
  const data = $form.query('.requireInfoForm').get('data')
  const vxeTable = $form.query('.itemList').take().componentProps.componentInstance.$refs.vxeTable
  console.log(data.isFormula, 'data.isFormula')

  // 是否清空公式值
  if (isClear) {
    row.materialFormulaRelateId = ''
    row.formulaId = ''
    row.formulaValue = ''
    row.formulaName = ''
    row.formulaValueList = []
  }

  // 获取公式报价参数
  const params = getFormulaValuePreconditions({
    orgOuId: row.orgOuId,
    noCodeItem: row.noCodeItem,
    categoryId: row.categoryId,
    itemId: row.itemId
  }, data.isFormula)

  if (params) {
    // 查询公式
    const { data: formulaValueData } = await formulaHttp.getMaterialFormulaRelateInfos([params])

    if (formulaValueData && formulaValueData[params.mapKey][params.valueKey]) {
      row.formulaValueList = formulaValueData[params.mapKey][params.valueKey]
      // 记录查询过公式了
      row.isSetFormulaValueList = true
      // 局部刷新行数据
      await vxeTable.reloadRow(row, row, 'formulaValueList')

      if (isOneSet && row.formulaValueList.length === 1) {
        // 只有一个公式，自动选中
        row.materialFormulaRelateId = row.formulaValueList[0].relateId
        formulaChange(row)
      }
    } else {
      // 没有公式，提示需要维护公式
      $message.warning($t('bidMod.biddingManagementBuyer.warningNoFormulaId'))
    }
  }

  console.log(row, 'rowHandelFormulaValue row')
}

/* 选择公式值 */
const formulaChange = (row: any) => {
  if (!row.materialFormulaRelateId) {
    // 清空选择
    row.formulaValue = ''
  } else {
    // 查找对应的公式值
    const formulaValue = row.formulaValueList.find((item: any) => item.relateId === row.materialFormulaRelateId)
    if (formulaValue) {
      row.formulaValue = formulaValue.formulaValue
      row.formulaName = formulaValue.formulaName
      row.formulaId = formulaValue.formulaId
      row.materialFormulaRelateId = formulaValue.relateId
    }
  }
}

/* 改变报价类型 */
const $orderTypeChange = (val: string, clear = true, $enum: any, itemListTake: any) => {
  const vxeTable = itemListTake.componentProps.componentInstance.$refs.vxeTable
  console.log(vxeTable, 'vxeTable')
  // 公式相关列
  const toggleFormula = (type: string) => {
    vxeTable[type]('materialFormulaRelateId')
    vxeTable[type]('formulaValue')
  }

  // 阶梯价相关列
  const toggleLadder = (type: string) => {
    vxeTable[type]('isLadder')
    vxeTable[type]('ladderList')
  }

  // 报价模板相关列
  const toggleTemplate = (type: string) => {
    vxeTable[type]('templatePrice')
  }

  let reloadItem: any = {}

  // 普通报价
  if (val === $enum.SOU_ORDER_TYPE_ENUM.SIMPLE) {
    // 清空公式相关 和模型报价相关
    reloadItem = {
      materialFormulaRelateId: '',
      formulaId: '',
      formulaValue: '',
      formulaName: '',
      formulaValueList: [],
      // FIXME [ORION] 模板数据
      templatePrice: ''
    }

    // 隐藏公式列
    toggleFormula('hideColumn')
    // 显示阶梯价列
    toggleLadder('showColumn')
    // 隐藏模板选择列
    toggleTemplate('hideColumn')
  }

  // 公式报价
  if (val === $enum.SOU_ORDER_TYPE_ENUM.FORMULA) {
    // 清空物料编码等数据
    reloadItem = {
      noCodeItem: 'N',
      itemId: '',
      itemCode: '',
      itemDesc: '',
      unit: '',
      categoryId: '',
      categoryName: '',
      isLadder: 'N',
      ladderList: [],
      // FIXME [ORION] 模板数据
      templatePrice: ''
    }

    // 显示公式列
    toggleFormula('showColumn')
    // 隐藏阶梯价列
    toggleLadder('hideColumn')
    // 隐藏模板选择列
    toggleTemplate('hideColumn')
  }

  // 报价模板
  if (val === $enum.SOU_ORDER_TYPE_ENUM.TEMPLATE) {
    // 清空公式和阶梯价相关
    reloadItem = {
      materialFormulaRelateId: '',
      formulaId: '',
      formulaValue: '',
      formulaName: '',
      formulaValueList: [],
      isLadder: 'N',
      ladderList: []
    }

    // 隐藏公式列
    toggleFormula('hideColumn')
    // 隐藏阶梯价列
    toggleLadder('hideColumn')
    // 显示模板选择列
    toggleTemplate('showColumn')
  }

  // 重置数据
  if (clear) {
    const { fullData } = vxeTable.getTableData()
    console.log(fullData, 'fullData')
    vxeTable.reloadData(fullData.map((item: any) => {
      return {
        ...item,
        ...reloadItem
      }
    }))
  }
}

const RequireInfoSegment: Record<any, any> = {
  // 表单
  requireInfoForm: {
    type: 'void',
    'x-decorator': 'FormLayout',
    'x-decorator-props': {
      colon: false,
      layout: 'vertical',
      feedbackLayout: 'terse'
    },
    'x-component': 'FormGrid',
    'x-component-props': {
      maxColumns: 4,
      columnGap: 32,
      maxWidth: 300,
      strictAutoFit: true
    },
    'x-data': {
      isFormula: false, // 是否是公式报价
      isNormal: false, // 是否是普通报价
      selectFormulaValueList: [] // 选择公式enum
    },
    properties: {
      // 报价类型
      orderType: {
        type: 'string',
        default: 'SIMPLE',
        title: i18nExpression('bidMod.pricingType'),
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-reactions': generateCharFunctionExpression(({ $self }) => {
          const data = $self.query('.requireInfoForm').get('data')
          data.isNormal = $self.value === 'SIMPLE'
          data.isFormula = $self.value === 'FORMULA'
        }),
        'x-component-props': {
          code: 'SOU_ORDER_TYPE',
          filterItem: ['TEMPLATE'],
          '@change': generateCharFunctionExpression(({ $form, $self, $enum }, value) => {
            console.log(value, 'orderType')
            const itemListTake = $form.query('itemList').take()
            $orderTypeChange(value, true, $enum, itemListTake)
          })
        },
        ...requiredValidatorSegment
      }
    }
  },

  // 按钮
  toolbar: {
    type: 'void',
    'x-component': 'Space',
    'x-component-props': {
      style: 'margin-bottom: 16px'
    },
    properties: {
      // 需求行数据
      addTips: {
        type: 'void',
        'x-component': 'span',
        'x-content': i18nExpression('bidMod.requireInfoLine'),
        'x-component-props': {
          style: 'margin-right: 10px;'
        }
      },
      // 新增
      add: {
        type: 'void',
        title: i18nExpression('bidMod.addItem'),
        'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
        'x-disabled': generateCharExpressionByFunction(({ $form }) => !$form.values.orderType),
        'x-component': 'RButton',
        'x-component-props': {
          type: 'primary',
          '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
            $self.query($getFieldParentFieldFormPath($self, 2).concat('itemList'))
              .take(field => {
                field.componentProps.componentInstance.addRow('push', { 'inqSouItem.itemType': 'STANDARD' })
              })
          })
        }
      }
    }
  },

  // 表格
  itemList: {
    type: 'array',
    'x-decorator': 'FormItem',
    'x-component': 'RenderTable',
    'x-component-props': {
      class: 'table-view-vxe-table',
      preColumns: 'seq',
      pagination: false,
      sortable: false,
      editMode: true
    },
    'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
    ...requiredFunctionValidatorSegment(i18nExpression('cusEntry.supplement20250211.plaintextenterMaterialInfoFirst')), // '请先录入物料信息!'
    properties: generateXindexInOrder({
      // 业务实体
      orgOuId: {
        type: 'string',
        title: i18nExpression('bid_mod.businessEntity'),
        'x-render-table-column': {
          width: 150
        },
        'x-component': 'OrganizationSelector',
        'x-component-props': {
          'read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
          nodeType: 'OU',
          parentId: -1,
          placeholder: i18nExpression('common.pleaseSelect'),
          clearable: false,
          // 来源需求池 && orgOuId 存在 不允许编辑
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!$self.value && !!$table.getRowByIndex($self.index)?.sourceFromLineId
          }),
          scope: generateCharExpressionByFunction(({ $table, $self }) => $table.getRowByIndex($self.index)),
          '@select': generateCharFunctionExpression(({ $table, $self, $form, $message, $t }, val: any) => {
            const row = $table.getRowByIndex($self.index)
            const { organizationId = '', organizationCode = '', organizationName = '' } = val || {}

            if (val && row.orgOuId === organizationId) {
              // 避免重复执行
              return
            }

            row.orgOuId = organizationId
            row.orgOuCode = organizationCode
            row.orgOuName = organizationName
            // 清空库存组织
            row.orgInvId = ''
            row.orgInvCode = ''
            row.orgInvName = ''

            // 重选库存组织，清空物料选择
            if (row.noCodeItem !== 'Y' && row.itemId) {
              // 非无料号
              $itemCodeChange($form, $message, $t, row, null)
            } else {
              $rowHandelFormulaValue(row, true, true, $form, $message, $t)
            }
          })
        },
        ...requiredFunctionValidatorSegment(i18nExpression('dataConfMod.msgPleaseSelectOrg'))  // '请选择业务实体!'
      },

      // 库存组织
      orgInvId: {
        type: 'string',
        title: i18nExpression('bid_mod.inv'),
        'x-render-table-column': {
          width: 150
        },
        'x-component': 'OrganizationSelector',
        'x-component-props': {
          'read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
          scope: generateCharExpressionByFunction(({ $table, $self }) => $table.getRowByIndex($self.index)),
          nodeType: 'INV',
          clearable: false,
          placeholder: i18nExpression('common.pleaseSelect'),
          parentId: generateCharExpressionByFunction(({ $table, $self }) => $table.getRowByIndex($self.index)?.orgOuId || ''),
          // 来源需求池 && orgInvId存在 不允许编辑
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!$self.value && !!$table.getRowByIndex($self.index)?.sourceFromLineId
          }),
          '@select': generateCharFunctionExpression(({ $table, $self, $form, $message, $t }, val: any) => {
            const row = $table.getRowByIndex($self.index)
            const { organizationId = '', organizationCode = '', organizationName = '' } = val || {}

            if (val && row.orgInvId === organizationId) {
              // 避免重复执行
              return
            }

            row.orgInvId = organizationId
            row.orgInvCode = organizationCode
            row.orgInvName = organizationName

            // 重选库存组织，清空物料选择
            if (row.noCodeItem !== 'Y' && row.itemId) {
              // 非无料号
              $itemCodeChange($form, $message, $t, row, null)
            }
          })
        },
        ...requiredFunctionValidatorSegment(i18nExpression('dataConfMod.msgPSelectOrgza'))  // '请选择库存组织!'
      },

      // 是否无料号寻源
      noCodeItem: {
        type: 'string',
        default: 'N',
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-component': 'Checkbox',
        'x-component-props': {
          trueLabel: 'Y',
          falseLabel: 'N',
          // 来源需求池不允许编辑
          disabled: generateCharExpressionByFunction(({ $table, $self, $readonly }) => {
            return !!$table.getRowByIndex($self.index).sourceFromLineId || $readonly
          }),
          '@change': generateCharFunctionExpression(({ $table, $self }) => {
            const row = $table.getRowByIndex($self.index)
            console.log(row, 'noCodeItem')
            // 无料号，清空原有选的料号相关数据
            row.itemId = ''
            row.itemCode = ''
            row.itemDesc = ''
            row.categoryName = ''
            row.formulaId = ''
            row.materialFormulaRelateId = ''
            row.formulaValue = ''
            row.formulaValueList = []
          })
        },
        'x-render-table-column': {
          width: 130,
          title: '是否无料号寻源'
        }
      },

      // 物料编码
      itemCode: {
        type: 'string',
        title: i18nExpression('bidMod.itemCode'),
        'x-render-table-column': {
          width: 150
        },
        'x-component': 'QuickSearchWrapper',
        'x-component-props': {
          name: 'scc_base_material_item_inv_enable',
          showKey: 'materialCode',
          // 无料号不允许输入
          'read-pretty': generateCharExpressionByFunction(({ $table, $self, $readonly }) => {
            return ($table.getRowByIndex($self.index) && $table.getRowByIndex($self.index).noCodeItem === 'Y') || $readonly
          }),
          // 必须要选了业务实体和库存组织才能使用下拉搜索
          disabledSelect: generateCharExpressionByFunction(({ $table, $self }) => {
            return !$table.getRowByIndex($self.index).orgOuId || !$table.getRowByIndex($self.index).orgInvId
          }),
          // 来源需求池不允许编辑
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!$table.getRowByIndex($self.index).sourceFromLineId
          }),
          preQueryData: {
            'scboa.ORGANIZATION_ID': '{{$table.getRowByIndex($self.index).orgInvId}}'
          },
          '@close-quicksearch': generateCharFunctionExpression(({ $table, $self, $form, $message, $t }, value) => {
            const row = $table.getRowByIndex($self.index)
            $itemCodeChange($form, $message, $t, row, value)
          }),
          '@before-open': generateCharFunctionExpression(({ $table, $self, $message }, _, callback) => {
            const row = $table.getRowByIndex($self.index)
            if (!row.orgOuId || !row.orgInvId) {
              $message.warning(i18nExpression('bidMod.selectOuAndOrgMsg'))  // '请先选择业务实体以及库存组织'
              callback(null)
            }
          })
        }
      },

      // 物料描述
      itemDesc: {
        type: 'string',
        title: i18nExpression('bidMod.itemDesc'),
        maxLength: 100,
        'x-render-table-column': {
          minWidth: 150
        },
        // 无料号才允许输入
        'x-read-pretty': generateCharExpressionByFunction(({ $table, $self }) => {
          return $table.getRowByIndex($self.index) && $table.getRowByIndex($self.index).noCodeItem === 'N'
        }),
        'x-component-props': {
          // 来源需求池不允许编辑
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!$table.getRowByIndex($self.index).sourceFromLineId
          })
        }
      },

      // 物料分类
      categoryName: {
        type: 'string',
        title: i18nExpression('bidMod.categoryName'),
        'x-render-table-column': {
          width: 150
        },
        'x-component': 'QuickSearchWrapper',
        'x-component-props': {
          'read-pretty': generateCharExpressionByFunction(({ $table, $self }) => {
            return $table.getRowByIndex($self.index)?.noCodeItem === 'N'
          }),
          disabled: false,
          name: 'scc_base_purchase_category2',
          showKey: 'categoryName',
          clearable: true,
          '@close-quicksearch': generateCharFunctionExpression(({ $table, $self, $form, $message, $t }, value) => {
            const row = $table.getRowByIndex($self.index)

            row.categoryCode = value.categoryCode || ''
            row.categoryName = value.categoryName || ''
            row.categoryId = value.categoryId || ''

            $rowHandelFormulaValue(row, true, true, $form, $message, $t)
          })
        }
      },

      // 组合
      itemGroup: {
        type: 'string',
        title: i18nExpression('bidMod.itemGroup'),
        maxLength: 30,
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-render-table-column': {
          minWidth: 150
        },
        'x-component': 'Input',
        'x-component-props': {
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!($table.getRowByIndex($self.index)?.sourceFromLineId)
          })
        }
      },

      // 行类型
      'inqSouItem.itemType': {
        type: 'string',
        title: i18nExpression('bidMod.itemType'),
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-render-table-column': {
          minWidth: 120
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'DMAND_LINE_TYPE',
          clearable: true
        },
        ...requiredFunctionValidatorSegment(i18nExpression('cusEntry.supplement20250211.selectRowType'))  // '请选择行类型!'
      },

      // 预计数量
      requireQuantity: {
        type: 'string',
        title: i18nExpression('bidMod.demandQuantity'),
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-render-table-column': {
          minWidth: 100
        },
        'x-component-props': {
          // FIXME 只能输入数字
          // 来源需求池不允许变更
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return !!$table.getRowByIndex($self.index)?.sourceFromLineId
          })
        },
        ...requiredFunctionValidatorSegment(i18nExpression('cusEntry.supplement20250211.expectedPurchaseQuantity'))  // '请录入预计采购量!'
      },

      // 单位
      unit: {
        type: 'string',
        title: i18nExpression('bidMod.unit'),
        // 无料号寻源才允许编辑
        'x-read-pretty': generateCharExpressionByFunction(({ $table, $self, $readonly }) => {
          return $readonly || $table.getRowByIndex($self.index)?.noCodeItem === 'N'
        }),
        'x-render-table-column': {
          minWidth: 100
        },
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'unit',
          clearable: true,
          disabled: false
        }
      },

      // 是否阶梯报价
      isLadder: {
        type: 'string',
        default: 'N',
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return $form.values.orderType === 'SIMPLE'
        }),
        'x-component': 'Checkbox',
        'x-component-props': {
          trueLabel: 'Y',
          falseLabel: 'N'
        },
        'x-render-table-column': {
          disabled: false,
          width: 115,
          title: i18nExpression('bidMod.isLadder')
        }
      },

      // 阶梯价报价
      ladderList: {
        type: 'void',
        title: i18nExpression('bidMod.ladderQuote'),
        // 普通报价才有
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return $form.values.orderType === 'SIMPLE'
        }),
        'x-component': 'TableButton',
        'x-component-props': {
          type: 'text',
          title: i18nExpression('bidMod.ladderPrice'),
          // 勾选阶梯价才允许点击
          disabled: generateCharExpressionByFunction(({ $table, $self }) => {
            return $table.getRowByIndex($self.index)?.isLadder === 'N'
          }),
          '@click': generateCharFunctionExpression(({ $self, $table, $getFieldParentFieldFormPath }) => {
            const row = $table.getRowByIndex($self.index)
            // 只触发当前的
            const takeObj: any = $self.query($getFieldParentFieldFormPath($self, 3).concat('ladderPriceDialog')).take()
            takeObj.setData({
              editRow: row,
              editRowIndex: $self.index
            })
            takeObj.setComponentProps({ visible: true })
          })
        },
        'x-render-table-column': {
          width: 100,
          customRender: true
        }
      },

      // 选择公式 materialFormulaRelateId
      'inqSouItem.materialFormulaRelateId': {
        type: 'string',
        title: '选择公式',
        'x-component': 'Select',
        // 公式报价才有
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return $form.values.orderType === 'FORMULA'
        }),
        'x-render-table-column': {
          minWidth: 125
        },
        'x-reactions': generateCharFunctionExpression(async ({ $form, $table, $self, $message, $t }, field) => {
          const row = $table.getRowByIndex($self.index)
          console.log(row, 'materialFormulaRelateId  row')
          const data = $form.query('.requireInfoForm').get('data')
          if (row && data.isFormula) {
            // 单行公式查询处理
            await $rowHandelFormulaValue(row, true, true, $form, $message, $t)
            console.log(row.formulaValueList, 'formulaValueList')
            const options = row.formulaValueList.map((item: any, index: number) => {
              return {
                key: item.relateId + index,
                label: item.formulaName,
                value: item.relateId
              }
            })
            console.log(options, 'options')

            $self.setDataSource(options)
          }
        }),
       // enum: expression('$form.query("requireInfoForm").get("data").selectFormulaValueList'),
        'x-component-props': {
          disabled: false,
          options: [{ label: '1', value: 2 }],
          '@change': generateCharFunctionExpression((_, val) => {
            // TODO 待实现
            console.log(val)
          }, true)
        }
      },

      // 公式值
      formulaValue: {
        type: 'string',
        title: i18nExpression('bid_mod.formulaValue'),
        // 公式报价才有
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return $form.values.orderType === 'FORMULA'
        }),
        'x-read-pretty': true,
        'x-render-table-column': {
          minWidth: 125
        }
      },

      // TODO 暂时屏蔽 模版报价
      // templatePrice: {
      //   type: 'string',
      //   title: i18nExpression('templatePrice.label'),
      //   'x-render-table-column': {
      //     width: 160
      //   },
      //   'x-component': 'QuickSearch',
      //   'x-component-props': {
      //     name: 'sou_quote_temp',
      //     btnTitle: i18nExpression('templatePrice.select'),
      //     btnType: 'text',z
      //     showButton: true,
      //     multiSelect: true,
      //     confirmAutoClose: false,
      //     preQueryData: { 't.temp_status': 'VALID' }
      //     // 'close-quicksearch': 'writeBackContract'
      //   }
      // },

      // 定价开始日期
      priceStartTime: {
        title: i18nExpression('bidMod.fixedPriceBegin'),
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        ...yearMonthDayHourMinuteSecondSelectorSegment,
        'x-render-table-column': {
          width: 180
        },
        'x-component-props': {
          ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
          ...cannotLessCurrentTimeSegment,
          formatter: expression(`({ cellValue, row, column }) => {
            parseTime(row.priceStartTime, '{y}-{m}-{d} {h}:{i}:{s}')
         }`)
        },
        ...requiredFunctionValidatorSegment(i18nExpression('cusEntry.supplement20250211.pricingStartDate'))  // '请选择定价开始日期!'
      },

      // 定价结束日期
      priceEndTime: {
        title: i18nExpression('bidMod.fixedPriceEnd'),
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        ...yearMonthDayHourMinuteSecondSelectorSegment,
        'x-render-table-column': {
          width: 180
        },
        'x-component-props': {
          ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
          ...cannotLessCurrentTimeSegment,
          formatter: expression(`({ cellValue, row, column }) => {
            parseTime(row.priceEndTime, '{y}-{m}-{d} {h}:{i}:{s}')
         }`)
        },
        ...requiredFunctionValidatorSegment(i18nExpression('cusEntry.supplement20250211.selectPricingEndDate'))  // '请选择定价结束日期!'
      },

      // 技术文件
      itemFiles: {
        type: 'void',
        title: generateCharExpressionByFunction(({ $t, $readonly }) => $t($readonly ? 'common.view' : 'common.select')),
        'x-component': 'RButton',
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
          width: 100,
          customRender: true
        }
      },

      // 备注
      remark: {
        type: 'string',
        title: i18nExpression('common.remark'),
        'x-read-pretty': generateCharExpressionByFunction(({ $readonly }) => $readonly),
        'x-render-table-column': {
          minWidth: 150
        },
        maxLength: 300
      },

      // 操作
      operation: {
        type: 'void',
        title: i18nExpression('common.operation'),
        'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
        'x-render-table-column': {
          width: 80,
          fixed: 'right'
        },
        'x-component': 'RenderTableButtonList',
        properties: {
          // 删除
          delete: {
            type: 'void',
            title: i18nExpression('common.delete'),
            // 来源需求池不允许编辑
            'x-visible': generateCharExpressionByFunction(({ $table, $self }) => {
              return !$table.getRowByIndex($self.index).sourceFromLineId
            }),
            'x-component-props': {
              '@click': generateCharFunctionExpression(({ $table, $self }) => {
                $table.remove($self.index)
              })
            }
          }
        }
      }
    })
  },

  // 阶梯价弹窗
  ...LadderPriceSegment(),

  // 技术文件弹窗
  ...TechnicalDocumentsSegment()
}

export default RequireInfoSegment

export const requireInfo = {
  $itemCodeChange,
  $orderTypeChange,
  $rowHandelFormulaValue
}
