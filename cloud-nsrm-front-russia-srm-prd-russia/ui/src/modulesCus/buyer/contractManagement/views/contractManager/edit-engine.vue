<script setup lang="ts">
import { useAttrs, type Vue, nextTick } from 'vue-demi'
import { useDebounceFn } from '@vueuse/core'
import { RenderEngine } from 'lib@/components/render-engine'
import ApprovalProcess from 'modc@/components/approval-process'
import {
  editTableFormItemValid,
  requiredValidatorSegment,
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  formGridSegment,
  radioGroupByYOrNSegment,
  checkboxByYOrNSegment,
  selectByYOrNSegment,
  feedbackLayoutIsPopover
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  action,
  queryFieldValueExpression,
  ViewModel,
  FieldModel,
  toJS,
  createViewModel,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

// @ts-ignore
import cloneDeep from 'lodash/cloneDeep'
// @ts-ignore
import { FILE_UPLOAD } from '@/api/common'
// @ts-ignore
import { contractManagement } from 'modcb@/contractManagement/api/index'
// @ts-ignore
import { DictClass, createDictClass } from '@/library/utils/dict/dict-utils'
// @ts-ignore
import { loadJS } from '@/utils';
// @ts-ignore
import Parser from 'modcb@/contractManagement/views/contractManager/parser'
// @ts-ignore
import CFillProgress from 'lib@/components/c-fill-progress'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { numericUppercase } from 'lib@/utils/number'
import { RuleRegExp } from '@meicloud/validator'

import Secret from '@/utils/secret'

const { http, emitTabRemove, app, t, eqY, eqN, buyer, vendor } = usePageHelper()
// TODO 封装一个可以传递泛型的
const attrs: any = useAttrs()

const form = createViewModel()
const buttonShowFlag = window.location.href.indexOf('flowTaskViewBase')

const $wrapper = (options, $root) => {
  return Object.keys(options).reduce((acc, key) => {
    acc[key] = options[key].bind($root)
    return acc
  }, {})
}
// 控制契约锁盖章
const $showLockSeal = ($form: any) => {
  let visible = false
  if (!$form.values.stampContractFileuploadId) {
    return false
  }
  const partnerData = $form.query('partnerData').take()?.value || []
  if (partnerData.length) {
    // 甲方授权人是否是当前用户
    const partA = partnerData.find(parter => parter.partnerType == '甲方')
    if (partA && partA.extEmployeeNumber === app.$store.getters.userInfo.username) {
      // 判断签约顺序
      const extStampSignSeq = $form.values.extStampSignSeq
      if (extStampSignSeq === 'COMPANY_FIRST') {
        // 甲方先签
        visible = partA.extStampStatus === 'UNSTAMP'
      } else if (extStampSignSeq === 'VENDOR_FIRST') {
        // 乙方先签
        const partyB = partnerData.find(parter => parter.partnerType == '乙方')
        visible = partyB.extStampStatus === 'STAMP' && partA.extStampStatus === 'UNSTAMP'
      }
    }
  }
  return visible
}
const $preOptions = {
  nextStep: async function preNextStepHandler() {
    let checkResult = true
    const { data } = await http({
      url: '/api-cm/contractHead/ext/checkVendor',
      method: 'GET',
      params: {
        vendorId: this.$form.values.vendorId
      }
    })
    const {
      isBlack,
      focusFlag
    } = data
    if (isBlack === 'Y') {
      app.$message.warning(t('cusEntry.tipMessage.isBackgMsg'))
      return false
    }
    if (focusFlag === 'Y') {
      await app.$confirm(t('cusEntry.tipMessage.isFocusFlag'), '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        checkResult = true
      }).catch(() => {
        checkResult = false  
      })
    }
    if (!checkResult) {
      return false
    }
    if (this.$form.values.contractStatus === 'SUPPLIER_CONFIRMED') {
      return true
    }
    const validResult =  await $saveBill(this.$form, this.$queryEngine, this.$bus, this.$eqY, 'submit')
    return validResult
  }
}

const viewUpdateButtonSave = ($form: any) => {
  if (attrs.params.flag == 'view') {
    return false
  }
  return $form.values.needVendorConfirm != 'Y' &&
    (
      ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
      (attrs.params ? (attrs.params.flag === 'add' || ((attrs.params.flag === 'termination' || attrs.params?.contractType == 'TERMINATION') && $form.values.contractStatus === 'ARCHIVED')) : null)
    )
}

const viewUpdateButtonsubmit = ($form: any) => {
  if (attrs.params.flag == 'view')  {
    return false
  }

  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

  if (attrs.params?.flag === 'termination' || attrs.params?.contractType == 'TERMINATION') {
    if (buyer() && ['SUPPLIER_CONFIRMED'].includes(attrs.params?.row?.contractStatus)) {
      return true
    }
    return (
      $form.values.needVendorConfirm != 'Y' && buyer() &&
      (['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
        (attrs.params ? ((attrs.params.flag === 'termination' || attrs.params?.contractType == 'TERMINATION') && $form.values.contractStatus === 'ARCHIVED') : null))
    )
  }

  if (componentInstance.workflowParamsInfo.integrationMode !== 'None') { // 有工作流
    return buyer() && ((
      $form.values.needVendorConfirm != 'Y' &&
      (['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
        (attrs.params ? attrs.params.flag === 'add' : null))
    ) || ( $form.values.needVendorConfirm === 'Y') && ['SUPPLIER_CONFIRMED'].includes($form.values.contractStatus))
  } else {
    if ($form.values.needVendorConfirm != 'Y') {
      if (buyer() &&
        (['DRAFT', 'WITHDRAW', 'REJECTED', 'SUPPLIER_CONFIRMED'].includes($form.values.contractStatus) ||
          (attrs.params ? attrs.params.flag === 'add' : null))) {
        return true
      } else {
        return false
      }
    } else {
      if (buyer() &&
        (['SUPPLIER_CONFIRMED'].includes($form.values.contractStatus))) {
        return true
      } else {
        return false
      }
    }
  }
}

const disabledUpdateButton = ($form: any) => {
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  const state = $form.values.contractStatus
  if (componentInstance.workflowParamsInfo.integrationMode == 'None' && ['SUPPLIER_CONFIRMED'].includes(state)) {
    return false
  }
  if (attrs.params.flag === 'view') {
    return true
  }
  if (attrs.params.flag === 'add' || attrs.params.flag === 'termination') {
    return false
  }
  if (!buyer() && state === 'SUPPLIER_CONFIRMING') {
    return false
  }
  if (state && ['DRAFT', 'REFUSED', 'WITHDRAW'].includes(state)) {
    return false
  }
  return false
}

const updateWorkflowconfig = (componentInstance, businessId, tabDisabled, businessVariables) => {
  componentInstance.setWorkflowBusinessId(businessId)
  componentInstance.setWorkflowTabDisabled(tabDisabled)
  componentInstance.setWorkflowBusinessVariables(businessVariables)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    $form.query('state').get('data').showButtonConfig = {
      saveAndNextStep : $form.values.needVendorConfirm === 'N'
    }
  }, 50)
}

const $compileMarkedContent = async ($form: ViewModel, modeContent: string, isInit = true ) => {
  const markedContentNode = document.getElementById('markedContent')

  if (!markedContentNode) {
    return
  }

  markedContentNode.innerHTML = ''

  // @ts-ignore
  const initialize = !isInit ? isInit : attrs.params.flag === 'add'

  // 替换分页符
  const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
  modeContent = modeContent.replace(breakPageMatcher, () => {
    return '<div class="breakPage" style="break-after: page;"></div>'
  })

  if (modeContent) {
    const { vueTemplate, elementCodes } = Parser.replacer(
      modeContent,
      initialize
    )

    const elemKeys = !isInit
      ? elementCodes
      : $form.values.modelLines.reduce((obj, i) => {
        const { modelKey, modelValue } = i
        let value = modelValue
        try {
          if (isNaN(modelValue)) {
            value = JSON.parse(modelValue)
          }
        } catch (e) {

        }
        obj[modelKey] = value
        return obj
      }, {})

    let extContentFinal = generateComponent({
      node: markedContentNode,
      $form,
      html: vueTemplate,
      elemKeys,
      onInit: (componentInstance) => {
        $form.query('state').take(field => {
          field.setData({
            contractTemplateComponentInstance: markRaw(componentInstance)
          })
        })
      },
    })
    console.log('extContentFinal',extContentFinal)
    $form.query('state').get('data').extContentFinal = extContentFinal
  }
}

const initData = ($form: any) => {
  $form.values.contractType = attrs.params.contractType // 合同类型

  if (attrs.params.flag === 'add') {
    $form.values.sourceType = 'MANUALLY_CREATED'
    // MIAN_CONTRACT_ALTER 变更
    if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes($form.values.contractType)) {
      $form.values.contractHeadId = attrs.params.rowId
    } else {
      return false
    }
      if($form.values.contractType === 'SUPPLEMENTAL_AGREEMENT'){
        app.$http({
          url:'/api-cm/contractHead/ext/generateExtContractCode',
          method:'GET',
          params:{
            contractHeadId:attrs.params.rowId
          }
        }).then(response => {

        })
      }

  } else {
    $form.values.contractHeadId = attrs.params.row.contractHeadId
  }
  return true
}

const $cancel = ($bus: Vue) => {
  // @ts-ignore
  emitTabRemove(attrs.params?.flag === 'add'
    ? 'contractInformation'
    // @ts-ignore
    : ((attrs.params?.flag === 'termination' || attrs.params?.contractType === 'TERMINATION')
      ? 'termination' + attrs.params?.row.contractName
      : 'contractInformation' + attrs.params?.row.contractName)
  )

  $bus.$emit('ContractHead')
}

const $getPdfFile = async (flag = false) => {
  // let htmlBody = app.$refs.printContent.innerHTML
  let htmlBody1 = document.getElementById('printContent')?.innerHTML ?? ''

  let htmlBody = htmlBody1.replace('disabled="disabled"', ' ')

  const res = await http.post('/egg/upload', {
    options: {
      format: 'a4',
      margin: {
        left: '1cm',
        top: '1cm',
        right: '1cm',
        bottom: '1cm'
      }
    },
    htmlString: '<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">' + htmlBody + '</div>'
  }, { responseType: 'arraybuffer', loading: true, baseURL: '', returnDirectly: true })

  const blob = new Blob([res.data], { type: 'application/pdf' })

  const formData = new FormData()
  formData.append('file', blob, 'myfile.pdf')

  // @ts-ignore
  const pdf = await http.post('/api-cm/contractHead/ext/pdfAddPage', formData, {
    headers: {
      contentType: 'form-data'
    },
    responseType: 'arraybuffer',
    loading: true,
    returnDirectly: true
  })

  let blobs = new Blob([pdf.data], { type: 'application/pdf' })
  if (flag) {
    const iframeNode = document.getElementById('pdfIframe') as HTMLIFrameElement
    if (iframeNode) {
      iframeNode.src = URL.createObjectURL(blobs)

      setTimeout(() => {
        iframeNode.contentWindow!.print()
      }, 1000)
    }
  }
  return blobs
}

// 算合同总金额（含税）
const $calcIncludeTaxAmount = useDebounceFn(($form: ViewModel) => {
  if ($form.values.contractStatus === 'ARCHIVED') {
    return
  }

  const materialListData = $form.query('materialListData').get('value')

  if (!materialListData?.length) {
    return
  }

  const totalAmount = materialListData.reduce((sum, item) => {
    return Number(sum) + Number(item.amount)
  }, 0)

  if (!Number.isNaN(totalAmount)) {
    // @ts-ignore
    $form.values.totalItems = totalAmount + '元'
    $form.values.totalMaterialAmount = numericUppercase(totalAmount)
    $form.query('includeTaxAmount').take((field: FieldModel) => {
      field.value = Number(totalAmount).toFixed(2)
    })
  }
}, 1000)

const $archive = async ($form, $queryEngine, $bus, $message) => {
  const { stampAnnexes, contractHeadId } = $form.values
  if(!stampAnnexes || !stampAnnexes.length){
    return $message.warning('盖章附件信息不能为空')
  }
  await $queryEngine.request.save({contractHeadId, stampAnnexes})
  const response = await app.$http({
    url:'/api-cm/contractInterface/ext/contractFiling',
    method:'GET',
    params: {
      contractHeadId
    },
    loading: true
  })
  if(response){
    if($form.values.isFrameworkAgreement !== 'Y'){
      // $message.success('归档成功，请及时创建合同履约计划')
      $message({
        type:'success',
        message:'归档成功，请及时创建合同履约计划',
        duration: 5000
      })
    }else{
      $message.success(t('common.success'))
    }
    $cancel($bus)
  }
}

const $isTermination = attrs.params?.flag === 'termination' || attrs.params?.contractType === 'TERMINATION'

