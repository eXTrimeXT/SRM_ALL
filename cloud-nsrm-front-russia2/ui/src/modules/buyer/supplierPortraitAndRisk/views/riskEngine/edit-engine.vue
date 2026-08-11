<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'

import riskTable from './components/riskTable'
import RuleDialog from './components/ruleDialog'

import {
  requiredValidatorSegment,
  formGridSegment,
  editTableFormItemValid,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

// @ts-ignore
import { setRepeatData, throttle } from 'lib@/utils/util'
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import { adaptDictData } from '@/utils'
// @ts-ignore
import { getDictItem } from '@/api/common'

const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()

const $attrs: any = useAttrs()

const $riskPossibilityList: any = ref([
  { id: 1, label: '1-' + $t('supRisk.veryLow'), value: '1' },
  { id: 2, label: '2-' + $t('supRisk.low'), value: '2' },
  { id: 3, label: '3-' + $t('supRisk.middle'), value: '3' },
  { id: 4, label: '4-' + $t('supRisk.high'), value: '4' },
  { id: 5, label: '5-' + $t('supRisk.veryHigh'), value: '5' }
])

const $calcRiskCoefficient = ($values:any, $form:any) => {
  const { riskPossibility, riskScore } = $values
  if (riskPossibility && riskScore) {
    let count = (riskPossibility * riskScore).toFixed(2)
    $values.riskCoefficient = count
    const num = [1, 12, 13, 25, 26, 38, 39, 50]
    let max = 0
    if (count == 1) {
      max = 1
    } else {
      max = num.findIndex(i => count <= i)
    }
    const range = `${num[max - 1]}-${num[max]}`
    $values.riskLevel = range
  }
}

const $addPro = ($form:any, field:any) => {
  let flowList = $form.query('Monitoring').get('data').flowList
  // let field = $form.query('form4').take()
  const addProperties:any = {
    flowControl: {
      type: 'void',
      ...formGridSegment,
      properties: {
      }
    }

  }
  for (const item of flowList) {
    addProperties.flowControl.properties[item.value] = {
      type: 'string',
      title: item.label,
      'x-decorator': 'FormItem',
      'x-component': 'Checkbox',
      'x-component-props': {
        trueLabel: 'Y',
        falseLabel: 'N',
        disabled: expression('$form.readPretty')
      },
      'x-render-table-column': {
        minWidth: 160
      }
    }
  }
  setTimeout(() => {
    console.log('addProperties', addProperties)

    field.invoke('addProperties', addProperties)
    // field.setValue(getInviteSuppliersData($form, props, data, vendorList))
  })
}

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('RiskMonitoring')
  emitTabRemove($attrs.tabName)
}

const getFlowList = ($self:any) => {
  getDictItem('RISK_PROCESS_CONTROL').then(res => {
    $self.data.flowList = adaptDictData(res.data, 'dict')
  })
}

// 保存
const $saveData = throttle(async ($form: any, $queryEngine: any, $message: any, $bus: any, status:any, action: any) => {
  $form.validate().then(() => {
    const form = toJS($form.values)
    let checkList = $form.query('processControlsCheckBox').get('value')
    form.processControls = checkList.map(tag => ({ tag }))
    let cacheProcessControls = $form.query('state').get('data').cacheProcessControls
    if (cacheProcessControls.length) {
      cacheProcessControls.forEach(item => {
        form.processControls.push({ '$delete': item.processControlId })
      })
    }
    form.status = status
    $queryEngine.request
      .baseRequest({
        type: 'Monitoring',
        lang: 'zh-cn',
        loading: true,
        payload: [form],
        action: action || 'save',
        query: {
          '*': {}
        }
      })
      .then((res: any) => {
        app.$message.success($t('common.successSave'))
        $closePageAndRefreshListPageData($bus)
      })
  }).catch((err: any) => {
    console.log(err, 'err')
  })
}, 300)

// 打印

