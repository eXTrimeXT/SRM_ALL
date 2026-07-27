<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'

import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  toJS
} from '@meicloud/render-engine'

import { useAttrs } from 'vue-demi'

// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import StatementDetails from './components/collapseItem/statementDetails'
// @ts-ignore
import StatementDialog from './components/dialog/statementDialog'
// @ts-ignore
import PerformanceAssessment from './components/collapseItem/performanceAssessment'
// @ts-ignore
import PerformanceDialog from './components/dialog/performanceDialog'
// @ts-ignore
import PrepayApplyDetails from './components/collapseItem/prepayApplyDetails'
// @ts-ignore
import PrepayApplyDialog from './components/dialog/prepayApplyDialog'
// @ts-ignore
import InvoiceInfo from './components/collapseItem/invoiceInfo'
// @ts-ignore
import CInvoiceUpload from 'lib@/components/c-ocr/c-invoice-upload'
// @ts-ignore
import FilePreview from './components/filePreview'
// @ts-ignore
import FileUploads from './components/collapseItem/fileUploads'
// @ts-ignore
import CPagination from 'lib@/components/c-pagination'
// @ts-ignore
import FormWrapper from 'lib@/components/Table/FormWrapper'
// @ts-ignore
import { sysPrefix } from '@/config/ipConfig'
// @ts-ignore
import { downloadWithParam, getImgSrc } from 'lib@/utils/file'
// @ts-ignore
import { parseTime } from '@/utils'

const { emitTabRemove, t: $t, app, http: $http } = usePageHelper()

let $attrs: any = useAttrs()

const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const values = toJS($form.values)
  values.punishList.forEach((item: any) => {
    item.assessmentDate = item.assessmentDate ? parseTime(item.assessmentDate, '{y}-{m}-{d}') : ''
  })
  values.onlineInvoiceType = 'VENDOR'

  if (type === 'SAVE') {
    values.invoiceStatus = 'DRAFT'
    $submitData('save', values, $form, $queryEngine, $confirm, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      values.invoiceStatus = 'VENDOR_SUBMITTED'
      $submitData('submit', values, $form, $queryEngine, $confirm, $message, $bus)
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const $submitData = (type: string, values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  $queryEngine.request.save(values, { loading: true }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      if (type === 'submit') {
        $closePageAndRefreshListPageData($bus)
      } else {
        console.log(res)
        $bus.$emit('OnlineInvoiceVendor')
        $form.values.onlineInvoiceId = res.originalData?.records[0] || ''
        $queryEngine.request.read()
      }
    }
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  emitTabRemove($attrs.tabName)
  $bus.$emit('OnlineInvoiceVendor')
}

// 打开对账单弹框
const $openStatementDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyId || !$form.values.taxKey || !$form.values.payAccountPeriodCode) {
    return $message.warning($t('vendorMod.pleasefinishRequired'))
  }

  $form.query('statementDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    let params: any = {}
    const conditions = [
      'orgId',
      'organizationId',
      'vendorId',
      'currencyId',
      'taxKey',
      'payAccountPeriodCode'
    ]
    Object.keys($form.values).forEach(key => {
      if (conditions.includes(key) && $form.values[key]) {
        params[key] = $form.values[key]
      }
    })

    $form.query('OnlineInvoiceVendor').get('data').statementDialogInitQuery = params

    $form.query('OnlineInvoiceVendor').get('data').statementDialogPageNum = 1
    $form.query('OnlineInvoiceVendor').get('data').statementDialogPageSize = 5
    $getStatementDialogData($form)
  })
}

// 获取对账单弹框数据
const $getStatementDialogData = ($form: any) => {
  const { statementDialogInitQuery, statementDialogQuery, statementDialogPageNum: pageNum, statementDialogPageSize: pageSize } = $form.query('OnlineInvoiceVendor').get('data')
  $http({
    url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoiceDetail',
    method: 'POST',
    data: { ...statementDialogInitQuery, ...statementDialogQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.statementDialogTable = res.data.list
        $form.query('OnlineInvoiceVendor').get('data').statementDialogTotal = res.data.total
      }
    })
}

