<script setup lang="ts">
import { onActivated } from 'vue-demi'
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression } from '@meicloud/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import ContractPerformanceCheckDetailEngine from './edit-engine'

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
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
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
              }
            } catch (e) {
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
            title: '合同验收单号',
            'x-query-engine-query-operator': 'contains'
          },
          buId: {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: false
            },
            'x-query-engine-relation': 'perPlanId'
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
            title: '单据状态',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_CHECK_STATUS'
            }
          },
          creationDate: {
            title: '创建日期',
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
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
          void: {}
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
          perAcceptanceId: {
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
          perAcceptanceNo: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => {
                let type = ['SUBMITTED', 'APPROVED'].includes(row.status) ? 'approval' : 'view'
                $editTab(type, row)
              }`)
            },
            'x-render-table-column': {
              title: '合同验收单号',
              minWidth: 150,
              customRender: true
            }
          },
          'perPlanNo': {
            type: 'string',
            title: '合同履约计划单号',
            'x-query-engine-relation': 'perPlanId',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          status: {
            type: 'string',
            title: '单据状态',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_CHECK_STATUS'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          'buName': {
            type: 'string',
            title: '{{$t(\'bid_mod.businessEntity\')}}',
            'x-query-engine-relation': 'perPlanId',
            'x-render-table-column': {
              minWidth: 130
            }
          },
          'vendorName': {
            type: 'string',
            title: '{{$t(\'common.vendorName\')}}',
            'x-query-engine-relation': 'perPlanId',
            'x-render-table-column': {
              minWidth: 150
            }
          },
          'vendorCode': {
            type: 'string',
            title: '{{$t(\'common.vendorCode\')}}',
            'x-query-engine-relation': 'perPlanId',
            'x-render-table-column': {
              minWidth: 120
            }
          },
          'contractNo': {
            type: 'string',
            title: '合同序号',
            'x-query-engine-relation': 'perPlanId',
            'x-render-table-column': {
              minWidth: 100
            }
          },
          'nodePersonName': {
            type: 'string',
            title: '节点负责人',
            'x-query-engine-relation': 'perPlanMilestoneId',
            'x-render-table-column': {
              minWidth: 130
            }
          },
          createdFullName: {
            type: 'string',
            title: '创建人',
            'x-render-table-column': {
              minWidth: 120
            }
          },
          creationDate: {
            'x-query-engine-sort': 'desc',
            title: '创建时间',
            type: 'date',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            default: undefined,
            'x-render-table-column': {
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              minWidth: 120,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
              properties: {
                deliver: {
                  type: 'void',
                  title: '交付',
                  'x-reactions': changeFieldVisibleByDeps(
                    ['.status'],
                    '[\'DRAFT\'].includes($deps[0])'
                  ),
                  'x-component-props': {
                    type: 'text',
                    '@click': expression(`({ row }) => {
                      $editTab('edit', row)
                    }`)
                  }
                },
                edit: {
                  type: 'void',
                  title: '编辑',
                  'x-reactions': changeFieldVisibleByDeps(
                    ['.status'],
                    '[\'FIRST_REJECTED\'].includes($deps[0])'
                  ),
                  'x-component-props': {
                    type: 'text',
                    '@click': expression(`({ row }) => {
                      $editTab('edit', row)
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

const { emitTabAdd, app } = usePageHelper()

const $editTab = (type: string, row: any) => {
  let name = row.perAcceptanceNo || ''
  emitTabAdd({
    component: ContractPerformanceCheckDetailEngine,
    params: {
      flag: type,
      row: {
        ...row,
        perPlanMilestoneId: row.perPlanMilestoneId?.perPlanMilestoneId ?? row.perPlanMilestoneId
      },
      tabName: '合同验收' + name,
      fromContractPerformancePlan: app.$route.params?.from === 'contractPerformancePlan'
    },
    title: name ? '合同验收' + name : '新增合同验收',
    name: '合同验收' + name
  })
}

onActivated(() => {
  if (app.$route.params?.from === 'contractPerformancePlan' && app.$route.params?.row) {
    $editTab('edit', app.$route.params.row)
  }
})

const scope = {
  $editTab
}

</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="schema" :scope="scope" schemaKey="PerAcceptance" />
</template>
