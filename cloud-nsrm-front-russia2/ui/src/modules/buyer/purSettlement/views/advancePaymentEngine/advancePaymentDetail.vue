<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  toJS
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
import { useAttrs } from 'vue-demi'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import InvoiceDetail from './components/collapseItem/invoiceDetail'
// @ts-ignore
import InvoiceDialog from './components/dialog/invoiceDialog'
// @ts-ignore
import AdvancePaymentDetail from './components/collapseItem/advancePaymentDetail'
// @ts-ignore
import AdvancePaymentDialog from './components/dialog/advancePaymentDialog'
// @ts-ignore
import FileUploads from './components/collapseItem/fileUploads'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'

const { emitTabRemove, t: $t, http: $http, app } = usePageHelper()

let $attrs: any = useAttrs()

const $setButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

    componentInstance.buttonConfigInfo.save.view = !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = !$form.readPretty
    componentInstance.buttonConfigInfo.cancel.view = !$form.readPretty
    componentInstance.buttonConfigInfo.close.view = $form.readPretty
    componentInstance.setWorkflowBusinessId($form.values.advanceApplyId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const $setUserInfo = ($form: any) => {
  // @ts-ignore
  const { nickname, ceeaDeptId, department } = app.$store.getters.userInfo
  // 设置单据基础信息-创建人、部门
  $form.values.departmentName = department
  $form.values.departmentId = ceeaDeptId
  $form.values.createdFullName = nickname
}

const $init = ($form: any) => {
  // 设置审批流按钮
  $setButtonConfig($form)

  if ($attrs.params.flag === 'add') {
    $setUserInfo($form)
    return false
  }

  if ($attrs.params.flag === 'playPlan') {
    $setUserInfo($form)
    const head = $attrs.params.head
    $form.values.vendorCode = head.vendorCode
    $form.values.vendorId = head.vendorId
    $form.values.vendorName = head.vendorName
    $form.values.orgId = head.buId
    $form.values.orgCode = head.buCode
    $form.values.orgName = head.buName
    $form.values.currencyId = head.currencyId
    $form.values.currencyName = head.currencyName
    $form.values.currencyCode = head.currencyCode
    $form.values.billType = 'CONTRACT'
    return false
  } else {
    $form.values.advanceApplyId = $attrs.params.row.advanceApplyId
    return true
  }
}

// 保存
const $saveBill = (type: string, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  const values = toJS($form.values)
  if (values.advanceApplyDetailList?.length) values.advanceApplyDetailList.forEach((item: any) => { item.paymentAmountAppliedN = item.paymentAmountAppliedN ? +item.paymentAmountAppliedN : null })
  const data = $form.query('AdvanceApply').get('data')
  if (values.billType === 'ORDER') {
    if (data.perAdvanceApplyDetailsBackup.length) {
      values.perAdvanceApplyDetails = []
      data.perAdvanceApplyDetailsBackup.forEach((item: any, index: any) => {
        if (item['$delete']) {
          values.perAdvanceApplyDetails.push(item)
        } else {
          values.perAdvanceApplyDetails.push({ '$delete': item.perAdvanceApplyDetailId })
        }
      })
    }
  } else if (values.billType === 'CONTRACT') {
    if (data.advanceApplyDetailListBackup.length) {
      values.advanceApplyDetailList = []
      data.advanceApplyDetailListBackup.forEach((item: any, index: any) => {
        if (item['$delete']) {
          values.advanceApplyDetailList.push(item)
        } else {
          values.advanceApplyDetailList.push({ '$delete': item.advanceApplyDetailId })
        }
      })
    }
  }

  if (type === 'SAVE') {
    $submitData('save', values, $form, $queryEngine, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      $submitData('submit', values, $form, $queryEngine, $message, $bus)
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const $submitData = (type: any, values: any, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  let request = type === 'submit' ? $queryEngine.request.save(values, { customizeAction: 'submit', loading: true }) : $queryEngine.request.save(values, { loading: true })
  request.then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $bus.$emit('AdvanceApplyHead')

      const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
      if (type === 'submit' && ['None', 'Push'].includes(componentInstance?.workflowParamsInfo?.integrationMode)) {
        $closePageAndRefreshListPageData($bus)
      } else {
        $form.values.advanceApplyId = res.originalData?.records[0] || ''
        $queryEngine.request.read()

        if (type === 'submit') {
          $attrs.params.flag = 'approvalOnly'
          componentInstance.setWorkflowBusinessId($form.values.advanceApplyId || null)
          componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
          componentInstance.handlerAfter('SUBMIT', () => {
            $closePageAndRefreshListPageData($bus)
          })
        }
      }
    }
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  emitTabRemove($attrs.tabName)
  $bus.$emit('AdvanceApplyHead')
}

// 设置供应商
const $setVendor = ($form: any, val: any) => {
  $form.values.vendorId = val ? val.companyId : ''
  $form.values.vendorCode = val ? val.companyCode : ''
  $form.values.vendorName = val ? val.companyName : ''

  // 携带银行信息
  $http({
    url: '/api-sup/info/bankInfo/getMainAccountByCompanyId',
    method: 'GET',
    params: { companyId: val.companyId },
    loading: true
  }).then((res: any) => {
    if (res.code === '0') {
      const { bankAccount, bankAccountName, bankName, openingBank } = res.data
      $form.values.bankAccount = bankAccount
      $form.values.bankAccountName = bankAccountName
      $form.values.bankName = bankName
      $form.values.openingBank = openingBank
    }
  })
}
// 打开订单弹框
const $openInvoiceDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyName || !$form.values.taxKey || !$form.values.payMethod) {
    // 请先完成预付款申请明细中的必填项
    return $message.warning($t('advancePayment.prompt1'))
  }

  $form.query('invoiceDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    let params: any = {}
    const conditions = [
      'orgId',
      'organizationId',
      'vendorId',
      'currencyId',
      'taxKey'
    ]
    Object.keys($form.values).forEach(key => {
      if (conditions.includes(key) && $form.values[key]) {
        params[key] = $form.values[key]
      }
    })
    $form.query('AdvanceApply').get('data').invoiceDialogInitQuery = params
    $form.query('AdvanceApply').get('data').invoiceDialogPageNum = 1
    $form.query('AdvanceApply').get('data').invoiceDialogPageSize = 5
    $getInvoiceDialogData($form)
  })
}

