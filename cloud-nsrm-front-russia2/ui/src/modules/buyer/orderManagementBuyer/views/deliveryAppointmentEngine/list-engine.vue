<script setup lang="ts">
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import editEngine from './edit-engine'
// @ts-ignore
import { transformQuery } from '@/utils'
// @ts-ignore
import { filterStatusInit } from 'lib@/utils/util'
// @ts-ignore
import {
  defineSchemas,
  expression,
  i18nExpression
} from '@meicloud/render-engine'

const { emitTabAdd, t, app } = usePageHelper()

// 抽离校验和返回id参数
const getAcceptOrRejectParams = async (list: any[], isAccept: boolean) => {
  let message = ''
  let params: any[] = []

  if (list.length < 1) {
    message = app.$message.warning(t('common.pleaseSelectMinOne'))
  }

  let noWaiting = list.filter((item: any) => item.deliveryAppointStatus != 'WAITING_CONFIRM')
  if (noWaiting.length) {
    if (isAccept) {
      message = app.$message.warning(t('purchaseDemand.have') + noWaiting.length + t('orderMod.msgOrder[20]'))
    } else {
      message = app.$message.warning(t('purchaseDemand.have') + noWaiting.length + t('orderMod.msgOrder[21]'))
    }
  }

  // 有报错提示就返回空数组
  if (message) return params

  if (isAccept) {
    params = list.map((row: any) => {
      return {
        deliveryAppointId: row.deliveryAppointId
      }
    })
  } else {
    let { value } = await app.$prompt(t('orderMod.msgRufuseReason'), t('common.tips'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      inputPattern: /\S{1,}/,
      inputErrorMessage: t('orderMod.refuseReasonRequire')
    })

    params = list.map((row: any) => {
      return {
        deliveryAppointId: row.deliveryAppointId,
        refusedReason: value
      }
    })
  }

  return params
}

// 接受
const handleAccept = async ($form: any, $queryEngine: any, type: string, row?: any) => {
  const selections = $form.query('.table').take().componentProps.componentInstance.getCheckboxRecords()
  const list = type === 'mutil' ? selections : [row]

  const params = await getAcceptOrRejectParams(list, true)
  if (!params.length) return

  await $queryEngine.request.baseRequest({
    type: 'DeliveryAppoint',
    action: 'accept',
    payload: params,
    query: { '*': {} }
  })
  app.$message.success(t('common.success'))
  $queryEngine.state.paginationManagement.refresh()
}

// 拒绝
const handleReject = async ($form: any, $queryEngine: any, type: string, row?: any) => {
  const selections = $form.query('.table').take().componentProps.componentInstance.getCheckboxRecords()
  let list = type === 'mutil' ? selections : [row]

  const params = await getAcceptOrRejectParams(list, false)
  if (!params.length) return

  await $queryEngine.request.baseRequest({
    type: 'DeliveryAppoint',
    action: 'reject',
    payload: params,
    query: { '*': {} }
  })

  app.$message.success(t('common.success'))
  $queryEngine.state.paginationManagement.refresh()
}

// 跳转详情
const viewDelivery = (row: any) => {
  emitTabAdd({
    component: editEngine,
    params: { flag: 'view', row },
    title: t('orderMod.deliveryAppointmentReceipt') + row.deliveryAppointNumber,
    name: 'deliveryAppointmentDetail_buyer' + row.deliveryAppointId
  })
}

const scope = {
  handleAccept,
  handleReject,
  viewDelivery,
  transformQuery,
  filterStatusInit
}
const components = {}
const schema = defineSchemas({
  DeliveryAppoint: {
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

            // 初始过滤状态，采购商仅可查看待确认、接受、拒绝单据
            data.payload.filter = filterStatusInit(data.payload.filter, 'deliveryAppointStatus', ['WAITING_CONFIRM', 'ACCEPT', 'REJECT'])

            if ($values.query.deliveryNumber) {
              data.query = transformQuery(data.query,['appointDeliveryNotes.deliveryNoteId'])  
            }
            data.query['*'] = {}
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
          eventName: 'DeliveryAppoint',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        'type': 'object',
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
        'properties': {
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
            'x-query-engine-relation': 'appointDeliveryNotes.deliveryNoteId'
          },
          // 供应商名称
          vendorId: {
            type: 'string',
            title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyId',
              name: 'scc_sup_company_info_all'
            }
          },
          // 状态 采购商需要过滤掉拟定状态
          deliveryAppointStatus: {
            type: 'string',
            title: i18nExpression('common.status'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'DELIVERY_APPOINT_STATUS',
              filterItem: ['DRAFT']
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
          'accept': {
            'type': 'void',
            'title': i18nExpression('orderMod.accept'),
            'x-component-props': {
              'type': 'primary',
              '@click': `{{() => {    
                  handleAccept($form, $queryEngine, 'mutil')
                }
              }}`
            }
          },
          'refused': {
            'type': 'void',
            'title': i18nExpression('common.refused'),
            'x-component-props': {
              'type': 'default',
              '@click': `{{() => {
                  handleReject($form, $queryEngine, 'mutil')
                }
              }}`
            }
          }
        }
      },
      table: {
        'type': 'array',
        'x-component': 'RenderTable',
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
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression('({ row }) => viewDelivery(row)')
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
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              formatter: expression(`({ cellValue, row, column }) => {
                parseTime(row.entryTime, '{y}-{m}-{d}')
              }`)
            },
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
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.creationDate, '{y}-{m}-{d}')
                }`)
            },
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
            'x-component-props': {
                ...yearMonthDaySelectorSegment['x-component-props'],
                formatter: expression(`({ cellValue, row, column }) => {
                  parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
                }`)
            },
            'x-query-engine-sort': 'desc',
            'x-render-table-column': {
              width: 130
            }
          },
          operation: {
            'type': 'void',
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              title: i18nExpression('common.operation'),
              width: 150,
              fixed: 'right'
            },
            'properties': {
              // 接受
              accept: {
                type: 'void',
                title: i18nExpression('orderMod.accept'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    handleAccept($form, $queryEngine, 'one', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = row.deliveryAppointStatus === 'WAITING_CONFIRM'
                  }
                }}`
              },
              // 拒绝
              refused: {
                type: 'void',
                title: i18nExpression('common.refused'),
                'x-component-props': {
                  '@click': expression(`({ row, rowIndex }) => {
                    handleReject($form, $queryEngine, 'one', row)
                  }`)
                },
                'x-reactions': `{{
                  (field) => {
                    const row = $table.getRowByIndex($self.index)
                    field.visible = row.deliveryAppointStatus === 'WAITING_CONFIRM'
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
  <RenderEngine :scope="scope" :components="components" :schema="schema" schemaKey="DeliveryAppoint" />
</template>
