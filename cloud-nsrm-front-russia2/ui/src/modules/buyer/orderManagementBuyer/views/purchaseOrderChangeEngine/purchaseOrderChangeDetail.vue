<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
// @ts-ignore
import { parseTime, deepClone } from '@/utils'

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
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'

const { emitTabRemove, t: $t, app, http: $http } = usePageHelper()

let $attrs: any = useAttrs()

const $setButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

    componentInstance.buttonConfigInfo.save.view = !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = !$form.readPretty
    componentInstance.buttonConfigInfo.cancel.view = !$form.readPretty
    componentInstance.buttonConfigInfo.close.view = $form.readPretty
    componentInstance.setWorkflowBusinessId($form.values.orderChangeId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const $init = ($form: any, $queryEngineConfig: any) => {
  // 设置审批流按钮
  $setButtonConfig($form)

  $form.values.orderChangeId = $attrs.params.row.orderChangeId
  return true
}

const $computational = (list: any) => {
  let flag = false
  let sequences = 0
  let sequencesBetween = null
  for (let i = 0; i < list.length; i++) {
    sequences += list[i].length
    sequencesBetween = list[i].length === 1 ? sequences : (String(sequences - list[i].length + 1) + '-' + sequences)
    let total = list[i].reduce((prev: any, cur: any) => prev + Number(cur.orderNum || 0), 0)

    if (list[i][0].ladderPriceFlag === 'Y' && !(list[i][0].ladderPrices.find((item: any) => {
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

// 保存
const $saveBill = (type: string, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  let values: any = deepClone($form.values)
  values.orderChangeDetails.forEach((item: any) => {
    item.planReceiveDate = item.planReceiveDate ? parseTime(item.planReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}', true) : ''
  })

  if (type === 'SAVE') {
    $submitData('save', values, $form, $queryEngine, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      let map = new Map()
      let orderChangeDetailsNew = []
      values.orderChangeDetails.forEach((item: any) => {
        map.has(item.parentLineNum) ? map.get(item.parentLineNum).push(item) : map.set(item.parentLineNum, [item])
      })
      orderChangeDetailsNew = [...map.values()]

      const { flag, sequencesBetween } = $computational(orderChangeDetailsNew)
      if (flag) {
        // 行号【${sequencesBetween}】累计变更后数量无对应区间阶梯价，请修改！
        return $message.warning(`${$t('purchaseOrder.prompt8')}【${sequencesBetween}】${$t('purchaseOrderChange.prompt2')}！`)
      }
      $submitData('submit', values, $form, $queryEngine, $message, $bus)
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const $submitData = (type: any, values: any, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OrderChange',
    'lang': 'zh-cn',
    'payload': [values],
    'action': type,
    'loading': true
  }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $form.values.orderChangeId = res.originalData?.records[0] || ''
      $queryEngine.request.read()
      $bus.$emit('OrderChangeHead')
      if (type === 'submit') {
        $cancel($form, $bus)
      }
    }
  })
}

const $cancel = ($form: any, $bus: any) => {
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  if (!['None', 'Push'].includes(componentInstance?.workflowParamsInfo?.integrationMode)) {
    $attrs.params.flag = 'approvalOnly'
    componentInstance.setWorkflowBusinessId($form.values.orderChangeId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
    componentInstance.handlerAfter('SUBMIT', () => {
      $closePageAndRefreshListPageData($bus)
    })
  } else {
    $closePageAndRefreshListPageData($bus)
  }
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  emitTabRemove($attrs.tabName)
  $bus.$emit('OrderChangeHead')
}

// 关联合同
const $concatContract = async (row: any, $form: any) => {
  let data = $form.query('OrderChange').get('data')
  let res = await $http({
    url: '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId',
    method: 'POST',
    data: { orderChangeDetailId: row.orderChangeDetailId },
    loading: true
  })

  data.contractConcat.row = row
  data.contractConcat.params = {
    orderContractMappingList: res.data.orderChangeContractMappingList,
    ...res.data
  }
  $form.query('.contractConcatDialog').take().setComponentProps({
    visible: true
  })
}

// 获取新增合同列表
const $getContractList = async (params: any, list: any, $form: any) => {
  const { data } = await $http({
    url: '/api-sup-ce/po/orderchange/listContractMaterialByOrderChangeDetail',
    method: 'POST',
    data: {
      'materialId': list.contractConcat.row.materialId,
      'orderChangeDetailId': list.contractConcat.row.orderChangeDetailId,
      'orgId': $form.values.orgId,
      'organizationId': $form.values.organizationId,
      'receiveAddress': $form.values.receiveAddress,
      'vendorId': $form.values.vendorId,
      ...params
    },
    loading: true
  })
  return data
}

// 新增合同
const $addContract = async ($form: any) => {
  $form.query('.contractSelectDialog').take().setComponentProps({
    visible: true
  })
  let data = $form.query('OrderChange').get('data')
  const list = await $getContractList({}, data, $form)
  data.contractSelectView.row = data.contractConcat.row
  data.contractSelectView.params = {
    orderContractMappingList: list.orderChangeContractMappingList,
    ...list
  }
}

// 确认选择合同
const $confirmSelectContract = ($form: any) => {
  let data = $form.query('OrderChange').get('data')
  // 请先选择需关联合同行！
  if (data.selectedContract.length < 1) return app.$message.warning($t('orderMod.selectNeedConcatRow'))

  const ids = data.contractConcat.params.orderContractMappingList.map((item: any) => item.contractMaterialId)
  data.selectedContract.forEach((row: any) => {
    if (!ids.includes(row.contractMaterialId)) {
      // 将值设置给关联对象
      data.contractConcat.params.orderContractMappingList.unshift(row)
    }
  })
  // 新增之后清除数据
  data.selectedContract = []
  $form.query('.contractSelectDialog').take().setComponentProps({
    visible: false
  })
}

// 新增合同-合同搜索
const $searchContractData = async (obj: any, $form: any) => {
  let data = $form.query('OrderChange').get('data')
  const list = await $getContractList(obj, data, $form)
  data.contractSelectView.params = {
    orderContractMappingList: list.orderChangeContractMappingList,
    ...list
  }
}

const $isConfirmConcat = (data: any) => {
  let isEmptyArr: any[] = []
  let isCheckNumberArr: any[] = []
  data.contractConcat.params.orderContractMappingList.forEach((item: any, index: any) => {
    if (item.isFrameworkAgreement === 'N') { //  非框架协议的合同
      if (!item.correlatedQuantity) { // 关联数量不能为空且必须大于0
        isEmptyArr.push(index + 1)
      } else if (item.correlatedQuantity > item.unusedContractQuantity) { // 关联数量不能大于未关联数量
        isCheckNumberArr.push(index + 1)
      }
    }
  })
  if (isEmptyArr.length) {
    app.$message.warning(`${$t('orderMod.chapter')}${isEmptyArr.join(',')}${$t('orderMod.row')}：关联数量不能为空且必须大于0,请检查`)
    return false
  } else if (isCheckNumberArr.length) {
    app.$message.warning(`${$t('orderMod.chapter')}${isCheckNumberArr.join(',')}${$t('orderMod.row')}：${$t('orderMod.checkConcatNum')}`)
    return false
  }
  return true
}

// 确定关联合同
const $confirmConcat = async ($form: any) => {
  let data = $form.query('OrderChange').get('data')
  if (!$isConfirmConcat(data)) return false

  await $http({
    url: '/api-sup-ce/po/orderchange/saveContractMapping',
    method: 'POST',
    data: {
      orderNum: data.contractConcat.row.orderNum,
      orderChangeId: $form.values.orderChangeId,
      orderChangeDetailId: data.contractConcat.row.orderChangeDetailId,
      orderChangeContractMappingList: data.contractConcat.params.orderContractMappingList
    },
    loading: true
  })

  let num = data.contractConcat.params.orderContractMappingList.reduce((r: any, c: any) => {
    if (c.isFrameworkAgreement === 'N') {
      return Number(r) + Number(c.correlatedQuantity)
    } else {
      return Number(r)
    }
  }, 0)
  // 设置已关联合同数量
  data.contractConcat.row.usedContractQuantity = num
  app.$message.success($t('common.success'))

  $form.query('.contractConcatDialog').take().setComponentProps({
    visible: false
  })
}

// 变更前合同信息
const $viewPreContract = async (row: any, $form: any) => {
  let data = $form.query('OrderChange').get('data')
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
  data.contractViewParams = { from: 'preOrderChange', row }
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

// 变更后合同信息
const $viewAfterContract = async (row: any, $form: any) => {
  let data = $form.query('OrderChange').get('data')
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
  data.contractViewParams = { from: 'afterOrderChange', row }
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}

// 获取调用查看关联合同弹窗接口及参数
const $getQueryObj = (obj: any, data: any): any => {
  let map = new Map([
    ['preOrderChange', { // 变更前
      listName: 'originOrderChangeContractMappingList',
      url: '/api-sup-ce/po/orderchange/queryOriginContractMappingByOrderDetailId',
      params: {
        orderChangeDetailId: data.contractViewParams.row.orderChangeDetailId,
        ...obj
      }
    }
    ],
    ['afterOrderChange', { // 变更后
      listName: 'orderChangeContractMappingList',
      url: '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId',
      params: {
        orderChangeDetailId: data.contractViewParams.row.orderChangeDetailId,
        ...obj
      }
    }
    ]
  ])
  return map.get(data.contractViewParams.from) || {}
}

// 明细查看合同 - 搜索
const $searchViewContractData = async (obj: any, $form: any) => {
  let data = $form.query('OrderChange').get('data')
  const { url, params, listName } = $getQueryObj(obj, data)
  let res = await $http({
    url,
    method: 'POST',
    data: params,
    loading: true
  })

  data.contractView.params = {
    orderContractMappingList: res.data[listName],
    ...res.data
  }
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
    $form.values.orderChangeDetails.forEach((i: any) => {
      if (i.ladderPriceFlag === 'Y') {
        let ladderPrices: any = []
        res.data.forEach((j: any) => {
          if (i.parentLineId && i.parentLineId === j.orderDetailId) {
            ladderPrices.push(j)
          } else if (i.orderDetailId === j.orderDetailId) {
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

const $getLadderPrice = ($form: any, row: any) => {
  if (row.ladderPriceFlag === 'Y') {
    const ladderPrice = row.ladderPrices.find((item: any) => {
      if (item.endQuantity) {
        return row.orderNum >= item.beginQuantity && row.orderNum < item.endQuantity
      } else {
        return row.orderNum >= item.beginQuantity
      }
    })
    if (ladderPrice) {
      row.unitNoTaxPrice = ladderPrice.price
      row.unitTaxPrice = ladderPrice.price * (1 + row.taxAmount / 100)
    }
  }
}

const scope = {
  $attrs,
  parseTime,
  $t,
  app,
  $init,
  $setButtonConfig,
  emitTabRemove,
  $concatContract,
  $searchViewContractData,
  $submitData,
  $saveBill,
  $addContract,
  $confirmSelectContract,
  $searchContractData,
  $confirmConcat,
  $viewPreContract,
  $viewAfterContract,
  $closePageAndRefreshListPageData,
  $getLadderPrices,
  $getLadderPrice
}
const components = {
  FormCollapse,
  BaseInfo,
  OrderDetail,
  FileUploads,
  ContractInfor,
  CPagination
}

const schema = defineSchemas({
  OrderChange: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the-purchaseOrderChangeDetail-detail',
      direction: 'vertical'
    },
    'x-data': {
      contractViewParams: {
        from: '',
        row: {}
      },
      contractView: { // 查看合同
        row: {},
        params: {},
        title: $t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true
      },
      contractConcat: { // 关联合同
        row: {},
        params: {},
        title: $t('orderMod.relationshipAgreement'),
        checkbox: false,
        hiddenOperation: false
      },
      contractSelectView: { // 选择合同
        row: {},
        params: {},
        title: $t('orderMod.selectContract'),
        checkbox: true,
        hiddenOperation: true,
        selectContract: true
      },
      selectedContract: [] // 已选合同
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            return $init($form, $queryEngineConfig)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.orderChangeId || $form.values.orderChangeId]

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {

            const detailData = res.data[0]
            detailData.orderChangeDetails.sort((obj1, obj2) => obj1['lineNum'] - obj2['lineNum'])

            // 单纯文本只读状态
            $form.readPretty = ['view', 'approvalOnly'].includes($attrs.params.flag)
            $setButtonConfig($form)
            $form.setValues({
              ...detailData
            })
            if($attrs?.params?.row?.orderId){
              $getLadderPrices($form, $queryEngineConfig)
            }
          }`),
          save: {
            // 标记当前 action 需要消费底层储存的级联删除数据
            cascadeDeletion: true
          }
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: true
          },
          'business-id': expression('$form.values.orderChangeId || null'),
          'business-type': 'ORDERCHANGE',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine,  $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
          }`)
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
      }

    }
  },
  // 查看合同
  contractInforDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'OrderChange\').get(\'data\').contractView}}',
      '@searchData': expression(`(obj) => {
        $searchViewContractData(obj, $form)
      }`),
      '@close': expression(`() => {
        $form.query('.contractInforDialog').take().setComponentProps({
          visible: false
        })
      }`)
    }
  },
  // 关联合同
  contractConcatDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    properties: {
      add: {
        type: 'void',
        title: $t('common.add'), // 新增
        'x-component': 'RButton',
        'x-component-props': {
          type: 'primary',
          size: 'mini',
          '@click': expression(`() => {
            $addContract($form)
          }`)
        },
        'x-slot': 'default'
      },
      confirm: {
        type: 'void',
        title: $t('orderMod.confirmConcat'), // 确认关联
        'x-component': 'RButton',
        'x-component-props': {
          type: 'primary',
          size: 'mini',
          '@click': expression(`() => {
             $confirmConcat($form)
          }`)
        },
        'x-slot': 'default'
      }
    },
    'x-component-props': {
      'contract-view': '{{$form.query(\'OrderChange\').get(\'data\').contractConcat}}',
      '@close': expression(`() => {
        $form.query('.contractConcatDialog').take().setComponentProps({
          visible: false
        })
      }`),
      '@deleteRow': `{{(row) => {
          let mapList = $form.query('OrderChange').get('data').contractConcat.params.orderContractMappingList
          $form.query('OrderChange').get('data').contractConcat.params.orderContractMappingList = mapList.filter((item) => {
            return row.contractMaterialId !== item.contractMaterialId
          })
        }
      }}`,
      '@correlatedQuantityChange': expression(`(orderContractMappingList) => {
        $form.query('OrderChange').get('data').contractConcat.params.orderContractMappingList = orderContractMappingList
      }`)
    }
  },
  // 选择合同
  contractSelectDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'OrderChange\').get(\'data\').contractSelectView}}',
      '@handleChange': expression(`(selections) => {
        $form.query('OrderChange').get('data').selectedContract = selections
      }`),
      '@rowDblclick': expression(`(row) => {
        $form.query('OrderChange').get('data').selectedContract = [row]
        $confirmSelectContract($form)
      }`),
      '@searchData': expression(`(obj) => {
        $searchContractData(obj,$form)
      }`),
      '@close': expression(`() => {
        $form.query('.contractSelectDialog').take().setComponentProps({
          visible: false
        })
      }`)
    },
    properties: {
      confirm: {
        type: 'void',
        title: $t('common.confirm'), // 确认
        'x-component': 'RButton',
        'x-component-props': {
          type: 'primary',
          size: 'mini',
          '@click': expression(`() => {
            $confirmSelectContract($form)
          }`)
        },
        'x-slot': 'default'
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="BuyerPurchaseOrderChangeDetail"
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
