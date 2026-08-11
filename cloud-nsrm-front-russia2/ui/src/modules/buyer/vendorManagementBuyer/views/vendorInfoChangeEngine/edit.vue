<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  methodExpression,
  i18nExpression,
  observer, changeFieldVisibleByDeps
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
import { FormTab } from '@meicloud/render-pix'
import changeTitle from  './components/changeTitle.vue'
// @ts-ignore
import { useAttrs, computed, ref } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { formMain } from './components/form'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'
import { tabs } from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/tabs";
import DictSelect from "lib@/components/c-select/dict-select.vue";
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { vendorOptCommonApi } from 'mod@/common/userManage/api'

const { app, emitTabRemove, t, vendor } = usePageHelper()

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

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const changeStatus = attrs.params.row?.changeStatus || null
    // debugger
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form) || changeStatus == 'VENDOR_SUBMITTED'
    componentInstance.buttonConfigInfo.cancel.view = true && changeStatus != 'VENDOR_SUBMITTED'
    componentInstance.buttonConfigInfo.close.view = false

    const approveStatus = attrs.params.row?.changeStatus || null
    if ([null, 'DRAFT'].includes(approveStatus)) {
      componentInstance.buttonConfigInfo.save.name = '暂存'
      componentInstance.buttonConfigInfo.submit.name = '提交'
    }
    componentInstance.setWorkflowBusinessId(attrs.params.row?.changeId)
    componentInstance.setWorkflowTabDisabled(!['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED', 'VENDOR_SUBMITTED'].includes(attrs.params.row?.changeStatus))
    componentInstance.setWorkflowBusinessVariables({})
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
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
      companyChangeId: null
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
            initButtonConfig($form)
            return $attrs.params && $attrs.params?.changeId
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.query = {
              "*":{},
              companyInfoChange: {'*': {}},
              contactInfoChanges: {'*': {}},
              bankInfoChanges: {'*': {}},
              siteInfoChanges: {'*': {}},
              financeInfoChanges: {'*': {}},
              managementAttachChanges: {'*': {}},
              fileuploadChanges: {'*': {}},
              operatingLogs: {'*': {}}
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

            setTimeout(() => {
              $form.query('.bankInfoAfter').take().value = datas.bankInfoChanges
              $form.query('.contactDataAfter').take().value = datas.contactInfoChanges
              $form.query('.financeInfoChanges').take().value = datas.financeInfoChanges
              $form.query('.siteInfosAfter').take().value = datas.siteInfoChanges
              $form.query('.sceneAttachmentInfoAfter').take().value = datas.managementAttachChanges
              $form.query('.attachFileAfter').take().value = datas.fileuploadChanges
              $form.query('.operatingLogsData').take().value = datas.operatingLogs

              $form.query('state').get('data').companyChangeId = datas?.companyInfoChange?.companyChangeId

              $form.query('.attachFileAfter').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              $form.query('.attachFileBefore').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })

              let datasForm = JSON.parse(JSON.stringify(datas))
              delete datasForm.bankInfoChanges
              delete datasForm.companyInfoChange
              delete datasForm.contactInfoChanges
              delete datasForm.financeInfoChanges
              delete datasForm.siteInfoChanges
              delete datasForm.managementAttachChanges
              delete datasForm.fileuploadChanges
              delete datasForm.beforeChangeJson
              $form.query('form').take().value = datasForm
              if ($attrs.params?.flag == 'view' || ['APPROVED', 'SUBMITTED', 'REJECTED', 'ABANDONED'].includes($attrs.params.row?.changeStatus)) {
                $form.readPretty = true
              }
              $form.query('state').get('data').beforeChangeJson = datas.beforeChangeJson

              const beforeChangeJson = JSON.parse(datas.beforeChangeJson)

              if (beforeChangeJson.companyInfo) {
                let companyInfo = beforeChangeJson.companyInfo
                $form.query('.companyTypeBefore').take().value = companyInfo

                $form.query('.enterpriseThreeCertificatesBefore').take().value = companyInfo

                $form.query('.companyBaseInfoBefore').take().value = companyInfo
              }

              $form.query('.siteInfosBefore').take().value = beforeChangeJson.siteInfos || []

              $form.query('.bankInfoBefore').take().value = beforeChangeJson.bankInfos || []

              $form.query('.sceneAttachmentInfoBefore').take().value = beforeChangeJson.managementAttaches || []

              $form.query('.contactDataBefore').take().value = beforeChangeJson.contactInfos || []

              $form.query('.financeInfoBefore').take().value = beforeChangeJson.financeInfos || []

                $form.query('.companyTypeAfter').take().value = datas?.companyInfoChange
                $form.query('.enterpriseThreeCertificatesAfter').take().value = datas.companyInfoChange
                $form.query('.companyBaseInfoAfter').take().value = datas.companyInfoChange

              })

            return ress
          }`)
        },
        submit: {
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
        items: {
          type: 'object',
          'x-query-engine-skip': true,
          properties: {
            buyerReject: {
              type: 'void',
              'x-content': i18nExpression('common.toRefuse'),
              'x-component': 'Button',
              'x-component-props': {
                type: 'default',
                '@click': expression(`() => {
                   app.$prompt('', '驳回原因', {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      inputValidator: value => !(!value || value.length > 500),
                      inputErrorMessage: '驳回原因必填并且长度不能超过500字符！'
                    }).then(({ value }) => {
                      app.$http({
                        url: '/api-sup/change/infoChange/buyerReject',
                        method: 'POST',
                        data: {
                          changeId: $attrs.params.changeId,
                          flowRemark: value
                        },
                        loading: true
                      }).then(() => {
                        $message.success($t('common.success'))
                        $bus.$emit('vendorInfoChange')
                        emitTabRemove($attrs.tabName)
                      })
                    })
                }`)
              },
              'x-reactions': changeFieldVisibleByDeps(['form.changeStatus'], `
                  $deps[0] == 'VENDOR_SUBMITTED'
              `)
            }
          }
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
                      activeKey: 'tab1'
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

const fatchCompanyData = ( companyId:number, $form:any ) => {
  if (companyId) {
    vendorOptCommonApi.getCompanyForEdit({ companyId }).then((res) => {
      if (res) {
        $form.query('state').get('data').beforeChangeJson = JSON.stringify(res.data)
        if (res.data.infoChange) {
          $form.query('.form.noticeByName').take().value = res.data.infoChange.noticeByName
          $form.query('.form.noticeById').take().value = res.data.infoChange.noticeById
        }
        if (res.data.fileUploads) {
          for (let i = 0; i < res.data.fileUploads.length; i++) {
            const fileItem = res.data.fileUploads[i]
            fileItem.originalBusinessId = fileItem.businessId
            fileItem.sceneFileId = null
            fileItem.businessId = null
            fileItem['__edit_key__'] = true
            fileItem['__add_key__'] = true
            fileItem['__update_key__'] = true
          }
          console.log(res.data.fileUploads, 'fileUploads')
          $form.query('.attachFileBefore').take().value = res.data.fileUploads
          $form.query('.attachFileAfter').take().value = JSON.parse(JSON.stringify(res.data.fileUploads))
        }
        // this.$nextTick(() => {
        //   this.$refs.sceneAttachment.loadFileInfo()
        //   this.$refs.sceneAttachment2.loadFileInfo()
        // })
        if (res.data.companyInfo) {
          let companyInfo = res.data.companyInfo
          $form.query('.companyTypeAfter').take().value = companyInfo
          $form.query('.companyTypeBefore').take().value = JSON.parse(JSON.stringify(companyInfo))

          $form.query('.enterpriseThreeCertificatesAfter').take().value = companyInfo
          $form.query('.enterpriseThreeCertificatesBefore').take().value = JSON.parse(JSON.stringify(companyInfo))

          $form.query('.companyBaseInfoAfter').take().value = companyInfo
          $form.query('.companyBaseInfoBefore').take().value = JSON.parse(JSON.stringify(companyInfo))
        }

        $form.query('.siteInfosBefore').take().value = res.data.siteInfos || []
        $form.query('.siteInfosAfter').take().value = JSON.parse(JSON.stringify(res.data.siteInfos)) || []

        $form.query('.bankInfoBefore').take().value = res.data.bankInfos || []
        $form.query('.bankInfoAfter').take().value = JSON.parse(JSON.stringify(res.data.bankInfos)) || []

        $form.query('.sceneAttachmentInfoBefore').take().value = res.data.managementAttaches || []
        $form.query('.sceneAttachmentInfoAfter').take().value = JSON.parse(JSON.stringify(res.data.managementAttaches)) || []

        $form.query('.contactDataBefore').take().value = res.data.contactInfos || []
        $form.query('.contactDataAfter').take().value = JSON.parse(JSON.stringify(res.data.contactInfos)) || []

        $form.query('.financeInfoBefore').take().value = res.data.financeInfos || []
        $form.query('.financeInfoChanges').take().value = JSON.parse(JSON.stringify(res.data.financeInfos)) || []
      }
    })
  }
}

const $submits = (type, $form, $queryEngine, $message, $t, $bus) => {
  let values = $form.values
  // if (type !== 'savePublish') {
  //   $form.validate()
  // }
  const companyTypeAfter = $form.query('.companyTypeAfter').get('value')
  const enterpriseThreeCertificatesAfter = $form.query('.enterpriseThreeCertificatesAfter').get('value')
  let userType = values.form?.userType
  if (values.form?.userType == undefined || values.form?.userType == null) {
    userType = app.$store.getters.userType
  }
  let allData = {
    beforeChangeJson: $form.query('state').get('data').beforeChangeJson,
    ...values.form,
    userType,
    companyInfoChange: Object.assign({companyChangeId:$form.query('state').get('data')?.companyChangeId},companyTypeAfter ,enterpriseThreeCertificatesAfter),
    contactInfoChanges: $form.query('.contactDataAfter').get('value'),
    bankInfoChanges: $form.query('.bankInfoAfter').get('value'),
    siteInfoChanges: $form.query('.siteInfosAfter').get('value'),
    financeInfoChanges: $form.query('.financeInfoChanges').get('value'),
    managementAttachChanges: $form.query('.sceneAttachmentInfoAfter').get('value'),
    fileuploadChanges: $form.query('.attachFileAfter').get('value')
  }
  console.log(allData, 'allData')
  // 处理再次选择供应商的时候
  if (allData.changeId) {
    allData.companyInfoChange.changeId = allData.changeId
    allData.contactInfoChanges?.forEach((e) => {
      e.changeId = allData.changeId
    })
    allData.fileuploadChanges?.forEach((e) => {
      e.businessId = allData.changeId
    })
    allData.siteInfoChanges?.forEach((e) => {
      e.changeId = allData.changeId
    })
    allData.bankInfoChanges?.forEach((e) => {
      e.changeId = allData.changeId
    })
    allData.financeInfoChanges?.forEach((e) => {
      e.changeId = allData.changeId
    })
  }
  // 校验
  if (!allData.enable4mChange) {
    return app.$message.error($t('vendorMod.msgSelect4M')) // 请选择是否是4M变更
  }

  if (allData.companyInfoChange.registCurrency == '' || allData.companyInfoChange.registCurrency == null) {
    app.$message.error('请输入三证信息的币种')
    return false
  }

  let checkSiteInfoChangesOrgid = false
  let checkSiteInfoChangesvendorSiteCode = false
  let checkSiteInfoChangescountry = false
  let checkSiteInfoChangesaddressDetail = false
  if (allData.siteInfoChanges) {
    allData.siteInfoChanges.forEach((item) => {
      if (!item.orgId) {
        checkSiteInfoChangesOrgid = true
      }
      if (!item.vendorSiteCode) {
        checkSiteInfoChangesvendorSiteCode = true
      }
      if (!item.country) {
        checkSiteInfoChangescountry = true
      }
      if (!item.addressDetail) {
        checkSiteInfoChangesaddressDetail = true
      }
    })
  }
  if (checkSiteInfoChangesOrgid) {
    app.$message({
      type: 'warning',
      message: $t('vendorMod.msgWriteVOrg')
    }) // 请填写供应商地点的业务实体
    return false
  } else if (checkSiteInfoChangesvendorSiteCode) {
    app.$message({
      type: 'warning',
      message: $t('vendorMod.msgWriteVAddressName')
    }) // 请填写供应商地点的地点名称
    return false
  } else if (checkSiteInfoChangescountry) {
    app.$message({
      type: 'warning',
      message: $t('vendorMod.msgWriteVCountry')
    }) // 请填写供应商地点的国家
    return false
  } else if (checkSiteInfoChangesaddressDetail) {
    app.$message({
      type: 'warning',
      message: $t('vendorMod.msgWriteVAddressDetail')
    }) // 请填写供应商地点的详细地址
    return false
  }

  const changeStatus = attrs.params.row?.changeStatus || null
  if (type == 'SAVE') { // 暂存的时候
    if ([null, 'DRAFT'].includes(changeStatus)) { // 新增或者编辑的时候
      allData.changeStatus = 'DRAFT'
      $queryEngine.request.save(allData, { customizeAction: 'saveTemporary', query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('vendorInfoChange')
        emitTabRemove(attrs.tabName)
      })
    } else { // 供应商确认后的暂存
      $queryEngine.request.save(allData, { customizeAction: 'saveTemporary', query: { '*':{} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('vendorInfoChange')
        emitTabRemove(attrs.tabName)
      })
    }
  } else { // 提交
    if ([null, 'DRAFT', 'SUBMITTED'].includes(changeStatus)) { // 新增或者编辑的时候
      allData.changeStatus = 'DRAFT'
    }
    if (changeStatus == 'VENDOR_SUBMITTED') {
      const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
      componentInstance.setWorkflowBusinessId(allData.changeId || null)
      componentInstance.setWorkflowTabDisabled(true)
      componentInstance.setWorkflowBusinessVariables({})
      componentInstance.handlerAfter(type.toUpperCase(), () => {
        $bus.$emit('vendorInfoChange')
      })
    } else {
      $queryEngine.request.save(allData, { customizeAction: 'submit', query: { '*':{} } }).then((res) => {
        console.log(res, 'res')
        const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
        componentInstance.setWorkflowBusinessId(res.records[0] || null)
        componentInstance.setWorkflowTabDisabled(true)
        componentInstance.setWorkflowBusinessVariables({})
        componentInstance.handlerAfter(type.toUpperCase(), () => {
          $bus.$emit('vendorInfoChange')
        })
      })
    }
  }
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
  redFunction
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
.vendorInfoChange .redColorFont input{
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

</style>
