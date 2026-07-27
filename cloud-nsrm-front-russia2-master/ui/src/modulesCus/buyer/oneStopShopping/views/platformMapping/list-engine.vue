<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  queryFieldStatePropertyExpression,
  i18nExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment,
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission,
  dataTimeSelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $synchExternalMaterial = ($queryEngine: any) => {
  // 是否确定同步京东商品？
  app.$confirm($t('cusEntry.supplement20250205.isConfirmSyncJingDongGoods'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('components.common.cancel')
    // type: 'warning'
  }).then(() => {
    $queryEngine.request.baseRequest({
      type: 'ExternalMaterial',
      lang: 'zh-cn',
      loading: true,
      payload: [{}],
      action: 'synchExternalMaterial',
      query: {
        '*': {}
      }
    }).then(() => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const $createMapping = (val: any, row: any, $queryEngine: any) => {
  console.log(val, row, 111)
  if (val) {
    $queryEngine.request.baseRequest({
      type: 'ExternalMaterial',
      lang: 'zh-cn',
      loading: true,
      payload: [{
        externalMaterialId: row.externalMaterialId,
        materialId: val.materialId,
        materialCode: val.materialCode,
        materialName: val.materialName
      }],
      action: 'createMaterialMapping',
      query: {
        '*': {}
      }
    }).then(() => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  // '是否确定删除映射？'
  app.$confirm($t('cusEntry.supplement20250205.isConfirmDeleteMapping'), $t('components.approvalHead.tips.tip'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('components.common.cancel'),
    type: 'warning'
  }).then(() => {
    console.log(1111)
    $queryEngine.request.baseRequest({
      type: 'ExternalMaterial',
      lang: 'zh-cn',
      loading: true,
      payload: [{
        externalMaterialId: row.externalMaterialId
      }],
      action: 'removeMaterialMapping',
      query: {
        '*': {}
      }
    }).then(() => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const schema = defineSchemas({
  ExternalMaterial: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            return data
          }`),
          onSuccess: expression(`(res) => {

          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'platformMapping',
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
          skuId: {
            type: 'string',
            title: $t('cusEntry.supplement20250205.jdProductCode'),  // '京东商品编码'
            'x-query-engine-query-operator': 'contains'
          },
          materialCode: {
            type: 'string',
            title: $t('cusEntry.supplement20250205.greatWallMaterialCode'),  // '长城物料编码'
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          syncJD: {
            type: 'void',
            title: $t('cusEntry.supplement20250205.syncJdGoods'),  // "同步京东商品"
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $synchExternalMaterial($queryEngine)
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment,
              type: 'default',
              // ...buttonListItemVisibleByPermission('priceCatalog:export'),
              pageUrl: '/api-base/api-ql/ExternalMaterial/query',
              tableHeader: queryFieldStatePropertyExpression('ExternalMaterial.table', 'data.columns'),
              dictCodes: {}
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table platform-map-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          externalMaterialId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          skuId: {
            type: 'string',
            'x-render-table-column': {
              title: $t('cusEntry.supplement20250205.jdProductCode'),  // "京东商品编码"
              minWidth: 120
            }
          },
          skuName: {
            type: 'string',
            'x-render-table-column': {
              title: $t('cusEntry.sup.goodsName'),  // "商品名称"
              minWidth: 230
            }
          },
          brand: {
            type: 'string',
            // 'x-component': 'DictSelect',
            // 'x-component-props': {
            //   code: 'RISK_TYPE'
            // },
            'x-render-table-column': {
              title: $t('cusEntry.supplement20250205.brandName'),  // "品牌名称"
              minWidth: 100
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
              title: $t('cusEntry.supplement20250205.productUpdateTime'),  // "商品更新时间"
              minWidth: 120
            }
          },
          materialId: {
            type: 'string',
            'x-hidden': true
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: $t('cusEntry.supplement20250205.greatWallMaterialCode'),  // "长城物料编码"
              minWidth: 120
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: $t('common.materialName'),   // "物料名称"
              width: 120
            }
          },
          materialUpdateDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.materialUpdateDate, '{y}-{m}-{d}')
              }`)
            },
            title:$t('cusEntry.supplement20250205.materialMaintenanceTime'),  //  "物料维护时间"
            'x-render-table-column': { 
              width: 150
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2,
              class: 'operate-btn-list'
            },
            'x-render-table-column': {
              fixed: 'right',
              width: 170
            },
            properties: {
              edit: {
                type: 'void',
                title: "建立映射",
                'x-component': 'QuickSearch',
                'x-component-props': {
                  showKey: 'companyCode',
                  name: 'scc_base_material_item2',
                  showButton: true,
                  btnTitle: $t('cusEntry.supplement20250205.mappingEstablishment'),   // '建立映射',
                  btnType: 'text',
                  '@close-quicksearch': expression('(val) => $createMapping(val,$table.getRowByIndex($self.index), $queryEngine)')
                }
              },
              delete: {
                type: 'void',
                title: $t('cusEntry.supplement20250205.deleteMapping'),  // "删除映射"
                'x-component-props': {
                  // ...buttonListItemVisibleByPermission('sup:risk:deleteItem'),
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
})

// @ts-ignore
const components = {
}

const scope = {
  $createMapping,
  $synchExternalMaterial,
  $delete
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="platformMapping"
  />
</template>

<style lang="scss">
.platform-map-table .vxe-body--column .vxe-cell {
  display: flex !important;
  align-items: center !important;
}
</style>
