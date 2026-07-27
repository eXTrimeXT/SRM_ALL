<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  changeFieldVisibleByDeps,
  defineSchemas,
  markRaw,
  generateXindexInOrder,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'

import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { useAttrs, computed, ref } from 'vue-demi'

import BaseInfo from './components/baseInfo'
import MaterialsDetails from './components/materialsDetails'
import MaterialsDailog from './components/materialsDailog'
import FileInfo from './components/fileInfo'

const { emitTabRemove, t: $t, app } = usePageHelper()
let $attrs: any = useAttrs()

// saveOrUpdate    采购商保存 CREATE
// submit          采购方提交 / 供方驳回后重新提交 SUBMIT / VALID(关闭供方确认后直接生效)
// vendorAccept    供方接受  VALID
// vendorRefuse    供方驳回  REFUSE
const $saveFormBill = (type: string, status: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const { outsourceMaterialsDailog, ...values } = $form.values
  if (type === 'saveOrUpdate') {
    $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
  } else {
    // 页面不去校验弹框相关的字段
    $form.validate('*(!outsourceMaterialsDailog)').then(() => {
      // 校验是否维护物料
      if (values.detailList.length == 0) {
        $message.warning($t('outsource.fillDetailList')) // '请维护委外领料单明细!'
        return false
      } else {
        $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
      }
    }).catch((err: any) => {
      console.log(err)
    })
  }
}

const $submitData = (action: string, status: any, $values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const form = $values
  $queryEngine.request.baseRequest({
    type: 'OsMaterialReq',
    action: action,
    service: 'sup-ce',
    loading: true,
    payload: [{
      ...form,
      handleStatus: status
    }],
    query: { '*': {} }
  }).then((res:any) => {
    const materialReqId = res.originalData?.records[0] || ''
    if (action == 'saveOrUpdate') {
      $message.success($t('common.successSave'))
      $form.values.materialReqId = materialReqId
      $queryEngine.request.read(materialReqId, { action: 'getDetail' })
    } else {
      $message.success($t('common.successSave'))
      emitTabRemove($attrs.tabName)
    }
    $bus.$emit('osMaterialReqListBuyer')
  }).catch((err:any) => {
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
        ...row,
        ...otherFiled
      })
    }
  })
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
  // 去重依据字段 materialLineId 目前放开去重
  const ids = $form.values.detailList.map((item: any) => {
    if (!item.materialLineIdXXX) return item.materialCode
  })
  $setRepeatData(
    ids,
    $form.values.detailList,
    selections,
    'materialLineIdXXX',
    (v: any) => {
      return {
        materialLineId: v.materialLineId,
        materialHeadNum: v.materialHeadNum, // 委外用料单号
        materialRowNum: v.rowNum, // 委外用料单行号
        orderDetailId: v.orderDetailId, // 采购订单ID
        orderNumber: v.orderNumber, // 采购订单号
        orderRowNum: v.orderDetailRow, // 采购订单行号
        orderNum: v.orderDetailQuantity, // 采购订单数量
        materialId: v.materialId,
        materialCode: v.materialCode,
        materialName: v.materialName,
        materialUnit: v.materialUnit,
        baseMaterialId: v.baseMaterialId,
        baseMaterialCode: v.baseMaterialCode,
        baseMaterialName: v.baseMaterialName,
        baseMaterialUnit: v.baseMaterialUnit,
        baseMaterialNum: v.baseMaterialNum, // 组件物料数量
        unreceivedQuantity: v.unreceivedQuantity, // 未领数量
        receivedQuantity: v.receivedQuantity, // 已领料数量
        returnQuantity: null, // 已退料数量 v.returnQuantity
        thisReceivedQuantity: v.unreceivedQuantity, // 本次领料数量 (默认值为未领数量)
        vendorReceiptQty: null, // 供应商签收数量
        vendorDiffQty: null, // 差异数量
        notReceivedQuantity: null,
        receivedAddress: '',
        receivedLinkman: '',
        receivedPhone: '',
        buyerRemark: '', // 采购商 行明细备注
        rowStatus: ''
      }
    },
  )

  $form.query('.outsourceMaterialsDailog').take().setComponentProps({
    visible: false
  })
}

