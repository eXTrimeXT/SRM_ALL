<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import edit from './vendorPurchaseOrderDetail'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { transformColumns } from '@/utils'
// @ts-ignore
import ContractInfor from '@/library/composition/orderManagementBuyer/contract-infor'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
// @ts-ignore
import { orderConfig } from '@/config/orderConfig'

const { emitTabAdd, t: $t, http: $http, app } = usePageHelper()

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
// 获取合同接口
const $getContractList = async (row: any, params: any) => {
  const { data } = await $http({
    url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
    method: 'POST',
    data: {
      orderDetailId: row.orderDetailId,
      ...params
    },
    loading: true
  })
  return data
}

// 查看合同
const $viewContract = async (row: any, $form: any) => {
  let data = $form.query('OrderDetailVendor').get('data')
  const list = await $getContractList(row, {})
  data.contractView.row = row
  data.contractView.params = list
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

const scope = {
  $t,
  app,
  $readOne,
  $viewContract,
  transformColumns,
  orderConfig
}

const components = {
  ContractInfor
}

const schema = defineSchemas({
  OrderDetailVendor: {
    'x-data': {
      contractView: { // 查看合同
        row: {},
        params: {},
        title: $t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true,
        vendor: true
      }
    },
    type: 'void',
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'listDetailForVendor',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            if(data.query.orderId?.$condition){
              data.query.orderId.$condition.filter = {
                ...data.query.orderId.$condition.filter,
                orderStatus:{in: ['APPROVED_INVALID','APPROVED','REFUSED','ORDER_CHANGING','PART_ACCEPT']},
                vendorId: {eq: app.$store.getters.userInfo.companyId}
              }
            }else{
              data.query.orderId.$condition = {
                filter:{
                  orderStatus:{in: ['APPROVED_INVALID','APPROVED','REFUSED','ORDER_CHANGING','PART_ACCEPT']},
                  vendorId: {eq: app.$store.getters.userInfo.companyId}
                },
                $strictQuery:true
              }
            }
            data.payload.page = {
                sort: 'orderId desc',
                ...data.payload.page
            } 
            return data
          }`),
          onSuccess: expression(`(res) => {
            res.data.forEach(item => {
              item.orderNumberAndLineNum = item.orderNumber + '|' + item.lineNum 
            })
          }`)
        }
      }
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
              code: 'PURCHASE_TYPE'
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
              multiple: true
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
          orderDetailStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OrderDetailStatus',
              filterItem: ['DRAFT', 'CLOSED']
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
              'page-url': '/api-sup-ce/api-ql/OrderDetailVendor/listDetailForVendor',
              'dict-codes': {
                orderType: 'ORDER_TYPE',
                orderStatus: 'PURCHASE_ORDER',
                orderDetailStatus: 'OrderDetailStatus',
                ceeaIfSupplierConfirm: 'YES_OR_NO'
              }
            },
            'x-reactions': expression(`(field) => {
              $form.query('OrderDetailVendor.table').take(fields =>{
                let columns = fields?.data?.columns ?? []
                field.componentProps.tableHeader = transformColumns(columns,[{
                  targetFiled: 'orderNumberAndLineNum',
                  field: 'orderNumber',
                  title: $t('orderMod.buyerOrderSynergy.orderNumber')
                },{
                  targetFiled: 'orderNumberAndLineNum',
                  field: 'lineNum',
                  title: $t('orderMod.buyerOrderSynergy.lineNum')
                }])
              }) 
            }`)
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        // 'x-query-engine-skip': true,
        'x-read-pretty': true,
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
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true
          },
          orderId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'orderId'
          },
          orderNumber: { // 采购订单编号
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'orderId'
          },
          lineNum: { // 采购订单行号
            type: 'string',
            'x-hidden': true
          },
          orderNumberAndLineNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: '{{$t(\'orderMod.buyerOrderSynergy.orderNumber\') + \'|\' + $t(\'orderMod.buyerOrderSynergy.lineNum\')}}', // 采购订单编号|订单行号
              minWidth: 180,
              customRender: true
            },
            'x-query-engine-skip': true,
            'x-read-pretty': false
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
              titlePrefix: { content: $t('orderMod.notifiedNum') } // 通过订单创建送货通知单的累计通知数量+通过订单创建送货单的累计送货数量
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
          vendorName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
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
              minWidth: 230
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
            title: i18nExpression('orderMod.buyerOrderSynergy.taxRate'), // 税率
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
          ceeaOrgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
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
          refusedReason: {
            type: 'string',
            title: i18nExpression('purchaseOrder.refusedReason'), // 确认意见
            'x-render-table-column': {
              minWidth: 120
            }
          },
          contractInfor: {
            type: 'void',
            'x-visible': '{{orderConfig.showContractInfor === \'Y\'}}', // 通过开关控制是否展示
            'x-render-table-column': {
              title: i18nExpression('orderMod.contractInfor'), // 合同信息
              minWidth: 100,
              fixed: 'right',
              sortable: false
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  viewContract: {
                    type: 'void',
                    title: i18nExpression('common.view'), // 查看
                    'x-component': 'TableButton',
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('() => $viewContract($table.getRowByIndex($self.index), $form)')
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  // 查看合同
  contractInforDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'OrderDetailVendor\').get(\'data\').contractView}}',
      '@close': expression(`() => {
        $form.query('.contractInforDialog').take().setComponentProps({
          visible: false
        })
      }`)
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="VendorPurchaseOrderDetailList" />
</template>
