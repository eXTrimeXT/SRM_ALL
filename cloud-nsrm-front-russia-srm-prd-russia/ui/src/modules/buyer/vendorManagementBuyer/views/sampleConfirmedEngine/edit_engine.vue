<!-- eslint-disable quotes -->
<script setup lang="ts">
import { defineSchemas, i18nExpression, expression, changeFieldVisibleByDeps, generateXindexInOrder, toJS, connect, mapProps } from '@meicloud/render-engine'
import { RenderEngine } from 'lib@/components/render-engine'
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import { requiredValidatorSegment, editTableFormItemValid, yearMonthDaySelectorSegment } from 'lib@/components/render-engine/schema-segments'
import { useAttrs, computed, ref } from 'vue-demi'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import attachment, { $addAttachmentRow, fileValid } from 'modb@/vendorManagementBuyer/views/sampleConfirmedEngine/attachment'
import { saveOrUpdateOrderByUrl, accessCommonApi, quaApi, quaSampleApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

const { emitTabRemove, emitTabAdd, t, app } = usePageHelper()

let attrs:any = useAttrs()

const workflowStatus = ref('DRAFT') // 样品确认单状态

const $disabledFlag = computed(() => {
  let flag = !!(attrs.params.flag === 'view')
  return flag
})

const $curOpt = computed(() => {
  return (attrs.params.flag !== 'view')
})

const viewUpdateButton = computed(() => ['CONFIRMED'].includes(workflowStatus.value))

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take()?.componentProps.componentInstance
    if (!componentInstance) return
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value

    componentInstance.setWorkflowBusinessId($form.values.sampleId)
    componentInstance.setWorkflowTabDisabled(!['SUBMITTED', 'APPROVED'].includes($form.values.approveStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const $getCompanyObj = (val:any, $values:any) => {
  $values.vendorId = val ? val.companyId : ''
  $values.vendorCode = val ? val.companyCode : ''
  $values.vendorName = val ? val.companyName : ''
  $values.reviewFormId = null
  $values.reviewFormNumber = null
  $values.orgCateJournals = []
}

/** 资质审查选择回调 --start */
const $getReviewFormObj = (val:any, $form:any) => {
  let quaReviewType = val ? val.quaReviewType : ''
  $form.values.quaReviewType = val ? quaReviewType : ''
  $form.values.reviewFormNumber = val ? val.reviewFormNumber : ''
  $form.values.reviewFormId = val ? val.reviewFormId : ''
  // 获取组织品类关系
  if (val) {
    fatchIsMtTry(val.reviewFormId, $form) // 判断是否需要物料试用
    let query = { reviewFormId: val.reviewFormId, type: 'SAMPLE' }
    fatchQuaFileConfig(query, $form)
    fatchCatOrgDatas({ reviewFormId: val.reviewFormId }, $form) // 查询组织品类物料
  } else {
    $form.values.orgCateJournals = []
  }
}

// 查询是否物料试用
const fatchIsMtTry = async (reviewFormId:any, $form:any) => {
  const res = await quaSampleApi.getEntryConfigRecord({
    reviewFormId
  })
  $form.values.isMaterialTrial = 'Y'
  let isTrial
  if (res) {
    let trialProcess = res.data.ifMaterial
    if (trialProcess === 'Y') {
      $form.values.isMaterialTrial = 'Y'
      isTrial = true
    } else if (trialProcess === 'N') {
      $form.values.isMaterialTrial = 'N'
      isTrial = true
    } else if (trialProcess === 'C') {
      $form.values.isMaterialTrial = 'N'
      isTrial = true
    } else {
      $form.values.isMaterialTrial = ''
      isTrial = false
    }
    $form.values.isTrial = isTrial
  }
}

// 查询附件
const fatchQuaFileConfig = (query:any, $form:any) => {
  quaApi.getTemplateFilesByReviewFormId(query).then((res) => {
    if (res.data && res.data.length > 0) {
      $form.values.fileRecords = res.data.map((i) => ({
        ...i,
        fileId: '',
        fileName: ''
      }))
    } else {
      $form.values.fileRecords = []
    }
  })
}

// 根据资质审查单查询组织品类物料信息
const fatchCatOrgDatas = (query:any, $form:any) => {
  quaApi.listOrgCateJournalByReviewId(query).then((res) => {
    $form.values.orgCateJournals = res.data || []
  })
}
/** 资质审查选择回调 --end */
const $getMaterialList = (data:any, $form:any) => {
  if (data.length > 0) {
    let materialIdList = []
    for (let item of $form.values.orgCateJournals) {
      item.materialId && materialIdList.push(item.materialId)
    }
    console.log(data)
    data.forEach(item => {
      if (item.materialId && !materialIdList.includes(item.materialId)) {
        $form.query('orgCateJournals').take(field => {
          field.value.unshift({
            orgId: null,
            orgCode: null,
            orgName: null,
            categoryName: item.categoryName,
            categoryCode: item.categoryCode,
            categoryId: item.categoryId,
            materialId: item.materialId,
            materialName: item.materialName,
            materialCode: item.materialCode,
            quantity: null,
            result: null,
            resultRemark: null
          })
        })
      }
    })
  }
}

const $selectHandler = (node:any, row:any) => {
  row.orgId = node ? node.organizationId : null
  row.orgCode = node ? node.organizationCode : null
  row.orgName = node ? node.organizationName : null
}

// 确认选择物料
const $getItemObj = (val, data) => {
  data.materialId = val ? val.materialId : null
  data.materialCode = val ? val.materialCode : ''
  data.materialName = val ? val.materialName : ''
}

const $dataHandle = async (type:any, $form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
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
  if (type !== 'stage') await $form.validate()
  if (!fileValid($form, $message, t)) return
  return $queryEngine.request.baseRequest({
    type: 'QuaSample',
    // action: rest.sampleId ? 'update' : 'save',
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
      $form.values.sampleId = result.sampleId
      $queryEngine.request.read().then((response:any) => {
        if (type === 'SUBMIT') {
          const tabDisabled = false
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId($form.values.sampleId)
          componentInstance.setWorkflowTabDisabled(tabDisabled)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            console.log('handlerAfter QuaSample')
            emitTabRemove(attrs.tabName)
            $bus.$emit('QuaSample')
          })
        } else if (type === 'stage') { // 暂存不做处理

        } else {
          emitTabRemove(attrs.tabName)
          $bus.$emit('QuaSample')
        }
      })
    }
  })
}

