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
      // 采购商驳回校验驳回数据
      if (type == 'buyerReject') {
        if (!values.rejectReason) {
          return $message.warning($t('outsource.fillRejectReason')) // 请维护采购商驳回原因
        }
      }
      $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
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
    $bus.$emit('osgMaterialReturnListBuyer')
  }).catch((err: any) => {
    console.log(err)
  })
}

// 单据可编辑控制
const $formEditFlag = ($form: any, $queryEngine: any) => {
  let status = $form.values.handleStatus
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
                      $bus.$emit('osgMaterialReturnListBuyer')
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
            // 驳回 ( 采购商)
            refuse: {
              type: 'void',
              title: '{{$t("common.toRefuse")}}',
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceReturnMaterials:refuse'),
                '@click': expression(`async (values) => {
                  $saveFormBill('buyerReject','BUYER_REJECT', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['status'], `
                  $deps[0] === 'WAITING_BUYER_CONFIRM' && !$form.readPretty
              `)
            },
            // 接受 ( 采购商)
            accept: {
              type: 'void',
              title: '{{$t("common.accept")}}',
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceReturnMaterials:accept'),
                type: 'primary',
                '@click': expression(`async (values) => {
                  $saveFormBill('buyerAccept', 'VALID', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['status'], `
                  $deps[0] === 'WAITING_BUYER_CONFIRM' && !$form.readPretty
              `)
            }
          }
        }
      }
    }
  }
})

const scope = {
  emitTabRemove,
  app,
  $attrs,
  $markRaw: markRaw,
  $saveFormBill,
  $submitData,
  $formEditFlag
}
const components = {
}
</script>

<template>
  <RenderEngine
    schemaKey="outsourceReturnMaterialsDetail"
    :pageAttrs="$attrs"
    :schema="questionDetailSchema"
    :scope="scope"
    :components="components"
  />
</template>
