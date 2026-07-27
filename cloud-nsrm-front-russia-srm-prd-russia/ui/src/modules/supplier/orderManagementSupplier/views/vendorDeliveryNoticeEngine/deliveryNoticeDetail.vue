<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
// @ts-ignore
import DeliveryNoticeInfo from './components/collapseItem/deliveryNoticeInfo'
// @ts-ignore
import DeliveryNoticeDetail from './components/collapseItem/deliveryNoticeDetail'
// @ts-ignore
import FileInfo from './components/collapseItem/fileInfo'
// @ts-ignore
import purchaseOrderDetail from 'mods@/orderManagementSupplier/views/vendorPurchaseOrderEngine/vendorPurchaseOrderDetail'

import { useAttrs } from 'vue-demi'

const { emitTabAdd, emitTabRemove, t: $t } = usePageHelper()

const $attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryNoticeVendor')
  emitTabRemove($attrs.tabName)
}

// 拒绝
const $reject = ($form: any) => {
  $form.query('*.RejectReasonlDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.RejectReasonlDialog.form').take((field: any) => {
      field.reset()
    })
  })
}
// 接受
const $accept = async ($form: any, $queryEngine: any, $message: any, $confirm: any, $bus: any) => {
  const confirmResult = await $confirm(
    $t('orderMod.confirmNuticeNumAndDateTip'),
    $t('common.tips'),
    {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('common.cancel'),
      type: 'warning'
    })
  if (confirmResult !== 'confirm') return

  $queryEngine.request.baseRequest({
    'type': 'DeliveryNoticeVendor',
    "lang": 'zh-cn',
    'payload': [{
      deliveryNoticeId: $form.values.deliveryNoticeId
    }],
    'action': 'accept'
  }).then((res: any) => {
    $message.success($t('common.success'))
    $closePageAndRefreshListPageData($bus)
  })
}

const $readOrder = (row: any) => {
  emitTabAdd({
    component: purchaseOrderDetail,
    params: {
      flag: 'view',
      row,
      tabName: 'purchaseOrderDetail' + row.orderNumber
    },
    title: row.orderNumber,
    name: 'purchaseOrderDetail' + row.orderNumber
  })
}

// @ts-ignore
const scope = {
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $reject,
  $accept,
  $readOrder
}

// @ts-ignore
const components = {
  DeliveryNoticeInfo,
  DeliveryNoticeDetail,
  FileInfo,
  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  // 基本信息
  DeliveryNoticeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            $form.readPretty = true

            return $attrs.params.row.deliveryNoticeId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs.params.row.deliveryNoticeId || $form.values.deliveryNoticeId || '']

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.setValues({
              ...res.data[0],
              deliveryNoticeId: $attrs.params.row.deliveryNoticeId
            })

            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        reject: {
          type: 'void',
          'x-hidden': `{{$attrs.params.flag === 'view'}}`,
          'x-content': i18nExpression('common.refused'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $reject($form)
            }`)
          }
        },
        accept: {
          type: 'void',
          'x-hidden': `{{$attrs.params.flag === 'view'}}`,
          'x-content': i18nExpression('orderMod.accept'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $accept($form, $queryEngine, $message, $confirm,$bus)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          // 送货单据
          deliveryNoticeInfo: {
            ...DeliveryNoticeInfo
          },
          // 送货单明细
          deliveryNoticeDetail: {
            ...DeliveryNoticeDetail
          },
          // 附件
          fileInfo: {
            ...FileInfo
          }
        })
      },
      // 拒绝原因
      RejectReasonlDialog: {
        type: 'void',
        title: `{{$t('contractMod.refusedReason')}}`,
        'x-component': 'RDialog',
        'x-component-props': {
          class: 'dialogMain',
          size: 'middle',
          beforeClose: expression(`(done, type) => {
            if (type === 'ok') {
                const refuseReason = $form.values.form.refuseReason
                 if (!refuseReason) {
                  return $message.error($t('contractMod.refusedReason') + $t('contract_mod.required'))
                 }
                $queryEngine.request.baseRequest({
                  'type': 'DeliveryNoticeVendor',
                  "lang":  'zh-cn',
                  'payload':  [{
                    refuseReason,
                    deliveryNoticeId:  $form.values.deliveryNoticeId
                  }],
                  'action': 'refuse'
                }).then(res => {
                  $message.success($t('common.success'))
                  done()
                  $closePageAndRefreshListPageData($bus)
                })
            } else {
              done()
            }

          }`)
        },
        properties: {
          form: {
            type: 'object',
            ...formGridSegment,
            'x-read-pretty': false,
            properties: {
              refuseReason: {
                type: 'string',
                title: `{{$t('contractMod.refusedReason')}}`, // 拒绝原因
                'x-decorator': 'FormItem',
                'x-component-props': {
                  type: 'textarea',
                  rows: 3,
                  showWordLimit: true,
                  maxlength: '250'
                },
                ...requiredValidatorSegment
              }
            }
          }
        }
      }

    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="VendorDeliveryNoticeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
