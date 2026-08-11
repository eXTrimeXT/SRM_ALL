<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  methodExpression,
  i18nExpression,
  observer,
  useAutoMountInstanceToField,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
import { validEmail, validatePhone } from '@/utils/validate'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import CFillProgress from 'lib@/components/c-fill-progress'
import MainHerder from './mainHeater.vue'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import { userInfoForm } from './components/userInfoForm'
import { companyType } from './components/companyType'
import { companyInfo } from './components/companyInfo'
import { companyBaseInfo } from './components/companyBaseInfo'
import { contactInfoList } from './components/contactInfoList'
import { serviceRange } from './components/serviceRange'
import { authInfo } from './components/authInfo'
import { personBaseInfo } from './components/personBaseInfo'
import { vendorSiteInfoList } from './components/vendorSiteInfos'
import { qualificationInformation } from './components/qualificationInformation'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
import { DictClass, createDictClass } from '@/library/utils/dict/dict-utils'
import Note from './components/Note'
const props = defineProps({
  formCompanyNature: {
    type: Object,
    default: () => ({})
  },
  type: {
    type: String,
    default: () => ''
  }
})

const { app, emitTabRemove, t, vendor, http } = usePageHelper()

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
  '*': {},
  userInfo: { '*': {} },
  contactInfos: { '*': {} },
  orgCategorys: { '*': {} },
  orgInfos: { '*': {} },
  operationInfo: { '*': {} },
  supplierLeaderList: { '*': {} },
  companyAddressInfos: { '*': {} },
  cateJournalList: { '*': {}, npmSerciceCustoms: { '*': {} } },
  qualificationInfo: { '*': {} },
  fileUploads: { '*': {} },
  npmCompanyExceptionInfos: { '*': {} }
}

