/**
 * @description 供应商查看中标结果
 */
import {
  generateCharFunctionExpression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

const QuoteResultDialogSegment: Record<any, any> = {
  quoteResultDialog: {
    type: 'void',
    title: '查看中标结果',
    'x-component': 'RDialog',
    'x-component-props': {
      class: 'dialogMain',
      size: 'large',
      footer: false,
      '@opened': generateCharFunctionExpression(async ({ $bus, $self, $maxNumberOption }) => {
        $bus.$emit('quoteResultDialogQuery')
        // 编排查询条件
        // @ts-ignore
        $self.query('quoteResultDialogQuery.round').take().setDataSource($maxNumberOption($self.data.viewRow.currentRound))
      })
    },

    'x-data': {
      viewRow: null
    },

    properties: {
      InqSouOrderForVendor: {
        type: 'void',
        'x-query-engine': {
          service: 'sou',
          actions: {
            paginationQuery: {
              immediate: false,
              action: 'listOrderResult',
              // 添加额外查询字段
              transformRequest: generateCharFunctionExpression(({ $form }, data) => {
                console.log(data, 'data')
                data.payload = {
                  ...(data.payload || {}),
                  filter: {
                    ...(data.payload.filter || {}),
                    projectId: {
                      eq: $form.query('quoteResultDialog').get('data').viewRow.projectId
                    }
                  }
                }
                return data
              })
            }
          }
        },
        'x-decorator': 'el-container',
        'x-component': 'QueryEngine',
        'x-decorator-props': {
          class: 'flex-container',
          direction: 'vertical'
        },
        properties: {
          bus: {
            type: 'void',
            'x-component': 'BusEvent',
            'x-component-props': {
              eventName: 'quoteResultDialogQuery',
              '@listener': generateCharFunctionExpression(({ $queryEngine }) => {
                $queryEngine.state.paginationManagement.refresh()
              })
            }
          },

          quoteResultDialogQuery: {
            type: 'object',
            'x-query-engine-skip': true,
            'x-component': 'QueryFormByQueryEngine',
            properties: generateXindexInOrder({
              // 物料编码
              itemCode: {
                type: 'string',
                title: i18nExpression('bidMod.itemCode'),
                'x-query-engine-query-operator': 'contains',
                'x-query-engine-relation': 'projectId',
                'x-query-engine-relation-strict': true,
                'x-component-props': {
                  clearable: true
                }
              },
              // 轮次
              round: {
                type: 'string',
                title: i18nExpression('bidMod.currentRound'),
                'x-component': 'Select'
              }
            })
          },

          quoteResultDialogTable: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              class: 'table-view-vxe-table',
              openCustomTable: false
            },
            'x-read-pretty': true,
            properties: generateXindexInOrder({
              // 轮次
              round: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('bidMod.bidingRound'),
                  minWidth: 140
                }
              },
              // 物料编码
              itemCode: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('bidMod.itemCode'),
                  minWidth: 120
                }
                // FIXME 格式化
                // targetNumReveal
              },
              // 物料名称
              itemDesc: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('bidMod.itemName'),
                  minWidth: 150
                }
              },
              // 本轮入围情况
              winStatus: {
                type: 'string',
                'x-render-table-column': {
                  title: '本轮入围情况',
                  minWidth: 110
                },
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'SOU_WIN_STATUS'
                }
              },
              // 评选情况
              selectStatus: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('bidMod.selectSituation'),
                  minWidth: 110
                },
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'SOU_SELECT_STATUS'
                }
              }
            })
          }
        }
      }
    }
  }
}

export default QuoteResultDialogSegment
