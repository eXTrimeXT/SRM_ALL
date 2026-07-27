<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  dataTimeSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import Edit from "./edit_engine.vue"

const yearMonthDaySelectorSegmentHolder = (holder) => {
  let newObj = JSON.parse(JSON.stringify(yearMonthDaySelectorSegment))
  newObj['x-component-props'].placeholder = holder
  return newObj
}

const schema = defineSchemas({
  InviteVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'InviteVendor',
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
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          contactPerson: {
            type: 'string',
            title: "{{$t('vendorMod.contactPerson')}}",
            'x-query-engine-query-operator': 'contains'
          },
          contactEmail: {
            type: 'string',
            title: "{{$t('vendorMod.contactEmail')}}",
            'x-query-engine-query-operator': 'contains'
          },
          inviteStatus: {
            type: 'string',
            title: "{{$t('common.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVITE_SUPPLIER_STATUS'
            }
          },
          publishDate: {
            type: 'date',
            title: "{{$t('vendorMod.dateList')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          }
          // '[publishStartDate,publishEndDate]': {
          //   type: 'string',
          //   title: "{{$t('vendorMod.dateList')}}",
          //   'x-component': 'DatePicker',
          //   'x-component-props': {
          //     type: 'daterange',
          //     valueFormat: 'yyyy-MM-dd'
          //   }
          // }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': '{{() => $edit({}, "add")}}'
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          tableKey: 'inviteSupplier-tableList',
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          inviteVendorId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          inviteVendorNo: {
            type: 'string',
            title: "{{$t('vendorMod.inviteVendorNo2')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('vendorMod.vendorName')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          contactPerson: {
            type: 'string',
            title: "{{$t('vendorMod.contactPerson')}}",
            'x-render-table-column': {
              minWidth: 100
            }
          },
          socialCreditCode: {
            type: 'string',
            title: "{{$t('vendorMod.socialCreditCode3')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          publishDate: {
            type: 'string',
            title: "{{$t('vendorMod.publishDate')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          inviteStatus: {
            type: 'string',
            title: "{{$t('vendorMod.inviteStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INVITE_SUPPLIER_STATUS'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          contactEmail: {
            type: 'string',
            title: "{{$t('vendorMod.contactEmail')}}",
            "x-render-table-column": {
              minWidth: 100
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 180,
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
                  '@click': expression('({ row }) => $edit(row, "edit")')
                },
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inviteStatus'],
                  `['DRAFT'].includes($deps[0])`
                )
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inviteStatus'],
                  `['DRAFT'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression('({row}) => $delete(row, $queryEngine)')
                }
              },
              view: {
                type: 'void',
                title: "{{$t('common.view')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inviteStatus'],
                  `!['DRAFT'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression('({row}) => $edit(row, "view")')
                }
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t } = usePageHelper()

const $edit = (row: any, flag: string) => {
  let name, title
  if (flag === 'add') {
    name = 'inviteSupplierDeatil'
    title = t('vendorMod.inviteSuppliersToAdd')
  } else if (flag === 'edit') {
    name = 'inviteSupplierDeatil' + row.inviteVendorNo
    title = t('vendorMod.editInviteSuppliers')
  } else { // view
    name = 'inviteSupplierDeatil' + row.inviteVendorNo
    title = t('vendorMod.viewInvitedSuppliers')
  }
  let tab = {
    component: Edit,
    params: {
      row,
      flag,
      tabName: name
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const $delete = (row: any, queryEngine: any) => {
  queryEngine.request['delete']([row.inviteVendorId]).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $edit,
  $delete
}
</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="schema" :scope="scope" schemaKey="inviteSupplier" />
</template>
