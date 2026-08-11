import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const companySizesList = {
  companySizesList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.companySize'),
    },
    'x-query-engine-skip': true,
    properties: {
      companySizes: {
        type: 'array',
        'x-component': 'RenderTable',
        default: [{type:'人数'},{type:'劳务费用(元/年)'}],
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
          // 类型
          type: {
            type: 'string',
            title: i18nExpression(''),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true,
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 	员工
          employee: {
            type: 'string',
            title: i18nExpression('vendorMod.employee'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 管理员
          manager: {
            type: 'string',
            title: i18nExpression('vendorMod.manager'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 技术人员
          technician: {
            type: 'string',
            title: i18nExpression('vendorMod.technician'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          },
          // 	生产人员
          production: {
            type: 'string',
            title: i18nExpression('vendorMod.production'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            }
          }
        })
      }
    }
  }
}
