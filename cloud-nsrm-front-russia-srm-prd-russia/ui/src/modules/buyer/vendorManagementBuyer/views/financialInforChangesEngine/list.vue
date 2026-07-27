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
import editDetail from './edit'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  FinanceInfoChangeHeader: {
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
          eventName: 'financialChange',
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
          changeHeaderCode: {
            type: 'string',
            title: "{{$t('vendorMod.inviteVendorNo')}}", // 单据编码
            'x-query-engine-query-operator': 'contains'
          },
          changeHeaderName: {
            type: 'string',
            title: "{{$t('bidMod.documentTitle')}}", // 单据标题
            'x-query-engine-query-operator': 'contains'
          },
          createdBy: {
            type: 'string',
            title: "{{$t('purchaseDemand.applicant')}}", // 申请人
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              propKey: 'username',
              name: 'scc_rbac_user_display'
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('bidMod.status')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
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
               const tab = {
                  component: editDetail,
                  params: {
                    flag: 'add',
                    tabName: 'addDetail'
                  },
                  title: $t('common.add'), // '新增供应商',
                  name: 'addDetail'
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
          changeHeaderId: {
            type: 'string',
            'x-hidden': true
          },
          changeHeaderCode: {
            type: 'string',
            title: "{{$t('vendorMod.inviteVendorNo')}}", // 单据编码
            'x-render-table-column': {
              width: 120
            }
          },
          changeHeaderName: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                editTab('view', row)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.documentTitle'), // 单据标题
              customRender: true
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('dataConfMod.triggerState')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('purchaseDemand.applicant')}}", // 申请人
            'x-render-table-column': {
              width: 120
            }
          },
          creationDate: {
            title: "{{$t('purchaseDemand.creationDate')}}", // 创建时间
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          approveTime: {
            title: "{{$t('supplierRating.approvalTime')}}", // 审批时间
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
            title: "{{$t('common.operation')}}",
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
                title: "{{$t('common.edit')}}", // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT', 'WITHDRAW', 'REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    editTab('edit', row)
                  }`)
                }
              },
              abandon: {
                type: 'void',
                title: "{{$t('common.abandon')}}", // 废弃
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['WITHDRAW', 'REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    editTab('approved', row)
                  }`)
                }
              },
              doApproval: {
                type: 'void',
                title: "{{$t('vendorMod.doApproval')}}", // 审批
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    editTab('approved', row)
                  }`)
                }
              },
              view: {
                type: 'void',
                title: "{{$t('vendorMod.check')}}", // 查看
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['APPROVED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    editTab('view', row)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}", // 删除
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`({ row }) => {
                    $queryEngine.request.delete(row.changeHeaderId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
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

const editTab = (type:any, row:any) => {
  const tab = {
    component: editDetail,
    params: {
      flag: type,
      row: row,
      tabName: 'editDetail' + row.changeHeaderName
    },
    title: row.changeHeaderName,
    name: 'editDetail' + row.changeHeaderName
  }
  emitTabAdd(tab)
}

const scope = {
  emitTabAdd,
  app,
  i18nExpression,
  editDetail,
  editTab
}

const components = {

}

</script>

<template>
  <RenderEngine schemaKey="financialInforChangesList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
