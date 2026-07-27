<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import { useAttrs } from 'vue-demi'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission,
  editTableFormItemValid
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

const { emitTabAdd, t: $t, app, getCurrentUserInfo } = usePageHelper()

const attrs:any = useAttrs()

const $toConfirm = (rows, $form, $queryEngine, $message) => {
  let selecteds = []
  if (rows.length) {
    selecteds = rows
  } else { // 批量提交
    selecteds = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()
    if (!selecteds.length) {
      $message.warning('请勾选列表')
      return
    }
    if (selecteds.some(item => !['VENDOR_UN_CONFIRM', 'BUYER_REJECT'].includes(item.vendorInvStatus))) {
      $message.warning('盘点状态为待供方确认、已驳回才可以提交商')
      return
    }
  }
  let payload = selecteds.map(item => ({
    ...item,
    vendorInvStatus: 'VENDOR_CONFIRM_ED'
  }))
  $queryEngine.request.baseRequest({
    type: 'OsVendorInv',
    action: 'save',
    query: {
      '*': {}
    },
    payload,
    loading: true
  }).then(() => {
    $message.success('提交成功')
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $openDetailDialog = ($form, row, $queryEngine, $message) => {
  $form.query('OsVendorInv').get('data').visible = true
  $form.query('OsVendorInv').get('data').row = row
}

const scope = {
  $attrs: attrs,
  $toConfirm,
  $openDetailDialog,
  userInfo: getCurrentUserInfo()
}

const components = {
  InvDetailDialog
}

const schema = defineSchemas({
  OsVendorInv: {
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
              },
              vendorId:{
                eq:userInfo.companyId
              },
              vendorInvStatus:{
                in:['VENDOR_UN_CONFIRM','VENDOR_CONFIRM_ED','BUYER_REJECT']
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
          eventName: 'OsVendorInv',
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
            title: '盘点名称', // 盘点名称
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
          orgId: {
            type: 'string',
            title: '业务实体',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input'
            }
          },
          invResult: {
            type: 'string',
            title: '盘点结果',
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
          confirm: {
            type: 'void',
            title: i18nExpression('common.submit'), // 提交
            'x-component-props': {
              ...buttonListItemVisibleByPermission('osVendorInv:submit'),
              type: 'primary',
              '@click': expression('() => $toConfirm([],$form,$queryEngine,$message)')
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
          openCustomTable: true,
          editMode: 'multi-row'
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
              title: '状态', // 状态
              minWidth: 120,
              skipEditable: true
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_STATUS'
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: '业务实体', // 业务实体
              width: 160,
              skipEditable: true
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: '库存组织', // 库存组织
              width: 160,
              skipEditable: true
            }
          },
          baseMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: '委外组件编码', // 委外组件编码
              width: 160,
              skipEditable: true
            }
          },
          baseMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: '委外组件名称', // 委外组件名称
              width: 160,
              skipEditable: true
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: '供应商编码', // 供应商编码
              width: 160,
              skipEditable: true
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: '供应商名称', // 供应商名称
              width: 160,
              skipEditable: true
            }
          },
          vendorInvAmount: {
            type: 'string',
            'x-render-table-column': {
              title: '供方库存', // 供方库存
              width: 160,
              skipEditable: true
            }
          },
          vendorConfirmInvAmount: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: '供方确认库存', // 供方确认库存
              width: 160
            },
            'x-component': 'InputNumber',
            'x-component-props': {
              min: 0
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              setTimeout(()=>{
                $self.editable = ['VENDOR_UN_CONFIRM','BUYER_REJECT'].includes(row?.vendorInvStatus) 
                if($self.editable){
                  $self.value = row?.vendorInvAmount
                }
              })
            }`)
          },
          diff: {
            type: 'string',
            'x-render-table-column': {
              title: '差异', // 差异
              width: 100,
              skipEditable: true
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
              title: '盘点结果', // 盘点结果
              width: 160,
              skipEditable: true
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SC_OS_VENDOR_INV_RESULT'
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              let diff = row?.vendorConfirmInvAmount - row?.vendorInvAmount
              let invResult
              if(diff > 0){
                invResult = 'SURPLUS' 
              }else if(diff <0){
                invResult = 'UN_SURPLUS'
              }else{
                invResult = 'NORMAL'
              }
              $self.value = invResult
            }`)
          },
          rejectReason: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.rejectReason1'), // 驳回原因
              width: 130,
              skipEditable: true
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
                title: '库存详情', // 库存详情
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('osVendorInv:invDetail'),
                  '@click': expression('({ row }) => $openDetailDialog($form, row, $queryEngine, $message)')
                }
              },

              confirm: {
                type: 'void',
                title: i18nExpression('common.submit'), // 提交
                // 待供方确认
                'x-reactions': changeFieldVisibleByDeps(
                  ['.vendorInvStatus'],
                  `['VENDOR_UN_CONFIRM','BUYER_REJECT'].includes($deps[0])`
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('osVendorInv:submit'),
                  popconfirm: {
                    title: '是否提交本次盘点?'
                  },
                  '@click': expression('({ row }) => $toConfirm([row], $form, $queryEngine, $message)')
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
      'visible': expression(`$form.query('OsVendorInv').get('data').visible`),
      'row': expression(`$form.query('OsVendorInv').get('data').row`),
      '@close': expression(`() => {
        $form.query('OsVendorInv').get('data').visible = false
      }`)
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" :components="components" schemaKey="OsVendorInvManage" />
</template>
