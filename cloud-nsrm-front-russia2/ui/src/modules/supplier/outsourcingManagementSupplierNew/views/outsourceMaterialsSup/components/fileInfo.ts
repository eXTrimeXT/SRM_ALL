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
    title: i18nExpression('outsource.materialReqFile') // '相关附件上传'
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
        // 新增
        addFile: {
          type: 'void',
          title: '{{$t("common.add")}}',
          'x-component-props': {
            type: 'primary',
            disabled: false,
            '@click': expression(`() => {
              $self.query('.attachList').take(field => {
                field.componentProps.componentInstance.addRow()
              })
            }`)
          }
        }
      }
    },
    attachList: {
      type: 'array',
      'x-component': 'RenderTable',
      'x-component-props': {
        class: 'table-view-vxe-table',
        maxHeight: 400,
        pagination: false,
        sortable: false,
        editMode: true,
        preColumns: 'seq',
        // 联表主键的 key
        primaryKey: 'attachId',
        // 启用级联删除的储值行为
        cascadeDeletion: true
      },
      'x-query-engine-skip': true,
      'x-query-engine-relation': 'attachList:*',
      properties: generateXindexInOrder({
        materialReqId: {
          type: 'string',
          'x-hidden': true
        },
        attachId: {
          type: 'string',
          'x-hidden': true
        },
        fileuploadId: {
          type: 'string',
          'x-hidden': true
        },
        // 附件
        attachName: {
          type: 'string',
          'title': i18nExpression('outsource.attachName'),
          'x-component': 'SrmCommonFile',
          'x-component-props': {
            readonly: expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "CREATE"].includes($form.values.handleStatus)'),
            'extra-data': {
              fileModular: 'sup',
              fileFunction: 'contractPerformanceCheck',
              fileType: 'images'
            },
            'default-file': {
              fileId: '{{$table.getRowByIndex($self.index).fileuploadId}}',
              fileName: '{{$self.value}}'
            },
            '@on-change': expression(`({file}) => {
              let row = $table.getRowByIndex($self.index)
              const { fileId = '', fileName = '' } = file || {}
              row.fileuploadId = fileId.toString()
              $self.value = fileName
              row.createdFullName = file.createdBy
              row.creationDate = file.creationDate
            }`)
          },
          'x-render-table-column': {
            minWidth: 130
          }
        },
        // 上传人
        createdFullName: {
          type: 'string',
          'x-render-table-column': {
            title: i18nExpression('outsource.createdFullName'),
            minWidth: 120
          },
          'x-component-props': {
            disabled: true
          }
        },
        // 上传时间
        creationDate: {
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            formatter: expression(`({ cellValue, row, column }) => {
              parseTime(row.creationDate, '{y}-{m}-{d}')
            }`),
            disabled: true
          },
          'x-render-table-column': {
            title: i18nExpression('outsource.creationDate')
          }
        },
        operation: {
          type: 'void',
          title: i18nExpression('common.operation'),
          'x-render-table-column': {
            width: 120,
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
                disabled: expression('$table.getRowByIndex($self.index).attachId && ![undefined, "", "CREATE"].includes($form.values.handleStatus)'),
                '@click': expression(`() => {
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
