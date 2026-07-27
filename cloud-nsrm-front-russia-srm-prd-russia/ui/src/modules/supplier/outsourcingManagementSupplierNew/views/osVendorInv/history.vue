<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import AsyncFileExportButton from 'lib@/components/async-import-export/asyncFileExportButton.vue'
import AsyncFileShowButton from 'lib@/components/async-import-export/asyncFileShowButton.vue'

const { emitTabAdd, t: $t, getCurrentUserInfo } = usePageHelper()

const scope = {
  userInfo: getCurrentUserInfo()
}

const components = {
  AsyncFileExportButton,
  AsyncFileShowButton
}

const schema = defineSchemas({
  OsVendorInv: {
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
          immediate: true,
          transformRequest: expression(`(data,header) => {
            data.payload.filter = {
              ...data.payload.filter,
              vendorId:{
                eq:userInfo.companyId
              }
            }
            return data
          }`)
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'OsVendorInv',
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
          invTaskTitle: {
            type: 'string',
            title: '盘点名称', // 盘点名称
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'osVendorInvTaskId',
            'x-query-engine-relation-strict': true
          },
          baseMaterialId: {
            type: 'string',
            title: i18nExpression('common.materialCode'), // 物料编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              showKey: 'materialCode',
              propKey: 'materialId'
            }
          },
          orgId: {
            type: 'string',
            title: '业务实体',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect')
            }
          },
          invResult: {
            type: 'string',
            title: '盘点结果',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            }
          },
          invTime: {
            type: 'date',
            title: '盘点时间范围',
            'x-component-props': {
              type: 'datetimerange',
              format: 'yyyy-MM-dd HH:mm:ss',
              'value-format': 'yyyy-MM-dd',
              'default-time': ['00:00:00', '23:59:59']
            },
            'x-query-engine-query-operator': 'between'
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
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'primary',
              pageUrl: '/api-sup-ce/api-ql/OsVendorInv/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('OsVendorInv.table', 'data.columns'),
              dictCodes: {
                invResult: 'SC_OS_VENDOR_INV_RESULT'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1;',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          osVendorInvId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          invTaskTitle: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点名称', // 盘点名称
              minWidth: 130
            },
            'x-query-engine-relation': 'osVendorInvTaskId',
            'x-query-engine-relation-strict': true
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: '业务实体', // 业务实体
              width: 160
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: '库存组织', // 库存组织
              width: 160
            }
          },
          baseMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: '委外组件编码', // 委外组件编码
              width: 160
            }
          },
          baseMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: '委外组件名称', // 委外组件名称
              width: 160
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: '供应商编码', // 供应商编码
              width: 160
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: '供应商名称', // 供应商名称
              width: 160
            }
          },
          invTime: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点时间', // 盘点时间
              width: 160
            }
          },
          vendorInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: '供方库存', // 供方库存
              width: 160
            }
          },
          vendorConfirmInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: '供方确认库存', // 供方确认库存
              width: 160
            }
          },
          invResult: {
            type: 'string',
            'x-render-table-column': {
              title: '盘点结果', // 盘点结果
              minWidth: 130
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" :components="components" schemaKey="OsVendorInvHistory" />
</template>
