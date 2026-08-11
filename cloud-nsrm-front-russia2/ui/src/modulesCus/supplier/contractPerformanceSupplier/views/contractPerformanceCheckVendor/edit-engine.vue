<script setup lang="ts">
import { useAttrs } from 'vue'
import { RenderEngine } from 'lib@/components/render-engine'
import {
  yearMonthDaySelectorSegment,
  editTableFormItemValid
} from 'lib@/components/render-engine/schema-segments'
import { defineSchemas, expression, generateXindexInOrder, i18nExpression } from '@meicloud/render-engine'
import { FormCollapse } from '@meicloud/render-pix'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import omit from 'lodash/omit'
import { deppOmit } from '@/utils/util'

const handleSubmit = (type = 'submit', $values: any, $queryEngine: any) => {
  if (type === 'submit') {
    for (let i = 0; i < $values.perPlanMilestoneId.length; i += 1) {
      if (!$values.perPlanMilestoneId[i].practicallyEndDate) {
        app.__jump_error__('perPlanMilestone', 'component', '请填写里程碑---实际结束时间')
        return
      }
    }
  }

  // 临时去除
  let data = deppOmit($values, ['lastUpdateDate', 'creationDate', 'creationDate', 'contractCreationDate', 'currentPlanEndDate'])

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

  return $queryEngine.request.save({
    ...data,
    // 提交前要切换回对象
    perPlanMilestoneId: data.perPlanMilestoneId && data.perPlanMilestoneId[0]
  }, { customizeAction: type === 'submit' ? 'vendorSubmit' : undefined })
}

