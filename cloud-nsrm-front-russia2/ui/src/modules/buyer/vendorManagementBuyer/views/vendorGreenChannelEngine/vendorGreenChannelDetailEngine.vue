<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  observer,
  markRaw,
  useAutoMountInstanceToField
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import { validEmail, validatePhone } from '@/utils/validate'
import { Steps } from './components/steps'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import {userInfoForm} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/userInfoForm";
import {companyType} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyType";
import {companyInfo} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyInfo";
import {companyBaseInfo} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyBaseInfo";
import DictSelect from "lib@/components/c-select/dict-select.vue";
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import {contactInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/contactInfoList";
import {bankInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/bankInfoList";
import {cooInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/cooInfoList";
import {financeInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/financeInfoList";
import {factoryInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/factoryInfoList";
import {vendorSiteInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/vendorSiteInfos";
import {
  operatingPerformancesList
} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/operatingPerformances";
import {overallStrengths} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/overallStrengths";
import {companySizesList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companySizes";
import {rdCapableList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/rdCapable";
import {qualityControlList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/qualityControlList";
import {
  equipmentInformationList
} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/equipmentInformationList";
import {
  productCapableInfosList
} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/productCapableInfosList";
import {
  ceeaAfterSalesAbility
} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/ceeaAfterSalesAbility";
import {clientStatusList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/clientStatusList";
import {managementInfoList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/managementInfo";
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress'
import {fileUploadsList} from "modb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/fileUploadsList";

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

let $disabled = ['view', 'approve'].includes(attrs.params.flag || '')

const newAddress = defineComponent({
  name: 'newAddress',
  props: CAddress.props,
  setup(props, { listeners, attrs, slots }) {
    useAutoMountInstanceToField()

    return () => {
      return h(CAddress, { props: { ...attrs, ...props }, on: listeners, ref: 'address' }, slots)
    }
  }
})

// {
//   a: {
//     'x-component': newAddress
//   }
// }

// $form.query('a').take(field => {
//   console.log(field.componentProps.componentInstance.$refs.address.init())
// })

const customUpdateButton = computed(() => (!$disabled && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = computed(() => (!$disabled && !['APPROVED', 'SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))
const query = {
  "*":{},
  "bankInfos": {'*': {}},
  "siteInfos": {'*': {}},
  "contactInfos": {'*': {}},
  "financeInfos": {'*': {}},
  "orgCategorys": {'*': {}},
  "orgInfos": {'*': {}},
  "operationInfo": {'*': {}},
  "cateJournalList": {'*': {}},
  "plantInfos": {'*': {}},
  "fileUploads": {'*': {}},
  "operationQualities": {'*': {}},
  "operationProducts": {'*': {}},
  "operationEquipments": {'*': {}},
  "managementInfo": {'*': {}},
  "managementAttaches": {'*': {}},
  "supplierLeaderList": {'*': {}},
  "otherInfo": {'*': {}},
  "operatingLogList": {'*': {}},
  "questSupplierList": {'*': {}},
  "qualityControls": {'*': {}},
  "equipmentInformations": {'*': {}},
  "productCapableInfos": {'*': {}},
  "clientStatusList": {'*': {}},
  "overallStrengths": {'*': {}},
  "companySizes": {'*': {}},
  "rdCapables": {'*': {}},
  "rdCapableAdditionals": {'*': {}},
  "operatingPerformances": {'*': {}},
  "userInfo": {'*': {}}
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
    console.log(attrs.params, 'params')
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = !$disabled
    componentInstance.buttonConfigInfo.close.view = false
    componentInstance.setWorkflowBusinessId(attrs.params.companyId || '')
    componentInstance.setWorkflowTabDisabled(([null, undefined, 'DRAFT', 'WITHDRAW', 'REJECTED'].includes(attrs.params.row?.status) && attrs.params.flag != 'approve'))
    componentInstance.setWorkflowBusinessVariables({})
  })
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    // debugger
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.close.view = false
  }, 50)
}

const $managementChange = (value, name, $form) => {
  try {
    if (value) {
      let data = $form.query('.managementAttaches').take().value
      if (value == 'Y') {
        let bold = 1
        data.forEach((e) => {
          if (e.documentInspection == name) {
            bold = 0
          }
        })
        if (bold) {
          data.unshift({
            documentInspection: name,
            managementAttachId: null,
            managementInfoId: null,
            companyId: null,
            fileuploadId: null,
            authType: '',
            authDescription: '',
            authNum: '',
            authDate: '',
            authOrg: '',
            endDate: ''
          })
        }
      } else {
        data.forEach((e, index) => {
          if (e.documentInspection == name) {
            data.splice(index, 1)
          }
        })
      }
      $form.query('.managementAttaches').take().value = data
    }
  } catch (e) {

  }

}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyId: attrs.params.companyId || null,
      $disabled: false,
      deleAttr: [], // 主营品类删除的信息
      deleFileUploads: [] // 附件删除的信息
    }
  },
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container vendorGreen',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        greenQuery: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            initButtonConfig($form)
            return $attrs.params && $attrs.params.companyId
          }`),
          method: 'read',
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.action = 'greenQuery'
            data.tree = true
            data.loading = true
            data.query = query
            $form.query('state').get('data').companyId = $attrs.params.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: $attrs.params.companyId
                  }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res.records[0]
            if (data.userInfo) {
              data.userInfo.greenChannelReason = data?.greenChannelReason
            }
            data.ifTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.ifTechnicalStandard
            data.productsTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.productsTechnicalStandard
            data.memo = data.rdCapableAdditionals[data.rdCapableAdditionals.length-1]?.memo
            if(data.rdCapables.length == 0) {
              data.rdCapables = [{type:'人数'}]
            }
            $form.setValues(data)

            const status = data.status
            if (['APPROVED', 'SUBMITTED', 'ABANDONED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            }
            if ($disabled) {
              $form.query('state').get('data').$disabled = true
            }

            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
              $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
              })
              const cateJournalList = $form.query('.cateJournalList').take().value
              if (cateJournalList.length > 0) {
                 const categoryName = cateJournalList.map(v => v.categoryName).join(',')
                 $form.query('.categoryName').take().value = categoryName
              }
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
            return data
          }`)
        },
        greenSave: {
          method: 'read',
          cascadeDeletion: true,
          loading: true
        },
        greenSubmit: {
          method: 'read',
          cascadeDeletion: true,
          loading: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: expression('$attrs.params.flag != "view"')
          },
          'business-id': expression('$attrs.params?.companyId || null'),
          'business-type': 'supplierGreenChannel',
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus, $t)
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
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                'x-component-props': {
                  defaultOpenPanelCount: 1
                },
                properties: {
                  // 供应商账号信息
                  ...userInfoForm,
                  // 企业性质
                  ...companyType,
                  // 企业三证
                  ...companyInfo,
                  // 企业基本信息
                  ...companyBaseInfo,
                  // 联系人信息
                  ...contactInfoList,
                  // 银行信息
                  ...bankInfoList,
                  // 合作信息
                  ...cooInfoList,
                  // 财务信息
                  ...financeInfoList,
                  // 厂房信息
                  ...factoryInfoList,
                  // 供应商地点信息
                  ...vendorSiteInfoList,
                  // 经营业绩
                  ...operatingPerformancesList,
                  // 整体实力
                  ...overallStrengths,
                  // 公司规模
                  ...companySizesList,
                  // 研发能力
                  ...rdCapableList,
                  // 质量管控
                  ...qualityControlList,
                  // 设备信息
                  ...equipmentInformationList,
                  // 产品能力信息
                  ...productCapableInfosList,
                  // 售后服务
                  ...ceeaAfterSalesAbility,
                  // 客户情况
                  ...clientStatusList,
                  // 管理体系信息
                  ...managementInfoList,
                  // 相关附件信息
                  // ...fileUploadsList
                  fileUploadsList: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('vendorMod.sceneAttachmentInfo2'),
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      fileUploads: {
                        'x-query-engine-relation': 'fileUploads:*',
                        type: 'array',
                        'x-component': 'FileDynamic',
                        'x-component-props': {
                          'scene-module-code': "SCENE_SUPPLIER_ATTACHMENT",
                          'businessId': expression(`$attrs.params.companyId || null`),
                          'editable': expression(`!$disabled`),
                          'need-init': false
                        }
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
              class: 'contract-progress',
              'ref': 'contractProgress',
              'nodeName': "$t('logisticsMod.contractInfo')",
              data: `{{[
              {
                code: 'userInfoForm',
                name: $t('vendorMod.vendorUserInfo'),
                percentage: 0
              },
              {
                code: 'companyType',
                name: $t('vendorMod.companyType'),
                percentage: 0
              },
              {
                code: 'companyInfo',
                name: $t('vendorMod.enterpriseThreeCertificates'),
                percentage: 0
              },
              {
                code: 'companyBaseInfo',
                name: $t('vendorMod.companyBaseInfo'),
                percentage: 0
              },
              {
                code: 'contactInfoList',
                name: $t('vendorMod.contactInfo'),
                percentage: 0
              },
              {
                code: 'bankInfoList',
                name: $t('vendorMod.bankInfo'),
                percentage: 0
              },
              {
                code: 'cooInfo',
                name: $t('supRisk.cooInfo'),
                percentage: 0
              },
              {
                code: 'financeInfo',
                name: $t('vendorMod.financeInfo'),
                percentage: 0
              },
              {
                code: 'factoryInfo',
                name: $t('vendorMod.factoryInfo'),
                percentage: 0
              },
              {
                code: 'vendorSiteInfo',
                name: $t('vendorMod.vendorSiteInfos'),
                percentage: 0
              },
              {
                code: 'operatingPerformancesList',
                name: $t('vendorMod.operatingResult'),
                percentage: 0
              },
              {
                code: 'overallStrengthList',
                name: $t('vendorMod.overallStrength'),
                percentage: 0
              },
              {
                code: 'companySizesList',
                name: $t('vendorMod.companySize'),
                percentage: 0
              },
              {
                code: 'rdCapableList',
                name: $t('vendorMod.RandDCapable5'),
                percentage: 0
              },
              {
                code: 'qualityControlList',
                name: $t('vendorMod.qualityControl'),
                percentage: 0
              },
              {
                code: 'equipmentInformationList',
                name: $t('vendorMod.deviceInfo'),
                percentage: 0
              },
              {
                code: 'productCapableInfosList',
                name: $t('vendorMod.productCapableInfo'),
                percentage: 0
              },
              {
                code: 'ceeaAfterSalesAbilityList',
                name: $t('vendorMod.afterSalesService'),
                percentage: 0
              },
              {
                code: 'clientStatus',
                name: $t('vendorMod.clientStatus'),
                percentage: 0
              },
              {
                code: 'managementInfoList',
                name: $t('vendorMod.managementSystemInfo'),
                percentage: 0
              },
              {
                code: 'fileUploadsList',
                name: $t('vendorMod.sceneAttachmentInfo2'),
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

    }
  }
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('green')
}

const $saveBill = async(type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any, $t) => {
  let values = $form.values
  values.userInfo = $form.query('.userInfo').take().value
  values.contactInfos = $form.query('.contactInfos').take().value
  values.bankInfos = $form.query('.bankInfos').take().value
  values.orgCategorys = $form.query('.orgCategorys').take().value
  values.financeInfos = $form.query('.financeInfos').take().value
  values.plantInfos = $form.query('.plantInfos').take().value
  values.siteInfos = $form.query('.siteInfos').take().value
  values.operatingPerformances = $form.query('.operatingPerformances').take().value
  values.overallStrengths = $form.query('.overallStrengths').take().value
  values.companySizes = $form.query('.companySizes').take().value
  values.qualityControls = $form.query('.qualityControls').take().value
  values.equipmentInformations = $form.query('.equipmentInformations').take().value
  values.productCapableInfos = $form.query('.productCapableInfos').take().value
  values.clientStatusList = $form.query('.clientStatusList').take().value
  values.managementInfo = $form.query('.managementInfo').take().value
  values.managementAttaches = $form.query('.managementAttaches').take().value
  values.fileUploads = $form.query('.fileUploads').take().value
  values.rdCapableAdditionals = [{}]
  values.rdCapableAdditionals[0].ifTechnicalStandard = values.ifTechnicalStandard
  values.rdCapableAdditionals[0].productsTechnicalStandard = values.productsTechnicalStandard
  values.rdCapableAdditionals[0].memo = values.memo
  // 绿色通道不需要判断是否第一次登录，默认为N
  values.firstLoginFlag = 'N'
  // 营业期限更改

  const businessDate = $form.query('.businessDate').take()?.value
  if (businessDate && businessDate !== '' && businessDate.length > 0) {
    values.businessStartDate = $form.query('.businessDate').take()?.value[0]
    values.businessEndDate = $form.query('.businessDate').take()?.value[1]
  }
  // 营业地址组件修改后适配后端接口
  values.greenChannelReason = values.userInfo.greenChannelReason
  if ($form.query('state').get('data')?.deleAttr[0]) {
    values.cateJournalList = [...values.cateJournalList,...$form.query('state').get('data')?.deleAttr[0]] // 主营品类删除的内容
  }
  if ($form.query('state').get('data')?.deleFileUploads) {
    values.fileUploads.forEach(e => {
      delete e.sceneFileId
    })
    values.fileUploads = [...values.fileUploads,...$form.query('state').get('data')?.deleFileUploads] // 附件信息删除的内容
  }
  console.log(values, 'values')
  // 校验
  if (type != 'SAVE') {
    let validate = 0
    await $form.validate().then().catch(eq => {
      app.$message.error(eq[0].messages[0])
      validate = 1
    })
    if (validate) {
      return false
    }
    if ($form.query('.orgCategorys').take().value.length <= 0) {
      app.$message.error('请输入合作信息')
      return false
    }
    const companyId = attrs.params?.companyId || null
    const status = $form.query('.status').take().value

    // 新增用户名校验必填
    let overseasRelation = $form.query('.overseasRelation').take().value
    let supplierType = $form.query('.supplierType').take().value
    if (!overseasRelation || overseasRelation == '' || !supplierType || supplierType == '') {
      $message.error($t('请输入完整企业性质数据'))
      return false
    }
    const orgCategorys = $form.query('.orgCategorys').take().value
    if (orgCategorys.length > 0) {
      let orgBol = false
      orgCategorys.forEach(data => {
        if (['', null].includes(data.orgId) || ['', null].includes(data.categoryId)) {
          orgBol = true
        }
      })
      if (orgBol == true) {
        $message.error($t('vendorMod.msgOrgCatTableInfo'))
        return false
      }
    }
  }
  if (type == 'SAVE') { // 暂存的时候
    if ([null, undefined, 'DRAFT'].includes(status)) { // 新增或者编辑的时候
      values.status = 'DRAFT'
      $queryEngine.request.save(values, { query: {"*":{}}, action: 'greenSave' }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('green')
        emitTabRemove(attrs.tabName)
      }).catch( err => {
        console.log(err)
      })
    } else { // 其他状态的暂存基本是已发版状态
      $queryEngine.request.save(values, { query: {"*":{}}, action: 'greenSave' }).then(() => {
        $message.success($t('common.successSave'))
        $bus.$emit('green')
        emitTabRemove(attrs.tabName)
      }).catch( err => {
        console.log(err)
      })
    }
  } else { // 提交
      $queryEngine.request.save(values, { query: {"*":{}}, action: 'greenSubmit' }).then((res) => {
        const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
        componentInstance.setWorkflowBusinessId(res.data[0]?.companyId || null)
        componentInstance.setWorkflowTabDisabled(false)
        componentInstance.setWorkflowBusinessVariables({})
        componentInstance.handlerAfter(type.toUpperCase(), () => {
          $bus.$emit('green')
          emitTabRemove(attrs.tabName)
        })
        setTimeout(() => {
          $form.readPretty = true
          $form.query('state').get('data').$disabled = true
          componentInstance.buttonConfigInfo.save.view = false
          componentInstance.buttonConfigInfo.submit.view = false
        }, 100)
      })
  }
}

const scope = {
  app,
  t,
  $attrs: attrs,
  updateButtonConfig,
  performPlanService,
  $disabled,
  emitTabRemove,
  initButtonConfig,
  $saveBill,
  $back,
  DictSelect,
  observer,
  $managementChange,
  query,
  validEmail,
  validatePhone
}
const components = {
  SrmCommonFile,
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  newAddress
}
</script>

<template>
<!--  :readOnly="$disabled"-->
  <RenderEngine
    schemaKey="vendorGreenChannelDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style>
.vendorGreen .companyInfo {
  display: flex;
  padding: 16px;
  width: 100%;
}
.vendorGreen .render-form-container__fixed-footer{
  padding-top:0px
}
.info-fill-progress{
  position: fixed;
  width: 210px;
  top: 104px;
  right: 0px;
  bottom: 0px;
}
.vendorGreen {
  overflow: auto;
  padding-right: 181px;
}
.order-form-contain .contract-progress{
  top: 64px
}
.rdCapableList{
  height: 80px;
  margin-bottom: 20px;
}
.vendorGreen .render-pix-form-item-feedback-layout-loose.render-pix-form-item-feedback-has-text:not(.render-pix-form-item-inset) {
  margin-bottom: var(--mb-md)
}
</style>
