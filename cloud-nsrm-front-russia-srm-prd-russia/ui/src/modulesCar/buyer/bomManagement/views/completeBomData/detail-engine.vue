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
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment,
  dataTimeSelectorSegment, buttonListItemVisibleByPermission } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// import edit from './edit-engine.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

const $delete = ($form:any, $queryEngine: any) => {
  let selects = $form
    .query('table')
    .take()
    .componentProps.componentInstance.getCheckboxRecords()

  app.$confirm($t('是否确认删除'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return { bomId: row.bomId }
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
  Bom: {
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
            let row = $attrs.params.row
            let materialCode = row.materialCode
            $form.query('query.materialCode').take().setValue(materialCode)
            data.query['*'] = {}
            if(!data.payload['filter']) data.payload['filter'] = {}
            data.payload['filter'].materialCode = {contains: materialCode}
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
          lastUpdateDate: {
            title: "{{$t('bom.lastUpdateDate')}}",
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          invOrgId: {
            type: 'string',
            title: "{{$t('common.invOrg')}}",
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: "{{$t('common.pleaseSelect')}}",
              'parent-id': -1
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              disabled: true,
              name: 'scc_cost_car',
              'preQueryData': expression(`{'t.car_level': 2}`),
              showKey: 'carCode',
              propKey: 'carCode'
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_rbac_user_display',
              showKey: 'nickname',
              propKey: 'nickname'
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
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          bomId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          invOrgCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.invOrgName')}}",
              minWidth: 120
            }
          },
          materialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.carCodeName')}}",
              minWidth: 120
            }
          },
          parentMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.parentMaterialCode')}}",
              minWidth: 120
            }
          },
          parentMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.parentMaterialName')}}",
              minWidth: 120
            }
          },
          assembleCoefficient: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.assembleCoefficient')}}",
              minWidth: 120
            }
          },
          bomLevel: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.bomLevel')}}",
              minWidth: 120
            }
          },
          startDate: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.startDate')}}",
              minWidth: 120
            }
          },
          endDate: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.endDate')}}",
              minWidth: 120
            }
          },
          lineProjectNum: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.lineProjectNum')}}",
              minWidth: 120
            }
          },
          projectType: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.projectType')}}",
              minWidth: 120
            }
          },
          childMaterialCode: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.childMaterialCode')}}",
              minWidth: 120
            }
          },
          childMaterialName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.childMaterialName')}}",
              minWidth: 120
            }
          },
          baseAmount: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.baseAmount')}}",
              minWidth: 120
            }
          },
          baseAmountUnit: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.baseAmountUnit')}}",
              minWidth: 120
            }
          },
          materialSupplyIdentify: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.materialSupplyIdentify')}}",
              minWidth: 120
            }
          },
          replaceGroup: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.replaceGroup')}}",
              minWidth: 120
            }
          },
          replaceRate: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.replaceRate')}}",
              minWidth: 120
            }
          },
          moduleStartDate: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.moduleStartDate')}}",
              minWidth: 120
            }
          },
          moduleEndDate: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.moduleEndDate')}}",
              minWidth: 120
            }
          },
          bomSystemLineId: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.bomSystemLineId')}}",
              minWidth: 120
            }
          },
          purchaseType: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.purchaseType')}}",
              minWidth: 120
            }
          },
          specialPurchaseType: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.specialPurchaseType')}}",
              minWidth: 120
            }
          },
          materialGroup: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.materialGroup')}}",
              minWidth: 120
            }
          },
          materialGroupName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('bom.materialGroupName')}}",
              minWidth: 120
            }
          },
          creationDate: {
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },

          lastUpdateDate: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true
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
  $delete,
  app
}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="BomDetail"
  />
</template>
