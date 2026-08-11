<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  methodExpression,
  i18nExpression,
  observer,
  changeFieldVisibleByDeps,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { FormTab } from '@meicloud/render-pix'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import changeTitle from './components/changeTitle.vue'
// @ts-ignore
import { useAttrs, computed, ref } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { formMain } from './components/form'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'
import { tabs } from './components/tabs'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { vendorGreenApi } from 'modcb@/vendorManagementBuyer/api/vendorManagement'
import { transformMQL } from '@/library/utils/util'
import { validEmail, validatePhone } from '@/utils/validate'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
const { app, emitTabRemove, t, vendor, http } = usePageHelper()
import { createDictClass } from '@/library/utils/dict/dict-utils'
const attrs: any = useAttrs()
const workflowStatus = ref('DRAFT')

const redFunction = (oldData:any, newData:any) => {
  let className = ''
  if (oldData != newData) {
    className = 'redColorFont'
  }
  return className
}

const viewUpdateButton = ($form: any) => {
  let bol
  const changeStatus = attrs.params.row?.changeStatus || null
  if (changeStatus === 'APPROVED' ||
    changeStatus === 'SUBMITTED' ||
    changeStatus === 'ABANDONED') {
    bol = false
  } else if (attrs.params?.flag === 'view') {
    bol = false
  } else {
    bol = true
  }
  return bol
}