// 暂存
const $stagingHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  return $dataHandle('stage', $form, $queryEngine, $confirm, $message, $bus)
}

// 提交
const $publishHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  // TODO加上校验
  const form = toJS($form.values)
  const { orgCateJournals, fileRecords, ...rest } = form
  return $form.validate().then(() => {
    if (!orgCateJournals.length) {
      $message.warning(t('vendorMod.sampleEmptyWarning'))
      return
    }
    return $dataHandle('publish', $form, $queryEngine, $confirm, $message, $bus)
  })
}

// 确认
const $confirmedHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  return $form.validate().then(() => {
    return $dataHandle('confirmed', $form, $queryEngine, $confirm, $message, $bus)
  })
}

// 评价保存
const $saveTestResultsHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  return $dataHandle('submittedSave', $form, $queryEngine, $confirm, $message, $bus)
}

const scope = {
  emitTabRemove,
  emitTabAdd,
  app,
  initButtonConfig,
  workflowStatus,
  updateButtonConfig,
  $disabledFlag,
  $curOpt,
  $stagingHandle,
  $publishHandle,
  $confirmedHandle,
  $saveTestResultsHandle,
  $getCompanyObj,
  $getReviewFormObj,
  $getMaterialList,
  $selectHandler,
  $getItemObj,
  $dataHandle,
  $addAttachmentRow
}

const components = {
  VendorAccessSteps
}

