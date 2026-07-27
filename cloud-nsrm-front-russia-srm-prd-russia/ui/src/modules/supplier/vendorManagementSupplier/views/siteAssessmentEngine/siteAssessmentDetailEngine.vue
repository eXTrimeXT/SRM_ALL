<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  generateXindexInOrder,
  i18nExpression,
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
import vendorAccessAttachment from 'modb@//vendorManagementBuyer/components/vendorAccessAttachment'
// @ts-ignore
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'
import { Steps } from './components/steps'
import { collapseMain } from './components/collapse'
import { siteReviewModel, quaApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import CFillProgress from 'lib@/components/c-fill-progress'
import Printer from 'modb@//vendorManagementBuyer/views/siteReviewModel/printer'

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabledFlag = computed(() => {
  return true
})
const customUpdateButton = computed(() => (!$disabledFlag.value && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = ($form: any) => {
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  if (componentInstance.workflowParamsInfo.integrationMode !== 'None') {
    return attrs.params.flag != 'view'
  } else {
    if (attrs.params.flag != 'view') {
      return true
    } else if (attrs.params.flag == 'view' && $form.query('.approveStatus').take().value == 'PUBLISH') {
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

const $disabledAdd = ( $form ) => {
  const states = $form.query('state').get('data')
  return states.addressReadOnly || states.bolType == 4 || ['PUBLISH', 'SUBMITTED', 'APPROVED'].includes(states.orderStatus)
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton($form)
    componentInstance.buttonConfigInfo.cancel.view = true
    componentInstance.buttonConfigInfo.close.view = false

    const reviewResult = $form.query('.reviewResult').take().value
    if (!reviewResult || reviewResult == '') {
      componentInstance.buttonConfigInfo.save.name = '暂存'
      componentInstance.buttonConfigInfo.submit.name = '提交'
    }
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
  }, 50)
}

const schema = defineSchemas({
  // 响应状态，不参与实际业务, 可以理解为 vue 里边的 data
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      bolType: 1,
      disVendorName: false,
      reviewForm: {
        // 资质审查单入参
        't.vendor_id': '',
        't.approve_status': 'APPROVED',
        't.CEEA_IF_VENDOR_AUTH': 'Y'
      },
      addressReadOnly: false,
      modelList:[], // 评审模板列表
      editableTabs:[], // 评审模板
      tableData:[], // 评审模板table时数据
      fileRecordId:0 // 点击了哪一行的评审模板
    }
  },
  SiteFormVendor: {
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
        query: {
          immediate: true,
          ready: expression(`() => {
            initButtonConfig($form)
            return $attrs.params.flag != 'add' && $attrs.params.row.siteFormId
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            if (data.typeGet == "history") {
              delete data.typeGet
              return data
            }
            data.query = {
              "*":{},
              "siteFormPersonList": {'*': {}},
              "siteFormAddressList": {'*': {}},
              "fileRecords": {
                '*': {},
                modelResultList: {'*': {}}
              },
              "fileList": {'*': {}},
              "orgCateJournals": {'*': {}},
            }
            data.payload = {
              "filter": {
                  "siteFormId": {
                      eq: $attrs.params.row.siteFormId
                  }
              }
            }
            return data
          }`),
          transformResponse: expression(`(res) => {
            const ress = JSON.parse(res)

            if (ress.data.records.length > 1) {
              return ress
            }

            $form.readPretty = true

            let datas = ress.data.ref.SiteFormVendor[$attrs.params.row.siteFormId]
            // 流程按钮是否置灰判断
            const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
            const tabDisabled = !(datas.reviewResult && datas.approveStatus != 'DRAFT')
            componentInstance.setWorkflowTabDisabled(tabDisabled)

            let tablePersons = []
            let tableFileRecords = []
            let tableOrgCateJournals = []
            let tableSiteFormAddressList = []
            let tableFileList = []
            let tableModelResultList = []
            const personsList = datas.siteFormPersonList
            const fileRecords = datas.fileRecords
            const orgCateJournals = datas.orgCateJournals
            const siteFormAddressList = datas.siteFormAddressList
            const fileList = datas.fileList
            personsList.forEach((e) => {
              tablePersons.push(ress.data.ref.SiteFormPerson[e])
            })
            orgCateJournals.forEach((e) => {
              tableOrgCateJournals.push(ress.data.ref.OrgCateJournal[e])
            })
            siteFormAddressList.forEach((e) => {
              tableSiteFormAddressList.push(ress.data.ref.SiteFormAddress[e])
            })
            fileList.forEach((e) => {
              tableFileList.push(ress.data.ref.FileList[e])
            })

            $form.setValues(datas)
            $form.query('.siteFormPersonList').take().setValue(tablePersons)
            $form.query('.orgCateJournals').take().setValue(tableOrgCateJournals)
            $form.query('.siteFormAddressList').take().setValue(tableSiteFormAddressList)
            $form.query('.fileList').take().setValue(tableFileList)

            // 确定是那个评审状态
              let cMen = 0// 是否创建人
              let pMen = 0// 是否评审人
              const accountId = app.$store.getters.userId
              const createdId = $form.query('.createdId').take().value
              if (accountId == createdId) {
                cMen = 1
              }
              const approveStatus = $form.query('.approveStatus').take().value
              const states = $form.query('state').get('data')
              if ($attrs.params.flag == 'add' || approveStatus == 'DRAFT') {
                states.bolType = 1
              } else if ($attrs.params.flag == 'edit') {
                tablePersons.forEach(datas => {
                  if (datas.userId == accountId) {
                    states.bolType = 2
                    states.addressReadOnly = true
                  }
                })

                let scoreBol = 0 // 检查是否有写得分
                let authResultBol = 0 // 检查是否有写结果
                fileRecordsR.forEach(datas => {
                  if (datas.score) {
                    scoreBol = 1
                  }
                  if (datas.authResult) {
                    authResultBol = 1
                  }
                })

                if (
                  accountId == createdId &&
                  scoreBol == 1 &&
                  authResultBol == 1
                ) {
                  states.bolType = 3
                }
              } else {
                states.bolType = 4
              }

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
          'business-type': 'SUPPLIER',
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
            if (integrationMode.integrationMode == "None") {
              updateButtonConfig($form)
            }
          }`)
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            properties: {
              ...Steps,
              layout: {
                type: 'void',
                'x-component': 'FormContainer',
                properties: {
                  collapse: {
                    ...collapseMain
                  }
                }
              }
            }
          }
        }
      },
      // 下拉框节点列表
      CFillProgress: {
        type: 'void',
        'x-component': 'CFillProgress',
        'x-component-props': {
          class: "contract-progress",
          'ref': "contractProgress",
          'nodeName': "$t('logisticsMod.contractInfo')",
          data: `{{[
              {
                code: 'siteAssessmentForm',
                name: $t('route.siteAssessment2'),
                percentage: 0
              },
              {
                code: 'siteFormHistoryList',
                name: $t('vendorMod.historySiteAssessment'),
                percentage: 0
              },
              {
                code: 'authBaseInfo',
                name: $t('vendorMod.authBaseInfo'),
                percentage: 0
              },
              {
                code: 'authOrganization',
                name: $t('vendorMod.authOrganization'),
                percentage: 0
              },
              {
                code: 'workingGroupStaff',
                name: $t('vendorMod.workingGroupStaff'),
                percentage: 0
              },
              {
                code: 'attachment',
                name: $t('vendorMod.attachment'),
                percentage: 0
              }
            ]}}`,
          percentage: "{{true}}",
          '@index-click': `{{ (code) => {
              let anchorEle = document.querySelector('#collapse_' + code)
              if (anchorEle) {
                anchorEle.scrollIntoView(true)
              }
           } }}`
        }
      }
    }
  }
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('siteA')
}

