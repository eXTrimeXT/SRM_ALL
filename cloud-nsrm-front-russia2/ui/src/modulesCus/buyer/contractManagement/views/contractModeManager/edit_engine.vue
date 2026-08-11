<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, markRaw, changeFieldVisibleByDeps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import Tinymce from '@/components/Tinymce'
import Preview from './preview'
import FIXED_ELEM from './fixedElem'
import uniqueId from 'lodash/uniqueId'
import { contractManagement } from 'modb@/contractManagement/api/index'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
import dayjs from 'dayjs'

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

const attrs = useAttrs()

const scope = {
  $markRaw: markRaw,
  uniqueId,
  contractManagement,
  $attrs: attrs,
  emitTabRemove,
  app,
  emitTabAdd,
  Preview,
  $dayjs: dayjs
}

const schema = defineSchemas({
  ModelHead: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $readOnly
            let startDate = $attrs.params.row.startDate || ''
            let endDate = $attrs.params.row.endDate || ''
            if ($attrs.params.row) {
              $form.setValues({
                content: '',
                ...$attrs.params.row,
                allDate: [startDate, endDate]
              })
            }

            return false
          }`)
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        goBack: {
          type: 'void',
          'x-content': expression(`$t($readOnly ? 'common.backTo' : 'components.common.cancel')`),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async (values) => {
              // 此次修改并未保存是否取消
              app.$confirm(this.$t('outsource.goBackConfirm'), this.$t('components.approvalHead.tips.tip'), {
                confirmButtonText: this.$t('common.confirm'),
                cancelButtonText: this.$t('components.common.cancel'),
                type: 'warning'
              }).then(() => {
                emitTabRemove($attrs.tabName)
              }).catch(() => {
              });
            }`)
          }
        },
        view: {
          type: 'void',
          'x-content': i18nExpression('common.preview'),
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`async (values) => {
              const row = $values
              const tab = {
                component: Preview,
                params: { row: row },
                title: $t('common.preview') + (row.modelName ? '-' + row.modelName : ''),
                name: 'preview_' + (row.modelHeadId ? row.modelHeadId : '')
              }
              emitTabAdd(tab)
            }`)
          }
        },
        submit: {
          type: 'void',
          'x-content': i18nExpression('common.staging'),
          'x-component': 'Button',
          'x-reactions': changeFieldVisibleByDeps(
            ['.status'],
            `!$readOnly`
          ),
          'x-component-props': {
            type: 'default',
            plain: 'plain',
            '@submit': expression(`async (values) => {
              values.startDate = values.allDate && values.allDate[0] != '' ? values.allDate[0] : null
              values.endDate = values.allDate && values.allDate[1] != '' ? values.allDate[1] : null
              $queryEngine.request.save({
                ...values,
                status: values.status || 'DRAFT',
              }).then(() => {
                $bus.$emit('ModelHead')
                emitTabRemove($attrs.tabName)
              })
           }`)
          }
        },
        active: {
          type: 'void',
          'x-content': i18nExpression('common.active'),
          'x-component': 'Button',
          'x-reactions': changeFieldVisibleByDeps(
            ['.status'],
            `!$readOnly`
          ),
          'x-component-props': {
            '@submit': expression(`async (values) => {
              values.startDate = values.allDate && values.allDate[0] != '' ? values.allDate[0] : null
              values.endDate = values.allDate && values.allDate[1] != '' ? values.allDate[1] : null
              $queryEngine.request.save({
                ...values,
                status: 'VALID'
              }).then(() => {
                $bus.$emit('ModelHead')
                emitTabRemove($attrs.tabName)
              })
           }`)
          }
        }
      }
    },
    properties: {
      layout: {
        type: 'void',
        'x-component': 'FormLayout',
        'x-component-props': {
          layout: 'vertical',
          gridRowGap: 0
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 4,
              columnGap: 20,
              rowGap: 0
            },
            properties: {
              modelName: {
                type: 'string',
                title: i18nExpression('contractMod.templHeadId'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: `{{$readOnly}}`
                },
                ...requiredValidatorSegment
              },
              status: {
                type: 'string',
                title: i18nExpression('contractMod.contractStatus'),
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'CONTRACT_MODEL_STATUS',
                  disabled: true
                },
                'x-decorator': 'FormItem'
              },
              modelType: {
                type: 'string',
                title: i18nExpression('contractMod.contractType'),
                // required: true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'ELEM_CONTRACT_TYPE',
                  disabled: `{{$readOnly}}`
                },
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment
              },
              startDate: {
                type: 'string',
                default: null,
                'x-hidden': true
              },
              endDate: {
                type: 'string',
                default: null,
                'x-hidden': true
              },
              allDate: {
                type: 'string',
                title: i18nExpression('dataConfMod.expiryDate'),
                'x-query-engine-skip': true,
                'x-component': 'DatePicker',
                'x-component-props': {
                  type: 'daterange',
                  disabled: `{{$readOnly}}`
                },
                // 'x-reactions': expression(`(field) => {
                //   $form.query('.startDate').take().value = field.value[0]
                //   $form.query('.endDate').take().value = field.value[0]
                // }`),
                'x-decorator': 'FormItem'
              },
              extNoExamineFlag: {
                type: 'string',
                title: '是否免审',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO',
                  disabled: `{{$readOnly}}`
                },
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment
              },
              extTemplateCode: {
                type: 'string',
                title: '范本编号',
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment
              }
            }
          }
        }
      },
      toolbar: {
        type: 'void',
        'x-component': 'ButtonList',
        'x-component-props': {
          class: 'list-form__toolbar'
        },
        properties: {
          fixed: {
            type: 'void',
            title: "{{$t('contractMod.fixedElement')}}",
            'x-component-props': {
              type: 'primary',
              disabled: expression('$readOnly'),
              '@click': expression(`() => {
                $form.query('fixedElementDialog').take().setComponentProps({ visible: true })
              }`)
            }
          },
          fixed2: {
            type: 'void',
            title: "{{$t('contractMod.contractTypeElement')}}",
            'x-component-props': {
              disabled: expression('$readOnly'),
              '@click': expression(`() => {
                $form.query('TypeRange').take().setComponentProps({ visible: true })
              }`)
            }
          }
        }
      },
      content: {
        type: 'string',
        'x-component': 'Tinymce',
        'x-component-props': {
          id: 'tinymceContractMode',
          height: 460,
          "@setup": expression(`(editorInstance) => {
            const { flag, row } = $attrs.params
            if (flag === 'view') {
              editorInstance.setMode('readonly')
            }
            $self.setData({
              editorInstance: $markRaw(editorInstance)
            })
          }`)
        }
      }
    }
  },

  fixedElementDialog: {
    type: 'void',
    title: i18nExpression('contractMod.conpayType'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: false
    },
    properties: {
      fixedElementDialogTable: {
        type: 'array',
        'x-decorator': 'QueryEngine',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          performanceMode: true,
          pagination: false,
          maxHeight: '58vh'
        },
        default: FIXED_ELEM,
        properties: {
          elemName: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('contractMod.elemName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          elemCode: {
            type: 'string',
            'x-query-engine-skip': true,
            title: "{{$t('contractMod.elemCode')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 130,
              fixed: 'right',
              performanceMode: false
            },
            'x-component': 'RenderTableButtonList',
            properties: {
              insertFixedElem: {
                type: 'void',
                title: i18nExpression('contractMod.insert'),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    const { elemName, elemCode } = row
                    const _uniqueId = uniqueId('key_'+Date.now()+'_')
                    let content2 = ['$','{', '[', elemName, ']', elemCode, ':', _uniqueId, '}' ]
                    const content = content2.join("")

                    const { editorInstance } = $form.query('.content')
                        .get('data')

                    editorInstance.focus()
                    editorInstance.execCommand('mceInsertRawHTML', false, content)
                    $closed()
                  }`)
                }
              }
            }
          }
        }
      }
    }
  },

  TypeRange: {
    type: 'void',
    title: i18nExpression('contractMod.conpayType'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: false
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'cm',
      actions: {
        queryMaintain: {
          autoFormatResult: false,
          transformResponse: expression(`(res) => {
            const data = JSON.parse(res)

            if (data.data.records && data.data.records.length) {
              $form.query('TypeRange.table').take(field => {
                field.setValue(
                  data.data.records.map(id => data.data.ref.ElemMaintain[id])
                )
              })
            }

            return data
          }`)
        }
      }
    },
    properties: {
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          preColumns: 'seq',
          performanceMode: true,
          pagination: false,
          maxHeight: '58vh'
        },
        'x-reactions': expression(`() => {
          const value = $form.query('.modelType').take().value

          if (!value) {
            $self.setValue([])
            return
          }

          $queryEngine.request.baseRequest({
            action: 'queryMaintain',
            payload: {
              filter: {
                contractType: { eq: value  },
              }
            },
          })
        }`),
        properties: {
          addMethod: {
            type: 'string',
            'x-hidden': true,
            'x-query-engine-relation': 'elemMaintainId'
          },
          elemName: {
            type: 'string',
            title: "{{$t('contractMod.elemName')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          elemCode: {
            type: 'string',
            title: "{{$t('contractMod.elemCode')}}",
            'x-render-table-column': {
              minWidth: 150
            }
          },
          operation: {
            type: 'void',
            title: i18nExpression('common.operation'),
            'x-render-table-column': {
              width: 130,
              fixed: 'right',
              performanceMode: false
            },
            'x-query-engine-skip': true,
            'x-component': 'RenderTableButtonList',
            properties: {
              insertFixedElem: {
                type: 'void',
                title: i18nExpression('contractMod.insert'),
                'x-component-props': {
                  '@click': expression(`({ row }) => {
                    const { elemName, elemCode } = row
                    const _uniqueId = uniqueId('key_'+Date.now()+'_')
                    let content2 = ['$','{', '[', elemName, ']', elemCode, ':', _uniqueId, '}' ]
                    const content = content2.join("")

                    const { editorInstance } = $form.query('.content')
                      .get('data')

                    editorInstance.focus()
                    editorInstance.execCommand('mceInsertRawHTML', false, content)
                    $closed()
                  }`)
                }
              }
            }
          }
        }
      }
    }
  }
})

const components = {
  Tinymce
}
</script>

<template>
  <RenderEngine
    schemaKey="contractModeDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style>
.el-button--primary.is-plain{
  border: 1px #0077ff solid;
}
</style>
