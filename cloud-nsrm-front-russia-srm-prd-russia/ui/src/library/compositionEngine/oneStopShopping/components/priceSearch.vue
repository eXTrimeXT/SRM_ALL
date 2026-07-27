<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  i18nExpression,
  expression,
  defineSchemas,
  generateXindexInOrder,
  generateCharFunctionExpression
} from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { useAttrs, defineProps, defineEmits } from 'vue-demi'

const { emitTabRemove, t: $t, http, app, vendor } = usePageHelper()

const emits = defineEmits(['close'])

const $attrs: any = useAttrs()

const $defineProps = defineProps({
  id: {
    type: Array,
    default: () => []
  },
  visible: {
    type: Boolean,
    default: false
  },
  url: {
    type: String,
    default: '/api-sup-ce/pr/catalogOnShelves/getPriceLibraryForCatalogOnShelves'
  }
})

console.log($defineProps, 'defineProps')

const $searchParentOrg = async ($form: any, currentPage?: number, pageSize?: any) => {
  const obj = $form.query('PriceSearchDialog').get('data')
  let { data } = await http({
    url: $defineProps.url,
    method: 'POST',
    data: {
      ...$form.values.searchInner,
      pageNum: currentPage || obj.pageInfo.current,
      pageSize: pageSize || obj.pageInfo.pageSize
    },
    loading: true
  })

  $form.values.tableList = data.list

  Object.assign(obj.pageInfo, {
    current: data.pageNum,
    pageSize: data.pageSize,
    total: data.total
  })
}

// @ts-ignore
const scope = {
  emits,
  $attrs,
  $t,
  $defineProps,
  $searchParentOrg
}

// @ts-ignore
const components = {

}

