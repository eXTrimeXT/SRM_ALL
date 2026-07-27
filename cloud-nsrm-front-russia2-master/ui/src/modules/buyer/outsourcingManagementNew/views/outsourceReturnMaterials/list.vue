<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import outsourceReturnMaterialsDetail from './edit'
const { emitTabAdd, app } = usePageHelper()

const schema = defineSchemas({
  OsMaterialReturn: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          action: 'query',
          method: 'paginationQuery',
          transformRequest: expression(`(data, headers) => {
            // 添加过滤条件 去除拟定条件
            if (!data.payload?.filter?.status) {
              data.payload.filter = {
                status: { ne: 'DRAFT'},
                ...data.payload.filter
              }
            }
            return data
          }`)
        }
      }
    },
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container the_contractTemplateList_wrapper',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'osgMaterialReturnListBuyer',
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
          // 委外退料单号
          returnNum: {
            'type': 'string',
            'title': i18nExpression('outsource.returnNum')
          },
          // 单据状态
          status: {
            type: 'string',
            title: i18nExpression('outsource.outsourceReturnStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              filterItem: ['DRAFT'],
              code: 'OS_MATERIAL_RETURN_ORDER_STATUS'
            }
          },
          // 业务实体
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1,
              placeholder: i18nExpression('common.pleaseSelect')
            }
          },
          // 库存组织
          organizationId: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              placeholder: i18nExpression('common.pleaseSelect'),
              'parent-id': expression('$form.values.query.orgId || -1')
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              name: 'scc_sup_company_info_all'
            }
          },
          // 创建时间
          creationDate: {
            title: i18nExpression('common.createdFullName'),
            'x-query-engine-query-operator': 'between',
            type: 'date',
            'x-component-props': {
              placeholder: i18nExpression('common.pleaseSelectDate'),
              'value-format': 'yyyy-MM-dd',
              type: 'datetimerange',
              format: 'yyyy-MM-dd HH:mm:ss',
              'default-time': ['00:00:00', '23:59:59']
            }
          },
          // 创建人
          createdFullName: {
            'type': 'string',
            'title': i18nExpression('common.createdFullName'),
            'x-query-engine-query-operator': 'contains'
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          style: 'margin-bottom: 16px;height:28px;'
        },
        properties: {}
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          class: 'table-view-vxe-table',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          returnId: {
            type: 'string',
            'x-hidden': true
          },
          // 委外退料单号
          returnNum: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let returnId = row.returnId
                let tab = {
                  component: outsourceReturnMaterialsDetail,
                  params: {
                    flag: 'view',
                    returnId: returnId,
                    tabName: 'outsourceReturnMaterialsDetail' + row.returnNum,
                    row
                  },
                  title: row.returnNum,
                  name: 'outsourceReturnMaterialsDetail' + row.returnNum
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('outsource.returnNum'),
              minWidth: 130,
              customRender: true
            }
          },
          // 单据状态
          status: {
            type: 'string',
            title: i18nExpression('outsource.outsourceReturnStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_RETURN_ORDER_STATUS'
            },
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 业务实体
          orgName: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          orgCode: {
            type: 'string',
            'x-hidden': true
          },
          orgId: {
            type: 'string',
            'x-hidden': true
          },
          // 库存组织
          organizationName: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          organizationCode: {
            type: 'string',
            'x-hidden': true
          },
          organizationId: {
            type: 'string',
            'x-hidden': true
          },
          // 供应商编码
          vendorCode: {
            type: 'string',
            title: i18nExpression('common.vendorCode'),
            'x-render-table-column': {
              minWidth: 100
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'),
            'x-render-table-column': {
              minWidth: 150
            }
          },
          // 创建人
          createdFullName: {
            type: 'string',
            title: i18nExpression('common.createdFullName'),
            'x-render-table-column': {
              minWidth: 120
            }
          },
          // 创建时间
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)
            },
            title: i18nExpression('common.creationDate'),
            'x-render-table-column': {
              minWidth: 140
            }
          },
          lastUpdateDate: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          operation: {
            type: 'void',
            title: '{{$t(\'common.operation\')}}',
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              // DRAFT 拟定 | WAITING_BUYER_CONFIRM 待采购方确认 | BUYER_REJECT 采购商驳回 | VALID 生效
              // 编辑 （拟定）
              // 采购商 回复 (WAITING_BUYER_CONFIRM 待采购方确认)
              reply: {
                type: 'void',
                title: '{{$t("common.reply")}}',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '["WAITING_BUYER_CONFIRM"].includes($deps[0])'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceReturnMaterials:reply'),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let returnId = row.returnId
                    let tab = {
                      component: outsourceReturnMaterialsDetail,
                      params: {
                        flag: 'approve',
                        returnId: returnId,
                        row,
                        tabName: 'outsourceReturnMaterialsDetail' + row.returnNum
                      },
                      title: row.returnNum,
                      name: 'outsourceReturnMaterialsDetail' + row.returnNum
                    }
                    emitTabAdd(tab)
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
  emitTabAdd,
  app,
  i18nExpression,
  outsourceReturnMaterialsDetail
}

const components = {}

</script>

<template>
  <RenderEngine
    schemaKey="outsourceReturnMaterialsList"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
</style>
