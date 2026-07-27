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
import edit from './outsourceMaterialDetail.vue'

const { emitTabAdd, app, http: $http, t: $t } = usePageHelper()

// 获取配置
const $getConfig = async ($self: any) => {
  let res = await $http({
    url: '/api-sup-ce/purchaseConfig/get/outsourcing',
    method: 'GET'
  })
  if (res.code === '0') {
    $self.data.materialChange = res.data.configValue.materialChange
  }
}

const $detailOne = (flag: string, row: any) => {
  let name = row.materialHeadNum ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'outsourceMaterialDetail' + name : 'outsourceMaterialDetail'
    },
    title: name || $t('outsourceMaterialHead.add'), // 委外用料清单新增
    name: name ? 'outsourceMaterialDetail' + name : 'outsourceMaterialDetail'
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}
// 变更
const $change = async (row: any, $queryEngine:any) => {
  $queryEngine.request.baseRequest({
    'type': 'OsMaterial',
    'lang': 'zh-cn',
    'payload': { filter: { materialHeadId: row.materialHeadId } },
    'query': {
      '*': {},
      detailList: { '*': {} }
    },
    'action': 'getDetail'
  }).then((res: any) => {
    app.$router.push({
      name: 'outsourceMaterialChange',
      params: {
        from: 'outsourceMaterialList',
        row: res.data[0]
      }
    })
  })
}

const scope = {
  $readOne,
  $change,
  $getConfig
}

const schema = defineSchemas({
  OsMaterial: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      materialChange: true
    },
    'x-component': 'QueryEngine',
    'x-reactions': expression(`async () => {
      $getConfig($self)
    }`),
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
          eventName: 'OsMaterialBus',
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
            'x-query-engine-query-operator': 'contains'
          },
          status: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_ORDER_STATUS'
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
          vendorCode: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
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
          materialHeadId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
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
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_ORDER_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.deliveryNoteStatus'), // 单据状态
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
              $self.value = (row?.orderNumber|| '') + '|' + (row?.orderDetailRow||'')
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
          lastUpdatedFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.updatePeople'), // 更新人
              minWidth: 120
            }
          },
          lastUpdateDate: {
            ...yearMonthDaySelectorSegment,
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
              width: 80,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              change: {
                type: 'void',
                title: i18nExpression('outsourceMaterialHead.change'), // 变更
                'x-reactions': changeFieldVisibleByDeps(
                  // 已生效
                  ['.status'],
                  '$deps[0] === \'VALID\' && $form.query(\'OsMaterial\').get(\'data\').materialChange'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterialHead:change'),
                  '@click': expression('({ row }) =>$change(row,$queryEngine)')
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
  <RenderEngine :schema="schema" :scope="scope" schemaKey="OutsourceMaterialList" />
</template>
