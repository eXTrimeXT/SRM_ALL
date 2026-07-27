<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps, queryFieldValueExpression, queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import vendorProfileDetailReadEngine from './vendorProfileDetailReadEngine.vue'
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
          immediate: true
        },
        approve: {
          autoFormatResult: false
        },
        reject: {
          autoFormatResult: false
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
          lcCode: {
            type: 'string',
            title: "{{$t('vendorMod.lcCode')}}", // 社会统一信用代码
            'x-query-engine-query-operator': 'contains'
          },
          isBacklist: {
            type: 'string',
            title: "{{$t('vendorMod.isBacklist')}}", // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          overseasRelation: {
            type: 'string',
            title: "{{$t('vendorMod.overseasRelation')}}", // 境内外关系
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW'
            }
          },
          companyType: {
            type: 'string',
            title: "{{$t('vendorMod.companyType')}}", // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            }
          },
          potentialFlag: {
            type: 'string',
            title: "{{$t('vendorMod.potentialSupplier')}}", // 是否潜在供应商
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          dataSources: {
            type: 'string',
            title: "{{$t('vendorMod.dataSources')}}", // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
            }
          },
          status: {
            title: "{{$t('vendorMod.registerStatus')}}", // 注册状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_LIST_STATUS_vendorProfileList'
            }
          },
          supplierType: {
            type: 'string',
            title: "{{$t('supplierRating.supplierType')}}", // 供应商类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_TYPE'
            }
          },
          legalPerson: {
            type: 'string',
            title: "{{$t('vendorMod.legalPerson')}}", // 法定代表人
            'x-query-engine-query-operator': 'contains'
          },
          approvedDate: {
            type: 'date',
            title: "准入日期",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
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
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup/api-ql/CompanyInfo/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('CompanyInfo.table', 'data.columns'),
              dictCodes: {
                overseasRelation: 'RELATION_NEW',
                companyType: 'COMPANY_NATURE',
                status: 'SUPPLIER_LIST_STATUS',
                dataSources: 'DATA_SOURCE',
                isBacklist: 'YES_OR_NO',
                quitFlag: 'YES_OR_NO',
                supplierType: 'SUPPLIER_TYPE',
                forzenFlag: 'YES_OR_NO',
                potentialFlag: 'YES_OR_NO'
              }
            }
          },
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
          companyCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}", // 供应商编码
            'x-render-table-column': {
              width: 120
            }
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
              minWidth: 150,
              customRender: true
            }
          },
          supplierType: {
            type: 'string',
            title: "{{$t('supplierRating.supplierType')}}", // 供应商类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_TYPE'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          overseasRelation: {
            type: 'string',
            title: "{{$t('vendorMod.overseasRelation')}}", // 境内外关系
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION_NEW'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          companyType: {
            type: 'string',
            title: "{{$t('vendorMod.companyType')}}", // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          lcCode: {
            type: 'string',
            title: "{{$t('vendorMod.lcCode')}}", // 社会统一信用代码
            'x-render-table-column': {
              width: 150
            }
          },
          legalPerson: {
            type: 'string',
            title: "{{$t('vendorMod.legalPerson')}}", // 法定代表人
            'x-render-table-column': {
              width: 150
            }
          },
          isBacklist: {
            type: 'string',
            title: "{{$t('vendorMod.isBacklist')}}", // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          potentialFlag: {
            type: 'string',
            title: "{{$t('vendorMod.potentialSupplier')}}", // 是否潜在供应商
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          status: {
            type: 'string',
            title: "{{$t('vendorMod.registerStatus')}}", // 审批状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_LIST_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          approvedDate: {
            title: "{{$t('vendorMod.permitDate')}}", // 准入日期
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          dataSources: {
            type: 'string',
            title: "{{$t('vendorMod.dataSources')}}", // 数据来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DATA_SOURCE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          forzenFlag: {
            type: 'string',
            title: "{{$t('vendorMod.forzenFlag')}}", // 是否已冻结
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          quitFlag: {
            type: 'string',
            title: "{{$t('bidMod.quitFlag')}}", // 是否已退出
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 100
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
              width: 200,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              doApprovalPass: {
                type: 'void',
                title: "{{$t('purchaseDemand.confirm')}}", // 审核通过
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.dataSources'],
                  `['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let values = {
                      companyId: row.companyId
                    }
                    $queryEngine.request.save(values, { query: { '*':{} }, action: 'approve' }).then((res) => {
                      $message.success($t('purchaseDemand.confirm'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              refuse: {
                type: 'void',
                title: "{{$t('purchaseDemand.refuse')}}", // 驳回
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.dataSources'],
                  `['SUBMITTED'].includes($deps[0]) && !['MANUALLY_CREATE'].includes($deps[1])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    app.$prompt('', '驳回原因', {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      inputType: 'textarea'
                    }).then(({ value }) => {
                      let values = {
                        flowRemark: value,
                        companyId: row.companyId
                      }
                      $queryEngine.request.save(values, { query: { '*':{} }, action: 'reject' }).then((res) => {
                        $message.success($t('bidMod.toRefuseSuccess'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                    })
                  }`)
                }
              },
              createdQuaofReview: {
                type: 'void',
                title: "{{$t('vendorMod.createQua')}}", // 创建资质审查
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.isBacklist'],
                  `['APPROVED'].includes($deps[0]) && ['N'].includes($deps[1])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    if (row.supplierType === 'NO_MATERIAL') {
                      let row2 = {
                        vendorId: row.companyId,
                        vendorCode: row.companyCode,
                        vendorName: row.companyName
                      }
                      let tab = {
                        component: nonQuaOfReviewDetail,
                        params: {
                          flag: 'add',
                          row: row2,
                          tabName: 'quaOfReviewDetail'
                        },
                        title: () => $t('vendorMod.noAddQua'), // '资质审查新增',
                        name: 'quaOfReviewDetail'
                      }
                      emitTabAdd(tab)
                    } else {
                      let row2 = {
                        vendorId: row.companyId,
                        vendorCode: row.companyCode,
                        vendorName: row.companyName
                      }
                      let tab = {
                        component: quaOfReviewDetail,
                        params: {
                          flag: 'add',
                          row: row2,
                          tabName: 'quaOfReviewDetail'
                        },
                        title: () => $t('vendorMod.addQua'), // '资质审查新增',
                        name: 'quaOfReviewDetail'
                      }
                      emitTabAdd(tab)
                    }
                  }`)
                }
              },
              createdQuestionnaire: {
                type: 'void',
                title: "{{$t('quest.createdQuestionnaire')}}", // 创建调查表
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.isBacklist'],
                  `['APPROVED', 'SUBMITTED'].includes($deps[0]) && ['N'].includes($deps[1])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    app.$router.push('/vendorManagement/questManagement')
                  }`)
                }
              }
            }
          }
        })
      },
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
