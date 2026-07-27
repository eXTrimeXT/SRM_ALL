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
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, dataTimeSelectorSegment } from 'lib@/components/render-engine/schema-segments'
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

const $approve = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT')) app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认提交审批'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return row.rebateId
    })
    app.$http({
      url: '/api-cost/reduce/rebate/submitApproval',
      method: 'POST',
      data: { rebateIds: payload },
      loading: true
    }).then((res: any) => {
      app.$message.success($t('common.success'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const $delete = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  if (selects.some(item => item.approveStatus != 'DRAFT')) app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认删除'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return { rebateId: row.rebateId }
    })

    $queryEngine.request['delete'](payload, { loading: true }).then((res: any) => {
      app.$message.success($t('common.successDelete'))
      $queryEngine.state.paginationManagement.refresh()
    })
  }).catch((err) => {
    console.log(err)
  })
}

const schema = defineSchemas({
  ReduceRebate: {
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
          eventName: 'ReduceRebate',
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
          'reduceYear': {
            type: 'string',
            title: "{{$t('reduce.reduceYear')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              'value-format': 'yyyy'
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('reduce.materialCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          vendorCode: {
            type: 'string',
            title: "{{$t('reduce.vendorCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              showKey: 'companyCode',
              propKey: 'companyCode'
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
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceRebate:add'),
              '@click': expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{approveStatus: 'DRAFT'})
              }`)
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              ...buttonListItemVisibleByPermission('reduce:reduceRebate:import'),
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cost/reduce/rebate/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cost/reduce/rebate/exportExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ReduceRebate')
              }`)
            }
          },
          approve: {
            type: 'void',
            title: "{{$t('reduce.approve')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceRebate:approve'),
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
          rebateId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          reduceYear: {
            'x-render-table-column': {
              title: i18nExpression('reduce.reduceYear'),
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-decorator': 'FormItem',
            ...editTableFormItemValid,
            type: 'date',
            default: null,
            'x-component-props': {
              type: 'year',
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy',
              'value-format': 'yyyy'
            }
          },
          orgId: {
            type: 'string',
            ...editTableFormItemValid,
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              multiple: false,
              '@select': expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}
                if (val && row.orgId === organizationId) {
                  // 避免重复执行
                  return
                }
                row.orgId = organizationId
                row.orgCode = organizationCode
                row.orgName = organizationName
                // 清空库存组织
                row.invOrgId = ''
                row.invOrgCode = ''
                row.invOrgName = ''
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('common.orgId')}}",
              minWidth: 160
            }
          },
          invOrgId: {
            type: 'string',
            ...editTableFormItemValid,
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('bid_mod.inv')}}",
              multiple: false,
              // disabled: expression('!$form.values.query.orgId'),
              parentId: expression(`$table.getRowByIndex($self.index)?.orgId || '' `),
              '@select': expression(`(node, val) => {
                const row = $table.getRowByIndex($self.index)
                const { organizationId = '', organizationCode = '', organizationName = '' } = node || {}

                if (val && row.invOrgId === organizationId) {
                  // 避免重复执行
                  return
                }

                row.invOrgId = organizationId
                row.invOrgCode = organizationCode
                row.invOrgName = organizationName
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('common.invOrg')}}",
              minWidth: 160
            }
          },
          materialCode: {
            type: 'string',
            ...editTableFormItemValid,
            title: "{{$t('reduce.materialCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              showKey: 'materialCode',
              propKey: 'materialCode',
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.materialName = val ? val.materialName : ''
                row.materialId = val ? val.materialId : ''
                row.materialCode = val ? val.materialCode : ''

              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              minWidth: 150
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
            ...editTableFormItemValid,
            title: "{{$t('reduce.vendorCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sup_company_info_all',
              showKey: 'vendorCode',
              propKey: 'vendorCode',
              '@close-quicksearch': expression(`(val, scope) => {
                const row = $table.getRowByIndex($self.index)
                row.vendorName = val ? val.companyName : ''
                row.vendorId = val ? val.companyId : ''
                row.vendorCode = val ? val.companyCode : ''
              }`)
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
            'x-render-table-column': {
              title: "{{$t('reduce.vendorCode')}}",
              minWidth: 150
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
          rebateAmount: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.rebateAmount')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`),
          },
          approveStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'APPROVE_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.approveStatus')}}",
              skipEditable: true,
              minWidth: 100
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
                  // ...buttonListItemVisibleByPermission('reduce:reduceRebate:edit'),
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
  $submitData,
  $approve,
  $delete
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceRebate"
  />
</template>
