import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";



export const cooInfoList = {
  cooInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('supRisk.cooInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      orgInfos: {
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
          orgName: {
            type: 'string',
            title: i18nExpression('vendorMod.category'),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 150
            }
          },
          serviceStatus: {
            type: 'string',
            title: i18nExpression('vendorMod.orgServiceStatus'),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 120
            }
          },
          startDate: {
            type: 'string',
            title: i18nExpression('vendorMod.startDate'),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 120
            }
          },
          endDate: {
            type: 'string',
            title: i18nExpression('vendorMod.endDate'),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-read-pretty': true,
            'x-render-table-column': {
              minWidth: 120
            }
          }
        })
      }
    }
  }
}
