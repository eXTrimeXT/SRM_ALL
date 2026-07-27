import {
  i18nExpression,
  generateXindexInOrder,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"

export const abnormalInfo = {
  abnormalInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.abnormalInfo'),
    },
    'x-query-engine-skip': true,
    properties: {
      npmCompanyExceptionInfos: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMod: false,
          maxHeight: 250,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          exceptionType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.abnormalType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_EXCEPTION_TYPE'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          exceptionInfo: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.orgCategory'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          exceptionRemark: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.abnormalRemark'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          creationDate: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.createDate'),
            'x-render-table-column': {
              minWidth: 120
            }
          }
        })
      }
    }
  }
}