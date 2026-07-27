<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder } from '@meicloud/render-engine'
import {
  feedbackLayoutIsPopover,
  editTableFormItemValid,
  yearMonthDayHourMinuteSecondSelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import IFieldOptions from './i-field-options.vue'
// @ts-ignore
import performanceTpl from '@/service/modules/cmPerform/buyer/main'

import { useAttrs } from 'vue-demi'

const { emitTabRemove, t: $t } = usePageHelper()

const attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('PerTemplHead')
  emitTabRemove(attrs.tabName)
}

// 这里应该要理一下，不用那么复杂的，field name 跟提交的 name 一一对应，就不用这么多转化的过程了
const $submitFormData = async(values: any, type: string, $queryEngine: any, $message: any, $bus: any,$form: any) => {
  let data = values
  let bolErr = false
  await $form.validate().catch(err => {
    console.log(err)
    bolErr = true
    return $message.error($t('common.pleasefinishRequired'))
  })
  if (bolErr) {
    return false
  }

  if (type === 'submit' && data.perTemplLineList.some((i) => !i.milestoneType || !i.fileId)) {
    return $message.error(
      '' +
      $t('contract_mod.processNodeName') +
      $t('vendorMod.and') +
      $t('contract_mod.fileTpl') +
      $t('contract_mod.required')
    )
  }

  data.perTemplLineList.forEach((item: any, index: number) => {
    item.serialNumber = index + 1
  })

  data.status = type === 'submit' ? 'DRAFT' : (data.status || 'DRAFT')

  if (attrs.params.flag === 'add') {
    if (data.perTemplHeadId) {
      delete data.perTemplHeadId
    }
    data.perTemplLineList.forEach(e => {
      delete e.perTemplHeadId
      delete e.perTemplLineId
    })
  }
  console.log(data)
  $queryEngine.request.baseRequest({
    action: type === 'submit' ? 'submit' : 'save',
    payload: [data]
  }).then(() => {
    $closePageAndRefreshListPageData($bus)
  }).catch(() => {
    //
  })
}

const scope = {
  $submitFormData
}

const components = {
  IFieldOptions
}

