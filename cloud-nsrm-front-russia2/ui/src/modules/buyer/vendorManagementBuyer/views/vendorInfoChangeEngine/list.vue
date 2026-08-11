<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps, methodExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import detail from './edit'
const { emitTabAdd, app, createdUserIsCurrentUserByRow } = usePageHelper()

const schema = defineSchemas({
  InfoChange: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
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
          companyName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_display_buyer'
            },
            'x-query-engine-relation': 'companyInfoChange',
            'x-query-engine-relation-strict': true
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
          },
          legalPerson: {
            type: 'string',
            title: i18nExpression('vendorMod.legalPerson'), // 法定代表人
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'companyInfoChange',
            'x-query-engine-relation-strict': true
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 社会统一信用代码
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'companyInfoChange',
            'x-query-engine-relation-strict': true
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
               const tab = {
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
              code: 'RELATION_NEW'
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
          // 创建人
          createdBy: {
            type: 'string',
            'x-hidden': true,
            'x-render-table-column': {
              title: i18nExpression('bidMod.bidingCreatedBy'),
              minWidth: 120
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
            title: i18nExpression('创建人类型'),
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
                  `((app.$store.getters.userType == $deps[1] || $deps[1] == null) && ['DRAFT'].includes($deps[0])) ||
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
                        row,
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
                  `((app.$store.getters.userType == $deps[1] || $deps[1] == null) && ['DRAFT'].includes($deps[0]))`
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
              doApproval: {
                type: 'void',
                title: i18nExpression('vendorMod.doApproval'), // 审批
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus'],
                  `$buyer() && ['SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'doApproval',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.abandon'), // 废弃
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus'],
                  `['WITHDRAW', 'REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'doApproval',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              manage: {
                type: 'void',
                title: i18nExpression('contractMod.manage'), // 管理 [采购商]
                'x-reactions': changeFieldVisibleByDeps(
                  ['.changeStatus'],
                  `$buyer() && ['VENDOR_SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let changeId = row.changeId
                    let tab = {
                      component: detail,
                      params: {
                        flag: 'view',
                        changeId,
                        row,
                        tabName: 'detail' + row.companyName
                      },
                      title: row.companyName,
                      name: 'detail' + row.companyName
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
  app,
  i18nExpression,
  detail,
  $createdUserIsCurrentUserByRow: createdUserIsCurrentUserByRow
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
