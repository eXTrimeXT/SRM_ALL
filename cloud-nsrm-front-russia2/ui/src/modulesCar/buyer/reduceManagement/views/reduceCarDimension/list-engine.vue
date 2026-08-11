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
      return { dimensionId: row.dimensionId }
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
  ReduceCarDimension: {
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
          eventName: 'ReduceCarDimension',
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
          carCode: {
            type: 'string',
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_cost_car',
              'preQueryData': expression(`{'t.car_level': 2}`),
              showKey: 'carCode',
              propKey: 'carCode'
            }
          },
          createdUserName: {
            type: 'string',
            title: "{{$t('common.creator')}}",
            'x-query-engine-query-operator': 'contains'
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
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              title: "{{$t('common.import')}}",
              type: 'default',
              ...buttonListItemVisibleByPermission('reduce:reduceCarDimension:import'),
              extraData: {
                fileModular: 'sup',
                fileFunction: 'purchaseCatalog',
                fileType: 'excel'
              },
              upLoadUrl: '/api-cost/reduce/carDimension/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-cost/reduce/carDimension/exportExcelTemplate',
                fileName: "{{$t('logisticsMod.importTemplateXLSX')}}"
              },
              '@handleSuccess': expression(`() => {
                $bus.$emit('ReduceCarDimension')
              }`)
            }
          },
          delete: {
            type: 'void',
            title: "{{$t('common.delete')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceCarDimension:delete'),
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
          // editMode: true,
        },
        properties: generateXindexInOrder({
          dimensionId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          reduceYear: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: "{{$t('reduce.reduceYear')}}",
              minWidth: 120
            }
          },
          invOrgName: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: "{{$t('common.invOrg')}}",
              minWidth: 120
            }
          },
          parentCarName: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: "{{$t('reduce.carCode2')}}",
              minWidth: 100
            }
          },
          carName: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              title: "{{$t('reduce.carCode')}}",
              minWidth: 100
            }
          },
          dimensionType: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'REDUCE_DIMENSION_TYPE'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.dimensionType')}}",
              minWidth: 100
            }
          },
          dimensionStatus: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'REDUCE_DIMENSION_CAR_STATUS'
            },
            'x-render-table-column': {
              title: "{{$t('reduce.dimensionStatus')}}",
              minWidth: 100
            }
          },
          monthQuantity1: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.January')}}",
              minWidth: 120
            }
          },
          monthQuantity2: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.February')}}",
              minWidth: 120
            }
          },
          monthQuantity3: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.March')}}",
              minWidth: 120
            }
          },
          monthQuantity4: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.April')}}",
              minWidth: 120
            }
          },
          monthQuantity5: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.May')}}",
              minWidth: 120
            }
          },
          monthQuantity6: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.June')}}",
              minWidth: 120
            }
          },
          monthQuantity7: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.July')}}",
              minWidth: 120
            }
          },
          monthQuantity8: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.Augest')}}",
              minWidth: 120
            }
          },
          monthQuantity9: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.September')}}",
              minWidth: 120
            }
          },
          monthQuantity10: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.October')}}",
              minWidth: 120
            }
          },
          monthQuantity11: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.November')}}",
              minWidth: 120
            }
          },
          monthQuantity12: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.December')}}",
              minWidth: 120
            }
          },
          sumMonthQuantity: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.total')}}",
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
  $readOne

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceCarDimension"
  />
</template>
