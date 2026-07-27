<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import vendorProfileDetailReadEngine from 'modb@/vendorManagementBuyer/views/vendorProfileEngine/vendorProfileDetailReadEngine'
import nonQuaOfReviewDetail from 'modb@/vendorManagementBuyer/views/nonQuaOfReview/quaOfReviewDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
              data.payload.filter = {
                potentialFlag: {eq: 'Y'},
                supplierType: {eq: 'MATERIAL'},
                ...data.payload.filter
              }
              console.log(data)
              return data
            }`)
        }
      }
    },
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'green',
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
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}", // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          companyCode: {
            type: 'string',
            title: i18nExpression(`common.vendorCode`) // 供应商编码
          },
          companyType: {
            type: 'string',
            title: "{{$t('vendorMod.companyType')}}", // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            }
          },
          dataSources: {
            type: 'string',
            title: "{{$t('vendorMod.dataSources')}}", // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
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
          exportExcel: {
            type: 'void'
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          companyId: {
            type: 'string',
            'x-hidden': true
          },
          companyName: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let companyId = row.companyId
                let tab = {
                  component: vendorProfileDetailReadEngine,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorProfileDetailReadEngine' + row.companyName
                  },
                  title: row.companyName,
                  name: 'vendorProfileDetailReadEngine' + row.companyName
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // 供应商名称
              minWidth: 200,
              customRender: true
            }
          },
          companyCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'), // 企业性质
            'x-render-table-column': {
              width: 150
            }
          },
          companyType: {
            type: 'string',
            title: i18nExpression('vendorMod.companyType'), // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 社会统一信用代码
            'x-render-table-column': {
              width: 150
            }
          },
          status: {
            type: 'string',
            title: i18nExpression('vendorMod.registerStatus'), // 注册状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_LIST_STATUS'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          dataSources: {
            type: 'string',
            title: i18nExpression('vendorMod.dataSources'), // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
            },
            'x-render-table-column': {
              width: 150
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
            title: i18nExpression('bidMod.updateTime'), // 更新时间
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              width: 150
            }
          }
        })
      }
    }
  }
})

const scope = {
  emitTabAdd,
  app,
  i18nExpression,
  vendorProfileDetailReadEngine,
  nonQuaOfReviewDetail,
  quaOfReviewDetail
}

const components = {

}

</script>

<template>
  <RenderEngine schemaKey="vendorProfileList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
