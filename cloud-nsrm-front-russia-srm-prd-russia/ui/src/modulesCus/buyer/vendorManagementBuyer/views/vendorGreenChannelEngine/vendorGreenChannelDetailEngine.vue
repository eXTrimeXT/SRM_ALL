<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  observer,
  markRaw,
  useAutoMountInstanceToField,
  generateCharExpressionByFunction
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
import { userInfoForm } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/userInfoForm'
import { companyType } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyType'
import { companyInfo } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyInfo'
import { companyBaseInfo } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyBaseInfo'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import CAddress from 'lib@/components/c-address'
import CCategorySelect from 'lib@/components/c-category-select'
import { contactInfoList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/contactInfoList'
import { bankInfoList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/bankInfoList'
import { financeInfoList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/financeInfoList'
import { companySizesList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companySizes'
import { cooInfoList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/cooInfoList'
import { companyNatureEngine } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/companyNatureEngine'
import { serviceRange } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/serviceRange'
import { qualificationInformation } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/qualificationInformation'
import { authInfo } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/authInfo'
import { personBaseInfo } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/personBaseInfo'
import { FileDynamic } from '@/library/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress'
import { fileUploadsList } from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/fileUploadsList'
import natureChose from 'modcb@/vendorManagementBuyer/views/vendorGreenChannelEngine/components/natureChose'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
const { app, emitTabRemove, t, vendor, http } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

let $disabled = ['view', 'approve'].includes(attrs.params.flag || '')

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

// {
//   a: {
//     'x-component': newAddress
//   }
// }

// $form.query('a').take(field => {
//   console.log(field.componentProps.componentInstance.$refs.address.init())
// })
const $nodeList = userType => {
  const company = [
    {
      code: 'userInfoForm',
      name: t('vendorMod.vendorUserInfo'),
      percentage: 0
    },
    {
      code: 'companyType',
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
      code: 'cooInfo',
      name: t('supRisk.cooInfo'),
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
      code: 'bankInfoList',
      name: t('vendorMod.bankInfo'),
      percentage: 0
    },
    {
      code: 'serviceRange',
      name: t('cusEntry.vendorMod.serviceRange'),
      percentage: 0
    },
    {
      code: 'cooInfo',
      name: t('supRisk.cooInfo'),
      percentage: 0
    },
    {
      code: 'authInfo',
      name: t('cusEntry.vendorMod.authInfo'),
      percentage: 0
    }
  ]
  return userType === 'PERSONAL' ? person : company
}
const customUpdateButton = computed(
  () => !$disabled && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value),
)
const viewUpdateButton = computed(
  () => !$disabled && !['APPROVED', 'SUPPLIER_SUBMITTED'].includes(workflowStatus.value),
)
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))
const query = {
  '*': {},
  userInfo: { '*': {} },
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
  npmFinanceReports: { '*': {} }
}

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    if ($form.values.overseasRelation != 'OUT') {
      $form.values.bankInfos = [{}]
    } else {
      $form.values.bankInfos = []
    }
    let businessType =
          $form.query('state').get('data').overseasRelation === 'PERSONAL'
            ? 'supplierGreenChannelPersonal'
            : 'supplierGreenChannelCompany'
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.cancel.view = false
    componentInstance.buttonConfigInfo.close.view = false
    componentInstance.setWorkflowBusinessType(businessType)
    componentInstance.setWorkflowBusinessId(attrs.params.companyId || '')
    componentInstance.setWorkflowTabDisabled(
      [null, undefined, 'DRAFT', 'WITHDRAW', 'REJECTED'].includes(attrs.params.row?.status) &&
        attrs.params.flag != 'approve',
    )
    componentInstance.setWorkflowBusinessVariables({})
  })
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton.value
    componentInstance.buttonConfigInfo.save.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.submit.disabled = disabledUpdateButton.value
    componentInstance.buttonConfigInfo.close.view = false
    let approveStatus = attrs.params.row?.status || null
    if (approveStatus == 'SUBMITTED' && componentInstance.workflowParamsInfo
      .integrationMode == 'Push') {
      componentInstance.buttonConfigInfo.withdraw.view = true
    }
  }, 50)
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
    console.log(e)
  }
}
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
const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyId: attrs.params.companyId || null,
      $disabled: false,
      deleAttr: [], // 主营品类删除的信息
      deleFileUploads: [], // 附件删除的信息
      activeStep: expression('$attrs.params.activeStep'),
      overseasRelation: '',
      serciceCustomDelList: [],
      activeNavIndex: 0
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
            const activeStep = $form.query('state').get('data').activeStep
            if (activeStep === 'main') {
              initButtonConfig($form)
            }
            $form.values.serviceRangeList = [{
              tableForm: {},
              list: [{}]
            }]
            setTimeout(() => {
              $form.values.contactInfos = $form.values.overseasRelation !== 'PERSONAL' ? [
                {
                  position: 'SALES_MANAGER'
                },
                {
                  position: 'SENIOR_LEADER'
                }
              ] : [{ position: 'SALES_MANAGER' }]
            })
            // setTimeout(() => {
            //   $addScrollEvent($form)
            // }, 1000)
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
            if (data.overseasRelation !== 'OUT') {
              data.bankInfos = data.bankInfos.length ? data.bankInfos : [{}]
            }
            $form.setValues(data)
            $form.values.ceeaBusinessModel = data.ceeaBusinessModel ? data.ceeaBusinessModel.split(',') : []
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
            $form.query('state').get('data').overseasRelation = data.overseasRelation
            if (data.overseasRelation === 'PERSONAL') {
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
            } else {
              if (data.businessStartDate) {
                $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
              }
              $form.query('companySizes').take().value = data.npmCompanySizes || []
              // console.info($form.query('companySizes').take().value)
              $form.query('qualificationInfo').take().value = data.managementAttaches
            }
            const status = data.status
            if (['APPROVED', 'SUBMITTED', 'ABANDONED'].includes(status)) {
              $form.query('state').get('data').$disabled = true
            }
            if ($disabled) {
              $form.query('state').get('data').$disabled = true
            }
            let deleFileUploads = [] // 附件要删除的列表
            data.fileUploads.forEach(e => {
              deleFileUploads.push({$delete:e.sceneFileId})
            })
            $form.query('state').get('data').deleFileUploads = deleFileUploads
            if ($form.query('state').get('data').activeStep === 'main') {
              setTimeout(() => {
                $addScrollEvent($form)
              }, 1000)
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
      ...companyNatureEngine,
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          params: {
            activeWorkflowTab: expression('$attrs.params.flag != "view"')
          },
          'business-id': expression('$attrs.params?.companyId || null'),
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
            updateButtonConfig($form)
          }`)
        },
        'x-visible': generateCharExpressionByFunction(({ $form }) => {
          return $form.query('state').get('data').activeStep === 'main'
        }),
        items: {
          type: 'object',
          'x-query-engine-skip': true,
          properties: {
            preStep: {
              type: 'void',
              'x-visible': expression('!$disabled'),
              'x-content': i18nExpression('common.prevOne'),
              'x-component': 'Button',
              'x-component-props': {
                type: 'primary',
                style: {
                  'margin-right': '10px'
                },
                '@click': expression(`() => {
                  $form.query('state').get('data').activeStep = 'companyNature'
                  
                  app.$nextTick(() => {
                    console.log($form.query('state').get('data').overseasRelation)
                  })
                  setTimeout(() => {
                    $form.values.overseasRelation = $form.query('state').get('data').overseasRelation
                    console.log($form.values.overseasRelation)
                  })
                }`)
              }
            }
          }
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
                  // 个人基本信息
                  ...personBaseInfo,
                  // 企业基本信息
                  ...companyBaseInfo,
                  // 联系人信息
                  ...contactInfoList,
                  // 银行信息
                  ...bankInfoList,
                  // 公司规模
                  ...companySizesList,
                  // 财务信息
                  ...financeInfoList,
                  // 服务范围
                  ...serviceRange,
                  // 合作信息
                  ...cooInfoList,
                  // 资质信息
                  ...qualificationInformation,
                  // 认证协议
                  // ...authInfo,
                  // 相关附件信息
                  // ...fileUploadsList
                  fileUploadsList: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('vendorMod.sceneAttachmentInfo2')
                    },
                    'x-visible': generateCharExpressionByFunction(({ $form }) => {
                      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
                    }),
                    'x-query-engine-skip': true,
                    properties: {
                      fileUploads: {
                        'x-query-engine-relation': 'fileUploads:*',
                        type: 'array',
                        'x-component': 'FileDynamic',
                        'x-component-props': {
                          'scene-module-code': 'SCENE_SUPPLIER_ATTACHMENT',
                          businessId: expression('$attrs.params.companyId || null'),
                          editable: expression('!$disabled'),
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
              activeNavIndex: expression('$form.query(\'state\').get(\'data\').activeNavIndex'),
              class: 'contract-progress',
              ref: 'contractProgress',
              nodeName: '$t(\'logisticsMod.contractInfo\')',
              data: expression('$nodeList($form.query(\'state\').get(\'data\').overseasRelation)'),
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
    }
  }
})

const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('green')
}

const $saveBill = async (
  type: string,
  $form: any,
  $queryEngine: any,
  $confirm: any,
  $message: any,
  $bus: any,
  $t:any
) => {
  if (type == 'WITHDRAW') {
    emitTabRemove(attrs.tabName)
    $bus.$emit('green')
    return
  }
  let { personBaseInfo = {}, ...values } = JSON.parse(JSON.stringify($form.values))
  if (personBaseInfo.validityPeriodOfCard) {
    const [businessStartDate, businessEndDate] = personBaseInfo.validityPeriodOfCard
    personBaseInfo.businessStartDate = businessStartDate
    personBaseInfo.businessEndDate = businessEndDate
  }
  // 营业期限更改
  const businessDate = $form.query('.businessDate').take()?.value
  if (businessDate && businessDate?.length > 0) {
    values.businessStartDate = businessDate[0]
    values.businessEndDate = businessDate[1]
  }
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
  // 绿色通道不需要判断是否第一次登录，默认为N
  values.firstLoginFlag = 'N'
  // 营业期限更改
  // 营业地址组件修改后适配后端接口
  values.greenChannelReason = values.userInfo.greenChannelReason
  // 新增用户名校验必填
  let overseasRelation = $form.query('state').get('data').overseasRelation
  if (overseasRelation !== 'PERSONAL') {
    values.npmCompanySizes = $form.query('companySizes').get('value')
    values.managementAttaches = $form.query('qualificationInfo').get('value')
    values.fileUploads = $form.query('fileUploads').take().value
  }
  if (type != 'SAVE') {
    let validate = 0
    let validContact = true
    let orgBol = false
    let companyNameFlag = false
    let ceeaEnabled = ''
    let positionList = []
    await $form
      .validate()
      .then()
      .catch(eq => {
        app.$message.error(eq[0].messages[0])
        validate = 1
      })

    if (overseasRelation == 'INSIDE') {
      /* 校验注册币种 */
      // if (!values.registCurrency) {
      //   app.$message.warning($t('cusEntry.tipMessage.registCurrencyMsg'))
      //   return false
      // }
      /* 默认联系人社保证明必填 */
      values.contactInfos.some(item => {
        if (item.ceeaDefaultContact == 'Y' && !item.socialSecurityCertificateFileId) {
          validContact = false
          return true
        }
      })
      /* 校验联系人中至少有高层领导和销售经理职位 */
      positionList = values.contactInfos
        .filter(item => !!item.position)
        .map(itm => itm.position)
    }
    // if ($form.query('.orgCategorys').take().value.length <= 0) {
    //   app.$message.error('请输入合作信息')
    //   return false
    // }
    const companyId = attrs.params?.companyId || null
    // const status = $form.query('.status').take().value
    const orgCategorys = $form.query('orgCategorys').take().value
    if (orgCategorys.length > 0) {
      orgCategorys.forEach(data => {
        if (['', null].includes(data.orgId) || ['', null].includes(data.categoryId)) {
          orgBol = true
        }
      })
    }
    if (overseasRelation !== 'OUT') {
      /* 校验银行主账号账户名称和企业名称一致 */
      const { companyName, bankInfos } = { ...values, ...personBaseInfo }
      const mainAccountRow = bankInfos.find(item => item.ceeaMainAccount === 'Y')
      const mainAccountBankAccountName = mainAccountRow?.bankAccountName
      companyNameFlag = companyName !== mainAccountBankAccountName
      /* 校验银行主账号必须启用 */
      ceeaEnabled = mainAccountRow?.ceeaEnabled
    }
    let validFlag = true
    serviceRange.some(item => {
      if (!item.categoryId) {
        validFlag = false
        return true
      }
    })
    let str = ''
    /* 境内供应商校验联系人中至少有高层领导和销售经理职位 */
    overseasRelation == 'INSIDE' && !(positionList.includes('SALES_MANAGER') && positionList.includes('SENIOR_LEADER')) && (str += $t('cusEntry.tipMessage.atLeastManageAndLeader') + '\n')
    if (
      validate ||
      !validContact ||
      orgBol ||
      companyNameFlag ||
      ceeaEnabled !== 'Y' ||
      serviceRange.length === 0 ||
      !validFlag
    ) {
      /* 默认联系人社保证明必填 */
      overseasRelation == 'INSIDE' && !validContact && (str = $t('cusEntry.tipMessage.socialSecurityCertificateMsg') + '\n')
      orgBol && (str += $t('vendorMod.msgOrgCatTableInfo') + '\n')
      /* 校验银行主账号账户名称和企业名称一致 */
      overseasRelation !== 'OUT' && companyNameFlag && (str += $t('cusEntry.tipMessage.companyAndBankAccount') + '\n')
      /* 校验银行主账号必须启用 */
      overseasRelation !== 'OUT' && ceeaEnabled !== 'Y' && (str += $t('cusEntry.tipMessage.ceeaEnabled') + '\n')
      /* 校验服务范围品类不能为空 */
      serviceRange.length === 0 && (str += $t('cusEntry.tipMessage.atLeastCategory') + '\n')
      !validFlag && (str += $t('cusEntry.tipMessage.serviceRangeCategoryRequired') + '\n')
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
      return
    } else if (res.data === integritySystemResultMap.get('focus')) {
      values.focusFlag = 'Y'
      values.npmCompanyExceptionInfos.push({
        exceptionType: 'FOCUS_FLAG'
      })
    }
  }
  values.ceeaBusinessModel = values.ceeaBusinessModel?.length
    ? values.ceeaBusinessModel.join()
    : null
  if (type == 'SAVE') {
    // 暂存的时候
    if ([null, undefined, 'DRAFT'].includes(status)) {
      // 新增或者编辑的时候
      values.status = 'DRAFT'
      $queryEngine.request
        .save({ ...values, ...personBaseInfo }, { query: { '*': {} }, action: 'greenSave' })
        .then(() => {
          $message.success($t('common.successSave'))
          $bus.$emit('green')
          emitTabRemove(attrs.tabName)
        })
        .catch(err => {
          console.log(err)
          $form.values.ceeaBusinessModel = values.ceeaBusinessModel
            ? values.ceeaBusinessModel.split(',')
            : []
        })
    } else {
      // 其他状态的暂存基本是已发版状态
      $queryEngine.request
        .save({ ...values, ...personBaseInfo }, { query: { '*': {} }, action: 'greenSave' })
        .then(() => {
          $message.success($t('common.successSave'))
          $bus.$emit('green')
          emitTabRemove(attrs.tabName)
        })
        .catch(err => {
          console.log(err)
          $form.values.ceeaBusinessModel = values.ceeaBusinessModel
            ? values.ceeaBusinessModel.split(',')
            : []
        })
    }
  } else {
    // 提交
    $queryEngine.request
      .save({ ...values, ...personBaseInfo }, { query: { '*': {} }, action: 'greenSubmit' })
      .then(res => {
        const businessType =
          $form.query('state').get('data').overseasRelation === 'PERSONAL'
            ? 'supplierGreenChannelPersonal'
            : 'supplierGreenChannelCompany'
        $form.query('SchemaWorkflow').take(field => {
          field.componentProps.componentInstance.workflowParamsInfo.businessType = businessType
        })
        const componentInstance = $form.query('.SchemaWorkflow').take()
          .componentProps.componentInstance
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
      .catch(err => {
        console.log(err)
        $form.values.ceeaBusinessModel = values.ceeaBusinessModel
          ? values.ceeaBusinessModel.split(',')
          : []
      })
  }
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
const integritySystemResultMap = new Map([
  ['forbid', '禁止合作'],
  ['focus', '重点关注']
])
/* 添加导航监听器 */
const $addScrollEvent = ($form: any) => {
  /* 获取导航菜单节点 */
  const navNodes = $nodeList($form.query('state').get('data').overseasRelation)
  /* 所有锚点元素的offsetTop */
  const offsetTopArr = []
  navNodes.forEach(node => {
    const element = document.getElementById(`collapse_${node.code}`)
    offsetTopArr.push(element.offsetTop)
  })
  window.addEventListener('scroll', $throttle($scrollHandler, 100, $form, offsetTopArr), true)
}
/* 添加节流函数 */
const $throttle = (fn, delay, $form: any, offsetTopArr) => {
  let timer = null
  return () => {
    if (!timer) {
      setTimeout(() => {
        fn($form, offsetTopArr)
        clearTimeout(timer)
        timer = null
      }, delay)
    }
  }
}
/* 滚动处理函数 */
const $scrollHandler = ($form: any, offsetTopArr) => {
  const scrollTop = document.getElementsByClassName('el-tabs__content')[2]?.scrollTop
  /* 定义当前高亮导航的下标 */
  let navIndex = 0
  const targetIndex = offsetTopArr.findIndex(item => item >= scrollTop)
  for (let n = 0; n < offsetTopArr.length; n++) {
    if (scrollTop >= offsetTopArr[n]) {
      navIndex = n
    }
  }
  if ($form.query('state').get('data')) {
    $form.query('state').get('data').activeNavIndex = navIndex
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
  validatePhone,
  $nodeList,
  sceneFileApi,
  integritySystem,
  integritySystemResultMap,
  $showSunFile,
  $addScrollEvent,
  $throttle,
  $scrollHandler
}
const components = {
  SrmCommonFile,
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  newAddress,
  natureChose
}
</script>

<template>
  <RenderEngine
    schemaKey="vendorGreenChannelDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>

<style>
.bzBox {
  position: relative;
}
.bzTitle {
  position: absolute;
  top: -20px;
  left: 4px;
}
.bzTitle::before{
  content:'*';
  margin-right:4px;
  display: inline-block;
  vertical-align: top;
  color: #FF4A4D;
  font-family: SimSun, sans-serif;
}
.vendorGreen .companyInfo {
  display: flex;
  padding: 16px;
  width: 100%;
}
.vendorGreen .render-form-container__fixed-footer {
  padding-top: 0px;
}
.info-fill-progress {
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
.order-form-contain .contract-progress {
  top: 64px;
}
.rdCapableList {
  height: 80px;
  margin-bottom: 20px;
}
.vendorGreen
  .render-pix-form-item-feedback-layout-loose.render-pix-form-item-feedback-has-text:not(
    .render-pix-form-item-inset
  ) {
  margin-bottom: var(--mb-md);
}
.el-message--error{
  white-space:pre-line
}
</style>