const schema = defineSchemas({
  PerAcceptance: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      actions: {
        vendorSubmit: {
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
            data.query['*'] = {}

            return data
          }`)
        },
        queryMilestone: {
          immediate: true,
          ready: expression(`() => {
            return $attrs.params && $attrs.params.fromContractPerformancePlan
          }`),
          method: 'read',
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
                perAcceptanceNo: perAcceptanceItem.perAcceptanceNo,
                status: perAcceptanceItem.status || 'DRAFT',
                perPlanMilestoneId: [perAcceptanceItem.perPlanMilestoneId],
                perPlanId: perAcceptanceItem.perPlanId,
                perAcceptanceAttList: perAcceptanceItem.perAcceptanceAttList || [],
                perAcceptanceConfList: perAcceptanceItem.perAcceptanceConfList || [],
              })
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

          $form.setValues(res.data[0])
        }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        back: {
          type: 'void',
          'x-content': i18nExpression('common.cancel'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@submit': expression(`(values) => {
              $bus.$emit('PerAcceptance')
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        save: {
          type: 'void',
          'x-hidden': expression('$disabled'),
          'x-content': i18nExpression('common.save'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@submit': expression(`(values) => {
              $handleSubmit('save', $values, $queryEngine).then((res) => {
                app.$message.success($t('common.successSave'))
              })
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-hidden': expression('$disabled'),
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'primary',
            '@submit': expression(`(values) => {
              $handleSubmit('submit', $values, $queryEngine).then(() => {
                app.$message.success($t('common.successSubmit'))
                $bus.$emit('PerAcceptance')
                emitTabRemove($attrs.tabName)
              })
            }`)
          }
        }
      }
    },
    properties: {
      perPlanId: {
        type: 'string',
        'x-query-engine-skip': true,
        'x-query-engine-relation': 'perPlanId:*',
        'x-hidden': true
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
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
                    'x-query-engine-skip': true,
                    title: '合同序号',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  // 里程碑模板编号
                  'perPlanId.processNum': {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-query-engine-skip': true,
                    title: i18nExpression('contract_mod.processNum'),
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  // 里程碑模板名称
                  'perPlanId.templateName': {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-query-engine-skip': true,
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
                    'x-query-engine-skip': true,
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
                    'x-query-engine-skip': true,
                    'x-component-props': {
                      'disabled': true,
                      code: 'ELEM_CONTRACT_TYPE'
                    }
                  },
                  // 业务实体
                  'perPlanId.buName': {
                    type: 'string',
                    'x-decorator': 'FormItem',
                    'x-query-engine-skip': true,
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
                    'x-query-engine-skip': true,
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
                    'x-query-engine-skip': true,
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
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  // 创建时间
                  'perPlanId.creationDate': {
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem',
                    title: i18nExpression('common.creationTime'),
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      'disabled': true
                    }
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
                    'x-query-engine-skip': true,
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  // 里程碑名称
                  milestoneType: {
                    type: 'string',
                    title: i18nExpression('contract_mod.processNodeName'),
                    'x-component': 'DictSelect',
                    'x-query-engine-skip': true,
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
                    'x-query-engine-skip': true,
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  // 计划开始时间
                  planStartDate: {
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      formatter: expression(`({ cellValue, row, column }) => {
                        parseTime(row.planStartDate, '{y}-{m}-{d}')
                      }`)
                    },
                    'x-query-engine-skip': true,
                    title: i18nExpression('perfMod.planStartDate'),
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  // 计划结束时间
                  planEndDate: {
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      formatter: expression(`({ cellValue, row, column }) => {
                        parseTime(row.planEndDate, '{y}-{m}-{d}')
                      }`)
                    },
                    'x-query-engine-skip': true,
                    title: '计划结束时间',
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  // 节点交付数量
                  nodePlanNum: {
                    type: 'string',
                    'x-query-engine-skip': true,
                    'x-disabled': expression(`
                      $disabled
                      || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
                    `),
                    'x-render-table-column': {
                      title: '节点交付数量',
                      minWidth: 130
                    },
                    ...editTableFormItemValid
                  },
                  // 实际结束时间
                  practicallyEndDate: {
                    'x-query-engine-skip': true,
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      formatter: expression(`({ cellValue, row, column }) => {
                        parseTime(row.practicallyEndDate, '{y}-{m}-{d}')
                      }`)
                    },
                    'x-disabled': expression(`
                      $disabled
                      || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes($values.status)
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
                    'x-query-engine-skip': true,
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  fileId: {
                    type: 'string',
                    'x-hidden': true,
                    'title': i18nExpression('dataConfMod.attachmentTemplate'),
                    'x-component': 'SrmCommonFile',
                    'x-query-engine-skip': true,
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
                      disabled: expression('$disabled'),
                      '@click': expression(`(rowIndex) => {
                        $form.query(".perAcceptanceAttList").take().componentProps.componentInstance.addRow("unshift")
                      }`)
                    }
                  }
                }
              },
              perAcceptanceAttList: {
                type: 'array',
                'x-component': 'RenderTable',
                'x-component-props': {
                  maxHeight: '45vh',
                  class: 'table-view-vxe-table',
                  preColumns: 'seq',
                  editMode: true,
                  pagination: false
                },
                'x-read-pretty': true,
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'perAcceptanceAttList:*',
                properties: generateXindexInOrder({
                  fileName: {
                    type: 'string',
                    title: i18nExpression('vendorMod.attachmentUpload'),
                    'x-read-pretty': expression('$disabled || $table.getRowByIndex($self.index)?.uploadType===\'VENDOR\''),
                    'x-component': 'SrmCommonFile',
                    'x-component-props': {
                      'extra-data': {
                        fileModular: 'sup',
                        fileFunction: 'contractPerformanceCheck',
                        fileType: 'images'
                      },
                      'default-file': {
                        fileId: '{{$table.getRowByIndex($self.index)?.fileId}}',
                        fileName: '{{$self.value}}'
                      },
                      '@on-change': expression(`({file}) => {
                        let row = $table.getRowByIndex($self.index)
                        const { fileId = '', fileName = '' } = file || {}
                        row.fileId = fileId.toString()
                        row.fileName = fileName
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
                    'x-query-engine-skip': true,
                    'x-render-table-column': {
                      minWidth: 130
                    }
                  },
                  creationDate: {
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      formatter: expression(`({ cellValue, row, column }) => {
                        parseTime(row.creationDate, '{y}-{m}-{d}')
                      }`)
                    },
                    title: i18nExpression('outsource.creationDate'),
                    'x-render-table-column': {
                      minWidth: 150
                    }
                  },
                  operation: {
                    type: 'void',
                    title: i18nExpression('common.operation'),
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
                            title: '删除',
                            'x-component': 'TableButton',
                            'x-component-props': {
                              type: 'text',
                              disabled: expression('$disabled'),
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
})

const attrs = useAttrs()
const { app, emitTabRemove } = usePageHelper()

const $disabled = ['view', 'approval'].includes(attrs.params?.flag ?? '')

const scope = {
  app,
  $handleSubmit: handleSubmit,
  $disabled,
  emitTabRemove
}

const components = {
  FormCollapse,
  OrganizationSelector,
  QuickSearch
}
</script>

<template>
  <RenderEngine
    schemaKey="contractPerformanceCheckVendorDetail"
    :readOnly="$disabled"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
