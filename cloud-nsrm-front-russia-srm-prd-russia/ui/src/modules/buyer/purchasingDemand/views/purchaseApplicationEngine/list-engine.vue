<script setup lang="ts">
// @ts-ignore
import { onActivated } from 'vue-demi'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
// @ts-ignore
import purchaseApplicationDetail from './edit-engine'
// @ts-ignore
import { parseTime } from '@/utils'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'

const { emitTabAdd, t, app, http, confirmMessage, getGlobalNickname } = usePageHelper()
console.log(getGlobalNickname(), 'getGlobalNickname')

onActivated(() => {
  console.log('onActivated')
  let { from, funName, fdSubject } = app.$route.params
  if (
    from === 'fromFun' &&
    funName === 'purchaseApplication'
  ) {
    const requirementHeadId = Number(app.$route.params.formId)
    // 流程标题
    const formNo = app.$route.params.formNo
    const row = {
      ...app.$route.params,
      requirementHeadId,
      // tab 标题显示
      requirementHeadNum: formNo
    }
    $openDetailTag('approveNumber', row)
  }
  if (from === 'demandPoolManagement' && funName === 'purchaseApplication') {
    const row = {
      ...app.$route.params,
      requirementHeadId: fdSubject.requirementHeadId,
      requirementHeadNum: fdSubject.requirementHeadNum
    }
    $openDetailTag('approveNumber', row)
  }
})

const $openDetailTag = (type: string, row: any | {}) => {
  const mapInfo = new Map([
    // 编辑
    [
      'edit',
      {
        component: purchaseApplicationDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'purchaseApplicationDetail' + row?.requirementHeadNum
        },
        title: row?.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row?.requirementHeadNum
      }
    ],
    // 审批
    [
      'approvalOnly',
      {
        component: purchaseApplicationDetail,
        params: {
          flag: 'approvalOnly',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row?.requirementHeadNum,
          activeWorkflowTab: true
        },
        title: row?.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row?.requirementHeadNum
      }
    ],
    // 创建
    [
      'add',
      {
        component: purchaseApplicationDetail,
        params: {
          flag: 'add',
          tabName: 'purchaseApplicationDetail'
        },
        // 创建采购申请单
        title: t('purchaseDemand.addPurApplication'),
        name: 'purchaseApplicationDetail'
      }
    ],
    // 编号进入
    [
      'approveNumber',
      {
        component: purchaseApplicationDetail,
        params: {
          flag: 'approveNumber',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row?.requirementHeadNum
        },
        title: row?.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row?.requirementHeadNum
      }
    ]
  ])

  emitTabAdd(mapInfo.get(type))
}

