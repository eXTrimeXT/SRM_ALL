<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  generateCharFunctionExpression,
  generateCharReactionExpression,
  i18nExpression,
  queryFieldStatePropertyExpression,
  queryFieldValueExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, yearMonthDaySelectorSegment, buttonListItemVisibleByPermission, exportExcelSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $companyHandle = ($form:any) => {
  console.log('$form', $form)
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
  // let name = row.deliveryNumber ?? ''
  // emitTabAdd({
  //   component: edit,
  //   params: {
  //     flag: type,
  //     row: row,
  //     tabName: name ? 'buyerDeliveryOrderDetail' + name : 'buyerDeliveryOrderDetail'
  //   },
  //   title: $t('orderMod.buyerOrderSynergy.vendorDelivery') + name,
  //   name: name ? 'buyerDeliveryOrderDetail' + name : 'buyerDeliveryOrderDetail'
  // })
}

const $readOne = (row: any) => {
  $detailOne('view', row)
}

const $editOne = (row: any) => {
  $detailOne('edit', row)
}

const $delete = ($queryEngine: any, row: any, $message: any) => {
  $queryEngine.request['delete']([row.tagTemplateRelationId]).then((res: any) => {
    $message.success($t('common.successDelete'))
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
      budDepatInfo: []
    }
  },
  BudgetManagementHistory: {
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
          budgetManagementNumber: {
            type: 'string',
            title: "{{$t('purchaseDemand.budgetNumber')}}",
            'x-query-engine-query-operator': 'contains'
          },
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
            title: "{{$t('orderMod.buyerOrderSynergy.status')}}",
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
          exportExcel: {
            type: 'void',
            'x-component': 'ExportExcel',
            'x-component-props': {
              type: 'default',
              pageUrl: "/api-sup-ce/api-ql/BudgetManagementHistory/query",
              ...exportExcelSegment,
              meiqlKey: "BudgetManagementHistory", // meiQl 表格key
              filterParams: queryFieldValueExpression('query'),
              tableHeader: queryFieldStatePropertyExpression('BudgetManagementHistory.table', 'data.columns'),
              dictCodes: {
                status: 'BUDGET_MANAGEMENT_STATUS',
                expenseType: 'EXPENSE_TYPE',
                budgetItem: 'BUDGET_ITEM'
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
          tagTemplateRelationId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          companyName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('vendorMod.corporateName')}}",
              minWidth: 130
            }
          },
          deptName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetDepartment')}}",
              minWidth: 130
            }
          },
          year: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetYear')}}",
              minWidth: 110
            }
          },
          budgetManagementNumber: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetNumber')}}",
              minWidth: 110
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
              minWidth: 100
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
            }
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
            }
          },
          befAdjustBudgetAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetBeforeAdjustment')}}",
              minWidth: 150
            }
          },
          aftAdjustBudgetAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.budgetafterAdjustment')}}",
              minWidth: 150
            }
          },
          useableAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purSettlementMod.usableAmount')}}",
              minWidth: 100
            }
          },
          freezeAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('purchaseDemand.frozenAmount')}}",
              minWidth: 100
            }
          },
          usedAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('contractMod.usedAmount')}}",
              minWidth: 100
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true,
            'x-query-engine-primary-key': true
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
  $companyHandle

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
