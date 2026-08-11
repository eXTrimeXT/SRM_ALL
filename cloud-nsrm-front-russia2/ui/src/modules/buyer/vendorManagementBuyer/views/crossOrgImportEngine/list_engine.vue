<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
    yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import Edit from './edit_engine.vue'

const schema = defineSchemas({
  VendorImport: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
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
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          importNum: {
            type: 'string',
            title: '{{$t(\'vendorMod.importNum\')}}',
            'x-query-engine-query-operator': 'contains'
          },
          vendorId: {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              showKey: 'companyName',
              propKey: 'companyId'
            }
          },
          importStatus: {
            type: 'string',
            title: '{{$t(\'vendorMod.orderStatus\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDORIMPORTSTATUS'
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
            title: '{{$t(\'common.add\')}}',
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': '{{() => $edit({},"add")}}'
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex:1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          importId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          vendorId: {
            type: 'number',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          importStatus: {
            type: 'string',
            title: '{{$t(\'vendorMod.orderStatus\')}}',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDORIMPORTSTATUS'
            },
            'x-render-table-column': {
              minWidth: 90
            }
          },
          vendorCode: {
            type: 'string',
            title: '{{$t(\'common.vendorCode\')}}',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          vendorName: {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          importNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({row}) => $edit(row,"view")')
            },
            'x-render-table-column': {
              title: '{{$t(\'vendorMod.importNum\')}}',
              minWidth: 150,
              customRender: true
            }
          },
          createdFullName: {
            type: 'string',
            title: '{{$t(\'common.creator\')}}',
            'x-render-table-column': {
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
            title: '{{$t(\'common.creationTime\')}}',
            'x-render-table-column': {
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              width: 204,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: '{{$t(\'common.edit\')}}',
                'x-component-props': {
                  code: 'sup:crossOrgImportList:edit',
                  '@click': expression('({row}) => $edit(row, "edit")')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.importStatus'],
                  '[\'DRAFT\', \'REJECTED\', \'WITHDRAW\'].includes($deps[0])'
                )
              },
              delete: {
                type: 'void',
                title: '{{$t(\'common.delete\')}}',
                'x-component-props': {
                  code: 'sup:crossOrgImportList:delete',
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression('({row}) => $delete(row, $queryEngine)')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.importStatus'],
                  '[\'DRAFT\'].includes($deps[0])'
                )
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t } = usePageHelper()

const $edit = (row: any, flag: string) => {
  let name, title
  if (flag === 'add') {
    name = 'crossOrgImportDetail'
    title = t('vendorMod.addCrossImport')
  } else if (flag === 'edit') {
    name = 'crossOrgImportDetail' + row.importNum || row.importId
    title = row.importNum
  } else { // view
    name = 'crossOrgImportDetail' + row.importNum
    title = row.importNum
  }
  let tab = {
    component: Edit,
    params: {
      row,
      flag,
      tabName: name
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const $delete = (row: any, queryEngine: any) => {
  queryEngine.request.baseRequest({
    type: 'VendorImport',
    action: 'delete',
    payload: [{
      '$delete': row.importId,
      'vendorImport': [{
        '$delete': '*'
      }],
      'fileUploads': [{
        '$delete': '*'
      }]
    }],
    query: {
      '*': {}
    }
  }).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $edit,
  $delete
}

</script>

<template>
  <RenderEngine schemaKey="crossOrgImportList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
