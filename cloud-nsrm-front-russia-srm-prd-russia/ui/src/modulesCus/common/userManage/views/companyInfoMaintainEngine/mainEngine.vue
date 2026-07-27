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
import CFillProgress from 'lib@/components/c-fill-progress'
import MainHerder from './mainHeater.vue'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import { companyType } from './components/companyType'
import { companyInfo } from './components/companyInfo'
import { companyBaseInfo } from './components/companyBaseInfo'
import { contactInfoList } from './components/contactInfoList'
import { bankInfoList } from './components/bankInfoList'
import { factoryInfoList } from './components/factoryInfoList'
import { operatingPerformancesList } from './components/operatingPerformances'
import { overallStrengths } from './components/overallStrengths'
import { companySizesList } from './components/companySizes'
import { financeInfoList } from './components/financeInfoList'
import { serviceRange } from './components/serviceRange'
import { qualificationInformation } from './components/qualificationInformation'
import { authInfo } from './components/authInfo'
import { personBaseInfo } from './components/personBaseInfo'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
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
  bankInfos: { '*': {} },
  contactInfos: { '*': {} },
  orgCategorys: { '*': {} },
  orgInfos: { '*': {} },
  operationInfo: { '*': {} },
  fileUploads: { '*': {} },
  supplierLeaderList: { '*': {} },
  siteInfos: { '*': {} },
  npmCompanySizes: { '*': {} },
  managementAttaches: { '*': {} },
  cateJournalList: { '*': {}, npmSerciceCustoms: { '*': {} } },
  npmFinanceReports: { '*': {} },
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
      companyId: app.$store.getters.userInfo.companyId || null,
      $disabled: false,
      serciceCustomDelList: [],
      overseasRelation: '',
      status: '',
      deleAttr: [], // 主营品类删除的信息
      deleFileUploads: [], // 附件删除的信息
      type: '' // 是否在注册中来的
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
          ready: expression(`() => {
            // const params = {
            //   pageSize: 15,
            //   pageNum: 1,
            //   sceneCode: 'SCENE_COMPANY_SUNSHINE_FILE',
            //   sceneModuleCode: 'SCENE_COMPANY_SUNSHINE_FILE_ATTACHMENT'
            // }
            // sceneFileApi.listPage(params).then(res => {
            //   const {
            //     attachmentName,
            //     templateFileId
            //   } = res.data?.list?.[0]
            //   $form.values.protocolTemplateName = attachmentName
            //   $form.values.protocolTemplateId = templateFileId
            // })
            setTimeout(() => {
             $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
             $form.query('state').get('data').type = $props.type
             $form.query('.overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
             $form.query('.companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = [
              {
                position: 'SALES_MANAGER'
              },
              {
                position: 'SENIOR_LEADER'
              },
            ]
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

            $form.setValues(data)
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
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
              $form.query('overseasRelation').take().value = $props.formCompanyNature.value?.overseasRelation
              $form.query('state').get('data').overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // $form.query('companyType').take().value = $props.formCompanyNature.value?.companyType
            })
            if(!app.$store.getters.userInfo.companyId && !$buyer()){
              $form.query('fileUploads').take(field => {
                field.visible = true
              })
            }
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            $form.values.contactInfos = $props.formCompanyNature.value?.overseasRelation !== 'PERSONAL' ? [
              {
                position: 'SALES_MANAGER'
              },
              {
                position: 'SENIOR_LEADER'
              }
            ] : [{ position: 'SALES_MANAGER' }]
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
            $form.query('companySizes').take().value = data.npmCompanySizes || []
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
            $form.query('serviceRangeList').take().value = serviceRange
            $form.query('qualificationInfo').take().value = data.managementAttaches
            if (!$props.formCompanyNature.value?.overseasRelation) {
              $form.query('state').get('data').overseasRelation = data.overseasRelation
              const overseasRelation = state.overseasRelation
            }
            if ($props.formCompanyNature.value?.overseasRelation) {
              data.overseasRelation = $props.formCompanyNature.value?.overseasRelation
              // data.companyType = $props.formCompanyNature.value?.companyType
            }
            if (data.overseasRelation !== 'OUT') {
              data.bankInfos = data.bankInfos.length ? data.bankInfos : [{}]
            }
            $form.setValues({
              ...data
            })
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
            if (data.businessStartDate) {
              $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            }
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
                extSex,
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate
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
                extSex,
                businessScope,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress
              }
            }
            setTimeout(() => {
              $form.query('fileUploads').take(field => {
                field.visible = true
                field.componentProps.componentInstance.reLoadFileInfo()
              })
            }, 1000)
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
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
                '["", "DRAFT", "WITHDRAW"].includes($form.query("state").get("data").status)',
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
                '["", "DRAFT", "WITHDRAW", "REJECTED"].includes($form.query("state").get("data").status)',
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
              // 银行信息
              ...bankInfoList,
              // 公司规模
              ...companySizesList,
              // 财务报表
              ...financeInfoList,
              // 服务范围
              ...serviceRange,
              // 资质信息
              ...qualificationInformation,
              // 认证协议
              // ...authInfo,
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
                'x-visible': expression(
                  '$form.query("state").get("data").overseasRelation !== "PERSONAL"',
                ),
                'x-query-engine-skip': true,
                properties: {
                  fileUploads: {
                    'x-query-engine-relation': 'fileUploads:*',
                    type: 'array',
                    'x-component': 'FileDynamic',
                    'x-component-props': {
                      'scene-module-code': 'SCENE_SUPPLIER_ATTACHMENT',
                      businessId: expression('$form.values.companyId || null'),
                      editable: expression('!$form.query("state").get("data").$disabled'),
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
          data: expression('$nodeList($form.query("state").get("data").overseasRelation)'),
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
  /* 获取公司规模信息 */
  values.npmCompanySizes = $form.query('companySizes').get('value')
  /* 获取客户信息删除标识 */
  const serciceCustomDelList = $form.query('state').get('data').serciceCustomDelList || []
  /* 获取服务范围信息 */
  let serviceRange = $form
    .query('serviceRangeList')
    .get('value')
    .map(item => {
      const { list, tableForm } = item
      return {
        ...tableForm,
        npmSerciceCustoms: [...list, ...serciceCustomDelList]
      }
    })
  values.cateJournalList = serviceRange
  /* 获取资质信息 */
  values.managementAttaches = $form.query('qualificationInfo').get('value')
  // 营业期限更改
  const businessDate = $form.query('.businessDate').take()?.value
  if (businessDate && businessDate?.length > 0) {
    values.businessStartDate = businessDate[0]
    values.businessEndDate = businessDate[1]
  }
  // 营业地址组件修改后适配后端接口
  // let address = $form.query('.address').take()?.value
  // if (address && address !== '') {
  //   values.companyCountry = address[0]
  //   values.companyProvince = address[1]
  //   values.companyCity = address[2]
  // }

  if (
    $form.query('state').get('data')?.deleFileUploads &&
    $form.query('state').get('data').overseasRelation !== 'PERSONAL'
  ) {
    values.fileUploads.forEach(e => {
      delete e.sceneFileId
    })
    values.fileUploads = [
      ...values.fileUploads,
      ...$form.query('state').get('data')?.deleFileUploads
    ] // 附件信息删除的内容
  }
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
    let validFlag = true
    let validContact = true
    let overseasRelation = $form.query('state').get('data').overseasRelation
    let positionList = []
    let companyNameFlag = false
    let ceeaEnabled = ''
    let str = ''
    // 需要校验数据
    let bankInfosRequiredKeys = [
      { key: 'bankCode', message: '第$index行缺少银行代码' },
      { key: 'bankAccountName', message: '第$index行缺少账号名称' },
      { key: 'bankAccount', message: '第$index行缺少银行账号' },
      { key: 'currencyCode', message: '第$index行缺少币种' }
    ]
    !values.registCurrency && overseasRelation !== 'PERSONAL' && (str += $t('vendorMod.msgCurrencyCode') + '\n')
    if (overseasRelation !== 'PERSONAL') {
      /* 校验注册币种 */
      // if (!values.registCurrency) {
      //   app.$message.warning($t('cusEntry.tipMessage.registCurrencyMsg'))
      //   return false
      // }y
      if (overseasRelation == 'INSIDE') {
        values.contactInfos.some(item => {
          if (item.ceeaDefaultContact === 'Y' && !item.socialSecurityCertificateFileId) {
            validContact = false
            return true
          }
        })
        /* 校验联系人中至少有高层领导和销售经理职位 */
        positionList = values.contactInfos
          .filter(item => !!item.position)
          .map(itm => itm.position)
      }
    }
    serviceRange.some(item => {
      if (!item.categoryId) {
        validFlag = false
        return true
      }
    })
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
    if ($form.query('state').get('data').overseasRelation !== 'OUT') {
      /* 校验银行主账号账户名称和企业名称一致 */
      const { companyName, bankInfos } = { ...values, ...personBaseInfo }
      const mainAccountRow = bankInfos.find(item => item.ceeaMainAccount === 'Y')
      const mainAccountBankAccountName = mainAccountRow?.bankAccountName
      companyNameFlag = companyName !== mainAccountBankAccountName
      /* 校验银行主账号必须启用 */
      ceeaEnabled = mainAccountRow?.ceeaEnabled
      for (const [index, item] of new Map(values.bankInfos.map((item, index) => [index, item]))) {
        let errorItem = bankInfosRequiredKeys.find(keyItem => !item[keyItem.key])
        if (errorItem) {
          // 替换提示行字符
          str += (`银行信息${errorItem.message.replace('$index', index + 1)}`) + '\n'
        }
      }
    }
    /* 境内供应商校验联系人中至少有高层领导和销售经理职位 */
    overseasRelation == 'INSIDE' && !(positionList.includes('SALES_MANAGER') && positionList.includes('SENIOR_LEADER')) && (str += $t('cusEntry.tipMessage.atLeastManageAndLeader') + '\n')
    if (validate || !validFlag || serviceRange.length === 0 ||
    values.contactInfos.length === 0 || !validContact || values.bankInfos?.length === 0 || companyNameFlag || ceeaEnabled !== 'Y') {
      !validFlag && (str += $t('cusEntry.tipMessage.serviceRangeCategoryRequired') + '\n')
      /* 校验服务范围品类不能为空 */
      serviceRange.length === 0 && (str += $t('cusEntry.tipMessage.atLeastCategory') + '\n')
      // 校验联系人信息
      values.contactInfos.length === 0 && (str += $t('dashboard.addContactInformation') + '\n')
      /* 默认联系人社保证明必填 */
      overseasRelation == 'INSIDE' && !validContact && (str += $t('cusEntry.tipMessage.socialSecurityCertificateMsg') + '\n')

      if (overseasRelation != 'OUT') {
        // 校验银行信息
        values.bankInfos?.length === 0 && (str += '请添加银行信息')
        /* 校验银行主账号账户名称和企业名称一致 */
        companyNameFlag && (str += $t('cusEntry.tipMessage.companyAndBankAccount') + '\n')
        /* 校验银行主账号必须启用 */
        ceeaEnabled !== 'Y' && (str += $t('cusEntry.tipMessage.ceeaEnabled') + '\n')
      }
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
      .$prompt('', '撤回原因', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea'
      })
      .then(({ value }) => {
        let obj = {
          companyId: companyId,
          flowRemark: value
        }
        $queryEngine.request.save(obj, { query: query, action: 'vendorWithdraw' }).then(async res => {
          app.$message({
            message: '成功撤回',
            type: 'success'
          })
          $form.query('.fileUploads').take(field => {
            field.visible = false
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
const $nodeList = userType => {
  const company = [
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
      code: 'bankInfoList',
      name: t('vendorMod.bankInfo'),
      percentage: 0
    },
    {
      code: 'companySizesList',
      name: t('vendorMod.companySize'),
      percentage: 0
    },
    {
      code: 'financeInfo',
      name: t('cusEntry.vendorMod.financeReport'),
      percentage: 0
    },
    {
      code: 'serviceRange',
      name: t('cusEntry.vendorMod.serviceRange'),
      percentage: 0
    },
    {
      code: 'qualificationInformation',
      name: t('cusEntry.vendorMod.qualificationInformation'),
      percentage: 0
    },
    // {
    //   code: 'authInfo',
    //   name: t('cusEntry.vendorMod.authInfo'),
    //   percentage: 0
    // },
    {
      code: 'fileUploadsList',
      name: t('vendorMod.sceneAttachmentInfo2'),
      percentage: 0
    }
  ]
  const person = [
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
      code: 'bankInfoList',
      name: t('vendorMod.bankInfo'),
      percentage: 0
    },
    {
      code: 'serviceRange',
      name: t('cusEntry.vendorMod.serviceRange'),
      percentage: 0
    }
    // {
    //   code: 'authInfo',
    //   name: t('cusEntry.vendorMod.authInfo'),
    //   percentage: 0
    // }
  ]
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
  $monitorIpAddress
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
