<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  i18nExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './vendorDeliveryOrderDetail.vue'
// @ts-ignore
import tagManage from './tagManage.vue'

const { emitTabAdd, t: $t, app, vendor } = usePageHelper()

const $detailOne = (type: string, row: any) => {
  let name = row.deliveryNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag: type,
      row: row,
      tabName: name ? 'buyerDeliveryOrderDetail' + name : 'buyerDeliveryOrderDetail'
    },
    title: $t('orderMod.buyerOrderSynergy.vendorDelivery') + name,
    name: name ? 'buyerDeliveryOrderDetail' + name : 'buyerDeliveryOrderDetail'
  })
}
const $goTagManageConfirm = async (row: any, $queryEngine: any, $confirm: any) => {
  if (row.deliveryNoteStatus === 'CREATE') {
    let res = await tagOuterBoxFetch(row, $queryEngine)
    if (res?.data?.length > 0) {
      $toTagManage(row)
    } else {
      $confirm($t('buyerDeliveryOrder.prompt1'), {
        confirmButtonText: $t('buyerDeliveryOrder.toBind'),
        cancelButtonText: $t('common.cancel'),
        type: 'warning'
      }).then(() => {
        $toTagManage(row)
      }).catch(() => { })
    }
  } else {
    $toTagManage(row)
  }
}

