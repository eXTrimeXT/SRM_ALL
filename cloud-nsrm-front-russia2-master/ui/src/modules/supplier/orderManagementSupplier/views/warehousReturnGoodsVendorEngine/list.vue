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
  queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import { transformColumns } from '@/utils'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

const { app, vendor } = usePageHelper()

const scope = {
  app,
  transformColumns,
  $vendor: vendor
}

const schema = defineSchemas({
  StorageReturnVendor: {
    type: 'void',
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
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
            }     
            if(data.payload.filter?.dealDate?.between?.length){
              const between = data.payload.filter.dealDate.between
              data.payload.filter['$or'] = {
                receiveDate:{between},
                returnToSupplierDate:{between}
              }
              delete data.payload.filter.dealDate
            }
            data.payload.page['sort'] = 'warehousingReturnDetailId desc'
            return data
            }`),
          onSuccess: expression(`async (res) => {
            const materialCodes = res.data.map(item => item.itemCode) || []
            const resData = await app.$http({
              url: '/api-base/material/materialItem/ext/multilingual',
              method: 'POST',
              data: { materialCodes, language: app.$i18n.locale },
              loading: true
            })
            
            const list = res.data.map(item => {
              const data = resData.data.find(it => it.material === item.itemCode)
              return {
                ...item,
                materialNameShow: data?.materialName,
                handleDate: item.type === 'RECEIVE' ? item.receiveDate : item.returnToSupplierDate
              }
            })
            setTimeout(() => {
              $form.values.table = list
            })
          }`)
        }
      }
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          type: {
            type: 'string',
            title: i18nExpression('orderMod.transactionType'), // 事务处理类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WAREHOURING_RETURN_DETAIL'
            }
          },
          dealDate: {
            title: i18nExpression('warehousingAndReturnGoods.dealDate'), // 事务处理日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          },
          receiveOrderNo: {
            type: 'string',
            title: i18nExpression('orderMod.receiveOrderNo'), // 接收单号
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
              'parent-id': expression('$form.values.query.orgId?.length ? $form.values.query.orgId : -1')
            },
            'x-query-engine-query-operator': 'in'
          },
          categoryName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'categoryName',
              propKey: 'categoryName',
              name: 'scc_base_purchase_category'
            }
          },
          itemName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialName',
              name: 'scc_base_material_item'
            }
          },
          vendorId: {
            type: 'string',
            'x-hidden': '{{$vendor()}}',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          orderNumber: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
            'x-query-engine-query-operator': 'contains'
          },
          requirementHeadNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purRequisitionNum'), // 采购申请单号
            'x-query-engine-query-operator': 'contains'
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
          // 自定义导出
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              pageUrl: '/api-sup-ce/api-ql/StorageReturnVendor/query',
              // tableHeader: queryFieldStatePropertyExpression('StorageReturnVendor.table', 'data.columns'),
              dictCodes: {
                type: 'WAREHOURING_RETURN_DETAIL',
                sourceData: 'TRANSACTION_SOURCE'
              }
            },
            'x-reactions': expression(`(field) => {
              $form.query('StorageReturnVendor.table').take(fields => {
                let columns = fields?.data?.columns ?? []
                field.componentProps.tableHeader = transformColumns(columns,[{
                  targetFiled: 'materialNameShow',
                  field: 'itemName',
                  title: $t('purchaseDemand.itemName')
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
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
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
              title: i18nExpression('purchaseDemand.vendorCode'), // 供应商编码
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.vendorName'), // 供应商名称
              minWidth: 120
            }
          },
          receiveOrderNo: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.orderMod.erpOrderNumber'), // ERP订单号
              minWidth: 120
            }
          },
          type: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'WAREHOURING_RETURN_DETAIL'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.transactionType'), // 事务处理类型
              minWidth: 120
            }
          },
          sourceData: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TRANSACTION_SOURCE'
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.sourceData'), // 事务处理来源
              minWidth: 120
            }
          },
          receiveOrderLineNo: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.supplement20250314.erpOrderLineNumber'), // ERP订单行号
              minWidth: 120
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.materialCateSub'), // 物料小类
              minWidth: 120
            }
          },
          itemCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemCode'), // 物料编码
              minWidth: 120
            }
          },
          itemName: {
            type: 'string',
            'x-hidden': true
          },
          materialNameShow: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.itemName'), // 物料名称
              minWidth: 120
            },
            'x-query-engine-skip': true
          },
          unit: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.unitCode'), // 单位
              minWidth: 120
            }
          },
          receiveNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.transactionsNumber'), // 事务处理数量
              minWidth: 120
            }
          },
          requirementHeadNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.purRequisitionNum'), // 采购申请单号
              minWidth: 120
            }
          },
          rowNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.rowNum'), // 申请行号
              minWidth: 120
            }
          },
          orderNumber: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purSettlementMod.orderNumber'), // 采购订单号
              minWidth: 120
            }
          },
          lineNum: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.orderLineNum'), // 订单行号
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('cusEntry.orderMod.handler'), // 事务处理人
              minWidth: 120
            }
          },
          returnToSupplierDate: {
            'x-hidden': true,
            'x-render-table-column': {
              title: i18nExpression('orderMod.transactionDate'), // 事务处理日期
              minWidth: 120
            }
          },
          receiveDate: {
            'x-hidden': true,
            'x-render-table-column': {
              title: i18nExpression('orderMod.transactionDate'), // 事务处理日期
              minWidth: 120
            }
          },
          handleDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.handleDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.transactionDate'), // 事务处理日期
              minWidth: 120
            },
            'x-query-engine-skip': true
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="StorageReturnVendor" />
</template>
