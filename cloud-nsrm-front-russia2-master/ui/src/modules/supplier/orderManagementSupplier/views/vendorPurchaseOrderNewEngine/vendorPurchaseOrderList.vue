<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import { toJS } from '@meicloud/render-engine'
// @ts-ignore
import edit from './vendorPurchaseOrderDetail'

// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { getValidateFailureSequence, currying, parseTime, transformColumns } from '@/utils'
// @ts-ignore
import ContractInfor from '@/library/composition/orderManagementBuyer/contract-infor'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'

const { emitTabAdd, t: $t, http: $http, app } = usePageHelper()

onActivated(() => {
  const { from, taskIndex, formId: orderId, formNo: orderNumber, row } = app.$route.params
  if (from === 'fromFun' && taskIndex === 2) {
    $readOne({
      ...app.$route.params,
      orderId,
      orderNumber
    })
  }
  // 来源：订单协同-采购订单变更
  if (from === 'supplierPurchaseOrderChange') {
    $readOne(row)
  }
})

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
// 批量接受
const $batchAccept = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('OrderDetailVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }
  const fn = currying(getValidateFailureSequence)(rows, 'sequence')

  let sequences = fn((row: any) => !(row.orderDetailStatus === 'WAITING_VENDOR_CONFIRM' && row.orderStatus === 'APPROVED_INVALID'))
  if (sequences) {
    // 只有待供方确认的单据可以接受，序号${sequences}不可接受
    return $message.warning($t('cusEntry.supplement20250211.message13', {sequences}))
  }

  sequences = fn((row: any) => !row.ceeaPromiseReceiveDate)
  if (sequences) {
    // 序号${sequences}没有填写供方承诺到货日期
    return $message.warning($t('cusEntry.supplement20250211.message14', {sequences}))
  }

  $confirm($t('cusEntry.supplement20250211.message15'), { // 确定批量接受所选数据?
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $acceptOne(rows, $queryEngine, $message)
  })
}

