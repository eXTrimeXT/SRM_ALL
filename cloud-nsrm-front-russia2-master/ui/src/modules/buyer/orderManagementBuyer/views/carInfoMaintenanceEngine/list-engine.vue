<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"

const schema = defineSchemas({
  CarInfoBuyer: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'CarInfoBuyer',
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
          status: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_INFO_STATUS'
            }
          },
          carType: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.carType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_TYPE'
            }
          },
          licensePlate: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.licensePlate')}}",
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          void: {}
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
          carInfoId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            'x-query-engine-sort': 'desc',
            title: "{{$t('orderMod.buyerOrderSynergy.lastUpdateDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
                }`)
            }
          },
          status: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_INFO_STATUS'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          licensePlate: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.licensePlate')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          carType: {
            type: 'string',
            title: "{{$t('orderMod.buyerOrderSynergy.carType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_TYPE'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          effectiveDate: {
            title: "{{$t('orderMod.buyerOrderSynergy.effectiveDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.effectiveDate, '{y}-{m}-{d}')
                }`)
            }
          },
          expirationDate: {
            title: "{{$t('orderMod.buyerOrderSynergy.expirationDate')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.expirationDate, '{y}-{m}-{d}')
                }`)
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t } = usePageHelper()

const scope = {
}
</script>

<template>
  <RenderEngine schemaKey="carInfoMaintenance" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
