<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import edit from './purchaseOrderDetail'
// @ts-ignore
import purchaseApplicationDetail from 'modb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'

const { emitTabAdd, http: $http, t: $t } = usePageHelper()

// 查看
const $readOne = (row: any) => {
  emitTabAdd({
    component: edit,
    params: {
      flag: 'view',
      row,
      tabName: 'purchaseOrderDetail' + row.orderNumber
    },
    title: row.orderNumber,
    name: 'purchaseOrderDetail' + row.orderNumber
  })
}

// 跳转采购申请
const $readPurchaseApplication = async (row: any) => {
  const res = await $http({
    url: '/api-sup-ce/pr/requirementHead/getByHeadNum',
    method: 'GET',
    params: { requirementHeadNum: row.ceeaRequirementHeadNum },
    loading: true
  })
  emitTabAdd({
    component: purchaseApplicationDetail,
    params: {
      flag: 'readOnly',
      ctrlHeight: true,
      row: {
        requirementHeadId: res.data.requirementHeadId
      },
      showType: 'readOnly',
      tabName: 'purchaseApplicationDetail' + row.ceeaRequirementHeadNum
    },
    title: row.ceeaRequirementHeadNum,
    name: 'purchaseApplicationDetail' + row.ceeaRequirementHeadNum
  })
}

const scope = {
  $t,
  $readOne,
  $readPurchaseApplication
}