// 过滤查询对账单明细弹框数据
const $getStatementDialogQuerydata = (obj: any, $form: any) => {
  $form.query('OnlineInvoiceVendor').get('data').statementDialogPageNum = 1
  $form.query('OnlineInvoiceVendor').get('data').statementDialogQuery = { ...obj }
  $getStatementDialogData($form)
}

// 设置对账单明细数据
const $setStatementDetailsData = ($form: any, $message: any) => {
  const rows = $form.query('statementDialog.statementDialogTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = $form.values.detailList.map((item: any) => item.invoiceDetailId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.invoiceDetailId)) {
      $form.query('detailList').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增对账单存在重复数据，已去除
    $message.warning($t('agentOnlineInvoice.prompt1'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.statementDialogTable = []

  $setAmountCal($form)
}

const $setAmountCal = ($form: any) => {
  let { statementNoTax, statementTax } = $calStatementReduce($form)
  let { punishesNoTax, punishesTax } = $calPunishesReduce($form)

  // 系统含税总金额 = 对账单明细含税金额之和 - 考核单含税金额之和
  $form.values.taxTotalAmount = statementTax - punishesTax
  // 系统未税总金额 = 对账单明细未税金额之和 - 考核单未税金额之和
  $form.values.excluTaxTotalAmount = statementNoTax - punishesNoTax
  // 系统总税额 = 系统含税总金额 - 系统未税总金额
  $form.values.totalTax = $form.values.taxTotalAmount - $form.values.excluTaxTotalAmount

  // 计算未付款金额
  $setRowAmount($form)
}

// 对账单明细-未税总金额、含税金额计算
const $calStatementReduce = ($form: any) => {
  let statementNoTax = 0 // 对账单明细未税总金额之和
  let statementTax = 0 // 对账单明细含税金额之和
  if ($form.values.detailList.length > 0) {
    $form.values.detailList.forEach((item:any) => {
      let noTaxAmount = item.type === 'RETURN' ? -item.noTaxAmount : item.noTaxAmount
      let taxAmount = item.type === 'RETURN' ? -item.taxAmount : item.taxAmount
      statementNoTax += noTaxAmount
      statementTax += taxAmount
    })
  }
  return { statementNoTax, statementTax }
}

// 绩效考核明细未税总金额、含税总金额计算
const $calPunishesReduce = ($form: any) => {
  let punishesNoTax = 0 // 绩效单未税总金额
  let punishesTax = 0 // 绩效单含税总金额
  if ($form.values.punishList.length > 0) {
    $form.values.punishList.forEach((item:any) => {
      punishesNoTax += item.actualAssessmentAmountN
      punishesTax += item.actualAssessmentAmountY
    })
  }
  return { punishesNoTax, punishesTax }
}

// 已付款未付款金额计算
const $setRowAmount = ($form: any) => {
  // 已付款金额：当前开票单的本次核销金额（含税）
  $form.values.paidAmount = $form.values.advanceApplyList
    .map((v: any) => v.curWrittenOffAmount) // 本次核销金额
    .reduce((p: any, c: any) => (Number(p) || 0) + (Number(c) || 0), 0)

  // 未付款金额：系统含税金额 - 已付款金额
  $form.values.unPaidAmount = $form.values.taxTotalAmount - $form.values.paidAmount
}

// 打开绩效弹框
const $openPerformanceDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId) {
    // 请选择业务实体
    return $message.error($t('dataConfMod.msgPleaseSelectOrg'))
  }
  if (!$form.values.vendorId) {
    // 请先选择供应商
    return $message.error($t('bid_mod.setPermissionError'))
  }

  $form.query('performanceDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('OnlineInvoiceVendor').get('data').performanceDialogInitQuery = {
      organizationId: $form.values.orgId,
      ceeaVendorId: $form.values.vendorId
    }

    $form.query('OnlineInvoiceVendor').get('data').performanceDialogPageNum = 1
    $form.query('OnlineInvoiceVendor').get('data').performanceDialogPageSize = 5
    $getPerformanceDialogData($form)
  })
}

