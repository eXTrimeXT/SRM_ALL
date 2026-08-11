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
import { transformMQL } from 'lib@/utils/util'

const { emitTabAdd, t: $t } = usePageHelper()

const scope = {
  transformMQL
}

const components = {
  AsyncFileExportButton,
  AsyncFileShowButton
}

const schema = defineSchemas({
  OsVendorInvBuyer: {
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
          eventName: 'OsVendorInvBuyer',
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
            title: i18nExpression('outsource.inventoryName1'), // 盘点名称
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
          vendorId: {
            type: 'string',
            title: i18nExpression('common.vendor'),  //'供应商'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info',
              showKey: 'companyName',
              propKey: 'companyId'
            }
          },
          orgId: {
            type: 'string',
            title: i18nExpression('components.organization.ORG'),  // '业务实体'
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
            title: i18nExpression('outsource.inventoryResults'),  // '盘点结果'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            }
          },
          invTime: {
            type: 'date',
            title: i18nExpression('outsource.inventoryTimeRange'),  //'盘点时间范围'
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
          export: {
            type: 'void',
            title: i18nExpression('components.export.shishidaochu'), // 实时库存导出
            'x-component': 'AsyncFileExportButton',
            'x-component-props': {
              code: 'osVendorInvBuyer:export',
              model: 'OS_VENDOR_REAL_TIME_INV_IMPORT_EXPORT',
              url: '/api-sup-ce/api-ql/OsVendorInvBuyer/exportExcel',
              transformQueryParams: expression(`transformMQL.save('OsVendorInvBuyer',[{
                ...$form.values.query
              }],'exportExcel')`),
              adaptMql: true
            }
          },
          download: {
            type: 'void',
            title: i18nExpression('components.export.dowloadList'), // 下载列表
            'x-component': 'AsyncFileShowButton',
            'x-component-props': {
              type: 'default',
              code: 'osVendorInvBuyer:download',
              title: i18nExpression('components.export.dowloadList'),  // '下载列表'
              mode: 'OS_VENDOR_REAL_TIME_INV_IMPORT_EXPORT'
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup-ce/api-ql/OsVendorInvBuyer/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('OsVendorInvBuyer.table', 'data.columns'),
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
          style: 'flex: 1',
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
              title: i18nExpression('outsource.inventoryName1'), // 盘点名称
              minWidth: 130
            },
            'x-query-engine-relation': 'osVendorInvTaskId',
            'x-query-engine-relation-strict': true
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.organization.ORG'), // 业务实体
              width: 160
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.organization.INV'), // 库存组织
              width: 160
            }
          },
          baseMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.materialCode'), // 委外组件编码
              width: 160
            }
          },
          baseMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.materialName'), // 委外组件名称
              width: 160
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // 供应商编码
              width: 160
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.companyName'), // 供应商名称
              width: 160
            }
          },
          invTime: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.invTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('outsource.inventoryTime'), // 盘点时间
              width: 160
            }
          },
          vendorInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.supplierInventory'), // 供方库存
              width: 160
            }
          },
          vendorConfirmInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.supplierConfirmsInventory'), // 供方确认库存
              width: 160
            }
          },
          invResult: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.inventoryResults'), // 盘点结果
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
  <RenderEngine :schema="schema" :scope="scope" :components="components" schemaKey="OsVendorInvBuyerHistory" />
</template>
