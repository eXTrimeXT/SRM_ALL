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
import detail from './edit'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  InfoChangeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        },
        vendorWithdraw: {
          autoFormatResult: false
        },

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
          eventName: 'vendorInfoChange',
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
          changeApplyNo: {
            type: 'string',
            title: i18nExpression('vendorMod.changeApplyNo'), // 变更编号
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: i18nExpression('common.creationTime'), // 创建日期
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          changeStatus: {
            type: 'string',
            title: i18nExpression('vendorMod.changeStatus'), // 变更状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INFO_CHANGE_STATUS'
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
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
               tab = {
                  component: detail,
                  params: {
                    flag: 'add',
                    tabName: 'detail'
                  },
                  title: $t('vendorMod.addVendor'), // '新增供应商',
                  name: 'detail'
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
          changeId: {
            type: 'string',
            'x-hidden': true
          },
          changeStatus: {
            type: 'string',
            title: i18nExpression('vendorMod.changeStatus'), // 变更状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INFO_CHANGE_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          changeApplyNo: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let changeId = row.changeId
                let tab = {
                  component: detail,
                  params: {
                    flag: 'view',
                    changeId,
                    tabName: 'detail' + row.companyName
                  },
                  title: row.companyName,
                  name: 'detail' + row.companyName
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.changeApplyNo'), // 变更单号
              minWidth: 150,
              customRender: true
            }
          },
          companyCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'), // 供应商编码
            'x-query-engine-relation': 'companyInfoChange',
            'x-render-table-column': {
              width: 120
            }
          },
          companyName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // 供应商名称
            'x-query-engine-relation': 'companyInfoChange',
            'x-render-table-column': {
              width: 150
            }
          },
          overseasRelation: {
            type: 'string',
            title: i18nExpression('vendorMod.overseasRelation'), // 境内外关系
            'x-query-engine-relation': 'companyInfoChange',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RELATION'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 社会统一信用代码
            'x-query-engine-relation': 'companyInfoChange',
            'x-render-table-column': {
              width: 160
            }
          },
          legalPerson: {
            type: 'string',
            title: i18nExpression('vendorMod.legalPerson'), // 法定代表人
            'x-query-engine-relation': 'companyInfoChange',
            'x-render-table-column': {
              width: 120
            }
          },
          lastUpdateDate: {
            title: i18nExpression('vendorMod.changeApprovedDate'), // 审批日期
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              width: 130
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.creator'), // 创建人
            'x-render-table-column': {
              width: 120
            }
          },
          creationDate: {
            title: i18nExpression('common.creationTime'), // 创建日期
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
          userType: {
            type: 'string',
            title: i18nExpression('vendorMod.userType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'USER_TYPE'
            },
            'x-render-table-column': {
              width: 130
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
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
                title: i18nExpression('common.edit'), // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus', '.userType'],
                  `(app.$store.getters.userType == $deps[1] && ['DRAFT'].includes($deps[0])) ||
                      ($buyer() && ['REJECTED', 'WITHDRAW'].includes($deps[0])) ||
                      ($vendor() && ['VENDOR_WITHDRAW', 'VENDOR_REJECTED'].includes($deps[0]))`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'edit',
                        changeId,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // 删除
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus', '.userType'],
                  `(app.$store.getters.userType == $deps[1] && ['DRAFT'].includes($deps[0]))`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`({ row }) => {
                     $queryEngine.request.delete(row.changeId).then(() => {
                       $message.success($t('common.successDelete'))
                       $queryEngine.state.paginationManagement.refresh()
                     })
                  }`)
                }
              },
              recall: {
                type: 'void',
                title: i18nExpression('common.recall'), // 撤回 [供应商]
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus'],
                  `$vendor() && ['VENDOR_SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(` ({ row }) => {
                      app.$prompt('', $t('bidMod.withdrawReason'), {
                        confirmButtonText: $t('common.confirm'),
                        cancelButtonText: $t('components.common.cancel'),
                        inputType: 'textarea',
                        inputValidator: value => !(!value || value.length > 500),
                        inputErrorMessage: $t('vendorMod.reasonLimit500')
                      }).then(({ value }) => {
                        let obj = {
                          changeId: row.changeId,
                          flowRemark: value
                        }
                        $queryEngine.request.save(obj, { customizeAction: 'vendorWithdraw' }).then((res) => {
                          app.$message({
                            message: $t('cusEntry.tipMessage.recallSuccess'), // 撤回成功
                            type: 'success'
                          })
                          $queryEngine.state.paginationManagement.refresh() // 查询旧数据
                        })
                      })
                  }`)
                }
              },
              vendorAbandoned: {
                type: 'void',
                title: i18nExpression('common.abandon'), // 废弃
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus'],
                  `$vendor() && ['VENDOR_WITHDRAW','VENDOR_REJECTED','WITHDRAW','REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmAbandonRow')
                  },
                  '@click': expression(` ({ row }) => {
                      $queryEngine.request.save(row.changeId, { customizeAction: 'vendorAbandoned' }).then((res) => {
                          app.$message({
                            message: $t('bidMod.abandonedSuccess'),
                            type: 'success'
                          })
                          $queryEngine.state.paginationManagement.refresh() // 查询旧数据
                        })
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
  app,
  i18nExpression,
  detail
}

const components = {

}

</script>

<template>
  <RenderEngine class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
