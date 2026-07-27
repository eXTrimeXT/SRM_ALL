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
import PayInfor from './components/collapseItem/payInfor'
// @ts-ignore
import PurchaseDialog from './components/dialog/purchaseDialog'
// @ts-ignore
import MaterialDialog from './components/dialog/newMaterialDialog'
// @ts-ignore
import LadderPriceDialog, { ladderPriceDescribe } from 'lib@/compositionEngine/demandPoolManagement/ladderPriceDialog'
// @ts-ignore
import BomVersionDialog, { viewVersion } from './components/dialog/bomVersionDialog'
// @ts-ignore
import BomDetailDialog from './components/dialog/bomDetailDialog'
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
// @ts-ignore
import { orderConfig } from '@/config/orderConfig'
// @ts-ignore
import BomVersionSearch from 'mod@/buyer/purchasingDemand/views/purchaseApplicationEngine/components/bomVersionSearch'

const { emitTabRemove, t: $t, app, http: $http, getCurrentUserInfo } = usePageHelper()

let $attrs: any = useAttrs()

const $setButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = !$form.readPretty
    componentInstance.buttonConfigInfo.cancel.view = !$form.readPretty
    componentInstance.buttonConfigInfo.close.view = $form.readPretty
    componentInstance.setWorkflowBusinessId($form.values.orderId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const $init = ($form: any) => {
  // 设置审批流按钮
  $setButtonConfig($form)

  if ($attrs.params.flag === 'add') {
    const { phone, department } = getCurrentUserInfo()
    $form.values.tel = phone
    // $form.values.ceeaEmpUsername = nickname
    $form.values.ceeaDepartmentName = department
    $form.values.isManual = 'Y'
    return false
  }
  $form.values.orderId = $attrs.params.row.orderId
  $getOrderchangeRecordsData($form)
  return true
}

// 保存
const $saveBill = (type: string, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  let values: any = deepClone($form.values)
  values.detailList.forEach((item: any) => {
    item.ceeaPlanReceiveDate = item.ceeaPlanReceiveDate ? parseTime(item.ceeaPlanReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}') : ''
    item.ceeaPromiseReceiveDate = item.ceeaPromiseReceiveDate ? parseTime(item.ceeaPromiseReceiveDate, '{y}-{m}-{d} {h}:{i}:{s}') : ''
    delete item.isladderPriceFlag
    delete item.ladderPrices
  })
  delete values.orderchangeRecords

  let actionName = ''
  if (type === 'SAVE') {
    values.orderStatus = 'DRAFT'
    actionName = 'saveOrder'
  } else if (type === 'SUBMIT') {
    values.orderStatus = 'SUBMITTED'
    actionName = 'submitOrder'
  }
  $form.validate().then(() => {
    if (type === 'SUBMIT' && $form.values.detailList.length === 0) {
      return $message.error($t('purchaseOrder.prompt5')) // 订单明细不能为空
    }
    $submitData(actionName, values, $form, $queryEngine, $message, $bus)
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}

const $submitData = (actionName: any, values: any, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    'type': 'Order',
    'lang': 'zh-cn',
    'payload': [values],
    'action': actionName,
    'loading': true
  }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $form.values.orderId = res.originalData?.records[0] || ''
      $queryEngine.request.read()
      $bus.$emit('Order')
      if (actionName === 'submitOrder') {
        $cancel($form, $bus)
      }
    }
  })
}
const $cancel = ($form: any, $bus: any) => {
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  if (!['None', 'Push'].includes(componentInstance?.workflowParamsInfo?.integrationMode)) {
    $attrs.params.flag = 'approvalOnly'
    componentInstance.setWorkflowBusinessId($form.values.orderId || null)
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
  $bus.$emit('Order')
}

// 设置供应商
const $setVendor = ($form: any, val: any) => {
  $form.values.vendorId = val ? val.companyId : ''
  $form.values.vendorCode = val ? val.companyCode : ''
  $form.values.vendorName = val ? val.companyName : ''

  if (val?.companyId) {
    $http({
      url: '/api-sup/info/contactInfo/getContactInfoByCompanyId',
      method: 'POST',
      params: { companyId: val.companyId },
      loading: true
    }).then((res: any) => {
      if (res.code === '0') {
        const { contactName, ceeaContactMethod } = res.data
        $form.values.ceeaSupplierContacts = contactName
        $form.values.ceeaReceiveOrderTelephone = ceeaContactMethod
      }
    })
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

const $setRepeatData = (ids: any, data: any, selection: any, condition: any, lineSet: any) => {
  let getCondition = (row: any) => (typeof condition === 'function' ? condition(row) : row[condition])
  let dataArr: any = []
  let isTip = false

  selection.forEach((row: any, i: number) => {
    if (ids.includes(getCondition(row))) {
      isTip = true
    } else {
      let otherFiled = lineSet ? lineSet(row) : {}
      dataArr.push({
        ...row,
        ...otherFiled,
        ceeaRequirementLineId: row.requirementLineId,
        ceeaRequirementHeadNum: row.requirementHeadNum,
        ceeaRowNum: row.rowNum,
        ceeaOrganizationId: row.organizationId,
        ceeaOrganizationCode: row.organizationCode,
        ceeaOrganizationName: row.organizationName,
        receiptPlace: row.receivedFactory,
        ceeaUnitTaxPrice: row.taxPrice,
        ceeaUnitNoTaxPrice: row.noTaxPrice,
        ceeaAmountIncludingTax: null,
        ceeaAmountExcludingTax: null,
        ceeaContractNo: row.contractCode,
        confirmNum: null
      })
    }
  })
  data.push(...dataArr)
  // 设置行号
  data.forEach((row: any, i: number) => (row.lineNum = Number(i + 1)))
  // 已剔除重复勾选数据，明细行应唯一！
  if (isTip) return app.$message.warning($t('orderMod.checkDataRowUnique'))
}

// 打开采购申请弹框
const $openPurchaseDialog = ($form: any, $message: any) => {
  let { ceeaOrgId: orgId, organizationId, orderType: purchaseType, vendorId, receiveAddress, demandType, budgetManagementId } = $form.values
  let flag = !orgId || !organizationId || !purchaseType || !vendorId || !receiveAddress
  if (demandType === 'NONPRODUCTIVE_DEMAND') {
    flag = flag || !budgetManagementId
  }
  if (flag) {
    // 非生产性需求：请先选择业务实体、库存组织、订单类型、收货地址、供应商、预算编号
    // 生产性需求： 请先选择业务实体、库存组织、订单类型、收货地址、供应商
    return $message.warning(demandType === 'NONPRODUCTIVE_DEMAND' ? $t('purchaseOrder.prompt22') : $t('purchaseOrder.prompt23'))
  }

  let { ceeaOrgCode: orgCode, ceeaOrgName: orgName, organizationName, organizationCode, isManual, ifSample, receiveContact, receiveTelephone } = $form.values
  $form.query('Order').get('data').purchaseDialogQueryData = {
    orgId,
    orgName,
    orgCode,
    organizationId,
    organizationName,
    organizationCode,
    purchaseType,
    vendorId,
    isManual,
    ifSample,
    budgetManagementId,
    receiveContact,
    receiveTelephone,
    demandType
  }

  $form.query('.purchaseDialog').take().setComponentProps({
    visible: true
  })
}

// 采购申请新增
const $setPurchaseData = (selections: any, $form: any, $message: any) => {
  if (selections.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }

  const ids = $form.values.detailList.map((item: any) => item['ceeaRequirementLineId'])
  $setRepeatData(
    ids,
    $form.values.detailList,
    selections,
    'requirementLineId',
    (v: any) => {
      return {
        ceeaIfRequirement: 'Y',
        ceeaPromiseReceiveDate: v.requirementDate, // 订单创建时的承诺到货时间默认为需求日期
        ceeaTaxRate: v.taxRate,
        ceeaTaxKey: v.taxKey,
        bomVersion: v.bomVersionCode,
        bomId: v.bomHeadId
      }
    },
  )

  $form.query('.purchaseDialog').take().setComponentProps({
    visible: false
  })
}
// 打开物料弹框
// const $openMaterialDialog = ($form: any, $message: any) => {
//   let { ceeaOrgId, organizationId, orderType, vendorId, receiveAddress } = $form.values
//   if (!ceeaOrgId || !organizationId || !orderType || !vendorId || !receiveAddress) {
//     // 请先填写必填项
//     return $message.warning($t('orderMod.pleaseFillrequired'))
//   }

//   let { isManual, ifSample, receiveContact, receiveTelephone, orderType: purchaseType } = $form.values
//   $form.query('Order').get('data').materialDialogQueryData = {
//     ifSample,
//     ceeaOrgId,
//     vendorId,
//     receiveContact,
//     receiveTelephone,
//     receiveAddress,
//     isManual,
//     organizationId,
//     purchaseType
//   }

//   $form.query('.materialDialog').take().setComponentProps({
//     visible: true
//   })
// }

const $openMaterialDialog = ($form: any, $message: any) => {
  let { ceeaOrgId, organizationId, orderType, vendorId, receiveAddress } = $form.values
  if (!ceeaOrgId || !organizationId || !orderType || !vendorId || !receiveAddress) {
    // 请先选择业务实体、库存组织、订单类型、收货地址、供应商
    return $message.warning($t('purchaseOrder.prompt23'))
  }

  let { ceeaOrgCode, organizationCode, orderType: purchaseType, ifSample } = $form.values
  $form.query('Order').get('data').materialDialogQueryData = {
    ifSample: ifSample || 'N',
    orgCode: ceeaOrgCode,
    invOrgCodes: [organizationCode],
    vendorId,
    purchaseType,
    materialTypeNotEqual: purchaseType !== 'OUTSOURCING' ? 'OP' : undefined,
    materialType: purchaseType === 'OUTSOURCING' ? 'OP' : undefined
  }

  $form.query('.materialDialog').take().setComponentProps({
    visible: true
  })
}

// 物料新增
const $setMaterialData = (selections: any, $form: any, $message: any) => {
  if (selections.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }

  const ids = $form.values.detailList.map((item: any) => {
    if (!item.ceeaRequirementLineId) return item.materialCode
  })
  $setRepeatData(
    ids,
    $form.values.detailList,
    selections,
    'materialCode',
    (v: any) => {
      return {
        ceeaIfRequirement: 'N',
        unit: v.unitName,
        unitCode: v.unit,
        // ceeaTaxRate: v.taxRate,
        // ceeaTaxKey: v.taxCode,
        orderDetailStatus: '',
        categoryName: v.categoryName,
        categoryId: v.categoryId,
        categoryCode: v.categoryCode
      }
    },
  )

  $form.query('.materialDialog').take().setComponentProps({
    visible: false
  })
}

// 计算税额
const $setRowAmount = (row: any, $form: any) => {
  getLadderPrice($form, row)
  let tableData = $form.values.detailList
  row.ceeaAmountIncludingTax = Number(row.orderNum * row.ceeaUnitTaxPrice || 0).toFixed(2)
  row.ceeaAmountExcludingTax = Number(row.orderNum * row.ceeaUnitNoTaxPrice || 0).toFixed(2)
  // 合计
  $form.values.ceeaTotalNum = tableData
    .map((v: any) => v.orderNum)
    .reduce((p: number, c: number) => (Number(p) || 0) + (Number(c) || 0))
  // 合计金额含税
  $form.values.ceeaTaxAmount = Number(
    tableData.map((v: any) => v.ceeaAmountIncludingTax)
      .reduce((p: any, c: any) => Number(p) + Number(c)),
  ).toFixed(2)
  // 合计金额不含税
  $form.values.ceeaNoTaxAmount = Number(
    tableData
      .map((v: any) => v.ceeaAmountExcludingTax)
      .reduce((p: any, c: any) => Number(p) + Number(c)) || 0,
  ).toFixed(2)
}

const getLadderPrice = ($form: any, row: any) => {
  if (row.ladderPriceFlag === 'Y') {
    const ladderPrice = row.ladderPrices.find((item: any) => {
      if (item.endQuantity) {
        return row.orderNum >= item.beginQuantity && row.orderNum < item.endQuantity
      } else {
        return row.orderNum >= item.beginQuantity
      }
    })
    if (ladderPrice) {
      row.ceeaUnitNoTaxPrice = parseFloat(ladderPrice.price.toFixed(8))
      row.ceeaUnitTaxPrice = parseFloat((ladderPrice.price * (1 + row.ceeaTaxRate / 100)).toFixed(8))
    }
  }
}

const $openLadderPriceDialog = ($form: any, row: any) => {
  $form.query('*.ladderPriceDialog').take().setComponentProps({ visible: true })
  row.ladderPrices.forEach((item: any) => { item.unit = row.unit })
  setTimeout(() => {
    $form.query('Order').get('data').ladderPriceDescribeRow = row
    $form.query('*.ladderPriceDialog.*.ladderPrices').take((field: any) => {
      field.value = row.ladderPrices
    })
  })
}
const $getLadderPriceDescribeRow = ($form: any) => {
  return $form.query('Order').get('data').ladderPriceDescribeRow
}

// 合同关联数量-查看合同
const $viewContract = async (row: any, $form: any) => {
  let data = $form.query('Order').get('data')
  data.contractViewParams = { from: 'concatContract', row }
  data.contractView.row = row
  let res = await $http({
    url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
    method: 'POST',
    data: { orderDetailId: row.orderDetailId },
    loading: true
  })
  data.contractView.params = res.data
  $form.query('.contractInforDialog').take().setComponentProps({
    visible: true
  })
}
// 合同关联数量-查看合同-搜索
const $searchViewContractData = async (obj: any, $form: any) => {
  let data = $form.query('Order').get('data')
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

// 获取调用查看关联合同弹窗接口及参数
const $getQueryObj = (obj: any, data: any): any => {
  let map = new Map([
    ['concatContract', { // 合同已关联数量
      listName: 'orderContractMappingList',
      url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
      params: {
        orderDetailId: data.contractViewParams.row.orderDetailId,
        ...obj
      }
    }
    ],
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

// 关联合同
const $concatContract = async (row: any, $form: any) => {
  let data = $form.query('Order').get('data')
  // 请暂存提交后再关联合同！
  if (!row.orderDetailId) return app.$message.warning($t('orderMod.saveBeforContractTip'))

  data.contractConcat.row = row
  let res = await $http({
    url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
    method: 'POST',
    data: { orderDetailId: row.orderDetailId },
    loading: true
  })
  data.contractConcat.params = res.data
  // 给每一行设置一个字段存储初始关联数量
  data.contractConcat.params.orderContractMappingList.forEach((row: any) => {
    row.oldCorrelatedQuantity = row.correlatedQuantity || 0
  })
  $form.query('.contractConcatDialog').take().setComponentProps({
    visible: true
  })
}

// 获取新增合同列表
const $getContractList = async (params: any, list: any, $form: any) => {
  const { data } = await $http({
    url: '/api-sup-ce/po/order/listContractMaterialByOrderDetail',
    method: 'POST',
    data: {
      'materialId': list.contractConcat.row.materialId,
      'orderDetailId': list.contractConcat.row.orderDetailId,
      'orgId': $form.values.ceeaOrgId,
      'organizationId': $form.values.organizationId,
      'receiveAddress': $form.values.receiveAddress,
      'vendorId': $form.values.vendorId,
      ...params
    },
    loading: true
  })
  return data
}

// 新增合同-打开选择合同弹框
const $addContract = async ($form: any) => {
  $form.query('.contractSelectDialog').take().setComponentProps({
    visible: true
  })
  let data = $form.query('Order').get('data')
  const list = await $getContractList({}, data, $form)
  data.contractSelectView.row = data.contractConcat.row
  data.contractSelectView.params = list
}

// 选择合同-合同搜索
const $searchContractData = async (obj: any, $form: any) => {
  let data = $form.query('Order').get('data')
  const list = await $getContractList(obj, data, $form)
  data.contractSelectView.params = list
}

// 选择合同-确认选择合同
const $confirmSelectContract = ($form: any) => {
  let data = $form.query('Order').get('data')
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
    // 第n行：关联合同数量>未关联合同数量，请检查
    app.$message.warning(`${$t('orderMod.chapter')}${isCheckNumberArr.join(',')}${$t('orderMod.row')}：${$t('orderMod.checkConcatNum')}`)
    return false
  }
  return true
}

// 关联合同-确定关联合同
const $confirmConcat = async ($form: any) => {
  let data = $form.query('Order').get('data')
  if (!$isConfirmConcat(data)) return false

  await $http({
    url: '/api-sup-ce/po/order/saveContractMapping',
    method: 'POST',
    data: {
      orderNum: data.contractConcat.row.orderNum,
      orderId: $form.values.orderId,
      orderDetailId: data.contractConcat.row.orderDetailId,
      orderContractMappingList: data.contractConcat.params.orderContractMappingList
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
  let data = $form.query('Order').get('data')
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
  let data = $form.query('Order').get('data')
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

// 获取订单变更记录
const $getOrderchangeRecordsData = ($form: any) => {
  const { orderchangeRecordsInitQuery, orderchangeRecordsPageNum: pageNum, orderchangeRecordsPageSize: pageSize } = $form.query('Order').get('data')
  $http({
    url: '/api-sup-ce/po/orderchange/getOrderChangeHistoryList',
    method: 'POST',
    data: { ...orderchangeRecordsInitQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.orderchangeRecords = res.data.list
        $form.query('Order').get('data').orderchangeRecordsTotal = res.data.total
      }
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
const $openBomVersionDialog = ($form: any, $queryEngine: any, row: any) => {
  $form.query('*.bomVersionDialog').take().setComponentProps({ visible: true })
  $getBomVersionList($form, $queryEngine, row)
}

// 获取bom版本
const $getBomVersionList = ($form: any, $queryEngine: any, row: any) => {
  $queryEngine.request.baseRequest({
    'type': 'BomHead',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      'filter': {
        'materialId': {
          'eq': row.materialId
        },
        'organizationId': {
          'eq': $form.values.organizationId
        },
        'status': {
          'eq': 'Y'// 直接传Y
        }
      },
      'page': {
        'sort': 'creationDate desc',
        'pageNum': $form.query('Order').get('data').bomVersionListPageNum,
        'pageSize': $form.query('Order').get('data').bomVersionListPageSize
      }
    },
    'action': 'listBomByParam'
  }).then((res: any) => {
    $form.query('*.bomVersionDialog.*.bomVersionList').take((field: any) => {
      field.value = res.data
    })
    $form.query('Order').get('data').bomVersionListTotal = res.originalData.payload.total
  })
}

// 设置bom版本
const $selBomVersion = ($form: any, done?: any) => {
  const row = $form.query('*.bomVersionDialog.*.bomVersionList').take()
    .componentProps
    .componentInstance
    .getRadioRecord()
  $form.values.detailList[$form.query('Order').get('data').detailListCurrentIndex].bomVersion = row.versionCode
  $form.values.detailList[$form.query('Order').get('data').detailListCurrentIndex].bomId = row.bomHeadId
  if (done) {
    done()
  } else {
    $form.query('*.bomVersionDialog').take().setComponentProps({ visible: false })
  }
}

const $openBomVDetailDialog = ($form: any, row: any, $queryEngine: any) => {
  $form.query('*.bomDetailDialog').take().setComponentProps({ visible: true })
  $getBomDetailList($form, row, $queryEngine)
}

// 获取bom明细
const $getBomDetailList = ($form: any, row: any, $queryEngine: any) => {
  $queryEngine.request.baseRequest({
    'type': 'BomLine',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': {
      'filter': {
        'bomHeadId': {
          'eq': row.bomId
        },
        'distributeFlag': {
          'eq': 'Y'// 直接传Y
        }
      },
      'page': {
        'sort': 'creationDate desc',
        'pageNum': $form.query('Order').get('data').bomDetailListPageNum,
        'pageSize': $form.query('Order').get('data').bomDetailListPageSize
      }
    },
    'action': 'listBomLineByParam'
  }).then((res: any) => {
    console.log(res)
    res.data.forEach((item: any) => {
      item.componentQuantity = row.requirementQuantity || row.requirementQuantity === 0 ? item.baseMaterialNum * +row.requirementQuantity : null
      item.componentOrderNum = row.orderNum || row.orderNum === 0 ? item.baseMaterialNum * +row.orderNum : null
    })
    $form.query('*.bomDetailDialog.*.bomDetailList').take((field: any) => {
      field.value = res.data
    })
    $form.query('Order').get('data').bomDetailListTotal = res.originalData.payload.total
  })
}

const scope = {
  $attrs,
  parseTime,
  $t,
  app,
  $init,
  $setButtonConfig,
  emitTabRemove,
  $setVendor,
  $getPaymentTermsPage,
  $openPurchaseDialog,
  $openMaterialDialog,
  $setMaterialData,
  $setPurchaseData,
  $viewContract,
  $concatContract,
  $searchViewContractData,
  $setRowAmount,
  $submitData,
  $saveBill,
  $addContract,
  $confirmSelectContract,
  $searchContractData,
  $confirmConcat,
  $viewPreContract,
  $viewAfterContract,
  $getOrderchangeRecordsData,
  $closePageAndRefreshListPageData,
  $openLadderPriceDialog,
  $getLadderPriceDescribeRow,
  $ladderPriceDescribe: ladderPriceDescribe,
  $viewVersion: viewVersion,
  $selBomVersion,
  $getLadderPrices,
  $openBomVersionDialog,
  $getBomVersionList,
  $openBomVDetailDialog,
  orderConfig
}
const components = {
  FormCollapse,
  BaseInfo,
  PayInfor,
  OrderDetail,
  FileUploads,
  ChangeRecords,
  ContractInfor,
  PurchaseDialog,
  MaterialDialog,
  BomVersionDialog,
  BomDetailDialog,
  CPagination,
  BomVersionSearch,
  LadderPriceDialog
}

const schema = defineSchemas({
  Order: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the-purchaseOrderDetail-detail',
      direction: 'vertical'
    },
    'x-data': {
      ladderPriceDescribeRow: {},
      viewUpdateButton: true, // 控制工作流按钮
      paymentTermOptions: [], // 付款条件
      purchaseDialogQueryData: {}, // 采购申请弹框查询条件
      materialDialogQueryData: {}, // 物料弹框查询条件

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
      selectedContract: [], // 已选合同

      orderchangeRecordsInitQuery: { orderId: $attrs?.params?.row?.orderId },
      orderchangeRecordsPageNum: 1,
      orderchangeRecordsPageSize: 5,
      orderchangeRecordsTotal: 0,

      bomVersionListPageNum: 1,
      bomVersionListPageSize: 15,
      bomVersionListTotal: 0,
      bomDetailListPageNum: 1,
      bomDetailListPageSize: 15,
      bomDetailListTotal: 0,
      detailListCurrentIndex: null
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
            $form.readPretty = ['view', 'approvalOnly'].includes($attrs.params.flag)
            $setButtonConfig($form)
            if($form.readPretty ){
              detailData.receiveAddressName = detailData.receiveAddress
            }

            detailData.detailList.sort((obj1, obj2) => obj1['lineNum'] - obj2['lineNum'])
            $form.setValues({
              ...detailData,
              orderchangeRecords: $form.values.orderchangeRecords
            })
            if($attrs?.params?.row?.orderId){
              $getLadderPrices($form, $queryEngineConfig)
            }
          }`)
        },
        saveOrder: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        },
        submitOrder: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
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
          'business-id': expression('$form.values.orderId || null'),
          'business-type': 'ORDER',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
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
      // 阶梯价
      ladderPriceDialog: {
        ...LadderPriceDialog
      },
      bomVersionDialog: {
        ...BomVersionDialog
      },
      bomDetailDialog: {
        ...BomDetailDialog
      }
    }
  },
  // 物料明细选择（采购申请）
  purchaseDialog: {
    type: 'void',
    'x-component': 'PurchaseDialog',
    'x-component-props': {
      queryData: '{{$form.query(\'Order\').get(\'data\').purchaseDialogQueryData}}',
      '@confirm': expression('(selections) => $setPurchaseData(selections,$form,$message)'),
      '@close': expression(`() => {
          $form.query('.purchaseDialog').take().setComponentProps({
            visible: false
          })
        }`)
    }
  },
  // 物料新增 - 物料明细选择
  materialDialog: {
    type: 'void',
    'x-component': 'MaterialDialog',
    'x-component-props': {
      queryData: '{{$form.query(\'Order\').get(\'data\').materialDialogQueryData}}',
      '@confirm': expression('(selections) => $setMaterialData(selections,$form,$message)'),
      '@close': expression(`() => {
        $form.query('.materialDialog').take().setComponentProps({
          visible: false
        })
      }`)
    }
  },
  // 查看合同
  contractInforDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'Order\').get(\'data\').contractView}}',
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
      'contract-view': '{{$form.query(\'Order\').get(\'data\').contractConcat}}',
      '@deleteRow': `{{(row) => {
          let mapList = $form.query('Order').get('data').contractConcat.params.orderContractMappingList
          $form.query('Order').get('data').contractConcat.params.orderContractMappingList = mapList.filter((item) => {
            return row.contractMaterialId !== item.contractMaterialId
          })
        }
      }}`,
      '@close': expression(`() => {
        $form.query('.contractConcatDialog').take().setComponentProps({
          visible: false
        })
      }`),
      '@correlatedQuantityChange': expression(`(orderChangeContractMappingList) => {
        $form.query('Order').get('data').contractConcat.params.orderContractMappingList = orderChangeContractMappingList
      }`)
    }
  },
  // 选择合同
  contractSelectDialog: {
    type: 'void',
    'x-component': 'ContractInfor',
    'x-component-props': {
      'contract-view': '{{$form.query(\'Order\').get(\'data\').contractSelectView}}',
      '@handleChange': expression(`(selections) => {
        $form.query('Order').get('data').selectedContract = selections
      }`),
      '@rowDblclick': expression(`(row) => {
        $form.query('Order').get('data').selectedContract = [row]
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
    schemaKey="BuyerPurchaseOrderNewDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
.the-purchaseOrderDetail-detail {

  .high-light input,
  .high-light {
    color: #F25353;
    font-weight: bold;
  }
}
</style>
