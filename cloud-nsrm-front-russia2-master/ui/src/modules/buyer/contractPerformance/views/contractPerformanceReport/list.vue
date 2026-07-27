<script setup lang="ts">
import {
  expression,
  defineSchemas,
  generateXindexInOrder,
  i18nExpression, queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import {
  selectByYOrNAndNoFormItemSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

const schema = defineSchemas({
  PerPlanReport: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          perPlanNo: {
            type: 'string',
            title: i18nExpression('bid_mod.perPlanNo'),
            'x-query-engine-query-operator': 'contains'
          },
          // buId: {
          //   type: 'string',
          //   title: i18nExpression('bid_mod.businessEntity'),
          //   'x-query-engine-query-operator': 'contains',
          //   'x-component': 'DictSelect',
          //   'x-component-props': {
          //     code: 'OUorganizationSelector'
          //   }
          // },
          milestoneType: {
            type: 'string',
            title: i18nExpression('bid_mod.milestoneType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE'
            }
          },
          vendorId: {
            type: 'string',
            title: i18nExpression('common.vendorName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_display_buyer'
            }
          },
          perAcceptanceNo: {
            type: 'string',
            title: i18nExpression('bid_mod.perAcceptanceNo'),
            'x-query-engine-query-operator': 'contains'
          },
          contractNo: {
            type: 'string',
            title: i18nExpression('contractMod.contractNo_1'),
            'x-query-engine-query-operator': 'contains'
          },
          paymentDelay: {
            type: 'string',
            title: i18nExpression('bid_mod.paymentDelay'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          milestoneDelay: {
            type: 'string',
            title: i18nExpression('bid_mod.milestoneDelay'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
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
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-cm/api-ql/PerPlanReport/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('PerPlanReport.table', 'data.columns'),
              dictCodes: {
                status: 'CONTRACT_PLAN_STATUS',
                milestoneType: 'MILESTONE_SCHEDULE',
                planStatus: 'MILESTONE_STATE',
                milestoneDelay: 'YES_OR_NO',
                paymentDelay: 'YES_OR_NO'
              }
            }
          },
          desc: {
            type: 'void',
            'x-component': 'span',
            'x-content': i18nExpression('cusEntry.supplement20250211.delayDaysDescription')  // '延期天数核算说明：负数表示提前天数，正数为延期天数'
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          editMode: 'row',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          perPlanId: {
            type: 'string',
            'x-hidden': true
          },
          perPlanNo: {
            type: 'string',
            title: i18nExpression('bid_mod.perPlanNo'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          perAcceptanceNo: {
            type: 'string',
            title: i18nExpression('bid_mod.perAcceptanceNo'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          buName: {
            type: 'string',
            title: i18nExpression('bid_mod.businessEntity'),
            'x-render-table-column': {
              minWidth: 130
            }
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          contractNo: {
            type: 'string',
            title: i18nExpression('bidMod.compactIndex'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.createdFullName'),
            'x-render-table-column': {
              minWidth: 150
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
            title: i18nExpression('bidMod.creationDate'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_PLAN_STATUS'
            }
          },
          milestoneType: {
            type: 'string',
            title: i18nExpression('bid_mod.perAcceptanceNo'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE'
            }
          },
          planStatus: {
            type: 'string',
            title: i18nExpression('contractMod.milestoneStatus'),
            'x-render-table-column': {
              minWidth: 130
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_STATE'
            }
          },
          nodePersonName: {
            type: 'string',
            title: i18nExpression('contract_mod.nodePerson'),  // '节点责任人'
            'x-render-table-column': {
              minWidth: 120
            }
          },
          planEndDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.planEndDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('cusEntry.supplement20250211.milestonePlanEndTime'),  // '里程碑计划结束时间'
            'x-render-table-column': {
              minWidth: 150
            }
          },
          actualCompleteDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.actualCompleteDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('cusEntry.supplement20250211.milestoneActualEndTime'),  // '里程碑实际结束时间'
            'x-render-table-column': {
              minWidth: 150
            }
          },
          milestoneDelay: {
            type: 'string',
            // '里程碑是否延期'
            title: i18nExpression('bid_mod.milestoneDelay'),
            ...selectByYOrNAndNoFormItemSegment,
            default: null,
            'x-render-table-column': {
              minWidth: 130
            }
          },
          milestoneDelayDay: {
            type: 'string',
            // '里程碑延期天数'
            title: i18nExpression('cusEntry.supplement20250211.milestoneDelayDays'),
            'x-render-table-column': {
              minWidth: 130
            }
          },
          invoiceNo: {
            type: 'string',
            // '开票申请单号'
            title: i18nExpression('contract_mod.invoiceApplyNo'),
            'x-render-table-column': {
              minWidth: 130
            }
          },
          invoiceCompleteDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.invoiceCompleteDate, '{y}-{m}-{d}')
              }`)
            },
            // '实际开票完成时间'
            title: i18nExpression('contract_mod.actualInvoiceDate'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          invoicedTaxedAmount: {
            type: 'string',
            // '实际开票金额含税'
            title: i18nExpression('cusEntry.supplement20250211.actualInvoiceAmountWithTax'),
            'x-component-props': {
              style: 'float:right'
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },
          paymentApplyNo: {
            type: 'string',
            // '付款申请单号'
            title: i18nExpression('contractMod.paymentApplyNumber'),
            'x-render-table-column': {
              minWidth: 130
            }
          },
          palnPaymentDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.palnPaymentDate, '{y}-{m}-{d}')
              }`)
            },
            // '计划付款完成时间'
            title: i18nExpression('cusEntry.supplement20250211.planPaymentCompletionTime'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          actualPaymentDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.actualPaymentDate, '{y}-{m}-{d}')
              }`)
            },
            // '实际付款完成时间'
            title: i18nExpression('cusEntry.supplement20250211.actualPaymentCompletionTime'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          stagePaymentAmount: {
            type: 'string',
            // '实际付款金额'
            title: i18nExpression('cusEntry.supplement20250211.actualPaymentAmount'),
            'x-component-props': {
              style: 'float:right'
            },
            'x-render-table-column': {
              minWidth: 130
            }
          },
          // paymentDelay: {
          //   type: 'string',
          //   title: '付款是否延期',
          //   'x-render-table-column': {
          //     minWidth: 130
          //   }
          // },
          paymentDelayDay: {
            type: 'string',
            // '付款延期天数'
            title: i18nExpression('cusEntry.supplement20250211.paymentDelayDays'),
            'x-render-table-column': {
              minWidth: 130
            }
          }
        })
      }
    }
  }
})

const tableHeader = [
  {
    prop: 'perPlanNo',
    // '合同履约计划单号'
    label: i18nExpression('bid_mod.perPlanNo'),
    minWidth: 150
  },
  {
    prop: 'perAcceptanceNo',
    // '合同验收单号'
    label: i18nExpression('bid_mod.perAcceptanceNo'),
    minWidth: 150
  },
  {
    prop: 'buName',
    label: i18nExpression('components.organization.ORG'), // 业务实体
    minWidth: 130
  },
  {
    prop: 'vendorName',
    label: i18nExpression('common.companyName'), // 供应商名称
    minWidth: 150
  },
  {
    prop: 'vendorCode',
    label: i18nExpression('common.vendorCode'), // 供应商编码
    minWidth: 120
  },
  {
    prop: 'contractNo',
    label: i18nExpression('bidMod.compactIndex'),  // '合同序号'
    minWidth: 150
  },
  {
    prop: 'createdFullName',
    label: i18nExpression('cusEntry.supplement20250211.contractAcceptanceFormCreator'),  // '合同验收单创建人'
    minWidth: 150
  },
  {
    prop: 'creationDate',
    label: i18nExpression('cusEntry.supplement20250211.contractAcceptanceFormCreationTime'),  // '合同验收单创建时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'status',
    label: i18nExpression('vendorMod.relegation.documentStatus'),  // '单据状态'
    dataType: 'dict',
    code: 'CONTRACT_PLAN_STATUS',
    minWidth: 120
  },
  {
    prop: 'milestoneType',
    label: i18nExpression('bid_mod.milestoneType'),  // '里程碑'
    dataType: 'dict',
    code: 'MILESTONE_SCHEDULE',
    minWidth: 130
  },
  {
    prop: 'planStatus',
    label: i18nExpression('contractMod.milestoneStatus'),  // '里程碑状态'
    minWidth: 130,
    dataType: 'dict',
    code: 'MILESTONE_STATE'
  },
  {
    prop: 'nodePersonName',
    label: i18nExpression('contract_mod.nodePerson'),  // '节点责任人'
    minWidth: 120
  },
  {
    prop: 'planEndDate',
    label: i18nExpression('cusEntry.supplement20250211.milestonePlanEndTime'),  // '里程碑计划结束时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'actualCompleteDate',
    label: i18nExpression('cusEntry.supplement20250211.milestoneActualEndTime'),  // '里程碑实际结束时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'milestoneDelay',
    label: i18nExpression('bid_mod.milestoneDelay'),  // '里程碑是否延期'
    dataType: 'dict',
    code: 'YES_OR_NO',
    minWidth: 130
  },
  {
    prop: 'milestoneDelayDay',
    label: i18nExpression('cusEntry.supplement20250211.milestoneDelayDays'),  // '里程碑延期天数'
    minWidth: 130
  },
  {
    prop: 'invoiceNo',
    label: i18nExpression('contract_mod.invoiceApplyNo'),  // '开票申请单号'
    minWidth: 130
  },
  {
    prop: 'invoiceCompleteDate',
    label: i18nExpression('contract_mod.actualInvoiceDate'),  // '实际开票完成时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'invoicedTaxedAmount',
    label: i18nExpression('cusEntry.supplement20250211.actualInvoiceAmountWithTax'),  // '实际开票金额含税'
    minWidth: 150
  },
  {
    prop: 'paymentApplyNo',
    label: i18nExpression('contractMod.paymentApplyNumber'), //  '付款申请单号'
    minWidth: 130
  },
  {
    prop: 'palnPaymentDate',
    label: i18nExpression('cusEntry.supplement20250211.planPaymentCompletionTime'),  // '计划付款完成时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'actualPaymentDate',
    label: i18nExpression('cusEntry.supplement20250211.actualPaymentCompletionTime'),  // '实际付款完成时间'
    minWidth: 150,
    dataType: 'dateTime'
  },
  {
    prop: 'stagePaymentAmount',
    label: i18nExpression('cusEntry.supplement20250211.actualPaymentAmount'),  // '实际付款金额'
    minWidth: 130
  },
  {
    prop: 'paymentDelay',
    label: i18nExpression('bid_mod.paymentDelay'),  // '付款是否延期'
    dataType: 'dict',
    code: 'YES_OR_NO',
    minWidth: 130
  },
  {
    prop: 'paymentDelayDay',
    label: i18nExpression('cusEntry.supplement20250211.paymentDelayDays'),  // '付款延期天数'
    minWidth: 130
  }
]
const scope = {
  tableHeader
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformanceReport" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
