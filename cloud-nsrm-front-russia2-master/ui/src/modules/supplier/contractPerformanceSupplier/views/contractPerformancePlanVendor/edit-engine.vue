<script setup lang="ts">
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import IPerformPlan from '../components/i-perform-plan.vue'
import IPayformPlan from '../components/i-payform-plan.vue'
import IOrderDetail from '../components/i-order-detail.vue'
import IFileList from 'modb@/contractPerformance/views/components/i-file-list.vue'

import { useAttrs, computed } from 'vue-demi'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import performPlan from '@/service/modules/cmPerform/vendor/plan'

import { usePageHelper } from 'lib@/components/composables/usePageHelper'

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      bol: 0,
    }
  },
  PerPlan: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    items: {
      type: 'object',
      properties: {
        goBack: {
          type: 'void',
          'x-content': i18nExpression('vendorMod.goBack'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@submit': expression(`async (values) => {
              $bus.$emit('PerPlan')
              emitTabRemove(attrs.tabName)
            }`)
          }
        }
      }
    },
    properties: {
      collapse: {
        type: 'void',
        'x-component': 'FormCollapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        'x-reactions': expression(`async (id) => {
          let bol = $form.query('state').get('data').bol
          if (bol == 0) {
            $form.query('state').get('data').bol = 1
            performPlan.performPlan.getPerOrderById(attrs.params.row.perPlanId).then(async res => {
              $form.setValues(res.data)

              const list = await $milestoneTypeList(res.data.perPlanMilestoneList)
              $form.query('.IPayformPlan').take().setComponentProps({
                milestoneTypeList: list
              })
            })
          }
        }`),
        properties: {
          // 基础信息
          form: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: '{{ $t(\'vendorMod.companyBaseInfo2\') }}'
            },
            'x-reactions': expression(`async (id) => {
               $form.readPretty = $readOnly
            }`),
            properties: {
              layout: {
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
                    title: '{{$t(\'contractMod.contractNo_1\')}}',
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      'read-pretty': '{{$form.readPretty}}',
                      'show-input': '{{$values}}',
                      'show-key': 'contractNo',
                      'name': 'queryPerformContract',
                      'disabled': '{{$disabledFlag.value}}',
                      'close-quicksearch': 'writeBackContract'
                    }
                  },
                  // 里程碑模板编号
                  processNum: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: i18nExpression('contract_mod.processNum'),
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      'read-pretty': '{{$form.readPretty}}',
                      'show-input': '{{$values}}',
                      'show-key': 'processNum',
                      'name': 'scc_cont_per_templ_head',
                      'disabled': '{{$disabledFlag.value}}',
                      'close-quicksearch': 'processNumClose'
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
                      'disabled': true
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
                    title: i18nExpression('contractMod.fullPathId'),
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 合同履约单号
                  perPlanNo: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '{{ $t(\'contract_mod.perOrderNo\') }}',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 状态
                  status: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '{{ $t(\'common.status\') }}',
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
                    title: '{{ $t(\'purSettlementMod.includeTaxAmount\') }}',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 币种
                  currencyName: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: '{{ $t(\'contractMod.currencyCode\') }}',
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
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.creationTime'),
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      disabled: true
                    }
                  }
                }
              }
            }
          },
          perPlanDetailList: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: '{{ $t(\'other.key14\') }}'
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'IOrderDetail',
                'x-component-props': {
                  'data': '{{$form.values.perPlanDetailList}}'
                }
              }
            }
          },
          IPerformPlan: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: '{{ $t(\'contractMod.milestone\') }}'
            },
            properties: {
              layout: {
                type: 'void',
                'x-component': 'IPerformPlan',
                'x-component-props': {
                  'read-pretty': '{{$form.readPretty}}',
                  'data': '{{$form.values.perPlanMilestoneList}}',
                  'mode': 'manage',
                  'disabled': '{{$disabledFlag.value}}',
                  '@handover': expression(`(row, type) => {
                    $handover(row, type, $self.query('.IFileList'))
                  }`)
                }
              }
            }
          },
          payformPlan: {
            type: 'void',
            'x-component': 'FormCollapse.Item',
            'x-component-props': {
              title: '{{ $t(\'contractMod.paymentPlan\') }}'
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
                      'disabled': '{{$disabledFlag.value}}',
                      '@click': expression(`() => {
                        $form.values.perPayPlanList.push({
                          milestoneType: '',
                          nodePersonName: '',
                          nodePersonId: '',
                          nodePersonBy: '',
                          paymentStage: '',
                          payExplain: '',
                          payMethod: '',
                          paymentRatio: '',
                          stagePaymentAmount: '',
                          palnPaymentDate: null,
                          planInvoiceCompleteDate: null,
                          paymentNode: '',
                          paymentApplyNo: '',
                          performTemplLineId: null
                        })
                      }`)
                    }
                  }
                }
              },
              IPayformPlan: {
                type: 'void',
                'x-hidden': '{{!$form.values.perPlanMilestoneList || $form.values.perPlanMilestoneList.length <= 0}}',
                'x-component': 'IPayformPlan',
                'x-component-props': {
                  'read-pretty': '{{$form.readPretty}}',
                  'ref': 'payform',
                  'class': 'mt-10',
                  'data': '{{$form.values.perPayPlanList}}',
                  'mode': 'manage',
                  'disabled': '{{$disabledFlag.value}}',
                  // 'milestoneTypeList': "{{$milestoneTypeList($form.values.perPlanMilestoneList)}}",
                  '@setAmount': expression(`(row) => {
                    if (!$form.values.includeTaxAmount) return
                    row.stagePaymentAmount = Math.round((row.paymentRatio / 100) * $form.values.includeTaxAmount * 100) / 100
                  }`),
                  '@delete': expression(`(scope) => {
                    $form.values.perPayPlanList.splice(scope.$index, 1)
                  }`)
                }
              }
            }
          }
        }
      },
      IFileList: {
        type: 'void',
        'x-component': 'IFileList',
        'x-component-props': {
          '@close': expression(`() => {
            $self.setComponentProps({ show: false })
          }`)
        }
      }
    }
  }
})

const $handover = (row: any, type: any, IFileList: any) => {
  if (type === 'deliver') {
    // 确认要生成合同验收单么？
    app.$confirm(t('cusEntry.supplement20250211.confirmGenerateContractAcceptanceForm'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    }).then(() => {
      app.$router.push({
        name: 'contractPerformanceCheckVendor',
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

const $milestoneTypeList = async (perPlanMilestoneList: []) => {
  let list: any = []
  const dicts = await dictClass.getDict('MILESTONE_SCHEDULE')
  if (perPlanMilestoneList) {
    perPlanMilestoneList.forEach((item: any) => {
      dicts.forEach((innerItem: any) => {
        if (item.milestoneType === innerItem.value) {
          list.push({
            value: item.milestoneType,
            label: innerItem.label,
            id: item.performTemplLineId
          })
        }
      })
    })
  }
  return list
}

const { app, emitTabRemove, t } = usePageHelper()

let attrs: any = useAttrs()

const $disabledFlag = computed(() => {
  return !!['view', 'approval', 'manage'].includes('manage')
})

const scope = {
  app,
  t,
  attrs,
  $disabledFlag,
  emitTabRemove,
  performPlan,
  $handover,
  $milestoneTypeList
}
const components = {
  FormCollapse,
  OrganizationSelector,
  QuickSearch,
  IOrderDetail,
  IPerformPlan,
  IPayformPlan,
  IFileList
}
</script>

<template>
  <RenderEngine schemaKey="contractPerformancePlanVendorDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
