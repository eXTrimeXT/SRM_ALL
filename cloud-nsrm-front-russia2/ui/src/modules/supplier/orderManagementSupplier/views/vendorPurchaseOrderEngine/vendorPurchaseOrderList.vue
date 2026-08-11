<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import edit from './vendorPurchaseOrderDetail'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateXindexInOrder
} from '@meicloud/render-engine'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $detailOne = (flag: string, row: any) => {
  let name = row?.orderNumber || ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: 'purchaseOrderDetail' + name
    },
    title: name,
    name: 'purchaseOrderDetail' + name
  })
}
// 反馈
const $handleOne = (row: any) => {
  $detailOne('edit', row)
}
// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

const scope = {
  $t,
  app,
  $handleOne,
  $readOne
}

const schema = defineSchemas({
  OrderVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'list',
          transformRequest: expression(`(data, headers) => {  
            data.payload.filter = {
              vendorId: {eq: app.$store.getters.userInfo?.companyId},
              orderStatus:{in: ['APPROVED_INVALID','APPROVED','REFUSED','ORDER_CHANGING','PART_ACCEPT' ]},
              ...data.payload.filter
            }

            data.payload.page = {
              sort: 'lastUpdateDate desc,orderId desc',
              ...data.payload.page
            }
            return data
          }`)
        }
      }
    },
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'OrderVendor',
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
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains'
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_TYPE'
            }
          },
          ceeaOrgId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: true,
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            },
            'x-query-engine-query-operator': 'in'
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: true,
              'parent-id': expression('$form.values.query.ceeaOrgId?.length ? $form.values.query.ceeaOrgId : -1')
            },
            'x-query-engine-query-operator': 'in'
          },
          ceeaPurchaseOrderDate: {
            title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          orderStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_ORDER',
              filterItem: ['DRAFT', 'UNDER_APPROVAL', 'REJECT', 'WITHDRAW', 'CLOSED', 'ABANDONED', 'SUBMITTED']
            }
          },
          ceeaIfSupplierConfirm: {
            type: 'string',
            title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          void: {}
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        // 'x-query-engine-skip': true,
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: {
          orderId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true
          },
          orderNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              minWidth: 150,
              title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
              customRender: true
            }
          },
          budgetManagementNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaPurchaseOrderDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.ceeaPurchaseOrderDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
              minWidth: 160
            }
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orderStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_ORDER'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          storageStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'STORAGE_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaOrgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
            'x-render-table-column': {
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaEmpUsername: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.buyerName'), // 采购员
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaIfSupplierConfirm: {
            type: 'string',
            title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // refuseReason: {
          //   type: 'string',
          //   title: '确认意见', // 确认意见
          //   'x-render-table-column': {
          //     minWidth: 120
          //   }
          // },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.createdBy1'), // 创建人
            'x-render-table-column': {
              minWidth: 120
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
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 120
            }
          },
          sourceSystem: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOURCE_SYSTERM'
            },
            'x-render-table-column': {
              minWidth: 130
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
              handle: {
                type: 'void',
                title: i18nExpression('perfMod.retroaction'), // 反馈
                'x-reactions': changeFieldVisibleByDeps(
                  ['.orderStatus'],
                  // 待供方确认
                  '$deps[0] === \'APPROVED_INVALID\''
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $handleOne(row)')
                }
              }
            }
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :schema="schema" schemaKey="VendorPurchaseOrderList" />
</template>
