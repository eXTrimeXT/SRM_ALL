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
  InvoiceNoticeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      invoiceDialogPageNum: 1,
      invoiceDialogPageSize: 15,
      invoiceDialogTotal: 0
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            let id = $attrs.params?.invoiceNoticeId || $values.invoiceNoticeId
            $values.invoiceNoticeId = id
            $values.vendorCode = userInfo.companyCode
            $values.vendorName = userInfo.companyName
            $values.vendorId = userInfo.companyId
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
              $form.setValues({
                ...value
              })
              $form.readPretty = $readOnly
              $form.query('fileUploads').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            }
          }`)
        },
        save: {
          action: 'saveOrUpdate',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        back: {
          type: 'void',
          'x-content': "{{$t('common.cancel')}}",
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
          'x-content': "{{$t('flowMod.temporaryView')}}",
          'x-component': 'Button',
          'x-visible': expression(`!$readOnly`),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              return $saveBill('save',$form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-content': "{{$t('bidMod.submitapprovlaFlowing')}}",
          'x-component': 'Button',
          'x-visible': expression(`!$readOnly`),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              return $saveBill('submit',$form,$queryEngine,$confirm,$message,$bus)
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
  DialogInfo: {
    ...dialogInfo
  }
})

const { emitTabRemove, emitTabAdd, t: $t, app, getCurrentUserInfo } = usePageHelper()

let attrs:any = useAttrs()

attrs.params.flag = attrs.params?.flag

const $saveBill = async (type, $form, $queryEngine, $confirm, $message, $bus) => {
  console.log('type:::', type)
  if (type === 'submit') await $form.validate()
  let invoiceNoticeStatus = type === 'save' ? 'DRAFT' : 'VENDOR_SUBMITTED'
  const form = toJS($form.values)
  const { dialogForm, table, ...rest } = form
  $queryEngine.request.baseRequest({
    type: 'InvoiceNoticeVendor',
    lang: 'zh-cn',
    loading: true,
    query: {
      '*': {}
    },
    payload: [
      {
        ...rest,
        invoiceNoticeStatus
      }
    ],
    action: 'saveOrUpdate'
  }).then(res => {
    $message.success($t('common.successSave'))
    if (type === 'save') {
      $form.values.invoiceNoticeId = res?.data[0].invoiceNoticeId
      $queryEngine.request['read']()
      return
    }
    emitTabRemove(attrs.params.tabName)
    $bus.$emit('InvoiceNoticeVendor')
  })
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
    $form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageNum = 1
    $form.query('InvoiceNoticeVendor').get('data').invoiceDialogPageSize = 15
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
  const commonData = $form.query('InvoiceNoticeVendor').get('data')
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
    $form.query('InvoiceNoticeVendor').get('data').invoiceDialogTotal = res.data.total
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
  userInfo: getCurrentUserInfo(),
  $saveBill,
  $openInvoiceDialog,
  $getInvoiceDialogData,
  $invoiceDialogQuery,
  $invoiceDialogConfirm,
  $detailTableRemove
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
