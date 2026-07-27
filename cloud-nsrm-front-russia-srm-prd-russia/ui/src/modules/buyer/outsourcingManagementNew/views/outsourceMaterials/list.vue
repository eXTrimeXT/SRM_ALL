<script setup lang="ts">
import {
  defineSchemas,
  generateXindexInOrder,
  expression,
  i18nExpression,
  changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  dataTimeSelectorSegment,
  yearMonthDaySelectorSegment,
  buttonListItemVisibleByPermission
} from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import outsourceMaterialsDetail from './edit'
const { emitTabAdd, app } = usePageHelper()

// 获取委外配置： 是否启用委外领料单供应商确认功能
const $getReceiveConfirm = async ($form: any) => {
  let res = await app.$http({
    url: '/api-sup-ce/purchaseConfig/get/outsourcing/receiveConfirm',
    method: 'GET',
    loading: false
  })
  let receiveConfirmVal = res.data
  $form.query('state').get('data').receiveConfirm = res.data || false
  console.log('receiveConfirm', receiveConfirmVal)
}

const outsourceMaterialsList = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      receiveConfirm: false
    }
  },
  OsMaterialReq: {
    type: 'void',
    'x-component': 'QueryEngine',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        query: {
          immediate: true,
          method: 'paginationQuery',
          ready: expression(`() => {
            $getReceiveConfirm($form)
            return true
          }`),
          transformRequest: expression(`(data, headers) => {
            return data
          }`)
        }
      }
    },
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'osMaterialReqListBuyer',
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
          // 委外领料单号
          materialReqNum: {
            'type': 'string',
            'title': i18nExpression('outsource.materialReqNum')
          },
          // 单据状态
          handleStatus: {
            type: 'string',
            title: i18nExpression('outsource.handleStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_REQUISITION_STATUS'
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
              name: 'scc_sup_company_info_display_buyer'
            }
          },
          // 创建时间
          creationDate: {
            title: i18nExpression('common.creationDate'),
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
          style: 'margin-bottom: 16px'
        },
        properties: {
          add: {
            type: 'void',
            title: '{{$t("common.add")}}',
            'x-component-props': {
              ...buttonListItemVisibleByPermission('outsourceMaterials:create'),
              type: 'primary',
              '@click': expression(`() => {
                let tab = {
                  component: outsourceMaterialsDetail,
                  params: {
                    flag: 'add',
                    receiveConfirm: $form.query('state').get('data').receiveConfirm,
                    tabName: 'outsourceMaterialsDetail'
                  },
                  ctrlHeight: true,
                  title: $t('outsource.addMaterialReqNum'),// '新增委外领料单'
                  name: 'outsourceMaterialsDetail'
                }
                emitTabAdd(tab)
              }`)
            }
          }
        }
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
          materialReqId: {
            type: 'string',
            'x-hidden': true
          },
          // 委外领料单号
          materialReqNum: {
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              '@click': expression(`({row}) => {
                let materialReqId = row.materialReqId
                let tab = {
                  component: outsourceMaterialsDetail,
                  params: {
                    flag: 'view',
                    materialReqId: materialReqId,
                    receiveConfirm: $form.query('state').get('data').receiveConfirm,
                    tabName: 'outsourceMaterialsDetail' + row.materialReqNum,
                    row
                  },
                  title: row.materialReqNum,
                  name: 'outsourceMaterialsDetail' + row.materialReqNum
                }
                emitTabAdd(tab)
              }`)
            },
            'x-render-table-column': {
              title: i18nExpression('outsource.materialReqNum'),
              minWidth: 130,
              customRender: true
            }
          },
          // 状态
          handleStatus: {
            type: 'string',
            title: i18nExpression('outsource.handleStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'OS_MATERIAL_REQUISITION_STATUS'
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
            type: 'string',
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
            title: '{{$t("common.operation")}}',
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            properties: {
              // CREATE 拟定 | SUBMIT 已提交  | REFUSE 驳回 | VALID 已生效
              // 编辑 （拟定）
              edit: {
                type: 'void',
                title: '{{$t("common.edit")}}',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.handleStatus'],
                  '["CREATE"].includes($deps[0])'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterials:create'),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let materialReqId = row.materialReqId
                    let tab = {
                      component: outsourceMaterialsDetail,
                      params: {
                        flag: 'edit',
                        materialReqId: materialReqId,
                        receiveConfirm: $form.query('state').get('data').receiveConfirm,
                        tabName: 'outsourceMaterialsDetail' + row.materialReqNum,
                        row
                      },
                      title: row.materialReqNum,
                      name: 'outsourceMaterialsDetail' + row.materialReqNum
                    }
                    emitTabAdd(tab)
                  }`)
                }
              },
              // 删除 （拟定）
              delete: {
                type: 'void',
                title: '{{$t("common.delete")}}',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.handleStatus'],
                  '["CREATE"].includes($deps[0])'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterials:create'),
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression(`({ row }) => {
                    $queryEngine.request.delete(row.materialReqId).then(() => {
                      $message.success($t('common.successDelete'))
                      $queryEngine.state.paginationManagement.refresh()
                    })
                  }`)
                }
              },
              // 采购商 回复 (已驳回)
              replay: {
                type: 'void',
                title: '{{$t("common.reply")}}',
                'x-reactions': changeFieldVisibleByDeps(
                  ['.handleStatus'],
                  '["REFUSE"].includes($deps[0]) && $form.query("state").get("data").receiveConfirm'
                ),
                'x-component-props': {
                  ...buttonListItemVisibleByPermission('outsourceMaterials:reply'),
                  type: 'text',
                  '@click': expression(`({ row }) => {
                    let materialReqId = row.materialReqId
                    let tab = {
                      component: outsourceMaterialsDetail,
                      params: {
                        flag: 'approve',
                        materialReqId: materialReqId,
                        row,
                        receiveConfirm: $form.query('state').get('data').receiveConfirm,
                        tabName: 'outsourceMaterialsDetail' + row.materialReqNum
                      },
                      title: row.materialReqNum,
                      name: 'outsourceMaterialsDetail' + row.materialReqNum
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
  $getReceiveConfirm,
  outsourceMaterialsDetail
}

const components = {}

</script>

<template>
  <RenderEngine
    schemaKey="outsourceMaterialsList"
    :schema="outsourceMaterialsList"
    :scope="scope"
    :components="components"
  />
</template>

<style lang="scss">
</style>
