<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  toJS,
  markRaw,
  connect,
  mapProps
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
// @ts-ignore
import OrgIPerformPlan from '../components/i-perform-plan.vue'
// @ts-ignore
import OrgIPayformPlan from '../components/i-payform-plan.vue'
// @ts-ignore
import OrgIOrderDetail from '../components/i-order-detail.vue'
// @ts-ignore
import IFileList from 'modb@/contractPerformance/views/components/i-file-list.vue'

import { useAttrs, computed, ref } from 'vue-demi'
// @ts-ignore
import { createDictClass } from '@/library/utils/dict/dict-utils'
// @ts-ignore
import Tinymce from '@/components/Tinymce'
// @ts-ignore
import performPlan from '@/service/modules/cmPerform/buyer/main'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import advancePaymentDetail from 'modb@/purSettlement/views/advancePayment/advancePaymentDetail'
// @ts-ignore
import paymentPlanDetail from 'modb@/purSettlement/views/purPaymentApply/paymentPlanDetail'

const IPayformPlan = connect(OrgIPayformPlan, mapProps((props, field) => {
  return {
    ...props,
    data: toJS(field.value)
  }
}))
const IOrderDetail = connect(OrgIOrderDetail, mapProps({ value: 'data' }))
const IPerformPlan = connect(OrgIPerformPlan, mapProps({ value: 'data' }))

const { emitTabRemove, t: $t, emitTabAdd } = usePageHelper()

let attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabledFlag = computed(() => {
  return !!['view', 'approval', 'manage'].includes(attrs.params.flag)
})

const viewUpdateButton = computed(() => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(workflowStatus.value))
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = !$disabledFlag.value
    componentInstance.buttonConfigInfo.close.view = $disabledFlag.value
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value

    componentInstance.setWorkflowBusinessId($form.values.perPlanId)
    componentInstance.setWorkflowTabDisabled(['DRAFT'].includes($form.values.status))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const values = $form.values

  if (type === 'SAVE') {
    $submitData('save', values, $form, $queryEngine, $confirm, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      $submitData('submit', values, $form, $queryEngine, $confirm, $message, $bus)
    })
  }
}

