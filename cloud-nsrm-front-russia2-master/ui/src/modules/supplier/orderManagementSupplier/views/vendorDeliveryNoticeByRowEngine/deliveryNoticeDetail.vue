<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
// @ts-ignore
import DeliveryNoticeInfo from './components/collapseItem/deliveryNoticeInfo'
// @ts-ignore
import DeliveryNoticeDetail from './components/collapseItem/deliveryNoticeDetail'
// @ts-ignore
import FileInfo from './components/collapseItem/fileInfo'
// @ts-ignore
import purchaseOrderDetail from 'mods@/orderManagementSupplier/views/vendorPurchaseOrderEngine/vendorPurchaseOrderDetail'
// @ts-ignore
import SplitDialog, { split } from './components/dialog/splitDialog'
// @ts-ignore
import RefusedReasonDialog, { setRefusedReason } from './components/dialog/refusedReasonDialog'
// @ts-ignore
import { parseTime, getValidateFailureSequence } from '@/utils'

import { useAttrs } from 'vue-demi'

const { emitTabAdd, emitTabRemove, t: $t, http: $http } = usePageHelper()

const $attrs: any = useAttrs()

const $init = async ($form: any) => {
  let res = await $http({
    url: '/api-sup-ce/purchaseConfig/get/deliveryNotice',
    method: 'GET'
  })
  if (res.code === '0') {
    $form.query('DeliveryNoticeVendor').get('data').configValue = res.data.configValue
    $form.values.deliveryNoticeId = $attrs.params.row.deliveryNoticeId
    return true
  }
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryNoticeVendor')
  emitTabRemove($attrs.tabName)
}

