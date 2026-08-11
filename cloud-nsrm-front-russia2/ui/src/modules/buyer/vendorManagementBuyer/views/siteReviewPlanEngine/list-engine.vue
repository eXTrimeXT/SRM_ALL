<!-- eslint-disable quotes -->
<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  queryFieldValueExpression, queryFieldStatePropertyExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import {exportExcelSegment, RenderEngine} from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import Cselect from '@/library/components/c-select/dict-select'
import { DialogMain } from './components/dialog'
import { DialogMainCategory } from './components/categoryDialog'
import siteReviewPlanConfirm from 'modb@/vendorManagementBuyer/views/siteReviewPlanConfirmEngine/edit-engine'
import siteAssessment from 'modb@/vendorManagementBuyer/views/siteAssessmentEngine/siteAssessmentDetailEngine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  SiteReviewPlan: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
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
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          vendorId: {
            type: 'string',
            title: "{{$t('vendorMod.vendorId')}}", // 供应商名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
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
          planType: {
            type: 'string',
            title: "{{$t('vendorMod.planType')}}", // 计划类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_ASSESSMENT_TYPE'
            }
          },
          categoryCode: {
            type: 'string',
            title: "{{$t('vendorMod.categoryCode')}}", // 品类名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'categoryName',
              propKey: 'categoryCode',
              name: 'scc_base_purchase_category2'
            }
          },
          planStatus: {
            type: 'string',
            title: "{{$t('vendorMod.planStatus')}}", // 计划状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planStatus'
            }
          },
          planProcessStatus: {
            type: 'string',
            title: "{{$t('vendorMod.planProcessStatus')}}", // 计划状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planProcessStatus'
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
              '@click': expression(`() => {
                $form.query('Dialog').take(field => {
                  field.title = $t('vendorMod.siteReviewProgramManagementAdded')
                  field.component[1].visible = true
                })
                setTimeout(() => {
                  $form.query('*.*.Dialog.form').take((field) => {
                    field.reset()
                  })
                  $form.query('*.*.Dialog.siteReviewPlanCategorys').take((field) => {
                    field.reset()
                  })
                })
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...exportExcelSegment, // 需要先引入 -》 import { exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
              type: 'default',
              pageUrl: "/api-sup/api-ql/SiteReviewPlan/query", // meiql 接口
              tableHeader: queryFieldStatePropertyExpression('SiteReviewPlan.table', 'data.columns'),
              dictCodes: {
                planType: 'CEEA_ASSESSMENT_TYPE',
                planStatus: 'planStatus',
                planProcessStatus: 'planProcessStatus'
              }
            }
          },
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          siteReviewPlanId: {
            type: 'string',
            'x-hidden': true
          },
          planConfirmId: {
            type: 'string',
            'x-hidden': true
          },
          siteFormId: {
            type: 'string',
            'x-hidden': true
          },
          planName: {
            type: 'string',
            title: "{{$t('vendorMod.planName')}}", // 计划名称
            'x-render-table-column': {
              minWidth: 100
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('vendorMod.vendorName')}}", // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('vendorMod.vendorCode')}}", // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orgName: {
            type: 'string',
            title: "{{$t('vendorMod.orgName')}}", // 采购组织
            'x-render-table-column': {
              width: 100
            }
          },
          categoryName: {
            title: "{{$t('vendorMod.viewCategory')}}", // 品类
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $form.query('DialogCategory').take().setComponentProps({ visible: true })
                $form.query('*.DialogCategory.siteReviewPlanCategorys').take((field) => {
                  field.reset()
                })
                $queryEngine.request.baseRequest({
                  action: "read",
                  payload: [row.siteReviewPlanId],
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
              customRender: true
            }
          },
          planType: {
            type: 'string',
            title: "{{$t('vendorMod.planType')}}", // 计划类型
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CEEA_ASSESSMENT_TYPE'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          creationDate: {
            title: "{{$t('vendorMod.creationDate2')}}", // 创建时间
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130
            }
          },
          planStartDate: {
            title: "{{$t('vendorMod.planStartDate')}}", // 计划开始时间
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.planStartDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              width: 130
            }
          },
          planProcessStatus: {
            title: "{{$t('vendorMod.planProcessStatus')}}", // 计划状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planProcessStatus'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          planConfirmCode: {
            title: "{{$table.getRowByIndex($self.index)?.planStatus == 'DRAFT' ? '' : $self.value ? $self.value : $t('vendorMod.createPlanConfirm')}}",
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                if (row.planConfirmCode == '' || row.planConfirmCode == null) {
                  const datas = {
                    siteReviewPlanId: row.siteReviewPlanId,
                    vendorName: row.vendorName,
                    orgName: row.orgName,
                    categoryName: row.categoryName,
                    planName: row.planName,
                    planType: row.planType
                  }
                  const tab = {
                    component: siteReviewPlanConfirm,
                    params: {
                      row,
                      flag: 'add',
                      datas: datas
                    },
                    title: $t('vendorMod.planConfirmManagement'), // 计划落实管理
                    name: 'siteReviewPlanConfirm'
                  }
                  emitTabAdd(tab)
                } else {
                  const tab = {
                    component: siteReviewPlanConfirm,
                    params: {
                      row,
                      flag: 'view',
                      readOnly: true,
                    },
                    title: $t('vendorMod.planConfirmManagement'), // 计划落实管理
                    name: 'siteReviewPlanConfirm'
                  }
                  emitTabAdd(tab)
                }
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.planConfirmCode'), // 计划落实单号
              minWidth: 130,
              customRender: true
            }
          },
          siteReviewCode: {
            title: "{{$table.getRowByIndex($self.index)?.planStatus == 'DRAFT' ? '' : $self.value ? $self.value : $t('vendorMod.createSiteReviewCode')}}",
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                if (row?.siteReviewCode == '' || row?.siteReviewCode == null) {
                  const tab = {
                    component: siteAssessment,
                    params: {
                      flag: 'adds',
                      row
                    },
                    title: $t('vendorMod.appraisal'), // 评审
                    name: 'siteAssessment'
                  }
                  emitTabAdd(tab)
                } else {
                  const tab = {
                    component: siteAssessment,
                    params: {
                      siteFormId: row.siteFormId,
                      flag: 'view'
                    },
                    title: $t('vendorMod.appraisal'), // 评审
                    name: 'siteAssessment'
                  }
                  emitTabAdd(tab)
                }
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('vendorMod.siteReviewCode'), // 现场评审单号
              minWidth: 130,
              customRender: true
            }
          },
          planStatus: {
            type: 'string',
            title: "{{$t('vendorMod.planStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'planStatus'
            },
            'x-render-table-column': {
              width: 120
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right',
              sortable: false
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.planStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $form.query('*.*.Dialog').take(field =>{
                      field.setComponentProps({ visible: true })
                      field.title = $t('vendorMod.siteReviewProgramManagementEditor')
                    })

                    setTimeout(() => {
                      $form.query('*.*.Dialog.form').take().setValue(row)
                      $form.query('*.*.Dialog.siteReviewPlanCategoryList').take((field) => {
                        field.reset()
                      })
                      $queryEngine.request.baseRequest({
                        action: "read",
                        payload: [row.siteReviewPlanId],
                        query: {
                          "siteReviewPlanCategorys":{
                            "*":{}
                           }
                        }
                      }).then((res) => {
                        console.log(res)
                        $form.query('*.*.Dialog.siteReviewPlanCategorys').take().setValue(res.data[0].siteReviewPlanCategorys)
                      }).catch((err) => {
                        console.log(err)
                        $message.error(err.message)
                      })
                    });
                  }`)
                }
              },
              submit: {
                type: 'void',
                title: "{{$t('common.submit')}}",
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.planStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    $form.query('*.*.Dialog').take(field => {
                      field.componentProps.visible = true
                      field.title = $t('vendorMod.siteReviewProgramManagementEditor')
                    })

                    setTimeout(() => {
                      $form.query('*.*.Dialog.form').take().setValue(row)
                      $form.query('*.*.Dialog.siteReviewPlanCategorys').take((field) => {
                        field.reset()
                      })
                      $queryEngine.request.baseRequest({
                        action: "read",
                        payload: [row.siteReviewPlanId],
                        query: {
                          "siteReviewPlanCategorys":{
                            "*":{}
                           }
                        }
                      }).then((res) => {
                        console.log(res)
                        $form.query('*.*.Dialog.siteReviewPlanCategorys').take().setValue(res.data[0].siteReviewPlanCategorys)
                      }).catch((err) => {
                        console.log(err)
                        $message.error(err.message)
                      })
                    });
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.planStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  style: `margin-left: 8px`,
                  showPopconfirm: true,
                  type: 'text',
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.siteReviewPlanId).then(() => {
                       $message.success($t('common.successDelete'))
                       $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              }
            }
          }
        })
      },
      DialogAll: {
        ...DialogMain
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
  siteReviewPlanConfirm,
  siteAssessment
}

const components = {
  Cselect
}
</script>

<template>
  <RenderEngine schemaKey="siteReviewPlanList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}

</style>
