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
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { useAttrs, defineProps, defineEmits } from 'vue-demi'

const { t: $t, getCurrentUserInfo } = usePageHelper()

const emits = defineEmits(['close'])
const { companyId } = getCurrentUserInfo()

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
  form: {
    type: Object,
    default: () => {}
  }
})

console.log($defineProps, 'defineProps')

// 处理送货单查询参数
const getSearchParams = (params: any) => {
  let keys = []
  let newParams: any = {}

  for (let [key, value] of Object.entries(params)) {
    if (value && Object.values(value)[0]) keys.push(key)
  }
  console.log(keys, 'keys')

  for (let k in params) {
    if (keys.includes(k)) {
      newParams[k] = params[k]
    }
  }

  return newParams
}

const $searchParentOrg = async ($form: any, $queryEngine: any, pageNum?: number, pageSize?: any) => {
  let params = {
    deliveryNumber: { eq: $form.values.searchInner.deliveryNumber },
    deliveryDate: { between: $form.values.searchInner.deliveryDate }
  }

  const newParams = getSearchParams(params)
  console.log(newParams, 'newParams')
  const res = await $queryEngine.request.baseRequest({
    type: 'DeliveryNoteVendor',
    action: 'query',
    lang: 'zh-cn',
    payload: {
      page: {
        pageNum: pageNum || $form.query('DeliveryNoteVendor').get('data').pageInfo.pageNum,
        pageSize: pageSize || $form.query('DeliveryNoteVendor').get('data').pageInfo.pageSize,
        sort: "lastUpdateDate desc"
      },
      filter: {
        ...$form.query('DeliveryNoteVendor').get('data').filter,
        ...newParams
      }
    },
    'query': {
      '*': {}
    }
  })

  console.log(res, 'resss')

  $form.values.tableList = res.data

  Object.assign($form.query('DeliveryNoteVendor').get('data').pageInfo, {
    pageNum: res.originalData.payload.pageNum,
    pageSize: res.originalData.payload.pageSize,
    total: res.originalData.payload.total
  })
}

// @ts-ignore
const scope = {
  emits,
  $attrs,
  $t,
  $defineProps,
  $searchParentOrg,
  companyId
}

// @ts-ignore
const components = {

}

// @ts-ignore
const schema = defineSchemas({
  // 基本信息
  DeliveryNoteVendor: {
    type: 'void',
    title: i18nExpression('orderMod.selDeliveryNote'),
    'x-component': 'RDialog',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          ready: expression(`async () => {
            let promise = await new Promise(res => {
              setTimeout(() => {
                $form.query('DeliveryNoteVendor').get('data').filter = {
                  vendorId: { eq: companyId },
                  deliveryNoteStatus: { eq: 'DELIVERED' },
                  ifCreateDeliveryAppointment: { eq: 'N' },
                  orgId: { eq: $defineProps.form.orgId },
                  organizationId: { eq: $defineProps.form.organizationId }
                }

                res(true)
              })
            })
            return promise
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = {
              page: {
                pageNum: 1,
                pageSize: 15,
                sort: "lastUpdateDate desc"
              },
              filter: $form.query('DeliveryNoteVendor').get('data').filter
            }

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('paginationQuery onSuccess=>', res.data)
            $form.values.tableList = res.data
            Object.assign($form.query('DeliveryNoteVendor').get('data').pageInfo, {
              total: res.originalData.payload.total
            })
          }`)
        }
      }
    },
    'x-reactions': expression(`(field) => {
      setTimeout(() => {
        field.setComponentProps({
          visible: $defineProps.visible
        })
      },500)
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
          const selections = field.componentProps.componentInstance.getCheckboxRecords()
          if (selections.length < 1) {
            return $message.warning($t('common.pleaseSelectMinOne'))
          }

          emits('confirm', selections)
        }
        emits('close')
        done()
      }`),

      '@opened': expression(`() => {
        if ($defineProps.visible) {
          // $searchParentOrg($form, $queryEngine)
        }
      }`)
    },
    'x-data': {
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0,
        pageSizes: [15, 30, 60, 120, 300, 600, 1000, 1500]
      },
      filter: {}
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
              // 送货单号
              deliveryNumber: {
                type: 'string',
                title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'),
                'x-decorator': 'FormItem',
                'x-decorator-props': {
                  gridSpan: 1
                }
              },
              // 送货日期
              deliveryDate: {
                type: 'date',
                'x-decorator': 'FormItem',
                title: i18nExpression('purchaseDemand.applyDate'),
                'x-component-props': {
                  type: 'daterange',
                  format: 'yyyy-MM-dd',
                  'value-format': 'yyyy-MM-dd'
                },
                'x-query-engine-query-operator': 'between'
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
                        $searchParentOrg($form, $queryEngine)
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
                        $form.values.searchInner = {}
                        $searchParentOrg($form, $queryEngine)
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
              height: 300,
              preColumns: 'checkbox, seq',
              class: 'table-view-vxe-table',
              openCustomTable: false,
              sortable: false,
              editMode: false,
              pagination: expression(`$form.query('DeliveryNoteVendor').get('data').pageInfo`),
              '@pageChange': expression(`(currentPage) => {
                console.log('pageChange')
                $searchParentOrg($form, $queryEngine, currentPage)
              }`),
              '@pageSizeChange': expression(`(pageSize) => {
                console.log('pageSizeChange')
                $searchParentOrg($form, $queryEngine, null, pageSize)
              }`)
            },
            properties: generateXindexInOrder({
              // 送货单号
              deliveryNumber: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'),
                  minWidth: 100
                }
              },
              // 送货日期
              deliveryDate: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('orderMod.buyerOrderSynergy.entryTime'),
                  minWidth: 100
                }
              },
              // 备注
              comments: {
                type: 'string',
                'x-render-table-column': {
                  title: i18nExpression('common.remark'),
                  minWidth: 100
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
    schemaKey="DeliveryAppointSupplierDialog"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
