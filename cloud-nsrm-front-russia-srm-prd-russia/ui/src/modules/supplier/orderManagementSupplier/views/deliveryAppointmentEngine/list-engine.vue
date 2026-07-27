<script setup lang="ts">
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import editEngine from './edit-engine'
// @ts-ignore
import { deliveryAppointmentsApi } from 'mods@/orderManagementSupplier/api'
// @ts-ignore
import { transformQuery } from '@/utils'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression
} from '@meicloud/render-engine'

const { emitTabAdd, t, app, confirmMessage } = usePageHelper()

const getSubmitOrDeleteParams = async (list: any[], isSubmit: boolean) => {
  let message = ''
  let filters = []
  if (list.length < 1) {
    message = app.$message.warning(t('common.pleaseSelectMinOne'))
  }

  if (isSubmit) {
    // 提交校验
    filters = list.filter((item: any) => !['DRAFT', 'REJECT'].includes(item.deliveryAppointStatus))
    if (filters.length) {
      message = t('purchaseDemand.have') + filters.length + t('orderMod.msgVendorOrder[12]')
    }
  } else {
    // 删除校验
    filters = list.filter((item: any) => item.deliveryAppointStatus != 'DRAFT')
    if (filters.length) {
      message = t('purchaseDemand.have') + filters.length + t('orderMod.msgVendorOrder[13]')
    }
  }

  if (message) {
    app.$message.warning(message)
    return []
  }

  // 删除操作加确认提示
  if (!isSubmit) {
    const sign = await confirmMessage(t('common.confirmDelete'))
    if (sign !== 'confirm') return []
  }

  return list.map((row: any) => {
    return { deliveryAppointId: row.deliveryAppointId }
  })
}
// 提交
const handleSubmit = async ($form: any, $queryEngine: any, type: string, row?: any) => {
  const selections = $form.query('.table').take().componentProps.componentInstance.getCheckboxRecords()
  let list = type === 'mutil' ? selections : [row]
  const ids = await getSubmitOrDeleteParams(list, true)
  if (!ids.length) return

  await $queryEngine.request.baseRequest({
    type: 'DeliveryAppointVendor',
    action: 'submit',
    payload: ids,
    query: { '*': {} }
  })
  app.$message.success(t('common.successSubmit'))
  $queryEngine.state.paginationManagement.refresh()
}

// 删除
const handleDelete = async ($form: any, $queryEngine: any, type: string, row?: any) => {
  const selections = $form.query('.table').take().componentProps.componentInstance.getCheckboxRecords()
  let list = type === 'mutil' ? selections : [row]

  const ids = await getSubmitOrDeleteParams(list, false)
  if (!ids.length) return

  await $queryEngine.request.delete(ids)
  app.$message.success(t('common.successDelete'))
  $queryEngine.state.paginationManagement.refresh()
}

const openDetailTag = (type: string, row?: any) => {
  const mapInfo = new Map([
    // 跳转详情
    [
      'view',
      {
        component: editEngine,
        params: { flag: 'view', row },
        title: t('orderMod.deliveryAppointmentReceipt') + row?.deliveryAppointNumber,
        name: 'deliveryAppointmentDetail_supplier' + row?.deliveryAppointId
      }
    ],
    // 新增送货预约单据
    [
      'add',
      {
        component: editEngine,
        params: { flag: 'add' },
        title: t('orderMod.addAppointmentDeliveryNote2'),
        name: 'deliveryAppointmentDetail_supplier'
      }
    ],
    // 编辑
    [
      'edit',
      {
        component: editEngine,
        params: { flag: 'edit', row },
        title: t('orderMod.deliveryAppointmentReceipt') + row?.deliveryAppointNumber,
        name: 'deliveryAppointmentDetail_supplier' + row?.deliveryAppointId
      }
    ]
  ])

  emitTabAdd(mapInfo.get(type))
}

const scope = {
  handleSubmit,
  handleDelete,
  openDetailTag,
  transformQuery
}

const components = {}

