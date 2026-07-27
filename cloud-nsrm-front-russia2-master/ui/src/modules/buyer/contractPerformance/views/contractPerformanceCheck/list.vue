<script setup lang="ts">
import { onActivated } from 'vue-demi'
import {
  changeFieldVisibleByDeps,
  defineSchemas,
  generateXindexInOrder,
  expression,
  queryFieldValueExpression,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import ContractPerformanceCheckDetail from 'modb@/contractPerformance/views/contractPerformanceCheck/edit.vue'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  PerAcceptance: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: {
          immediate: true,
          transformResponse: expression(`(res) => {
            const data = JSON.parse(res)

            // TODO 后端把子实体查询主实体的关联关系给映射成 1:N，目前先由前端中转一下
            if (data.data.ref && data.data.ref.PerAcceptance) {
              Object.keys(data.data.ref.PerAcceptance).forEach((id) => {
                const item = data.data.ref.PerAcceptance[id]

                item.perPlanId = Array.isArray(item.perPlanId)
                  ? item.perPlanId[0]
                  : item.perPlanId
              })
            }

            return data
          }`)
        }
      }
    },
    'x-decorator': 'QueryEngine',
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
          eventName: 'PerAcceptance',
          '@listener': expression(`(params) => {
            try{
              if (params && params.reCalcContainerHeight) {
                $form.query('query').take(field => {
                  field.data.reCalcContainerHeight += 1
                })

                return
              }
            }  catch (e) {
              // console.log(e, 'catch')
            }


            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          perAcceptanceNo: {
            type: 'string',
            title: '{{ $t(\'bid_mod.perAcceptanceNo\') }}',
            'x-query-engine-query-operator': 'contains'
          },
          buId: {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'select-type': 'input'
            },
            'x-query-engine-relation': 'perPlanId'
          },
          vendorId: {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              propKey: 'companyId',
              showKey: 'companyName',
              name: 'scc_sup_company_info'
            },
            'x-query-engine-relation': 'perPlanId',
            'x-query-engine-relation-strict': true
          },
          createdFullName: {
            type: 'string',
            title: '{{$t(\'common.creator\')}}',
            'x-query-engine-query-operator': 'contains'
          },
          contractNo: {
            type: 'string',
            title: '{{$t(\'contractMod.contractNo_1\')}}',
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'perPlanId',
            'x-query-engine-relation-strict': true
          },
          status: {
            type: 'string',
            title: '{{$t(\'vendorMod.relegation.documentStatus\')}}',  // '单据状态'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_CHECK_STATUS'
            }
          },
          creationDate: {
            title: '{{$t(\'common.creationDate\')}}',  // '创建日期'
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
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
              pageUrl: '/api-cm/api-ql/PerAcceptance/query', // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('PerAcceptance.table', 'data.columns'),
              dictCodes: {
                status: 'CONTRACT_CHECK_STATUS'
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
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          'perAcceptanceId': {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            type: 'string',
            // 'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          'perAcceptanceNo': {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({row}) => $editTab("view", row)')
            },
            'x-render-table-column': {
              title: '{{$t(\'bid_mod.perAcceptanceNo\')}}',  // '合同验收单号'
              minWidth: 130,
              customRender: true
            }
          },
          'perPlanNo': {
            type: 'string',
            title: '{{$t(\'bid_mod.perPlanNo\')}}',  // '合同履约计划单号'
            'x-render-table-column': {
              minWidth: 150
            },
            'x-query-engine-relation': 'perPlanId'
          },
          'status': {
            type: 'string',
            title: '{{$t(\'vendorMod.relegation.documentStatus\')}}',  // '单据状态'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_CHECK_STATUS'
            },
            'x-render-table-column': {
              width: 110
            }
          },
          'buName': {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-render-table-column': {
              width: 130
            },
            'x-query-engine-relation': 'perPlanId'
          },
          'vendorName': {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-render-table-column': {
              width: 100
            },
            'x-query-engine-relation': 'perPlanId'
          },
          'vendorCode': {
            type: 'string',
            title: '{{$t(\'common.vendorCode\')}}',
            'x-render-table-column': {
              width: 100
            },
            'x-query-engine-relation': 'perPlanId'
          },
          'contractNo': {
            type: 'string',
            title: '{{$t(\'bidMod.compactIndex\')}}',  // '合同序号'
            'x-render-table-column': {
              width: 100
            },
            'x-query-engine-relation': 'perPlanId'
          },
          'nodePersonName': {
            type: 'string',
            title: '{{$t(\'common.nodeLeader\')}}',  // '节点负责人'
            'x-render-table-column': {
              width: 130
            },
            'x-query-engine-relation': 'perPlanMilestoneId'
          },
          'createdFullName': {
            type: 'string',
            title: '{{$t(\'common.creator\')}}',  // '创建人'
            'x-render-table-column': {
              width: 120
            }
          },
          'creationDate': {
            'x-query-engine-sort': 'desc',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d} {h}:{i}:{s}')
              }`)
            },
            default: undefined,
            title: '{{$t(\'common.creationTime\')}}',  // '创建时间'
            'x-render-table-column': {
              width: 150
            }
          },
          operation: {
            type: 'void',
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              width: 120,
              fixed: 'right'
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'RenderTableButtonList',
                'x-component-props': {
                  max: 2
                },
                properties: {
                  deliver: {
                    type: 'void',
                    title: '{{$t(\'supRisk.deliver\')}}',  // '交付'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      '[\'DRAFT\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $editTab("edit", row)')
                    }
                  },
                  edit: {
                    type: 'void',
                    title: '{{$t(\'common.edit\')}}',  // '编辑'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      '[\'WITHDRAW\', \'REJECTED\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $editTab("edit", row)')
                    }
                  },
                  confirm: {
                    type: 'void',
                    title: '{{$t(\'supRisk.deliverConfirm\')}}',  // '交付确认'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      '[\'SUPPLIER_SUBMITTED\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $editTab("edit", row)')
                    }
                  },
                  approval: {
                    type: 'void',
                    title: '{{$t(\'common.approve\')}}',  // '审批'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      '[\'SUBMITTED\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $editTab("view", row)')
                    }
                  },
                  submitApproval: {
                    type: 'void',
                    title: '{{$t(\'bidMod.submitApproval\')}}',  // '提交审批'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      '[\'FIRST_PASS\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({row}) => $editTab("edit", row)')
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

const $editTab = (type, row = {}) => {
  let name = row.perAcceptanceNo || ''
  let tab = {
    component: ContractPerformanceCheckDetail,
    params: {
      flag: type,
      row: {
        ...row,
        perPlanMilestoneId: row.perPlanMilestoneId?.perPlanMilestoneId ?? row.perPlanMilestoneId
      },
      tabName: '{{$t(\'contractMod.contractAcceptance\')}}' + name,  // '合同验收'
      fromContractPerformancePlan: app.$route.params?.from === 'contractPerformancePlan'
    },
    // title: name ? '合同验收' + name : '新增合同验收',
    title: name ? '{{$t(\'contractMod.contractAcceptance\')}}' + name : '{{$t(\'contractMod.newContractAcceptance\')}}',
    // name: '合同验收' + name
    name: '{{$t(\'contractMod.contractAcceptance\')}}' + name
  }
  emitTabAdd(tab)
}

onActivated(() => {
  if (app.$route.params?.from === 'contractPerformancePlan' && app.$route.params?.row) {
    $editTab('edit', app.$route.params.row)
  }
})

const tableHeader = [
  {
    prop: 'perAcceptanceNo',
    label: '{{$t(\'bid_mod.perAcceptanceNo\')}}',  // '合同验收单号'
    showType: 'button',
    btnStyle: 'text',
    minWidth: 150,
    callback: (row) => {
      $editTab('view', row)
    }
  },
  {
    prop: 'perPlanNo',
    label: '{{$t(\'bid_mod.perPlanNo\')}}',  //'合同履约计划单号'
    minWidth: 150
  },
  {
    prop: 'status',
    label: '{{$t(\'vendorMod.relegation.documentStatus\')}}',  // '单据状态'
    dataType: 'dict',
    code: 'CONTRACT_CHECK_STATUS',
    minWidth: 150
  },
  {
    prop: 'buName',
    label: '{{$t(\'components.organization.ORG\')}}', // 业务实体
    minWidth: 130
  },
  {
    prop: 'vendorName',
    label:'{{$t(\'common.companyName\')}}', // 供应商名称
    minWidth: 150
  },
  {
    prop: 'vendorCode',
    label: '{{$t(\'common.vendorCode\')}}', // 供应商编码
    minWidth: 120
  },
  {
    prop: 'contractNo',
    label: '{{$t(\'bidMod.compactIndex\')}}',  // '合同序号'
    minWidth: 130
  },
  {
    prop: 'nodePersonName',
    label: '{{$t(\'common.nodeLeader\')}}',  // '节点负责人'
    minWidth: 130
  },
  {
    prop: 'createdFullName',
    label: '{{$t(\'common.creator\')}}',  // '创建人'
    minWidth: 120
  },
  {
    prop: 'creationDate',
    label: '{{$t(\'common.creationTime\')}}',  // '创建时间'
    minWidth: 150,
    dataType: 'dateTime'
  }
]

const scope = {
  $editTab,
  tableHeader
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformanceCheckList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
