<!-- eslint-disable quotes -->
<script setup lang="ts">
import { useAttrs, ref } from 'vue-demi'
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
// @ts-ignore
import DeliveryOrderInfo from './components/collapseItem/deliveryOrderInfo'
// @ts-ignore
import DeliveryOrderDetail from './components/collapseItem/deliveryOrderDetail'
// @ts-ignore
import FileInfo from './components/collapseItem/fileInfo'
// @ts-ignore
import DeliveryNotice from './components/dialog/deliveryNotice'
// @ts-ignore
import PurchaseOrder from './components/dialog/purchaseOrder'
// @ts-ignore
import { setRepeatData } from 'lib@/utils/util'
// @ts-ignore
import { transformDetailQuery, transformDetailDetailListItem } from '@/utils'
// @ts-ignore
import { systemUrl } from '@/config/sysConfig'
// @ts-ignore
import tagManage from './tagManage.vue'

const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()

const $attrs: any = useAttrs()

const $orderSourceList: any = ref([
  {
    value: 'PURCHASE_ORDER',
    label: $t('route.buyerPurchaseOrder')
  },
  {
    value: 'DELIVERY_NOTICE',
    label: $t('orderMod.arrivalNotice')
  }
])

/// 保存并绑定条码
const $saveAndGoTag = useDebounceFn(($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $form.validate().then(() => {
    if ($validateForm($form, $message)) {
      const form = toJS($form.values)
      $form.query('DeliveryNote').get('data').saveAndGoTagLoading = true
      $queryEngine.request.save(form).then((res: any) => {
        if (res.data && res.data.length > 0) {
          let deliveryNote = res.data[0]
          $form.setValues({
            deliveryNumber: deliveryNote.deliveryNumber,
            deliveryNoteId: deliveryNote.deliveryNoteId
          })
          $jumpToTag($form, $queryEngine, $message, $bus)
        }
        $form.query('DeliveryNote').get('data').saveAndGoTagLoading = false
      }).catch(() => {
        $form.query('DeliveryNote').get('data').saveAndGoTagLoading = false
      })
    }
  }).catch((err: any) => {
    console.log(err, 'err')
    $validateForm($form, $message)
  })
}, 300)

const $jumpToTag = ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $closePageAndRefreshListPageData($bus)
  let row = {
    deliveryNumber: $form.values.deliveryNumber,
    deliveryNoteId: $form.values.deliveryNoteId
  }
  let name = $form.values.deliveryNumber ?? ''
  emitTabAdd({
    component: tagManage,
    params: {
      status: $form.values?.deliveryNoteStatus || '',
      row: row || '',
      tabName: name ? 'tagManage' + name : 'tagManage'
    },
    title: $t('orderMod.buyerOrderSynergy.tagManage') + name,
    name: name ? 'tagManage' + name : 'tagManage'
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryNote')
  emitTabRemove($attrs.tabName)
}

const $openDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.receivedFactory || !$form.values.orderSource) {
    return $message.warning($t('orderMod.pleaseFillrequired'))
  }
  $form.query('orderDetailsDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('DeliveryNote').get('data').queryData = {
      orgId: $form.values.orgId,
      orgCode: $form.values.orgCode,
      orgName: $form.values.orgName,
      organizationId: $form.values.organizationId,
      organizationCode: $form.values.organizationCode,
      organizationName: $form.values.organizationName,
      receiveAddress: $form.values.receivedFactory,
      orderStatus: 'APPROVED'
    }
    $form.query('DeliveryNote').get('data').orderDetailInit = true
  })
}

