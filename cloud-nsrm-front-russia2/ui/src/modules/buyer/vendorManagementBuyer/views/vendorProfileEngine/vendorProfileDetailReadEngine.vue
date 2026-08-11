<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  defineSchemas,
  expression,
  i18nExpression,
  observer,
  markRaw,
  useAutoMountInstanceToField, generateXindexInOrder
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import {userInfoForm} from "./components/userInfoForm"
import {companyType} from "./components/companyType"
import {companyInfo} from "./components/companyInfo"
import {companyBaseInfo} from "./components/companyBaseInfo"
import DictSelect from "lib@/components/c-select/dict-select.vue"
import CAddress from 'lib@/components/c-address/index.vue'
import CCategorySelect from 'lib@/components/c-category-select/index.vue'
import {contactInfoList} from "./components/contactInfoList"
import {bankInfoList} from "./components/bankInfoList"
import {cooInfoList} from "./components/cooInfoList"
import {financeInfoList} from "./components/financeInfoList"
import {factoryInfoList} from "./components/factoryInfoList"
import {vendorSiteInfoList} from "./components/vendorSiteInfos"
import {
  operatingPerformancesList
} from "./components/operatingPerformances"
import {overallStrengths} from "./components/overallStrengths"
import {companySizesList} from "./components/companySizes"
import {rdCapableList} from "./components/rdCapable"
import {qualityControlList} from "./components/qualityControlList"
import {
  equipmentInformationList
} from "./components/equipmentInformationList"
import {
  productCapableInfosList
} from "./components/productCapableInfosList"
import {
  ceeaAfterSalesAbility
} from "./components/ceeaAfterSalesAbility"
import {clientStatusList} from "./components/clientStatusList"
import {managementInfoList} from "./components/managementInfo"
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress/index.vue'
import vendorInfoChangeDetail from 'modb@/vendorManagementBuyer/views/vendorInfoChange/vendorInfoChangeDetail'
import {personal} from "./components/personal";

const { app, emitTabRemove, t, vendor, emitTabAdd } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabled = true

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
  "userInfo": {'*': {}},
  "infoChangeList": {'*': {}}
}

const $managementChange = (value, name, $form) => {
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
  $form.query('.questSupplier').take().visible = false
}

