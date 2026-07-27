<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import siteAssessmentDetail from './siteAssessmentDetailEngine'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  SiteFormVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'siteA',
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
          siteFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.siteOrderInfoV')}}" // 供应商评审单号
          },
          reviewFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.quaNum')}}" // 资质审查单号
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_APPROVE_STATUS_TYPE'
            }
          },
          assessmentType: {
            type: 'string',
            title: "{{$t('vendorMod.siteTypeV')}}", // 供应商评审类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_ASSESSMENT_TYPE'
            }
          },
          reviewResult: {
            type: 'string',
            title: "{{$t('vendorMod.certificationResult')}}", // 认证结果
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_RESULT_TYPE'
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}", // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          }
        })
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: false
        },
        properties: generateXindexInOrder({
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              minWidth: 90
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}", // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('vendorMod.vendorName')}}", // 供应商名称
            'x-render-table-column': {
              minWidth: 150
            }
          },
          siteFormNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let tab = {
                  component: siteAssessmentDetail,
                  params: {
                    flag: 'view',
                    row: row,
                    siteFormId: row.siteFormId,
                    tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                  },
                  title: row.siteFormNumber,
                  name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.siteOrderInfoV')}}", // 供应商评审单号
              minWidth: 140,
              customRender: true
            }
          },
          siteFormId: {
            type: 'string',
            'x-hidden': true
          },
          assessmentType: {
            type: 'string',
            title: "{{$t('vendorMod.siteTypeV')}}", // 供应商评审类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_ASSESSMENT_TYPE'
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },
          reviewResult: {
            type: 'string',
            title: "{{$t('vendorMod.certificationResult')}}", // 认证结果
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_RESULT_TYPE'
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}", // 创建人
            'x-render-table-column': {
              minWidth: 110
            }
          },
          creationDate: {
            title: "{{$t('vendorMod.creationDate2')}}", // 创建时间
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          }
        })
      }
    }
  }
})

const scope = {
  emitTabAdd,
  siteAssessmentDetail,
  quaOfReviewDetail,
  app
}

const components = {

}
</script>

<template>
  <RenderEngine schemaKey="siteAssessmentListVendor" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
