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
    title: type == 'add' ? '新增单据' : row.blackCode,
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
          onSuccess: expression(`(res) => {

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
              code: 'APPROVE_STATUS_TYPE'
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
                approveStatus: 'APPROVE_STATUS_TYPE'
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
            "x-content": '本功能用于黑名单供应商在列黑后的申诉与黑名单的解除。'
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
              code: 'APPROVE_STATUS_TYPE'
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
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  "['DRAFT'].includes($deps[0])"
                ),
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
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  "$deps[0] === 'DRAFT'"
                ),
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
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
