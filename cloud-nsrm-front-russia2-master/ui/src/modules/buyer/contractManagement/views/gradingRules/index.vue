<!-- eslint-disable quotes -->
<script setup lang="ts">
import { expression, generateXindexInOrder, defineSchemas } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

const schema = defineSchemas({
  CondFactor: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper gradingRulesWrap',
      direction: 'vertical'
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          struct: {
            type: 'string',
            title: "{{$t('contractMod.categoryName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          level: {
            type: 'string',
            title: "{{$t('contractMod.level')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTARCT_LEVEL'
            }
          },
          isValid: {
            type: 'string',
            title: "{{$t('contractMod.isValid')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
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
              type: 'primary'
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.excelImport')}}",
              type: 'default',
              'up-load-url': '/api-cm/contract/level-maintain/importExcel'
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq'
        },
        properties: generateXindexInOrder({
          categoryName: {
            type: 'string',
            title: "{{$t('contractMod.categoryName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryFullName: {
            type: 'string',
            title: "{{$t('contractMod.categoryFullName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          amount: {
            type: 'string',
            title: "{{$t('contractMod.amount')}}",
            'x-render-table-column': {
              width: 180
            }
          },
          operational: {
            type: 'string',
            title: "{{$t('contractMod.operational')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OPERATOR'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          level: {
            type: 'string',
            title: "{{$t('contractMod.level')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTARCT_LEVEL'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          startData: {
            title: "{{$t('contractMod.startDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.startData, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          endData: {
            title: "{{$t('contractMod.endDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.endData, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 150
            }
          },
          lastUpdatedUserName: {
            type: 'string',
            title: "{{$t('contractMod.lastUpdatedBy')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          lastUpdateDate: {
            title: "{{$t('contractMod.lastUpdateDate')}}",
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              minWidth: 150
            }

          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 130,
              fixed: 'right'
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  edit: {
                    type: 'void',
                    title: "{{$t('common.edit')}}",
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'text'
                    }
                  },
                  save: {
                    type: 'void',
                    title: "{{$t('common.save')}}",
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'text'
                    }
                  },
                  delete: {
                    type: 'void',
                    title: "{{$t('common.delete')}}",
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'text'
                    }
                  },
                  cancel: {
                    type: 'void',
                    title: "{{$t('common.cancel')}}",
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'text'
                    }
                  }
                }
              }
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine schemaKey="gradingRules" :pageAttrs="$attrs" :schema="schema" />
</template>

<style lang="scss">
.gradingRulesWrap {
  height: calc(100vh - 76px)!important;
  margin: 16px 16px 12px!important;
  padding: 16px 16px 12px;
  position: relative;
  background-color: #fff;
}
</style>
