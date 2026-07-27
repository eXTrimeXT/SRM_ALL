<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder
} from '@meicloud/render-engine'
import { useAttrs } from 'vue-demi'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  requiredValidatorSegment,
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'
const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()
let $attrs: any = useAttrs()
const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  $form.validate().then(res => {
    // 获取表单数据
    const { socialCreditCodeA, socialCreditCodeB } = $form.values
    if (socialCreditCodeA === socialCreditCodeB) {
      $message.warning($t('cusEntry.tipMessage.aCompanyAndbCompanySame'))
      return false
    }
    const associationId = $form.query('RelationSupBuyer').get('data').associationId || null
    $queryEngine.request.save({ ...$form.values, associationId }).then(res => {
      const {
        associationId
      } = res.data?.[0]
      $form.query('RelationSupBuyer').get('data').associationId = associationId
      if (type === 'SAVE') {
        $message.success($t('common.successSave'))
        $queryEngine.request.query()
      } else {
        $message.success($t('common.successSubmit'))
        emitTabRemove($attrs.params.tabName)
        $bus.$emit('relation')
      }
    })
  })
}
const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('RelationSupBuyer').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton && !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton && !$form.readPretty
    componentInstance.buttonConfigInfo.save.name = app.$t('common.staging')
    componentInstance.buttonConfigInfo.submit.name = app.$t('common.submit')
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = viewUpdateButton
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('RelationSupBuyer').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton && !$form.readPretty
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton && !$form.readPretty
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = viewUpdateButton
    componentInstance.setWorkflowTabDisabled($form.query('RelationSupBuyer').get('data').orderStatus === 'DRAFT')
  }, 50)
}
const schema = defineSchemas({
  RelationSupBuyer: {
    type: 'void',
    'x-component': 'el-container',
    'x-decorator': 'QueryEngine',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      viewUpdateButton: true,
      associationId: null
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        save: {
          action: 'saveOrUpdate',
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        },
        query: {
          action: 'getRelationSupById',
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            $form.readPretty = $attrs.params.flag === 'view'
            return $attrs.params?.row?.associationId || null
          }`),
          transformRequest: expression(`(data, headers) => {
            data.loading = true
            data.payload = [
              {
                "associationId": $attrs.params?.row?.associationId || $form.query('RelationSupBuyer').get('data').associationId
              }
            ]
            return data
          }`),
          onSuccess: expression(`res => {
            let detailData = res.data[0]
            $form.setValues({
              ...detailData
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
          'business-id': expression('$attrs.params.row?.associationId || null'),
          'business-type': 'relationSuppliers',
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
            properties: generateXindexInOrder({
              baseInfo: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('cusEntry.vendorMod.relationSuppliers')
                },
                'x-query-engine-skip': true,
                properties: {
                  layoutOne: {
                    type: 'void',
                    'x-component': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    ...formGridSegment,
                    properties: {
                      vendorCodeA: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('cusEntry.vendorMod.aCompanyCode'),
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          readPretty: '{{$form.readPretty}}',
                          showKey: 'companyCode',
                          propKey: 'companyCode',
                          'name': 'scc_sup_company_info2',
                          '@close-quicksearch': expression(`value => {
                            $form.values.vendorCodeA = value?.companyCode || ''
                            $form.values.vendorNameA = value?.companyName || ''
                            $form.values.vendorIdA = value?.companyId || ''
                            $form.values.socialCreditCodeA = value?.lcCode || ''
                          }`)
                        }
                      },
                      vendorIdA: {
                        type: 'string',
                        'x-hidden': true
                      },
                      socialCreditCodeA: {
                        type: 'string',
                        title: i18nExpression('cusEntry.vendorMod.socialCreditCodeA'),
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment
                      },
                      vendorNameA: {
                        type: 'string',
                        title: i18nExpression('cusEntry.vendorMod.aCompanyName'),
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment
                      },
                      createdUserName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creator'), // '创建人'
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      vendorCodeB: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('cusEntry.vendorMod.bCompanyCode'),
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          readPretty: '{{$form.readPretty}}',
                          showKey: 'companyCode',
                          propKey: 'companyCode',
                          'name': 'scc_sup_company_info2',
                          '@close-quicksearch': expression(`value => {
                            $form.values.vendorCodeB = value?.companyCode || ''
                            $form.values.vendorNameB = value?.companyName || ''
                            $form.values.vendorIdB = value?.companyId || ''
                            $form.values.socialCreditCodeB = value?.lcCode || ''
                          }`)
                        }
                      },
                      vendorIdB: {
                        type: 'string',
                        'x-hidden': true
                      },
                      socialCreditCodeB: {
                        type: 'string',
                        title: i18nExpression('cusEntry.vendorMod.socialCreditCodeB'),
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment
                      },
                      vendorNameB: {
                        type: 'string',
                        title: i18nExpression('cusEntry.vendorMod.bCompanyName'),
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment
                      },
                      // associationType: {
                      //   type: 'string',
                      //   title: i18nExpression('cusEntry.vendorMod.type'),
                      //   'x-decorator': 'FormItem',
                      //   'x-component': 'DictSelect',
                      //   'x-component-props': {
                      //     code: 'RELATION_TYPE'
                      //   },
                      //   ...requiredValidatorSegment
                      // },
                      creationDate: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creationTime'), // '创建时间'
                        'x-component-props': {
                          disabled: true
                        }
                      }
                    }
                  },
                  layoutTwo: {
                    type: 'void',
                    'x-component': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    ...formGridSegment,
                    properties: {
                      associationRemark: {
                        type: 'string',
                        title: i18nExpression('cusEntry.vendorMod.relationRemark'),
                        'x-decorator': 'FormItem',
                        'x-component-props': {
                          type: 'textarea',
                          autosize: {
                            minRows: 2,
                            maxRows: 3
                          }
                        },
                        'x-decorator-props': {
                          gridSpan: 4
                        }
                      }
                    }
                  }
                }
              }
            })
          }
        }
      }
    }
  }
})
const scope = {
  $attrs,
  $saveBill,
  emitTabRemove,
  initButtonConfig,
  updateButtonConfig
}
const components = {}
</script>

<template>
  <RenderEngine
    schemaKey="relationSuppliers"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
