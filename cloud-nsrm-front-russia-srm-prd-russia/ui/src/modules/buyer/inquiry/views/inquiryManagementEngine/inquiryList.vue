<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  i18nExpression,
  changeFieldVisibleByDeps,
  generateCharFunctionExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  SOU_APPROVAL_STATUS_ENUM,
  SOU_PROJECT_STATUS_ENUM,
  SOU_SCORE_RULE_TYPE_ENUM
} from 'lib@/compositionEngine/sourcing/enum'
import inquiryDetail from './inquiryDetail.vue'
import inquiryTrackingDetail from './inquiryTrackingDetail.vue'

const { emitTabAdd, t: $t } = usePageHelper()

// 声明询价对象
interface SouRow {
  souNo?: string;
  projectId?: string;
}

/* 打开详情页签 */
const $openDetailTab = (type: any, row: SouRow = {}) => {
  const { souNo = '' } = row
  const map = new Map([
    // 新增
    [
      'add',
      {
        component: inquiryDetail,
        params: {
          flag: 'add',
          readonly: false,
          tabName: 'inquiryDetail'
        },
        title: $t('inquiryBySimple.addInquiryBySimple'),
        name: 'inquiryDetail'
      }
    ],
    // 编辑
    [
      'edit',
      {
        component: inquiryDetail,
        params: {
          flag: 'edit',
          readonly: false,
          row: row,
          tabName: `inquiryEdit${souNo}`
        },
        title: souNo,
        name: `inquiryEdit${souNo}`
      }
    ],
    // 查看
    [
      'view',
      {
        component: inquiryDetail,
        params: {
          flag: 'view',
          readonly: true,
          row: row,
          tabName: `inquiryDetail${(souNo)}`
        },
        title: souNo,
        name: `inquiryDetail${souNo}`
      }
    ],
    // 询价管理
    [
      'manage',
      {
        component: inquiryTrackingDetail,
        params: {
          flag: 'manage',
          row: row,
          tabName: `inquiryManage${souNo}`
        },
        title: souNo,
        name: `inquiryManage${souNo}`
      }
    ]
  ])

  emitTabAdd(map.get(type))
}

const scope = {
  $openDetailTab,
  // 字典枚举
  $enum: {
    SOU_APPROVAL_STATUS_ENUM,
    SOU_PROJECT_STATUS_ENUM,
    SOU_SCORE_RULE_TYPE_ENUM
  }
}

