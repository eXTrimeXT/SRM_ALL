<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './detail.vue'

const { emitTabAdd, t: $t } = usePageHelper()

const $detailOne = (flag: string, row: any) => {
  let name = row.warehouseReceiptNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'orderStorageDetail' + name : 'orderStorageDetail'
    },
    title: name || $t('orderStorage.addOrderStorage'),
    name: name ? 'orderStorageDetail' + name : 'orderStorageDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add', {})
}

// 批量删除
const $batchDelete = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('WarehouseReceipt.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()
  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }

  $confirm($t('common.confirmDelete'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    const sign = rows.filter((row: any) => row.warehouseReceiptStatus !== 'DRAFT' && row.warehouseReceiptStatus !== 'WAITING_CONFIRM')
    if (sign.length) {
      return $message.warning($t('purchaseDemand.have') + sign.length + $t('orderMod.msgOrder[38]'))
    }
    $deleteOne(rows, $queryEngine, $message)
  })
}

// 删除
const $deleteOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      warehouseReceiptId: item.warehouseReceiptId,
      detailList: [
        {
          $delete: '*'
        }
      ]
    }
  })
  $queryEngine.request.delete(params).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 批量入库
const $batchConfirm = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('WarehouseReceipt.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()
  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }

  const sign = rows.filter((row: any) => row.warehouseReceiptStatus !== 'WAITING_CONFIRM')
  if (sign.length) {
    return $message.warning($t('purchaseDemand.have') + sign.length + $t('orderMod.msgOrder[36]'))
  }
  $confirmOne(rows, $queryEngine, $message)
}

// 入库
const $confirmOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      warehouseReceiptId: item.warehouseReceiptId
    }
  })

  $queryEngine.request.baseRequest({
    'type': 'WarehouseReceipt',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'confirm'
  }).then((res: any) => {
    $message.success($t('orderMod.confirmSuccess'))
    $queryEngine.state.paginationManagement.refresh()
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

const scope = {
  $addOne,
  $batchDelete,
  $deleteOne,
  $readOne,
  $editOne,
  $batchConfirm,
  $confirmOne
}

const schema = defineSchemas({
  WarehouseReceipt: {
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
          immediate: true
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'OrderStorageHead',
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
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.orderDetailId.orderId'
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
            'x-query-engine-relation': 'detailList.orderDetailId'
          },
          warehouseReceiptNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptNumber'), // 入库单号
            'x-query-engine-query-operator': 'contains'
          },
          deliveryNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.deliveryNoteDetailId.deliveryNoteId'
          },
          warehouseReceiptStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WAREHOUSE_RECEIPT_STATUS'
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
            title: i18nExpression('common.add'), // 新增
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
                $batchDelete($self, $queryEngine,$message,$confirm)              
              }`)
            }
          },
          doConfirmStorage: {
            type: 'void',
            title: i18nExpression('orderMod.buyerOrderSynergy.confirmStorage'), // 确认入库
            'x-component': 'RButton',
            'x-component-props': {
              type: 'default',
              '@click': expression(`() => {
                $batchConfirm($self, $queryEngine,$message,$confirm)              
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
          warehouseReceiptId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          warehouseReceiptNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptNumber'), // 入库单号
              minWidth: 150,
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
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
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
          warehouseReceiptStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WAREHOUSE_RECEIPT_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.status'), // 状态
              minWidth: 120
            }
          },
          comments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.remark'), // 备注
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
              minWidth: 160
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
              title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateDate'), // 最后更新日期
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
                  ['.warehouseReceiptStatus'],
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
                  ['.warehouseReceiptStatus'],
                  '[\'DRAFT\',\'WAITING_CONFIRM\'].includes($deps[0])'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression('({ row }) => $deleteOne([row], $queryEngine, $message)')
                }
              },

              doConfirmStorage: {
                type: 'void',
                title: i18nExpression('common.affirm'), // '入库'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.warehouseReceiptStatus'],
                  '$deps[0] === \'WAITING_CONFIRM\''
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $confirmOne([row], $queryEngine, $message)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="WarehouseReceipt" />
</template>
