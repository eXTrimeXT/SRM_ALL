<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
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
import edit from './detail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

onActivated(() => {
  const { from, row } = app.$route.params

  // 委外用料单点击变更按钮跳转至委外用料单变更详情页面
  if (from === 'outsourceMaterialList') {
    $detailOne('add', row)
  }
})

const $detailOne = (flag: string, row: any) => {
  let name = row.changeNum ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'outsourceMaterialListChangeDetail' + name : 'outsourceMaterialListChangeDetail'
    },
    title: name,
    name: name ? 'outsourceMaterialListChangeDetail' + name : 'outsourceMaterialListChangeDetail'
  })
}

// 查看--只读状态
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 删除
const $deleteOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.delete(row.changeId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 审批--跳审批流
const $approvalOne = (row: any) => {
  $detailOne('approvalOnly', row)
}

// 审批通过
const $confirmOne = (row: any, $queryEngine: any, $confirm: any, $message: any) => {
  // $confirm($t('orderMod.supplierConfirm'), // 确认后审批通过！
  //   $t('common.tips'),
  //   {
  //     confirmButtonText: $t('common.confirm'),
  //     cancelButtonText: $t('common.cancel'),
  //     type: 'warning'
  //   }).then(() => {
  //   $queryEngine.request.baseRequest({
  //     'type': 'OsMaterialChange',
  //     'lang': 'zh-cn',
  //     'payload': [{ changeId: row.changeId }],
  //     'actionConfig': { autoFormatResult: false },
  //     'action': 'approve'
  //   }).then((res: any) => {
  //     $message.success($t('common.success'))
  //     $queryEngine.state.paginationManagement.refresh()
  //   })
  // })
}

// 废弃
const $abandonOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OsMaterialChange',
    'lang': 'zh-cn',
    'payload': {
      'data': {
        'changeId': row.changeId,
        'status': 'ABANDONED'
      },
      'filter': {
        'changeId': {
          'eq': row.changeId
        }
      }
    },
    'action': 'updateStatus'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}
const scope = {
  $t,
  app,
  $readOne,
  $editOne,
  $deleteOne,
  $approvalOne,
  $confirmOne,
  $abandonOne
}

const schema = defineSchemas({
  OsMaterialChange: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      integrationMode: '' // 物料弹框查询条件
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            data.payload.page = {
              sort: 'lastUpdateDate desc',
              ...data.payload.page
            }
            return data
          }`),
          preFormat: expression(`async (data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'osMaterialChange' })
            $form.query('OsMaterialChange').get('data').integrationMode = res.data

            if (data.ref?.OsMaterialChange) {
              if (!app.notSearchTodoMode.includes($form.query('OsMaterialChange').get('data').integrationMode)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'osMaterialChange' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.OsMaterialChange).forEach(id => {
                    const item = data.ref.OsMaterialChange[id]
                    const tempId = String(item.changeId)
                    if (maps.includes(tempId)) {
                      item.workflowAuditStatus = 'WAIT'
                      item.arroverId = tempId
                    }
                  })
                }
              }
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
          eventName: 'OsMaterialChangeBus',
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
          changeNum: {
            type: 'string',
            title: i18nExpression('outsourceMaterialHead.changeNum'), // 委外用料单变更单号
            'x-query-engine-query-operator': 'contains'
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_CHANGE_ORDER_STATUS'
            }
          },
          materialHeadNum: {
            type: 'string',
            title: i18nExpression('outsourceMaterialHead.materialHeadNum'), // 委外用料单号
            'x-query-engine-query-operator': 'contains'
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
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            title: i18nExpression('common.updatePeople'), // 更新人
            'x-query-engine-query-operator': 'contains'
          },
          lastUpdateDate: {
            title: i18nExpression('common.lastUpdateDate'), // 更新日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {
          void: {}
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
        properties: generateXindexInOrder({
          changeId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          changeNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.changeNum'), // 委外用料变更单号
              minWidth: 140,
              customRender: true
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_CHANGE_ORDER_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
              minWidth: 120
            }
          },
          materialHeadNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.materialHeadNum'), // 委外用料单号
              minWidth: 120
            }
          },
          orderNumber: { // 采购订单号
            type: 'string',
            'x-hidden': true
          },
          orderDetailRow: { // 采购订单行号
            type: 'string',
            'x-hidden': true
          },
          orderNumberAndOrderDetailRow: {
            type: 'string',
            'x-render-table-column': {
              title: `{{$t('purSettlementMod.orderNumber') +
            '|' + $t('vendorMod.relegation.lineNumber')}}`, // 采购订单号|行号
              minWidth: 150
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              $self.value = row?.orderNumber + '|' + row?.orderDetailRow
            }`),
            'x-query-engine-skip': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
              minWidth: 120
            }
          },
          materialUnit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
              minWidth: 120
            }
          },
          orderDetailQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.orderNum'), // 订单数量
              minWidth: 120
            }
          },
          bomVersion: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.bomVersion'), // BOM版本
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
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // 供应商编码
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          comments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.comments'), // 变更原因
              minWidth: 120
            }
          },
          validDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.validDate, '{y}-{m}-{d}')
                }`)
            },
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.validDate'), // 生效日期
              minWidth: 160
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
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
                }`)
            },
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: i18nExpression('common.lastUpdateDate'), // 更新日期
              minWidth: 160
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
                  // 拟定/已驳回/已撤回
                  '[\'DRAFT\', \'REJECT\', \'WITHDRAW\'].includes($deps[0])'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterialChange:update'),
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  // 拟定
                  ['.status'],
                  '$deps[0] === \'DRAFT\''
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterialChange:delete'),
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression('({ row }) => $deleteOne(row, $queryEngine, $message)')
                }
              },
              approval: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  // 审批流已开启 && (已提交 || （审批中 && 待办存在当前单据）)
                  'app.flowWithTabMode.includes($form.query(\'OsMaterialChange\').get(\'data\').integrationMode) && ([\'SUBMITTED\'].includes($deps[0]) || ([\'UNDER_APPROVAL\'].includes($deps[0])&& !!$table.getRowByIndex($self.index).arroverId))'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterialChange:approval'),
                  '@click': expression('({ row }) => $approvalOne(row)')
                }
              },
              // confim: {
              //   type: 'void',
              //   title: i18nExpression('bidMod.approvalPass'), // 审批通过
              //   'x-reactions': changeFieldVisibleByDeps(
              //     ['.status'],
              //     // 审批流已关闭 && 已提交
              //     'app.srmFlowMode.includes($form.query(\'OsMaterialChange\').get(\'data\').integrationMode) && [\'SUBMITTED\'].includes($deps[0])'
              //   ),
              //   'x-component-props': {
              //     '@click': expression('({ row }) => $confirmOne(row,$queryEngine,$confirm,$message)')
              //   }
              // },
              abandon: {
                type: 'void',
                title: i18nExpression('common.cancelled'), // 作废
                'x-reactions': changeFieldVisibleByDeps(
                  // /已驳回/已撤回
                  ['.status'],
                  '[\'REJECT\', \'WITHDRAW\'].includes($deps[0])'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterialChange:abandon'),
                  popconfirm: {
                    title: i18nExpression('outsourceMaterialHead.prompt1')
                  },
                  '@click': expression('({ row }) => $abandonOne(row, $queryEngine, $message)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="OutsourceMaterialListChangeList" />
</template>
