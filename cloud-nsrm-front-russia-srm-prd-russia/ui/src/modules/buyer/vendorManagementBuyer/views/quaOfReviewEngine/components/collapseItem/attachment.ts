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
    title: i18nExpression('vendorMod.attachment')
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
                $addAttachmentRow($form)
             }`)
          }
        }
      }
    },
    fileRecords: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'fileRecordId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'fileRecords:*',
      properties: generateXindexInOrder({
        fileRecordId: {
          type: 'string',
          'x-hidden': true

        },
        formType: {
          type: 'string',
          'x-hidden': true,
          default: 'REVIEW_FORM'
        },
        templateDesc: {
          type: 'string',
          title: i18nExpression('vendorMod.attachmentName'), // 附件名称
          'x-render-table-column': {
            minWidth: 120
          }
        },
        templateFileName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            'readonly': true,
            'default-file': {
              fileId: expression(`$table.getRowByIndex($self.index).templateFileId`),
              fileName: expression('$self.value')
            }
          },
          'x-render-table-column': {
            title: i18nExpression('vendorMod.templateDownload'), // 模板下载
            minWidth: 150
          }
        },
        fileName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            extraData: {
              fileModular: 'sup',
              fileFunction: 'quaOfReview',
              fileType: 'images'
            },
            defaultFile: {
              fileId: expression(`$table.getRowByIndex($self.index).fileId`),
              fileName: expression('$self.value')
            },
            '@on-change': expression(`({ file }) => {
                const row = $table.getRowByIndex($self.index)
                const { fileId = '', fileName = '' } = file || {}
                row.fileId = fileId.toString() || null
                row.fileName = fileName
              }`)
          },
          'x-read-pretty': expression('$form.readPretty'),
          'x-render-table-column': {
            title: i18nExpression('vendorMod.attachmentUpload'), // 附件上传
            minWidth: 150
          },
          'x-validator': {
            required: expression('$self.query(\'.ifRequired\').get(\'value\') === \'Y\''),
            message: i18nExpression('common.requiredField')
          }
        },
        ifRequired: {
          type: 'string',
          'x-hidden': true
        },
        remark: {
          type: 'string',
          title: i18nExpression('vendorMod.remark'), // 备注
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
