<script setup lang="ts">
import { useAttrs, type Vue, nextTick } from 'vue-demi'
import { useDebounceFn } from '@vueuse/core'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  editTableFormItemValid,
  requiredValidatorSegment,
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  formGridSegment,
  radioGroupByYOrNSegment,
  checkboxByYOrNSegment,
  selectByYOrNSegment
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
  toJS
} from '@meicloud/render-engine'

// @ts-ignore
import cloneDeep from 'lodash/cloneDeep'
// @ts-ignore
import { FILE_UPLOAD } from '@/api/common'
// @ts-ignore
import { contractManagement } from 'modb@/contractManagement/api/index'
// @ts-ignore
import { DictClass, createDictClass } from '@/library/utils/dict/dict-utils'
// @ts-ignore
import { loadJS } from '@/utils';
// @ts-ignore
import Parser from 'modb@/contractManagement/views/contractManager/parser'
// @ts-ignore
import CFillProgress from 'lib@/components/c-fill-progress'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { numericUppercase } from 'lib@/utils/number'

const { http, emitTabRemove, app, t, eqY, eqN, buyer, vendor } = usePageHelper()
// TODO 封装一个可以传递泛型的
const attrs: any = useAttrs()

const viewUpdateButtonSave = ($form: any) => {
  if (attrs.params.flag == 'view') {
    return false
  }
  return $form.values.needVendorConfirm != 'Y' &&
    (
      ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
      (attrs.params ? (attrs.params.flag === 'add' || attrs.params.flag === 'termination' || attrs.params?.contractType == 'TERMINATION') : null)
    )
}

