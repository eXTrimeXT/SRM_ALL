<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  i18nExpression,
  connect,
  mapProps,
  toJS
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

import { collapseMain } from './components/collapse'

import { financeInfoChangeApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

import changeTitle from  './components/changeTitle'

import { FileDynamic } from '@/library/components/srm-components/file-dynamic'

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabledFlag = computed(() => {
  return true
})

const customUpdateButton = computed(() => (!$disabledFlag.value && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = ($form:any) => {
  const flag = attrs.params.flag
  console.log(flag)
  if (['approved', 'view'].includes(flag)) {
    $form.readPretty = true
    return false
  } else {
    return true
  }
}
const disabledUpdateButton = () => {
  const flag = attrs.params.flag == 'view'
  return !flag
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = true

    componentInstance.setWorkflowBusinessId(attrs.params?.row?.changeHeaderId)
    componentInstance.setWorkflowTabDisabled(['DRAFT'].includes(attrs.params?.row?.approveStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
    componentInstance.buttonConfigInfo.save.view = disabledUpdateButton()
    componentInstance.buttonConfigInfo.submit.view = disabledUpdateButton()
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
  }, 50)
}

// 供应商去重
const showCompany = (arr) => {
  let newArr = arr
  let bolArr = []
  if (arr.length > 0) {
    newArr.forEach(e => {
      let bol = 1
      bolArr.forEach(u => {
        if (e.companyId == u.companyId) {
          bol = 0
        }
      })
      if (bol == 1) {
        bolArr.push(e)
      }
    })
  }
  return bolArr
}

const schema = defineSchemas({
  FinanceInfoChangeHeader: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container siteReviewPlanConfirm',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          tree: true,
          ready: expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params?.row?.changeHeaderId
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              "*":{},
              "fileUploads": {'*': {}},
              "financeInfoChangeList": {'*': {}},
            }
            let req = {
              "filter": {
                  "changeHeaderId": {
                      eq: $attrs.params.row.changeHeaderId
                  }
              }
            }
            data.payload = req
            return data
          }`),
          transformResponse: expression(`(res) => {
            const ress = JSON.parse(res)
            if (ress.code != '0') {
              app.$message.warning(ress.message)
              return false
            }
            const data = ress.data.records[0]
            const financeInfoChangeList = data.financeInfoChangeList
            const fileUploads = data.fileUploads
            setTimeout(() => {
              $form.query('.form').take().value = data
              const before = financeInfoChangeList.filter(i => i.changeFlag == "BEFORE")
              $form.query('.changeBeforeList').take().value = before
              const after = financeInfoChangeList.filter(i => i.changeFlag == "AFTER")
              $form.query('.changeAfterList').take().value = after

            })

            return ress
          }`),
          onSuccess: expression(`(res) => {
            $form.query('fileUploads').take(field => {
              field.value = res.records[0].fileUploads
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params?.row?.changeHeaderId || null'),
          'business-type': 'FINANCECHANGE',
          '@click-handler': expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@close-tab': expression(`() => {
            $back($bus)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            console.log('update-integration-mode', integrationMode)
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            properties: {
              layout: {
                type: 'void',
                'x-component': 'FormContainer',
                properties: {
                  collapse: {
                    ...collapseMain
                  }
                }
              }
            }
          }
        }
      }
    }
  }
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('financialChange')
}

const $submits = (type, $form, $queryEngine, $message, $t, $bus) => {
  let values = $form.values.form
  const changeBeforeList = $form.query('.changeBeforeList').get('value')
  const changeAfterList = $form.query('.changeAfterList').get('value')
  values.financeInfoChangeList = [...changeBeforeList, ...changeAfterList]
  values.financeInfoChangeList = values.financeInfoChangeList.concat($form.query('.changeAfterListDele').take().value)
  values.fileUploads = $form.query('.fileUploads').get('value')
  const approveStatus = attrs.params.row?.approveStatus || null
  if (type == 'SAVE') { // 暂存的时候
    if ([null, 'DRAFT'].includes(approveStatus)) { // 新增或者编辑的时候
      values.approveStatus = 'DRAFT'
      $queryEngine.request.save(values, { query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('financialChange')
        emitTabRemove(attrs.tabName)
      })
    } else { // 供应商确认后的暂存
      $queryEngine.request.save(values, { query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('financialChange')
        emitTabRemove(attrs.tabName)
      })
    }
  } else { // 提交
     if ([null, 'DRAFT'].includes(approveStatus)) {
       values.approveStatus = 'DRAFT'
       // 供应商确认后的提交工作流
       $queryEngine.request.save(values, { query: { '*':{} } }).then((res) => {
         const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
         componentInstance.setWorkflowBusinessId(res.data[0]?.changeHeaderId || null)
         componentInstance.setWorkflowTabDisabled(true)
         componentInstance.setWorkflowBusinessVariables({})
         componentInstance.handlerAfter(type.toUpperCase(), () => {
           $bus.$emit('financialChange')
         })
         setTimeout(() => {
           $form.readPretty = true
           componentInstance.buttonConfigInfo.save.view = false
           componentInstance.buttonConfigInfo.submit.view = false
         }, 100)
       })
     } else {
       // 供应商确认后的提交工作流
       $queryEngine.request.save(values, { query: { '*':{} } }).then((res) => {
         const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
         componentInstance.setWorkflowBusinessId(res.data[0]?.changeHeaderId || null)
         componentInstance.setWorkflowTabDisabled(true)
         componentInstance.setWorkflowBusinessVariables({})
         componentInstance.handlerAfter(type.toUpperCase(), () => {
           $bus.$emit('financialChange')
         })
         setTimeout(() => {
           $form.readPretty = true
           componentInstance.buttonConfigInfo.save.view = false
           componentInstance.buttonConfigInfo.submit.view = false
         }, 100)
       })
     }
  }
}


const scope = {
  app,
  t,
  $attrs: attrs,
  $disabledFlag,
  emitTabRemove,
  initButtonConfig,
  $back,
  supCommonApi,
  $submits,
  financeInfoChangeApi,
  showCompany
}

const components = {
  changeTitle,
  FileDynamic
}
</script>

<template>
  <RenderEngine schemaKey="financialInforChangesDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>

<style>
.siteReviewPlanConfirm .render-form-container__fixed-footer{
  padding-top:0px
}
</style>
