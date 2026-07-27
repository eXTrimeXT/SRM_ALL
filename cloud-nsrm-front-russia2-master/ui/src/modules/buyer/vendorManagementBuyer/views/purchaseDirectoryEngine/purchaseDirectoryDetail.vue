<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, toJS, generateCharReactionExpression } from '@meicloud/render-engine'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { useAttrs } from 'vue-demi'
import orderForm from './components/orderForm'
import attrForm from './components/attrForm'
import file from './components/file'

const schema = defineSchemas({
  PurchaseCatalog: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
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
            let id = $attrs.params?.row?.catalogId
            $values.catalogId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            data.payload = [$values.catalogId]
            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res)
            $form.readPretty = $readOnly
            const value = res.data[0]
            $form.setValues({
              ...value
            })
          }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        back: {
          type: 'void',
          'x-content': "{{$readOnly ? $t('common.close') : $t('common.cancel')}}",
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              app.$confirm(${i18nExpression('outsource.goBackConfirm')}, ${i18nExpression('components.approvalHead.tips.tip')}, {
                confirmButtonText: ${i18nExpression('common.confirm')},
                cancelButtonText: ${i18nExpression('components.common.cancel')},
                type: 'warning'
              }).then(() => {
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)
          }
        },
        save: {
          type: 'void',
          'x-content': "{{$t('common.staging')}}",
          'x-component': 'Button',
          'x-visible': expression(`!$readOnly`),
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              return $submit('save',$form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-content': "{{$t('common.submit')}}",
          'x-component': 'Button',
          'x-visible': expression(`!$readOnly`),
          'x-component-props': {
            '@click': expression(`() => {
              return $submit('submit',$form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
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
})

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

const { emitTabRemove, t, app } = usePageHelper()

const attrs:any = useAttrs()

const $submit = async (type:string, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  const form = toJS($form.values)
  const { purCatalogAttList, ...rest } = form
  if (type === 'submit') {
    await $form.validate()
    if (rest.startDate && rest.endDate){
      if (rest.startDate >= rest.endDate) {
        // '失效时间必须大于生效时间'
        $message.warning(i18nExpression('vendorMod.materialBaseInfo'))
        return
      }
      if (form.startDate) form.startDate = form.startDate.includes('00:00:00') ? form.startDate : `${form.startDate} 00:00:00`
      if (form.endDate) form.endDate = form.endDate.includes('00:00:00') ? form.endDate : `${form.endDate} 00:00:00`
    }
  }
  if (type === 'save') {
    return $queryEngine.request.baseRequest(
      {
        type: 'PurchaseCatalog',
        loading: true,
        query: {
          '*': {}
        },
        payload: [
          {
            ...form,
            catalogStatus: 'DRAFT'
          }
        ],
        action: 'save'
      }
    ).then(res => {
      $message.success(t('common.successSave'))
      $form.values.catalogId = res?.data[0].catalogId
      $queryEngine.request['read']()
    })
  } else { // submit
    return $queryEngine.request.baseRequest(
      {
        type: 'PurchaseCatalog',
        loading: true,
        query: {
          '*': {}
        },
        payload: [
          {
            ...form
          }
        ],
        action: 'submitCatalog'
      }
    ).then(res => {
      $message.success(t('common.successSave'))
      emitTabRemove(attrs.params.tabName)
      $bus.$emit('PurchaseCatalog')
    })
  }
}

const scope = {
  emitTabRemove,
  app,
  $submit,
  cannotLessCurrentTimeOptions
}

</script>
<template>
  <RenderEngine schemaKey="purchaseDirectoryDetail" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