// 获取绩效弹框数据
const $getPerformanceDialogData = ($form: any) => {
  const { performanceDialogInitQuery, performanceDialogPageNum: pageNum, performanceDialogPageSize: pageSize } = $form.query('OnlineInvoiceVendor').get('data')
  $http({
    url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoicePunish',
    method: 'POST',
    data: { ...performanceDialogInitQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.performanceDialogTable = res.data.list
        $form.query('OnlineInvoiceVendor').get('data').performanceDialogTotal = res.data.total
      }
    })
}

// 设置绩效明细数据
const $setPerformanceDetailsData = ($form: any, $message: any) => {
  const rows = $form.query('performanceDialog.performanceDialogTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = $form.values.punishList.map((item: any) => item.assessmentNo)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.assessmentNo)) {
      $form.query('punishList').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增扣罚&返利存在重复数据，已去除
    $message.warning($t('agentOnlineInvoice.prompt17'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.performanceDialogTable = []

  $setAmountCal($form)
}

// 打开预付款申请弹框
const $openPrepayApplyDialog = ($form: any, $message: any) => {
  if (!$form.values.orgId || !$form.values.organizationId || !$form.values.vendorId || !$form.values.currencyId || !$form.values.taxKey || !$form.values.payAccountPeriodCode) {
    return $message.warning($t('vendorMod.pleasefinishRequired'))
  }

  $form.query('prepayApplyDialog').take().setComponentProps({ visible: true })

  setTimeout(() => {
    let params: any = {}
    const conditions = [
      'orgId',
      'organizationId',
      'vendorId',
      'currencyId',
      'taxKey',
      'payAccountPeriodCode'
    ]
    Object.keys($form.values).forEach(key => {
      if (conditions.includes(key) && $form.values[key]) {
        params[key] = $form.values[key]
      }
    })

    $form.query('OnlineInvoiceVendor').get('data').prepayApplyDialogInitQuery = params
    $form.query('OnlineInvoiceVendor').get('data').prepayApplyDialogPageNum = 1
    $form.query('OnlineInvoiceVendor').get('data').prepayApplyDialogPageSize = 5

    $getPrepayApplyDialogData($form)
  })
}

// 获取预付款申请弹框数据
const $getPrepayApplyDialogData = ($form: any) => {
  const { prepayApplyDialogInitQuery, prepayApplyDialogPageNum: pageNum, prepayApplyDialogPageSize: pageSize } = $form.query('OnlineInvoiceVendor').get('data')
  $http({
    url: '/api-sup-ce/ps/invoice/onlineInvoice/listPageOnlineInvoiceAdvanceApply',
    method: 'POST',
    data: { ...prepayApplyDialogInitQuery, pageNum, pageSize },
    loading: true
  })
    .then((res: any) => {
      if (res.data) {
        $form.values.prepayApplyDialogTable = res.data.list
        $form.query('OnlineInvoiceVendor').get('data').prepayApplyDialogTotal = res.data.total
      }
    })
}

// 设置预付款申请明细数据
const $setPrepayApplyDetailsData = ($form: any, $message: any) => {
  const rows = $form.query('prepayApplyDialog.prepayApplyDialogTable').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }

  const ids = $form.values.advanceApplyList.map((item: any) => item.advanceApplyId)
  let isRepeat = false

  rows.forEach((item: any) => {
    if (!ids.includes(item.advanceApplyId)) {
      $form.query('advanceApplyList').take((field: any) => field.value.push(item))
    } else {
      isRepeat = true
    }
  })

  if (isRepeat) {
    // 新增预付款存在重复数据，已去除
    $message.warning($t('agentOnlineInvoice.prompt3'))
  }
  // 表格实例没有暴露清除选中的方法，暂时清空数据处理
  $form.values.prepayApplyDialogTable = []

  $setRowAmount($form)
}

// 编辑发票行
const $editInvoiceRow = ($form: any, row: any) => {
  for (let item of $form.values.ocrInvoiceList) {
    if (item.fileuploadId === row.fileuploadId) {
      Object.assign(item, row)
      break
    }
  }
  $setNoTaxCal($form)
}

// 新增发票 - 保存
const $saveInvoice = ($form: any, fileList: any) => {
  $form.query('ocrInvoiceList').take((field: any) => field.value.push(...fileList))
  $setNoTaxCal($form)
}

// 发票信息计算
const $setNoTaxCal = ($form: any) => {
  if ($form.values.ocrInvoiceList.length) {
    let actualInvoiceAmountN = 0
    let actualInvoiceAmountY = 0
    let invoiceTax = 0

    $form.values.ocrInvoiceList.forEach((item:any) => {
      actualInvoiceAmountN += +item.noTaxTotalAmount
      actualInvoiceAmountY += +item.totalAmount
      invoiceTax += +item.totalTax
    })
    $form.values.actualInvoiceAmountN = actualInvoiceAmountN // 发票明细未税金额之和
    $form.values.actualInvoiceAmountY = actualInvoiceAmountY // 发票含税总金额 = 发票明细含税金额之和
    $form.values.invoiceTax = invoiceTax // 发票税额
  }
}

const $invoicePreview = ($form: any, row: any) => {
  $form.query('filePreview').take().setComponentProps({ visible: true })

  setTimeout(() => {
    $form.query('OnlineInvoiceVendor').get('data').fileSourceName = row.fileSourceName.split('.')[0]
    $form.query('OnlineInvoiceVendor').get('data').fileuploadId = row.fileuploadId
    $form.query('OnlineInvoiceVendor').get('data').filePreviewInit = true
  })
}
// 批量删除发票信息
const $batchDeleteInvoice = ($form: any, $message: any) => {
  const rows = $form.query('ocrInvoiceList').take()
    .componentProps
    .componentInstance
    .getCheckboxRecords()

  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const ids = rows.map((select: any) => select.fileuploadId)
  $form.values.ocrInvoiceList = $form.values.ocrInvoiceList.filter((item: any) => !ids.includes(item.fileuploadId))
  $setNoTaxCal($form)
}

// 下载发票
const $downloadInvoice = (row: any, $message: any) => {
  if (row.fileuploadId) {
    downloadWithParam(
      row.fileuploadId,
      row.fileSourceName,
    ).catch(() => {
      $message.error($t('components.eio.downloadFail')) // 下载失败
    })
  } else {
    throw new Error('AttachId is null.')
  }
}

const scope = {
  app,
  $attrs,
  emitTabRemove,
  parseTime,
  $submitData,
  $saveBill,
  $closePageAndRefreshListPageData,
  $openStatementDialog,
  $getStatementDialogData,
  $getStatementDialogQuerydata,
  $setStatementDetailsData,
  $openPerformanceDialog,
  $getPerformanceDialogData,
  $setPerformanceDetailsData,
  $openPrepayApplyDialog,
  $getPrepayApplyDialogData,
  $setPrepayApplyDetailsData,
  $setAmountCal,
  $setRowAmount,
  $calStatementReduce,
  $calPunishesReduce,
  $setNoTaxCal,
  $editInvoiceRow,
  $saveInvoice,
  $invoicePreview,
  $batchDeleteInvoice,
  $downloadInvoice,
  sysPrefix,
  getImgSrc
}

const components = {
  BaseInfo,
  StatementDetails,
  StatementDialog,
  PerformanceAssessment,
  PerformanceDialog,
  PrepayApplyDetails,
  PrepayApplyDialog,
  InvoiceInfo,
  CInvoiceUpload,
  FilePreview,
  FileUploads,
  CPagination,
  FormWrapper
}

// @ts-ignore
const schema = defineSchemas({
  OnlineInvoiceVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      isFirstApproveShow: false,
      statementDetailsDialogQueryForm: [
        // 对账单
        {
          prop: 'invoiceNoticeNumber',
          label: $t('purSettlementMod.statementNumber')
        },
        // 订单号
        {
          prop: 'orderNumber',
          label: $t('orderMod.buyerOrderSynergy.orderNumber2')
        },
        // 入库退货单
        {
          prop: 'receiveOrderNo',
          label: $t('purSettlementMod.inboundReturnNumber')
        }
      ],
      statementDialogInitQuery: {},
      statementDialogQuery: {},
      statementDialogPageNum: 1,
      statementDialogPageSize: 5,
      statementDialogTotal: 0,

      performanceDialogInitQuery: {},
      performanceDialogPageNum: 1,
      performanceDialogPageSize: 5,
      performanceDialogTotal: 0,

      prepayApplyDialogInitQuery: {},
      prepayApplyDialogPageNum: 1,
      prepayApplyDialogPageSize: 5,
      prepayApplyDialogTotal: 0,

      fileuploadId: null,
      filePreviewInit: false,

      fileList: [],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'sup-ce',
        fileFunction: 'SUPPLIER_INVOICE_SETTLE',
        fileType: 'images'
      }
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'

            return $attrs.params.row?.onlineInvoiceId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.onlineInvoiceId || $form.values.onlineInvoiceId]

            data.query['*'] = {}

            return data
         }`),
          onSuccess: expression(`(res) => {
          $form.setValues({
              ...res.data[0]
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
          'x-content': i18nExpression('common.cancel'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`)
        },
        close: {
          type: 'void',
          'x-content': i18nExpression('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = $form.readPretty
            }`)
        },
        save: {
          type: 'void',
          'x-content': i18nExpression('flowMod.temporaryView'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveBill('SAVE', $form, $queryEngine, $confirm, $message, $bus)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`)
        },
        submit: {
          type: 'void',
          'x-content': i18nExpression('bidMod.submitapprovlaFlowing'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveBill('SUBMIT', $form, $queryEngine, $confirm, $message, $bus)
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty
            }`)
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          // 单据信息
          baseInfo: {
            ...BaseInfo
          },
          // 对账单明细
          statementDetails: {
            ...StatementDetails
          },
          // 绩效考核
          performanceAssessment: {
            ...PerformanceAssessment
          },
          // 预付款申请明细
          prepayApplyDetails: {
            ...PrepayApplyDetails
          },
          // 发票信息
          invoiceInfo: {
            ...InvoiceInfo
          },
          // 附件
          fileUploads: {
            ...FileUploads
          }
        })
      }
    }
  },
  // 对账单明细选择
  statementDialog: {
    ...StatementDialog
  },
  // 绩效考核明细选择
  performanceDialog: {
    ...PerformanceDialog
  },
  // 预付款申请明细选择
  prepayApplyDialog: {
    ...PrepayApplyDialog
  },
  // 发票预览
  filePreview: {
    type: 'void',
    title: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').fileSourceName + \'- 预览\''),
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'xLarge',
      footer: true,
      okButtonProps: false,
      beforeClose: expression(`(done, type) => {
        $form.query('OnlineInvoiceVendor').get('data').filePreviewInit = false
        done()
      }`)
    },
    properties: {
      purchaseOrder: {
        type: 'void',
        'x-component': 'FilePreview',
        'x-component-props': {
          init: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').filePreviewInit'),
          fileuploadId: expression('$form.query(\'OnlineInvoiceVendor\').get(\'data\').fileuploadId')
        }
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="VendorOnlineInvoiceDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
.the-purInvoice-detail {
  .render-pix-form-item-label-tooltip {
    align-items: baseline;
  }
}
</style>
