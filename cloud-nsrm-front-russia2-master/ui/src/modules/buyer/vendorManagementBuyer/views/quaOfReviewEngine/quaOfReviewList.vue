<script setup lang="ts">

import { onActivated } from 'vue-demi'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  i18nExpression,
  expression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import quaOfReviewDetail from './quaOfReviewDetail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

onActivated(() => {
  const { params: { from, funName, fdSubject } } = app.$route
  if (from === 'vendorProfileList' && funName === 'quaOfReview') {
    $detailOne('add', {
      vendorId: fdSubject.companyId,
      vendorCode: fdSubject.companyCode,
      vendorName: fdSubject.companyName
    })
  }
})

const $detailOne = (flag: string, row?: any) => {
  let name = row?.reviewFormNumber ?? ''
  emitTabAdd({
    component: quaOfReviewDetail,
    params: {
      flag,
      row,
      tabName: name ? 'quaOfReviewDetail' + name : 'quaOfReviewDetail'
    },
    title: name || $t('vendorMod.addQua'), // 资质审查新增
    name: name ? 'quaOfReviewDetail' + name : 'quaOfReviewDetail'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add')
}

// 删除
const $deleteOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.delete(row.reviewFormId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', row)
}

// @ts-ignore
const scope = {
  $addOne,
  $deleteOne,
  $readOne,
  $editOne
}

// @ts-ignore
const schema = defineSchemas({
  ReviewForm: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component-props': {
      class: 'flex-container reviewFormList',
      direction: 'vertical'
    },
    'x-component': 'el-container',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ReviewFormHead',
          // @ts-ignore
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          reviewFormNumber: {
            type: 'string',
            title: i18nExpression('vendorMod.quaNum'), // '资质审查单号'
            'x-query-engine-query-operator': 'contains'
          },
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'), // '供应商名称'
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          approveStatus: {
            type: 'string',
            title: i18nExpression('vendorMod.orderStatus'), // '状态'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            }
          },
          quaReviewType: {
            type: 'string',
            title: i18nExpression('vendorMod.quaType'), // '资质审查类型'
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUA_REVIEW_TYPE'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $addOne()')
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          reviewFormId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS_TYPE'
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.orderStatus'), // '状态'
              minWidth: 100
            }
          },
          vendorId: { // 供应商ID
            type: 'string',
            'x-hidden': true
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorCode'), // '供应商编码'
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.vendorName'), // '供应商名称'
              minWidth: 120
            }
          },
          reviewFormNumber: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('vendorMod.quaNum'), // '资质审查单号'
              minWidth: 120,
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            }
          },
          quaReviewType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('vendorMod.quaType'), // '资质审查类型'
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'QUA_REVIEW_TYPE'
            }
          },

          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('common.creator'), // '创建人'
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('common.creationTime'), // '创建时间'
              minWidth: 120
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'

          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 160,
              fixed: 'right',
              sortable: false
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'Space',
                properties: {
                  edit: {
                    type: 'void',
                    title: i18nExpression('common.edit'), // 编辑
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.approveStatus'],
                      '[\'DRAFT\', \'REJECTED\', \'WITHDRAW\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({ row }) => $editOne(row)')
                    }
                  },
                  view: {
                    type: 'void',
                    title: i18nExpression('common.approve'), // 审批
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.approveStatus'],
                      '[\'SUBMITTED\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({ row }) => $readOne(row)')
                    }
                  },
                  delete: {
                    type: 'void',
                    title: i18nExpression('common.delete'), // '删除'
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.approveStatus'],
                      '[\'DRAFT\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      showPopconfirm: true,
                      '@confirm': expression('({ row }) => $deleteOne(row, $queryEngine, $message)')
                    }
                  },
                  reject: {
                    type: 'void',
                    title: i18nExpression('common.abandon'), // 废弃
                    'x-component': 'TableButton',
                    'x-reactions': changeFieldVisibleByDeps(
                      ['.approveStatus'],
                      '[\'WITHDRAW\',\'REJECTED\'].includes($deps[0])'
                    ),
                    'x-component-props': {
                      type: 'text',
                      '@click': expression('({ row }) => $readOne(row)')
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
})
</script>

<template><RenderEngine :schema="schema" :scope="scope" schemaKey="quaOfReviewList" /></template>

<style lang="scss">
.reviewFormList .render-table {
  position: relative;
  height: 0;
  flex: 1;
}
</style>
