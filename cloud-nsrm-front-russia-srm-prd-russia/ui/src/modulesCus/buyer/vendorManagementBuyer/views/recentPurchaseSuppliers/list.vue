<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
const scope = {}
const components = {}
const schema = defineSchemas({
  PurchaseSuppliers: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          '@listener': expression(`() => {
            $queryEngine.state.pagenationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // '供应商名称'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          mdmCode: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.mdmCode')
          },
          socialCreditCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'),
            'x-query-engine-query-operator': 'contains'
          },
          type: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.type'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_SUPPLIERS_TYPE'
            }
          },
          recentCooperateTime: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.recentCooperateTime'),
            'x-component-props': {
              type: 'daterange'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: {
            'margin-bottom': '10px'
          }
        },
        properties: {
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup/api-ql/BlackCompany/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('PurchaseSuppliers.table', 'data.columns'),
              dictCodes: {
                type: 'PURCHASE_SUPPLIERS_TYPE'
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
          vendorId: { // 供应商ID
            type: 'string',
            'x-hidden': true
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // '供应商编码'
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // '供应商名称'
              minWidth: 120
            }
          },
          categoryId: {
            type: 'string',
            'x-hidden': true
          },
          categoryCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.categoryCode'),
              minWidth: 120
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.categoryCode'),
              minWidth: 120
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.orgName'),
              minWidth: 120
            }
          },
          socialCreditCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          recentCooperateTime: {
            type: 'string',
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('cusEntry.vendorMod.recentCooperateTime'),
            }
          },
          type: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_SUPPLIERS_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.type'),
              minWidth: 120
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="recentPurchaseSuppliers"
    :scope="scope"
    :schema="schema"
    :components="components"
  />
</template>
