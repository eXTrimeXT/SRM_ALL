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

const { emitTabAdd, t: $t } = usePageHelper()

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

// 新增
const $addOne = () => {
  $detailOne('add')
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 批量删除
const $batchDelete = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('ReturnOrder.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }
  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.returnStatus !== 'DRAFT')
  if (sequences) {
    return $message.warning(`只有拟定状态的单据才可以删除，序号${sequences}不可删除`)
  }

  $confirm($t('common.confirmDelete'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    $deleteOne(rows, $queryEngine, $message)
  })
}

// 删除
const $deleteOne = (rows: any, $queryEngine: any, $message: any) => {
  let ids = rows.map((item: any) => item.returnOrderId)
  $queryEngine.request.delete(ids).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $addOne,
  $batchDelete,
  $deleteOne,
  $readOne,
  $editOne
}

const schema = defineSchemas({
  ReturnOrder: {
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
          onSuccess: expression(`(res) => {
            res.data.forEach((item,index) =>{
              item.sequence = index + 1
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
          eventName: 'ReturnOrder',
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
          vendorName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
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
              code: 'RETURN_ORDER_STATUS'
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
          add: {
            type: 'void',
            title: i18nExpression('orderMod.buyerOrderSynergy.suppleDelivery'), // 创建退货单
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
            }
          },
          batchDelete: {
            type: 'void',
            title: i18nExpression('orderMod.batchDelete'), // 批量删除
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchDelete($self,$queryEngine,$message,$confirm)              
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
              title: i18nExpression('orderMod.refuseReason'), // 拒绝原因
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
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 160,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.returnStatus'],
                  '$deps[0] === \'DRAFT\''
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },

              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.returnStatus'],
                  '$deps[0] === \'DRAFT\''
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression('({ row }) => $deleteOne([row], $queryEngine, $message)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="BuyerReturnOrderList" />
</template>
