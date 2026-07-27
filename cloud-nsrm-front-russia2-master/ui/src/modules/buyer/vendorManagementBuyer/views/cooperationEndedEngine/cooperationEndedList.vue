<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps, queryFieldValueExpression, queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import endDetail from './cooperationEndedDetail'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  OrgCatForm: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'cooperationEnd',
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
          orgCatFormNumber: {
            type: 'string',
            title: i18nExpression(`vendorMod.controlNumber`), // '控制单号'
            'x-query-engine-query-operator': 'contains'
          },
          vendorId: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          approveStatus: {
            type: 'string',
            title: i18nExpression('common.status'), // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            }
          },
          supplierControlType: {
            type: 'string',
            title: i18nExpression('vendorMod.controlType'), // 控制类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_CONTROL_TYPE2'
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
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'add',
                    tabName: 'endDetail'
                  },
                  title: $t('vendorMod.addSite'),
                  name: 'endDetail'
                }
                emitTabAdd(tab)
              }`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          approveStatus: {
            type: 'string',
            title: i18nExpression('common.status'), // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          vendorId: {
            type: 'string',
            'x-hidden': true,
            'x-render-table-column': {
              width: 120
            }
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'), // 供应商编码
            'x-render-table-column': {
              width: 120
            }
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // 供应商名称
            'x-render-table-column': {
              width: 120
            }
          },
          orgCatFormId: {
            type: 'string',
            'x-hidden': true,
            'x-render-table-column': {
              width: 120
            }
          },
          orgCatFormNumber: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let orgCatFormId = row.orgCatFormId
                let tab = {
                  component: endDetail,
                  params: {
                    flag: 'view',
                    orderId: orgCatFormId,
                    tabName: 'CooperationEndedDetail' + row.vendorName
                  },
                  title: row.vendorName,
                  name: 'CooperationEndedDetail' + row.vendorName
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.controlNumber'), // 控制单号
              minWidth: 200,
              customRender: true
            }
          },
          supplierControlType: {
            type: 'string',
            title: i18nExpression('vendorMod.controlType'), // 控制类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_CONTROL_TYPE2'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          createdBy: {
            type: 'string',
            title: i18nExpression('common.creator'), // 创建人
            'x-render-table-column': {
              width: 120
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
            title: i18nExpression('common.creationTime'), // 创建时间
            'x-render-table-column': {
              width: 120
            }
          },
          startDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('common.effectTime'), // 生效时间
            'x-render-table-column': {
              width: 120
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              width: 120
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 200,
              fixed: 'right'
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // '编辑'
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'edit',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  style: `margin-left: 8px`,
                  showPopconfirm: true,
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.orgCatFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              doApproval: {
                type: 'void',
                title: i18nExpression('vendorMod.doApproval'), // '审批'
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['SUBMITTED'].includes($deps[0]) && $buyer()`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'view',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.abandon'), // '废弃'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['REJECTED', 'WITHDRAW'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const orgCatFormId = row.orgCatFormId
                    const tab = {
                      component: endDetail,
                      params: {
                        flag: 'view',
                        orderId: orgCatFormId,
                        tabName: 'CooperationEndedDetail' + row.vendorName,
                        row
                      },
                      title: row.vendorName,
                      name: 'CooperationEndedDetail' + row.vendorName
                    }
                    emitTabAdd(tab)
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
  emitTabAdd,
  app,
  i18nExpression,
  endDetail
}

const components = {

}

</script>

<template>
  <RenderEngine schemaKey="cooperationList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

