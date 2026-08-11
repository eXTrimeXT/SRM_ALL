<!-- eslint-disable quotes -->
<script setup lang='ts'>
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, queryFieldStatePropertyExpression,
  queryFieldValueExpression } from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { RenderEngine,exportExcelSegment } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import ComplaintinfoEdit from './edit'

const schema = defineSchemas({
  ComplaintInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
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
          eventName: 'ComplaintInfo',
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
          complaintNo: {
            type: 'string',
            title: "{{$t('vendorMod.complaintInfoId')}}",
            'x-query-engine-query-operator': 'contains'
          },
          orgId: {
            type: 'string',
            title: "{{$t('supplierRating.entity')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU'
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('vendorMod.categoryName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          complaintType: {
            type: 'string',
            title: "{{$t('vendorMod.complaintType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_TYPE'
            }
          },
          complaintStatus: {
            type: 'string',
            title: "{{$t('vendorMod.complaintStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_STATUS'
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('vendorMod.creatTime2')}}",
            'x-component': 'DatePicker',
            'x-query-engine-query-operator': 'between',
            'x-component-props': {
              type: 'daterange',
              'value-format': 'yyyy-MM-dd'
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
                $editTab('add',{})
              }`)
            }
          },
          // TODO 导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/ComplaintInfo/query",
              meiqlKey: "ComplaintInfo", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ComplaintInfo.table', 'data.columns'),
              dictCodes: {
                complaintType: 'COMPLAINT_TYPE',
                complaintStatus: 'COMPLAINT_STATUS'
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
          complaintInfoId: {
            type: 'number',
            'x-hidden': false,
            'x-query-engine-primary-key': true
          },
          complaintNo: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.complaintInfoId')}}",
              'min-width': 130,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                viewHandle(row,'see')
              }`)
            }
          },
          complaintTheme: {
            type: 'string',
            title: "{{$t('vendorMod.complaintTheme')}}",
            'x-render-table-column': {
              'min-width': 100
            }
          },
          complaintType: {
            type: 'string',
            title: "{{$t('vendorMod.complaintType')}}",
            'x-render-table-column': {
              'min-width': 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_TYPE'
            }
          },
          complaintStatus: {
            type: 'string',
            title: "{{$t('vendorMod.complaintStatus')}}",
            'x-render-table-column': {
              'min-width': 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPLAINT_STATUS'
            }
          },
          orgName: {
            type: 'string',
            title: "{{$t('vendorMod.ceeaOrgName')}}",
            'x-render-table-column': {
              'min-width': 100
            }
          },
          complaintUserName: {
            type: 'string',
            title: "{{$t('vendorMod.complaintUserName')}}",
            'x-render-table-column': {
              'min-width': 130
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('vendorMod.categoryName')}}",
            'x-render-table-column': {
              'min-width': 120
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
            title: "{{$t('barcodeManageNew.creationDate')}}",
            'x-render-table-column': {
              'min-width': 160
            }
          },
          complaintEndDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.complaintEndDate, '{y}-{m}-{d}')
              }`)
            },
            title: "{{$t('vendorMod.complaintEndDate')}}",
            'x-render-table-column': {
              'min-width': 160
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
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
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.complaintStatus'], `['DRAFT'].includes($deps[0])`)
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $deleteHandle(row,$queryEngine,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.complaintStatus'], `['DRAFT'].includes($deps[0])`)
              },
              view: {
                type: 'void',
                title: "{{$t('common.view')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    viewHandle(row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.complaintStatus'], `['SUBMITTED','REPLY_FEEDBACK','ANSWERED','CLOSED'].includes($deps[0])`)
              },
              approve: {
                type: 'void',
                title: "{{$t('common.toApprove')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    return  $changeStatus(row,'CLOSED',$queryEngine,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.complaintStatus'], `['APPLICATION_CLOSED'].includes($deps[0])`)
              },
              refuse: {
                type: 'void',
                title: "{{$t('common.refused')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    return  $changeStatus(row,'REPLY_FEEDBACK',$queryEngine,$message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.complaintStatus'], `['APPLICATION_CLOSED'].includes($deps[0])`)
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $editTab = (type:string, row:Object) => {
  let name, title
  if (type === 'add') {
    name = 'ComplaintinfoEdit'
    title = t('vendorMod.complaintAdd')
  } else if (type === 'edit') {
    name = 'ComplaintinfoEdit' + row.complaintNo
    title = t('vendorMod.complaintEdit')
  }
  let tab = {
    component: ComplaintinfoEdit,
    params: {
      flag: type,
      row,
      tabName: name
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const viewHandle = (row:any, type:string) => {
  let readOnly = type === 'see'
  let name = 'complaintinfoEdit' + row.complaintNo
  const tab = {
    component: ComplaintinfoEdit,
    params: {
      row,
      flag: 'view',
      readOnly,
      tabName: name
    },
    title: t('vendorMod.complaintView'),
    name
  }
  emitTabAdd(tab)
}

const $deleteHandle = (row:any, $queryEngine:any, $message:any) => {
  $queryEngine.request.baseRequest({
    type: 'ComplaintInfo',
    action: 'delete',
    payload: [{
      '$delete': row.complaintInfoId,
      'fileUploads': [{
        '$delete': '*'
      }],
      'reviews': [{
        '$delete': '*'
      }]
    }]
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $changeStatus = (row:any, status:String, $queryEngine:any, $message:any) => {
  return $queryEngine.request['save']({
    ...row,
    complaintStatus: status
  }).then(() => {
    $message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $editTab,
  $deleteHandle,
  $changeStatus,
  viewHandle
}

</script>
<template>
  <RenderEngine schemaKey="complaintinfoList" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
