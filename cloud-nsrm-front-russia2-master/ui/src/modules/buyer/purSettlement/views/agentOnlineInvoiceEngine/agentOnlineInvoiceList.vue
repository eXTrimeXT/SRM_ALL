<script setup lang="ts">
import { onActivated, ref } from 'vue-demi'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'

import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression,
  i18nExpression
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './agentOnlineInvoiceDetail.vue'

const { emitTabAdd, app, t: $t } = usePageHelper()

const globalNickname = app.$store.getters.userInfo.username ?? null

let integrationMode = ref(null) // 流程模式

onActivated(() => {
  const { from, funName, formId: onlineInvoiceId, formNo: onlineInvoiceNum } = app.$route.params
  if (from === 'fromFun' && funName === 'agentOnlineInvoice') {
    $detailOne('view', {
      ...app.$route.params,
      onlineInvoiceId,
      onlineInvoiceNum
    })
  }
})

const $detailOne = (flag: string, row?: any) => {
  let name = row?.onlineInvoiceNum ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'agentOnlineInvoiceDetail' + name : 'agentOnlineInvoiceDetail'
    },
    title: name || $t('purSettlementMod.newOnlineInvoice'),
    name: name ? 'agentOnlineInvoiceDetail' + name : 'agentOnlineInvoiceDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add')
}

// 编辑
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 删除
const $deleteOne = async (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.delete(row.onlineInvoiceId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

//  查看审批
const $viewOne = (row: any) => {
  $detailOne('viewApproval', row)
}

// 废弃
const $abandonOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OnlineInvoice',
    'lang': 'zh-cn',
    'payload': [{ onlineInvoiceId: row.onlineInvoiceId }],
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
      'type': 'OnlineInvoice',
      'lang': 'zh-cn',
      'payload': [{ onlineInvoiceId: row.onlineInvoiceId }],
      'action': 'finalApprove'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const scope = {
  $addOne,
  $deleteOne,
  $readOne,
  $editOne,
  $abandonOne,
  $viewOne,
  $approvalOne,
  $confirmOne,
  app,
  globalNickname,
  integrationMode
}

const schema = defineSchemas({
  OnlineInvoice: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.payload.filter = {
              $or:{ 
                $and: {
                  onlineInvoiceType: {eq: 'VENDOR'},
                  invoiceStatus: {in: ['VENDOR_SUBMITTED','FIRST_REVIEW_APPROVED', 'REJECTED', 'UNDER_APPROVAL','FINAL_REVIEW_APPROVED', 'WITHDRAW']},
                },
                onlineInvoiceType: {eq: 'BUYER'}
              },
              ...data.payload.filter
            }
            data.payload.page = {
                sort: 'onlineInvoiceId desc',
                ...data.payload.page
            } 

            return data
          }`),
          preFormat: expression(`async(data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'onlineInvoice' })
            integrationMode.value = res.data

            if (data.ref?.OnlineInvoice) {
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'onlineInvoice' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.OnlineInvoice).forEach(id => {
                    const item = data.ref.OnlineInvoice[id]
                    const tempId = String(item.onlineInvoiceId)
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
          eventName: 'OnlineInvoice',
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
          onlineInvoiceNum: {
            type: 'string',
            title: i18nExpression('purSettlementMod.billingNumber'), // 开票单号
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
          creationDate: {
            title: i18nExpression('quota.createdDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          approvedDate: {
            title: i18nExpression('purSettlementMod.approvalCompleTime'), // 审批完成时间
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          invoiceNoticeNumber: {
            type: 'string',
            title: i18nExpression('purSettlementMod.statementNumber'), // 对账单号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'detailList',
            'x-query-engine-relation-strict': true
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('bidMod.provider'), // 供应商
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          },
          invoiceStatus: {
            type: 'string',
            title: i18nExpression('purSettlementMod.paymentPlanStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVOICE_STATUS'
            }
          },
          payMethod: {
            type: 'string',
            title: i18nExpression('paymentType.paymentWay'), // 付款方式
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_MODE'
            }
          },
          invoiceCode: {
            type: 'string',
            title: i18nExpression('accountMod.invoiceCode'), // 发票代码
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'ocrInvoiceList',
            'x-query-engine-relation-strict': true
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('purSettlementMod.createBillingSlip'), // 创建开票单
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
              pageUrl: '/api-sup-ce/api-ql/OnlineInvoice/query',
              tableHeader: queryFieldStatePropertyExpression('OnlineInvoice.table', 'data.columns'),
              dictCodes: {
                invoiceStatus: 'INVOICE_STATUS',
                businessType: 'BUSINESS_TYPE',
                payMethod: 'PAYMENT_MODE'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          onlineInvoiceId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true
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
          onlineInvoiceNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.billingNumber'), // 开票单号
              minWidth: 120,
              customRender: true
            }
          },
          invoiceStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVOICE_STATUS'
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
          taxTotalAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.taxTotalAmount'), // 开票含税金额
              minWidth: 120
            }
          },
          payMethod: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PAYMENT_MODE'
            },
            'x-render-table-column': {
              title: i18nExpression('paymentType.paymentWay'), // 付款方式
              minWidth: 120
            }
          },
          currencyName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('vendorMod.currencyCode'), // 币种
              minWidth: 120
            }
          },
          taxRate: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.taxRate'), // 税率
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
              title: i18nExpression('common.creator'), // 创建人
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
              title: i18nExpression('common.creationTime'), // 创建日期
              minWidth: 120
            }
          },
          approvedDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.approvedDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.approvedDate'), // 审批完成日期
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
                  // 拟定
                  ['.invoiceStatus'],
                  '[\'DRAFT\'].includes($deps[0])'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.cancelled'), // 作废
                'x-reactions': changeFieldVisibleByDeps(
                  // 审批已驳回/已撤回 && 当前用户是单据创建人
                  ['.invoiceStatus'],
                  '[\'REJECTED\',\'WITHDRAW\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('advancePayment.abandonTip')
                  },
                  '@click': expression('({ row }) => $abandonOne(row, $queryEngine, $message)')
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  // 拟定 && 当前用户是单据创建人
                  ['.invoiceStatus'],
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
                  // 审批流已开启 && (采购已提交/已初审 || （审批中 && 待办存在当前单据）)
                  ['.invoiceStatus'],
                  'app.flowWithTabMode.includes(integrationMode.value) && ([\'SUBMITTED\',\'FIRST_REVIEW_APPROVED\'].includes($deps[0]) || ([\'UNDER_APPROVAL\'].includes($deps[0]) && !!$table.getRowByIndex($self.index).arroverId))'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $approvalOne(row)')
                }
              },
              confirm: {
                type: 'void',
                title: i18nExpression('bidMod.approvalPass'), // 审批通过
                'x-reactions': changeFieldVisibleByDeps(
                  ['.invoiceStatus'],
                  // 审批流已关闭 && 已初审/采购已提交
                  'app.srmFlowMode.includes(integrationMode.value) && [\'FIRST_REVIEW_APPROVED\',\'SUBMITTED\'].includes($deps[0])'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $confirmOne(row,$queryEngine,$confirm,$message)')
                }
              },
              view: {
                type: 'void',
                title: i18nExpression('common.edit'), // 查看审批 -- 改为编辑
                'x-reactions': changeFieldVisibleByDeps(
                  // 供方已提交/审批已驳回/已撤回
                  ['.invoiceStatus'],
                  '[\'VENDOR_SUBMITTED\',\'REJECTED\',\'WITHDRAW\'].includes($deps[0])'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $viewOne(row)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="BuyerOnlineInvoiceList" />
</template>
