<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  toJS
} from '@meicloud/render-engine'
import { useAttrs } from 'vue-demi'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { baseInfo } from './components/baseInfo'
import { introduceScene } from './components/introduceScene'
import { relateOrders } from './components/relateOrders'
import { orgCategorys } from './components/orgCategorys'
import { files } from './components/file'
const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()
let $attrs: any = useAttrs()
const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  if (type == 'WITHDRAW') {
    emitTabRemove($attrs.tabName)
    return
  }
  $form.validate().then(() => {
    const {
      filesList,
      introduceSceneList,
      orgCategorysList,
      relateOrdersList,
      ...form
    } = $form.values
    const params = {
      ...form,
      effectFormImportScenes: introduceSceneList,
      effectFormOrgCategorys: orgCategorysList,
      effectFormRelationForms: relateOrdersList,
      effectFormReqHeads: [],
      fileRecordIdes: filesList
    }
    $queryEngine.request.save(params, { loading: true }).then(res => {
      if (res && res.data && res.data[0]) {
        if (type === 'SAVE') {
          $message.success($t('common.successSave'))
          $form.values.effectFormId = res.data[0].effectFormId
        } else {
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId(res.data[0].effectFormId)
          componentInstance.setWorkflowTabDisabled(false)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            $message.success($t('common.successSubmit'))
            emitTabRemove($attrs.params.tabName)
            $bus.$emit('RefreshSuppliersEffectiveList')
          })
        }
      }
    })
  })
}
const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('effectForm').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.save.name = app.$t('common.staging')
    componentInstance.buttonConfigInfo.submit.name = app.$t('common.submit')
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = viewUpdateButton
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const {
      isReadOnly,
      viewUpdateButton
    } = $form.query('effectForm').get('data')
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton && !isReadOnly
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton && !isReadOnly
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = viewUpdateButton
    let approveStatus = $attrs.params.row?.approveStatus || null
    if (approveStatus == 'SUBMITTED' && componentInstance.workflowParamsInfo
      .integrationMode == 'Push') {
      componentInstance.buttonConfigInfo.withdraw.view = true
    }
    componentInstance.setWorkflowTabDisabled($form.query('effectForm').get('data').approveStatus === 'DRAFT')
  }, 50)
}
const schema = defineSchemas({
  effectForm: {
    type: 'void',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-decorator': 'QueryEngine',
    'x-data': {
      viewUpdateButton: true,
      isReadOnly: false
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            if ($attrs?.params?.row?.effectFormId) {
              // 单纯文本只读状态
              $form.readPretty = $attrs.params.flag === 'view'
              $form.query('effectForm').get('data').isReadOnly = $attrs.params.flag === 'view'
              return true
            }
            return false
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.effectFormId || $form.values.effectFormId || '']
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            updateButtonConfig($form)
            $form.setValues({
              ...res.data[0]
            })
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
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
          'business-id': expression('$attrs.params.row?.effectFormId || null'),
          'business-type': 'EffectForm',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            properties: {
              // 基础信息
              baseInfo: {
                ...baseInfo
              },
              // 引入场景
              ...introduceScene,
              // 关联单据
              ...relateOrders,
              // 组织与品类
              ...orgCategorys,
              // 附件
              ...files
            }
          }
        }
      }
    }
  }
})

/* 获取资质审查单明细 */
const $getDetailByReviewForm = ($queryEngine: any, $form: any) => {
  const payload = {
    action: 'getDetailByReviewForm',
    loading: true,
    type: 'effectForm',
    query: {
      '*': {}
    },
    actionConfig: {
      autoFormatResult: false
    },
    payload: [$form.values.reviewFormId]
  }
  $queryEngine.request.baseRequest(payload).then((res: any) => {
    if (res && res.records && res.records[0]) {
      const {
        effectFormImportScenes,
        effectFormRelationForms,
        effectFormOrgCategorys
      } = res.records[0]
      $form.query('introduceSceneList').take().value = effectFormImportScenes
      $form.query('relateOrdersList').take().value = effectFormRelationForms
      $form.query('orgCategorysList').take().value = effectFormOrgCategorys
    }
  })
}
const scope = {
  $attrs,
  $t,
  $saveBill,
  emitTabRemove,
  initButtonConfig,
  updateButtonConfig,
  $getDetailByReviewForm
}
const components = {}
</script>

<template>
  <RenderEngine
    schemaKey="suppliersEffective"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