const $handleSubmit =
  async ($form: ViewModel, $queryEngine, $bus, type = 'submit') => {
    // 暂存数据
    let validResult = true
    const temporaryData = (type === '' || type === 'savePublish')

    const run = async (values: Record<string, any>) => {
      // if(values.sourceType !== 'BID_NOTICE'){
        $calcIncludeTaxAmount($form)
      // }

      const state = $form.query('state').get('data')

      const workFlow = ['approval', 'publish'].includes(type)

      if (eqY(values.ceeaIsPortableContract) && workFlow) {
        if (!temporaryData && values.includeTaxAmount > 20000) {
          // "合同金额大于2万，不能设置为便捷合同，已自动帮您修改为非便捷合同！"
          // @ts-ignore
          app.$message.warning(t('contractMod.msgContractManage[10]'))
          values.ceeaIsPortableContract = 'N'
          validResult = false
          return
        }
      }

      if (!temporaryData && values.effectiveDateFrom && new Date(values.effectiveDateFrom.replace(/-/g, '/')) > new Date(values.effectiveDateTo.replace(/-/g, '/'))) {
        validResult = false
        return app.$message.error(t('合同有效期有误') as string)
      }

      // TODO 放置在验证器去处理
      // 校验是否有多个甲方
      let bolpartnerType = 0
      let bolpartnerType2 = 0
      values.partnerData.forEach(item => {
        if (item.partnerType == '甲方') {
          bolpartnerType++
        }

        if (item.partnerType == '乙方') {
          bolpartnerType2++
        }
      })

      if (!temporaryData && bolpartnerType > 1) {
        validResult = false
        return app.$message.error(t('只能有一个甲方') as string)
      }
      if (!temporaryData && bolpartnerType2 > 1) {
        validResult = false
        return app.$message.error(t('只能有一个乙方') as string)
      }
      if(!temporaryData){
        for(let item of values.partnerData){
          if(item.phone && !RuleRegExp.Mobile.test(item.phone)){
            validResult = false
            return app.$message.error(`合作伙伴-${item.partnerType}联系电话格式不正确`)
          }
        }
      }

      // 校验文件是否上传
      let isNull = !values.fileUploads.length || values.fileUploads.some(i => !i.fileuploadId)

      if (!temporaryData && isNull && ($isTermination || workFlow && values.modelEnable == 'N')) {
        validResult = false
        return app.$message.error(t('contractMod.msgContractManage[11]') as string)
      }

      // 合同附件 - 美国成分分析表 AMERICAN_COMPOSITION、阳光协议 SUNSHINE_PROTOCOL 必填
      // 暂存、合作终止、补充协议不校验附件
      if(!temporaryData && !$isTermination && values.contractType !== 'SUPPLEMENTAL_AGREEMENT'){
        let fileMessage1 = '合同附件信息-请上传美国成分分析表附件'
        let fileMessage2 = '合同附件信息-请上传阳光协议附件'
        // if(values.fileUploads.every(item => item.fileType !== 'AMERICAN_COMPOSITION')){
        //   validResult = false
        //   return app.$message.error(fileMessage1)
        // }
        if(values.fileUploads.every(item => item.fileType !== 'SUNSHINE_PROTOCOL')){
          validResult = false
          return app.$message.error(fileMessage2)
        }
        for(let item of values.fileUploads){
          // if(item.fileType === 'AMERICAN_COMPOSITION' && !item.fileuploadId){
          //   validResult = false
          //   return app.$message.error(fileMessage1)
          // }
          if(item.fileType === 'SUNSHINE_PROTOCOL' && !item.fileuploadId){
            validResult = false
            return app.$message.error(fileMessage2)
          }
        }
      }

      const modelLines = []
      const elemKeys = state.contractTemplateComponentInstance?.elemKeys

      if (elemKeys) {
        for (const [key, value] of Object.entries(elemKeys)) {
          try {
            const modelLineId = (
              values.modelLines.find(i => key === i.modelKey) || {}
            ).modelLineId
            if (value) {
              if (Array.isArray(value)) {
                modelLines.push({
                  modelLineId: modelLineId || null,
                  modelKey: key,
                  modelValue: JSON.stringify(value)
                })
              } else {
                modelLines.push({
                  modelLineId: modelLineId || null,
                  modelKey: key,
                  modelValue: value
                })
              }
            }

          } catch (e) {
            console.log(e)
          }
        }
      }

      let finalHTML = null
      try {
        finalHTML = Parser.unReplacer(document.getElementById('markedContent')?.innerHTML)
        // finalHTML = document.getElementById('markedContent')?.innerHTML
      } catch {
        //
      }

      let extContentFinal = $form.query('state').get('data').extContentFinal

      if (workFlow && eqY(state.ceeaIfVirtual)) {
        if (!temporaryData && !values.frameworkAgreementCode) {
          validResult = false
          return app.$message.error(t('contractMod.msgContractManage[12]') as string)
        }
      }

      if (eqN(values.isFrameworkAgreement) && workFlow) {
        const totalPercent = values.payPlanData.reduce((sum, item) => {
          return sum + Number(item.paymentRatio)
        }, 0)

        const totalMoney = values.payPlanData.reduce((sum, item) => {
          return sum + Number(item.stagePaymentAmount) * 1000
        }, 0)

        // if (!temporaryData && totalPercent !== 100 && !$isTermination) {
        //   // app.$message.error(app.$t('contractMod.msgContractManage[7]')) // 付款比例之和不能大于100！
        //   app.$message.error('付款比例之和必须等于100！')
        //   return
        // }
        if (!$isTermination) {
          const includeTaxAmount = Number($form.query('.includeTaxAmount').take().value) * 1000
          if (!temporaryData && includeTaxAmount != totalMoney && eqN(values.isFrameworkAgreement)) {
            app.$message.error('阶段付款金额总和应与合同总金额相等')
            validResult = false
            return false
          }
        }


        if (!temporaryData && !values.materialListData.length && !$isTermination) {
          validResult = false
          return app.$message.error(t('contractMod.msgContractManage[27]') as string)
        }

        if (!temporaryData && !values.payPlanData.length && workFlow && !$isTermination) {
          validResult = false
          return app.$message.error(t('contractMod.msgContractManage[15]') as string)
        }

        const payPlanDataBol = values.payPlanData.some(e => !(e.paymentPeriod && e.paymentStage && e.payExplain && e.dateNum  && e.plannedPaymentDate && e.payMethod))

        if (!temporaryData && payPlanDataBol && workFlow && !$isTermination) {
          app.$message.error(t('contractMod.payPlanDataBol') as string)
          validResult = false
          return
        }

        for(let item of values.payPlanData){
          if(['HONOUR','WIRE_AND_HONOUR'].includes(item.payMethod) && !item.extAcceptanceDate){
            app.$message.error('付款计划-付款方式为承兑、电汇+承兑时，承兑期限必填')
            validResult = false
            return
          }
          if(item.payMethod === 'WIRE_AND_HONOUR' && Number(item.extAcceptanceRatio) + Number(item.extWireTransferRatio) != 100){
            app.$message.error('付款计划-付款方式为电汇+承兑时，承兑比例和电汇比例之和必须等于100')
            validResult = false
            return
          }
        }

        let materialListDataBol = values.materialListData.some(e => !(e.untaxedPrice && e.contractQuantity && (e.taxRate != null && e.taxRate !== '')))
        if (!temporaryData && materialListDataBol && workFlow && !$isTermination) {
          app.$message.error(t('contractMod.materialListDataBol') as string)
          validResult = false
          return
        }
      }

      const { fileUploads, payPlanData, partnerData, materialListData, ...rest } = toJS(values)

      if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT', 'TERMINATION'].includes($form.values.contractType) && (attrs.params?.flag == 'add' || attrs.params?.flag == 'termination')) {
        values.fileUploads.forEach((e) => {
          delete e.annexId
          delete e.contractHeadId
        })
        values.payPlanData.forEach((e) => {
          delete e.payPlanId
          delete e.contractHeadId
        })
        materialListData.forEach((e) => {
          delete e.contractMaterialId
          delete e.contractHeadId
        })
        values.partnerData.forEach((e) => {
          delete e.partnerId
          delete e.contractHeadId
        })
        modelLines.forEach((e) => {
          delete e.modelLineId
        })
      }
      // 校验供应商
      if (workFlow) {
        let checkResult = true
        const { data } = await http({
          url: '/api-cm/contractHead/ext/checkVendor',
          method: 'GET',
          params: {
            vendorId: $form.values.vendorId
          }
        })
        const {
          isBlack,
          focusFlag
        } = data
        if (isBlack === 'Y') {
          app.$message.warning(t('cusEntry.tipMessage.isBackgMsg'))
          return false
        }
        if (focusFlag === 'Y') {
          await app.$confirm(t('cusEntry.tipMessage.isFocusFlag'), '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            checkResult = true
          }).catch(() => {
            checkResult = false  
          })
        }
        if (!checkResult) {
          return false
        }
      }
      const data = {
        ...rest,
        modelLines,
        annexes: values.fileUploads,
        payPlans: values.payPlanData,
        contractMaterials: materialListData,
        contractPartners: values.partnerData,
      } as any

      if (finalHTML) {
        data.content = finalHTML
      }

      if(extContentFinal){
        data.extContentFinal = extContentFinal.innerHTML
      }

      data.isDeleteLine = 'Y'
      data.isSavePerCheck = 'Y'

      // 采购商
      if (buyer()) {
        if (type === 'approval') {
          data.contractStatus = data.contractStatus ?? 'DRAFT'
        } else if (type === 'publish') {
          data.contractStatus = 'SUPPLIER_CONFIRMING'
        } else {
          data.contractStatus = 'DRAFT'
        }
      } else {
        if (type === 'SUPPLIER_CONFIRMING') {
          // 供应商确认
          data.contractStatus = 'SUPPLIER_CONFIRMED'
        } else if (type === 'SUPPLIER_REFUSE') {
          // 供应商已驳回
          data.contractStatus = 'SUPPLIER_REJECTED'
        } else {
          // 供应商已确认
          data.contractStatus = 'SUPPLIER_CONFIRMING'
        }
      }

      const resetContractHeadId = (obj: any) => {
        // 删除原主表 id 值
        if (obj?.contractHeadId) {
          obj.contractHeadId = null
        }
      }

      // 新增合同单，并变更合同类型
      if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT', 'TERMINATION'].includes(attrs.params.contractType)) {
        data.mainContractNo = attrs.params.mainContractNo
        if (attrs.params.flag !== 'edit') {
          data.contractType = attrs.params.contractType
          data.contractOldCode = values.contractNo
          data.ceeaContractOldId = values.contractHeadId
          resetContractHeadId(data)

            ;['modelLines', 'annexes', 'payPlans', 'contractMaterials', 'contractPartners', 'basisAnnexes','stampAnnexes','operationLogs']
              .forEach((relationTable) => {
                const relationTableData = data[relationTable]
                if (Array.isArray(relationTableData) && relationTableData.length) {
                  data[relationTable].forEach(item => {
                    resetContractHeadId(item)
                  })

                  return
                }

                resetContractHeadId(relationTableData)
              })
        }
      }

      if (attrs.params.termination) {
        data.contractType = 'TERMINATION'

        // 合同终止
        if (attrs.params?.flag !== 'edit' && type === 'SUPPLIER_CONFIRMING') {
          data.contractStatus = 'TERMINATED'
        }
      }

      const res = await $queryEngine.request.save(data, {
        customizeAction: vendor() && type === 'publish' ? 'publish' : undefined
      })
      const contractHeadId = res.originalData.records[0] || values.contractHeadId
      $form.values.contractHeadId = contractHeadId
      $form.setValues({
        contractHeadId,
        ...res.data[0],
        contractType: attrs.params.contractType
      })

      if (type === 'approval') {
        const tabDisabled = !['SUPPLIER_CONFIRMED', 'REJECTED', 'WITHDRAW', 'APPROVAL', 'UNDER_REVIEW', 'UN_ARCHIVED', 'SIGNATUREING', 'ARCHIVED', 'TERMINATED', 'ABANDONED'].includes(values.contractStatus) || values.contractStatus === 'DRAFT'
        const componentInstance = $form.query('.SchemaWorkflow').take()!.componentProps.componentInstance
        componentInstance.setWorkflowBusinessId(contractHeadId)
        componentInstance.setWorkflowTabDisabled(tabDisabled)
        componentInstance.setWorkflowBusinessVariables({})
        componentInstance.handlerAfter('SUBMIT')

        return
      }

      // type === '' 是暂存
      if (['publish', 'SUPPLIER_CONFIRMING', 'SUPPLIER_REFUSE'].includes(type)) {
        $cancel($bus)
        return
      }
    }
    if (type && type !== 'savePublish') {
      await $form.validate()
      await run($form.values)
      // $form.submit(run)
    } else {
      await run($form.values)
    }
    return validResult
  }

const $handlePreview = ($form: ViewModel) => {
  const state = $form.query('state').get('data')

  if (state.contractTemplateComponentInstance) {
    state.contractTemplateComponentInstance.editable = false
  }

  state.contenteditable = false
}

const $saveBill = async ($form: any, $queryEngine: any, $bus: any, $eqY: any, type = '') => {
  // 校验是否编辑合同模式，如果是的话要求转成预览模式
  const modelHeadId = $form.query('modelHeadId').get('value')
  const contractType = $form.query('contractType').get('value')
  const modelEnable = $form.query('modelEnable').get('value')
  const state = $form.query('state').get('data')
  const contractStatus = $form.query('contractStatus').get('value')
  if (!!modelHeadId
    && (state.contractTemplateComponentInstance && state.contractTemplateComponentInstance.editable)
    && (contractType === 'MIAN_CONTRACT_ADD' || contractType === 'MIAN_CONTRACT_ALTER' || contractType === 'SUPPLEMENTAL_AGREEMENT')
    && (contractStatus === 'DRAFT' || contractStatus === '' || contractStatus === null)
    && $eqY(modelEnable) && attrs.params.flag !== 'view') {
    app.$message.warning(t('现在合同为编辑模式,请切换为浏览模式'))
    return false
  }
  $handlePreview($form)
  const validResult = await $handleSubmit($form, $queryEngine, $bus, type)
  return validResult
  // setTimeout(() => {
  //   $handleSubmit($form, $queryEngine, $bus, type)
  // }, 50)
}

const generateComponent = ({ node, $form, html, elemKeys, onInit }: any) => {
  const mergeForm = cloneDeep($form.values) // toJS 会跳过 markRaw 过的响应式数据

  const $el = Parser.generateComponent({
    html,
    elemKeys,
    onInit,
    context: {
      mergeForm,
      partnerData: mergeForm.partnerData ?? [],
      materialEditableRows: mergeForm.materialListData ?? []
    },
    wrapper: node
  })
  node.appendChild($el)
  return node
}

const $refuseDialogConfirm = async (values,$form,$queryEngine,$message,$bus,closeLoading,done) => {
  const response = await $queryEngine.request.save({
    contractStatus:'SUPPLIER_REJECTED',
    contractHeadId:$form.values.contractHeadId,
    ...values
  }).finally(() => { done() })
  if(response){
    $message.success(t('common.success'))
    $cancel($bus)
  }
}

const $approvalHanlder = (type, $form: any, $queryEngine: any, $bus: any, $eqY: any) => {
  switch (type) {
  case 'save':
    // 判断是否供应商已确认
    if ($form.values.contractStatus === 'SUPPLIER_CONFIRMED') {
      app.$message.warning(t('cusEntry.approval.supplierConfirmed'))
      return false
    }
    $saveBill($form, $queryEngine, $bus, $eqY, '')
    break
  case 'submit':
    $cancel($bus)
    break
  case 'abandon':
    $cancel($bus)
    break
  case 'recall':
    $cancel($bus)
    break
  case 'pass':
    $cancel($bus)
    break
  default:
    break
  }
}
// 契约锁盖章
const $lockSealHandler = ($form: any) => {
  const {
    stampContractFileuploadId,
  } = $form.values
  const {
    contactName,
    partnerName,
    extEmployeeNumber
  } = $form.query('partnerData').take().value.find(item => item.partnerType == '甲方')
  app.$http({
    url: `/api-pj/external/ContractLock/signUrl2?contractId=${stampContractFileuploadId}&tenantName=${partnerName}&tenantType=COMPANY&receiverNumber=${extEmployeeNumber}`,
    method: 'POST',
    loading: true
  }).then(res => {
    if (res.data) {
      window.open(res.data, '_blank')
    }
  })
}

const $onlyOfficeView = async (row: any, $form: any, isApproval: boolean) => {
  if (!row.fileuploadId) {
    return
  }
  let list = []
  if (isApproval) {
    const res = await app.$http({
      url: `/api-pj/bpmFlow/findTaskListNew?businessType=CONTRACT&businessId=${$form.values.contractHeadId}`,
      method: 'GET',
      loading: true,
    })
    list = res.data || []
  }
  const flag = list.find((item: any) => {
    return item.actionName === '正在办理' && item.activityName === '经办人清稿' && item.createUserName === app.$store.getters.userInfo.nickname
  })
  app.$http({
    url: `/api-file/edit/onlyoffice/api/edit?fileuploadId=${Secret.getValue(row.fileuploadId)}`,
    method: 'GET',
    loading: true
  }).then(res => {
    if (res.data) {
      const pathname = window.location.pathname
      const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
      const { storage, key, title, fileType } = res.data.document
      const { callbackUrl } = res.data.documentEditParam
      const callbackUrlParams = callbackUrl + `&fileuploadId=${Secret.getValue(row.fileuploadId)}`
      window.open(`${systemUrl}/#/onlyoffice?${flag ? 'comment=clear&revision=clear&' : ''}url=${btoa(encodeURIComponent(storage))}&key=${key}&title=${title}&fileType=${fileType}&callbackUrl=${btoa(encodeURIComponent(callbackUrlParams))}`, '_blank')
    }
  })
}