// 接受
const $acceptOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      orderDetailId: item.orderDetailId,
      ceeaPromiseReceiveDate: parseTime(item.ceeaPromiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true)
    }
  })
  $queryEngine.request.baseRequest({
    'type': 'OrderDetailVendor',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'supplierConfirm',
    'query': {
      '*': {}
    }
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 批量拒绝
const $batchRreject = async ($self: any, $queryEngine: any, $message: any, $prompt: any) => {
  const rows = $self.query('OrderDetailVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => !(row.orderDetailStatus === 'WAITING_VENDOR_CONFIRM' && row.orderStatus === 'APPROVED_INVALID'))
  if (sequences) {
    // 只有待供方确认的单据可以拒绝，序号${sequences}不可拒绝
    return $message.warning($t('cusEntry.supplement20250211.message16', {sequences}))
  }

  $rejectOne(rows, $queryEngine, $message, $prompt)
}

// 拒绝
const $rejectOne = async (rows: any, $queryEngine: any, $message: any, $prompt: any) => {
  const prompt = await $prompt($t('orderMod.msgRufuseReason'), $t('common.tips'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    inputPattern: /\S{1,}/,
    inputValidator: (value: any) => {
      if (value.length > 100) {
        return $t('cusEntry.supplement20250211.message17') // 请输入100个字符以内的拒绝原因
      }
    },
    inputErrorMessage: $t('orderMod.refuseReasonRequire')
  }).catch(() => false)

  if (!prompt) return

  let params = rows.map((item: any) => {
    return {
      orderDetailId: item.orderDetailId,
      refusedReason: prompt.value
    }
  })
  $queryEngine.request.baseRequest({
    'type': 'OrderDetailVendor',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'supplierReject',
    'query': {
      '*': {}
    }
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
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
  toJS,
  app,
  $readOne,
  $batchAccept,
  $acceptOne,
  $batchRreject,
  $rejectOne,
  $viewContract,
  transformColumns
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
          transformRequest: expression(`(data, headers) => {
            if(data.query.orderId?.$condition){
              data.query.orderId.$condition.filter = {
                ...data.query.orderId.$condition.filter,
                orderStatus:{in: ['APPROVED','APPROVED_INVALID','REFUSED','ORDER_CHANGING']},
                vendorId: {eq: app.$store.getters.userInfo.companyId}
              }
            }else{
              data.query.orderId.$condition = {
                filter:{
                  orderStatus:{in: ['APPROVED','APPROVED_INVALID','REFUSED','ORDER_CHANGING']},
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
            res.data.forEach((item,index) =>{
              item.ceeaPromiseReceiveDateIsEdit = (item.orderStatus === 'APPROVED_INVALID' && item.orderDetailStatus === 'WAITING_VENDOR_CONFIRM') ? true : false
              item.sequence = index + 1
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
        'x-component-props': {
          immediateQueryForm: true
        },
        properties: generateXindexInOrder({
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-relation': 'orderId',
            'x-query-engine-relation-strict': true,
            'x-query-engine-query-operator': 'contains',
            'x-reactions': {
              effects: ['onFieldInit'],
              fulfill: {
                state: {
                  value: expression('app.$route?.params?.from === \'fromFun\' && app.$route?.params?.taskIndex === 1 ? app.$route?.params?.formNo : \'\'')
                }
              }
            }
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
          $batchAccept: {
            type: 'void',
            title: i18nExpression('orderMod.accept'), // 接受
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $batchAccept($self, $queryEngine,$message,$confirm)              
              }`)
            }
          },
          $batchRreject: {
            type: 'void',
            title: i18nExpression('common.refused'), // 拒绝
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchRreject($self, $queryEngine,$message,$prompt)              
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
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
          preColumns: 'checkbox, seq',
          openCustomTable: true,
          editMode: true
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
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              $self.value = row?.orderNumber + '|' + row?.lineNum
            }`),
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
              titlePrefix: { content: $t('orderMod.notNotifiedCal') } // 未通知=订单数量-已通知
            }
          },
          inDeliveryNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.onWay'), // 在途
              minWidth: 120,
              titlePrefix: { content: $t('orderMod.onWayCal') } // 在途=已确认发货-累计入库数量
            }
          },
          unDeliveryNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.unSent'), // 未送
              minWidth: 120,
              titlePrefix: { content: $t('orderMod.unSentCal') } // 未送=订单数量-已确认发货（已关闭状态为0）
            }
          },
          inStockNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.inStock'), // 已入库
              minWidth: 120,
              titlePrefix: { content: $t('orderMod.inStockCal') } // 已入库=累计入库-已退货
            }
          },
          returnNum: {
            type: 'string',
            title: i18nExpression('orderMod.returned'), // 已退货
            'x-render-table-column': {
              minWidth: 120
            }
          },
          unit: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'unit'
            },
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
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.ceeaPurchaseOrderDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
              minWidth: 160
            },
            'x-query-engine-relation': 'orderId'
          },
          ceeaPlanReceiveDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.ceeaPlanReceiveDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.requirementDate1'), // 要求到货日期
              minWidth: 160
            }
          },
          ceeaPromiseReceiveDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.ceeaPromiseReceiveDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
              minWidth: 230
            },
            'x-read-pretty': expression('!$table.getRowByIndex($self.index)?.ceeaPromiseReceiveDateIsEdit')
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
          contractInfor: {
            type: 'void',
            'x-read-pretty': false,
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
          },
          operation: {
            type: 'void',
            'x-read-pretty': false,
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 130,
              fixed: 'right',
              sortable: false
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  accept: {
                    type: 'void',
                    title: i18nExpression('orderMod.accept'), // 接受
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.orderDetailStatus', '.orderStatus'],
                      '$deps[0] === \'WAITING_VENDOR_CONFIRM\' && $deps[1] === \'APPROVED_INVALID\''
                    ),
                    'x-component-props': {
                      title: i18nExpression('cusEntry.supplement20250211.confirmAcceptOrder'), // 确认接受该订单
                      showPopconfirm: true,
                      '@confirm': expression(`({ row }) => {  
                        if(!row.ceeaPromiseReceiveDate) {
                          // 请填写供方承诺到货日期
                          return $message.warning($t('cusEntry.supplement20250211.message18'))
                        }
                        $acceptOne([row], $queryEngine, $message)
                      }`)
                    }
                  },
                  reject: {
                    type: 'void',
                    title: i18nExpression('common.refused'), // 拒绝
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.orderDetailStatus', '.orderStatus'],
                      '$deps[0] === \'WAITING_VENDOR_CONFIRM\' && $deps[1] === \'APPROVED_INVALID\''
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({ row }) => $rejectOne([row], $queryEngine, $message,$prompt)')
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
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="VendorPurchaseOrderNewList" />
</template>
