import {
  expression, generateXindexInOrder, i18nExpression
} from '@meicloud/render-engine'


// 新增附件信息
export const $addAttachmentRow = ($form: any) => {
  const obj = {
    templateDesc: null,
    templateFileId: null,
    templateFileName: null,
    fileId: null,
    fileName: null,
    ifRequired: 'N',
    ifValidDate: 'N',
    fileValidDate: null,
    reviewPeople: null,
    vendorAssessor: null,
    reviewDate: null,
    score: null,
    authResult: null,
    remark: null
  }
  $form.query('fileRecords').take((field: any) => field.value.push(obj))
}

export const fileValid = ($form: any, $message: any, $t: any) => {
  // 判断附件是否上传
  const fileRecords = $form.values.fileRecords || []
  let fileFlag = true
  for (const item of fileRecords) {
    if (item.ifRequired === 'Y' && !item.fileId) {
      // 设置了必传
      // 请上传
      $message.error($t('vendorMod.msgUpload') + item.templateDesc + $t('vendorMod.msgAttachInfo'))
      fileFlag = false
      break
    }
    if (item.ifValidDate === 'Y' && !item.fileValidDate) {
      // 设置了有效期必填
      $message.error($t('vendorMod.msgMaintain') + item.templateDesc + $t('vendorMod.msgAttachDeadline'))
      fileFlag = false
      break
    }
  }
  if (!fileFlag) {
    return false
  }
  return true
}

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
              field.visible = !$form.readPretty && ['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus)
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
        templateDesc: {
          type: 'string',
          title: i18nExpression('vendorMod.attachmentName'), // 附件名称
          'x-read-pretty': expression('$form.readPretty || ![\'DRAFT\',\'WITHDRAW\',\'REJECTED\'].includes($values.approveStatus)'),
          'x-render-table-column': {
            minWidth: 120
          }
        },
        templateFileName: {
          type: 'string',
          title: i18nExpression('vendorMod.templateDownload'), // 模板下载
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            'readonly': true,
            'default-file': {
              fileId: expression('$table.getRowByIndex($self.index).templateFileId'),
              fileName: expression('$self.value')
            }
          },
          'x-render-table-column': {
            minWidth: 150
          }
        },
        fileName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            'readonly': expression('$form.readPretty || ![\'DRAFT\',\'WITHDRAW\',\'REJECTED\'].includes($values.approveStatus)'),
            extraData: {
              fileModular: 'sup',
              fileFunction: 'quaOfReview',
              fileType: 'images'
            },
            defaultFile: {
              fileId: expression('$table.getRowByIndex($self.index).fileId'),
              fileName: expression('$self.value')
            },
            '@on-change': expression(`({ file }) => {
                let row = $table.getRowByIndex($self.index)
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
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 60
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
                '@click': expression(`({rowIndex}) => {
                  $table.remove(rowIndex)
                }`)
              }
            }
          }
        }
      })
    }
  }
}
