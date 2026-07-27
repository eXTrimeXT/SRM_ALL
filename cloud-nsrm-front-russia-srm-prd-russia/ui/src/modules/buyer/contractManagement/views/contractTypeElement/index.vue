<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDayStartSelectorSegment,
  yearMonthDayEndSelectorSegment,
  yearMonthDaySelectorSegment,
  yearMonthDayHourMinuteSecondSelectorSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
import publicOperationProperties from '../conditionFactor/public_operation_properties'
import FilterText from '../contractElement/filter-text.vue'
import $dayjs from 'dayjs'

const scope = {
  $dayjs
}

const schema = defineSchemas({
  TypeRange: {
    type: 'void',
    'x-query-engine': {
      service: 'cm',
      actions: {
        paginationQuery: { immediate: true }
      }
    },
    'x-decorator': 'QueryEngine',
    'x-component': 'PageContainer',
    properties: {
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          contractType: {
            type: 'string',
            title: i18nExpression('contractMod.contractType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            }
          },
          elemName: {
            type: 'string',
            title: i18nExpression('contractMod.elemName'),
            'x-query-engine-query-operator': 'contains'
          },
          startDate: {
            title: i18nExpression('contractMod.startDate'),
            'x-query-engine-query-operator': '>=',
            ...yearMonthDayStartSelectorSegment
          },
          endDate: {
            title: i18nExpression('contractMod.endDate'),
            'x-query-engine-query-operator': '<=',
            ...yearMonthDayEndSelectorSegment
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
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('({ rowIndex }) => $form.query(".table").take().componentProps.componentInstance.addRow("unshift")')
            }
          },
          submit: {
            type: 'void',
            title: i18nExpression('common.submit'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'normal',
              '@click': expression(`() => {
                $form.validate().then(() => {
                  const $table = $form.query(".table").take().componentProps.componentInstance
                  const rows = $table.getUpdateRecords()

                  if (rows.length <= 0) {
                    $message.error($t('common.addOrUpdateRequired'))
                    return
                  }

                  for (let i = 0; i < rows.length; i += 1) {
                    const { startDate, endDate } = rows[i]

                    if (startDate && endDate && !$dayjs(startDate).isBefore($dayjs(endDate))) {
                      $message.error('结束时间不能小于当前时间')
                      return
                    }
                  }

                  $queryEngine.request.save(rows).then(() => {
                    $message.success($t('common.successSave'))
                    $table.clearAllEditStatus()
                    $queryEngine.state.paginationManagement.refresh()
                  })
                }).catch(err => {
                  $message.warning($t('common.pleasefinishRequired'))
                })
              }
              `)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          editMode: 'multi-row',
          openCustomTable: true,
          dblclickEditable: true
        },
        properties: generateXindexInOrder({
          createdFullName: {
            type: 'string',
            'x-hidden': true
          },
          typeRangeId: {
            type: 'string',
            'x-hidden': true
          },
          contractType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.contractType'),
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE'
            },
            ...editTableFormItemValid
          },
          elemName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.elemName'),
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEMNAME',
              'custom-select-type': 'ELEMNAME',
              '@change-value': expression(`(_, item) => {
                const row = $table.getRowByIndex($self.index)
                row.elemCode = item.key
                row.elemMaintainId = item.id
              }`)
            },
            ...editTableFormItemValid
          },
          elemCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.elemCode'),
              minWidth: 150,
              // 跳过行内编辑
              skipEditable: true
            }
          },
          startDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('contractMod.startDate'),
              minWidth: 150
            },
            ...editTableFormItemValid
          },
          endDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('contractMod.endDate'),
              minWidth: 150
            }
          },
          // createdUserName收集用的
          createdBy: {
            type: 'string',
            'x-hidden': true
          },
          // createdUserName收集用的
          lastUpdatedBy: {
            type: 'string',
            'x-hidden': true
          },
          createdUserName: {
            type: 'string',
            title: i18nExpression('contractMod.createdBy'),
            'x-query-engine-skip': true,
            'x-render-table-column': {
              minWidth: 150,
              // 跳过行内编辑
              skipEditable: true
            }
          },
          creationDate: {
            'x-query-engine-sort': 'desc',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            title: i18nExpression('contractMod.creationDate'),
            'x-render-table-column': {
              minWidth: 150,
              // 跳过行内编辑
              skipEditable: true
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 135,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: publicOperationProperties('typeRangeId', 'contractType', true)
          }
        })
      }
    }
  }
})

const components = {
  FilterText
}
</script>

<template>
  <RenderEngine schemaKey="contractTypeElement" :pageAttrs="$attrs" :schema="schema" :components="components" :scope="scope" />
</template>
