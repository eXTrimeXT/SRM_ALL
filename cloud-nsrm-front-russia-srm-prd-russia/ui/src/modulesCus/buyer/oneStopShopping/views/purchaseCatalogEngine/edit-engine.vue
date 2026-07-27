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
const addShoppingCart = ($form: any, $queryEngine: any, $bus: any, $self) => {
  /* 获取加购数量 */
  const requirementNum = $self.query('.requirementNum').take().value
  const {
    catalogOnShelvesId,
    unitCode,
    categoryCode
  } = $form.values
  const {
    extAddressId,
    extAddressName,
    extAreaCode
  } = app.$attrs.params.queryForm
  // /* 获取收货地址的收货人以及收货人联系方式 */
  const addressList = app.$attrs.params.addressList
  const { receiverPhone, receiver } = addressList.find(item => item.value === extAddressId)
  if (!unitCode || !categoryCode) {
    return app.$message.warning(t('oneStopShopping.addShoppingCartMsg1'))
  }
  $queryEngine.request.baseRequest({
    action: 'addToShoppingCart',
    payload: [{
      catalogOnShelvesId,
      requirementNum,
      extAddressId,
      extAddressName,
      extAreaCode,
      extReceiver: receiver,
      extReceiverContact: receiverPhone
    }],
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
        // 加入数量
        num: {
          type: 'void',
          'x-component': 'Span',
          'x-component-props': {
            style: {
              'margin-top': '6px'
            }
          },
          'x-content': i18nExpression('cusEntry.sup.pushBuyNum')
        },
        requirementNum: {
          type: 'number',
          'x-component-props': {
            style: {
              width: '10%'
            },
            min: 0,
            precision: 0
          }
        },
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
              addShoppingCart($form, $queryEngine, $bus, $self)
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
                  // 最小起订量
                  orderQuantityMinimum: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('cusEntry.sup.orderQuantityMinimum')
                  },
                  // 送货周期
                  deliveryCycle: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('cusEntry.sup.deliveryCycle')
                  },
                  // 质保期
                  extShelfLife: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('cusEntry.sup.extShelfLife')
                  },
                  // 品牌
                  brand: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('dataConfMod.band')
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
