<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import { useAttrs } from 'vue-demi'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission
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
import InvDetailDialog from './dialog/invDetailDialog'

const { emitTabAdd, t: $t, app } = usePageHelper()

const attrs:any = useAttrs()

// 结束盘点
const $endInv = async ($queryEngine, $message) => {
  let response = await $queryEngine.request.baseRequest({
    type: 'OsVendorInvBuyer',
    action: 'endInv',
    query: {
      '*': {}
    },
    loading: true,
    payload: [{
      osVendorInvTaskId: attrs.params.row.osVendorInvTaskId
    }]
  })
  if (response) {
    // '本次盘点已结束'
    $message.success(i18nExpression('outsource.inventoryEnded'))
    $queryEngine.state.paginationManagement.refresh()
  }
}

const $sendVendor = async ($form, $queryEngine, $message) => {
  let selecteds = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()
  if (!selecteds.length) {
    // '请勾选列表'
    $message.warning(i18nExpression('outsource.pleaseCheckList'))
    return
  }
  if (selecteds.some(item => !['DRAFT'].includes(item.vendorInvStatus))) {
    // '盘点状态为未发送才可以发送供应商'
    $message.warning(i18nExpression('outsource.inventoryStatusNotSent'))
    return
  }
  let osVendorInvIds = selecteds.map(item => item.osVendorInvId)
  let response = await $queryEngine.request.baseRequest({
    type: 'OsVendorInvBuyer',
    action: 'sendInv',
    query: {
      '*': {}
    },
    loading: true,
    payload: [{
      osVendorInvTaskId: attrs.params.row.osVendorInvTaskId,
      osVendorInvIds
    }]
  })
  if (response) {
    // '发送成功'
    $message.success(i18nExpression('outsource.sentSuccessfully'))
    $queryEngine.state.paginationManagement.refresh()
  }
}

