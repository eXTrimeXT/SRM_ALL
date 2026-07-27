<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  exportExcelSegment,
  dataTimeSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
// @ts-ignore
import { transformColumns } from '@/utils'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './outsourceMaterialDetail.vue'

const { emitTabAdd, t: $t } = usePageHelper()
const $readOne = (row: any) => {
  emitTabAdd({
    component: edit,
    params: {
      flag: 'view',
      row: row.materialHeadId,
      tabName: 'outsourceMaterialDetail' + row.materialHeadNum
    },
    title: row.materialHeadNum,
    name: 'outsourceMaterialDetail' + row.materialHeadNum
  })
}

const scope = {
  $readOne,
  transformColumns,
  $t
}

const schema = defineSchemas({
  OsMaterialDetail: {
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
          eventName: 'OsMaterialDetailBus',
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
          materialHeadNum: {
            type: 'string',
            title: i18nExpression('outsourceMaterialHead.materialHeadNum'), // 委外用料单号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_ORDER_STATUS'
            },
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
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
            },
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
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
            },
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            },
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
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
            'x-query-engine-relation': 'materialHeadId',
            'x-query-engine-relation-strict': true
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
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              code: 'outsourceMaterialHead:export',
              ...exportExcelSegment,
              pageUrl: '/api-sup-ce/api-ql/OsMaterialDetail/query',
              dictCodes: {
                status: 'OS_MATERIAL_ORDER_STATUS'
              }
            },
            'x-reactions': expression(`(field) => {
                $form.query('OsMaterialDetail.table').take(fields =>{
                    let columns = fields?.data?.columns ?? []
                    field.componentProps.tableHeader = transformColumns(columns,[{
                      targetFiled: 'orderNumberAndOrderDetailRow',
                      field: 'orderNumber',
                      title: $t('purSettlementMod.orderNumber') // 采购订单号
                    },{
                      targetFiled: 'orderNumberAndOrderDetailRow',
                      field: 'orderDetailRow',
                      title: $t('orderMod.buyerOrderSynergy.orderLineNum') // 采购订单行号
                  }])
                })
              }`)
          }
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
          materialLineId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          materialHeadId: {
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          materialHeadNum: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.materialHeadNum'), // 委外用料单号
              minWidth: 120,
              customRender: true
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          rowNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.rowNum'), // 委外用料单行号
              minWidth: 130
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_ORDER_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.status'), // 单据状态
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          orderNumber: { // 采购订单号
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'materialHeadId'
          },
          orderDetailRow: { // 采购订单行号
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'materialHeadId'
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
              $self.value = (row?.orderNumber||'') + '|' + (row?.orderDetailRow||'')
            }`),
            'x-query-engine-skip': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          materialUnit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.unit'), // 单位
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          orderDetailQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.orderNum'), // 订单数量
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          bomVersion: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.bomVersion'), // BOM版本
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.businessEntity'), // 业务实体
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // 供应商编码
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // 供应商名称
              minWidth: 120
            },
            'x-query-engine-relation': 'materialHeadId'
          },
          baseMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.baseMaterialCode'), // 组件物料编码
              minWidth: 120
            }
          },
          baseMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.baseMaterialName'), // 组件物料名称
              minWidth: 120
            }
          },
          baseMaterialUnit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.baseMaterialUnit'), // 组件物料单位
              minWidth: 120
            }
          },
          bomQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.bomQuantity'), // BOM数量
              minWidth: 120,
              titlePrefix: { content: i18nExpression('outsourceMaterialHead.bomQuantityTip') } // BOM数量：生产一个总成物料所需组件物料数量；值来源于BOM清单维护的数量
            }
          },
          orderQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.orderQuantity'), // 组件订单数量
              minWidth: 130,
              titlePrefix: { content: i18nExpression('outsourceMaterialHead.orderQuantityTip') } // 组件订单数量：订单数量*BOM数量
            }
          },
          receivedQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.receivedQuantity'), //  已领数量
              minWidth: 120,
              titlePrefix: { content: i18nExpression('outsourceMaterialHead.receivedQuantityTip') } // 已领数量=此委外用料单行累计供方签收数量-累计退料数量（更新未领数量的退料数量）
            }
          },
          returnQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.returnQuantity'), // 退料数量
              minWidth: 120
            }
          },
          unreceivedQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.unreceivedQuantity'), // 未领数量
              minWidth: 120,
              titlePrefix: { content: i18nExpression('outsourceMaterialHead.unreceivedQuantityTip') } // 未领数量：组件订单数量-已领数量+退料数量（更新未领数量的退料数量）

            }
          },
          detailComments: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourceMaterialHead.detailComments'), // 明细备注
              minWidth: 120
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="OutsourceMaterialDetailList" />
</template>
