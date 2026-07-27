<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import Preview from "./preview.vue"
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import Edit from "./edit_engine.vue"


const schema = defineSchemas({
  ModelHead: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ModelHead',
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
          modelCode: {
            type: 'string',
            title: "{{$t('dataConfMod.templateCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          modelName: {
            type: 'string',
            title: "{{$t('contractMod.templHeadId')}}",
            'x-query-engine-query-operator': 'contains'
          },
          status: {
            type: 'string',
            title: "{{$t('common.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_MODEL_STATUS'
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
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': '{{() => $edit({}, "add")}}'
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
          modelHeadId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          creationDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          content: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          modelCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({row}) => $edit(row, "view")')
            },
            'x-render-table-column': {
              title: "{{$t('dataConfMod.templateCode')}}",
              minWidth: 130,
              customRender: true
            }
          },
          modelName: {
            type: 'string',
            title: "{{$t('contractMod.templHeadId')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          modelType: {
            type: 'string',
            title: "{{$t('contractMod.templType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          // ceeaControlMethod: {
          //   type: 'string',
          //   title: "{{$t('contractMod.controlMethod')}}",
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'MANAGEMENT_CONTROL_MODEL'
          //   },
          //   'x-render-table-column': {
          //     width: 130
          //   }
          // },
          startDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('basicPrice.effectiveDateFrom')}}",
            'x-render-table-column': {
              width: 100
            }
          },
          endDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('basicPrice.effectiveDateTo')}}",
            'x-render-table-column': {
              width: 100
            }
          },
          status: {
            type: 'string',
            title: "{{$t('contractMod.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_MODEL_STATUS',
              showType: 'statusCol', // 标识状态列
              statusList: { // 状态分类入参
                green: ['VALID'],
                red: [],
                orange: ['INVALID'],
                invalid: ['FREEZE']
              }
            },
            'x-render-table-column': {
              width: 100
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 170,
              fixed: 'right',
              showOverflow: false
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              copy: {
                type: 'void',
                title: "{{$t('common.copy')}}",
                'x-component-props': {
                  '@click': expression('(row) => $copy(row, $queryEngine)')
                }
              },
              preview: {
                type: 'void',
                title: "{{$t('common.preview')}}",
                'x-component-props': {
                  '@click': expression('({ row }) => $preview(row)')
                }
              },

              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression('({ row }) => $edit(row, "edit")')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT', 'FREEZE'].includes($deps[0])`
                )
              },
              active: {
                type: 'void',
                title: "{{$t('common.active')}}",
                'x-component-props': {
                  '@click': expression(`({ row })=>
                    $queryEngine.request.update({
                      modelHeadId: row.modelHeadId,
                      status: 'VALID'
                    }).then(() => {
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  `)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT', 'FREEZE'].includes($deps[0])`
                )
              },
              inactive: {
                type: 'void',
                title: "{{$t('common.inactive')}}",
                'x-component-props': {
                  '@click': expression('({row}) => $inactive(row, $queryEngine)')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['VALID'].includes($deps[0])`
                )
              },
              freeze: {
                type: 'void',
                title: "{{$t('contractMod.freeze')}}",
                'x-component-props': {
                  '@click': expression('({row}) => $freeze(row, $queryEngine)')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['VALID'].includes($deps[0])`
                )
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression('({row}) => $delete(row, $queryEngine)')
                }
              }
            }
          }
        })
      }
    }
  }
})


const { emitTabAdd, t } = usePageHelper()

const $preview = (row: any) => {
  emitTabAdd({
    component: Preview,
    params: { row },
    title: t('common.preview') + `${row.modelName ? '-' + row.modelName : ''}`,
    name: `preview_${row.modelHeadId ? row.modelHeadId : ''}`
  })
}

const $edit = (row: any, flag: string) => {
  let tab = {
    component: Edit,
    params: { row, flag },
    title: t('common.edit') + `${row.modelName ? '-' + row.modelName : ''}`,
    name: `${flag}_${row.modelHeadId ? row.modelHeadId : ''}`
  }
  if (flag === 'add') {
    tab.title = t('common.add') as string
    tab.name = 'add'
  }
  if (flag === 'view') {
    tab.title = t('common.view') as string
  }
  emitTabAdd(tab)
}

const $inactive = (row: any, queryEngine: any) => {
  let obj = {
    modelHeadId: row.modelHeadId,
    status: 'INVALID'
  }
  queryEngine.request['update'](obj).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}

const $freeze = (row: any, queryEngine: any) => {
  let obj = {
    modelHeadId: row.modelHeadId,
    status: 'FREEZE'
  }
  queryEngine.request['update'](obj).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}

const $delete = (row: any, queryEngine: any) => {
  queryEngine.request['delete'](row.modelHeadId).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}

const $copy = (row: any, queryEngine: any) => {
  queryEngine.request.read(row.row.modelHeadId, { query: {"*":{}} }).then((res:any) => {
    const data = res.data[0]
    data.status = 'DRAFT'
    delete data.modelHeadId

    queryEngine.request.create(data).then(() => {
      queryEngine.state.paginationManagement.refresh()
    })
  })
}

const scope = {
  $preview,
  $edit,
  $inactive,
  $freeze,
  $delete,
  $copy
}
</script>

<template>
  <RenderEngine schemaKey="contractModeList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
