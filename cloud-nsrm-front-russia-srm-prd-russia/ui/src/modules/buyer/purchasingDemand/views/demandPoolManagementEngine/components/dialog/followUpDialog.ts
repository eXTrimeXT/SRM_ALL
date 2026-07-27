import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-decorator': 'QueryEngine',
  title: i18nExpression('purchaseDemand.subsequentDocuments'), // 后续单据一览
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'large',
    footer: false,
    'close-on-click-modal': false
  },
  properties: {
    layout: {
      type: 'void',
      properties: {
        followOrderList: {
          type: 'array',
          'x-query-engine-skip': true,
          'x-component': 'RenderTable',
          'x-component-props': {
            class: 'table-view-vxe-table',
            preColumns: 'seq',
            pagination: false,
            sortable: false
          },
          'x-read-pretty': true,
          properties: generateXindexInOrder({
            subsequentDocumentsNumber: {
              type: 'string',
              title: i18nExpression('purchaseDemand.subsequentDocumentsNumber'), // 后续单据编号
              'x-render-table-column': {
                minWidth: 100
              }
            },
            isubsequentDocumentssType: {
              type: 'string',
              title: i18nExpression('purchaseDemand.isubsequentDocumentssType'), // 后续单据类型
              'x-render-table-column': {
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PR_SUBSEQUENT_DOC_TYPE'
              }
            },
            docStatus: {
              type: 'string',
              title: i18nExpression('common.status'), // 状态
              'x-render-table-column': {
                minWidth: 100
              },
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'PR_SUBSEQUENT_DOC_STATUS'
              }
            },
            createdUserName: {
              type: 'string',
              title: i18nExpression('purchaseDemand.createdBy1'), // 创建人
              'x-render-table-column': {
                minWidth: 100
              }
            },
            creationDate: {
              type: 'string',
              title: i18nExpression('purchaseDemand.creationDate'), // 创建时间
              'x-render-table-column': {
                minWidth: 100
              }
            }

          })
        }
      }
    }

  }
}
