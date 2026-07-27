<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, i18nExpression, expression, changeFieldVisibleByDeps, generateXindexInOrder, toJS } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
import { crossOrgImport } from 'modb@/vendorManagementBuyer/api/supApi'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { useAttrs, computed, ref } from 'vue-demi'

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

let attrs:any = useAttrs()

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

    componentInstance.setWorkflowBusinessId($form.values.importId)
    componentInstance.setWorkflowTabDisabled(['DRAFT'].includes($form.values.importStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const $selectHandler = (node:any, value:any, $table:any, $self:any) => {
  console.log('node:::', node)
  const row = $table.getRowByIndex($self.index)
  row.orgId = node ? node.organizationId : null
  row.orgCode = node ? node.organizationCode : ''
  row.orgName = node ? node.organizationName : null
  if (node && node.organizationId) {
    crossOrgImport.getBuByOrgId(node.organizationId).then(data => {
        row.division = data.data.organizationCode
      })
      .catch(err => {
        console.log(err)
      })
  } else {
    row.division = null
  }
}

const $saveBill = (type:string, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  const values = $form.values
  if (type === 'SAVE') {
    $form.validate().then(() => {
      $submitData('save', values, $form, $queryEngine, $confirm, $message, $bus)
    })
  } else {
    $form.validate().then(() => {
      $submitData('submit', values, $form, $queryEngine, $confirm, $message, $bus)
    })
  }
}

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const form = toJS($values)
  delete form.categoryList
  if (!form.vendorImportDetails.length) {
    $message.warning('请至少新增一条引入至业务实体数据')
    return
  }
  if (!form.status) form.status = 'DRAFT'
  $queryEngine.request.save(form).then((res:any) => {
    $message.success(t('common.successSave'))
    if (res.data && res.data.length) {
      let result = res.data[0]
      let importId = result.importId
      $form.values.importId = importId
      $queryEngine.request.read(importId).then(() => {
        if (type === 'submit') {
          const tabDisabled = false
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId(importId)
          componentInstance.setWorkflowTabDisabled(tabDisabled)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            console.log('handlerAfter VendorImport')
            emitTabRemove(attrs.tabName)
            $bus.$emit('VendorImport')
          })
        } else {
          emitTabRemove(attrs.tabName)
          $bus.$emit('VendorImport')
        }
      })
    }
  })
}

const scope = {
  emitTabRemove,
  emitTabAdd,
  app,
  $selectHandler,
  $saveBill,
  $submitData,
  $crossOrgImport: crossOrgImport,
  initButtonConfig,
  workflowStatus,
  updateButtonConfig,
  $disabledFlag
}

const components = {
  FileDynamic
}

