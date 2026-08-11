<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
import {
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { useAttrs } from 'vue-demi'
// @ts-ignore
import { transformDetailQuery, transformDetailDetailListItem } from '@/utils'

const { emitTabRemove, t: $t, getCurrentUserInfo } = usePageHelper()

const $attrs: any = useAttrs()
const $userInfo = getCurrentUserInfo()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryAppoint')
  emitTabRemove($attrs.tabName)
}

// @ts-ignore
const scope = {
  $userInfo,
  $attrs,
  $t,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $transformDetailQuery: transformDetailQuery,
  $transformDetailDetailListItem: transformDetailDetailListItem
}

// @ts-ignore
const components = {

}

// @ts-ignore
const schema = defineSchemas({
  // 基本信息
  DeliveryAppoint: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = true

            const { companyId, companyName, companyCode } = $userInfo
            Object.assign($form.values, {
              vendorName: companyName,
              vendorCode: companyCode,
              vendorId: companyId
            })
            return !!$attrs?.params?.row?.deliveryAppointId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs.params?.row?.deliveryAppointId || $form.values.deliveryAppointId || '']
            data.query['*'] = {}
            data.query.appointDeliveryNotes = {
              deliveryNoteId: {}
            }
            data.query = $transformDetailQuery(data.query, ['appointDeliveryNotes.deliveryNoteId'])

            return data
          }`),
          onSuccess: expression(`(res) => {
            let { appointDeliveryNotes } = res.data[0]

            if(res.originalData.ref?.DeliveryNote && appointDeliveryNotes.length){
              appointDeliveryNotes.forEach((item, index) =>{
                const {appointDeliveryNoteItem,deliveryNoteItem} = $transformDetailDetailListItem(item, res.originalData.ref,['AppointDeliveryNote.DeliveryNote'])
           
                appointDeliveryNotes.splice(index,1,{ ...appointDeliveryNoteItem,...deliveryNoteItem})
              })
            }
            
            $form.setValues({
              ...res.data[0]
            })
                       
          }`)
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.close'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $closePageAndRefreshListPageData($bus)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          deliveryAppointForm: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('supRisk.baseInfo') // 基础信息
            },
            'x-query-engine-skip': true,
            properties: {
              layout: {
                type: 'void',
                ...formGridSegment,
                properties: {
                  // 供应商名称
                  vendorName: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.vendorName'),
                    'x-decorator': 'FormItem'
                  },
                  // 业务实体
                  orgName: {
                    type: 'string',
                    title: i18nExpression('oneStopShopping.businessEntity'),
                    'x-decorator': 'FormItem'
                  },
                  // 库存组织
                  organizationName: {
                    type: 'string',
                    title: i18nExpression('purchaseDemand.invOrg'),
                    'x-decorator': 'FormItem'
                  },
                  // 收货地址
                  receiveAddress: {
                    type: 'string',
                    title: i18nExpression('oneStopShopping.receiveAddress'),
                    'x-decorator': 'FormItem'
                  },
                  // 受访人员
                  respondents: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondents'),
                    'x-decorator': 'FormItem'
                  },
                  // 受访人编号
                  respondentsNo: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsNo'),
                    'x-decorator': 'FormItem'
                  },
                  // 受访人电话
                  respondentsPhone: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsPhone'),
                    'x-decorator': 'FormItem'
                  },
                  // 受访部门
                  respondentsGound: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsGound'),
                    'x-decorator': 'FormItem'
                  },
                  // 车辆类型
                  carType: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.carType'),
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'CAR_TYPE'
                    }
                  },
                  // 车牌号码
                  licensePlate: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.licensePlate'),
                    'x-decorator': 'FormItem'
                  },
                  // 送货日期
                  entryTime: {
                    title: i18nExpression('orderMod.buyerOrderSynergy.entryTime'),
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem'
                  },
                  // 送货地点
                  deliveryLocation: {
                    type: 'string',
                    title: i18nExpression('orderMod.deliveryLocation'),
                    'x-decorator': 'FormItem'
                  },
                  // 状态
                  deliveryAppointStatus: {
                    type: 'string',
                    title: i18nExpression('common.status'),
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'DELIVERY_APPOINT_STATUS'
                    }
                  },
                  // 创建人
                  createdFullName: {
                    type: 'string',
                    title: i18nExpression('common.creator'),
                    'x-decorator': 'FormItem'
                  },
                  // 创建日期
                  creationDate: {
                    title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'),
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem'
                  },
                  // 最后更新人
                  lastUpdatedFullName: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'),
                    'x-decorator': 'FormItem'
                  },
                  // 最后更新日期
                  lastUpdateDate: {
                    title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateDate'),
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem'
                  },
                  // 备注
                  comments: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-decorator-props': { gridSpan: 4 },
                    title: i18nExpression('contractMod.remark'),
                    'x-component-props': {
                      type: 'textarea',
                      maxlength: '500',
                      showWordLimit: true,
                      autosize: { minRows: 2, maxRows: 5 }
                    }
                  }
                }
              }
            }
          },
          deliveryAppointDetail: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('orderMod.buyerOrderSynergy.appointDeliveryNotesList') // 送货单据
            },
            properties: {
              appointDeliveryNotes: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  editMode: true,
                  preColumns: 'seq',
                  pagination: false,
                  sortable: false,
                  // 联表主键的 key
                  primaryKey: 'appointDeliveryNoteId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true
                },
                'x-query-engine-skip': true,
                // 'x-query-engine-relation': 'appointDeliveryNote:*',
                'x-read-pretty': true,
                properties: generateXindexInOrder({
                  appointDeliveryNoteId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  // 送货单号
                  deliveryNumber: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  // 送货日期
                  deliveryDate: {
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.deliveryDate2'),
                      minWidth: 160
                    },
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                        ...yearMonthDaySelectorSegment['x-component-props'],
                        formatter: expression(`({ cellValue, row, column }) => {
                          parseTime(row.deliveryDate, '{y}-{m}-{d}')
                        }`)
                    }
                  },
                  // 备注
                  comments: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.comments'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  operation: {
                    type: 'void',
                    title: i18nExpression('common.operation'),
                    'x-render-table-column': {
                      width: 60,
                      fixed: 'right'
                    },
                    'x-component': 'RenderTableButtonList',
                    'x-reactions': expression(`(field) => {
                        field.visible = !$form.readPretty
                    }`),
                    properties: {
                      delete: {
                        type: 'void',
                        title: i18nExpression('common.delete'),
                        'x-component-props': {
                          type: 'text',
                          '@click': expression(`
                            ({ rowIndex }) => {
                                $table.remove(rowIndex)
                            }
                          `)
                        }
                      }
                    }
                  }
                })
              }
            }
          },
          // 来访人员
          visitorsDetail: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: i18nExpression('orderMod.buyerOrderSynergy.visitorsList')
            },
            properties: {
              deliveryAppointVisitors: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  editMode: true,
                  preColumns: 'seq',
                  pagination: false,
                  sortable: false,
                  // 联表主键的 key
                  primaryKey: 'visitorId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true
                },
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'deliveryAppointVisitors:*',
                'x-read-pretty': true,
                properties: generateXindexInOrder({
                  visitorId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  // 姓名
                  visitorName: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.visitorName'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  // 证件类型
                  idType: {
                    type: 'string',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'ID_TYPE'
                    },
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.idType'),
                      minWidth: 120
                    }
                  },
                  // 证件号码
                  idNo: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.idNo'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  // 联系电话
                  linkPhone: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.linkPhone'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  // 备注
                  comments: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.comments'),
                    'x-render-table-column': {
                      minWidth: 120
                    }
                  },
                  operation: {
                    type: 'void',
                    title: i18nExpression('common.operation'),
                    'x-render-table-column': {
                      width: 60,
                      fixed: 'right'
                    },
                    'x-component': 'RenderTableButtonList',
                    'x-reactions': expression(`(field) => {
                        field.visible = !$form.readPretty
                    }`),
                    properties: {
                      delete: {
                        type: 'void',
                        title: i18nExpression('common.delete'),
                        'x-component-props': {
                          type: 'text',
                          '@click': expression(`
                            ({ rowIndex }) => {
                                $table.remove(rowIndex)
                            }
                          `)
                        }
                      }
                    }
                  }
                })
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
  <RenderEngine
    schemaKey="DeliveryAppointDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
