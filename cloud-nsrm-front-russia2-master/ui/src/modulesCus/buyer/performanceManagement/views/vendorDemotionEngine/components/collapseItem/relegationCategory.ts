import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

import {
  feedbackLayoutIsPopover,
  yearMonthDaySelectorSegment,
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('vendorMod.relegation.relegationCategory'),
     
  },
  properties: {
    add: {
      type: 'string',
      title: i18nExpression('common.new'),
      'x-component': 'QuickSearchWrapper',
      'x-query-engine-skip': true,
      'x-component-props': {
        style:{
          'margin-bottom': '10px'
        },
        showButton: true,
        multiSelect: true,
        disabled: `{{!$values.companyName || !$values.demotionType}}`,
        btnTitle: `{{$t('common.new')}}`,
        'read-pretty': '{{$form.readPretty}}',
        'name': 'scc_sup_company_demotion_category_url',
        '@close-quicksearch': expression(`(val, scope) => {
          $catSelectHandel($values,val,$form)
        }`)
      },
      'x-reactions': {
        dependencies: ['companyId','demotionType'],
        fulfill: {
          state: {
            'component[1].preQueryData': expression('{\'t.COMPANY_ID\': $values.companyId,\'t.DEMOTION_TYPE\': $deps[1]}')
          }
        }
      }
    },
    companyDemotionCategories: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        editMode: true,
        preColumns: expression('$form.readPretty ? \'seq\' : \'seq\''),
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'companyDemotionId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-read-pretty': true,
      'x-query-engine-relation': 'companyDemotionCategories:*',
      properties: generateXindexInOrder({
        companyDemotionId: {
          type: 'string',
          'x-hidden': true

        },
        categoryFullName: {
          type: 'string',
          title: i18nExpression('vendorMod.relegation.categoryFullPath'), 
          'x-render-table-column': {
            minWidth: 120
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('vendorMod.relegation.categoryName'), 
          'x-render-table-column': {
            minWidth: 120
          }
        },
      
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 60,
            fixed: 'right'
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
                field.visible = !$form.readPretty 
            }`),
          properties: {
            delete: {
              type: 'void',
              title: i18nExpression('common.delete'),
              'x-component-props': {
                type: 'text',
                '@click': expression(`
                       ({ rowIndex }) => {
                          $table.remove(rowIndex)
                       }
                   `)
              },
              'x-reactions': expression(`(field) => {
                field.disabled = $values.demotionType === 'DEMOTION_TO_BLACK'
            }`),
            }
          }
        }
      })
    }
  }
}
