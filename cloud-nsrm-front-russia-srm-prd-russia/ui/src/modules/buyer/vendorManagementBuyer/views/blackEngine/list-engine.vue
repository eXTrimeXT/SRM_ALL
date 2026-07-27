<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { exportExcelSegment, RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any) => {
  let tabName = type == 'add' ? 'blackEdit' : 'blackEdit' + row.blackId.blackId
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      tabName: tabName
    },
    title: type == 'add' ? '新增单据' : row.companyName,
    name: tabName
  })
}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.blackCompanyId]).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  BlackCompany: {
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
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
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
          eventName: 'BlackList',
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
          blackCode: {
            type: 'string',
            title: "{{$t('black.blacklistApprovalNumber')}}",
            'x-query-engine-query-operator': 'contains'
          },
          companyName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          socialCreditCode: {
            type: 'string',
            title: "{{$t('vendorMod.lcCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          'blackId.approveStatus': {
            type: 'string',
            title: "{{$t('vendorMod.relegation.documentStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-query-engine-relation': true,
            'x-query-engine-relation-strict': true
          },
          creationDate: {
            type: 'string',
            title: "{{$t('common.creationTime')}}",
            'x-query-engine-query-operator': 'between',
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'daterange',
              valueFormat: 'yyyy-MM-dd'
            }
          },
          createdId: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'userId'
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
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup/api-ql/BlackCompany/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('BlackCompany.table', 'data.columns'),
              dictCodes: {
                companyType: 'COMPANY_NATURE',
                blackType: 'BLACK_TYPE',
                approveStatus: 'APPROVE_STATUS_TYPE'
              }
            }
          },
          tips: {
            type: 'void',
            "x-component": 'div',
            "x-component-props": {
              style: {
                display: 'inline-block',
                color: '#D9001B'
              }
            },
            "x-content": '本功能用于供应商黑名单管理，若提交审批后，未审批完成，该供应商状态为“清算中”；审批通过后，该供应商状态为“退出”，并且变成黑名单供应商'
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
          blackCompanyId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          blackId: {
            type: 'number',
            'x-hidden': true
          },
          companyName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.vendorName')}}",
              minWidth: 120
            }
          },
          socialCreditCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.lcCode')}}",
              minWidth: 120
            }
          },
          companyCreationDate: {
            'x-render-table-column': {
              title: "{{$t('bidMod.companyCreationDate')}}",
              minWidth: 120
            },
            ...yearMonthDaySelectorSegment
          },
          registeredCapital: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.registeredCapital')}}",
              minWidth: 120
            }
          },
          companyType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE'
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.companyType')}}",
              minWidth: 100
            }
          },
          legalPerson: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.corporateRepresentative')}}",
              minWidth: 120
            }
          },

          blackCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: "{{$t('black.blacklistApprovalNumber')}}",
              minWidth: 150,
              customRender: true
            }
          },
          blackType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BLACK_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('black.blackType')}}",
              minWidth: 100
            },
            'x-query-engine-relation': 'blackId'
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.relegation.documentStatus')}}",
              minWidth: 100
            },
            'x-query-engine-relation': 'blackId'
          },
          effectiveTime: {
            'x-render-table-column': {
              title: "{{$t('vendorMod.startDate')}}",
              minWidth: 120
            },
            'x-query-engine-relation': 'blackId',
            ...yearMonthDaySelectorSegment
          },
          endDate: {
            'x-render-table-column': {
              title: "{{$t('vendorMod.endDate')}}",
              minWidth: 120
            },
            'x-query-engine-relation': 'blackId',
            ...yearMonthDaySelectorSegment
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120
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
              width: 120
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  "['DRAFT', 'REJECTED', 'WITHDRAW'].includes($deps[0])"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('base:black:edit'),
                  '@click': expression(`({row}) => {
                    $editOne(row)
                  }`)
                }
              },
              manage: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  "['SUBMITTED','REJECTED','WITHDRAW'].includes($deps[0])"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('base:black:edit'),
                  '@click': expression(`({row}) => {
                    $editOne(row)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  "$deps[0] === 'DRAFT'"
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('base:black:delete'),
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
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
  $readOne

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    schemaKey="BlackList"
    :schema="schema"
  />
</template>
