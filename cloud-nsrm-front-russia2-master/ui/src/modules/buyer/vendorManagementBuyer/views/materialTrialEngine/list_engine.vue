<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
    yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import materialTrialDetail from './edit_engine'
import sampleConfirmedDetail from 'modb@/vendorManagementBuyer/views/sampleConfirmedEngine/edit_engine'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReviewEngine/quaOfReviewDetail'

const schema = defineSchemas({
  MaterialTrial: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'el-container',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'MaterialTrial',
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
          categoryName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-component': 'CCategorySelect',
            'x-component-props': {
              'show-key': 'categoryName'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          materialCode: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              'show-key': 'materialCode'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          trialNumber: {
            type: 'string',
            title: "{{$t('vendorMod.mtTrialNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              'show-key': 'companyName',
              'prop-key': 'companyName'
            }
          },
          result: {
            type: 'string',
            title: "{{$t('vendorMod.mtTestResult')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SAMPLE_TEST_RESULT'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          sampleNumber: {
            type: 'string',
            title: "{{$t('vendorMod.sampleNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          'trialStartDate': {
            type: 'string',
            title: "{{$t('vendorMod.trialStartTime')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'date',
              'value-format': 'yyyy-MM-dd'
            },
            'x-query-engine-query-operator': 'ge'
          },
          'trialEndDate': {
            type: 'string',
            title: "{{$t('vendorMod.trialEndTime')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'date',
              'value-format': 'yyyy-MM-dd'
            },
            'x-query-engine-query-operator': 'le'
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SAMPLE_STATUS'
            }
          },
          reviewFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.quaNum')}}",
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => $editTab('add',{})`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex:1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          materialTrialId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          sampleId: {
            type: 'number',
            'x-hidden': true
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-render-table-column': {
              'minWidth': 80
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SAMPLE_STATUS'
            }
          },
          entryType: {
            type: 'string',
            // "试用类型"
            title: i18nExpression('vendorMod.trialType'),
            'x-render-table-column': {
              'minWidth': 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MATERRIAL_ENTRY_TYPE'
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              'minWidth': 150
            }
          },
          trialNumber: {
            type: 'string',
            'x-render-table-column': {
              customRender: true,
              'minWidth': 130,
              title: "{{$t('vendorMod.mtTrialNum')}}"
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('view', row)
              }`)
            }
          },
          reviewFormId: {
            type: 'number',
            'x-hidden': true
          },
          reviewFormNumber: {
            type: 'string',
            'x-render-table-column': {
              customRender: true,
              'minWidth': 130,
              title: "{{$t('vendorMod.quaNum')}}"
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('reviewView', row)
              }`)
            }
          },
          sampleNumber: {
            type: 'string',
            'x-render-table-column': {
              'minWidth': 130,
              title: "{{$t('vendorMod.sampleNum')}}",
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('sampleView', row)
              }`)
            }
          },
          trialStartDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.trialStartDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('vendorMod.trialStartTime')}}",
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          trialEndDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.trialEndDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('vendorMod.trialEndTime')}}",
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          sourceData: {
            type: 'string',
            // '来源系统'
            title: i18nExpression('orderMod.buyerOrderSynergy.sourceSystem'),
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-render-table-column': {
              'minWidth': 100
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
            title: "{{$t('common.creationTime')}}",
            'x-render-table-column': {
              'minWidth': 100
            },
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              'width': 120,
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
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `$deps[0] === 'DRAFT'`)
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $delete(row,$queryEngine)
                  }`),
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  }
                },
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `$deps[0] === 'DRAFT'`)
              },
              evaluate: {
                type: 'void',
                title: "{{$t('vendorMod.evaluateMt')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit', row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `['CONFIRMED','WITHDRAW','REJECTED'].includes($deps[0])`)
              },
              approval: {
                type: 'void',
                title: "{{$t('vendorMod.doApproval')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('doApproval', row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `['SUBMITTED'].includes($deps[0])`)
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $editTab = (type:String, row:Object) => {
let tab = {}
  if (type === 'add') {
    // 新增
    tab = {
      component: materialTrialDetail,
      params: {
        flag: 'add',
        tabName: 'materialTrialDetail'
      },
      title: () => t('vendorMod.addMtTrial'), // '新增物料试用',
      name: 'materialTrialDetail'
    }
  } else if (type === 'view') {
    // 修改
    tab = {
      component: materialTrialDetail,
      params: {
        flag: 'view',
        row: row,
        materialTrialId: row.materialTrialId,
        tabName: 'materialTrialDetail' + row.trialNumber
      },
      title: row.trialNumber,
      name: 'materialTrialDetail' + row.trialNumber
    }
  } else if (type === 'sampleView') {
    // 修改
    let sampleId = row.sampleId
    tab = {
      component: sampleConfirmedDetail,
      params: {
        flag: 'view',
        sampleId: sampleId,
        tabName: 'sampleConfirmedDetail' + row.sampleNumber
      },
      title: row.sampleNumber,
      name: 'sampleConfirmedDetail'
    }
  } else if (type === 'doApproval') {
    tab = {
      component: materialTrialDetail,
      params: {
        flag: type,
        row,
        materialTrialId: row.materialTrialId,
        tabName: 'materialTrialDetail' + row.trialNumber
      },
      title: row.trialNumber,
      name: 'materialTrialDetail' + row.trialNumber
    }
  } else if (type === 'reviewView') {
        tab = {
          component: quaOfReviewDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'quaOfReviewDetail' + row.reviewFormNumber
          },
          title: row.reviewFormNumber,
          name: 'quaOfReviewDetail' + row.reviewFormNumber
        }
  } else {
    // 修改
    tab = {
      component: materialTrialDetail,
      params: {
        flag: 'edit',
        row: row,
        materialTrialId: row.materialTrialId,
        tabName: 'materialTrialDetail' + row.trialNumber
      },
      title: row.trialNumber,
      name: 'materialTrialDetail' + row.trialNumber
    }
  }
  emitTabAdd(tab)
}

const $delete = (row:Object, $queryEngine:any) => {
  $queryEngine.request.delete([row.materialTrialId]).then(() => {
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $editTab,
  $delete
}

</script>
<template>
  <RenderEngine schemaKey="materialTrialList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
