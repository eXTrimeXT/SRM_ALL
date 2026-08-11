<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment, requiredValidatorSegment, editTableFormItemValid } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { parseTime } from '@/utils'

const schema = defineSchemas({
  SupplierLeader: {
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
          eventName: 'SupplierLeader',
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
          companyCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          responsibilityId: {
            type: 'string',
            title: "{{$t('dataConfMod.principal')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'userId'
            }
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
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: i18nExpression('common.excelImport'),
              type: 'primary',
              extraData: {
                fileModular: 'base',
                fileFunction: 'categoryDivision',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup/supplier-leader/importExcelNew',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup/supplier-leader/importExcelTemplate',
                fileName: expression(`parseTime(new Date()) + $t('dataConfMod.expTemplateXLSX')`)
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('SupplierLeader')
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
          style: 'flex: 1;',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          companyCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          responsibilityId: {
            type: 'string',
            'x-hidden': true
          },
          responsibilityName: {
            type: 'string',
            title: "{{$t('dataConfMod.principal')}}",
            'x-render-table-column': {
              minWidth: 150
            },
            ...editTableFormItemValid
          }
        })
      }
    }
  }
})

const scope = {
  downloadFileLinkByPost,
  parseTime
}
</script>

<template>
  <RenderEngine schemaKey="categoryResponsibility" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
