import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'

const ladderPriceDescribe = ($form:any, $t:any, $getLadderPriceDescribeRow:any) => {
  const row = $getLadderPriceDescribeRow($form)
  return {
    functional: true,
    render: (h: any) => {
      return h('div',
        [
          h('p',
            [
              h('span',
                {
                  style: {
                    'margin-right': '20px'
                  }
                },
                // 物料编码
                $t('bidMod.targetNum') + '：' + row?.materialCode
              ),
              h('span',
                // 物料名称
                $t('bidMod.targetDesc') + '：' + row?.materialName
              )
            ]
          ),
          h('p',
            [
              // 阶梯价类型
              $t('bidMod.ladderType') + '：' + $t('bidMod.standardladderPrice')
            ]
          )
        ]
      )
    }
  }
}

export default {
  type: 'void',
  'x-decorator': 'QueryEngine',
  title: i18nExpression('bidMod.ladderPrice'), // 阶梯价
  'x-component': 'RDialog',
  'x-component-props': {
    'close-on-click-modal': false,
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {
            return [
              {...cancelButton, text: $t('bidMod.backTo')}
            ]
          }`)
  },
  properties: {
    layout: {
      type: 'void',
      properties: {
        ladderPriceDescribe: {
          type: 'void',
          'x-content': '{{ $ladderPriceDescribe($form,$t,$getLadderPriceDescribeRow) }}'
        },
        ladderPrices: {
          type: 'array',
          'x-query-engine-skip': true,
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',
            preColumns: 'seq',
            pagination: false,
            sortable: false
          },
          'x-read-pretty': true,
          properties: generateXindexInOrder({
            beginQuantity: {
              type: 'string',
              title: i18nExpression('bidMod.beginQuantity'), // 数量从（>=）
              'x-render-table-column': {
                minWidth: 100
              }
            },
            endQuantity: {
              type: 'string',
              title: i18nExpression('bidMod.endQuantity'), // 数量至（<）
              'x-render-table-column': {
                minWidth: 100
              }
            },
            unit: {
              type: 'string',
              title: i18nExpression('bidMod.unit'), // 单位
              'x-render-table-column': {
                minWidth: 100
              }
            },
            price: {
              type: 'string',
              title: i18nExpression('bidMod.notaxSelectedPrice'), // 单价（未税）
              'x-render-table-column': {
                minWidth: 100
              }
            }
          })
        }
      }
    }

  }
}

export {
  ladderPriceDescribe
}
