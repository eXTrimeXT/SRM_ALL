<script setup lang="ts">
// @ts-ignore
import { onActivated, ref } from 'vue-demi'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import edit from './purchaseOrderDetail'
// @ts-ignore
import { getValidateFailureSequence } from '@/utils'
import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateXindexInOrder,
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'

const { emitTabAdd, t: $t, http: $http, app } = usePageHelper()

const globalNickname = app.$store.getters.userInfo?.username

let integrationMode = ref(null)

onActivated(() => {
  const { from, funName, formId: orderId, formNo: orderNumber, row } = app.$route.params

  if (from === 'fromFun' && funName === 'buyerPurchaseOrder') {
    const row = {
      ...app.$route.params,
      orderId,
      orderNumber// tab 标题显示
    }
    $detailOne('view', row)
  }
  // 来源：采购订单变更
  if (from === 'purchaseOrderChangeList') {
    $detailOne('view', row)
  }
})

const $detailOne = (flag: string, row?: any) => {
  let name = row?.orderNumber || ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'purchaseOrderDetail' + name : 'purchaseOrderDetail'
    },
    title: name || $t('orderMod.buyerOrderSynergy.newOrder'),
    name: name ? 'purchaseOrderDetail' + name : 'purchaseOrderDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add')
}

// 查看--只读状态
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// 批量删除
const $batchDelete = async ($self: any, $queryEngine: any, $message: any, $confirm: any) => {
  const rows = $self.query('Order.table').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()
  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }

  let sequences = getValidateFailureSequence(rows, 'sequence', (row: any) => row.orderStatus !== 'DRAFT')
  if (sequences) {
    // 只有拟定状态的单据才可以删除，序号${sequences}不可删除
    return $message.warning(`${$t('purchaseOrder.prompt3')}${sequences}${$t('purchaseOrder.prompt4')}`)
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
  let params = rows.map((item: any) => {
    return {
      orderId: item.orderId,
      detailList: [
        {
          $delete: '*'
        }
      ],
      attachmentList: [
        {
          $delete: '*'
        }
      ],
      paymentProvisionList: [
        {
          $delete: '*'
        }
      ],
      ladderPriceList: [
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

// 审批--跳审批流
const $approvalOne = (row: any) => {
  $detailOne('approvalOnly', row)
}

// 审批通过
const $confirmOne = (row: any, $queryEngine: any, $confirm: any, $message: any) => {
  $confirm($t('orderMod.supplierConfirm'), // 确认后审批通过！
    $t('common.tips'),
    {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('common.cancel'),
      type: 'warning'
    }).then(() => {
    $queryEngine.request.baseRequest({
      'type': 'Order',
      'lang': 'zh-cn',
      'payload': [{ orderId: row.orderId }],
      'action': 'approve'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

// 订单变更
const $change = async (row: any, $queryEngine:any) => {
  $queryEngine.request.baseRequest({
    'type': 'OrderChange',
    'lang': 'zh-cn',
    'payload': [{ orderId: row.orderId }],
    'action': 'start',
    'query': { '*': {} }
  }).then((res: any) => {
    app.$router.push({
      name: 'purchaseOrderChange',
      params: {
        from: 'buyerPurchaseOrder',
        row: res.data[0]
      }
    })
  })
}

// 废弃
const $abandonOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'Order',
    'lang': 'zh-cn',
    'payload': [{ orderId: row.orderId }],
    'action': 'abandon'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const scope = {
  $t,
  $http,
  integrationMode,
  globalNickname,
  app,
  $addOne,
  $readOne,
  $editOne,
  $deleteOne,
  $batchDelete,
  $approvalOne,
  $confirmOne,
  $abandonOne,
  $change
}

const schema = defineSchemas({
  Order: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'listForBuyer',
          transformRequest: expression(`(data, headers) => {  
            data.payload.page = {
              sort: 'lastUpdateDate desc,orderId desc',
              ...data.payload.page
            }
            return data
          }`),
          preFormat: expression(`async (data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
            integrationMode.value = res.data
            
            if (data.ref?.Order) {
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'ORDER' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.Order).forEach(id => {
                    const item = data.ref.Order[id]
                    const tempId = String(item.orderId)
                    if (maps.includes(tempId)) {
                      item.workflowAuditStatus = 'WAIT'
                      item.arroverId = tempId
                    }
                  })
                }
              }
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
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'Order',
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
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains'
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_TYPE'
            }
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
              multiple: true,
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            },
            'x-query-engine-query-operator': 'in'
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              multiple: true,
              'parent-id': expression('$form.values.query.ceeaOrgId?.length ? $form.values.query.ceeaOrgId : -1')
            },
            'x-query-engine-query-operator': 'in'
          },
          ceeaPurchaseOrderDate: {
            title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          vendorId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          orderStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_ORDER'
            }
          },
          ceeaIfSupplierConfirm: {
            type: 'string',
            title: i18nExpression('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          budgetManagementId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'), // 预算编号
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'budgetManagementNumber',
              propKey: 'budgetManagementId',
              name: 'scc_pb_budget_management_effective'
            }
          },
          sourceSystem: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOURCE_SYSTERM'
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
          // 自定义导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              pageUrl: '/api-sup-ce/api-ql/Order/listForBuyer',
              tableHeader: queryFieldStatePropertyExpression('Order.table', 'data.columns'),
              dictCodes: {
                orderType: 'ORDER_TYPE',
                orderStatus: 'PURCHASE_ORDER',
                storageStatus: 'STORAGE_STATUS',
                ceeaIfSupplierConfirm: 'YES_OR_NO',
                sourceSystem: 'SOURCE_SYSTERM'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        // 'x-query-engine-skip': true,
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'checkbox, seq',
          openCustomTable: true
        },
        properties: {
          orderId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true
          },
          orderNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              minWidth: 150,
              title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
              customRender: true
            }
          },
          budgetManagementNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'), //  // 预算编号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaPurchaseOrderDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.orderDate'), // 订单日期
              minWidth: 160
            }
          },
          orderType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'), // 采购类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            },
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
            }
          },
          storageStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'STORAGE_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaOrgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'), // 业务实体
            'x-render-table-column': {
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ceeaEmpUsername: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.buyerName'), // 采购员
            'x-render-table-column': {
              minWidth: 120
            }
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
            }
          },
          // refuseReason: {
          //   type: 'string',
          //   title: i18nExpression('orderMod.buyerOrderSynergy.refuseReason'), // 拒绝订单原因
          //   'x-render-table-column': {
          //     minWidth: 120
          //   }
          // },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.createdBy1'), // 创建人
            'x-render-table-column': {
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
          sourceSystem: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOURCE_SYSTERM'
            },
            'x-render-table-column': {
              minWidth: 130
            }
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
                  ['.orderStatus'],
                  // 拟定/已驳回/已撤回
                  '[\'DRAFT\', \'REJECT\', \'WITHDRAW\'].includes($deps[0])'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  // 拟定
                  ['.orderStatus'],
                  '$deps[0] === \'DRAFT\''
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression('({ row }) => $deleteOne([row], $queryEngine, $message)')
                }
              },
              approval: {
                type: 'void',
                title: i18nExpression('common.approve'), // 审批
                'x-reactions': changeFieldVisibleByDeps(
                  ['.orderStatus'],
                  // 审批流已开启 && (已提交 || （审批中 && 待办存在当前单据）)
                  'app.flowWithTabMode.includes(integrationMode.value) && ([\'SUBMITTED\'].includes($deps[0]) || ([\'UNDER_APPROVAL\'].includes($deps[0])&& !!$table.getRowByIndex($self.index).arroverId))'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $approvalOne(row)')
                }
              },
              confim: {
                type: 'void',
                title: i18nExpression('bidMod.approvalPass'), // 审批通过
                'x-reactions': changeFieldVisibleByDeps(
                  ['.orderStatus'],
                  // 审批流已关闭 && 已提交 && 当前用户是单据创建人
                  'app.srmFlowMode.includes(integrationMode.value) && [\'SUBMITTED\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $confirmOne(row,$queryEngine,$confirm,$message)')
                }
              },
              change: {
                type: 'void',
                title: i18nExpression('orderMod.changeOrder'), // 订单变更
                'x-reactions': changeFieldVisibleByDeps(
                  // 已生效/部分生效
                  ['.orderStatus'],
                  '[\'APPROVED\',\'PART_ACCEPT\'].includes($deps[0])'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('purchaseOrder.prompt24') // 订单行状态是“接受”才可变更，确认变更该订单？
                  },
                  '@click': expression('({ row }) =>$change(row,$queryEngine)')
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.cancelled'), // 作废
                'x-reactions': changeFieldVisibleByDeps(
                  // 已拒绝/已撤回/已驳回
                  ['.orderStatus'],
                  '[\'REFUSED\',\'WITHDRAW\',\'REJECT\'].includes($deps[0])&& $table.getRowByIndex($self.index).createdBy=== globalNickname'
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('advancePayment.abandonTip') // 确认作废该行数据
                  },
                  '@click': expression('({ row }) => $abandonOne(row, $queryEngine, $message)')
                }
              }
            }
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :schema="schema" schemaKey="BuyerPurchaseOrderNewList" />
</template>