const $setSelectedData = ($form: any) => {
  let selections = $form.query('DeliveryNote').get('data').selectedData
  selections.forEach((item:any) => {
    item.orderLineNum = item.lineNum
  })
  // 条件判断回调 送货通知和订单区分开
  let condition = (row: any) => {
    if ($form.values.orderSource === 'DELIVERY_NOTICE') return `${row.deliveryNoticeNumber}_${row.deliveryNoticeLineNum}`
    if ($form.values.orderSource === 'PURCHASE_ORDER') return `${row.orderNumber}_${row.orderLineNum}`
  }
  // 行添加补充字段
  let lineSet = (row: any) => {
    return {
      deliveryQuantity: row.numberRemaining,
      deliverPlanNum: $form.values.orderSource === 'DELIVERY_NOTICE' ? row.deliveryNoticeNumber : ''
    }
  }
  // 明细添加去重
  setRepeatData($form.values.detailList, selections, condition, lineSet)
  $form.query('orderDetailsDialog').take().setComponentProps({ visible: false })
}

const $validateForm = ($form: any, $message: any) => {
  // 基础信息
  const baseInfoFlag = $form.values.deliveryDate && $form.values.orgId && $form.values.organizationId && $form.values.receivedFactory && $form.values.orderSource
  if (!baseInfoFlag) {
    $message.error($t('vendorMod.pleasefinishRequired'))
    return false
  }

  // 送货单明细
  let detailList = $form.values.detailList || []
  let flag = true
  if (detailList.length === 0) {
    $message.error($t('buyerDeliveryOrder.prompt3'))
    return false
  } else {
    for (let i = 0; i < detailList.length; i++) {
      if (!detailList[i].deliveryQuantity) {
        $message.error($t('buyerDeliveryOrder.prompt4'))
        flag = false
        break
      }
    }
  }

  return flag
}

