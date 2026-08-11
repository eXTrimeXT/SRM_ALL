import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const equipmentInformationList = {
  // 设备信息
  equipmentInformationList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.deviceInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              disabled: expression(`$form.query('state').get('data').$disabled`),
              '@click': expression(`() => {
                 $self.query('equipmentInformations')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      equipmentInformations: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false,
          primaryKey: 'id',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          // 设备类型
          equipmentType: {
            type: 'string',
            title: i18nExpression('vendorMod.equipmentType'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 设备名称
          equipmentName: {
            type: 'string',
            title: i18nExpression('vendorMod.equipmentName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 规格型号
          equipmentSpecification: {
            type: 'string',
            title: i18nExpression('vendorMod.specification'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 数量
          equipmentQuantity: {
            type: 'string',
            title: i18nExpression('bid_mod.quantity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 单位设备产能
          unitEquipmentCapacity: {
            type: 'string',
            title: i18nExpression('vendorMod.equipmentCapacity'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 	生产厂家
          manufacturer: {
            type: 'string',
            title: i18nExpression('vendorMod.manufacturer'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 	已服役年限
          yearsOfService: {
            type: 'string',
            title: i18nExpression('vendorMod.serviceYear'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  'disabled': expression(`$form.query('state').get('data').$disabled`),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                      $table.remove($self.index)
                  }`)
                }
              }
            }}
        })
      }
    }
  }
}
