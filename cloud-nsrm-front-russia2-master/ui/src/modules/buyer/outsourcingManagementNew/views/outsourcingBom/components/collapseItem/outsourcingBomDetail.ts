import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import {
  feedbackLayoutIsPopover,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('outsourcingBomNew.detail') // 明细
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
            '@click': expression(`() => {
              $self.query('.bomLines').take().componentProps.componentInstance.addRow('unshift')
             }`)
          }
        }
      }
    },
    bomLines: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: 'seq',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'bomLineId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'bomLines:*',
      properties: generateXindexInOrder({
        bomLineId: {
          type: 'string',
          'x-hidden': true
        },
        materialCode: {
          type: 'string',
          title: i18nExpression('outsourcingBomNew.materialCode'), // 委外组件编码
          'x-render-table-column': {
            minWidth: 120
          },
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            readPretty: '{{$form.readPretty}}',
            showKey: 'materialCode',
            propKey: 'materialCode',
            name: 'scc_base_material_item_bom_line',
            disabled: '{{!$form.values.organizationId || !$form.values.materialId}}',
            preQueryData: '{{{ \'t.materialIdNotEqual\': $form.values.materialId, \'t.invOrgCodes\': [$form.values.organizationCode], \'t.enabled\': \'Y\' }}}',
            '@close-quicksearch': expression(`(val, scope) => {
              let row = $table.getRowByIndex($self.index)
              row.unit = val ? val.unit : ''
              row.unitName = val ? val.unitName : ''
              row.materialCode = val ? val.materialCode : ''
              row.materialName = val ? val.materialName : ''
              row.materialId = val ? val.materialId : ''
            }`)
          },
          ...feedbackLayoutIsPopover,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if (value === $form.values.materialCode) {
                return '不可与已选的头行信息的物料编码重复，请重新选择！'
              }
            }`)
          }
        },
        materialName: {
          type: 'string',
          title: i18nExpression('outsourcingBomNew.materialName'), // 委外组件名称
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
        unit: {
          type: 'string',
          title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
          'x-component-props': {
            disabled: expression('$form.readPretty ? undefined : true')
          },
          'x-render-table-column': {
            minWidth: 120
          }
        },
        baseMaterialNum: {
          type: 'number',
          title: i18nExpression('outsourcingBomNew.num'), // 数量
          'x-render-table-column': {
            minWidth: 100
          },
          ...editTableFormItemValid
        },
        distributeFlag: {
          type: 'string',
          default: 'Y',
          title: i18nExpression('outsourcingBomNew.distributeFlag'), // 是否发料
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'YES_OR_NO'
          },
          'x-render-table-column': {
            minWidth: 100
          },
          ...editTableFormItemValid
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
