<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { exportExcelSegment, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission } from 'lib@/components/render-engine/schema-segments'
import relationSuppliersDetail from './detail'
const { emitTabAdd, emitTabRemove, t: $t, app, confirmDeleteMessage } = usePageHelper()
const schema = defineSchemas({
  RelationSupBuyer: {
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
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            return data
          }`)
        },
        delete: {
          action: 'delRelationSup'
        }
      }
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'relation',
          '@listener': expression(`() => {
            $queryEngine.state.pagenationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-component': 'QueryFormByQueryEngine',
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          vendorNameA: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.aCompanyName'),
            'x-query-engine-query-operator': 'contains'
          },
          vendorNameB: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.bCompanyName'),
            'x-query-engine-query-operator': 'contains'
          },
          createdId: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.applyer'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'userId'
            }
          },
          creationDate: {
            type: 'string',
            title: i18nExpression('common.creationTime'),
            'x-query-engine-query-operator': 'between',
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'daterange',
              valueFormat: 'yyyy-MM-dd'
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
              ...buttonListItemVisibleByPermission('sup:relationSuppliers:add'),
              '@click': expression(`() => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail',
                  params: {
                    flag: 'add',
                    tabName: 'relationSuppliersDetail'
                  },
                  title: $t('cusEntry.vendorMod.addRelationSuppilers')
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
              pageUrl: '/api-sup/api-ql/RelationSupBuyer/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('RelationSupBuyer.table', 'data.columns'),
              dictCodes: {
                type: 'RELATION_TYPE'
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
          vendorCodeA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.aCompanyCode'),
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeA,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeA
                  },
                  title: row.vendorCodeA
                })
              }`)
            }
          },
          socialCreditCodeA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.socialCreditCodeA'),
              minWidth: 120
            }
          },
          vendorNameA: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.aCompanyName'),
              minWidth: 120
            }
          },
          vendorCodeB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.bCompanyCode'),
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                emitTabAdd({
                  component: relationSuppliersDetail,
                  name: 'relationSuppliersDetail' + row.vendorCodeB,
                  params: {
                    flag: 'view',
                    row,
                    tabName: 'relationSuppliersDetail' + row.vendorCodeB
                  },
                  title: row.vendorCodeB
                })
              }`)
            }
          },
          socialCreditCodeB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.socialCreditCodeB'),
              minWidth: 120
            }
          },
          vendorNameB: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.bCompanyName'),
              minWidth: 120
            }
          },
          // associationType: {
          //   type: 'string',
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'RELATION_TYPE'
          //   },
          //   'x-render-table-column': {
          //     title: i18nExpression('cusEntry.vendorMod.type'),
          //     minWidth: 120
          //   }
          // },
          associationRemark: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.vendorMod.relationRemark'),
              minWidth: 120
            }
          },
          createdUserName: {
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
              width: 120,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:relationSuppliers:edit'),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    emitTabAdd({
                      component: relationSuppliersDetail,
                      name: 'relationSuppliersDetail',
                      params: {
                        flag: 'edit',
                        row,
                        tabName: 'relationSuppliersDetail'
                      },
                      title: $t('cusEntry.vendorMod.addRelationSuppilers')
                    })
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:relationSuppliers:delete'),
                  '@click': expression(`({ row }) => {
                    const Message = confirmDeleteMessage()
                    Message.then(res => {
                      $queryEngine.request.delete(row.associationId).then(() => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      }).catch((e) => {
                        console.log(e)
                      })
                    }).catch(() => {})
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
})
const scope = {
  relationSuppliersDetail,
  emitTabAdd,
  $t,
  app,
  confirmDeleteMessage
}
const components = {}
</script>

<template>
  <RenderEngine
    schemaKey="relationSuppliers"
    :scope="scope"
    :schema="schema"
    :components="components"
  />
</template>
