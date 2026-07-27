<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import DeliveryNoticeInfo from './components/collapseItem/deliveryNoticeInfo'
import DeliveryNoticeDetail from './components/collapseItem/deliveryNoticeDetail'
import FileInfo from './components/collapseItem/fileInfo'
// @ts-ignore
import PurchaseOrder from './components/dialog/purchaseOrder'
// @ts-ignore
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrderNewEngine/purchaseOrderDetail'
// @ts-ignore
import { parseTime } from '@/utils'

import { useAttrs } from 'vue-demi'

const { emitTabAdd, emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryNotice')
  $bus.$emit('DeliveryNoticeDetailBuyer')
  emitTabRemove($attrs.tabName)
}

// 打开采购订单弹窗
const $openDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId) {
    // 请先选择业务实体、库存组织、供应商！
    return $message.warning($t('orderMod.selectInputValue'))
  }
  $form.query('purchaseOrderDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('DeliveryNotice').get('data').queryData = {
      orgId: $form.values.orgId,
      organizationId: $form.values.organizationId,
      vendorId: $form.values.vendorId
    }

    $form.query('DeliveryNotice').get('data').dialogInit = true
  })
}

// 设置送货通知单数据
const $setSelectedData = ($form: any, $message: any) => {
  const ids = $form.values.detailList.map((item: any) => item.orderDetailId)
  let isRepeat = false

  $form.query('DeliveryNotice').get('data').selectedData.forEach((item: any) => {
    if (!ids.includes(item.orderDetailId)) {
      item.remainingNoticeQuantity = item.remainingDeliveryNoticeQuantity
      $form.query('detailList').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    $message.warning($t('buyerDeliveryNotice.prompt1')) // 新增订单明细存在重复数据，已去除
  }
}

// 暂存/提交
const $saveData = ($form: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  const form = toJS($form.values)
  form.status = type

  form.detailList.forEach((item: any) => {
    item.receiveDate = item.receiveDate ? parseTime(item.receiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true) : ''
  })

  if (type === 'DRAFT') {
    saveFetch($form, form, $queryEngine, $message, $bus, type)
  } else {
    $form.validate().then(() => {
      if (form.detailList.length === 0) {
        $message.warning($t('buyerDeliveryNotice.prompt2')) // 请选择订单明细
      } else {
        saveFetch($form, form, $queryEngine, $message, $bus, type)
      }
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const saveFetch = ($form: any, params: any, $queryEngine: any, $message: any, $bus: any, type: any) => {
  $queryEngine.request.save(params, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      if (type === 'DRAFT') {
        $bus.$emit('DeliveryNotice')
        $bus.$emit('DeliveryNoticeDetailBuyer')
        $form.values.deliveryNoticeId = res.originalData.records[0] || ''
        $queryEngine.request.read()
      } else {
        $closePageAndRefreshListPageData($bus)
      }
    }
  })
}

// 跳转采购订单
const $readOrder = (row: any) => {
  emitTabAdd({
    component: purchaseOrderDetail,
    params: {
      flag: 'view',
      row,
      tabName: 'purchaseOrderDetail' + row.orderNumber
    },
    title: row.orderNumber,
    name: 'purchaseOrderDetail' + row.orderNumber
  })
}

const scope = {
  emitTabRemove,
  $attrs,
  $t,
  $closePageAndRefreshListPageData,
  $openDialog,
  $setSelectedData,
  $saveData,
  $readOrder
}

const components = {
  DeliveryNoticeInfo,
  DeliveryNoticeDetail,
  FileInfo,
  PurchaseOrder,
  FileDynamic
}

const schema = defineSchemas({
  // 基本信息
  DeliveryNotice: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      dialogInit: false,
      queryData: {},
      selectedData: []
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return $attrs.params.row.deliveryNoticeId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.deliveryNoticeId || $form.values.deliveryNoticeId || '']

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.setValues({
              ...res.data[0]
            })

            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
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
          'x-content': i18nExpression('common.close'), // 关闭
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
          'x-content': i18nExpression('common.staging'), // 暂存
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus, 'DRAFT')
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-hidden': '{{$form.readPretty}}',
          'x-content': i18nExpression('common.submit'), // 提交
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(` () => {
              $saveData($form, $queryEngine, $message, $bus, 'WAITING_VENDOR_CONFIRM')
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
          // 送货单据
          deliveryNoticeInfo: {
            ...DeliveryNoticeInfo
          },
          // 送货单明细
          deliveryNoticeDetail: {
            ...DeliveryNoticeDetail
          },
          // 附件
          fileInfo: {
            ...FileInfo
          }
        })
      }

    }
  },
  // 订单明细选择
  purchaseOrderDialog: {
    type: 'void',
    title: $t('route.buyerPurchaseOrder'),
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'large',
      footer: true,
      beforeClose: expression(`(done, type) => {
        $form.query('DeliveryNotice').get('data').dialogInit = false
        if ( type === 'ok') {
          $setSelectedData ($form, $message)  
        }
        done()
      }`)
    },
    properties: {
      purchaseOrder: {
        type: 'void',
        'x-component': 'PurchaseOrder',
        'x-component-props': {
          init: expression('$form.query(\'DeliveryNotice\').get(\'data\').dialogInit'),
          queryData: expression('$form.query(\'DeliveryNotice\').get(\'data\').queryData'),
          '@getSelections': expression(`(selections) => {
              $form.query('DeliveryNotice').get('data').selectedData = selections
          }`)
        }
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="BuyerDeliveryNoticeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
