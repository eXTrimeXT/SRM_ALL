<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'

// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './deliveryNoticeDetail.vue'

const { app, emitTabAdd } = usePageHelper()

const $detailOne = (flag: string, row: any) => {
  let name = row.deliveryNoticeNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: 'deliveryNoticeDetail' + name
    },
    title: name,
    name: 'deliveryNoticeDetail' + name
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// @ts-ignore
const scope = {
  app,
  $readOne,
  $editOne
}

// @ts-ignore
const schema = defineSchemas({
  DeliveryNoticeVendor: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {            
              data.payload.filter = {
                vendorId: {eq: app.$store.getters.userInfo?.companyId},
                ...data.payload.filter
              }
              data.payload.page = {
                sort: 'lastUpdateDate desc,deliveryNoticeId desc',
                ...data.payload.page
              } 
              return data
            }`)
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'DeliveryNoticeVendor',
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
          orgId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            }
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              'parent-id': expression('$form.values.query.orgId || -1')
            }
          },
          deliveryNoticeNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNoticeNum'), // 送货单通知号
            'x-query-engine-query-operator': 'contains'
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList'
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTICE_STATUS_NEW',
              filterItem: ['DRAFT']
            }
          },
          creationDate: {
            title: i18nExpression('quota.createdDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              pageUrl: '/api-sup-ce/api-ql/DeliveryNoticeVendor/query',
              tableHeader: queryFieldStatePropertyExpression('DeliveryNoticeVendor.table', 'data.columns'),
              dictCodes: {
                status: 'DELIVERY_NOTICE_STATUS_NEW' // 单据状态
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
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          deliveryNoticeId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          deliveryNoticeNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $readOne(row)`)
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNoticeNum'), // 送货通知单号
              minWidth: 120,
              customRender: true
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.businessEntity'), // 业务实体
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendor'), // 供应商
              minWidth: 120
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTICE_STATUS_NEW'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'), // 创建人
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 120
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.updatePeople'), // 更新人
              minWidth: 120
            }
          },
          lastUpdateDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('common.lastUpdateDate'), // 更新日期
              minWidth: 160
            }
          },
          confirmDate: {
            'x-render-table-column': {
              title: i18nExpression('orderMod.confirmDate'), // 供方确认日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment
          },
          refuseReason: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.refuseReason'), // 拒绝原因
              minWidth: 120
            }
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 100,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('perfMod.retroaction'), // 反馈
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `$deps[0] === 'WAITING_VENDOR_CONFIRM'`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $editOne(row)`)
                }
              }
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="VendorDeliveryNoticeList" />
</template>