// 审批通过
const $approvalOneItem = (row: any, $queryEngine: any) => {
  http({
    url: '/api-sup-ce/pr/requirementHead/approval',
    method: 'GET',
    params: { requirementHeadId: row.requirementHeadId },
    loading: true
  })
    .then(() => {
      // 操作成功
      app.$message.success(t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
}

// 废弃
const $abandonOne = async (row: any, $queryEngine: any) => {
  // 确认作废这条数据
  const confirmSelectValue = await confirmMessage(t('common.confirmAbandon'))
  if (confirmSelectValue !== 'confirm') return

  $queryEngine.request.baseRequest({
    action: 'abandonRequirement',
    payload: [{ requirementHeadId: row.requirementHeadId }],
    query: { '*': {} }
  })
    .then(() => {
      app.$message.success(t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
}

const $budgetRelease = async (row: any, $queryEngine: any) => {
  // 确认释放预算吗
  const confirmSelectValue = await confirmMessage(t('purchaseDemand.sureReleaseBugget'))
  if (confirmSelectValue !== 'confirm') return
  $queryEngine.request.baseRequest({
    type: 'PrRequirementForBuyer',
    action: 'releaseBudget',
    payload: [{ requirementHeadId: row.requirementHeadId }],
    query: { '*': {} }
  })
    .then(() => {
      app.$message.success(t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
}

const integrationMode = (async () => {
  let { data } = await app.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'MQL_PR_REQUIREMENT_INIT' })
  return data
})()

const listQueryTodo = async () => {
  let { data } = await app.$api.base.flowAPI.queryTodo({ businessType: 'MQL_PR_REQUIREMENT_INIT' })
  return data
}

const scope = {
  $t: t,
  app,
  parseTime,
  purchaseApplicationDetail,
  $approvalOneItem,
  $abandonOne,
  $budgetRelease,
  integrationMode,
  listQueryTodo,
  getGlobalNickname,
  $openDetailTag
}

const components = {
  purchaseApplicationDetail
}

const schema = defineSchemas({
  PrRequirementForBuyer: {
    type: 'void',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'listRequirements',
          onSuccess: expression(`async (res) => {
            let mode = await integrationMode
            let tableData = res.data
            tableData.forEach(item => (item.integrationMode = mode))

            if (app.notSearchTodoMode.includes(mode)) {
              $form.values.materialDetail = tableData
              return
            }

            let queryTodoList = await listQueryTodo()

            const maps = []
            queryTodoList.forEach(item => maps.push(item.businessId))
            tableData.forEach(row => {
              let tempId = String(row.requirementHeadId)
              if (maps.includes(tempId)) {
                row.workflowAuditStatus = 'WAIT'
                row.arroverId = tempId
              }
            })

            $form.values.materialDetail = tableData
          }`)
        }
      }
    },
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'PrRequirementForBuyer',
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
          // 采购需求编号(申请编号)
          requirementHeadNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.requirementHeadNum')
          },
          // 采购类型
          ceeaPurchaseType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            }
          },
          // 单据状态
          auditStatus: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applyStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVAL_STATUS'
            }
          },
          // 业务实体
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1
            }
          },
          // 库存组织
          organizationId: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': '{{$form.values.query.orgId}}',
              'node-type': 'INV',
              'scope': '{{ $form.values.query }}'
            }
          },
          // 采购项目
          purchaseProject: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseItem')
          },
          // 申请日期
          applyDate: {
            type: 'date',
            title: i18nExpression('purchaseDemand.applyDate'),
            'x-component-props': {
              type: 'daterange',
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd'
            },
            'x-query-engine-query-operator': 'between'
          },
          // 申请部门
          ceeaDepartmentId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ceeaDepartment'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'descr',
              propKey: 'deptid',
              name: 'ceea_base_dept'
            }
          },
          // 申请人
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applicant'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'nickname',
              name: 'scc_rbac_user_display',
              '@close-quicksearch': expression(`(val) => {
                $self.value = val ? val.nickname : ''
              }`)
            }
          },
          // 物料大类
          categoryId: {
            type: 'number',
            title: i18nExpression('purchaseDemand.materialCate'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'categoryName',
              propKey: 'categoryId',
              name: 'scc_base_purchase_category3'
            }
          },
          // 需求类型
          demandType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.demandType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMAND_TYPE'
            }
          },
          // 预算编号
          budgetManagementId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'budgetManagementNumber',
              propKey: 'budgetManagementId',
              name: 'scc_pb_budget_management_effective'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-query-engine-skip': true,
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $openDetailTag('add')
              }`)
            }
          }
        }
      },
      materialDetail: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true,
          editMode: 'multi-row'
        },
        properties: {
          requirementHeadId: {
            // 主键ID
            type: 'string',
            'x-hidden': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          requirementHeadNum: {
            // 采购需求编号(申请编号)
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression(`({ row }) => {
                $openDetailTag('approveNumber', row)
              }`)
            },
            'x-render-table-column': {
              width: 150,
              title: i18nExpression('purchaseDemand.requirementHeadNum')
            }
          },
          // 预算编号
          budgetManagementNum: {
            type: 'string',
            title: i18nExpression('purchaseDemand.budgetNumber'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 需求类型
          demandType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.demandType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DEMAND_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          // 采购类型
          ceeaPurchaseType: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PURCHASE_TYPE'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          // 单据状态
          auditStatus: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applyStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVAL_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          // 申请日期
          applyDate: {
            type: 'date',
            title: i18nExpression('purchaseDemand.applyDate'),
            'x-component-props': {
              format: 'yyyy-MM-dd'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          // 业务实体
          orgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 库存组织
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 采购项目
          purchaseProject: {
            type: 'string',
            title: i18nExpression('purchaseDemand.purchaseItem'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 申请部门
          ceeaDepartmentName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.ceeaDepartment'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 申请人
          createdFullName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.applicant'),
            'x-render-table-column': {
              width: 100
            }
          },
          // 物料大类
          categoryName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.materialCate'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 备注
          comments: {
            type: 'string',
            title: i18nExpression('common.remark'),
            'x-render-table-column': {
              width: 120
            }
          },
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          sourceSystem: {
            type: 'string',
            'x-hidden': true
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 130,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              // 编辑
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    $openDetailTag('edit', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      ['DRAFT', 'WITHDRAW', 'REJECTED'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`
              },
              // 审批
              approval: {
                type: 'void',
                title: i18nExpression('common.approve'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    $openDetailTag('approvalOnly', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      app.flowWithTabMode.includes($table.getRowByIndex($self.index).integrationMode) &&
                      (
                        $table.getRowByIndex($self.index).auditStatus === 'SUBMITTED' ||
                        (
                          $table.getRowByIndex($self.index).auditStatus === 'APPROVING' &&
                          !!$table.getRowByIndex($self.index).arroverId
                        )
                      )
                  }
                }}`
              },
              // 审批通过
              approvalPass: {
                type: 'void',
                title: i18nExpression('purchaseDemand.approved'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                      $approvalOneItem(row, $queryEngine)
                    }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      ['SUBMITTED', 'APPROVING'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      app.srmFlowMode.includes($table.getRowByIndex($self.index).integrationMode) &&
                      !$table.getRowByIndex($self.index).workflowAuditStatus
                  }
                }}`
              },
              // 删除
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression(`() => {
                    const row = $table.getRowByIndex($self.index)
                    console.log(row,'row')

                    $queryEngine.request.baseRequest({
                      action: 'removeRequirement',
                      payload: [{ requirementHeadId: row.requirementHeadId }],
                      query: { '*': {} }
                    })
                      .then((res) => {
                        $message.success($t('common.successDelete'))
                        $queryEngine.state.paginationManagement.refresh()
                      })
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      ['DRAFT'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`
              },
              // 废弃
              abandon: {
                type: 'void',
                title: i18nExpression('common.abandon'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                      $abandonOne(row, $queryEngine)
                    }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      ['WITHDRAW', 'REJECTED', 'REFUSED', 'UNDER_APPROVAL'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`
              },
              // 预算释放 条件（已审批或者已废弃、非生产性需求、剩余可用金额大于0）
              budgetRelease: {
                type: 'void',
                title: i18nExpression('purchaseDemand.budgetRelease'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                      $budgetRelease(row, $queryEngine)
                    }`)
                },
                'x-reactions': `{{
                  (field) => {
                    field.visible =
                      ['APPROVED', 'ABANDONED'].includes($table.getRowByIndex($self.index).auditStatus) &&
                      $table.getRowByIndex($self.index).demandType === 'NONPRODUCTIVE_DEMAND' &&
                      $table.getRowByIndex($self.index).unusedBudget > 0 &&
                      (
                        $table.getRowByIndex($self.index).createdBy === getGlobalNickname() ||
                        $table.getRowByIndex($self.index).sourceSystem === 'MRP'
                      )
                  }
                }}`
              }
            }
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="PrRequirementForBuyerListPage" />
</template>
