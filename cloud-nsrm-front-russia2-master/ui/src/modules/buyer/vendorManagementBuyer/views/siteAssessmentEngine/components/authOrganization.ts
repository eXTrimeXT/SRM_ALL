import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from "@meicloud/render-engine";



export const authOrganization = {
  orgCateJournals: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      maxHeight: 400,
      pagination: false,
      sortable: false
    },
    'x-query-engine-skip': true,
    properties: generateXindexInOrder({
      quoted: {
        type: 'string',
        title: i18nExpression(' '),
        'x-render-table-column': {
          width: 45
        },
        'x-component': 'Checkbox',
        'x-component-props': {
          trueLabel: 'Y',
          falseLabel: 'N',
          disabled: expression(`$disabledAdd($form)`)
        }
      },
      orgName: {
        type: 'string',
        title: i18nExpression('vendorMod.orgName2'),
        'x-render-table-column': {
          minWidth: 200
        }
      },
      categoryName: {
        type: 'string',
        title: i18nExpression('vendorMod.categoryName'),
        'x-render-table-column': {
          minWidth: 200
        }
      },
    })
  }
}
