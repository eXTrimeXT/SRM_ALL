import {
  expression,
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

export const files = {
  files: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.file')
    },
    properties: {
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return !$form.query('effectForm').get('data').isReadOnly
        }),
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $self.query('filesList')
                  .take(field => {
                  field.componentProps.componentInstance.addRow()
                 })
              }`)
            }
          }
        }
      },
      filesList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          editMode: true,
          maxHeight: 250,
          pagination: false,
          sortable: false,
          primaryKey: 'fileRecordId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'fileRecordIdes:*',
        properties: generateXindexInOrder({
          fileId: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.file')
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$table.getRowByIndex($self.index).fileId`),
                fileName: expression(`$table.getRowByIndex($self.index).fileName`)
              },
              '@on-change': expression(`({file}) => {
                const { fileId = null, fileName = null } = file || {}
                let row = $table.getRowByIndex($self.index)
                row.fileId  = fileId
                row.fileName  = fileName
              }`)
            }
          },
          fileName: {
            type: 'string',
            'x-hidden': true
          },
          fileValidDate: {
            type: 'date',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.fileValidDate')
            }
          },
          reviewPeopleName: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.reviewPeopleName')
            }
          },
          vendorAssessor: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.vendorAssessor')
            }
          },
          reviewDate: {
            type: 'date',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.reviewDate')
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: '60px',
              fixed: 'right'
            },
            'x-visible': generateCharExpressionByFunction(({ $form }) => {
              return !$form.query('effectForm').get('data').isReadOnly
            }),
            'x-component': 'RenderTableButtonList',
            properties: {
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`()=> {
                    $table.remove($self.index)
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
}