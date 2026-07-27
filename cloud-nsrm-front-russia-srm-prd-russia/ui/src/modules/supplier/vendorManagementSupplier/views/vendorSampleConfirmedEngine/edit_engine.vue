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

const $disabledFlag = computed(() => {
  let flag = !!(attrs.params.flag === 'view')
  return flag
})

const $curOpt = computed(() => {
  return (attrs.params.flag !== 'view')
})

const $getCompanyObj = (val:any, $values:any) => {
  $values.vendorId = val ? val.companyId : ''
  $values.vendorCode = val ? val.companyCode : ''
  $values.vendorName = val ? val.companyName : ''
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
            orgId: '',
            orgCode: '',
            orgName: '',
            categoryName: item.categoryName,
            categoryCode: item.categoryCode,
            categoryId: item.categoryId,
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
      type: 'QuaSampleVendor',
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
          } else {
            emitTabRemove(attrs.tabName)
            $bus.$emit('QuaSample')
          }
        })
      }
    })
  })
}

// 暂存
const $stagingHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  $dataHandle('stage', $form, $queryEngine, $confirm, $message, $bus)
}

// 提交
const $publishHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  // TODO加上校验
  const form = toJS($form.values)
  const { orgCateJournals, fileRecords, ...rest } = form
  $form.validate().then(() => {
    if (!orgCateJournals.length) {
      $message.warning(t('vendorMod.sampleEmptyWarning'))
      return
    }
    $dataHandle('publish', $form, $queryEngine, $confirm, $message, $bus)
  })
}

// 确认
const $confirmedHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  $form.validate().then(() => {
    $dataHandle('confirmed', $form, $queryEngine, $confirm, $message, $bus)
  })
}

// 评价保存
const $saveTestResultsHandle = ($form:any, $queryEngine:any, $confirm:any, $message:any, $bus:any) => {
  $dataHandle('submittedSave', $form, $queryEngine, $confirm, $message, $bus)
}

const scope = {
  emitTabRemove,
  emitTabAdd,
  app,
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
  QuaSampleVendor: {
    type: 'void',
    'x-component': 'FormContainer',
    'x-decorator': 'QueryEngine',
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            let id = $attrs.params?.sampleId
            $values.sampleId = id
            $values.approveStatus = ''
            return !!id
          }`),
          transformRequest: expression(`(data,headers) => {
            data.payload = [$values.sampleId]
            data.query['*'] = {}
          }`),
          onSuccess: expression(`(res) => {
            console.log('read:::',res)
            const value = res.data[0]
            $form.readPretty = $readOnly
            $form.setValues({
              ...value
            })
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
          'x-visible': expression(`!!($values.approveStatus === 'PUBLISHED') && !$readOnly`),
          'x-component': 'Button',
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
              orgCateJournals: {
                type: 'array',
                'x-query-engine-skip': true,
                'x-query-engine-relation': 'orgCateJournals:*',
                'x-component': 'RenderTable',
                'x-component-props': {
                  // preColumns: 'seq',
                  editMode: true,
                  pagination: false,
                  maxHeight: '58vh',
                  sortable: false
                },
                properties: generateXindexInOrder({
                  vendorId: {
                    type: 'number',
                    'x-hidden': true
                  },
                  materialId: {
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
                      'read-pretty': true,
                      '@select': expression(`(node) => {
                        const row = $table.getRowByIndex($self.index)
                        $selectHandler(node,row)
                      }`)
                    }
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
                      'read-pretty': expression(`$readOnly`),
                      'name': 'scc_base_material_item_display',
                      'show-key': 'materialCode',
                      'pre-query-data': expression(`{
                        't.category_id': $self.query('.categoryId').get('value')
                      }`),
                      'disabled': true,
                      '@close-quicksearch': expression(`(node) => {
                        const row = $table.getRowByIndex($self.index)
                        $getItemObj(node,row)
                      }`)
                    }
                  },
                  materialName: {
                    type: 'string',
                    title: "{{$t('common.materialName')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-component-props': {
                      'disabled': true
                    }
                  },
                  quantity: {
                    type: 'string',
                    title: "{{$t('vendorMod.sampleQty')}}",
                    'x-render-table-column': {
                      minWidth: 120
                    },
                    'x-component-props': {
                      type: 'number',
                      'disabled': true
                    }
                  },
                  result: {
                    type: 'string',
                    title: "{{$t('vendorMod.testResult')}}",
                    'x-render-table-column': {
                      minWidth: 100
                    },
                    'x-reactions': expression(`(field) => {
                      let visible = ['APPROVED','SUBMITTED'].includes($values.approveStatus)
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
                      let visible = ['APPROVED','SUBMITTED'].includes($values.approveStatus)
                      field.visible = visible
                    }`),
                    'x-component-props': {
                      disabled: expression(`$values.approveStatus !== 'CONFIRMED' `)
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
              let visible = ['PUBLISHED','CONFIRMED','SUBMITTED','APPROVED'].includes($values.approveStatus)
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
                    'x-component': 'DictSelect',
                    'x-component-props': {
                      code: 'SEND_TYPE',
                      disabled: expression(`$values.approveStatus !== 'PUBLISHED' || !$curOpt.value`)
                    },
                    ...requiredValidatorSegment
                  },
                  expressNumber: {
                    type: 'string',
                    title: "{{$t('vendorMod.expressNum')}}",
                    'x-decorator': 'FormItem',
                    'x-component-props': {
                      disabled: expression(`$values.approveStatus !== 'PUBLISHED' || !$curOpt.value`)
                    }
                  },
                  estimatedDeliveryTime: {
                    title: "{{$t('vendorMod.estimatedDeliveryTime')}}",
                    'x-decorator': 'FormItem',
                    ...yearMonthDaySelectorSegment,
                    'x-component-props': {
                      ...yearMonthDaySelectorSegment['x-component-props'],
                      disabled: expression(`$values.approveStatus !== 'PUBLISHED' || !$curOpt.value`)
                    },
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
                      type: 'textarea',
                      disabled: expression(`$values.approveStatus !== 'PUBLISHED' || !$curOpt.value`)
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
})

</script>

<template>
  <RenderEngine schemaKey="vendorSampleConfirmedVendorDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>
