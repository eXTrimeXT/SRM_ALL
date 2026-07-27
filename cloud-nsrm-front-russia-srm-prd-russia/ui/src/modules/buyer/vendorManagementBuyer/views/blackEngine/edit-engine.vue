<!-- eslint-disable quotes -->
<script setup lang="ts">
import { i18nExpression, expression, defineSchemas, generateXindexInOrder, toJS } from '@meicloud/render-engine'
// @ts-ignore
import { useDebounceFn } from '@vueuse/core'
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
// @ts-ignore
import { usePageHelper } from "lib@/components/composables/usePageHelper"
// @ts-ignore
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import {
  requiredValidatorSegment,
  formGridSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

// @ts-ignore
import { setRepeatData, throttle } from 'lib@/utils/util'
import { useAttrs, ref } from 'vue-demi'
// @ts-ignore
import blackDetails from './components/blackDetails'
import FileInfo from './components/fileInfo'
import { bus } from 'lib@/components/render-engine/components/bus'

const { emitTabAdd, emitTabRemove, t: $t, app, vendor } = usePageHelper()

const $attrs: any = useAttrs()

const $closePageAndRefreshListPageData = ($bus: any) => {
  $bus.$emit('DeliveryNoteHead')
  emitTabRemove($attrs.tabName)
}

const $clearFormField = ($form:any) => {
  setTimeout(() => {
    $form.query('configForm').take().reset()
  })
}

const $addCompanyByImportExcel = ($form:any, list:any) => {
  let newArray = $form.query('blackCompanyList').get('value')
  list.forEach(item => {
    newArray.push(item)
  })
  $form.query('blackCompanyList').take().setValue(newArray)
}

const $addCompanyOneItem = ($form:any, $values:any, done:any, closeLoading:any) => {
  $form.query('.configForm').take().validate().then(() => {
    if ($values.blackCompanyList && $values.blackCompanyList.length) {
      if ($values.blackCompanyList.find(item => item.socialCreditCode === $values.configForm.socialCreditCode)) {
        return app.$message.warning($t('black.msgLcCode'))
      }
    }
    var checkData = {
      ...$form.values,
      blackCompanyList: [$values.configForm]
    }
    app.$http({
      url: '/api-sup/sup/black/checkSubmitData',
      method: 'POST',
      data: checkData,
      loading: true
    }).then((res: any) => {
      console.log('[submitEngine]', res)

      let newObj = {
        companyId: $values.configForm.companyId,
        companyName: $values.configForm.companyName || $values.configForm.vendorName,
        companyCode: $values.configForm.companyCode,
        companyType: $values.configForm.companyType,
        socialCreditCode: $values.configForm.socialCreditCode,
        legalPerson: $values.configForm.legalPerson,
        registeredCapital:
          $values.configForm.registeredCapital,
        companyCountry: $values.configForm.companyCountry,
        companyProvince: $values.configForm.companyProvince,
        companyCity: $values.configForm.companyCity,
        companyCreationDate: $values.configForm.companyCreationDate
      }
      let newArray = $form.query('blackCompanyList').get('value')
      newArray.push(newObj)
      $form.query('blackCompanyList').take().setValue(newArray)
      done()
    }).catch(() => {
      closeLoading()
    })
  }).catch(() => {
    closeLoading()
  })
}

const $saveBill = throttle(
  async (type: any, $form: any, $queryEngine: any, $message: any, $bus: any,) => {
    const values = $form.values
    if (type === 'SAVE') {
      $form.validate().then(() => {
        $submitData(type, values, $form, $queryEngine)
      })
    } else if (type === 'SUBMIT') {
      $form
        .validate()
        .then(() => {
          let tips = '提交审批后，供应商将进入清算状态，审批通过后，供应商进入正式黑名单，从提交审批后开始，该供应商无法进行任何业务操作（包括创建、编辑等）。'
          app.$confirm(tips, '是否确认提交', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            $submitData(type, values, $form, $queryEngine)
          })
        })
        .catch((err: any) => {
          console.log(err, 'err')
        })
    }
  },
  300,
)

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any) => {
  const form = toJS($values)
  delete form.configForm
  form.isAllowSourcing = 'N'
  form.isAllowCreateOrder = 'N'
  form.isAllowWarehousing = 'N'
  form.isAllowFinance = 'N'
  form.isAllowPayment = 'N'
  if ($attrs.params.flag === 'add') form.approveStatus = 'DRAFT'
  $queryEngine.request
    .baseRequest({
      type: 'Black',
      lang: 'zh-cn',
      loading: true,
      tree: true,
      payload: [form],
      query: { '*': {},blackCompanyList: {'*': {}}, fileUploads: {'*': {}} },
      action: 'save'
    })
    .then((res: any) => {
      console.log(res, 'res')
      if (res.data && res.data.length > 0) {
        const datas = res.data[0]
        app.$message.success($t('common.successSave'))
        if (type === 'SUBMIT') {
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
          componentInstance.setWorkflowBusinessId(datas.blackId || '')
          componentInstance.setWorkflowTabDisabled(false)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            emitTabRemove($attrs.params.tabName)
            bus.$emit('BlackList')
          })
          setTimeout(() => {
            $form.readPretty = true
            componentInstance.buttonConfigInfo.save.view = false
            componentInstance.buttonConfigInfo.submit.view = false
          }, 100)
        } else {
          $form.values.blackId = datas.blackId || ''
          $queryEngine.request.read()
          bus.$emit('BlackList')
        }
      }
    })
}
const $cancel = () => {
  // @ts-ignore
  emitTabRemove($attrs.tabName)
  bus.$emit('BlackList')
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton

    // componentInstance.buttonConfigInfo.save.code = 'XXXCC' // 按钮权限
    // componentInstance.buttonConfigInfo.submit.code = 'XXXCC' // 按钮权限
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton

    const disabled = !['SUBMITTED', 'REJECTED', 'WITHDRAW', 'ABANDONED', 'APPROVED'].includes($attrs?.params?.row.approveStatus)
    console.log($attrs?.params?.row?.blackId)
    componentInstance.setWorkflowBusinessId($attrs?.params?.row?.blackId?.blackId)
    componentInstance.setWorkflowTabDisabled(disabled)
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('state').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
  }, 50)
}

