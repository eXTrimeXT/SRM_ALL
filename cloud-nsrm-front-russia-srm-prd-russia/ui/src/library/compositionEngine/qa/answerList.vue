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

// 行数据 | 操作类型 view add edit
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
  } else if (['edit'].includes(flag)) { // 编辑 | 澄清质疑
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
  answer: {
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
      "type": "SouAnswer",
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
      SouAnswer: {
        type: 'void',
        properties: {
          bus: {
            type: 'void',
            'x-component': 'BusEvent',
            'x-component-props': {
              eventName: 'answerList',
              '@listener': expression(`() => {
                $queryEngine.state.paginationManagement.refresh()
              }`)
            }
          },
          toolbarAnswer: {
            type: 'void',
            'x-component': 'Space',
            'x-component-props': {
              style: 'margin-bottom: 16px'
            },
            properties: {
              add: {
                type: 'void',
                title: "{{$t('common.add')}}",
                'x-component': 'RButton',
                'x-hidden': `{{ $vendor() }}`,
                'x-component-props': {
                  type: 'primary',
                  '@click': '{{() => $aEdit({}, "add")}}',
                }
              }
            }
          },
          tableAnswer: {
            type: 'array',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              style: 'flex: 1',
              preColumns: 'seq',
              openCustomTable: true
            },
            properties: generateXindexInOrder({
              answerId: {
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
              answerNum: {
                type: 'string',
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => $aEdit(row, "view")`)
                },
                'x-render-table-column': {
                  title: "{{$t('bidMod.answerNum')}}",
                  minWidth: 150,
                  customRender: true
                }
              },
              answerTitle: {
                type: 'string',
                title: "{{$t('bidMod.answerTitle')}}",
                'x-render-table-column': {
                  minWidth: 150
                }
              },
              answerStatus: {
                type: 'string',
                title: "{{$t('bidMod.answerStatus')}}",
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'CLARIFIED_STATUS'
                },
                'x-render-table-column': {
                  width: 110
                }
              },
              questionId: {
                type: 'string',
                'x-hidden': true
              },
              questionNum: {
                type: 'string',
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => $qEdit(row, "view")`)
                },
                'x-render-table-column': {
                  title: "{{$t('bidMod.questionNum')}}",
                  minWidth: 150,
                  customRender: true
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
                  // 采购商 - 编辑
                  edit: {
                    type: 'void',
                    title: "{{$t('common.edit')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.answerStatus'],
                      `($buyer() && ['DRAFT'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression('({ row }) => $aEdit(row, "edit")')
                    }
                  },
                   // 采购商 - 删除澄清
                  delete: {
                    type: 'void',
                    title: "{{$t('common.delete')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.answerStatus'],
                      `($buyer() && ['DRAFT'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      popconfirm: {
                        title: i18nExpression('common.confirmDeleteRow')
                      },
                      '@click': expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
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
                  // 采购商 - 发布澄清
                  publish: {
                    type: 'void',
                    title: "{{$t('common.publish')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.answerStatus'],
                      `($buyer() && ['DRAFT'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "publish",
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
                   // 采购商 - 撤回澄清
                  withdraw: {
                    type: 'void',
                    title: "{{$t('bidMod.withdraw')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.answerStatus'],
                      `($buyer() && ['ISSUED'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "withdraw", // 后端大小写不一致
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
                  // 供应商 - 接受澄清
                  acceptClarify: {
                    type: 'void',
                    title: "{{$t('bidMod.acceptClarify')}}",
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.answerStatus'],
                      `($vendor() && ['ISSUED'].includes($deps[0]))`
                    ),
                    'x-component-props': {
                      '@click': expression(`({ row }) => {
                        let obj = {
                          answerId: row.answerId,
                          souType: $attrs.params.souType
                        }
                        $queryEngine.request.baseRequest({
                          type: "SouAnswer",
                          action: "vendorAccept",
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
  souQuestionApi,
  $qEdit,
  $aEdit,
  $markRaw: markRaw,
}
const components = {}
</script>

<template>
  <RenderEngine schemaKey="answerList" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
