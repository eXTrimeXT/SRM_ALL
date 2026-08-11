<script setup lang="ts">
import { onActivated, ref } from 'vue-demi'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { getValidateFailureSequence, currying } from '@/utils'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './paymentPlanDetail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const globalNickname = app.$store.getters.userInfo?.username

let integrationMode = ref(null)

onActivated(() => {
  const { from, funName, formId: paymentApplyId, formNo: paymentApplyNumber, row, form } = app.$route.params
  if (from === 'fromFun' && funName === 'purPaymentApply') {
    $readOne(
      {
        ...app.$route.params,
        paymentApplyId,
        paymentApplyNumber // tab 标题显示
      }
    )
  } else if (from === 'contractPerformancePlan') { // 来源：采购订单变更
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
      tabName: 'paymentPlanDetail'
    },
    title: $t('contractMod.paymentApplyDetail'),
    name: 'paymentPlanDetail'
  })
}

const $detailOne = (flag: string, row?: any, activeWorkflowTab?: any) => {
  let name = row?.paymentApplyNumber || ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      activeWorkflowTab,
      tabName: name ? 'paymentPlanDetail' + name : 'paymentPlanDetail'
    },
    title: name || $t('contractMod.newPaymentApply'),
    name: name ? 'paymentPlanDetail' + name : 'paymentPlanDetail'
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
  $queryEngine.request.delete(row.paymentApplyId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 批量废弃
const $batchAbandon = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('PaymentApply.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }

  const fn = currying(getValidateFailureSequence)(rows, 'sequence')
  // 只有单据状态为【已提交/已驳回/已撤回】可以作废
  let sequences = fn((row: any) => !['SUBMITTED', 'REJECTED', 'WITHDRAW'].includes(row.status))
  if (sequences) {
    // 只有单据状态为【已提交/已驳回/已撤回】可以作废，序号${sequences}不可作废
    return $message.warning(`${$t('purPaymentApply.prompt1')}${sequences}${$t('purPaymentApply.prompt2')}`)
  }
  // 不做作废他人创建的单据
  sequences = fn((row: any) => row.createdBy !== globalNickname)
  if (sequences) {
    // 不做作废他人创建的单据，序号${sequences}不可作废
    return $message.warning(`${$t('purPaymentApply.prompt3')}${sequences}${$t('purPaymentApply.prompt2')}`)
  }

  // 确定批量作废所选数据？
  $confirm($t('purPaymentApply.prompt4'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $abandonOne(rows, $queryEngine, $message)
  })
}
// 废弃
const $abandonOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      paymentApplyId: item.paymentApplyId
    }
  })
  $queryEngine.request.baseRequest({
    'type': 'PaymentApply',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'abandon'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 批量付款
const $batchPayment = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('PaymentApply.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }
  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => !(row.status === 'APPROVAL' && row.paymentStatus === 'UN_PAID'))
  if (sequences) {
    // 只有已审批的未付款单据才可以付款，序号${sequences}不可付款
    return $message.warning(`${$t('purPaymentApply.prompt5')}${sequences}${$t('purPaymentApply.prompt6')}`)
  }

  $confirm($t('purSettlementMod.payAmountListConfirm'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    let params = rows.map((item: any) => {
      return {
        paymentApplyId: item.paymentApplyId
      }
    })
    $queryEngine.request.baseRequest({
      'type': 'PaymentApply',
      'lang': 'zh-cn',
      'payload': params,
      'action': 'pay'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

// 审批--跳审批流
const $approvalOne = (row: any) => {
  $detailOne('approvalOnly', row, true)
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
      'type': 'PaymentApply',
      'lang': 'zh-cn',
      'payload': [{ paymentApplyId: row.paymentApplyId }],
      'action': 'approve'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const scope = {
  $addOne,
  $batchAbandon,
  $batchPayment,
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
  PaymentApply: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          preFormat: expression(`async (data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'paymentapply' })
            integrationMode.value = res.data
            
            if (data.ref?.PaymentApply) {
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'paymentapply' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.PaymentApply).forEach(id => {
                    const item = data.ref.PaymentApply[id]
                    const tempId = String(item.paymentApplyId)
                    if (maps.includes(tempId)) {
                      item.workflowAuditStatus = 'WAIT'
                      item.arroverId = tempId
                    }
                  })
                }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            res.data.forEach((item,index) =>{
              item.sequence = index + 1
            })
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
          eventName: 'PaymentApplyHead',
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
          paymentApplyNumber: {
            type: 'string',
            title: i18nExpression('contractMod.paymentApplyNumber'), // 付款申请单号
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
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              'parent-id': expression('$form.values.query.orgId || -1')
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.creator'), // 创建人
            'x-query-engine-query-operator': 'contains'
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'), // 供应商编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyCode',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          },
          creationDate: {
            title: i18nExpression('quota.createdDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          status: {
            type: 'string',
            title: i18nExpression('purSettlementMod.paymentPlanStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_APPLY_STATUS'
            }
          },
          paymentStatus: {
            type: 'string',
            title: i18nExpression('contractMod.payStatus'), // 付款状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_PAID_STATUS'
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
            title: i18nExpression('common.add'), // 新增
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
            }
          },
          // 自定义导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: '/api-sup-ce/api-ql/PaymentApply/query',
              tableHeader: queryFieldStatePropertyExpression('PaymentApply.table', 'data.columns'),
              dictCodes: {
                status: 'PAYMENT_APPLY_STATUS',
                paymentStatus: 'PAYMENT_PAID_STATUS'
              }
            }
          },
          abandon: {
            type: 'void',
            title: i18nExpression('common.cancelled'), // 作废
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchAbandon($self, $queryEngine,$message,$confirm)              
              }`)
            }
          },
          payment: {
            type: 'void',
            title: i18nExpression('purSettlementMod.payment'), // 付款
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchPayment($self, $queryEngine,$message,$confirm)              
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
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          paymentApplyId: { // 单据ID
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true,
            'x-query-engine-sort': 'desc'
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
          paymentApplyNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('contractMod.paymentApplyNumber'), // 付款申请单号
              minWidth: 120,
              customRender: true
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_APPLY_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.paymentPlanStatus'), // 单据状态
              minWidth: 120
            }
          },
          paymentStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_PAID_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('contractMod.payStatus'), // 付款状态
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
          actualInvoiceAmountY: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.actualInvoiceAmountY2'), // 发票含税总金额
              minWidth: 120
            }
          },
          includeTaxAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.includeTaxAmount3'), // 付款含税总金额
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
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'), // 创建人
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
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 120
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
            'x-render-table-column': {
              title: i18nExpression('common.lastUpdateDate'), // 更新日期
              minWidth: 120
            },
            'x-hidden': true
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
                  ['.status'],
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
                  ['.status'],
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
                  ['.status'],
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
                  ['.status'],
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
                  // 已提交/已驳回/已撤回 && 当前用户是单据创建人
                  ['.status'],
                  '[\'SUBMITTED\',\'REJECTED\', \'WITHDRAW\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy=== globalNickname'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('advancePayment.abandonTip') // 确认作废该行数据
                  },
                  '@click': expression('({ row }) => $abandonOne([row], $queryEngine, $message)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="PaymentApply" />
</template>
