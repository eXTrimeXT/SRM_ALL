<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, toJS, generateCharReactionExpression } from '@meicloud/render-engine'
import { requiredValidatorSegment, editTableFormItemValid } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { useAttrs } from 'vue-demi'
import { useDebounceFn } from '@vueuse/core'

const schema = defineSchemas({
  TagRuleConfig: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        save: {
          cascadeDeletion: true,
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
          }`)
        },
        read: {
          immediate: true,
          ready: expression(`() => {
            let id = $attrs.params?.row?.tagRuleId
            $values.tagRuleId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            data.payload = [$values.tagRuleId]
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
              // '此次修改并未保存是否取消'
              app.$confirm(i18nExpression('outsource.goBackConfirm'), i18nExpression('components.approvalHead.tips.tip'), {
                confirmButtonText: i18nExpression('common.confirm'),
                cancelButtonText: i18nExpression('components.common.cancel'),
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
            type: 'default',
            '@click': expression(`() => {
              $submit('save',$form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-content': "{{$t('common.submit')}}",
          'x-component': 'Button',
          'x-visible': expression(`!$readOnly`),
          'x-component-props': {
            '@submit': expression(`() => {
              $submit('submit',$form,$queryEngine,$confirm,$message,$bus)
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
          // 条码规则基本信息
          orderInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('barcodeManageNew.baseInfo')}}"
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
                  maxColumns: 3,
                  columnGap: 32,
                  rowGap: 0
                },
                properties: generateXindexInOrder({
                  tagRuleId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  tagRuleCode: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.ruleCode')}}",
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  tagRuleName: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.ruleName')}}",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment
                  },
                  tagRuleType: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.ruleType')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'TAG_RULE_TYPE'
                    },
                    ...requiredValidatorSegment
                  },
                  tagRuleDesc: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.ruleDes')}}",
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 4
                    },
                    'x-component-props': {
                      type: 'textarea'
                    }
                  }
                })
              }
            }
          },
          // 条码段信息
          tagLine: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('barcodeManageNew.tagLine')}}"
            },
            properties: {
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-component-props': {
                  style: 'margin-bottom:5px;display:block;'
                },
                'x-reactions': expression(`field => {
                  field.visible = !$readOnly
                }`),
                properties: {
                  add: {
                    type: 'void',
                    title: "{{$t('common.add')}}",
                    'x-component': 'RButton',
                    'x-component-props': {
                      type: 'primary',
                      '@click': expression(`() => {
                        $form.query('tagRuleConfigLineList').take(field => {
                          field.value.push({
                            columnType:null,
                            columnValue:null
                          })
                        })
                      }`)
                    }
                  }
                }
              },
              tagRuleConfigLineList: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  preColumns: 'seq',
                  editMode: true,
                  pagination: false,
                  maxHeight: '58vh',
                  sortable: false,
                  primaryKey: 'tagRuleLineId',
                  cascadeDeletion: true
                },
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'tagRuleConfigLineList:*',
                properties: generateXindexInOrder({
                  tagRuleLineId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  columnType: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.columnType')}}",
                    'x-render-table-column': {
                      'min-width': 150
                    },
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'TAG_RULE_COLUMN_TYPE',
                      '@change': expression(`val => {
                        console.log('$$$$',val,$self.query('.columnValue').take())
                        if(['FIXED_VALUE','SERIAL_CODE'].includes(val)){
                          $self.query('.columnValue').take(field => field.value = null)
                        }else{
                          $self.query('.columnValue').take(field => field.value = '系统取值')
                        }
                      }`)
                    },
                    ...editTableFormItemValid
                  },
                  columnValue: {
                    type: 'string',
                    title: "{{$t('barcodeManageNew.columnValue')}}",
                    'x-render-table-column': {
                      'min-width': 150
                    },
                    'x-reactions': [{
                      dependencies: ['.columnType'],
                      fulfill: {
                        state: {
                          disabled: expression(`!['FIXED_VALUE','SERIAL_CODE'].includes($deps[0]) || $readOnly`),
                          // 'component[1].placeholder': expression(`$deps[0] === 'SERIAL_CODE' ? '请输入流水码位数' : '请输入'`)
                          'component[1].placeholder': expression(
                            `$deps[0] === 'SERIAL_CODE' ? ${i18nExpression('cusEntry.supplement20250211.inputWaterCodeDigits')} 
                            : ${i18nExpression('common.pleaseInput')}`)
                        }
                      }
                    }
                    ],
                    ...editTableFormItemValid
                  },
                  operation: {
                    type: 'void',
                    title: "{{$t('common.operation')}}",
                    'x-render-table-column': {
                      width: 120
                    },
                    'x-component': 'RenderTableButtonList',
                    'x-reactions': expression(`field => {
                      field.visible = !$readOnly
                    }`),
                    properties: {
                      delete: {
                        type: 'void',
                        title: "{{$t('common.delete')}}",
                        'x-component-props': {
                          '@click': expression(`({rowIndex}) => {
                            $table.remove(rowIndex)
                          }`)
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
    }
  }
})

const { emitTabRemove, t, app } = usePageHelper()

const attrs:any = useAttrs()

const $submit = useDebounceFn((type:string, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  const form = toJS($form.values)
  const { tagRuleName, tagRuleType, tagRuleConfigLineList } = form
  if (type === 'submit') {
    if (!tagRuleConfigLineList || !tagRuleConfigLineList.length) {
      $message.warning(t('barcodeManageNew.notNull'))
      return
    }
  }
  for (let item of tagRuleConfigLineList) {
    if (item.columnType === 'SERIAL_CODE' && !/^[1-9]\d*$/.test(item.columnValue)) {
      $message.warning(t('barcodeManageNew.tips3'))
      return
    }
    if (item.columnType === 'FIXED_VALUE' && item.columnValue.toString().length > 50) {
      // '固定值对应的字段长度不能超过50个字符'
      $message.warning(t('cusEntry.supplement20250211.fixedValueFieldLengthLimit'))
      return
    }
  }
  $queryEngine.request.baseRequest(
    {
      type: 'TagRuleConfig',
      lang: 'zh-cn',
      loading: true,
      query: {
        '*': {},
      },
      payload: [
        {
        ...form,
        status: type === 'submit' ? 'Y' : 'DRAFT'
        },
      ],
      action: 'save',
    }
  ).then(res => {
    $message.success(t('common.successSave'))
    if (type === 'save') {
      console.log('response', res)
      $form.values.tagRuleId = res?.data[0].tagRuleId
      $queryEngine.request['read']()
    } else {
      emitTabRemove(attrs.params.tabName)
      $bus.$emit('TagRuleConfig')
    }
  })
}, 216)

const scope = {
  emitTabRemove,
  app,
  $submit
}

</script>
<template>
  <RenderEngine schemaKey="barcodeRuleConfigDetail" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
