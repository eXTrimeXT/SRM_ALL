<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression
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
import PayInfor from './components/collapseItem/payInfor'
// @ts-ignore
import OrderDetail from './components/collapseItem/orderDetail'
// @ts-ignore
import FileUploads from './components/collapseItem/fileUploads'
// @ts-ignore
import ChangeRecords from './components/collapseItem/changeRecords'
// @ts-ignore
import ContractInfor from '@/library/composition/orderManagementBuyer/contract-infor'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'

const { emitTabRemove, t: $t, app, http: $http } = usePageHelper()

let $attrs: any = useAttrs()

const $init = ($form: any) => {
  $form.values.orderId = $attrs.params.row.orderId
  $getOrderchangeRecordsData($form)
  return true
}

const $getPaymentTermsPage = async ($self: any) => {
  let { data } = await $http({
    url: '/api-cm/template/payType/paymentTermsPage',
    method: 'POST',
    data: { pageNum: 1, pageSize: 1000 },
    loading: true
  })

  let options = (data.list || []).map((item: any) => {
    return {
      label: item.payExplain,
      value: String(item.payTypeId)
    }
  })

  $self.data.paymentTermOptions = options
}

// 获取合同接口
const $getViewContractList = async (row: any) => {
  const { data } = await $http({
    url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
    method: 'POST',
    data: {
      orderDetailId: row.orderDetailId
    },
    loading: true
  })
  return data
}
// 查看合同
const $viewContract = async (row: any, $form: any) => {
  let data = $form.query('OrderVendor').get('data')
  const list = await $getViewContractList(row)
  data.contractView.row = row
  data.contractView.params = list
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

// 变更前合同信息
const $viewPreContract = async (row: any, $form: any) => {
  let data = $form.query('OrderVendor').get('data')
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
  let data = $form.query('OrderVendor').get('data')
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

// 获取订单变更记录
const $getOrderchangeRecordsData = ($form: any) => {
  const { orderchangeRecordsInitQuery, orderchangeRecordsPageNum: pageNum, orderchangeRecordsPageSize: pageSize } = $form.query('OrderVendor').get('data')
  $http({
    url: '/api-sup-ce/po/orderchange/getOrderChangeHistoryList',
    method: 'POST',
    data: { ...orderchangeRecordsInitQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.orderchangeRecords = res.data.list
        $form.query('OrderVendor').get('data').orderchangeRecordsTotal = res.data.total
      }
    })
}

const scope = {
  $attrs,
  parseTime,
  $t,
  app,
  $init,
  emitTabRemove,
  $getPaymentTermsPage,
  $viewContract,
  $viewPreContract,
  $viewAfterContract,
  $getOrderchangeRecordsData
}
const components = {
  FormCollapse,
  BaseInfo,
  PayInfor,
  OrderDetail,
  FileUploads,
  ChangeRecords,
  ContractInfor,
  CPagination
}

const schema = defineSchemas({
  OrderVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      paymentTermOptions: [], // 付款条件

      concatSelectRow: {}, // 明细行点击关联合同选择数据
      contractViewParams: {
        from: '',
        visible: false,
        row: {}
      },
      contractView: { // 查看合同
        row: {},
        params: {},
        title: $t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true,
        vendor: true
      },
      orderchangeRecordsInitQuery: { orderId: $attrs?.params?.row?.orderId },
      orderchangeRecordsPageNum: 1,
      orderchangeRecordsPageSize: 5,
      orderchangeRecordsTotal: 0

    },
    'x-reactions': expression(`async () => {
      $getPaymentTermsPage($self)
    }`),
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
            data.payload = [$attrs?.params?.row?.orderId || $form.values.orderId]

            data.query['*'] = {}

            return data
        }`),
          onSuccess: expression(`(res) => {

          const detailData = res.data[0]

          // 单纯文本只读状态
          $form.readPretty = true
          detailData.receiveAddressName = detailData.receiveAddress
          $form.setValues({
            ...detailData,
            orderchangeRecords: $form.values.orderchangeRecords
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
          'x-content': $t('common.close'),
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
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 单据基本信息
          baseInfo: {
            ...BaseInfo
          },
          // 付款信息
          payInfor: {
            ...PayInfor
          },
          // 订单明细
          orderDetail: {
            ...OrderDetail
          },
          // 附件
          fileUploads: {
            ...FileUploads
          },
          // 订单变更记录
          changeRecords: {
            ...ChangeRecords
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
      'contract-view': '{{$form.query(\'OrderVendor\').get(\'data\').contractView}}',
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
    schemaKey="VendorPurchaseOrderNewDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
