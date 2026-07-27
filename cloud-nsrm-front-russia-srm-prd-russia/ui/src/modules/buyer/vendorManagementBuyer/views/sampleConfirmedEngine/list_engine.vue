<!-- eslint-disable quotes -->
<script setup lang="ts">
import { changeFieldVisibleByDeps, defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment, yearMonthDayHourMinuteSecondSelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import SampleConfirmedDetail from './edit_engine'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReviewEngine/quaOfReviewDetail'
import { saveOrUpdateOrderByUrl, quaSampleApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

const schema = defineSchemas({
  QuaSample: {
    type: 'void',
    'x-query-engine': {
      service: 'sup',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container the_dictionary_wrapper',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'QuaSample',
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
          categoryName: {
            type: 'string',
            title: "{{$t('common.category')}}",
            'x-component': 'CCategorySelect',
            'x-component-props': {
              showKey: 'categoryName'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          materialCode: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              showKey: 'materialCode',
              propKey: 'materialCode'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          sampleNumber: {
            type: 'string',
            title: "{{$t('vendorMod.sampleNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          result: {
            type: 'string',
            title: "{{$t('vendorMod.testResult')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SAMPLE_TEST_RESULT'
            },
            'x-query-engine-relation': 'orgCateJournals',
            'x-query-engine-relation-strict': true
          },
          isMaterialTrial: {
            type: 'string',
            title: "{{$t('vendorMod.isTrial')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          reviewFormNumber: {
            type: 'string',
            title: "{{$t('vendorMod.quaNum')}}",
            'x-query-engine-query-operator': 'contains'
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.approveStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: "SAMPLE_STATUS"
            }
          },
          receiver: {
            type: 'string',
            title: "{{$t('vendorMod.sampleReceiver')}}",
            'x-query-engine-query-operator': 'contains'
          },
          receiverPhone: {
            type: 'string',
            title: "{{$t('vendorMod.receiverPhone')}}",
            'x-query-engine-query-operator': 'contains'
          },
          requireSendTime: {
            type: 'string',
            title: "{{$t('vendorMod.requestTime')}}",
            // 定义查询是eq,lt,between
            'x-query-engine-query-operator': 'between',
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'daterange',
              valueFormat: 'yyyy-MM-dd'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: `
            margin-bottom: 16px;
            margin-top:16px;
          `
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': '{{() => $editTab( "add",{})}}'
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
          sampleId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          reviewFormId: {
            tpe: 'string',
            'x-hidden': true
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('vendorMod.orderStatus')}}",
            'x-render-table-column': {
              minWidth: 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'SAMPLE_STATUS'
            }
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('common.vendorCode')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          sampleNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => $editTab('view',row)`)
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.sampleNum')}}",
              minWidth: 150,
              customRender: true
            }
          },
          isMaterialTrial: {
            type: 'string',
            title: "{{$t('vendorMod.isTrial')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          reviewFormNumber: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => $editTab('reviewView',row)`)
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.quaNum')}}",
              minWidth: 120,
              customRender: true
            }
          },
          receiver: {
            type: 'string',
            title: "{{$t('vendorMod.sampleReceiver')}}",
            'x-render-table-column': {
              minWidth: 120
            }
          },
          receiverPhone: {
            type: 'string',
            title: "{{$t('vendorMod.receiverPhone')}}",
            "x-render-table-column": {
              minWidth: 120
            }
          },
          requireSendTime: {
            type: 'string',
            title: "{{$t('vendorMod.sendTime')}}",
            "x-render-table-column": {
              minWidth: 130
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            "x-render-table-column": {
              minWidth: 100
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('common.creationTime')}}",
            'x-query-engine-sort': 'desc',
            "x-render-table-column": {
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-render-table-column': {
              width: 170,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression('({ row }) => $editTab("edit",row)')
                },
                'x-reactions': {
                  dependencies: ['.approveStatus'],
                  fulfill: {
                    schema: {
                      'x-visible': expression(`
                        ['DRAFT'].includes($deps[0])
                      `)
                    }
                  }
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': {
                  dependencies: ['.approveStatus'],
                  fulfill: {
                    schema: {
                      'x-visible': expression(`
                        ['DRAFT'].includes($deps[0])
                      `)
                    }
                  }
                },
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDeleteRow')
                  },
                  '@click': expression('({row}) => $delete(row, $queryEngine)')
                }
              },
              evaluateSimpel: {
                type: 'void',
                title: "{{$t('vendorMod.evaluateSimpel')}}",
                'x-reactions': {
                  dependencies: ['.approveStatus'],
                  fulfill: {
                    schema: {
                      'x-visible': expression(`
                        ['CONFIRMED','WITHDRAW','REJECTED'].includes($deps[0])
                      `)
                    }
                  }
                },
                'x-component-props': {
                  '@click': expression('({ row }) => $editTab("editResult", row)')
                }
              },
              doApproval: {
                type: 'void',
                title: "{{$t('vendorMod.doApproval')}}",
                'x-reactions': {
                  dependencies: ['.approveStatus'],
                  fulfill: {
                    schema: {
                      'x-visible': expression(`
                        ['SUBMITTED'].includes($deps[0])
                      `)
                    }
                  }
                },
                'x-component-props': {
                  '@click': expression('({ row }) => $editTab("doApproval", row)')
                }
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $delete = (row: any, queryEngine: any) => {
  queryEngine.request['delete']([row.sampleId]).then(() => {
    queryEngine.state.paginationManagement.refresh()
  })
}
const $editTab = (type:any, row:any, $queryEngine:any) => {
  let tab = {}
  if (type === 'add') {
    // 新增
    tab = {
      component: SampleConfirmedDetail,
      params: {
        flag: 'add',
        tabName: 'sampleConfirmedDetail'
      },
      title: () => t('vendorMod.addSimpel'), // '新增样品确认单',
      name: 'sampleConfirmedDetail'
    }
  } else if (type === 'view') {
    // 修改
    let sampleId = row.sampleId
    tab = {
      component: SampleConfirmedDetail,
      params: {
        flag: type,
        sampleId: sampleId,
        tabName: 'sampleConfirmedDetail' + row.sampleNumber
      },
      title: row.sampleNumber,
      name: 'sampleConfirmedDetail' + row.sampleNumber
    }
  } else if (type === 'doApproval') {
    // 审批
    let sampleId = row.sampleId
    tab = {
      component: SampleConfirmedDetail,
      params: {
        flag: type,
        sampleId: sampleId,
        tabName: 'sampleConfirmedDetail' + row.sampleId
      },
      title: row.sampleNumber,
      name: 'sampleConfirmedDetail' + row.sampleId
    }
  } else if (type === 'reviewView') {
    // 查看
    tab = {
      component: quaOfReviewDetail,
      params: {
        flag: 'view',
        row: row,
        tabName: 'quaOfReviewDetail' + row.reviewFormNumber
      },
      title: row.reviewFormNumber,
      name: 'quaOfReviewDetail' + row.reviewFormNumber
    }
  } else if (type === 'vendorEditRefuse') {
    // 确认拒绝该样品确认单吗?
    app.$confirm(t('vendorMod.isRejectSampleConfirmation'), t('common.tips'), {
      confirmButtonText: t('common.affirm'), // 确定
      cancelButtonText: t('common.cancel'), // 取消
      type: 'warning'
    }).then(() => {
      $queryEngine.request.baseRequest({
        type: 'QuaSample',
        action: 'update',
        payload: [{
          ...row,
          approveStatus: 'REFUSED'
        }]
      }).then(() => {
        $queryEngine.state.paginationManagement.refresh()
      })
    }).catch(() => {

    })
    return false
  } else {
    // 修改
    let title = t('vendorMod.sampleConfirmed') + row.sampleNumber // '样品确认单'
    if (type === 'editResult') {
      title = t('vendorMod.evaluateSimpel') + row.sampleNumber // '评价样品'
    } else if (type === 'vendorEdit') {
      title = t('vendorMod.sampleConfirmed') // '样品确认'
    }
    let sampleId = row.sampleId
    tab = {
      component: SampleConfirmedDetail,
      params: {
        flag: 'edit',
        sampleId: sampleId,
        tabName: 'sampleConfirmedDetail' + row.sampleNumber
      },
      title: title,
      name: 'sampleConfirmedDetail' + row.sampleNumber
    }
  }
  app.$emit('tab-add', tab)
}

const scope = {
  $editTab,
  $delete
}
</script>

<template>
  <RenderEngine schemaKey="sampleConfirmedList" :pageAttrs="$attrs" :schema="schema" :scope="scope" />
</template>