// 获取订单弹框数据
const $getInvoiceDialogData = ($form: any) => {
  const { invoiceDialogInitQuery, invoiceDialogQuery, invoiceDialogPageNum: pageNum, invoiceDialogPageSize: pageSize } = $form.query('AdvanceApply').get('data')
  $http({
    url: '/api-sup-ce/sup/advanceApplyDetail/searchOrderDetail',
    method: 'POST',
    data: { ...invoiceDialogInitQuery, ...invoiceDialogQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        res.data.list.forEach((item: any) => {
          item.amountIncludingTax = Number(item.amountIncludingTax.toFixed(2))
          item.paymentAmountAppliedN = Number(item.paymentAmountAppliedN.toFixed(2))
        })
        $form.values.invoiceDialogTable = res.data.list
        $form.query('AdvanceApply').get('data').invoiceDialogTotal = res.data.total
      }
    })
}
// 过滤查询合同履约预付款弹框数据
const $getInvoiceQuerydata = (obj: any, $form: any) => {
  $form.query('AdvanceApply').get('data').invoiceDialogPageNum = 1
  $form.query('AdvanceApply').get('data').invoiceDialogQuery = { ...obj }
  $getInvoiceDialogData($form)
}

// 设置订单数据
const $setInvoiceDetailData = ($form: any, $message: any) => {
  const rows = $form.query('invoiceDialog.invoiceDialogTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = $form.values.advanceApplyDetailList.map((item: any) => item.orderDetailId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.orderDetailId)) {
      $form.query('advanceApplyDetailList').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增订单预付款明细存在重复数据，已去除
    $message.warning($t('advancePayment.prompt2'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.invoiceDialogTable = []

  $setAmountCal($form)
}

// 计算头表金额
const $setAmountCal = ($form: any) => {
  let tableData = $form.values.billType == 'CONTRACT' ? $form.values.perAdvanceApplyDetails : $form.values.advanceApplyDetailList
  let includeTaxAmount = $form.values.billType == 'CONTRACT' ? 'currentPaymentAmount' : 'paymentAmountApply'
  if (tableData.length > 0) {
    // 含税金额
    $form.values.includeTaxAmount = tableData
      .map((row: any) => row[includeTaxAmount])
      .reduce((p: any, c: any) => (Number(p) || 0) + (Number(c) || 0)) || 0
  } else {
    $form.values.includeTaxAmount = 0
  }

  // 未税金额= 含税金额/(1+税率)
  $form.values.excludeTaxAmount = (($form.values.includeTaxAmount / (1 + Number($form.values.taxRate) / 100)) || 0).toFixed(2)

  // 税额
  $form.values.totalTax = ($form.values.includeTaxAmount - $form.values.excludeTaxAmount).toFixed(2)
}

// 打开合同履约预付款弹框
const $openAdvancePaymentDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyName || !$form.values.taxKey || !$form.values.payMethod) {
    // 请先完成预付款申请明细中的必填项
    return $message.warning($t('advancePayment.prompt1'))
  }

  $form.query('advancePaymentDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('formWrapper').take().invoke('currentInstance').reset()
    const params = {
      querySource: 'advance',
      vendorId: $form.values.vendorId,
      vendorCode: $form.values.vendorCode,
      vendorName: $form.values.vendorName,
      invId: $form.values.organizationId,
      buId: $form.values.orgId
    }
    $form.query('AdvanceApply').get('data').advancePaymentDialogInitQuery = params
    $form.query('AdvanceApply').get('data').advancePaymentDialogPageNum = 1
    $form.query('AdvanceApply').get('data').advancePaymentDialogPageSize = 5
    $getAdvancePaymentDialogData($form)
  })
}

