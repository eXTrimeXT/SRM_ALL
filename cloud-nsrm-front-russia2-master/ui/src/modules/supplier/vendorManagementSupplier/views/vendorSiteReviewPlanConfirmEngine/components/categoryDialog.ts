import {expression, generateXindexInOrder} from "@meicloud/render-engine";

export const DialogMainCategory = {
  type: 'void',
  title: `{{$t('dataConfMod.categoryDetails')}}`,
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    class: 'dialogMain',
    size: 'middle',
    footer: false
  },
  properties: {
    categoryList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        style: 'height:250px',
        preColumns: 'seq',
        pagination: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        categoryCode: {
          type: 'string',
          title: "{{$t('common.categoryCode')}}", // 品类编码
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            disabled: true,
            showKey: 'companyName',
            propKey: 'companyName',
            'name': 'scc_base_purchase_category2',
          },
          'x-render-table-column': {
            minWidth: 150,
            // 跳过行内编辑
            skipEditable: true
          }
        },
        categoryName: {
          type: 'string',
          title: "{{$t('common.categoryName')}}", // 品类名称
          'x-render-table-column': {
            minWidth: 120,
            // 跳过行内编辑
            skipEditable: true
          }
        }
      })
    }
  }
}