const schema = defineSchemas({
  VendorImport: {
    type: 'void',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        save: {
          transformRequest: expression(`(data,headers) => {
            data.query['*'] = {}
            data.query['vendorImportDetails'] = {
              '*':{}
            }
            return data
          }`)
        },
        read: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            let id = $attrs.params.row.importId
            $form.values.importId = id
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$form.values.importId]
            data.query['*'] = {}
          }`),
          onSuccess: expression(`(res) => {
            console.log('res:::',res)
            $form.readPretty = $readOnly
            const value = res.data[0]
            workflowStatus.value = value.importStatus
            const {vendorImportDetails,fileUploads,...rest} = value
            $form.setValues({
              ...rest,
              fileUploads
            })
            updateButtonConfig($form)
            if(value.vendorId && value.oldOrgId){
              $crossOrgImport.listOrgCategoryByParam({
                companyId:value.vendorId,
                orgId:value.oldOrgId
              }).then(result => {
                $form.values.categoryList = result.data || []
              }).catch(err => console.log(err))
            }
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.importId || null'),
          'business-type': 'vendorImport',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            console.log('click-handler', type, $form, $confirm, $message)
            $saveBill(type,$form,$queryEngine,$confirm,$message,$bus)
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
            emitTabRemove($attrs.tabName)
            $bus.$emit('VendorImport')
          }`)
        },
        "x-reactions": expression(`field => {
          if(!$values.importId) return
          $queryEngine.request.baseRequest({
              type:'VendorImportDetail',
              action:'getDetail',
              payload:[$values.importId],
              query:{
                '*':{}
              }
            }).then(response => {
              console.log('response:::',response)
              $form.setValues({
                vendorImportDetails:response.data
              })
            })
        }`),
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-component-props:': {
              defaultOpenPanelCount: 1
            },
            properties: {
              // 供应商扩展
              vendorExpansion: {
                type: 'void',
                'x-query-engine-skip': true,
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('vendorMod.vendorExpansion')
                },
                properties: {
                  vendorImport: {
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
                      vendorId: { // 后续需要使用到的值需要提前声明
                        type: 'number',
                        'x-hidden': true
                      },
                      vendorName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.vendorName'),
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          'disabled': expression('$readOnly'),
                          'read-pretty': '{{$readOnly}}',
                          'show-key': 'companyName',
                          'prop-key': 'companyName',
                          'name': 'scc_sup_company_info2',
                          '@close-quicksearch': expression(`(val) => {
                            console.log('val',val)
                            let {companyId,companyCode,companyName} = val || {}
                            if(companyId) {
                              $values.vendorId = companyId
                              $values.vendorCode = companyCode
                              $values.vendorName = companyName
                            }
                          }`)
                        },
                        ...requiredValidatorSegment
                      },
                      importNum: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('vendorMod.importNum'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      createdUserName: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creator'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      oldOrgId: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('vendorMod.oldOrg'),
                        'x-component': 'Select',
                        'x-component-props': {
                          '@change': expression(`(val) => {
                            let options = $self.dataSource
                            if(val){
                              let obj = options.find(item => item.value === val) || {}
                              $values.oldOrgCode = obj.code
                              $values.oldOrgName = obj.label
                              if(!$values.vendorId) return
                              let data = {
                                companyId:$values.vendorId,
                                orgId:val
                              }
                              $crossOrgImport.listOrgCategoryByParam(data).then(result => {
                                $form.values.categoryList = result.data || []
                              }).catch(err => console.log(err))
                            }
                          }`)
                        },
                        'x-reactions': expression(`(field) => {
                          const vendorId = field.query('vendorId').get('value')
                          if(!vendorId) return
                          $crossOrgImport.getOrgByVendorId(vendorId).then(res => {
                            $self.dataSource = (res.data || []).map(item => {
                              return {
                                value: item.orgId,
                                label: item.orgName,
                                code: item.orgCode
                              }
                            })
                          })
                          .catch(err => {
                            console.log(err)
                          })
                        }`),
                        ...requiredValidatorSegment
                      },
                      importStatus: {
                        type: 'string',
                        default: 'DRAFT',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.status'),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'VENDORIMPORTSTATUS',
                          disabled: true
                        }
                      },
                      creationDate: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('common.creationTime'),
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      importExplain: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: i18nExpression('vendorMod.vendorImportExplain'),
                        'x-component-props': {
                          type: 'textarea',
                          disabled: expression('$readOnly')
                        }
                      }
                    }
                  }
                }
              },
              // 引入至业务实体
              importOrg: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('vendorMod.importOrg')
                },
                properties: {
                  toolbar: {
                    type: 'void',
                    'x-component': 'Space',
                    'x-component-props': {
                      style: 'margin-bottom:10px'
                    },
                    properties: {
                      add: {
                        type: 'void',
                        title: '{{$t(\'common.new\')}}',
                        'x-component': 'RButton',
                        'x-component-props': {
                          'type': 'primary',
                          'disabled': '{{$readOnly}}',
                          '@click': expression(`() => {
                            let {vendorId,oldOrgId} = $form.values
                            if(!vendorId || !oldOrgId){
                              $message.warning($t('vendorMod.msgVendorAndOldOrg'))
                              return
                            }
                            $form.query('vendorImportDetails').take(field => {
                              field.value.push({
                                importId: null,
                                importDetailId: null,
                                orgId: null,
                                orgCode: null,
                                orgName: null,
                                division: null
                              })
                            })
                          }`)
                        }
                      }
                    }
                  },
                  vendorImportDetails: {
                    type: 'array',
                    'x-query-engine-skip': true,
                    'x-query-engine-relation': 'vendorImportDetails:*',
                    'x-component': 'RenderTable',
                    'x-component-props': {
                      preColumns: 'seq',
                      editMode: true,
                      pagination: false,
                      maxHeight: '58vh',
                      sortable: false
                    },
                    properties: generateXindexInOrder({
                      orgId: {
                        type: 'string',
                        title: "{{$t('vendorMod.importOU')}}",
                        'x-render-table-column': {
                          minWidth: 200
                        },
                        'x-component': 'OrganizationSelector',
                        'x-component-props': {
                          'node-type': 'OU',
                          'parent-id': -1,
                          'placeholder': "{{$t('common.pleaseSelect')}}",
                          'scope': expression('$table.getRowByIndex($self.index)'),
                          '@select': expression(`(node,value) => $selectHandler(node,value,$table,$self)`)
                        }
                      },
                      division: {
                        type: 'string',
                        title: "{{$t('vendorMod.buName')}}",
                        'x-render-table-column': {
                          minWidth: 200
                        },
                        'x-read-pretty': true
                      },
                      operation: {
                        type: 'void',
                        title: "{{$t('common.operation')}}",
                        'x-render-table-column': {
                          width: 80
                        },
                        'x-component': 'RenderTableButtonList',
                        'x-reactions': expression(`(field) => {
                          field.visible = !$readOnly
                        }`),
                        properties: {
                          delete: {
                            type: 'void',
                            title: "{{$t('common.delete')}}",
                            'x-component-props': {
                              type: 'text',
                              '@click': expression(`({row,rowIndex}) => {
                                $table.remove(rowIndex)
                                // if(row.importDetailId){
                                //   $crossOrgImport.deleteOneList(row.importDetailId).then(res => {
                                //     $table.remove(rowIndex)
                                //   }).catch(err => console.log(err))
                                // }else{
                                //   $table.remove(rowIndex)
                                // }
                              }`)
                            }
                          }
                        }
                      }
                    })
                  }
                }
              },
              // 扩展的品类
              expandCate: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('vendorMod.expandCate')}}"
                },
                properties: {
                  categoryList: {
                    type: 'array',
                    'x-query-engine-skip': true,
                    'x-component': 'RenderTable',
                    'x-component-props': {
                      preColumns: 'seq',
                      editMode: false,
                      pagination: false,
                      maxHeight: '58vh',
                      sortable: false
                    },
                    properties: generateXindexInOrder({
                      categoryName: {
                        type: 'string',
                        title: "{{$t('common.category')}}",
                        'x-render-table-column': {
                          minWidth: 200
                        }
                      }
                    })
                  }
                }
              },
              // 附件
              accessory: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('vendorMod.relegation.accessory')}}"
                },
                properties: {
                  fileUploads: {
                    type: 'array',
                    'x-query-engine-relation': 'fileUploads:*',
                    'x-component': 'FileDynamic',
                    'x-component-props': {
                      'scene-module-code': 'SCENE_ORG_IMPORT_ATTACHMENT',
                      'business-id': '{{$attrs.params.row.importId}}',
                      'editable': '{{!$readOnly}}',
                      'need-init': true
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

</script>

<template>
  <RenderEngine schemaKey="crossOrgImportDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
