import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine";
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const relationSuppliers = {
  relationSuppliers: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.relationSuppliers')
    },
    'x-query-engine-skip': true,
    'x-visible': generateCharExpressionByFunction(({ $attrs }) => {
      return ['passRegister', 'view'].includes($attrs.params.flag)
    }),
    properties: {
      relationSuppliersList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 250,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          vendorCodeA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.aCompanyCode'),
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeA,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeA
                  },
                  title: row.vendorCodeA
                })
              }`)
            }
          },
          socialCreditCodeA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.socialCreditCodeA'),
              minWidth: 150
            },
            'x-read-pretty': true
          },
          vendorNameA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.aCompanyName'),
              minWidth: 120
            },
            'x-read-pretty': true
          },
          vendorCodeB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.bCompanyCode'),
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeB,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeB
                  },
                  title: row.vendorCodeB
                })
              }`)
            }
          },
          socialCreditCodeB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.socialCreditCodeB'),
              minWidth: 150
            },
            'x-read-pretty': true
          },
          vendorNameB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.bCompanyName'),
              minWidth: 120
            },
            'x-read-pretty': true
          },
          associationRemark: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.relationRemark'),
              minWidth: 120
            },
            'x-read-pretty': true
          },
          createdUserName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.creator'), // '创建人'
              minWidth: 150
            },
            'x-read-pretty': true
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('common.creationTime'), // '创建时间'
              minWidth: 150
            },
            'x-read-pretty': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc',
          }
        })
      }
    }
  }
}
