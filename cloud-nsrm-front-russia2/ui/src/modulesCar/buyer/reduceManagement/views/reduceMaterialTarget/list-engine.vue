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
    let regex = /^-?\d+(\.\d{1,4})?$/
    if (row.materialTargetUnitPrice < 0 || !regex.test(row.materialTargetUnitPrice.toString())) return app.$message.warning('年度降本目标(单价)为正数，最大支持4位小数')
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

  if (selects.some(item => item.approveStatus != 'DRAFT') || !selects.length) return app.$message.warning('请选择状态为拟定的数据')

  app.$confirm($t('是否确认提交审批'), '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let payload = selects.map((row: any) => {
      return row.materialTargetId
    })
    app.$http({
      url: '/api-cost/reduce/material/target/submitApproval',
      method: 'POST',
      data: { materialTargetIds: payload },
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
  ReduceMaterialTarget: {
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
          approve: {
            type: 'void',
            title: "{{$t('reduce.approve')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('reduce:reduceMaterialTarget:approve'),
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
          materialTargetId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
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
              minWidth: 120,
              skipEditable: true
            }
          },
          invOrgName: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('common.invOrg')}}",
              minWidth: 120,
              skipEditable: true
            }
          },
          carCode: {
            type: 'string',
            title: "{{$t('reduce.carCode')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'carCode',
              propKey: 'carCode',
              'name': 'scc_cost_car'
            },
            'x-render-table-column': {
              minWidth: 100
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
          confirBasicPointPrice: {
            type: 'string',
            'x-render-table-column': {
              title: "{{$t('reduce.confirBasicPointPrice2')}}",
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
              minWidth: 120,
              skipEditable: true
            }
          },
          materialTargetUnitPrice: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.materialTargetUnitPrice')}}",
              minWidth: 120
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)
          },
          materialTargetTotalAmount: {
            type: 'string',
            ...editTableFormItemValid,
            'x-render-table-column': {
              title: "{{$t('reduce.materialTargetTotalAmount')}}",
              minWidth: 120,
              skipEditable: true
            },
            'x-reactions': expression(`(field) => {
                let isRowEditable = $table.getSelfRowEditable($self)
              setTimeout(()=>{
                $self.editable = $table.getRowByIndex($self.index)?.approveStatus == 'DRAFT' && isRowEditable
              })
            }`)
          },
          // annualPredictQuantity: {
          //   type: 'string',
          //   'x-render-table-column': {
          //     title: "{{$t('reduce.annualPredictQuantity')}}",
          //     minWidth: 120,
          //     skipEditable: true
          //   }
          // },

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
  $approve

}
</script>
<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :scope="scope"
    :components="components"
    :schema="schema"
    schemaKey="reduceMaterialTarget"
  />
</template>