const schema = defineSchemas({
  QuaSample: {
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
        read: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            let id = $attrs.params?.sampleId
            $values.sampleId = id
            $values.approveStatus = ($values.approveStatus || 'DRAFT')
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$values.sampleId]
            data.query['*'] = {}
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res)
            const value = res.data[0]
            workflowStatus.value = value.approveStatus
            $form.readPretty = $readOnly
            $form.setValues({
              ...value
            })
            updateButtonConfig($form)
          }`)
        },
        save: {
          // 启用级联删除的储值行为
          cascadeDeletion: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params?.sampleId || null'),
          'business-type': 'QUASAMPLE',
          'ref-name': 'workflowMulti',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            console.log('click-handler', type, $form, $confirm, $message)
            return $dataHandle(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            console.log('submit-direct', type)
            return $dataHandle(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            console.log('confirm', type)
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
            $bus.$emit('QuaSample')
          }`)
        },
        properties: {
          buttonList: {
            type: 'void',
            'x-component': 'ButtonList',
            'x-slot': 'buttonOne',
            properties: {
              cancel: {
                type: 'void',
                title: "{{$t('common.cancel')}}",
                'x-component-props': {
                  '@click': expression(`() => {
                    emitTabRemove($attrs.tabName)
                    $bus.$emit('QuaSample')
                  }`)
                }
              },
              staging: {
                type: 'void',
                title: "{{$t('common.staging')}}",
                'x-visible': expression(`['DRAFT', '', 'REJECTED', 'WITHDRAW'].includes($values.approveStatus) && !$readOnly`),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression(`() => {
                    return $stagingHandle($form,$queryEngine,$confirm,$message,$bus)
                  }`)
                }
              },
              submit: {
                type: 'void',
                title: "{{$t('common.submit')}}",
                'x-visible': expression(`['DRAFT', '', 'REJECTED', 'WITHDRAW'].includes($values.approveStatus) && !$readOnly`),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression(`() => {
                    return $publishHandle($form,$queryEngine,$confirm,$message,$bus)
                  }`)
                }
              },
              saveEvaluate: {
                type: 'void',
                title: "{{$t('vendorMod.saveEvaluate')}}",
                'x-visible': expression(`$values.approveStatus === 'CONFIRMED' && !$readOnly`),
                'x-component-props': {
                  type: 'primary',
                  '@click': expression(`() => {
                    return $saveTestResultsHandle($form,$queryEngine,$confirm,$message,$bus)
                  }`)
                }
              }
            }
          },
          vendorAccessSteps: {
            type: 'void',
            'x-component': 'VendorAccessSteps',
            'x-component-props': {
              'access-type': 'sample',
              'approve-status': expression(`$values.approveStatus`),
              'style': 'margin-bottom:30px'
            }
          },
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-component-props': {
              defaultOpenPanelCount: 1
            },
            properties: {
              // 采购商填写
              sampleOrderInfoBuyer: {
                type: 'void',
                'x-query-engine-skip': true,
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('vendorMod.sampleOrderInfo')}}"
                },
                properties: {
                  sampleForm: {
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
                      sampleId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      vendorId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      isTrial: {
                        type: 'boolean',
                        'x-hidden': true,
                        default: false
                      },
                      sampleNumber: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: "{{$t('vendorMod.sampleNum')}}",
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      vendorName: {
                        type: 'string',
                        title: "{{$t('common.vendorName')}}",
                        'x-decorator': 'FormItem',
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          'read-pretty': expression(`$readOnly`),
                          'show-key': 'companyName',
                          'prop-key': 'companyName',
                          'name': 'scc_sup_company_info2',
                          '@close-quicksearch': expression(`(val) => {
                            $getCompanyObj(val,$values)
                          }`)
                        },
                        ...requiredValidatorSegment,
                        'x-reactions': {
                          dependencies: ['.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[0])`)
                            }
                          }
                        }
                      },
                      reviewFormNumber: {
                        type: 'string',
                        title: "{{$t('vendorMod.quaNum')}}",
                        'x-decorator': 'FormItem',
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          'read-pretty': expression(`$readOnly`),
                          'show-key': 'reviewFormNumber',
                          'name': 'scc_sup_auth_review_form',
                          '@close-quicksearch': expression(`(val) => {
                            $getReviewFormObj(val,$form)
                          }`)
                        },
                        'x-reactions': {
                          dependencies: ['.vendorId', '.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].preQueryData': expression(`{
                                't.vendor_id': $deps[0],
                                't.approve_status': 'APPROVED',
                                't.CEEA_IF_VENDOR_AUTH': 'Y'
                              }`),
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[1])`)
                            }
                          }
                        }
                      },
                      receiver: {
                        type: 'string',
                        title: "{{$t('vendorMod.sampleReceiver')}}",
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment,
                        'x-reactions': {
                          dependencies: ['.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[0])`)
                            }
                          }
                        }
                      },
                      receiverPhone: {
                        type: 'string',
                        title: "{{$t('vendorMod.receiverPhone')}}",
                        'x-decorator': 'FormItem',
                        ...requiredValidatorSegment,
                        'x-reactions': {
                          dependencies: ['.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[0])`)
                            }
                          }
                        }
                      },
                      requireSendTime: {
                        type: 'string',
                        title: "{{$t('vendorMod.sendTime')}}",
                        'x-decorator': 'FormItem',
                        ...yearMonthDaySelectorSegment,
                        ...requiredValidatorSegment,
                        'x-reactions': {
                          dependencies: ['.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[0])`)
                            }
                          }
                        }
                      },
                      isMaterialTrial: {
                        type: 'string',
                        title: "{{$t('vendorMod.isTrial')}}",
                        'x-decorator': 'FormItem',
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'YES_OR_NO'
                        },
                        'x-reactions': {
                          dependencies: ['.isTrial', '.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || $deps[0] || !['DRAFT', '', 'REJECTED'].includes($deps[1])`)
                            }
                          }
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
                      buyerConfirmRemark: {
                        type: 'string',
                        title: "{{$t('vendorMod.vendorConfirmRemark')}}",
                        'x-decorator': 'FormItem',
                        'x-decorator-props': {
                          gridSpan: 2
                        },
                        'x-component-props': {
                          type: 'textarea',
                          autosize: { minRows: 2, maxRows: 4 }
                        },
                        'x-reactions': {
                          dependencies: ['.approveStatus'],
                          fulfill: {
                            state: {
                              'component[1].disabled': expression(`$disabledFlag.value || !['DRAFT', '', 'REJECTED'].includes($deps[0])`)
                            }
                          }
                        }
                      }
                    }
                  }
                }
              },
              // 样品信息
              sampleInfo: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('vendorMod.sampleInfo')}}"
                },
                properties: {
                  toolbar: {
                    type: 'void',
                    'x-component': 'Space',
                    'x-component-props': {
                      style: 'margin-bottom:16px;display:block;'
                    },
                    'x-reactions': expression(`field => {
                      field.visible = !$readOnly && ['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus)
                    }`),
                    properties: {
                      add: {
                        type: 'void',
                        'x-component': 'QuickSearch',
                        'x-component-props': {
                          name: 'scc_base_material_item_display',
                          showButton: true,
                          multiSelect: true,
                          btnTitle: "{{$t('common.add')}}",
                          '@close-quicksearch': expression(`(data) => {
                            $getMaterialList(data,$form)
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
                      // preColumns: 'seq',
                      editMode: true,
                      pagination: false,
                      maxHeight: 400,
                      sortable: false,
                      primaryKey: 'orgCateJourId',
                      cascadeDeletion: true
                    },
                    properties: generateXindexInOrder({
                      orgCateJourId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      vendorId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      orgId: { // TODO少了vendorId判断
                        type: 'string',
                        title: "{{$t('vendorMod.organization')}}",
                        'x-render-table-column': {
                          minWidth: 120
                        },
                        'x-component': 'OrganizationSelector',
                        'x-component-props': {
                          'node-type': 'OU',
                          'parent-id': -1,
                          'placeholder': "{{$t('common.pleaseSelect')}}",
                          'readPretty': expression(`!['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus) || !$curOpt.value || !!$table.getRowByIndex($self.index)?.vendorId`),
                          '@select': expression(`(node) => {
                            const row = $table.getRowByIndex($self.index)
                            $selectHandler(node,row)
                          }`)
                        }
                      },
                      orgCode: {
                        type: 'string',
                        'x-hidden': true
                      },
                      orgName: {
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
                      categoryId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      categoryCode: {
                        type: 'string',
                        'x-hidden': true
                      },
                      materialId: {
                        type: 'number',
                        'x-hidden': true
                      },
                      materialCode: {
                        type: 'string',
                        title: "{{$t('common.materialCode')}}",
                        'x-render-table-column': {
                          minWidth: 120
                        },
                        'x-component': 'QuickSearchWrapper',
                        'x-component-props': {
                          'name': 'scc_base_material_item_display',
                          'show-key': 'materialCode',
                          'readPretty': expression(`!['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus) || !$curOpt.value`),
                          '@close-quicksearch': expression(`(node) => {
                            const row = $table.getRowByIndex($self.index)
                            $getItemObj(node,row)
                          }`)
                        },
                        'x-reactions': expression(`(field) => {
                          let categoryId = $table.getRowByIndex($self.index)?.categoryId
                          $self.setComponentProps({ preQueryData: {'t.category_id':categoryId} })
                        }`),
                        ...editTableFormItemValid
                      },
                      materialName: {
                        type: 'string',
                        title: "{{$t('common.materialName')}}",
                        'x-render-table-column': {
                          minWidth: 120
                        },
                        'x-read-pretty': true
                      },
                      quantity: {
                        type: 'string',
                        title: "{{$t('vendorMod.sampleQty')}}",
                        'x-render-table-column': {
                          minWidth: 120
                        },
                        'x-component-props': {
                          type: 'number',
                          'disabled': expression(`!['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus) || !$curOpt.value`)
                        }
                      },
                      result: {
                        type: 'string',
                        'x-render-table-column': {
                          title: "{{$t('vendorMod.testResult')}}",
                          minWidth: 100
                        },
                        'x-reactions': expression(`(field) => {
                          let visible = ['CONFIRMED','APPROVED','SUBMITTED'].includes($values.approveStatus)
                          field.visible = visible
                        }`),
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'SAMPLE_TEST_RESULT',
                          disabled: expression(`$values.approveStatus !== 'CONFIRMED' `)
                        },
                        ...editTableFormItemValid
                      },
                      resultRemark: {
                        type: 'string',
                        title: "{{$t('vendorMod.sampleTestResult')}}",
                        'x-render-table-column': {
                          minWidth: 120
                        },
                        'x-reactions': expression(`(field) => {
                          let visible = ['CONFIRMED','APPROVED','SUBMITTED'].includes($values.approveStatus)
                          field.visible = visible
                        }`),
                        'x-component-props': {
                          disabled: expression(`$values.approveStatus !== 'CONFIRMED' `)
                        }
                      },
                      operation: {
                        type: 'void',
                        title: "{{$t('common.operation')}}",
                        'x-render-table-column': {
                          width: 100
                        },
                        'x-component': 'RenderTableButtonList',
                        'x-reactions': expression(`(field) => {
                          field.visible = (['DRAFT','WITHDRAW','REJECTED'].includes($values.approveStatus) && $curOpt.value)
                        }`),
                        properties: {
                          delete: {
                            type: 'void',
                            title: "{{$t('common.delete')}}",
                            'x-component-props': {
                              type: 'text',
                              '@click': expression(`({rowIndex}) => {
                                $table.remove(rowIndex)
                              }`)
                            }
                          }
                        }
                      }
                    })
                  }
                }
              },
              // 供方填写样品确认单
              sampleOrderInfoVendor: {
                type: 'void',
                'x-query-engine-skip': true,
                'x-decorator': 'FormLayout',
                'x-decorator-props': {
                  layout: 'vertical'
                },
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: "{{$t('vendorMod.sampleOrderInfo')}}"
                },
                'x-reactions': expression(`(field) => {
                  let visible = ['CONFIRMED','SUBMITTED','APPROVED'].includes($values.approveStatus)
                  field.visible = visible
                }`),
                properties: {
                  vendorConfirmInfo: {
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
                      expressType: {
                        type: 'string',
                        title: "{{$t('vendorMod.expressType')}}",
                        'x-decorator': 'FormItem',
                        'x-read-pretty': true,
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'SEND_TYPE'
                        }
                      },
                      expressNumber: {
                        type: 'string',
                        title: "{{$t('vendorMod.expressNum')}}",
                        'x-decorator': 'FormItem',
                        'x-read-pretty': true
                      },
                      estimatedDeliveryTime: {
                        title: "{{$t('vendorMod.estimatedDeliveryTime')}}",
                        'x-decorator': 'FormItem',
                        ...yearMonthDaySelectorSegment,
                        'x-read-pretty': true
                      },
                      vendorConfirmRemark: {
                        type: 'string',
                        title: "{{$t('vendorMod.vendorConfirmRemark')}}",
                        'x-decorator': 'FormItem',
                        'x-decorator-props': {
                          gridSpan: 2
                        },
                        'x-read-pretty': true,
                        'x-component-props': {
                          type: 'textarea'
                        }
                      }
                    }
                  }
                }
              },
              // 附件
              vendorAccessAttachment: {
                ...attachment
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
  <RenderEngine
    schemaKey="sampleConfirmedDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
