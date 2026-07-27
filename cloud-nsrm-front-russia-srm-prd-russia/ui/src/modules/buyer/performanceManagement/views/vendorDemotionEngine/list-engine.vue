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
import {
  requiredValidatorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app, createdUserIsCurrentUserByRow } = usePageHelper()

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any, activeWorkflowTab?: any) => {
  let name = row.demotionNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      activeWorkflowTab,
      tabName: name ? 'CompanyDemotion' + name : 'CompanyDemotion'
    },
    title: name || $t('vendorMod.relegation.newRelegation'),
    name: name ? 'CompanyDemotion' + name : 'CompanyDemotion'
  })
}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $approvalOne = (row: any) => {
  $detailOne('approvalOnly', row, true)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.companyDemotionId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  CompanyDemotion: {
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
            data.query['*'] = {}
            return data
          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'CompanyDemotionList',
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
          demotionNumber: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.receiptNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          demotionName: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.billName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          categoryId: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.dropCategory')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category2',
              showKey: 'categoryName'
            },
            'x-query-engine-relation': 'companyDemotionCategories',
            'x-query-engine-relation-strict': true
          },
          status: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.documentStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            }
          },
          companyId: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.degradedSupplier')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              showKey: 'companyName'
            }
          },
          createdBy: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'username'
            }
          },
          demotionType: {
            type: 'string',
            title: "{{$t('vendorMod.relegation.demotionType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMOTION_TYPE'
            }
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
          companyDemotionId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          demotionNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.receiptNum')}}",
              minWidth: 120,
              customRender: true
            }
          },
          demotionName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.billName')}}",
              minWidth: 120
            }
          },
          companyName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.relegationVendor')}}",
              minWidth: 140
            }
          },
          performanceNumber: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.associatedNum')}}",
              minWidth: 140
            },
            'x-query-engine-relation': 'companyDemotionId'
          },
          demotionType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMOTION_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.relegationType')}}",
              width: 120
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.state')}}", // 状态
              width: 100
            }
          },
          createdUserName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120,
              formatter: (val:any, row:any) => {
                return val ? `${row.createdFullName}(${val})` : ''
              }
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
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
              width: 170,
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editOne(row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "['DRAFT', 'REJECTED', 'WITHDRAW'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))"
                )
              },
              view: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-component': 'TableButton',
                // 'x-reactions': changeFieldVisibleByDeps(
                //   ['.status'],
                //   '[\'SUBMITTED\'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))'
                // ),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '[\'SUBMITTED\'].includes($deps[0])'
                ),
                'x-component-props': {
                  type: 'text',
                  code: 'sup:vendorDemotion:approveRowData',
                  '@click': expression('({ row }) => $approvalOne(row)')
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  "['DRAFT'].includes($deps[0]) && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))"
                )
              },
              reject: {
                type: 'void',
                title: i18nExpression('common.abandon'), // 废弃
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.enableFlag'],
                  "$deps[0]=== 'N' && $createdUserIsCurrentUserByRow($table.getRowByIndex($self.index))"
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression('({ row }) => $readOne(row)')
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
const components = {}

const scope = {
  $addOne,
  $delete,
  $detailOne,
  $editOne,
  $readOne,
  $approvalOne,
  $createdUserIsCurrentUserByRow: createdUserIsCurrentUserByRow
}
</script>
<template>
  <RenderEngine
    schemaKey="vendorDemotionList"
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
  />
</template>