// 保存
const $saveData = ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $form.validate().then(() => {
    const form = toJS($form.values)
    if (form.detailList.length === 0) {
      $message.error($t('buyerDeliveryOrder.prompt3'))
    } else {
      $queryEngine.request.save(form, { loading: true }).then((res: any) => {
        if (res.data && res.data.length > 0) {
          $message.success($t('common.successSave'))
          $closePageAndRefreshListPageData($bus)
        }
      })
    }
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

// 打印
const $printBill = ($form: any) => {
  const xml = encodeURIComponent('database:database:送货单打印.ureport.xml')
  const params = encodeURIComponent(`param=${$form.values.deliveryNumber}`)
  const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

// @ts-ignore
const scope = {
  $attrs,
  app,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $orderSourceList,
  $openDialog,
  $setSelectedData,
  $saveData,
  $saveAndGoTag,
  $jumpToTag,
  $printBill,
  $transformDetailQuery: transformDetailQuery,
  $transformDetailDetailListItem: transformDetailDetailListItem
}

// @ts-ignore
const components = {
  DeliveryOrderInfo,
  DeliveryOrderDetail,
  FileInfo,
  DeliveryNotice,
  PurchaseOrder,
  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  // 基本信息
  DeliveryNote: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      orderDetailInit: false,
      orderSource: '',
      queryData: {},
      selectedData: [],
      saveAndGoTagLoading: false
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        query: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return $attrs.params.row.deliveryNoteId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = {filter: {deliveryNoteId: $attrs?.params?.row?.deliveryNoteId || $form.values.deliveryNoteId || '' }}
            data.query['*'] = {}
            data.query.detailList = {
              orderDetailId: {
                orderId: {}
              },
              deliveryNoticeDetailId: {
                deliveryNoticeId: {}
              },
              fileUploads: {}
            }
            data.query = $transformDetailQuery(data.query, ['detailList.orderDetailId.orderId','detailList.deliveryNoticeDetailId.deliveryNoticeId','detailList.fileUploads'])

            return data
          }`),
          onSuccess: expression(`(res) => {
            let { detailList } = res.data[0]
            if(detailList.length){
              detailList.forEach((item, index) =>{
                let obj = {}
                if(res.originalData.ref?.OrderDetail){
                  const {deliveryNoteDetailItem,orderDetailItem,orderItem} = $transformDetailDetailListItem(item, res.originalData.ref,['DeliveryNoteDetail.OrderDetail.Order'])
                  obj = {...item, ...orderDetailItem, ...orderItem, ...deliveryNoteDetailItem}
                }else if(res.originalData.ref?.DeliveryNoticeDetail){
                  const {deliveryNoteDetailItem,deliveryNoticeDetailItem,deliveryNoticeItem} = $transformDetailDetailListItem(item, res.originalData.ref,['DeliveryNoteDetail.DeliveryNoticeDetail.DeliveryNotice'])
                  obj = {...item, ...deliveryNoticeDetailItem, ...deliveryNoticeItem, ...deliveryNoteDetailItem}
                }
                obj.orderLineNum = obj.lineNum || null
                detailList.splice(index,1,obj)
              })
            }

            $form.setValues({
              ...res.data[0]
            })

            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        },
        save: {
          action: 'saveOrUpdate',
          transformRequest: expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),
          onSuccess: expression(`(res) => {

          }`),
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.cancel'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $closePageAndRefreshListPageData($bus)
            }`)
          }
        },
        saveAndGoTag: {
          type: 'void',
          'x-content': `{{$form.readPretty?$t('orderMod.goTag'):$t('orderMod.buyerOrderSynergy.saveAndGoTag')}}`,
          'x-component': 'Button',
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`),
          'x-component-props': {
            type: 'primary',
            loading: `{{$form.query('DeliveryNote').get('data').saveAndGoTagLoading}}`,
            disabled: expression(`$form.values.deliveryNoteStatus === 'SUBMIT'`),
            '@click': expression(`() => {
              $confirm($t('buyerDeliveryOrder.prompt1'), {
                confirmButtonText: $t('buyerDeliveryOrder.toBind'),
                cancelButtonText: $t('common.cancel'),
                  type: 'warning'
                }).then(() => {
                  $saveAndGoTag($form, $queryEngine, $message, $bus)
                }).catch(() => {})
              
            }`)
          }
        },
        saveBill: {
          type: 'void',
          'x-hidden': '{{!$vendor()}}',
          'x-content': i18nExpression('common.save'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            disabled: expression(`$form.values.deliveryNoteStatus === 'SUBMIT'`),
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`)
        },
        printBill: {
          type: 'void',
          'x-content': i18nExpression('route.pdfPrint'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(` () => {
                $printBill($form)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = $vendor() && $form.readPretty
            }`)
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          // 送货单据
          deliveryOrderInfo: {
            ...DeliveryOrderInfo
          },
          // 送货单明细
          deliveryOrderDetail: {
            ...DeliveryOrderDetail
          },
          // 附件
          fileInfo: {
            ...FileInfo
          }
        })
      }

    }
  },
  orderDetailsDialog: {
    type: 'void',
    title: $t('purchaseDemand.orderDetailSelect'),
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'large',
      footer: true,
      beforeClose: expression(`(done, type) => {
            $form.query('DeliveryNote').get('data').orderDetailInit = false
            if ( type === 'ok') {
              $setSelectedData ($form)
            }
            done()
      }`)
    },
    properties: {
      deliveryNotice: {
        type: 'void',
        'x-hidden': `{{$form.values.orderSource === 'PURCHASE_ORDER'}}`,
        'x-component': 'DeliveryNotice',
        'x-component-props': {
          init: expression('$form.query(\'DeliveryNote\').get(\'data\').orderDetailInit'),
          queryData: expression('$form.query(\'DeliveryNote\').get(\'data\').queryData'),
          '@getSelections': expression(`(selections) => {
            $form.query('DeliveryNote').get('data').selectedData = selections
          }`)
        }
      },
      purchaseOrder: {
        type: 'void',
        'x-component': 'PurchaseOrder',
        'x-hidden': `{{$form.values.orderSource === 'DELIVERY_NOTICE'}}`,
        'x-component-props': {
          init: expression('$form.query(\'DeliveryNote\').get(\'data\').orderDetailInit'),
          queryData: expression('$form.query(\'DeliveryNote\').get(\'data\').queryData'),
          '@getSelections': expression(`(selections) => {
            $form.query('DeliveryNote').get('data').selectedData = selections
          }`)
        }
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="DeliveryNoteDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
