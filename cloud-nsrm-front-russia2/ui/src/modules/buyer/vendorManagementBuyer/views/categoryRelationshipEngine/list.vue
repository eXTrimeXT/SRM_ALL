<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"

const schema = defineSchemas({
  OrgCategory: {
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
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
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
          serviceStatus: {
            type: 'string',
            title: "{{$t('vendorMod.catServiceStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATEGORY_STATUS'
            }
          },
          orgId: {
            type: 'string',
            title: "{{$t('dataConfMod.orgId')}}",
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
            title: "{{$t('dataConfMod.categoryLittle')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category2',
              showKey: 'categoryName'
            }
          },
          warningStatus: {
            type: 'string',
            title: "{{$t('vendorMod.warningStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WARNING_STATUS'
            }
          },
          categoryLevel: {
            type: 'string',
            title: "{{$t('vendorMod.vendorClassification')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDOR_LEVEL'
            }
          },
          tempCompanyFlag: {
            type: 'string',
            title: "{{$t('vendorMod.tempCompanyFlag')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          companyStatus: {
            type: 'string',
            title: "{{$t('bidMod.vendorStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "VENDOR_STATUS"
            }
          }
        })
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
            title: "{{$t('dataConfMod.orgId')}}",
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
            title: "{{$t('vendorMod.categoryFullName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('vendorMod.littleCategory')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          companyStatus: {
            type: 'string',
            title: "{{$t('bidMod.vendorStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDOR_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          tempCompanyFlag: {
            type: 'string',
            title: "{{$t('vendorMod.tempCompanyFlag')}}",
            'x-render-table-column': {
              minWidth: 130
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          warningStatus: {
            type: 'string',
            title: "{{$t('vendorMod.warningStatus')}}",
            "x-render-table-column": {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WARNING_STATUS'
            }
          },
          categoryLevel: {
            type: 'string',
            title: "{{$t('vendorMod.vendorClassification')}}",
            "x-render-table-column": {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'VENDOR_LEVEL'
            }
          },
          serviceStatus: {
            type: 'string',
            title: "{{$t('vendorMod.catServiceStatus')}}",
            "x-render-table-column": {
              minWidth: 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATEGORY_STATUS'
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
}
</script>

<template>
  <RenderEngine schemaKey="categoryRelationshipList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
