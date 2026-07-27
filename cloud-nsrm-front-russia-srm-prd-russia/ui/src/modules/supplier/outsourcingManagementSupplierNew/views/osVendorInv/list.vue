<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import history from './history'
import manage from './manage'

const { emitTabAdd, t: $t } = usePageHelper()

// 查看历史
const $listHistory = () => {
  $editTab('history', {})
}

// 管理
const $manage = (row, $queryEngine, $message) => {
  $editTab('manage', row)
}

const $editTab = (flag: string, row: any) => {
  const map = new Map([
    [
      'history',
      {
        component: history,
        params: {
          flag,
          row,
          tabName: 'osVendorInvHistory'
        },
        title: '查看历史',
        name: 'osVendorInvHistory'
      }
    ],
    [
      'manage',
      {
        component: manage,
        params: {
          flag,
          row,
          tabName: 'osVendorInvManage' + row.invTaskNo
        },
        title: '盘点管理' + row.invTaskNo,
        name: 'osVendorInvManage' + row.invTaskNo
      }
    ]
  ])
  const tab = map.get(flag)
  emitTabAdd(tab)
}

const scope = {
  $listHistory,
  $manage
}

const schema = defineSchemas({
  OsVendorInvTask: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'OsVendorInvTask',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          invTaskNo: {
            type: 'string',
            title: '盘点流水号',
            'x-query-engine-query-operator': 'contains'
          },
          invTaskTitle: {
            type: 'string',
            title: '盘点名称', // 盘点名称
            'x-query-engine-query-operator': 'contains'
          },
          taskStatus: {
            type: 'string',
            title: '盘点状态',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_TASK_STATUS'
            }
          },
          creationDate: {
            type: 'date',
            title: '创建时间',
            'x-component-props': {
              type: 'datetimerange',
              'format': 'yyyy-MM-dd HH:mm:ss',
              'value-format': 'yyyy-MM-dd',
              'default-time': ['00:00:00', '23:59:59']
            },
            'x-query-engine-query-operator': 'between'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          listHistory: {
            type: 'void',
            title: '查看历史', // 查看历史
            'x-component-props': {
              ...buttonListItemVisibleByPermission('osVendorInv:history'),
              type: 'primary',
              '@click': expression('() => $listHistory()')
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          osVendorInvTaskId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          invTaskNo: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点流水号' // 盘点流水号
            }
          },
          invTaskTitle: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点名称' // 盘点名称
            }
          },
          taskStatus: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点状态' // 盘点状态
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_TASK_STATUS'
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: '创建人' // 创建人
            }
          },
          creationDate: {
            type: 'string',
            'x-render-table-column': {
              title: '创建时间' // 创建时间
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 130,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              manage: {
                type: 'void',
                title: '管理', // 管理
                // 盘点中
                'x-reactions': changeFieldVisibleByDeps(
                  ['.taskStatus'],
                  '$deps[0] === \'INV_ING\''
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('osVendorInv:manage'),
                  '@click': expression('({ row }) => $manage(row, $queryEngine, $message)')
                }
              }
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="OsVendorInvList" />
</template>
