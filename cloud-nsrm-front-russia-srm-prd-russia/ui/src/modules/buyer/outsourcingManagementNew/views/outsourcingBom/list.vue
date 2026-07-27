<script setup lang="ts">
// @ts-ignore
import { onActivated } from 'vue-demi'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  dataTimeSelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import {
  defineSchemas,
  expression,
  i18nExpression,
  generateXindexInOrder,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import edit from './detail.vue'

const { emitTabAdd, t: $t, app } = usePageHelper()

onActivated(() => {
  const { from, bomHeadId } = app.$route?.params

  if (from === 'order') {
    $readOne({ bomHeadId })
  }
})

const $detailOne = (flag: string, title: any, row: any) => {
  let name = row.bomHeadId ?? ''
  emitTabAdd({
    component: edit,
    params: {
      flag,
      row,
      tabName: name ? 'bomHeadId' + name : 'bomHeadId'
    },
    title,
    name: name ? 'bomHeadId' + name : 'bomHeadId'
  })
}

// 新增
const $addOne = () => {
  $detailOne('add', $t('outsourcingBomNew.add'), {})
}

// 删除
const $deleteOne = (row: any, $queryEngine: any, $message: any) => {
  $queryEngine.request.delete(row.bomHeadId).then(() => {
    $message.success($t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

// 查看
const $readOne = (row: any) => {
  $detailOne('view', $t('outsourcingBomNew.view'), row)
}

// 修改
const $editOne = (row: any) => {
  $detailOne('edit', $t('outsourcingBomNew.edit'), row)
}

const scope = {
  app,
  $addOne,
  $deleteOne,
  $editOne,
  $readOne
}

const schema = defineSchemas({
  BomHead: {
    type: 'void',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'BomHeadBus',
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
          materialCode: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'materialName',
              propKey: 'materialCode',
              name: 'scc_base_material_item'
            }
          },
          orgId: {
            type: 'string',
            title: i18nExpression('oneStopShopping.businessEntity'), // 业务实体
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': -1,
              'node-type': 'OU',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              '@select': expression(`(node) => {
                if($form.values.query.organizationId){
                  $form.values.query.organizationId = null
                }
              }`)
            }
          },
          organizationId: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'INV',
              'select-type': 'input',
              placeholder: i18nExpression('common.pleaseSelect'),
              'parent-id': expression('$form.values.query.orgId || -1')
            }
          },
          versionCode: {
            type: 'string',
            title: i18nExpression('outsourcingBomNew.version'), // 版本
            'x-query-engine-query-operator': 'contains'
          },
          status: {
            type: 'string',
            title: i18nExpression('outsourcingBomNew.isValid'), // 是否生效
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          createdFullName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'), // 创建人
            'x-query-engine-query-operator': 'contains'
          },
          creationDate: {
            title: i18nExpression('quota.createdDate'), // 创建日期
            'x-query-engine-query-operator': 'between',
            ...dataTimeSelectorSegment
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('common.add'), // 新增
            'x-component-props': {
              type: 'primary',
              ...buttonListItemVisibleByPermission('outsourcingBomNew:create'),
              '@click': expression('() => $addOne()')
            }
          },
          importExcel: {
            type: 'void',
            'x-component': 'ImportExcel',
            'x-component-props': {
              code: 'outsourcingBomNew:import',
              title: i18nExpression('outsourcingBomNew.import'), // 自定义导入
              type: 'default',
              extraData: {
                fileModular: 'sup-ce',
                fileFunction: 'outsourcingBomList',
                fileType: 'excel'
              },
              upLoadUrl: '/api-sup-ce/sup/bom/importExcel',
              downloadTemplateOptions: {
                downloadUrl: '/api-sup-ce/sup/bom/importModelDownload',
                fileName: i18nExpression('outsourcingBomNew.importTemplate') // 委外BOM导入模板
              },
              '@handleSuccess': expression(`() => {
                    $bus.$emit('BomHeadBus')
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
          bomHeadId: { // 单据ID - 主键
            type: 'string',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          materialCode: {
            type: 'string',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression('({ row }) => $readOne(row)')
            },
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialCode'), // 物料编码
              minWidth: 120,
              customRender: true
            }
          },
          materialName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.materialName'), // 物料名称
              minWidth: 120
            }
          },
          unitName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bidMod.unit'), // 单位
              minWidth: 120
            }
          },
          orgName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('bid_mod.businessEntity'), // 业务实体
              minWidth: 120
            }
          },
          organizationName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('purchaseDemand.invOrg'), // 库存组织
              minWidth: 120
            }
          },
          versionCode: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.version'), // 版本
              minWidth: 120
            }
          },
          bomDetailDescription: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.bomDetailDescription'), // BOM明细说明
              minWidth: 120
            }
          },
          status: {
            type: 'string',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-render-table-column': {
              title: i18nExpression('outsourcingBomNew.isValid'), // 是否生效
              minWidth: 120
            }
          },
          createdFullName: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'), // 创建人
              minWidth: 120
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
              minWidth: 160
            }
          },
          lastUpdateDate: {
            type: 'Date',
            'x-hidden': true,
            'x-query-engine-sort': 'desc'
          },
          editFlag: { // 为Y表示被订单引用
            type: 'string',
            'x-hidden': true
          },
          operation: {
            type: 'void',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 120,
              fixed: 'right',
              sortable: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'), // 编辑
                // 被订单引用不可编辑
                'x-reactions': changeFieldVisibleByDeps(
                  [],
                  '$table.getRowByIndex($self.index).editFlag === \'Y\''
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourcingBomNew:update'),
                  '@click': expression('({ row }) => $editOne(row)')
                }
              },

              delete: {
                type: 'void',
                title: i18nExpression('common.delete'), // '删除'
                'x-reactions': changeFieldVisibleByDeps(
                  ['.status'],
                  '$deps[0] === \'N\''
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourcingBomNew:delete'),
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  showPopconfirm: true,
                  '@click': expression('({ row }) => $deleteOne(row, $queryEngine, $message)')
                }
              }
            }
          }
        })
      }
    }
  }
})
</script>

<template>
  <RenderEngine :schema="schema" :scope="scope" schemaKey="BomHeadList" />
</template>
