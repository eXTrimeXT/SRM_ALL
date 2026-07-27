<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression, queryFieldValueExpression, queryFieldStatePropertyExpression } from '@meicloud/render-engine'
import { exportExcelSegment, dataTimeSelectorSegment, yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment, requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import InvoiceNoticeDetail from './invoiceNoticeDetail'
import { parseTime } from '@/utils'

const schema = defineSchemas({
  InvoiceNoticeVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {  
            data.payload.filter = {
              vendorId: {eq: app.$store.getters.userInfo.companyId},
              $or:{
                userType: {eq: 'VENDOR'},
                invoiceNoticeStatus: {eq: 'FINAL_REVIEW_APPROVED'},
              },
              ...data.payload.filter
            }
            data.payload.page = {
              sort: 'invoiceNoticeId desc',
              ...data.payload.page
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
          eventName: 'InvoiceNoticeVendor',
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
              'parent-id': -1,
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            }
          },
          organizationId: {
            type: 'string',
            title: "{{$t('purchaseDemand.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'parent-id': expression('$form.values.query.orgId || -1')
            }
          },
          creationDate: {
            title: "{{$t('bidMod.dateCreated')}}",
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
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
              pageUrl: "/api-sup-ce/api-ql/InvoiceNoticeVendor/query",
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('InvoiceNoticeVendor.table', 'data.columns'),
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
          rejectReason: {
            type: 'string',
            title: "{{$t('purSettlementMod.rejectReason')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 130,
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
                // 拟定/采购已驳回
                'x-reactions': changeFieldVisibleByDeps(['.invoiceNoticeStatus'], `['DRAFT','FIRST_REJECTED'].includes($deps[0])`)
              },
              cancelled: {
                type: 'void',
                title: "{{$t('common.cancelled')}}",
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('purSettlementMod.isDiscarded')}}"
                  },
                  '@click': expression(`({row}) => {
                    $abandon(row,$queryEngine,$bus,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.invoiceNoticeStatus'], `['FIRST_REJECTED'].includes($deps[0])`)
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
                'x-reactions': changeFieldVisibleByDeps(['.invoiceNoticeStatus'], `['DRAFT'].includes($deps[0])`)
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $abandon = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'InvoiceNoticeVendor',
    action: 'abandon',
    loading: true,
    query: {
      '*': {}
    },
    payload: [{
      invoiceNoticeId: row.invoiceNoticeId
    }]
  }).then(() => {
    $message.success(t('common.success'))
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
      title: t('purSettlementMod.newStatement'),
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

const $deleteOne = (row:any, $queryEngine:any, $bus:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'InvoiceNoticeVendor',
    action: 'delete',
    payload: [
      row.invoiceNoticeId
    ],
    query: {
      '*': {}
    }
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  app,
  $editTab,
  $deleteOne,
  $abandon,
  parseTime
}
</script>

<template>
  <RenderEngine schemaKey="InvoiceNoticeList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
