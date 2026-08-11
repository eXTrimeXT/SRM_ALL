<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, markRaw, generateXindexInOrder, expression, i18nExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  requiredValidatorSegment,
  formGridSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import { useAttrs, computed, ref } from 'vue-demi'

import baseInfo from './components/baseInfo'
import materialsDetails from './components/materialsDetails'
import returnMaterialsDailog from './components/materialsDailog'
import fileInfo from './components/fileInfo'
const { emitTabRemove, t: $t, app } = usePageHelper()
let $attrs: any = useAttrs()

// saveOrUpdate    供应商保存 DRAFT
// vendorSubmit    供应商提交 / 采购商驳回后重新提交 WAITING_BUYER_CONFIRM
// buyerAccept     采购方接受 VALID
// buyerReject     采购方驳回 BUYER_REJECT
const $saveFormBill = (type: string, status: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const { returnMaterialsDailog, ...values } = $form.values
  if (type === 'saveOrUpdate') {
    $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
  } else {
    $form.validate('*(!returnMaterialsDailog)').then(() => {
      // 校验是否维护物料
      if (values.detailList.length == 0) {
        $message.warning($t('outsource.fillReturnDetailList')) // 请维护委外退料单明细!
        return false
      } else {
        $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
      }
    }).catch((err: any) => {
      console.log(err)
    })
  }
}

const $submitData = (action: string, billStatus: any, $values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const form = $values
  $queryEngine.request.baseRequest({
    type: 'OsMaterialReturn',
    action: action,
    service: 'sup-ce',
    loading: true,
    payload: [{
      ...form,
      status: billStatus
    }],
    query: { '*': {} }
  }).then((res: any) => {
    const returnId = res.originalData?.records[0] || ''
    if (action == 'saveOrUpdate') {
      $message.success($t('common.successSave'))
      $form.values.returnId = returnId
      $queryEngine.request.read(returnId, { action: 'getDetail' })
    } else {
      $message.success($t('common.successSave'))
      emitTabRemove($attrs.tabName)
    }
    $bus.$emit('osgMaterialReturnList')
  }).catch((err: any) => {
    console.log(err)
  })
}
// 选择物料去重
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
        // ...row,
        ...otherFiled
      })
    }
  })
  console.log(dataArr)
  data.push(...dataArr)
  // 设置行号
  data.forEach((row: any, i: number) => (row.rowNum = Number(i + 1)))
  // 已剔除重复勾选数据，明细行应唯一！
  if (isTip) return app.$message.warning($t('orderMod.checkDataRowUnique'))
}
// 物料新增弹框选择确认
const $setMaterialData = (selections: any, $form: any, $message: any) => {
  if (selections.length === 0) {
    return $message.warning($t('common.msgSelectData'))
  }
  // 去重依据字段 materialReqDetailId，目前放开去重
  const ids = $form.values.detailList.map((item: any) => {
    if (!item.materialReqDetailIdXXX) return item.materialCode
  })
  $setRepeatData(
    ids,
    $form.values.detailList,
    selections,
    'materialReqDetailIdXXX',
    (v: any) => {
      return {
        materialLineId: v.materialLineId,
        materialHeadNum: v.materialHeadNum,
        materialReqRow: v.rowNum,
        orderDetailId: v.orderDetailId,
        orderNumber: v.orderNumber, // 采购订单号
        orderDetailRow: v.orderRowNum, // 采购订单行号
        materialReqNum: v.materialReqNum, // 委外领料单
        materialReqDetailId: v.materialReqDetailId, // 委外领料单ID
        materialId: v.materialId,
        materialCode: v.materialCode,
        materialName: v.materialName,
        materialUnit: v.materialUnit,
        baseMaterialId: v.baseMaterialId,
        baseMaterialCode: v.baseMaterialCode,
        baseMaterialName: v.baseMaterialName,
        baseMaterialUnit: v.baseMaterialUnit,
        baseMaterialNum: v.baseMaterialNum,
        materialRowNum: v.materialRowNum,
        returnQuantity: v.returnQuantity, // 已退料数量
        receivedQuantity: v.receivedQuantity, // 已领数量
        orderQuantity: v.orderNum, // 采购订单数量
        thisReturnQuantity: null, // 本次退料数量
        isUpdateUnreceived: 'Y', // 是否更新未领数量
        returnReason: '', // 退料原因
        detailComments: '' // 明细备注
      }
    },
  )

  $form.query('.returnMaterialsDailog').take().setComponentProps({
    visible: false
  })
}

