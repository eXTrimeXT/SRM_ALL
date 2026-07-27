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
import RejectDialog from './components/dialog/rejectDialog'
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
    componentInstance.setWorkflowBusinessId($form.values.paymentApplyId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const $init = ($form: any) => {
  // 设置审批流按钮
  $setButtonConfig($form)

  if ($attrs.params.flag === 'add') {
    return false
  }

  if ($attrs.params.flag === 'playPlan') {
    // @ts-ignore
    const { nickname, ceeaDeptId, department } = app.$store.getters.userInfo
    // 设置单据基础信息-创建人、部门
    $form.values.departmentName = department
    $form.values.departmentId = ceeaDeptId
    $form.values.createdFullName = nickname

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
    $form.values.paymentApplyId = $attrs.params.row.paymentApplyId
    return true
  }
}

// 保存
const $saveBill = (type: string, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  const values = toJS($form.values)
  const data = $form.query('PaymentApply').get('data')
  if (values.billType === 'ORDER') {
    if (type === 'SUBMIT' && values.invoices.length === 0) {
      // 付款明细不能为空
      return $message.error($t('purPaymentApply.prompt7'))
    }

    if (data.contractsBackup.length) {
      values.contracts = []
      data.contractsBackup.forEach((item: any, index: any) => {
        if (item['$delete']) {
          values.contracts.push(item)
        } else {
          values.contracts.push({ '$delete': item.perAdvanceApplyDetailId })
        }
      })
    }
  } else if (values.billType === 'CONTRACT') {
    if (type === 'SUBMIT' && values.contracts.length === 0) {
      // 付款明细不能为空
      return $message.error($t('purPaymentApply.prompt7'))
    }

    if (data.invoicesBackup.length) {
      values.invoices = []
      data.invoicesBackup.forEach((item: any, index: any) => {
        if (item['$delete']) {
          values.invoices.push(item)
        } else {
          values.invoices.push({ '$delete': item.paymentApplyDetailId })
        }
      })
    }
  }

  if (type === 'SAVE') {
    values.status = 'DRAFT'
    $submitData('save', values, $form, $queryEngine, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      values.status = 'SUBMITTED'
      $submitData('submit', values, $form, $queryEngine, $message, $bus)
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const $submitData = (type: any, values: any, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.save(values, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $bus.$emit('PaymentApplyHead')

      const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
      if (type === 'submit' && ['None', 'Push'].includes(componentInstance?.workflowParamsInfo?.integrationMode)) {
        $closePageAndRefreshListPageData($bus)
      } else {
        $form.values.paymentApplyId = res.originalData?.records[0] || ''
        $queryEngine.request.read()

        if (type === 'submit') {
          $attrs.params.flag = 'approvalOnly'
          componentInstance.setWorkflowBusinessId($form.values.paymentApplyId || null)
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
  $bus.$emit('PaymentApplyHead')
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
// 打开开票单弹框
const $openInvoiceDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyName || !$form.values.taxKey || !$form.values.payMethod) {
    // 请先完成付款申请详情中的必填项
    return $message.warning($t('purPaymentApply.prompt8'))
  }

  $form.query('invoiceDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    let params: any = {}
    const conditions = [
      'orgId',
      'orgCode',
      'orgName',
      'organizationId',
      'organizationCode',
      'organizationName',
      'vendorId',
      'vendorName',
      'vendorCode',
      'currencyId',
      'currencyName',
      'currencyCode',
      'taxRate',
      'taxKey',
      'payMethod'
    ]
    Object.keys($form.values).forEach(key => {
      if (conditions.includes(key) && $form.values[key]) {
        params[key] = $form.values[key]
      }
    })
    $form.query('PaymentApply').get('data').invoiceDialogInitQuery = params
    $form.query('PaymentApply').get('data').invoiceDialogPageNum = 1
    $form.query('PaymentApply').get('data').invoiceDialogPageSize = 5
    $getInvoiceDialogData($form)
  })
}

// 获取开票单弹框数据
const $getInvoiceDialogData = ($form: any) => {
  const { invoiceDialogInitQuery, invoiceDialogPageNum: pageNum, invoiceDialogPageSize: pageSize } = $form.query('PaymentApply').get('data')
  $http({
    url: '/api-sup-ce/payment/paymentApplyDetail/searchOnlineInvoice',
    method: 'POST',
    data: { ...invoiceDialogInitQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        res.data.list.forEach((item: any) => {
          item.actualInvoiceAmountY = item.actualInvoiceAmountY.toFixed(8)
          item.unPaidAmount = item.unPaidAmount.toFixed(8)
        })
        $form.values.invoiceDialogTable = res.data.list
        $form.query('PaymentApply').get('data').invoiceDialogTotal = res.data.total
      }
    })
}

// 设置开票单数据
const $setInvoiceDetailData = ($form: any, $message: any) => {
  const rows = $form.query('invoiceDialog.invoiceDialogTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = $form.values.invoices.map((item: any) => item.onlineInvoiceId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.onlineInvoiceId)) {
      $form.query('invoices').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增开票单存在重复数据，已去除
    $message.warning($t('purPaymentApply.prompt9'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.invoiceDialogTable = []

  $setAmountCal($form)
}

// 计算头表金额
const $setAmountCal = ($form: any) => {
  let tableData = $form.values.billType == 'CONTRACT' ? $form.values.contracts : $form.values.invoices
  let actualInvoiceAmountY = $form.values.billType == 'CONTRACT' ? 'invoicedTaxedAmount' : 'actualInvoiceAmountY'
  let includeTaxAmount = $form.values.billType == 'CONTRACT' ? 'currentPaymentAmount' : 'payingAmount'
  if (tableData.length > 0) {
    // 发票含税总金额 = 开票单明细发票含税金额之和
    $form.values.actualInvoiceAmountY = tableData
      .map((row: any) => row[actualInvoiceAmountY])
      .reduce((p: any, c: any) => (Number(p) || 0) + (Number(c) || 0)) || 0

    // 付款含税总金额：开票单明细行的本次付款金额之和
    $form.values.includeTaxAmount = tableData
      .map((row: any) => row[includeTaxAmount])
      .reduce((p: any, c: any) => (Number(p) || 0) + (Number(c) || 0)) || 0
  } else {
    $form.values.actualInvoiceAmountY = 0
    $form.values.includeTaxAmount = 0
  }

  // 付款未税总金额=付款含税总金额/(1+税率)
  $form.values.excludeTaxAmount = ($form.values.includeTaxAmount / (1 + Number($form.values.taxRate) / 100)) || 0

  // 付款总税额=发票含税总金额*税率
  $form.values.totalTax = $form.values.includeTaxAmount - $form.values.excludeTaxAmount
}

// 打开合同履约预付款弹框
const $openAdvancePaymentDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyName || !$form.values.taxKey || !$form.values.payMethod) {
    // 请先完成付款申请详情中的必填项
    return $message.warning($t('purPaymentApply.prompt8'))
  }

  $form.query('advancePaymentDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('formWrapper').take().invoke('currentInstance').reset()
    const params = {
      querySource: 'payment',
      vendorId: $form.values.vendorId,
      vendorCode: $form.values.vendorCode,
      vendorName: $form.values.vendorName,
      invId: $form.values.organizationId,
      buId: $form.values.orgId
    }
    $form.query('PaymentApply').get('data').advancePaymentDialogInitQuery = params
    $form.query('PaymentApply').get('data').advancePaymentDialogPageNum = 1
    $form.query('PaymentApply').get('data').advancePaymentDialogPageSize = 5
    $getAdvancePaymentDialogData($form)
  })
}

// 获取合同履约预付款弹框数据
const $getAdvancePaymentDialogData = ($form: any) => {
  const { advancePaymentDialogInitQuery, advancePaymentDialogQuery, advancePaymentDialogPageNum: pageNum, advancePaymentDialogPageSize: pageSize } = $form.query('PaymentApply').get('data')
  $http({
    url: '/api-cm/contract/performInvoice/getPerInvoiceDetailPage',
    method: 'POST',
    data: { ...advancePaymentDialogInitQuery, ...advancePaymentDialogQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.advancePaymentTable = res.data.list
        $form.query('PaymentApply').get('data').advancePaymentDialogTotal = res.data.total
      }
    })
}

// 过滤查询合同履约预付款弹框数据
const $getAdvancePaymentQuerydata = (obj: any, $form: any) => {
  $form.query('PaymentApply').get('data').advancePaymentDialogPageNum = 1
  $form.query('PaymentApply').get('data').advancePaymentDialogQuery = { ...obj }
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
  const ids = $form.values.contracts.map((item: any) => item.perInvoiceDetailId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.perInvoiceDetailId)) {
      $form.query('contracts').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增合同履约付款明细存在重复数据，已去除
    $message.warning($t('purPaymentApply.prompt10'))
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
  RejectDialog,
  CPagination
}

const schema = defineSchemas({
  PaymentApply: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      contractsBackup: [],
      invoicesBackup: [],
      integrationMode: '',

      invoiceDialogInitQuery: {},
      invoiceDialogPageNum: 1,
      invoiceDialogPageSize: 5,
      invoiceDialogTotal: 0,

      advancePaymentDialogQueryForm: [
        {
          prop: 'materialCode',
          label: () => $t('purchaseDemand.itemName'), // 物料名称
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
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
          action: 'getDetail',
          ready: expression(`() => {
            return $init($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.paymentApplyId || $form.values.paymentApplyId]

            data.query['*'] = {}

            return data
        }`),
          onSuccess: expression(`(res) => {
            
          const detailData = res.data[0]
          
          // 单纯文本只读状态
          $form.readPretty =  ['view', 'approvalOnly'].includes($attrs.params.flag)
          $setButtonConfig($form)
          console.log($form.query('PaymentApply').get('data'),466)
          $form.query('PaymentApply').get('data').contractsBackup = detailData?.contracts || []
          $form.query('PaymentApply').get('data').invoicesBackup = detailData?.invoices || []
          $form.setValues({
            ...detailData
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
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: true
          },
          'business-id': expression('$form.values.paymentApplyId || null'),
          'business-type': 'paymentapply',
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
            // $form.query('PaymentApply').get('data').integrationMode = integrationMode
          }`)
        },
        properties: {
          // reject: {
          //   type: 'void',
          //   title: i18nExpression('common.toRefuse'),
          //   'x-component': 'RButton',
          //   'x-visible': expression(`$form.values.status === 'SUBMITTED' && $form.query('PaymentApply').get('data').integrationMode === 'None'`),
          //   'x-component-props': {
          //     '@click': expression(`(rowIndex) => {
          //       $form.query('rejectDialog').take().setComponentProps({ visible: true })
          //      }`)
          //   },
          //   'x-slot': 'buttonTwo'
          // },
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
      },
      // 驳回原因
      rejectDialog: {
        ...RejectDialog
      }

    }
  },
  // 新增开票单
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
    schemaKey="PaymentApplyDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
