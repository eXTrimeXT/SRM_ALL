<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
// @ts-ignore
import { parseTime, deepClone } from '@/utils'
import { useAttrs } from 'vue-demi'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import Detail from './components/collapseItem/detail'

const { emitTabRemove, t: $t, app } = usePageHelper()

let $attrs: any = useAttrs()

const $setButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.code = 'outsourceMaterialChange:save'
    componentInstance.buttonConfigInfo.submit.code = 'outsourceMaterialChange:submit'
    componentInstance.buttonConfigInfo.save.view = !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = !$form.readPretty
    componentInstance.buttonConfigInfo.cancel.view = !$form.readPretty
    componentInstance.buttonConfigInfo.close.view = $form.readPretty
    componentInstance.setWorkflowBusinessId($form.values.changeId || null)
    componentInstance.setWorkflowTabDisabled($attrs.params.flag !== 'approvalOnly')
  }, 50)
}

const $init = ($form: any) => {
  // 设置审批流按钮
  $setButtonConfig($form)

  if ($attrs.params.flag === 'add') {
    $attrs.params.row.status = 'DRAFT'
    $attrs.params.row.detailList.forEach((item: any) => {
      item.beforeBomQuantity = item.bomQuantity
      item.afterBomQuantity = item.bomQuantity
      item.beforeRawQuantity = item.orderQuantity
      item.changeType = 'NOT_CHANGE'
    })
    $form.setValues({
      ...$attrs.params.row
    })
    return false
  }
  $form.values.changeId = $attrs.params.row.changeId
  return true
}

// 保存
const $saveBill = (type: string, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  let values: any = deepClone($form.values)
  values.detailList.forEach((item:any, index:any) => {
    item.rowNum = index + 1
  })
  let actionName = ''

  if (type === 'SAVE') {
    actionName = 'saveOrUpdate'
    $submitData(actionName, values, $form, $queryEngine, $message, $bus)
  } else if (type === 'SUBMIT') {
    actionName = 'submit'
    $form.validate().then(() => {
      $submitData(actionName, values, $form, $queryEngine, $message, $bus)
    }).catch((err: any) => {
      console.log(err, 'err')
    })
  }
}

const $submitData = (actionName: any, values: any, $form: any, $queryEngine: any, $message: any, $bus: any) => {
  $queryEngine.request.baseRequest({
    'type': 'OsMaterialChange',
    'lang': 'zh-cn',
    'payload': [values],
    'action': actionName,
    'loading': true
  }).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      $form.values.changeId = res.originalData?.records[0] || ''
      $queryEngine.request.read()
      $bus.$emit('OsMaterialChangeBus')
      if (actionName === 'submit') {
        $cancel($form, $bus)
      }
    }
  })
}

const $cancel = ($form: any, $bus: any) => {
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  componentInstance.setWorkflowBusinessId($form.values.changeId || null)
  componentInstance.setWorkflowTabDisabled(false)
  componentInstance.handlerAfter('SUBMIT', () => {
    $closePageAndRefreshListPageData($bus)
  })
  setTimeout(() => {
    if (!['None', 'Push'].includes(componentInstance?.workflowParamsInfo?.integrationMode)) {
      $attrs.params.flag = 'approvalOnly'
      $form.readPretty = true
      componentInstance.buttonConfigInfo.save.view = false
      componentInstance.buttonConfigInfo.submit.view = false
    }
  }, 100)
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  emitTabRemove($attrs.tabName)
  $bus.$emit('OsMaterialChangeBus')
}

const $setMaterialData = ($form: any, data: any, $message: any) => {
  const ids = $form.values.detailList.map((item: any) => item.baseMaterialId)
  let isRepeat = false

  data.forEach((item: any) => {
    $form.query('detailList').take((field: any) => {
      if (!ids.includes(item.materialId)) {
        field.value.push({
          changeType: 'ADD',
          baseMaterialCode: item.materialCode,
          baseMaterialName: item.materialName,
          baseMaterialId: item.materialId,
          baseMaterialUnit: item.unitName,
          beforeBomQuantity: 0
        })
      } else {
        isRepeat = true
      }
    })
  })

  if (isRepeat) {
    // 新增物料存在重复数据，已去除
    $message.warning($t('outsourceMaterialHead.prompt2'))
  }
}

const scope = {
  $attrs,
  parseTime,
  $t,
  app,
  $init,
  $setButtonConfig,
  emitTabRemove,
  $submitData,
  $saveBill,
  $closePageAndRefreshListPageData,
  $setMaterialData

}
const components = {
  FormCollapse,
  BaseInfo,
  Detail
}

const schema = defineSchemas({
  OsMaterialChange: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          action: 'getDetail',
          ready: expression(`() => {
            return $init($form)
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = {filter: {changeId: $attrs?.params?.row?.changeId || $form.values.changeId || '' }}

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            const detailData = res.data[0]

            // 单纯文本只读状态
            $form.readPretty = ['view', 'approvalOnly'].includes($attrs.params.flag)
            $setButtonConfig($form)
          
            $form.setValues({
              ...detailData
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
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: true
          },
          'business-id': expression('$form.values.changeId || null'),
          'business-type': 'osMaterialChange',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine,$message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
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
              // 单据基本信息
              baseInfo: {
                ...BaseInfo
              },
              // 订单明细
              detail: {
                ...Detail
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
    schemaKey="OutsourceMaterialListChangeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
