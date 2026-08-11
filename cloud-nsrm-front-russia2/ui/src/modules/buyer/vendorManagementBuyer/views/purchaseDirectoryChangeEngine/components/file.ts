/* eslint-disable quotes */
import { expression, generateXindexInOrder } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  toolbar: {
    type: 'void',
    'x-component': 'Space',
    'x-component-props': {
      style: 'margin-bottom:5px;display:block;'
    },
    'x-reactions': expression(`field => {
      field.visible = !$readOnly
    }`),
    properties: {
      add: {
        type: 'void',
        title: "{{$t('common.add')}}",
        'x-component': 'RButton',
        'x-component-props': {
          '@click': expression(`() => {
            $form.query('purCatalogAttChangeList').take(field => {
              field.value.push({
                fileId:null,
                fileName:null
              })
            })
          }`)
        }
      }
    }
  },
  purCatalogAttChangeList: {
    type: 'array',
    'x-query-engine-skip': true,
    // 'x-query-engine-relation': 'purCatalogAttList:*',
    'x-query-engine-relation': 'purCatalogAttChangeList:*',
    'x-component': 'RenderTable',
    'x-component-props': {
      preColumns: 'seq',
      editMode: true,
      pagination: false,
      maxHeight: '58vh',
      sortable: false,
      primaryKey: 'attachId',
      cascadeDeletion: true
    },
    properties: generateXindexInOrder({
      attachId: {
        type: 'string',
        'x-hidden': true
      },
      fileId: {
        type: 'string',
        title: "{{$t('vendorMod.attachmentUpload')}}",
        'x-render-table-column': {
          minWidth: '150px'
        },
        'x-component': 'SrmCommonFile',
        'x-component-props': {
          'extra-data': expression(`{
            fileModular: 'sup',
            fileFunction: 'purchaseDirectory',
            fileType: 'images'
          }`),
          'default-file': {
            fileId: expression('$self.value'),
            fileName: expression('$table.getRowByIndex($self.index).fileName || \'\'')
          },
          readonly: expression(`$readOnly`),
          '@on-change': expression(`({file,$index}) => {
            const { fileId = '', fileName = '' } = file || {}
            const row = $table.getRowByIndex($self.index)
            row.fileId = fileId
            row.fileName = fileName
          }`)
        }
      },
      createdFullName: {
        type: 'string',
        title: "{{$t('quota.uploadBy')}}",
        'x-render-table-column': {
          minWidth: 100
        },
        'x-read-pretty': true
      },
      creationDate: {
        ...yearMonthDaySelectorSegment,
        'x-component-props': {
          ...yearMonthDaySelectorSegment['x-component-props'],
          formatter: expression(`({ cellValue, row, column }) => {
            parseTime(row.creationDate, '{y}-{m}-{d}')
          }`)
        },
        title: "{{$t('components.fileupload.uploadDate')}}",
        'x-render-table-column': {
          minWidth: 100
        },
        'x-read-pretty': true
      },
      operation: {
        type: 'void',
        title: "{{$t('common.operation')}}",
        'x-component': 'RenderTableButtonList',
        'x-render-table-column': {
          width: 120
        },
        'x-reactions': expression(`field => {
          field.visible = !$readOnly
        }`),
        properties: {
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component-props': {
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