const schema = defineSchemas({
  DeliveryAppointVendor: {
    'type': 'void',
    'x-component': 'QueryEngine',
    'x-decorator': 'el-container',
    'x-decorator-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      'actions': {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data) => {
            console.log('transformRequest=>', data)
            
            if ($values.query.deliveryNumber) {
              data.query = transformQuery(data.query,['appointDeliveryNotes.deliveryNoteId'])  
            }
            data.query['*'] = {}
            return data
          }`)
        },
        delete: {
          loading: true,
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          transformRequest: expression(`(data, headers) => {
            console.log('delete=>', data, headers)
            data.query = {
              '*': {}
            }

            return data
          }`)
        }
      }
    },
    properties: {
      // 事件总线
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'DeliveryAppointVendor',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        'type': 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        'x-decorator': 'FormItem',
        'x-validator': [],
        'x-component-props': {
          'minWidth': 100,
          'minColumns': 0,
          'maxColumns': 3,
          'columnGap': 10,
          'rowGap': 5,
          'colWrap': true,
          'labelWidth': 80,
          'immediateQueryForm': false,
          'style': {
            'opacity': 1
          }
        },
        properties: {
          // 业务实体
          orgId: {
            type: 'string',
            title: i18nExpression('dataConfMod.orgId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'node-type': 'OU',
              'parent-id': -1
            }
          },
          // 库存组织
          organizationId: {
            type: 'string',
            title: i18nExpression('dataConfMod.organizationId'),
            'x-component': 'OrganizationSelector',
            'x-component-props': {
              'parent-id': '{{$form.values.query.orgId}}',
              'node-type': 'INV',
              'scope': '{{ $form.values.query }}'
            }
          },
          // 送货预约号
          deliveryAppointNumber: {
            'type': 'string',
            'title': i18nExpression('orderMod.buyerOrderSynergy.deliveryAppointNumber'),
            'x-decorator': 'FormItem'
          },
          // 送货单号
          deliveryNumber: {
            'type': 'string',
            'title': i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'),
            'x-decorator': 'FormItem',
            'x-query-engine-query-operator': 'contains',
            'x-query-engine-relation': 'appointDeliveryNotes.deliveryNoteId:*'
          },
          // 状态
          deliveryAppointStatus: {
            type: 'string',
            title: i18nExpression('common.status'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_APPOINT_STATUS'
            }
          }
        }
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        'properties': {
          // 预约送货
          'delivery': {
            'type': 'void',
            'title': i18nExpression('orderMod.addDelivery'),
            'x-component-props': {
              'type': 'primary',
              '@click': `{{() => {    
                  openDetailTag('add')
                }
              }}`
            }
          },
          // 提交
          'submit': {
            'type': 'void',
            'title': i18nExpression('common.submit'),
            'x-component-props': {
              'type': 'default',
              '@click': `{{() => {
                  handleSubmit($form, $queryEngine, 'mutil')
                }
              }}`
            }
          },
          // 删除
          'delete': {
            'type': 'void',
            'title': i18nExpression('common.delete'),
            'x-component-props': {
              'type': 'default',
              '@click': `{{() => {
                handleDelete($form, $queryEngine, 'mutil')
                }
              }}`
            }
          }
        }
      },
      'table': {
        'type': 'array',
        'x-component': 'RenderTable',
        'x-validator': [],
        'x-component-props': {
          style: 'flex: 1',
          preColumns: 'checkbox, seq',
          openCustomTable: true,
          editMode: 'multi-row',
          // 联表主键的 key
          primaryKey: 'deliveryAppointId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'properties': {
          deliveryAppointId: {
            'type': 'void',
            'x-hidden': true
          },
          // 送货预约号
          deliveryAppointNumber: {
            type: 'string',
            default: 'xxxxxx',
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => openDetailTag("view", row)')
            },
            'x-render-table-column': {
              width: 150,
              title: i18nExpression('orderMod.buyerOrderSynergy.deliveryAppointNumber')
            }
          },
          // 业务实体
          orgName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.businessEntity'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 库存组织
          organizationName: {
            type: 'string',
            title: i18nExpression('purchaseDemand.invOrg'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 收货地址
          receiveAddress: {
            type: 'string',
            title: i18nExpression('oneStopShopping.receiveAddress'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 供应商名称
          vendorName: {
            type: 'string',
            title: i18nExpression('common.vendorName'),
            'x-render-table-column': {
              width: 150
            }
          },
          entryTime: {
            title: i18nExpression('orderMod.buyerOrderSynergy.entryTime'),
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          // 受访人员
          respondents: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.respondents'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 收货地点
          deliveryLocation: {
            type: 'string',
            title: i18nExpression('orderMod.deliveryLocation'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 车牌号码
          licensePlate: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.licensePlate'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 车辆类型
          carType: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.carType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'CAR_TYPE'
            },
            'x-render-table-column': {
              width: 150
            }
          },
          // 备注
          comments: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.comments'),
            'x-render-table-column': {
              width: 150
            }
          },
          // 状态
          deliveryAppointStatus: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.status'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_APPOINT_STATUS'
            },
            'x-render-table-column': {
              width: 100
            }
          },
          // 拒绝原因
          refusedReason: {
            type: 'string',
            title: i18nExpression('oneStopShopping.refusedReason'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 创建人
          createdFullName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.createdBy'),
            'x-render-table-column': {
              width: 120
            }
          },
          // 创建日期
          creationDate: {
            title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'),
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 130
            }
          },
          // 最后更新人
          lastUpdatedFullName: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'),
            'x-render-table-column': {
              width: 130
            }
          },
          // 最后更新日期
          lastUpdateDate: {
            title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateDate'),
            ...yearMonthDaySelectorSegment,
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              width: 130
            }
          },
          operation: {
            'type': 'void',
            'x-component': 'RenderTableButtonList',
            'x-component-props': {
              max: 2
            },
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 150,
              fixed: 'right'
            },
            'properties': {
              // 编辑
              edit: {
                type: 'void',
                title: i18nExpression('common.edit'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    openDetailTag('edit', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
                  }
                }}`
              },
              // 提交
              submit: {
                type: 'void',
                title: i18nExpression('common.submit'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    handleSubmit($form, $queryEngine, 'one', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
                  }
                }}`
              },
              // 删除
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-component-props': {
                  popconfirm: {
                    title: i18nExpression('common.confirmDelete')
                  },
                  '@click': expression(`({ row, rowIndex }) => {
                    handleDelete($form, $queryEngine, 'one', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = row.deliveryAppointStatus === 'DRAFT'
                  }
                }}`
              }
            }
          }
        }
      }
    }
  }
})
</script>
<template>
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="DeliveryAppointSupplier" />
</template>
