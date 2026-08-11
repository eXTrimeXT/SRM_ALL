<script setup lang="ts">
import { onActivated, ref } from 'vue-demi'
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { getValidateFailureSequence, currying } from '@/utils'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './advancePaymentDetail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const globalNickname = app.$store.getters.userInfo?.username

let integrationMode = ref(null)

onActivated(() => {
  const { from, funName, formId: advanceApplyId, formNo: advanceApplyNumber, row, form } = app.$route.params
  if (from === 'fromFun' && funName === 'purPaymentApply') {
    $readOne(
      {
        ...app.$route.params,
        advanceApplyId,
        advanceApplyNumber // tab 标题显示
      }
    )
  } else if (from === 'contractPerformancePlan') {
    $playPlan(row, form)
  }
})

const $playPlan = (row: any, head: any) => {
  emitTabAdd({
    component: edit,
    params: {
      flag: 'playPlan',
      row,
      head,
      tabName: 'advancePaymentDetail' + row.advanceApplyNumber
    },
    title: $t('purSettlementMod.prepaidApplyDetail'),
    name: 'advancePaymentDetail' + row.advanceApplyNumber
  })
}

const $detailOne = (flag: string, row?: any) => {
  let name = row?.advanceApplyNumber || ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'advancePaymentDetail' + name : 'advancePaymentDetail'
    },
    title: name || $t('purSettlementMod.prepaidApplyDetail'),
    name: name ? 'advancePaymentDetail' + name : 'advancePaymentDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add')
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}
// 删除
const $deleteOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.delete(row.advanceApplyId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 废弃 TODO-接口报错
const $abandonOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'AdvanceApply',
    'lang': 'zh-cn',
    'payload': [{ advanceApplyId: row.advanceApplyId }],
    'action': 'abandon'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 审批--跳审批流
const $approvalOne = (row: any) => {
  $detailOne('approvalOnly', row)
}

// 审批通过
const $confirmOne = (row: any, $queryEngine: any, $confirm: any, $message: any) => {
  $confirm($t('orderMod.supplierConfirm'), // 确认后审批通过！
    $t('common.tips'),
    {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('common.cancel'),
      type: 'warning'
    }).then(() => {
    $queryEngine.request.baseRequest({
      'type': 'AdvanceApply',
      'lang': 'zh-cn',
      'payload': [{ advanceApplyId: row.advanceApplyId }],
      'action': 'approve'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const scope = {
  $addOne,
  $approvalOne,
  $confirmOne,
  $abandonOne,
  $deleteOne,
  $readOne,
  $editOne,
  app,
  globalNickname,
  integrationMode
}

const schema = defineSchemas({
  AdvanceApply: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.payload.page = {
                sort: 'lastUpdateDate desc',
                ...data.payload.page
            } 

            return data
          }`),
          preFormat: expression(`async (data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ADVANCEPAYMENT' })
            integrationMode.value = res.data

            if (data.ref?.AdvanceApply) {
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'ADVANCEPAYMENT' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.AdvanceApply).forEach(id => {
                    const item = data.ref.AdvanceApply[id]
                    const tempId = String(item.advanceApplyId)
                    if (maps.includes(tempId)) {
                      item.workflowAuditStatus = 'WAIT'
                      item.arroverId = tempId
                    }
                  })
                }
              }
            }
            return data
          }`)
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'AdvanceApplyHead',
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
          advanceApplyNumber: {
            type: 'string',
            title: i18nExpression('purSettlementMod.advancePaymentNum'), // 预付款申请单号
            'x-query-engine-query-operator': 'contains'
          },
          orgId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            }
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': expression('$form.values.query.orgId || -1'),
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect')
            }
          },
          creationDate: {
            title: i18nExpression('quota.createdDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyCode',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          },
          advanceApplyStatus: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applyStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_APPLY_STATUS'
            }
          },
          createdBy: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applicant'), // 申请人
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              propKey: 'username',
              name: 'scc_rbac_user_display'
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
            title: i18nExpression('purSettlementMod.newAdvanceApply'), // 新增
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-query-engine-skip': true,
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          advanceApplyId: { // 单据ID
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.businessEntity'), // 业务实体
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            }
          },
          advanceApplyNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.advancePaymentNum'), // 预付款申请单号
              minWidth: 120,
              customRender: true
            }
          },
          advanceApplyStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ADVANCE_APPLY_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.paymentPlanStatus'), // 单据状态
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // 供应商编码
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          includeTaxAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.includeTaxAmount2'), // 申请付款金额
              minWidth: 120
            }
          },
          unWrittenOffAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.unWrittenOffAmount'), // 未核销金额
              minWidth: 120
            }
          },
          currencyName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('quota.currency'), // 币种
              minWidth: 120
            }
          },
          taxRate: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.taxRate'), // 税率
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.applicant'), // 创建人
              minWidth: 120
            }
          },
          departmentName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.ceeaDepartment'), // 申请部门
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)
            },
            'x-render-table-column': {
              title: i18nExpression('quota.createdDate'), // 创建日期
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 160,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.advanceApplyStatus'],
                  // 拟定/已驳回/已撤回
                  '[\'DRAFT\', \'REJECTED\', \'WITHDRAW\'].includes($deps[0])'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },

              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  // 拟定 && 当前用户是单据创建人
                  ['.advanceApplyStatus'],
                  '$deps[0] === \'DRAFT\' && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression('({ row }) => $deleteOne(row, $queryEngine, $message)')
                }
              },

              approval: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-reactions': changeFieldVisibleByDeps(
                  ['.advanceApplyStatus'],
                  // 审批流已开启 && (已提交 || （审批中 && 待办存在当前单据）)
                  'app.flowWithTabMode.includes(integrationMode.value) && ([\'SUBMITTED\'].includes($deps[0]) || ([\'UNDER_APPROVAL\'].includes($deps[0])&& !!$table.getRowByIndex($self.index).arroverId))'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $approvalOne(row)')
                }
              },
              confim: {
                type: 'void',
                title: i18nExpression('bidMod.approvalPass'), // 审批通过
                'x-reactions': changeFieldVisibleByDeps(
                  ['.advanceApplyStatus'],
                  // 审批流已关闭 && 已提交 && 当前用户是单据创建人
                  'app.srmFlowMode.includes(integrationMode.value) && [\'SUBMITTED\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $confirmOne(row,$queryEngine,$confirm,$message)')
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.cancelled'), // 作废
                'x-reactions': changeFieldVisibleByDeps(
                  // 已驳回/已撤回/已提交 && 当前用户是单据创建人
                  ['.advanceApplyStatus'],
                  '[\'REJECTED\', \'WITHDRAW\',\'SUBMITTED\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy=== globalNickname'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('advancePayment.abandonTip')
                  },
                  '@click': expression('({ row }) => $abandonOne(row, $queryEngine, $message)')
                }
              }

            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="AdvanceApply" />
</template>
