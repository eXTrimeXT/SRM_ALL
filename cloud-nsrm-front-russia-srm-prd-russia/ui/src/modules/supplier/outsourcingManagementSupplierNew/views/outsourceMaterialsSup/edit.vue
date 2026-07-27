<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, markRaw, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { useAttrs, computed, ref } from 'vue-demi'
import {
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'

import BaseInfo from './components/baseInfo'
import MaterialsDetails from './components/materialsDetails'
import FileInfo from './components/fileInfo'

const { emitTabRemove, t: $t, app } = usePageHelper()
let $attrs: any = useAttrs()

// saveOrUpdate    采购商保存 CREATE
// submit          采购方提交 / 供方驳回后重新提交 SUBMIT / VALID(关闭供方确认后直接生效)
// vendorAccept    供方接受  VALID
// vendorRefuse    供方驳回  REFUSE
const $saveFormBill = (type: string, status: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const values = $form.values
  if (type === 'saveOrUpdate') {
    $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
  } else {
    $form.validate().then(() => {
      // 供应商驳回
      if (type == 'vendorRefuse') {
        if (!values.vendorDiffDescription) {
          return $message.warning($t('outsource.fillVendorDiffDescription')) // 请维护供应商差异说明
        }
      }
      $submitData(type, status, values, $form, $queryEngine, $confirm, $message, $bus)
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
    $bus.$emit('osMaterialReqList')
  }).catch((err:any) => {
    console.log(err)
  })
}
// 单独根据某个状态控制输入框禁用编辑
const $formInputEditFlag = ($form: any, ctrlStatus: any[]) => {
  let status = $form.values.handleStatus
  return ctrlStatus.includes(status)
}
// 供方确认隐藏控制
const $receiveConfirmCtrl = ($form: any, $queryEngine: any) => {
  let receiveConfirm = $form.query('state').get('data').receiveConfirm
  let handleStatus = $form.values.handleStatus
  return !receiveConfirm || (receiveConfirm && !['', 'CREATE', 'SUBMIT', 'REFUSE', 'VALID'].includes(handleStatus))
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
                      $bus.$emit('osMaterialReqList')
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
            // 驳回 ( 供应商)
            reject: {
              type: 'void',
              title: '{{$t("common.toRefuse")}}',
              'x-component-props': {
                ...buttonListItemVisibleByPermission('outsourceMaterialsSup:reject'),
                '@click': expression(`(values) => {
                  // 驳回的时候差异说明必填
                  $saveFormBill('vendorRefuse','REFUSE', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['handleStatus'], `
                  $deps[0] === 'SUBMIT' && !$form.readPretty
                `)
            },
            // 接受 ( 供应商)
            accept: {
              type: 'void',
              title: '{{$t("common.accept")}}',
              'x-component-props': {
                type: 'primary',
                ...buttonListItemVisibleByPermission('outsourceMaterialsSup:accept'),
                '@click': expression(`async (values) => {
                  $saveFormBill('vendorAccept','VALID', $form, $queryEngine, $confirm, $message, $bus)
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(
                ['handleStatus'], `
                  $deps[0] === 'SUBMIT' && !$form.readPretty
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
  $formInputEditFlag,
  $receiveConfirmCtrl,
  $formInputRebackFlag
}
const components = {
  BaseInfo,
  MaterialsDetails,
  FileInfo
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
