<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import vendorGreenChannelDetail from './vendorGreenChannelDetailEngine'
const { emitTabAdd, app } = usePageHelper()
const schema = defineSchemas({
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        greenQuery: {
          immediate: true,
          method: 'paginationQuery',
          transformRequest: expression(`(data, headers) => {
              // if (data.payload.filter) {
              //   data.payload.filter['dataSources'] = {
              //     eq: 'MANUALLY_CREATE'
              //   }
              // } else {
              //    data.payload.filter['dataSources'] = {
              //     eq: 'MANUALLY_CREATE'
              //   }
              //   data.payload = {
              //     "filter": {
              //         "dataSources": {
              //             eq: 'MANUALLY_CREATE'
              //         }
              //     }
              //   }
              // }
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
            title: '{{$t(\'common.vendorName\')}}', // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_display_buyer'
            }
          },
          lcCode: {
            type: 'string',
            title: '{{$t(\'vendorMod.lcCode\')}}', // 社会统一信用代码
            'x-query-engine-query-operator': 'contains'
          },
          isBacklist: {
            type: 'string',
            title: '{{$t(\'vendorMod.isBacklist\')}}', // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          overseasRelation: {
            type: 'string',
            title: '{{$t(\'vendorMod.overseasRelation\')}}', // 境内外关系
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION'
            }
          },
          companyType: {
            type: 'string',
            title: '{{$t(\'vendorMod.companyType\')}}', // 企业性质
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            }
          },
          status: {
            type: 'string',
            title: '{{$t(\'vendorMod.approveStatus\')}}', // 审批状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            }
          },
          legalPerson: {
            type: 'string',
            title: '{{$t(\'vendorMod.legalPerson\')}}', // 法定代表人
            'x-query-engine-query-operator': 'contains'
          },
          approvedDate: {
            title: '{{$t(\'vendorMod.permitDate\')}}', // 准入日期
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
          add: {
            type: 'void',
            title: '{{$t(\'common.add\')}}',
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
               tab = {
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'add',
                    tabName: 'vendorGreenChannelDetail',
                    activeStep: 'companyNature'
                  },
                  title: $t('vendorMod.addVendor'),
                  name: 'vendorGreenChannelDetail'
                }
               emitTabAdd(tab)
              }`)
            }
          },
          importPersonVendor: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: i18nExpression('cusEntry.vendorMod.importPersonVendor'),
              type: 'default',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoImportExcel',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup/pj/info/companyInfo/person/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup/pj/info/companyInfo/person/importExcelTemplate',
                fileName: expression('$t(\'vendorMod.vendorImportTemplateXLXS\')')
              },
              '@handleSuccess': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)
            },
            'x-visible': expression('$authorityVisible(\'sup:greenChannel:importPerson\')')
          },
          importCompanyVendor: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: i18nExpression('cusEntry.vendorMod.importCompanyVendor'),
              type: 'default',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoImportExcel',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup/pj/companyInfo/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup/pj/companyInfo/importExcelTemplate',
                fileName: expression('$t(\'vendorMod.vendorImportTemplateXLXS\')')
              },
              '@handleSuccess': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)
            },
            'x-visible': expression('$authorityVisible(\'sup:greenChannel:importCompany\')')
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
          companyCode: {
            type: 'string',
            title: '{{$t(\'common.vendorCode\')}}', // 供应商编码
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
                  component: vendorGreenChannelDetail,
                  params: {
                    flag: 'view',
                    companyId: companyId,
                    tabName: 'vendorGreenChannelDetail' + row.companyName,
                    row,
                    activeStep: 'main'
                  },
                  title: row.companyName,
                  name: 'vendorGreenChannelDetail' + row.companyName
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
          // supplierType: {
          //   type: 'string',
          //   title: '{{$t(\'supplierRating.supplierType\')}}', // 供应商类型
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'SUPPLIER_TYPE'
          //   },
          //   'x-render-table-column': {
          //     width: 150
          //   }
          // },
          overseasRelation: {
            type: 'string',
            title: '{{$t(\'vendorMod.overseasRelation\')}}', // 境内外关系
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
            title: '{{$t(\'vendorMod.companyType\')}}', // 企业性质
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
            title: '{{$t(\'vendorMod.lcCode\')}}', // 社会统一信用代码
            'x-render-table-column': {
              width: 150
            }
          },
          legalPerson: {
            type: 'string',
            title: '{{$t(\'vendorMod.legalPerson\')}}', // 法定代表人
            'x-render-table-column': {
              width: 150
            }
          },
          isBacklist: {
            type: 'string',
            title: '{{$t(\'vendorMod.isBacklist\')}}', // 是否黑名单
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          status: {
            type: 'string',
            title: '{{$t(\'vendorMod.approveStatus\')}}', // 审批状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          approvedDate: {
            title: '{{$t(\'vendorMod.permitDate\')}}', // 准入日期
            ...yearMonthDaySelectorSegment,
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
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: '{{$t(\'common.edit\')}}', // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'DRAFT\', \'WITHDRAW\', \'REJECTED\'].includes($deps[0])'
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'edit',
                        companyId: companyId,
                        tabName: 'vendorGreenChannelDetail' + row.companyName,
                        row,
                        activeStep: 'main'
                      },
                      title: row.companyName,
                      name: 'vendorGreenChannelDetail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: '{{$t(\'common.delete\')}}', // 删除
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'DRAFT\'].includes($deps[0])'
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    app.$confirm($t('common.confirmDelete'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning'
                    })
                      .then(() => {
                        $queryEngine.request.delete(row.companyId).then(() => {
                          $message.success($t('common.successDelete'))
                          $queryEngine.state.paginationManagement.refresh()
                        })
                      })
                      .catch(() => {})
                  }`)
                }
              },
              // doApproval: {
              //   type: 'void',
              //   title: '{{$t(\'vendorMod.doApproval\')}}', // 审批
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status'],
              //     '[\'SUBMITTED\'].includes($deps[0])'
              //   ),
              //   'x-component-props': {
              //     type: 'text',
              //     '@click': expression(`({ row }) => {
              //       let companyId = row.companyId
              //       let tab = {
              //         component: vendorGreenChannelDetail,
              //         params: {
              //           flag: 'approve',
              //           companyId: companyId,
              //           row,
              //           tabName: 'vendorGreenChannelDetail' + row.companyName
              //         },
              //         title: row.companyName,
              //         name: 'vendorGreenChannelDetail' + row.companyName
              //       }
              //       emitTabAdd(tab)
              //     }`)
              //   }
              // },
              abandon: {
                type: 'void',
                title: '{{$t(\'common.abandon\')}}', // 废弃
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'WITHDRAW\', \'REJECTED\'].includes($deps[0])'
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let companyId = row.companyId
                    let tab = {
                      component: vendorGreenChannelDetail,
                      params: {
                        flag: 'approve',
                        companyId: companyId,
                        tabName: 'vendorGreenChannelDetail' + row.companyName,
                        row
                      },
                      title: row.companyName,
                      name: 'vendorGreenChannelDetail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              }
              // recall: {
              //   type: 'void',
              //   title: i18nExpression('common.recall'), // 撤回 [供应商]
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status'],
              //     '$deps[0] === \'SUBMITTED\''
              //   ),
              //   'x-component-props': {
              //     type: 'text',
              //     '@click': expression(` ({ row }) => {
              //         app.$prompt('', '撤回原因', {
              //           confirmButtonText: '确定',
              //           cancelButtonText: '取消',
              //           inputType: 'textarea',
              //           inputValidator: value => !(!value || value.length > 500),
              //           inputErrorMessage: '撤回原因必填并且长度不能超过500字符！'
              //         }).then(({ value }) => {
              //           const submitData = {
                          
              //           }
              //           $recallFlow(app, submitData).then(res => {
              //             app.$message({
              //               message: $t('cusEntry.tipMessage.recallSuccess'),
              //               type: 'success'
              //             })
              //             $queryEngine.state.paginationManagement.refresh() // 查询旧数据
              //           })
              //         })
              //     }`)
              //   }
              // }
            }
          }
        })
      }
    }
  }
})
/* 撤回审批流 */
const $recallFlow = (app, data) => {
  return app.$http({
    url: '/api-pj/external/bpm/public/flow/native/rollBackProcess',
    method: 'POST',
    data,
    loading: true
  })
}
const scope = {
  emitTabAdd,
  app,
  i18nExpression,
  vendorGreenChannelDetail,
  $recallFlow
}

const components = {

}

</script>

<template>
  <RenderEngine
    schemaKey="vendorGreenChannelList"
    class="contractPaymentType"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
