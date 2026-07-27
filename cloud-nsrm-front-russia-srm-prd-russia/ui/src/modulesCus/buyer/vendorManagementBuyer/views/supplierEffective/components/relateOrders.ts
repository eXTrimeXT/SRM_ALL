import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

export const relateOrders = {
  relateOrders: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.relateOrders')
    },
    properties: {
      relateOrdersList: {
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
        'x-query-engine-relation': 'effectFormRelationForms:*',
        properties: generateXindexInOrder({
          formType: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.formType')
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_QUALIFY_PROGRESS_DATA'
            }
          },
          formCode: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.formNum')
            }
          },
          submitDate: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.submitDate')
            }
          },
          approvedDate: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.approvedDate')
            }
          },
          approveStatus: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.relateOrderStatus')
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            }
          }
        })
      }
    }
  }
}