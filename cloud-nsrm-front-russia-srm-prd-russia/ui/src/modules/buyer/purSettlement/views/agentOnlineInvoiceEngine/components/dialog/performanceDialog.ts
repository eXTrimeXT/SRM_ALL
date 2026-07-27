import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.supplierAssessmentDetails'),
  'x-decorator': 'QueryEngine',
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    'close-on-click-modal': false,
    footer: true,
    beforeClose: expression(`(done, type) => {
              if ( type === 'ok') {
                $setPerformanceDetailsData($form, $message)  
              }
              done()
        }`)
  },
  properties: {
    performanceDialogTable: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        preColumns: 'checkbox,seq',
        pagination: false,
        sortable: false
      },
      'x-query-engine-skip': true,
      properties: generateXindexInOrder({
        assessmentNo: {
          type: 'string',
          title: i18nExpression('perfMod.assessmentNo'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        assessmentDate: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('perfMod.assessmentDate'),
            minWidth: 100
          }
        },
        assessmentType: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'ASSESSMENT_BILL_TYPE'
          },
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.assessmentEvent'),
            minWidth: 100
          }
        },
        actualAssessmentAmountN: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('purSettlementMod.actualAssessmentAmountN'),
            minWidth: 100
          }
        },
        tax: {
          type: 'string',
          title: i18nExpression('contractMod.taxQuota'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        actualAssessmentAmountY: {
          type: 'string',
          title: i18nExpression('purSettlementMod.actualAssessmentAmountY'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        currencyCode: {
          type: 'string',
          'x-component': 'DictSelect',
          'x-component-props': {
            code: 'currency'
          },
          title: i18nExpression('quota.currency'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        categoryName: {
          type: 'string',
          title: i18nExpression('bidMod.categoryName'),
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemCode: {
          type: 'string',
          title: i18nExpression('common.materialCode'), // 物料编码
          'x-render-table-column': {
            minWidth: 100
          }
        },
        itemName: {
          type: 'string',
          title: i18nExpression('common.materialName'), // 物料名称
          'x-render-table-column': {
            minWidth: 100
          }
        },
        comment: {
          type: 'string',
          title: i18nExpression('common.remark'),
          'x-render-table-column': {
            minWidth: 100
          }
        }
      })
    },
    pagination: {
      type: 'void',
      'x-component': 'CPagination',
      'x-component-props': {
        pageNum: expression('$form.query(\'OnlineInvoice\').get(\'data\').performanceDialogPageNum'),
        pageSize: expression('$form.query(\'OnlineInvoice\').get(\'data\').performanceDialogPageSize'),
        total: expression('$form.query(\'OnlineInvoice\').get(\'data\').performanceDialogTotal'),
        pageSizes: [5, 15, 30, 60, 120, 300, 600, 1000, 1500],
        '@current-change': expression(`(num) => {
              $form.query('OnlineInvoice').get('data').performanceDialogPageNum = num
              $getPerformanceDialogData($form)
            }`),
        '@size-change': expression(`(size) => {
              $form.query('OnlineInvoice').get('data').performanceDialogPageSize = size
              $getPerformanceDialogData($form)
            }`)
      }
    }

  }
}
