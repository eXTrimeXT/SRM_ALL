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

const { emitTabAdd, t: $t, app } = usePageHelper()

watch(() => app.$route.path, (newPath, oldPath) => {
  const { from, funName, formId: orderChangeId, formNo: orderNumber } = app.$route.params

  if (from === 'fromFun' && funName === 'purchaseOrderChange') {
    $readOne({
      ...app.$route.params,
      orderChangeId,
      orderNumber// tab 标题显示
    })
  }
}, { immediate: true })

const $detailOne = (flag: string, row?: any) => {
  let name = row?.orderNumber || ''
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
    name: 'vendorPurchaseOrder',
    params: { from: 'supplierPurchaseOrderChange', row }
  })
}

// 查看--只读状态
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 编辑
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const scope = {
  $t,
  app,
  $readOrderOne,
  $readOne,
  $editOne
}

const schema = defineSchemas({
  OrderChangeVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'vendorQuery',
          transformRequest: expression(`(data, headers) => {
            data.payload.page = {
                sort: 'lastUpdateDate desc',
                ...data.payload.page
            } 

            return data
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
              placeholder: i18nExpression('common.pleaseSelect')
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
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 100,
              fixed: 'right',
              sortable: false
            },
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('perfMod.retroaction'), // 反馈
                'x-reactions': changeFieldVisibleByDeps(
                  ['.orderChangeStatus'],
                  // 待供方确认
                  '$deps[0] === \'WAITING_VENDOR_CONFIRM\''
                ),
                'x-component-props': {
                  '@click': expression('({ row }) => $editOne(row)')
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
  <RenderEngine :scope="scope" :schema="schema" schemaKey="VendorPurchaseOrderChangeList" />
</template>