// @ts-ignore
const schema = defineSchemas({
  PriceSearchDialog: {
    type: 'void',
    title: '价格库编号',
    'x-component': 'RDialog',
    'x-reactions': expression(`(field) => {
      setTimeout(() => {
        field.setComponentProps({
          visible: $defineProps.visible
        })
      })
    }`),
    'x-component-props': {
      class: 'dialogMain',
      size: 'large',
      appendToBody: true,
      closeOnClickModal: false,
      okButtonText: i18nExpression('common.submit'),
      beforeClose: expression(`(done, type) => {
        if (type === 'ok') {
          const field = $form.query('.tableList').take()
          const selections = field.invoke('getRealRowValueByRow', field.invoke('getVxeTableInstance').getRadioRecord()) || []
          if (selections.length < 1) {
            return $message.warning($t('common.pleaseSelectOne'))
          }

          emits('confirm', selections)
        }
        emits('close')
        done()
      }`),

      '@opened': expression(`() => {
        if ($defineProps.visible) {
          $searchParentOrg($form)
        }
      }`)
    },
    'x-data': {
      pageInfo: {
        current: 1,
        pageSize: 15,
        total: 0,
        pageSizes: [15, 30, 60, 120, 300, 600, 1000, 1500]
      }
    },

    properties: {
      formEngine: {
        type: 'void',
        properties: {
          searchInner: {
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'horizontal'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              minColumns: 1,
              columnGap: 10,
              rowGap: 0
            },
            properties: generateXindexInOrder({
              // 供应商名称
              vendorName: {
                type: 'string',
                'x-visible': !vendor(),
                'x-decorator': 'FormItem',
                title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  showKey: 'companyName',
                  showInput: '{{$values.searchInner.vendorName}}',
                  name: 'scc_sup_company_info_all',
                  '@close-quicksearch': expression(`async (val) => {
                    $values.searchInner.vendorId = val ? val.companyId : ''
                    $values.searchInner.vendorCode = val ? val.companyCode : ''
                    $values.searchInner.vendorName = val ? val.companyName : ''
                  }`)
                }
              },
              // 物料名称
              materialName: {
                type: 'void',
                'x-decorator': 'FormItem',
                title: i18nExpression('purchaseDemand.itemName'),
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  showKey: 'materialName',
                  name: 'scc_base_material_item',
                  '@close-quicksearch': expression(`async (val) => {
                    $values.searchInner.materialId = val ? val.materialId : ''
                    $values.searchInner.materialCode = val ? val.materialCode : ''
                    $values.searchInner.materialName = val ? val.materialName : ''
                  }`)
                }
              },
              // 业务实体
              orgId: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('dataConfMod.orgId'),
                'x-component': 'OrganizationSelector',
                'x-component-props': {
                  'node-type': 'OU',
                  'parent-id': -1,
                  '@select': expression(`(node) => {
                    $values.searchInner.orgId = node ? node.organizationId : null
                    $values.searchInner.orgCode = node ? node.organizationCode : null
                    $values.searchInner.orgName = node ? node.organizationName : null
                  }`)
                }
              },
              searchGroup: {
                type: 'void',
                'x-component': 'div',
                'x-decorator': 'FormItem',
                'x-query-engine-skip': true,
                'x-component-props': {
                  style: 'display: flex; justify-content: flex-end;'
                },
                properties: {
                  searchBtn: {
                    type: 'void',
                    'x-component': 'RButton',
                    'x-content': i18nExpression('common.search'),
                    'x-decorator': 'FormItem',
                    'x-query-engine-skip': true,
                    'x-component-props': {
                      type: 'primary',
                      style: 'margin-right: 8px;',
                      '@click': expression(`() => {
                        console.log($form,'form')
                        $searchParentOrg($form)
                      }`)
                    }
                  },
                  resetBtn: {
                    type: 'void',
                    'x-component': 'RButton',
                    'x-content': i18nExpression('common.reset'),
                    'x-decorator': 'FormItem',
                    'x-query-engine-skip': true,
                    'x-component-props': {
                      type: 'default',
                      '@click': expression(`() => {
                        console.log('重置')
                      }`)
                    }
                  }
                }
              }
            })
          },
          tableList: {
            type: 'array',
            'x-component': 'NormalRenderTable',
            'x-component-props': {
              preColumns: 'radio, seq',
              class: 'table-view-vxe-table',
              openCustomTable: false,
              sortable: false,
              editMode: false,
              pagination: expression(`$form.query('PriceSearchDialog').get('data').pageInfo`),
              '@pageChange': expression(`(currentPage) => {
                console.log('pageChange')
                $searchParentOrg($form, currentPage)
              }`),
              '@pageSizeChange': expression(`(pageSize) => {
                console.log('pageSizeChange')
                $searchParentOrg($form, null, pageSize)
              }`)
            },
            properties: generateXindexInOrder({
              // 价格库编号
              priceLibraryNo: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('bidMod.priceLibraryNo'),
                  minWidth: 150
                }
              },
              // 供应商名称
              vendorName: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('common.vendorName'),
                  minWidth: 150
                }
              },
              // 物料名称
              materialName: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('common.materialName'),
                  minWidth: 150
                }
              },
              // 物料编码
              materialCode: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('common.materialCode'),
                  minWidth: 150
                }
              },
              // 业务实体
              orgName: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('purchaseDemand.businessEntity'),
                  minWidth: 150
                }
              },
              // 库存组织
              organizationName: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('purchaseDemand.invOrg'),
                  minWidth: 150
                }
              },
              // 价格
              taxPrice: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('materialPrice.price'),
                  minWidth: 150
                }
              },
              // 币种
              currencyCode: {
                type: 'string',
                'x-component-props': {
                  code: 'currency'
                },
                'x-render-table-column': {
                  title: i18nExpression('purchaseDemand.currency'),
                  minWidth: 150
                }
              },
              // 价格有效期
              effectiveDate: {
                type: 'string',
                'x-render-table-column': {
                  title: '价格有效期',
                  minWidth: 150,
                  formatter: expression(`({ cellValue, row, column }) => {
                    let effectiveDate = row.effectiveDate ? parseTime(row.effectiveDate, '{y}-{m}-{d}') : ''
                    let expirationDate = row.expirationDate ? parseTime(row.expirationDate, '{y}-{m}-{d}') : ''
                    return effectiveDate + '/' + expirationDate
                  }`)
                }
              }
            })
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="PriceSearchDialog"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
