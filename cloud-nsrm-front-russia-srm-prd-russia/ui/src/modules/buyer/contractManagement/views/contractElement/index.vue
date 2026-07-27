<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, expression, i18nExpression, connect, mapProps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import FilterText from './filter-text.vue'
import InitComponent from './initComponent.vue'
import publicOperationProperties from '../conditionFactor/public_operation_properties'
import {
  yearMonthDaySelectorSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
import $dayjs from 'dayjs'

const scope = {
  $dayjs
}

const schema = defineSchemas({
  ElemMaintain: {
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
          elemCode: {
            type: 'string',
            title: i18nExpression('contractMod.elemCode'),
            'x-query-engine-query-operator': 'contains'
          },
          elemName: {
            type: 'string',
            title: i18nExpression('contractMod.elemName'),
            'x-query-engine-query-operator': 'contains'
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
            title: i18nExpression('common.save'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'normal',
              '@click': expression(`() => {
                $form.validate().then(() => {
                  const $table = $form.query(".table").take().componentProps.componentInstance
                  const rows = $table.getUpdateRecords()
                  if (rows.length > 0) {
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
                  } else {
                    $message.error($t('common.addOrUpdateRequired'))
                  }

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
          elemMaintainId: {
            type: 'string',
            'x-query-engine-sort': 'desc',
            'x-hidden': true
          },
          elemName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.elemName'),
              minWidth: 150
            },
            ...editTableFormItemValid
          },
          elemCode: {
            type: 'string',
            title: i18nExpression('contractMod.elemCode'),
            'x-render-table-column': {
              minWidth: 150,
              // 跳过行内编辑
              skipEditable: true
            }
          },
          addMethod: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contractMod.addMethod'),
              minWidth: 150
            },
            'x-component': 'FilterText',
            'x-component-props': {
              filterType: 'componentMap'
            },
            ...editTableFormItemValid
          },
          elemRanges: {
            type: 'string',
            'x-query-engine-relation': 'elemRanges:*',
            'x-render-table-column': {
              title: i18nExpression('contractMod.elemRanges'),
              minWidth: 150
            },
            'x-component': 'FilterText',
            'x-component-props': {
              filterType: 'elemRanges'
            },
            'x-reactions': {
              dependencies: ['.addMethod', '.elemMaintainId'],
              fulfill: {
                state: {
                  'component[1].addMethod': '{{$deps[0]}}',
                  'component[1].elemMaintainId': '{{$deps[1]}}'
                }
              }
            },
            'x-decorator': 'FormItem',
            // 'x-decorator-props': {
            //   feedbackLayout: 'popover'
            // },
            'x-validator': {
              required: false,
              message: i18nExpression('contractMod.elemRanges')
            }
          },
          initValue: {
            type: 'string',
            default: '',
            'x-render-table-column': {
              title: i18nExpression('contractMod.initValue'),
              minWidth: 150
            },
            'x-component': 'InitComponent',
            'x-component-props': {
              addMethod: ''
              // value: '',
            },
            'x-reactions': {
              dependencies: ['.addMethod', '.initValue'],
              fulfill: {
                state: {
                  'component[1].addMethod': '{{$deps[0]}}',
                  'component[1].value': '{{$deps[1]}}'
                }
              }
            },
            'x-decorator': 'FormItem',
            // 'x-decorator-props': {
            //   feedbackLayout: 'popover'
            // },
            'x-validator': {
              required: false,
              message: i18nExpression('contractMod.initValue')
            }
          },
          startDate: {
            'x-render-table-column': {
              title: i18nExpression('contractMod.startDate'),
              minWidth: 150
            },
            'x-decorator': 'FormItem',
            default: expression('Date.now()'),
            ...editTableFormItemValid,
            // 'x-decorator-props': {
            //   feedbackLayout: 'popover'
            // },
            ...yearMonthDaySelectorSegment
            // 'x-reactions': {
            //   dependencies: ['.endDate'],
            //   fulfill: {
            //     state: {
            //       'component[1].picker-options': expression(`
            //       {
            //         disabledDate(time) {
            //           return !$deps[0]
            //             ? false
            //             : (time.getTime() < Date.now()) || (time.getTime() > new Date($deps[0]).getTime())
            //         }
            //       }
            //       `)
            //     }
            //   }
            // }
          },
          endDate: {
            'x-render-table-column': {
              title: i18nExpression('contractMod.endDate'),
              minWidth: 150
            },
            'x-decorator': 'FormItem',
            // 'x-decorator-props': {
            //   feedbackLayout: 'popover'
            // },
            ...yearMonthDaySelectorSegment
            // 'x-reactions': {
            //   dependencies: ['.startDate'],
            //   fulfill: {
            //     state: {
            //       'component[1].picker-options': `{{{
            //         disabledDate(time) {
            //           return time.getTime() < new Date($deps[0]).getTime()
            //         }
            //       }}}`
            //     }
            //   }
            // }
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
            title: i18nExpression('contractMod.creationDate'),
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              format: 'yyyy-MM-dd HH:mm:ss'
            },
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
            properties: publicOperationProperties('elemMaintainId', 'elemName', true)
          }
        })
      }
    }
  }
})

const components = {
  FilterText: connect(FilterText, mapProps({ editable: 'isEdit' })),
  InitComponent: connect(InitComponent, mapProps({ editable: 'isEdit' }))
}
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :components="components"
    :scope="scope"
    schemaKey="contractElement"
  />
</template>
