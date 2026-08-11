<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  expression,
  defineSchemas,
  generateXindexInOrder,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateCharFunctionExpression,
  generateCharReactionExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  SOU_ORDER_STATUS_ENUM,
  SOU_PROJECT_STATUS_ENUM
} from 'lib@/compositionEngine/sourcing/enum'
import { maxNumberOption } from 'lib@/compositionEngine/sourcing/unit'
import inquiryOrdersDetail from './inquiryOrdersDetail.vue'
import inquiryOrdersQuote from './inquiryOrdersQuote.vue'
import QuoteResultDialogSegment from './inquiryOrderList/quoteResultDialog'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

const { emitTabAdd, t: $t, app, getCurrentUserInfo } = usePageHelper()

// 声明询价对象
interface SouRow {
  souNo?: string
}

/* 撤回 */
const $rollback = async ($prompt: any, $message: any, $queryEngine: any, row: any) => {
  const promptResult = await $prompt(
    '撤回原因',
    '撤回说明',
    {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('common.cancel')
    }
  )

  if (promptResult.action !== 'confirm') {
    return
  }

  if (!promptResult.value) {
    // $message.warning('请输入撤回原因！')
    $message.warning($t('bidMod.withdrawTips1'))
    return
  }
  if (promptResult.value.length > 250) {
    // $message.warning('撤回原因长度最多250个字符！')
    $message.warning($t('bidMod.withdrawTips2'))
    return
  }

  const response = await $queryEngine.request.baseRequest({
    action: 'withdrawOrder',
    payload: [
      {
        projectId: row.souProject,
        cancelReason: promptResult.value
      }
    ],
    query: {
      souProject: {
        '*': {}
      }
    }
  }).catch(() => {})

  if (response) {
    // $message.success('撤回成功！')
    $message.success($t('cusEntry.supplement20250121.withdrawalSuccessful'))
    $queryEngine.state.paginationManagement.refresh()
  }
}

/* 打开详情页签 */
const $openDetailTab = (type: any, row: SouRow = {}) => {
  const { souNo = '' } = row
  const map = new Map([
    // 查看
    [
      'view',
      {
        component: inquiryOrdersDetail,
        params: {
          flag: 'view',
          readonly: true,
          row,
          tabName: `inquiryOrders${type}${souNo}`
        },
        title: souNo,
        name: `inquiryOrders${type}${souNo}`
      }
    ],
    // 报价
    [
      'quote',
      {
        component: inquiryOrdersQuote,
        params: {
          flag: 'quote',
          readonly: false,
          row,
          tabName: `inquiryOrders${type}${souNo}`
        },
        title: souNo,
        name: `inquiryOrders${type}${souNo}`
      }
    ]
  ])

  emitTabAdd(map.get(type))
}

const scope = {
  getCurrentUserInfo,
  $openDetailTab,
  $prompt: app.$prompt,
  // 字典枚举
  $enum: {
    SOU_ORDER_STATUS_ENUM,
    SOU_PROJECT_STATUS_ENUM
  },
  $rollback,
  // 注入工具类方法
  $maxNumberOption: maxNumberOption
}

