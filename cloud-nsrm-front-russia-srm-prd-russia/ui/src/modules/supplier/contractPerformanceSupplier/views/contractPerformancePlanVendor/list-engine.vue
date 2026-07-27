<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import ContractPerformancePlanDetailEngine from './edit-engine'

const schema = defineSchemas({
  PerPlan: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true },
        update: true,
        delete: true,
        create: true
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'PerPlan',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          labelCol: 9
        },
        properties: generateXindexInOrder({
          perPlanNo: {
            type: 'string',
            title: "{{ $t('bid_mod.perPlanNo') }}",
            'x-query-engine-query-operator': 'contains'
          },
          buId: {
            type: 'string',
            title: "{{$t('contractMod.buId')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: false
            }
          },
          status: {
            type: 'string',
            title: "{{ $t('vendorMod.relegation.documentStatus') }}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_PLAN_STATUS'
            }
          },
          createdBy: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              'show-key': 'nickname',
              'prop-key': 'username',
              'name': 'scc_rbac_user_display'
            }
          },
          contractNo: {
            type: 'string',
            title: "{{$t('contractMod.contractNo_1')}}",
            'x-query-engine-query-operator': 'contains'
          },
          contractClass: {
            type: 'string',
            title: "{{ $t('contractMod.contractType') }}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            }
          },
          creationDate: {
            title: "{{ $t('vendorMod.creationDate1') }}",
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
          perPlanId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          perPlanNo: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row, rowIndex}) => {
                let type = ['SUBMITTED', 'APPROVED'].includes(row.status) ? 'approval' : 'view'
                $editTab('view', row)
              }`)
            },
            'x-render-table-column': {
              title: "{{ $t('bid_mod.perPlanNo') }}",
              minWidth: 150,
              customRender: true
            }
          },
          processNum: {
            type: 'string',
            title: "{{ $t('contract_mod.processNum') }}",
            'x-render-table-column': {
              width: 150
            }
          },
          templateName: {
            type: 'string',
            title: "{{ $t('contract_mod.templateName') }}",
            'x-render-table-column': {
              width: 150
            }
          },
          status: {
            type: 'string',
            title: "{{ $t('vendorMod.relegation.documentStatus') }}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_PLAN_STATUS'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          buName: {
            type: 'string',
            title: "{{$t('bid_mod.businessEntity')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              width: 130
            }
          },
          contractClass: {
            type: 'string',
            title: "{{$t('contractMod.contractType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          contractNo: {
            type: 'string',
            title: "{{ $t('contractMod.contractNo_1') }}",
            'x-render-table-column': {
              width: 150
            }
          },
          includeTaxAmount: {
            type: 'string',
            title: "合同含税金额",
            'x-component-props': {
              style: "float:right"
            },
            'x-render-table-column': {
              width: 130
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{ $t('common.creator') }}",
            'x-render-table-column': {
              width: 120
            }
          },
          creationDate: {
            'x-query-engine-sort': 'desc',
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },
          currentMilestoneType: {
            type: 'string',
            title: "当前里程碑节点",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE'
            },
            'x-render-table-column': {
              width: 130
            }
          },
          currentNodePersonName: {
            type: 'string',
            title: "节点责任人",
            'x-render-table-column': {
              width: 120
            }
          },
          currentPlanEndDate: {
            title: "里程碑计划结束日期",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 130,
              fixed: 'right'
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  management: {
                    type: 'void',
                    title: "{{$t('bidMod.management')}}",
                    'x-component': 'TableButton',
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => $editTab('manage', row)`)
                    },
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.status'],
                      `['IN_PERFORMANCE', 'APPROVED'].includes($deps[0])`
                    )
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

const { emitTabAdd } = usePageHelper()

const $editTab = (type: string, row: any) => {
  let name = row.perPlanNo || ''
  emitTabAdd({
    component: ContractPerformancePlanDetailEngine,
    params: {
      flag: type,
      row,
      tabName: '履约计划' + name
    },
    title: name ? '履约计划' + name : '新增履约计划',
    name: '履约计划' + name
  })
}

const scope = {
  $editTab
}
</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="schema" :scope="scope" schemaKey="contractPerformancePlanVendorList" />
</template>
