<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  observer
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
// @ts-ignore
import { parseTime, getValidateFailureSequence, currying } from '@/utils'
import { useAttrs } from 'vue-demi'
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
import BatchSetDialog, { batchSet } from './components/dialog/batchSetDialog'
// @ts-ignore
import SplitDialog, { split } from './components/dialog/splitDialog'
// @ts-ignore
import ContractInfor from '@/library/composition/orderManagementBuyer/contract-infor'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'
// @ts-ignore
import { orderConfig } from '@/config/orderConfig'
// @ts-ignore
import { getToken } from '@/utils/auth'
// @ts-ignore
import axios from 'axios'
// @ts-ignore
import { getMenuInfo } from '@/utils/menu-auth'
// @ts-ignore
import { sysPrefix } from '@/config/ipConfig'

const { emitTabRemove, t: $t, app, http: $http } = usePageHelper()

let $attrs: any = useAttrs()

const $init = async ($form: any) => {
  let res = await $http({
    url: '/api-sup-ce/purchaseConfig/get/order',
    method: 'GET'
  })
  if (res.code === '0') {
    $form.query('OrderVendor').get('data').configValue = res.data.configValue
    $form.values.orderId = $attrs.params.row.orderId
    $getOrderchangeRecordsData($form)
    return true
  }
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

// 批量维护
const $openBatchSetDialog = ($self: any, $form: any, $message: any) => {
  const rows = $self.query('detailList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (!rows.length) {
    return $message.error($t('contractMod.msgSelData'))
  }

  $form.query('OrderVendor').get('data').batchSetIds = rows.map((item: any) => item.lineNum)
  $form.query('*.batchSetDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.batchSetDialog.form').take().reset()
  })
}

// 暂存
const $save = ($form: any, $queryEngine: any, $message: any) => {
  let params = $form.values.detailList.map((item: any) => {
    return {
      parentLineId: item.parentLineId, confirmNum: item.confirmNum, ceeaPromiseReceiveDate: item.ceeaPromiseReceiveDate ? parseTime(item.ceeaPromiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true) : null, refusedReason: item.refusedReason
    }
  })
  $queryEngine.request.baseRequest({
    type: 'OrderDetailVendor',
    loading: true,
    payload: params,
    action: 'supplierSave'
  }).then((res: any) => {
    $message.success($t('common.successSave'))
    $queryEngine.request.read()
  })
}

const $computational = (list: any, type: any) => {
  let flag = false
  let sequences = 0
  let sequencesBetween = null
  for (let i = 0; i < list.length; i++) {
    sequences += list[i].length
    sequencesBetween = list[i].length === 1 ? sequences : (String(sequences - list[i].length + 1) + '-' + sequences)
    let total = list[i].reduce((prev: any, cur: any) => prev + Number(cur.confirmNum || 0), 0)

    if (type === 1 && total !== list[i][0].orderNum && total !== 0) {
      // 非生产性需求
      flag = true
      break
    } else if (type === 2 && total > list[i][0].orderNum) {
      // 允许供应商更改订单数量和拒绝订单时-累计确认订单数量大于订单数量
      flag = true
      break
    } else if (type === 3 && total < 0) {
      // 允许供应商更改订单数量和拒绝订单时-累计确认订单数量小于0
      flag = true
      break
    } else if (type === 4 && total <= 0) {
      // 允许供应商更改订单数量时-累计确认订单数量小于等于0
      flag = true
      break
    } else if (type === 5 && total !== list[i][0].orderNum) {
      // 不允许供应商更改订单数量和拒绝订单时-累计确认订单数量不等于订单数量
      flag = true
      break
    } else if (type === 6 && list[i][0].ladderPriceFlag === 'Y' && !(list[i][0].ladderPrices.find((item: any) => {
      if (item.endQuantity) {
        return total >= item.beginQuantity && total < item.endQuantity
      } else {
        return total >= item.beginQuantity
      }
    }))) {
      // 校验阶梯价
      flag = true
      break
    }
  }
  return { flag, sequencesBetween }
}