const schema = defineSchemas({
  state: {
    type: 'void',
    'x-component': 'Fragment',
    'x-hidden': true,
    'x-data': {
      companyId: attrs.params.companyId || null,
      // 锚点数据
      progressData: [
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
  code: 'financeInfo',
    name: t('vendorMod.financeInfo'),
  percentage: 0
},
{
  code: 'factoryInfo',
    name: t('vendorMod.factoryInfo'),
  percentage: 0
},
{
  code: 'vendorSiteInfo',
    name: t('vendorMod.vendorSiteInfos'),
  percentage: 0
},
{
  code: 'operatingPerformancesList',
    name: t('vendorMod.operatingResult2'),
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
      class: 'flex-container vendorGreen',
      direction: 'vertical'
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        query: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            return $attrs.params && $attrs.params.companyId
          }`),
          method: 'read',
          autoFormatResult: false,
          transformRequest: expression(`(data, headers) => {
            data.tree = true
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
          transformResponse: expression(`(res) => {
            const data = JSON.parse(res).data.records[0]
            const rdCapables = [data.rdCapables[data.rdCapables?.length-1]]
            data.rdCapables = rdCapables
            data.companySizes = [data.companySizes[data.companySizes?.length-2],data.companySizes[data.companySizes?.length-1]]
            data.ifTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.ifTechnicalStandard
            data.productsTechnicalStandard = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.productsTechnicalStandard
            data.memo = data.rdCapableAdditionals[data.rdCapableAdditionals?.length-1]?.memo
            data.companyShortName2 = data?.companyShortName
            data.companyName2 = data?.companyName
            $form.setValues(data)

            $form.query('.businessDate').take().value = [data.businessStartDate, data.businessEndDate]
            let addressList = []
            console.log(data.overseasRelation, 'overseasRelation')
            if (data.overseasRelation == 'PERSONAL') {
                $form.query('state').get('data').progressData = personProgressData
                $ifPersonal($form)

                data.companyName2 = data.companyName
                data.companyShortName2 = data.companyShortName
            }

              $form.query('fileUploads').take(field => {
                field.componentProps.componentInstance.reLoadFileInfo()
              })
              const cateJournalList = $form.query('.cateJournalList').take()?.value
              if (cateJournalList?.length > 0) {
                 const categoryName = cateJournalList.map(v => v.categoryName)?.join(',')
                 $form.query('.categoryName').take().value = categoryName
              }

            console.log($form.query('.businessDate').take().value , 'businessDate')
            return data
          }`)
        }
      }
    },
    properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            properties: {
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                'x-component-props': {
                  defaultOpenPanelCount: 1
                },
                properties: {
                  // 供应商账号信息
                  ...userInfoForm,
                  // 个人
                  ...personal,
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
                  },
                  // 调查表清单
                  questSupplier: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('quest.questSupplierModule')
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      questSupplierList: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          editMode: true,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false
                        },
                        'x-query-engine-skip': true,
                        properties: generateXindexInOrder({
                          // 调查表编号
                          questNo: {
                            type: 'string',
                            title: i18nExpression('quest.questNo'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component': 'TableButton',
                            'x-component-props': {
                              type: 'text',
                              '@click': expression(`({row}) => {
                                let tab = {
                                  component: questManagementDetail,
                                  params: {
                                    flag: 'view',
                                    row: row,
                                    tabName: 'questManagementDetail' + row.questSupId
                                  },
                                  title: this.$t('dashboard.enumerationForm') + row.questNo,
                                  name: 'questManagementDetail' + row.questSupId
                                }
                                emitTabAdd(tab)
                              }`)
                            }
                          },
                          // 调查表名称
                          questName: {
                            type: 'string',
                            title: i18nExpression('quest.questName'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 调查表状态
                          approvalStatus: {
                            type: 'string',
                            title: i18nExpression('quest.approvalStatus'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 供应商编码
                          companyCode: {
                            type: 'string',
                            title: i18nExpression('quest.companyCode'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 供应商名称
                          companyName: {
                            type: 'string',
                            title: i18nExpression('quest.companyName'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 业务组织
                          questTemplateOrgName: {
                            type: 'string',
                            title: i18nExpression('quest.questTemplateOrgName'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 调查模板编码
                          questTemplateCode: {
                            type: 'string',
                            title: i18nExpression('quest.questTemplateCode'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 调查表模板名称
                          questTemplateName: {
                            type: 'string',
                            title: i18nExpression('quest.questTemplateName'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 创建人
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('quest.createdFullName'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 创建时间
                          creationDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`),
                              'disabled': expression(`$disabled`)
                            },
                            title: i18nExpression('quest.creationDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          }
                        })
                      }
                    }
                  },
                  // 供应商信息变更记录
                  infoChange: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('quest.changeRecord')
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      infoChangeList: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          editMode: true,
                          maxHeight: 400,
                          pagination: false,
                          sortable: false
                        },
                        'x-query-engine-skip': true,
                        properties: generateXindexInOrder({
                          // 变更状态
                          changeStatus: {
                            type: 'string',
                            title: i18nExpression('quest.changeStatus'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component': 'DictSelect',
                            'x-component-props': {
                              'disabled': expression(`$disabled`),
                              code: 'INFO_CHANGE_STATUS'
                            }
                          },
                          // 变更单号
                          changeApplyNo: {
                            type: 'string',
                            'x-render-table-column': {
                              title: i18nExpression('quest.changeApplyNo'),
                              minWidth: 150
                            },
                            'x-component': 'TableButton',
                            'x-component-props': {
                              type: 'text',
                              '@click': expression(`({row}) => {
                                let changeId = row.changeId
                                let companyId = row.companyId
                                let tab = {
                                  component: vendorInfoChangeDetail,
                                  params: {
                                    flag: 'view',
                                    changeId: changeId,
                                    companyId: companyId,
                                    tabName: 'vendorInfoChangeDetail' + row.changeApplyNo
                                  },
                                  title: row.changeApplyNo,
                                  name: 'vendorInfoChangeDetail' + row.changeApplyNo
                                }
                                emitTabAdd(tab)
                              }`)
                            }
                          },
                          // 审批日期
                          changeApplyDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`),
                              'disabled': expression(`$disabled`)
                            },
                            title: i18nExpression('quest.changeApplyDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          },
                          // 创建人
                          createdUserName: {
                            type: 'string',
                            title: i18nExpression('quest.createdFullName'),
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          // 创建时间
                          creationDate: {
                            ...yearMonthDaySelectorSegment,
                            'x-component-props': {
                              ...yearMonthDaySelectorSegment['x-component-props'],
                              formatter: expression(`({ cellValue, row, column }) => {
                                parseTime(row.creationDate, '{y}-{m}-{d}')
                              }`),
                              'disabled': expression(`$disabled`)
                            },
                            title: i18nExpression('quest.creationDate'),
                            'x-render-table-column': {
                              minWidth: 150
                            }
                          }
                        })
                      }
                    }
                  },
                  // 注册操作历史
                  operatingLog: {
                    type: 'void',
                    'x-component': 'CollapseItem',
                    'x-component-props': {
                      title: i18nExpression('common.operationRecord')
                    },
                    'x-query-engine-skip': true,
                    properties: {
                      operatingLogList: {
                        type: 'array',
                        'x-component': 'RenderTable',
                        'x-component-props': {
                          preColumns: 'seq',
                          editMode: true,
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
                              }`),
                              'disabled': expression(`$disabled`)
                            },
                            title: i18nExpression('dataConfMod.operationTime'),
                            'x-render-table-column': {
                              minWidth: 100
                            }
                          },
                          operation: {
                            type: 'string',
                            title: i18nExpression('contractMod.operationType'),
                            'x-component': 'DictSelect',
                            'x-render-table-column': {
                              minWidth: 150
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`),
                              code: 'OPERATING_TYPE'
                            }
                          },
                          reason: {
                            type: 'string',
                            title: i18nExpression('common.explanationOfReasons'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          },
                          createdFullName: {
                            type: 'string',
                            title: i18nExpression('flowMod.operator'),
                            'x-render-table-column': {
                              minWidth: 100
                            },
                            'x-component-props': {
                              'disabled': expression(`$disabled`)
                            }
                          }
                        })
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
          data: expression(`$form.query('state').get('data').progressData`),
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
  $bus.$emit('green')
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
  $attrs: attrs,
  performPlanService,
  $disabled,
  emitTabRemove,
  $back,
  DictSelect,
  observer,
  $managementChange,
  query,
  vendorInfoChangeDetail,
  emitTabAdd,
  $ifPersonal,
  personProgressData
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
    schemaKey="vendorProfileDetail"
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
</style>
