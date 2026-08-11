<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, toJS, expression, i18nExpression, generateCharFunctionExpression, connect, mapProps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { useAttrs, computed, ref } from 'vue-demi'
import fileUploads from './components/fileUploads'
import receiptInfo from './components/receiptInfo'
import detailInfo from './components/detailInfo'
import dialogInfo from './components/dialogInfo'
import CPagination from 'lib@/components/c-pagination'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'

const schema = defineSchemas({
  InvoiceNotice: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      invoiceDialogPageNum: 1,
      invoiceDialogPageSize: 15,
      invoiceDialogTotal: 0,
      isFirstApproveShow: false
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            let id = $attrs.params?.invoiceNoticeId || $values.invoiceNoticeId
            $values.invoiceNoticeId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$values.invoiceNoticeId]
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res.data[0])
            if(res?.data[0]){
              const value = res.data[0]
              workflowStatus.value = value.invoiceNoticeStatus
              $form.setValues({
                ...value
              })
              $form.query('fileUploads').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              $form.readPretty = ['approvalOnly','view'].includes($attrs.params.flag)
              $form.query('InvoiceNotice').get('data').isFirstApproveShow = judgeFirstApproveShow(value)
              updateButtonConfig($form)
            }
          }`)
        },
        save: {
          // 启用级联删除的储值行为
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
          'business-id': expression('$values.invoiceNoticeId || null'),
          'business-type': 'invoiceNotice',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            if(type === 'SAVE'){
              $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
            }else if(type === 'SUBMIT'){
              $submitBill(type, $form, $queryEngine, $confirm, $message, $bus)
            }
          }`),
          '@submit-direct': expression(`(type) => {
            if(type === 'SAVE'){
              $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
            }else if(type === 'SUBMIT'){
              $submitBill(type, $form, $queryEngine, $confirm, $message, $bus)
            }
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.params.tabName)
          }`)
        },
        properties: {
          buttonList: {
            type: 'void',
            'x-component': 'ButtonList',
            'x-slot': 'buttonOne',
            properties: {
              cancel: {
                type: 'void',
                title: "{{$t('common.cancel')}}",
                'x-component-props': {
                  '@click': expression(`() => {
                    emitTabRemove($attrs.params.tabName)
                    $bus.$emit('InvoiceNotice')
                  }`)
                }
              },
              reject: {
                type: 'void',
                title: "{{$t('common.toRefuse')}}",
                'x-visible': expression(`$form.query('InvoiceNotice').get('data').isFirstApproveShow`),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression(`() => {
                    $rejectForm($form,$message)
                  }`)
                }
              }
            }
          },
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-component-props': {
              defaultOpenPanelCount: 1
            },
            properties: {
              receiptInfo: {
                ...receiptInfo
              },
              detailInfo: {
                ...detailInfo
              },
              fileUploads: {
                ...fileUploads
              }
            }
          }
        }
      },
      RejectDialog: {
        type: 'void',
        title: "{{$t('purSettlementMod.rejectReason')}}",
        'x-decorator': 'QueryEngine',
        'x-query-engine': {
          service: 'sup-ce'
        },
        'x-component': 'RDialog',
        'x-component-props': {
          size: 'small',
          'close-on-click-modal': false,
          beforeClose: expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              const rejectReason = $form.query('*.RejectDialog.rejectReason').take().value
              console.log('values:::',rejectReason)
              if(!rejectReason){
                $message.warning($t('purInvoice.prompt1')) // 请填写驳回原因！
                closeLoading()
                return
              }
              $queryEngine.request.baseRequest({
                type:'InvoiceNotice',
                action:'firstReject',
                lang: 'zh-cn',
                loading: true,
                query: {
                  '*': {}
                },
                payload:[
                  {
                    invoiceNoticeId:$values.invoiceNoticeId,
                    rejectReason
                  }
                ]
              }).then(res => {
                $message.success($t('common.success'))
                done()
                emitTabRemove($attrs.params.tabName)
                $bus.$emit('InvoiceNotice')
              }).catch(() => { closeLoading() })
            }else{
              done()
            }
          }`)
        },
        properties: {
          rejectReason: {
            type: 'string',
            'x-component-props': {
              type: 'textarea',
              autosize: { minRows: 4 },
              placeholder: "{{$t('purSettlementMod.pleaseFillReasonForRejection')}}"
            },
            ...requiredValidatorSegment
          }
        }
      }
    }
  },
  DialogInfo: {
    ...dialogInfo
  }
})

const { emitTabRemove, emitTabAdd, t: $t, app } = usePageHelper()

let attrs:any = useAttrs()

const workflowStatus = ref('DRAFT')

attrs.params.flag = attrs.params?.flag

// 点击驳回撤回添加拟定等可编辑字段按钮控制
const isViewApproval = computed(() => !['viewApproval', 'add', 'edit'].includes(attrs.params.flag))

// 供方已提交状态或者供方创建的审批已驳回状态判断
const judgeFirstApproveShow = (value) => {
  const isView = attrs.params.flag !== 'view' // 是否是查看状态
  const vendorSubmit = value.invoiceNoticeStatus === 'VENDOR_SUBMITTED' // 供方已提交状态
  const isRejectOrWithdraw = ['REJECTED', 'WITHDRAW'].includes(value.invoiceNoticeStatus) // 审批已驳回/撤回状态
  const vendorCreated = value.userType === 'VENDOR' // 供方创建单据
  return isView && (vendorSubmit || (isRejectOrWithdraw && vendorCreated))
}

const initButtonConfig = ($form:any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = !isViewApproval.value
    componentInstance.buttonConfigInfo.submit.view = !isViewApproval.value
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
    componentInstance.setWorkflowTabDisabled(attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const updateButtonConfig = ($form:any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take()?.componentProps.componentInstance
    if (!componentInstance) return
    componentInstance.buttonConfigInfo.save.view = !$form.query('InvoiceNotice').get('data').isFirstApproveShow && !isViewApproval.value
    componentInstance.buttonConfigInfo.submit.view = !isViewApproval.value
    componentInstance.setWorkflowBusinessId($form.values.invoiceNoticeId)
    componentInstance.setWorkflowTabDisabled(attrs.params.flag !== 'approvalOnly')
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const $saveBill = async (type, $form, $queryEngine, $confirm, $message, $bus) => {
  console.log('type:::', type)
  const form = toJS($form.values)
  const { dialogForm, table, ...rest } = form
  $queryEngine.request.baseRequest({
    type: 'InvoiceNotice',
    lang: 'zh-cn',
    loading: true,
    query: {
      '*': {}
    },
    payload: [
      {
        ...rest,
        invoiceNoticeStatus: 'DRAFT'
      }
    ],
    action: 'save'
  }).then(res => {
    $message.success($t('common.successSave'))
    console.log('response', res)
    $form.values.invoiceNoticeId = res?.data[0].invoiceNoticeId
    $queryEngine.request['read']()
  })
}

const $submitBill = async (type, $form, $queryEngine, $confirm, $message, $bus) => {
  await $form.validate()
  const form = toJS($form.values)
  const { dialogForm, table, ...rest } = form
  let action = 'save'
  let invoiceNoticeStatus = rest.invoiceNoticeStatus
  if ($form.query('InvoiceNotice').get('data').isFirstApproveShow) {
    action = 'firstApprove'
  } else {
    invoiceNoticeStatus = 'SUBMITTED'
  }
  $queryEngine.request.baseRequest({
    type: 'InvoiceNotice',
    lang: 'zh-cn',
    loading: true,
    $queryEngine: {
      '*': {}
    },
    payload: [
      {
        ...rest,
        invoiceNoticeStatus
      }
    ],
    action
  }).then(res => {
    $message.success($t('common.successSubmit'))
    // if (!$form.query('InvoiceNotice').get('data').isFirstApproveShow) {
    const tabDisabled = false
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.setWorkflowBusinessId($form.values.invoiceNoticeId)
    componentInstance.setWorkflowTabDisabled(tabDisabled)
    componentInstance.setWorkflowBusinessVariables({})
    componentInstance.handlerAfter(type.toUpperCase(), () => {
      console.log('handlerAfter InvoiceNotice')
      emitTabRemove(attrs.params.tabName)
      $bus.$emit('InvoiceNotice')
    })
    // } else {
    //   emitTabRemove(attrs.params.tabName)
    //   $bus.$emit('InvoiceNotice')
    // }
  })
}

const $rejectForm = ($form, $message) => {
  $form.query('RejectDialog').take().setComponentProps({ visible: true })
}

const $detailTableRemove = ($table, rowIndex, $form) => {
  $table.remove(rowIndex)
  if (!$form.values.detailList.length) {
    $form.values.ceeaTotalTax = (0).toFixed(8)
    $form.values.ceeaTaxTotalAmount = (0).toFixed(8)
    $form.values.ceeaNoTaxTotalAmount = (0).toFixed(8)
  } else {
    getTaxCal($form)
  }
}

const $openInvoiceDialog = ($form, $message) => {
  const form = toJS($form.values)
  const { orgId, organizationId, ceeaReceiveStartDate, ceeaReceiveEndDate, vendorId, currencyCode, taxKey } = form
  const sign = !orgId || !organizationId || !ceeaReceiveStartDate || !ceeaReceiveEndDate || !vendorId || !currencyCode || !taxKey
  if (sign) {
    return $message.error($t('purSettlementMod.selectionCriteria'))
  }
  $form.query('DialogInfo').take().setComponentProps({ visible: true })
  setTimeout(() => {
    const dialogForm = $form.query('DialogInfo.dialogForm').take()?.reset() // 清除弹框查询条件
    $form.query('DialogInfo.table').take().value = []
    $form.query('InvoiceNotice').get('data').invoiceDialogPageNum = 1
    $form.query('InvoiceNotice').get('data').invoiceDialogPageSize = 15
    $getInvoiceDialogData($form)
  })
}

const $initDialogParams = ($form) => {
  let values = toJS($form.values)
  const dialogForm = $form.query('DialogInfo.dialogForm').take().value
  const filterForm = {
    vendorId: values.vendorId,
    ceeaReceiveStartDate: values.ceeaReceiveStartDate + ' 00:00:00',
    ceeaReceiveEndDate: values.ceeaReceiveEndDate + ' 23:59:59',
    currencyCode: values.currencyCode,
    taxKey: values.taxKey,
    materialId: dialogForm.materialId,
    materialCode: dialogForm.materialCode,
    materialName: dialogForm.materialName,
    orgId: values.orgId,
    orgName: values.orgName,
    ceeaEmpUsername: null,
    categoryCode: null,
    organizationId: values.organizationId,
    orderNumber: dialogForm.orderNumber,
    receiveOrderNo: dialogForm.receiveOrderNo,
    orderStatus: 'ACCEPT'
  }
  const commonData = $form.query('InvoiceNotice').get('data')
  const pageParams = {
    pageNum: commonData.invoiceDialogPageNum,
    pageSize: commonData.invoiceDialogPageSize
  }
  console.log('queryParmas:::', { ...filterForm, ...pageParams })
  return {
    ...filterForm,
    ...pageParams
  }
}

const $getInvoiceDialogData = ($form) => {
  const params = $initDialogParams($form)
  app.$http({
    url: '/api-sup-ce/ps/invoice/invoiceNoticeDetail/searchWarehousingReturnDetail',
    method: 'POST',
    data: params,
    loading: true
  }).then(res => {
    $form.query('DialogInfo.table').take().value = res.data.list || []
    $form.query('InvoiceNotice').get('data').invoiceDialogTotal = res.data.total
  })
}

const $invoiceDialogQuery = ($form) => {
  $getInvoiceDialogData($form)
}

// 计算含税金额，未税金额，税额
const getTaxCal = ($form) => {
  const taxAll = $form.values.detailList.map(item => {
    const obj = {}
    if (item.tax) {
      obj.tax = item.type === 'RETURN' ? -item.tax : Number(item.tax)
    }
    if (item.taxAmount) {
      obj.taxAmount = item.type === 'RETURN' ? -item.taxAmount : Number(item.taxAmount)
    }
    if (item.noTaxAmount) {
      obj.noTaxAmount = item.type === 'RETURN' ? -item.noTaxAmount : Number(item.noTaxAmount)
    }
    return obj
  })

  // 税额
  $form.values.ceeaTotalTax = taxAll
    .map(v => v.tax)
    .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0)).toFixed(8)
  // 含税金额
  $form.values.ceeaTaxTotalAmount = taxAll
    .map(v => v.taxAmount)
    .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0)).toFixed(8)
  // 未税金额
  $form.values.ceeaNoTaxTotalAmount = taxAll
    .map(v => v.noTaxAmount)
    .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0)).toFixed(8)
}

const $invoiceDialogConfirm = ($form, $message) => {
  const rows = $form.query('DialogInfo.table').take().componentProps.componentInstance.getCheckboxRecords()
  if (rows.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  const warehousingReturnDetailIdArr = $form.values.detailList.map(v => v.warehousingReturnDetailId)
  let flag = false
  for (const item of rows) {
    if (!warehousingReturnDetailIdArr.includes(item.warehousingReturnDetailId)) {
      $form.values.detailList.push(item)
    } else {
      flag = true
      continue
    }
  }
  if (flag) $message.warning($t('purInvoice.prompt2')) // 新增入库退货明细存在重复数据，已去除
  $form.values.detailList.forEach(item => {
    item.invoiceQuantity = item.receiveNum || 0
    item.notInvoiceQuantity = item.receiveNum || 0
  })
  getTaxCal($form)
  $form.query('DialogInfo').take().setComponentProps({ visible: false })
}

const scope = {
  emitTabRemove,
  emitTabAdd,
  app,
  $t,
  workflowStatus,
  initButtonConfig,
  updateButtonConfig,
  $saveBill,
  $submitBill,
  $rejectForm,
  $openInvoiceDialog,
  $getInvoiceDialogData,
  $invoiceDialogQuery,
  $invoiceDialogConfirm,
  $detailTableRemove,
  judgeFirstApproveShow,
  isViewApproval
}

const components = {
  CPagination,
  FileDynamic
}

</script>
<template>
  <RenderEngine
    schemaKey="InvoiceNoticeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
<style lang="scss">
.invoice_colorBold {
  color:red;
  font-weight:bold;
}
</style>
