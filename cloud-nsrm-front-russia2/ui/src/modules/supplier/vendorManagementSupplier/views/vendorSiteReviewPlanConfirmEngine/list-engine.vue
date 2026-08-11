<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldValueExpression, queryFieldStatePropertyExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import Cselect from '@/library/components/c-select/dict-select'
import { DialogMainCategory } from './components/categoryDialog'
import sitereviewplanconfirmEdit from './edit-engine.vue'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { emitTabAdd, app } = usePageHelper()

onActivated(() => {
  const params = app.$route.params
  if (params.from === "fromFun" && app.$route?.params.taskIndex === 2) {
    let row = {
      planConfirmId: app.$route?.params?.formId,
      planConfirmCode: app.$route?.params?.formNo
    }
    const tab = {
      component: sitereviewplanconfirmEdit,
      params: {
        readOnly: true,
        row,
        flag: 'view'
      },
      title: row.planConfirmCode, // 计划落实管理查看
      name: 'sitereviewplanconfirmEdit' + row.planConfirmId
    }
    emitTabAdd(tab)
  }
})

const schema = defineSchemas({
  SiteReviewPlanConfirm: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
          paginationQuery: {
            transformRequest: expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              return data
            }`),
            onSuccess: expression(`(res) => {
              console.log('read:::',$form.values.table)
              setTimeout(() => {
                $form.values.table = []
                $form.values.table = res.data
              })
            }`)
          }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'SiteReviewPlanConfirm',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          immediateQueryForm: true
        },
        properties: generateXindexInOrder({
          planName: {
            type: 'string',
            title: "{{$t('vendorMod.planName')}}" // 计划名称
          },
          planType: {
            type: 'string',
            title: "{{$t('vendorMod.planType')}}", // 计划类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planType'
            }
          },
          categoryCode: {
            type: 'string',
            title: "{{$t('common.categoryName')}}", // 品类名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'categoryName',
              propKey: 'categoryCode',
              name: 'scc_base_purchase_category2'
            }
          },
          orgId: {
            type: 'string',
            title: "{{$t('vendorMod.orgId')}}", // 采购组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              multiple: false
            }
          },
          planConfirmCode:{
            type: 'string',
            title: "{{$t('vendorMod.planCode')}}", // 计划编码
            'x-query-engine-query-operator': 'contains',
            'x-reactions': {
              effects: ['onFieldInit'],
              fulfill: {
                state: {
                  value: expression(`app.$route?.params.from === 'fromFun' && app.$route?.params.taskIndex === 1 ? app.$route?.params?.formNo : ''`)
                }
              }
            }
          },
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        properties: {
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-sup/sup/sitereviewplanconfirm/listPage",
              exportMode: "front",
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('SiteReviewPlanConfirm.table', 'data.columns'),
              dictCodes: {
                planType: 'planType',
                approveStatus: 'approveStatus'
              }
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true,
          editMode: 'multi-row'
        },
        properties: generateXindexInOrder({
          creationDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          planConfirmId: {
            type: 'string',
            'x-hidden': true
          },
          orgName: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('vendorMod.orgName')}}", // 采购组织
            'x-render-table-column': {
              width: 100,
              skipEditable: true
            }
          },
          categoryName: {
            title: "{{$t('vendorMod.viewCategory')}}", // 品类
            'x-component': 'TableButton',
            'x-query-engine-skip': true,
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $form.query('DialogCategory').take().setComponentProps({ visible: true })
                $form.query('*.DialogCategory.siteReviewPlanCategoryList').take((field) => {
                  field.reset()
                })
                $queryEngine.request.baseRequest({
                  action: "read",
                  payload: [row.siteReviewPlanId],
                  type: "SiteReviewPlan",
                  query: {
                    "siteReviewPlanCategorys":{
                       "*":{}
                    }
                  }
                }).then((res) => {
                  console.log(res)
                  $form.query('*.DialogCategory.categoryList').take().setValue(res.data[0].siteReviewPlanCategorys)
                }).catch((err) => {
                  console.log(err)
                  $message.error(err.message)
                })
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.categoryName'),
              minWidth: 100,
              skipEditable: true,
              customRender: true
            }
          },
          planType: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('vendorMod.planType')}}", // 计划类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planType'
            },
            'x-render-table-column': {
              width: 100,
              skipEditable: true
            }
          },
          planName: {
            type: 'string',
            title: "{{$t('vendorMod.planName3')}}", // 关联计划
            'x-render-table-column': {
              width: 100,
              skipEditable: true
            }
          },
          personList: {
            title: "{{$t('vendorMod.check')}}",
            'x-component': 'TableButton',
            'x-query-engine-skip': true,
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                  const tab = {
                  component: sitereviewplanconfirmEdit,
                  params: {
                    readOnly: true,
                    row,
                    flag: 'view'
                  },
                  title: $t('vendorMod.planConfirmCheck'), // 计划落实管理查看
                  name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.personList'), // 工作小组成员
              minWidth: 130,
              customRender: true,
              skipEditable: true
            }
          },
          vendorContact: {
            type: 'string',
            title: "{{$t('vendorMod.vendorContact')}}", // 供应商联系人
            'x-render-table-column': {
              width: 130
            }
          },
          vendorContactTel: {
            type: 'string',
            title: "{{$t('vendorMod.vendorContactTel')}}", // 供应商联系电话
            'x-render-table-column': {
              width: 130
            },
            'x-reactions': expression(`() => {
              setTimeout(() => {
                const values = $form.query('.table')?.take()?.value
                values?.forEach((e,rowIndex) => {
                  if (e.approveStatus == 'RELEASED') {
                    $table.editRowByIndex(rowIndex)
                  }
                })
              })
            }`)
          },
          planSetOutTime: {
            title: "{{$t('vendorMod.planSetOutTime')}}", // 计划出发时间
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.planSetOutTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130,
              skipEditable: true
            }
          },
          planVisitTime: {
            title: "{{$t('vendorMod.planVisitTime')}}", // 计划到访时间
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.planVisitTime, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130,
              skipEditable: true
            }
          },
          visitDays: {
            type: 'string',
            title: "{{$t('vendorMod.visitDays')}}", // 计划到访天数
            'x-render-table-column': {
              width: 130,
              skipEditable: true
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.status')}}", // 状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'approveStatus'
            },
            'x-render-table-column': {
              width: 120,
              skipEditable: true
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              confirm: {
                type: 'void',
                title: i18nExpression('components.common.confirm'), // 确认
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['RELEASED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row, rowIndex }) => {
                    let rows = row
                    const vendorContact = rows.vendorContact
                    const vendorContactTel = rows.vendorContactTel
                    if (!vendorContact || !vendorContactTel || vendorContact == '' || vendorContactTel == '') {
                      app.$message.error($t('vendorMod.supplierPhoneEmpty'))
                      return false
                    }
                    rows.approveStatus = 'VENDOR_CONFIRMED'
                    $queryEngine.request.save(rows).then((res) => {
                      $table.cancelEditRow(rowIndex)
                      $message.success($t('common.successConfirm'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              refuse: {
                type: 'void',
                title: i18nExpression('common.toRefuse'), // 驳回
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['RELEASED'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row, rowIndex }) => {
                    let rows = row
                    const vendorContact = rows.vendorContact
                    const vendorContactTel = rows.vendorContactTel
                    if (!vendorContact || !vendorContactTel || vendorContact == '' || vendorContactTel == '') {
                      app.$message.error($t('vendorMod.supplierPhoneEmpty'))
                      return false
                    }
                    rows.approveStatus = 'VENDOR_REJECT'
                    $queryEngine.request.save(rows).then((res) => {
                      $table.cancelEditRow(rowIndex)
                      $message.success($t('agentOnlineInvoice.prompt5'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              }
            }
          }
        })
      },
      DialogCategory: {
        ...DialogMainCategory
      }
    }
  }
})

const scope = {
  emitTabAdd,
  app,
  sitereviewplanconfirmEdit,
}

const components = {
  Cselect
}
</script>

<template>
  <RenderEngine schemaKey="vendorSiteReviewPlanConfirmList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
