<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression, i18nExpression
} from '@meicloud/render-engine'
import { exportExcelSegment, RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { yearMonthDaySelectorSegment, buttonListItemVisibleByPermission } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any) => {
  let tabName = type == 'add' ? 'blackEdit' : 'blackEdit' + row.blackId
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      tabName: tabName
    },
    title: type == 'add' ? $t('vendorMod.addDocument') : row.blackCode,
    name: tabName
  })
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.rescindId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  BlackRescind: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`async (res) => {
            const queryTodoRes = await app.$api.base.flowAPI.queryTodo()
            let queryTodoList = queryTodoRes.data || []
            $form.values.table = res.data.map(item => {
              let obj = queryTodoList.find(todoItem => item.rescindId + '' === todoItem.businessId + '')
              return { ...item, isApprover: obj ? 'Y' : 'N' }
            })
          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'BlackRescind',
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
          rescindCode: {
            type: 'string',
            title: i18nExpression(`vendorMod.relegation.receiptNum`),
            'x-query-engine-query-operator': 'contains'
          },
          rescindName: {
            type: 'string',
            title: i18nExpression(`vendorMod.relegation.billName`),
            'x-query-engine-query-operator': 'contains'
          },
          approveStatus: {
            type: 'string',
            title: i18nExpression('vendorMod.relegation.documentStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PJ_APPROVE_STATUS_TYPE'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.addSecure')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $addOne()
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup/api-ql/BlackRescind/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('BlackRescind.table', 'data.columns'),
              dictCodes: {
                approveStatus: 'PJ_APPROVE_STATUS_TYPE'
              }
            }
          },
          tips: {
            type: 'void',
            "x-component": 'div',
            "x-component-props": {
              style: {
                display: 'inline-block',
                color: '#D9001B'
              }
            },
            "x-content": i18nExpression('vendorMod.blackListTips3') // 本功能用于黑名单供应商在列黑后的申诉与黑名单的解除。
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
          rescindId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          rescindCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let tab = {
                  component: edit,
                  params: {
                    flag: 'view',
                    row: row,
                    tabName: 'edit' + row.rescindCode || row.rescindId
                  },
                  title: row.rescindName,
                  name: 'edit' + row.rescindCode
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression(`vendorMod.relegation.receiptNum`),
              minWidth: 120,
              customRender: true
            }
          },
          rescindName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression(`vendorMod.relegation.billName`),
              minWidth: 120
            }
          },
          rescindContent: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression(`vendorMod.relegation.sketch`),
              minWidth: 120
            }
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PJ_APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.documentStatus')}}",
              minWidth: 100
            }
          },
          createdBy: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            'x-render-table-column': {
              fixed: 'right',
              width: 120
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('base:black:edit'),
                  '@click': expression(`({row}) => {
                    let tab = {
                      component: edit,
                      params: {
                        flag: 'edit',
                        row: row,
                        tabName: 'edit' + row.rescindCode || row.rescindId
                      },
                      title: row.rescindName,
                      name: 'edit' + row.rescindCode
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['DRAFT'].includes(row.approveStatus) && app.$store.getters.userInfo.userId == row.createdId
                }`),
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                }
              },
              // 审批
              approve: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-reactions': expression(`(field) => {
                  const row = $table.getRowByIndex($self.index)
                  field.visible = ['SUBMITTED'].includes(row.approveStatus) && (app.$store.getters.userInfo.userId == row.createdId || row.isApprover == 'Y')
                }`),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let tab = {
                      component: edit,
                      params: {
                        flag: 'view',
                        row: row,
                        tabName: 'view' + row.rescindCode || row.rescindId
                      },
                      title: row.rescindName,
                      name: 'view' + row.rescindCode
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

// @ts-ignore
const components = {
}

const scope = {
  app,
  $addOne,
  $editOne,
  $delete,
  $detailOne,
  emitTabAdd,
  edit
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    schemaKey="BlackList"
    :schema="schema"
  />
</template>