const viewUpdateButtonsubmit = ($form: any) => {
  if (attrs.params.flag == 'view') {
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
        (attrs.params ? (attrs.params.flag === 'termination' || attrs.params?.contractType == 'TERMINATION') : null))
    )
  }

  if (componentInstance.workflowParamsInfo.integrationMode !== 'None') { // 有工作流
    return (
      $form.values.needVendorConfirm != 'Y' && buyer() &&
      (['DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.values.contractStatus) ||
        (attrs.params ? attrs.params.flag === 'add' : null))
    )
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

const handleButtonConfig = ($form: any, componentInstance: any) => {
  componentInstance.buttonConfigInfo.save.view = viewUpdateButtonSave($form)
  componentInstance.buttonConfigInfo.submit.view = viewUpdateButtonsubmit($form)
  componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton($form)
  componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton($form)

  const contractHeadId = $form.values.contractHeadId || ''
  const tabDisabled = !['SUPPLIER_CONFIRMED', 'REJECTED', 'WITHDRAW', 'APPROVAL', 'UNDER_REVIEW', 'UN_ARCHIVED', 'SIGNATUREING', 'ARCHIVED', 'TERMINATED', 'ABANDONED'].includes($form.values.contractStatus) || $form.values.contractStatus === 'DRAFT'
  updateWorkflowconfig(componentInstance, contractHeadId, tabDisabled, {})
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.name = '暂存'
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
    handleButtonConfig($form, componentInstance)
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    handleButtonConfig($form, componentInstance)
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

    generateComponent({
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
  }
}

const initData = ($form: any) => {
  $form.values.contractType = attrs.params.contractType // 合同类型

  if (attrs.params.flag === 'add') {
    // MIAN_CONTRACT_ALTER 变更
    if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes($form.values.contractType)) {
      $form.values.contractHeadId = attrs.params.rowId
    } else {
      return false
    }
  } else {
    $form.values.contractHeadId = attrs.params.row.contractHeadId
  }

  updateButtonConfig($form)

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
  const pdf = await http.post('/api-base/pdf/pdfAddWatermark', formData, {
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

const $isTermination = attrs.params?.flag === 'termination' || attrs.params?.contractType === 'TERMINATION'

const $handleSubmit =
  async ($form: ViewModel, $queryEngine, $bus, type = 'submit') => {
    // 暂存数据
    const temporaryData = type === 'savePublish'

    const run = async (values: Record<string, any>) => {
      $calcIncludeTaxAmount($form)

      const state = $form.query('state').get('data')

      const workFlow = ['approval', 'publish'].includes(type)

      if (eqY(values.ceeaIsPortableContract) && workFlow) {
        if (!temporaryData && values.includeTaxAmount > 20000) {
          // "合同金额大于2万，不能设置为便捷合同，已自动帮您修改为非便捷合同！"
          // @ts-ignore
          app.$message.warning(t('contractMod.msgContractManage[10]'))
          values.ceeaIsPortableContract = 'N'
          return
        }
      }

      if (!temporaryData && new Date(values.effectiveDateFrom.replace(/-/g, '/')) > new Date(values.effectiveDateTo.replace(/-/g, '/'))) {
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
        return app.$message.error(t('只能有一个甲方') as string)
      }
      if (!temporaryData && bolpartnerType2 > 1) {
        return app.$message.error(t('只能有一个乙方') as string)
      }

      // 校验文件是否上传
      let isNull = !values.fileUploads.length || values.fileUploads.some(i => !i.fileuploadId)

      if (!temporaryData && isNull && ($isTermination || workFlow && values.modelEnable == 'N')) {
        return app.$message.error(t('contractMod.msgContractManage[11]') as string)
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

      if (workFlow && eqY(state.ceeaIfVirtual)) {
        if (!temporaryData && !values.frameworkAgreementCode) {
          return app.$message.error(t('contractMod.msgContractManage[12]') as string)
        }
      }

      if (eqN(values.isFrameworkAgreement) && workFlow) {
        const totalPercent = values.payPlanData.reduce((sum, item) => {
          return sum + Number(item.paymentRatio)
        }, 0)

        const totalMoney = values.payPlanData.reduce((sum, item) => {
          return sum + Number(item.stagePaymentAmount)
        }, 0)

        if (!temporaryData && totalPercent !== 100 && !$isTermination) {
          // app.$message.error(app.$t('contractMod.msgContractManage[7]')) // 付款比例之和不能大于100！
          app.$message.error('付款比例之和必须等于100！')
          return
        }
        if (!$isTermination) {
          const includeTaxAmount = $form.query('.includeTaxAmount').take().value
          if (!temporaryData && includeTaxAmount != totalMoney) {
            app.$message.error('阶段付款金额总和应与合同总金额相等')
            return false
          }
        }


        if (!temporaryData && !values.materialListData.length && !$isTermination) {
          return app.$message.error(t('contractMod.msgContractManage[27]') as string)
        }

        if (!temporaryData && !values.payPlanData.length && workFlow && !$isTermination) {
          return app.$message.error(t('contractMod.msgContractManage[15]') as string)
        }

        const payPlanDataBol = values.payPlanData.some(e => !(e.paymentPeriod && e.paymentStage && e.payExplain && e.dateNum && e.paymentRatio && e.plannedPaymentDate && e.payMethod))

        if (!temporaryData && payPlanDataBol && workFlow && !$isTermination) {
          app.$message.error(t('contractMod.payPlanDataBol') as string)
          return
        }

        let materialListDataBol = values.materialListData.some(e => !(e.invId && e.tradingLocations && e.materialCode && e.untaxedPrice && e.contractQuantity && e.taxRate))
        if (!temporaryData && materialListDataBol && workFlow && !$isTermination) {
          app.$message.error(t('contractMod.materialListDataBol') as string)
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

            ;['modelLines', 'annexes', 'payPlans', 'contractMaterials', 'contractPartners']
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
      if (!values.contractHeadId) {
        $form.setValues({
          contractHeadId,
          ...res.data[0],
          contractType: attrs.params.contractType
        })
      }

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
      if (['savePublish', 'publish', 'SUPPLIER_CONFIRMING', 'SUPPLIER_REFUSE', ''].includes(type)) {
        $cancel($bus)
        return
      }
    }

    if (type !== 'savePublish') {
      await $form.validate()
      run($form.values)
      // $form.submit(run)
    } else {
      run($form.values)
    }
  }

const $handlePreview = ($form: ViewModel) => {
  const state = $form.query('state').get('data')

  if (state.contractTemplateComponentInstance) {
    state.contractTemplateComponentInstance.editable = false
  }

  state.contenteditable = false
}

const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any, $eqY: any) => {
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

  if (type === 'SUBMIT') {
    $handlePreview($form)
    setTimeout(() => {
      $handleSubmit($form, $queryEngine, $bus, "approval")
    }, 50)
  } else if (type === 'SAVE') {
    $handlePreview($form)
    setTimeout(() => {
      $handleSubmit($form, $queryEngine, $bus, "")
    }, 50)
  }
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
}

const scope = {
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

  // api
  $contractManagement: contractManagement,
  numericUppercase,

  $calcIncludeTaxAmount,
  $handleSubmit,
  $cancel,
  $compileMarkedContent,
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
    $calcIncludeTaxAmount($form)

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
  initButtonConfig,
  updateButtonConfig,
  viewUpdateButtonSave,
  $saveBill,
  initData
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

  // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
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
                code: 'contractFinancialInformation',
                name: t('contractMod.contractFinancialInformation'),
                percentage: 0
              },
              {
                code: 'itemInfo',
                name: t('purchaseDemand.itemInfo'),
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
          code: 'contractFinancialInformation',
          name: t('contractMod.contractFinancialInformation'),
          percentage: 0
        },
        {
          code: 'itemInfo',
          name: t('purchaseDemand.itemInfo'),
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
            initButtonConfig($form)

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
                  text: 'loading',
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
            value.totalItems = value?.includeTaxAmount
            value.totalMaterialAmount = numericUppercase(value?.includeTaxAmount)

            $form.setValues({
              ...value,
              contractType: $attrs.params.contractType || value.contractType
            })

            updateButtonConfig($form)

            // 单纯文本只读状态
            $form.readPretty = $readOnly || $attrs.params.flag === 'view'
            if (['SUPPLIER_CONFIRMED'].includes($attrs.params.row.contractStatus)) {
              $form.readPretty = true
            }
            if ($attrs.params.flag === 'add') {
              $form.query('.contractStatus').take(filed => {
                filed.value = ''
              })
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
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression(`$form.values.contractHeadId || null`),
          'business-type': 'CONTRACT',
          'button-custom': expression(`{}`),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $eqY)
          }`),
          '@close-tab': expression(`() => {
            $cancel($bus)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)
        },
        items: {
          type: 'object',
          'x-query-engine-skip': true,
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
                '@click': expression(`() => {
                      $handlePreview($form)
                      $getPdfFile(true)
                    }`)
              },
              'x-reactions': changeFieldVisibleByDeps(['modelHeadId'], `
                  !!$deps[0] && $illegalNotView && $attrs.params.flag !== 'view'
                  `)
            },
            //  编辑合同详情
            editContractDetail: {
              type: 'void',
              'x-content': i18nExpression('contractMod.editContractDetail'),
              'x-component': 'Button',
              'x-component-props': {
                style: {
                  'margin': '8px'
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
                  'margin': '8px'
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
                  'margin-right': '8px'
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
                // 供应商确认按钮
                confirm: {
                  type: 'void',
                  'x-content': i18nExpression('orderMod.buyerOrderSynergy.confirm'),
                  'x-component': 'Button',
                  'x-component-props': {
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
                    '@click': expression('() => $handleSubmit($form, $queryEngine, $bus, "SUPPLIER_REFUSE")')
                  },
                  'x-reactions': changeFieldVisibleByDeps(['contractStatus'], `
                  $deps[0] === 'SUPPLIER_CONFIRMING' && !$buyer() && $attrs.params.flag !== 'view'
                  `)
                }
              }
            },
            properties: {
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
                              ? 4
                              : $deps[0] === 'APPROVAL'
                                ? 3
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
                    'x-read-pretty': expression(`$vendor() || $isTermination || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                            'x-visible': expression('!$isTermination'),
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
                              showWordLimit: true
                            },
                            // 'x-index': expression('$isTermination ? 0 : 4'),
                            ...requiredValidatorSegment
                          },
                          // 业务实体
                          buId: {
                            type: 'string',
                            title: i18nExpression('contractMod.fullPathId'),
                            'x-visible': expression('$buyer()'),
                            'x-decorator': 'FormItem',
                            'x-component': 'OrganizationSelector',
                            'x-component-props': {
                              placeholder: i18nExpression('common.pleaseSelect'),
                              'read-pretty': '{{$form.readPretty || $vendor()}}',
                              'parent-id': -1,
                              jumpLogin: expression('$jumpLogin'),
                              'node-type': 'OU',
                              disabled: expression('$isTermination || $form.readPretty || $values.contractType !== "MIAN_CONTRACT_ADD"'),
                              '@select': expression(`(node, value) => {
                                const { organizationCode, organizationName, fullPathId, organizationId } = node

                                // 设置到 form.values 上
                                $values.buCode = organizationCode
                                $values.buName = organizationName
                                $values.buFullPathId = fullPathId

                                const partnerData = $form.query('partnerData').take()

                                partnerData.value.forEach((e, index) => {
                                  if (e.partnerType == '甲方') {
                                    partnerData.remove(index)
                                  }
                                })

                                $self.disabled = true

                                $http({
                                  url: '/api-base/organization/organization/getCompanyByOuId',
                                  method: 'GET',
                                  loading: true,
                                  params: {
                                    organizationId: organizationId,
                                  }
                                }).then((res) => {
                                  if (res.code != '0') {
                                    $self.value = ''
                                  }

                                  const partnerDataItem = {
                                    partnerType: '甲方',
                                    ouId: res.data?.organization?.organizationId,
                                    partnerName: res.data?.organization?.organizationName,
                                    taxPayer: res.data?.orgCompany?.taxNumber
                                  }

                                  if (res.data.orgCompanyBankList.length > 0) {
                                    Object.assign(partnerDataItem, {
                                      partnerName: res.data.organization.organizationName,
                                      bankAccount: res.data.orgCompanyBankList[0].bankAccount,
                                      bankName: res.data.orgCompanyBankList[0].bankName
                                    })
                                  }
                                  if (res.data.orgCompanyPersonList.length > 0) {
                                    Object.assign(partnerDataItem, {
                                      contactName: res.data.orgCompanyPersonList[0].name
                                    })
                                  }
                                  if (res.data.orgCompanyAddressList.length > 0) {
                                    res.data.orgCompanyAddressList.forEach(datas=>{
                                      if (datas.isActive == 'Y') {
                                        Object.assign(partnerDataItem, {
                                          postCode : datas?.postalCode,
                                          phone: datas?.phone,
                                          address : datas?.address
                                        })
                                        return false
                                      }
                                    })
                                  }

                                  partnerData.push(partnerDataItem)
                                }, (resOrr) => {
                                  $self.value = ''
                                  $values.buCode = ''
                                  $values.buName = ''
                                  $values.buFullPathId = ''
                                }).finally(() => {
                                  $self.disabled = false
                                })
                              }`)
                            },
                            ...requiredValidatorSegment
                          },
                          buName: {
                            type: 'string',
                            title: i18nExpression('contractMod.fullPathId'),
                            'x-hidden': expression('$buyer()'),
                            'x-decorator': 'FormItem'
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
                              'name': 'scc_sup_company_info_new',
                              '@close-quicksearch': expression(`(val, scope) => {
                                $values.vendorId = val ? val.companyId : ''
                                $values.vendorCode = val ? val.companyCode : ''
                                $values.erpVendorCode = val ? val.erpVendorCode : ''
                                $values.erpVendorId = val ? val.erpVendorId : ''
                                if (val.companyName) {
                                  const partnerData = $form.query('partnerData').take()

                                  if (!partnerData.value.some(item => item.partnerType === '乙方')) {
                                    partnerData.value.push({
                                      partnerType: '乙方',
                                      partnerName: val.companyName,
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
                              code: 'CONTRACT_FORM2'
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
                              code: 'MANAGEMENT_CONTROL_MODEL'
                            }
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
                              placeholder: i18nExpression('common.pleaseTypeContents')
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
                    'x-read-pretty': expression(`$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                              }`),
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
                                    $self.readPretty || !['MIAN_CONTRACT_ALTER','MIAN_CONTRACT_ADD'].includes($deps[0])
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
                            title: i18nExpression('contractMod.totalAmountTax1'),
                            default: 0,
                            'x-decorator': 'FormItem',
                            'x-component-props': {
                              controls: false,
                              precision: 2
                            }
                          },
                          // 币种
                          currencyCode: {
                            type: 'string',
                            title: i18nExpression('contractMod.currencyCode'),
                            default: 'CNY',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'currency'
                            },
                            'x-decorator': 'FormItem',
                            ...requiredValidatorSegment
                          },
                          // 是否框架协议
                          isFrameworkAgreement: {
                            title: i18nExpression('contractMod.isFrameworkAgreement'),
                            ...radioGroupByYOrNSegment,
                            ...requiredValidatorSegment
                          },
                          // 框架协议编号
                          frameworkAgreementCode: {
                            type: 'string',
                            title: i18nExpression('contractMod.frameworkAgreementCode'),
                            'x-decorator': 'FormItem',
                            'x-content': expression(`{
                              append: {
                                functional: true,
                                render(h) {
                                  return $vendor() || $form.readPretty
                                    ? undefined
                                    : h('el-button', {
                                        props: { type: 'primary', icon: 'el-icon-search', disabled:$form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT' },
                                        on: {
                                          click: () => {
                                            const vendorNameField = $form.query('vendorName').take()

                                            if (!$values.vendorId || !vendorNameField.value) {
                                              // 请先选择供应商
                                              return $message.error($t('bid_mod.setPermissionError'))
                                            }

                                            $form.query('frameworkAgreementDialog').take().setComponentProps({ visible: true })

                                            setTimeout(() => {
                                              $reactiveAction(() => {
                                                const queryDataField = $form.query('frameworkAgreementDialog.queryData').take()
                                                queryDataField.value.vendorName = vendorNameField.value
                                                queryDataField.data.vendorId = $values.vendorId
                                              })
                                            })
                                          }
                                        }
                                      })
                                    }
                                  }
                                }
                            `)
                          }
                        }
                      }
                    }
                  },
                  // 物料明细
                  itemInfo: {
                    type: 'void',
                    'x-component': 'FormCollapse.Item',
                    'x-component-props': {
                      title: i18nExpression('purchaseDemand.itemInfo')
                    },
                    'x-visible': expression('!$isTermination'),
                    'x-read-pretty': expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                              disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              '@click': expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('materialListData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
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
                            'x-hidden': expression('!$buyer()'),
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
                            title: i18nExpression('contractMod.tradingLocations'),
                            'x-render-table-column': {
                              minWidth: 300
                            },
                            'x-component': 'DictSelect',
                            'x-read-pretty': expression(`$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                            'x-component-props': {
                              // disabled: expression(`$vendor() || $form.readPretty || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              code: expression(`String($self.query('.invId').get('value') || '')`),
                              'custom-select-type': expression(`
                                $self.query('.invId').get('value') ? 'RECEIVE_ADDRESS' : ''
                              `),
                              style: {
                                width: '100%'
                              },
                              '@change-value': expression(`(_, node) => {
                                const row = $table.getRowByIndex($self.index)
                                row.receiveContact = node.receiver
                                row.receiveTelephone = node.receiverPhone
                                // row.tradingLocations = node.siteName
                              }`)
                            },
                            ...editTableFormItemValid
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
                              disabled: expression(`$form.readPretty || !$self.query('.invId').get('value') || $vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              'show-input': expression(`$self.value`),
                              'read-pretty': '{{$form.readPretty}}',
                              'show-key': 'materialCode',
                              'name': 'scc_base_material_item_contract',
                              'pre-query-data': expression(`{
                                'o.ORGANIZATION_ID': $self.query('.invId').get('value')
                              }`),
                              '@close-quicksearch': expression(`(val, scope) => {
                                const row = $table.getRowByIndex($self.index)

                                row.materialId = val ? val.materialId : null
                                row.materialName = val ? val.materialName : null
                                row.categoryName = val ? val.categoryName : null
                                row.categoryId = val ? val.categoryId : null
                                row.categoryCode = val ? val.categoryCode : null
                                row.specification = val ? val.specification : null
                                row.unitCode = val ? val.unit : null
                                row.unitName = val ? val.unitName : null
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          materialName: {
                            type: 'string',
                            title: i18nExpression('contractMod.materialName'),
                            'x-render-table-column': {
                              minWidth: 140,
                              static: true
                            }
                          },
                          categoryName: {
                            type: 'string',
                            title: i18nExpression('contractMod.categoryName'),
                            'x-render-table-column': {
                              minWidth: 140,
                              static: true
                            }
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
                              static: true
                            }
                          },
                          // 实际用于计算并且不会被 render-table 真实创建 or 渲染
                          taxRate: {
                            type: 'string',
                            'x-hidden': true
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
                          ceeaUsedAmount: {
                            type: 'number',
                            title: i18nExpression('contractMod.usedAmount'),
                            default: 0,
                            'x-component-props': {
                              controls: false,
                              precision: 2
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          ceeaUsedNumber: {
                            type: 'number',
                            title: i18nExpression('contractMod.usedNumber'),
                            default: 0,
                            'x-component-props': {
                              controls: false,
                              precision: 2
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          startDate: {
                            title: i18nExpression('bid_mod.priceStartTime'),
                            'x-render-table-column': {
                              minWidth: 230
                            },
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.startDate, '{y}-{m}-{d}')
                              }`)
                            }
                          },
                          endDate: {
                            title: i18nExpression('bid_mod.priceEndTime'),
                            'x-render-table-column': {
                              minWidth: 230
                            },
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.endDate, '{y}-{m}-{d}')
                              }`)
                            }
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
                          isDangerChemistry: {
                            type: 'string',
                            title: i18nExpression('contractMod.isDangerChemistry'),
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'YES_OR_NO'
                            },
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          placeOfOrigin: {
                            type: 'string',
                            title: i18nExpression('contractMod.placeOfOrigin'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          isInstallDebug: {
                            title: i18nExpression('contractMod.isInstallDebug'),
                            'x-render-table-column': {
                              minWidth: 140
                            },
                            ...selectByYOrNSegment,
                            'x-decorator': ''
                          },
                          shelfLife: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.shelfLife'),
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
                          itemName: {
                            type: 'string',
                            title: i18nExpression('contractMod.itemName'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          taskNumber: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.taskNumber'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          taskName: {
                            type: 'string',
                            title: i18nExpression('contractMod.taskName'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          shipFrom: {
                            type: 'string',
                            title: i18nExpression('contractMod.shipFrom'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          destination: {
                            type: 'string',
                            title: i18nExpression('contractMod.destination'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          tradeTerm: {
                            type: 'string',
                            title: i18nExpression('bidMod.tradeTerm'),
                            'x-render-table-column': {
                              minWidth: 120
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'trade_clause'
                            }
                          },
                          sourceNumber: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.sourceNumber'),
                            'x-render-table-column': {
                              minWidth: 120,
                              static: true
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
                      title: i18nExpression('contractMod.paymentPlan')
                    },
                    'x-visible': expression('!$isTermination'),
                    'x-read-pretty': expression(`$form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                              disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                            title: i18nExpression('contractMod.paymentPeriod'),
                            'x-render-table-column': {
                              minWidth: 80,
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
                            title: i18nExpression('contractMod.paymentStage'),
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
                            title: i18nExpression('contractMod.payExplain'),
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
                            type: 'string',
                            title: i18nExpression('contractMod.dateNum'),
                            'x-render-table-column': {
                              minWidth: 80
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'PAYMENT_PERIOD'
                            },
                            ...editTableFormItemValid
                          },
                          paymentRatio: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.paymentRatio'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              controls: false,
                              '@change': expression(`(value) => {
                                const includeTaxAmount = $form.query('includeTaxAmount').get('value')

                                if (value && includeTaxAmount) {
                                  const stagePaymentAmount = (includeTaxAmount * value) / 100

                                  $self.query('.stagePaymentAmount').take(field => {
                                    field.value = stagePaymentAmount
                                  })
                                }
                              }`)
                            },
                            ...editTableFormItemValid
                          },
                          stagePaymentAmount: {
                            type: 'number',
                            default: undefined,
                            title: i18nExpression('contractMod.stagePaymentAmount'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            // 'x-disabled': true,
                            'x-component-props': {
                              controls: false,
                            },
                            ...editTableFormItemValid
                          },
                          plannedPaymentDate: {
                            title: i18nExpression('contractMod.plannedPaymentDate'),
                            'x-render-table-column': {
                              minWidth: 100
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
                          payMethod: {
                            type: 'string',
                            title: i18nExpression('contractMod.paymentMethod'),
                            'x-render-table-column': {
                              minWidth: 140
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'PAYMENT_MODE'
                            },
                            ...editTableFormItemValid
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
                              disabled: expression(`$vendor() || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
                              '@click': expression(`() => {
                                $self.query($getFieldParentFieldFormPath($self, 2).concat('partnerData'))
                                  .take(field => {
                                    field.componentProps.componentInstance.addRow()
                                  })
                              }`)
                            }
                          }
                        }
                      },
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
                            }
                          },
                          partnerName: {
                            type: 'string',
                            title: i18nExpression('contractMod.partnerName'),
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
                                  'x-read-pretty': expression(`$form.readPretty || ($deps[0] !== '乙方' && !!$self.value) || $form.values.contractType == 'SUPPLEMENTAL_AGREEMENT'`),
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
                            }
                          },
                          contactName: {
                            type: 'string',
                            title: i18nExpression('contractMod.represent'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          phone: {
                            type: 'string',
                            title: i18nExpression('contractMod.mobileNumber'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          address: {
                            type: 'string',
                            title: i18nExpression('components.address.addressInfo'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          fax: {
                            type: 'string',
                            title: i18nExpression('contractMod.fax'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
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
                          postCode: {
                            type: 'string',
                            title: i18nExpression('contractMod.postcode'),
                            'x-render-table-column': {
                              minWidth: 100
                            }
                          },
                          taxPayer: {
                            type: 'string',
                            title: i18nExpression('dataConfMod.taxPayer'),
                            'x-render-table-column': {
                              minWidth: 100
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
                                  'x-component-props': {
                                    disabled: expression(`$vendor()`)
                                  },
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)
                                },
                                'x-reactions': expression(`(field) => {
                                  const contractType = field.query('contractType').get('value')
                                  const row = $table.getRowByIndex(field.index)

                                  const visible = () => {
                                    if (!$buyer()) {
                                      return false
                                    }

                                    // 新增
                                    if (contractType === 'MIAN_CONTRACT_ADD') {
                                      return true
                                    }

                                    // 变更
                                    if (contractType === 'MIAN_CONTRACT_ALTER') {
                                      return !row.sourceId
                                    }

                                    if (contractType === 'SUPPLEMENTAL_AGREEMENT') {
                                      return false
                                    }

                                    return true
                                  }

                                  field.visible = visible()
                                }`)
                              },
                              // 失效
                              failure: {
                                type: 'void',
                                title: i18nExpression('common.inactive'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$vendor()`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      const row = $table.getRowByIndex(field.index)

                                      row.enable = 'N'
                                    }
                                  `)
                                },
                                'x-reactions': expression(`(field) => {
                                  const contractType = field.query('contractType').get('value')
                                  const row = $table.getRowByIndex(field.index)

                                  const visible = () => {
                                    if (!$buyer()) {
                                      return false
                                    }

                                    // 新增
                                    if (contractType === 'MIAN_CONTRACT_ADD') {
                                      return false
                                    }

                                    // 变更
                                    if (contractType === 'MIAN_CONTRACT_ALTER') {
                                      return (
                                        row.sourceId &&
                                        (row.partnerType === '丙方' ||
                                        row.partnerType === '甲方')
                                      )
                                    }

                                    if (contractType === 'SUPPLEMENTAL_AGREEMENT') {
                                      return false
                                    }

                                    return true
                                  }

                                  field.visible = visible()
                                }`)
                              }
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
                      title: i18nExpression('contractMod.fileInfo')
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
                        'x-read-pretty': true,
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
                            }
                          },
                          // 上传人
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('purchaseDemand.attachmentCreatedBy'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // 上传时间
                          creationDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`)
                            },
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
                              field.visible = !$form.readPretty
                            }`),
                            properties: {
                              delete: {
                                type: 'void',
                                title: i18nExpression('common.delete'),
                                'x-component-props': {
                                  type: 'text',
                                  disabled: expression(`$vendor() || $form.readPretty`),
                                  '@click': expression(`
                                    ({ rowIndex }) => {
                                      $table.remove(rowIndex)
                                    }
                                  `)
                            },
                          }
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
  CFillProgress
}
</script>

<template>
  <RenderEngine schemaKey="contractManagerDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
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
