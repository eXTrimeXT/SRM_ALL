<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  methodExpression,
  i18nExpression,
  observer,
  useAutoMountInstanceToField
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
import { validEmail, validatePhone } from '@/utils/validate'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import { companyType } from './components/companyType'
import { companyInfo } from './components/companyInfo'
import { companyBaseInfo } from './components/companyBaseInfo'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import { contactInfoList } from './components/contactInfoList'
import { bankInfoList } from './components/bankInfoList'
import { factoryInfoList } from './components/factoryInfoList'
import {
  operatingPerformancesList
} from './components/operatingPerformances'
import { overallStrengths } from './components/overallStrengths'
import { companySizesList } from './components/companySizes'
import { rdCapableList } from './components/rdCapable'
import { qualityControlList } from './components/qualityControlList'

import {
  equipmentInformationList
} from './components/equipmentInformationList'
import {
  productCapableInfosList
} from './components/productCapableInfosList'
import {
  ceeaAfterSalesAbility
} from './components/ceeaAfterSalesAbility'
import { clientStatusList } from './components/clientStatusList'
import { managementInfoList } from './components/managementInfo'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress'
import MainHerder from './mainHeater.vue'
import {personal} from "mod@/common/userManage/views/companyInfoMaintainEngine/components/personal";
import {vendorSiteInfoList} from "mod@/common/userManage/views/companyInfoMaintainEngine/components/vendorSiteInfos";

const props = defineProps({
  formCompanyNature: {
    type: Object,
    default: () => ({})
  },
  type: {
    type: String,
    default: () => ('')
  }
})

const { app, emitTabRemove, t, vendor } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

let $disabled = false

const newAddress = defineComponent({
  name: 'newAddress',
  props: CAddress.props,
  setup (props, { listeners, attrs, slots }) {
    useAutoMountInstanceToField()

    return () => {
      return h(CAddress, { props: { ...attrs, ...props }, on: listeners, ref: 'address' }, slots)
    }
  }
})

