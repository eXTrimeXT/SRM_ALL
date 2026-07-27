import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

export const orgCategorys = {
  orgCategorys: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.orgCategorys')
    },
    properties: {
      orgCategorysList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: false,
          maxHeight: 250,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'effectFormOrgCategorys:*',
        properties: generateXindexInOrder({
          orgName: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.relateOrgName')
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.categoryName')
            }
          },
          serviceStatus: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.relateServiceStatus')
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATEGORY_STATUS'
            }
          }
        })
      }
    }
  }
}