// @ts-ignore
const scope = {
  $attrs,
  app,
  emitTabRemove,
  $closePageAndRefreshListPageData,
  $vendor: vendor,
  $addCompanyOneItem,
  $clearFormField,
  $saveBill,
  updateButtonConfig,
  initButtonConfig,
  $addCompanyByImportExcel

}
// @ts-ignore
const components = {

  FileDynamic
}

// @ts-ignore
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      isSrmCompany: false,
      viewUpdateButton: true,
      orderStatus: 'DRAFT'
    }
  },
  // 基本信息
  Black: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            $form.readPretty = $attrs.params.flag === 'view' || ['SUBMITTED', 'REJECTED', 'WITHDRAW', 'ABANDONED', 'APPROVED'].includes($attrs?.params?.row.approveStatus)

            console.log($form.readPretty)
            console.log(typeof $form.readPretty)
            initButtonConfig($form)

            return $attrs.params.row.blackId
          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.blackId || $form.values.blackId || '']
            data.query['*'] = {}
            return data
          }`),
          onSuccess: expression(`(res) => {
            let detailData = res.data[0]
            $form.query('state').get('data').orderStatus = detailData.approveStatus

            $form.query('state').get('data').viewUpdateButton = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(detailData.approveStatus) || $attrs.params.flag === 'add'
            updateButtonConfig($form)

            $form.setValues({
              ...detailData
            })
            // 附件
            $form.query('fileUploads').take(field => {
              field.componentProps.componentInstance.reLoadFileInfo()
            })
          }`)
        },
        save: {
          transformRequest: expression(`(data, headers) => {
             data.query['*'] = {}
             return data
          }`),
          onSuccess: expression(`(res) => {

          }`),
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$form.values.blackId || null'),
          'business-type': 'black',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)
        },
        properties: {
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            'x-read-pretty': expression('$form.readPretty'),
            properties: generateXindexInOrder({
              baseInfo: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('vendorMod.receiptInfo')
                },
                'x-query-engine-skip': true,
                properties: {
                  layout: {
                    type: 'void',
                    ...formGridSegment,
                    properties: {
                      blackName: {
                        type: 'string',
                        title: i18nExpression('vendorMod.relegation.billName'),
                        'x-decorator': 'FormItem',
                        'x-validator': {
                          required: true,
                          message: i18nExpression('common.requiredField')
                        }
                      },
                      blackCode: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: "{{$t('black.blacklistApprovalNumber')}}",
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      createdBy: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: "{{$t('common.creator')}}",
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      creationDate: {
                        type: 'string',
                        'x-decorator': 'FormItem',
                        title: "{{$t('common.creationTime')}}",
                        'x-component-props': {
                          disabled: true
                        }
                      },
                      blackType: {
                        type: 'string',
                        title: "{{$t('black.blackType')}}",
                        'x-component': 'DictSelect',
                        'x-component-props': {
                          code: 'BLACK_TYPE',
                          disabled: expression('$form.readPretty')
                        },
                        'x-decorator': 'FormItem',
                        'x-validator': {
                          required: true,
                          message: i18nExpression('common.requiredField')
                        }
                      },
                      endDate: {
                        type: 'date',
                        title: "{{$t('black.blackTime')}}", // 黑名单截止日期
                        'x-decorator': 'FormItem'
                      },
                      blackDescription: {
                        type: 'string',
                        title: "{{$t('black.blackSketch')}}", // 供应商简述
                        'x-component-props': {
                          type: 'textarea',
                          autosize: { minRows: 2, maxRows: 4 },
                          maxlength: 80,
                          showWordLimit: true
                        },
                        'x-decorator-props': {
                          gridSpan: 2
                        },
                        'x-decorator': 'FormItem'
                      }
                    }
                  }
                }
              },

              blackDetail: {
                ...blackDetails
              },
              riskControl: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: i18nExpression('black.riskControl')
                },
                'x-query-engine-skip': true,
                "x-hidden": true,
                properties: {
                  layout: {
                    type: 'void',
                    ...formGridSegment,
                    properties: {
                      isImmediatelyBox: {
                        type: 'void',
                        title: i18nExpression('black.isImmediately'),
                        'x-decorator': 'FormItem',
                        'x-query-engine-skip': true,
                        properties: {
                          isImmediately: {
                            type: 'string',
                            default: 'N',
                            'x-component': 'Checkbox',
                            "x-component-props": {
                              label: i18nExpression('black.immediate'),
                              'true-label': 'Y',
                              'false-label': 'N'
                            }
                          },
                          excessiveTime: {
                            type: 'number',
                            default: 0,
                            'x-component': 'Radio.Group',
                            'x-reactions': expression(`field => {
                            field.visible = $values.isImmediately === 'N'
                          }`),
                            "x-component-props": {
                              style: {
                                'margin-left': '20px'
                              }
                            },
                            enum: [
                              {
                                label: i18nExpression('vendorMod.day30'),
                                value: 30
                              },
                              {
                                label: i18nExpression('vendorMod.day60'),
                                value: 60
                              }
                            ]
                          }
                        }
                      }
                    }
                  }
                }
              },
              fileInfo: {
                ...FileInfo
              }
            })
          }

        }
      }

    }
  },
  addDialog: {
    type: 'void',
    title: i18nExpression('black.impoblackCompanyAddrt'),
    'x-decorator': 'QueryEngine',
    'x-component': 'RDialog',
    'x-component-props': {
      'close-on-click-modal': false,
      destroyOnClose: true,
      size: 'large',
      footerButtonList: expression(`(_, { cancelButton,okButton }) => {
        return [
          cancelButton,
          okButton
        ]

        }`),
      beforeClose: expression(`(done, type,closeLoading) => {
          if ( type === 'ok') {
            $addCompanyOneItem($form,$values,done,closeLoading)
          } else {
            done()
            }
          }
        `)
    },
    properties: {
      configForm: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          vendorName: {
            type: 'string',
            title: "{{$t('common.vendorName')}}",
            'x-decorator': 'FormItem',
            'x-component': 'QuickSearchWrapper',
            'x-reactions': expression(`field => {
                 field.visible = $form.query('state').get('data').isSrmCompany
               }`),
            'x-component-props': {
              showKey: 'companyName',
              propKey: 'companyName',
              'name': 'scc_sup_company_info2',
              '@close-quicksearch': expression(`(val, scope) => {
                  $values.configForm.companyId = val ? val.companyId : null
                  $values.configForm.companyCode = val ? val.companyCode : ''
                  $values.configForm.companyName = val ? val.companyName : ''
                  $values.configForm.companyType = val ? val.companyType : ''
                  $values.configForm.socialCreditCode = val ? val.lcCode : ''
                  $values.configForm.legalPerson = val ? val.legalPerson : ''
                  $values.configForm.registeredCapital = val ? val.registeredCapital : ''
                  $values.configForm.companyCountry = val ? val.companyCountry : ''
                  $values.configForm.companyProvince = val ? val.companyProvince : ''
                  $values.configForm.companyCity = val ? val.companyCity : ''
                  $values.configForm.companyCreationDate = val ? val.companyCreationDate : ''
                }`)
            },
            ...requiredValidatorSegment
          },
          companyName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('common.vendorName')}}",
            'x-reactions': expression(`field => {
                field.visible = !$form.query('state').get('data').isSrmCompany
              }`),
            ...requiredValidatorSegment

          },
          socialCreditCode: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('vendorMod.socialCreditCode')}}",
            'x-component-props': {
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`
            },
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          companyCreationDate: {
            'x-decorator': 'FormItem',
            title: "{{$t('bidMod.companyCreationDate')}}",
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            },
            type: 'date',
            default: null,
            'x-component-props': {
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`,
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd'
            }
          },
          legalPerson: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('vendorMod.corporateRepresentative')}}",
            'x-component-props': {
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`
            },
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          registeredCapital: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('vendorMod.registeredCapital')}}",
            'x-component-props': {
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`
            },
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          companyCountry: {
            type: 'string',
            title: "{{$t('vendorMod.businessAddr')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`,
              '@change': expression(`() => {
                  if ($values.configForm?.companyCountry != 'CN') {
                    $values.configForm.companyProvince = null
                    $values.configForm.companyCity = null
                  }
                }`)
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          companyProvince: {
            type: 'string',
            title: "{{$t('vendorMod.province')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PROVINCE',
              'custom-select-type': "PROVINCE",
              disabled: expression(`$values.configForm?.companyCountry !== 'CN' || $form.query('state').get('data').isSrmCompany`)
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: `{{$values.configForm?.companyCountry === 'CN' && !$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          companyCity: {
            type: 'string',
            title: "{{$t('vendorMod.city')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$values.configForm?.companyProvince`),
              'custom-select-type': "CITY",
              disabled: expression(`$values.configForm?.companyCountry !== 'CN' || $form.query('state').get('data').isSrmCompany`)
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: `{{ $values.configForm?.companyCountry === 'CN' && !$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
            }
          },
          companyType: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: "{{$t('vendorMod.companyType')}}",
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'COMPANY_NATURE',
              disabled: `{{$form.query('state').get('data').isSrmCompany}}`
            },
            'x-validator': {
              required: `{{!$form.query('state').get('data').isSrmCompany}}`,
              message: i18nExpression('common.requiredField')
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
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
    schemaKey="BlackDetail"
  />
</template>
