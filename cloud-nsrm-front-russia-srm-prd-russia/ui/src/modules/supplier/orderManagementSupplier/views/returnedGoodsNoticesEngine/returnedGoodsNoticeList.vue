<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { getValidateFailureSequence } from '@/utils'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './returnedGoodsNoticeDetail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $detailOne = (flag: string, row?: any) => {
  let name = row?.returnOrderNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'returnedGoodsNoticeDetail' + name : 'returnedGoodsNoticeDetail'
    },
    title: name || $t('orderMod.buyerOrderSynergy.addGoodsReturned'),
    name: name ? 'returnedGoodsNoticeDetail' + name : 'returnedGoodsNoticeDetail'
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 批量接受
const $batchAccept = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('ReturnOrderVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }
  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.returnStatus !== 'WAITING_CONFIRM')
  if (sequences) {
    // 只有待确认状态的单据才可以接受，序号x不可接受
    return $message.warning(`${$t('returnedGoodsNotice.prompt4')}${sequences}${$t('returnedGoodsNotice.prompt5')}`)
  }

  // 确定批量接受所选数据
  $confirm($t('returnedGoodsNotice.prompt6') + '?', {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $acceptOne(rows, $queryEngine, $message)
  })
}

// 接受
const $acceptOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => { return { returnOrderId: item.returnOrderId } })
  $queryEngine.request.baseRequest({
    'type': 'ReturnOrderVendor',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'accepet',
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
  const rows = $self.query('ReturnOrderVendor.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
  }

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.returnStatus !== 'WAITING_CONFIRM')
  if (sequences) {
    return $message.warning(`只有待确认状态的单据才可以拒绝，序号${sequences}不可拒绝`)
  }

  $rejectOne(rows, $queryEngine, $message, $prompt)
}

// 拒绝
const $rejectOne = async (rows: any, $queryEngine: any, $message: any, $prompt: any) => {
  const prompt = await $prompt($t('orderMod.msgRufuseReason'), $t('common.tips'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    inputPattern: /\S{1,}/,
    inputErrorMessage: $t('orderMod.refuseReasonRequire')
  }).catch(() => false)

  if (!prompt) return

  let params = rows.map((item: any) => {
    return {
      returnOrderId: item.returnOrderId,
      rejectReason: prompt.value
    }
  })
  $queryEngine.request.baseRequest({
    'type': 'ReturnOrderVendor',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'refuse',
    'query': {
      '*': {}
    }
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  app,
  $readOne,
  $batchAccept,
  $acceptOne,
  $batchRreject,
  $rejectOne
}

const schema = defineSchemas({
  ReturnOrderVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {   
            data.payload.filter = {
              vendorId: {eq: app.$store.getters.userInfo.companyId},
              returnStatus: data.payload.filter?.returnStatus ? data.payload.filter.returnStatus : {in: ['WAITING_CONFIRM','ACCEPT','REJECT']},
              ...data.payload.filter
            }
             
            return data
          }`),
          onSuccess: expression(`(res) => {
            res.data.forEach((item,index) =>{
              item.sequence = index + 1
            })
          }`)
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ReturnOrderVendor',
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
          organizationId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect')
            }
          },
          returnOrderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.returnOrderNumber'), // 退货单号
            'x-query-engine-query-operator': 'contains'
          },
          materialCode: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialCode',
              name: 'scc_base_material_item'
            },
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.deliveryNoteDetailId.orderDetailId'
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.deliveryNoteDetailId.orderDetailId.orderId'
          },
          deliveryNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号

            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.deliveryNoteDetailId.deliveryNoteId'
          },
          returnStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.returnStatus'), // 退货单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RETURN_ORDER_STATUS',
              filterItem: ['DRAFT']
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
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          returnOrderId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          returnOrderNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.returnOrderNumber'), // 退货单号
              minWidth: 120,
              customRender: true
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
              minWidth: 120
            }
          },
          returnStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RETURN_ORDER_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.returnStatus'), // 退货单状态
              minWidth: 120
            }
          },
          comments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.comments1'), // 备注
              minWidth: 120
            }
          },
          rejectReason: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.refusedReason'), // 拒绝原因
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
              title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'), // 最后更新人
              minWidth: 120
            }
          },
          lastUpdateDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateDate'), // 最后更新时间
              minWidth: 120
            },
            'x-query-engine-sort': 'desc'
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
            'x-component': 'RenderTableButtonList',
            properties: {
              accept: {
                type: 'void',
                title: i18nExpression('orderMod.accept'), // 接受
                'x-reactions': changeFieldVisibleByDeps(
                  ['.returnStatus'],
                  '$deps[0] === \'WAITING_CONFIRM\''
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => {  
                        $acceptOne([row], $queryEngine, $message)
                      }`)
                }
              },
              reject: {
                type: 'void',
                title: i18nExpression('common.refused'), // 拒绝
                'x-reactions': changeFieldVisibleByDeps(
                  ['.returnStatus'],
                  '$deps[0] === \'WAITING_CONFIRM\''
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $rejectOne([row], $queryEngine, $message,$prompt)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="VendorReturnOrderList" />
</template>