// 获取合同履约预付款弹框数据
const $getAdvancePaymentDialogData = ($form: any) => {
  const { advancePaymentDialogInitQuery, advancePaymentDialogQuery, advancePaymentDialogPageNum: pageNum, advancePaymentDialogPageSize: pageSize } = $form.query('AdvanceApply').get('data')
  $http({
    url: '/api-cm/contract/performInvoice/getPerInvoiceDetailPage',
    method: 'POST',
    data: { ...advancePaymentDialogInitQuery, ...advancePaymentDialogQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.advancePaymentTable = res.data.list
        $form.query('AdvanceApply').get('data').advancePaymentDialogTotal = res.data.total
      }
    })
}

// 过滤查询合同履约预付款弹框数据
const $getAdvancePaymentQuerydata = (obj: any, $form: any) => {
  $form.query('AdvanceApply').get('data').advancePaymentDialogPageNum = 1
  $form.query('AdvanceApply').get('data').advancePaymentDialogQuery = { ...obj }
  $getAdvancePaymentDialogData($form)
}

// 设置合同履约预付款数据
const $setAdvancePaymentDetailData = ($form: any, $message: any) => {
  const rows = $form.query('advancePaymentDialog.advancePaymentTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }

  const ids = $form.values.perAdvanceApplyDetails.map((item: any) => item.perInvoiceDetailId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.perInvoiceDetailId)) {
      $form.query('perAdvanceApplyDetails').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增合同履约付款明细存在重复数据，已去除
    $message.warning($t('advancePayment.prompt3'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.advancePaymentTable = []
}

const scope = {
  $attrs,
  $t,
  $init,
  $setButtonConfig,
  emitTabRemove,
  $setVendor,
  $openInvoiceDialog,
  $getInvoiceDialogData,
  $getInvoiceQuerydata,
  $setInvoiceDetailData,
  $getAdvancePaymentQuerydata,
  $setAmountCal,
  $openAdvancePaymentDialog,
  $getAdvancePaymentDialogData,
  $setAdvancePaymentDetailData,
  $submitData,
  $saveBill,
  $closePageAndRefreshListPageData
}
const components = {
  FormCollapse,
  BaseInfo,
  InvoiceDetail,
  InvoiceDialog,
  AdvancePaymentDetail,
  AdvancePaymentDialog,
  FileUploads,
  CPagination
}

const schema = defineSchemas({
  AdvanceApply: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      perAdvanceApplyDetailsBackup: [],
      advanceApplyDetailListBackup: [],

      invoiceDialogQueryForm: [
        {
          prop: 'orderNumber',
          label: () => $t('orderMod.buyerOrderSynergy.orderNumber2') // 订单号
        },
        {
          prop: 'materialCode',
          label: () => $t('common.materialName'), // 物料名称
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item_display'
        }
      ],
      invoiceDialogInitQuery: {},
      invoiceDialogPageNum: 1,
      invoiceDialogPageSize: 5,
      invoiceDialogTotal: 0,

      advancePaymentDialogQueryForm: [
        {
          prop: 'contractName',
          label: () => $t('vendorMod.contractName') // 合同名称
        },
        {
          prop: 'invoiceNo',
          label: () => $t('contract_mod.processNum2') // 合同履约开票单号
        }
      ],
      advancePaymentDialogInitQuery: {},
      advancePaymentDialogQuery: {},
      advancePaymentDialogPageNum: 1,
      advancePaymentDialogPageSize: 5,
      advancePaymentDialogTotal: 0
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            return $init($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.advanceApplyId || $form.values.advanceApplyId]

            data.query['*'] = {}

            return data
        }`),
          onSuccess: expression(`(res) => {
            
          const detailData = res.data[0]
          console.log(detailData)
          
          // 单纯文本只读状态
          $form.readPretty =  ['view', 'approvalOnly'].includes($attrs.params.flag)
          $setButtonConfig($form)
          $form.query('AdvanceApply').get('data').perAdvanceApplyDetailsBackup = detailData?.perAdvanceApplyDetails || []  // 合同
          $form.query('AdvanceApply').get('data').advanceApplyDetailListBackup = detailData?.advanceApplyDetailList || [] // 订单
          $form.setValues({
            ...detailData
          }) 
        }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          action: 'saveTemporary'
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
          'business-id': expression('$form.values.advanceApplyId || null'),
          'business-type': 'ADVANCEPAYMENT',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $message, $bus)
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
              // 开票单
              invoiceDetail: {
                ...InvoiceDetail
              },
              // 合同履约预付款明细
              advancePaymentDetail: {
                ...AdvancePaymentDetail
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
  // 新增订单
  invoiceDialog: {
    ...InvoiceDialog
  },
  // 新增合同履约预付款
  advancePaymentDialog: {
    ...AdvancePaymentDialog
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="AdvanceApplyDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
