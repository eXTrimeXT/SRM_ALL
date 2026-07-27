<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  i18nExpression, queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import {
  selectByYOrNAndNoFormItemSegment
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
            'x-content': '延期天数核算说明：负数表示提前天数，正数为延期天数'
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
            type: 'string',
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
            title: '节点责任人',
            'x-render-table-column': {
              minWidth: 120
            }
          },
          planEndDate: {
            type: 'string',
            title: '里程碑计划结束时间',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          actualCompleteDate: {
            type: 'string',
            title: '里程碑实际结束时间',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          milestoneDelay: {
            type: 'string',
            title: '里程碑是否延期',
            ...selectByYOrNAndNoFormItemSegment,
            default: null,
            'x-render-table-column': {
              minWidth: 130
            }
          },
          milestoneDelayDay: {
            type: 'string',
            title: '里程碑延期天数',
            'x-render-table-column': {
              minWidth: 130
            }
          },
          invoiceNo: {
            type: 'string',
            title: '开票申请单号',
            'x-render-table-column': {
              minWidth: 130
            }
          },
          invoiceCompleteDate: {
            type: 'string',
            title: '实际开票完成时间',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          invoicedTaxedAmount: {
            type: 'string',
            title: '实际开票金额含税',
            'x-component-props': {
              style: 'float:right'
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },
          paymentApplyNo: {
            type: 'string',
            title: '付款申请单号',
            'x-render-table-column': {
              minWidth: 130
            }
          },
          palnPaymentDate: {
            type: 'string',
            title: '计划付款完成时间',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          actualPaymentDate: {
            type: 'string',
            title: '实际付款完成时间',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          stagePaymentAmount: {
            type: 'string',
            title: '实际付款金额',
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
            title: '付款延期天数',
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
    label: '合同履约计划单号',
    minWidth: 150
  },
  {
    prop: 'perAcceptanceNo',
    label: '合同验收单号',
    minWidth: 150
  },
  {
    prop: 'buName',
    label: '业务实体', // 业务实体
    minWidth: 130
  },
  {
    prop: 'vendorName',
    label: '供应商名称', // 供应商名称
    minWidth: 150
  },
  {
    prop: 'vendorCode',
    label: '供应商编码', // 供应商编码
    minWidth: 120
  },
  {
    prop: 'contractNo',
    label: '合同序号',
    minWidth: 150
  },
  {
    prop: 'createdFullName',
    label: '合同验收单创建人',
    minWidth: 150
  },
  {
    prop: 'creationDate',
    label: '合同验收单创建时间',
    minWidth: 150
  },
  {
    prop: 'status',
    label: '单据状态',
    dataType: 'dict',
    code: 'CONTRACT_PLAN_STATUS',
    minWidth: 120
  },
  {
    prop: 'milestoneType',
    label: '里程碑',
    dataType: 'dict',
    code: 'MILESTONE_SCHEDULE',
    minWidth: 130
  },
  {
    prop: 'planStatus',
    label: '里程碑状态',
    minWidth: 130,
    dataType: 'dict',
    code: 'MILESTONE_STATE'
  },
  {
    prop: 'nodePersonName',
    label: '节点责任人',
    minWidth: 120
  },
  {
    prop: 'planEndDate',
    label: '里程碑计划结束时间',
    minWidth: 150
  },
  {
    prop: 'actualCompleteDate',
    label: '里程碑实际结束时间',
    minWidth: 150
  },
  {
    prop: 'milestoneDelay',
    label: '里程碑是否延期',
    dataType: 'dict',
    code: 'YES_OR_NO',
    minWidth: 130
  },
  {
    prop: 'milestoneDelayDay',
    label: '里程碑延期天数',
    minWidth: 130
  },
  {
    prop: 'invoiceNo',
    label: '开票申请单号',
    minWidth: 130
  },
  {
    prop: 'invoiceCompleteDate',
    label: '实际开票完成时间',
    minWidth: 150
  },
  {
    prop: 'invoicedTaxedAmount',
    label: '实际开票金额含税',
    minWidth: 150
  },
  {
    prop: 'paymentApplyNo',
    label: '付款申请单号',
    minWidth: 130
  },
  {
    prop: 'palnPaymentDate',
    label: '计划付款完成时间',
    minWidth: 150
  },
  {
    prop: 'actualPaymentDate',
    label: '实际付款完成时间',
    minWidth: 150
  },
  {
    prop: 'stagePaymentAmount',
    label: '实际付款金额',
    minWidth: 130
  },
  {
    prop: 'paymentDelay',
    label: '付款是否延期',
    dataType: 'dict',
    code: 'YES_OR_NO',
    minWidth: 130
  },
  {
    prop: 'paymentDelayDay',
    label: '付款延期天数',
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
