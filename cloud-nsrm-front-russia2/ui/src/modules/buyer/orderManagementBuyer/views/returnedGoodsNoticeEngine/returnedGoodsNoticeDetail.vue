<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
// @ts-ignore
import performanceTpl from '@/service/modules/cmPerform/buyer/main'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import Detail from './components/collapseItem/detail'
// @ts-ignore
import DetailDialog from './components/dialog/detailDialog'
// @ts-ignore
import { setRepeatData } from 'lib@/utils/util'
// @ts-ignore
import { deepClone, parseTime, transformDetailQuery, transformDetailDetailListItem } from '@/utils'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const saveFetch = ($form: any, params: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  $queryEngine.request.save(params, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      if (type === 'DRAFT') {
        $bus.$emit('ReturnOrder')
        $form.values.returnOrderId = res.originalData?.records[0] || ''
        $queryEngine.request.read()
      } else {
        $closePageAndRefreshListPageData($bus)
      }
    }
  })
}

// 保存
const $saveData = ($form: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  const form = toJS($form.values)
  form.returnStatus = type

  $form.validate().then(() => {
    if (type === 'WAITING_CONFIRM' && form.detailList.length === 0) {
      $message.error($t('returnedGoodsNotice.prompt1')) // 请选择退货明细信息
    } else {
      saveFetch($form, form, $queryEngine, $message, $bus, type)
    }
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('ReturnOrder')
  emitTabRemove($attrs.tabName)
}

const $openDialog = ($form: any, $message: any) => {
  if (!$form.values.vendorName) {
    return $message.warning($t('vendorMod.msgVendor') + '！')
  }
  if (!$form.values.organizationId) {
    return $message.warning($t('purchaseDemand.orgIdTips') + '！')
  }

  $form.query('selWarehousingDetailDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('ReturnOrder').get('data').queryData = {
      orgId: $form.values.organizationId,
      orgName: $form.values.organizationName,
      vendorName: $form.values.vendorName
    }

    $form.query('ReturnOrder').get('data').dialogInit = true
  })
}

const $setSelectedData = ($form: any) => {
  let detailList = deepClone($form.values.detailList)
  let selectedData = $form.query('ReturnOrder').get('data').selectedData
  let data = setRepeatData(detailList, selectedData, 'deliveryNoteDetailId', null)
  $form.values.detailList = data
}

const scope = {
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $openDialog,
  $setSelectedData,
  $saveData,
  parseTime,
  $transformDetailQuery: transformDetailQuery,
  $transformDetailDetailListItem: transformDetailDetailListItem
}

const components = {
  BaseInfo,
  Detail,
  DetailDialog
}

const schema = defineSchemas({
  // 基本信息
  ReturnOrder: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-data': {
      isAdd: false,
      dialogInit: false,
      queryData: {},
      selectedData: {}
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            $form.query('ReturnOrder').get('data').isAdd = $attrs.params.flag === 'add'

            return !!$attrs.params?.row?.returnOrderId || !!$form.values.returnOrderId
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
                let {returnDetailItem,deliveryNoteDetailItem,deliveryNoteItem,orderDetailItem,orderItem} = $transformDetailDetailListItem(item, res.originalData.ref,['ReturnDetail.DeliveryNoteDetail.DeliveryNote','ReturnDetail.DeliveryNoteDetail.OrderDetail.Order'])

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
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          action: 'saveOrUpdate'
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': `{{$form.readPretty ? $t('common.close') : $t('common.cancel') }}`,
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        save: {
          type: 'void',
          'x-hidden': '{{$form.readPretty}}',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus, 'DRAFT')
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-hidden': '{{$form.readPretty}}',
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(` () => {
              $saveData($form, $queryEngine, $message, $bus, 'WAITING_CONFIRM')
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
  },
  // 退货明细弹窗
  selWarehousingDetailDialog: {
    type: 'void',
    title: $t('returnedGoodsNotice.dialogTitle'), // 请选择退货明细
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'large',
      footer: true,
      beforeClose: expression(`(done, type) => {
        $form.query('ReturnOrder').get('data').dialogInit = false
        if ( type === 'ok') {
          $setSelectedData ($form)  
        }
        done()
      }`)
    },
    properties: {
      detailDialog: {
        type: 'void',
        'x-component': 'DetailDialog',
        'x-component-props': {
          init: expression('$form.query(\'ReturnOrder\').get(\'data\').dialogInit'),
          queryData: expression('$form.query(\'ReturnOrder\').get(\'data\').queryData'),
          '@getSelections': expression(`(selections) => {
            $form.query('ReturnOrder').get('data').selectedData = selections
          }`)
        }
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
    schemaKey="BuyerReturnOrderDetail"
  />
</template>
