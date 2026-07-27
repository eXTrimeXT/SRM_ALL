import {
  expression,
  generateXindexInOrder,
  i18nExpression,
} from "@meicloud/render-engine";

export const fileUploadsList = {
  // 客户情况
  fileUploadsList: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.sceneAttachmentInfo2')
    },
    'x-query-engine-skip': true,
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              disabled: expression(`$disabled`),
              '@click': expression(`() => {
                 $self.query('clientStatusList')
                   .take(field => {
                     field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      fileUploads: {
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
          // 模板附件名称
          customerName: {
            type: 'string',
            title: i18nExpression('returnGoodsBill.key11'),
            default: i18nExpression('returnGoodsBill.key12'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          fileName: {
            type: 'string',
            title: i18nExpression('vendorMod.attachmentUpload'),
            'x-read-pretty': expression('$disabled && $buyer()'),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                fileModular: 'sup',
                fileFunction: 'contractPerformanceCheck',
                fileType: 'images'
              },
              'default-file': {
                fileId: '{{$table.getRowByIndex($self.index).fileId}}',
                fileName: '{{$self.value}}'
              },
              '@on-change': expression(`({file}) => {
                                let row = $table.getRowByIndex($self.index)
                                const { fileId = '', fileName = '' } = file || {}
                                row.fileId = fileId.toString()
                                $self.value = fileName
                                row.createdUserName = file.createdBy
                                row.creationDate = file.creationDate
                              }`)
            },
            'x-render-table-column': {
              minWidth: 150
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