const scope = {
  $onlyOfficeView,
  $lockSealHandler,
  $wrapper,
  $showLockSeal,
  $preOptions,
  $attrs: attrs,
  // 通用方法
  $eqY: eqY,
  $eqN: eqN,
  app,
  $reactiveAction: action,

  $dictClass: DictClass,
  $taxDictClass: createDictClass({ 'tax': [] }),

  // 外部传参
  // TODO 这部分代码后边有应该要优化掉
  $isAdd: attrs.params?.flag === 'add',
  $isTermination,
  // @ts-ignore
  $illegal: attrs.params?.flag,
  // @ts-ignore
  $illegalNotView: attrs.params?.illegal !== 'view',
  // @ts-ignore
  $readOnly: Boolean(attrs.params?.isReadOnly),
  // @ts-ignore
  $jumpLogin: !attrs.params?.jumpLogin,
  $buttonType: attrs.params?.buttonType ? attrs.params?.buttonType : buttonShowFlag > -1 ? 'approve' : '',

  // api
  $contractManagement: contractManagement,
  numericUppercase,

  $calcIncludeTaxAmount,
  $handleSubmit,
  $cancel,
  $compileMarkedContent,
  $refuseDialogConfirm,
  // 内部业务处理
  $uploadPDF: async () => {
    const blob = await $getPdfFile()
    const file = new window.File([blob], 'myfile.pdf', {
      type: 'application/pdf'
    })
    const data = {
      file,
      uploadType: 'DEF',
      sourceType: 'WEB_APP',
      fileModular: 'api-cm',
      fileFunction: 'contractInformation',
      fileType: 'pdf'
    }
    const formData = new FormData()
    formData.append('file', file)
    for (const [key, value] of Object.entries(data)) {
      formData.append(key, value)
    }
    const { data: file_data } = await http.post(FILE_UPLOAD, formData, {
      headers: {
        contentType: 'form-data'
      },
      loading: true
    })

    return file_data
  },
  $handlePreview,
  $html2diff: ($form: ViewModel) => {
    // 这个功能应该是死功能
    nextTick(async () => {
      const modelHeadId = $form.query('modelHeadId').get('value')
      const state = $form.query('state').get('data')

      const res1 = await contractManagement.getById(modelHeadId)
      const res2 = await contractManagement.modelLine.getModelLine(modelHeadId)
      const arr = attrs.params?.flag === 'add' ? res2.data : $form.values.modelLines
      const initialModelValue = arr.reduce((obj, i) => {
        const { modelKey, modelValue } = i
        let value = modelValue
        try {
          value = JSON.parse(modelValue)
        } catch (e) {

        }
        obj[modelKey] = value
        return obj
      }, {})
      let content = res1.data.content
      // 替换分页符
      const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
      content = content?.replace(breakPageMatcher, () => {
        return '<div class="breakPage" style="break-after: page;"></div>'
      }) ?? ''

      const templateNode = document.getElementById('templateNode')
      if (templateNode) {
        templateNode.innerHTML = ''
        const { vueTemplate } = Parser.replacer(content, false)

        generateComponent({
          node: templateNode,
          $form,
          html: vueTemplate,
          elemKeys: initialModelValue
        })
      }

      const oldContent = templateNode?.innerHTML ?? ''

      const markedContentNode = document.getElementById('markedContent')
      const newContent = markedContentNode?.innerHTML || ''

      const open = (textHtml: string) => {
        if (!textHtml) {
          return
        }

        $form.query('diffChangeDialog').take()?.setComponentProps({ visible: true })

        setTimeout(() => {
          const diffChangeContentNode = document.getElementById('diffChangeContent')

          if (!diffChangeContentNode) {
            return
          }

          diffChangeContentNode.innerHTML = textHtml
        })
      }

      if (typeof Worker === 'undefined') {
        loadJS('./htmldiff.js', () => {
          // @ts-ignore
          open(getHTMLDiff(oldContent, newContent))
        })
      } else {
        const worker = new Worker('./htmldiff.js')
        worker.postMessage({
          newVersion: newContent,
          oldVersion: oldContent
        })
        worker.onmessage = evt => {
          open(evt.data)
        }
      }
    })
  },
  $getPdfFile,
  $calcMaterialTaxedPrice: ($form: ViewModel, row: any) => {
    // if($form.values.sourceType !== 'BID_NOTICE'){
      $calcIncludeTaxAmount($form)
    // }

    if (row.untaxedPrice && row.contractQuantity) {
      const unAmount =
        parseFloat(row.untaxedPrice) * parseFloat(row.contractQuantity)
      row.unAmount = unAmount.toFixed(2)
      if (row.taxRate) {
        const amount = Number((unAmount * (1 + row.taxRate / 100)).toFixed(2))
        const num = Number(row.contractQuantity)
        row.amount = amount
        row.taxQuota = Number(amount - unAmount).toFixed(2)
        row.taxedPrice = amount / num
      }

      return
    }

    if (row.taxedPrice && row.contractQuantity) {
      const amount = parseFloat(row.taxedPrice) * parseFloat(row.contractQuantity)
      row.amount = amount.toFixed(2)
      if (row.taxRate) {
        const unAmount = Number((amount / (1 + row.taxRate / 100)).toFixed(2))
        const num = Number(row.contractQuantity)
        row.unAmount = unAmount
        row.taxQuota = Number(amount - unAmount).toFixed(2)
        row.untaxedPrice = unAmount / num
      }
    }
  },
  updateButtonConfig,
  viewUpdateButtonSave,
  $saveBill,
  initData,
  $archive,
  $approvalHanlder
}

// 供应商登录时需要只读部分表单区域
const vendorReadPrettyCollapseItemSegment = {
  'x-read-pretty': expression(`$vendor() || $form.readPretty`),
}

