<script setup lang="ts">
// @ts-ignore
import {
  dataTimeSelectorSegment,
  formGridSegment,
  RenderEngine
} from 'lib@/components/render-engine'
import {
  connect,
  defineSchemas,
  expression,
  generateXindexInOrder,
  i18nExpression,
  useAutoMountInstanceToField
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { blackComApi } from 'modb@/vendorManagementBuyer/api/black'
import { forms } from 'modcb@/vendorManagementBuyer/views/cooperationEndedEngine/form'
import range from './range'
import { orgCatForm } from 'modcb@/vendorManagementBuyer/api/supApi'
import { getHeaderField } from '@/utils'
// @ts-ignore

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const viewUpdateButton = ($form: any) => {
  let approveStatus = $form.query('.approveStatus').take().value
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  if (!['None', null].includes(componentInstance.workflowParamsInfo.integrationMode)) {
    return attrs.params.flag != 'view'
  } else {
    if (attrs.params.flag != 'view') {
      return true
    } else if (
      attrs.params.flag == 'view' &&
      approveStatus == 'PUBLISH'
    ) {
      return true
    } else {
      return false
    }
  }
}
const disabledUpdateButton = () => {
  const readOnly = attrs.params.flag == 'view'
  return !readOnly
}

const updateWorkflowconfig = (componentInstance, businessId, tabDisabled, businessVariables) => {
  componentInstance.setWorkflowBusinessId(businessId)
  componentInstance.setWorkflowTabDisabled(tabDisabled)
  componentInstance.setWorkflowBusinessVariables(businessVariables)
}

const handleButtonConfig = ($form: any, componentInstance: any) => {
  const orderId = attrs.params.orderId || ''
  const approveStatus = attrs.params.row?.approveStatus || null
  const tabDisabled = approveStatus != 'SUBMITTED'
  componentInstance.setWorkflowApproveStatus(approveStatus)
  updateWorkflowconfig(componentInstance, orderId, tabDisabled, {
    procTitle: $form.values.orgCatFormNumber
  })
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.cancel.view = true
    componentInstance.buttonConfigInfo.close.view = false
    handleButtonConfig($form, componentInstance)
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
    let approveStatus = attrs.params.row?.approveStatus || null
    if (approveStatus == 'SUBMITTED' && componentInstance.workflowParamsInfo
      .integrationMode == 'Push') {
      componentInstance.buttonConfigInfo.withdraw.view = true
    }
    handleButtonConfig($form, componentInstance)
  }, 50)
}

const newRange = defineComponent({
  name: 'newRange',
  props: range.props,
  setup (props, { listeners, attrs, slots }) {
    useAutoMountInstanceToField()

    return () => {
      return h(range, { props: { ...attrs, ...props }, on: listeners, ref: 'range' }, slots)
    }
  }
})

