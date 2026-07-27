<script setup lang="ts">
// @ts-ignore
import { onActivated, ref } from 'vue-demi'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import edit from './purchaseOrderChangeDetail'
// @ts-ignore
import { getValidateFailureSequence } from '@/utils'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'

const { emitTabAdd, t: $t, http: $http, app } = usePageHelper()

const globalNickname = app.$store.getters.userInfo?.username

let integrationMode = ref(null)

onActivated(() => {
  const { from, funName, formId: orderChangeId, formNo: orderChangeNumber, row } = app.$route.params

  if (from === 'fromFun' && funName === 'purchaseOrderChange') {
    $readOne({
      ...app.$route.params,
      orderChangeId,
      orderChangeNumber// tab 标题显示
    })
  }
  // 采购订单点击订单变更按钮跳转至采购订单变更详情页面
  if (from === 'buyerPurchaseOrder') {
    $detailOne('edit', row)
  }
})

const $detailOne = (flag: string, row?: any) => {
  console.log(row)
  let name = row?.orderChangeNumber || ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: 'purchaseOrderChangeDetail' + name
    },
    title: name,
    name: 'purchaseOrderChangeDetail' + name
  })
}

// 跳转采购订单页面 - 只读
const $readOrderOne = (row: any) => {
  app.$router.push({
    name: 'buyerPurchaseOrder',
    params: { from: 'purchaseOrderChangeList', row }
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
  $queryEngine.request.delete(row.orderChangeId).then(() => {
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
      'type': 'OrderChange',
      'lang': 'zh-cn',
      'payload': [{ orderChangeId: row.orderChangeId }],
      'actionConfig': { autoFormatResult: false },
      'action': 'approve'
    }).then((res: any) => {
      $message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

// 废弃
const $abandonOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OrderChange',
    'lang': 'zh-cn',
    'payload': [{ orderChangeId: row.orderChangeId }],
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
  $readOrderOne,
  $readOne,
  $editOne,
  $deleteOne,
  $approvalOne,
  $confirmOne,
  $abandonOne
}

const schema = defineSchemas({
  OrderChange: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'buyerQuery',
          transformRequest: expression(`(data, headers) => {  
            data.query['*'] = {}
            data.payload.page = {
              sort: 'lastUpdateDate desc',
              ...data.payload.page
            }
            return data
          }`),
          preFormat: expression(`async (data) => {
            const res = await $api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDERCHANGE' })
            integrationMode.value = res.data
            
            if (data.ref?.OrderChange) {
              if (!app.notSearchTodoMode.includes(integrationMode.value)) {
                let queryTodoList = await $api.base.flowAPI.queryTodo({ businessType: 'ORDERCHANGE' })
                if (queryTodoList.data.length) {
                  let maps =  queryTodoList.data.map(item => item.businessId)
                  Object.keys(data.ref.OrderChange).forEach(id => {
                    const item = data.ref.OrderChange[id]
                    const tempId = String(item.orderChangeId)
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
          eventName: 'OrderChangeHead',
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
          orderChangeNumber: {
            type: 'string',
            title: i18nExpression('orderMod.orderChangeNumber'), // 订单变更编号
            'x-query-engine-query-operator': 'contains'
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
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
          orderChangeStatus: {
            type: 'string',
            title: i18nExpression('quest.changeStatus'), // 变更状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_CHANGE_STATUS'
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
          space: {
            type: 'void',
            'x-hidden': true
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-query-engine-skip': true,
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: {
          orderChangeId: { // 单据ID
            type: 'string',
            'x-hidden': true
          },
          orderChangeNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              minWidth: 150,
              title: i18nExpression('orderMod.orderChangeNumber'), // 订单变更编号
              customRender: true
            }
          },
          orderNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => $readOrderOne(row)')
            },
            'x-render-table-column': {
              minWidth: 150,
              title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
              customRender: true
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
            }
          },
          orderChangeStatus: {
            type: 'string',
            title: i18nExpression('orderMod.orderChangeStatus'), // 变更单状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ORDER_CHANGE_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orderChangeVersion: {
            type: 'string',
            title: i18nExpression('orderMod.orderVersion'), // 订单版本号
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orgName: {
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
          empUsername: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.buyerName'), // 采购员
            'x-render-table-column': {
              minWidth: 120
            }
          },
          ifSupplierConfirm: {
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
          sourceSystem: {
            type: 'string',
            title: i18nExpression('orderMod.orderSource'), // 订单来源
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOURCE_SYSTERM'
            },
            'x-render-table-column': {
              minWidth: 120
            }
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
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 120
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            title: i18nExpression('common.updatePeople'), // 更新人
            'x-render-table-column': {
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
            'x-render-table-column': {
              title: i18nExpression('qualitySynergy.updateDate'), // 更新日期
              minWidth: 120
            }
          },
          orderChangeDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.orderChangeDate, '{y}-{m}-{d}')
                }`)
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.effectiveDate'), // 生效日期
              minWidth: 120
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
                  ['.orderChangeStatus'],
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
                  ['.orderChangeStatus'],
                  '$deps[0] === \'DRAFT\' && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
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
                  ['.orderChangeStatus'],
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
                  ['.orderChangeStatus'],
                  // 审批流已关闭 && 已提交 && 当前用户是单据创建人
                  'app.srmFlowMode.includes(integrationMode.value) && [\'SUBMITTED\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy === globalNickname'
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $confirmOne(row,$queryEngine,$confirm,$message)')
                }
              },
              abandon: {
                type: 'void',
                title: i18nExpression('common.cancelled'), // 作废
                'x-reactions': changeFieldVisibleByDeps(
                  // /已驳回/已撤回/已拒绝
                  ['.orderChangeStatus'],
                  '[\'REJECT\', \'WITHDRAW\', \'REFUSED\'].includes($deps[0]) && $table.getRowByIndex($self.index).createdBy=== globalNickname'
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
  <RenderEngine :scope="scope" :schema="schema" schemaKey="BuyerPurchaseOrderChangeList" />
</template>
