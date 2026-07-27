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
      questionContentEditor: null
    }
  },
  SouQuestion: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sou',
      "type": "SouAnswer",
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            let id = $attrs.params.row.answerId
            $form.values.answerId = id
            if ($attrs.params.flag === 'clarifyFormQuestion'){
              $form.values.projectId = $attrs.params.row.projectId
              $form.values.souNo = $attrs.params.row.souNo
              $form.values.souName = $attrs.params.row.souName
              $form.values.questionId = $attrs.params.row.questionId
              $form.values.questionNum = $attrs.params.row.questionNum
            }
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$form.values.answerId]
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
                  answerId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  answerStatus: {
                    type: 'string',
                    'x-hidden': true
                  },
                  answerNum: {
                    type: 'string',
                    title: i18nExpression('bidMod.answerNum'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  answerTitle: {
                    type: 'string',
                    title: i18nExpression('bidMod.answerTitle'),
                    required: true,
                    'x-decorator': 'FormItem'
                  },
                  questionNum: {
                    type: 'string',
                    title: '质疑单号',
                    'x-hidden': true
                  },
                  souName: {
                    type: 'string',
                    title: i18nExpression('bidMod.bidingName'),
                    'x-decorator': 'FormItem',
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      readPretty: '{{$form.readPretty}}',
                      showKey: 'souName',
                      propKey: 'souNo',
                      'preQueryData': '{{{ "t.sou_type": $attrs.params.souType }}}',
                      'name': 'scc_sou_project',
                      disabled: '{{ $vendor() || $attrs.params.flag == "clarifyFormQuestion" }}',
                      '@close-quicksearch': expression(`(value) => {
                        if (value) {
                          $form.values.projectId = value ? value.projectId : ''
                          $form.values.souNo = value ? value.souNo : ''
                          $form.values.souName = value ? value.souName : ''
                        } else {
                          $form.values.projectId = ''
                          $form.values.souName = ''
                          $form.values.souNo = ''
                        }
                      }`),
                      ...requiredValidatorSegment
                    }
                  },
                  projectId: {
                    type: 'string',
                    title: i18nExpression('bidMod.bidingName'),
                    'x-hidden': true,
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
          answerContentDetail: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('bidMod.questionContent')
            },
            properties: {
              answerContent: {
                type: 'string',
                'x-component': 'Tinymce',
                'x-component-props': {
                  id: 'answerTinymce',
                  height: 460,
                  '@setup': expression(`(editorInstance) => {
                    this.questionContentEditor = editorInstance
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
                  'business-id': expression(`$form.query('.answerId').get('value')`),
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
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              }).catch((err) => {
                console.log(err)
              });
            }`)
          }
        },
        save: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-hidden': `{{ $vendor()}}`,
          'x-component-props': {
            type: 'default',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                answerStatus: values.answerStatus || 'DRAFT',
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              })
            }`)
          }
        },
        publish: {
          type: 'void',
          'x-content': i18nExpression('common.publish'),
          'x-component': 'Button',
          'x-hidden': `{{ $vendor()}}`,
          // 'x-visible': `{{ $buyer() && ['add','edit'].includes($attrs.params.flag)) }}`,
          'x-component-props': {
            '@submit': expression(`async (values) => {
              $queryEngine.request.save({
                ...values,
                projectId: Number(values.projectId),
                souType: $attrs.params.souType,
                answerStatus: 'SUBMITTED'
              },{
                customizeAction:'publish'
              }).then(() => {
                $message.success($t('common.successSave'))
                $bus.$emit('answerList')
                emitTabRemove($attrs.tabName)
              })
            }`)
          }
        }
      }
    }
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
  <RenderEngine schemaKey="answerDetail" :pageAttrs="$attrs" :schema="questionDetailSchema" :scope="scope" :components="components" />
</template>