const initButtonConfig = ($form: any, $t: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const changeStatus = attrs.params.row?.changeStatus || null

    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form) || changeStatus == 'VENDOR_SUBMITTED'
    componentInstance.buttonConfigInfo.cancel.view = true && changeStatus != 'VENDOR_SUBMITTED'
    componentInstance.buttonConfigInfo.close.view = false

    const approveStatus = attrs.params.row?.changeStatus || null
    if ([null, 'DRAFT'].includes(approveStatus)) {
      componentInstance.buttonConfigInfo.save.name = $t('common.staging')
      componentInstance.buttonConfigInfo.submit.name = $t('common.submit')
    }
    componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId)
    componentInstance.setWorkflowTabDisabled(!['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED', 'VENDOR_SUBMITTED'].includes(attrs.params.row?.changeStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false

    componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId)
    componentInstance.setWorkflowTabDisabled(!['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes(attrs.params.row?.changeStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      beforeChangeJson: null,
      companyChangeId: null,
      userType: '',
      serciceCustomDelList: []
    }
  },
  InfoChange: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container infoChange',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          tree: true,
          ready: expression(`() => {
            initButtonConfig($form, t)
            const companyId = app.$store.getters.companyId
            fatchCompanyData(companyId, $form)
            return $attrs.params && $attrs.params?.changeId
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              "*":{},
              companyInfoChange: {'*': {}},
              contactInfoChanges: {'*': {}},
              siteInfoChanges: {'*': {}},
              fileuploadChanges: {'*': {}},
              operatingLogs: {'*': {}},
              npmCompanySizeChanges: { '*': {}},
              npmCateJournalChanges: { '*': {}, npmSerciceCustomChanges: { '*': {}} },
              managementAttachChanges: { '*': {}}
            }
            let req = {
              "filter": {
                  "changeId": {
                      eq: $attrs.params.changeId
                  }
              }
            }
            data.payload = req
            return data
          }`),
          transformResponse: expression(`(res) => {
            const ress = JSON.parse(res)
            const datas = ress.data.records[0]
            const isPerson = datas?.companyInfoChange?.overseasRelation === 'PERSONAL'
            const beforeChangeJson = JSON.parse(datas.beforeChangeJson)
            $form.query('state').get('data').beforeChangeJson = beforeChangeJson
            const {
              contactInfos,
              fileUploads,
              managementAttaches,
              cateJournalList,
              ...beforeCompanyInfo
            } = beforeChangeJson
            const {
              companyInfoChange,
              contactInfoChanges,
              siteInfoChanges,
              managementAttachChanges,
              fileuploadChanges,
              operatingLogs,
              npmCompanySizeChanges,
              npmCateJournalChanges,
              ...companyInfo
            } = datas
            const {
              totalAssets,
              currentAssets,
              fixedAssets,
              avgAnnualOutput,
              avgAnnualProfit,
              sunshineFileName,
              sunshineFileId
            } = companyInfoChange
            $form.query('form').take().value = companyInfo
            $form.query('state').get('data').userType = companyInfo.overseasRelation
            $form.query('state').get('data').companyChangeId = datas?.companyInfoChange?.companyChangeId
            setTimeout(() => {
              if ($attrs.params?.flag == 'view' || ['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes($attrs.params.row?.changeStatus)) {
                $form.readPretty = true
              }
              if (!isPerson) {
                $form.query('.companyTypeBefore').take().value = beforeCompanyInfo
                beforeCompanyInfo.ceeaBusinessModel = beforeCompanyInfo.ceeaBusinessModel ? beforeCompanyInfo.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoBefore').take().value = beforeCompanyInfo
                $form.query('.attachFileBefore').take().value = fileUploads || []
                $form.query('.companyTypeAfter').take().value = companyInfoChange
                $form.query('.enterpriseThreeCertificatesAfter').take().value = companyInfoChange
                companyInfoChange.ceeaBusinessModel = companyInfoChange.ceeaBusinessModel ? companyInfoChange.ceeaBusinessModel.split(',') : []
                $form.query('.companyBaseInfoAfter').take().value = companyInfoChange
              } else {
                const {
                  businessStartDate,
                  businessEndDate
                } = beforeCompanyInfo
                beforeCompanyInfo.validityPeriodOfCard = [businessStartDate, businessEndDate]
                $form.query('.personBaseInfoBefore').take().value = beforeCompanyInfo
                companyInfoChange.validityPeriodOfCard = [companyInfoChange.businessStartDate, companyInfoChange.businessEndDate]
                $form.query('.personBaseInfoAfter').take().value = companyInfoChange
              }
              // $form.query('.authInfoBefore').take().value = { sunshineFileName: beforeCompanyInfo.sunshineFileName, sunshineFileId: beforeCompanyInfo.sunshineFileId }
              // $form.query('.authInfoAfter').take().value = { sunshineFileName, sunshineFileId }
              $form.query('.enterpriseThreeCertificatesBefore').take().value = beforeCompanyInfo
              $form.query('.contactDataBefore').take().value = contactInfos || []
              $form.query('.contactInfoChanges').take().value = contactInfoChanges
              $form.query('.qualificationInfoBefore').take().value = managementAttaches || []
              $form.query('.qualificationInfoAfter').take().value = managementAttachChanges
              $form.query('attachFileAfter').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              $form.query('.attachFileBefore').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              $form.query('.attachFileAfter').take().value = fileuploadChanges
              $form.query('.operatingLogsData').take().value = operatingLogs
              const serviceRange = cateJournalList.map(item => {
                const {
                  npmSerciceCustoms,
                  ...form
                } = item
                return {
                  list: npmSerciceCustoms,
                  tableForm: form
                }
              })
              $form.query('.serviceRangeBefore').take().value = serviceRange || []
              $form.query('.serviceRangeAfter').take().value = npmCateJournalChanges.map(item => {
                const {
                  npmSerciceCustomChanges,
                  ...form
                } = item
                return { list: npmSerciceCustomChanges, tableForm: form}
              })
            })
            return ress
          }`)
        },
        vendorSubmit: {
          autoFormatResult: false,
          cascadeDeletion: true
        },
        saveTemporary: {
          autoFormatResult: false,
          cascadeDeletion: true
        },
        save: {
          cascadeDeletion: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.planConfirmId || null'),
          'business-type': 'SUPPLIERINFOCHANGE',
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
            if (integrationMode?.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            'x-component-props': {
              class: 'vendorInfoChange'
            },
            properties: {
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                properties: generateXindexInOrder({
                  // 供应商信息
                  vendorInfo: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('vendorMod.vendorInfo')
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      form: {
                        ...formMain
                      }
                    }
                  }
                })
              },
              layout: {
                type: 'void',
                'x-component': 'FormContainer',
                properties: {
                  tabs: {
                    type: 'void',
                    'x-component': 'FormTab',
                    'x-component-props': {
                      type: 'card',
                      class: 'changeTab',
                      activeKey: generateCharExpressionByFunction(({ $form }) => {
                        return $form.query('state').get('data').userType !== 'PERSONAL' ? 'tab1' : 'tab11'
                      })
                    },
                    properties: {
                      ...tabs
                    }
                  }
                }
              },
              collapse2: {
                type: 'void',
                'x-component': 'Collapse',
                properties: generateXindexInOrder({
                  // 供应商信息
                  operatingLogs: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('common.operationRecord')
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      operatingLogsData: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          editMode: false,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false
                        },
                        'x-query-engine-skip': true,
                        properties: generateXindexInOrder({
                          creationDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`)
                            },
                            title: i18nExpression('common.operationTime'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          operation: {
                            type: 'string',
                            title: i18nExpression('contractMod.operationType'),
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              code: 'OPERATING_TYPE'
                            },
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          reason: {
                            type: 'string',
                            title: i18nExpression('common.explanationOfReasons'),
                            'x-render-table-column': {
                              minWidth: 120
                            }
                          },
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('common.operator'),
                            'x-render-table-column': {
                              minWidth: 120
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
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('ModelHead')
}

const fatchCompanyData = async (companyId:number, $form:any) => {
  if (companyId) {
    const payload = {
      'filter': {
        'companyId': {
          'eq': companyId
        }
      }
    }
    const query = {
      '*': {},
      contactInfos: { '*': {} },
      fileUploads: { '*': {} },
      managementAttaches: { '*': {} },
      operatingLogs: { '*': {} },
      cateJournalList: { '*': {}, npmSerciceCustoms: { '*': {} } }
    }
    const transformParams = transformMQL.save('CompanyInfo', payload, 'query', query)
    const response = await vendorGreenApi.getCompanyInfo(transformParams)
    if (response && response.data && response.data.records.length) {
      const {
        contactInfos,
        fileUploads,
        managementAttaches,
        cateJournalList,
        operatingLogs,
        ...rest
      } = response.data.records[0]
      const {
        companyId,
        companyName,
        companyCode,
        totalAssets,
        currentAssets,
        fixedAssets,
        avgAnnualOutput,
        avgAnnualProfit,
        sunshineFileName,
        sunshineFileId
      } = rest
      let stateData = $form.query('state').get('data')
      stateData.userType = rest.overseasRelation
      const isPerson = rest.overseasRelation === 'PERSONAL'
      stateData.beforeChangeJson = JSON.stringify(response.data.records[0])
      $form.query('form').take().value = { companyId, companyName, companyCode }
      
      if (fileUploads) {
        for (let i = 0; i < fileUploads.length; i++) {
          const fileItem = fileUploads[i]
          fileItem.originalBusinessId = fileItem.businessId
          fileItem.sceneFileId = null
          fileItem.businessId = null
          fileItem['__edit_key__'] = true
          fileItem['__add_key__'] = true
          fileItem['__update_key__'] = true
        }
        $form.query('.attachFileBefore').take().value = fileUploads
        $form.query('attachFileBefore').take(field => {
          field.componentProps.componentInstance.reLoadFileInfo()
        })
        $form.query('.attachFileAfter').take().value = JSON.parse(JSON.stringify(fileUploads))
      }
      if (rest) {
        if (!isPerson) {
          $form.query('.companyTypeAfter').take().value = JSON.parse(JSON.stringify(rest))
          $form.query('.companyTypeBefore').take().value = rest
          $form.query('.enterpriseThreeCertificatesAfter').take().value = JSON.parse(JSON.stringify(rest))
          $form.query('.enterpriseThreeCertificatesBefore').take().value = rest
        }
        rest.ceeaBusinessModel = rest.ceeaBusinessModel ? rest.ceeaBusinessModel.split(',') : []
        $form.query('.companyBaseInfoAfter').take().value = JSON.parse(JSON.stringify(rest))
        $form.query('.companyBaseInfoBefore').take().value = rest
      }
      if (isPerson) {
        const {
          businessStartDate,
          businessEndDate
        } = rest
        rest.validityPeriodOfCard = [businessStartDate, businessEndDate]
        setTimeout(() => {
          $form.query('.personBaseInfoBefore').take().value = rest
          $form.query('.personBaseInfoAfter').take().value = JSON.parse(JSON.stringify(rest))
        })
      }
      // const authInfo = { sunshineFileName, sunshineFileId }
      // $form.query('authInfoBefore').take().value = authInfo
      // $form.query('authInfoAfter').take().value = JSON.parse(JSON.stringify(authInfo))
      $form.query('.qualificationInfoBefore').take().value = managementAttaches || []
      $form.query('.qualificationInfoAfter').take().value = JSON.parse(JSON.stringify(managementAttaches)) || []
      $form.query('.contactDataBefore').take().value = contactInfos || []
      $form.query('.contactInfoChanges').take().value = JSON.parse(JSON.stringify(contactInfos)) || []
      const serviceRange = cateJournalList.map(item => {
        const {
          npmSerciceCustoms,
          formId,
          ...form
        } = item
        return {
          list: npmSerciceCustoms,
          tableForm: { formId: null, ...form }
        }
      })
      $form.query('.serviceRangeBefore').take().value = serviceRange || []
      $form.query('.serviceRangeAfter').take().value = JSON.parse(JSON.stringify(serviceRange)) || []
    }
  }
}

const $submits = async (type, $form, $queryEngine, $message, $t, $bus) => {
  let values = $form.values
  // if (type !== 'savePublish') {
  //   $form.validate()
  // }
  const isPerson = $form.query('state').get('data').userType === 'PERSONAL'
  const personFormAfter = (isPerson && $form.query('.personBaseInfoAfter').get('value')) || {}
  if (personFormAfter?.validityPeriodOfCard?.length) {
    const [businessStartDate, businessEndDate] = personFormAfter.validityPeriodOfCard
    personFormAfter.businessStartDate = businessStartDate
    personFormAfter.businessEndDate = businessEndDate
  }
  const companyTypeAfter = $form.query('.companyTypeAfter').get('value')
  // const authInfoAfter = $form.query('.authInfoAfter').get('value')
  const enterpriseThreeCertificatesAfter = $form.query('.enterpriseThreeCertificatesAfter').get('value')
  const serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList || []
  let serviceRange = $form.query('.serviceRangeAfter').get('value').map(item => {
    const {
      list,
      tableForm
    } = item
    return {
      ...tableForm,
      npmSerciceCustomChanges: [...list, ...serciceCustomDelList]
    }
  })
  const managementAttachChanges = $form.query('.qualificationInfoAfter').get('value')
  let userType = values.form?.userType
  if (values.form?.userType == undefined || values.form?.userType == null) {
    userType = app.$store.getters.userType
  }
  let allData = {
    beforeChangeJson: $form.query('state').get('data').beforeChangeJson,
    ...values.form,
    userType,
    companyInfoChange: Object.assign({ companyChangeId: $form.query('state').get('data')?.companyChangeId }, companyTypeAfter, enterpriseThreeCertificatesAfter, personFormAfter),
    contactInfoChanges: $form.query('.contactInfoChanges').get('value'),
    fileuploadChanges: $form.query('.attachFileAfter').get('value'),
    npmCateJournalChanges: serviceRange,
    managementAttachChanges
  }
  // 处理再次选择供应商的时候
  if (allData.changeId) {
    allData.companyInfoChange.changeId = allData.changeId
    allData.contactInfoChanges || [].forEach((e) => {
      e.changeId = allData.changeId
    })
    allData.managementAttachChanges || [].forEach((e) => {
      e.changeId = allData.changeId
    })
    allData.fileuploadChanges || [].forEach((e) => {
      e.businessId = allData.changeId
    })
    allData.siteInfoChanges || [].forEach((e) => {
      e.changeId = allData.changeId
    })
  }
  // 校验
  // if (!allData.enable4mChange) {
  //   return app.$message.error($t('vendorMod.msgSelect4M')) // 请选择是否是4M变更
  // }
  if (!isPerson && (allData.companyInfoChange.businessLicenseFileId == '' || allData.companyInfoChange.businessLicenseFileId == null)) {
    // 请输入三证信息的营业执照
    app.$message.warning($t('cusEntry.supplement20250314.businessLicenseInfoMsg'))
    return false
  }
  if (!isPerson && (allData.companyInfoChange.registCurrency == '' || allData.companyInfoChange.registCurrency == null)) {
    // 请输入三证信息的币种
    app.$message.warning($t('vendorMod.enterCurrency'))
    return false
  }
  allData.companyInfoChange.ceeaBusinessModel = allData.companyInfoChange.ceeaBusinessModel.length ? allData.companyInfoChange.ceeaBusinessModel.join() : ''
  
  const changeStatus = attrs.params.row?.changeStatus || null
  if (type == 'SAVE') { // 暂存的时候
    if ([null, 'DRAFT'].includes(changeStatus)) { // 新增或者编辑的时候
      allData.changeStatus = 'DRAFT'
      $queryEngine.request.save(allData, { customizeAction: 'saveTemporary', query: { '*': {} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('vendorInfoChange')
        emitTabRemove(attrs.tabName)
      }).catch(() => {
        allData.companyInfoChange.ceeaBusinessModel = allData.companyInfoChange.ceeaBusinessModel ? allData.companyInfoChange.ceeaBusinessModel.split(',') : []
      })
    } else { // 供应商确认后的暂存
      $queryEngine.request.save(allData, { customizeAction: 'saveTemporary', query: { '*': {} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('vendorInfoChange')
        emitTabRemove(attrs.tabName)
      }).catch(() => {
        allData.companyInfoChange.ceeaBusinessModel = allData.companyInfoChange.ceeaBusinessModel ? allData.companyInfoChange.ceeaBusinessModel.split(',') : []
      })
    }
  } else { // 提交
    let validate = 0
    await $form.validate().then().catch(eq => {
      app.$message.error(eq[0].messages[0])
      validate = 1
    })
    if (validate) {
      return false
    }
    if ($form.query('state').get('data').userType !== 'OUT') {
      /** 校验联系人中默认联系人有且只有一个 */
      let ceeaDefaultList = allData.contactInfoChanges.filter(item => item.ceeaDefaultContact === 'Y')
      if (!ceeaDefaultList.length || ceeaDefaultList.length > 1) {
        app.$message.warning($t('cusEntry.vendorMod.defaultContactTips'))
        return
      }
    }
    // 校验服务范围品类不能重复
    const categoryIdList = new Set(serviceRange.map(item => item.categoryId))
    if (serviceRange.length !== categoryIdList.size) {
      let nameRecords = []
      for (let id of categoryIdList) {
        const record = serviceRange.filter(item => item.categoryId === id)
        if (record.length > 1) {
          nameRecords.push(record[0].categoryName)
        }
      }
      $message.warning($t('cusEntry.tipMessage.serviceRangeCategoryRepeat', { name: nameRecords.join(';') }))
      return false
    }
    // 校验资质信息
    let valid = allData.managementAttachChanges.findIndex((item: any) => item.extIsMandatory === 'Y' && !item.fileuploadId)
    if (valid > -1) {
      // 资质信息第index行请上传附件！
      $message.warning($t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: valid + 1, message: $t('bidMod.pleaseUploadFile')}))
      return
    }
    let bol = false
    let bolMsg = ''
    allData.managementAttachChanges.some((item: any, index: any) => {
      if (item.extIfEndDateRequired === 'Y' && !item.endDate) {
        bol = true
        // 资质信息第index行 有效截止时间必填
        bolMsg = $t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: index + 1, message: $t('cusEntry.vendorMod.endTime') + $t('contract_mod.required')})
        return true
      }
    })
    if (bol) {
      $message.warning(bolMsg)
      return
    }

    $queryEngine.request.save(allData, { customizeAction: 'vendorSubmit', query: { '*': {} } }).then(async (res) => {
      $message.success($t('common.successSave'))
      $bus.$emit('vendorInfoChange')
      emitTabRemove(attrs.tabName)
      const {
        companyId,
        companyName,
        companyCode
      } = allData.companyInfoChange
      await $monitorIpAddress({
        supplierId: companyId,
        supplierCode: companyCode,
        supplierName: companyName,
        source: $source.get('supplierChangeSubmit')
      })
    }).catch(() => {
      allData.companyInfoChange.ceeaBusinessModel = allData.companyInfoChange.ceeaBusinessModel ? allData.companyInfoChange.ceeaBusinessModel.split(',') : []
    })
  }
}
/* ip地址监控 */
const $monitorIpAddress = data => {
  return http({
    url: '/api-sou/bids/ip/address/ipAddress/save',
    method: 'POST',
    data
  })
}
/* 来源 */
 // 变更提交
  // 变更撤回
const $source = new Map([
  ['supplierChangeSubmit', t('cusEntry.vendorMod.submitChange')],
  ['supplierChangeUndo', t('cusEntry.vendorMod.withdrawChange')]
])
const $showSunFile = ($self) => {
  setTimeout(() => {
    const newData = $self.query('.sunshineFileName').get('value')?.split(',') || []
    const oldData = $self.query($self.parent.parent.parent.address.concat('beforeChange.authInfoBefore.sunshineFileName')).get('value')?.split(',') || []
    let className = ''
    const diff = new Set([...newData, ...oldData])
    if (diff.size !== oldData.length || newData.length !== oldData.length) {
      className = 'redColorFont'
    }
    const fileList = []
    if ($self.value) {
      const fileIdList = $self.value?.split(',')
      const fileNameList = $self.query('.sunshineFileName').get('value')?.split(',')
      fileIdList.forEach((item, index) => {
        fileList.push({
          fileId: item,
          fileName: fileNameList?.[index]
        })
      })
    }
    $self.setComponentProps({
      fileList,
      class: className
    })
  })
}
const $showBeforeSunFile = ($self) => {
  const fileList = []
  if ($self.value) {
    const fileIdList = $self.value?.split(',')
    const fileNameList = $self.query('.sunshineFileName').get('value')?.split(',')
    fileIdList.forEach((item, index) => {
      fileList.push({
        fileId: item,
        fileName: fileNameList?.[index]
      })
    })
  }
  $self.setComponentProps({
    fileList
  })
}
const scope = {
  app,
  t,
  $attrs: attrs,
  emitTabRemove,
  initButtonConfig,
  $back,
  supCommonApi,
  $submits,
  observer,
  DictSelect,
  fatchCompanyData,
  redFunction,
  validEmail,
  validatePhone,
  sceneFileApi,
  $showSunFile,
  $showBeforeSunFile,
  $taxDictClass: createDictClass({ 'country': [] })
}

const components = {
  FormTab,
  changeTitle,
  FileDynamic
}
</script>

<template>
  <RenderEngine :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>

<style lang="scss">
.authInfo-layout {
  display: flex;
  .authInfo-form-layout {
    padding-left: 15px !important;
  }
}
.redColorFont {
  color: red !important;
}
.vendorInfoChange .redColorFont input{
  color: red !important;
}
.vendorInfoChange .redColorFont .el-tag.el-tag--info{
  color: red !important;
}
.vendorInfoChange .redColorFont .is-checked .el-radio__label {
  color: red !important;
}
.vendorInfoChange .redColorFont .el-textarea__inner{
  color: red !important;
}
.vendorInfoChange .redColorFont .file-item-name {
  color: red !important;
}
.vendorInfoChange .formClassAllChange{
  display: block;
  width: 50%;
  padding: 0px 10px 10px 0;
}
.vendorInfoChange .formClassAllChange .forms{
  padding-left: 18px;
  width: 320px;
}
.vendorInfoChange .formClassAllChange .forms .render-pix-form-item-label-content{
  line-height: 1 !important;
  min-height: 13px !important;
}
.vendorInfoChange .formClassAllChange .forms .render-pix-form-item-asterisk{
  display: inline-block;
  vertical-align: top;
  line-height: 22px;
  margin-top: 5px;
}
.vendorInfoChange .changeTab{
  margin-top: 18px;
}
.vendorInfoChange .formClassWrap{
  display: flex;
}
.vendorInfoChange .el-input-group__append {
  min-width: 100px;
  padding: 0;
  .el-select {
    width: 100%;
    height: 26px !important;
    display: block;
    margin: 0 !important;
    border-radius: 0 4px 4px 0;
    .el-input {
      height: 100% !important;
      .el-input__inner {
        height: 100% !important;
        border: none;
        padding: 0 28px 0 8px;
        border-radius: 0 4px 4px 0;
        background: #fff;
      }
    }
  }
}
.bzBox{
  position: relative;
}
.bzTitle{
  position:absolute;
  top:-20px;
  left:4px;
}
.bzTitle::before{
  content:'*';
  margin-right:4px;
  display: inline-block;
  vertical-align: top;
  color: #FF4A4D;
  font-family: SimSun, sans-serif;
}
</style>
