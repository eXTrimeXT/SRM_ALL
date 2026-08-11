<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression, toJS, generateCharReactionExpression } from '@meicloud/render-engine'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { useAttrs, nextTick } from 'vue-demi'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import ReviewList from 'mods@/vendorManagementSupplier/views/complaintinfoEngine/reviewList'
import { complaintInfo } from 'mods@/vendorManagementSupplier/api'

const schema = defineSchemas({
  // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      curStep: 0,
      // 信息交流列表
      content: [
        // {
        //   reviewUserName: '之煜答复',
        //   creationDate: '2023-03-03 16:43',
        //   reviewContent: '现在应该只有合同和条码的改了，只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的。',
        //   fileUploads: [
        //     {
        //       fileuploadId: '1',
        //       fileuploadName: '供应商投诉处理附件.xlsx'
        //     }
        //   ]
        // },
        // {
        //   reviewUserName: '之煜答复',
        //   creationDate: '2023-03-03 16:43',
        //   reviewContent: '现在应该只有合同和条码的改了，只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的只有这两个是项目上用到的。',
        //   fileUploads: [
        //     {
        //       fileuploadId: '1',
        //       fileuploadName: '供应商投诉处理附件.xlsx'
        //     },
        //     {
        //       fileuploadId: '1',
        //       fileuploadName: '供应商投诉处理附件.xlsx'
        //     }
        //   ]
        // }
      ]
    }
  },
  ComplaintInfo: {
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
            return data
          }`)
        },
        read: {
          immediate: true,
          ready: expression(`() => {
            let id = $attrs.params?.row?.complaintInfoId
            $values.complaintInfoId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            data.payload = [$values.complaintInfoId]
            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res)
            $form.readPretty = $readOnly
            const value = res.data[0]
            $form.setValues({
              ...value
            })
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
            const state = $form.query('state').get('data')
            if($values.complaintInfoId){
              complaintInfo.getByCompliantInfoId($values.complaintInfoId).then(({data}) => {
                console.log('data:::',data)
                state.content = data || []
              })
            }
            let step;
            if(['SUBMITTED','REPLY_FEEDBACK'].includes($values.complaintStatus)){
              step = 1
            }else if(['ANSWERED','APPLICATION_CLOSED'].includes($values.complaintStatus)){
              step = 2
            }else if($values.complaintStatus === 'CLOSED'){
              step = 4
            }else{
              step = 0
            }
            state.curStep = step
          }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        back: {
          type: 'void',
          'x-content': "{{$t('common.cancel')}}",
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        submitReply: {
          type: 'void',
          'x-content': "{{$t('vendorMod.submitAnswer')}}",
          'x-visible': expression(`!$attrs.params.readonly && ['SUBMITTED','REPLY_FEEDBACK'].includes($values.complaintStatus)`),
          'x-component': 'Button',
          'x-component-props': {
            '@click': expression(`() => {
              return $submitReview($form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        },
        complaintClose: {
          type: 'void',
          'x-content': "{{$t('vendorMod.applyClose')}}",
          'x-visible': expression(`!$attrs.params.readonly && ['ANSWERED','REPLY_FEEDBACK'].includes($values.complaintStatus)`),
          'x-component': 'Button',
          'x-component-props': {
            '@click': expression(`() => {
              return $submit('APPLICATION_CLOSED',$form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        }
      }
    },
    properties: {
      steps: {
        type: 'void',
        'x-decorator': 'div',
        'x-decorator-props': {
          class: 'stepDiv'
        },
        'x-component': 'Steps',
        'x-component-props': {
          alignCenter: true,
          finishStatus: 'success'
        },
        'x-reactions': {
          dependencies: ['complaintStatus'],
          fulfill: {
            state: {
              'component[1].active': expression(`$form.query('state').get('data').curStep`)
            }
          }
        },
        properties: {
          step2: {
            type: 'void',
            'x-component': 'el-step',
            'x-component-props': {
              title: "{{$t('vendorMod.submitted')}}"
            }
          },
          step3: {
            type: 'void',
            'x-component': 'el-step',
            'x-component-props': {
              title: "{{$t('vendorMod.buyerDeal')}}"
            }
          },
          step4: {
            type: 'void',
            'x-component': 'el-step',
            'x-component-props': {
              title: "{{$t('vendorMod.supplierDeal')}}"
            }
          },
          step5: {
            type: 'void',
            'x-component': 'el-step',
            'x-component-props': {
              title: "{{$t('vendorMod.closed')}}"
            }
          }
        }
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          activeKey: ['1', '2', '3', '4', '5']
        },
        properties: {
          // 投诉基本信息
          '1': {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.complaintBaseInfo')}}"
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
                properties: generateXindexInOrder({
                  complaintInfoId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  complaintNo: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintInfoId')}}",
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  complaintType: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintType')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'COMPLAINT_TYPE'
                    },
                    ...requiredValidatorSegment
                  },
                  complaintStatus: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintStatus')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'COMPLAINT_STATUS',
                      disabled: true
                    }
                  },
                  supplierName: {
                    type: 'string',
                    title: "{{$t('common.vendor')}}",
                    'x-decorator': 'FormItem'
                  },
                  supplierCode: {
                    type: 'string',
                    title: "{{$t('common.vendorCode')}}",
                    'x-decorator': 'FormItem'
                  },
                  complaintUserName: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintUserName')}}",
                    'x-decorator': 'FormItem'
                  },
                  complaintUserPhone: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintUserPhone')}}",
                    'x-decorator': 'FormItem'
                  },
                  complaintUserEmail: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintUserEmail')}}",
                    'x-decorator': 'FormItem'
                  },
                  authUserName: {
                    type: 'string',
                    title: "{{$t('vendorMod.PersonLiable')}}",
                    'x-decorator': 'FormItem'
                  },
                  orgName: {
                    type: 'string',
                    title: "{{$t('vendorMod.ceeaOrgName')}}",
                    'x-decorator': 'FormItem'
                  },
                  categoryName: {
                    type: 'string',
                    title: "{{$t('vendorMod.categoryName')}}",
                    'x-decorator': 'FormItem'
                  }
                })
              }
            }
          },
          // 投诉内容
          '2': {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.complaintContent')}}"
            },
            properties: {
              contentForm: {
                type: 'void',
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                  layout: 'vertical'
                },
                'x-component': 'FormGrid',
                'x-component-props': {
                  maxColumns: 1,
                  columnGap: 32,
                  rowGap: 0
                },
                properties: {
                  complaintTheme: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintTheme')}}",
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 4
                    },
                    ...requiredValidatorSegment
                  },
                  complaintContent: {
                    type: 'string',
                    title: "{{$t('vendorMod.complaintContent')}}",
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 4
                    },
                    'x-component-props': {
                      type: 'textarea',
                      autosize: expression(`{ minRows: 6, maxRows: 8 }`)
                    },
                    ...requiredValidatorSegment
                  }
                }
              }
            }
          },
          // 附件
          '3': {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.relegation.accessory')}}"
            },
            properties: {
              fileUploads: {
                type: 'array',
                'x-query-engine-relation': 'fileUploads:*',
                'x-component': 'FileDynamic',
                'x-component-props': {
                  'scene-module-code': 'SCENE_COMPLAINT_INFO_ATTACHMENT',
                  primaryKey: 'sceneFileId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true,
                  'business-id': expression(`$values.complaintInfoId`),
                  editable: false
                }
              }
            }
          },
          // 信息交流
          '4': {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: '信息交流记录'
            },
            'x-visible': expression(`$form.query('state').get('data').content?.length`),
            properties: {
              reviewList: {
                type: 'void',
                'x-component': 'ReviewList',
                'x-component-props': {
                  content: expression(`$form.query('state').get('data').content`)
                }
              }
            }
          },
          '5': {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.buyerAnswer')}}"
            },
            'x-visible': expression(`['SUBMITTED','REPLY_FEEDBACK'].includes($values.complaintStatus)`),
            properties: {
              reviewForm: {
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
                  reviewContent: {
                    type: 'string',
                    title: "{{$t('vendorMod.responsibleReview')}}",
                    'x-read-pretty': false,
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 4
                    },
                    'x-component-props': {
                      disabled: expression(`$attrs.params.readonly`),
                      type: 'textarea',
                      autosize: expression(`{ minRows: 6, maxRows: 8 }`)
                    },
                    ...requiredValidatorSegment
                  }
                }
              },
              reviewFile: {
                type: 'array',
                'x-component': 'FileDynamic',
                'x-component-props': {
                  'scene-module-code': 'SCENE_COMPLAINT_INFO_REVIEW_ATTACHMENT',
                  'editable': expression(`!$attrs.params.readonly && $values.complaintStatus !== 'CLOSED'`)
                }
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

const $submit = async (type:string, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  const form = toJS($form.values)
  return $queryEngine.request.baseRequest(
    {
      type: 'ComplaintInfo',
      lang: 'zh-cn',
      loading: true,
      query: {
        '*': {}
      },
      payload: [
        {
          ...form,
          complaintStatus: type
        }
      ],
      action: 'require'
    }
  ).then(res => {
    $message.success(t('common.success'))
    emitTabRemove(attrs.params.tabName)
    $bus.$emit('ComplaintInfo')
  })
}

const $submitReview = async ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  await $form.validate()
  const form = toJS($form.values)
  const { reviewContent, reviewFile, complaintInfoId } = form
  let complaintdealparentid = null
  const content = $form.query('state').get('data').content
  if (content.length) {
    complaintdealparentid = content[content.length - 1].complaintDealId
  }
  let params = {
    complaintInfoId,
    reviewContent,
    complaintdealparentid,
    fileUploads: reviewFile
  }
  console.log('params:::', params)
  complaintInfo.review(params).then(() => {
    $message.success(t('common.success'))
    emitTabRemove(attrs.params.tabName)
    $bus.$emit('ComplaintInfo')
  })
}

const scope = {
  emitTabRemove,
  app,
  $submit,
  $submitReview,
  nextTick,
  complaintInfo
}

const components = {
  FileDynamic,
  ReviewList
}

</script>
<template>
  <RenderEngine
    schemaKey="complaintReviewDetail"
    :pageAttrs="$attrs"
    :scope="scope"
    :schema="schema"
    :components="components"
  />
</template>
