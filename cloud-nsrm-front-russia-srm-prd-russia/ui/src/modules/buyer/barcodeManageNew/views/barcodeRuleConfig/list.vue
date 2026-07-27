<!-- eslint-disable quotes -->
<script setup lang='ts'>
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import BarcodeRuleConfigDetail from './edit'

const schema = defineSchemas({
  TagRuleConfig: {
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
          immediate: true
        }
      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'TagRuleConfig',
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
          tagRuleName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleName')}}",
            'x-query-engine-query-operator': 'contains'
          },
          tagRuleCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleCode')}}",
            'x-query-engine-query-operator': 'contains'
          },
          tagRuleType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_TYPE'
            }
          },
          status: {
            type: 'string',
            title: "{{$t('barcodeManageNew.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_STATUS'
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
              '@click': expression(`() => {
                $editTab('add',{})
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
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          tagRuleId: {
            type: 'number',
            'x-hidden': false,
            'x-query-engine-primary-key': true
          },
          tagRuleCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleCode')}}",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          tagRuleName: {
            type: 'string',
            'x-render-table-column': {
              'min-width': 150,
              title: "{{$t('barcodeManageNew.ruleName')}}",
              customRender: true
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                $editTab('view',row)
              }`)
            }
          },
          tagRuleDesc: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleDes')}}",
            'x-render-table-column': {
              'min-width': 180
            }
          },
          tagRuleType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleType')}}",
            'x-render-table-column': {
              'min-width': 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_TYPE'
            }
          },
          status: {
            type: 'string',
            title: "{{$t('barcodeManageNew.status')}}",
            'x-render-table-column': {
              'min-width': 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_STATUS'
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('barcodeManageNew.creationDate')}}",
            'x-render-table-column': {
              'min-width': 120
            },
            'x-query-engine-sort': 'desc'
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.createdFullName')}}",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              fixed: 'right',
              width: 160
            },
            'x-component-props': {
              max: 2
            },
            properties: {
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.status'], `['DRAFT','N'].includes($deps[0])`)
              },
              enable: {
                type: 'void',
                title: "{{$t('common.enable')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $enable($queryEngine,row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.status'], `['N'].includes($deps[0])`)
              },
              disable: {
                type: 'void',
                title: "{{$t('common.disable')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $disable($queryEngine,row)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['.status'], `['Y'].includes($deps[0])`)
              }
            }
          }
        })
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $editTab = (type:string, row:Object) => {
  let name, title
  if (type === 'add') {
    name = 'barcodeRuleConfigDetail'
    title = t('barcodeManageNew.barcodeManageNewAdd')
  } else {
    name = 'barcodeRuleConfigDetail' + row.tagRuleCode
    title = row.tagRuleCode
  }
  let tab = {
    component: BarcodeRuleConfigDetail,
    params: {
      flag: type,
      row,
      tabName: name
    },
    title,
    name
  }
  emitTabAdd(tab)
}

const changeStatus = ($queryEngine:any, row:any, status:String) => {
  $queryEngine.request['save']({
    ...row,
    status
  }).then(() => {
    app.$message.success(t('common.success'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const $enable = ($queryEngine:any, row:any) => {
  changeStatus($queryEngine, row, 'Y')
}

const $disable = ($queryEngine:any, row:any) => {
  changeStatus($queryEngine, row, 'N')
}

const scope = {
  $editTab,
  $enable,
  $disable
}

</script>
<template>
  <RenderEngine schemaKey="barcodeRuleConfig" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
