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
import { formMain } from './components/form'
import { collapseMain } from './components/collapse'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabledFlag = computed(() => {
  return true
})

const customUpdateButton = computed(() => (!$disabledFlag.value && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = () => {
  const flag = attrs.params.flag
  const readOnly = attrs.params.readOnly
  if (flag == 'approve' || !readOnly) {
    return true
  } else {
    return false
  }
}
const disabledUpdateButton = () => {
  const readOnly = attrs.params.readOnly
  return !readOnly
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton()
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton()
    componentInstance.buttonConfigInfo.cancel.view = true
    componentInstance.buttonConfigInfo.close.view = false

    const approveStatus = attrs.params.row?.approveStatus || null
    if ([null, 'DRAFT'].includes(approveStatus)) {
      componentInstance.buttonConfigInfo.save.name = '暂存'
      componentInstance.buttonConfigInfo.submit.name = '提交'
    }
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

const schema = defineSchemas({
  SiteReviewPlanConfirm: {
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
          ready: expression(`() => {
            initButtonConfig($form)
            return $attrs.params && ($attrs.params.row.planConfirmId || $attrs.params.row.planConfirmCode)
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              "*":{},
              "siteReviewPlanConfirmAddress": {'*': {}},
              "siteReviewPlanConfirmPersons": {'*': {}},
            }
            let req = {
              "filter": {
                  "planConfirmId": {
                      eq: $attrs.params.row.planConfirmId
                  }
              }
            }
            if (!$attrs.params.row.planConfirmId) {
              req = {
                "filter": {
                  "planConfirmCode": {
                      eq: $attrs.params.row.planConfirmCode
                  }
                }
              }
            }
            data.payload = req
            return data
          }`),
          transformResponse: expression(`(res) => {
            const ress = JSON.parse(res)
            const { readOnly } = $attrs.params

            const row = $attrs.params.row
            console.log(row, 'row')
            if (row) {
              $form.query('.vendorName').take().setValue(row.vendorName)
              $form.query('.orgName').take().setValue(row.orgName)
              $form.query('.planType').take().setValue(row.planType)
            }

            let datas = ress.data.ref.SiteReviewPlanConfirm[ress.data.records[0]]
            // 设置文本只读
            $form.readPretty = readOnly || ['VENDOR_CONFIRMED'].includes(datas.approveStatus)
            const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
            const tabDisabled = !['ABANDONED', 'REJECT', 'PASS', 'VENDOR_CONFIRMED'].includes(datas.approveStatus)
            componentInstance.setWorkflowTabDisabled(tabDisabled)
            let tableAddress = []
            let tableStaff = []
            const addressList = datas.siteReviewPlanConfirmAddress
            const personsList = datas.siteReviewPlanConfirmPersons
            addressList.forEach((e) => {
              tableAddress.push(ress.data.ref.SiteReviewPlanConfirmAddress[e])
            })
            personsList.forEach((e) => {
              tableStaff.push(ress.data.ref.SiteReviewPlanConfirmPerson[e])
            })
            $form.setValues(datas)
            $form.query('.tableAddress').take().setValue(tableAddress)
            $form.query('.tableStaff').take().setValue(tableStaff)
            return ress
          }`)
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.planConfirmId || null'),
          'business-type': 'PLANCONFIRM',
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
              form: {
                ...formMain
              },
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
  $bus.$emit('ModelHead')
}

const $submits = (type, $form, $queryEngine, $message, $t, $bus) => {
  let values = $form.values
  const tableAddress = $form.query('.tableAddress').get('value')
  const tableStaff = $form.query('.tableStaff').get('value')
  values.siteReviewPlanConfirmAddress = tableAddress
  values.siteReviewPlanConfirmPersons = tableStaff
  const approveStatus = attrs.params.row?.approveStatus || null
  if (type == 'SAVE') { // 暂存的时候
    if ([null, 'DRAFT'].includes(approveStatus)) { // 新增或者编辑的时候
      values.approveStatus = 'DRAFT'
      $queryEngine.request.save(values, { query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('SiteReviewPlanConfirm')
        emitTabRemove(attrs.tabName)
      })
    } else { // 供应商确认后的暂存
      $queryEngine.request.save(values, { query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('SiteReviewPlanConfirm')
        emitTabRemove(attrs.tabName)
      })
    }
  } else { // 提交
    if ([null, 'DRAFT', 'RELEASED', 'VENDOR_REJECT'].includes(approveStatus)) { // 新增或者编辑的时候
      values.approveStatus = 'RELEASED'
      $queryEngine.request.save(values, { query: { '*':{} } }).then(() => {
        $message.success($t('common.successSubmit'))
        $bus.$emit('SiteReviewPlanConfirm')
        emitTabRemove(attrs.tabName)
      })
    } else { // 供应商确认后的提交工作流
      $queryEngine.request.save(values, { query: { '*':{} } }).then((res) => {
        console.log('res', res)
        const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
        componentInstance.setWorkflowBusinessId(res.data[0]?.planConfirmId || null)
        componentInstance.setWorkflowTabDisabled(true)
        componentInstance.setWorkflowBusinessVariables({})
        componentInstance.handlerAfter(type.toUpperCase(), () => {
          $bus.$emit('SiteReviewPlanConfirm')
        })
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
  $submits
}

const components = {

}
</script>

<template>
  <RenderEngine schemaKey="siteReviewPlanConfirmDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>

<style>
.siteReviewPlanConfirm .render-form-container__fixed-footer{
  padding-top:0px
}
</style>
