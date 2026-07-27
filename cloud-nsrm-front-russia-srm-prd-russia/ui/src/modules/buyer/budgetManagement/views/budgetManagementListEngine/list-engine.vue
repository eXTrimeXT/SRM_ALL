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
import { yearMonthDayStartSelectorSegment,
  dataTimeSelectorSegment,
  yearMonthDayEndSelectorSegment,
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  exportExcelSegment,
  editTableFormItemValid } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $submitData = ($form:any, $queryEngine:any, $table:any, $index:any, row:any) => {
  $queryEngine.request.save(row, { loading: true }).then((res) => {
    if (res.data.length) app.$message.success($t('common.success'))
    $table.cancelEditRow($index)
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $companyHandle = ($form:any) => {
  let companyId = $form.values.query.companyId
  $form.values.query.deptId = undefined
  app.$http({
    url: '/api-base/base/org_company_dept/listPage',
    method: 'POST',
    data: {
      organizationId: companyId
    },
    loading: true
  }).then(res => {
    const depts = []
    res.data.list.forEach(v => {
      depts.push({
        value: v.companyDeptId,
        label: v.deptName,
        params: {
          deptId: v.companyDeptId,
          deptCode: v.deptCode,
          deptName: v.deptName
        }
      })
    })
    $form.query('state').get('data').budDepatInfo = depts
  })
}

const $tableCompanyHandle = (field:any, $form:any) => {
  let companyId = field
  //  $form.values.query.deptId = undefined
  app.$http({
    url: '/api-base/base/org_company_dept/listPage',
    method: 'POST',
    data: {
      organizationId: companyId
    },
    loading: true
  }).then(res => {
    const depts = []
    res.data.list.forEach(v => {
      depts.push({
        value: v.companyDeptId,
        label: v.deptName,
        params: {
          deptId: v.companyDeptId,
          deptCode: v.deptCode,
          deptName: v.deptName
        }
      })
    })
    $form.query('state').get('data').tableBudDepatInfo = depts
  })
}

const $getCompanyList = ($self: any, $form:any) => {
  app.$http({
    url: '/api-base/organization/organization/listAllOrganization',
    method: 'POST',
    data: {
      organizationTypeCode: 'COMPANY'
    },
    loading: true
  })
    .then(res => {
      const companys = []
      res.data.list.forEach(v => {
        companys.push({
          value: v.organizationId,
          label: v.organizationName,
          params: {
            companyId: v.organizationId,
            companyCode: v.organizationCode,
            companyName: v.organizationName
          }
        })
      })
      $self.dataSource = companys
      $form.query('state').get('data').companyInfo = companys
    })
}

const $addOne = () => {
  $detailOne('add', {})
}

const $detailOne = (type: string, row: any) => {

}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request.baseRequest({
    'type': 'BudgetManagement',
    'lang': 'zh-cn',
    'payload': [
      { budgetManagementId: row.budgetManagementId }
    ],
    loading: true,
    'action': 'delete'
  }).then((res: any) => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $effectHandle = ($queryEngine: any, row: any) => {
  $queryEngine.request.baseRequest({
    'type': 'BudgetManagement',
    'lang': 'zh-cn',
    'payload': [
      row
    ],
    loading: true,
    'action': 'effect'
  }).then((res: any) => {
    app.$message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $adjustHandle = ($queryEngine: any, row: any) => {
  $queryEngine.request.baseRequest({
    'type': 'BudgetManagement',
    'lang': 'zh-cn',
    'payload': [
      row
    ],
    'action': 'adjustBudget',
    loading: true
  }).then((res: any) => {
    app.$message.success($t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyInfo: [],
      budDepatInfo: [],
      tableBudDepatInfo: []
    }
  },
  BudgetManagement: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          // action: 'query',
          immediate: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          transformResponse: (res: string) => {
            const data = JSON.parse(res)
            if (data.data?.ref?.BudgetManagement) {
              const keys = Object.keys(data.data.ref.BudgetManagement ?? {})
              keys.forEach(key => {
                const item = data.data.ref.BudgetManagement[key]
                item.year = String(item.year)
              })
            }
            return data
          }
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
          budgetManagementNumber: {
            type: 'string',
            title: "{{$t('purchaseDemand.budgetNumber')}}",
            'x-query-engine-query-operator': 'contains'
          },
          // todo --
          companyId: {
            type: 'string',
            title: "{{$t('vendorMod.corporateName')}}",
            'x-component': 'Select',
            'x-component-props': {
              '@change': expression(`(field) => {
                $companyHandle($form)

              }`)
            },
            "x-reactions": [
              expression(`(field) => {
                $getCompanyList($self,$form)
              }`)
            ]
          },
          deptId: {
            type: 'string',
            title: "{{$t('purchaseDemand.budgetDepartment')}}",
            'x-component': 'Select',
            enum: expression('$form.query(\'state\').get(\'data\').budDepatInfo')
          },
          status: {
            type: 'string',
            title: "{{$t('purchaseDemand.applyStatus')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BUDGET_MANAGEMENT_STATUS'
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-query-operator': 'contains'
          },
          'year': {
            type: 'string',
            title: "{{$t('dataConfMod.year')}}",
            'x-component': 'DatePicker',
            'x-component-props': {
              type: 'year',
              'value-format': 'yyyy'
            }
          },
          budgetItem: {
            type: 'string',
            title: "{{$t('purchaseDemand.budgetAccount')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BUDGET_ITEM'
            }
          },
          expenseType: {
            type: 'string',
            title: "{{$t('purchaseDemand.typeOfFee')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'EXPENSE_TYPE'
            }
          },
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
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                $form.query("table").take().componentProps.componentInstance.addRow("unshift",{status: 'DRAFT'})
              }`)
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              extraData: {
                fileModular: 'sup-ce',
                fileFunction: 'BudgetManagement',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup-ce/budget/budgetManagement/importBudgetManagement',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup-ce/budget/budgetManagement/importTemplateDownload',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('BudgetManagement')
              }`)
            }
          },
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/BudgetManagement/query",
              ...exportExcelSegment,
              meiqlKey: "BudgetManagement", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('BudgetManagement.table', 'data.columns'),
              dictCodes: {
                status: 'BUDGET_MANAGEMENT_STATUS',
                expenseType: 'EXPENSE_TYPE',
                budgetItem: 'BUDGET_ITEM'
              }
            }
          },

          tips: {
            type: 'void',
            "x-component": 'div',
            "x-component-props": {
              style: {
                fontSize: '14px',
                display: 'inline-block',
                color: 'red'
              }
            },
            "x-content": "{{$t('purchaseDemand.amountsIncludingTax')}}"
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
          openCustomTable: true,
          editMode: 'multi-row'
          // dblclickEditable: true
        },
        properties: generateXindexInOrder({
          budgetManagementId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          companyName: {
            type: 'string',
            'x-component': 'Select',
            enum: expression('$form.query(\'state\').get(\'data\').companyInfo'),
            'x-component-props': {
              '@change': expression(`(field)=>{
                let companyItem = $form.query('state').get('data').companyInfo.find(item=>item.value == field)
                $form.values.table[$self.index].companyCode = companyItem.params.companyCode
                $form.values.table[$self.index].companyName = companyItem.params.companyName
                $form.values.table[$self.index].companyId = companyItem.params.companyId
                $tableCompanyHandle(companyItem.params.companyId,$form)
                $form.values.table[$self.index].deptId = null
                $form.values.table[$self.index].deptCode = null
                $form.values.table[$self.index].deptName = null
              }`)
            },
            'x-render-table-column': {
              title: "{{$t('vendorMod.corporateName')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
              let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
              })
            }`),
            ...editTableFormItemValid
          },
          deptName: {
            type: 'string',
            'x-component': 'Select',
            enum: expression('$form.query(\'state\').get(\'data\').tableBudDepatInfo'),
            'x-component-props': {
              '@change': expression(`(field)=>{
                let item = $form.query('state').get('data').tableBudDepatInfo.find(item=>item.value == field)
                $form.values.table[$self.index].deptCode = item.params.deptCode
                $form.values.table[$self.index].deptName = item.params.deptName
                $form.values.table[$self.index].deptId = item.params.deptId

              }`)
            },
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetDepartment')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
              let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
              })
            }`),
            ...editTableFormItemValid
          },
          year: {
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.budgetYear'),
              minWidth: 150
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
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
          budgetManagementNumber: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetNumber')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BUDGET_MANAGEMENT_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.applyStatus')}}",
              minWidth: 100,
              skipEditable: true
            }

          },
          expenseType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'EXPENSE_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.typeOfFee')}}",
              minWidth: 100
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
              })
            }`),
            ...editTableFormItemValid
          },
          budgetItem: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BUDGET_ITEM'
            },
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetAccount')}}",
              minWidth: 100
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
                setTimeout(()=>{
                  $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
                })
            }`),
            ...editTableFormItemValid
          },
          befAdjustBudgetAmount: {
            type: 'number',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetBeforeAdjustment')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
                setTimeout(()=>{
                  $self.editable = $table.getRowByIndex($self.index)?.status == 'DRAFT' && isRowEditable
                })

            }`),
            ...editTableFormItemValid
            // todo -- checkRowDataAmount
          },
          aftAdjustBudgetAmount: {
            type: 'number',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetafterAdjustment')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
              let isRowEditable = $table.getSelfRowEditable($self)
                setTimeout(()=>{
                  $self.editable = $table.getRowByIndex($self.index)?.status == 'ADJUSTIVE' && isRowEditable
                })
            }`)
          },
          useableAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purSettlementMod.usableAmount')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          freezeAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.frozenAmount')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          usedAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('contractMod.usedAmount')}}",
              minWidth: 120,
              skipEditable: true
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
              title: "{{$t('perfMod.lastUpdatedFullName')}}",
              width: 120,
              skipEditable: true
            }
          },
          lastUpdateDate: {
            title: "{{ $t('perfMod.lastUpdateDate') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150,
              skipEditable: true
            },
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              fixed: 'right',
              width: 170
            },
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['DRAFT', 'ADJUSTIVE'].includes($deps[0]) && !$table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({rowIndex}) => {
                    console.log($table)
                    $table.editRowByIndex(rowIndex)
                  }`)
                }
              },
              cancel: {
                type: 'void',
                title: "{{$t('common.cancel')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  [`.status`],
                  `$table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({rowIndex}) => {
                    $table.cancelEditRow(rowIndex)
                  }`)
                }
              },
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `$deps[0] === 'DRAFT' || ($table.getSelfRowEditable($self) && $deps[0] !== 'ADJUSTIVE')`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                }
              },
              save: {
                type: 'void',
                title: "{{$t('common.save')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `$deps[0] !== 'ADJUSTIVE' && $table.getSelfRowEditable($self) `
                ),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    $form.validate().then(() => {
                      $submitData($form,$queryEngine,$table,$self.index,row)
                    })

                  }`)
                }
              },
              active: {
                type: 'void',
                title: "{{$t('common.active')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status', '.budgetManagementNumber'],
                  `['ADJUSTIVE', 'DRAFT'].includes($deps[0]) && $deps[1] `
                ),

                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('确认执行该行数据？')}}"
                  },
                  '@click': expression(`({row}) => {
                    $effectHandle($queryEngine,row)
                  }`)
                }
              },
              budgetAdjustment: {
                type: 'void',
                title: "{{$t('common.budgetAdjustment')}}",
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  `['EFFECTIVE'].includes($deps[0])`
                ),
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('确认执行该行数据？')}}"
                  },
                  '@click': expression(`({rowIndex,row}) => {
                    $adjustHandle($queryEngine,row)
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
  $addOne,
  $editOne,
  $delete,
  $detailOne,
  $readOne,
  $getCompanyList,
  $companyHandle,
  $tableCompanyHandle,
  $submitData,
  $effectHandle,
  $adjustHandle

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="ShopCartList"
  />
</template>