const query = {
  "*":{},
  "bankInfos": {'*': {}},
  "contactInfos": {'*': {}},
  "orgCategorys": {'*': {}},
  "orgInfos": {'*': {}},
  "operationInfo": {'*': {}},
  "plantInfos": {'*': {}},
  "fileUploads": {'*': {}},
  "operationQualities": {'*': {}},
  "operationProducts": {'*': {}},
  "operationEquipments": {'*': {}},
  "managementInfo": {'*': {}},
  "managementAttaches": {'*': {}},
  "supplierLeaderList": {'*': {}},
  "otherInfo": {'*': {}},
  "siteInfos": {'*': {}},
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
  'cateJournalList': {'*': {}}
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
          $form.query('.managementAttaches').take().invoke('addRow', 'unshift', {
            documentInspection: name,
            managementAttachId: null,
            managementInfoId: null,
            companyId: null,
            fileuploadId: null,
            authType: '',
            authDescription: '',
            authNum: '',
            authDate: null,
            authOrg: '',
            endDate: null
          })
        }
      } else {
        data.forEach((e, index) => {
          if (e.documentInspection == name) {
            $form.query('.managementAttaches').take().invoke('remove', index)
          }
        })
      }
    }
  } catch (e) {

  }
}
const $ifPersonal = ($form) => {
  $form.query('.personal').take().visible = true
  $form.query('.companyTypeAll').take().visible = false
  $form.query('.companyInfo').take().visible = false
  $form.query('.companyBaseInfo').take().visible = false
  $form.query('.factoryInfo').take().visible = false
  $form.query('.operatingPerformancesList').take().visible = false
  $form.query('.overallStrengthList').take().visible = false
  $form.query('.companySizesList').take().visible = false
  $form.query('.rdCapableList').take().visible = false
  $form.query('.qualityControlList').take().visible = false
  $form.query('.equipmentInformationList').take().visible = false
  $form.query('.productCapableInfosList').take().visible = false
  $form.query('.ceeaAfterSalesAbilityList').take().visible = false
  $form.query('.clientStatus').take().visible = false
  $form.query('.managementInfoList').take().visible = false
  $form.query('.fileUploadsList').take().visible = false
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyId: app.$store.getters.userInfo.companyId || null,
      $disabled: false,
      overseasRelation: '',
      status: '',
      deleAttr: [], // 主营品类删除的信息
      deleFileUploads: [], // 附件删除的信息
      type: '', // 是否在注册中来的
      // 锚点数据
      progressData: [
              {
                code: 'companyTypeAll',
                name: t('vendorMod.companyType'),
                percentage: 0
              },
              {
                code: 'companyInfo',
                name: t('vendorMod.enterpriseThreeCertificates'),
                percentage: 0
              },
              {
                code: 'companyBaseInfo',
                name: t('vendorMod.companyBaseInfo2'),
                percentage: 0
              },
              {
                code: 'contactInfoList',
                name: t('vendorMod.contactInfo'),
                percentage: 0
              },
              {
                code: 'bankInfoList',
                name: t('vendorMod.bankInfo'),
                percentage: 0
              },
              {
                code: 'factoryInfo',
                name: t('vendorMod.factoryInfo'),
                percentage: 0
              },
              {
                code: 'operatingPerformancesList',
                name: t('vendorMod.operatingResult'),
                percentage: 0
              },
              {
                code: 'overallStrengthList',
                name: t('vendorMod.overallStrength'),
                percentage: 0
              },
              {
                code: 'companySizesList',
                name: t('vendorMod.companySize'),
                percentage: 0
              },
              {
                code: 'rdCapableList',
                name: t('vendorMod.RandDCapable5'),
                percentage: 0
              },
              {
                code: 'qualityControlList',
                name: t('vendorMod.qualityControl'),
                percentage: 0
              },
              {
                code: 'equipmentInformationList',
                name: t('vendorMod.deviceInfo'),
                percentage: 0
              },
              {
                code: 'productCapableInfosList',
                name: t('vendorMod.productCapableInfo'),
                percentage: 0
              },
              {
                code: 'ceeaAfterSalesAbilityList',
                name: t('vendorMod.afterSalesService'),
                percentage: 0
              },
              {
                code: 'clientStatus',
                name: t('vendorMod.clientStatus'),
                percentage: 0
              },
              {
                code: 'managementInfoList',
                name: t('vendorMod.managementSystemInfo'),
                percentage: 0
              },
              {
                code: 'fileUploadsList',
                name: t('vendorMod.sceneAttachmentInfo2'),
                percentage: 0
              }
            ]
    }
  },
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: expression(`$form.query('state').get('data').type !== 'registered' ? 'flex-container companyInfos' : 'flex-container registered'`),
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation

             $form.query('state').get('data').type = $props.type

             if ($props.formCompanyNature.value?.overseasRelation == 'PERSONAL') {
              $form.query('.personal').take().visible = true
              $form.query('.companyTypeAll').take().visible = false
              $form.query('.companyInfo').take().visible = false
              $form.query('.companyBaseInfo').take().visible = false

              $form.query('.overseasRelation2').take().value = 'PERSONAL'
            } else {
              $form.query('.personal').take().visible = false
              $form.query('.companyTypeAll').take().visible = true
              $form.query('.companyInfo').take().visible = true
              $form.query('.companyBaseInfo').take().visible = true
            }
             $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            return app.$store.getters.userInfo.companyId && $buyer()
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.tree = true
            data.query = query
            $form.query('state').get('data').companyId = app.$store.getters.userInfo.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),
          transformResponse: expression(`(res) => {
            const data = JSON.parse(res).data.records[0]

            const status = data.status
            if (['APPROVED','SUBMITTED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status
            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
               mainHerder.componentProps.status = status
               if (status == 'SUBMITTED') {
                 mainHerder.componentProps.stepsActive = 4
               }
               if (status == 'APPROVED') {
                 mainHerder.componentProps.stepsActive = 6
               }
               mainHerder.componentProps.flowRemark = data.flowRemark
            }

            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = $form.query('state').get('data').overseasRelation
              if (overseasRelation == 'PERSONAL') {
                $form.query('.personal').take().visible = true
                $form.query('.companyTypeAll').take().visible = false
                $form.query('.companyInfo').take().visible = false
                $form.query('.companyBaseInfo').take().visible = false

                data.companyName2 = data.companyName
              } else {
                $form.query('.personal').take().visible = false
                $form.query('.companyTypeAll').take().visible = true
                $form.query('.companyInfo').take().visible = true
                $form.query('.companyBaseInfo').take().visible = true
              }
            }

            $form.setValues(data)
            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
            setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.visible = true
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)

            return data
          }`)
        },
        vendorRead: {
          immediate: true,
          loading: true,
          method: 'read',
          ready: expression(`() => {
            setTimeout(() => {
             if ($props.formCompanyNature.value?.overseasRelation == 'PERSONAL') {
              let state = $form.query('state').get('data')
              $ifPersonal($form)
              state.progressData = personProgressData
            }
            $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
            $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            console.log(app.$store.getters.userInfo.companyId, 'companyId')
            console.log(!$buyer(), '$buyer')
            if(!app.$store.getters.userInfo.companyId && !$buyer()){
              $form.query('fileUploads').take(field => {
                  field.visible = true
              })
            }
            return app.$store.getters.userInfo.companyId && !$buyer()
          }`),
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.tree = true
            data.action = 'vendorRead'
            data.query = query
            $form.query('state').get('data').companyId = app.$store.getters.userInfo.companyId
            data.payload = {
              "filter": {
                  "companyId": {
                      eq: app.$store.getters.userInfo.companyId
                  }
              }
            }
            return data
          }`),
          onSuccess: expression(`(res) => {
            const data = res[0]

            const status = data.status
            if (['APPROVED','SUBMITTED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status

            data.ifTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.ifTechnicalStandard
            data.productsTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.productsTechnicalStandard
            data.memo = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.memo

            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
               mainHerder.componentProps.status = status
               if (status == 'SUBMITTED') {
                 mainHerder.componentProps.stepsActive = 4
               }
               if (status == 'APPROVED') {
                 mainHerder.componentProps.stepsActive = 6
               }
               mainHerder.componentProps.flowRemark = data.flowRemark
            }

            if(data.rdCapables?.length == 0) {
              data.rdCapables = [{type: $t('vendorMod.peopleNumber')}]
            }
            const rdCapables = [data.rdCapables[data.rdCapables?.length-1]]
            data.rdCapables = rdCapables
            data.companySizes = [data.companySizes[data.companySizes?.length-2],data.companySizes[data.companySizes?.length-1]]

            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
              if (overseasRelation == 'PERSONAL') {
                state.progressData = personProgressData
                $ifPersonal($form)

                data.companyName2 = data.companyName
                data.companyShortName2 = data.companyShortName
              }
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
             data.overseasRelation2 = $props.formCompanyNature.value?.overseasRelation
             data.companyType = $props.formCompanyNature.value?.companyType
             data.companyName2 = data.companyName
             data.companyShortName2 = data.companyShortName
            }
            console.log(data, 'data')
            $form.setValues(data)

            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.visible = true
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)

              const cateJournalList = $form.query('.cateJournalList').take().value
              if (cateJournalList?.length > 0) {
                 const categoryName = cateJournalList.map(v => v.categoryName).join(',')
                 $form.query('.categoryName').take().value = categoryName
              }
              let deleFileUploads = [] // 附件要删除的列表
              data.fileUploads.forEach(e => {
                deleFileUploads.push({$delete:e.sceneFileId})
              })
              $form.query('state').get('data').deleFileUploads = deleFileUploads

              // 处理供应商为个人类型的时候，公司规模与研发能力不显示的问题
              setTimeout(() => {

              })

          }`)
        },
        vendorSave: {
          method: 'read',
          autoFormatResult: false,
          cascadeDeletion: true,
          loading: true,
        },
        vendorWithdraw: {
          autoFormatResult: false,
          loading: true,
        },
        vendorSubmit: {
          autoFormatResult: false,
          cascadeDeletion: true,
          loading: true,
        },
      }
    },
    properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            items: {
              type: 'object',
              properties: {
                prevOne: {
                  type: 'void',
                  'x-content': i18nExpression('common.prevOne'),
                  'x-component': 'Button',
                  'x-visible': expression(`['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status)`),
                  'x-component-props': {
                    type: 'default',
                    '@click': expression(`async (values) => {
                      let overseasRelation = null
                      overseasRelation = $form.query('state').get('data').overseasRelation
                      app.$emit('whatOverseasRelation', overseasRelation)
                    }`)
                  }
                },
                staging: {
                  type: 'void',
                  'x-content': i18nExpression('common.staging'),
                  'x-component': 'Button',
                  'x-visible': expression(`['', 'DRAFT', 'WITHDRAW'].includes($form.query('state').get('data').status)`),
                  'x-component-props': {
                    type: 'default',
                    '@click': expression(`async (values) => {
                      $saveBill('staging', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)
                  }
                },
                submit: {
                  type: 'void',
                  'x-content': i18nExpression('common.submit'),
                  'x-component': 'Button',
                  'x-visible': expression(`['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status)`),
                  'x-component-props': {
                    '@click': expression(`async (values) => {
                      $saveBill('submit', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)
                  }
                },
                recall: {
                  type: 'void',
                  'x-content': i18nExpression('common.recall'),
                  'x-component': 'Button',
                  'x-visible': expression(`['SUBMITTED'].includes($form.query('state').get('data').status)`),
                  'x-component-props': {
                    '@click': expression(`async (values) => {
                      $saveBill('recall', $form, $queryEngine, $confirm, $message, $bus, $t)
                    }`)
                  }
                }
              }
            },
            properties: {
              mainHerder: {
                type: 'void',
                'x-component': 'MainHerder',
                'x-component-props': {
                  stepsActive: 3,
                  flowRemark: '',
                  registered: expression(`$form.query('state').get('data').type == 'registered'`) // 判断是否在注册来的
                },
              },
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                'x-component-props': {
                  defaultOpenPanelCount: 1
                },
                properties: {
                  // 企业性质
                  ...companyType,
                  // 个人
                  ...personal,
                  // 企业三证
                  ...companyInfo,
                  // 企业基本信息
                  ...companyBaseInfo,
                  // 联系人信息
                  ...contactInfoList,
                  // 银行信息
                  ...bankInfoList,
                  // 地址信息
                  ...vendorSiteInfoList,
                  // 厂房信息
                  ...factoryInfoList,
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
                  fileUploadsList: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('vendorMod.sceneAttachmentInfo2')
                    },
                    'x-visible': expression(`$form.query('state').get('data').overseasRelation !== 'PERSONAL'`),
                    'x-query-engine-skip': true,
                    properties: {
                      fileUploads: {
                        'x-query-engine-relation': 'fileUploads:*',
                        type: 'array',
                        'x-component': 'FileDynamic',
                        'x-component-props': {
                          'scene-module-code': 'SCENE_SUPPLIER_ATTACHMENT',
                          'businessId': expression('$form.values.companyId || null'),
                          'editable': expression(`!$form.query('state').get('data').$disabled`),
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
        // 'x-visible': expression(`$form.query('state').get('data').type !== 'registered'`),
        'x-component-props': {
          class: 'contract-progress',
          'ref': 'contractProgress',
          'nodeName': "$t('logisticsMod.contractInfo')",
          data: expression(`$form.query('state').get('data').progressData`),
          percentage: '{{true}}',
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

const $saveBill = async (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any, $t) => {
  let values = JSON.parse( JSON.stringify ( $form.values ) )
  values.rdCapableAdditionals = [{}]
  values.rdCapableAdditionals[0].ifTechnicalStandard = values.ifTechnicalStandard
  values.rdCapableAdditionals[0].productsTechnicalStandard = values.productsTechnicalStandard
  values.rdCapableAdditionals[0].memo = values.memo
  // 营业期限更改
  const businessDate = $form.query('.businessDate').take()?.value
  if (businessDate && businessDate?.length > 0) {
    values.businessStartDate = businessDate[0]
    values.businessEndDate = businessDate[1]
  }
  if (values.overseasRelation2) { // 个人
    values.overseasRelation = values.overseasRelation2
  }

  // 营业地址组件修改后适配后端接口
  // let address = $form.query('.address').take()?.value
  // if (address && address !== '') {
  //   values.companyCountry = address[0]
  //   values.companyProvince = address[1]
  //   values.companyCity = address[2]
  // }
  if ($form.query('state').get('data')?.deleAttr[0] && $form.query('state').get('data').overseasRelation !== 'PERSONAL') {
    values.cateJournalList = [...values.cateJournalList,...$form.query('state').get('data')?.deleAttr[0]] // 主营品类删除的内容
  }
  if ($form.query('state').get('data')?.deleFileUploads && $form.query('state').get('data').overseasRelation !== 'PERSONAL') {
    values.fileUploads?.forEach(e => {
      delete e.sceneFileId
    })
    values.fileUploads = [...values.fileUploads,...$form.query('state').get('data')?.deleFileUploads] // 附件信息删除的内容
  }
  // 校验联系人中默认联系人是否唯一
  // if (values.contactInfos.length > 1) {
  //   let num = 0 // 默认联系人数量
  //   values.contactInfos.forEach((e, index) => {
  //     if (e.ceeaDefaultContact == 'Y') {
  //       num++
  //     }
  //   })
  //   if (num > 1) {
  //     app.$message.error($t('dataConfMod.isDefaultMsg'))
  //     return false
  //   }
  // }

  if (type == 'submit') { // 提交时校验
    let validate = 0
    await $form.validate().then().catch(eq => {
      app.$message.error(eq[0].messages[0])
      validate = 1
    })
    if (validate) {
      return false
    }
    // 校验联系人信息
    // if (values.contactInfos.length === 0) {
    //   app.$message.warning($t('dashboard.addContactInformation'))
    //   return
    // }
    // 需要校验数据
    // const contactInfosRequiredKeys = [
    //   { key: 'contactName', message: '第$index行缺少姓名' },
    //   { key: 'ceeaContactMethod', message: '第$index行缺少联系方式' },
    //   { key: 'email', message: '第$index行缺少邮箱' }
    // ]
    // for (const [index, item] of new Map(values.contactInfos.map((item, index) => [index, item]))) {
    //   const errorItem = contactInfosRequiredKeys.find(keyItem => !item[keyItem.key])
    //   if (errorItem) {
    //     // 替换提示行字符
    //     app.$message.warning(`联系人信息${errorItem.message.replace('$index', index + 1)}`)
    //     return
    //   }
    // }
    // 个人的时候不校验
    if ($form.query('state').get('data').overseasRelation !== 'PERSONAL') {
      // 校验厂房信息
      if (values.plantInfos?.length == 0) {
        app.$message({
          message: $t('vendorMod.msgAtLeastPlantInfos'),
          type: 'error'
        })
        return false
      } else {
        let bol = false
        values.plantInfos?.forEach((e) => {
          if (!e.plantName || !e.plantNature || !e.plantArea || !e.plantCountry) {
            bol = true
          }
        })
        if (bol) {
          app.$message({
            message: $t('vendorMod.msgAtLeastPlantInfos2'),
            type: 'error'
          })
          return false
        }
      }

      // 校验管理体系信息
      let bolManage = false
      values.managementAttaches.forEach((e) => {
        if (e.documentInspection && !e.fileuploadId) {
          bolManage = e.documentInspection
        }
      })
      if (bolManage) {
        app.$message.error($t('common.pleaseUpload') + bolManage + $t('components.workedProcess.headers.attachment'))
        return false
      }
    }

    // 校验银行信息
    if (values.bankInfos?.length === 0) {
      app.$message.warning($t('dataConfMod.addBankInfo'))
      return
    }
    // 需要校验数据
    const bankInfosRequiredKeys = [
      { key: 'bankCode', message: $t('cusEntry.supplement20250211.bankCodeTip') }, // 缺少银行代码
      { key: 'bankAccountName', message: $t('cusEntry.supplement20250211.bankAccountNameTip') }, // 缺少账号名称
      { key: 'bankAccount', message: $t('cusEntry.supplement20250211.bankAccountTip') }, // 缺少银行账号
      { key: 'currencyCode', message: $t('cusEntry.supplement20250211.currencyCodeTip') } // 缺少币种
    ]
    for (const [index, item] of new Map(values.bankInfos.map((item, index) => [index, item]))) {
      const errorItem = bankInfosRequiredKeys.find(keyItem => !item[keyItem.key])
      if (errorItem) {
        // 替换提示行字符
        const tip = $t('vendorMod.bankInfo') + $t('bidMod.warningMessage', { index: index + 1 , message: errorItem.message })
        app.$message.warning(tip)
        return
      }
    }


  }
  if (values.supplierType == '') {
    values.supplierType = null
  }
  if (values.companyName2 || values.companyShortName2) {
    values.companyName = values.companyName2
    values.companyShortName = values.companyShortName2
  }

  const companyId = app.$store.getters.userInfo.companyId || null
  const status = $form.query('.status').take().value
  if (type == 'staging') { // 暂存的时候
    if ([null, undefined, 'DRAFT'].includes(status)) { // 新增或者编辑的时候
      values.status = 'DRAFT'
      $queryEngine.request.save(values, { query: query, tree: true, action: 'vendorSave' }).then((res) => {
        $message.success($t('common.successSave'))
        console.log(res, 'res')
        if (!app.$store.getters.userInfo.companyId) {
          let userInfo = app.$store.getters.userInfo
          userInfo.companyId = res[0].companyId
          app.$store.commit('user/SET_USER_INFO', userInfo)
          app.$store.commit('user/SET_COMPANYID', res[0].companyId)
        }
        // location.reload()
        $queryEngine.request.baseRequest(
          {
            action: 'vendorRead'
          }
        )
      }).catch( err => {
        console.log(err)
      })
    } else { // 其他状态的暂存基本是已发版状态
      $queryEngine.request.save(values, { query: query, action: 'vendorSave' }).then((res) => {
        $message.success($t('common.successSave'))
        if (!app.$store.getters.userInfo.companyId) {
          let userInfo = app.$store.getters.userInfo
          userInfo.companyId = res[0].companyId
          app.$store.commit('user/SET_USER_INFO', userInfo)
          app.$store.commit('user/SET_COMPANYID', res[0].companyId)
        }
        $queryEngine.request.baseRequest(
          {
            action: 'vendorRead'
          }
        )
      }).catch( err => {
        console.log(err)
      })
    }
  } else if (type == 'recall') {
    app.$prompt('', $t('bidMod.withdrawReason'), {
      confirmButtonText: $t('common.confirm'),
      cancelButtonText: $t('components.common.cancel'),
      inputType: 'textarea'
    }).then(({ value }) => {
      let obj = {
        companyId: companyId,
        flowRemark: value
      }
      $queryEngine.request.save(obj, { query: query, action: 'vendorWithdraw' }).then((res) => {
        app.$message({
          message: $t('dashboard.withdrawSuccess'),
          type: 'success'
        })
        $form.query('.fileUploads').take(field => {
          field.visible = false
        })
        $queryEngine.request.baseRequest(
          {
            action: 'vendorRead'
          }
        )
      })
    })
  } else { // 提交
    // 如果是供应商的时候默认是潜在供应商
    if (app.$store.getters.userInfo != 'BUYER') {
      values.potentialFlag = 'Y'
    }
    $queryEngine.request.save(values, { query: query, tree: true, action: 'vendorSubmit' }).then((res) => {
      if (!app.$store.getters.userInfo.companyId) {
        let userInfo = app.$store.getters.userInfo
        userInfo.companyId = res[0].companyId
        app.$store.commit('user/SET_USER_INFO', userInfo)
        app.$store.commit('user/SET_COMPANYID', res[0].companyId)
      }
      $form.values.companyId = res[0].companyId
      app.$emit('saveAll')
    })
  }
}

const personProgressData = [
  {
    code: 'userInfoForm',
    name: t('vendorMod.vendorUserInfo'),
    percentage: 0
  },
  {
    code: 'companyInfo',
    name: t('vendorMod.enterpriseThreeCertificates'),
    percentage: 0
  },
  {
    code: 'contactInfoList',
    name: t('vendorMod.contactInfo'),
    percentage: 0
  },
  {
    code: 'bankInfoList',
    name: t('vendorMod.bankInfo'),
    percentage: 0
  },
  {
    code: 'financeInfo',
    name: t('vendorMod.financeInfo'),
    percentage: 0
  },
  {
    code: 'vendorSiteInfo',
    name: t('vendorMod.vendorSiteInfos'),
    percentage: 0
  }
]

const scope = {
  app,
  t,
  $props: props,
  $attrs: attrs,
  performPlanService,
  $disabled,
  emitTabRemove,
  $saveBill,
  DictSelect,
  observer,
  $managementChange,
  query,
  validEmail,
  validatePhone,
  personProgressData,
  $ifPersonal
}
const components = {
  SrmCommonFile,
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  newAddress,
  MainHerder
}
</script>

<template>
  <!--  :readOnly="$disabled"-->
  <RenderEngine
    schemaKey="companyInfoMain"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style>
.registered{
  /*height: calc(100vh - 150px);*/
  /*width:100%*/
}
.companyInfos .companyInfo,.registered .companyInfo {
  display: flex;
  padding: 16px;
  width: 100%;
}
.companyInfos .render-form-container__fixed-footer,.registered .render-form-container__fixed-footer{
  padding-top:0px
}
.info-fill-progress{
  position: fixed;
  width: 210px;
  top: 64px;
  right: 0px;
  bottom: 0px;
}
.companyInfos {
  overflow: auto;
  padding-right: 181px;
}
.order-form-contain .contract-progress{
  top: 64px
}
.registered .contract-progress{
  /*position: sticky!important;*/
  width: 13%;
  top:28%;
  right:5%;
}
@media screen and (max-width: 1500px) {
  .registered .contract-progress {
    width: 13%;
    top:28%;
    right:2%;
  }
}
.rdCapableList{
  height: 80px;
  margin-bottom: 20px;
}
.companyInfos .render-pix-form-item-feedback-layout-loose.render-pix-form-item-feedback-has-text:not(.render-pix-form-item-inset) {
  margin-bottom: var(--mb-md)
}
</style>
