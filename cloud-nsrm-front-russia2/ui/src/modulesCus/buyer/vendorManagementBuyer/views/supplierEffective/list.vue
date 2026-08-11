
<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { exportExcelSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import supplierEffectiveDetial from './detail'
const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()
const schema = defineSchemas({
  effectForm: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          autoFormatResult: true,
          immediate: true
        }
      }
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'RefreshSuppliersEffectiveList',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-component': 'QueryFormByQueryEngine',
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          effectFormNumber: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.effectFormNumber'),
          },
          quaReviewType: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.quaReviewType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUA_REVIEW_TYPE'
            }
          },
          createdId: {
            type: 'string',
            title: i18nExpression('common.creator'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'userId'
            }
          },
          companyName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // '供应商名称'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          approveTime: {
            type: 'date',
            title: i18nExpression('cusEntry.vendorMod.approveTime'),
            'x-component-props': {
              type: 'daterange'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component': 'Button',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                emitTabAdd({
                  component: supplierEffectiveDetial,
                  name: 'supplierEffectiveDetial',
                  params: {
                    flag: 'add',
                    tabName: 'supplierEffectiveDetial'
                  },
                  title: $t('cusEntry.vendorMod.addSupplierEffective')
                })
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup/api-ql/effectForm/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('effectForm.table', 'data.columns'),
              dictCodes: {
                quaReviewType: 'QUA_REVIEW_TYPE',
                approveStatus: 'APPROVE_STATUS_TYPE'
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
          effectFormId: {
            type: 'string',
            'x-hidden': true
          },
          effectFormNumber: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.effectFormNumber'),
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => {
                emitTabAdd({
                  component: supplierEffectiveDetial,
                  name: 'supplierEffectiveDetial' + row.effectFormNumber,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'supplierEffectiveDetial' + row.effectFormNumber
                  },
                  title: row.effectFormNumber
                })
              }`)
            }
          },
          companyName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'),
              minWidth: 120
            }
          },
          quaReviewType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUA_REVIEW_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.quaReviewType'),
              minWidth: 120
            }
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.approveStatus'),
              minWidth: 120
            }
          },
          approveTime: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.approveTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.approveTime'),
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.creator'), // '创建人'
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('common.creationTime'), // '创建时间'
              minWidth: 120
            },
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 120,
              fixed: 'right'
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    emitTabAdd({
                      component: supplierEffectiveDetial,
                      name: 'supplierEffectiveDetial' + row.effectFormNumber,
                      params: {
                        flag: 'edit',
                        row,
                        tabName: 'supplierEffectiveDetial' + row.effectFormNumber
                      },
                      title: row.effectFormNumber
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  '[\'DRAFT\', \'REJECTED\', \'WITHDRAW\'].includes($deps[0])'
                )
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  showPopconfirm: true,
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.effectFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  '[\'DRAFT\'].includes($deps[0])'
                )
              }
            }
          }
        })
      }
    }
  }
})
const scope = {
  emitTabAdd,
  $t,
  supplierEffectiveDetial
}
const components = {}
</script>

<template>
  <RenderEngine
    schemaKey="supplierEffective"
    :scope="scope"
    :schema="schema"
    :components="components"
  />
</template>
