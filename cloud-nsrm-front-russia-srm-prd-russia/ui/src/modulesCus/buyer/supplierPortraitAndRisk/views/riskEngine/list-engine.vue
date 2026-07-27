<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  i18nExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, dataTimeSelectorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any) => {
  let name = row.riskCode ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      tabName: name ? 'riskMonitoring' + name : 'riskMonitoring'
    },
    title: $t(`supRisk.${type}`) + name,
    name: name ? 'riskMonitoring' + name : 'riskMonitoring'
  })
}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any, type = 'edit') => {
  $detailOne(type, row)
}

const $passOne = (row: any, type:any, $queryEngine:any) => {
  let form = row
  form.status = type
  $queryEngine.request
    .baseRequest({
      type: 'Monitoring',
      lang: 'zh-cn',
      loading: true,
      payload: [form],
      action: 'save',
      query: {
        '*': {}
      }
    })
    .then((res: any) => {
      app.$message.success($t('common.successSave'))
      $queryEngine.state.paginationManagement.refresh()
    })
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.riskMonitoringId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  Monitoring: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            return data
          }`),
          onSuccess: expression(`(res) => {

          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'RiskMonitoring',
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
          categoryId: {
            type: 'string',
            title: "{{$t('supRisk.categoryId')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category',
              showKey: 'categoryId',
              propKey: 'categoryId'
            }
          },
          vendorId: {
            type: 'string',
            title: "{{$t('supRisk.vendorId')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info',
              showKey: 'companyName',
              propKey: 'companyId'
            }
          },
          status: {
            type: 'string',
            title: "{{$t('supRisk.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_MONITORING_STATUS'
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('supRisk.createdName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'nickname'
            }
          },
          riskType: {
            type: 'string',
            title: "{{$t('supRisk.riskType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_TYPE'
            }
          },
          riskLevel: {
            type: 'string',
            title: "{{$t('supRisk.riskLevel')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_LEVEL'
            }
          },
          creationDate: {
            title: "{{$t('common.creationTime')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
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
            title: "{{$t('common.add')}}",
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('sup:risk:add'),
              '@click': expression(`() => {
                $addOne()
              }`)
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
          riskMonitoringId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          riskCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: "{{$t('supRisk.riskCode')}}",
              minWidth: 120,
              customRender: true
            }
          },
          // categoryName: {
          //   type: 'string',
          //   'x-render-table-column': {
          //     title: "{{$t('supRisk.categoryId')}}",
          //     minWidth: 120
          //   }
          // },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('supRisk.vendorId')}}",
              minWidth: 120
            }
          },
          riskType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('supRisk.riskType')}}",
              minWidth: 100
            }
          },
          riskDescription: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('supRisk.riskDescription')}}",
              minWidth: 120
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_MONITORING_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('supRisk.status')}}",
              minWidth: 100
            }
          },
          riskImplementDesc: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('supRisk.riskImplementDesc')}}",
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120
            }
          },
          creationDate: {
            type: 'string',
            title: "{{ $t('common.creationTime') }}",
            // ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            'x-render-table-column': {
              fixed: 'right',
              width: 170
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "$deps[0] == 'ADD'"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:risk:edit'),
                  '@click': expression(`({row}) => {
                    $editOne(row)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "$deps[0] == 'ADD'"
                ),
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  ...buttonListItemVisibleByPermission('sup:risk:deleteItem'),
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                }
              },
              // riskUpdate: {
              //   type: 'void',
              //   title: "{{$t('supRisk.riskUpdate')}}",
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status'],
              //     "$deps[0] == 'MONITORING'"
              //   ),
              //   'x-component-props': {
              //     ...buttonListItemVisibleByPermission('sup:risk:riskUpdate'),
              //     '@click': expression(`({row}) => {
              //       $editOne(row,'update')
              //     }`)
              //   }
              // },
              addPass: {
                type: 'void',
                title: "{{$t('supRisk.addPass')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "$deps[0] == 'APPROVAL_ADD'"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:risk:addPass'),
                  '@click': expression(`({row}) => {
                    $passOne(row,'MONITORING',$queryEngine)
                  }`)
                }
              },
              closePass: {
                type: 'void',
                title: "{{$t('supRisk.closePass')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "$deps[0] == 'APPROVAL_CLOSE'"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:risk:closePass'),
                  '@click': expression(`({row}) => {
                    $passOne(row,'CLOSED',$queryEngine)
                  }`)
                }
              },
              riskClose: {
                type: 'void',
                title: "{{$t('supRisk.riskClose')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "$deps[0] == 'MONITORING'"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('sup:risk:riskClose'),
                  '@click': expression(`({row}) => {
                    $editOne(row,'riskClose')
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

// @ts-ignore
const components = {
}

const scope = {
  $addOne,
  $editOne,
  $delete,
  $detailOne,
  $readOne,
  $passOne
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="RiskList"
  />
</template>