// @ts-ignore
const scope = {
  $attrs,
  app,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $saveData,
  $t,
  $riskPossibilityList,
  getDictItem,
  adaptDictData,
  $addPro,
  $calcRiskCoefficient,
  getFlowList

}
// @ts-ignore
const components = {
  riskTable,
  RuleDialog,
  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      cacheProcessControls: []
    }
  },
  // 基本信息
  Monitoring: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-data': {
      flowList: []

    },
    'x-reactions': expression(`async () => {
      getFlowList($self)
    }`),
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = ['view','riskClose'].includes($attrs.params.flag) 

            return $attrs.params.row.riskMonitoringId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.riskMonitoringId || $form.values.riskMonitoringId || '']
            data.query['*']= {
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            let detailData = res.data[0]
            $form.query('state').get('data').cacheProcessControls = detailData.processControls || []
            let checkList = detailData.processControls.map(i => i.tag)
            
            $form.setValues({
              ...detailData
            })  
            $form.query('processControlsCheckBox').take().setValue(checkList)
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),
          onSuccess: expression(`(res) => {

          }`),
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    items: {
      type: 'void',
      properties: {
        cancel: {
          type: 'void',
          'x-content': i18nExpression('common.cancel'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              $closePageAndRefreshListPageData($bus)
            }`)
          }
        },
        saveBill: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus,'ADD')
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty &&  ['add','edit'].includes($attrs.params.flag)
            }`)
        },
        submitData: {
          type: 'void',
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $form.validate().then(() => {
                $saveData($form, $queryEngine, $message, $bus,'APPROVAL_ADD')
              })
              
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = !$form.readPretty && ['add','edit'].includes($attrs.params.flag)
            }`)
        },
        riskUpdate: {
          type: 'void',
          'x-content': i18nExpression('supRisk.riskUpdate'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus,'MONITORING')
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = $form.values.status == 'MONITORING' && $attrs.params.flag == 'update'
            }`)
        },
        riskClose: {
          type: 'void',
          'x-content': i18nExpression('supRisk.riskClose'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $saveData($form, $queryEngine, $message, $bus,'APPROVAL_CLOSE','closed')
            }`)
          },
          'x-reactions': expression(`(field) => {
              field.visible = $form.values.status == 'MONITORING' && $attrs.params.flag == 'riskClose'
            }`)
        }

      }
    },
    properties: {
      layout: {
        type: 'void',
        ...formGridSegment,
        properties: {
          riskCode: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('supRisk.riskCode')}}",
            'x-component-props': {
              disabled: true
            }
          },
          status: {
            type: 'string',
            title: "{{$t('supRisk.status')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'RISK_MONITORING_STATUS',
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          createdFullName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('supRisk.createdName')}}",
            'x-component-props': {
              disabled: true
            }
          },
          department: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('supRisk.department')}}",
            'x-component-props': {
              disabled: true
            }
          },
          creationDate: {
            ...yearMonthDaySelectorSegment,
            'x-decorator': 'FormItem',
            title: "{{$t('supRisk.creationDate')}}",
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              disabled: true
            }
          }
        }
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        properties: generateXindexInOrder({
          form1: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: `{{$t('supRisk.title1')}}`
            },
            'x-query-engine-skip': true,
            'x-read-pretty': expression('$form.readPretty'),
            properties: {
              layout: {
                type: 'void',
                ...formGridSegment,
                properties: {
                  categoryName: {
                    type: 'string',
                    title: "{{$t('supRisk.categoryId')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      showKey: 'categoryName',
                      propKey: 'categoryName',
                      readPretty: '{{$form.readPretty}}',
                      disabled: `{{
                            $form.readPretty || $attrs.params.row.status == 'MONITORING'
                          }}`,
                      'name': 'scc_base_purchase_category2',
                      '@close-quicksearch': expression(`(val, scope) => {
                        $values.categoryId = val ? val.categoryId : ''
                        $values.categoryName = val ? val.categoryName : ''
                        $values.categoryCode = val ? val.categoryCode : ''
                      }`)
                    }
                  },
                  vendorName: {
                    type: 'string',
                    title: "{{$t('supRisk.vendorId')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      showKey: 'companyName',
                      propKey: 'companyName',
                      readPretty: '{{$form.readPretty}}',
                      disabled: `{{
                            $form.readPretty || $attrs.params.row.status == 'MONITORING'
                          }}`,
                      'name': 'scc_sup_company_info',
                      '@close-quicksearch': expression(`(val, scope) => {
                        $values.vendorCode = val ? val.companyCode : ''
                        $values.vendorName = val ? val.companyName : ''
                        $values.vendorId = val ? val.companyId : ''
                      }`)
                    },
                    ...editTableFormItemValid
                  },
                  riskDescription: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: "{{$t('supRisk.riskDescription')}}",

                    'x-component-props': {
                      disabled: `{{$attrs.params.row.status == 'MONITORING'}}`
                    }
                  },
                  empty: {
                    type: 'void',
                    'x-decorator': 'FormItem',
                    'x-hidden': true,
                    'x-query-engine-skip': true
                  }
                }
              }
            }
          },
          form2: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: `{{$t('supRisk.title2')}}`
            },
            'x-query-engine-skip': true,
            'x-read-pretty': expression('$form.readPretty'),
            properties: {
              showRuleBtn: {
                type: 'void',
                'x-component': 'div',
                "x-component-props": {
                  type: 'text',
                  '@click': expression(`() => {
                    console.log('点击',$form.query('ruleDialog').take())
                    $form.query('ruleDialog').take().setComponentProps({ visible: true })
                  }`)
                },
                properties: {
                  showRule: {
                    type: 'void',
                    'x-query-engine-skip': true,

                    'x-component': 'el-alert',
                    "x-component-props": {
                      style: {
                        'margin-bottom': '10px'
                      },
                      type: 'info',
                      'show-icon': true,
                      closable: false,
                      description: expression('$t("supRisk.alertTitle")')
                    }

                  }
                }
              },
              // showRule:{
              //   type: 'void',
              //   'x-query-engine-skip': true,

              //   'x-component': 'el-alert',
              //   "x-component-props": {
              //     style:{
              //       'margin-bottom': '10px'
              //     },
              //     type: 'info',
              //     'show-icon': true,
              //     closable: false,
              //     description: expression('$t("supRisk.alertTitle")'),
              //   },

              // },

              layout: {
                type: 'void',
                ...formGridSegment,
                properties: {
                  riskType: {
                    type: 'string',
                    title: "{{$t('supRisk.riskType')}}",
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'RISK_TYPE'
                    },
                    'x-decorator': 'FormItem'
                  },
                  riskPossibility: {
                    type: 'string',
                    title: "{{$t('supRisk.riskPossibility')}}",
                    enum: expression('$riskPossibilityList.value.map(item => ({  ...item }))'),
                    'x-component': 'Select',
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      '@change': expression(`(val) =>{
                          $calcRiskCoefficient($values,$form)
                      }`)
                    }
                  },
                  riskScore: {
                    type: 'string',
                    title: i18nExpression('supRisk.riskScore'),
                    'x-component': 'Select',
                    'x-decorator': 'FormItem',
                    enum: [
                      { id: 1, label: '1', value: '1' },
                      { id: 2, label: '2', value: '2' },
                      { id: 3, label: '3', value: '3' },
                      { id: 4, label: '4', value: '4' },
                      { id: 5, label: '5', value: '5' },
                      { id: 6, label: '6', value: '6' },
                      { id: 7, label: '7', value: '7' },
                      { id: 8, label: '8', value: '8' },
                      { id: 9, label: '9', value: '9' },
                      { id: 10, label: '10', value: '10' }
                    ],
                    'x-component-props': {
                      '@change': expression(`(val) =>{
                          $calcRiskCoefficient($values,$form)

                      }`)
                    }
                  },
                  riskCoefficient: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: "{{$t('supRisk.riskCoefficient')}}",
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  riskLevel: {
                    type: 'string',
                    title: "{{$t('supRisk.riskLevel')}}",
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'RISK_LEVEL',
                      disabled: true
                    },
                    'x-decorator': 'FormItem'
                  },
                  riskInfluencesDescription: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: "{{$t('supRisk.riskInfluencesDescription')}}",
                    'x-component-props': {
                    }
                  }
                }
              }
            }
          },
          form3: {
            ...riskTable
          },
          processControls: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: `{{$t('supRisk.title4')}}`
            },
            'x-query-engine-skip': true,
            'x-read-pretty': expression('$form.readPretty'),
            'x-query-engine-relation': 'processControls:*',
            properties: {
              'processControlsCheckBox': {
                type: 'array',
                title: '',
                default: null,
                'x-decorator': 'FormItem',
                'x-component': 'Checkbox.Group',
                enum: expression(`$form.query('Monitoring').get('data').flowList`),
                'x-component-props': {
                },
                'x-query-engine-skip': true

              }

            }
          },
          form5: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: `{{$t('supRisk.title5')}}`
            },
            'x-query-engine-skip': true,
            'x-read-pretty': expression('$form.readPretty'),
            properties: {
              layout: {
                type: 'void',
                ...formGridSegment,
                properties: {
                  // 日期
                  closeDate: {
                    'x-decorator': 'FormItem',
                    title: "{{$t('supRisk.closeDate')}}",
                    ...yearMonthDaySelectorSegment,
                    default: null,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      'value-format': 'yyyy-MM-dd',
                      disabled: true
                    }
                  },
                  riskImplementDesc: {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    title: "{{$t('supRisk.riskImplementDesc')}}",
                    'x-component-props': {
                    }
                  }

                }
              }
            }
          }
        })
      }

    }
  },
  ruleDialog: {
    'x-decorator': 'QueryEngine',
    ...RuleDialog({ scope })
  }

})
</script>

<template>
  <RenderEngine
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="RiskDetail"
  />
</template>
