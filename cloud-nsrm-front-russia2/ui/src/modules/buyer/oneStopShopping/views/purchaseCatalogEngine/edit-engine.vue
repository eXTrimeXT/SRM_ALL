<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  connect
} from '@meicloud/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import priceDetailCard from './components/priceDetailCard'
// @ts-ignore
import { getCountDown } from 'lib@/utils/date-format'

const { app, t, emitTabRemove } = usePageHelper()

const attrs: any = useAttrs()

const $closeTab = ($bus: any) => {
  $bus.$emit('purchaseCatalog')
  emitTabRemove(attrs.tabName)
}

// 初始化页面
const queryDetails = (data: any, $form: any) => {
  // 获取格式化后截止时间
  data.remainingTime = getCountDown(data.expirationDate)

  // 设置卡片信息
  $form.query('priceDetailCard').take().setComponentProps({
    formDetail: data,
    hasImg: data.catalogOnShelvesAttaches.length > 0,
    catalogList: data.catalogOnShelvesAttaches
  })

  // 设置物料参数
  $form.setValues(data)
}

// 加入购物车
const addShoppingCart = ($form: any, $queryEngine: any, $bus: any) => {
  if (!$form.values.unitCode || !$form.values.categoryCode) {
    return app.$message.warning(t('oneStopShopping.addShoppingCartMsg1'))
  }
  if (!$form.values.orgId || !$form.values.organizationId) {
    return app.$message.warning(t('oneStopShopping.addShoppingCartMsg2'))
  }
  $queryEngine.request.baseRequest({
    action: 'addToShoppingCart',
    payload: [$form.values.catalogOnShelvesId],
    query: { '*': {}, catalogOnShelvesAttaches: { '*': {} } }
  }).then(() => {
    app.$message.success(t('common.success'))
    $closeTab($bus)
  })
}

let scope: any = {
  app,
  attrs,
  $closeTab,
  emitTabRemove,
  queryDetails,
  addShoppingCart
}

const components = {
  priceDetailCard: connect(priceDetailCard)
}

const schema = defineSchemas({
  CatalogOnShelves: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            return !!attrs?.params?.row?.catalogOnShelvesId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [attrs?.params?.row?.catalogOnShelvesId || data.payload[0]]

            data.query = {
              '*': {},
              catalogOnShelvesAttaches:{
                "*":{}
              }
            }

            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('onSuccess=>', res.data[0])
            queryDetails(res.data[0], $form)
          }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        // 返回
        goBack: {
          type: 'void',
          'x-content': i18nExpression('vendorMod.goBack'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async () => {
              $closeTab($bus)
            }`)
          }
        },
        // 加入购物车
        addShoppingCart: {
          type: 'void',
          'x-content': i18nExpression('common.addShoppingCart'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`async () => {
              addShoppingCart($form, $queryEngine, $bus)
            }`)
          }
        }
      }
    },
    properties: {
      priceDetailCard: {
        type: 'void',
        'x-component': 'priceDetailCard',
        'x-component-props': {
          formDetail: {},
          hasImg: false,
          catalogList: []
        }
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 物料参数
          materialItem: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('dataConfMod.materialParams')
            },
            properties: {
              materialParams: {
                type: 'void',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                  layout: 'vertical'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                  maxColumns: 4,
                  columnGap: 32,
                  rowGap: 0
                },
                'x-read-pretty': true,
                properties: {
                  // 送货周期
                  deliveryCycle: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.deliveryCycle')
                  },
                  // 品牌
                  brand: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.band')
                  },
                  // 最小起订量
                  orderQuantityMinimum: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.orderQuantityMinimum')
                  },
                  // 规格/型号
                  specification: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.specification')
                  },
                  // 重量
                  weight: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.weight')
                  },
                  // 尺寸
                  size: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.size')
                  },
                  // 颜色
                  color: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.ceeaColor')
                  }
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
  <RenderEngine :pageAttrs="attrs" :schema="schema" :components="components" :scope="scope" />
</template>

<style scoped lang="scss">
</style>