const $confirm = ($form: any, $queryEngine: any, $message: any, $bus: any) => {
  $form.validate().then(() => {
    const detailList = $form.values.detailList

    const { vendorRefuse, vendorModifyCount, vendorSplitReply } = $form.query('OrderVendor').get('data').configValue

    if (vendorSplitReply) {
      // 允许拆行时校验
      let map = new Map()
      let detailListNew = []
      detailList.forEach((item: any) => {
        map.has(item.parentLineNum) ? map.get(item.parentLineNum).push(item) : map.set(item.parentLineNum, [item])
      })
      detailListNew = [...map.values()]

      if ($form.values.demandType === 'NONPRODUCTIVE_DEMAND') {
        // 非生产性需求
        const { flag, sequencesBetween } = $computational(detailListNew, 1)
        if (flag) {
          // `行号【${sequencesBetween}】累计供方确认订单数量必须等于订单数量，若更改只可改为0 ，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt25')}`)
        }
      } else if (vendorRefuse && vendorModifyCount) {
        // 允许供应商更改订单数量和拒绝订单时
        const { flag, sequencesBetween } = $computational(detailListNew, 2)
        if (flag) {
          // 行号【${sequencesBetween}】累计供方确认订单数量大于订单数量，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt19')}`)
        }

        const { flag: flagNew, sequencesBetween: sequencesBetweenNew } = $computational(detailListNew, 3)
        if (flagNew) {
          // 行号【${sequencesBetween}】累计供方确认订单数量不能小于0，请检查
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetweenNew}】${$t('purchaseOrder.prompt26')}`)
        }
      } else if (vendorModifyCount) {
        // 允许供应商更改订单数量时
        const { flag, sequencesBetween } = $computational(detailListNew, 2)
        if (flag) {
          // 行号【${sequencesBetween}】累计供方确认订单数量大于订单数量，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt19')}`)
        }

        const { flag: flagNew, sequencesBetween: sequencesBetweenNew } = $computational(detailListNew, 4)
        if (flagNew) {
          // 行号【${sequencesBetween}】累计供方确认订单数量必须大于0，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetweenNew}】${$t('purchaseOrder.prompt27')}`)
        }
      } else if (vendorRefuse) {
        // 允许供应商拒绝时
        const { flag, sequencesBetween } = $computational(detailListNew, 1)
        if (flag) {
          // `行号【${sequencesBetween}】累计供方确认订单数量必须等于订单数量，若更改只可改为0 ，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt25')}`)
        }
      } else if (!vendorRefuse && !vendorModifyCount) {
        // 不允许供应商更改订单数量和拒绝订单时
        const { flag, sequencesBetween } = $computational(detailListNew, 5)
        if (flag) {
          // 行号【${sequencesBetween}】累计供方确认订单数量不等于订单数量，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt21')}`)
        }
      } else {
        const { flag, sequencesBetween } = $computational(detailListNew, 6)
        if (flag) {
          // 行号【${sequencesBetween}】累计供方确认订单数量无对应区间阶梯价，请修改！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrder.prompt20')}！`)
        }
      }
    } else {
      // 不允许拆行时校验
      const fn = currying(getValidateFailureSequence)(detailList, 'lineNum')
      let sequences = null
      if ($form.values.demandType === 'NONPRODUCTIVE_DEMAND') {
        // 非生产性需求
        sequences = fn((row: any) => row.confirmNum !== row.orderNum && row.confirmNum !== 0)
        if (sequences) {
          // `行号【${sequences}】供方确认订单数量若更改只可改为0，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt9')}`)
        }
      } else if (vendorRefuse && vendorModifyCount) {
        // 允许供应商更改订单数量和拒绝订单时
        sequences = fn((row: any) => row.confirmNum > row.orderNum)
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量大于订单数量，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt10')}`)
        }

        sequences = fn((row: any) => row.confirmNum < 0)
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量不能小于0，请检查
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt11')}`)
        }
      } else if (vendorModifyCount) {
        // 允许供应商更改订单数量时
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量大于订单数量，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt12')}`)
        }

        sequences = fn((row: any) => row.confirmNum <= 0)
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量必须大于0，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt13')}`)
        }
      } else if (vendorRefuse) {
        // 允许供应商拒绝时
        sequences = fn((row: any) => row.confirmNum !== row.orderNum && row.confirmNum !== 0)
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量若更改只可改为0，请检查！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt14')}！`)
        }
      } else {
        sequences = fn((row: any) => row.ladderPriceFlag === 'Y' && !(row.ladderPrices.find((item: any) => {
          if (item.endQuantity) {
            return row.orderNum >= item.beginQuantity && row.orderNum < item.endQuantity
          } else {
            return row.orderNum >= item.beginQuantity
          }
        })))
        if (sequences) {
          // 行号【${sequences}】供方确认订单数量无对应区间阶梯价，请修改！
          return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequences}】${$t('purchaseOrder.prompt18')}！`)
        }
      }
    }

    const params = detailList.map((item: any) => {
      return {
        parentLineId: item.parentLineId, confirmNum: item.confirmNum, ceeaPromiseReceiveDate: parseTime(item.ceeaPromiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true), refusedReason: item.refusedReason
      }
    })
    $confirmFetch($queryEngine, $message, params, $bus)
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

const $confirmFetch = ($queryEngine: any, $message: any, params: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    type: 'OrderDetailVendor',
    payload: params,
    action: 'supplierConfirm'
  }).then((res: any) => {
    $message.success($t('common.successSubmit'))
    $closePageAndRefreshListPageData($bus)
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('OrderVendor')
  emitTabRemove($attrs.tabName)
}

const $exportExcel = ($form: any, $message: any) => {
  let query: any = { orderDetailId: {} }
  let titleList: any = []
  let languageList: any = []
  $form.query('detailList').take((fields: any) => {
    fields.data.columns.forEach((item: any) => {
      if (!['contractInfor', 'operation'].includes(item.field)) {
        query[item.field] = {}
        titleList.push(item.field)
        // 这2个属性导入时必填，但界面不用编辑
        if (item.required || ['lineNum', 'materialCode'].includes(item.field)) {
          languageList.push('*' + item.title)
        } else {
          languageList.push(item.title)
        }
      }
    })
  })
  let menuInfo = getMenuInfo()
  axios({
    url: `${sysPrefix()}/api-file/common-export/exportExcel`,
    method: 'POST',
    timeout: 350000,
    headers: {
      Authorization: 'Bearer ' + getToken(),
      'X-Fun-Info': menuInfo.secretKey
    },
    responseType: 'arraybuffer',
    data: {
      'queryParam': {
        'pageSize': 2000,
        'showErrMsg': true,
        'meiqlPayload': {
          'type': 'OrderDetailVendor',
          'lang': 'zh-cn',
          'query': query,
          'action': 'export',
          'payload': {
            'filter': {
              'orderId': {
                'eq': $attrs.params.row.orderId
              }
            },
            'page': {
              'sort': 'lineNum asc',
              'pageNum': 1,
              'pageSize': 15
            }
          }
        }
      },
      'fileName': $t('purchaseOrder.downloadName1'),
      'titleList': titleList,
      'languageList': languageList,
      'dictCodes': '{"ladderPriceFlag":"YES_OR_NO","orderDetailStatus":"OrderDetailStatus"}',
      // eslint-disable-next-line no-template-curly-in-string
      'url': '${srm}/cloud-srm/api-sup-ce/api-ql/OrderDetailVendor/export',
      'permissionName': $t('purchaseOrder.downloadName1')
    }
  }).then(response => {
    const { data } = response
    if (response.headers['content-type'].startsWith('application/json')) {
      let enc = new TextDecoder('utf-8')
      let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
      throw new Error(res.message)
    }
    const blob = new Blob([data])
    const disposition = response.headers['content-disposition'] || ''
    const filename = decodeURIComponent(disposition.split('=')[1])
    const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
    let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
    dom.style.display = 'none'
    dom.href = url
    dom.rel = 'noopener'
    // 采购订单明细维护.xlsx
    dom.setAttribute('download', `${$t('purchaseOrder.downloadName1')}.xlsx` || filename) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
    document.body.appendChild(dom)
    dom.click()
  })
    .catch(error => {
      console.log(error)
      $message({ type: 'error', message: error.message })
    })
}

const $opentSplitDialog = (row: any, $form: any) => {
  $form.query('OrderVendor').get('data').currentOrderRow = row
  $form.query('*.splitDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.splitDialog.form').take().reset()
  })
}

// 获取阶梯价列表
const $getLadderPrices = ($form: any, $queryEngine: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OrderLadderPrice',
    'lang': 'zh-cn',
    'service': 'sup-ce',
    'query': {
      '*': {}
    },
    'payload': { filter: { orderId: { 'eq': $attrs?.params?.row?.orderId } } },
    'action': 'query'
  }).then((res: any) => {
    $form.values.detailList.forEach((i: any) => {
      if (i.ladderPriceFlag === 'Y') {
        let ladderPrices: any = []
        res.data.forEach((j: any) => {
          if (i.orderDetailId === j.orderDetailId) {
            ladderPrices.push(j)
          }
        })
        i.ladderPrices = ladderPrices.sort(function (x: any, y: any) {
          return (x.beginQuantity - y.beginQuantity)
        })
      }
    })
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
  $getOrderchangeRecordsData,
  $openBatchSetDialog,
  $batchSet: batchSet,
  $save,
  $confirm,
  orderConfig,
  $exportExcel,
  observer,
  $getLadderPrices,
  $opentSplitDialog,
  $split: split
}
const components = {
  FormCollapse,
  BaseInfo,
  PayInfor,
  OrderDetail,
  FileUploads,
  ChangeRecords,
  ContractInfor,
  CPagination,
  BatchSetDialog,
  SplitDialog
}

const schema = defineSchemas({
  OrderVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-component-props': {
      class: 'the-vendorPurchaseOrderDetail-detail'
    },
    'x-data': {
      configValue: {
        vendorRefuse: null, // 允许供应商拒绝采购订单
        vendorModifyCount: null, // 允许供应商修改订单数量（仅针对生产性需求）
        vendorSplitReply: null // 允许供应商分批次确认交期
      },
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
      orderchangeRecordsTotal: 0,

      batchSetIds: [],
      currentOrderRow: null

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
          // 单纯文本只读状态
          $form.readPretty = $attrs.params.flag === 'view'

          const detailData = res.data[0]
          detailData.receiveAddressName = detailData.receiveAddress
          detailData.detailList.sort((obj1, obj2) => obj1['lineNum'] - obj2['lineNum'])
          const { vendorSplitReply } = $form.query('OrderVendor').get('data').configValue

          let i = null
          detailData.detailList.forEach(item =>{
            if(!vendorSplitReply || !item.parentLineNum){
              item.confirmNum = item.confirmNum || item.confirmNum === 0 ? item.confirmNum : item.orderNum // 设置确认订单数量
            } 
            if(!item.parentLineNum){
              Object.assign(item, {
                parentLineId: item.orderDetailId, // 设置父id
                parentLineNum: item.lineNum, // 设置父行号
                isParentLine: true  // 是否父行
              });
            }else{
              item.isParentLine = false // 是否父行
              if(!i || item.parentLineNum !== i.parentLineNum){
                item.isParentLine = true  
                i = item
              }
            }
          })

          $form.setValues({
            ...detailData,
            orderchangeRecords: $form.values.orderchangeRecords
          })
          $getLadderPrices($form, $queryEngineConfig)
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
        },
        save: {
          type: 'void',
          'x-hidden': '{{$form.readPretty}}',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
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
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(` () => {
              $confirm($form, $queryEngine, $message, $bus)
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
      },
      // 分配/转办弹框
      batchSetDialog: {
        ...BatchSetDialog
      },
      // 拆分弹框
      splitDialog: {
        ...SplitDialog
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
<style lang="scss">
.the-vendorPurchaseOrderDetail-detail {

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