const schema = defineSchemas({
  // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      listDataAll: [],
      rangeDeleteList: [],
      rangeDetailDeleteList: []
    }
  },
  OrgCatForm: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container siteAssessment',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        },
        query: {
          immediate: true,
          tree: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view'
            initButtonConfig($form)
            return $attrs.params.flag != 'add' && $attrs.params.orderId
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              "*":{},
              'rangeList': {'*': {}},
              'detailList': {'*': {}},
              'fileList': {'*': {}}
            }
            data.payload = {
              "filter": {
                  "orgCatFormId": {
                      eq: $attrs.params.orderId
                  }
              }
            }
            return data
          }`),
          transformResponse: expression(`(res) => {
            const ress = JSON.parse(res)
            const data = ress.data.records[0]
            $form.setValues(data)
            $form.query('.rangeData').take().value = data.rangeList
            $form.query('state').get('data').listDataAll = data.detailList
            updateButtonConfig($form)
            return ress
          }`)
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.siteFormId || null'),
          'business-type': 'supplierLimitation',
          '@click-handler': expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $submits(type, $form, $queryEngine, $message, $t, $bus)
          }`),
          '@close-tab': expression(`() => {
            $back($bus)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            properties: {
              layout: {
                type: 'void',
                'x-component': 'FormContainer',
                properties: {
                  collapse: {
                    type: 'void',
                    'x-component': 'Collapse',
                    properties: generateXindexInOrder({
                      // 合作终止单form
                      cooperationEndForm: {
                        type: 'void',
                        'x-component': 'CollapseItem',
                        'x-component-props': {
                          title: i18nExpression('cusEntry.vendorMod.cooperationEndedForm')
                        },
                        'x-query-engine-skip': true,
                        properties: {
                          coopForm: {
                            type: 'void',
                            'x-query-engine-skip': true,
                            'x-component': 'FormGrid',
                            'x-component-props': {
                              maxColumns: 4,
                              columnGap: 32,
                              rowGap: 0
                            },
                            properties: {
                              // 现场评审表单
                              ...forms
                            }
                          }
                        }
                      }
                    })
                  },
                  rangeData: {
                    type: 'Array',
                    'x-component': 'newRange',
                    'x-component-props': {
                      style: 'margin-top:-5px;',
                      'range-type': expression('$form.query(\'.supplierControlType\').take()?.value'),
                      'list-data-all': expression('$form.query(\'state\').get(\'data\')?.listDataAll'),
                      'cur-opt': expression('$attrs.params.flag')
                    }
                  },
                  collapseBusiness: {
                    type: 'void',
                    'x-component': 'Collapse',
                    'x-component-props': {
                      style: 'margin-top:-5px;border-top:0'
                    },
                    properties: generateXindexInOrder({
                      // 商务事项
                      businessForm: {
                        type: 'void',
                        'x-component': 'CollapseItem',
                        'x-component-props': {
                          title: i18nExpression('cusEntry.vendorMod.other')
                        },
                        'x-query-engine-skip': true,
                        properties: {
                          businessList: {
                            type: 'void',
                            'x-component': 'FormGrid',
                            'x-component-props': {
                              maxColumns: 1,
                              columnGap: 32,
                              rowGap: 0
                            },
                            properties: {
                              // businessMatterType: {
                              //   type: 'array',
                              //   title: '',
                              //   'x-decorator': 'FormItem',
                              //   'x-component': 'Checkbox.Group',
                              //   enum: [
                              //     {
                              //       label: i18nExpression('vendorMod.cooperationEndedDetail[0]'),
                              //       value: '20'
                              //     },
                              //     {
                              //       label: i18nExpression('vendorMod.cooperationEndedDetail[1]'),
                              //       value: '50'
                              //     },
                              //     {
                              //       label: i18nExpression('vendorMod.cooperationEndedDetail[2]'),
                              //       value: '30'
                              //     },
                              //     {
                              //       label: i18nExpression('vendorMod.cooperationEndedDetail[3]'),
                              //       value: '40'
                              //     },
                              //     {
                              //       label: i18nExpression('vendorMod.cooperationEndedDetail[4]'),
                              //       value: '10'
                              //     }
                              //   ]
                              // },
                              otherExplain: {
                                type: 'string',
                                title: i18nExpression('vendorMod.cooperationEndedDetail[5]'),
                                'x-decorator': 'FormItem',
                                'x-component-props': {
                                  type: 'textarea'
                                },
                                'x-validator': {
                                  required: true,
                                  message: i18nExpression('common.requiredField')
                                }
                              }
                            }
                          }
                        }
                      },
                      file: {
                        type: 'void',
                        'x-component': 'CollapseItem',
                        'x-component-props': {
                          title: i18nExpression('cusEntry.vendorMod.fileList')
                        },
                        'x-query-engine-skip': true,
                        properties: {
                          toolbar: {
                            type: 'void',
                            'x-component': 'ButtonList',
                            'x-component-props': {
                              class: 'list-form__toolbar'
                            },
                            'x-visible': expression('!$form.readPretty'),
                            properties: {
                              add: {
                                type: 'void',
                                title: i18nExpression('common.new'),
                                'x-component-props': {
                                  type: 'primary',
                                  '@click': expression(`() => {
                                    $self.query('.fileList').take(field => {
                                      field.componentProps.componentInstance.addRow('unshift', {
                                        fileId: null,
                                        fileName: null
                                      }) 
                                    })
                                  }`)
                                }
                              }
                            }
                          },
                          fileList: {
                            type: 'array',
                            'x-component': 'RenderTable',
                            'x-component-props': {
                              preColumns: 'seq',
                              editMode: true,
                              maxHeight: 400,
                              pagination: false,
                              sortable: false,
                              primaryKey: 'orgCatFormFileId',
                              // 启用级联删除的储值行为
                              cascadeDeletion: true
                            },
                            properties: generateXindexInOrder({
                              fileId: {
                                type: 'string',
                                title: i18nExpression('cusEntry.vendorMod.fileName'),
                                'x-component': 'SrmCommonFile',
                                'x-component-props': {
                                  'default-file': {
                                    fileId: '{{$table.getRowByIndex($self.index)?.fileId}}',
                                    fileName: '{{$table.getRowByIndex($self.index)?.fileName}}'
                                  },
                                  'extra-data': {
                                    fileModular: 'sup',
                                    fileFunction: 'SUPPLIER_RESTRICTION',
                                    fileType: 'images'
                                  },
                                  '@on-change': expression(`({file}) => {
                                    const { fileId, fileName } = file || {}
                                    const row = $table.getRowByIndex($self.index)
                                    row.fileId = fileId?.toString() || null
                                    row.fileName = fileName || null
                                  }`)
                                },
                                'x-render-table-column': {
                                  minWidth: 120
                                }
                              },
                              remark: {
                                type: 'string',
                                title: i18nExpression('cusEntry.vendorMod.remark'),
                                'x-render-table-column': {
                                  minWidth: 120
                                }
                              },
                              operation: {
                                type: 'void',
                                title: i18nExpression('common.operation'),
                                'x-render-table-column': {
                                  width: 60,
                                  fixed: 'right'
                                },
                                'x-visible': expression('!$form.readPretty'),
                                'x-component': 'RenderTableButtonList',
                                properties: {
                                  delete: {
                                    type: 'void',
                                    title: i18nExpression('common.delete'),
                                    'x-component-props': {
                                      type: 'text',
                                      '@click': expression(`({ row }) => {
                                        $table.remove($self.index)
                                      }`)
                                    }
                                  }
                                }
                              }
                            })
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
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('cooperationEnd')
}

const $submits = async (type, $form, $queryEngine, $message, $t, $bus) => {
  if (type == 'WITHDRAW') {
    emitTabRemove(attrs.tabName)
    $bus.$emit('cooperationEnd')
    return
  }
  $form.validate().then(e => {
    let url = ''
    const submitData = JSON.parse(JSON.stringify($form.values))
    let rangeData = submitData.rangeData
    let listDataAll = JSON.parse(JSON.stringify($form.query('state').get('data').listDataAll))
    const rangeDatas = $form.query('.rangeData').take().componentProps.componentInstance.$refs.range
    if (
      [
        'CATEGORY_EXIT',
        'CATEGORY_FORZEN',
        'CATEGORY_THAW',
        'CATEGORY_LIMIT_FLAG',
        'CATEGORY_LIMIT_FLAG_REMOVE'
      ].includes(submitData.supplierControlType)
    ) {
      // 如果是品类
      let bol2 = 0
      let optSelectRange =
        rangeDatas.rangeList.length > 0 ? rangeDatas.rangeList : rangeDatas.getRangeList() // 控制范围
      rangeData.forEach((datas: any) => {
        let categoryId = datas.categoryId
        let selectedIndex = optSelectRange.findIndex(i => i.categoryId == categoryId)
        if (selectedIndex > -1) {
          // 选择的设置为Y
          datas.selected = 'Y'
          bol2 += 1
        } else {
          datas.selected = 'N'
        }
      })
      if (bol2 == 0) {
        app.$message.warning(app.$t('dataConfMod.msgInputCate')) // 请输入品类
        return false
      }
      if (
        ['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW', 'CATEGORY_LIMIT_FLAG', 'CATEGORY_LIMIT_FLAG_REMOVE'].includes(
          submitData.supplierControlType,
        )
      ) {
        let bol1 = 0
        let selectedCategoryList =
          rangeDatas.categoryList.length > 0
            ? rangeDatas.categoryList
            : rangeDatas.getCategoryList() // 控制明细
        listDataAll.forEach((datas: any) => {
          let rowKey = datas.orgId + '_' + datas.categoryId
          let detailSelectedIndex = selectedCategoryList.findIndex(
            i => i.orgId + '_' + i.categoryId == rowKey,
          )
          if (detailSelectedIndex > -1) {
            // 选择的设置为Y
            datas.selected = 'Y'
            bol1 += 1
          } else {
            datas.selected = 'N'
          }
        })
        if (bol1 == 0) {
          app.$message.warning(app.$t('dataConfMod.msgInputUnit2')) // 请输入组织
          return false
        }
      }
    } else if (
      [
        'ORGANIZATION_EXIT',
        'ORGANIZATION_FORZEN',
        'ORGANIZATION_THAW',
        'POSITION_LIMIT_FLAG',
        'POSITION_LIMIT_FLAG_REMOVE'
      ].includes(submitData.supplierControlType)
    ) {
      // 组织退出
      let bol1 = 0
      let optSelectRange =
        rangeDatas.rangeList.length > 0 ? rangeDatas.rangeList : rangeDatas.getRangeList() // 控制范围
      rangeData.forEach(datas => {
        let orgId = datas.orgId
        let selectedIndex = optSelectRange.findIndex(i => i.orgId == orgId)
        if (selectedIndex > -1) {
          // 选择的设置为Y
          datas.selected = 'Y'
          bol1 += 1
        } else {
          datas.selected = 'N'
        }
      })
      if (bol1 == 0) {
        app.$message.warning(app.$t('dataConfMod.msgInputUnit2')) // 请输入组织
        return false
      }

      let rangeObj = {}
      rangeData.forEach(rangeItem => {
        if (rangeItem.selected == 'Y') {
          rangeObj[rangeItem.orgId] = rangeItem
        }
      })
      listDataAll.forEach(datas => {
        if (rangeObj.hasOwnProperty(datas.orgId)) {
          datas.selected = 'Y'
        } else {
          datas.selected = 'N'
        }
      })
    } else {
      rangeData = []
      listDataAll.forEach(datas => {
        datas.selected = 'Y'
      })
    }
    const rangeDeleteList = $form.query('state').get('data').rangeDeleteList
    const rangeDetailDeleteList = $form.query('state').get('data').rangeDetailDeleteList
    submitData.rangeList = [...rangeData, ...rangeDeleteList]
    submitData.detailList = [...listDataAll, ...rangeDetailDeleteList]
    delete submitData.rangeData
    const run = async () => {
      if ([null, undefined, 'DRAFT'].includes(submitData.approveStatus)) {
        submitData.approveStatus = 'DRAFT'
      }
      if (type == 'SAVE') {
        // 暂存的时候
        $queryEngine.request
          .save(submitData, {
            query: {
              '*': {},
              OrgCatFormCategoryRange: { '*': {} },
              OrgCatFormCategoryDetail: { '*': {} }
            },
            loading: true
          })
          .then(res => {
            $message.success($t('common.successSave'))
            $bus.$emit('cooperationEnd')
            emitTabRemove(attrs.tabName)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        // 提交
        $queryEngine.request
          .save(submitData, {
            query: {
              '*': {},
              OrgCatFormCategoryRange: { '*': {} },
              OrgCatFormCategoryDetail: { '*': {} }
            },
            tree: true,
            loading: true
          })
          .then(res => {
            const datas = res.data[0]
            let formHeaderValue = getHeaderField(datas)
            $form.values.orgCatFormId = res.data[0].orgCatFormId
            const componentInstance = $form.query('.SchemaWorkflow').take()
              .componentProps.componentInstance
            componentInstance.setWorkflowBusinessId(res.data[0]?.orgCatFormId || null)
            componentInstance.setWorkflowTabDisabled(true)
            componentInstance.setWorkflowBusinessVariables({
              procTitleObj: formHeaderValue
            })
            componentInstance.handlerAfter(type.toUpperCase(), () => {
              $bus.$emit('cooperationEnd')
              emitTabRemove(attrs.tabName)
            })
          })
      }
    }
    return run()
  })
}

const scope = {
  app,
  t,
  $attrs: attrs,
  emitTabRemove,
  initButtonConfig,
  updateButtonConfig,
  $back,
  $submits,
  blackComApi,
  orgCatForm
}

const components = {
  newRange
}
</script>

<template>
  <RenderEngine
    schemaKey="siteAssessmentDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
