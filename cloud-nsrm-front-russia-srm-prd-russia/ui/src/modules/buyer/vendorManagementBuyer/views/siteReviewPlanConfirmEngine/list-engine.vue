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
import { DialogMainCategory } from 'modb@/vendorManagementBuyer/views/siteReviewPlanEngine/components/categoryDialog'
import siteAssessmentDetail from 'modb@/vendorManagementBuyer/views/siteAssessmentEngine/siteAssessmentDetailEngine'
import sitereviewplanconfirmEdit from './edit-engine.vue'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  SiteReviewPlanConfirm: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
          paginationQuery: {
            immediate: true,
            transformRequest: expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              return data
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
        properties: generateXindexInOrder({
          planName: {
            type: 'string',
            title: "{{$t('vendorMod.planName')}}", // 计划名称
            'x-query-engine-query-operator': 'contains'
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
              code: 'planType'
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.planStatus')}}", // 计划状态
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'approveStatus'
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
                  const tab = {
                    component: sitereviewplanconfirmEdit,
                    params: {
                      flag: 'add'
                    },
                    title: $t('vendorMod.planConfirmAdd'), // 计划落实管理新增
                    name: 'sitereviewplanconfirmEdit'
                  }
                  emitTabAdd(tab)
              }`)
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
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          planConfirmId: {
            type: 'string',
            'x-hidden': true
          },
          planConfirmCode: {
            type: 'string',
            'x-component': 'TableButton',
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
              title: i18nExpression('vendorMod.planConfirmCode2'), // 计划落实编号
              minWidth: 120,
              customRender: true
            }
          },
          vendorName: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('vendorMod.vendorName')}}", // 供应商名称
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorCode: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('vendorMod.vendorCode')}}", // 供应商编码
            'x-render-table-column': {
              minWidth: 120
            }
          },
          orgName: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('vendorMod.orgName')}}", // 采购组织
            'x-render-table-column': {
              width: 100
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
              width: 100
            }
          },
          planName: {
            type: 'string',
            title: "{{$t('vendorMod.planName')}}", // 关联计划
            'x-render-table-column': {
              width: 100
            }
          },
          creationDate: {
            title: "{{$t('vendorMod.creationDate2')}}", // 创建时间
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
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
              customRender: true
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
            }
          },
          planSetOutTime: {
            title: "{{$t('vendorMod.planSetOutTime')}}", // 计划出发时间
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          planVisitTime: {
            title: "{{$t('vendorMod.planVisitTime')}}", // 计划到访时间
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          visitDays: {
            type: 'string',
            title: "{{$t('vendorMod.visitDays')}}", // 计划到访天数
            'x-render-table-column': {
              width: 130
            }
          },
          siteReviewCode: {
            type: 'string',
            title: "{{$t('评审单编码')}}", // 评审单编码
            'x-render-table-column': {
              width: 130
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
              width: 120
            }
          },
          rejectReason: {
            type: 'string',
            title: "{{$t('vendorMod.rejectReason')}}", // 驳回原因
            'x-render-table-column': {
              width: 120
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            properties: {
              createSiteReview: {
                type: 'void',
                'x-component': 'TableButton',
                title: "{{$t('vendorMod.createSiteReviewCode')}}", // 创建评审单
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus', '.siteReviewCode'],
                  `$deps[0] === 'PASS' && ($deps[1] == null || $deps[1] == '')`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
                      component: siteAssessmentDetail,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'adds'
                      },
                      title: $t('vendorMod.createSiteReviewCode'), // 创建评审单
                      name: 'siteAssessmentDetail'
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              edit: {
                type: 'void',
                'x-component': 'TableButton',
                title: "{{$t('common.edit')}}", // 编辑
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT','VENDOR_REJECT','REJECT'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
                      component: sitereviewplanconfirmEdit,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'edit'
                      },
                      title: $t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
                      name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              publish: {
                type: 'void',
                'x-component': 'TableButton',
                title: "{{$t('common.publish')}}", // 发布
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `['DRAFT','VENDOR_REJECT','REJECT'].includes($deps[0])`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    // RELEASED  已发布
                    let datas = row
                    datas.approveStatus = 'RELEASED'
                    $queryEngine.request.save(datas).then(() => {
                      $message.success($t('common.successPublish'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `$deps[0] === 'DRAFT'`
                ),
                'x-component-props': {
                  style: `margin-left: 8px`,
                  showPopconfirm: true,
                  '@confirm': expression(`({ row }) => {
                    $queryEngine.request.delete(row.planConfirmId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              approve: {
                type: 'void',
                title: "{{$t('common.approve')}}", // 审批
                'x-component': 'TableButton',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `$deps[0] === 'VENDOR_CONFIRMED'`
                ),
                'x-component-props': {
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    const tab = {
                      component: sitereviewplanconfirmEdit,
                      params: {
                        readOnly: false,
                        row,
                        flag: 'approve'
                      },
                      title: $t('vendorMod.planConfirmEdit'), // 计划落实管理编辑
                      name: 'sitereviewplanconfirmEdit' + row.planConfirmId
                    }
                    emitTabAdd(tab)
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
  siteAssessmentDetail,
  sitereviewplanconfirmEdit
}

const components = {
  Cselect
}
</script>

<template>
  <RenderEngine schemaKey="siteReviewPlanConfirmList" class="contractPaymentType" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.dialogMain .el-dialog__body {
  max-height: 363px;
  overflow: auto;
}
</style>
