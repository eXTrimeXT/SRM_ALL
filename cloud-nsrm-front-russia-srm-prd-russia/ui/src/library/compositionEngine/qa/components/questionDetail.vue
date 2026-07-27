<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, markRaw, generateXindexInOrder, expression, i18nExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
import { useAttrs, computed, ref } from 'vue-demi'

import Tinymce from '@/components/Tinymce'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { souQuestionApi } from '../api'

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

let attrs:any = useAttrs()
const questionDetailSchema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      souProjectList: [],
      answerContentEditor: null
    }
  },
  SouQuestion: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      "type": "SouQuestion",
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.row.questionId
            $form.values.questionId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$form.values.questionId]
          }`),
          onSuccess: expression(`(res) => {
            console.log('res::',res)
            const value = res.data[0]
            $form.setValues({
              ...value
            })
            $form.query('sceneFiles').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        },
        save: {
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
          // 单据信息
          receiptInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('vendorMod.receiptInfo')
            },
            properties: {
              orderInfo: {
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
                properties: {
                  questionId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  questionNum: {
                    type: 'string',
                    title: `{{$t('bidMod.questionNum')}}`,
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  questionTitle: {
                    type: 'string',
                    title: `{{$t('bidMod.questionTitle')}}`,
                    required: true,
                    'x-decorator': 'FormItem'
                  },
                  questionStatus: {
                    type: 'string',
                    title: i18nExpression('bidMod.questionStatus'),
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'QUERY_STATUS',
                      disabled: true
                    }
                  },
                  projectId: {
                    type: 'string',
                    title: `{{$t('bidMod.bidingName')}}`,
                    required: true,
                    'x-decorator': 'FormItem',
                    'x-component': 'Select',
                    'x-hidden': `{{ $buyer() }}`,
                    'x-component-props': {
                      'filterable': true,
                      'remote': true,
                      'clearable': true,
                      'automatic-dropdown': true,
                      'placeholder': `{{$t('bidMod.msgKeyword')}}`,
                      '@change': expression(`(val) => {
                        if (val) {
                          const souProjectList = $form.query('state').get('data').souProjectList
                          const project = souProjectList.find(item => item.value === val)
                          const { label = '', no = '' } = project || {}
                          $form.values.souName = label
                          $form.values.souNo = no
                        }else {
                          $form.values.souName = ''
                          $form.values.souNo = ''
                        }
                      }`),
                      'remote-method': expression(`(val) => {
                        let parma = {
                          souType: $attrs.params.souType,
                          souName: val,
                          pageSize: 999,
                          pageNum: 1
                        }
                        souQuestionApi.getSouProjectList(parma).then(res => {
                          let attr = res.data.list || []
                          let optionData = []
                          attr.forEach(item => {
                            const objs = {
                              id: (item.projectId).toString(),
                              no: item.souNo,
                              value: (item.projectId).toString(),
                              label: item.souName
                            }
                            optionData.push(objs)
                          })
                          $form.query('state').get('data').souProjectList = optionData
                          $self.dataSource = optionData
                        })
                      }`)
                    },
                    'x-reactions': [
                      expression(`(field) => {
                        let parma = {
                          souType: $attrs.params.souType,
                          pageSize: 999,
                          pageNum: 1
                        }
                        souQuestionApi.getSouProjectList(parma).then(res => {
                          let attr = res.data.list || []
                          let optionData = []
                          attr.forEach(item => {
                            const objs = {
                              id: (item.projectId).toString(),
                              no: item.souNo,
                              value: (item.projectId).toString(),
                              label: item.souName
                            }
                            optionData.push(objs)
                          })
                          $form.query('state').get('data').souProjectList = optionData
                          $self.dataSource = optionData
                        })
                      }`)
                    ]
                  },
                  souName: {
                    type: 'string',
                    title: i18nExpression('bidMod.bidingName'),
                    'x-hidden': `{{ $vendor() }}`,
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  souNo: {
                    type: 'string',
                    title: i18nExpression('bidMod.bidingNum'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  }
                }
              }
            }
          },
          questionContentDetail: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.questionContent')
            },
            properties: {
              questionContent: {
                type: 'string',
                'x-component': 'Tinymce',
                'x-component-props': {
                  id: 'questionTinymce',
                  height: 460,
                  '@setup': expression(`(editorInstance) => {
                    this.answerContentEditor = editorInstance
                    if ($attrs.params.flag == 'view') {
                      editorInstance.setMode('readonly')
                    } else {
                      editorInstance.setMode('design')
                    }
                    $self.setData({
                      editorInstance: $markRaw(editorInstance)
                    })
                  }`)
                }
              }
            }
          },
          // 相关附件
          relevantAttachment: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.attachment')
            },
            properties: {
              sceneFiles: {
                type: 'array',
                'x-query-engine-relation': 'sceneFiles:*',
                'x-component': 'FileDynamic',
                'x-component-props': {
                  primaryKey: 'sceneFileId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true,
                  'scene-module-code': 'SCENE_SOU_QUESTION_FILE_ATTACHMENT',
                  'business-id': expression(`$form.query('.questionId').get('value')`),
                  'editable': expression(`$attrs.params.flag != 'view'`),
                  'need-init': false
                }
              }
            }
          }
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        goBack: {
          type: 'void',
          'x-content': expression(`$t($readOnly ? 'common.backTo' : 'components.common.cancel')`),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async (values) => {
              if($readOnly){
                emitTabRemove($attrs.tabName)
                return
              }
              app.$confirm('此次修改并未保存是否取消', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)
          }
        },
        save: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-hidden': '{{ $buyer()}}',
          'x-component-props': {
            type: 'default',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                questionStatus: values.questionStatus || 'DRAFT',
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              })
            }`)
          }
        },
        publish: {
          type: 'void',
          'x-content': i18nExpression('common.publish'),
          'x-component': 'Button',
          'x-hidden': '{{ $buyer()}}',
          'x-component-props': {
            '@submit': expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                questionStatus: 'SUBMITTED'
              },{
                customizeAction:'submit'
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('questionList')
                emitTabRemove($attrs.tabName)
              })
            }`)
          }
        }
      }
    },
  }
})

const scope = {
  souQuestionApi,
  emitTabRemove,
  app,
  $markRaw: markRaw
}
const components = {
  Tinymce,
  FileDynamic
}
</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="questionDetailSchema" :scope="scope" :components="components" />
</template>
