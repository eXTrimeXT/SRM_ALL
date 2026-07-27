<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, toJS, generateCharReactionExpression } from '@meicloud/render-engine'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { useAttrs, computed, ref } from 'vue'
import orderForm from './components/orderForm'
import attrForm from './components/attrForm'
import file from './components/file'

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      hasSubmit: false
    }
  },
  PurCatalogChangeVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        save: {
          cascadeDeletion: true,
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            return data
          }`)
        },
        read: {
          immediate: true,
          ready: expression(`() => {
            if($attrs.params?.flag == 'add') {
              let row = $attrs.params?.row
              app.$http({
                url: '/api-sup/purchaseCataLog/get',
                method: 'GET',
                params: { catalogId: row.catalogId },
                loading: true
              }).then(res => {
                let value = res.data || {}
                value.changeStatus = 'DRAFT'
                value.purCatalogAttChangeList = value.purCatalogAttList
                delete value.purCatalogAttList
                let attrs = ['createdBy', 'createdByIp', 'createdFullName', 'createdId', 'createdUserName', 'creationDate','updatedReason']
                for (let key of attrs) {
                  value[key] = null
                }
                initButtonConfig($form)
                updateButtonConfig($form)
                $form.setValues({
                  ...value
                })
              })
              return false
            }
            let id = $attrs.params?.row?.changeId
            $values.changeId = id
            initButtonConfig($form)
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            data.payload = [$values.changeId]
            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.readPretty = $readOnly
            const value = res.data[0]
            workflowStatus.value = value.changeStatus
            
            updateButtonConfig($form)
            $form.setValues({
              ...value
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
          params: {
            activeWorkflowTab: true
          },
          'business-id': expression('$form.values.changeId || null'),
          'business-type': 'PURLOGCHANGE',
          'button-custom': expression(`{}`),
          '@click-handler': expression(`(type) => {
            $submit(type,$form,$queryEngine,$confirm,$message,$bus)
          }`),
          '@submit-direct': expression(`(type) => {

            $submit(type,$form,$queryEngine,$confirm,$message,$bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $submit(type,$form,$queryEngine,$confirm,$message,$bus)
          }`),
          '@close-tab': expression(`() => {
            $cancel($readOnly,$attrs,$form)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            "x-read-pretty": `{{ ['view','manage'].includes($attrs.params.flag) }}`,
            'x-component-props': {
              defaultOpenPanelCount: 1
            },
            properties: {
              // 物料基础信息
              orderInfo: {
                type: 'void',
                'x-query-engine-skip': true,
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('purchase.BasicMaterialInformation')}}"
                },
                properties: {
                  orderForm: {
                    type: 'void',
                    'x-decorator': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    'x-component': 'FormGrid',
                    "x-read-pretty": true,
                    'x-component-props': {
                      maxColumns: 4,
                      columnGap: 32,
                      rowGap: 0
                    },
                    properties: generateXindexInOrder({ ...orderForm })
                  }
                }
              },
              // 物料属性信息
              attrInfo: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('purchase.MaterialAttributeInformation')}}"
                },
                properties: {
                  attrForm: {
                    type: 'void',
                    'x-decorator': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    'x-component': 'FormGrid',
                    'x-component-props': {
                      maxColumns: 4,
                      columnGap: 32,
                      rowGap: 0
                    },
                    properties: generateXindexInOrder({ ...attrForm })
                  }
                }
              },
              UpdateReasonDescription: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('purchase.UpdateReasonDescription')}}"
                },
                properties: {
                  updatedReason: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 0,
                      style: {
                        'grid-column': 'span 4'
                      }
                    },
                    'x-component-props': {
                      type: 'textarea',
                      autosize: expression(`{ minRows: 4, maxRows: 4 }`),
                      maxlength: '200',
                      showWordLimit: true
                    },
                    ...requiredValidatorSegment
                  }
                }
              },
              file: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('accountMod.relevantAttachment')}}"
                },
                properties: {
                  ...file
                }
              }
            }
          }
        }
      }

    }
  }
})

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $readOnlyFlag = ['view'].includes(attrs.params.flag)

const viewUpdateButton = computed(() => (!$readOnlyFlag && !['CONFIRMING'].includes(workflowStatus.value)))

const $cancel = ($readOnly:any, $attrs:any, $form: any) => {
  if ($readOnly || $form.query('state').get('data').hasSubmit) {
    emitTabRemove($attrs.tabName)
    return
  }
  app.$confirm('此次修改并未保存是否取消', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    emitTabRemove($attrs.tabName)
  }).catch(() => {
  })
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = false
    componentInstance.buttonConfigInfo.save.name = app.$t('common.staging')
    componentInstance.buttonConfigInfo.submit.name = app.$t('common.submit')
    componentInstance.buttonConfigInfo.cancel.view = !$readOnlyFlag
    componentInstance.buttonConfigInfo.close.view = $readOnlyFlag
    componentInstance.setWorkflowBusinessId($form.values.changeId)
    componentInstance.setWorkflowTabDisabled(
      [null, undefined, 'DRAFT', 'WITHDRAW', 'REJECTED'].includes(attrs.params.row.changeStatus)
    )
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = attrs.params.flag === 'submit'
  }, 50)
}

const cannotLessCurrentTimeOptions = {
  disabledDate: time => {
    const nowDate = new Date()
    nowDate.setHours(0)
    nowDate.setMinutes(0)
    nowDate.setSeconds(0)
    nowDate.setMilliseconds(0)
    return time.getTime() < nowDate.getTime()
  }
}

const { emitTabRemove, t, app, vendor, buyer } = usePageHelper()

const $submit = async (type:string, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  const form = toJS($form.values)

  const { purCatalogAttList, ...rest } = form
  if (type === 'SUBMIT') {
    await $form.validate()
  }
  if (type === 'SAVE') {
    return $queryEngine.request.baseRequest(
      {
        type: 'PurCatalogChangeVendor',
        loading: true,
        query: {
          '*': {}
        },
        payload: [
          {
            ...form
            // catalogStatus: 'DRAFT'
          }
        ],
        action: 'save'
      }
    ).then(res => {
      $message.success(t('common.successSave'))
      $form.values.changeId = res?.data[0].changeId
      $queryEngine.request['read']()
    })
  } else { // submit
    return $queryEngine.request.baseRequest(
      {
        type: 'PurCatalogChangeVendor',
        loading: true,
        query: {
          '*': {}
        },
        payload: [
          {
            ...form
          }
        ],
        action: 'submitFirst'
      }
    ).then(res => {
      $message.success(t('common.successSave'))
      $form.query('state').get('data').hasSubmit = true
      emitTabRemove(attrs.params.tabName)
      $bus.$emit('PurCatalogChangeVendor')
    })
  }
}

const scope = {
  emitTabRemove,
  app,
  $submit,
  cannotLessCurrentTimeOptions,
  initButtonConfig,
  updateButtonConfig,
  $cancel,
  $readOnlyFlag,
  workflowStatus,
  viewUpdateButton
}

</script>
<template>
  <RenderEngine schemaKey="purchaseDirectoryDetail" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
