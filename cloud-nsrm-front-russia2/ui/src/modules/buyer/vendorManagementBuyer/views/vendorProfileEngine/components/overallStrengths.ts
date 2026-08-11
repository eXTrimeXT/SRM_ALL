import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const overallStrengths = {
  overallStrengthList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.overallStrength'),
    },
    'x-query-engine-skip': true,
    properties: {
      overallStrengths: {
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
          // 行业类型
          industryType: {
            type: 'string',
            title: i18nExpression('vendorMod.industryType'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 	年份
          year: {
            type: 'string',
            title: i18nExpression('dataConfMod.year'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 行业排名
          industryRanking: {
            type: 'string',
            title: i18nExpression('vendorMod.industryRank'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 市场份额
          marketShare: {
            type: 'string',
            title: i18nExpression('vendorMod.marketShare'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 行业前三的企业名称
          topThreeEnterprises: {
            type: 'string',
            title: i18nExpression('vendorMod.top3CompanyName'),
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
