<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
// @ts-ignore
import { transformColumns } from '@/utils'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './deliveryNoticeDetail.vue'
// @ts-ignore
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrderNewEngine/purchaseOrderDetail'

const { emitTabAdd, t: $t } = usePageHelper()

// 送货通知单详情
const $readOne = (row: any) => {
  emitTabAdd({
    component: edit,
    params: {
      flag: 'view',
      row: row,
      tabName: 'buyerDeliveryOrderDetail' + row.deliveryNoticeNumber
    },
    title: row.deliveryNoticeNumber,
    name: 'buyerDeliveryOrderDetail' + row.deliveryNoticeNumber
  })
}

// 采购订单详情
const $readOrder = (row: any) => {
  emitTabAdd({
    component: purchaseOrderDetail,
    params: {
      flag: 'view',
      row,
      tabName: 'purchaseOrderDetail' + row.orderNumber
    },
    title: row.orderNumber,
    name: 'purchaseOrderDetail' + row.orderNumber
  })
}

const scope = {
  $readOne,
  $readOrder,
  transformColumns
}

const schema = defineSchemas({
  DeliveryNoticeDetail: {
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
          action: 'list',
          transformRequest: expression(`(data, headers) => { 
              data.payload.page = {
                sort: 'lastUpdateDate desc,deliveryNoticeDetailId asc',
                ...data.payload.page
              } 
              return data
          }`),
          onSuccess: expression(`(res) => {
            res.data.forEach(item => {
              item.deliveryNoticeNumberAndLineNum = item.deliveryNoticeNumber + '|' + item.lineNum 
              item.orderNumberAndOrderDetailLineNum = item.orderNumber + '|' + item.orderDetailLineNum 
            })
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
          eventName: 'DeliveryNoticeDetail',
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
            },
            'x-query-engine-relation': 'deliveryNoticeId',
            'x-query-engine-relation-strict': true
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
            },
            'x-query-engine-relation': 'deliveryNoticeId',
            'x-query-engine-relation-strict': true
          },
          deliveryNoticeNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNoticeNum'), // 送货单通知号
            'x-query-engine-query-operator': 'contains'
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            },
            'x-query-engine-relation': 'deliveryNoticeId',
            'x-query-engine-relation-strict': true
          },
          materialId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialId',
              name: 'scc_base_material_item'
            }
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains'
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.rowStatus'), // 行状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
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
              pageUrl: '/api-sup-ce/api-ql/DeliveryNoticeDetail/list',
              dictCodes: {
                status: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW' // 行状态
              }
            },
            'x-reactions': expression(`(field) => {
                $form.query('DeliveryNoticeDetail.table').take(fields =>{
                    let columns = fields?.data?.columns ?? []
                    field.componentProps.tableHeader = transformColumns(columns,[{
                      targetFiled: 'deliveryNoticeNumberAndLineNum',
                      field: 'deliveryNoticeNumber',
                      title: $t('orderMod.buyerOrderSynergy.deliveryNoticeNum') // 送货通知单号
                    },{
                      targetFiled: 'deliveryNoticeNumberAndLineNum',
                      field: 'lineNum',
                      title: $t('orderMod.deliveryLineNum') // 送货单行号
                  },{
                      targetFiled: 'orderNumberAndOrderDetailLineNum',
                      field: 'orderNumber',
                      title: $t('orderMod.buyerOrderSynergy.orderNumber') // 采购订单编号
                    },{
                      targetFiled: 'orderNumberAndOrderDetailLineNum',
                      field: 'orderDetailLineNum',
                      title: $t('orderMod.buyerOrderSynergy.orderLineNum') // 采购订单行号
                  }])
                })
              }`)
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
          deliveryNoticeDetailId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          deliveryNoticeId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'deliveryNoticeId'
          },
          orderId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'orderId'
          },
          orderDetailId: {
            type: 'string',
            'x-hidden': true
          },
          deliveryNoticeNumber: {
            type: 'string',
            'x-hidden': true
          },
          lineNum: {
            type: 'string',
            'x-hidden': true
          },
          deliveryNoticeNumberAndLineNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: '{{$t(\'orderMod.buyerOrderSynergy.deliveryNoticeNum\') + \'|\' + $t(\'vendorMod.relegation.lineNumber\')}}', // 送货通知单号|行号
              minWidth: 150,
              customRender: true
            },
            'x-query-engine-skip': true
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.businessEntity'), // 业务实体
              minWidth: 120
            },
            'x-query-engine-relation': 'deliveryNoticeId'
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            },
            'x-query-engine-relation': 'deliveryNoticeId'
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendor'), // 供应商
              minWidth: 120
            },
            'x-query-engine-relation': 'deliveryNoticeId'
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTICE_DETAIL_STATUS_NEW'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.rowStatus'), // 行状态
              minWidth: 120
            }
          },
          orderNumber: {
            type: 'string',
            'x-hidden': true
          },
          orderDetailLineNum: {
            type: 'string',
            'x-hidden': true
          },
          orderNumberAndOrderDetailLineNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOrder(row)')
            },
            'x-render-table-column': {
              title: `{{$t('orderMod.buyerOrderSynergy.orderNumber') +
            '|' + $t('vendorMod.relegation.lineNumber')}}`, // 采购订单编号|行号
              minWidth: 150,
              customRender: true
            },
            'x-query-engine-skip': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
              minWidth: 120
            }
          },
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
              minWidth: 120
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.category'), // 品类
              minWidth: 120
            }
          },
          orderNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
              minWidth: 120
            }
          },
          remainingNoticeQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.noticeQuantity'), // 可通知数量
              minWidth: 120,
              titlePrefix: { content: $t('orderMod.noticeQuantityDesc') } // 可通知数量=订单数量-累计通知数量
            }
          },
          noticeSum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.noticeSum'), // 本次通知数量
              minWidth: 120
            }
          },
          deliveryNoticeQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量
              minWidth: 120
            }
          },
          deliveryQuantitySum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.deliveryQuantityHeader'), // 已发货数量
              minWidth: 120,
              titlePrefix: { content: i18nExpression('orderMod.deliveryQuantityHeaderDesc') } // 已发货数量=送货通知单创建送货单已发货数量
            }
          },
          warehouseQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.warehouseQuantity'), // 已入库数量
              minWidth: 120,
              titlePrefix: { content: $t('orderMod.warehouseQuantityDesc') } // 已入库数量=送货通知单创建送货单已入库数量
            }
          },
          returnedQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.returnedQuantity'), // 已退货数量
              minWidth: 120
            },
            'x-query-engine-skip': true
          },
          receiveDate: {
            'x-render-table-column': {
              title: i18nExpression('contractMod.deliveryDate1'), // 到货日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.receiveDate, '{y}-{m}-{d}')
              }`)
            }
          },
          promiseReceiveDate: {
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.promiseReceiveDate, '{y}-{m}-{d}')
              }`)
            }
          },
          receiveContact: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.receiveContacts'), // 收货联系人
              minWidth: 120
            }
          },
          receiveTelephone: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.receiveTelephone'), // 收货联系电话
              minWidth: 120
            }
          },
          receiveAddress: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
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
            'x-render-table-column': {
              title: i18nExpression('common.lastUpdateDate'), // 更新日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)
            }
          },
          confirmDate: {
            'x-render-table-column': {
              title: i18nExpression('orderMod.confirmDate'), // 供方确认日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.confirmDate, '{y}-{m}-{d}')
              }`)
            },
            'x-query-engine-relation': 'deliveryNoticeId'
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="BuyerDeliveryNoticeDetailList" />
</template>