const schema = defineSchemas({
  // 列表
  InqSouProjectForVendor: {
    type: 'void',
    'x-query-engine': {
      service: 'sou',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'listOrders'
        },
        paginationQuery2: {
          immediate: false,
          action: 'listOrderResult',
          // 添加额外查询字段
          transformRequest: generateCharFunctionExpression(({ $form }, data) => {
            // $form.query('quoteResultDialog').get('data').viewRow = row
            // data.query = {
            //   ...data.query,
            //   souProject: {
            //     ...data.query.souProject,
            //     projectId: {}
            //   }
            // }
            data.payload.filter.projectId = {
              eq: $form.query('quoteResultDialog').get('data').viewRow.projectId
            }
            return data
          })
        }
      }
    },
    'x-decorator': 'el-container',
    'x-component': 'QueryEngine',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    // 属性描述
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'paginationRefresh',
          '@listener': generateCharFunctionExpression(({ $queryEngine }) => {
            $queryEngine.state.paginationManagement.refresh()
          })
        }
      },
      // 描述查询条件
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          // 询价单号
          souNo: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryNo'),
            'x-query-engine-query-operator': 'contains',
            'x-component-props': {
              clearable: true
            }
          },
          // 物料名称
          itemDesc: {
            type: 'string',
            title: i18nExpression('quota.itemName'),
            'x-query-engine-query-operator': 'contains',
            'x-component-props': {
              clearable: true
            }
          },
          // 报价状态
          orderStatus: {
            type: 'string',
            title: i18nExpression('bidMod.quoteStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INQ_SOU_ORDER_STATUS',
              clearable: true
            }
          },
          // 询价状态
          extProjectStatus: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_PROJECT_STATUS',
              clearable: true
            }
          }
        })
      },
      // 表格
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true,
          style: 'margin-top: 44px;'
        },
        properties: generateXindexInOrder({
          // 询价单号
          souNo: {
            type: 'string',
            'x-component': 'RenderTableLink',
            'x-component-props': {
              type: 'text',
              '@click': generateCharFunctionExpression(({ row }) => {
                $openDetailTab('view', row)
              }, true)
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.inquiryNo'),
              minWidth: 140,
              customRender: true
            }
          },
          // 询价标题
          souName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.inquiryTitle'),
              minWidth: 140
            }
          },
          // 单据状态
          extProjectStatus: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_PROJECT_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 轮次
          currentRound: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.currentRound'),
              minWidth: 100
            }
          },
          // 报价状态
          orderStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'INQ_SOU_ORDER_STATUS'
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.quoteStatus'),
              minWidth: 120
            }
          },
          // 报价截止时间
          orderEndTime: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.orderEndTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.quotedeadline'),
              minWidth: 160
            }
          },
          // 创建人
          createdBy: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.bidingCreatedBy'),
              minWidth: 120
            }
          },
          // 发布时间
          publishTime: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.publishTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('bidMod.releaseDatetime'),
              minWidth: 160
            }
          },
          canOrder: {
            type: 'string',
            'x-hidden': true
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 220,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              // 报价
              quote: {
                type: 'void',
                title: i18nExpression('bidMod.doQuote'),
                'x-reactions': generateCharReactionExpression(({ $self, $table }, field) => {
                  const row = $table.getRowByIndex($self.index)
                  // 待报价/已撤回 && 接受报价中 && 允许报价
                  field.visible = ['DRAFT', 'WITHDRAW'].includes(row.orderStatus) &&
                    row.extProjectStatus === 'ACCEPT_ORDER' &&
                    row.canOrder === 'Y'
                }),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ row }) => {
                    $openDetailTab('quote', row)
                  }, true)
                }
              },
              // 撤回
              delete: {
                type: 'void',
                title: i18nExpression('bidMod.withdraw'),
                'x-reactions': generateCharReactionExpression(({ $self, $table }, field) => {
                  const row = $table.getRowByIndex($self.index)
                  // 已报价 && 接受报价中 && 允许撤回
                  field.visible = row.orderStatus === 'SUBMISSION' &&
                    row.extProjectStatus === 'ACCEPT_ORDER' &&
                    row.allowWithdraw === 'Y'
                }),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(async ({ $prompt, $message, $queryEngine }, { row }) => {
                    $rollback($prompt, $message, $queryEngine, row)
                  })
                }
              },
              // 查看结果
              quoteResult: {
                type: 'void',
                // title: '查看结果',
                title: $t('bidMod.viewResults'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.orderStatus', '.currentRound'],
                  // 已报价 || 当前轮次大于1
                  `$deps[0] === $enum.SOU_ORDER_STATUS_ENUM.SUBMISSION || $deps[1] > 1`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ $form, $queryEngine }, { row }) => {
                    console.log()
                    $form.query('quoteResultDialog').get('data').viewRow = row
                    $form.query('quoteResultDialog').take(field => {
                      field.setComponentProps({ visible: true })
                    })
                  })
                }
              }
            }
          }
        })
      },

      // 查看中标结果弹窗
      ...QuoteResultDialogSegment
    }
  }
})

</script>

<template>
  <RenderEngine
    schemaKey="inquiryOrdersList"
    :schema="schema"
    :scope="scope"
    :components="{}"
  />
</template>