const schema = defineSchemas({
  PerPlan: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'cm',
      actions: {
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)
        },
        read: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)

            return !!attrs.params?.row?.perPlanId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [attrs.params.row.perPlanId || $form.values.perPlanId]

            data.query['*'] = {}

            return data
        }`),
          onSuccess: expression(`(res) => {
          $form.readPretty = $readOnly

          const detailData = $formatInitializationData(res.data[0])

          workflowStatus.value = detailData.status
          $form.setValues(detailData)
          updateButtonConfig($form)
        }`)
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('attrs.params.row?.perPlanId || null'),
          'business-type': 'performPlan',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            console.log('click-handler', type, $form, $confirm, $message)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            console.log('submit-direct', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            console.log('confirm', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            console.log('wwwwwww')
            emitTabRemove(attrs.tabName)
            $bus.$emit('ModelHead')
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-component-props': {
              defaultOpenPanelCount: 1
            },
            properties: {
              // 基础信息
              form: {
                type: 'void',
                'x-query-engine-skip': true,
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('supRisk.baseInfo')
                },
                properties: {
                  baseinfo: {
                    type: 'void',
                    'x-decorator': 'FormLayout',
                    'x-decorator-props': {
                      layout: 'vertical'
                    },
                    'x-component': 'FormGrid',
                    'x-component-props': {
                      maxColumns: 4,
                      columnGap: 32,
                      rowGap: 0
                    },
                    properties: {
                      // 合同序号
                      contractNo: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('合同序号'),
                        'x-component': 'QuickSearch',
                        'x-component-props': {
                          'read-pretty': '{{$form.readPretty}}',
                          'show-key': 'contractNo',
                          'selectClearable': false,
                          'name': 'queryPerformContract',
                          '@close-quicksearch': expression('(val) => $writeBackContract(val, $form, $queryEngine)')
                        },
                        'x-reactions': {
                          dependencies: ['contractNo'],
                          fulfill: {
                            state: {
                              'component[1].showInput': expression('$deps[0]'),
                              'component[1].disabled': expression('$disabledFlag.value')
                            }
                          }
                        }
                      },
                      // 里程碑模板编号
                      processNum: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('contract_mod.processNum'),
                        'x-component': 'QuickSearch',
                        'x-component-props': {
                          'read-pretty': '{{$form.readPretty}}',
                          'show-key': 'processNum',
                          'name': 'scc_cont_per_templ_head',
                          '@close-quicksearch': expression('(val) => $processNumClose(val, $form, $queryEngine)')
                        },
                        'x-reactions': {
                          dependencies: ['processNum', 'contractNo', 'contractClass'],
                          fulfill: {
                            state: {
                              'component[1].showInput': expression('$deps[0]'),
                              'component[1].disabled': expression('$disabledFlag.value || !$deps[1]'),
                              'component[1].preQueryData': expression('{\'t.CONTRACT_TYPE\': $deps[2]}')
                            }
                          }
                        }
                      },
                      // 里程碑模板名称
                      templateName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('contract_mod.templateName'),
                        'x-component-props': {
                          'disabled': true
                        }
                      },
                      // 供应商名称
                      vendorName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('contractMod.vendorName'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 合同类型
                      contractClass: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('contract_mod.contractType'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'ELEM_CONTRACT_TYPE',
                          disabled: true
                        }
                      },
                      // 业务实体
                      buName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('bid_mod.businessEntity'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 合同履约单号
                      perPlanNo: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('contract_mod.perOrderNo'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 状态
                      status: {
                        type: 'string',
                        default: 'DRAFT',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('状态'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'CONTRACT_PLAN_STATUS',
                          disabled: true
                        }
                      },
                      // 合同总金额（含税）
                      includeTaxAmount: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('合同总金额（含税）'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 币种
                      currencyCode: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('币种'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 创建人
                      createdFullName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creator'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      // 创建时间
                      creationDate: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creationTime'),
                        'x-component-props': {
                          disabled: true
                        }
                      }
                    }
                  }
                }
              },
              collapsItem1: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: '{{ $t(\'other.key14\') }}'
                },
                properties: {
                  perPlanDetailList: {
                    type: 'array',
                    'x-query-engine-relation': 'perPlanDetailList:*',
                    'x-component': 'IOrderDetail'
                  }
                }
              },
              IPerformPlan: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: '{{ $t(\'contractMod.milestone\') }}'
                },
                properties: {
                  perPlanMilestoneList: {
                    type: 'array',
                    'x-query-engine-relation': 'perPlanMilestoneList:*',
                    'x-component': 'IPerformPlan',
                    'x-component-props': {
                      'read-pretty': '{{$form.readPretty}}',
                      'dictClass': '{{dictClass}}',
                      'mode': 'manage',
                      'disabled': $disabledFlag.value,
                      '@handover': expression(`(row, type) => {
                            $handover(row, type, $self.query('.IFileList'), $confirm, $router)
                          }`)
                    }
                  }
                }
              },
              IPayformPlan: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: '付款计划'
                },
                properties: {
                  toolbar: {
                    type: 'void',
                    'x-component': 'Space',
                    'x-component-props': {
                      style: 'margin-bottom: 8px'
                    },
                    properties: {
                      add: {
                        type: 'void',
                        title: '{{$t(\'common.add\')}}',
                        'x-component': 'RButton',
                        'x-component-props': {
                          'type': 'primary',
                          'disabled': $disabledFlag.value,
                          '@click': expression(`() => {
                              $form.query('perPayPlanList').take(field => {
                                field.value.push({
                                  milestoneType: '',
                                  nodePersonName: '',
                                  nodePersonId: '',
                                  nodePersonBy: '',
                                  paymentStage: '',
                                  payExplain: '',
                                  payMethod: '',
                                  paymentRatio: 0,
                                  stagePaymentAmount: 0,
                                  palnPaymentDate: undefined,
                                  planInvoiceCompleteDate: undefined,
                                  paymentNode: '',
                                  paymentApplyNo: '',
                                  performTemplLineId: null
                                })
                              })
                          }`)
                        },
                        'x-reactions': {
                          dependencies: ['contractNo', 'processNum'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression('(!$deps[0] || !$deps[1])||$disabledFlag.value')
                            }
                          }
                        }
                      }
                    }
                  },
                  perPayPlanListDele: {
                    type: 'array',
                    'x-hidden': true
                  },
                  perPayPlanList: {
                    type: 'array',
                    'x-hidden': '{{!$form.values.perPlanMilestoneList || $form.values.perPlanMilestoneList.length <= 0}}',
                    'x-query-engine-relation': 'perPayPlanList:*',
                    'x-component': 'IPayformPlan',
                    'x-component-props': {
                      'mode': 'manage',
                      'disabled': $disabledFlag.value,
                      'milestoneTypeList': '{{$form.values.milestoneTypeList}}',
                      '@setAmount': expression(`(row) => {
                            console.log('setAmount', row)
                            if (!$form.values.includeTaxAmount) return
                            row.stagePaymentAmount = Math.round((row.paymentRatio / 100) * $form.values.includeTaxAmount * 100) / 100
                          }`),
                      '@delete': expression(`(scope) => {
                            //
                            if(scope.row?.perPlanId){
                              $form.values.perPayPlanList.splice(scope.$index, 1)
                              $form.query('.perPayPlanListDele').take().value.push({ $delete: scope.row.perPayPlanId })
                            } else {
                              $form.values.perPayPlanList.splice(scope.$index, 1)
                            }
                          }`),
                      '@payment': expression(`(row) => {
                            $payment(row, $form, $router)
                          }`),
                      '@advanceApply': expression(`(id) => {
                            $advanceApply(id)
                          }`),
                      '@paymentApply': expression(`(id) => {
                            $paymentApply(id)
                          }`)
                    }
                  }
                }
              }
            }
          },
          IFileList: {
            type: 'array',
            'x-query-engine-skip': true,
            'x-component': 'IFileList',
            'x-component-props': {
              '@close': expression('() => $self.setComponentProps({ show: false })')
            }
          }
        }
      }

    }
  }
})

// 请求回来的数据，无需拷贝隔离
const $formatInitializationData = (data: any) => {
  data.perPlanMilestoneList = data.perPlanMilestoneList || []
  data.perPayPlanList = data.perPayPlanList || []

  // data.perPayPlanList.forEach((item) => (item.payExplain = Number(item.payExplain)))

  // 这个是多余的，先复用原有处理吧
  data.milestoneTypeList = $milestoneTypeList(data.perPlanMilestoneList)

  return data
}

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  if (type === 'submit') {
    let { perPlanMilestoneList, perPayPlanList } = $values
    if (!perPlanMilestoneList.length) {
      $message.error('请填写里程碑相关信息')
      return
    }
    for (let item of perPlanMilestoneList) {
      if (!item.nodePersonName) {
        $message.error('里程碑---节点负责人必填')
        return
      }
      if (!item.planStartDate) {
        $message.error('里程碑---计划开始时间必填')
        return
      }
      if (!item.planEndDate) {
        $message.error('里程碑---计划结束时间必填')
        return
      }
      if (new Date(item.planStartDate).getTime() > new Date(item.planEndDate).getTime()) {
        $message.error('里程碑---计划完成时间必须晚于里程碑计划开始时间')
        return
      }
    }
    if (!perPayPlanList.length) {
      $message.error('请填写付款计划相关信息')
      return
    }
    let totalPercent = 0
    let milestoneTypeArr = []
    for (let item of perPayPlanList) {
      if (!item.milestoneType) {
        $message.error('付款计划---里程碑名称必填')
        return
      }
      if (!item.nodePersonName) {
        $message.error('付款计划---节点负责人必填')
        return
      }
      if (!item.paymentStage) {
        $message.error('付款计划---付款阶段必填')
        return
      }
      if (!item.payExplain) {
        $message.error('付款计划---付款条件必填')
        return
      }
      if (!item.payMethod) {
        $message.error('付款计划---付款方式必填')
        return
      }
      if (!item.paymentRatio) {
        $message.error('付款计划---付款比例必填')
        return
      }
      if (!item.palnPaymentDate) {
        $message.error('付款计划---计划开始时间必填')
        return
      }
      if (!item.planInvoiceCompleteDate) {
        $message.error('付款计划---计划结束时间必填')
        return
      }
      if (new Date(item.palnPaymentDate).getTime() > new Date(item.planInvoiceCompleteDate).getTime()) {
        $message.error('付款计划---计划结束时间必须晚于计划开始时间')
        return
      }
      totalPercent += item.paymentRatio
      milestoneTypeArr.push(item.milestoneType)
    }
    let typeArr = milestoneTypeArr.reduce((pre, cur) => {
      if (cur in pre) {
        pre[cur]++
      } else {
        pre[cur] = 1
      }
      return pre
    }, {})
    for (let key in typeArr) {
      if (typeArr[key] > 1) {
        $message.error('付款计划---里程碑重复，请修改')
        return
      }
    }
    if (totalPercent.toString() !== '100') {
      $message.error('付款比例相加应该等于100')
      return
    }
  } else {
    let { perPayPlanList } = $values

    for (let item of perPayPlanList) {
      if (item.perPlanId) {
        if (!item.paymentRatio) {
          $message.error('付款计划---付款比例必填')
          return
        }
        if (!item.stagePaymentAmount) {
          $message.error('付款计划---阶段付款金额必填')
          return
        }
        if (!item.payExplain) {
          $message.error('付款计划---付款条件必填')
          return
        }
        if (!item.nodePersonName) {
          $message.error('付款计划---节点负责人必填')
          return
        }
      }
    }
  }

  const form = toJS($values)
  delete form.lastUpdateDate
  delete form.creationDate
  delete form.contractCreationDate
  delete form.milestoneTypeList

  if (!form.perPlanId) {
    form.perPayPlanList.forEach((item: any) => {
      item['$perPlanMilestoneIndex'] = form.perPlanMilestoneList.findIndex((row: any) => row.perPlanMilestoneId === item.perPlanMilestoneId)
      delete item.perPlanMilestoneId
    })

    form.perPlanMilestoneList.forEach((item: any) => {
      delete item.perPlanMilestoneId
    })
  }

  if (!form.status) {
    form.status = 'DRAFT'
  }

  if ($form.query('.perPayPlanListDele').take().value) {
    form.perPayPlanList = form.perPayPlanList.concat($form.query('.perPayPlanListDele').take().value)
  }

  $queryEngine.request.save(form).then((res: any) => {
    if (res.data && res.data.length > 0) {
      $message.success($t('common.successSave'))
      const perPlanId = res.originalData?.records[0] || ''

      $form.setValues($formatInitializationData(res.data[0]))

      $queryEngine.request.read(perPlanId).then(() => {
        if (type === 'submit') {
          const tabDisabled = false
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

          componentInstance.setWorkflowBusinessId(perPlanId)
          componentInstance.setWorkflowTabDisabled(tabDisabled)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            console.log('handlerAfter ModelHead')
            emitTabRemove(attrs.tabName)
            $bus.$emit('ModelHead')
          })
        }
      })
    }
  })
}

const $handover = (row: any, type: any, IFileList: any, $confirm: any, $router: any) => {
  if (type === 'deliver') {
    $confirm('确认要生成合同验收单么？', {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('common.cancel'),
      type: 'warning'
    }).then(() => {
      $router.push({
        name: 'contractPerformanceCheck',
        params: {
          from: 'contractPerformancePlan',
          row
        }
      })
    }).catch(() => { })
  }
  if (type === 'file') {
    IFileList.take().setComponentProps({ 'show': true })
    IFileList.take().setComponentProps({ 'id': row.perPlanMilestoneId })
  }
}

const dictClass = createDictClass({
  MILESTONE_SCHEDULE: [], // 里程碑名称
  ELEM_CONTRACT_TYPE: [], // 合同类型
  CONTRACT_PLAN_STATUS: [], // 履约状态
  MILESTONE_STATE: [] // 里程碑列表状态
})

const $milestoneTypeList = (perPlanMilestoneList: []) => {
  let list: any = []
  const dicts = dictClass.getDict('MILESTONE_SCHEDULE')
  if (perPlanMilestoneList) {
    perPlanMilestoneList.forEach((item: any) => {
      dicts.forEach((innerItem: any) => {
        if (item.milestoneType === innerItem.value) {
          list.push({
            value: item.milestoneType,
            label: innerItem.label,
            id: item.performTemplLineId,
            perPlanMilestoneId: item.perPlanMilestoneId // 里程碑id
          })
        }
      })
    })
  }

  return list
}

const $writeBackContract = (data: any, $form: any, $queryEngine: any) => {
  console.log('writeBackContract', data)
  // data.contractNo =  'CN22101500003'
  if (!data || !data.contractNo) return
  const form: any = {
    contractNo: data.contractNo,
    vendorName: null,
    contractClass: null,
    buName: null,
    status: 'DRAFT',
    perPlanNo: null,
    createdFullName: null,
    includeTaxAmount: null,
    currencyCode: null,
    creationDate: null,
    processNum: null,
    templateName: null,
    perTemplHeadId: null,
    performTemplHeadId: null
  }

  $queryEngine.request.baseRequest({
      'type': 'ContractHead',
      // "lang":"zh-cn",
      'query': {
        '*': {},
        'contractMaterials': {
          '*': {}
        }
      },
      'payload': {
        'filter': {
          'contractNo': { 'eq': data.contractNo }
        }
      },
      'action': 'queryPerPlan'
  }).then((res: any) => {
    if (res?.data[0]) {
      const { contractMaterials, perPlanMilestoneList, perPayPlanList, ...rest } = res?.data[0]
      let { processNum, templateName, status, ...restCopy } = rest
      Object.assign(form, restCopy)
      form.perPlanDetailList = contractMaterials

      // perPayPlanList && perPayPlanList.forEach((item: any) => (item.payExplain = Number(item.payExplain)))

      $form.setValues(form)
    }
  })
}

const $processNumClose = (node: any, $form: any, $queryEngine: any) => {
  let attrs_ = ['processNum', 'templateName', 'perTemplHeadId']
  const form = $form.values
  for (let key of attrs_) {
    form[key] = node ? node[key] : null
  }
  form.performTemplHeadId = form.perTemplHeadId

  if (!node || !node.perTemplHeadId) return

  $queryEngine.request.baseRequest({
    type: 'PerTemplLine',
    action: 'queryMilestone',
    query: { '*': {} },
    payload: {
      filter: { perTemplHeadId: node.perTemplHeadId },
      page: { sort: 'serialNumber asc' }
    }
  }).then((res: any) => {
    if (res?.originalData?.records) {
      form.perPlanMilestoneList = res?.originalData?.records.map((id: number) => {
        const record = res.originalData.ref.PerTemplLine[id]
        record.nodePersonBy = ''
        record.nodePersonId = null
        record.nodePersonName = ''
        record.nodePlanNum = null
        return record
      })
      form.perPayPlanList = []

      const milestoneTypeList = $milestoneTypeList(form.perPlanMilestoneList)
      form.milestoneTypeList = milestoneTypeList

      $form.setValues(form)
    }
  })
}

const $payment = (row: any, $form: any, $router: any) => {
  let con = null
  if (row.paymentStage == 'ADVANCE_CHARGE') {
    con = 'advancePayment'
  } else {
    con = 'purPaymentApply'
  }
  $router.push({
    name: con,
    params: {
      from: 'contractPerformancePlan',
      row: row,
      form: $form.values
    }
  })
}

const $paymentApply = (id: string) => {
  emitTabAdd({
    component: paymentPlanDetail,
    params: {
      flag: 'readOnly',
      showType: 'approveNumber',
      paymentApplyId: id,
      tabName: 'paymentPlanDetail' + id
    },
    title: id,
    name: 'paymentPlanDetail' + id
  })
}

const $advanceApply = (id: string) => {
  emitTabAdd({
    component: advancePaymentDetail,
    params: {
      flag: 'readOnly',
      row: {
        advanceApplyId: id
      },
      showType: 'approveNumber',
      tabName: 'advancePaymentDetail' + id
    },
    title: id,
    name: 'advancePaymentDetail' + id
  })
}

const scope = {
  attrs,
  emitTabRemove,
  performPlan,
  $handover,
  dictClass: markRaw(dictClass),
  $milestoneTypeList,
  $formatInitializationData,
  $submitData,
  $processNumClose,
  $writeBackContract,
  $disabledFlag,
  $payment,
  $paymentApply,
  $advanceApply,
  $saveBill,
  initButtonConfig,
  workflowStatus,
  updateButtonConfig
}
const components = {
  FormCollapse,
  Tinymce,
  IOrderDetail,
  IPerformPlan,
  IPayformPlan,
  IFileList,
  advancePaymentDetail,
  paymentPlanDetail
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformancePlanDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