const $toTagManage = (row: any) => {
  // let flag = 'edit'
  // if(['DELIVERED','CANCELLED'].includes(row.deliveryNoteStatus)) flag = 'readOnly'
  let params = {
    deliveryNumber: row.deliveryNumber,
    deliveryNoteId: row.deliveryNoteId
  }
  let name = params.deliveryNumber ?? ''
  emitTabAdd({
    component: tagManage,
    params: {
      status: row.deliveryNoteStatus || 'CREATE',
      row: row || '',
      tabName: name ? 'tagManage' + name : 'tagManage'
    },
    title: $t('orderMod.buyerOrderSynergy.tagManage') + name,
    name: name ? 'tagManage' + name : 'tagManage'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add', {})
}

// 删除
const $deleteOne = (row: any, $queryEngine: any, $message: any) => {
  let params = {
    deliveryNoteId: row.deliveryNoteId,
    detailList: [
      {
        $delete: "*"
      }
    ],
    fileUploads: [
      {
        $delete: "*"
      }
    ]
  }
  $queryEngine.request.delete(params).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

const tagOuterBoxFetch = (row: any, $queryEngine: any) => {
  return $queryEngine.request.baseRequest({
    type: 'TagOuterBox',
    lang: 'zh-cn',
    query: {
      outerBoxId: {}
    },
    payload: {
      filter: {
        deliveryNumber: {
          eq: row.deliveryNumber
        }
      },
      page: {
        pageNum: 1,
        pageSize: 15
      }
    },
    action: 'query',
    loading: true
  })
}

const $editOneConfirm = async (row: any, $confirm: any, $queryEngine: any) => {
  let res = await tagOuterBoxFetch(row, $queryEngine)
  if (res?.data?.length > 0) {
    $confirm(
      $t('buyerDeliveryOrder.prompt2'),
      {
        confirmButtonText: $t('buyerDeliveryOrder.viewTag'),
        cancelButtonText: $t('common.confirm'),
        type: 'warning'
      },
    )
      .then(() => {
        $toTagManage(row)
      })
      .catch(() => { })
  } else {
    $editOne(row)
  }
}
// 编辑
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 取消发货
const $cancelDelivery = (row: any, $queryEngine: any, $message: any,) => {
  $queryEngine.request.baseRequest({
    'type': 'DeliveryNote',
    'lang': 'zh-cn',
    'payload': [
      { 'deliveryNoteId': row.deliveryNoteId }
    ],
    'action': 'cancel'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 确认发货
const $confirmDelivery = (row: any, $queryEngine: any, $message: any, $confirm: any) => {
  $confirm($t('orderMod.isConfirmDelivery'))
    .then(() => {
      $queryEngine.request.baseRequest({
        'type': 'DeliveryNote',
        'lang': 'zh-cn',
        'payload': [
          { 'deliveryNoteId': row.deliveryNoteId }
        ],
        'action': 'confirm'
      }).then((res: any) => {
        $message.success($t('common.success'))
        $queryEngine.state.paginationManagement.refresh()
      })
    })
}

// @ts-ignore
const scope = {
  app,
  $vendor: vendor,
  $addOne,
  $deleteOne,
  $readOne,
  $editOneConfirm,
  $cancelDelivery,
  $goTagManageConfirm,
  $confirmDelivery
}
// @ts-ignore
const schema = defineSchemas({
  DeliveryNote: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            if($vendor()){
              data.payload.filter = {
                vendorId: {eq: app.$store.getters.userInfo.companyId},
                ...data.payload.filter
              }
            }else{
              data.payload.filter = {
                deliveryNoteStatus: data.payload.filter?.deliveryNoteStatus ? data.payload.filter.deliveryNoteStatus : {in: ['DELIVERED','CANCELLED']},
                ...data.payload.filter
              }
            }      
            
            return data
            }`),
          onSuccess: expression(`(res) => {
            console.log(res)
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
          eventName: 'DeliveryNote',
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
          deliveryNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号
            'x-query-engine-query-operator': 'contains'
          },
          receivedFactory: {
            type: 'string',
            title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
            'x-query-engine-query-operator': 'contains'
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList.orderDetailId.orderId'
          },
          deliveryNoteStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.status'), // '状态'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTE_STATUS',
              filterItem: `{{$vendor() ? [] : ['CREATE','SUBMITTED','REJECTED','CLOSE']}}`
            }
          },
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
          deliveryDate: {
            title: i18nExpression('buyerDeliveryOrder.deliveryDate'),
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          vendorCode: {
            type: 'string',
            'x-hidden': '{{$vendor()}}',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          add: {
            type: 'void',
            'x-hidden': '{{!$vendor()}}',
            title: i18nExpression('orderMod.buyerOrderSynergy.createDelivery'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
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
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          deliveryNoteId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          deliveryNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'), // 送货单号
              minWidth: 120,
              customRender: true
            }
          },
          deliveryDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.deliveryDate2'), // 送货日期
              minWidth: 160
            }
          },
          deliveryNoteStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTE_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.billstatus'), // 状态
              minWidth: 120
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
          receivedFactory: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.receiveAddress'), // 收货地址
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          comments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.comments'), // 备注
              minWidth: 120
            }
          },
          creationDate: {
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 160
            },
            ...yearMonthDaySelectorSegment
          },
          lastUpdateDate: {
            "x-hidden": true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 120,
              performanceMode: false,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
                  edit: {
                    type: 'void',
                    title: i18nExpression('common.edit'), // 编辑
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.deliveryNoteStatus'],
                      `!(['DELIVERED','CANCELLED'].includes($deps[0])) && $vendor()`
                    ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $editOneConfirm(row, $confirm,$queryEngine)')
                    }
                  },

                  delete: {
                    type: 'void',
                    title: i18nExpression('common.delete'), // '删除'
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.deliveryNoteStatus'],
                      '$deps[0] === \'CREATE\' && $deps[0] !== \'CANCELLED\' && $vendor()'
                    ),
                    'x-component-props': {
                      popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                      '@click': expression('({ row }) => $deleteOne(row, $queryEngine, $message)')
                    }
                  },

                  cancelDelivery: {
                    type: 'void',
                    title: i18nExpression('orderMod.cancelDelivery'), // 取消发货
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.deliveryNoteStatus'],
                      '$deps[0] === \'DELIVERED\' && $deps[0] !== \'CANCELLED\' && $vendor()'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({ row }) => $cancelDelivery(row, $queryEngine, $message)')
                    }
                  },
                  confirmDelivery: {
                    type: 'void',
                    title: i18nExpression('orderMod.confirmDelivery'), // 确认发货
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.deliveryNoteStatus'],
                      `!(['DELIVERED','CANCELLED'].includes($deps[0])) && $vendor()`
                    ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $confirmDelivery(row, $queryEngine, $message, $confirm)')
                    }
                  },
                  tagManage: {
                    type: 'void',
                    title: "{{$t('orderMod.tagManage')}}", // 条码绑定
                    // 'x-reactions': changeFieldVisibleByDeps(
                    //   ['.deliveryNoteStatus'],
                    //   '$deps[0] !== \'DELIVERED\''
                    // ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $goTagManageConfirm(row, $queryEngine ,$confirm)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="DeliveryNoteList" />
</template>
