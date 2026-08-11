<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  changeFieldVisibleByDeps,
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { exportExcelSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { FormTab } from '@meicloud/render-pix'
import detail from './detailList.vue'

const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()
const $detailOne = (row: any, $form: any) => {
  let name = row.companyName + '-' + row.categoryName
  let queryParam = $form.query('.query').get('value')
  // for (let key in queryParam) {
  //   name += '-' + key + queryParam[key]
  // }
  emitTabRemove(name)
  emitTabAdd({
    component: detail,
    params: {
      flag: 'view',
      row: row,
      query: queryParam
    },
    title: row.companyName + '-' + row.categoryName,
    name: name
  })
}

const tableHeaderDetail = [
  {
    prop: 'companyCode',
    label: "{{$t('common.vendorCode')}}",
    width: 120
  },
  {
    prop: 'companyName',
    label: "{{$t('common.vendorName')}}",
    width: 120
  },
  {
    prop: 'orgName',
    label: "{{$t('cusEntry.vendorMod.orgName')}}",
    width: 120
  },
  {
    prop: 'categoryFullName',
    label: "{{$t('cusEntry.vendorMod.categoryFullName')}}",
    width: 120
  },
  {
    prop: 'categoryName',
    label: "{{$t('cusEntry.vendorMod.categoryLittle')}}",
    width: 120
  },
  {
    prop: 'pjOrgStatus',
    label: "{{$t('cusEntry.vendorMod.unitStatus')}}",
    width: 120
  },
  {
    prop: 'pjCategoryStatus',
    label: "{{$t('vendorMod.catServiceStatus')}}",
    width: 120
  },
  {
    prop: 'companyStatus',
    label: "{{$t('cusEntry.vendorMod.vendorCategoryStatus')}}",
    width: 120
  },
  {
    prop: 'lastUpdateDate',
    label: "{{$t('common.updateTime')}}",
    width: 120,
    dataType: 'dateTime'
  }
]
const schema = defineSchemas({
  OrgCategory: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          // immediate: true,
          action: 'listPageHeader'
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
          eventName: 'categoryRelationShipList',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          action: 'listPageHeader'
          // immediateQueryForm: true
        },
        properties: generateXindexInOrder({
          companyId: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          categoryId: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryLittle')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category2',
              showKey: 'categoryName',
              propKey: 'categoryId'
            }
          },
          orgIdList: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.orgName')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: true,
              'node-type': 'OU',
              'parent-id': -1
            },
            'x-query-engine-query-operator': 'in'
          },
          serviceStatus: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.vendorCategoryStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "CATEGORY_STATUS"
            }
          },
          pjOrgStatus: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.unitStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'UNIT_STATUS'
            }
          },
          pjCategoryStatus: {
            type: 'string',
            title: "{{$t('vendorMod.catServiceStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'UNIT_STATUS'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        properties: {
          exportExcelAll: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup/api-ql/OrgCategory/listPageHeader', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('OrgCategory.table', 'data.columns'),
              dictCodes: {
                pjOrgStatus: 'CATEGORY_STATE_STORE'
              },
              title: i18nExpression('cusEntry.common.exportSum') // 自定义汇总导出
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              exportMode: 'front',
              exportType: 'meiqlApi',
              generateMeiQLExportRequest: `{{
                () =>{
                  const {
                    filter,
                    ...rest
                  }  = $form.query('query').take().invoke('getQueryParamsByQueryFrom')
                  if (filter?.orgIdList) {
                    filter.orgId = filter.orgIdList
                    Reflect.deleteProperty(filter, 'orgIdList')
                  }
                  return $queryEngine.request.getQueryRequestInfo({
                    action: 'query',
                    filter,
                    ...rest
                  })
                }
              }}`,
              type: 'default',
              pageUrl: '/api-sup/api-ql/OrgCategory/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('OrgCategory.table2', 'data.columns'),
              dictCodes: {
                pjOrgStatus: 'UNIT_STATUS',
                pjCategoryStatus: 'UNIT_STATUS',
                serviceStatus: 'CATEGORY_STATUS'
              },
              title: i18nExpression('cusEntry.common.exportDetail') // 自定义明细导出
            }
          },
          buttonGroup: {
            type: 'void',
            'x-component': 'el-button-group',
            properties: {
              all: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.all'),
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('query').take().invoke('resetQuery')
                  }`)
                }
              },
              limitUnit: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.limitUnit'),
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').pjOrgStatus = 'N'
                    $form.query('.query').get('value').pjCategoryStatus = null
                    $form.query('.query').take().invoke('query')
                  }`)
                }
              },
              categoryLimit: {
                type: 'void',
                'x-component': 'Button',
                'x-content': i18nExpression('cusEntry.common.categoryLimit'),
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('.query').get('value').pjOrgStatus = null
                    $form.query('.query').get('value').pjCategoryStatus = 'N'
                    $form.query('.query').take().invoke('query')
                  }`)
                }
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
          style: 'flex: 1;margin-top:36px;',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          orgCategoryId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          companyId: {
            type: 'string',
            'x-hidden': true
          },
          companyCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryId: {
            type: 'string',
            'x-hidden': true
          },
          categoryFullName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryFullName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryLittle')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          pjOrgStatus: {
            type: 'string',
            title: "{{$t('common.status')}}",
            "x-render-table-column": {
              minWidth: 140
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "CATEGORY_STATE_STORE"
            }
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
                title: "{{$t('common.view')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $detailOne(row,$form)
                  }`)
                }
              } }
          }
        })
      },
      table2: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1;margin-top:36px;display:none',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          orgCategoryId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          companyId: {
            type: 'string',
            'x-hidden': true
          },
          companyCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          orgName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.orgName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryId: {
            type: 'string',
            'x-hidden': true
          },
          categoryFullName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryFullName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryLittle')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          pjOrgStatus: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.unitStatus'),
            "x-render-table-column": {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'UNIT_STATUS'
            }
          },
          pjCategoryStatus: {
            type: 'string',
            title: "{{$t('vendorMod.catServiceStatus')}}",
            "x-render-table-column": {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'UNIT_STATUS'
            }
          },
          serviceStatus: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.vendorCategoryStatus')}}",
            "x-render-table-column": {
              minWidth: 140
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "CATEGORY_STATUS"
            }
          },
          lastUpdateDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('common.updateTime')}}",
            'x-query-engine-sort': 'desc',
            "x-render-table-column": {
              minWidth: 100
            }
          }
        })
      }
    }
  }
})

const scope = {
  $detailOne
}
const components = {
  FormTab
}
</script>

<template>
  <RenderEngine
    schemaKey="categoryRelationshipList"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
