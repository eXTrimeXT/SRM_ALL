/**
 * @description 内外部附件
 */
import {
  methodExpression,
  generateXindexInOrder,
  i18nExpression,
  generateCharFunctionExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

// 查看附件
const EnclosuresWrapSegment: Record<any, any> = {
  enclosuresWrap: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'enclosures-wrap'
    },
    properties: {
      // 内部附件
      innerFileList: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          style: 'width: 50%; padding: 3px; float: left;'
        },
        properties: {
          // 按钮
          toolbar: {
            type: 'void',
            'x-component': 'Space',
            'x-component-props': {
              style: 'margin-bottom: 16px'
            },
            properties: {
              // 新增
              add: {
                type: 'void',
                title: i18nExpression('common.add'),
                'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
                'x-component': 'RButton',
                'x-component-props': {
                  type: 'primary',
                  '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
                    $self.query($getFieldParentFieldFormPath($self, 2).concat('innerFile')).take(field => {
                      field.componentProps.componentInstance.addRow('push', { fileType: 'INNER' })
                    })
                  })
                }
              },
              toolbarTips: {
                type: 'void',
                'x-component': 'span',
                'x-content': i18nExpression('bidMod.innerFileList'),
                'x-component-props': {
                  style: 'margin-left: 10px;'
                }
              }
            }
          },
          innerFile: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              preColumns: 'seq',
              pagination: false,
              sortable: false,
              editMode: true
            },
            'x-reactions': {
              dependencies: ['fileList'],
              fulfill: {
                state: {
                  value: generateCharExpressionByFunction(({ $form }) => {
                    return $form.values.fileList && $form.values.fileList.filter((item: any) => item.fileType === 'INNER').concat()
                  })
                }
              }
            },
            properties: generateXindexInOrder({
              // 文件类型
              fileType: {
                type: 'string',
                default: 'INNER',
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
                  },
                  '@on-change': generateCharFunctionExpression(({ $self, $table }, { file }) => {
                    const row = $table.getRowByIndex($self.index)
                    row.souDocId = String(file.fileId)

                    setTimeout(() => {
                      $self.value = String(file.fileName)
                    })
                  })
                },
                'x-read-pretty': methodExpression('$readonly'),
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              // 备注
              souRemark: {
                type: 'string',
                title: i18nExpression('common.remark'),
                'x-disabled': methodExpression('$readonly'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  controls: false
                }
              },
              operation: {
                type: 'void',
                title: i18nExpression('common.operation'),
                'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
                'x-render-table-column': {
                  width: 80,
                  fixed: 'right'
                },
                'x-component': 'RenderTableButtonList',
                properties: {
                  // 删除
                  delete: {
                    type: 'void',
                    title: i18nExpression('common.delete'),
                    'x-component-props': {
                      '@click': generateCharFunctionExpression(({ $table, $self }) => $table.remove($self.index))
                    }
                  }
                }
              }
            })
          }
        }
      },

      // 外部附件
      outerFileList: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          style: 'width: 50%; padding: 3px; float: left;'
        },
        properties: {
          // 按钮
          toolbar: {
            type: 'void',
            'x-component': 'Space',
            'x-component-props': {
              style: 'margin-bottom: 16px'
            },
            properties: {
              // 新增
              add: {
                type: 'void',
                title: i18nExpression('common.add'),
                'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
                'x-component': 'RButton',
                'x-component-props': {
                  type: 'primary',
                  '@click': generateCharFunctionExpression(({ $self, $getFieldParentFieldFormPath }) => {
                    $self.query($getFieldParentFieldFormPath($self, 2).concat('outerFile')).take(field => {
                      field.componentProps.componentInstance.addRow('push', { fileType: 'OUTER' })
                    })
                  })
                }
              },
              toolbarTips: {
                type: 'void',
                'x-component': 'span',
                'x-content': i18nExpression('bidMod.supplierFileList'),
                'x-component-props': {
                  style: 'margin-left: 10px;'
                }
              }
            }
          },

          outerFile: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              preColumns: 'seq',
              pagination: false,
              sortable: false,
              editMode: true
            },
            'x-reactions': {
              dependencies: ['fileList'],
              fulfill: {
                state: {
                  value: generateCharExpressionByFunction(({ $form }) => {
                    return $form.values.fileList && $form.values.fileList.filter((item: any) => item.fileType === 'OUTER').concat()
                  })
                }
              }
            },
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
                  },
                  '@on-change': generateCharFunctionExpression(({ $self, $table }, { file }) => {
                    const row = $table.getRowByIndex($self.index)
                    row.souDocId = String(file.fileId)

                    setTimeout(() => {
                      $self.value = String(file.fileName)
                    })
                  })
                },
                'x-read-pretty': methodExpression('$readonly'),
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              // 备注
              souRemark: {
                type: 'string',
                title: i18nExpression('common.remark'),
                'x-disabled': methodExpression('$readonly'),
                'x-render-table-column': {
                  minWidth: 100
                },
                'x-component-props': {
                  controls: false
                }
              },
              operation: {
                type: 'void',
                title: i18nExpression('common.operation'),
                'x-visible': generateCharExpressionByFunction(({ $readonly }) => !$readonly),
                'x-render-table-column': {
                  width: 80,
                  fixed: 'right'
                },
                'x-component': 'RenderTableButtonList',
                properties: {
                  // 删除
                  delete: {
                    type: 'void',
                    title: i18nExpression('common.delete'),
                    'x-component-props': {
                      '@click': generateCharFunctionExpression(({ $table, $self }) => $table.remove($self.index))
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
}

export default EnclosuresWrapSegment
