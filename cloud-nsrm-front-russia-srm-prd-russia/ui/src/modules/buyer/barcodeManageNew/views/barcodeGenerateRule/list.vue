<!-- eslint-disable quotes -->
<script setup lang='ts'>
import { defineSchemas, generateXindexInOrder, changeFieldVisibleByDeps, expression, i18nExpression } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
import { usePageHelper } from "lib@/components/composables/usePageHelper"
import { throttle } from '@/utils'

const schema = defineSchemas({
  TagGenerateRuleConfig: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-component': 'el-container',
    'x-query-engine': {
      service: 'sup-ce',
      actions: {
        paginationQuery: {
          immediate: true
        }
      }
    },
    properties: {
      // bus: {
      //   type: 'void',
      //   'x-component': 'BusEvent',
      //   'x-component-props': {
      //     eventName: 'TagGenerateRuleConfig',
      //     '@listener': expression(`() => {
      //       $queryEngine.state.paginationManagement.refresh()
      //     }`)
      //   }
      // },
      query: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'QueryFormByQueryEngine',
        properties: generateXindexInOrder({
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
          },
          ruleType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_TYPE'
            }
          },
          tagRuleName: {
            type: 'string',
            title: "条码规则名称",
            'x-component': 'QuickSearchWrapper',
            'x-component-props': {
              name: 'scc_sc_tag_rule_config',
              showKey: 'tagRuleName'
            }
          },
          tagType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.tagStyle')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_TYPE'
            }
          },
          defaultFlag: {
            type: 'string',
            title: "{{$t('barcodeManageNew.isDefault')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
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
                $editTab('add',{},$form)
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
          tagGenerateRuleId: {
            type: 'number',
            'x-hidden': false
          },
          categoryCode: {
            type: 'string',
            title: "{{$t('common.categoryCode')}}",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          categoryName: {
            type: 'string',
            title: "{{$t('common.categoryName')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          materialCode: {
            type: 'string',
            title: "{{$t('common.materialCode')}}",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          materialName: {
            type: 'string',
            title: "{{$t('common.materialName')}}",
            'x-render-table-column': {
              'min-width': 150
            }
          },
          ruleType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.ruleType')}}",
            'x-render-table-column': {
              'min-width': 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_RULE_TYPE'
            }
          },
          tagRuleCode: {
            type: 'string',
            title: "条码规则编码",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          tagRuleName: {
            type: 'string',
            title: "条码规则名称",
            'x-render-table-column': {
              'min-width': 120
            }
          },
          tagType: {
            type: 'string',
            title: "{{$t('barcodeManageNew.tagStyle')}}",
            'x-render-table-column': {
              'min-width': 120
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'TAG_TYPE'
            }
          },
          defaultFlag: {
            type: 'string',
            title: "{{$t('barcodeManageNew.isDefault')}}",
            'x-render-table-column': {
              'min-width': 100
            },
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            }
          },
          creationDate: {
            type: 'string',
            title: "{{$t('barcodeManageNew.creationDate')}}",
            'x-render-table-column': {
              'min-width': 120
            },
            'x-query-engine-sort': 'desc'
          },
          createdFullName: {
            type: 'string',
            title: "{{$t('barcodeManageNew.createdFullName')}}",
            'x-render-table-column': {
              'min-width': 120
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
              edit: {
                type: 'void',
                title: "{{$t('common.edit')}}",
                'x-component-props': {
                  '@click': expression(`({row}) => {
                    $editTab('edit',row,$form)
                  }`)
                }
              },
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
        title: '新增条码生成规则',
        'x-component': 'RDialog',
        'x-component-props': {
          class: 'the-barcodePrint',
          'close-on-click-modal': false,
          beforeClose: expression(`(done,type) => {
            if(type === 'ok'){
              $self.query('*.dialog.form').take().submit(values => {
                console.log('values:::',values)
                $handleFn($self,values,$message,$confirm,$queryEngine,done)
              })
            }else{
              done()
            }
          }`)
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
            properties: generateXindexInOrder({
              tagGenerateRuleDimension: {
                type: 'string',
                title: "条码生成规则维度",
                required: true,
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_GENERATE_RULE_DIMENSION'
                }
              },
              materialId: {
                type: 'number',
                'x-hidden': true
              },
              materialCode: {
                type: 'string',
                title: "{{$t('common.materialCode')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  name: 'scc_base_material_item',
                  showKey: 'materialCode',
                  '@close-quicksearch': expression(`(val) => {
                    let arr = ['materialId','materialCode','materialName','categoryId','categoryCode','categoryName']
                    if(val){
                      for(let key of arr){
                        $self.query('*.dialog.form').take().value[key] = val[key]
                      }
                    }
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.tagGenerateRuleDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`$deps[0] === 'MATERIAL'`)
                    }
                  }
                }
              },
              materialName: {
                type: 'string',
                title: "{{$t('common.materialName')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component-props': {
                  disabled: true
                },
                'x-reactions': {
                    dependencies: ['.tagGenerateRuleDimension'],
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
              categoryCode: {
                type: 'string',
                title: "{{$t('common.categoryCode')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  name: 'scc_base_purchase_category',
                  showKey: 'categoryCode',
                  '@close-quicksearch': expression(`(val) => {
                    if(val){
                      let arr = ['categoryId','categoryCode','categoryName']
                      for(let key of arr){
                        $self.query('*.dialog.form').take().value[key] = val[key]
                      }
                    }
                  }`)
                },
                'x-reactions': {
                  dependencies: ['.tagGenerateRuleDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`['CATEGORY','MATERIAL'].includes($deps[0])`),
                      disabled: expression(`$deps[0] === 'MATERIAL'`)
                    }
                  }
                }
              },
              categoryName: {
                type: 'string',
                title: "{{$t('common.categoryName')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component-props': {
                  disabled: true
                },
                'x-reactions': {
                  dependencies: ['.tagGenerateRuleDimension'],
                  fulfill: {
                    state: {
                      visible: expression(`['CATEGORY','MATERIAL'].includes($deps[0])`)
                    }
                  }
                }
              },
              ruleType: {
                type: 'string',
                title: "{{$t('barcodeManageNew.ruleType')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_RULE_TYPE',
                  '@change': expression(`val => {
                    if(val){
                      $self.query('*.dialog.form').take().value.tagRuleCode = null
                      $self.query('*.dialog.form').take().value.tagRuleId = null
                      $self.query('*.dialog.form').take().value.tagRuleName = null
                    }
                  }`)
                }
              },
              tagRuleId: {
                type: 'number',
                'x-hidden': true
              },
              tagRuleCode: {
                type: 'string',
                title: "条码规则编码",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'QuickSearchWrapper',
                'x-component-props': {
                  name: 'scc_sc_tag_rule_config',
                  showKey: 'tagRuleCode',
                  'pre-query-data': expression(`{
                    't.TAG_RULE_TYPE': $self.query('.ruleType').get('value')
                  }`),
                  '@close-quicksearch': expression(`(val) => {
                    if(val){
                      $self.query('*.dialog.form').take().value.tagRuleCode = val.tagRuleCode
                      $self.query('*.dialog.form').take().value.tagRuleId = val.tagRuleId
                      $self.query('*.dialog.form').take().value.tagRuleName = val.tagRuleName
                    }
                  }`)
                }
              },
              tagRuleName: {
                type: 'string',
                title: "条码规则名称",
                'x-decorator': 'FormItem',
                required: true,
                'x-component-props': {
                  disabled: true
                }
              },
              tagType: {
                type: 'string',
                title: "{{$t('barcodeManageNew.tagStyle')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'TAG_TYPE'
                }
              },
              defaultFlag: {
                type: 'string',
                title: "{{$t('barcodeManageNew.isDefault')}}",
                'x-decorator': 'FormItem',
                required: true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                }
              }
            })
          }
        }
      }
    }
  }
})

const { emitTabAdd, t, app } = usePageHelper()

const $editTab = (type:string, row:Object, $form:any) => {
  $form.query('*.dialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('*.dialog.form').take((field) => {
      if (type === 'add') {
        field.reset()
      } else {
        field.setValue({
          ...row
        })
      }
    })
  })
}

const $delete = ($queryEngine:any, row:Object, $message:any) => {
  $queryEngine.request['delete']([row.tagGenerateRuleId]).then((res:any) => {
    $message.success(t('common.successDelete'))
    $queryEngine.state.paginationManagement.refresh()
  })
}

const handleSave = ($queryEngine:any, values:any, done:any, $message:any) => {
  $queryEngine.request.save({
    ...values
  }).then(() => {
    $message.success(t('common.successSave'))
    $queryEngine.state.paginationManagement.refresh()
    done()
  })
}

const $handleFn = throttle(($self:any, values:any, $message:any, $confirm:any, $queryEngine:any, done:any) => {
  if (values.defaultFlag == 'N') {
    handleSave($queryEngine, values, done, $message)
    return
  }
  $queryEngine.request.baseRequest({
    type: 'TagGenerateRuleConfig',
    action: 'checkDefault',
    query: {
      '*': {}
    },
    payload: [{
      ...values
    }]
  }).then(result => {
    console.log('result', result)
    if (result?.data.length) {
      $confirm('该品类/物料已存在一条默认的规则，是否修改?', {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }).then(() => {
        handleSave($queryEngine, values, done, $message)
      }).catch(() => {

      })
    } else {
      handleSave($queryEngine, values, done, $message)
    }
  })
}, 3000)

const scope = {
  $editTab,
  $delete,
  handleSave,
  $handleFn,
  throttle
}

</script>
<template>
  <RenderEngine schemaKey="barcodeGenerateRule" :pageAttrs="$attrs" :scope="scope" :schema="schema" />
</template>
<style lang="scss">
.the-barcodePrint {
  .render-pix-form-item-feedback-layout-loose {
    margin-bottom: 20px !important;
  }
}
</style>