const schema = defineSchemas({
  // 基本信息
  PerTemplHead: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-data': {
      curPerformTemplLineRow: -1,
      curConfigListRow: -1,
      curFieldOptionType: ''
    },
    'x-query-engine': {
      service: 'cm',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            return $attrs.params && !!$attrs.params.row.perTemplHeadId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs.params.row.perTemplHeadId]

            data.query.perTemplHeadId = {}
            data.query.perTemplLineList.configList = { '*': {} }

            return data
          }`),
          onSuccess: expression(`(res) => {
            // 单纯文本只读状态
            $form.readPretty = $readOnly

            res.data[0].perTemplLineList.forEach(item => {
              item.configList = item.configList.map(id => res.originalData.ref.PerTemplLineConfig[id])
            })

            $form.setValues(res.data[0])
          }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          "cascadeDeletion": true,
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
            '@click': expression(`async (values) => {
              $bus.$emit('ModelHead')
              $emitTabRemove($attrs.tabName)
            }`)
          }
        },
        staging: {
          type: 'void',
          'x-hidden': `{{$attrs.params.flag === 'view'}}`,
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-component-props': {
            '@click': expression(`() => $submitFormData($values, 'staging', $queryEngine, $message, $bus, $form)`)
          }
        },
        submit: {
          type: 'void',
          'x-hidden': `{{$attrs.params.flag === 'view'}}`,
          'x-content': i18nExpression('common.submit'),
          'x-component': 'Button',
          'x-component-props': {
            '@click': expression(`() => {
              $form.validate().then(() => {
                $submitFormData($values, 'submit', $queryEngine, $message, $bus, $form)
              }).catch(err => {
                  console.log(err)
                  $message.warning($t('common.pleasefinishRequired'))
              })
           }`)
          }
        }
      }
    },
    properties: {
      layoutForm: {
        type: 'void',
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 4,
          columnGap: 20,
          rowGap: 0
        },
        properties: {
          contractType: {
            type: 'string',
            title: i18nExpression('contract_mod.contractType'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'ELEM_CONTRACT_TYPE',
              disabled: `{{$attrs.params.flag === 'view'}}`
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: true,
              message: i18nExpression('contract_mod.required')
            }
          },
          processNum: {
            type: 'string',
            title: i18nExpression('contract_mod.processNum'),
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          status: {
            type: 'string',
            title: i18nExpression('contract_mod.configStatus'),
            'x-component': 'DictSelect',
            'x-component-props': {
              disabled: true,
              code: 'PERFORMANCE_OF_CONTRACT'
            },
            'x-decorator': 'FormItem'
          },
          templateName: {
            type: 'string',
            title: i18nExpression('contract_mod.templateName'),
            'x-component-props': {
              'maxlength': "30",
              'show-word-limit': true,
              disabled: `{{$attrs.params.flag === 'view'}}`
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: true,
              message: i18nExpression('contract_mod.required')
            }
          },
          createdBy: {
            type: 'string',
            default: '',
            title: i18nExpression('common.creator'),
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          creationDate: {
            title: i18nExpression('contract_mod.contractType'),
            'x-decorator': 'FormItem',
            ...yearMonthDayHourMinuteSecondSelectorSegment,
            'x-component-props': {
              ...yearMonthDayHourMinuteSecondSelectorSegment['x-component-props'],
              disabled: true
            }
          }
        }
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom: 16px'
        },
        'x-visible': expression('!$readOnly'),
        properties: {
          add: {
            type: 'void',
            title: i18nExpression('contract_mod.addProcessNode'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression('() => $form.query("perTemplLineList").take().componentProps.componentInstance.addRow("push")')
            }
          }
        }
      },
      // 里程碑节点基本列表
      perTemplLineList: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          preColumns: 'seq',
          pagination: false,
          editMode: true,
          primaryKey: 'perTemplLineId',
          // 启用级联删除的储值行为
          cascadeDeletion: true
        },
        'x-query-engine-relation': 'perTemplLineList:*',
        'x-query-engine-skip': true,
        properties: generateXindexInOrder({
          milestoneType: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.processNodeName'),
              minWidth: 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'MILESTONE_SCHEDULE',
              disabled: `{{$attrs.params.flag === 'view'}}`
            },
            ...editTableFormItemValid,
            'x-validator': {
              required: true,
              message: i18nExpression('contract_mod.processNodeName')
            }
          },
          fileId: {
            type: 'string',
            'x-render-table-column': {
              title: i18nExpression('contract_mod.fileTpl'),
              minWidth: 150
            },
            'x-component': 'SrmCommonFile',
            default: null,
            'x-component-props': {
              'extra-data': {
                fileModular: 'sup',
                fileFunction: 'contractPerformanceProcessConfigEdit',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression('$self.value'),
                fileName: expression('$table.getRowByIndex($self.index).fileName || \'\'')
              },
              readonly: '{{$readOnly}}',
              '@on-change': expression(`({ file }) => {
                const row = $table.getRowByIndex($self.index)
                const { fileId = '', fileName = '' } = file || {}
                row.fileId = fileId.toString()
                row.fileName = fileName
              }`)
            },
            'x-decorator': 'FormItem',
            ...feedbackLayoutIsPopover,
            'x-decorator-props': {
              ...feedbackLayoutIsPopover['x-decorator-props'],
              fullness: false,
              disabled: `{{$attrs.params.flag === 'view'}}`
            },
            'x-validator': {
              required: '{{$attrs.params.flag === \'add\'}}', // 新增需要校验, 编辑不需要校验
              triggerType: 'onChange',
              message: i18nExpression('contract_mod.contractType')
            }
          },
          configList: {
            type: 'array',
            default: [],
            title: expression(`$t($readOnly ? 'common.view' : 'common.edit')`),
            'x-render-table-column': {
              title: i18nExpression('contract_mod.payTpl'),
              minWidth: 150
            },
            'x-component': 'TableButton',
            'x-component-props': {
              type: 'text',
              disabled: false,
              '@click': expression(`({ rowIndex }) => {

                const curData = $form.query('.PerTemplHead').take().data || {}
                curData.curPerformTemplLineRow = rowIndex
                $form.query('.PerTemplHead').take().setData(curData)

                const row = $table.getRowByIndex(rowIndex)
                if (!row.configList) {
                  row.configList = []
                }
                $form.query('configListDialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('.configListTable').take().value.splice(0)
                  row.configList.forEach(item => $form.query('.configListTable').take().value.push(item))
                })
              }`)
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-hidden': `{{$attrs.params.flag === 'view'}}`,
            'x-render-table-column': {
              width: 150,
              fixed: 'right'
            },
            properties: {
              delete: {
                type: 'void',
                title: i18nExpression('common.delete'),
                'x-render-table-column': {
                  title: i18nExpression('components.headers.operation'),
                  minWidth: 150
                },
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  // disabled: `{{$attrs.params.flag === 'view'}}`,
                  '@click': expression(`({ rowIndex }) => $table.remove(rowIndex)`)
                }
              }
            }
          }
        })
      }
    }
  },
  // 交付模板弹窗
  configListDialog: {
    type: 'void',
    title: expression(`$t($readOnly ? 'common.view' : 'common.edit') + $t('contract_mod.payTpl')`),
    'x-component': 'RDialog',
    'x-component-props': {
      width: '1080px',
      footer: `{{$attrs.params.flag !== 'view'}}`,
      beforeOkClose: expression(`async () => {
        const configListTable = $form.query('configListTable').get('value')
        if (configListTable.some((i) => !i.fieldType || !i.fieldCode || !i.fieldName)) {
          $message.error(
            '【' + $t('contract_mod.fieldType') + '】和【' + $t('contract_mod.fieldCode') + '】和【' + $t('contract_mod.fieldName') + '】' + $t('contract_mod.required')
          )

          return Promise.reject()
        }

        configListTable.splice(0)
      }`),
      '@ok': expression(`() => {
        const perTemplLineList = $form.query('perTemplLineList').get('value')
        const configListTable = $form.query('configListTable').get('value')
        const curPerformTemplLineRow = $form.query('.PerTemplHead').get('data').curPerformTemplLineRow
        perTemplLineList[curPerformTemplLineRow].configList = JSON.parse(JSON.stringify(configListTable))
      }`),
      '@cancel': expression(`() => {
        console.log('cancel')
      }`)
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
            'x-hidden': `{{$attrs.params.flag === 'view'}}`,
            title: i18nExpression('common.add'),
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              disabled: `{{$attrs.params.flag === 'view'}}`,
              '@click': expression(`({ rowIndex }) => $form.query(".configListTable").take().componentProps.componentInstance.addRow()`)
            }
          }
        }
      },
      layout: {
        type: 'void',
        properties: {
          // 交付模板表格
          configListTable: {
            type: 'array',
            'x-decorator': 'QueryEngine',
            'x-component': 'RenderTable',
            'x-component-props': {
              class: 'table-view-vxe-table',
              style: "height: 58vh;",
              preColumns: 'seq',
              editMode: `{{$attrs.params.flag !== 'view'}}`,
              pagination: false
            },
            properties: {
              fieldType: {
                type: 'string',
                title: i18nExpression('contract_mod.fieldType'),
                'x-render-table-column': {
                  minWidth: 150
                },
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'FIELD_TYPE',
                  disabled: '{{$readOnly}}'
                }
              },
              // 字段配置
              fieldOptions: {
                type: 'string',
                title: expression(`$t($readOnly ? 'common.view' : 'common.edit')`),
                'x-render-table-column': {
                  title: i18nExpression('contract_mod.fieldOptions'),
                  minWidth: 150
                },
                'x-component': 'TableButton',
                'x-component-props': {
                  type: 'text',
                  disabled: false,
                  '@click': expression(`({ row, rowIndex }) => {
                    const fieldType = row.fieldType

                    if (!fieldType) {
                      return $message.error(
                        '【' + $t('contract_mod.fieldType') + '】' + $t('contract_mod.required')
                      )
                    }

                    const curData = $form.query('.PerTemplHead').take().data || {}
                    curData.curConfigListRow = rowIndex
                    curData.curFieldOptionType = fieldType
                    $form.query('.PerTemplHead').take().setData(curData)

                    $form.query('.fieldOptionDialog').take().setComponentProps({
                      visible: true
                    })

                    setTimeout(() => {
                      $form.query('fieldOptionDialog.fieldOptions').take(field => {
                        console.log(row.fieldOptions, 'row')
                        field.value = row.fieldOptions
                      })
                    })
                  }`)
                }
              },
              fieldName: {
                type: 'string',
                'x-query-engine-skip': true,
                title: i18nExpression('contract_mod.fieldName'),
                'x-render-table-column': {
                  minWidth: 150
                },
                'x-component-props': {
                  disabled: '{{$readOnly}}'
                }
              },
              fieldCode: {
                type: 'string',
                'x-query-engine-skip': true,
                title: i18nExpression('contract_mod.fieldCode'),
                'x-render-table-column': {
                  minWidth: 150
                },
                'x-component-props': {
                  disabled: '{{$readOnly}}'
                }
              },
              required: {
                // type: 'string',
                type: 'number',
                // default: "true",
                default: 1,
                'x-decorator': 'FormItem',
                'x-component': 'Checkbox',
                'x-render-table-column': {
                  minWidth: 100,
                  title: i18nExpression('contract_mod.required')
                },
                'x-component-props': {
                  disabled: '{{$readOnly}}',
                  // trueLabel: "true",
                  // falseLabel: "false"
                  trueLabel: 1,
                  falseLabel: 0
                }
              },
              operation: {
                type: 'void',
                'x-hidden': `{{$attrs.params.flag === 'view'}}`,
                title: i18nExpression('common.operation'),
                'x-render-table-column': {
                  width: 130,
                  fixed: 'right'
                },
                properties: {
                  delete: {
                    type: 'void',
                    title: i18nExpression('common.delete'),
                    'x-render-table-column': {
                      title: i18nExpression('components.headers.operation'),
                      minWidth: 150
                    },
                    'x-component': 'TableButton',
                    'x-component-props': {
                      type: 'text',
                      disabled: `{{$attrs.params.flag === 'view'}}`,
                      '@click': expression(`({ rowIndex }) => $table.remove(rowIndex)`)
                    }
                  }
                }
              }
            }
          }

        }
      }

    }
  },
  // 字段配置弹窗
  fieldOptionDialog: {
    type: 'void',
    title: expression(`$t($readOnly ? 'common.view' : 'common.edit') + $t('contract_mod.fieldOptions')`),
    'x-component': 'RDialog',
    'x-component-props': {
      beforeClose: expression(`(done, type) => {
        const curConfigListRow = $form.query('.PerTemplHead').take().data.curConfigListRow
        const configListTable = $form.query('.configListTable').take().value
        const row = configListTable[curConfigListRow]
        if (type === 'ok') {
          row.fieldOptions = row.newFieldOptions || ''
        }
        delete row.newFieldOptions
        done()
      }`)
    },
    properties: {
      fieldOptions: {
        type: 'string',
        default: '',
        'x-component': 'IFieldOptions',
        'x-component-props': {
          disabled: '{{$readOnly}}',
          'field-type': `{{$form.query('.PerTemplHead').take().data.curFieldOptionType}}`,
          '@change': expression(`(fieldOptions) => {
            const curConfigListRow = $form.query('.PerTemplHead').take().data.curConfigListRow
            const configListTable = $form.query('.configListTable').take().value
            const row = configListTable[curConfigListRow]
            row.newFieldOptions = fieldOptions
          }`)
        }
      }
    }
  }
})
</script>

<template>
  <RenderEngine
    schemaKey="contractPerformanceProcessConfigDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
