<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  exportExcelSegment,
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import CategorySelect from 'modcb@/vendorManagementBuyer/views/quaOfReviewEngine/components/categorySelect.vue'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
const { t: $t, app } = usePageHelper()
const schema = defineSchemas({
  OrderConfig: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce'
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'refreshList',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-component': 'QueryFormByQueryEngine',
        'x-component-props': {
          immediateQueryForm: true,
          action: 'list'
        },
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          creationDate: {
            title: i18nExpression('cusEntry.orderMod.creationDate'),
            ...dataTimeSelectorSegment,
            'x-query-engine-query-operator': 'between'
          },
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1
            },
            'x-query-engine-relation-strict': true,
            'x-query-engine-relation': 'detailList'
          },
          createdBy: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.applyer'),
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'object',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'),
            'x-component': 'Button',
            'x-component-props': {
              '@click': expression(`() => {
                $form.query('table').take(field => {
                  field.componentProps.componentInstance.addRow('unshift', {
                    editable: true,
                    creatorOrgName: app.$store.getters.userInfo.ceeaCompany
                  })
                })
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
          preColumns: 'seq',
          editMode: true,
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          configId: {
            type: 'string',
            'x-hidden': true
          },
          configNum: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.configNum'),
            'x-render-table-column': {
              width: 120
            },
            'x-read-pretty': true
          },
          configName: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.configName'),
            'x-render-table-column': {
              width: 120
            },
            'x-read-pretty': '{{!$table.getRowByIndex($self.index)?.editable}}'
          },
          categoryCode: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'detailList'
          },
          categoryId: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'detailList'
          },
          categoryName: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.materialCategory'),
            'x-component': '{{ $table.getRowByIndex($self.index)?.editable ?  \'CCategorySelect\' : \'Input\'}}',
            'x-reactions': expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              $self.value = (row?.categoryList?.map(item => item.categoryName) || []).join()
            }`),
            'x-component-props': {
              'multiple': true,
              '@select': expression(`(data) => {
                let row = $table.getRowByIndex($self.index)
                row.categoryList = data?.map(item => {
                  const {
                    categoryJournalId,
                    ...other
                  } = item
                  return other
                })
              }`)
            },
            'x-render-table-column': {
              width: 150
            },
            'x-query-engine-relation': 'detailList',
            'x-read-pretty': '{{!$table.getRowByIndex($self.index)?.editable}}'
          },
          orgName: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'detailList'
          },
          orgCode: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'detailList'
          },
          orgId: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.companyName'),
            'x-component': '{{ $table.getRowByIndex($self.index)?.editable ? \'OrganizationSelector\' : \'Input\'}}',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1,
              multiple: true,
              '@select': expression(`selects => {
                /* 组件下拉选择存在bug，这里去重下 */
                let set = new Set()
                selects = selects?.filter(item => !set.has(item.organizationId) && set.add(item.organizationId))
                let row = $table.getRowByIndex($self.index)
                row.orgList = selects?.map(item => {
                  const {
                    organizationId:orgId,
                    organizationCode:orgCode,
                    organizationName:orgName
                  } = item
                  return {
                    orgId,
                    orgCode,
                    orgName
                  }
                })
              }`)
            },
            'x-reactions': expression(`() => {
              let row = $table.getRowByIndex($self.index)
              $self.value = row?.editable ? (row?.orgList?.map(item => item.orgId) || []) : row?.orgList?.map(item => item.orgName).join()
            }`),
            'x-render-table-column': {
              width: 200
            },
            'x-query-engine-relation': 'detailList',
            'x-read-pretty': '{{!$table.getRowByIndex($self.index)?.editable}}'
          },
          createdBy: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.createdBy'),
            'x-render-table-column': {
              width: 120
            },
            'x-read-pretty': true
          },
          creationDate: {
            title: i18nExpression('common.creationTime'),
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)
            },
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true
          },
          creatorOrgName: {
            type: 'string',
            title: i18nExpression('cusEntry.orderMod.creatorOrgName'),
            'x-render-table-column': {
              minWidth: 120
            },
            'x-read-pretty': true
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 120,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-component-props': {
                  '@click': expression(`({rowIndex, row}) => {
                    row.editable = true
                    const path = 'table.' + rowIndex + '.*'
                    const propsList = ['configName', 'categoryName', 'orgName']
                    $form.query(path).forEach(field => {
                      if (propsList.includes(field.props.name)) {
                        field.editable = true
                      }
                    })
                  }`)
                },
                'x-visible': '{{!$table.getRowByIndex($self.index).editable}}'
              },
              save: {
                type: 'void',
                title: i18nExpression('common.save'),
                'x-component-props': {
                  '@click': expression(`({rowIndex, row}) => {
                    $queryEngine.request.save(row, { customizeAction: 'saveOrUpdate'}).then(res => {
                      $message.success($t('common.successSave'))
                      $bus.$emit('refreshList')
                    })
                  }`)
                },
                'x-visible': '{{$table.getRowByIndex($self.index).editable ?? false}}'
              },
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    const payload = {
                      '$delete': row.configId,
                      'detailList': [{
                        '$delete': '*'
                      }]
                    }
                    $queryEngine.request.delete(payload).then(res => {
                      $message.success($t('common.successDelete'))
                      $bus.$emit('refreshList')
                    })
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
const scope = {
  app,
  $t
}
const components = {
  CategorySelect
}
</script>

<template>
  <RenderEngine
    schemaKey="autoOrderConfig"
    :scope="scope"
    :schema="schema"
    :components="components"
  />
</template>
