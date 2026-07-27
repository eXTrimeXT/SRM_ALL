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
  SiteForm: {
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
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                let tab = {
                  component: siteAssessmentDetail,
                  params: {
                    flag: 'add',
                    tabName: 'siteAssessmentDetail'
                  },
                  title: $t('vendorMod.addSite'),
                  name: 'siteAssessmentDetail'
                }
                emitTabAdd(tab)
              }`)
            }
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
          siteFormId: {
            type: 'string',
            'x-hidden': true
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
          vendorName: {
            type: 'string',
            title: "{{$t('vendorMod.vendorName')}}", // 供应商名称
            'x-render-table-column': {
              minWidth: 150
            }
          },
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
          reviewProcess: {
            type: 'string',
            title: "{{$t('vendorMod.reviewProcess')}}", // 评审进度
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
          reviewFormId:{
            type: 'string',
            'x-hidden': true
          },
          reviewFormNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let tab = {
                  component: quaOfReviewDetail,
                  params: {
                    flag: 'view',
                    row: row,
                    tabName: 'quaOfReviewDetail' + row.reviewFormNumber
                  },
                  title: () => app.$t('vendorMod.checkQuaOrderInfo'), // '查看资质审查单',
                  name: 'quaOfReviewDetail' + row.reviewFormNumber
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.quaNum')}}", // 资质审查单号
              minWidth: 140,
              customRender: true
            }
          },
          createdBy: {
            type: 'string',
            'x-hidden': true
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
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right',
              sortable: false
            },
            properties: {
              appraisal: {
                type: 'void',
                'x-component': 'TableButton',
                title: "{{$t('vendorMod.appraisal')}}", // '评审'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus', '.reviewResult'],
                  `['PUBLISH'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        flag: 'appraisal',
                        row: row,
                        siteFormId: row.siteFormId,
                        tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                      },
                      title: row.siteFormNumber,
                      name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              recall: {
                type: 'void',
                title: "{{$t('common.recall')}}", // '撤回'
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus', '.reviewResult', '.createdId'],
                  `['PUBLISH'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $queryEngine.request.save(row.siteFormId, { customizeAction: 'withdraw' })
                      .then(() => {
                        $message.success($t('common.successWithdraw'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)
                }
              },
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}", // '编辑'
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        flag: 'edit',
                        row: row,
                        siteFormId: row.siteFormId,
                        tabName: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                      },
                      title: row.siteFormNumber,
                      name: 'siteAssessmentDetail' + row.siteFormNumber || row.siteFormId
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              doApproval: {
                type: 'void',
                title: "{{$t('vendorMod.doApproval')}}", // '审批'
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus','.reviewResult'],
                  `['SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
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
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  style: `margin-left: 8px`,
                  showPopconfirm: true,
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.siteFormId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              abandon: {
                type: 'void',
                title: "{{$t('common.abandon')}}", // '废弃'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['REJECTED', 'WITHDRAW'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
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
                }
              }
            }
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
  <RenderEngine schemaKey="siteAssessmentList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