const schema = defineSchemas({
  InqSouProjectForBuyer: {
    type: 'void',
    'x-query-engine': {
      service: 'sou',
      actions: {
        paginationQuery: {
          action: 'listProjects',
          immediate: true,
          // 添加额外查询字段
          transformRequest: generateCharFunctionExpression((data) => {
            if (!data.payload.filter) {
              data.payload.filter = {
                souType: { eq: 'inq' }
              }
            } else {
              data.payload.filter.souType = { eq: 'inq' }
            }

            return data
          }, true)
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
          // 询价标题
          souName: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryTitle'),
            'x-component-props': {
              clearable: true
            }
          },
          // 询价单号
          souNo: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryNo'),
            'x-component-props': {
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
          },
          // 评分规则
          scoreRuleType: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryRule'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_SCORE_RULE_TYPE',
              filterItem: [generateCharFunctionExpression(({ $enum }) => $enum.SOU_SCORE_RULE_TYPE_ENUM.COMPOSITE_PRICE)],
              clearable: true
            }
          },
          // 创建人
          createdId: {
            type: 'string',
            title: i18nExpression('bidMod.creator'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              propKey: 'userId',
              name: 'scc_rbac_user_display',
              clearable: true
            }
          },
          // 审批状态
          createApprovalStatus: {
            type: 'string',
            title: i18nExpression('bidMod.auditStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_APPROVAL_STATUS',
              clearable: true
            }
          }
        })
      },

      // 工具栏
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          // 新增
          add: {
            type: 'void',
            title: i18nExpression('inquiryBySimple.addInquiryBySimple'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': generateCharFunctionExpression(() => {
                $openDetailTab('add')
              }, true)
            }
          }
        }
      },

      // 表格
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          // 询价单号
          souNo: {
            type: 'string',
            'x-component': 'RButton',
            'x-component-props': {
              type: 'text',
              '@click': generateCharFunctionExpression(({ $self, $table }) => {
                if ($self.value) {
                  $openDetailTab('view', $table.getRowByIndex($self.index))
                }
              })
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
              minWidth: 160
            }
          },
          // 评分规则
          scoreRuleType: {
            type: 'string',
            title: i18nExpression('bidMod.inquiryRule'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_SCORE_RULE_TYPE'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          // 单据状态
          'inqSouProject.extProjectStatus': {
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
          // 审核状态
          createApprovalStatus: {
            type: 'string',
            title: i18nExpression('bidMod.auditStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_APPROVAL_STATUS'
            },
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 报价方式
          orderWay: {
            type: 'string',
            title: i18nExpression('bidMod.quoteRule'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SOU_ORDER_WAY'
            },
            'x-render-table-column': {
              minWidth: 110
            }
          },
          // 轮次
          currentRound: {
            type: 'string',
            title: i18nExpression('bidMod.bidingRound'),
            'x-render-table-column': {
              minWidth: 80
            }
          },
          // 这三个字段是为了自动转换
          round: {
            type: 'string',
            // 'x-query-engine-relation': 'souRoundList',
            'x-hidden': true
          },
          inviteCount: {
            type: 'string',
            // 'x-query-engine-relation': 'souRoundList',
            'x-hidden': true
          },
          orderCount: {
            type: 'string',
            // 'x-query-engine-relation': 'souRoundList',
            'x-hidden': true
          },
          // 报价回应
          quoteCnt: {
            type: 'string',
            'x-query-engine-skip': true,
            title: i18nExpression('bidMod.quoteCnt'),
            'x-render-table-column': {
              minWidth: 110
            },
            'x-reactions': generateCharFunctionExpression(({ $table, $self }) => {
              const row = $table.getRowByIndex($self.index)
              if (!row) return
              $self.setValue((row?.orderCount || 0) + ' / ' + (row?.inviteCount || 0))
            })
          },
          // 创建人
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.creator'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 创建时间
          creationDate: {
            type: 'string',
            title: i18nExpression('common.creationTime'),
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              minWidth: 160
            }
          },
          // 发布时间
          publishTime: {
            type: 'string',
            title: i18nExpression('bidMod.releaseDatetime'),
            'x-render-table-column': {
              minWidth: 160
            }
          },
          // 报价截止时间
          orderEndTime: {
            type: 'string',
            title: i18nExpression('bidMod.deadline'),
            'x-render-table-column': {
              minWidth: 160
            }
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
              // 编辑
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus', '.createApprovalStatus'],
                  // 单据状态拟定 && 审核状态拟定
                  `$deps[0] === $enum.SOU_PROJECT_STATUS_ENUM.DRAFT && $deps[1] === $enum.SOU_APPROVAL_STATUS_ENUM.DRAFT`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ row }) => {
                    $openDetailTab('edit', row)
                  }, true)
                }
              },
              // 删除
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus'],
                  `$deps[0] === $enum.SOU_PROJECT_STATUS_ENUM.DRAFT`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(async ({ $queryEngine, $message, $confirm }, { row }) => {
                    const confirmResult = await $confirm($t('common.delRow'), {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning'
                    }).catch(() => { /* nothing */ })

                    if (confirmResult !== 'confirm') {
                      return
                    }

                    const response = await $queryEngine.request.baseRequest({
                      action: 'removeSou',
                      payload: [{ projectId: row.projectId, souType: 'inq' }]
                    }).catch(() => {})

                    if (response) {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    }
                  })
                }
              },
              // 询价管理
              management: {
                type: 'void',
                title: '询价管理',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus'],
                  // 单据状态 非拟定 非取消
                  `!([$enum.SOU_PROJECT_STATUS_ENUM.DRAFT, $enum.SOU_PROJECT_STATUS_ENUM.CANCEL].includes($deps[0]))`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ row }) => {
                    $openDetailTab('manage', row)
                  }, true)
                }
              },
              // 审批
              approval: {
                type: 'void',
                title: i18nExpression('common.approve'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.createApprovalStatus'],
                  // 单据状态拟定
                  `$deps[0] === $enum.SOU_APPROVAL_STATUS_ENUM.SUBMITTED`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ row }) => {
                    $openDetailTab('view', row)
                  }, true)
                }
              },
              // 取消
              cancel: {
                type: 'void',
                title: i18nExpression('common.cancel'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus'],
                  // 单据状态 非拟定、非取消、非已定价
                  `!([
                    $enum.SOU_PROJECT_STATUS_ENUM.DRAFT,
                    $enum.SOU_PROJECT_STATUS_ENUM.CANCEL,
                    $enum.SOU_PROJECT_STATUS_ENUM.PRICE_END
                   ].includes($deps[0]))`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(async ({ $queryEngine, $message, $confirm }, { row }) => {
                    const confirmResult = await $confirm('确认取消项目吗？', {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning'
                    }).catch(() => { /* nothing */ })

                    if (confirmResult !== 'confirm') {
                      return
                    }

                    const response = await $queryEngine.request.baseRequest({
                      action: 'cancelSou',
                      payload: [
                        {
                          projectId: row.projectId,
                          souType: 'inq',
                          cancelReason: ''
                        }
                      ],
                      query: { '*': {} }
                    }).catch(() => {})

                    if (response) {
                      $message.success('取消项目成功')
                      $queryEngine.state.paginationManagement.refresh()
                    }
                  })
                }
              },
              // 查看
              view: {
                type: 'void',
                title: i18nExpression('common.view'),
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus'],
                  // 单据状态拟定
                  `$deps[0] === $enum.SOU_PROJECT_STATUS_ENUM.CANCEL`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(({ row }) => {
                    $openDetailTab('view', row)
                  }, true)
                }
              },
              // 复制询价单
              copy: {
                type: 'void',
                title: '复制询价单',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.inqSouProject.extProjectStatus'],
                  // 单据状态 非拟定、非取消、非已定价
                  `![$enum.SOU_PROJECT_STATUS_ENUM.DRAFT, $enum.SOU_PROJECT_STATUS_ENUM.CANCEL].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': generateCharFunctionExpression(async ({ $queryEngine, $message, $confirm }, { row }) => {
                    const confirmResult = await $confirm('确认复制该询价单吗？', {
                      confirmButtonText: $t('common.confirm'),
                      cancelButtonText: $t('common.cancel'),
                      type: 'warning'
                    }).catch(() => { /* nothing */ })

                    if (confirmResult !== 'confirm') {
                      return
                    }

                    const response = await $queryEngine.request.baseRequest({
                      action: 'copySou',
                      loading: true,
                      payload: [
                        {
                          projectId: row.projectId,
                          souType: 'inq'
                        }
                      ],
                      query: {
                        '*': {}
                      },
                      tree: true
                    }).catch(() => {})

                    if (response) {
                      if (Array.isArray(response.data) && response.data.length === 1) {
                        const { projectId, souNo } = response.data[0] || {}
                        if (projectId && souNo) {
                          $message.success($t('logisticsMod.copySuccess'))
                          $queryEngine.state.paginationManagement.refresh()
                          $openDetailTab('edit', { souNo, projectId })
                        } else {
                          $message.error('无法获取复制的询价单')
                          $queryEngine.state.paginationManagement.refresh()
                        }
                      } else {
                        $message.error('无法获取复制的询价单')
                        $queryEngine.state.paginationManagement.refresh()
                      }
                    }
                  })
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

<template>
  <RenderEngine
    schemaKey="inquiryList"
    :schema="schema"
    :scope="scope"
    :components="{}"
  />
</template>
