<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  changeFieldVisibleByDeps,
} from '@meicloud/render-engine'
import {
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import Edit from './edit.vue'
// @ts-ignore
import performanceTpl from '@/service/modules/cmPerform/buyer/main'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $handleOne = (row: any, flag: string) => {
  let tab = {
    component: Edit,
    params: {
      row,
      flag,
      tabName: 'contractPerformanceProcessConfigEdit' + (row.perTemplHeadId || ''),
    },
    title: row.processNum,
    name: `${flag}_${row.perTemplHeadId ? row.perTemplHeadId : ''}`,
  }
  if (flag === 'add') {
    tab.title = $t('common.add') as string
  }
  if (flag === 'view') {
    tab.title = `${$t('common.view')} - ${row.processNum}` as string
  }
  emitTabAdd(tab)
}

const schema = defineSchemas({
  PerTemplHead: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true },
      },
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical',
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'PerTemplHead',
          '@listener': expression(`() => $queryEngine.state.paginationManagement.refresh()`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          labelCol: 9,
        },
        properties: generateXindexInOrder({
          contractType: {
            type: 'string',
            title: i18nExpression('contract_mod.contractType'),
            // 'x-query-engine-query-operator': 'contains',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE',
            },
          },
          status: {
            type: 'string',
            title: i18nExpression('contract_mod.configStatus'),
            // 'x-query-engine-query-operator': 'contains',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PERFORMANCE_OF_CONTRACT',
            },
          },
          processNum: {
            type: 'string',
            title: i18nExpression('contract_mod.processNum'),
            'x-query-engine-query-operator': 'contains',
          },
          templateName: {
            type: 'string',
            title: i18nExpression('contract_mod.templateName'),
            'x-query-engine-query-operator': 'contains',
          },
        }),
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px',
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $handleOne({}, "add")'),
            },
          },
        },
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          editMode: 'row',
          openCustomTable: true,
        },
        properties: generateXindexInOrder({
          perTemplHeadId: {
            type: 'string',
            'x-hidden': true,
          },
          contractType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.contractType'),
              minWidth: 150,
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE',
            },
          },
          processNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.processNum'),
              minWidth: 150,
              customRender: true,
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $handleOne(row, "view")`),
            },
            'x-query-engine-sort': 'desc'
          },
          templateName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.templateName'),
              minWidth: 150,
            }
          },
          status: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.configStatus'),
              minWidth: 120,
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PERFORMANCE_OF_CONTRACT',
            },
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.creator'),
              minWidth: 100,
            }
          },
          creationDate: {
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)
            },
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: i18nExpression('common.creationTime'),
              minWidth: 150,
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.updateBy'),
              minWidth: 100,
            }
          },
          lastUpdateDate: {
            'x-render-table-column': {
              title: i18nExpression('contract_mod.updateDate'),
              minWidth: 150,
            },
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 170,
              fixed: 'right',
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT'].includes($deps[0])`,
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $handleOne(row, "edit")`),
                },
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT'].includes($deps[0])`,
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`
                    ({ row }) => $queryEngine.request.delete(row.perTemplHeadId).then(() => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      })

                  `),
                },
              },
              failure: {
                type: 'void',
                title: "{{$t('contract_mod.invalid')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['VALID'].includes($deps[0])`,
                ),
                'x-component-props': {
                  '@click': expression(`
                    ({ row }) => $confirm($t('contractMod.validConfirm'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning',
                    }).then(() => {
                      performanceTpl.performanceTpl.failure(row.perTemplHeadId).then((res) => {
                        $message.success(res?.message || '')
                        $queryEngine.state.paginationManagement.refresh()
                      })
                    })
                  `),
                },
              },
              view: {
                type: 'void',
                title: "{{$t('common.view')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['INVALID'].includes($deps[0])`,
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $handleOne(row, "view")`),
                },
              },

              copy: {
                type: 'void',
                title: i18nExpression('common.copy'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT'].includes($deps[0])`,
                ),
                'x-component-props': {
                  '@click': expression(`
                    ({ row }) => {
                      const tab = {
                        component: Edit,
                        params: {
                          row,
                          flag: 'add',
                          tabName: 'contractPerformanceProcessConfigEdit'
                        },
                        title: '复制新增',
                        name: 'contractPerformanceProcessConfigEdit'
                      }
                      emitTabAdd(tab)
                    }
                  `),
                },
              },
            },
          },
        }),
      },
    },
  },
})

const scope = {
  $handleOne,
  performanceTpl,
  app,
  Edit,
  emitTabAdd,
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformanceProcessConfigList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
