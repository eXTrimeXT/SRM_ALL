/**
 * @description 技术文件
 */
import {
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction,
  expression,
  generateXindexInOrder
} from '@meicloud/render-engine'

interface TechnicalDocumentsProps {
  // 只读
  readonly: boolean;
}

export default function (props?: TechnicalDocumentsProps): Record<any, any> {
  const { readonly = false } = props || {}

  const selfReadonly = (flag?: boolean) => expression(`${flag ? '!' : ''}(${readonly} || $readonly)`)

  return {
    technicalDocumentsDialog: {
      type: 'void',
      title: i18nExpression('bidMod.technicalDocuments.title'),
      'x-component': 'RDialog',
      'x-component-props': {
        class: 'dialogMain',
        size: 'large',
        appendToBody: true,
        closeOnClickModal: false,
        footer: selfReadonly(true),
        okButtonText: i18nExpression('common.submit'),
        beforeClose: generateCharFunctionExpression(({ $form, $self, $t, $message }, done, type) => {
          if (!type || type === 'cancel') {
            done()
            return
          }

          $self.query($self.address.concat('itemFiles')).take((field: any) => {
            const list = field.value
            if (list.length === 0) {
              $message.warning($t('bidMod.technicalDocuments.fileListEmptyMessage'))
              return
            }

            // TODO 校验表格必填

            $form.values.itemList[$self.data.editRowIndex].itemFiles = list.concat()

            // 清空弹窗数据
            field.value = []
          })

          done()
        }),

        '@opened': generateCharFunctionExpression(async ({ $self, $sceneFileCompApi }) => {
          const state = $self.data
          let list: any[] = []
          if (state.editRow && Array.isArray(state.editRow.detailData) && state.editRow.detailData.length > 0) {
            // 带入旧数据
            list = state.editRow.detailData.concat()
          } else if (state.editRow.businessId) {
            // 从场景附件查询
            const response = await $sceneFileCompApi.sceneFileListAll({ businessId: state.editRow.businessId })
            if (response && response.data) {
              list = response.data.concat()
            }
          }
          console.log(list)

          $self.query($self.address.concat('itemFiles')).take((field: any) => {
            field.setValue(list)
          })
        })
      },

      'x-data': {
        editRow: null,
        editRowIndex: -1
      },

      properties: {
        toolbar: {
          type: 'void',
          'x-component': 'Space',
          'x-component-props': {
            style: 'margin-bottom: 16px'
          },
          'x-visible': selfReadonly(true),
          properties: {
            // TODO 从物料附件选择
            // drawing: {
            //   type: 'void',
            //   title: i18nExpression('bidMod.technicalDocuments.drawingButton'),
            //   'x-component': 'RButton',
            //   'x-component-props': {
            //     type: 'primary',
            //     '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
            //       $self.query($getFieldParentFieldFormPath($self, 2).concat('requireInfoTable'))
            //         .take(field => {
            //           field.componentProps.componentInstance.addRow()
            //         })
            //     })
            //   }
            // },

            // 新增
            add: {
              type: 'void',
              title: i18nExpression('common.add'),
              'x-component': 'RButton',
              'x-component-props': {
                type: 'primary',
                '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
                  $self.query($getFieldParentFieldFormPath($self, 2).concat('itemFiles')).take(field => {
                    field.componentProps.componentInstance.addRow('push')
                  })
                })
              }
            }
          }
        },

        // 表格
        itemFiles: {
          type: 'array',
          'x-component': 'RenderTable',
          'x-component-props': {
            preColumns: 'seq',
            class: 'table-view-vxe-table',
            openCustomTable: false,
            sortable: false,
            editMode: true,
            pagination: false
          },
          'x-read-pretty': selfReadonly(),
          properties: generateXindexInOrder({
            // 附件名称
            fileCustomName: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('bidMod.attachment'),
                minWidth: 100
              },
              'x-read-pretty': generateCharExpressionByFunction(({ $readonly, $self, $table }) => {
                return $readonly || $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
              })
            },

            // 附件
            fileName: {
              type: 'string',
              title: i18nExpression('bidMod.attachmentName'),
              'x-component': 'SrmCommonFile',
              'x-component-props': {
                defaultFile: {
                  fileId: generateCharExpressionByFunction(({ $self, $table }) => {
                    return !$self.value
                      ? undefined
                      : $table.getRowByIndex($self.index).fileuploadId
                  }),
                  fileName: generateCharExpressionByFunction(({ $self }) => $self.value && String($self.value))
                },
                // 标记来源物料图纸的不允许修改 item.attachmentType === 'DRAWINGSHEAD'
                // readonly: generateCharExpressionByFunction(({ $self, $table }) => {
                //   return $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
                // }),
                '@on-change': generateCharFunctionExpression(({ $self, $table }, { file }) => {
                  const row = $table.getRowByIndex($self.index)
                  row.fileuploadId = String(file.fileId)

                  setTimeout(() => {
                    $self.value = String(file.fileName)
                  })
                })
              },
              'x-read-pretty': expression('$readonly'),
              'x-render-table-column': {
                minWidth: 150
              }
            },

            // 附件地址
            fileLink: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('drawingshead.drawingAddress'),
                minWidth: 100
              },
              'x-read-pretty': generateCharExpressionByFunction(({ $readonly, $self, $table }) => {
                return $readonly || $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
              })
            },

            // 附件类型
            businessFileType: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('dataConfMod.attachmentType'),
                minWidth: 100
              },
              'x-read-pretty': generateCharExpressionByFunction(({ $readonly, $self, $table }) => {
                return $readonly || $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
              }),
              'x-component': 'DictSelect',
              'x-component-props': {
                code: 'DRAWING_TYPE'
              }
            },

            // 备注
            remark: {
              type: 'string',
              'x-render-table-column': {
                title: i18nExpression('common.remark'),
                minWidth: 100
              },
              'x-read-pretty': generateCharExpressionByFunction(({ $readonly, $self, $table }) => {
                return $readonly || $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
              })
            },

            operation: {
              type: 'void',
              'x-render-table-column': {
                title: i18nExpression('common.operation'),
                width: 150,
                fixed: 'right'
              },
              'x-visible': selfReadonly(true),
              'x-component': 'RenderTableButtonList',
              properties: {
                // 删除
                delete: {
                  type: 'void',
                  title: i18nExpression('common.delete'),
                  'x-component-props': {
                    disabled: generateCharExpressionByFunction(({ $table, $self }) => {
                      return $table.getRowByIndex($self.index)?.attachmentType === 'DRAWINGSHEAD'
                    }),
                    '@click': generateCharFunctionExpression(({ $table, $self }) => {
                      $table.remove($self.index)
                    })
                  }
                }
              }
            }
          })
        }
      }
    }
  }
}
