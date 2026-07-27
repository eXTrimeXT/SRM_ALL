<!-- eslint-disable quotes -->
<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  changeFieldVisibleByDeps,
  defineSchemas,
  markRaw,
  generateXindexInOrder,
  expression,
  i18nExpression,
  toJS
} from '@meicloud/render-engine'
import {
  buttonListItemVisibleByPermission,
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { useAttrs, computed, ref } from 'vue-demi'
import ApprovalHeader from './priceApprovalDetail/approvalHeader'
import ApprovalReport from './priceApprovalDetail/approvalReport'
import ApprovalFiles, { $addFileOne } from './priceApprovalDetail/approvalFiles'
import ApprovalBidding, { sourceTypeBoolean } from './priceApprovalDetail/approvalBidding'
import { PRICE_APPROVAL_FROM_TYPE_ENUM, mapToSourceType } from './composition'
import ProviceCity from 'lib@/components/provice-city'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import LadderPriceDialog from './priceApprovalDetail/ladderPrice'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import { transformMQL } from 'lib@/utils/util'
import { getHeaderField } from '@/utils'

const { emitTabRemove, t: $t, app } = usePageHelper()

/** 中标明细行 ---- start */

// 新增明细行
const $addOneBidItem = ($form: any) => {
  $form.query('.itemList').take((field:any) => {
    field.invoke('addRowByAllScenesEditableStatus', {
      // 价格类型默认值改成标准
      priceType: 'STANDARD',
      paymentList: []
    })
  })
}

// 处理虚拟物料
const $dealVisiualItem = ($form, $self, $queryEngine, $message) => {
  const rows = $form.query('PriceApprovalForBuyer.SchemaWorkflow.collapse.bidInfo.itemList').take()
    .componentProps.componentInstance
    .getCheckboxRecords()
  let visiualList = rows.filter(item => item.noCodeItem === 'Y' && item.hasRefreshNoCodeItem === 'N').filter(item => item.itemCode)
  if (visiualList.length) {
    let params = visiualList.map(item => ({
      approvalItemId: item.approvalItemId,
      itemCode: item.itemCode
    }))
    $queryEngine.request.baseRequest({
      type: 'PriceApprovalForBuyer',
      action: 'handleVirtualItems',
      query: {
        '*': {}
      },
      loading: true,
      payload: params
    }).then((res) => {
      $message.success($t('common.success'))
    })
  } else {
    // 请勾选待处理的虚拟物料行并且选择物料编码
    $message($t('bidMod.visitualItemMsg'))
  }
}

// 冗余业务实体信息
const setOrgObj = ($form: any, $table: any, $self: any, node: any) => {
  console.log('node', node)
  const row = $table.getRowByIndex($self.index)
  if (node && row.orgOuId === node.organizationId) {
    return
  }
  row.orgOuId = node ? node.organizationId : null
  row.orgOuCode = node ? node.organizationCode : null
  row.orgOuName = node ? node.organizationName : null
  row.orgInvId = null
  row.orgInvCode = null
  row.orgInvName = null

  // 重选库存组织，清空物料选择
  if (row.noCodeItem !== 'Y' && row.itemId) {
    // 非无料号
    setItemObj($form, $table, $self, null)
  } else {
    itemsDataRenderUpdate($self.index, row, $form)
  }
}

/* 冗余库存组织信息 */
const setOrganizationObj = ($form: any, $table: any, $self: any, node: any) => {
  const row = $table.getRowByIndex($self.index)
  if (node && row.orgInvId === node.organizationId) {
    return
  }

  row.orgInvId = node ? node.organizationId : null
  row.orgInvCode = node ? node.organizationCode : null
  row.orgInvName = node ? node.organizationName : null

  // 重选库存组织，清空物料选择
  if (row.noCodeItem !== 'Y' && row.itemId) {
    // 非无料号
    setItemObj($form, $table, $self, null)
  } else {
    itemsDataRenderUpdate($self.index, row, $form)
  }
}

/* 冗余供应商信息 */
const setVendorObj = ($form:any, $table:any, $self:any, val:any) => {
  const row = $table.getRowByIndex($self.index)
  row.vendorId = val ? val.companyId : null
  row.vendorCode = val ? val.companyCode : null
  row.vendorName = val ? val.companyName : null
  row.linkMan = val ? val.legalPerson : null
  itemsDataRenderUpdate($self.index, row, $form)
}

/* 冗余物料信息 */
const setItemObj = ($form, $table, $self, val) => {
  let row = $table.getRowByIndex($self.index)
  const rowItemObj = {
    itemId: val ? val.materialId : null,
    itemCode: val ? val.materialCode : null,
    itemDesc: val ? val.materialName : null,
    unit: val ? val.unit : null,
    categoryId: val ? val.categoryId : null,
    categoryCode: val ? val.categoryCode : null,
    categoryName: val ? val.categoryName : null
  }
  // 如果是简易询价的无料号寻源
  if ($pageType($form).isInquiry && row.noCodeItem === 'Y' && row.approvalItemId) {
    // 覆盖表格中同一个无料号来源的物料
    $form.values.itemList.forEach((item, itemIndex) => {
      if (row.approvalItemId === item.approvalItemId && item.noCodeItem === 'Y') {
        $form.values.itemList.splice(itemIndex, 1, {
          ...item,
          ...rowItemObj
        })
      }
    })
  } else {
    row = {
      ...row,
      ...rowItemObj
    }
    itemsDataRenderUpdate($self.index, row, $form)
  }
}

/* 触发数组渲染更新 */
const itemsDataRenderUpdate = (index, row, $form) => {
  $form.values.itemList.splice(index, 1, row)
}

/* 判断先选业务实体和库存组织才能选物料 */
const itemCodeQuickSearchBeforeOpen = (value, callback, $table, $self, $message) => {
  const row = $table.getRowByIndex($self.index)
  if (!row.orgOuId || !row.orgInvId) {
    $message.warning($t('bidMod.selectOuAndOrgMsg'))
    callback(null)
  }
}

/* 冗余币种信息 */
const setCurrency = ($form, $table, $self, value, dictItem) => {
  const row = $table.getRowByIndex($self.index)
  row.currencyId = dictItem ? dictItem.id : null
  row.currencyName = dictItem ? dictItem.label : null
}

/* 打开阶梯报价 */
const ladderClick = ($form, $table, $self, $message) => {
  const row = $table.getRowByIndex($self.index)
  if (!row.needNum) {
    $message.warning($t('bidMod.inpRequireNum'))
    return
  }
  $form.query('state').get('data').editIndex = $self.index
  $form.query('state').get('data').editRow = row
  $form.query('LadderPriceDialog').take().setComponentProps({
    visible: true
  })
}
/* 保存阶梯价 */
const saveLadderPrices = ($form, data) => {
  const { editIndex } = $form.query('state').get('data')
  $form.values.itemList[editIndex].ladderPriceType = data.ladderPriceType
  $form.values.itemList[editIndex].ladderPriceList = data.ladderPriceList
}

/* 查看付款方式 */
const openPaymentTypeDialog = ($form, $table, $self, $message) => {
  const row = $table.getRowByIndex($self.index)
  $form.query('state').get('data').editIndex = $self.index
  $form.query('state').get('data').paymentRow = row
  $form.query('state').get('data').paymentTypeDialogVisible = true
}

/* 保存付款条款 */
const savePaymentType = ($form, data) => {
  const { editIndex } = $form.query('state').get('data')
  $form.values.itemList[editIndex].paymentList = data
}

/* 删除一行 */
const deleteBidItem = ($form, $table, $self, $message, rowIndex) => {
  $table.remove(rowIndex)
}

/** 中标明细行 ---- end */

/** 暂存、提交 --- start */
const $saveBill = async (type, $form, $queryEngine, $confirm, $message, $bus) => {
  let tempSave = true
  if (type === 'SUBMIT') {
    /** 提交校验 */
    await $form.validate()
    // 手工创建需校验中标行信息
    if ($pageType($form).isHandMake) {
      const validate = await validateForm($form, $message)
      if (!validate) return
    }
    tempSave = false
  }
  const { itemList, ...rest } = $form.values
  let itemListCopy = JSON.parse(JSON.stringify(itemList))
  if (itemListCopy.length) {
    itemListCopy.forEach(item => {
      if (item.arrivalPlace && typeof (item.arrivalPlace) !== 'string') item.arrivalPlace = JSON.stringify(item.arrivalPlace)
    })
  }
  $queryEngine.request.baseRequest({
    type: 'PriceApprovalForBuyer',
    action: 'editPriceApproval',
    query: {
      '*': {}
    },
    timeout: 1000000,
    loading: true,
    payload: [{
      ...rest,
      itemList: itemListCopy,
      tempSave
    }]
  }).then(async (res: any) => {
    if (res?.data[0]) {
      let result = res?.data[0]
      $message.success($t('common.successSave'))
      $form.values.approvalId = result.approvalId
      await $queryEngine.request.read($form.values.approvalId, { action: 'getApprovalDetail' })
      if (type === 'SUBMIT') {
        const tabDisabled = false
        let formHeaderValue = getHeaderField(result)
        const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
        componentInstance.setWorkflowBusinessId($form.values.approvalId)
        componentInstance.setWorkflowTabDisabled(tabDisabled)
        componentInstance.setWorkflowBusinessVariables({
          procTitleObj: formHeaderValue
        })
        componentInstance.handlerAfter(type.toUpperCase(), () => {
          console.log('handlerAfter PriceApprovalForBuyer')
          emitTabRemove($attrs.tabName)
          $bus.$emit('PriceApprovalForBuyer')
        })
      }
    }
  }).catch((err) => {
    console.log(err)
  })
}

/* 校验 */
const validateForm = ($form, $message) => {
  return new Promise(resolve => {
    if ($form.values.itemList.length === 0) {
      // 中标行信息
      $message.warning($t('bidMod.msgWonBidRowInfo'))
      resolve(false)
      return
    }
    let resolveStatus = true
    for (const i of $form.values.itemList) {
      if (!i.vendorCode) {
        // 供应商编码
        $message.warning($t('bidMod.msgVendorCode'))
        resolveStatus = false
        return
      }
      if (!i.itemCode) {
        // 物料编码
        $message.warning($t('bidMod.msgItemInfo'))
        resolveStatus = false
        return
      }
      if (!i.taxKey) {
        // 税率
        $message.warning($t('bidMod.msgSelTaxRate'))
        resolveStatus = false
        return
      }
      if (!i.categoryName) {
        // 品类
        $message.warning($t('bidMod.msgEnterCate'))
        resolveStatus = false
        return
      }
      if (!i.orderCurrency) {
        // 币种
        $message.warning($t('vendorMod.msgCurrencyCode'))
        resolveStatus = false
        return
      }
      if (!i.winNum && $pageType($form).isHandMake) {
        // 中标数量必填
        $message.warning($t('vendorMod.msgSelBidder'))
        resolveStatus = false
        return
      }
    }
    resolve(resolveStatus)
  })
}

const parseJson = (json) => {
  if (typeof json === 'string') {
    json = JSON.parse(json)
    return parseJson(json)
  } else {
    return json
  }
}
/** 暂存、提交 --- end */

/** 审批流 ---- start */
const workflowStatus = ref('DRAFT')

const viewUpdateButton = computed(() => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(workflowStatus.value) && $attrs.params.flag !== 'readonly')

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = !$form.readPretty
    componentInstance.buttonConfigInfo.close.view = $form.readPretty
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    let formHeaderValue = getHeaderField($form.values)
    const componentInstance = $form.query('.SchemaWorkflow').take()?.componentProps.componentInstance
    if (!componentInstance) return
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.setWorkflowBusinessId($form.values.approvalId)
    componentInstance.setWorkflowTabDisabled(['DRAFT', 'REJECTED', 'WITHDRAW'].includes($form.values.approveStatus))
    componentInstance.setWorkflowBusinessVariables({
      procTitleObj: toJS(formHeaderValue)
    })
  }, 50)
}
/** 审批流 ---- end */

