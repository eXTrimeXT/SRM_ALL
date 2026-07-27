<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression, queryFieldValueExpression, queryFieldStatePropertyExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { exportExcelSegment, dataTimeSelectorSegment, yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment, requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import InvoiceNoticeDetail from './invoiceNoticeDetail'
import { onActivated } from 'vue-demi'
import { parseTime } from '@/utils'

const schema = defineSchemas({
  InvoiceNotice: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.payload.filter = {
              "$or": {
                "$and": {
                  "userType": {
                    "eq": "VENDOR"
                  },
                  "invoiceNoticeStatus": {
                    "in": [
                      "VENDOR_SUBMITTED",
                      "FIRST_REVIEW_APPROVED",
                      "REJECTED",
                      "UNDER_APPROVAL",
                      "FINAL_REVIEW_APPROVED",
                      "WITHDRAW"
                    ]
                  }
                },
                "userType": {
                  "eq": "BUYER"
                }
              },
              ...data.payload.filter
            }
            data.payload.page = {
              sort: 'invoiceNoticeId desc',
              ...data.payload.page
            }

            return data
          }`),
          preFormat: expression(`async(data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'invoiceNotice' })
            integrationMode.value = res.data
            if (data.ref?.InvoiceNotice) {
              // notSearchTodoMode ->列表页面查询后afterQuery定义哪些模式不需要查询代办数据
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'invoiceNotice' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.InvoiceNotice).forEach(id => {
                    const item = data.ref.InvoiceNotice[id]
                    const tempId = String(item.invoiceNoticeId)
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
            if(res?.data){
              res.data.forEach(item => item.creationDate = item.creationDate ? parseTime(item.creationDate,'{y}-{m}-{d}') : '-')
            }
          }`)
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'InvoiceNotice',
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
          invoiceNoticeNumber: {
            type: 'string',
            title: "{{$t('purSettlementMod.statementNumber')}}",
            'x-query-engine-query-operator': 'contains'
          },
          orgId: {
            type: 'string',
            title: "{{$t('vendorMod.ceeaOrgName')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1
            }
          },
          organizationId: {
            type: 'string',
            title: "{{$t('purchaseDemand.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'parent-id': -1
            }
          },
          creationDate: {
            title: "{{$t('bidMod.dateCreated')}}",
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          vendorId: {
            type: 'string',
            title: "{{$t('bidMod.provider')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          invoiceNoticeStatus: {
            type: 'string',
            title: "{{$t('purSettlementMod.paymentPlanStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVOICE_NOTICE_STATUS'
            }
          },
          approvedDate: {
            title: "{{$t('purSettlementMod.approvalCompleTime')}}",
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          receiveOrderNo: {
            type: 'string',
            title: "{{$t('accountMod.inboundReturnOrderNo')}}",
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'detailList',
            'x-query-engine-relation-strict': true
          },
          orderNumber: {
            type: 'string',
            title: "{{$t('purSettlementMod.orderNumber')}}",
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'detailList',
            'x-query-engine-relation-strict': true
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{ $t('purSettlementMod.newStatement')}}",
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $editTab('add',{})
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/InvoiceNotice/query",
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('InvoiceNotice.table', 'data.columns'),
              dictCodes: {
                invoiceNoticeStatus: 'INVOICE_NOTICE_STATUS'
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
          style: 'flex: 1;',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          invoiceNoticeId: {
            type: 'string',
            'X-query-engine-primary-key': true,
            'x-hidden': true
          },
          userType: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            title: "{{$t('quota.org')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            title: "{{$t('purchaseDemand.invOrg')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          invoiceNoticeNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => $editTab('view',row)`)
            },
            'x-render-table-column': {
              title: "{{$t('purSettlementMod.statementNumber')}}",
              minWidth: 120,
              customRender: true
            }
          },
          invoiceNoticeStatus: {
            type: 'string',
            title: "{{$t('purSettlementMod.paymentPlanStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVOICE_NOTICE_STATUS'
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('bidMod.provider')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          ceeaReceiveStartDate: {
            type: 'string',
            title: "{{$t('purSettlementMod.statementStartTime')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          ceeaReceiveEndDate: {
            type: 'string',
            title: "{{$t('purSettlementMod.statementEndTime')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          ceeaTaxTotalAmount: {
            type: 'string',
            title: "{{$t('contractMod.totalAmountTax')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          currencyName: {
            type: 'string',
            title: "{{$t('vendorMod.currencyCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          taxRate: {
            type: 'string',
            title: "{{$t('bid_mod.taxRate')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          createdFullName: { // createdUserName
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('purSettlementMod.creationDate')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          approvedDate: {
            type: 'string',
            title: "{{$t('purSettlementMod.approvalCompleTime')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 170,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.invoiceNoticeStatus'], `['DRAFT'].includes($deps[0])`)
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`({row}) => {
                    $deleteOne(row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.invoiceNoticeStatus', '.createdBy'],
                  fulfill: {
                    'state': {
                      visible: expression(`['DRAFT'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy === globalNickname`)
                    }
                  }
                }
              },
              approval: {
                type: 'void',
                title: "{{$t('common.approve')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $approvalOne(row)
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.invoiceNoticeStatus'],
                  fulfill: {
                    state: {
                      visible: expression(`app.flowWithTabMode.includes(integrationMode.value) ? (['SUBMITTED','FIRST_REVIEW_APPROVED'].includes($deps[0]) || ($deps[0] === 'UNDER_APPROVAL' && !!$table.getRowByIndex($self.index).arroverId)) : false`)
                    }
                  }
                }
              },
              approvalPass: {
                type: 'void',
                title: "{{$t('purSettlementMod.Approved')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $finalApprove(row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.invoiceNoticeStatus'],
                  fulfill: {
                    state: {
                      visible: expression(`['FIRST_REVIEW_APPROVED', 'SUBMITTED'].includes($deps[0]) && app.srmFlowMode.includes(integrationMode.value) && !$table.getRowByIndex($self.index).workflowAuditStatus`)
                    }
                  }
                }
              },
              viewTab: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $viewTab(row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.invoiceNoticeStatus'], `['VENDOR_SUBMITTED', 'REJECTED', 'WITHDRAW'].includes($deps[0])`)
              },
              cancelled: {
                type: 'void',
                title: "{{$t('common.cancelled')}}",
                'x-component-props': {
                  popConfirm: {
                    title: "{{$t('common.confirmAbandon')}}"
                  },
                  '@click': expression(`({row}) => {
                    $abandon(row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.invoiceNoticeStatus', '.createdBy'],
                  fulfill: {
                    state: {
                      visible: expression(`$deps[0] === 'REFUSED' && $table.getRowByIndex($self.index).createdBy === globalNickname`)
                    }
                  }
                }
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t: $t, app } = usePageHelper()

let integrationMode = ref(null) // 流程模式

onActivated(() => {
  const { from, funName, formId, formNo } = app.$route.params
  if (from === 'fromFun' && funName === 'purInvoice') {
    const row = {
      ...app.$route.params,
      invoiceNoticeId: formId,
      invoiceNoticeNumber: formNo
    }
    $editTab('view', row)
  }
})

const $abandon = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'InvoiceNotice',
    action: 'abandon',
    loading: true,
    query: {
      '*': {}
    },
    payload: [{
      invoiceNoticeId: row.invoiceNoticeId
    }]
  }).then(() => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}
const $finalApprove = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'InvoiceNotice',
    action: 'finalApprove',
    loading: true,
    query: {
      '*': {}
    },
    payload: [{
      invoiceNoticeId: row.invoiceNoticeId
    }]
  }).then(() => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $editTab = (type:string, row:Object) => {
  let tab = {}
  if (type === 'add') {
    // 新增
    tab = {
      component: InvoiceNoticeDetail,
      params: {
        flag: 'add',
        tabName: 'invoiceNoticeDetail'
      },
      title: $t('purSettlementMod.newStatement'),
      name: 'invoiceNoticeDetail'
    }
  } else {
    // 修改
    tab = {
      component: InvoiceNoticeDetail,
      params: {
        flag: type,
        invoiceNoticeId: row.invoiceNoticeId,
        tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
      },
      title: row.invoiceNoticeNumber,
      name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
    }
  }
  emitTabAdd(tab)
}

const $approvalOne = (row) => {
  emitTabAdd({
    component: InvoiceNoticeDetail,
    params: {
      flag: 'approvalOnly',
      invoiceNoticeId: row.invoiceNoticeId,
      tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber,
      activeWorkflowTab: true
    },
    title: row.invoiceNoticeNumber,
    name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
  })
}

const $viewTab = (row) => {
  // 修改
  const tab = {
    component: InvoiceNoticeDetail,
    params: {
      flag: 'viewApproval',
      invoiceNoticeId: row.invoiceNoticeId,
      tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
    },
    title: row.invoiceNoticeNumber,
    name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
  }
  emitTabAdd(tab)
}

const $deleteOne = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'InvoiceNotice',
    action: 'delete',
    payload: [
      row.invoiceNoticeId
    ],
    query: {
      '*': {}
    }
  }).then(() => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  app,
  $t,
  $editTab,
  $deleteOne,
  globalNickname: app.$store.getters.userInfo?.username,
  integrationMode,
  $approvalOne,
  $viewTab,
  $abandon,
  $finalApprove,
  parseTime
}
</script>

<template>
  <RenderEngine schemaKey="InvoiceNoticeList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
