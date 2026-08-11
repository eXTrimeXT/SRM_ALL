<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  i18nExpression,
  defineSchemas,
  expression,
  toJS
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
// @ts-ignore
import { parseTime } from '@/utils'

import { useAttrs } from 'vue-demi'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import OrderDetail from './components/collapseItem/orderDetail'
// @ts-ignore
import FileUploads from './components/collapseItem/fileUploads'
// @ts-ignore
import ContractInfor from '@/library/composition/orderManagementBuyer/contract-infor'

const { emitTabRemove, t: $t, app, http: $http } = usePageHelper()

let $attrs: any = useAttrs()

const $init = ($form: any) => {
  $form.values.orderChangeId = $attrs.params.row.orderChangeId
  return true
}

// 拒绝
const $reject = async ($form: any, $queryEngine: any, $message: any, $prompt: any, $bus: any) => {
  const prompt = await $prompt($t('orderMod.msgRufuseReason'), $t('common.tips'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    inputPattern: /\S{1,}/,
    inputErrorMessage: $t('orderMod.refuseReasonRequire')
  }).catch(() => false)

  if (!prompt) return

  let params = [{
    orderChangeId: $form.values.orderChangeId,
    rejectReason: prompt.value
  }]
  $queryEngine.request.baseRequest({
    'type': 'OrderChangeVendor',
    'lang': 'zh-cn',
    'payload': params,
    'action': 'refuse'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $closePageAndRefreshListPageData($bus)
  })
}
// 接受
const $accept = async ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OrderChangeVendor',
    'lang': 'zh-cn',
    'payload': [{ orderChangeId: $form.values.orderChangeId }],
    'action': 'accept'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $closePageAndRefreshListPageData($bus)
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  emitTabRemove($attrs.tabName)
  $bus.$emit('OrderChangeHead')
}

// 变更前合同信息
const $viewPreContract = async (row: any, $form: any) => {
  let data = $form.query('OrderChangeVendor').get('data')
  let url = '/api-sup-ce/po/orderchange/queryOriginContractMappingByOrderDetailId'
  let res = await $http({
    url: url,
    method: 'POST',
    data: { orderChangeDetailId: row.orderChangeDetailId },
    loading: true
  })

  data.contractView.row = row
  data.contractView.params = {
    orderContractMappingList: res.data.originOrderChangeContractMappingList,
    ...res.data
  }
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

// 变更后合同信息
const $viewAfterContract = async (row: any, $form: any) => {
  let data = $form.query('OrderChangeVendor').get('data')
  let url = '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId'
  let res = await $http({
    url: url,
    method: 'POST',
    data: { orderChangeDetailId: row.orderChangeDetailId },
    loading: true
  })

  data.contractView.row = row
  data.contractView.params = {
    orderContractMappingList: res.data.orderChangeContractMappingList,
    ...res.data
  }
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

const scope = {
  $attrs,
  parseTime,
  $t,
  app,
  $init,
  emitTabRemove,
  $viewPreContract,
  $viewAfterContract,
  $reject,
  $accept
}
const components = {
  FormCollapse,
  BaseInfo,
  OrderDetail,
  FileUploads,
  ContractInfor
}

const schema = defineSchemas({
  OrderChangeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      contractView: { // 查看合同
        row: {},
        params: {},
        vendor: true,
        title: $t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true
      }
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'vendorRead',
          ready: expression(`() => {
            return $init($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.orderChangeId || $form.values.orderChangeId]

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {

            const detailData = res.data[0]

            // 单纯文本只读状态
            $form.readPretty = true
            detailData.receiveAddressName = detailData.receiveAddress
            detailData.orderChangeDetails.sort((obj1, obj2) => obj1['lineNum'] - obj2['lineNum'])
            
            $form.setValues({
              ...detailData
            })
          }`),
          save: {
            // 标记当前 action 需要消费底层储存的级联删除数据
            cascadeDeletion: true
          }
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': $t('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
                emitTabRemove($attrs.tabName)
            }`)
          }
        },
        reject: {
          type: 'void',
          'x-hidden': '{{$attrs.params.flag === \'view\'}}',
          'x-content': i18nExpression('common.refused'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $reject($form, $queryEngine, $message,$prompt,$bus)
            }`)
          }
        },
        accept: {
          type: 'void',
          'x-hidden': '{{$attrs.params.flag === \'view\'}}',
          'x-content': i18nExpression('orderMod.accept'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $accept($form, $queryEngine, $message,$bus)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 单据基本信息
          baseInfo: {
            ...BaseInfo
          },
          // 订单明细
          orderDetail: {
            ...OrderDetail
          },
          // 附件
          fileUploads: {
            ...FileUploads
          }
        }
      }
    }
  },
  // 查看合同
  contractInforDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'OrderChangeVendor\').get(\'data\').contractView}}',
      '@close': expression(`() => {
        $form.query('.contractInforDialog').take().setComponentProps({
          visible: false
        })
      }`)
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="VendorPurchaseOrderChangeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
<style lang="scss">
.the-purchaseOrderChangeDetail-detail .high-light input {
  color: #F25353;
  font-weight: bold;
}
</style>