// 单据可编辑控制
const $formEditFlag = ($form: any, $queryEngine: any) => {
  let status = $form.values.status
  return ![undefined, '', 'DRAFT'].includes(status)
}

const questionDetailSchema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
    }
  },
  OsMaterialReturn: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      'type': 'OsMaterialReturn',
      actions: {
        getDetail: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.returnId
            $form.values.returnId = id
            return !!id
          }`),
          method: 'read',
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            let returnId = $attrs.params.returnId || data.payload[0]
            data.action = 'getDetail'
            data.tree = true
            data.loading = true
            data.payload = {
              "filter": {
                  "returnId": {
                      eq: returnId
                  }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res.records[0]
            $form.setValues({
              ...data
            })
          }`)
        },
        saveOrUpdate: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        },
        vendorSubmit: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
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
          // 委外领料单基础信息
          receiptInfo: {
            ...baseInfo
          },
          // 委外领料单明细
          orderDetail: {
            ...materialsDetails
          },
          // 相关附件
          relevantAttachment: {
            ...fileInfo
          }
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        buttonList: {
          type: 'void',
          'x-component': 'ButtonList',
          properties: {
            // DRAFT 拟定 | WAITING_BUYER_CONFIRM 待采购方确认 | BUYER_REJECT 采购商驳回 | VALID 生效
            goBack: {
              type: 'void',
              title: expression('$t($readOnly ? "common.backTo" : "common.cancel")'),
              'x-component-props': {
                messageBox: expression(`$readOnly ? undefined : () => ({
                  type: 'warning',
                  title: $t("common.tips"),
                  message: $t('outsource.goBackConfirm'),
                  showCancelButton: true,
                  beforeClose: (action, dom, done) => {
                    // action的值有cancel confirm
                    if (action === 'confirm') {
                      $bus.$emit('osgMaterialReturnList')
                      emitTabRemove($attrs.tabName)
                    }
                    done()
                  }
                })`),
                '@click': expression(`()=> {
                  if($readOnly){
                    emitTabRemove($attrs.tabName)
                    return
                  }
                }`)
              }
            },
            // 保存
            save: {
              type: 'void',
              title: i18nExpression('common.staging'),
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceReturnMaterialsSup:create'),
                type: 'default',
                plain: 'plain',
                '@click': expression(`async (values) => {
                  $saveFormBill('saveOrUpdate','DRAFT', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['status'], `
                  [undefined,'DRAFT'].includes($deps[0]) && !$form.readPretty
              `)
            },
            // 提交
            submit: {
              type: 'void',
              title: i18nExpression('common.submit'),
              'x-component-props': {
                type: 'primary',
                ...buttonListItemVisibleByPermission('outsourceReturnMaterialsSup:create'),
                '@click': expression(`async (values) => {
                  $saveFormBill('vendorSubmit','WAITING_BUYER_CONFIRM', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['status'], `
                  [undefined,'DRAFT'].includes($deps[0]) && !$form.readPretty
              `)
            },
            // 回复
            replay: {
              type: 'void',
              title: i18nExpression('common.reply'),
              'x-component-props': {
                type: 'primary',
                ...buttonListItemVisibleByPermission('outsourceReturnMaterialsSup:reply'),
                '@click': expression(`async (values) => {
                  $saveFormBill('vendorSubmit','WAITING_BUYER_CONFIRM', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['status'], `
                  ['BUYER_REJECT'].includes($deps[0]) && !$form.readPretty
              `)
            }
          }
        }
      }
    }
  },
  // 委外领料单明细选择弹窗
  ...returnMaterialsDailog
})

const scope = {
  emitTabRemove,
  app,
  $attrs,
  $markRaw: markRaw,
  $saveFormBill,
  $submitData,
  $formEditFlag,
  $setRepeatData,
  $setMaterialData
}
const components = {
}
</script>

<template>
  <RenderEngine
    schemaKey="outsourceReturnMaterialsDetailSup"
    :pageAttrs="$attrs"
    :schema="questionDetailSchema"
    :scope="scope"
    :components="components"
  />
</template>
