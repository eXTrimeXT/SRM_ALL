<!-- eslint-disable quotes -->
<script setup lang="ts">
// @ts-ignore
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
// @ts-ignore
import { formGridSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import SelectDeliveryDialog from "./components/selectDeliveryDialog"
// @ts-ignore
import { setRepeatData, setWarningTip } from 'lib@/utils/util'
// @ts-ignore
import { parseTime, transformDetailQuery, transformDetailDetailListItem } from '@/utils'
// @ts-ignore
import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t, http, app, getCurrentUserInfo } = usePageHelper()

const $attrs: any = useAttrs()
const $userInfo = getCurrentUserInfo()

const isReadOnly = (() => {
  return $attrs.params.flag === 'view'
})()

const editPage = (() => {
  return $attrs.params.flag === 'add' || $attrs.params.flag === 'edit'
})()

const $closeTabDetail = ($bus: any) => {
  $bus.$emit('DeliveryAppointVendor')
  emitTabRemove($attrs.tabName)
}

const $remoteMethod = ($self: any, licensePlate?: any) => {
  const params: any = { pageNum: 1, pageSize: 15, status: 'EFFECTIVE' }
  if (licensePlate) {
    params.licensePlate = licensePlate
  }
  http({
    url: '/api-sup-ce/order/carInfo/listPage',
    method: 'POST',
    data: params
  }).then((res: any) => {
    $self.setDataSource(res.data.list.map((i: any) => ({
      id: i.carInfoId,
      value: i.licensePlate,
      label: i.licensePlate,
      type: i.carType
    })))
  })
}

// 选择车牌号码
const $selectlicensePlate = (licensePlate: any, $self: any, $values: any) => {
  const item = $self.dataSource.find((i) => i.value === licensePlate)
  if (item) {
    $values.carType = item.type
  }
}

// 时间disabled
const $disabledDate = (time: any) => {
  const today = new Date()
  today.setHours(0)
  today.setMinutes(0)
  today.setSeconds(0)
  today.setMilliseconds(0)
  return time.getTime() < today.getTime()
}

// 保存
const handleSave = async ($form: any, $queryEngine: any) => {
  $form.values.deliveryAppointStatus = 'DRAFT'
  const { data } = await $queryEngine.request.baseRequest({
    'action': 'saveOrUpdate',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [$form.values]
  })
  $queryEngine.request.read([data[0].deliveryAppointId])
  app.$message.success($t('common.successSave'))
}

// 提交
const handleSubmit = ($form: any, $queryEngine: any, $bus: any) => {
  $form.validate().then(async () => {
    if ($form.values.appointDeliveryNotes.length < 1) {
      return app.$message.warning($t('deliveryAppointment.prompt1')) // 送货单不能为空！
    }
    if ($form.values.deliveryAppointVisitors.length < 1) {
      return app.$message.warning($t('deliveryAppointment.prompt2')) // 来访人员不能为空！
    }

    $form.values.deliveryAppointStatus = 'WAITING_CONFIRM'
    await $queryEngine.request.baseRequest({
      'action': 'saveOrUpdate',
      'lang': 'zh-cn',
      'query': {
        '*': {}
      },
      'payload': [$form.values]
    })
    app.$message.success($t('common.success'))
    $closeTabDetail($bus)
  }).catch((err: any) => {
    setWarningTip(err)
  })
}

const $solveHandler = (type: string, $form: any, $queryEngine: any, $bus: any) => {
  if (type === 'SAVE') handleSave($form, $queryEngine)
  if (type === 'SUBMIT') handleSubmit($form, $queryEngine, $bus)
}

const addAppointDelivery = ($form: any) => {
  if (!$form.values.orgId && !$form.values.organizationId) {
    return app.$message.warning($t('purchaseDemand.openDialogWarning1'))
  }
  $form.query('.SelectDeliveryDialog').take((field: any) => {
    field.visible = true
    field.setComponentProps({
      id: $form.values.appointDeliveryNotes.map((item: any) => item.deliveryAppointId),
      visible: true,
      form: $form.values
    })
  })
}

// @ts-ignore
const scope = {
  $t,
  $attrs,
  $userInfo,
  emitTabRemove,
  $closeTabDetail,
  $remoteMethod,
  $selectlicensePlate,
  $disabledDate,
  isReadOnly,
  editPage,
  $solveHandler,
  setRepeatData,
  addAppointDelivery,
  $transformDetailQuery: transformDetailQuery,
  $transformDetailDetailListItem: transformDetailDetailListItem
}