const $submits = (type, $form, $queryEngine, $message, $t, $bus) => {
  let values = $form.values
  const tablefileList = $form.query('.fileList').take().value
  const tablefileRecords = $form.query('.fileRecords').get('value')
  const tableorgCateJournals = $form.query('.orgCateJournals').get('value')
  const tablesiteFormAddressList = $form.query('.siteFormAddressList').get('value')
  const tablesiteFormPersonList = $form.query('.siteFormPersonList').get('value')
  values.fileList = tablefileList
  values.fileRecords = tablefileRecords
  values.orgCateJournals = tableorgCateJournals
  values.siteFormAddressList = tablesiteFormAddressList
  values.siteFormPersonList = tablesiteFormPersonList
  console.log(values)
  const siteFormId = attrs.params.row?.siteFormId || null
  const approveStatus = $form.query('.approveStatus').take().value
  if (type == 'SAVE') { // 暂存的时候
    if ([null, 'DRAFT'].includes(approveStatus)) { // 新增或者编辑的时候
      values.approveStatus = 'DRAFT'
      $queryEngine.request.save(values, { query: { '*':{},
          "siteFormPersonList": {'*': {}},
          "siteFormAddressList": {'*': {}},
          "fileRecords": {'*': {}},
          "fileList": {'*': {}},
          "orgCateJournals": {'*': {}} } }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('siteA')
        emitTabRemove(attrs.tabName)
      }).catch( err => {
        console.log(err)
      })
    } else { // 其他状态的暂存基本是已发版状态
      $queryEngine.request.save(values, { query: { '*':{},
          "siteFormPersonList": {'*': {}},
          "siteFormAddressList": {'*': {}},
          "fileRecords": {'*': {}},
          "fileList": {'*': {}},
          "orgCateJournals": {'*': {}} }
      }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('siteA')
        emitTabRemove(attrs.tabName)
      }).catch( err => {
        console.log(err)
      })
    }
  } else { // 提交
    const reviewResult = $form.query('.reviewResult').take().value
    if (!reviewResult || reviewResult == '') { // 没有最终结论的时候
      values.approveStatus = 'PUBLISH'
      $queryEngine.request.save(values, { query: { '*':{} },
        "siteFormPersonList": {'*': {}},
        "siteFormAddressList": {'*': {}},
        "fileRecords": {'*': {}},
        "fileList": {'*': {}},
        "orgCateJournals": {'*': {}} }).then(() => {
        $message.success($t('common.successSubmit'))
        $bus.$emit('siteA')
        emitTabRemove(attrs.tabName)
      })
    } else { // 供应商确认后的提交工作流
      $queryEngine.request.save(values, { query: { '*':{},
          "siteFormPersonList": {'*': {}},
          "siteFormAddressList": {'*': {}},
          "fileRecords": {'*': {}},
          "fileList": {'*': {}},
          "orgCateJournals": {'*': {}} } }).then((res) => {
        const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
        componentInstance.setWorkflowBusinessId(res.data[0]?.planConfirmId || null)
        componentInstance.setWorkflowTabDisabled(true)
        componentInstance.setWorkflowBusinessVariables({})
        componentInstance.handlerAfter(type.toUpperCase(), () => {
          $bus.$emit('siteA')
          emitTabRemove(attrs.tabName)
        })
      })
    }
  }
}