const $toRefuse = (row, $queryEngine, $message) => {
  app.$prompt(app.$t('bidMod.rejectReason1'), app.$t('bidMod.rejectReason1'), {
    confirmButtonText: app.$t('common.confirm'),
    cancelButtonText: app.$t('common.cancel'),
    inputValidator: value => !(!value || value.length === 0 || value.length > 300),
    // '驳回原因必填且长度不能超过300字符'
    inputErrorMessage: i18nExpression('cusEntry.supplement20250211.rejectReasonRequiredAndMaxLength300')
  }).then(({ value }) => {
    $queryEngine.request.baseRequest({
      type: 'OsVendorInvBuyer',
      action: 'rejectInv',
      query: {
        '*': {}
      },
      payload: [{
        osVendorInvTaskId: row.osVendorInvTaskId,
        osVendorInvId: row.osVendorInvId,
        rejectReason: value
      }],
      loading: true
    }).then(() => {
      $message.success(app.$t('bidMod.successRefuse'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const $openDetailDialog = ($form, row, $queryEngine, $message) => {
  $form.query('OsVendorInvBuyer').get('data').visible = true
  $form.query('OsVendorInvBuyer').get('data').row = row
}

const scope = {
  $endInv,
  $sendVendor,
  $attrs: attrs,
  $toRefuse,
  $openDetailDialog
}

const components = {
  InvDetailDialog
}

const schema = defineSchemas({
  OsVendorInvBuyer: {
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
          transformRequest: expression(`(data) => {
            const {osVendorInvTaskId} = $attrs.params.row
            data.payload.filter = {
              ...data.payload.filter,
              osVendorInvTaskId:{
                eq:osVendorInvTaskId
              }
            }
            data.query['*'] = {}
            return data
          }`)
        }
      }
    },
    'x-data': {
      visible: false,
      row: {}
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'OsVendorInvBuyerManage',
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
          osVendorInvTaskId: {
            type: 'string',
            'x-hidden': true,
            default: expression(`$attrs.params.row.osVendorInvTaskId`)
          },
          invTaskTitle: {
            type: 'string',
            title: i18nExpression('outsource.inventoryName1'), // 盘点名称
            default: expression(`$attrs.params.row.invTaskTitle`),
            'x-component-props': {
              disabled: true
            }
          },
          baseMaterialId: {
            type: 'string',
            title: i18nExpression('common.materialCode'), // 物料编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'baseMaterialCode',
              propKey: 'baseMaterialId',
              name: 'scc_price_material'
            }
          },
          vendorId: {
            type: 'string',
            title: i18nExpression('common.vendor'),  // '供应商'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info'
            }
          },
          orgId: {
            type: 'string',
            title: i18nExpression('components.organization.ORG'),  // '业务实体'
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input'
            }
          },
          invResult: {
            type: 'string',
            title: i18nExpression('outsource.inventoryResults'),  // '盘点结果'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          sendVendor: {
            type: 'void',
            title: i18nExpression('outsource.sendSupplier'), // 发送供应商
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('osVendorInvBuyer:send'),
              '@click': expression('() => $sendVendor($form,$queryEngine,$message)')
            }
          },
          endInv: {
            type: 'void',
            title: i18nExpression('outsource.endInventory'), // 结束盘点
            'x-component-props': {
              ...buttonListItemVisibleByPermission('osVendorInvBuyer:endInv'),
              '@click': expression('() => $endInv($queryEngine,$message)')
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
          preColumns: 'seq, checkbox',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          osVendorInvId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          osVendorInvTaskId: {
            type: 'string',
            'x-hidden': true
          },
          vendorInvStatus: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.stratProcess.headers.docStatusValue'), // 状态
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_STATUS'
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.organization.ORG'), // 业务实体
              width: 160
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('components.organization.INV'), // 库存组织
              width: 160
            }
          },
          baseMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.materialCode'), // 委外组件编码
              width: 160
            }
          },
          baseMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.materialName'), // 委外组件名称
              width: 160
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // 供应商编码
              width: 160
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.companyName'), // 供应商名称
              width: 160
            }
          },
          vendorInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.supplierInventory'), // 供方库存
              width: 160
            }
          },
          vendorConfirmInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.supplierConfirmsInventory'), // 供方确认库存
              width: 160
            }
          },
          diff: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.difference'), // 差异
              width: 100
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              $self.value = row?.vendorInvAmount - row?.vendorConfirmInvAmount
            }`),
            'x-query-engine-skip': true
          },
          invResult: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsource.inventoryResults'), // 盘点结果
              width: 160
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            }
          },
          rejectReason: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.rejectReason1'), // 驳回原因
              width: 130
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 130,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              detail: {
                type: 'void',
                title: this.$t('outsource.stockDetails'), // 库存详情
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('osVendorInvBuyer:invDetail'),
                  '@click': expression('({ row }) => $openDetailDialog($form, row, $queryEngine, $message)')
                }
              },

              reject: {
                type: 'void',
                title: i18nExpression('common.toRefuse'), // 驳回
                // 供方已确认
                'x-reactions': changeFieldVisibleByDeps(
                  ['.vendorInvStatus'],
                  '$deps[0] === \'VENDOR_CONFIRM_ED\''
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('osVendorInvBuyer:reject'),
                  '@click': expression('({ row }) => $toRefuse(row, $queryEngine, $message)')
                }
              }
            }
          }
        })
      }
    }
  },
  invDetailDialog: {
    type: 'void',
    'x-component': InvDetailDialog,
    'x-component-props': {
      'visible': expression(`$form.query('OsVendorInvBuyer').get('data').visible`),
      'row': expression(`$form.query('OsVendorInvBuyer').get('data').row`),
      '@close': expression(`() => {
        $form.query('OsVendorInvBuyer').get('data').visible = false
      }`)
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" :components="components" schemaKey="OsVendorInvBuyerManage" />
</template>