// 来源方式判断
const $pageType = ($form:any) => {
  // 默认手工创建
  const sourceFromType = $form.values.sourceFromType ?? PRICE_APPROVAL_FROM_TYPE_ENUM.HAND
  return {
    // 简易询价
    isInquiry: PRICE_APPROVAL_FROM_TYPE_ENUM.INQ === sourceFromType,
    // 手工创建
    isHandMake: PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE === sourceFromType,
    // 招标
    isBiding: PRICE_APPROVAL_FROM_TYPE_ENUM.BID === sourceFromType,
    // 项目式询价
    isBargain: PRICE_APPROVAL_FROM_TYPE_ENUM.BRG === sourceFromType,
    // 竞价
    isCompetition: PRICE_APPROVAL_FROM_TYPE_ENUM.COMP === sourceFromType,
    // 竞价MQL
    isAuct: PRICE_APPROVAL_FROM_TYPE_ENUM.AUCT === sourceFromType
  }
}

let $attrs: any = useAttrs()

const scope = {
  emitTabRemove,
  app,
  PRICE_APPROVAL_FROM_TYPE_ENUM,
  mapToSourceType,
  $attrs,
  $pageType,
  $addFileOne,
  $addOneBidItem,
  sourceTypeBoolean,
  setOrgObj,
  setOrganizationObj,
  setVendorObj,
  setItemObj,
  itemCodeQuickSearchBeforeOpen,
  setCurrency,
  ladderClick,
  openPaymentTypeDialog,
  savePaymentType,
  deleteBidItem,
  $saveBill,
  workflowStatus,
  initButtonConfig,
  updateButtonConfig,
  saveLadderPrices,
  parseJson,
  $dealVisiualItem
}

