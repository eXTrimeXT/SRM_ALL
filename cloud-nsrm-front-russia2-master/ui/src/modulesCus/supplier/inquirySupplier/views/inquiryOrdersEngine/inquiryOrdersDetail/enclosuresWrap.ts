/**
 * @description 内外部附件
 */
import {
  generateXindexInOrder,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

// 查看附件
const EnclosuresWrapSegment: Record<any, any> = {
  outerFile: {
    type: 'array',
    'x-component': 'RenderTable',
    'x-component-props': {
      class: 'table-view-vxe-table',
      preColumns: 'seq',
      pagination: false,
      sortable: false,
      editMode: false
    },
    'x-reactions': {
      dependencies: ['souFileList'],
      fulfill: {
        state: {
          value: generateCharExpressionByFunction(({ $form }) => {
            return $form.values.souFileList && $form.values.souFileList.filter((item: any) => item.fileType === 'OUTER')
          })
        }
      }
    },
    'x-read-pretty': true,
    properties: generateXindexInOrder({
      // 文件类型
      fileType: {
        type: 'string',
        default: 'OUTER',
        'x-hidden': true,
        'x-render-table-column': {}
      },
      // 上传附件
      souFileName: {
        type: 'string',
        title: i18nExpression('bidMod.fileName'),
        'x-component': 'SrmCommonFile',
        'x-component-props': {
          defaultFile: {
            fileId: generateCharExpressionByFunction(({ $self, $table }) => {
              return !$self.value
                ? undefined
                : $table.getRowByIndex($self.index).souDocId
            }),
            fileName: generateCharExpressionByFunction(({ $self }) => $self.value && String($self.value))
          }
        },
        'x-render-table-column': {
          minWidth: 150
        }
      },
      // 备注
      souRemark: {
        type: 'string',
        title: i18nExpression('common.remark'),
        'x-render-table-column': {
          minWidth: 100
        }
      }
    })
  }
}

export default EnclosuresWrapSegment
