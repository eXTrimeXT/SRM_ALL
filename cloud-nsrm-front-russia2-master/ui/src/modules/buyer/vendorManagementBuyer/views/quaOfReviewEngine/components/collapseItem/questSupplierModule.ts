import {
    expression,
    i18nExpression,
    generateXindexInOrder
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('quest.questSupplierModule')
    },
    properties: {
      questSupplierList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          editMode: false,
          maxHeight: 400,
          pagination: false,
          sortable: false
        },
        'x-query-engine-skip': true,
        // 'x-query-engine-relation': 'questSupplierList:*',
        properties: generateXindexInOrder({
          questNo: {
            type: 'string',
            title: i18nExpression('quest.questNo'), // 调查表编号
            'x-render-table-column': {
              minWidth: 180
            }
          },
          questName: {
            type: 'number',
            title: i18nExpression('quest.questName'), // 调查表名称
            default: 0,
            'x-component-props': {
              controls: false,
              precision: 2
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },

          approvalStatus: {
            type: 'string',
            title: i18nExpression('quest.approvalStatus'), // 调查表状态
            'x-render-table-column': {
              minWidth: 120
            }
          },
          companyCode: {
            type: 'string',
            title: i18nExpression('quest.companyCode'), // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          companyName: {
            type: 'string',
            title: i18nExpression('quest.companyName'), // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          questTemplateOrgName: {
            type: 'string',
            title: i18nExpression('quest.questTemplateOrgName'), // 业务组织
            'x-render-table-column': {
              minWidth: 120
            }
          },
          questTemplateCode: {
            type: 'string',
            title: i18nExpression('quest.questTemplateCode'), // 调查模板编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          questTemplateName: {
            type: 'string',
            title: i18nExpression('quest.questTemplateName'), // 调查表模板名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('quest.createdFullName'), // 创建人
            'x-render-table-column': {
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('quest.creationDate'), // 创建时间
            'x-render-table-column': {
              minWidth: 120
            }
          }

        })
      }
    }
}
