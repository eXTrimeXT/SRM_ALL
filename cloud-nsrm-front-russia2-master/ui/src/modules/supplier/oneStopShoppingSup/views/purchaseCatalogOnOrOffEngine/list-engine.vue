<script setup lang="ts">
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import purchaseCatalogOnOrOffDetail from './edit-engine'
// @ts-ignore
import { parseTime } from '@/utils'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

const schema = defineSchemas({
  CatalogOnShelvesVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            console.log('transformRequest=>', data, headers)

            data.query = {'*': {}}

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
      // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
      data: {
        type: 'void',
        'x-component': 'Fragment',
        'x-hidden': true,
        'x-data': {
          currentRows: []
        }
      },
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'PurchaseCatalogOnOrOff',
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
          // 业务实体
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-query-engine-query-operator': 'in',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: true,
              collapseTags: true,
              'node-type': 'OU',
              'parent-id': -1,
              '@select': expression(`(node) => {
                if ($values.query.organizationId) {
                  $values.query.organizationId = null
                  $values.query.organizationCode = null
                  $values.query.organizationName = null
                }
              }`)
            }
          },
          // 库存组织
          organizationId: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-query-engine-query-operator': 'in',
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: true,
              collapseTags: true,
              'parent-id': '{{$form.values.query.orgId}}',
              'node-type': 'INV',
              'scope': '{{ $form.values.query }}'
            }
          },
          // 状态
          status: {
            type: 'string',
            title: i18nExpression('common.status'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATALOG_ON_SHELVES_STATUS'
            }
          },
          // 品类名称
          categoryId: {
            type: 'string',
            title: i18nExpression('common.categoryName'),
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryId'
            }
          },
          // 物料名称
          materialId: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialId',
              name: 'scc_base_material_item'
            }
          },
          // 供应商名称
          vendorId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
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
            'x-component': 'AuthorityButton',
            'x-content': i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              code: 'sup:purchaseCatalogOnOrOffSupplier:add',
              '@click': expression(`() => {
                // $form.query('.table').take().componentProps.componentInstance.addRow('unshift')
                
                emitTabAdd({
                  component: purchaseCatalogOnOrOffDetail,
                  params: {
                    flag: 'add',
                    tabName: 'purchaseCatalogOnOrOffDetail'
                  },
                  title: $t('route.purchaseCatalogOnOrOff'),
                  name: 'purchaseCatalogOnOrOffDetail'
                })
              }`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          style: 'flex: 1',
          preColumns: 'checkbox, seq',
          openCustomTable: true,
          editMode: 'multi-row',
          '@checkbox-change': expression('(val) => handleCurrentChange(val, $form)')
        },
        properties: {
          offShelvesId: {
            // 主键ID
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          // 业务实体
          orgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 库存组织
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 物料编码
          materialCode: {
            type: 'string',
            title: i18nExpression('common.materialCode'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 物料名称
          materialName: {
            type: 'string',
            title: i18nExpression('common.materialName'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 品类
          categoryName: {
            type: 'string',
            title: i18nExpression('dataConfMod.category'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 含税单价
          taxPrice: {
            type: 'string',
            title: i18nExpression('purchaseDemand.taxPrice'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorCode'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.vendorName'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 价格库编号
          priceLibraryNo: {
            type: 'string',
            title: i18nExpression('bidMod.priceLibraryNo'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 价格有效期起
          effectiveDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.effectiveDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('dataConfMod.priceEffectiveDate'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 价格有效期至
          expirationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.expirationDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('dataConfMod.priceExpirationDate'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 创建人
          createdUserName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.createdBy1'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 创建日期
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 下架人
          offShelvesBy: {
            type: 'string',
            title: i18nExpression('dataConfMod.remover'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 下架原因
          offShelvesReason: {
            type: 'string',
            title: i18nExpression('dataConfMod.offShelvesReason'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 下架时间
          offShelvesDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.offShelvesDate, '{y}-{m}-{d}')
              }`)
            },
            title: i18nExpression('dataConfMod.removeDate'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 状态
          status: {
            type: 'string',
            title: i18nExpression('common.status'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CATALOG_ON_SHELVES_STATUS',
              fixed: 'right'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-component': 'Space',
            'x-render-table-column': {
              width: 130,
              fixed: 'right'
            },
            properties: {
              // 管理
              manageOne: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('contractMod.manage'),
                'x-component-props': {
                  type: 'text',
                  code: 'sup:purchaseCatalogOnOrOffSupplier:manage',
                  '@click': expression(`() => {
                      console.log($table.getRowByIndex($self.index), 'edit')
                      manageOne($table.getRowByIndex($self.index))
                    }`)
                },
                // 待上架、已上架、已下架、待审核
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `
                  ['TO_BE_SUBMIT', 'REJECTED'].includes($deps[0])
                  `
                )
              },
              // 查看
              viewOne: {
                type: 'void',
                'x-component': 'AuthorityButton',
                'x-content': i18nExpression('common.view'),
                'x-component-props': {
                  type: 'text',
                  code: 'sup:purchaseCatalogOnOrOffSupplier:view',
                  '@click': expression(`() => {
                      console.log($table.getRowByIndex($self.index), 'edit')
                      viewOne($table.getRowByIndex($self.index))
                    }`)
                }
              }
            }
          }
        }
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const { globalNickname } = app.$store.getters.userInfo

// 管理
const manageOne = (row: any) => {
  emitTabAdd({
    component: purchaseCatalogOnOrOffDetail,
    params: {
      flag: 'edit',
      row,
      tabName: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
    },
    title: row.priceLibraryNo,
    name: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
  })
}

// 查看
const viewOne = (row: any, showType = '') => {
  emitTabAdd({
    component: purchaseCatalogOnOrOffDetail,
    params: {
      flag: 'view',
      row,
      tabName: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
    },
    title: row.priceLibraryNo,
    name: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
  })
}

const scope = {
  $t: t,
  app,
  parseTime,
  globalNickname,
  emitTabAdd,
  manageOne,
  viewOne,
  purchaseCatalogOnOrOffDetail
}

const components = {
  purchaseCatalogOnOrOffDetail
}
</script>

<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="CatalogOnShelvesVendor" />
</template>
