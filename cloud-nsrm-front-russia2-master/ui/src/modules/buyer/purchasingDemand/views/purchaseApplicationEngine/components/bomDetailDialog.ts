import {
  generateXindexInOrder,
  i18nExpression,
  expression
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-decorator': 'QueryEngine',
  title: i18nExpression('purchaseApplication.bomDetail'), // BOM明细
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {
      return [
        {...cancelButton, text: $t('bidMod.backTo')}
      ]
    }`)
  },
  properties: {
    layout: {
      type: 'void',
      properties: {
        bomDetailList: {
          type: 'array',
          'x-query-engine-skip': true,
          'x-component': 'RenderTable',
          'x-component-props': {
            height: '450px',
            editMode: false,
            preColumns: 'seq',
            pagination: false,
            sortable: false
          },
          properties: generateXindexInOrder({
            materialCode: {
              type: 'string',
              title: i18nExpression('outsourcingBomNew.materialCode'), // 委外组件编码
              'x-render-table-column': {
                minWidth: 120
              }
            },
            materialName: {
              type: 'string',
              title: i18nExpression('outsourcingBomNew.materialName'), // 委外组件名称
              'x-render-table-column': {
                minWidth: 120
              }
            },
            unitName: {
              type: 'string',
              title: i18nExpression('purchaseApplication.unitName'), // 委外组件单位
              'x-render-table-column': {
                minWidth: 120
              }
            },
            baseMaterialNum: {
              type: 'string',
              title: i18nExpression('outsourcingBomNew.num'), // 数量
              'x-render-table-column': {
                minWidth: 120
              }
            },
            componentQuantity: {
              type: 'string',
              title: i18nExpression('purchaseApplication.componentQuantity'), // 组件需求数量
              'x-render-table-column': {
                minWidth: 120
              }
            }
          })
        }
      }
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomDetailListPageNum'),
        pageSize: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomDetailListPageSize'),
        total: expression('$form.query(\'PrRequirementForBuyer\').get(\'data\').bomDetailListTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
                $form.query('PrRequirementForBuyer').get('data').bomDetailListPageNum = num
                $getBomDetailListList($form)
              }`),
        '@size-change': expression(`(size) => {
                $form.query('PrRequirementForBuyer').get('data').bomDetailListPageSize = size
                $getBomDetailListList($form)
              }`)
      }
    }

  }
}
