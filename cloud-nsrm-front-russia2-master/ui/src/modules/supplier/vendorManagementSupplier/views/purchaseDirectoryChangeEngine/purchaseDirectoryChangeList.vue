<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression, queryFieldValueExpression, queryFieldStatePropertyExpression } from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment, requiredValidatorSegment, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import PurchaseDirectoryChangeDetail from './purchaseDirectoryChangeDetail'
import { onActivated } from 'vue-demi'

const schema = defineSchemas({
  PurCatalogChangeVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
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
          eventName: 'PurCatalogChangeVendor',
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
          purchaseOrgId: {
            type: 'string',
            title: "{{$t('vendorMod.ceeaOrgName')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialCode',
              propKey: 'materialCode',
              name: 'scc_base_material_item'
            }
          },
          changeStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MATERIAL_LIST_CHANGE'
            }
          },
          vendorId: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryName'
            }
          },
          createdBy: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'username'
            }
          },
          invId: {
            type: 'string',
            title: "{{$t('common.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'parent-id': -1
            }
          },
          materialName: {
            type: 'string',
            title: "{{$t('common.materialName')}}",
            'x-query-engine-query-operator': 'contains'
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
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'primary',
              pageUrl: "/api-sup/api-ql/PurCatalogChangeVendor/query",
              meiqlKey: "PurCatalogChangeVendor", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('PurCatalogChangeVendor.table', 'data.columns'),
              dictCodes: {
                changeStatus: 'MATERIAL_LIST_CHANGE',
                dataSource: 'PURCHASE_DATA_SOURCE'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1;',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          changeId: {
            type: 'string',
            'X-query-engine-primary-key': true,
            'x-hidden': true
          },
          catalogId: {
            type: 'string',
            'x-hidden': true
          },
          vendorId: {
            type: 'string',
            'x-hidden': true
          },
          changeNo: {
            type: 'string',
            title: "{{$t('purchase.SourceChangeOrderNo')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          purchaseOrgId: {
            type: 'string',
            'x-hidden': true
          },
          purchaseOrgName: {
            type: 'string',
            title: "{{$t('vendorMod.ceeaOrgName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          invName: {
            type: 'string',
            title: "{{$t('common.invOrg')}}",
            'x-render-table-column': {
              minWidth: 130
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          materialName: {
            type: 'string',
            title: "{{$t('common.materialName')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          categoryFullName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          changeStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MATERIAL_LIST_CHANGE'
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
            title: "{{$t('common.effectTime')}}",
            'x-render-table-column': {
              minWidth: 100
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
            title: "{{$t('dataConfMod.endDateTime')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-render-table-column': {
              minWidth: 100
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
            title: "{{$t('common.creationTime')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          updatedReason: {
            type: 'string',
            title: "{{$t('vendorMod.updatedReason')}}",
            'x-render-table-column': {
              minWidth: 100
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
            'x-render-table-column': {
              width: 130,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {

              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.changeStatus'], `['DRAFT'].includes($deps[0])  || (['FIRST_REJECT'].includes($deps[0]) && $vendor())
                `)
              },
              view: {
                type: 'void',
                title: "{{$t('common.view')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('view',row)
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

const { emitTabAdd, t, app, vendor } = usePageHelper()

onActivated(() => {
  const { from, row } = app.$route?.params
  if (from === 'purchaseDirectory') {
    $editTab('change', row)
  }
})

const $editTab = (type:string, row:Object) => {
  let name, title
  if (type === 'add') {
    name = t('vendorMod.supplyList')
    title = t('vendorMod.newSourcelList')
  } else if (type === 'change') { // 更新 跳转到货源变更
    emitTabAdd({
      component: PurchaseDirectoryChangeDetail,
      ctrlHeight: true,
      params: {
        flag: 'add',
        row,
        tabName: t('route.purchaseDirectoryChange')
      },
      title: t('route.purchaseDirectoryChange'),
      name: t('route.purchaseDirectoryChange')
    })
    return
  } else { // edit/view
    name = t('route.purchaseDirectoryChange') + row.changeNo
    title = t('route.purchaseDirectoryChange') + row.changeNo
  }
  let tab = {
    component: PurchaseDirectoryChangeDetail,
    ctrlHeight: true,
    params: {
      flag: type,
      row,
      tabName: name
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const $deleteOne = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'PurchaseCatalog',
    action: 'delete',
    payload: [{
      '$delete': row.catalogId,
      'purCatalogAttList': [{
        '$delete': '*'
      }]
    }],
    query: {
      '*': {}
    }
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $editTab,
  $deleteOne,
  $vendor: vendor
}
</script>

<template>
  <RenderEngine schemaKey="purchaseDirectoryChangeList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
