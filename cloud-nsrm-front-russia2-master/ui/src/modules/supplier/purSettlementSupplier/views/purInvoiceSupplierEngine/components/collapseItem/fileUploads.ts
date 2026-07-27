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
    title: i18nExpression('purSettlementMod.addUploadFile')
  },
  properties: {
    toolbar: {
      type: 'void',
      'x-component': 'ButtonList',
      'x-component-props': {
        class: 'list-form__toolbar'
      },
      'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`),
      properties: {
        add: {
          type: 'void',
          title: i18nExpression('common.add'),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $self.query('.fileUploads').take().componentProps.componentInstance.addRow()
             }`)
          }
        }
      }
    },
    fileUploads: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        preColumns: 'seq',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'attachId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-read-pretty': true,
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'fileUploads:*',
      properties: generateXindexInOrder({
        attachId: {
          type: 'string',
          'x-hidden': true
        },
        attachName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-read-pretty': expression('$form.readPretty'),
          'x-render-table-column': {
            title: i18nExpression('bidMod.fileName'),
            minWidth: 150
          },
          'x-component-props': {
            extraData: {
              uploadType: 'DEF',
              sourceType: 'WEB_APP',
              fileModular: 'sup',
              fileFunction: 'agentOnlineInvoice',
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
          title: i18nExpression('quota.uploadDate'),
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
                        () => {
                          $table.remove($self.index)
                        }
                      `)
              }
            }
          }
        }
      })
    }
  }
}
