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
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { FormTab } from '@meicloud/render-pix'
const schema = defineSchemas({
  OrgCategory: {
    type: 'void',
    'x-query-engine': {
      service: 'sup'
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
          action: 'listPageHeader',
          immediateQueryForm: true
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
          companyStatus: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.vendorCategoryStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "VENDOR_STATUS"
            }
          },
          orgId: {
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
          categoryName: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.categoryLittle')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category2',
              showKey: 'categoryName'
            }
          },
          pjCategoryStatus: {
            type: 'string',
            title: "{{$t('vendorMod.catServiceStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'UNIT_STATUS'
            }
          },
          pjOrgStatus: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.unitStatus'),
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
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: '/api-sup/api-ql/OrgCategory/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('OrgCategory.table', 'data.columns'),
              dictCodes: {
                pjOrgStatus: 'UNIT_STATUS',
                pjCategoryStatus: 'UNIT_STATUS'
              }
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
          companyStatus: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.vendorCategoryStatus')}}",
            "x-render-table-column": {
              minWidth: 140
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "VENDOR_STATUS"
            }
          },
          lastUpdateDate: {
            type: 'string',
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