const schema = defineSchemas({
  OrderDetail: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'listDetailForBuyer',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.payload.page = {
              sort: 'orderId desc',
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
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true,
            'x-query-engine-query-operator': 'contains'
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_TYPE'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
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
            'x-query-engine-query-operator': 'in',
            'x-query-engine-relation': 'orderId',
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
              multiple: true,
              'parent-id': expression('$form.values.query.ceeaOrgId?.length ? $form.values.query.ceeaOrgId : -1')
            },
            'x-query-engine-query-operator': 'in',
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          ceeaPurchaseOrderDate: {
            title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment,
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          vendorId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          orderStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_ORDER'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          ceeaIfSupplierConfirm: {
            type: 'string',
            title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          budgetManagementId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'budgetManagementNumber',
              propKey: 'budgetManagementId',
              name: 'scc_pb_budget_management_effective'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          sourceSystem: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOURCE_SYSTERM'
            },
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true
          },
          orderDetailStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OrderDetailStatus'
            }
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
              pageUrl: '/api-sup-ce/api-ql/OrderDetail/listDetailForBuyer',
              tableHeader: queryFieldStatePropertyExpression('OrderDetail.table', 'data.columns'),
              'dict-codes': {
                orderType: 'ORDER_TYPE',
                orderStatus: 'PURCHASE_ORDER',
                orderDetailStatus: 'OrderDetailStatus',
                ceeaIfSupplierConfirm: 'YES_OR_NO',
                sourceSystem: 'SOURCE_SYSTERM'
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
        properties: {
          orderDetailId: { // // 主键ID
            type: 'string',
            'x-hidden': true
          },
          orderId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'orderId'
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
            },
            'x-query-engine-relation': 'orderId'
          },
          lineNum: {
            type: 'string',
            title: i18nExpression('purSettlementMod.orderLineNumber'), // 采购订单行号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          budgetManagementNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
          },
          ceeaPurchaseOrderDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
              minWidth: 160
            },
            'x-query-engine-relation': 'orderId'
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_TYPE'
            },
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
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
            },
            'x-query-engine-relation': 'orderId'
          },
          orderDetailStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OrderDetailStatus'
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
            },
            'x-query-engine-relation': 'orderId'
          },
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
          },
          ceeaEmpUsername: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.buyerName'), // 采购员
            'x-render-table-column': {
              minWidth: 120
            },
            'x-query-engine-relation': 'orderId'
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
            },
            'x-query-engine-relation': 'orderId'
          },
          ceeaRequirementHeadNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => $readPurchaseApplication(row)')
            },
            'x-render-table-column': {
              minWidth: 120,
              title: i18nExpression('orderMod.requirementHeadNum'), // 采购申请编号
              customRender: true
            }
          },
          ceeaRowNum: {
            type: 'string',
            title: i18nExpression('bid_mod.purchaseRequestRowNum'), // 采购申请行号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          materialCode: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          requirementQuantity: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.requirementQuantity'), // 需求数量
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orderNum: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNum'), // 订单数量
            'x-render-table-column': {
              minWidth: 120
            }
          },
          confirmNum: {
            type: 'string',
            title: i18nExpression('purchaseOrder.confirmNum'), // 供方确认订单数量
            'x-render-table-column': {
              minWidth: 140
            }
          },
          deliveryNoticeQuantity: {
            type: 'string',
            'x-hidden': true
          },
          notifiedNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.notified'), // 已通知
              minWidth: 120,
              titlePrefix: { message: $t('orderMod.notifiedNum') } // 通过订单创建送货通知单的累计通知数量+通过订单创建送货单的累计送货数量
            }
          },
          notNotifiedNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.notNotified'), // 未通知
              minWidth: 120,
              titlePrefix: { content: $t('purchaseOrder.notNotifiedNumTip') } // 未通知=供方确认订单数量-已通知数量
            }
          },
          inDeliveryNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.onWay'), // 在途
              minWidth: 120,
              titlePrefix: { content: $t('purchaseOrder.inDeliveryNumTip') } // 在途=已确认发货数量-累计入库数量
            }
          },
          unDeliveryNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.unSent'), // 未送
              minWidth: 120,
              titlePrefix: { content: $t('purchaseOrder.unDeliveryNumTip') } // 未送=供方确认订单数量-已确认发货数量
            }
          },
          inStockNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.inStock'), // 已入库
              minWidth: 120,
              titlePrefix: { content: $t('purchaseOrder.inStockNumTip') } // 已入库=累计入库数量-累计已退货数量
            }
          },
          returnNum: {
            type: 'string',
            title: i18nExpression('orderMod.returned'), // 已退货
            'x-render-table-column': {
              minWidth: 120
            }
          },
          storageNum: { // 后端计算用，必传
            type: 'string',
            'x-hidden': true
          },
          requirementDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementDate'), // 需求日期
              minWidth: 160
            }
          },
          ceeaPlanReceiveDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
              minWidth: 160
            }
          },
          ceeaPromiseReceiveDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
              minWidth: 160
            }
          },
          categoryName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
            'x-render-table-column': {
              minWidth: 120
            }
          },
          unit: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
            // 'x-component': 'DictSelect',
            // 'x-component-props': {
            //   code: 'unit'
            // },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaUnitTaxPrice: {
            type: 'string',
            title: i18nExpression('purchaseDemand.taxPrice'), // 含税单价
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaUnitNoTaxPrice: {
            type: 'string',
            title: i18nExpression('contractMod.notaxPrice'), // 不含税单价
            'x-render-table-column': {
              minWidth: 120
            }
          },
          currencyName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.currency'), // 币种
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaTaxRate: {
            type: 'string',
            title: i18nExpression('bidMod.taxRate'), // 税率
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaAmountIncludingTax: {
            type: 'string',
            title: i18nExpression('purSettlementMod.totalAmount'), // 含税金额
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaAmountExcludingTax: {
            type: 'string',
            title: i18nExpression('contractMod.excludeTaxPayAmount'), // 不含税金额
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaTaxAmount: {
            type: 'string',
            title: i18nExpression('contractMod.taxQuota'), // 税额
            'x-render-table-column': {
              minWidth: 120
            }
          },
          refusedReason: {
            type: 'string',
            title: '确认意见', // 确认意见
            'x-render-table-column': {
              minWidth: 120
            }
          }
          // closedCause: {
          //   type: 'string',
          //   title: i18nExpression('orderMod.closeDes'), // 关闭说明
          //   'x-render-table-column': {
          //     minWidth: 120
          //   }
          // }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :schema="schema" schemaKey="BuyerPurchaseOrderNewDetailList" />
</template>
