<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression } from '@meicloud/render-engine'
import {dataTimeSelectorSegment, RenderEngine} from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import materialTrialDetail from './edit_engine'
import sampleConfirmedDetail from 'mods@/vendorManagementSupplier/views/vendorSampleConfirmedEngine/edit_engine'

onActivated(() => {
  const params = app.$route.params
  if (params.from === "fromFun" && app.$route?.params.taskIndex === 2) {
    let row = {
      materialTrialId: app.$route?.params?.formId,
      trialNumber: app.$route?.params?.formNo
    }
    $editTab('view', row)
  }
})

const schema = defineSchemas({
  MaterialTrialVendor: {
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
          // immediate: true
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
        'x-component-props': {
          immediateQueryForm: true
        },
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
            'x-query-engine-query-operator': 'contains',
            'x-reactions': {
              effects: ['onFieldInit'],
              fulfill: {
                state: {
                  value: expression(`app.$route?.params.from === 'fromFun' && app.$route?.params.taskIndex === 1 ? app.$route?.params?.formNo : ''`)
                }
              }
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
          }
        })
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1;margin-top:30px;',
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
            title: "试用类型",
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
            type: 'string',
            title: "{{$t('vendorMod.trialStartTime')}}",
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          trialEndDate: {
            type: 'string',
            title: "{{$t('vendorMod.trialEndTime')}}",
            'x-render-table-column': {
              'minWidth': 120
            }
          },
          sourceData: {
            type: 'string',
            title: '来源系统',
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
            type: 'string',
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
            properties: {
              affirm: {
                type: 'void',
                title: "{{$t('common.affirm')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `$deps[0] === 'PUBLISHED'`)
              },
              refused: {
                type: 'void',
                title: "{{$t('common.refused')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $refuse(row,$queryEngine,$message)
                  }`),
                  popconfirm: {
                    title: '确认拒绝该物料试用单吗？'
                  }
                },
                'x-reactions': changeFieldVisibleByDeps(['.approveStatus'], `$deps[0] === 'PUBLISHED'`)
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
 if (type === 'view') {
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

const $refuse = (row:Object, $queryEngine:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'MaterialTrialVendor',
    action: 'update',
    payload: [{
      ...row,
      approveStatus: 'REFUSED'
    }]
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $editTab,
  $delete,
  $refuse,
  app
}

</script>
<template>
  <RenderEngine schemaKey="vendorMaterialTrialList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