// @ts-ignore
const components = {
  SelectDeliveryDialog
}

// @ts-ignore
const schema = defineSchemas({
  // 基本信息
  DeliveryAppointVendor: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-decorator-props': {
      onMounted: `{{() => {
        const licensePlateSelf = $form.query('licensePlate').take()
        $remoteMethod(licensePlateSelf)
      }}}`
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
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
            $form.readPretty = isReadOnly
            let { appointDeliveryNotes } = res.data[0]
            if(res.originalData.ref?.DeliveryNoteVendor && appointDeliveryNotes.length){
              appointDeliveryNotes.forEach((item, index) =>{
                const {appointDeliveryNoteItem,deliveryNoteItem} = $transformDetailDetailListItem(item, res.originalData.ref,['AppointDeliveryNoteVendor.DeliveryNoteVendor'],'Vendor')
           
                appointDeliveryNotes.splice(index,1,{ ...appointDeliveryNoteItem,...deliveryNoteItem})
              })
            }
            $form.setValues({
              ...res.data[0]
            })
           
          }`)
        },
        saveOrUpdate: {
          loading: true,
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          transformRequest: expression(`(data, headers) => {
            console.log('save=>', data, headers)
            return data
          }`),
          onSuccess: expression(`(res) => {
            $form.readPretty = isReadOnly
            let { appointDeliveryNotes } = res.data[0]
            if(res.originalData.ref?.DeliveryNoteVendor && appointDeliveryNotes.length){
              appointDeliveryNotes.forEach((item, index) =>{
                const {appointDeliveryNoteItem,deliveryNoteItem} = $transformDetailDetailListItem(item, res.originalData.ref,['AppointDeliveryNoteVendor.DeliveryNoteVendor'],'Vendor')
           
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
          'x-content': '{{ editPage ? $t("common.cancel") : $t("common.close") }}',
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $closeTabDetail($bus)
            }`)
          }
        },
        save: {
          type: 'void',
          'x-visible': '{{editPage}}',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $solveHandler('SAVE', $form, $queryEngine, $bus)
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-visible': '{{editPage}}',
          'x-content': i18nExpression('common.submit'),
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $solveHandler('SUBMIT', $form, $queryEngine, $bus)
            }`)
          }
        }
      }
    },
    properties: {
      // 送货单弹窗
      SelectDeliveryDialog: {
        type: 'void',
        'x-visible': false,
        'x-query-engine-skip': true,
        'x-component': 'SelectDeliveryDialog',
        'x-component-props': {
          '@close': expression(`(field) => {
            $self.visible = false
            $self.setComponentProps({
              visible: false
            })
          }`),
          '@confirm': expression(`(selection) => {
            setRepeatData($form.values.appointDeliveryNotes, selection, 'deliveryNoteId')
          }`)
        }
      },
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
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 业务实体
                  orgId: {
                    type: 'string',
                    title: i18nExpression('purchaseDemand.businessEntity'),
                    'x-decorator': 'FormItem',
                    'x-component': 'OrganizationSelector',
                    'x-component-props': {
                      readPretty: '{{$form.readPretty}}',
                      'parent-id': -1,
                      'node-type': 'OU',
                      '@select': expression(`(node) => {
                        $values.orgId = node ? String(node.organizationId) : null
                        $values.orgCode = node ? String(node.organizationCode) : null
                        $values.orgName = node ? node.organizationName : null

                        if (!$values.organizationId) return
                        $values.organizationId = null
                        $values.organizationName = null
                        $values.organizationCode = null
                      }`)
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('purchaseDemand.orgIdTips')
                    }
                  },
                  // 库存组织
                  organizationId: {
                    type: 'string',
                    title: i18nExpression('purchaseDemand.invOrg'),
                    'x-decorator': 'FormItem',
                    'x-component': 'OrganizationSelector',
                    'x-component-props': {
                      readPretty: '{{$form.readPretty}}',
                      'parent-id': '{{$values.orgId}}',
                      'node-type': 'INV',
                      '@select': expression(`(node) => {
                        $values.organizationId = node ? String(node.organizationId) : null
                        $values.organizationCode = node ? String(node.organizationCode) : null
                        $values.organizationName = node ? node.organizationName : null

                        if (!node) {
                          $values.receiveContact = ''
                          $values.receiveTelephone = ''
                          $values.receiveAddress = ''
                        }
                      }`)
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('purchaseDemand.organizationIdTips')
                    }
                  },
                  // 收货地址
                  receiveAddress: {
                    type: 'string',
                    title: i18nExpression('oneStopShopping.receiveAddress'),
                    'x-component': 'DictSelect',
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      code: `{{String($values.organizationId)}}`,
                      'custom-select-type': `{{$values.organizationId ? 'RECEIVE_ADDRESS' : ''}}`,
                      '@change-value': expression(`(val, {element}) => {
                        $values.receiveContact = element ? element.receiver : ''
                        $values.receiveTelephone = element ? element.receiverPhone : ''
                        $values.receiveAddress = element ? element.siteName : ''
                      }`)
                    }
                  },
                  // 受访人员
                  respondents: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondents'),
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      readPretty: '{{$form.readPretty}}',
                      propKey: 'nickname',
                      showKey: 'nickname',
                      showInput: `{{$values.respondents}}`,
                      name: 'scc_rbac_user_display',
                      '@close-quicksearch': expression(`(val) => {
                        $form.values.respondents = val ? val.nickname : ''
                        $form.values.respondentsNo = val ? val.username : ''
                        $form.values.respondentsPhone = val ? val.phone : ''
                        $form.values.respondentsGound = val ? val.department : ''
                        $form.values.respondentsGoundNumber = val ? val.ceeaDeptid : ''
                      }`)
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[27]')
                    }
                  },
                  // 受访人编号
                  respondentsNo: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsNo'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 受访人电话
                  respondentsPhone: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsPhone'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 受访部门
                  respondentsGound: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.respondentsGound'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 车辆类型
                  carType: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.carType'),
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'CAR_TYPE'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[28]')
                    }
                  },
                  // 车牌号码
                  licensePlate: {
                    type: 'string',
                    title: i18nExpression('orderMod.buyerOrderSynergy.licensePlate'),
                    'x-decorator': 'FormItem',
                    'x-component': 'Select',
                    'x-component-props': {
                      filterable: true,
                      remote: true,
                      clearable: true,
                      'automatic-dropdown': true,
                      'remote-method': '{{(licensePlate) => $remoteMethod($self, licensePlate)}}',
                      '@change': '{{(licensePlate) => $selectlicensePlate(licensePlate, $self, $values)}}'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[29]')
                    }
                  },
                  // 送货日期
                  entryTime: {
                    type: 'date',
                    default: parseTime(new Date(), '{y}-{m}-{d}'),
                    'x-decorator': 'FormItem',
                    title: i18nExpression('orderMod.buyerOrderSynergy.entryTime'),
                    'x-component-props': {
                      placeholder: i18nExpression('common.pleaseSelectDate'),
                      format: 'yyyy-MM-dd',
                      'value-format': 'yyyy-MM-dd',
                      'picker-options': expression(`{
                        disabledDate: $disabledDate
                      }`)
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[30]')
                    }
                  },
                  // 送货地点
                  deliveryLocation: {
                    type: 'string',
                    title: i18nExpression('orderMod.deliveryLocation'),
                    'x-decorator': 'FormItem',
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[31]')
                    }
                  },
                  // 状态
                  deliveryAppointStatus: {
                    type: 'string',
                    default: 'DRAFT',
                    'x-hidden': '{{$attrs.params.flag === "add"}}',
                    title: i18nExpression('common.status'),
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'DELIVERY_APPOINT_STATUS',
                      disabled: true
                    }
                  },
                  // 创建人
                  createdFullName: {
                    type: 'string',
                    'x-visible': '{{$attrs.params.flag !== "add"}}',
                    title: i18nExpression('common.creator'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 创建日期
                  creationDate: {
                    type: 'date',
                    'x-decorator': 'FormItem',
                    'x-visible': '{{$attrs.params.flag !== "add"}}',
                    title: i18nExpression('orderMod.buyerOrderSynergy.creationDate'),
                    'x-component-props': {
                      placeholder: i18nExpression('common.pleaseSelectDate'),
                      format: 'yyyy-MM-dd',
                      'value-format': 'yyyy-MM-dd',
                      disabled: true
                    }
                  },
                  // 最后更新人
                  lastUpdatedFullName: {
                    type: 'string',
                    'x-visible': '{{$attrs.params.flag !== "add"}}',
                    title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateBy'),
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 最后更新日期
                  lastUpdateDate: {
                    type: 'date',
                    'x-decorator': 'FormItem',
                    'x-visible': '{{$attrs.params.flag !== "add"}}',
                    title: i18nExpression('orderMod.buyerOrderSynergy.lastUpdateDate'),
                    'x-component-props': {
                      placeholder: i18nExpression('common.pleaseSelectDate'),
                      format: 'yyyy-MM-dd',
                      'value-format': 'yyyy-MM-dd',
                      disabled: true
                    }
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
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-reactions': expression(`(field) => {
                  field.visible = !$form.readPretty
                }`),
                properties: {
                  // 新增
                  add: {
                    type: 'void',
                    'x-component': 'RButton',
                    title: i18nExpression('common.add'),
                    'x-component-props': {
                      type: 'primary',
                      style: 'margin-bottom: 10px;',
                      disabled: '{{isReadOnly}}',
                      '@click': expression(`() => {
                        addAppointDelivery($form)
                      }`)
                    }
                  }
                }
              },
              appointDeliveryNotes: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  preColumns: 'seq',
                  editMode: false,
                  pagination: false,
                  sortable: false,
                  // 联表主键的 key
                  primaryKey: 'appointDeliveryNoteId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true
                },
                'x-query-engine-skip': true,
                // 'x-query-engine-relation': 'appointDeliveryNotes:*',
                properties: generateXindexInOrder({
                  appointDeliveryNoteId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  // 送货单号
                  deliveryNumber: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.deliveryNumber'),
                      minWidth: 120
                    }
                  },
                  // 送货日期
                  deliveryDate: {
                    type: 'date',
                    'x-decorator': 'FormItem',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.deliveryDate2'),
                      minWidth: 160
                    },
                    'x-component-props': {
                      placeholder: i18nExpression('common.pleaseSelectDate'),
                      format: 'yyyy-MM-dd',
                      'value-format': 'yyyy-MM-dd'
                    }
                  },
                  // 备注
                  comments: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.comments'),
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
              visitorsToolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-visible': '{{!$form.readPretty}}',
                properties: {
                  // 新增
                  addVisitors: {
                    type: 'void',
                    'x-component': 'RButton',
                    title: i18nExpression('common.add'),
                    'x-component-props': {
                      type: 'primary',
                      style: 'margin-bottom: 10px;',
                      disabled: '{{isReadOnly}}',
                      '@click': expression(`() => {
                        $self.query('.deliveryAppointVisitors').take().componentProps.componentInstance.addRow('unshift')
                      }`)
                    }
                  }
                }
              },
              deliveryAppointVisitors: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  class: 'table-view-vxe-table',
                  editMode: '{{!isReadOnly}}',
                  preColumns: 'seq',
                  pagination: false,
                  sortable: false,
                  // 联表主键的 key
                  primaryKey: 'deliveryAppointVisitorId',
                  // 启用级联删除的储值行为
                  cascadeDeletion: true
                },
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'deliveryAppointVisitors:*',
                properties: generateXindexInOrder({
                  deliveryAppointVisitorId: {
                    type: 'string',
                    'x-hidden': true
                  },
                  // 姓名
                  visitorName: {
                    type: 'string',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.visitorName'),
                      minWidth: 120
                    },
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      feedbackLayout: 'popover'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('vendorMod.msgNickname')
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
                    },
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      feedbackLayout: 'popover'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[23]')
                    }
                  },
                  // 证件号码
                  idNo: {
                    type: 'string',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.idNo'),
                      minWidth: 120
                    },
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      feedbackLayout: 'popover'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[24]')
                    }
                  },
                  // 联系电话
                  linkPhone: {
                    type: 'string',
                    'x-render-table-column': {
                      title: i18nExpression('orderMod.buyerOrderSynergy.linkPhone'),
                      minWidth: 120
                    },
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      feedbackLayout: 'popover'
                    },
                    'x-validator': {
                      required: true,
                      message: i18nExpression('orderMod.msgOrder[22]')
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
    schemaKey="DeliveryAppointSupplierDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
