import {expression,methodExpression , generateXindexInOrder} from "@meicloud/render-engine";
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'


export const siteFormHistoryList = {
  historyTableAll: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      type: 'SiteFormVendor',
      actions: {
        paginationQuery: {
          immediate: true,
          ready: expression(`() => {
            return !!$attrs.params.row.vendorName || $form.query('.vendorName').take().value
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload.filter = {
              "vendorName": {
                eq: $attrs.params.row.vendorName || $form.query('.vendorName').take().value
              }
            }
            return data
          }`)
        }
      }
    },
    properties: {
      historyTable: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          maxHeight: 400,
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: false
        },
        properties: generateXindexInOrder({
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SUPPLIER_APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              minWidth: 90
            }
          },
          siteFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.siteFormNumber')}}", // 现场评审编号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          assessmentType: {
            type: 'string',
            title: "{{$t('vendorMod.assessmentType')}}", // 现场评审类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_ASSESSMENT_TYPE'
            },
            'x-render-table-column': {
              minWidth: 150
            }
          },
          reviewFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.reviewFormNumber2')}}", // 资质审查单号
            'x-render-table-column': {
              minWidth: 150
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('vendorMod.vendorName')}}", // 供应商名称
            'x-render-table-column': {
              minWidth: 100
            }
          },
          siteAdress: {
            type: 'string',
            title: "{{$t('vendorMod.siteAdress')}}", // 评审地址
            'x-render-table-column': {
              minWidth: 140
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('vendorMod.createdFullName')}}", // 创建人
            'x-render-table-column': {
              minWidth: 110
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
            title: "{{$t('vendorMod.creationDate2')}}", // 创建时间
            'x-render-table-column': {
              width: 130
            }
          },
          reviewResult: {
            type: 'string',
            title: "{{$t('vendorMod.reviewResult')}}", // 评审结论
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_RESULT_TYPE'
            },
            'x-render-table-column': {
              minWidth: 150
            }
          }
        })
      }
    }
  }

}
