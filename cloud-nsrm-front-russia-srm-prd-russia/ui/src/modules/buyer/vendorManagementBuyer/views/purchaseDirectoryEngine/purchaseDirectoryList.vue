<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression, queryFieldValueExpression, queryFieldStatePropertyExpression } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  requiredValidatorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import PurchaseDirectoryDetail from './purchaseDirectoryDetail'

const schema = defineSchemas({
  PurchaseCatalog: {
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
          eventName: 'PurchaseCatalog',
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
          catalogStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATALOG_STATUS'
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
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $editTab('add',{})
              }`)
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.excelImport')}}",
              type: 'default',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup/purchaseCataLog/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup/purchaseCataLog/importExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('PurchaseCatalog')
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup/api-ql/PurchaseCatalog/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('PurchaseCatalog.table', 'data.columns'),
              dictCodes: {
                catalogStatus: 'CATALOG_STATUS',
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
          catalogId: {
            type: 'string',
            'X-query-engine-primary-key': true,
            'x-hidden': true
          },
          vendorId: {
            type: 'string',
            'x-hidden': true
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
              minWidth: 130
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
              minWidth: 100
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
          catalogStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATALOG_STATUS'
            }
          },
          startDate: {
            type: 'string',
            title: "{{$t('common.effectTime')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          endDate: {
            type: 'string',
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
            type: 'string',
            title: "{{$t('common.creationTime')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          dataSource: {
            type: 'string',
            title: "{{$t('vendorMod.dataSources')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_DATA_SOURCE'
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            title: "{{$t('common.updatePeople')}}",
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
            title: "{{$t('common.updateTime')}}",
            'x-render-table-column': {
              minWidth: 100
            },
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 160,
              fixed: 'right',
              showOverFlow: false
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              inactive: {
                type: 'void',
                title: "{{$t('common.inactive')}}",
                'x-component-props': {
                  popconfirm: {
                    title: '确定要失效此物料信息？'
                  },
                  '@click': expression(`({row}) => {
                    $changeStatus('INVALID',row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.catalogStatus'], `['VALID'].includes($deps[0])`)
              },
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.catalogStatus'], `['DRAFT'].includes($deps[0])`)
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`({row}) => {
                    $deleteOne(row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.catalogStatus'], `['DRAFT'].includes($deps[0])`)
              },
              update: {
                type: 'void',
                title: "{{$t('common.modify')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('change',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.catalogStatus'], `['VALID'].includes($deps[0])`)
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

const { emitTabAdd, t, app } = usePageHelper()

const $changeStatus = (status:any, row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'PurchaseCatalog',
    action: 'failure',
    loading: true,
    query: {
      '*': {}
    },
    payload: [{
      ...row
    }]
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $editTab = (type:string, row:Object) => {
  let name, title
  if (type === 'add') {
    name = '货源清单'
    title = '新增货源清单'
  } else if (type === 'change') { // 更新 跳转到货源变更
    app.$router.push({
      name: 'purchaseDirectoryChange',
      params: {
        from: 'purchaseDirectory',
        row
      }
    })
    return
  } else { // edit/view
    name = '货源清单' + row.catalogId
    title = '货源清单' + row.catalogId
  }
  let tab = {
    component: PurchaseDirectoryDetail,
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
  $changeStatus
}
</script>

<template>
  <RenderEngine schemaKey="purchaseDirectoryList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
