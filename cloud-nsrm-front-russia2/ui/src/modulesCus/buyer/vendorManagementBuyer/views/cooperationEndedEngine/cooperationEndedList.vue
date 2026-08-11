<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  buttonListItemVisibleByPermission,
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
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.orgCatFormId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)
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
            title: i18nExpression('vendorMod.controlNumber'), // '控制单号'
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
              code: 'PJ_APPROVE_STATUS_TYPE'
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
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('sup:cooperationEndList:add'),
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
              code: 'PJ_APPROVE_STATUS_TYPE'
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
                    tabName: 'CooperationEndedDetail' + row.vendorName,
                    row,
                  },
                  title: row.vendorName,
                  name: 'CooperationEndedDetail' + row.vendorName
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.controlNumber'), // 控制单号
              minWidth: 180,
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
          createdUserName: {
            type: 'string',
            title: i18nExpression('common.creator'), // 创建人
            'x-render-table-column': {
              width: 200
            }
          },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('common.creationTime'), // 创建时间
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
              width: 150,
              fixed: 'right'
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // '编辑'
                'x-component': 'TableButton',
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),
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
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),
                'x-component-props': {
                  style: 'margin-left: 8px',
                  showPopconfirm: true,
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.orgCatFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              // 审批
              approve: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-component': 'TableButton',
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED'].includes(row.approveStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),
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