// 拒绝/接受
const $rejectOrAccept = ($form: any, $self: any, $message: any, type: any) => {
  const rows = $self.query('detailList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }

  const sequence = getValidateFailureSequence(rows, 'lineNum', (row: any) => row.status !== 'WAITING_VENDOR_CONFIRM')
  if (sequence) {
    // 行号【${sequence}】行状态不为“待供方确认”，请检查！
    return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequence}】${$t('buyerDeliveryNotice.prompt10')}`)
  }
  if (type === 'REFUSE') {
    $form.query('DeliveryNoticeVendor').get('data').currentRows = rows
    $form.query('*.refusedReasonDialog').take().setComponentProps({ visible: true })
    setTimeout(() => {
      $form.query('*.refusedReasonDialog.form').take().reset()
    })
  } else {
    $rejectOrAcceptHandle(rows, $self, type)
  }
}

const $rejectOrAcceptHandle = (rows: any, $form: any, type: any, refusedReason?:any) => {
  rows.forEach((item: any) => {
    item.refusedReason = refusedReason || ''
    item.status = type
    item.confirmNum = type === 'ACCEPT' ? item.noticeSum : 0
    $form.query('detailList').take((field: any) => {
      const vxeTable = field.invoke('getVxeTableInstance')
      const { tableData } = vxeTable.getTableData()
      vxeTable.setCheckboxRow([tableData[item.lineNum - 1]], false)
    })
  })
}

// 暂存
const $save = ($form: any, $queryEngine: any, $message: any) => {
  let params = $form.values.detailList.map((item: any) => {
    return {
      parentLineId: item.parentLineId, confirmNum: item.confirmNum, promiseReceiveDate: item.promiseReceiveDate ? parseTime(item.promiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true) : null, refusedReason: item.refusedReason
    }
  })
  $queryEngine.request.baseRequest({
    type: 'DeliveryNoticeDetailVendor',
    action: 'supplierSave',
    loading: true,
    payload: params
  }).then((res: any) => {
    $message.success($t('common.successSave'))
    $queryEngine.request.read()
  })
}

const $computational = (list: any) => {
  let flag = false
  let sequences = 0
  let sequencesBetween = null
  for (let i = 0; i < list.length; i++) {
    sequences += list[i].length
    sequencesBetween = list[i].length === 1 ? sequences : (String(sequences - list[i].length + 1) + '-' + sequences)
    let total = list[i].reduce((prev: any, cur: any) => prev + Number(cur.confirmNum || 0), 0)

    if (total !== list[i][0].noticeSum) {
      // 允许拆行时累计供方确认通知数量不等于本次通知送货数量
      flag = true
      break
    }
  }
  return { flag, sequencesBetween }
}

const $confirm = ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $form.validate().then(() => {
    const detailList = $form.values.detailList

    const { vendorSplitReply } = $form.query('DeliveryNoticeVendor').get('data').configValue

    if (vendorSplitReply) {
      // 允许拆行时校验
      let map = new Map()
      let detailListNew = []
      detailList.forEach((item: any) => {
        map.has(item.parentLineNum) ? map.get(item.parentLineNum).push(item) : map.set(item.parentLineNum, [item])
      })
      detailListNew = [...map.values()]

      // 允许拆行时累计供方确认通知数量不等于本次通知送货数量
      const { flag, sequencesBetween } = $computational(detailListNew)
      if (flag) {
        // 行号【x】累计供方确认通知数量不等于本次通知送货数量，请检查！
        return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('buyerDeliveryNotice.prompt8')}`)
      }
    } else {
      const sequence = getValidateFailureSequence(detailList, 'lineNum', (row: any) => row.status === 'WAITING_VENDOR_CONFIRM')
      if (sequence) {
        // 行号【${sequence}】行状态为“待供方确认”，请检查！
        return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequence}】${$t('buyerDeliveryNotice.prompt9')}`)
      }
    }

    const params = detailList.map((item: any) => {
      return {
        parentLineId: item.parentLineId, confirmNum: item.confirmNum, promiseReceiveDate: parseTime(item.promiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true), refusedReason: item.refusedReason
      }
    })
    $confirmFetch($queryEngine, $message, params, $bus)
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

const $confirmFetch = ($queryEngine: any, $message: any, params: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    type: 'DeliveryNoticeDetailVendor',
    payload: params,
    loading: true,
    action: 'supplierConfirm'
  }).then((res: any) => {
    $message.success($t('common.successSubmit'))
    $closePageAndRefreshListPageData($bus)
  })
}

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

const $opentSplitDialog = (row: any, $form: any) => {
  $form.query('DeliveryNoticeVendor').get('data').currentOrderRow = row
  $form.query('*.splitDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.splitDialog.form').take().reset()
  })
}

const scope = {
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $readOrder,
  $rejectOrAccept,
  $save,
  $confirm,
  $opentSplitDialog,
  $split: split,
  $init,
  $setRefusedReason: setRefusedReason,
  $rejectOrAcceptHandle
}

const components = {
  DeliveryNoticeInfo,
  DeliveryNoticeDetail,
  FileInfo,
  FileDynamic,
  SplitDialog,
  RefusedReasonDialog
}

const schema = defineSchemas({
  // 基本信息
  DeliveryNoticeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-component-props': {
      class: 'the-deliveryNoticeDetail-detail'
    },
    'x-data': {
      configValue: {
        vendorSplitReply: false // 允许拆行
      },
      currentOrderRow: null,
      currentRows: []
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            return $init($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs.params.row.deliveryNoticeId || $form.values.deliveryNoticeId || '']

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
          $form.readPretty = $attrs.params.flag === 'view'

          const detailData = res.data[0]
          
          detailData.detailList.sort((obj1, obj2) => obj1['lineNum'] - obj2['lineNum'])
          const { vendorSplitReply } = $form.query('DeliveryNoticeVendor').get('data').configValue
          
          let i = null
          detailData.detailList.forEach(item =>{
            if(!item.parentLineNum){
              Object.assign(item, {
                parentLineId: item.deliveryNoticeDetailId, // 设置父id
                parentLineNum: item.lineNum, // 设置父行号
                isParentLine: true,  // 是否父行
              })
              if(vendorSplitReply){
                item.confirmNum = item.confirmNum || item.confirmNum === 0 ? item.confirmNum : item.noticeSum // 设置确认通知数量
              }
            }else{
              item.isParentLine = false // 是否父行
              if(!i || item.parentLineNum !== i.parentLineNum){
                item.isParentLine = true  
                i = item
              }
            }

            if(!vendorSplitReply){
              item.status = item.confirmNum === item.noticeSum ? 'ACCEPT' : (item.confirmNum === 0 ? 'REFUSE' : item.status)
            } 
          })

            $form.setValues({
              ...detailData,
              deliveryNoticeId: $attrs.params.row.deliveryNoticeId
            })

            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
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
      },
      // 拆分弹框
      splitDialog: {
        ...SplitDialog
      },
      // 拒绝原因
      refusedReasonDialog: {
        ...RefusedReasonDialog
      }

    },

    items: {
      type: 'void',
      properties: {
        buttonList: {
          type: 'void',
          'x-component': 'ButtonList',
          properties: {
            cancel: {
              type: 'void',
              title: i18nExpression('common.close'),
              'x-component-props': {
                '@click': expression(`()=> {
                  emitTabRemove($attrs.tabName)
                }`)
              }
            },
            save: {
              type: 'void',
              'x-hidden': '{{$form.readPretty}}',
              title: i18nExpression('common.staging'), // 暂存
              'x-component-props': {
                type: 'default',
                '@click': expression(`() => {
                  $save($form, $queryEngine, $message)
                }`)
              }
            },
            submit: {
              type: 'void',
              'x-hidden': '{{$form.readPretty}}',
              title: i18nExpression('common.submit'), // 提交
              'x-component-props': {
                type: 'primary',
                '@click': expression(`() => {
                  $confirm($form, $queryEngine, $message, $bus)
                }`)
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
  <RenderEngine
    schemaKey="VendorDeliveryNoticeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
<style lang="scss">
.the-deliveryNoticeDetail-detail {

  .high-light input,
  .high-light {
    color: #F25353;
    font-weight: bold;
  }
}

.the-splitDialog .render-pix-form-item-control-content {
  margin-bottom: 5px !important;
}
</style>
