import {
    expression,
    i18nExpression,
    generateXindexInOrder
} from '@meicloud/render-engine'

export default {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('route.pollingSource2') // 寻源需求单
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
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              'disabled': expression('!$form.values.vendorId'),
              '@click': expression('() =>  $addSourcingItem($form)')
            }
          }
        }
      },
      reviewRelations: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          maxHeight: 400,
          preColumns: 'seq',
          pagination: false,
          sortable: false,
          // 联表主键的 key
          primaryKey: 'relationId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'reviewRelations:*',
        properties: generateXindexInOrder({
          relationId: {
            type: 'string',
            'x-hidden': true

          },
          reqHeadNo: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              disabled: false,
              type: 'text',
              '@click': expression('({ row }) => $readSourcing(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('sourcingBuyer.reqHeadNo'), // 寻源单号
              minWidth: 120,
              customRender: true
            }
          },
          souReqTitile: {
            type: 'string',
            title: i18nExpression('sourcingBuyer.souReqTitile'), // 需求标题
            'x-render-table-column': {
              minWidth: 100
            }
          },
          auditStatus: {
            type: 'string',
            title: i18nExpression('sourcingBuyer.status'), // 状态
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            }
          },
          expirationTime: {
            type: 'string',
            title: i18nExpression('sourcingBuyer.expirationTime'), // 截止时间
            'x-render-table-column': {
              minWidth: 100
            }
          },
          reviewCreatedBy: {
            type: 'string',
            title: i18nExpression('sourcingBuyer.createdFullName'), // 创建人
            'x-render-table-column': {
              minWidth: 100
            }
          },
          reviewCreationDate: {
            type: 'string',
            title: i18nExpression('sourcingBuyer.creationDate'), // 创建时间
            'x-render-table-column': {
              minWidth: 100
            }
          },

          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 60,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-reactions': expression(`(field) => {
                  field.visible = !$form.readPretty
              }`),
            properties: {
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`
                        ({rowIndex}) => {
                          const row = $table.getRowByIndex(rowIndex)
                          $delSourcingItem($form,$table,row,rowIndex)
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
