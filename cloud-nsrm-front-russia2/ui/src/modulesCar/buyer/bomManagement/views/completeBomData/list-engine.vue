<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  generateCharExpressionByFunction,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import edit from './detail-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any) => {
  let name = row.materialCode ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      tabName: name ? 'viewBomDetail' + name : 'viewBomDetail'
    },
    title: $t('bom.viewDetail') + '-' + name,
    name: name ? 'viewBomDetail' + name : 'viewBomDetail'
  })
}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const schema = defineSchemas({
  Bom: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'cost',
      actions: {
        paginationQuery: {
          action: 'groupListPage',
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
          eventName: 'CompleteBomData',
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
          materialCode: {
            type: 'string',
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_cost_car',
              'preQueryData': expression(`{'t.car_level': 2}`),
              showKey: 'carCode',
              propKey: 'carCode'
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
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cost/cost/bom/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cost/cost/bom/exportExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('CompleteBomData')
              }`)
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
          openCustomTable: false
          // editMode: true,
        },
        properties: generateXindexInOrder({
          bomId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.carCode')}}",
              minWidth: 120
            }
          },
          lastUpdateDate: {
            title: "{{ $t('bom.updateDate') }}",
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              fixed: 'right'
            },
            properties: {
              viewDetail: {
                type: 'void',
                title: "{{$t('bom.viewDetail')}}",
                'x-component-props': {
                  // ...buttonListItemVisibleByPermission('base:black:edit'),
                  '@click': expression(`({row}) => {
                    $editOne(row)
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
  $detailOne,
  $readOne

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="completeBomData"
  />
</template>
