<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldValueExpression,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import $dayjs from 'dayjs'
import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'

const scope = {
  $dayjs
}

const schema = defineSchemas({
  DrawingsHead: {
    type: 'void',
    'x-query-engine': {
      service: 'base',
      actions: {
        paginationQuery: { immediate: true },
        save: {
          loading: true
        }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'PageContainer',
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          materialCode: {
            type: 'string',
            title: i18nExpression('common.materialCode'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialCode',
              propKey: 'materialCode',
              name: 'scc_base_material_item'
            }
          },
          drawingsVersion: {
            type: 'number',
            title: i18nExpression('drawingshead.drawingVersion'),
            'x-component-props': {
              type: 'number',
              negative: false,
              zero: false
            }
          },
          drawingsStatus: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DRAWINGS_STATUS'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'), // 新增
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $form.query(".table").take().componentProps.componentInstance.addRow("unshift")')
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-base/api-ql/DrawingsHead/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('DrawingsHead.table', 'data.columns'),
              dictCodes: {
                drawingsType: 'DRAWING_TYPE',
                isLatest: 'DRAWING_IS_LATEST',
                drawingsStatus: 'DRAWINGS_STATUS'
              }
            }
          },
          activeHandel: {
            type: 'void',
            title: i18nExpression('common.active'), // 生效
            'x-component': 'RButton',
            'x-component-props': {
              type: 'normal',
              '@click': expression(`({ rowIndex }) => {
                const rows = $self.query('.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()
                rows.forEach(val => {
                  val.drawingsStatus = 'Y'
                })
                $queryEngine.request.save(rows, { action: 'takeEffect', loading: true }).then(() => {
                    $queryEngine.state.paginationManagement.refresh()
                })
              }`)
            }
          },
          inactiveHandel: {
            type: 'void',
            title: i18nExpression('common.inactive'), // 失效
            'x-component': 'RButton',
            'x-component-props': {
              type: 'normal',
              '@click': expression(`({ rowIndex }) => {
                const rows = $self.query('.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()
                rows.forEach(val => {
                  val.drawingsStatus = 'N'
                })
                $queryEngine.request.save(rows, { action: 'failure', loading: true }).then(() => {
                    $queryEngine.state.paginationManagement.refresh()
                })
              }`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'checkbox,seq',
          editMode: 'multi-row',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          drawingsId: {
            type: 'string',
            'x-hidden': true
          },
          materialCode: {
            type: 'string',
            title: i18nExpression('common.materialCode'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              'show-key': 'materialCode',
              'readPretty': expression(`!$table.getSelfRowEditable($self)`),
              '@close-quicksearch': expression(`(val) => {
                  const row = $table.getRowByIndex($self.index)
                  row.materialId = val.materialId || null
                  row.materialCode = val.materialCode || null
                  row.materialName = val.materialName || null
              }`)
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-render-table-column': {
              minWidth: 120,
              skipEditable: true
            }
          },
          drawingsType: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DRAWING_TYPE'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          drawingsVersion: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingVersion'),
            'x-render-table-column': {
              minWidth: 120,
              skipEditable: true
            }
          },
          isLatest: {
            type: 'string',
            title: i18nExpression('drawingshead.isItCurrent'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DRAWING_IS_LATEST'
            },
            'x-render-table-column': {
              minWidth: 130,
              skipEditable: true
            }
          },
          fileuploadAddress: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingAddress'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          fileuploadId: {
            type: 'number',
            'x-hidden': true,
            default: null,
            'x-render-table-column': {
              minWidth: 100
            }
          },
          attachType: {
            type: 'string',
            'x-hidden': true,
            'x-render-table-column': {
              minWidth: 100
            }
          },
          attachName: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingAttachment'),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              extraData: {
                fileModular: 'workFlow', // 文件所属模块 -》审批流程
                fileFunction: 'workflowReport', // 审批流相关文件
                fileType: 'images', // 文件所属类型
                fileMaxSize: 10 * 1024 * 1024
              },
              readonly: false,
              defaultFile: expression(`{
                fileId: $table.getRowByIndex($self.index)?.fileuploadId,
                fileName: $self.value
              }`),
              '@on-change': expression(`({ file }) => {
                const { fileId , fileName , fileType } = file
                const row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.attachName = fileName
                row.attachType = fileType
              }`)
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },
          drawingsStatus: {
            type: 'string',
            title: i18nExpression('drawingshead.drawingStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DRAWINGS_STATUS'
            },
            'x-render-table-column': {
              minWidth: 130
            }
          },
          creationDate: {
            type: 'string',
            title: i18nExpression('common.creationTime'),
            'x-render-table-column': {
              minWidth: 130,
              skipEditable: true
            },
            'x-query-engine-sort': 'desc'
          },
          lastUpdateDate: {
            type: 'string',
            title: i18nExpression('common.updateTime'),
            'x-render-table-column': {
              minWidth: 140,
              skipEditable: true
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              editRow: {
                type: 'void',
                title: i18nExpression('common.save'),
                'x-visible': expression(`$table.getSelfRowEditable($self)`),
                'x-component-props': {
                  '@click': expression(`({ row }) => $queryEngine.request.save(row).then(() => {
                    $table.cancelEditRow($index)
                    $queryEngine.state.paginationManagement.refresh()
                  })`)
                }
              },
              cancelEditRow: {
                type: 'void',
                title: i18nExpression('common.cancel'),
                'x-visible': expression(`$table.getSelfRowEditable($self)`),
                'x-component-props': {
                  '@click': expression('({ rowIndex }) => $table.cancelEditRow(rowIndex)')
                }
              }
            }
          }
        })
      }
    }
  }
})

const components = {

}
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :components="components"
    :scope="scope"
    schemaKey="contractElement"
  />
</template>
