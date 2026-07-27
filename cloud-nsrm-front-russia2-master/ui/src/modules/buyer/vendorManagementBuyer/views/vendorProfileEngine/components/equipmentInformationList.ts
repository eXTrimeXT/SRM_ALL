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
      equipmentInformations: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 400,
          pagination: false,
          sortable: false
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
              'disabled': expression(`$disabled`)
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
                  'disabled': expression(`$disabled`),
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
