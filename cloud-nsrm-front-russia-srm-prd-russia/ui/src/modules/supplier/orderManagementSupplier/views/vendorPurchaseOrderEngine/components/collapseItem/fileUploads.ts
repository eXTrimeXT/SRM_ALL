import {
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('orderMod.orderAttachInfo') // 订单附件信息
  },
  properties: {
    attachmentList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        preColumns: 'seq',
        maxHeight: 400,
        sortable: false
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'attachmentList:*',
      properties: generateXindexInOrder({
        attachId: {
          type: 'string',
          'x-hidden': true
        },
        attachName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-read-pretty': true,
          'x-render-table-column': {
            title: i18nExpression('bidMod.fileName'),
            minWidth: 150
          },
          'x-component-props': {
            extraData: {
              uploadType: 'PAAS_MINIO',
              sourceType: 'WEB_APP',
              fileModular: 'sup',
              fileFunction: 'purPaymentApply',
              fileType: 'images'
            },
            defaultFile: {
              fileId: expression('$table.getRowByIndex($self.index)?.fileuploadId'),
              fileName: expression('$self.value')
            },
            '@on-change': expression(`({ file }) => {
                let row = $table.getRowByIndex($self.index)
                const { fileId = '', fileName = '', createdBy = '', creationDate = ''} = file || {}
                row.fileuploadId = fileId.toString() || null
                row.attachName = fileName
                row.createdUserName = createdBy
                row.creationDate = creationDate
              }`)
          }
        },

        createdUserName: {
          type: 'string',
          title: i18nExpression('quota.uploadBy'),
          'x-render-table-column': {
            minWidth: 120
          },
          'x-read-pretty': true
        },
        creationDate: {
          type: 'string',
          title: i18nExpression('quota.uploadDate'),
          'x-render-table-column': {
            minWidth: 120
          },
          'x-read-pretty': true
        }
      })
    }
  }
}