// 单据可编辑控制
const $formEditFlag = ($form: any, $queryEngine: any) => {
  let status = $form.values.handleStatus
  return ![undefined, '', 'CREATE'].includes(status)
}
// 单独根据某个状态控制输入框禁用编辑
const $formInputEditFlag = ($form: any, ctrlStatus: any[]) => {
  let status = $form.values.handleStatus
  return ctrlStatus.includes(status)
}
// 供方确认隐藏控制(控制表格明细差异字段)
const $receiveConfirmCtrl = ($form: any, $queryEngine: any) => {
  let receiveConfirm = $form.query('state').get('data').receiveConfirm
  let handleStatus = $form.values.handleStatus
  return !receiveConfirm || (receiveConfirm && !['REFUSE', 'VALID', 'SUBMIT'].includes(handleStatus))
}

// 单独根据某个状态控制输入框禁用编辑
const $formInputRebackFlag = ($form: any, ctrlStatus: any[]) => {
  let receiveConfirm = $form.query('state').get('data').receiveConfirm
  let status = $form.values.handleStatus
  return !receiveConfirm || (receiveConfirm && ctrlStatus.includes(status))
}

const outsourceMaterialsDetailSchema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      receiveConfirm: false
    }
  },
  OsMaterialReq: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      'type': 'OsMaterialReq',
      actions: {
        getDetail: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            $form.query('state').get('data').receiveConfirm = $attrs.params.receiveConfirm
            let id = $attrs.params.materialReqId
            $form.values.materialReqId = id
            return !!id
          }`),
          method: 'read',
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            let materialReqId = $attrs.params.materialReqId || data.payload[0]
            data.action = 'getDetail'
            data.tree = true
            data.loading = true
            data.payload = {
              "filter": {
                  "materialReqId": {
                      eq: materialReqId
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
        submit: {
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
            ...BaseInfo
          },
          // 委外领料单明细
          orderDetail: {
            ...MaterialsDetails
          },
          // 相关附件
          relevantAttachment: {
            ...FileInfo
          }
        }
      }
    },
    // 按钮操作
    items: {
      type: 'void',
      properties: {
        buttonList: {
          type: 'void',
          'x-component': 'ButtonList',
          properties: {
            // 返回 取消
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
                      $bus.$emit('osMaterialReqListBuyer')
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
                ...buttonListItemVisibleByPermission('outsourceMaterials:create'),
                type: 'default',
                plain: 'plain',
                '@click': expression(`async (values) => {
                  $saveFormBill('saveOrUpdate','CREATE', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(['handleStatus'], `
                [undefined, '', 'CREATE'].includes($deps[0]) && !$form.readPretty
              `)
            },
            // 提交
            submit: {
              type: 'void',
              title: i18nExpression('common.submit'),
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceMaterials:create'),
                type: 'primary',
                '@click': expression(`async (values) => {
                  let handleStatus = await $form.query("state").get("data").receiveConfirm ? 'SUBMIT' : 'VALID'
                  $saveFormBill('submit', handleStatus, $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['handleStatus'], `
                  [undefined, '', 'CREATE'].includes($deps[0]) && !$form.readPretty
                `)
            },
            // 回复
            replay: {
              type: 'void',
              title: i18nExpression('common.reply'),
              'x-hidden': expression(`() => {
                let receiveConfirm = $form.query('state').get('data').receiveConfirm
                let handleStatus =  $form.values.handleStatus
                return !receiveConfirm || (receiveConfirm && !['','CREATE','SUBMIT'].includes(handleStatus))
              }`),
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceMaterials:reply'),
                type: 'primary',
                '@click': expression(`async (values) => {
                  $saveFormBill('submit','SUBMIT', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['handleStatus'],
                '$deps[0] === "REFUSE" && $form.query("state").get("data").receiveConfirm && !$form.readPretty'
              )
            }
          }
        }
      }
    }
  },

  // 委外领料单明细选择弹窗
  ...MaterialsDailog
})

const scope = {
  emitTabRemove,
  app,
  $attrs,
  $markRaw: markRaw,
  $saveFormBill,
  $submitData,
  $setRepeatData,
  $setMaterialData,
  $formEditFlag,
  $receiveConfirmCtrl,
  $formInputEditFlag,
  $formInputRebackFlag
}
const components = {
  BaseInfo,
  MaterialsDetails,
  FileInfo,
  MaterialsDailog
}
</script>

<template>
  <RenderEngine
    schemaKey="outsourceMaterialsDetail"
    :pageAttrs="$attrs"
    :schema="outsourceMaterialsDetailSchema"
    :scope="scope"
    :components="components"
  />
</template>
