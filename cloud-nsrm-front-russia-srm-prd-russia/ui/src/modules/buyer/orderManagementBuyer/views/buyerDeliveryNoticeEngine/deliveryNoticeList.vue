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
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './deliveryNoticeDetail.vue'

const { emitTabAdd, t: $t } = usePageHelper()

const $detailOne = (flag: string, row: any) => {
  let name = row.deliveryNoticeNumber ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'deliveryNoticeDetail' + name : 'deliveryNoticeDetail'
    },
    title: name || $t('orderMod.createDeliveryBill'),
    name: name ? 'deliveryNoticeDetail' + name : 'deliveryNoticeDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add', {})
}

// 批量删除
const $batchDelete = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('DeliveryNotice.table').take()
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
    const sign = rows.some((row: any) => row.status !== 'DRAFT')
    if (sign) {
      return $message.warning($t('orderMod.selectDraftDataDelete'))
    }
    $deleteOne(rows, $queryEngine, $message)
  })
}

// 删除
const $deleteOne = (rows: any, $queryEngine: any, $message: any) => {
  let params = rows.map((item: any) => {
    return {
      deliveryNoticeId: item.deliveryNoticeId,
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
  $editOne
}

const schema = defineSchemas({
  DeliveryNotice: {
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
          eventName: 'DeliveryNotice',
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
            'x-query-engine-relation': 'detailList',
            'x-query-engine-relation-strict': true
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_NOTICE_STATUS_NEW'
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
          add: {
            type: 'void',
            title: i18nExpression('orderMod.createDeliveryBill'), // 创建通知单
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
            }
          },
          // 自定义导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: '/api-sup-ce/api-ql/DeliveryNotice/query',
              tableHeader: queryFieldStatePropertyExpression('DeliveryNotice.table', 'data.columns'),
              dictCodes: {
                status: 'DELIVERY_NOTICE_STATUS_NEW' // 单据状态
              }
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
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'checkbox, seq',
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
              '@click': expression('({ row }) => $readOne(row)')
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
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.confirmDate'), // 供方确认日期
              minWidth: 160
            }
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
              width: 120,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // 编辑

                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
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
                  ['.status'],
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="BuyerDeliveryNoticeList" />
</template>
