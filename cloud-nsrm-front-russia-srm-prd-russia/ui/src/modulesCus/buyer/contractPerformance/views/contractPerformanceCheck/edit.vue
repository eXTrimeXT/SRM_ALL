<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  i18nExpression,
  connect,
  mapProps,
  toJS, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
import ApprovalProcess from 'modc@/components/approval-process'
// @ts-ignore
import { useAttrs, computed, ref } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import OriginIFieldView from '../components/i-field-view.vue'
import omit from 'lodash/omit'
import { deppOmit } from '@/utils/util'
import { systemUrl } from '@/config/sysConfig'

const IFieldView = connect(OriginIFieldView, mapProps({ value: 'data' }))

const { http, app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabled = ['view', 'approval'].includes(attrs.params.flag)

const customUpdateButton = computed(() => (!$disabled && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = computed(() => (!$disabled && !['APPROVED', 'SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))

const schema = defineSchemas({
  // pdf 打印
  pdfIframe: {
    type: 'void',
    'x-component': 'iframe',
    'x-component-props': {
      id: 'pdfIframe',
      // src: '#',
      style: {
        display: 'none'
      }
    }
  },
  PerAcceptance: {
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
        queryMilestone: {
          immediate: true,
          method: 'read',
          ready: expression(`() => {
            return $attrs.params && $attrs.params.fromContractPerformancePlan
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.action = 'queryMilestone'
            data.query = {
              "*":{},
              "perAcceptanceConfList": {'*': {}},
              "perAcceptanceAttList": {'*': {}},
              "perPlanMilestoneId": {'*': {}},
              "perPlanId": {'*': {}},
              "perTemplLineId": { configList: {'*': {}}},
            }
            data.payload = {
              filter: {
                perPlanMilestoneId: {
                  eq: $attrs.params.row.perPlanMilestoneId
                }
              }
            }

            return data
          }`),
          transformResponse: expression(`(res) => {
            const data = JSON.parse(res)
            if (data.data.ref) {
              const perAcceptanceItem = {}

              // 可以通过 perPlanMilestoneId 查询到对应的履约计划数据
              if (data.data.type === '[PerAcceptance]' && data.data.ref.PerAcceptance) {
                const perAcceptanceId = Object.keys(data.data.ref.PerAcceptance)[0]

                Object.assign(perAcceptanceItem, data.data.ref.PerAcceptance[perAcceptanceId])

                if (perAcceptanceItem.perAcceptanceConfList) {
                  perAcceptanceItem.perAcceptanceConfList = perAcceptanceItem.perAcceptanceConfList.map(id => {
                    return data.data.ref.PerAcceptanceConf[id]
                  })
                }

                if (perAcceptanceItem.perAcceptanceAttList) {
                  perAcceptanceItem.perAcceptanceAttList = perAcceptanceItem.perAcceptanceAttList.map(id => {
                    return data.data.ref.PerAcceptanceAtt[id]
                  })
                }

                if (perAcceptanceItem.perPlanId) {
                  perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perAcceptanceItem.perPlanId]
                }

                if (perAcceptanceItem.perPlanMilestoneId) {
                  perAcceptanceItem.perPlanMilestoneId = data.data.ref.PerPlanMilestone[perAcceptanceItem.perPlanMilestoneId]
                }
              } else {
                // 反之是查不到
                const perPlanId = Object.keys(data.data.ref.PerPlan)[0]
                perAcceptanceItem.perPlanId = data.data.ref.PerPlan[perPlanId]

                const perPlanMilestoneId = Object.keys(data.data.ref.PerPlanMilestone)[0]
                perAcceptanceItem.perPlanMilestoneId =
                  data.data.ref.PerPlanMilestone[perPlanMilestoneId]

                if (perAcceptanceItem.perPlanMilestoneId.perTemplLineId) {
                  const perTemplLine = data.data.ref.PerTemplLine[perAcceptanceItem.perPlanMilestoneId.perTemplLineId]

                  if (perTemplLine && perTemplLine.configList) {
                    perAcceptanceItem.perAcceptanceConfList = perTemplLine.configList.map(id => {
                      return data.data.ref.PerTemplLineConfig[id]
                    })
                  }
                }
              }

              // 设置文本只读
              $form.readPretty = $readOnly || $disabled
              $form.setValues({
                ...perAcceptanceItem,
                status: perAcceptanceItem.status || 'DRAFT',
                perAcceptanceNo: perAcceptanceItem.perAcceptanceNo,
                perAcceptanceId: perAcceptanceItem.perAcceptanceId,
                perPlanMilestoneId: [perAcceptanceItem.perPlanMilestoneId],
                perPlanId: perAcceptanceItem.perPlanId,
                perAcceptanceAttList: perAcceptanceItem.perAcceptanceAttList || [],
                perAcceptanceConfList: perAcceptanceItem.perAcceptanceConfList || [],
              })
              if (['DRAFT', 'SUPPLIER_SUBMITTED', 'WITHDRAW', 'REJECTED'].includes($form.values.status)) {
                const xData = $form.query(\'PerAcceptance\').get(\'data\')
                xData.showButtonConfig = {
                  saveAndNextStep: true
                }
                xData.showTabConfig = {
                  approval: true
                }
              }
            }

            return data
          }`)
        },
        query: {
          immediate: true,
          ready: expression(`() => {
            return !!(
              $attrs.params
              && $attrs.params.row
              && !$attrs.params.fromContractPerformancePlan
              && ($attrs.params.row.perAcceptanceId || $attrs.params.row.perPlanMilestoneId)
            )
          }`),
          transformRequest: expression(`(data, headers) => {
            if (!data.payload.filter) {
              data.payload.filter = {}

              if ($attrs.params.row) {
                if ($attrs.params.row.perAcceptanceId) {
                  data.payload.filter.perAcceptanceId = {
                    eq: $attrs.params.row.perAcceptanceId
                  }
                }

                // 履约计划调跳转过来生成合同验收单的查询
                if ($attrs.params.row.perPlanMilestoneId) {
                  data.payload.filter.perPlanMilestoneId = {
                    eq: $attrs.params.row.perPlanMilestoneId
                  }
                }
              }
            }

            delete data.payload.page

            data.query['*'] = {}

            return data
          }`),
          onSuccess: expression(`(res) => {
            // 设置文本只读
            $form.readPretty = $readOnly || $disabled

            if (res.data[0]) {
              // UI 呈现需要是数组，实际是单个对象
              res.data[0].perPlanMilestoneId = [res.data[0].perPlanMilestoneId].filter(Boolean)
            }
            if (res.data[0] && ['DRAFT', 'SUPPLIER_SUBMITTED', 'WITHDRAW', 'REJECTED'].includes(res.data[0].status)) {
              const xData = $form.query(\'PerAcceptance\').get(\'data\')
              xData.showButtonConfig = {
                saveAndNextStep: true
              }
              xData.showTabConfig = {
                approval: true
              }
            }
            $form.setValues(res.data[0])
          }`)
        },
        vendorSubmit: {
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`res => {
            $form.values.perAcceptanceId = res.data[0]?.perAcceptanceId
          }`)
        }
      }
    },
    'x-data': {
      showButtonConfig: {
        saveAndNextStep: false
      },
      showTabConfig: {}
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'ApprovalProcess',
        'x-component-props': {
          'business-id': expression(`$form.values.perAcceptanceId || null`),
          'business-type': 'performAcceptance',
          approvalStatus: expression(`$form.values.status || 'DRAFT'`),
          'operation-pre-options': expression(`$wrapper($preOptions, $root)`),
          'show-button-config': expression('$form.query(\'PerAcceptance\').get(\'data\').showButtonConfig'),
          'show-tab-config': expression('$form.query(\'PerAcceptance\').get(\'data\').showTabConfig'),
          readonly: expression('$attrs.params.flag === \'view\''),
          '@approval-handler-callback': expression(`(type) => {
            $approvalHanlder(type, $form, $queryEngine, $bus)
          }`)
        },
        properties: {
          customButtonList: {
            type: 'void',
            'x-component': 'ButtonList',
            'x-component-props': {
              style: {
                'margin-right': '8px'
              }
            },
            'x-slot': 'custom',
            properties: {
              // 供应商驳回按钮
              refuse: {
                type: 'void',
                'x-content': i18nExpression('components.approvalHead.headers.refuse'),
                'x-component': 'Button',
                'x-component-props': {
                  type: 'default',
                  '@click': expression(`() => {
                    console.log($values, '$values')
                    $values.status = 'FIRST_REJECTED'
                    $queryEngine.request.save($values).then(() => {
                      $message.success(t('common.successSave'))
                      $bus.$emit('PerAcceptance')
                      emitTabRemove($attrs.tabName)
                    })
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['status'], `
                    $deps[0] === 'SUPPLIER_SUBMITTED' && $attrs.params.flag !== 'view'
                    `)
              },
              // 打印
              pdfPrint: {
                type: 'void',
                'x-content': i18nExpression('route.pdfPrint'),
                'x-component': 'Button',
                'x-component-props': {
                  type: 'default',
                  style: {
                    'margin-left': '8px'
                  },
                  '@click': expression(`() => {
                    $openPrint($form)
                  }`)
                },
                'x-reactions': changeFieldVisibleByDeps(['status'], `
                    $deps[0] === 'APPROVED'
                `)
              }
            }
          },
          perPlanId: {
            type: 'string',
            'x-query-engine-skip': true,
            'x-query-engine-relation': 'perPlanId:*',
            'x-hidden': true
          },
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            properties: {
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                'x-component-props': {
                  defaultOpenPanelCount: 1,
                  id: 'printContent'
                },
                properties: {
                  // 基础信息
                  baseInfo: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('supRisk.baseInfo')
                    },
                    'x-query-engine-skip': true,
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
                          'perPlanId.contractNo': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            'x-component': 'Input',
                            title: '合同序号',
                            'x-component-props': {
                              disabled: true
                            }
                          },
                          // 里程碑模板编号
                          'perPlanId.processNum': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('contract_mod.processNum'),
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 里程碑模板名称
                          'perPlanId.templateName': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('contract_mod.templateName'),
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 供应商名称
                          'perPlanId.vendorName': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('common.vendorName'),
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 合同类型
                          'perPlanId.contractClass': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('contract_mod.contractType'),
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              'disabled': true,
                              code: 'ELEM_CONTRACT_TYPE'
                            }
                          },
                          // 业务实体
                          'perPlanId.buName': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('cusEntry.vendorMod.orgName'),
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 合同验收单号
                          perAcceptanceNo: {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: '合同验收单号',
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 状态
                          status: {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: '状态',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'CONTRACT_CHECK_STATUS',
                              'disabled': true
                            }
                          },
                          // 合同履约计划单号
                          'perPlanId.perPlanNo': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: '合同履约计划单号',
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 合同总金额（含税）
                          'perPlanId.includeTaxAmount': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: '合同总金额（含税）',
                            'x-component-props': {
                              'disabled': true
                            }
                          },
                          // 币种
                          'perPlanId.currencyCode': {
                            type: 'string',
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'currency',
                              'disabled': true
                            },
                            'x-decorator': 'FormItem',
                            title: '币种'
                          },
                          // 创建人
                          'perPlanId.createdFullName': {
                            type: 'string',
                            'x-decorator': 'FormItem',
                            title: i18nExpression('common.creator'),
                            'x-disabled': true
                          },
                          // 创建时间
                          'perPlanId.creationDate': {
                            ...yearMonthDaySelectorSegment,
                            default: undefined,
                            'x-decorator': 'FormItem',
                            title: i18nExpression('common.creationTime'),
                            'x-disabled': true
                          }
                        }
                      }
                    }
                  },
                  // 里程碑
                  perPlanMilestone: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('contractMod.milestone')
                    },
                    properties: {
                      perPlanMilestoneId: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          class: 'table-view-vxe-table',
                          preColumns: 'seq',
                          pagination: false,
                          maxHeight: '45vh'
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'perPlanMilestoneId:*',
                        properties: generateXindexInOrder({
                          // 节点
                          serialNumber: {
                            type: 'string',
                            title: i18nExpression('components.processTable.headers.fdNodeName'),
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          // 里程碑名称
                          milestoneType: {
                            type: 'string',
                            title: i18nExpression('contract_mod.processNodeName'),
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'MILESTONE_SCHEDULE'
                            },
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          // 节点负责人
                          nodePersonName: {
                            type: 'string',
                            title: '节点负责人',
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          // 计划开始时间
                          planStartDate: {
                            title: i18nExpression('perfMod.planStartDate'),
                            ...yearMonthDaySelectorSegment,
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          // 计划结束时间
                          planEndDate: {
                            title: '计划结束时间',
                            ...yearMonthDaySelectorSegment,
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          // 节点交付数量
                          nodePlanNum: {
                            type: 'number',
                            'x-disabled': expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),
                            'x-render-table-column': {
                              title: '节点交付数量',
                              minWidth: 130
                            },
                            ...editTableFormItemValid
                          },
                          // 实际结束时间
                          practicallyEndDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-disabled': expression(`
                            $disabled
                              || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                              || $form.values.dataCreationType === 'VENDOR'
                            `),
                            'x-render-table-column': {
                              title: '实际结束时间',
                              minWidth: 130
                            },
                            ...editTableFormItemValid
                          },
                          // 特殊备注
                          remarks: {
                            type: 'string',
                            title: '特殊备注',
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          fileName: {
                            type: 'string',
                            'x-hidden': true,
                            'x-query-engine-skip': true
                          },
                          fileId: {
                            type: 'string',
                            'x-hidden': true,
                            'title': i18nExpression('dataConfMod.attachmentTemplate'),
                            'x-component': 'SrmCommonFile',
                            'x-component-props': {
                              'readonly': true,
                              'default-file': {
                                fileId: '{{$table.getRowByIndex($self.index)?.fileId}}',
                                fileName: '{{$table.getRowByIndex($self.index)?.fileName}}'
                              }
                            },
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          }
                        })
                      }
                    }
                  },
                  // 履约过程评价
                  perAcceptanceConf: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: '履约过程评价'
                    },
                    'x-hidden': true,
                    properties: {
                      perAcceptanceConfList: {
                        type: 'string',
                        'x-component': 'IFieldView',
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'perAcceptanceConfList:*',
                        default: [],
                        'x-component-props': {
                          disabled: expression('$disabled')
                        }
                      }
                    }
                  },
                  // 交付说明
                  delivery: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: '验收意见'
                    },
                    properties: {
                      // 交付说明
                      deliveryExplain: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        'x-component': 'Input.TextArea',
                        'x-component-props': {
                          disabled: '{{$disabled}}',
                          rows: '3',
                          maxlength: '300',
                          'show-word-limit': true
                        }
                      }
                    }
                  },
                  // 相关附件
                  relevantAttachment: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('accountMod.relevantAttachment')
                    },
                    properties: {
                      toolbar: {
                        type: 'void',
                        'x-component': 'Space',
                        'x-component-props': {
                          style: 'margin-bottom: 16px'
                        },
                        properties: {
                          add: {
                            type: 'void',
                            title: i18nExpression('common.add'),
                            'x-component': 'RButton',
                            'x-component-props': {
                              type: 'primary',
                              'disabled': '{{$disabled}}',
                              '@click': expression(`(rowIndex) => {
                                $form.query(".perAcceptanceAttList").take().componentProps.componentInstance.addRow()
                              }`)
                            }
                          }
                        }
                      },
                      perAcceptanceAttList: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-read-pretty': true,
                        'x-component-props': {
                          maxHeight: '45vh',
                          class: 'table-view-vxe-table',
                          preColumns: 'seq',
                          editMode: true,
                          pagination: false
                        },
                        'x-query-engine-skip': true,
                        'x-query-engine-relation': 'perAcceptanceAttList:*',
                        properties: generateXindexInOrder({
                          fileName: {
                            type: 'string',
                            title: i18nExpression('vendorMod.attachmentUpload'),
                            'x-read-pretty': expression('$disabled && $buyer()'),
                            'x-component': 'SrmCommonFile',
                            'x-component-props': {
                              'extra-data': {
                                fileModular: 'sup',
                                fileFunction: 'contractPerformanceCheck',
                                fileType: 'images'
                              },
                              'default-file': {
                                fileId: '{{$table.getRowByIndex($self.index).fileId}}',
                                fileName: '{{$self.value}}'
                              },
                              '@on-change': expression(`({file}) => {
                                let row = $table.getRowByIndex($self.index)
                                const { fileId = '', fileName = '' } = file || {}
                                row.fileId = fileId.toString()
                                $self.value = fileName
                                row.createdUserName = file.createdBy
                                row.creationDate = file.creationDate
                              }`)
                            },
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          createdUserName: {
                            type: 'string',
                            title: i18nExpression('components.fileupload.uploadUserName'),
                            'x-render-table-column': {
                              minWidth: 130
                            }
                          },
                          creationDate: {
                            type: 'string',
                            title: i18nExpression('outsource.creationDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          operation: {
                            type: 'void',
                            title: '{{$t(\'common.operation\')}}',
                            'x-render-table-column': {
                              minWidth: 120,
                              fixed: 'right'
                            },
                            properties: {
                              layout: {
                                type: 'void',
                                'x-component': 'FormButtonGroup',
                                properties: {
                                  edit: {
                                    type: 'void',
                                    title: i18nExpression('common.delete'),
                                    'x-component': 'TableButton',
                                    'x-component-props': {
                                      type: 'text',
                                      disabled: '{{$disabled}}',
                                      '@click': expression(`({row, rowIndex}) => {
                                        $table.remove(rowIndex)
                                      }`)
                                    }
                                  }
                                }
                              }
                            }
                          }
                        })
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('PerAcceptance')
}

const getFormDetail = (data: any = {}, $form: any, $queryEngine: any) => {
  return $queryEngine.request.query({
    perAcceptanceId: data?.perAcceptanceId,
    perPlanMilestoneId: data?.perPlanMilestoneId?.perPlanMilestoneId
  })
    .then((res: any) => {
      workflowStatus.value = res.data.status
      $form.setValues(res.data)
      return res.data
    })
}

const $saveBill = async (type: string, $form: any, $queryEngine: any, $bus: any) => {
  let validResult = true
  // 临时去除
  let data = deppOmit($form.values, ['lastUpdateDate', 'creationDate', 'creationDate', 'contractCreationDate', 'currentPlanEndDate'])
  // TODO 后端暂时不处理这样冗余数据，前端先进行删除再入库
  data = omit(data, [
    'perPlanId.contractClass',
    'perPlanId.contractNo',
    'perPlanId.includeTaxAmount',
    'perPlanId.vendorName',
    'perPlanId.buName',
    'perPlanId.currencyName',
    'perPlanId.templateName',
    'perPlanId.perPlanNo',
    'perPlanId.processNum',
    'perPlanMilestoneId.serialNumber',
    'perPlanMilestoneId.milestoneType',
    'perPlanMilestoneId.nodePersonName',
    'perPlanMilestoneId.planStartDate',
    'perPlanMilestoneId.planEndDate',
    'perPlanMilestoneId.remarks',
    'perPlanMilestoneId.fileId',
    'perAcceptanceAttList.fileName',
    'perAcceptanceAttList.createdUserName'
  ])

  if (type === 'SAVE') {
    $submitData('save', data, $form, $queryEngine, $bus)
  } else if (type === 'SUBMIT') {
    await $form.validate().then(async () => {
      validResult = await $submitData('submit', data, $form, $queryEngine, $bus)
    }).catch(err => {
      validResult = false
    })
  } else if (type === 'PASS' || type === 'REJECT') {
    let { approvePass, rejected } = performPlanService.performAcceptance
    let saveMethods: any = type === 'PASS' ? approvePass : rejected
    saveMethods(data).then((res: any) => {
      app.$message.success(res.message)
      $back($bus)
    })
  }
  return validResult
}

const $submitData = async (type: string, $values: any, $form: any, $queryEngine: any, $bus: any) => {
  if (type === 'submit') {
    // $values.status = 'SUBMITTED'
    for (let i = 0; i < $values.perPlanMilestoneId.length; i += 1) {
      if (!$values.perPlanMilestoneId[i].practicallyEndDate) {
        app.$message.warning('请填写里程碑---实际结束时间')
        return
      }
    }
  }

  let fileNum = 0;

  ($values.perAcceptanceAttList || []).forEach((row: any) => {
    fileNum += (row.fileName ? 1 : 0)
  });

  ($values.perPlanMilestoneId || []).forEach((row: any) => {
    row.fileNum = fileNum
  });

  // 提交前要切换回对象
  $values.perPlanMilestoneId = $values.perPlanMilestoneId?.[0]

  await $queryEngine.request.save($values, { customizeAction: vendor() && type === 'submit' ? 'vendorSubmit' : undefined }).then((res: any) => {
    if (type === 'save') {
      app.$message.success(t('common.successSave'))
    }
    // getFormDetail(res.data?.[0], $form, $queryEngine).then(() => {
    //   if (type === 'save') {
    //     return
    //   }

    //   const curAction = 'approval'
    //   let tabDisabled = true
    //   if (curAction) {
    //     if (curAction !== 'approval') {
    //       tabDisabled = true
    //     } else {
    //       tabDisabled = false
    //     }
    //   } else {
    //     if (['DRAFT', 'SUPPLIER_SUBMITTED', 'FIRST_PASS'].includes(workflowStatus.value)) {
    //       tabDisabled = true
    //     } else {
    //       tabDisabled = false
    //     }
    //   }
    // })
  })
  return true
}

const $getPdfFile = async (flag = false) => {
  // let htmlBody = app.$refs.printContent.innerHTML
  let htmlBody1 = document.getElementById('printContent')?.innerHTML ?? ''

  let htmlBody = htmlBody1.replace('disabled="disabled"', ' ')

  const res = await http.post('/egg/upload', {
    options: {
      format: 'a4',
      margin: {
        left: '1cm',
        top: '1cm',
        right: '1cm',
        bottom: '1cm'
      }
    },
    htmlString: '<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">' + htmlBody + '</div>'
  }, { responseType: 'arraybuffer', loading: true, baseURL: '', returnDirectly: true })

  const blob = new Blob([res.data], { type: 'application/pdf' })

  const formData = new FormData()
  formData.append('file', blob, 'myfile.pdf')

  // @ts-ignore
  const pdf = await http.post('/api-base/pdf/pdfAddWatermark', formData, {
    headers: {
      contentType: 'form-data'
    },
    responseType: 'arraybuffer',
    loading: true,
    returnDirectly: true
  })

  let blobs = new Blob([pdf.data], { type: 'application/pdf' })
  if (flag) {
    const iframeNode = document.getElementById('pdfIframe') as HTMLIFrameElement
    if (iframeNode) {
      iframeNode.src = URL.createObjectURL(blobs)

      setTimeout(() => {
        iframeNode.contentWindow!.print()
      }, 1000)
    }
  }
  return blobs
}

// ureport 打印
const $openPrint = ($form:any) => {
  const { perAcceptanceId, perPlanMilestoneId } = $form.values
  let planMilestoneId = null
  if(perPlanMilestoneId && perPlanMilestoneId.length){
    planMilestoneId = perPlanMilestoneId[0].perPlanMilestoneId
  }
  const xml = encodeURIComponent('database:合同验收.ureport.xml')
  const params = encodeURIComponent(`perAcceptanceId=${perAcceptanceId}&perPlanMilestoneId=${planMilestoneId}`)
  const url = `${systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
  window.open(url)
}

const $wrapper = (options, $root) => {
  return Object.keys(options).reduce((acc, key) => {
    acc[key] = options[key].bind($root)
    return acc
  }, {})
}

const $preOptions = {
  nextStep: async function preNextStepHandler() {
    const validResult =  await $saveBill('SUBMIT', this.$form, this.$queryEngine, this.$bus)
    return validResult
  }
}
const $approvalHanlder = (type, $form: any, $queryEngine: any, $bus: any) => {
  switch (type) {
  case 'save':
    // 判断是否供应商已确认
    if ($form.values.contractStatus === 'SUPPLIER_SUBMITTED') {
      app.$message.warning(t('cusEntry.approval.supplierConfirmed'))
      return false
    }
    $saveBill('SAVE', $form, $queryEngine, $bus)
    break
  case 'submit':
    $back($bus)
    break
  case 'abandon':
    $back($bus)
    break
  case 'recall':
    $back($bus)
    break
  case 'pass':
    $back($bus)
    break
  default:
    break
  }
}
const scope = {
  $wrapper,
  $preOptions,
  $approvalHanlder,
  app,
  t,
  $attrs: attrs,
  performPlanService,
  $disabled,
  emitTabRemove,
  getFormDetail,
  $saveBill,
  $back,
  $getPdfFile,
  $openPrint
}

const components = {
  IFieldView,
  ApprovalProcess
}
</script>

<template>
  <RenderEngine
    schemaKey="contractPerformanceCheckDetail"
    :readOnly="$disabled"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
