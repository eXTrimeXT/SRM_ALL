<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import Detail from './components/collapseItem/detail'
// @ts-ignore
import { parseTime, transformDetailQuery, transformDetailDetailListItem } from '@/utils'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('ReturnOrderVendor')
  emitTabRemove($attrs.tabName)
}

const scope = {
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  parseTime,
  $transformDetailQuery: transformDetailQuery,
  $transformDetailDetailListItem: transformDetailDetailListItem
}

const components = {
  BaseInfo,
  Detail
}

const schema = defineSchemas({
  // 基本信息
  ReturnOrderVendor: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return !!$attrs.params?.row?.returnOrderId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.returnOrderId || $form.values.returnOrderId]
            data.query['*'] = {}

            data.query.detailList = {
              deliveryNoteDetailId: {
                deliveryNoteId: {},
                orderDetailId: {
                  orderId: {}
                }
              }
            }
            data.query = $transformDetailQuery(data.query, ['detailList.deliveryNoteDetailId.deliveryNoteId','detailList.deliveryNoteDetailId.orderDetailId.orderId'])
 
            return data
          }`),
          onSuccess: expression(`(res) => {
            let { detailList } = res.data[0]

            if(detailList.length){
              detailList.forEach((item, index) =>{
                let {returnDetailItem,deliveryNoteDetailItem,deliveryNoteItem,orderDetailItem,orderItem} = $transformDetailDetailListItem(item, res.originalData.ref,['ReturnDetailVendor.DeliveryNoteDetailVendor.DeliveryNoteVendor','ReturnDetailVendor.DeliveryNoteDetailVendor.OrderDetailVendor.OrderVendor'],'Vendor')
               

                orderDetailItem.orderLineNum = orderDetailItem.lineNum
                deliveryNoteDetailItem.notReturnedNum = deliveryNoteDetailItem.warehouseQuantity - deliveryNoteDetailItem.actualReturnedNum
                let obj = { ...orderDetailItem,...deliveryNoteDetailItem,...returnDetailItem,...deliveryNoteItem,...orderItem}
                detailList.splice(index,1,obj)
              })
            }
            $form.setValues({
              ...res.data[0]
            })
                       
          }`)
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          // 基本信息
          baseInfo: {
            ...BaseInfo
          },
          // 退货明细信息
          detail: {
            ...Detail
          }
        })
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="VendorReturnOrderDetail"
  />
</template>
