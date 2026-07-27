<!-- eslint-disable quotes -->
<script setup lang='ts'>
import {
  defineSchemas,
  generateXindexInOrder,
  changeFieldVisibleByDeps,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'

const { emitTabAdd, t: $t } = usePageHelper()

const $save = ($form, $queryEngine, $message, done, closeLoading) => {
  return $form
    .query('*.dialog.form')
    .take()
    .submit(values => {
      return $queryEngine.request.create(values, { query: { '*': {} } }).then(() => {
        $message.success($t('common.successSave'))
        $queryEngine.state.paginationManagement.refresh()
        done()
      })
    })
}

const schema = defineSchemas({
  TagTemplateRelation: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true,
          transformRequest: expression(`(data, headers) => {
              data.query = {
                '*': {}
              }
              return data
          }`)
        }

      }
    },
    properties: {
      bus: {
        type: 'void',
        'x-component': 'BusEvent',
        'x-component-props': {
          eventName: 'BarCode',
          '@listener': expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)
        }
      },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
          templateCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.templateName')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_PRINT_TEMPLATE'
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('common.categoryName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_purchase_category',
              showKey: 'categoryName'
            }
          },
          materialName: {
            type: 'string',
            title: "{{$t('common.materialName')}}",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_base_material_item',
              showKey: 'materialName'
            }
          }
        })
      },
      toolbar: {
        type: 'void',
        'x-component': 'Space',
        'x-component-props': {
          style: 'margin-bottom:16px;'
        },
        properties: {
          add: {
            type: 'void',
            title: "{{$t('common.add')}}",
            'x-component': 'RButton',
            'x-component-props': {
              type: 'primary',
              '@click': expression(`() => {
                console.log('dialog',$form.query('*.dialog'))
                $form.query('*.dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('*.dialog.form').take((field) => {
                    field.reset()
                  })
                })
              }`)
            }
          }
        }
      },
      table: {
        type: 'array',
        'x-component': 'RenderTable',
        'x-component-props': {
          class: 'table-view-vxe-table',
          style: 'flex: 1',
          preColumns: 'seq',
          openCustomTable: true
        },
        properties: generateXindexInOrder({
          tagTemplateRelationId: {
            type: 'number',
            'x-hidden': true,
            'x-query-engine-primary-key': true
          },
          templateCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.templateCode')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          templateName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.templateName')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          type: {
            type: 'string',
            title: "{{$t('barcodeManageNew.boxType')}}",
            'x-render-table-column': {
              'min-width': 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_TYPE'
            }
          },
          templateRelationDimension: {
            type: 'string',
            title: "{{$t('barcodeManageNew.templateRelationDimension')}}",
            'x-render-table-column': {
              'min-width': 150
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_GENERATE_RULE_DIMENSION'
            }
          },
          categoryCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.categoryCode')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.categoryName')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('barcodeManageNew.materialCode')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          materialName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.materialName')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          createdFullName: {
            type: 'string',
            title: "{{ $t('common.creator') }}",
            'x-render-table-column': {
              width: 120
            }
          },
          creationDate: {
            'x-query-engine-sort': 'desc',
            title: "{{ $t('common.creationTime') }}",
            ...yearMonthDaySelectorSegment,
            'x-render-table-column': {
              width: 150
            }
          },
          operation: {
            type: 'void',
            title: "{{$t('common.operation')}}",
            'x-component': 'RenderTableButtonList',
            'x-render-table-column': {
              fixed: 'right',
              width: 120
            },
            properties: {
              delete: {
                type: 'void',
                title: "{{$t('common.delete')}}",
                'x-component-props': {
                  popconfirm: {
                    title: "{{$t('common.confirmDeleteRow')}}"
                  },
                  '@click': expression(`({row}) => {
                    $delete($queryEngine,row,$message)
                  }`)
                }
              }
            }
          }
        })
      },
      dialog: {
        type: 'void',
        title: "{{$t('barcodeManageNew.addRelateTemplate')}}",
        'x-component': 'RDialog',
        'x-component-props': {
          beforeClose: expression(`(done, type,closeLoading) => {
            if ( type === 'ok') {
              return $save($form,$queryEngine,$message,done,closeLoading).catch(() => {closeLoading()})
            } else {
              done()
              }
            }
          `)
        },
        properties: {
          form: {
            type: 'object',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical'
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              // 模板关联维度
              templateRelationDimension: {
                type: 'string',
                required: true,
                title: `{{$t('barcodeManageNew.templateRelationDimension')}}`,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_GENERATE_RULE_DIMENSION'
                },
                'x-decorator': 'FormItem'
              },
              materialId: {
                type: 'number',
                'x-hidden': true
              },
              // 物料编码
              materialCode: {
                type: 'string',
                required: true,
                ...requiredValidatorSegment,
                'x-decorator': 'FormItem',
                title: i18nExpression('barcodeManageNew.materialCode'),
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  'show-key': 'materialCode',
                  name: 'scc_base_material_item',
                  '@close-quicksearch': expression(`(val,scope) => {
                    let arr = ['materialId','materialCode','materialName','categoryId','categoryCode','categoryName']
                    if(val){
                      for(let key of arr){
                        $self.query('*.dialog.form').take().value[key] = val[key]
                      }
                    }
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.templateRelationDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`$deps[0] === 'MATERIAL'`)
                    }
                  }
                }
              },
              // 物料名称
              materialName: {
                type: 'string',
                title: `{{$t('barcodeManageNew.materialName')}}`,
                required: true,
                ...requiredValidatorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                'x-reactions': {
                  dependencies: ['.templateRelationDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`$deps[0] === 'MATERIAL'`)
                    }
                  }
                }
              },
              categoryId: {
                type: 'number',
                'x-hidden': true
              },

              // 品类编码
              categoryCode: {
                type: 'string',
                required: true,
                ...requiredValidatorSegment,
                'x-decorator': 'FormItem',
                title: i18nExpression('barcodeManageNew.categoryCode'),
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  'show-key': 'categoryCode',
                  name: 'scc_base_purchase_category',
                  '@close-quicksearch': expression(`(val,scope) => {
                    if(val){
                      let arr = ['categoryId','categoryCode','categoryName']
                      for(let key of arr){
                        $self.query('*.dialog.form').take().value[key] = val[key]
                      }
                    }

                    }`)
                },
                'x-reactions': {
                  dependencies: ['.templateRelationDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`['CATEGORY','MATERIAL'].includes($deps[0])`),
                      disabled: expression(`$deps[0] === 'MATERIAL'`)
                    }
                  }
                }
              },
              // 品类名称
              categoryName: {
                type: 'string',
                title: `{{$t('barcodeManageNew.categoryName')}}`,
                required: true,
                ...requiredValidatorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                'x-reactions': {
                  dependencies: ['.templateRelationDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`['CATEGORY','MATERIAL'].includes($deps[0])`)
                    }
                  }
                }
              },
              templateCode: {
                type: 'string',
                title: "{{$t('barcodeManageNew.templateName')}}",
                'x-decorator': 'FormItem',
                required: true,
                ...requiredValidatorSegment,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_PRINT_TEMPLATE',
                  '@change-value': expression(`(val, element) =>{
                    $self.query('*.dialog.form').take().value['templateName'] = element.label?element.label:''
                    $self.query('*.dialog.form').take().value['templatePath'] = element.description?element.description:''
                  }`)
                }
              },
              type: {
                type: 'string',
                title: `{{$t('barcodeManageNew.boxType')}}`,
                required: true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_RULE_TYPE',
                  '@change-value': expression(`(val, element) =>{
                  }`)
                },
                'x-decorator': 'FormItem'
              },
              templateName: {
                type: 'string',
                'x-hidden': true
              },
              templatePath: {
                type: 'string',
                'x-hidden': true
              }
            }
          }
        }
      }
    }
  }
})

const $enable = ($queryEngine: any, row: any) => {}

const $delete = ($queryEngine: any, row: Object, $message: any) => {
  $queryEngine.request['delete']([row.tagTemplateRelationId]).then((res: any) => {
        $message.success($t('common.successDelete'))
        $queryEngine.state.paginationManagement.refresh()
      })
}

const scope = {
  // $editTab,
  $enable,
  $delete,
  $save
}
</script>
<template>
  <RenderEngine
    schemaKey="barcodePrintTemp"
    :pageAttrs="$attrs"
    :scope="scope"
    :schema="schema"
  />
</template>