const components = {
  ApprovalHeader,
  ApprovalReport,
  ProviceCity,
  RenderAsyncText,
  LadderPriceDialog,
  PaymentTypeDialog
}

const schemas = defineSchemas({
  // 用于挂载全局数据
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      paymentRow: null, // 付款条款 当前行
      editRow: null, // 阶梯报价 当前行
      editIndex: null,
      ladderPriceDialogVisible: false,
      paymentTypeDialogVisible: false,
      sourceFromType: null,
      sourceFromId: null
    }
  },
  PriceApprovalForBuyer: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sou',
      actions: {
        getApprovalDetail: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            $form.readPretty = ['view','readonly','approval'].includes($attrs.params.flag)
            initButtonConfig($form)
            let id = $attrs.params.row.approvalId
            $form.values.approvalId = id
            return !!id
          }`),
          method: 'read',
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.action = 'getApprovalDetail'
            data.tree = true
            data.loading = true
            data.payload = [{
              approvalId:$form.values.approvalId
            }]
            data.query = {
              '*':{}
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res.records[0]
            if(data?.itemList?.length){
              data.itemList.forEach(item => {
                item.arrivalPlace && (item.arrivalPlace = parseJson(item.arrivalPlace))
              })
            }
            const { sourceFromType,sourceFromId } = data
            $form.query('state').get('data').sourceFromType = mapToSourceType[sourceFromType] // 存储旧的businessType，组件中使用
            $form.query('state').get('data').sourceFromId = sourceFromId
            $form.setValues({
              ...data
            })
            workflowStatus.value = data.approvalStatus
            updateButtonConfig($form)
          }`)
        },
        editPriceApproval: {
          // 启用级联删除的储值行为
          cascadeDeletion: true,
          immediate: false
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.approvalId || null'),
          'business-type': 'PRICE_APPROVAL_INITN',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            console.log('click-handler', type, $form, $confirm, $message)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            console.log('submit-direct', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            console.log('confirm', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
            $bus.$emit('PriceApprovalForBuyer')
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
              // 价格审批单 - 单据信息
              baseInfo: {
                ...ApprovalHeader
              },
              // 报表筛选条件
              approvalReport: {
                type: 'void',
                'x-component': 'CollapseItem', 
                'x-visible': expression(`$attrs.params.flag !== 'add' && ![PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE,PRICE_APPROVAL_FROM_TYPE_ENUM.AUCT].includes($form.values.sourceFromType)`),
                'x-component-props': {
                  // 报表
                  title: i18nExpression('bidMod.statement')
                },
                properties: {
                  report: {
                    type: 'void',
                    'x-component': 'ApprovalReport',
                    'x-component-props': {
                      'approval-header': expression(`{
                        sourceFromType: $form.query('state').get('data').sourceFromType,
                        sourceFromId: $form.query('state').get('data').sourceFromId,
                      }`),
                      'attrs-params-row': expression('$attrs.params.row'),
                      'page-type': expression(`$pageType($form)`)
                    }
                  }
                }
              },
              // 附件
              fileInfo: {
                ...ApprovalFiles,
                'x-visible': expression(`$form.values.sourceFromType !== PRICE_APPROVAL_FROM_TYPE_ENUM.INQ`)
              },
              // 中标行信息
              bidInfo: {
                ...ApprovalBidding
              }
            }
          }
        }
      }
    }
  },
  // 阶梯价弹窗
  LadderPriceDialog: {
    type: 'void',
    'x-component': 'LadderPriceDialog',
    'x-component-props': {
      'edit-row': expression(`$form.query('state').get('data').editRow`),
      'is-read-only': expression(sourceTypeBoolean),
      '@saveLadderPrices': expression(`(data) => {
        saveLadderPrices($form,data)
      }`),
      '@close': expression(`() => {
        $form.query('LadderPriceDialog').take().setComponentProps({
          visible:false
        })
      }`)
    }
  },
  PaymentTypeDialog: {
    type: 'void',
    'x-component': 'PaymentTypeDialog',
    'x-component-props': {
      'visible': expression(`$form.query('state').get('data').paymentTypeDialogVisible`),
      'business-type': expression(`$form.values.sourceFromType === PRICE_APPROVAL_FROM_TYPE_ENUM.HANDMAKE ? 'PRICE' : $form.query('state').get('data').sourceFromType`),
      'edit-row': expression(`$form.query('state').get('data').paymentRow`),
      'readonly': expression(sourceTypeBoolean),
      'adaptMql': true,
      '@savePaymentType': expression(`(data) => {
        savePaymentType($form,data)
      }`),
      '@before-close': expression(`() => {
        $form.query('state').get('data').paymentTypeDialogVisible = false
      }`)
    }
  }
})

</script>
<template>
  <RenderEngine
    schemaKey="priceApprovalDetail"
    :pageAttrs="$attrs"
    :schema="schemas"
    :scope="scope"
    :components="components"
  />
</template>