const $managementChange = (value, name, $form) => {
  try {
    if (value) {
      let data = $form.query('.managementAttaches').take().value
      if (value == 'Y') {
        let bold = 1
        data.forEach(e => {
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
    console.log(e)
  }
}
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      dataFrom: 'buyer',
      userInfoObj: {},
      companyId: app.$store.getters.userInfo.companyId || null,
      $disabled: false,
      serciceCustomDelList: [],
      overseasRelation: '',
      status: '',
      deleAttr: [], // 主营品类删除的信息
      type: '', // 是否在注册中来的
      supplementAble: false, // 是否显示补充资质信息按钮
      deleFileUploads: [] // 附件删除的信息
    }
  },
  CompanyInfo: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: expression(
        '$form.query("state").get("data").type !== "registered" ? "flex-container companyInfos" : "flex-container registered"',
      ),
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          loading: true,
          ready: expression(`async () => {
            if (!app.$store.getters.userInfo.companyId) {
              const url = '/api-rbac/extUser/getByUserIdForVendor?id=' + app.$store.getters?.user?.userInfo.userId
              const res = await app.$http({
                url,
                method: 'GET',
                loading: true
              })
              $form.query(\'state\').get(\'data\').dataFrom = 'vendor'
              $form.query(\'state\').get(\'data\').userInfoObj = {
                username: res.data.username,
                phone: res.data.phone,
                email: res.data.email
              }
            }
            
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('state').get('data').type = $props.type
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
              if (!app.$store.getters.userInfo.companyId) {
                $form.query('.userInfo.username').take().value = $form.query(\'state\').get(\'data\').userInfoObj?.username
                $form.query('.userInfo.phone').take().value = $form.query(\'state\').get(\'data\').userInfoObj?.phone
                $form.query('.userInfo.email').take().value = $form.query(\'state\').get(\'data\').userInfoObj?.email
              }
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [{}]
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
              if (data.dataSources == 'MANUALLY_CREATE' && data.ifSupplierCompleteInfo != 'Y') {
                $form.query('state').get('data').$disabled = false
              } else {
                $form.query('state').get('data').$disabled = true
              }
              // 资质附件审批状态=拟定、驳回时显示补充资质附件信息按钮
              if ((!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) && !!data.companyCode) {
                $form.query('state').get('data').supplementAble = true
              }
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
                $form.query('.companyTypeAll').take().visible = false
                $form.query('.companyInfo').take().visible = false
                $form.query('.companyBaseInfo').take().visible = false

                data.companyName2 = data.companyName
              } else {
                $form.query('.companyTypeAll').take().visible = true
                $form.query('.companyInfo').take().visible = true
                $form.query('.companyBaseInfo').take().visible = true
              }
            }
            
            // 附件要删除的列表
            let curFile = data.fileUploads || []
            $form.query('state').get('data').deleFileUploads = curFile.map(item => item.sceneFileId)
            if ($form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE') {
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)
            }
            data.userInfo.lcCode = data.lcCode
            data.userInfo.accountGroup = data.accountGroup
            data.nickname = data.userInfo.nickname
            $form.setValues(data)
            if (!data.qualificationInfo || !data.qualificationInfo.length) {
              $initQualificationInfo($form)
            }
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            return data
          }`)
        },
        vendorRead: {
          immediate: true,
          loading: true,
          method: 'read',
          ready: expression(`() => {
            setTimeout(() => {
              $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [{}]
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
              if (data.dataSources == 'MANUALLY_CREATE' && data.ifSupplierCompleteInfo != 'Y') {
                $form.query('state').get('data').$disabled = false
              } else {
                $form.query('state').get('data').$disabled = true
              }
              // 资质附件审批状态=拟定、驳回时显示补充资质附件信息按钮
              if ((!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) && !!data.companyCode) {
                $form.query('state').get('data').supplementAble = true
              }
            } else {
              $form.query('state').get('data').$disabled = false
            }
            const state = $form.query('state').get('data')
            state.status = status
            const mainHerder = $form.query('.mainHerder').take()
            if (status) {
              mainHerder.componentProps.status = status
              mainHerder.componentProps.flowRemark = data.flowRemark
              if (status == 'SUBMITTED') {
                mainHerder.componentProps.stepsActive = 4
              }
              if (status == 'APPROVED') {
                mainHerder.componentProps.stepsActive = 6
                if (!data.extIsQualifiedStatus || ['DRAFT'].includes(data.extIsQualifiedStatus)) {
                  if (!!data.extRejectQualificationReason) {
                    mainHerder.componentProps.status = 'REJECTED'
                    mainHerder.componentProps.fileStatus = 'REJECTED'
                    mainHerder.componentProps.flowRemark = data.extRejectQualificationReason
                  }
                } else if (['APPROVED'].includes(data.extIsQualifiedStatus)) {
                  mainHerder.componentProps.fileStatus = status
                } else {
                  mainHerder.componentProps.status = 'SUBMITTED'
                  mainHerder.componentProps.fileStatus = 'SUBMITTED'
                }
              }
            }
            const serviceRange = data.cateJournalList.map(item => {
              const {
                npmSerciceCustoms,
                ...form
              } = item
              return {
                list: npmSerciceCustoms,
                tableForm: form
              }
            })
            data.serviceRangeList = serviceRange
            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // data.companyType = $props.formCompanyNature.value?.companyType
            }
            data.userInfo.lcCode = data.lcCode
            data.userInfo.accountGroup = data.accountGroup
            data.nickname = data.userInfo.nickname

            $form.setValues({
              ...data
            })
            // 附件要删除的列表
            let curFile = data.fileUploads || []
            $form.query('state').get('data').deleFileUploads = curFile.map(item => item.sceneFileId)
            if ($form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE') {
              setTimeout(() => {
                $form.query('fileUploads').take(field => {
                  field.componentProps.componentInstance.reLoadFileInfo()
                })
              }, 5000)
            }
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            if (state.overseasRelation === 'PERSONAL') {
              /* 获取个人信息, 后面优化个人信息赋值 */
              const {
                companyName,
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate,
                lcCode,
                enterpriseNo
              } = data
              $form.values.personBaseInfo = { 
                companyName, 
                companyShortName,
                businessLicense,
                businessLicenseFileId,
                extIdCardOppositeFileName,
                extIdCardOppositeFileId,
                idNumber,
                validityPeriodOfCard: businessStartDate ? [businessStartDate, businessEndDate] : [],
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                lcCode,
                enterpriseNo
              }
            }
            if (!data.qualificationInfo || !data.qualificationInfo.length) {
              $initQualificationInfo($form)
            }
          }`)
        },
        vendorSave: {
          method: 'read',
          autoFormatResult: false,
          cascadeDeletion: true,
          loading: true
        },
        vendorWithdraw: {
          autoFormatResult: false,
          loading: true
        },
        vendorSubmit: {
          autoFormatResult: false,
          cascadeDeletion: true,
          loading: true
        }
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
              'x-visible': expression(
                '["", "DRAFT", "WITHDRAW", "REJECTED"].includes($form.query("state").get("data").status)'
              ),
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
              'x-visible': expression(
              `
              ['', 'DRAFT', 'WITHDRAW'].includes($form.query('state').get('data').status) ||
              !$form.query('state').get('data').$disabled
              `,
              ),
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
              'x-visible': expression(
                `
                ['', 'DRAFT', 'WITHDRAW', 'REJECTED'].includes($form.query('state').get('data').status) ||
                !$form.query('state').get('data').$disabled
                `,
              ),
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
              'x-visible': expression(
                '["SUBMITTED"].includes($form.query("state").get("data").status)',
              ),
              'x-component-props': {
                '@click': expression(`async (values) => {
                  $saveBill('recall', $form, $queryEngine, $confirm, $message, $bus, $t)
                }`)
              }
            },
            supplementQualifications: {
              type: 'void',
              'x-content': i18nExpression('cusEntry.vendorMod.supplementQualifications'), // 提交补充资质信息
              'x-component': 'Button',
              'x-visible': expression(`$form.query('state').get('data').supplementAble && $form.values.dataSources != 'MANUALLY_CREATE'`),
              'x-component-props': {
                '@click': expression(`async (values) => {
                  let validate = true
                  await $form.validate().then().catch(eq => {
                    app.$message.error(eq[0].messages[0])
                    validate = false
                  })
                  if (!validate) {
                    return false
                  }
                  const { qualificationInfo, ceeaBusinessModel } = $form.values
                  let valid = qualificationInfo.findIndex(item => item.extIsMandatory == 'Y' && !item.fileuploadId)
                  if (valid > -1) {
                    // 资质信息第index行请上传附件！
                    $message.warning($t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: valid + 1, message: $t('bidMod.pleaseUploadFile')}))
                    return
                  }

                  let bol = false
                  let bolMsg = ''
                  qualificationInfo.some((item, index) => {
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

                  $form.values.ceeaBusinessModel = ceeaBusinessModel?.length ? ceeaBusinessModel.join() : null
                  if ($form.query('state').get('data')?.deleFileUploads) {
                    $form.query('state').get('data').deleFileUploads.forEach(item => {
                      let obj = $form.values.fileUploads.find(e => e.sceneFileId == item)
                      if (!obj) {
                        $form.values.fileUploads.push({ $delete: item })
                      }
                    })
                  }
                  $queryEngine.request.save({ ...$form.values }, { query: query, action: 'vendorQualifiedSubmit' }).then((res) => {
                    $message.success($t('common.success'))
                    location.reload()
                  })
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
              registered: expression('$form.query("state").get("data").type == "registered"') // 判断是否在注册来的
            }
          },
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
              // 个人基本信息
              ...personBaseInfo,
              // 联系人信息
              ...contactInfoList,
              // 服务范围
              ...serviceRange,
              // 地址信息
              ...vendorSiteInfoList,
              // 认证协议
              ...authInfo,
              // 资质信息
              ...qualificationInformation,
              // 相关附件信息
              fileUploadsList: {
                type: 'void',
                'x-component': 'CollapseItem',
                'x-component-props': {
                  title: `{{observer({
                    render(h) {
                      return h($$components.Note, {
                        props: {
                          title: t('vendorMod.sceneAttachmentInfo2'),
                          value: $form.values.extRejectAttribute11,
                          readonly: true
                        }
                      })
                    }
                  })}}`
                },
                'x-visible': generateCharExpressionByFunction(({ $form }) => {
                  return $form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE'
                }),
                'x-query-engine-skip': true,
                properties: {
                  fileUploads: {
                    'x-query-engine-relation': 'fileUploads:*',
                    type: 'array',
                    'x-component': 'FileDynamic',
                    'x-component-props': {
                      'scene-module-code': 'SCENE_SUPPLIER_ATTACHMENT',
                      businessId: expression('$form.query("state").get("data").companyId || null'),
                      editable: expression(`$form.query('state').get('data').supplementAble || ($form.values.dataSources == 'MANUALLY_CREATE' && $form.values.ifSupplierCompleteInfo != 'Y')`),
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
          ref: 'contractProgress',
          nodeName: i18nExpression('logisticsMod.contractInfo'),
          data: expression('$nodeList($form)'),
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
  /* 切换上下步数据丢失 */
  const overseasRelation = $form.values.overseasRelation || $form.query('state').get('data').overseasRelation
  let { personBaseInfo = {}, ...values } = JSON.parse(JSON.stringify($form.values))
  if (personBaseInfo.validityPeriodOfCard) {
    const [businessStartDate, businessEndDate] = personBaseInfo.validityPeriodOfCard
    personBaseInfo.businessStartDate = businessStartDate
    personBaseInfo.businessEndDate = businessEndDate
  }
  values.lcCode = values.userInfo.lcCode
  values.accountGroup = values.userInfo.accountGroup
  values.userInfo.nickname = values.nickname
  
  /* 获取客户信息删除标识 */
  const serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList || []
  /* 获取服务范围信息 */
  let serviceRange = $form.query('serviceRangeList').get('value')
  values.cateJournalList = serviceRange ? serviceRange.map(item => {
      const { list, tableForm } = item
      return {
        ...tableForm,
        npmSerciceCustoms: [...list, ...serciceCustomDelList]
      }
    }) : []
  // 营业地址组件修改后适配后端接口
  // let address = $form.query('.address').take()?.value
  // if (address && address !== '') {
  //   values.companyCountry = address[0]
  //   values.companyProvince = address[1]
  //   values.companyCity = address[2]
  // }

  // 校验联系人中默认联系人是否唯一
  if (values.contactInfos.length > 1) {
    let num = 0 // 默认联系人数量
    values.contactInfos.forEach((e, index) => {
      if (e.ceeaDefaultContact == 'Y') {
        num++
      }
    })
    if (num > 1) {
      app.$message.error($t('dataConfMod.isDefaultMsg'))
      return false
    }
  }
  if ($form.query('state').get('data').supplementAble) {
    values.qualificationInfo = $form.query('qualificationInfo').get('value')
    values.fileUploads = $form.query('fileUploads').take().value
  }
  if (type == 'submit') {
    // 提交时校验
    let validate = true
    await $form
      .validate()
      .then()
      .catch(eq => {
        app.$message.error(eq[0].messages[0])
        validate = false
      })
    if (!validate) {
      return false
    }
    let overseasRelation = $form.query('state').get('data').overseasRelation
    if (overseasRelation !== 'PERSONAL' && !$form.query('.businessLicenseFileId').take().value) {
      app.$message({
        message: $t('vendorMod.msgBusinessLicense'),
        type: 'error'
      })
      return false
    }
    let str = ''
    !values.registCurrency && overseasRelation !== 'PERSONAL' && (str += $t('vendorMod.msgCurrencyCode') + '\n')
    // 校验服务范围品类不能重复
    if (serviceRange) {
      const tableFormList = serviceRange.map(item => item.tableForm)
      const categoryIdList = new Set(tableFormList.map(item => item.categoryId))
      if (tableFormList.length !== categoryIdList.size) {
        let nameRecords = []
        for (let id of categoryIdList) {
          const record = tableFormList.filter(item => item.categoryId === id)
          if (record.length > 1) {
            nameRecords.push(record[0].categoryName)
          }
        }
        $message.warning($t('cusEntry.tipMessage.serviceRangeCategoryRepeat', { name: nameRecords.join(';') }))
        return false
      }
    }
    if (validate || values.contactInfos.length === 0) {
      // 校验联系人信息
      values.contactInfos.length === 0 && (str += $t('dashboard.addContactInformation') + '\n')
    }
    if (str.length) {
      $message.error(str)
      return false
    }
    /* 是否黑名单校验 */
    const { lcCode, idNumber } = { ...values, ...personBaseInfo }
    const res = await integritySystem(lcCode || idNumber)
    if (res.data === integritySystemResultMap.get('forbid')) {
      $message.warning($t('cusEntry.tipMessage.blackForbid'))
      return false
    } else if (res.data === integritySystemResultMap.get('focus')) {
      values.focusFlag = 'Y'
      values.npmCompanyExceptionInfos.push({
        exceptionType: 'FOCUS_FLAG'
      })
    }
    // 来源绿色通道的需要增加资质信息的校验
    if (values.dataSources === 'MANUALLY_CREATE') {
      let valid = values.qualificationInfo.findIndex((item: any) => item.extIsMandatory === 'Y' && !item.fileuploadId)
      if (valid > -1) {
        // 资质信息第index行请上传附件！
        $message.warning($t('cusEntry.vendorMod.qualificationInformation') + $t('bidMod.warningMessage', { index: valid + 1, message: $t('bidMod.pleaseUploadFile')}))
        return
      }
      let bol = false
      let bolMsg = ''
      values.qualificationInfo.some((item: any, index: any) => {
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
    }
  }
  if (values.supplierType == '') {
    values.supplierType = null
  }
  const companyId = app.$store.getters.userInfo.companyId || null
  const status = $form.query('.status').take().value
  values.ceeaBusinessModel = values.ceeaBusinessModel?.length
    ? values.ceeaBusinessModel.join()
    : null
  if (type == 'staging') {
    // 暂存的时候
    if ([null, undefined, 'DRAFT'].includes(status)) {
      // 新增或者编辑的时候
      values.status = 'DRAFT'
      $queryEngine.request.save({ ...values, ...personBaseInfo, overseasRelation }, { query: query, tree: true, action: 'vendorSave' }).then((res) => {
        $message.success($t('common.successSave'))
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
      }).catch(err => {
        console.log(err)
        $form.values.ceeaBusinessModel = values.ceeaBusinessModel ? values.ceeaBusinessModel.split(',') : []
      })
    } else { // 其他状态的暂存基本是已发版状态
      $queryEngine.request.save({ ...values, ...personBaseInfo, overseasRelation }, { query: query, action: 'vendorSave' }).then((res) => {
        $message.success($t('common.successSave'))
        if (!app.$store.getters.userInfo.companyId) {
          let userInfo = app.$store.getters.userInfo
          userInfo.companyId = res[0].companyId
          app.$store.commit('user/SET_USER_INFO', userInfo)
          app.$store.commit('user/SET_COMPANYID', res[0].companyId)
        }
        $queryEngine.request.baseRequest({ action: 'vendorRead' }).catch(err => {
          console.log(err)
          $form.values.ceeaBusinessModel = values.ceeaBusinessModel ? values.ceeaBusinessModel.split(',') : []
        })
      })
    }
  } else if (type == 'recall') {
    app
      .$prompt('', $t('bidMod.withdrawReason'), {
        confirmButtonText: $t('common.confirm'),
        cancelButtonText: $t('components.common.cancel'),
        inputType: 'textarea'
      })
      .then(({ value }) => {
        let obj = {
          companyId: companyId,
          flowRemark: value
        }
        $queryEngine.request.save(obj, { query: query, action: 'vendorWithdraw' }).then(async res => {
          app.$message({
            message: $t('dashboard.withdrawSuccess'),
            type: 'success'
          })
          $queryEngine.request.baseRequest({
            action: 'vendorRead'
          })
        })
      })
  } else {
    // 提交
    // 如果是供应商的时候默认是潜在供应商
    if (app.$store.getters.userInfo != 'BUYER') {
      values.potentialFlag = 'Y'
    }
    $queryEngine.request.save({ ...values, ...personBaseInfo, overseasRelation }, { query: query, tree: true, action: 'vendorSubmit' }).then(async res => {
      if (!app.$store.getters.userInfo.companyId) {
        let userInfo = app.$store.getters.userInfo
        userInfo.companyId = res[0].companyId
        app.$store.commit('user/SET_USER_INFO', userInfo)
        app.$store.commit('user/SET_COMPANYID', res[0].companyId)
      }
      $form.values.companyId = res[0].companyId
      const {
        companyId,
        companyName,
        companyCode
      } = $form.values
      await $monitorIpAddress({
        supplierId: companyId,
        supplierCode: companyCode,
        supplierName: companyName,
        source: source.get('registerSubmit')
      })
      app.$emit('saveAll')
    }).catch(e => {
      $form.values.ceeaBusinessModel = values.ceeaBusinessModel ? values.ceeaBusinessModel.split(',') : []
    })
  }
}
const $nodeList = ($form: any) => {
  let userType = $form.query("state").get("data").overseasRelation
  let nodeList1 = [
    {
      code: 'qualificationInformation',
      name: t('cusEntry.vendorMod.qualificationInformation'),
      percentage: 0
    },
    {
      code: 'fileUploadsList',
      name: t('vendorMod.sceneAttachmentInfo2'),
      percentage: 0
    }
  ]
  let company = [
    {
      code: 'userInfoForm',
      name: t('vendorMod.vendorUserInfo'),
      percentage: 0
    },
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
      name: t('vendorMod.companyBaseInfo'),
      percentage: 0
    },
    {
      code: 'contactInfoList',
      name: t('vendorMod.contactInfo'),
      percentage: 0
    },
    {
      code: 'serviceRange',
      name: t('cusEntry.vendorMod.serviceRange'),
      percentage: 0
    },
    {
      code: 'vendorSiteInfo',
      name: t('vendorMod.vendorSiteInfos2'),
      percentage: 0
    },
    {
      code: 'authInfo',
      name: t('cusEntry.vendorMod.authInfo'),
      percentage: 0
    }
  ]
  let person = [
    {
      code: 'userInfoForm',
      name: t('vendorMod.vendorUserInfo'),
      percentage: 0
    },
    {
      code: 'person',
      name: t('cusEntry.vendorMod.baseInfo'),
      percentage: 0
    },
    {
      code: 'contactInfoList',
      name: t('vendorMod.contactInfo'),
      percentage: 0
    },
    {
      code: 'serviceRange',
      name: t('cusEntry.vendorMod.serviceRange'),
      percentage: 0
    },
    {
      code: 'vendorSiteInfo',
      name: t('vendorMod.vendorSiteInfos2'),
      percentage: 0
    }
  ]
  if ($form.query('state').get('data').supplementAble || $form.values.extIsQualifiedFileUpload == 'Y' || $form.values.dataSources == 'MANUALLY_CREATE') {
    company = company.concat(nodeList1)
    person = person.concat(nodeList1)
  }
  return userType === 'PERSONAL' ? person : company
}
/* 对接阳光诚信系统 */
const integritySystem = lcCode => {
  return http({
    url: '/api-sup/pj/companyInfo/queryIfBlackCompany',
    method: 'POST',
    data: {
      lcCode
    }
  })
}
/* ip地址监控 */
const $monitorIpAddress = data => {
  return http({
    url: '/api-sou/bids/ip/address/ipAddress/save',
    method: 'POST',
    data
  })
}
const integritySystemResultMap = new Map([
  ['forbid', '禁止合作'],
  ['focus', '重点关注']
])
/* 来源 */
const source = new Map([
  ['registerSubmit', '注册提交'],
  ['registerUndo', '注册撤回']
])
const $showSunFile = ($self) => {
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

const $initQualificationInfo = async ($form: any) => {
  let cur = $form.query('state').get('data').overseasRelation
  let dictCode = `CERTIFICATE_TYPE_${cur}`
  let res = DictClass.getDict(dictCode)
  if (!!cur && !res) {
    let dictRes = await http({
      url: '/api-base/dict/base-dict-item/listByDictCode',
      method: 'POST',
      data: [dictCode]
    })
    res = dictRes.data || []
  }
  $form.values.qualificationInfo = !res ? [] : res.map((item: any) => {
    return {
      authNum: item.dictItemCode,
      extIsMandatory: item.itemDescription || 'N'
    }
  })
}

const scope = {
  app,
  http,
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
  $nodeList,
  sceneFileApi,
  integritySystem,
  integritySystemResultMap,
  $showSunFile,
  source,
  $monitorIpAddress,
  $initQualificationInfo,
  $taxDictClass: createDictClass({ 'country': [] })
}
const components = {
  SrmCommonFile,
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  newAddress,
  MainHerder,
  Note
}
</script>

<template>
  <RenderEngine
    schemaKey="companyInfoMain"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style>
.render-pix-form-collapse-errors-badge {
  width: 100%;
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
.registered {
  /*height: calc(100vh - 150px);*/
  /*width:100%*/
}
.companyInfos .companyInfo,
.registered .companyInfo {
  display: flex;
  padding: 16px;
  width: 100%;
}
.companyInfos .render-form-container__fixed-footer,
.registered .render-form-container__fixed-footer {
  padding-top: 0px;
}
.info-fill-progress {
  position: fixed;
  width: 210px;
  top: 64px;
  right: 0px;
  bottom: 0px;
}
.companyInfos {
  overflow: auto;
  padding-right: 181px;
  padding-bottom: 50px;
}
.order-form-contain .contract-progress {
  top: 64px;
}
.registered .contract-progress {
  /*position: sticky!important;*/
  width: 13%;
  top: 28%;
  right: 5%;
}
@media screen and (max-width: 1500px) {
  .registered .contract-progress {
    width: 13%;
    top: 28%;
    right: 2%;
  }
}
.companyInfos
  .render-pix-form-item-feedback-layout-loose.render-pix-form-item-feedback-has-text:not(
    .render-pix-form-item-inset
  ) {
  margin-bottom: var(--mb-md);
}
.el-message--error{
  white-space:pre-line
}
</style>