const $getorgCateJournalByFormId = (reviewFormId:any, isAdd = false, $form) => {
  app.$http({
    url: '/api-sup/review/reviewForm/listOrgAndCategoryByReviewId',
    method: 'GET',
    params: { reviewFormId },
    loading: true
  })
    .then(res => {
      if (res.data) {
        const orgData = res.data.orgInfos
        const catData = res.data.orgCategorys

        if (isAdd) {
          let orgCateJournalsArr = []
          // 需要将实体数组和品类数组打平
          orgData.forEach(oItem => {
            catData.forEach(cItem => {
              let orgCateObj = {
                categoryCode: cItem.categoryCode,
                categoryFullName: cItem.categoryFullName,
                categoryId: cItem.categoryId,
                categoryName: cItem.categoryName,
                orgCode: oItem.orgCode,
                orgId: oItem.orgId,
                orgName: oItem.orgName,
                quoted: 'N'
              }
              orgCateJournalsArr.push(orgCateObj)
            })
          })
          $form.query('.orgCateJournals').take().value = orgCateJournalsArr
        }
      }
    })
    .catch(err => {
      console.log(err)
    })
}


const scope = {
  app,
  t,
  $attrs: attrs,
  $disabledFlag,
  emitTabRemove,
  initButtonConfig,
  $back,
  supCommonApi,
  siteReviewModel,
  quaApi,
  $submits,
  $getorgCateJournalByFormId,
  $disabledAdd
}

const components = {
  vendorAccessAttachment,
  CFillProgress,
  Printer
}
</script>

<template>
  <RenderEngine schemaKey="siteAssessmentDetail" :pageAttrs="$attrs" :schema="schema" :scope="scope" :components="components" />
</template>

<style>
.siteAssessment .render-form-container__fixed-footer{
  padding-top:0px
}
.info-fill-progress{
  position: fixed;
  width: 210px;
  top: 104px;
  right: 0px;
  bottom: 0px;
}
.siteAssessment {
  overflow: auto;
  padding-right: 181px;
}
.order-form-contain .contract-progress{
  top: 64px
}
</style>
