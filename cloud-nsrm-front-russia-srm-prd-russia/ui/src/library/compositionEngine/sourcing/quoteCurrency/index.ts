/**
 * @description 商务信息币种配置组件
 */
import $dayjs from 'dayjs'
import {
  expression, generateCharExpressionByFunction,
  generateCharFunctionExpression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  editTableFormItemValid,
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { purchase } from '@/service/modules/base'

type QuoteCurrencyProps = {
  // 是否供应商查看
  isVendorView?: boolean
}

// interface GetRateByCodeProps {
//   $form: any;
//   row: any;
//   value: any
// }

//  选择一个外币，查询税率，对外提供，注入作用域钟
const getRateByCode = async ($form: any, $message: any, row: any, fromCode: any) => {
  console.log($form, row, fromCode)
  if (!fromCode) {
    // 清空
    row.priceTax = ''
    return
  }
  // 获取本位币
  const standardCurrency = $form.values?.standardCurrency
  if (standardCurrency === fromCode) {
    // 选了本位币
    row.priceTax = 1
    return
  }

  const paramData = {
    toCurrencyCode: standardCurrency,
    fromCurrencyCode: fromCode,
    rateType: $form.values.exchangeRateType,
    exchangeDate: $dayjs($form.values.inqSouProject.currencyExchangeDate).format('YYYY-MM-DD')
  }
  console.log(paramData)
  const data = await purchase.purchaseExchangeRate(paramData)
  const list = data.data.list

  if (data && data.data && Array.isArray(list)) {
    if (list.length === 0) {
      $message.warning(`${fromCode} TO ${standardCurrency} 没有配置日期${paramData.exchangeDate}的汇率!`)
      row.priceTax = ''
      return
    }
    // 取第一个
    row.priceTax = list[0].priceTax || ''
  }
}

export default function (props?: QuoteCurrencyProps): Record<any, any> {
  const { isVendorView = false } = props || {}

  // 当前只读态
  const selfReadonly = (flag?: boolean) => isVendorView ? !flag : expression(`${flag ? '!' : ''}$readonly`)

  return {
    // 表单
    quoteCurrencyForm: {
      type: 'void',
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 3,
        columnGap: 32,
        rowGap: 0
      },
      'x-read-pretty': selfReadonly(),
      properties: {
        // 本位币
        standardCurrency: {
          type: 'string',
          title: i18nExpression('bid_mod.standardCurrency'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'currency'
          },
          ...requiredValidatorSegment
        },
        // 价格精度
        pricePrecision: {
          type: 'string',
          title: i18nExpression('bid_mod.pricePrecision'),
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PRICE_PRECISION'
          },
          ...requiredValidatorSegment
        },
        // 汇率类型
        'inqSouProject.exchangeRateType': {
          type: 'string',
          title: i18nExpression('bid_mod.exchangeRateType'),
          'x-visible': !isVendorView,
          'x-decorator': 'FormItem',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'EXCHANGE_RATE_TYPE'
          },
          ...requiredValidatorSegment
        },
        // 币种转换日期
        'inqSouProject.currencyExchangeDate': {
          type: 'date',
          title: i18nExpression('bid_mod.currencyChangeDate'),
          'x-visible': !isVendorView,
          'x-decorator': 'FormItem',
          'x-component-props': {
            placeholder: i18nExpression('common.pleaseSelectDate'),
            type: 'datetime',
            format: 'yyyy-MM-dd HH:mm:ss',
            'value-format': 'yyyy-MM-dd HH:mm:ss'
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
        // 新增
        add: {
          type: 'void',
          title: i18nExpression('common.add'),
          'x-visible': selfReadonly(true),
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            style: 'margin-right: 10px;',
            '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
              $self.query($getFieldParentFieldFormPath($self, 2).concat('currencyList'))
                .take(field => {
                  field.componentProps.componentInstance.addRow()
                })
            })
          },
          // 需要选了本位币 价格精度 币种转换日期
          'x-reactions': {
            dependencies: ['standardCurrency', 'inqSouProject.exchangeRateType', 'inqSouProject.currencyExchangeDate'],
            fulfill: {
              state: {
                'component[1].disabled': generateCharExpressionByFunction(({ $deps }) => !$deps[0] || !$deps[1] || !$deps[2])
              }
            }
          }
        },
        // 以下是投标人可以使用的外币清单
        addTips: {
          type: 'void',
          'x-component': 'span',
          'x-content': i18nExpression('bid_mod.quoteCurrencyExplain')
        }
      }
    },
    // 表格
    currencyList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        editMode: selfReadonly(true)
      },
      properties: generateXindexInOrder({
        // 币种
        currencyCode: {
          type: 'string',
          title: i18nExpression('bid_mod.currencyName'),
          'x-render-table-column': {
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'currency',
            '@change': generateCharFunctionExpression(({ $form, $table, $self, $message, $getRateByCode }) => {
              const row = $table.getRowByIndex($self.index)
              $getRateByCode($form, $message, row, row.currencyCode)
            })
          }
        },
        // 汇率
        'inqSouCurrency.priceTax': {
          type: 'string',
          title: i18nExpression('bid_mod.priceTax'),
          'x-render-table-column': {
            minWidth: 100
          },
          'x-editable': false,
          ...editTableFormItemValid
        },
        // 价格精度
        pricePrecision: {
          type: 'string',
          title: i18nExpression('bid_mod.pricePrecision'),
          'x-render-table-column': {
            minWidth: 100
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'PRICE_PRECISION'
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-visible': selfReadonly(true),
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

export {
  getRateByCode
}
