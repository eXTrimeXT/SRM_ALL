<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
// @ts-ignore
import performanceTpl from '@/service/modules/cmPerform/buyer/main'
// @ts-ignore
import OrderStorageInfo from './components/collapseItem/orderStorageInfo'
// @ts-ignore
import OrderStorageDetail from './components/collapseItem/orderStorageDetail'
// @ts-ignore
import SelWarehousingDetail from './components/dialog/selWarehousingDetail'
// @ts-ignore
import { setRepeatData } from 'lib@/utils/util'
// @ts-ignore
import { deepClone, parseTime, transformDetailQuery, transformDetailDetailListItem } from '@/utils'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('OrderStorageHead')
  emitTabRemove($attrs.tabName)
}

const $openDialog = ($form: any, $message: any) => {
  if (!$form.values.vendorName || !$form.values.orgId || !$form.values.organizationId) {
    // 请先选择业务实体、库存组织、供应商！
    return $message.warning($t('orderMod.buyerOrderSynergy.orderDetailsMsg2'))
  }
  $form.query('selWarehousingDetailDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('WarehouseReceipt').get('data').queryData = {
      orgId: $form.values.orgId,
      organizationId: $form.values.organizationId,
      vendorName: $form.values.vendorName,
      orgName: $form.values.orgName
    }

    $form.query('WarehouseReceipt').get('data').dialogInit = true
  })
}

const $setSelectedData = ($form: any) => {
  let detailList = deepClone($form.values.detailList)
  let selectedData = $form.query('WarehouseReceipt').get('data').selectedData
  let data = setRepeatData(detailList, selectedData, 'deliveryNoteDetailId', (row: any) => {
    return {
      warehouseReceiptRowNum: '',
      warehouseQuantity: row.notWarehouseQuantity,
      deliveryNoteLineNum: row.lineNum
    }
  })
  $form.values.detailList = data
}

const saveFetch = ($form: any, params: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  $queryEngine.request.save(params, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      if (type === 'DRAFT') {
        $bus.$emit('OrderStorageHead')
        $form.values.warehouseReceiptId = res.originalData?.records[0] || ''
        $queryEngine.request.query()
      } else {
        $closePageAndRefreshListPageData($bus)
      }
    }
  })
}

// 保存
const $saveData = ($form: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  const form = toJS($form.values)
  form.warehouseReceiptStatus = type

  if (type === 'DRAFT') {
    saveFetch($form, form, $queryEngine, $message, $bus, type)
  } else {
    $form.validate().then(() => {
      if (form.detailList.length === 0) {
        $message.error($t('orderStorage.prompt1')) // 请选择入库明细信息
      } else {
        saveFetch($form, form, $queryEngine, $message, $bus, type)
      }
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
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
  OrderStorageInfo,
  OrderStorageDetail,
  SelWarehousingDetail,
  FileDynamic
}

const schema = defineSchemas({
  // 基本信息
  WarehouseReceipt: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-data': {
      dialogInit: false,
      queryData: {},
      selectedData: {}
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        query: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            $form.isAdd = $attrs.params.flag === 'add'

            return $attrs.params.row.warehouseReceiptId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = {filter: {warehouseReceiptId: $attrs?.params?.row?.warehouseReceiptId || $form.values.warehouseReceiptId || '' }}
            data.query['*'] = {}
            data.query.detailList = {
              orderDetailId: {
                orderId: {}
              },
              deliveryNoteDetailId: {
                deliveryNoteId: {}
              }
            }
            data.query = $transformDetailQuery(data.query, ['detailList.orderDetailId.orderId','detailList.deliveryNoteDetailId.deliveryNoteId'])

            return data
          }`),
          onSuccess: expression(`(res) => {
            let { detailList } = res.data[0]

            if(detailList.length){
              detailList.forEach((item, index) =>{
                const {warehouseReceiptDetailItem,deliveryNoteDetailItem,deliveryNoteItem,orderDetailItem,orderItem} = $transformDetailDetailListItem(item, res.originalData.ref,['WarehouseReceiptDetail.DeliveryNoteDetail.DeliveryNote','WarehouseReceiptDetail.OrderDetail.Order'])
                
                let obj = {
                  deliveryNoteLineNum: deliveryNoteDetailItem?.lineNum ?? '',
                  orderLineNum: orderDetailItem?.lineNum ?? '',
                  notWarehouseQuantity: +(deliveryNoteDetailItem?.deliveryQuantity ?? 0) - +(deliveryNoteDetailItem?.warehouseQuantity ?? 0)
                }
                obj = { ...deliveryNoteDetailItem,...warehouseReceiptDetailItem,...deliveryNoteItem,...orderDetailItem,...orderItem,...obj }
                detailList.splice(index,1,obj)
              })
            }
            
            res.data[0].receiveAddressName = res.data[0].receiveAddress
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
          'x-content': '{{$form.readPretty ? $t(\'common.close\') : $t(\'common.cancel\') }}',
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
          orderStorageInfo: {
            ...OrderStorageInfo
          },
          // 入库明细信息
          orderStorageDetail: {
            ...OrderStorageDetail
          }
        })
      }

    }
  },
  // 入库明细弹窗
  selWarehousingDetailDialog: {
    type: 'void',
    title: $t('orderMod.selWarehousingDetail'),
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'large',
      footer: true,
      beforeClose: expression(`(done, type) => {
            $form.query('WarehouseReceipt').get('data').dialogInit = false
            if ( type === 'ok') {
              $setSelectedData ($form)  
            }
            done()
      }`)
    },
    properties: {
      selWarehousingDetail: {
        type: 'void',
        'x-component': 'SelWarehousingDetail',
        'x-component-props': {
          init: expression('$form.query(\'WarehouseReceipt\').get(\'data\').dialogInit'),
          queryData: expression('$form.query(\'WarehouseReceipt\').get(\'data\').queryData'),
          '@getSelections': expression(`(selections) => {
            $form.query('WarehouseReceipt').get('data').selectedData = selections
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
    schemaKey="WarehouseReceipt"
  />
</template>