const schema = defineSchemas({
  // pdf 打印
  pdfIframe: {
    type: 'void',
    'x-component': 'iframe',
    'x-component-props': {
      id: 'pdfIframe',
      // src: '#',
      style: {
        display: 'none'
      }
    }
  },

  templateNode: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      id: 'templateNode',
      // src: '#',
      style: {
        display: 'none'
      }
    }
  },

  // 对比更改 - 对应的是 html2diff 的触发，目前看应该是死代码
  diffChangeDialog: {
    type: 'void',
    title: i18nExpression('contractMod.compareChange'),
    'x-component': 'RDialog',
    properties: {
      diffChangeContainer: {
        type: 'void',
        'x-component': 'HTMLElement',
        'x-component-props': {
          style: {
            overflow: 'hidden',
          }
        },
        properties: {
          diffChangeContent: {
            type: 'void',
            'x-component': 'div',
            'x-component-props': {
              id: 'diffChangeContent',
              class: 'conetnt paper',
              style: {
                width: '98%'
              },
            }
          }
        }
      }
    }
  },

  // 维护框架协议
  frameworkAgreementDialog: {
    type: 'void',
    title: i18nExpression('contractMod.maintainFrameworkAgreement'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: false
    },
    properties: {
      queryData: {
        type: 'object',
        default: {},
        // 使用 data 冗余属性而不是直接定义 field 的方式，减少 createField 开销
        'x-data': {
          pageSize: 9999,
          pageNum: 1,
          vendorId: undefined,
          globalcontractIds: []
        },
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'horizontal'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 4,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendor'),
            'x-decorator': 'FormItem',
            'x-disabled': true
          },
          // 是否协议框架
          isFrameworkAgreement: {
            title: i18nExpression('bidMod.isFrameworkAgreement'),
            'x-disabled': true,
            ...checkboxByYOrNSegment,
            default: 'Y'
          }
        }
      },
      queryEngine: {
        type: 'void',
        'x-decorator': 'QueryEngine',
        'x-query-engine': {
          service: 'cm',
          type: 'ContractHead',
          transformRequest: expression(`(data, headers) => {
            data.query.vendorId = {}
            data.query.ceeaIfVirtual = {}

            return data
          }`)
        },
        'x-query-engine-skip': true,
        properties: {
          dialogTable: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              preColumns: 'seq',
              pagination: false,
              // 后面考虑内置然后通过媒体查询的方式做 size 响应式变化
              maxHeight: '45vh'
            },
            'x-reactions': expression(`(field) => {
              const queryDataField = field.query('frameworkAgreementDialog.queryData').take()

              $effect(() => {
                if (queryDataField.value.vendorName) {
                  $queryEngine.state.paginationManagement.configState.value.pageSize = 999
                  $queryEngine.state.paginationManagement.queryParams.value = {
                    vendorId: { eq: queryDataField.data.vendorId },
                    isFrameworkAgreement: { eq: queryDataField.value.isFrameworkAgreement },
                    vendorName: { eq: queryDataField.value.vendorName },
                    contractStatus: { eq: 'ARCHIVED' },
                  }

                  $queryEngine.state.paginationManagement.refresh()
                }
              }, [queryDataField.data.vendorId])
            }`),
            properties: generateXindexInOrder({
              contractCode: {
                type: 'string',
                title: i18nExpression('contractMod.contractCode'),
                'x-render-table-column': {
                  //
                }
              },
              contractName: {
                type: 'string',
                title: i18nExpression('contractMod.contractName'),
                'x-render-table-column': {
                  //
                }
              },
              operation: {
                type: 'void',
                title: i18nExpression('common.operation'),
                'x-render-table-column': {
                  width: 60
                },
                'x-component': 'RenderTableButtonList',
                properties: {
                  save: {
                    type: 'void',
                    title: i18nExpression('common.save'),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression(`({ row }) => {
                        $values.frameworkAgreementId = row.contractHeadId
                        $values.frameworkAgreementName = row.contractName
                        $values.frameworkAgreementCode = row.contractCode

                        const state = $form.query('state').get('data')
                        // 虚拟合同 合同编号和框架协议编号一样
                        if ($eqY(state.ceeaIfVirtual)) {
                          $form.query('contractCode').take(field => {
                            field.value = row.contractCode
                          })
                        }

                        $closed()
                      }`)
                    }
                  }
                }
              }
            })
          }
        }
      }
    }
  },
  // 发布到签章平台
  releaseSignPlatformDialog: {
    type: 'void',
    title: i18nExpression('contractMod.releaseSignPlatform1'),
    // 'x-decorator': 'QueryEngine',
    // 'x-query-engine': {
    //   type: 'ContractHead'
    // },
    'x-component': 'RDialog',
    'x-component-props': {
      beforeClose: expression(`(done, type) => {
        if (type !== 'ok') {
          done()
          return
        }

        $form.query('releaseSignPlatformDialog.releaseParams').take().submit(async (params) => {
          const modelEnable = $form.query('modelEnable').get('value')

          const data = {
            contractHeadId: $form.values.contractHeadId,
            ...params
          }

          if ($eqY(modelEnable)) {
            const fileData = await $uploadPDF()
            const { fileuploadId } = fileData

            data.fileuploadId = fileuploadId
          }

          // TODO MeiQL 接口改造
          // $queryEngine.request.save(data).then(() => {
          //   done()

          //   // 发布到签章平台成功！
          //   $message.success($t('contractMod.successPostSignPlatform'))

          //   $cancel($bus)
          // })

          $contractManagement.contract.release(data)
          .then(res => {
            done()

            // 发布到签章平台成功！
            $message.success($t('contractMod.successPostSignPlatform'))

            $cancel($bus)
          })
        })
      }`)
    },
    properties: {
      releaseParams: {
        type: 'object',
        ...formGridSegment,
        'x-read-pretty': false,
        properties: {
          name: {
            type: 'string',
            title: i18nExpression('dataConfMod.userName'),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          phone: {
            type: 'string',
            title: i18nExpression('contractMod.phone'),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          email: {
            type: 'string',
            title: i18nExpression('dataConfMod.email'),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
        }
      }
    }
  },
  // 驳回弹窗
  refuseQuery: {
    type: 'void',
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      type: 'ContractHead'
    },
    properties: {
      refuseDialog: {
        type:'void',
        title:'驳回',
        'x-component': 'RDialog',
        'x-component-props': {
          size:'small',
          'close-on-click-modal': false,
          beforeClose:expression(`(done,type,closeLoading) => {
            if(type === 'ok'){
              $self.query('refuseQuery.refuseDialog.form').take().submit(values => {
                console.log('values',values)
                $refuseDialogConfirm(values,$form,$queryEngine,$message,$bus,closeLoading,done)
              }).catch(() => { closeLoading() })
            }else{
              done()
            }
          }`)
        },
        properties:{
          form:{
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties:generateXindexInOrder({
              vendorRejectReason:{
                type:'string',
                title:'驳回原因说明',
                'x-decorator':'FormItem',
                required:true,
                'x-component-props':{
                  type:'textarea',
                  autosize:{
                    minRows:4,
                    maxRows:6
                  }
                }
              }
            })
          }
        }
      }
    }
  },

  // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      showButtonConfig: {
        saveAndNextStep: false
      },
      showTabConfig: {},
      extContentFinal: null,
      // 合同签约明细收货地址取值范围
      locateList:[],
      // 合同模板渲染组件实例
      contractTemplateComponentInstance: null,
      contenteditable: false,
      // 应该是废弃功能
      ceeaIfVirtual: 'N',
      // 右边描点
      progress:[
              {
                code: 'contractInfo',
                name: t('logisticsMod.contractInfo'),
                percentage: 0
              },
              {
                code: 'otherInfo',
                name: t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo"),
                percentage: 0
              },
              {
                code:'basisFileInfo',
                name:'合同依据',
                percentage: 0
              },
              {
                code: 'contractFinancialInformation',
                name: t('contractMod.contractFinancialInformation'),
                percentage: 0
              },
              {
                code: 'itemInfo',
                name: '合同签约明细',
                percentage: 0
              },
              {
                code: 'paymentPlan',
                name: t('cusEntry.contractMod.paymentPlan'),
                percentage: 0
              },
              {
                code: 'partner',
                name: t('contractMod.partner'),
                percentage: 0
              },
              {
                code: 'fileInfo',
                name: t('contractMod.fileInfo'),
                percentage: 0
              },
              {
                code:'sealFileInfo',
                name:'盖章附件信息',
                percentage: 0
              },
              {
                code:'operateRecord',
                name:'操作记录',
                percentage: 0
              },
              {
                code: 'contractContent',
                name: t('contractMod.contractContent'),
                percentage: 0
              }
            ],
      progressNo:[
        {
          code: 'contractInfo',
          name: t('logisticsMod.contractInfo'),
          percentage: 0
        },
        {
          code: 'otherInfo',
          name: t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo"),
          percentage: 0
        },
        {
          code:'basisFileInfo',
          name:'合同依据',
          percentage: 0
        },
        {
          code: 'contractFinancialInformation',
          name: t('contractMod.contractFinancialInformation'),
          percentage: 0
        },
        {
          code: 'itemInfo',
          name: '合同签约明细',
          percentage: 0
        },
        {
          code: 'paymentPlan',
          name: t('contractMod.paymentPlan'),
          percentage: 0
        },
        {
          code: 'partner',
          name: t('contractMod.partner'),
          percentage: 0
        },
        {
          code: 'fileInfo',
          name: t('contractMod.fileInfo'),
          percentage: 0
        },
        {
          code:'sealFileInfo',
          name:'盖章附件信息',
          percentage: 0
        },
        {
          code:'operateRecord',
          name:'操作记录',
          percentage: 0
        }
      ]
    }
  },

  ContractHead: {
    type: 'void',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container contractMg',
      direction: 'vertical'
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            return initData($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$form.values.contractHeadId]

            data.query['*'] = {}
            data.query.modelLines = {
              '*': {},
            }

            // http.js 封装的 loading 不合理，这里手动维护
            $form.query('state').take(field => {
              field.setData({
                loadingInstance: app.$loading({
                  lock: true,
                  text: '加载中',
                  background: 'rgba(0, 0, 0, 0.4)'
                })
              })
            })

            return data
          }`),
          onSuccess: expression(`(res) => {
            // requestIdleCallback(() => {
              $form.query('state').take(field => {
                field.data.loadingInstance?.close()
              })
            // })

            const value = res.data[0]

            console.log('value:::',value)

            // const {buId} = value || {}

            // // 合同签约明细 - 收货地点取值
            // // if(buId){
            // //   $http({
            // //     url: '/api-pj/organization/organization/getOrganization',
            // //     method: 'GET',
            // //     loading: true,
            // //     params: {
            // //       organizationId: buId,
            // //     }
            // //   }).then(result => {
            // //     if(result.data.siteList.length > 0){
            // //       let siteList = result.data.siteList.filter(item => item.siteName).map(item => item.siteName)
            // //       if(siteList.length){
            // //         siteList = Array.from(new Set(siteList))
            // //         $form.query('state').get('data').locateList = []
            // //         for(let item of siteList){
            // //           $form.query('state').get('data').locateList.push({
            // //             value:item,
            // //             label:item
            // //           })
            // //         }
            // //       }
            // //     }
            // //   })
            // // }


            // 合同终止
            if ($attrs.params?.flag === 'termination') {
              value.fileUploads = [{
                fileuploadId: null,
                fileSourceName: '',
                fileType:
                'TERMINATION_AGREEMENT',
                del: 'N'
              }]
            }
            // 合作终止右方描点修改
            if ($attrs.params?.flag === 'termination' || $attrs.params?.contractType === 'TERMINATION') {
              let progress = [
                {
                  code: 'contractInfo',
                  name: $t('logisticsMod.contractInfo'),
                  percentage: 0
                },
                {
                  code: 'otherInfo',
                  name: $t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo"),
                  percentage: 0
                },
                {
                  code: 'fileInfo',
                  name: $t('contractMod.fileInfo'),
                  percentage: 0
                }
              ]
              $form.query('state').get('data').progress = progress
            }
            // 补充协议新增-清掉操作记录、合同附件信息、合同依据、契约锁id
            if($attrs.params?.flag === 'add' && $attrs.params?.contractType === 'SUPPLEMENTAL_AGREEMENT'){
              value.operationLogs = []
              value.stampContractFileuploadId = null
              value.fileUploads = []
              value.basisAnnexes = []
              value.stampAnnexes = []
              value.contractStatus = 'DRAFT'
            }
            value.totalItems = value?.includeTaxAmount
            value.totalMaterialAmount = numericUppercase(value?.includeTaxAmount)

            $form.setValues({
              ...value,
              contractType: $attrs.params.contractType || value.contractType
            })

            // if($attrs.params.flag === 'add' && $attrs.params?.contractType === 'SUPPLEMENTAL_AGREEMENT'){
            //   app.$http({
            //     url:'/api-cm/contractHead/ext/generateExtContractCode',
            //     method:'GET',
            //     params:{
            //       contractHeadId:$attrs.params.rowId
            //     }
            //   }).then(response => {
            //     if(response.data){
            //       $form.values.contractNo = response.data
            //     }
            //   })
            // }

            // updateButtonConfig($form)

            // 单纯文本只读状态
            $form.readPretty = $readOnly || $attrs.params.flag === 'view'
            if (['SUPPLIER_CONFIRMED'].includes($attrs.params.row.contractStatus)) {
              $form.readPretty = true
              // 控制审批流按钮权限
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep: true
                }
                stateData.showTabConfig = {
                  approval: true
                }
              }, 50)
            } else if (['ARCHIVED'].includes($attrs.params.row.contractStatus)) {
              // 控制审批流按钮权限
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep: true
                }
                stateData.showTabConfig = {
                  approval: true
                }
              }, 50)
            } else {
              $buyer() && setTimeout(() => {
                let stateData = $form.query('state').get('data')
                stateData.showButtonConfig = {
                  saveAndNextStep : $form.values.needVendorConfirm === 'N' && ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus)
                }
              }, 50)
            }
            if ($attrs.params.flag === 'add') {
              $form.query('.contractStatus').take(filed => {
                filed.value = 'DRAFT'
              })
            } else {
              if ($form.values.extContractHandlerId && !$form.values.extHandlerBuId) {
                $contractManagement.contract.getDepartment($form.values.extContractHandlerAccount).then(res => {
                  if (res.data) {
                   $form.values.extHandlerBuName = res.data.ouOrganization?.organizationName
                   $form.values.extHandlerBuId = res.data.ouOrganization?.organizationId
                   $form.values.extHandlerBuCode = res.data.ouOrganization?.organizationCode
                  }
                })
              }
            }

            setTimeout(() => {
              $compileMarkedContent($form, value.content || '', true)
            })
          }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          "cascadeDeletion": true,
          loading: true
        },
      },
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'ApprovalProcess',
        'x-component-props': {
          'business-id': expression(`$form.values.contractHeadId || null`),
          'business-type': 'CONTRACT',
          'show-approval-tab-record': expression(`!$vendor()`),
          'approval-status': expression(`$form.values.contractStatus || 'DRAFT'`),
          'operation-pre-options': expression(`$wrapper($preOptions, $root)`),
          'show-button-config': expression('$form.query(\'state\').get(\'data\').showButtonConfig'),
          'show-tab-config': expression('$form.query(\'state\').get(\'data\').showTabConfig'),
          'status-map': expression(`{
            DRAFT: 'DRAFT', // 拟定
            SUBMITTED: 'UNDER_REVIEW', // 已提交
            APPROVED: 'APPROVED', // 审批通过
            REJECTED: 'REJECTED', // 已驳回
            WITHDRAW: 'WITHDRAW', // 已撤回
            ABANDONED: 'ABANDONED' // 已废弃
          }`),
          readonly: expression('$attrs.params.flag === \'view\''),
          '@approval-handler-callback': expression(`(type) => {
            $approvalHanlder(type, $form, $queryEngine, $bus, $eqY)
          }`)
        },
        properties: {
          customButtonList: {
            type: 'void',
            'x-component': 'ButtonList',
            'x-component-props': {
              style: {
                'margin-right': '8px'
              }
            },
            'x-slot': 'custom',
            properties: {
              cancel: {
                type: 'void',
                'x-content': i18nExpression('common.cancel'),
                'x-component': 'Button',
                'x-component-props': {
                  type: 'default',
                  '@click': expression(`() => {
                    $cancel($bus)
                  }`)
                }
              },
              pdfPrint: {
                type: 'void',
                'x-content': i18nExpression('route.pdfPrint'),
                'x-component': 'Button',
                'x-component-props': {
                  type: 'default',
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression(`() => {
                        $handlePreview($form)
                        $getPdfFile(true)
                      }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['modelHeadId'], `
                    !!$deps[0] && $illegalNotView && $attrs.params.flag !== 'view' && $attrs.params.flag !== 'archive'
                    `)
              },
              //  编辑合同详情
              editContractDetail: {
                type: 'void',
                'x-content': i18nExpression('contractMod.editContractDetail'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression(`() => {
                        const state = $form.query('state').get('data')

                        if (state.contractTemplateComponentInstance) {
                          state.contractTemplateComponentInstance.editable = true
                          $self.query('.previewContractDetail').take().visible = true
                          $self.visible = false
                        }

                        if ($eqN($values.enable)) {
                          state.contenteditable = true
                        }
                      }`)
                    },
                    'x-reactions': [
                      expression(`(field) => {
                        const modelHeadId = $form.query('modelHeadId').get('value')
                        const contractType = $form.query('contractType').get('value')
                        const modelEnable = $form.query('modelEnable').get('value')
                        const state = $form.query('state').get('data')
                        const contractStatus = $form.query('contractStatus').get('value')

                        field.visible = !!modelHeadId
                          && (state.contractTemplateComponentInstance && !state.contractTemplateComponentInstance.editable)
                          && (contractType === 'MIAN_CONTRACT_ADD' || contractType === 'MIAN_CONTRACT_ALTER' || contractType === 'SUPPLEMENTAL_AGREEMENT')
                          && (contractStatus === 'DRAFT' || contractStatus === '' || contractStatus === null)
                          && $illegalNotView
                          && $eqY(modelEnable) && $attrs.params.flag !== 'view'
                      }`),
                      {
                        dependencies: ['contractStatus'],
                        fulfill: {
                          state: {
                            disabled: expression(`
                              // TODO 接入流程
                              // if (app.workflowParamsInfo.integrationMode == 'None' && ['SUPPLIER_CONFIRMED'].includes(state)) {
                              //   return false
                              // }

                              $readOnly || $isAdd
                                ? false
                                : (!$buyer() && $deps[0] === 'SUPPLIER_CONFIRMING')
                                  ? false
                                  : !['DRAFT', 'REFUSED', 'WITHDRAW'].includes($deps[0])
                            `)
                      }
                    }
                  }
                ]
              },
              // 预览合同详情
              previewContractDetail: {
                type: 'void',
                'x-content': i18nExpression('contractMod.previewContractDetail'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  type: 'default',
                  '@click': expression(`() => {
                    $handlePreview($form)
                    $self.query('.editContractDetail').take().visible = true
                    $self.visible = false
                  }`)
                },
                'x-reactions': {
                  dependencies: ['modelHeadId', 'contractType', 'modelEnable', 'contractStatus'],
                  fulfill: {
                    state: {
                      visible: expression(`
                          !!$deps[0]
                          && $deps[3]
                          && $deps[1] === 'MIAN_CONTRACT_ADD'
                          && $deps[4] === 'DRAFT'
                          && $illegalNotView
                          && $eqY($deps[2]) && $attrs.params.flag !== 'view'
                      `)
                    }
                  }
                }
                // 'x-reactions': changeFieldVisibleByDeps(['modelHeadId', 'contractType', 'modelEnable', 'contractTemplateComponentInstance.editable', 'contractStatus'], `
                //       !!$deps[0]
                //           && !$deps[3]
                //           && $deps[1] === 'MIAN_CONTRACT_ADD'
                //           && $deps[4] === 'DRAFT'
                //           && $illegalNotView
                //           && $eqY($deps[2])
                //     `)
              },
              // 对比更改
              compareChange: {
                type: 'void',
                'x-content': i18nExpression('contractMod.compareChange'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin': '8px'
                  },
                  '@click': expression(`() => {
                    $handlePreview($form)
                    $html2diff($form)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['modelHeadId', 'enable', 'modelEnable'], `
                    (
                      (!!$deps[0] && $eqN($deps[1])) || $illegal === 'view'
                    ) && $eqY($deps[2]) && !$isTermination && $attrs.params.flag !== 'view'
                  `)
              },
              // 发布签章平台
              releaseSignPlatform: {
                type: 'void',
                'x-content': i18nExpression('contractMod.releaseSignPlatform'),
                'x-component': 'Button',
                'x-hidden':true,
                'x-component-props': {
                  '@click': expression(`() => {
                    $form.query('releaseSignPlatformDialog').take().setComponentProps({ visible: true })
                  }`)
                },
                'x-reactions': expression(`(field) => {
                  const contractType = field.query('contractType').get('value')
                  const contractStatus = field.query('contractStatus').get('value')
                  const formal = field.query('formal').get('value')

                  field.visible = $buyer()
                    && ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER','SUPPLEMENTAL_AGREEMENT'].includes(contractType)
                    && contractStatus === 'APPROVAL'
                    && formal === 'ELECTRONIC_CONTRACT' && $attrs.params.flag !== 'view'
                }`)
              },
              // 发布供应商暂存
              staging: {
                type: 'void',
                'x-content': i18nExpression('common.staging'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression('() => $handleSubmit($form, $queryEngine, $bus, "savePublish")')
                },
                'x-reactions': changeFieldVisibleByDeps(['needVendorConfirm', 'contractStatus'], `
                  $eqY($deps[0]) && $buyer() &&
                  (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes($deps[1]) || $isAdd || $isTermination) && $attrs.params.flag !== 'view' && !['SUPPLIER_CONFIRMED'].includes($deps[1])
                `)
              },
              // 发布供应商
              releaseSupplier: {
                type: 'void',
                'x-content': i18nExpression('contractMod.releaseSupplier'),
                'x-component': 'Button',
                'x-component-props': {
                  '@click': expression('() => $handleSubmit($form, $queryEngine, $bus, "publish")')
                },
                'x-reactions': changeFieldVisibleByDeps(['needVendorConfirm', 'contractStatus'], `
                  $eqY($deps[0]) && $buyer() &&
                  (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes($deps[1]) || $isAdd || $isTermination) && $attrs.params.flag !== 'view' && !['SUPPLIER_CONFIRMED'].includes($deps[1])
                `)
              },
              // 归档
              archive: {
                type:'void',
                'x-content':'归档',
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression(`() => {
                    $archive($form, $queryEngine, $bus, $message)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['contractStatus'],`
                  $buyer() && $deps[0] === 'UN_ARCHIVED' && $attrs.params.flag === 'archive'
                `)
              },
              // 供应商确认按钮
              confirm: {
                type: 'void',
                'x-content': i18nExpression('orderMod.buyerOrderSynergy.confirm'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression('() => $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_CONFIRMING")')
                },
                'x-reactions': changeFieldVisibleByDeps(['contractStatus'], `
                $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                `)
              },
              // 供应商驳回按钮
              refuse: {
                type: 'void',
                'x-content': i18nExpression('components.approvalHead.headers.refuse'),
                'x-component': 'Button',
                'x-component-props': {
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression(`() => {
                    $form.query('refuseQuery.refuseDialog').take().setComponentProps({visible:true})
                    // $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_REFUSE")
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['contractStatus'], `
                $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                `)
              }
            }
          },
          lockSeal: {
            type: 'void',
            'x-component': 'ElButton',
            'x-component-props': {
              type: 'primary',
              style: {
                position: 'fixed',
                top: '150px',
                right: '222px',
                'z-index': 999
              },
              '@click': expression(`() => {
                $lockSealHandler($form)
              }`)
            },
            'x-visible': expression(`$showLockSeal($form)`),
            'x-content': i18nExpression('cusEntry.common.lockSeal')
            },
              steps: {
                type: 'void',
                'x-decorator': 'div',
                'x-decorator-props': {
                  class: 'stepDiv'
                },
                'x-component': 'Steps',
                'x-component-props': {
                  alignCenter: true,
                  finishStatus: 'success'
                },
                'x-reactions': {
                  dependencies: ['contractStatus'],
                  fulfill: {
                    state: {
                      // TODO 优化判断
                      // @ts-ignore
                      'component[1].active': expression(`
                        $attrs.params.termination
                          ? (
                            ['DRAFT', '', 'ARCHIVED'].includes($deps[0])
                              ? 0
                              : $deps[0] === 'SUPPLIER_CONFIRMED'
                                ? 1
                                : $deps[0] === 'UNDER_REVIEW'
                                  ? 2
                                  : $deps[0] === 'TERMINATED'
                                    ? 3
                                    : 0
                          )
                          : (
                            $deps[0] === 'ARCHIVED'
                              ? 5
                              : $deps[0] === 'APPROVAL'
                                ? 3
                                :$deps[0] === 'UN_ARCHIVED'
                                ? 4
                                : ['UNDER_REVIEW', 'REFUSED', 'SIGNATUREING', 'SUPPLIER_CONFIRMED'].includes($deps[0])
                                  ? 2
                                  : ['SUPPLIER_CONFIRMED', 'SUPPLIER_REJECTED'].includes($deps[0])
                                    ? 1
                                    : 0
                          )
                      `)
                    }
                  }
                },
                properties: {
                  step1: {
                    type: 'void',
                    'x-component': 'el-step',
                    'x-component-props': {
                      title: expression(`$t($attrs.params.termination ? 'contractMod.terminationRelease' : 'contractMod.contractRelease')`)
                    }
                  },
                  step2: {
                    type: 'void',
                    'x-component': 'el-step',
                    'x-component-props': {
                      title: expression(`$t($attrs.params.termination ? 'contractMod.terminationDetermine' : 'contractMod.contractConfirmation')`)
                    },
                    'x-reactions': {
                      dependencies: ['needVendorConfirm'],
                      fulfill: {
                        state: {
                          visible: expression('$eqY($deps[0])')
                        }
                      }
                    }
                  },
                  step3: {
                    type: 'void',
                    'x-component': 'el-step',
                    'x-component-props': {
                      title: expression(`$t($attrs.params.termination ? 'contractMod.terminationApproval' : 'contractMod.contractApproval')`)
                    }
                  },
                  step4: {
                    type: 'void',
                    'x-component': 'el-step',
                    'x-component-props': {
                      title: expression(`$t($attrs.params.termination ? 'contractMod.termination' : 'contractMod.contractSigning')`)
                    }
                  },
                  step5: {
                    type: 'void',
                    'x-component': 'el-step',
                    'x-component-props': {
                      title: i18nExpression('contractMod.contractFiling')
                    },
                    'x-visible': expression('!$attrs.params.termination')
                  }
                }
              },
              collapse: {
                type: 'void',
                'x-component': 'FormCollapse',
                properties: generateXindexInOrder({
                  // 合同信息
                  contractInfo: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('logisticsMod.contractInfo'),
                      id: 'contractInfo'
                    },
                    'x-query-engine-skip': true,
                    'x-read-pretty': expression(`$vendor() || $isTermination || $form.readPretty`),
                    properties: {
                      // 用于其他字段监听状态进行相关处理
                      contractType: {
                        type: 'string',
                        default: 'MIAN_CONTRACT_ADD',
                        'x-hidden': true
                      },
                      layout: {
                        type: 'void',
                        ...formGridSegment,
                        properties: {
                          // 合同编号
                          contractNo: {
                            type: 'string',
                            title: i18nExpression('contractMod.contractNo'),
                            // 'x-index': expression('$isTermination ? 1 : 0'),
                            'x-decorator': 'FormItem',
                            'x-disabled': expression('$form.readPretty ? undefined : true')
                          },
                          // 状态
                          contractStatus: {
                            type: 'string',
                            title: i18nExpression('contractMod.status'),
                            'x-hidden': expression('$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_STATUS',
                              disabled: expression('$form.readPretty ? undefined : true'),
                              '@change': expression(`() => updateButtonConfig($form)`)
                            }
                          },
                          // 创建人
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('contractMod.createdBy'),
                            'x-visible': expression('!$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-disabled': expression('$form.readPretty ? undefined : true')
                          },
                          // 创建时间
                          creationDate: {
                            title: i18nExpression('contractMod.creationDate'),
                            'x-visible': expression('!$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-disabled': expression('$form.readPretty ? undefined : true'),
                            ...yearMonthDayHourMinuteSecondSelectorSegment
                          },
                          // 合同名称
                          contractName: {
                            type: 'string',
                            title: i18nExpression('contractMod.contractName'),
                            'x-decorator': 'FormItem',
                            'x-component-props': {
                              maxLength: 100,
                              showWordLimit: true,
                              disabled:expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            },
                            // 'x-index': expression('$isTermination ? 0 : 4'),
                            ...requiredValidatorSegment
                          },
                          // 业务实体
                          // buId: {
                          //   type: 'string',
                          //   title: '我方签约主体',
                          //   'x-visible': expression('$buyer()'),
                          //   'x-decorator': 'FormItem',
                          //   'x-component': 'OrganizationSelector',
                          //   'x-component-props': {
                          //     placeholder: i18nExpression('common.pleaseSelect'),
                          //     'read-pretty': '{{$form.readPretty || $vendor()}}',
                          //     'parent-id': -1,
                          //     jumpLogin: expression('$jumpLogin'),
                          //     'node-type': 'OU',
                          //     disabled: expression('$isTermination || $form.readPretty || $values.contractType !== "MIAN_CONTRACT_ADD"'),
                          //     '@select': expression(`(node, value) => {
                          //       const { organizationCode, organizationName, fullPathId, organizationId } = node

                          //       // 设置到 form.values 上
                          //       $values.buCode = organizationCode
                          //       $values.buName = organizationName
                          //       $values.buFullPathId = fullPathId

                          //       const partnerData = $form.query('partnerData').take()

                          //       partnerData.value.forEach((e, index) => {
                          //         if (e.partnerType == '甲方') {
                          //           partnerData.remove(index)
                          //         }
                          //       })

                          //       $self.disabled = true

                          //       $http({
                          //         url: '/api-pj/organization/organization/getOrganization',
                          //         method: 'GET',
                          //         loading: true,
                          //         params: {
                          //           organizationId: organizationId,
                          //         }
                          //       }).then((res) => {
                          //         if (res.code != '0') {
                          //           $self.value = ''
                          //         }

                          //         const partnerDataItem = {
                          //           partnerType: '甲方',
                          //           ouId: res.data?.organization?.organizationId,
                          //           partnerName: res.data?.organization?.organizationName,
                          //           taxPayer: res.data?.orgCompany?.taxNumber
                          //         }

                          //         if (res.data.orgCompanyBankList.length > 0) {
                          //           let minObj = res.data.orgCompanyBankList.find(item => item.isMain === 'Y')
                          //           let bankObj = res.data.orgCompanyBankList[0]
                          //           let companyBankObj = minObj || bankObj
                          //           Object.assign(partnerDataItem, {
                          //             partnerName: res.data.organization.organizationName,
                          //             bankAccount: companyBankObj.bankAccount,
                          //             bankName: companyBankObj.bankName
                          //           })
                          //         }
                          //         if (res.data.orgCompanyPersonList.length > 0) {
                          //           Object.assign(partnerDataItem, {
                          //             contactName: res.data.orgCompanyPersonList[0].name
                          //           })
                          //         }
                          //         if (res.data.orgCompanyAddressList.length > 0) {
                          //           res.data.orgCompanyAddressList.forEach(datas=>{
                          //             if (datas.isActive == 'Y') {
                          //               Object.assign(partnerDataItem, {
                          //                 postCode : datas?.postalCode,
                          //                 phone: datas?.phone,
                          //                 address : datas?.address
                          //               })
                          //               return false
                          //             }
                          //           })
                          //         }
                                  
                          //         if(res.data.orgInvoiceInfoList.length > 0){
                          //           let orgInvoiceInfo = res.data.orgInvoiceInfoList[0] || {}
                          //           Object.assign(partnerDataItem,{
                          //             phone:orgInvoiceInfo.phone,
                          //             address:orgInvoiceInfo.address,
                          //             bankName:orgInvoiceInfo.openingName,
                          //             bankAccount:orgInvoiceInfo.openingAccount,
                          //             taxPayer:orgInvoiceInfo.taxpayerNum
                          //           })
                          //         }
                                  
                          //         if(res.data.siteList.length > 0){
                          //           let siteList = res.data.siteList.filter(item => item.siteName).map(item => item.siteName)
                          //           if(siteList.length){
                          //             siteList = Array.from(new Set(siteList))
                          //             $form.query('state').get('data').locateList = []
                          //             for(let item of siteList){
                          //               $form.query('state').get('data').locateList.push({
                          //                 value:item,
                          //                 label:item
                          //               })
                          //             }
                          //           }
                          //         }else{
                          //           $form.query('state').get('data').locateList = []
                          //         }

                          //         partnerData.push(partnerDataItem)
                          //       }, (resOrr) => {
                          //         $self.value = ''
                          //         $values.buCode = ''
                          //         $values.buName = ''
                          //         $values.buFullPathId = ''
                          //       }).finally(() => {
                          //         $self.disabled = false
                          //       })
                          //     }`)
                          //   },
                          //   ...requiredValidatorSegment
                          // },
                          buName:{
                            type:'string',
                            title:'我方签约主体',
                            'x-decorator': 'FormItem',
                            'x-component': 'QuickSearchWrapper',
                            'x-component-props':{
                              showKey:'companyName',
                              propKey:'companyName',
                              name: 'scc_pj_bpm_incorporated_company',
                              'read-pretty': '{{$form.readPretty || $vendor()}}',
                              '@close-quicksearch':expression(`(val, scope) => {
                                $values.buId = val ? val.bpmIncorporatedCompanyId : null
                                $values.buCode = val ? val.companyNo : null
                                $values.buName = val ? val.companyName : null
                                if(val.companyName){
                                  const partnerData = $form.query('partnerData').take()
                                  partnerData.value.forEach((e, index) => {
                                    if (e.partnerType == '甲方') {
                                      partnerData.remove(index)
                                    }
                                  })
                                  const partnerDataItem = {
                                    partnerType: '甲方',
                                    ouId: val.bpmIncorporatedCompanyId,
                                    partnerName: val.companyName,
                                    taxPayer: val.creditCode
                                  }
                                  partnerData.push(partnerDataItem)
                                }
                                // 清空印章
                                 $values.sealName = ''
                                 $values.sealId = null
                              }`)
                            },
                            ...requiredValidatorSegment
                          },
                          buId: {
                            type: 'string',
                            title: i18nExpression('contractMod.fullPathId'),
                            'x-hidden': true
                          },
                          // 供应商名称
                          vendorName: {
                            type: 'string',
                            title: i18nExpression('contractMod.vendorName'),
                            'x-decorator': 'FormItem',
                            'x-component': 'QuickSearchWrapper',
                            'x-component-props': {
                              showKey: 'companyName',
                              propKey: 'companyName',
                              'read-pretty': '{{$form.readPretty || $vendor()}}',
                              'disabled':expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              'name': 'scc_sup_company_info_new',
                              '@close-quicksearch': expression(`(val, scope) => {
                                $values.vendorId = val ? val.companyId : ''
                                $values.vendorCode = val ? val.companyCode : ''
                                $values.erpVendorCode = val ? val.erpVendorCode : ''
                                $values.erpVendorId = val ? val.erpVendorId : ''
                                if (val.companyName) {
                                  const partnerData = $form.query('partnerData').take()
                                  partnerData.value.forEach((e, index) => {
                                    if (e.partnerType == '乙方') {
                                      partnerData.remove(index)
                                    }
                                  })

                                  if (!partnerData.value.some(item => item.partnerType === '乙方')) {
                                    partnerData.value.push({
                                      partnerType: '乙方',
                                      partnerName: val.companyName,
                                      bankName: val.bankName,
                                      bankCode: val.bankCode,
                                      bankAccount: val.bankAccount,
                                      taxPayer: val.lcCode,
                                      address: val.address,
                                      lcCode: val.lcCode
                                    })
                                  }
                                }
                              }`)
                            },
                            'x-reactions': {
                              dependencies: ['contractType'],
                              fulfill: {
                                schema: {
                                  // @ts-ignore
                                  'component[1].disabled': expression('$deps[0] !== "MIAN_CONTRACT_ADD"')
                                }
                              }
                            },
                            ...requiredValidatorSegment
                          },
                          // 合同形式
                          formal: {
                            type: 'string',
                            title: i18nExpression('contractMod.signingMethod'),
                            'x-visible': expression('!$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_FORM2',
                              'disabled':expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            },
                            ...requiredValidatorSegment
                          },
                          // 合同有效期从
                          effectiveDateFrom: {
                            title: i18nExpression('contractMod.contractValidFrom'),
                            'x-decorator': 'FormItem',
                            ...yearMonthDaySelectorSegment,
                            ...requiredValidatorSegment
                          },
                          // 合同有效期至
                          effectiveDateTo: {
                            title: i18nExpression('contractMod.contractValidTo'),
                            'x-decorator': 'FormItem',
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              'picker-options': expression(`{
                                disabledDate: (time) => {
                                  const effectiveDateFrom = $self.query('.effectiveDateFrom').get('value')

                                  return time.getTime() < (new Date(effectiveDateFrom)).getTime()
                                }
                              }`)
                            },
                            ...requiredValidatorSegment
                          },
                          // 合同类型
                          contractClass: {
                            type: 'string',
                            title: i18nExpression('contractMod.mgsContractType'),
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'ELEM_CONTRACT_TYPE',
                              'disabled':expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              beforeChange: expression(`(value) => {
                                const modelHeadIdField = $form.query('modelHeadId').take()

                                if (modelHeadIdField?.value) {
                                  return $confirm($t('contractMod.clearModelMsg'))
                                    .then(() => {
                                      // 重置模板名称
                                      modelHeadIdField.value = undefined
                                      document.getElementById('markedContent').innerHTML = ''
                                    })
                                }

                                return Promise.resolve()
                              }`)
                            },
                            'x-reactions': [
                              {
                                dependencies: ['contractType'],
                                fulfill: {
                                  schema: {
                                    // @ts-ignore
                                    'component[1].disabled': expression(`
                                      $deps[0] === 'MIAN_CONTRACT_ALTER'
                                        ? false
                                        : $deps[0] !== 'MIAN_CONTRACT_ADD'
                                          ? !!$self.value
                                          : false
                                    `)
                                  }
                                }
                              }
                            ],
                            ...requiredValidatorSegment
                          },
                          // 合同模式
                          ceeaControlMethod: {
                            type: 'string',
                            title: i18nExpression('vendorMod.controlMethod'),
                            'x-visible': expression('!$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'MANAGEMENT_CONTROL_MODEL',
                              'disabled':expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            }
                          },
                          extContractHandlerName: {
                            type:'string',
                            title:'合同经办人',
                            'x-decorator':'FormItem',
                            'x-component':'QuickSearchWrapper',
                            'x-component-props':{
                              name:'scc_rbac_user_display',
                              showKey:'nickname',
                              propKey:'userId',
                              '@close-quicksearch':expression(`(val) => {
                                $values.extContractHandlerId = val ? val.userId : null
                                $values.extContractHandlerName = val ? val.nickname : null
                                $values.extContractHandlerAccount = val ? val.username : null
                                if (val?.username) {
                                  $contractManagement.contract.getDepartment(val.username).then(res => {
                                    if (res.data) {
                                      $values.extHandlerBuName = res.data.ouOrganization?.organizationName
                                      $values.extHandlerBuId = res.data.ouOrganization?.organizationId
                                      $values.extHandlerBuCode = res.data.ouOrganization?.organizationCode
                                    }
                                  })
                                } else {
                                  $values.extHandlerBuName = null
                                  $values.extHandlerBuId = null
                                  $values.extHandlerBuCode = null
                                }
                              }`)
                            },
                            ...requiredValidatorSegment
                          },
                          extHandlerBuId: {
                            type: 'string',
                            title: '经办人部门',
                            'x-decorator':'FormItem',
                            'x-component': 'OrganizationSelector',
                            'x-component-props': {
                              'parent-id': -1,
                              'node-type': 'OU',
                              '@select': expression(`(node) => {
                                $values.extHandlerBuName = node ? node.organizationName : null
                                $values.extHandlerBuId = node ? node.organizationId : null
                                $values.extHandlerBuCode = node ? node.organizationCode : null
                              }`)
                            },
                            // ...requiredValidatorSegment
                          },
                          // 是否框架协议
                          isFrameworkAgreement: {
                            title: i18nExpression('contractMod.isFrameworkAgreement'),
                            ...radioGroupByYOrNSegment,
                            ...requiredValidatorSegment,
                            default: 'N',
                            'x-component-props':{
                              disabled:expression(`$vendor() || $isTermination || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              '@change': expression(`(value) => {
                                if(value === 'Y'){
                                  $values.ceeaControlMethod = 'CERTAION_AMOUNT'
                                }
                              }`)
                            }
                          },
                          // 来源类型 字典 CONTRACT_SOURCE_TYPE
                          sourceType: {
                            type:'string',
                            'x-hidden':true
                          },
                          // TODO:来源类型为中标通知书默认为否，不可修改
                          extPricePoolFlag:{
                            type:'string',
                            title:'是否进价格库',
                            'x-decorator':'FormItem',
                            'x-decorator-props':{
                              tooltip:'进入价格库物资，协议期内采购无需询比价',
                              tooltipLayout: 'icon'
                            },
                            'x-component':'DictSelect',
                            'x-component-props':{
                              code:'YES_OR_NO',
                              disabled:expression(`$values.sourceType === 'BID_NOTICE' || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            },
                            ...requiredValidatorSegment
                          },
                          extInvestNo:{
                            type:'string',
                            title:'投资编号',
                            'x-decorator':'FormItem',
                            'x-component-props':{
                              disabled:expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            }
                          },
                          sealName: {
                            type: 'string',
                            title: '我方印章',
                            'x-decorator': 'FormItem',
                            'x-component': 'QuickSearchWrapper',
                            'x-component-props': {
                              name: 'scc_pj_contract_seal',
                              showKey:'sealName',
                              'pre-query-data': expression(`{
                                't.SIGN_COMPANY_NAME': $form.values.buName
                              }`),
                              '@before-open': expression(`(value, callback) => {
                                if (!$form.values.buName) {
                                  $message.warning($t('cusEntry.tipMessage.selectBuName'))
                                  callback()
                                }
                              }`),
                              '@close-quicksearch':expression(`(val) => {
                                $values.sealName = val ? val.sealName : ''
                                $values.sealId = val ? val.sealId : null
                              }`)
                            },
                            ...requiredValidatorSegment
                          },
                          extIncome: {
                            type: 'string',
                            title: '收支方向',
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_EXT_INCOME'
                            },
                            ...requiredValidatorSegment
                          },
                          extRent: {
                            type: 'string',
                            title: '是否租赁',
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'YES_OR_NO'
                            },
                            ...requiredValidatorSegment
                          },
                          extCycle: {
                            type: 'string',
                            title: '是否周期合同',
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'YES_OR_NO'
                            },
                            ...requiredValidatorSegment
                          },
                          // 签约地址
                          signingAddress: {
                            type: 'string',
                            title: i18nExpression('contractMod.signingAddress'),
                            'x-visible': expression('!$isTermination'),
                            'x-decorator': 'FormItem',
                            'x-decorator-props': { gridSpan: 2 },
                            'x-component': 'Input.TextArea',
                            'x-component-props': {
                              placeholder: i18nExpression('common.pleaseTypeContents'),
                              disabled:expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`)
                            }
                          }
                        }
                      }
                    }
                  },
                  // 其他信息
                  otherInfo: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: expression('$t($isTermination ? "contractMod.terminationInformation" : "vendorMod.otherInfo")')
                    },
                    'x-query-engine-skip': true,
                    'x-read-pretty': expression(`$vendor() || $form.readPretty`),
                    properties: {
                      layout: {
                        type: 'void',
                        ...formGridSegment,
                        properties: {
                          // 是否需要供应商确认
                          needVendorConfirm: {
                            title: i18nExpression('contractMod.supplierConfirmation'),
                            'x-hidden': `{{$vendor()}}`,
                            ...radioGroupByYOrNSegment,
                            ...requiredValidatorSegment,
                            'x-component-props': {
                              '@change': expression(`() => updateButtonConfig($form)`)
                            }
                          },
                          // 是否启用模板
                          modelEnable: {
                            title: i18nExpression('vendorMod.enableFlagModel'),
                            ...radioGroupByYOrNSegment,
                            ...requiredValidatorSegment,
                            default: 'N',
                            'x-visible': expression('!$isTermination'),
                            'x-component-props': {
                              '@change': expression(`(value) => {
                                const fileUploads = $form.query('fileUploads').take()
                                if ($eqN(value)) {
                                  if (!fileUploads.value.some(row => row.fileType === 'CONTRACT_AGREEMENT')) {
                                    fileUploads.push({
                                      fileuploadId: null,
                                      fileSourceName: '',
                                      fileType: 'CONTRACT_AGREEMENT',
                                      del: 'N'
                                    })
                                  }
                                  $form.values.modelHeadId = null
                                  $form.values.modelName = ''
                                  return
                                }

                                fileUploads.value.forEach((row, index) => {
                                  if (row.fileType == 'CONTRACT_AGREEMENT') {
                                    fileUploads.remove(index)
                                  }
                                })
                              }`)
                            }
                          },
                          modelName: {
                            type: 'string',
                            'x-hidden': true
                          },
                          // 模板名称
                          modelHeadId: {
                            type: 'string',
                            title: i18nExpression('dataConfMod.templateName'),
                            'x-decorator': 'FormItem',
                            'x-hidden': expression('$isTermination'),
                            'x-component': 'Select',
                            'x-component-props': {
                              '@change': expression(`(val) => {
                                if (!val) return

                                const option = $self.dataSource.find(item => item.value === val)
                                $form.query('.modelName').take().value = option.label

                                $compileMarkedContent($form, (option && option.content) || '', false)
                              }`)
                            },
                            'x-reactions': [
                              {
                                dependencies: ['modelEnable'],
                                fulfill: {
                                  state: {
                                    visible: expression('!$isTermination && $eqY($deps[0])')
                                  }
                                }
                              },
                              expression(`(field) => {
                                const contractClass = field.query('contractClass').get('value')
                                if (contractClass) {
                                  $queryEngine.request.query(
                                    { modelType: { eq: contractClass }, status: {eq: "VALID"}},
                                    { pageSize: 999, pageNum: 1 },
                                    {
                                      type: 'ModelHead',
                                      query: {
                                        modelCode: {}, modelName: {}, modelHeadId: {}, content: {}
                                    }
                                  }).then(res => {
                                    if (!res.data) {
                                      return
                                    }

                                    $self.dataSource = res.data.map(i => ({
                                      ...i,
                                      id: i.modelCode,
                                      label: i.modelName,
                                      value: i.modelHeadId,
                                      type: i.modelType
                                    }))
                                  })
                                }
                              }`)
                            ],
                            ...requiredValidatorSegment
                          },
                          // 标准合同
                          enable: {
                            type: 'string',
                            title: i18nExpression('contractMod.standardContract'),
                            'x-decorator': 'FormItem',
                            'x-component': 'DictSelect',
                            default: 'Y',
                            'x-read-pretty': expression('$form.readPretty'),
                            'x-component-props': {
                              code: 'YES_OR_NO',
                              '@change': expression(`(val) => {
                                if (val === 'Y') {
                                  $form.query('state').get('data').contenteditable = false
                                } else {
                                  $form.query('state').get('data').contenteditable = true
                                }
                              }`)
                            },
                            'x-visible': expression('!$isTermination'),
                            'x-reactions': {
                              dependencies: ['contractType','modelEnable'],
                              fulfill: {
                                schema: {
                                  'x-disabled': expression(`
                                    $self.readPretty || !['MIAN_CONTRACT_ALTER','MIAN_CONTRACT_ADD'].includes($deps[0]) || $vendor()
                                  `)
                                },
                                state: {
                                  visible: expression('!$isTermination && $eqY($deps[1])')
                                }
                              }
                            },
                            ...requiredValidatorSegment
                          },
                          // 是否失效原合同
                          isInvalidOldContract: {
                            title: i18nExpression('vendorMod.enableFlagInvalid'),
                            ...radioGroupByYOrNSegment,
                            default: 'N',
                            'x-decorator-props': { gridSpan: 4 },
                            'x-component-props': {
                              '@change': expression(`(value) => {
                                const fileUploads = $form.query('fileUploads').take()

                                if (value === 'Y') {
                                  const attr = fileUploads.value.map(item => item.fileType)

                                  if (!attr.includes('TERMINATION_AGREEMENT')) {
                                    fileUploads.push({
                                      fileuploadId: null,
                                      fileSourceName: '',
                                      fileType: 'TERMINATION_AGREEMENT',
                                      del: 'N'
                                    })
                                  }
                                } else {
                                  fileUploads.value.forEach((e, index) => {
                                    if (e.fileType == 'TERMINATION_AGREEMENT') {
                                      fileUploads.remove(index)
                                    }
                                  })
                                }
                              }`)
                            },
                            'x-reactions': {
                              dependencies: ['contractType'],
                              fulfill: {
                                schema: {
                                  'x-visible': expression(`
                                    $deps[0] === 'MIAN_CONTRACT_ALTER'
                                  `)
                                }
                              }
                            },
                            ...requiredValidatorSegment
                          }
                        }
                      },
                      layout2: {
                        type: 'void',
                        ...formGridSegment,
                        properties: {
                          // 备注
                          contractRemark: {
                            type: 'string',
                            title: i18nExpression('bid_mod.remark'),
                            'x-visible': expression('!$isTermination'),
                            'x-component': 'Input.TextArea',
                            'x-component-props': {
                              rows: 2,
                              placeholder: i18nExpression('common.pleaseTypeContents')
                            },
                            'x-decorator': 'FormItem',
                            'x-decorator-props': { gridSpan: 4 }
                          },
                          // 终止原因
                          contractTerminationReason: {
                            type: 'string',
                            title: i18nExpression('contractMod.reasonTermination'),
                            'x-visible': expression('$isTermination'),
                            'x-component': 'Input.TextArea',
                            'x-component-props': {
                              rows: 2,
                              maxlength: 300,
                              'show-word-limit': true,
                              placeholder: i18nExpression('common.pleaseTypeContents')
                            },
                            'x-decorator': 'FormItem',
                            'x-decorator-props': { gridSpan: 4 },
                            ...requiredValidatorSegment
                          },
                          // 起草人意见
                          drafterOpinion: {
                            type: 'string',
                            title: i18nExpression('vendorMod.loggerComment'),
                            'x-component': 'Input.TextArea',
                            'x-component-props': {
                              placeholder: i18nExpression('common.pleaseTypeContents'),
                              rows: 2
                            },
                            'x-decorator': 'FormItem',
                            'x-decorator-props': { gridSpan: 4 }
                          }
                        }
                      }
                    }
                  },
                  // 合同依据
                  basisFileInfo:{
                    type:'void',
                    'x-visible': expression('!$isTermination && $buyer()'),
                    'x-component':'FormCollapse.Item',
                    'x-component-props':{
                      title:'合同依据（长城内部可见，用于合同发起的背景说明或技术需求）'
                    },
                    properties:{
                      toolbar:{
                        type:'void',
                        'x-component':'ButtonList',
                        'x-component-props':{
                          class:'list-form__toolbar'
                        },
                        'x-reactions':expression(`(field) => {
                          field.visible = !$form.readPretty && $buyer()
                        }`),
                        properties: {
                          add:{
                            type:'void',
                            title:i18nExpression('common.add'),
                            'x-component-props':{
                              type:'primary',
                              disabled: expression(`$vendor()`),
                              '@click':expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('basisAnnexes'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)
                            }
                          }
                        } 
                      },
                      basisAnnexes:{
                        type:'array',
                        'x-component':'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          maxHeight: 400,
                          editMode: true,
                          pagination: false,
                          sortable: false,
                          // 主键id
                          primaryKey:'basisAnnexId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true
                        },
                        'x-query-engine-skip':true,
                        'x-query-engine-relation':'basisAnnexes:*',
                        properties: generateXindexInOrder({
                          fileSourceName: {
                            type:'string',
                            title: i18nExpression('bidMod.fileName'),
                            'x-component': 'SrmCommonFile',
                            'x-component-props':{
                              disabled:expression(`$form.readPretty || $vendor()`),
                              defaultFile:{
                                fileId: expression(`
                                  !$self.value ? undefined : $table.getRowByIndex($self.index).fileuploadId
                                `),
                                fileName: expression(`$self.value`)
                              },
                              '@on-change':expression(`({file}) => {
                                const row = $table.getRowByIndex($self.index)
                                row.fileuploadId = file.fileId
                                row.fileSourceName = file.fileName
                              }`)
                            },
                            'x-read-pretty': expression('$readOnly'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            ...editTableFormItemValid
                          },
                          // 上传时间
                          creationDate: {
                            type: 'string',
                            title: i18nExpression('purchaseDemand.attachmentCreatedDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-read-pretty': true
                          },
                          remark:{
                            type:'string',
                            title:'备注',
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-read-pretty': expression('$readOnly || $vendor()'),
                          },
                          operation: {
                            type:'void',
                            title: i18nExpression('common.operation'),
                            'x-render-table-column':{
                              width:60,
                              fixed:'right'
                            },
                            'x-component':'RenderTableButtonList',
                            'x-reactions':expression(`field => {
                              field.visible = !$form.readPretty
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type:'text',
                                  disabled: expression(`$form.readPretty || $vendor()`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)
                                }
                              }
                            }
                          }
                        })
                      }
                    }
                  },  
                  // 合同财务信息
                  contractFinancialInformation: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('contractMod.contractFinancialInformation')
                    },
                    'x-reactions': expression(`field => {
                      field.visible = !$isTermination

                      if (field.visible) {
                        field.readPretty = $vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'
                      }
                    }`),
                    properties: {
                      layout: {
                        type: 'void',
                        ...formGridSegment,
                        'x-query-engine-skip': true,
                        properties: {
                          // 合同总金额
                          includeTaxAmount: {
                            type: 'number',
                            title: i18nExpression('cusEntry.contractMod.totalAmountTax1'),
                            default: 0,
                            'x-decorator': 'FormItem',
                            'x-component-props': {
                              controls: false,
                              precision: 2,
                              // disabled:expression(`$values.sourceType === 'BID_NOTICE'`)
                            }
                          },
                          // 币种
                          currencyCode: {
                            type: 'string',
                            title: i18nExpression('contractMod.currencyCode'),
                            default: 'RMB',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'currency'
                            },
                            'x-decorator': 'FormItem',
                            ...requiredValidatorSegment
                          },
                          
                          // 框架协议编号
                          // frameworkAgreementCode: {
                          //   type: 'string',
                          //   title: i18nExpression('contractMod.frameworkAgreementCode'),
                          //   'x-decorator': 'FormItem',
                          //   'x-content': expression(`{
                          //     append: {
                          //       functional: true,
                          //       render(h) {
                          //         return $vendor() || $form.readPretty
                          //           ? undefined
                          //           : h('el-button', {
                          //               props: { type: 'primary', icon: 'el-icon-search', disabled:$form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT' },
                          //               on: {
                          //                 click: () => {
                          //                   const vendorNameField = $form.query('vendorName').take()

                          //                   if (!$values.vendorId || !vendorNameField.value) {
                          //                     // 请先选择供应商
                          //                     return $message.error($t('bid_mod.setPermissionError'))
                          //                   }

                          //                   $form.query('frameworkAgreementDialog').take().setComponentProps({ visible: true })

                          //                   setTimeout(() => {
                          //                     $reactiveAction(() => {
                          //                       const queryDataField = $form.query('frameworkAgreementDialog.queryData').take()
                          //                       queryDataField.value.vendorName = vendorNameField.value
                          //                       queryDataField.data.vendorId = $values.vendorId
                          //                     })
                          //                   })
                          //                 }
                          //               }
                          //             })
                          //           }
                          //         }
                          //       }
                          //   `)
                          // }
                        }
                      }
                    }
                  },
                  // 物料明细
                  itemInfo: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('合同签约明细')
                    },
                    'x-visible': expression(`!$isTermination`), 
                    'x-read-pretty': expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                    properties: {
                      toolbar: {
                        type: 'void',
                        'x-component': 'ButtonList',
                        'x-component-props': {
                          class: 'list-form__toolbar'
                        },
                        'x-reactions': expression(`(field) => {
                          // 来源类型为中标通知书和手工新增显示
                          field.visible = !$form.readPretty  && ['BID_NOTICE','MANUALLY_CREATED'].includes($values.sourceType)
                        }`),
                        properties: {
                          add: {
                            type: 'void',
                            title: i18nExpression('common.add'),
                            'x-component-props': {
                              type: 'primary',
                              disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              '@click': expression(`() => {
                                let contractQuantity = $eqY($values.isFrameworkAgreement) ? 1 : undefined
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('materialListData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow('push',{
                                      contractQuantity
                                    })
                                  })
                              }`)
                            }
                          },
                          importExcel: {
                            type: 'void',
                            'x-component': 'ImportExcel',
                            'x-component-props': {
                              title: i18nExpression('common.import'),
                              type: 'default',
                              disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              extraData: {
                                fileModular: 'cm',
                                fileFunction: 'contractItemImportExcel',
                                fileType: 'excel'
                              },
                              upLoadUrl: '/api-cm/contractHead/ext/analyzeExcel',
                              downloadTemplateOptions: {
                                downloadUrl: '/api-cm/contractHead/ext/downloadMaterialModel',
                                fileName:'合同明细导入模板.xlsx'
                              },
                              '@handleSuccess': expression(`(data) => {
                                console.log('data',data)
                                if(data && data.length){
                                  const materialListData = $form.query('materialListData').take()
                                  for(let item of data){
                                    materialListData.value.push(item)
                                  }
                                }
                              }`)
                            }
                          }
                        }
                      },
                      materialListData: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          editMode: true,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false,
                          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                          primaryKey: 'contractMaterialId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true,
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'contractMaterials:*',
                        'x-read-pretty': expression(`$vendor() || $form.readPretty`),
                        properties: generateXindexInOrder({
                          contractMaterialId: {
                            type: 'number',
                            'x-hidden': true
                          },
                          lineNumber: {
                            type: 'number',
                            'x-hidden': true
                          },
                          invName:{
                            type: 'string',
                            title: i18nExpression('contractMod.invId'),
                            'x-hidden': expression('$buyer()'),
                            'x-render-table-column': {
                              minWidth: 180,
                              static: true
                            }
                          },
                          invId: {
                            type: 'number',
                            default: null,
                            title: i18nExpression('contractMod.invId'),
                            'x-render-table-column': {
                              minWidth: 180
                            },
                            'x-hidden': true,
                            'x-component': 'OrganizationSelector',
                            'x-component-props': {
                              jumpLogin: expression('$jumpLogin'),
                              'read-pretty': expression(`$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              nodeType: 'INV',
                              parentId: queryFieldValueExpression('buId'),
                              scope: expression('$table.getRowByIndex($self.index)'),
                              '@select': expression(`(node, _) => {
                                const row = $table.getRowByIndex($self.index)
                                row.invCode = node && node.organizationCode
                                row.invName = node && node.organizationName
                                row.invFullPathId = node && node.fullPathId
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          tradingLocations: {
                            type: 'string',
                            title: '收货地点',
                            'x-render-table-column': {
                              minWidth: 300
                            },
                            // 'x-component': 'Select',
                            'x-read-pretty': expression(`$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                            // enum:expression(`$form.query('state').get('data').locateList`),
                            // 'x-component-props': {
                            //   style: {
                            //     width: '100%'
                            //   }
                            // },
                            // ...editTableFormItemValid
                          },
                          materialCode: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.materialCode'),
                            'x-render-table-column': {
                              minWidth: 140
                            },
                            'x-component': 'QuickSearchWrapper',
                            'x-component-props': {
                              disabled: expression(`$form.readPretty || $vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              'show-input': expression(`$self.value`),
                              'read-pretty': '{{$form.readPretty}}',
                              'show-key': 'materialCode',
                              'name': 'scc_base_material_item_contract',
                              // 'pre-query-data': expression(`{
                              //   'o.ORGANIZATION_ID': $self.query('.invId').get('value')
                              // }`),
                              '@close-quicksearch': expression(`(val, scope) => {
                                const row = $table.getRowByIndex($self.index)

                                row.materialId = val ? val.materialId : null
                                row.materialName = val ? val.materialName : null
                                row.categoryName = val ? val.categoryName : null
                                row.categoryId = val ? val.categoryId : null
                                row.categoryCode = val ? val.categoryCode : null
                                row.specification = val ? val.materialType : null
                                row.unitCode = val ? val.unit : null
                                row.unitName = val ? val.unitName : null
                              }`)
                            }
                          },
                          materialName: {
                            type: 'string',
                            title: i18nExpression('contractMod.materialName'),
                            'x-render-table-column': {
                              minWidth: 140,
                            },
                            'x-component-props':{
                              disabled:expression(`!!$table.getRowByIndex($self.index)?.materialCode`)
                            },
                            ...editTableFormItemValid
                          },
                          categoryName: {
                            type: 'string',
                            title: i18nExpression('common.categoryName'),
                            'x-render-table-column': {
                              minWidth: 140,
                            },
                            'x-component': 'CCategorySelect',
                            'x-component-props':{
                              showKey: 'categoryName',
                               '@select': expression(`(node) => {
                                let row = $table.getRowByIndex($self.index)
                                row.categoryId = node ? node.categoryId : null
                                row.categoryName = node ? node.categoryName : ''
                                row.categoryCode = node ? node.categoryCode : ''
                              }`)
                            }
                          },
                          // 只用于展示
                          taxKey: {
                            type: 'string',
                            title: i18nExpression('contractMod.taxRate'),
                            'x-render-table-column': {
                              minWidth: 120
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'tax',
                              '@change': expression(`(value) => {
                                const data = $taxDictClass.getDictDetail('tax', value)

                                const row = $table.getRowByIndex($self.index)

                                if (data) {
                                  row.taxRate = data.key
                                }

                                $calcMaterialTaxedPrice($form, row)
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          // 未税单价
                          untaxedPrice: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('bid_mod.untaxedPrice'),
                            'x-render-table-column': {
                              minWidth: 120
                            },
                            'x-component-props': {
                              '@change': expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          taxedPrice: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('bid_mod.taxUnitPrice'),
                            'x-render-table-column': {
                              minWidth: 120,
                              static: true
                            },
                            'x-component-props': {
                              controls: false,
                              precision: 2
                            }
                          },
                          contractQuantity: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.contractQuantity'),
                            'x-render-table-column': {
                              minWidth: 120
                            },
                            'x-component-props': {
                              '@change': expression(`() => {
                                $calcMaterialTaxedPrice($form, $table.getRowByIndex($self.index))
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          amount: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.amount2'),
                            'x-render-table-column': {
                              minWidth: 120,
                              static: true
                            }
                          },
                          unitName: {
                            type: 'string',
                            title: i18nExpression('contractMod.unitName'),
                            'x-render-table-column': {
                              minWidth: 120,
                            },
                            'x-component':'DictSelect',
                            'x-component-props':{
                              code:'unit'
                            }
                          },
                          // 实际用于计算并且不会被 render-table 真实创建 or 渲染
                          taxRate: {
                            type: 'string',
                            'x-hidden': true
                          },
                          
                          unAmount: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.unAmount'),
                            'x-render-table-column': {
                              minWidth: 120,
                              static: true
                            }
                          },
                          taxQuota: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.taxQuota'),
                            'x-render-table-column': {
                              minWidth: 120,
                              static: true
                            }
                          },
                          // ceeaUsedAmount: {
                          //   type: 'number',
                          //   title: i18nExpression('contractMod.usedAmount'),
                          //   default: 0,
                          //   'x-component-props': {
                          //     controls: false,
                          //     precision: 2
                          //   },
                          //   'x-render-table-column': {
                          //     minWidth: 150
                          //   }
                          // },
                          // ceeaUsedNumber: {
                          //   type: 'number',
                          //   title: i18nExpression('contractMod.usedNumber'),
                          //   default: 0,
                          //   'x-component-props': {
                          //     controls: false,
                          //     precision: 2
                          //   },
                          //   'x-render-table-column': {
                          //     minWidth: 150
                          //   }
                          // },
                          // startDate: {
                          //   title: '价格执行有效期从',
                          //   'x-render-table-column': {
                          //     minWidth: 230
                          //   },
                          //   type: 'date',
                          //   default: null,
                          //   'x-component-props': {
                          //     placeholder: i18nExpression('common.pleaseSelectDate'),
                          //     format: 'yyyy-MM-dd',
                          //     'value-format': 'yyyy-MM-dd'
                          //   }
                          // },
                          // endDate: {
                          //   title: i18nExpression('bid_mod.priceEndTime'),
                          //   'x-render-table-column': {
                          //     minWidth: 230
                          //   },
                          //   type: 'date',
                          //   default: null,
                          //   'x-component-props': {
                          //     placeholder: i18nExpression('common.pleaseSelectDate'),
                          //     format: 'yyyy-MM-dd',
                          //     'value-format': 'yyyy-MM-dd'
                          //   }
                          // },
                          extInvoiceType:{
                            type: 'string',
                            title:'发票类型',
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component':'DictSelect',
                            'x-component-props':{
                              code:'EXT_SOU_INQ_ORDER_INVOICE_TYPE'
                            },
                            ...editTableFormItemValid
                          },
                          specification: {
                            type: 'string',
                            title: i18nExpression('contractMod.specification'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          manufacturer: {
                            type: 'string',
                            title: i18nExpression('contractMod.manufacturer'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          // isDangerChemistry: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.isDangerChemistry'),
                          //   'x-component': 'DictSelect',
                          //   'x-component-props': {
                          //     code: 'YES_OR_NO'
                          //   },
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // placeOfOrigin: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.placeOfOrigin'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // isInstallDebug: {
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.isInstallDebug'),
                          //   'x-render-table-column': {
                          //     minWidth: 140
                          //   },
                          //   ...selectByYOrNSegment,
                          //   'x-decorator': ''
                          // },
                          shelfLife: {
                            type: 'number',
                            default: undefined,
                            title: '质保期(自然日)',
                            'x-component-props': {
                              controls: false,
                            },
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          lineRemark: {
                            type: 'string',
                            title: i18nExpression('contractMod.lineRemark'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          itemNumber: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.itemNumber'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          // itemName: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.itemName'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // taskNumber: {
                          //   type: 'number',
                          //   'x-hidden': true,
                          //   default: undefined,
                          //   title: i18nExpression('contractMod.taskNumber'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // taskName: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.taskName'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // shipFrom: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.shipFrom'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // destination: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('contractMod.destination'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   }
                          // },
                          // tradeTerm: {
                          //   type: 'string',
                          //   'x-hidden': true,
                          //   title: i18nExpression('bidMod.tradeTerm'),
                          //   'x-render-table-column': {
                          //     minWidth: 120
                          //   },
                          //   'x-component': 'DictSelect',
                          //   'x-component-props': {
                          //     code: 'trade_clause'
                          //   }
                          // },
                          // sourceNumber: {
                          //   type: 'number',
                          //   'x-hidden': true,
                          //   default: undefined,
                          //   title: i18nExpression('contractMod.sourceNumber'),
                          //   'x-render-table-column': {
                          //     minWidth: 120,
                          //     static: true
                          //   }
                          // },
                          operation: {
                            type: 'void',
                            title: i18nExpression('common.operation'),
                            'x-render-table-column': {
                              width: 60,
                              fixed: 'right'
                            },
                            'x-component': 'RenderTableButtonList',
                            'x-reactions': expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                                  '@click': expression(`
                                    () => {
                                      $table.remove($self.index)
                                    }
                                  `)
                                }
                              }
                            }
                          }
                        })
                      }
                    }
                  },
                  // 付款计划
                  paymentPlan: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('cusEntry.contractMod.paymentPlan')
                    },
                    'x-visible': expression('!$isTermination'),
                    // 'x-read-pretty': expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                    properties: {
                      toolbar: {
                        type: 'void',
                        'x-component': 'ButtonList',
                        'x-component-props': {
                          class: 'list-form__toolbar'
                        },
                        'x-reactions': expression(`(field) => {
                          field.visible = !$form.readPretty
                        }`),
                        properties: {
                          add: {
                            type: 'void',
                            title: i18nExpression('common.add'),
                            'x-component-props': {
                              type: 'primary',
                              disabled: expression(`$vendor()`),
                              '@click': expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('payPlanData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)
                            }
                          }
                        }
                      },
                      payPlanData: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          editMode: true,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false,
                          primaryKey: 'payPlanId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true,
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'payPlans:*',
                        'x-read-pretty': expression(`$vendor() || $form.readPretty`),
                        properties: generateXindexInOrder({
                          paymentPeriod: {
                            type: 'string',
                            title: i18nExpression('cusEntry.contractMod.paymentPeriod'),
                            'x-render-table-column': {
                              minWidth: 120,
                              align: 'center'
                            },
                            'x-component': 'RenderTableIndex',
                            'x-component-props': {
                              '@changeIndex': expression(`(index) => {
                                $self.value = index + 1
                              }`)
                            },
                          },
                          paymentStage: {
                            type: 'string',
                            title: i18nExpression('cusEntry.contractMod.paymentStage'),
                            'x-render-table-column': {
                              minWidth: 130
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'PAYMENT_STAGE'
                            },
                            ...editTableFormItemValid
                          },
                          // 付款条件
                          payExplain: {
                            type: 'string',
                            title: i18nExpression('cusEntry.contractMod.payExplain'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'payExplain',
                              'custom-select-type': 'payExplain'
                            },
                            ...editTableFormItemValid
                          },
                          dateNum: {
                            type: 'number',
                            title: i18nExpression('contractMod.dateNum'),
                            'x-render-table-column': {
                              minWidth: 80
                            },
                            'x-component-props': {
                              controls: false,
                            },
                            ...editTableFormItemValid
                          },
                          // paymentRatio: {
                          //   type: 'number',
                          //   default: undefined,
                          //   title: i18nExpression('contractMod.paymentRatio'),
                          //   'x-render-table-column': {
                          //     minWidth: 100
                          //   },
                          //   'x-component-props': {
                          //     controls: false,
                          //     '@change': expression(`(value) => {
                          //       const includeTaxAmount = $form.query('includeTaxAmount').get('value')

                          //       if (value && includeTaxAmount) {
                          //         const stagePaymentAmount = (includeTaxAmount * value) / 100

                          //         $self.query('.stagePaymentAmount').take(field => {
                          //           field.value = stagePaymentAmount
                          //         })
                          //       }
                          //     }`)
                          //   },
                          //   ...editTableFormItemValid
                          // },
                          stagePaymentAmount: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('cusEntry.contractMod.stagePaymentAmount'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            // 'x-disabled': true,
                            'x-component-props': {
                              controls: false,
                            },
                            ...editTableFormItemValid
                          },
                          plannedPaymentDate: {
                            title: i18nExpression('cusEntry.contractMod.plannedPaymentDate'),
                            'x-render-table-column': {
                              minWidth: 130
                            },
                            type: 'date',
                            default: null,
                            'x-component-props': {
                              placeholder: i18nExpression('common.pleaseSelectDate'),
                              format: 'yyyy-MM-dd',
                              'value-format': 'yyyy-MM-dd',
                            },
                            ...editTableFormItemValid
                          },
                          // WIRE_TRANSFER 电汇、HONOUR 承兑 、WIRE_AND_HONOUR 电汇+承兑
                          payMethod: {
                            type: 'string',
                            title: i18nExpression('cusEntry.contractMod.paymentMethod'),
                            'x-render-table-column': {
                              minWidth: 140
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'PAYMENT_MODE'
                            },
                            ...editTableFormItemValid
                          },
                          extAcceptanceDate: {
                            type:'date',
                            title:'承兑期限',
                            'x-render-table-column':{
                              minWidth: 120
                            },
                            default:null,
                            'x-component-props':{
                              placeholder: i18nExpression('common.pleaseSelectDate'),
                              format: 'yyyy-MM-dd',
                              'value-format': 'yyyy-MM-dd',
                            }
                            // ...editTableFormItemValid
                          },
                          /**
                           * TODO:
                           *  1、付款方式为承兑，默认100
                           *  2、付款方式为电汇+承兑时，需要填写值并且承兑比例和电汇比例等于100
                           */
                          extAcceptanceRatio:{
                            type:'number',
                            default: undefined,
                            title: '承兑比例%',
                            'x-render-table-column':{
                              minWidth:120
                            },
                            'x-component-props': {
                              controls: false
                            },
                            ...feedbackLayoutIsPopover,
                            'x-validator': {
                              required: true,
                              message: i18nExpression('common.requiredField')
                            },
                            'x-reactions':{
                              dependencies:['.payMethod','.extWireTransferRatio'],
                              fulfill:{
                                state:{
                                  value:expression(`
                                    $deps[0] === 'HONOUR' ? 100 : (
                                      $deps[0] === 'WIRE_TRANSFER' ? 0 : (100-$deps[1])
                                    )
                                  `)
                                }
                              }
                            }
                          },
                          /**
                           * TODO:
                           *  1、付款方式为电汇时，默认100
                           *  2、付款方式为电汇+承兑时，需要填写值并且承兑比例和电汇比例等于100
                           */
                          extWireTransferRatio:{
                            type:'number',
                            default: undefined,
                            title: '电汇比例%',
                            'x-render-table-column':{
                              minWidth:120
                            },
                            'x-component-props': {
                              controls: false
                            },
                            ...feedbackLayoutIsPopover,
                            'x-validator': {
                              required: true,
                              message: i18nExpression('common.requiredField')
                            },
                            'x-reactions':{
                              dependencies:['.payMethod','.extAcceptanceRatio'],
                              fulfill:{
                                state:{
                                  value:expression(`
                                    $deps[0] === 'WIRE_TRANSFER' ? 100 : (
                                      $deps[0] === 'HONOUR' ? 0 : (100-$deps[1])
                                    )
                                  `)
                                }
                              }
                            }
                          },
                          operation: {
                            type: 'void',
                            title: i18nExpression('common.operation'),
                            'x-render-table-column': {
                              width: 60,
                              fixed: 'right'
                            },
                            'x-component': 'RenderTableButtonList',
                            'x-reactions': expression(`(field) => {
                              field.visible = !$form.readPretty
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)
                                }
                              }
                            }
                          }
                        })
                      }
                    }
                  },
                  /**
                  * 合作伙伴
                    1、合同头表信息“是否框架协议”为是时
                    合作伙伴为手工新增，可新增甲方，乙方，丙方
                    物料明细及付款计划不做强制校验是否录入，物料明细无需显示寻源单查询按钮，新增物料明细，不影响合作伙伴的内容显示

                    2、合同头表信息“是否框架协议”为否，
                    合作伙伴信息仅可手工添加丙方，且伙伴名称数据来源为除甲方外的业务实体进行选择
                    甲方、乙方均不可手工新增，
                    甲方仅为物料明细行所包含的所有业务实体
                    乙方为头表信息上的供应商信息

                    3、甲方/丙方所显示的伙伴名称，修改为业务实体对应的公司名称，但仍需记录对应的OU ID
                  */
                  partner: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('contractMod.partner')
                    },
                    'x-visible': expression('!$isTermination'),
                    'x-read-pretty': expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                    properties: {
                      partnerData: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          editMode: true,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false,
                          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                          primaryKey: 'partnerId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'contractPartners:*',
                        'x-read-pretty': expression(`$vendor() || $form.readPretty`),
                        properties: generateXindexInOrder({
                          partnerType: {
                            type: 'string',
                            'x-read-pretty': true,
                            title: i18nExpression('contractMod.partnerType'),
                            enum: [
                              {
                                label: i18nExpression('contractMod.owner'),
                                value: '甲方'
                              },
                              {
                                label: i18nExpression('contractMod.partyB'),
                                value: '乙方'
                              },
                              {
                                label: i18nExpression('contractMod.partyC'),
                                value: '丙方'
                              }
                            ],
                            'x-component': 'Select',
                            'x-component-props': {
                              // 没从原有功能看出作用，应该是死功能
                              // '@change': expression(`(value) => {
                              //   if (value === '乙方') {
                              //     const parntererNameList = vendorName
                              //       ? [{ address: vendorName, value: vendorName }]
                              //       : []
                              //   } else {
                              //     parntererNameList = []
                              //   }
                              // }`)
                            },
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            ...editTableFormItemValid
                          },
                          partnerName: {
                            type: 'string',
                            title: i18nExpression('contractMod.partnerName'),
                            'x-read-pretty': true,
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component': 'OrganizationSelector',
                            'x-component-props': {
                              multiple: false,
                              nodeType: 'COMPANY',
                              'read-pretty': expression(`$vendor() || $form.readPretty`),
                              scope: expression('$table.getRowByIndex($self.index)'),
                              '@select': expression(`(node, value, scope) => {
                                const row = $table.getRowByIndex($self.index)
                                $self.value = node ? node.organizationName : null
                                row.ouId = node ? node.organizationId : null
                                row.ouCode = node ? node.organizationCode : null
                                row.ouName = node ? node.organizationName : null
                              }`)
                            },
                            'x-reactions': {
                              dependencies: ['.partnerType'],
                              fulfill: {
                                schema: {
                                  // 'x-read-pretty': expression(`$form.readPretty || ($deps[0] !== '乙方' && !!$self.value) || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                                  'x-component': expression(`
                                    $self.index === undefined
                                      ? ''
                                      : $deps[0] === '乙方' || !!$self.value
                                        ? 'Input'
                                        : $deps[0] === '丙方'
                                        ? 'QuickSearchWrapper'
                                        : 'OrganizationSelector'
                                  `),
                                  'x-component-props': expression(`
                                    $deps[0] === '丙方'
                                    ? {
                                      showKey: 'companyName',
                                      propKey: 'companyName',
                                      'name': 'scc_sup_company_info_new',
                                    } : {
                                      multiple: false,
                                      nodeType: 'COMPANY',
                                      disabled: $vendor() || $form.readPretty,
                                      scope: $table.getRowByIndex($self.index),
                                      '@select': (node, value, scope) => {
                                        const row = $table.getRowByIndex($self.index)
                                        $self.value = node ? node.organizationName : null
                                        row.ouId = node ? node.organizationId : null
                                        row.ouCode = node ? node.organizationCode : null
                                        row.ouName = node ? node.organizationName : null
                                      }
                                    }
                                  `)
                                }
                              }
                            },
                            ...editTableFormItemValid
                          },
                          contactName: {
                            type: 'string',
                            title: '授权代表',
                            'x-render-table-column': {
                              minWidth: 120
                            },
                            'x-reactions':{
                              dependencies: ['.partnerType'],
                              fulfill:{
                                schema:{
                                  'x-read-pretty':expression(`$form.readPretty || $vendor()`),
                                  'x-component':expression(`
                                    $self.index === undefined ? '' :
                                    ($deps[0] === '甲方' ?
                                    'QuickSearchWrapper' : 'Input')
                                  `),
                                  'x-component-props':expression(`
                                    $deps[0] === '甲方' ?
                                    {
                                      showKey:'nickname',
                                      propKey:'contactName',
                                      name:'scc_rbac_user_display',
                                      '@close-quicksearch': (val,scope) => {
                                        const row = $table.getRowByIndex($self.index)
                                        $self.value = val ? val.nickname : null
                                        row.contactName = val ? val.nickname : null
                                        row.extEmployeeNumber = val ? val.username : null
                                      }
                                    }:{}
                                  `)
                                }
                              }
                            },
                            ...editTableFormItemValid
                          },
                          phone: {
                            type: 'string',
                            title: i18nExpression('contractMod.mobileNumber'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            ...editTableFormItemValid
                          },
                          address: {
                            type: 'string',
                            title: i18nExpression('components.address.addressInfo'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // fax: {
                          //   type: 'string',
                          //   title: i18nExpression('contractMod.fax'),
                          //   'x-render-table-column': {
                          //     minWidth: 150
                          //   }
                          // },
                          bankName: {
                            type: 'string',
                            title: i18nExpression('contractMod.openingBank'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          bankAccount: {
                            type: 'string',
                            title: i18nExpression('contractMod.bankAccount'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // postCode: {
                          //   type: 'string',
                          //   title: i18nExpression('contractMod.postcode'),
                          //   'x-render-table-column': {
                          //     minWidth: 100
                          //   }
                          // },
                          taxPayer: {
                            type: 'string',
                            title: i18nExpression('dataConfMod.taxPayer'),
                            'x-render-table-column': {
                              minWidth: 130
                            },
                            ...editTableFormItemValid
                          },
                          extStampStatus: {
                            type: 'string',
                            default: 'UNSTAMP',
                            'x-hidden': expression('$form.values.formal !== \'ELECTRONIC_CONTRACT\''),
                            title: i18nExpression('cusEntry.dataConfMod.extStampStatus'),
                            'x-render-table-column': {
                              minWidth: 130
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_EXT_STAMP_STATUS',
                              disabled: true
                            }
                          }
                        })
                      }
                    }
                  },
                  // 合同附件信息
                  fileInfo: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: '合同附件信息（内外部均可见，仅用于传递合同盖章文件）'
                    },
                    properties: {
                      toolbar: {
                        type: 'void',
                        'x-component': 'ButtonList',
                        'x-component-props': {
                          class: 'list-form__toolbar'
                        },
                        'x-reactions': expression(`(field) => {
                          field.visible = !$form.readPretty && $buyer()
                        }`),
                        properties: {
                          add: {
                            type: 'void',
                            title: i18nExpression('common.add'),
                            'x-component-props': {
                              type: 'primary',
                              disabled: expression(`$vendor()`),
                              '@click': expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('fileUploads'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)
                            }
                          }
                        }
                      },
                      fileUploads: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          maxHeight: 400,
                          editMode: true,
                          pagination: false,
                          sortable: false,
                          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                          primaryKey: 'annexId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'annexes:*',
                        properties: generateXindexInOrder({
                          // 协议类型
                          fileType: {
                            type: 'string',
                            title: i18nExpression('dataConfMod.attachmentType'),
                            default: 'OTHER_AGREEMENT',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              disabled: expression(`$vendor() || $form.readPretty`),
                              code: 'CONTRACT_AGREEMENT_ATTACHMENT'
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // 附件名称
                          fileSourceName: {
                            type: 'string',
                            title: i18nExpression('bidMod.fileName'),
                            'x-component': 'SrmCommonFile',
                            'x-component-props': {
                              disabled: expression(`$vendor() || $form.readPretty`),
                              extraData: {
                                fileModular: 'sup',
                                fileFunction: 'vendorBiddingManagement',
                                fileType: 'images'
                              },
                              defaultFile: {
                                fileId: expression(`
                                !$self.value
                                  ? undefined
                                  : $table.getRowByIndex($self.index).fileuploadId
                                `),
                                fileName: expression('$self.value && String($self.value)')
                              },
                              '@on-change': expression(`({ file }) => {
                                const row = $table.getRowByIndex($self.index)
                                row.fileuploadId = String(file.fileId)
                                row.createdFullName = file.createdFullName
                                row.creationDate = file.creationDate

                                setTimeout(() => {
                                  $self.value = String(file.fileName)
                                })
                              }`)
                            },
                            'x-read-pretty': expression('$readOnly'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            ...editTableFormItemValid
                          },
                          // 上传人
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('purchaseDemand.attachmentCreatedBy'),
                            'x-read-pretty':true,
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // 上传时间
                          creationDate: {
                            type: 'string',
                            title: i18nExpression('purchaseDemand.attachmentCreatedDate'),
                            'x-read-pretty':true,
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          operation: {
                            type: 'void',
                            title: i18nExpression('common.operation'),
                            'x-render-table-column': {
                              width: 120,
                              fixed: 'right'
                            },
                            'x-component': 'RenderTableButtonList',
                            'x-reactions': expression(`(field) => {
                              $buttonType == 'management' || $buttonType == 'approve'
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$buttonType !== 'management'`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }`
                                  )
                                },
                              },
                              edit: {
                                type: 'void',
                                title: i18nExpression('common.edit'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$buttonType !== 'approve'`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      const row = $table.getRowByIndex(rowIndex)
                                      $onlyOfficeView(row, $form, $buttonType === 'approve')
                                    }`
                                  )
                                },
                              }
                            }
                          }
                        })
                      }
                    }
                  },
                  // 盖章附件信息
                  sealFileInfo: {
                    type: 'void',
                    'x-visible': expression('!$isTermination'),
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: '盖章附件信息'
                    },
                    properties: {
                      toolbar: {
                        type: 'void',
                        'x-component': 'ButtonList',
                        'x-component-props': {
                          class: 'list-form__toolbar'
                        },
                        'x-reactions': expression(`(field) => {
                          field.visible = ($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')
                        }`),
                        properties: {
                          add: {
                            type: 'void',
                            title: i18nExpression('common.add'),
                            'x-component-props': {
                              type: 'primary',
                              disabled: expression(`$vendor()`),
                              '@click': expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('stampAnnexes'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)
                            }
                          }
                        }
                      },
                      stampAnnexes: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          maxHeight: 400,
                          editMode: true,
                          pagination: false,
                          sortable: false,
                          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                          primaryKey: 'stampAnnexId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'stampAnnexes:*',
                        properties: generateXindexInOrder({
                          // 协议类型
                          fileType: {
                            type: 'string',
                            title: i18nExpression('dataConfMod.attachmentType'),
                            'x-read-pretty': expression(`!($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')`),
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_AGREEMENT_ATTACHMENT'
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // 附件名称
                          fileSourceName: {
                            type: 'string',
                            title: i18nExpression('bidMod.fileName'),
                            'x-component': 'SrmCommonFile',
                            'x-component-props': {
                              disabled: expression(`!($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')`),
                              extraData: {
                                fileModular: 'sup',
                                fileFunction: 'vendorBiddingManagement',
                                fileType: 'images'
                              },
                              defaultFile: {
                                fileId: expression(`
                                !$self.value
                                  ? undefined
                                  : $table.getRowByIndex($self.index).fileuploadId
                                `),
                                fileName: expression('$self.value && String($self.value)')
                              },
                              '@on-change': expression(`({ file }) => {
                                const row = $table.getRowByIndex($self.index)
                                row.fileuploadId = String(file.fileId)
                                row.createdFullName = file.createdFullName
                                row.creationDate = file.creationDate

                                setTimeout(() => {
                                  $self.value = String(file.fileName)
                                })
                              }`)
                            },
                            'x-read-pretty': expression('$readOnly'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            ...editTableFormItemValid
                          },
                          // 上传时间
                          creationDate: {
                            type: 'string',
                            'x-read-pretty':true,
                            title: i18nExpression('purchaseDemand.attachmentCreatedDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                           operation: {
                            type: 'void',
                            title: i18nExpression('common.operation'),
                            'x-render-table-column': {
                              width: 60,
                              fixed: 'right'
                            },
                            'x-component': 'RenderTableButtonList',
                            'x-reactions': expression(`(field) => {
                              field.visible = ($attrs.params.flag === 'archive' && $buyer() && $values.formal === 'PAPER_CONTRACT')
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type: 'text',
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }`
                                  )
                                },
                              }
                            }
                          }
                        })
                      }
                    }
                  },
                  // 操作记录
                  operateRecord: {
                    type:'void',
                    'x-visible': expression('!$isTermination'),
                    'x-component':'FormCollapse.Item',
                    'x-component-props':{
                      title:'操作记录'
                    },
                    'x-read-pretty':true,
                    properties: {
                      operationLogs:{
                        type:'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns:'seq',
                          editMode: false,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false,
                          // 如果都没有标记，那么默认使用 id 作为联表主键的 key
                          primaryKey: 'operationLogId',
                          // 启用级联删除的储值行为
                          cascadeDeletion: true,
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'operationLogs:*',
                        'x-read-pretty': true,
                        properties: generateXindexInOrder({
                          operationType:{
                            type:'string',
                            title:'操作类型',
                            'x-render-table-column':{
                              minWidth:130
                            },
                            'x-component':'DictSelect',
                            'x-component-props':{
                              code:'CONTRACT_STATUS'
                            }
                          },
                          operationDesc: {
                            type:'string',
                            title:'描述',
                            'x-render-table-column': {
                              minWidth: 180
                            }
                          },
                          creationDate: {
                            type:'string',
                            title:'操作时间',
                            'x-render-table-column':{
                              minWidth:130
                            }
                          },
                          createdFullName:{
                            type:'string',
                            title:'操作人',
                            'x-render-table-column':{
                              minWidth:130
                            }
                          }
                        })
                      }
                    }
                  },
                  // 补充协议说明
                  supplementaryAgreement: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('contractMod.supplementaryAgreement')
                    },
                    'x-visible': expression('!$isTermination'),
                    'x-reactions': changeFieldVisibleByDeps(['contractType'], `$deps[0] === 'SUPPLEMENTAL_AGREEMENT'`),
                    properties: {
                      layout: {
                        type: 'void',
                        'x-decorator': 'FormLayout',
                        'x-decorator-props': {
                          layout: 'vertical'
                        },
                        'x-component': 'FormGrid',
                        'x-component-props': {
                          maxColumns: 4,
                          columnGap: 32,
                          rowGap: 0
                        },
                        properties: {
                          supplementAgreementExplain: {
                            type: 'string',
                            title: i18nExpression('common.pleaseTypeContents'),
                            'x-component': 'Input.TextArea',
                            'x-component-props': {
                              rows: 4,
                              maxlength: 200,
                              'show-word-limit': true,
                              placeholder: i18nExpression('common.pleaseTypeContents')
                            }
                          }
                        }
                      }
                    }
                  },
                  // 合同详情
                  contractContent: {
                  type: 'void',
                  'x-component': 'FormCollapse.Item',
                  'x-component-props': {
                    title: i18nExpression('contractMod.contractContent')
                  },
                  'x-reactions': changeFieldVisibleByDeps(
                    ['modelEnable'],
                    '!$isTermination && $eqY($deps[0])'
                  ),
                  properties: {
                    // 标准合同：只能编辑合同模板带出来的元素值
                    // 非标准合同：能编辑合同模板带出来的元素值、和合同模板内容
                    printContent: {
                      type: 'void',
                      'x-component': 'HTMLElement',
                      'x-component-props': {
                        id: 'printContent',
                        style: {
                          // IE浏览器 649px x 978px
                          width: '794px',
                          // height: '1123px',
                          margin: '0 auto',
                          position: 'relative'
                        }
                      },
                      properties: {
                        markedContent: {
                          type: 'void',
                          'x-component': 'div',
                          'x-component-props': {
                            id: 'markedContent',
                            style: {
                              width: '100%'
                            },
                            contenteditable: queryFieldValueExpression('state', 'data.contenteditable')
                          }
                        }
                      }
                    }
                  }
                  }
            })
          }
        }
      },
      // 下拉框节点列表
      CFillProgress: {
        type: 'void',
        'x-component': 'CFillProgress',
        'x-component-props': {
          class: "contract-progress",
          'ref': "contractProgress",
          'nodeName': "$t('logisticsMod.contractInfo')",
          data: expression(`($attrs.params?.flag === 'termination' || $attrs.params?.contractType === 'TERMINATION') || $form.query('modelEnable').take().value == 'Y'?$form.query('state').get('data').progress:$form.query('state').get('data').progressNo`),
          percentage: "{{true}}",
          '@index-click': `{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`
        }
      }
    }
  }
})

const components = {
  CFillProgress,
  ApprovalProcess
}
</script>

<template>
  <RenderEngine :viewModel="form" schemaKey="contractManagerDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>

<style>
.info-fill-progress{
  position: fixed;
  width: 210px;
  top: 104px;
  right: 0px;
  bottom: 0px;
}
.contractMg {
  overflow: auto;
  padding-right: 181px;
}
.order-form-contain .contract-progress{
  top: 64px
}
</style>
