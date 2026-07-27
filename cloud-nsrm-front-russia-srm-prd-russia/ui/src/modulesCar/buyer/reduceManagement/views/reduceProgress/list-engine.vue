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
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, dataTimeSelectorSegment, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $submitData = ($form:any, $queryEngine:any, $table:any, $index:any, row:any) => {
  $form.validate().then(() => {
    $queryEngine.request.save(row, { query: { '*': {} }, loading: true }).then((res) => {
      if (res.data.length) app.$message.success($t('common.success'))
      $table.cancelEditRow($index)
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch(err => {
    app.$message.warning($t('common.pleasefinishRequired'))
  })
}

const $createNegotiation = ($form:any, $queryEngine:any, $table:any, $index:any, row:any) => {
  let payload = {
    progressNo: row.progressNo,
    progressId: row.progressId,
    vendorCode: row.vendorCode,
    vendorId: row.vendorId,
    vendorName: row.vendorName
  }
  $queryEngine.request.baseRequest({
    'type': 'ReduceNegotiation',
    'lang': 'zh-cn',
    'payload': [ payload ],
    'action': 'save',
    'query': { '*': {} }
  }).then((res: any) => {
    if (res.data.length) {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    }
  })
}

const $toNegotiation = (row) => {
  app.$router.push({
    name: 'reduceNegotiation',
    params: {
      from: 'fromFun',
      funName: 'reduceProgress',
      formId: row.progressId,
      formNo: row.progressNo,
      row: row
    }
  })
}

const $addProgress = (val:any, $form:any) => {
  let key = ['reduceYear', 'carCode', 'carName', 'orgId', 'orgCode', 'orgName', 'invOrgId', 'invOrgCode', 'invOrgName', 'materialCode', 'materialName', 'categoryCode', 'categoryName', 'vendorCode', 'vendorName', 'categoryName', 'priceType', 'effectiveDate', 'expirationDate', 'assembleCoefficient', 'referBasicPointPrice', 'confirBasicPointPrice']
  let obj = {}
  for (let k of key) {
    obj[k] = val[k] || undefined
  }
  obj.approveStatus = 'DRAFT'
  // obj.createNegotiationStatus = 'N'
  $form.query("table").take().componentProps.componentInstance.addRow("unshift", obj)
}
const $approve = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT') || !selects.length) return app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认提交审批'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return row.progressId
    })
    app.$http({
      url: '/api-cost/reduce/progress/submitApproval',
      method: 'POST',
      data: { progressIds: payload },
      loading: true
    }).then((res: any) => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const schema = defineSchemas({
  ReduceProgress: {
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
          eventName: 'BarCode',
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
          // 业务实体
          orgId: {
            type: 'string',
            title: "{{$t('common.orgId')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              '@select': expression(`(node) => {
                  if (!$form.values.query.invOrgId) return
                  $form.values.query.invOrgId = null
              }`)
            }
          },
          // 库存组织
          invOrgId: {
            type: 'string',
            title: "{{$t('common.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              disabled: expression('!$form.values.query.orgId'),
              'parent-id': expression('$form.values.query.orgId')
            }
          },
          'reduceYear': {
            type: 'string',
            title: "{{$t('reduce.reduceYear')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              'value-format': 'yyyy'
            }
          },
          approveStatus: {
            type: 'string',
            title: "{{$t('reduce.approveStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            }
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
          add: {
            type: 'void',
            title: i18nExpression('common.new'),
            'x-component': 'QuickSearchWrapper',
            'x-query-engine-skip': true,
            'x-component-props': {
              ...buttonListItemVisibleByPermission('reduce:reduceProgress:add'),
              showButton: true,
              multiSelect: false,
              btnTitle: `{{$t('common.add')}}`,
              // 'read-pretty': '{{$form.readPretty}}',
              'name': 'scc_cost_reduce_basic_price',
              '@close-quicksearch': expression(`(val, scope) => {
                $addProgress(val,$form)
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              ...buttonListItemVisibleByPermission('reduce:reduceProgress:export'),
              type: 'default',
              pageUrl: "/api-cost/api-ql/ReduceProgress/query",
              ...exportExcelSegment,
              meiqlKey: "ReduceProgress", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('ReduceProgress.table', 'data.columns'),
              dictCodes: {
                approveStatus: 'APPROVE_STATUS'
              }
            }
          },
          approve: {
            type: 'void',
            title: "{{$t('reduce.approve')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceProgress:approve'),
              '@click': expression(`() => {
                $approve($form,$queryEngine)
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
          progressId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          progressNo: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.progressNo')}}",
              minWidth: 150,
              skipEditable: true
            }
          },
          reduceYear: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.reduceYear')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.orgId')}}",
              minWidth: 130,
              skipEditable: true
            }
          },
          invOrgName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.invOrg')}}",
              minWidth: 130,
              skipEditable: true
            }
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.materialCode')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.materialName')}}",
              minWidth: 120,
              skipEditable: true
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
              minWidth: 150,
              skipEditable: true
            }
          },
          categoryName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.categoryName')}}",
              minWidth: 150,
              skipEditable: true
            }
          },
          confirBasicPointPrice: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.basePrice')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          priceType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PRICE_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.priceType')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          effectiveDate: {
            'x-render-table-column': {
              title: "{{$t('reduce.effectiveDate')}}",
              minWidth: 120,
              skipEditable: true
            },
            ...yearMonthDaySelectorSegment
          },
          expirationDate: {
            'x-render-table-column': {
              title: "{{$t('reduce.expirationDate')}}",
              minWidth: 120,
              skipEditable: true
            },
            ...yearMonthDaySelectorSegment
          },
          // 百分比用作显示
          progressProportionStr: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.progressProportion')}}",
              width: 120,
              skipEditable: true
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              let num = '-'
              if(row && row.progressProportion){
                num = parseFloat((row.progressProportion*100).toFixed(4)) + '%'
              }
              setTimeout(()=>{
                field.setValue(num)
              })
            }`)
          },
          progressProportion: {
            type: 'string',
            'x-hidden': true,
            'x-render-table-column': {
              title: "{{$t('reduce.progressProportion')}}",
              width: 120
            },
            'x-reactions': expression(`(field) => {
            }`)
          },
          progressReduceAmount: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.progressReduceAmount')}}",
              width: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
                setTimeout(()=>{
                  $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
                })

                let row = $table.getRowByIndex($self.index)
                if(row && row.progressReduceAmount != undefined && row.confirBasicPointPrice != undefined ){
                  let value = (row.progressReduceAmount - row.confirBasicPointPrice)/row.confirBasicPointPrice
                  setTimeout(()=>{
                    row.progressProportion = parseFloat(value.toFixed(4))
                  })
                }
            }`)
          },
          progressReduceChangeValue: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.progressReduceChangeValue')}}",
              width: 120,
              skipEditable: true
            },
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              if(row && row.progressReduceAmount != undefined && row.confirBasicPointPrice != undefined ){
                let value = row.progressReduceAmount - row.confirBasicPointPrice
                setTimeout(()=>{
                  field.setValue(parseFloat(value.toFixed(4)))
                })
              }
            }`)
          },
          startDate: {
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.startDate')}}",
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
          endDate: {
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.endDate')}}",
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
          remarks: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.remarks')}}",
              width: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.approveStatus')}}",
              minWidth: 100,
              skipEditable: true
            }
          },

          createdUserName: {
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
          lastUpdatedUserName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.updatePeople')}}",
              width: 120,
              skipEditable: true
            }
          },
          lastUpdateDate: {
            type: 'string',
            ...yearMonthDaySelectorSegment,
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: "{{$t('common.updateTime')}}",
              width: 120,
              skipEditable: true
            }
          },
          // negotiation: {
          //   type: 'void',
          //   'x-component': 'AuthorityButton',
          //   'x-content': '查看',
          //   'x-component-props': {
          //     type: 'text',
          //     '@click': expression(`() => {
          //       let row = $table.getRowByIndex($self.index)
          //       $toNegotiation(row)
          //     }`)
          //   },
          //   'x-render-table-column': {
          //     title: "{{$t('reduce.negotiation')}}",
          //     minWidth: 120,
          //     customRender: true
          //   }
          // },
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
                  `['DRAFT'].includes($deps[0]) && !$table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  // ...buttonListItemVisibleByPermission('reduce:reduceProgress:edit'),
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
                  `$table.getSelfRowEditable($self) `
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
                  `['DRAFT'].includes($deps[0]) && $table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    $submitData($form,$queryEngine,$table,$self.index,row)
                  }`)
                }
              }
              // createNegotiation: {
              //   title: "{{$t('reduce.createNegotiation')}}",
              //   'x-reactions': expression(`(field) => {
              //     $self.visible = !!$table.getRowByIndex($self.index)?.progressNo
              //   }`),
              //   'x-component-props': {
              //     '@click': expression(`({ row }) => {
              //       $createNegotiation($form,$queryEngine,$table,$self.index,row)
              //     }`)
              //   }
              // }
            }
          }
        })
      }
    }
  }
})

// @ts-ignore
const components = {
}

const scope = {
  $approve,
  $submitData,
  $addProgress,
  $toNegotiation,
  $createNegotiation
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceReportMaterial"
  />
</template>
