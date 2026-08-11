import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('outsourceMaterialHead.detail') // 组件物料明细
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
        add: {
          type: 'void',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showButton: true,
            multiSelect: true,
            name: 'scc_base_material_item_bom_line',
            'btn-title': i18nExpression('common.add'), // 新增
            preQueryData: '{{{ \'t.materialIdNotEqual\': $form.values.materialId, \'t.invOrgCodes\': [$form.values.organizationCode], \'t.enabled\': \'Y\' }}}',
            '@close-quicksearch': expression(`data => {
              $setMaterialData($form, data, $message)
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
        preColumns: 'seq',
        pagination: false,
        sortable: false,
        primaryKey: 'changeLineId',
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'detailList:*',
      properties: generateXindexInOrder({
        changeLineId: {
          type: 'string',
          'x-hidden': true

        },
        changeType: {
          type: 'string',
          title: i18nExpression('outsourceMaterialHead.changeType'), // 变更类型
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'OS_MATERIAL_CHANGE_TYPE'
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            if($self.value !== 'ADD'){
              $self.value = row?.beforeBomQuantity === row?.afterBomQuantity ? 'NOT_CHANGE'  : 'MODIFY'
            }
          }`)
        },
        baseMaterialCode: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.baseMaterialCode'), // 组件物料编码
            minWidth: 120
          }
        },
        baseMaterialName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.baseMaterialName'), // 组件物料名称
            minWidth: 120
          }
        },
        baseMaterialUnit: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
            minWidth: 120
          }
        },
        beforeBomQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.beforeBomQuantity'), // 变更前BOM数量
            minWidth: 130,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.beforeBomQuantityTip') } // 变更前BOM数量：生产一个总成物料所需组件物料数量；值来源BOM清单维护的数量
          }
        },

        afterBomQuantity: {
          type: 'number',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.afterBomQuantity'), // 变更后BOM数量
            minWidth: 130,
            customRender: true
          },
          ...editTableFormItemValid,
          'x-read-pretty': '{{$form.readPretty}}'
        },
        beforeRawQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.beforeRawQuantity'), //  变更前组件订单数量
            minWidth: 150,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.beforeRawQuantityTip') } // 变更前组件订单数量：订单数量*变更前BOM数量
          }
        },
        afterRawQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.afterRawQuantity'), // 变更后组件订单数量
            minWidth: 150,
            titlePrefix: { content: i18nExpression('outsourceMaterialHead.afterRawQuantityTip') } // 变更后组件订单数量：订单数量*变更后BOM数量
          },
          'x-reactions': expression(`(field) => {
            let row = $table.getRowByIndex($self.index)
            $self.value = (row?.afterBomQuantity || row?.afterBomQuantity === 0) ? $form.values.orderDetailQuantity * row?.afterBomQuantity : null
          }`)
        },
        receivedQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.receivedQuantity'), //   已领数量
            minWidth: 120
          }
        },
        returnQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.returnQuantity'), // 退料数量
            minWidth: 120
          }
        },
        unreceivedQuantity: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.unreceivedQuantity'), // 未领数量
            minWidth: 120
          }
        },
        detailComments: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsourceMaterialHead.detailComments'), // 明细备注
            minWidth: 120,
            customRender: true
          },
          'x-read-pretty': '{{$form.readPretty}}'
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 80,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty
            }`),
          properties: {
            delete: {
              type: 'void',
              'x-hidden': '{{$table.getRowByIndex($self.index)?.changeType !== \'ADD\'}}',
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
