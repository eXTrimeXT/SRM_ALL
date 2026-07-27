<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  generateCharExpressionByFunction,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import {
  requiredValidatorSegment,
  editTableFormItemValid,
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission,
  dataTimeSelectorSegment,
  exportExcelSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
import { bus } from 'lib@/components/render-engine/components/bus'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $submitData = ($form: any, $queryEngine: any, $table: any, $index: any, row: any) => {
  $queryEngine.request.save(row, { query: { '*': {} }, loading: true }).then(res => {
    if (res.data.length) app.$message.success($t('common.success'))
    $table.cancelEditRow($index)
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $delete = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  // if (selects.some(item => item.approveStatus != 'DRAFT')) app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认删除'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return { negotiationId: row.negotiationId }
    })

    $queryEngine.request['delete'](payload, { loading: true }).then((res: any) => {
      app.$message.success($t('common.successDelete'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const $approve = ($form: any, $queryEngine: any) => {
  let selects = $form.query('table').take().componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT')) {
    app.$message.warning('请选择状态为拟定的数据')
  }

  app
    .$confirm($t('是否确认提交审批'), '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(() => {
      let payload = selects.map((row: any) => {
        return { carTargetId: row.carTargetId }
      })
      $queryEngine.request
        .baseRequest({
          type: 'ReduceCarTarget',
          lang: 'zh-cn',
          loading: true,
          payload: [payload],
          action: ''
        })
        .then((res: any) => {
          app.$message.success($t('common.success'))
          $queryEngine.state.paginationManagement.refresh()
        })
    })
    .catch(err => {
      console.log(err)
    })
}

onActivated(() => {
  console.log('onActivated')
  let { from, funName, row } = app.$route.params
  if (
    from === 'fromFun' && funName === 'reduceProgress'
  ) {
    bus.$emit('ReduceNegotiationByProgressNo')
  }
})

const schema = defineSchemas({
  ReduceNegotiation: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'cost',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {

          }`)
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ReduceNegotiation',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      bus2: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'ReduceNegotiationByProgressNo',
          '@listener': expression(`() => {
            let { from, funName, row,formNo } = app.$route.params
            $form.query('query.progressNo').take().setValue(formNo)
            setTimeout(()=>{
              console.log($form.query('query.progressNo').take())
              $queryEngine.state.paginationManagement.queryParams.value = {
                progressNo: { contains: formNo }
              }
              $queryEngine.state.paginationManagement.refresh()
            })

          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          progressNo: {
            type: 'string',
            title: "{{$t('reduce.progressNo')}}",
            'x-query-engine-query-operator': 'contains'
          },
          vendorCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'), // 供应商编码
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyCode',
              propKey: 'companyCode',
              name: 'scc_sup_company_info_all'
            }
          },
          // 创建人
          createdFullName: {
            type: 'string',
            title: "{{$t('supRisk.createdName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'nickname'
            }
          },
          // todo 洽谈日期

          // 创建时间
          creationDate: {
            title: "{{$t('common.creationTime')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          // approve: {
          //   type: 'void',
          //   title: "{{$t('reduce.approve')}}",
          //   'x-component': 'RButton',
          //   'x-component-props': {
          //     type: 'primary',
          //     '@click': expression(`() => {
          //       $approve($form,$queryEngine)
          //     }`)
          //   }
          // },
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{})
              }`)
            }
          },
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $delete($form,$queryEngine)
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-cost/api-ql/ReduceNegotiation/query",
              ...exportExcelSegment,
              meiqlKey: "ReduceNegotiation", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ReduceNegotiation.table', 'data.columns'),
              dictCodes: {
              }
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cost/reduce/negotiation/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cost/reduce/negotiation/exportExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ReduceNegotiation')
              }`)
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
          preColumns: 'checkbox,seq',
          openCustomTable: true,
          editMode: 'multi-row'
        },
        properties: generateXindexInOrder({
          negotiationId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          // progressNo: {
          //   type: 'string',
          //   'x-render-table-column': {
          //     title: "{{$t('reduce.progressNo')}}",
          //     minWidth: 120,
          //     skipEditable: true
          //   }
          // },
          progressNo: {
            type: 'string',
            title: "{{$t('reduce.progressNo')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'progressNo',
              propKey: 'progressNo',
              'name': 'scc_cost_reduce_progress',
              'preQueryData': expression(`{'t.car_level': 2}`),
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.vendorId = val ? val.vendorId : ''
                row.vendorCode = val ? val.vendorCode : ''
                row.vendorName = val ? val.vendorName : ''
                row.progressNo = val ? val.progressNo : ''
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable =  isRowEditable && !$table.getRowByIndex($self.index)?.progressNo
              })
            }`),
            'x-render-table-column': {
              minWidth: 180
            }
          },
          negotiationDate: {
            'x-render-table-column': {
              title: "{{$t('reduce.negotiationDate')}}",
              minWidth: 140
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            type: 'date',
            default: null,
            'x-component-props': {
              style: {
                width: '120px'
              },
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd'
            }
          },
          vendorCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.vendorCode')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          vendorName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.vendorName')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          negotiationResult: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.negotiationResult')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = isRowEditable
              })
            }`)
          },
          ourParty: {
            type: 'string',
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable =  isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('reduce.ourParty')}}",
              minWidth: 120
            }
          },
          fileName: {
          // 附件名称
            type: 'void',
            title: "{{$t('reduce.negotiationFile')}}",
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                fileModular: 'sup',
                fileFunction: 'vendorBiddingManagement',
                fileType: 'images'
              },
              defaultFile: {
                fileId: `{{$table.getRowByIndex($self.index)?.fileId}}`,
                fileName: `{{$table.getRowByIndex($self.index)?.fileName}}`
              },
              readonly: false,
              '@on-change': expression(`({file}) => {
                const row = $table.getRowByIndex($self.index)
                row.fileId = file.fileId.toString()
                row.fileName = file.fileName
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.componentProps.readonly = !isRowEditable
              })
            }`),
            'x-render-table-column': {
              minWidth: 120
            }
          },

          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.creator')}}",
              width: 120,
              skipEditable: true
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150,
              skipEditable: true
            }
          },
          lastUpdatedFullName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.updatePeople')}}",
              width: 120,
              skipEditable: true
            }
          },
          lastUpdateDate: {
            'x-query-engine-sort': 'desc',
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: "{{$t('common.updateTime')}}",
              skipEditable: true,
              width: 120
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            'x-render-table-column': {
              fixed: 'right',
              width: 120
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `!$table.getSelfRowEditable($self) `,
                ),
                'x-component-props': {
                  '@click': expression(`({rowIndex}) => {
                    $table.editRowByIndex(rowIndex)
                  }`)
                }
              },
              cancel: {
                type: 'void',
                title: "{{$t('common.cancel')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  [`.approveStatus`],
                  `$table.getSelfRowEditable($self) `,
                ),
                'x-component-props': {
                  '@click': expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)
                }
              },
              save: {
                type: 'void',
                title: "{{$t('common.save')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.approveStatus'],
                  `$table.getSelfRowEditable($self) `,
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)
                }
              }
            }
          }
        })
      }
    }
  }
})

// @ts-ignore
const components = {}

const scope = {
  app,
  $submitData,
  $delete,
  $approve
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceNegotiation"
  />
</template>
