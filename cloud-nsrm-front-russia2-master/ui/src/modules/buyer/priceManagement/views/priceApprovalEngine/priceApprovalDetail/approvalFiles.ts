/* eslint-disable quotes */
import { expression, generateXindexInOrder, i18nExpression } from '@meicloud/render-engine'

export const $addFileOne = ($form: any) => {
  $form.query('fileList').take((field: any) => {
    field.value.push({
      fileDocId: null,
      fileName: null,
      remark: null
    })
  })
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
        field.visible = !$form.readPretty && $form.values.sourceFromType === PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE
      }`),
      properties: {
        add: {
          type: 'void',
          title: i18nExpression('common.add'),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => $addFileOne($form)`)
          }
        }
      }
    },
    fileList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        editMode: true,
        maxHeight: 400,
        pagination: false,
        sortable: false,
        // 联表主键的 key
        primaryKey: 'approvalFileId',
        preColumns: 'seq',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'fileList:*',
      properties: generateXindexInOrder({
        approvalFileId: {
          type: 'string',
          'x-hidden': true,
          'x-query-engine-primary-key': true
        },
        fileDocId: {
          type: 'string',
          'x-hidden': true
        },
        fileName: {
          type: 'string',
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            'extra-data': {
              fileModular: 'sou',
              fileFunction: 'priceApproval',
              fileType: 'images'
            },
            readonly: expression(`$form.readPretty || $form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE`),
            defaultFile: {
              fileId: expression(`$table.getRowByIndex($self.index)?.fileDocId`),
              fileName: expression(`$self.value`)
            },
            '@on-change': expression(`({ file }) => {
              let row = $table.getRowByIndex($self.index)
              const { fileId,fileName } = file || {}
              row.fileDocId = fileId
              row.fileName = fileName
            }`)
          },
          'x-render-table-column': {
            title: i18nExpression('bidMod.fileName'), // 附件名称
            minWidth: 200
          }
        },
        remark: {
          type: 'string',
          title: i18nExpression('bidMod.remark'),
          'x-render-table-column': {
            minWidth: 250
          }
        },
        // 操作
        operation: {
          type: 'void',
          'x-render-table-column': {
            title: i18nExpression('common.operation'),
            width: 100,
            sortable: false
          },
          'x-component': 'RenderTableButtonList',
          'x-reactions': expression(`(field) => {
            field.visible = !$form.readPretty
          }`),
          properties: {
            // 删除
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
