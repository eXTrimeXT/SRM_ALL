<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { exportExcelSegment, RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  i18nExpression, queryFieldValueExpression, queryFieldStatePropertyExpression
} from '@meicloud/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import contractInformation from './edit-engine.vue'
// @ts-ignore
import performPlan from '@/service/modules/cmPerform/buyer/main'
import { onActivated } from 'vue-demi'

const { emitTabAdd, t: $t, app, http } = usePageHelper()

onActivated(() => {
  const { from, funName, formId, formNo } = app.$route.params
  if (from === "fromFun" && funName === 'contractPerformancePlan') {
    let row = {
      perPlanId: formId,
      perPlanNo: formNo
    }
    $detailOne('manage', row)
  }
})

const $detailOne = (type: string, row: any, isReadOnly: boolean = false) => {
  let name = row.perPlanNo || ''
  emitTabAdd({
    component: contractInformation,
    params: {
      flag: type,
      row: row,
      isReadOnly: isReadOnly
    },
    title: name ? '履约计划' + name : '新增履约计划',
    name: '履约计划' + name
  })
}

const $readOne = (row: any) => {
  $detailOne('view', row, true)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $addOne = () => {
  $detailOne('add', {})
}

const $manageOne = (row: any) => {
  $detailOne('manage', row)
}

const $rejectOne = (row: any, $queryEngine: any, $confirm: any, $message: any) => {
  $confirm($t('common.confirmAbandon'), $t('common.tips'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    performPlan.performPlan.abandon(row.perPlanId).then((res: any) => {
      $message.success(res?.message || $t('common.successAbandon'))
      $queryEngine.state.paginationManagement.refresh()
    })
  })
}

const scope = {
  $addOne,
  $readOne,
  $editOne,
  $manageOne,
  $rejectOne,
  http
}

const schema = defineSchemas({
  PerPlan: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true },
        update: true,
        delete: true,
        create: true
      }
    },
    'x-data': {
      currentRow: null
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        // 'x-hidden': true,
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ModelHead',
          '@listener': expression(`() => {
            console.log('paginationManagement ModelHead')
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          perPlanNo: {
            type: 'string',
            title: "合同履约计划单号",
            'x-query-engine-query-operator': 'contains'
          },
          buId: {
            type: 'string',
            title: "{{$t('cusEntry.vendorMod.orgName')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: false
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-component': 'QuickSearch',
            'x-component-props': {
              showKey: 'companyName',
              name: 'scc_sup_company_info',
              '@close-quicksearch': expression(`(val) => $self.value = val ? val.companyName : ''`)
            }
          },
          createdBy: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              'show-key': 'nickname',
              'prop-key': 'username',
              'name': 'scc_rbac_user_display'
            }
          },
          contractNo: {
            type: 'string',
            title: i18nExpression('contractMod.contractNo'),
            'x-query-engine-query-operator': 'contains'
          },
          contractClass: {
            type: 'string',
            title: "{{$t('contractMod.contractType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            }
          },
          creationDate: {
            type: 'date',
            title: "创建日期",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          status: {
            type: 'string',
            title: "单据状态",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_PLAN_STATUS'
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
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => $addOne()`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-cm/api-ql/PerPlan/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('PerPlan.table', 'data.columns'),
              dictCodes: {
                status: 'CONTRACT_PLAN_STATUS',
                contractClass: 'ELEM_CONTRACT_TYPE',
                currentMilestoneType: 'MILESTONE_SCHEDULE'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          perPlanId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          perPlanNo: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({ row }) => $readOne(row)`)
            },
            'x-render-table-column': {
              title: "合同履约计划单号",
              minWidth: 150,
              customRender: true
            }
          },
          processNum: {
            type: 'string',
            'x-render-table-column': {
              title: "里程碑模板编号",
              width: 150
            }
          },
          templateName: {
            type: 'string',
            'x-render-table-column': {
              title: "里程碑模板名称",
              width: 150
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CONTRACT_PLAN_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('contractMod.status')}}",
              width: 150
            }
          },
          // 业务实体
          buName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('cusEntry.vendorMod.orgName')}}",
              width: 130
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.vendorName')}}",
              minWidth: 150
            }
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.vendorCode')}}",
              minWidth: 120
            }
          },
          contractClass: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('contractMod.contractType')}}",
              width: 120
            }
          },
          contractHeadId: {
            type: 'string',
            'x-hidden': true
          },
          contractNo: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.contractNo'),
              minWidth: 150
            }
          },
          includeTaxAmount: {
            type: 'string',
            // 'x-component-props': {
            //   style: "float:right"
            // },
            'x-render-table-column': {
              title: "合同含税金额",
              width: 130,
              align: 'right'
            }
          },
          // TODO 后边需要替换成 createdUserName
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120
            }
          },
          creationDate: {
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: "{{$t('common.creationTime')}}",
              width: 150
            }
          },
          currentMilestoneType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE'
            },
            'x-render-table-column': {
              title: "当前里程碑节点",
              minWidth: 130
            }
          },
          currentNodePersonName: {
            type: 'string',
            'x-render-table-column': {
              title: "节点责任人",
              minWidth: 120
            }
          },
          currentPlanEndDate: {
            type: 'string',
            'x-render-table-column': {
              title: "里程碑计划结束日期",
              minWidth: 150
            }
          },
          extCancelBy: {
            type: 'string',
            'x-render-table-column': {
              title: "取消人",
              minWidth: 120
            }
          },
          extCancelTime: {
            type: 'string',
            'x-render-table-column': {
              title: "取消时间",
              minWidth: 120
            }
          },
          extCancelDesc: {
            type: 'string',
            'x-render-table-column': {
              title: "取消说明",
              minWidth: 120
            }
          },
          extCancelFileName: {
            type: 'string',
            'x-render-table-column': {
              title: "取消附件",
              minWidth: 120
            },
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              defaultFile: {
                fileId: '{{$table.getRowByIndex($self.index).extCancelFileId}}',
                fileName: '{{$self.value}}'
              },
              readonly: true
            }
          },
          extCancelFileId: {
            type: 'string',
            'x-hidden': true
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: "{{$t('common.operation')}}",
              width: 170,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              view: {
                type: 'void',
                title: "审批",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['SUBMITTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $readOne(row)`)
                }
              },
              management: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['IN_PERFORMANCE', 'APPROVED'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $manageOne(row)`)
                }
              },
              reject: {
                type: 'void',
                title: "废弃",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['REJECTED'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $rejectOne(row, $queryEngine, $confirm, $message)`)
                }
              },
              cancel: {
                type: 'void',
                title: "取消",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `!['CANCEL'].includes($deps[0])`
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('cm:plan:cancel'),
                  '@click': expression(`({ row }) => {
                    $form.query('PerPlan').get('data').currentRow = row
                    $form.query('cancelDialog').take().setComponentProps({visible:true})
                    setTimeout(() => {
                      $form.query('extCancelDesc').take().value = ''
                      $form.query('extCancelFileName').take().value = ''
                      $form.query('extCancelFileId').take().value = null
                    }, 100)
                  }`)
                }
              },
              pay: {
                type: 'void',
                title: "{{$t('bidMod.management')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['COMPLETE_PERFORMANCE'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $readOne(row)`)
                }
              },
              edit: {
                type: 'void',
                title: "编辑",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['REJECTED', 'DRAFT', 'WITHDRAW'].includes($deps[0])`
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => $editOne(row)`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression(`({ row }) => $queryEngine.request.delete(row.perPlanId)
                    .then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                `)
                }
              }
            }
          }
        })
      },
      cancelDialog: {
        type: 'void',
        title: i18nExpression('common.cancel'),
        'x-component': 'RDialog',
        'x-component-props': {
          beforeClose: expression(`(done, type, closeLoading) => {
            if (type === 'ok') {
              const extCancelDesc = $form.query('extCancelDesc').take().value
              const extCancelFileName = $form.query('extCancelFileName').take().value
              const extCancelFileId = $form.query('extCancelFileId').take().value
              const row = $form.query('PerPlan').get('data').currentRow
              http({
                url: '/api-cm/perPlan/ext/cancel',
                method: 'POST',
                data: {
                  ...row,
                  extCancelDesc,
                  extCancelFileName,
                  extCancelFileId
                },
                loading: true
              }).then(() => {
                $queryEngine.state.paginationManagement.refresh()
                done()
              }).catch(() => {
                closeLoading()
              })
            } else {
              done()
            }
          }`)
        },
        properties: {
          extCancelDesc: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extCancelDesc'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              type: 'textarea'
            }
          },
          extCancelFileName: {
            type: 'string',
            'x-hidden': true
          },
          extCancelFileId: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extCancelFileName'),
            'x-decorator': 'FormItem',
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              readonly: false,
              defaultFile: {
                fileId: expression('$self.value'),
                fileName: expression('$self.query(\'.extCancelFileName\').take().value')
              },
              '@on-change': expression(`({ file }) => {
                const { fileId, fileName } = file || {}
                $self.value = fileId || null
                $self.query('.extCancelFileName').take().value = fileName || ''
              }`)
            }
          }
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine schemaKey="PerPlan" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
