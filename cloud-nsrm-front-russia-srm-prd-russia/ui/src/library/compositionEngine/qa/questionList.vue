<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, markRaw, generateXindexInOrder, expression, i18nExpression, generateCharFunctionExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"

import QuestionDetail from './components/questionDetail.vue'
import AnswerDetail from './components/answerDetail.vue'

import { souQuestionApi } from './api'

const { emitTabAdd, t } = usePageHelper()
import { useAttrs, computed, ref } from 'vue-demi'
let attrs:any = useAttrs()

// 行数据 | 操作类型 clarifyFormQuestion view add edit
const $qEdit = (row: any, flag: string) => {
  let name, title
  if (flag === 'add') { // 新增
    name = 'questionDetail'
    title = '新增质疑'
  } else if (['edit'].includes(flag)) { // 编辑 | 澄清质疑
    name = 'questionDetail' + row.questionNum || row.questionId
    title = row.questionNum
  } else {
    name = 'questionDetail' + row.questionNum
    title = row.questionNum
  }
  let tab = {
    component: QuestionDetail,
    params: {
      row,
      flag,
      tabName: name,
      souType: attrs.params.souType
    },
    title,
    name
  }
  emitTabAdd(tab)
}
// 澄清
const $aEdit = (row: any, flag: string) => {
  let name, title
  if (flag === 'add') { // 新增
    name = 'answerDetail'
    title = '新增澄清'
  } else if (flag === 'clarifyFormQuestion') {
    name = 'answerDetail' + row.questionNum
    title = row.questionNum + '-新增澄清'
  } else if (flag === 'edit') { // 编辑 | 澄清质疑
    name = 'answerDetail' + row.answerNum || row.answerId
    title = row.answerNum
  } else {
    name = 'answerDetail' + row.answerNum
    title = row.answerNum
  }
  let tab = {
    component: AnswerDetail,
    params: {
      row,
      flag,
      tabName: name,
      souType: attrs.params.souType
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {}
  },
  question: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-decorator-props': {
      style: 'height:auto'
    },
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sou',
      "type": "SouQuestion",
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              '*': {}
            }
            let filter =  data.payload.filter || {}
            data.payload = {
              "page": {
                sort: "creationDate desc"
              },
              "filter": {
                ...filter,
                'souType': {eq: $attrs.params.souType }
              }
            }
            return data
          }`)
        }
      }
    },
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          '@query': expression(`(val) => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        },
        properties: generateXindexInOrder({
          // 项目编号
          souNo: {
            type: 'string',
            title: "{{$t('bidMod.bidingNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 项目名称
          souName: {
            type: 'string',
            title: "{{$t('bidMod.bidingName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 质疑标题
          questionTitle: {
            type: 'string',
            title: "{{$t('bidMod.questionTitle')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 质疑编号
          questionNum: {
            type: 'string',
            title: "{{$t('bidMod.questionNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 质疑状态
          questionStatus: {
            type: 'string',
            title: "{{$t('bidMod.questionStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUERY_STATUS'
            }
          },
          // 澄清标题
          answerTitle: {
            type: 'string',
            title: "{{$t('bidMod.answerTitle')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 澄清编号
          answerNum: {
            type: 'string',
            title: "{{$t('bidMod.answerNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // 澄清状态
          answerStatus: {
            type: 'string',
            title: "{{$t('bidMod.answerStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CLARIFIED_STATUS'
            }
          }
        })
      },
      SouQuestion: {
        type: 'void',
        properties: {
          bus: {
            type: 'void',
            'x-component': 'BusEvent',
            'x-component-props': {
              eventName: 'questionList',
              '@listener': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)
            }
          },
          toolbarQuestion: {
            type: 'void',
            'x-component': 'Space',
            'x-component-props': {
              style: 'margin-bottom: 16px'
            },
            properties: {
              // 新增质疑
              add: {
                type: 'void',
                title: "{{$t('common.add')}}",
                'x-component': 'RButton',
                'x-hidden': `{{ $buyer() }}`,
                'x-component-props': {
                  type: 'primary',
                  '@click': '{{() => $qEdit({}, "add")}}'
                }
              }
            }
          },
          tableQuestion: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              style: 'flex: 1',
              preColumns: 'seq',
              openCustomTable: true
            },
            properties: generateXindexInOrder({
              questionId: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-primary-key': true
              },
              creationDate: {
                type: 'string',
                'x-hidden': true,
                'x-query-engine-primary-key': true
              },
              lastUpdateDate: {
                type: 'string',
                'x-query-engine-sort': 'desc',
                'x-hidden': true,
                'x-query-engine-primary-key': true
              },
              questionNum: {
                type: 'string',
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let optFlag = row.questionStatus == 'DRAFT' ? 'edit' : 'view'
                    $qEdit(row, optFlag)
                  }`)
                },
                'x-render-table-column': {
                  title: "{{$t('bidMod.questionNum')}}",
                  minWidth: 150,
                  customRender: true
                }
              },
              questionTitle: {
                type: 'string',
                title: "{{$t('bidMod.questionTitle')}}",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              questionStatus: {
                type: 'string',
                title: "{{$t('bidMod.questionStatus')}}",
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'QUERY_STATUS'
                },
                'x-render-table-column': {
                  width: 110
                }
              },
              souNo: {
                type: 'string',
                title: "{{$t('bidMod.bidingNum')}}",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              souName: {
                type: 'string',
                title: "{{$t('bidMod.bidingName')}}",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              submitTime: {
                type: 'string',
                title: "澄清日期",
                'x-render-table-column': {
                  width: 100
                }
              },
              vendorName: {
                type: 'string',
                title: "质疑来源",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              rejectReason: {
                type: 'string',
                title: "{{$t('bidMod.rejectReason')}}",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              createdFullName: {
                type: 'string',
                title: "创建人",
                'x-render-table-column': {
                  minWidth: 100
                }
              },
              operation: {
                type: 'void',
                title: "{{$t('common.operation')}}",
                'x-render-table-column': {
                  width: 204,
                  fixed: 'right'
                },
                'x-component': 'RenderTableButtonList',
                properties: {
                  // 供应商 编辑
                  edit: {
                    type: 'void',
                    title: "{{$t('common.edit')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.questionStatus'],
                      `($vendor() && ['DRAFT'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $qEdit(row, "edit")')
                    }
                  },
                  // 供应商 删除
                  delete: {
                    type: 'void',
                    title: "{{$t('common.delete')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.questionStatus'],
                      `($vendor() && ['DRAFT'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      popconfirm: {
                        title: i18nExpression('common.confirmDeleteRow')
                      },
                      '@click': expression(`({ row }) => {
                        let obj = {
                          questionId: row.questionId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouQuestion",
                          action: "delete",
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)
                    }
                  },
                  // 供应商 - 撤回质疑
                  withdraw: {
                    type: 'void',
                    title: "{{$t('bidMod.withdraw')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.questionStatus'],
                      `($vendor() && ['SUBMITTED'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression(`({ row }) => {
                        let obj = {
                          questionId: row.questionId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouQuestion",
                          action: "withDraw", // 后端大小写不一致
                          service: 'sou',
                          loading: true,
                          payload: [{
                            ...obj
                          }],
                          query: { "*": {}}
                        }).then((res) => {
                          $queryEngine.state.paginationManagement.refresh()
                        }).catch((err) => {
                          $message.error(err.message)
                        })
                      }`)
                    }
                  },
                  // 采购商 - 澄清质疑
                  clarification: {
                    type: 'void',
                    title: "{{$t('bidMod.clarification')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.questionStatus'],
                      `($buyer() && !['CLARIFIED'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $aEdit(row, "clarifyFormQuestion")')
                    }
                  },
                  // 采购商 - 驳回质疑
                  toRefuse: {
                    type: 'void',
                    title: "{{$t('common.toRefuse')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.questionStatus'],
                      `($buyer() && ['SUBMITTED'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression(`({ row }) => {
                        $prompt($t('bidMod.msgRejectReason'), $t('common.toRefuse'), {
                          confirmButtonText: $t('common.confirm'),
                          cancelButtonText: $t('common.cancel'),
                          inputValidator: (value) => !(!value || value.length > 200),
                          inputErrorMessage: $t('bidMod.biddingManagementBuyer.rejectReason')
                        }).then(({ value }) => {
                          let obj = {
                            questionId: row.questionId,
                            souType: $attrs.params.souType,
                            rejectReason: value
                          }
                          $queryEngine.request.baseRequest({
                            type: "SouQuestion",
                            action: "reject",
                            service: 'sou',
                            loading: true,
                            payload: [{
                              ...obj
                            }],
                            query: { "*": {}}
                          }).then((res) => {
                            $queryEngine.state.paginationManagement.refresh()
                          }).catch((err) => {
                            $message.error(err.message)
                          })
                        }).then(() => {
                          $message.success($t('bidMod.successRefuse'))
                        }).catch((err) => {
                          console.error('approvalRefuse', err)
                        })
                      }`)
                    }
                  }
                }
              }
            })
          },
        }
      }
    }
  }
})


const scope = {
  souQuestionApi,
  $qEdit,
  $aEdit,
  $markRaw: markRaw,
}
const components = {}
</script>

<template>
  <RenderEngine schemaKey="questionList" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components"/>
</template>
