<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, generateXindexInOrder, toJS, expression, i18nExpression, generateCharFunctionExpression, connect, mapProps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { useAttrs, computed, ref } from 'vue-demi'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import attachment, { $addAttachmentRow, fileValid } from 'modb@/vendorManagementBuyer/views/sampleConfirmedEngine/attachment'
import { saveOrUpdateOrderByUrl, accessCommonApi, materialTrialApi, quaApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

const schema = defineSchemas({
  MaterialTrialVendor: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'FormContainer',
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            let id = $attrs.params?.materialTrialId || $values.materialTrialId
            $values.materialTrialId = id
            $values.approveStatus = ($values.approveStatus || 'DRAFT')
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$values.materialTrialId]
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res[0])
            if(res?.data[0]){
              const value = res.data[0]
              workflowStatus.value = value.approveStatus
              $form.setValues({
                ...value
              })
              $form.readPretty = $readOnly
            }
          }`)
        },
        save: {
          // 启用级联删除的储值行为
          cascadeDeletion: true
        }
      }
    },
    items: {
      type: 'object',
      properties: {
        cancel: {
          type: 'void',
          'x-content': "{{$t('common.cancel')}}",
          'x-component': 'Button',
          'x-component-props': {
            type: 'default',
            '@click': expression(`() => {
              emitTabRemove($attrs.tabName)
            }`)
          }
        },
        affirm: {
          type: 'void',
          'x-content': "{{$t('common.affirm')}}",
          'x-visible': expression(`['PUBLISHED'].includes($values.approveStatus) && !$readOnly`),
          'x-component': 'RButton',
          'x-component-props': {
            type: 'primary',
            '@click': expression(`() => {
              $confirmedHandle($form,$queryEngine,$confirm,$message,$bus)
            }`)
          }
        }
      }
    },
    properties: {
      vendorAccessSteps: {
        type: 'void',
        'x-component': 'VendorAccessSteps',
        'x-component-props': {
          'access-type': 'material',
          'approve-status': expression(`$values.approveStatus`),
          'style': 'margin-bottom:30px;'
        }
      },
      collapse: {
        type: 'void',
        'x-component': 'Collapse',
        'x-component-props': {
          defaultOpenPanelCount: 1
        },
        properties: {
          // 物料使用单
          mtTrialOrderInfo: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.mtTrialNum')}}"
            },
            properties: {
              materialForm: {
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
                properties: generateXindexInOrder({
                  materialTrialId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  sourceData: {
                    type: 'string',
                    'x-hidden': true,
                    default: '手工创建'
                  },
                  trialNumber: {
                    type: 'string',
                    title: "{{$t('vendorMod.mtTrialNum')}}",
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: true
                    }
                  },
                  vendorId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  vendorCode: {
                    type: 'string',
                    'x-hidden': true
                  },
                  vendorName: {
                    type: 'string',
                    title: "{{$t('common.vendorName')}}",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      name: 'scc_sup_company_info2',
                      showKey: 'companyName',
                      propKey: 'companyName',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      readPretty: expression(`$readOnly`),
                      '@close-quicksearch': expression(`(val) => {
                        $values.vendorId = val ? val.companyId : null
                        $values.vendorCode = val ? val.companyCode : null
                        $values.vendorName = val ? val.companyName : null
                        $values.entryType = null
                        $values.reviewFormNumber = null
                        $values.reviewFormId = null
                        $values.sampleId = null
                        $values.sampleNumber = null
                        $values.orgCateJournals = []
                      }`)
                    }
                  },
                  entryType: {
                    type: 'string',
                    title: "物料试用类型",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'MATERRIAL_ENTRY_TYPE',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      '@change': expression(`(val) => {
                        $values.reviewFormNumber = null
                        $values.reviewFormId = null
                        $values.sampleId = null
                        $values.sampleNumber = null
                        $values.orgCateJournals = []
                      }`)
                    }
                  },
                  reviewFormId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  reviewFormNumber: {
                    type: 'string',
                    title: '资质审查单号',
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    'x-visible': expression(`$values.entryType === 'ENTRY_TYPE'`),
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      name: 'scc_sup_auth_review_form',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      readPretty: expression(`$readOnly`),
                      showKey: 'reviewFormNumber',
                      'pre-query-data': expression(`{
                        't.vendor_id': $values.vendorId,
                        't.approve_status': 'APPROVED',
                        't.CEEA_IF_VENDOR_AUTH': 'Y'
                      }`),
                      '@close-quicksearch': expression(`(val) => {
                        getReviewFormObj($values, val)
                      }`)
                    }
                  },
                  sampleId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  sampleNumber: {
                    type: 'string',
                    title: "{{$t('vendorMod.sampleNum')}}",
                    'x-decorator': 'FormItem',
                    'x-visible': expression(`$values.entryType === 'OTHER_TYPE'`),
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      name: 'scc_sup_auth_qua_sample',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      readPretty: expression(`$readOnly`),
                      'show-key': 'sampleNumber',
                      'pre-query-data': expression(`{
                        // 查询样品确认单入参设置
                        't.vendor_id': $values.vendorId,
                        't.is_material_trial': 'Y'
                      }`),
                      '@close-quicksearch': expression(`(val) => {
                        getSimpleFormObj($values, val)
                      }`)
                    }
                  },
                  trialRequireTime: {
                    type: 'string',
                    title: "{{$t('vendorMod.mtRequireTime')}}",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`)
                    }
                  },
                  trialStartDate: {
                    type: 'string',
                    title: "{{$t('vendorMod.trialStartTime')}}",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      'picker-options': expression(`{
                        disabledDate:(time) => {
                          const endDateVal = new Date($values.trialEndDate).getTime()
                          if (endDateVal) {
                            return time.getTime() > endDateVal - 0
                          }
                        }
                      }`)
                    }
                  },
                  trialEndDate: {
                    type: 'string',
                    title: "{{$t('vendorMod.trialEndTime')}}",
                    'x-decorator': 'FormItem',
                    ...requiredValidatorSegment,
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      'picker-options': expression(`{
                        disabledDate:(time) => {
                          const beginDateVal = new Date($values.trialStartDate).getTime()
                          if (beginDateVal) {
                            return time.getTime() < beginDateVal - 0
                          }
                        }
                      }`)
                    }
                  },
                  tryBatch: {
                    type: 'string',
                    title: "{{$t('vendorMod.tryBatch')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'TRIAL_TIMES',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`)
                    }
                  },
                  approveStatus: {
                    type: 'string',
                    title: "{{$t('vendorMod.orderStatus')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'SAMPLE_STATUS',
                      disabled: true
                    }
                  },
                  trialInstruction: {
                    type: 'string',
                    title: "{{$t('vendorMod.trialRemark')}}",
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 2
                    },
                    'x-component-props': {
                      type: 'textarea',
                      disabled: expression(`$values.approveStatus !== 'DRAFT'`),
                      rows: 2
                    }
                  }
                })
              }
            }
          },
          // 试用信息
          trialInfo: {
            type: 'void',
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.trialInfo')}}"
            },
            properties: {
              toolbar: {
                type: 'void',
                'x-component': 'Space',
                'x-component-props': {
                  style: 'margin-bottom:5px;display:block;'
                },
                'x-reactions': expression(`field => {
                  field.visible = !$readOnly && ['DRAFT'].includes($values.approveStatus)
                }`),
                properties: {
                  add: {
                    type: 'void',
                    'x-component': 'QuickSearch',
                    'x-component-props': {
                      name: 'scc_base_material_item_display',
                      'btn-title': "{{$t('common.add')}}",
                      showButton: true,
                      multiSelect: true,
                      '@close-quicksearch': expression(`(data) => {
                        getMaterialList($form, $values, data)
                      }`)
                    }
                  }
                }
              },
              orgCateJournals: {
                type: 'array',
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'orgCateJournals:*',
                'x-component': 'RenderTable',
                'x-component-props': {
                  editMode: true,
                  pagination: false,
                  maxHeight: '58vh',
                  sortable: false,
                  primaryKey: 'orgCateJournalId',
                  cascadeDeletion: true
                },
                properties: generateXindexInOrder({
                  orgCateJournalId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  orgName: {
                    type: 'string',
                    'x-hidden': true
                  },
                  orgCode: {
                    type: 'code',
                    'x-hidden': true
                  },
                  orgId: {
                    type: 'string',
                    title: "{{$t('vendorMod.organization')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-component': 'OrganizationSelector',
                    'x-component-props': {
                      readPretty: true,
                      nodeType: 'OU',
                      '@select': expression(`(val) => {
                        const row = $table.getRowByIndex($self.index)
                        row.orgId = val ? val.organizationId : null
                        row.orgCode = val ? val.organizationCode : null
                        row.orgName = val ? val.organizationName : null
                      }`)
                    }
                  },
                  categoryId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  categoryCode: {
                    type: 'string',
                    'x-hidden': true
                  },
                  categoryName: {
                    type: 'string',
                    title: "{{$t('common.category')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-read-pretty': true
                  },
                  materialCode: {
                    type: 'string',
                    title: "{{$t('common.materialCode')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-component': 'QuickSearchWrapper',
                    'x-component-props': {
                      name: 'scc_base_material_item_display',
                      showKey: 'materialCode',
                      readPretty: true,
                      'pre-query-data': expression(`{
                        't.category_id':$self.query('.categoryId').get('value')
                      }`),
                      '@close-quicksearch': expression(`(val) => {
                        const row = $table.getRowByIndex($self.index)
                        row.materialId = val ? val.materialId : null
                        row.materialCode = val ? val.materialCode : null
                        row.materialName = val ? val.materialName : null
                      }`)
                    }
                  },
                  materialName: {
                    type: 'string',
                    title: "{{$t('common.materialName')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-read-pretty': true
                  },
                  // otherTrialQty: { // otherTrialQty
                  //   type: 'string',
                  //   title: "{{$t('vendorMod.trialQty')}}",
                  //   'x-render-table-column': {
                  //     minWidth: 120
                  //   },
                  //   'x-reactions': {
                  //     dependencies: ['.quantity'],
                  //     fulfill: {
                  //       state: {
                  //         'component[1].visible': expression(`!$deps[0]`)
                  //       }
                  //     }
                  //   }
                  // },
                  quantity: {
                    type: 'string',
                    title: "{{$t('vendorMod.trialQty')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-read-pretty': true
                    // 'x-reactions': {
                    //   dependencies: ['.otherTrialQty'],
                    //   fulfill: {
                    //     state: {
                    //       'component[1].visible': expression(`!$deps[0]`)
                    //     }
                    //   }
                    // }
                  },
                  result: {
                    type: 'string',
                    title: "{{$t('vendorMod.trialResult')}}",
                    'x-render-table-column': {
                      minWidth: 100
                    },
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'SAMPLE_TEST_RESULT'
                    },
                    'x-reactions': expression(`field => {
                      field.visible = ['CONFIRMED','SUBMITTED','APPROVED'].includes($values.approveStatus)
                    }`),
                    'x-read-pretty': true,
                    ...editTableFormItemValid
                  },
                  resultRemark: {
                    type: 'string',
                    title: "{{$t('vendorMod.sampleTestResult')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-reactions': expression(`field => {
                      field.visible = ['CONFIRMED','SUBMITTED','APPROVED'].includes($values.approveStatus)
                    }`),
                    'x-read-pretty': true,
                    'x-component-props': {
                      disabled: expression(`$values.approveStatus !== 'CONFIRMED'`)
                    }
                  }
                })
              }
            }
          },
          // 供应商确认信息
          vendorConfirm: {
            type: 'void',
            'x-query-engine-skip': true,
            'x-visible': expression(`['PUBLISHED','CONFIRMED','SUBMITTED','APPROVED'].includes($values.approveStatus)`),
            'x-component': 'CollapseItem',
            'x-component-props': {
              title: "{{$t('vendorMod.vendorConfirm')}}"
            },
            properties: {
              vendorForm: {
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
                properties: generateXindexInOrder({
                  expressType: {
                    type: 'string',
                    title: "{{$t('vendorMod.expressType')}}",
                    'x-decorator': 'FormItem',
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'SEND_TYPE'
                    },
                    ...requiredValidatorSegment
                  },
                  expressNumber: {
                    type: 'string',
                    title: "{{$t('vendorMod.expressNum')}}",
                    'x-decorator': 'FormItem'
                  },
                  estimatedDeliveryTime: {
                    title: "{{$t('vendorMod.estimatedDeliveryTime')}}",
                    ...yearMonthDaySelectorSegment,
                    'x-decorator': 'FormItem',
                     ...requiredValidatorSegment
                  },
                  vendorConfirmRemark: {
                    type: 'string',
                    title: "{{$t('vendorMod.vendorConfirmRemark')}}",
                    'x-decorator': 'FormItem',
                    'x-decorator-props': {
                      gridSpan: 2
                    },
                    'x-component-props': {
                      type: 'textarea'
                    }
                  }
                })
              }
            }
          },
          // 附件
          file: {
            ...attachment
          }
        }
      }
    }
  }
})

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

let attrs:any = useAttrs()

const workflowStatus = ref('DRAFT')

const viewUpdateButton = computed(() => ['CONFIRMED'].includes(workflowStatus.value))

const initButtonConfig = ($form:any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
  }, 50)
}

const updateButtonConfig = ($form:any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.setWorkflowBusinessId($form.values.materialTrialId)
    componentInstance.setWorkflowTabDisabled(!['SUBMITTED', 'APPROVED', 'WITHDRAW', 'REJECTED'].includes(workflowStatus.value))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

// 选择资质审查单据回调 (快速查询回调)
const getReviewFormObj = ($values:any, val:any) => {
  if (val) {
    $values.reviewFormNumber = val ? val.reviewFormNumber : null
    $values.reviewFormId = val ? val.reviewFormId : null
    if (!$values.reviewFormId) return
    app.$http({
      url: '/api-sup/review/reviewForm/getReviewFormDTO',
      method: 'GET',
      params: { reviewFormId: $values.reviewFormId },
      loading: true
    }).then(res => {
      let { cateJournals, orgJournals } = res.data
      let arr = []
      for (let item of orgJournals) {
        for (let innerItem of cateJournals) {
          arr.push({
            orgId: item.orgId,
            orgCode: item.orgCode,
            orgName: item.orgName,
            categoryId: innerItem.categoryId,
            categoryCode: innerItem.categoryCode,
            categoryName: innerItem.categoryName,
            materialId: null,
            materialCode: null,
            materialName: null,
            quantity: null,
            result: null,
            resultRemark: null
          })
        }
      }
      $values.orgCateJournals = arr
    })
  }
}
// 查询附件信息
const fatchQuaFileConfig = (query:any, $values:any) => {
  quaApi.getTemplateFilesBySampleId(query).then((res) => {
    if (res.data && res.data.length > 0) {
      $values.fileRecords = res.data.map((i) => ({
        ...i,
        fileId: '',
        fileName: ''
      }))
    } else {
      $values.fileRecords = []
    }
  })
}
// 查询
const fatchSampleCatData = (sampleId:any, $values:any) => {
  if (sampleId) {
    accessCommonApi.getQuaSampleData({ sampleId }).then((res) => {
      if (res && res.data) {
        $values.orgCateJournals = res.data.orgCateJournals || []
      }
    })
  }
}

// 查询样品确认单回调
const getSimpleFormObj = ($values:any, val:any) => {
  $values.sampleId = val.sampleId || null
  $values.sampleNumber = val.sampleNumber || ''
  if (val) {
    let sampleId = val.sampleId
    if ($values.entryType !== 'ENTRY_TYPE') {
      fatchQuaFileConfig({ sampleId }, $values)
      fatchSampleCatData(sampleId, $values)
    }
  } else {
    $values.fileRecords = []
  }
}

// 物料试用信息
const getMaterialList = ($form:any, $values:any, data:any) => {
  if (data.length > 0) {
    let materialIdList = []
    for (let item of $values.orgCateJournals) {
      item.materialId && materialIdList.push(item.materialId)
    }
    data.forEach(item => {
      if (item.materialId && !materialIdList.includes(item.materialId)) {
        $form.query('orgCateJournals').take(field => {
            field.value.unshift({
            orgId: '',
            orgCode: '',
            orgName: '',
            categoryName: item.categoryName,
            categoryCode: item.categoryCode,
            materialId: item.materialId,
            materialName: item.materialName,
            materialCode: item.materialCode,
            quantity: '',
            result: '',
            resultRemark: ''
          })
        })
      }
    })
  }
}

const $dataHandle = (type:any, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  let form = toJS($form.values)
  let { orgCateJournals, fileRecords, ...rest } = form
  if (type === 'stage') {
    // 暂存
    form.approveStatus = 'DRAFT'
  } else if (type === 'publish') {
    // 采购商填完单据后发布
    form.approveStatus = 'PUBLISHED'
  } else if (type === 'confirmed') {
    // 供应商确认
    form.approveStatus = 'CONFIRMED'
  } else if (type === 'submittedSave') {
    // 保存评价结果
    form.approveStatus = 'CONFIRMED'
  } else if (type === 'SUBMIT') {
    // 采购商维护测试结果后提交审批
  }
  if (!fileValid($form, $message, t)) return
  $form.validate().then(() => {
    $queryEngine.request.baseRequest({
      type: 'MaterialTrialVendor',
      action: 'save',
      'query': {
        '*': {},
        'orgCateJournals': {
          '*': {}
        },
        'fileRecords': {
          '*': {}
        }
      },
      payload: [form]
    }).then((res:any) => {
      if (res?.data[0]) {
        let result = res?.data[0]
        $message({
          message: t('common.success'),
          type: 'success'
        })
        $form.values.materialTrialId = result.materialTrialId
        $queryEngine.request.read().then((response:any) => {
          if (type === 'SUBMIT') {
            const tabDisabled = false
            const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
            componentInstance.setWorkflowBusinessId($form.values.materialTrialId)
            componentInstance.setWorkflowTabDisabled(tabDisabled)
            componentInstance.setWorkflowBusinessVariables({})
            componentInstance.handlerAfter(type.toUpperCase(), () => {
              console.log('handlerAfter MaterialTrial')
              emitTabRemove(attrs.tabName)
              $bus.$emit('MaterialTrial')
            })
          } else {
            emitTabRemove(attrs.tabName)
            $bus.$emit('MaterialTrial')
          }
        })
      }
    })
  })
}

// 确认
const $confirmedHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  $form.validate().then(() => {
    $dataHandle('confirmed', $form, $queryEngine, $confirm, $message, $bus)
  })
}

const scope = {
  emitTabRemove,
  emitTabAdd,
  app,
  initButtonConfig,
  updateButtonConfig,
  getReviewFormObj,
  getSimpleFormObj,
  workflowStatus,
  getMaterialList,
  $dataHandle,
  $confirmedHandle,
  $addAttachmentRow
}

const components = {
  VendorAccessSteps
}

</script>
<template>
  <RenderEngine schemaKey="vendorMaterialTrialDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
