<script setup lang="ts">
// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'
import {
  toJS,
  defineSchemas,
  expression,
  i18nExpression,
  observer,
  markRaw,
  useAutoMountInstanceToField,
  generateXindexInOrder,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
// @ts-ignore
import { useAttrs, computed, ref, defineComponent } from 'vue'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import performPlanService from '@/service/modules/cmPerform/vendor/check'
import SrmCommonFile from 'lib@/components/srm-ui/packages/srm-common-file'
import { userInfoForm } from './components/userInfoForm'
import { companyType } from './components/companyType'
import { companyInfo } from './components/companyInfo'
import { companyBaseInfo } from './components/companyBaseInfo'
import DictSelect from 'lib@/components/c-select/dict-select.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import CAddress from 'lib@/components/c-address/index.vue'
import CCategorySelect from 'lib@/components/c-category-select/index.vue'
import { contactInfoList } from './components/contactInfoList'
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress/index.vue'
import vendorInfoChangeDetail from 'modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/edit'
import { personBaseInfo } from './components/personal'
import { serviceRange } from './components/serviceRange'
import { vendorSiteInfoList } from './components/vendorSiteInfos'
import { authInfo } from './components/authInfo'
import { relationSuppliers } from './components/relationSuppliers'
import relationSuppliersDetail from 'modcb@/vendorManagementBuyer/views/relationSuppliers/detail'
import { qualificationInformation } from './components/qualificationInformation'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
import Note from './components/Note'
import { transformMQL } from '@/library/utils/util'
import { getHeaderField } from '@/utils'
const { app, emitTabRemove, t, vendor, emitTabAdd, http } = usePageHelper()

const attrs: any = useAttrs()

const workflowStatus = ref('DRAFT')

const $disabled = true

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
  'contactInfos': { '*': {} },
  'orgCategorys': { '*': {} },
  'orgInfos': { '*': {} },
  'operationInfo': { '*': {} },
  'fileUploads': { '*': {} },
  'supplierLeaderList': { '*': {} },
  'companyAddressInfos': { '*': {} },
  'managementAttaches': { '*': {} },
  'cateJournalList': { '*': {}, npmSerciceCustoms: { '*': {} } },
  'userInfoList': { '*': {} },
  'npmCompanyExceptionInfos': {
    '*': {},
    '$condition': {
      '$strictQuery': false,
      'filter': {
        'deleteFlag': {
          'eq': 'N'
        }
      }
    }
  }
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
      userType: '',
      // 锚点数据
      progressData: expression('$nodeList($form.query(\'state\').get(\'data\').userType)'),
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
        query: {
          immediate: true,
          loading: true,
          ready: expression(`() => {
            initButtonConfig($form)
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
            const {
              userInfoList,
              cateJournalList,
              ...other
            } = data
            $form.setValues({
              ...other
            })
            // 获取关联供应商数据
            http({
              url: '/api-sup/api-ql/RelationSupBuyer/query',
              method: 'POST',
              data: transformMQL.listPageData({
                type: 'RelationSupBuyer',
                params: { vendorIdA: other.companyId },
                filterOperator: { vendorIdA: 'eq' },
                query: { '*': {} },
                action: 'query',
                pageNum: 1,
                pageSize: 10000
              }),
              loading: true
            }).then(res => {
              if ($form.query('relationSuppliersList').take()) {
                $form.query('relationSuppliersList').take().value = res?.data?.records || []
              }
              $form.values.relationSuppliersList = res?.data?.records || []
            })

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
            $form.query('CompanyInfo').get('data').totalServiceRangeList = serviceRange
            $form.query('serviceRangeList').take().value = serviceRange.slice(0, 10)
            $form.values.userInfo = userInfoList
            $form.query('state').get('data').userType = other.overseasRelation
             if (other.overseasRelation === 'PERSONAL') {
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
                lcCode,
                enterpriseNo,
                companyCountry,
                companyProvince,
                companyCity,
                companyAddress,
                businessStartDate,
                businessEndDate
              } = other
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
                lcCode,
                enterpriseNo,
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
            setTimeout(() => {
              $addScrollEvent($form)
              updateButtonConfig($form)
            }, 1000)
            
            return data
          }`)
        }
      }
    },
    'x-data': {
      totalServiceRangeList: [],
      flowData: {
        taskName: ''
      } // 审批流信息
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$form.values.companyId || null'),
          'business-type': 'InviteVendor',
          'button-custom': expression('{}'),
          'showTopBtn': expression(`!['view'].includes($attrs.params.flag)`),
          'viewType': expression(`['startFileApproval', 'approval', 'view'].includes($attrs.params.flag) ? 'WORKFLOW' : 'SINGLE'`),
          'showUnpass': expression(`['法务部/Юридический отдел', '财务部/Финансовый отдел', '安全部/Отдел безопасности', '区域负责人/Региональный менеджер по России', '总经理/Генеральный директор'].includes($form.query('state').get('data').flowData?.taskName)`),
          'beforeApprove': expression(`(flowData, type) => {
            return new Promise((rs, rj) => {
              if (['法务部/Юридический отдел', '财务部/Финансовый отдел', '安全部/Отдел безопасности', '区域负责人/Региональный менеджер по России', '总经理/Генеральный директор'].includes(flowData.taskName)) {
                let submitData = {
                  companyId: $form.values.companyId
                }
                let res = type == 'approveNo' ? 'N' : 'Y'
                switch (flowData.taskName) {
                  case '法务部/Юридический отдел':
                    submitData.extLegalOpinion = res
                    break
                  case '财务部/Финансовый отдел':
                    submitData.extFinancialOpinion = res
                    break
                  case '安全部/Отдел безопасности':
                    submitData.extSecurityAndRiskOpinion = res
                    break
                  case '区域负责人/Региональный менеджер по России':
                  case '总经理/Генеральный директор':
                    submitData.extIfFinalApprove = res
                    break
                }
                app.$http({
                  url: '/api-sup/pj/companyInfo/updateApprovingOpinion',
                  method: 'POST',
                  data: submitData,
                  loading: true
                }).then(res => {
                  rs(true)
                }).catch(err => {
                  rs(false)
                })
              } else {
                rs(true)
              }
            })
          }`),
          '@click-handler': expression(`(type) => {
            $startApprovalHandle(type, $form, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $startApprovalHandle(type, $form, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $startApprovalHandle(type, $form, $bus)
          }`),
          '@close-tab': expression(`() => {
            $back($bus)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {}`),
          '@updateFlowData': expression(`flowData => {
            $form.query('state').get('data').flowData = flowData
          }`)
        },
        properties: {
          layout: {
            type: 'void',
            'x-component': 'FormContainer',
            items: {
              type: 'object',
              properties: {
                close: {
                  type: 'void',
                  'x-content': i18nExpression('common.close'),
                  'x-component': 'Button',
                  'x-component-props': {
                    type: 'default',
                    '@click': expression(`() => {
                      $back($bus)
                    }`)
                  }
                },
                // TODO: 加上审批流后这个操作是否还有必要？待测试时确认
                getMdmCode: {
                  type: 'void',
                  'x-visible': generateCharExpressionByFunction(`({ $form }) => {
                    return $form.values.status === 'APPROVED' && !$form.values.companyCode && $attrs.params.flag === 'approval'
                  }`),
                  'x-content': i18nExpression('cusEntry.common.getMdmCode'),
                  'x-component': 'Button',
                  'x-component-props': {
                    type: 'primary',
                    '@click': expression(`() => {
                      http({
                        url: '/api-sup/pj/companyInfo/getMdmCodeByCompanyId',
                        method: 'GET',
                        params: {
                          companyId: $form.values.companyId
                        },
                        loading: true
                      }).then(res => {
                        $message.success($t('cusEntry.tipMessage.getMdmCodeSuccess'))
                        $back($bus)
                      })
                    }`)
                  }
                },
                submit: {
                  type: 'void',
                  'x-content': i18nExpression('common.toApprove'),
                  'x-component': 'Button',
                  'x-visible': expression(`$attrs.params.flag === 'passRegister'`),
                  'x-component-props': {
                    '@click': expression(`() => {
                      let values = {
                        companyId: $form.values.companyId
                      }
                      $queryEngine.request.save(values, { query: { '*':{} }, action: 'approve' }).then((res) => {
                        $message.success($t('purchaseDemand.confirm'))
                        $back($bus)
                      })
                    }`)
                  }
                },
                refuse: {
                  type: 'void',
                  'x-visible': expression(`$attrs.params.flag === 'passRegister'`),
                  'x-content': i18nExpression('purchaseDemand.refuse'),
                  'x-component': 'Button',
                  'x-component-props': {
                    '@click': expression(`async () => {
                      $form.query('rejectDialog').take().setComponentProps({
                        visible: true
                      })
                      setTimeout(() => {
                        $form.query('reason').take().value = $getNoteValue($form.values)
                      })
                    }`)
                  }
                },
                // approval: {
                //   type: 'void',
                //   'x-content': i18nExpression('common.submit'),
                //   'x-component': 'Button',
                //   'x-visible': generateCharExpressionByFunction(`({ $form }) => {
                //     return $attrs.params.flag === 'approval'
                //   }`),
                //   'x-component-props': {
                //     '@click': expression(`() => {
                //       let values = {
                //         companyId: $form.values.companyId
                //       }
                //       $queryEngine.request.save(values, { query: { '*':{} }, action: 'qualifiedApprove' }).then((res) => {
                //         $message.success($t('common.success'))
                //         $back($bus)
                //       })
                //     }`)
                //   }
                // }
              }
            },
            properties: {
              collapse: {
                type: 'void',
                'x-component': 'Collapse',
                'x-component-props': {
                  defaultOpenPanelCount: 1,
                  class: 'scroll-area'
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
                  // 个人
                  ...personBaseInfo,
                  // 联系人信息
                  ...contactInfoList,
                  // 服务范围
                  ...serviceRange,
                  // 地址信息
                  ...vendorSiteInfoList,
                  // 认证协议
                  ...authInfo,
                  // 关联供应商
                  ...relationSuppliers,
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
                              readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'approval')
                            },
                            on: {
                              change: value => {
                                $form.values.extRejectAttribute11 = value
                              }
                            }
                          })
                        }
                      })}}`
                    },
                    'x-query-engine-skip': true,
                    'x-visible': generateCharExpressionByFunction(({ $attrs }) => {
                      return ['approval', 'view'].includes($attrs.params.flag)
                    }),
                    properties: {
                      fileUploads: {
                        'x-query-engine-relation': 'fileUploads:*',
                        type: 'array',
                        'x-component': 'FileDynamic',
                        'x-component-props': {
                          'scene-module-code': 'SCENE_SUPPLIER_ATTACHMENT',
                          'businessId': expression('$attrs.params.companyId || null'),
                          'editable': expression('!$disabled'),
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
              'ref': 'contractProgress',
              'nodeName': i18nExpression('logisticsMod.contractInfo'),
              data: expression('$form.query(\'state\').get(\'data\').progressData'),
              percentage: '{{true}}',
              '@index-click': `{{ (code) => {
                  let anchorEle = document.querySelector('#collapse_' + code)
                  if (anchorEle) {
                    anchorEle.scrollIntoView(true)
                  }
              } }}`
            }
          },
          rejectDialog: {
            type: 'void',
            'x-component': 'RDialog',
            'x-component-props': {
              title: i18nExpression('cusEntry.vendorMod.reject'),
              size: 'middle',
              footer: true,
              'close-on-click-modal': false,
              cancelText: i18nExpression('common.cancel'),
              beforeClose: expression(`(done, type, closeLoading) => {
                if ( type === 'ok') {
                  $form.validate('CompanyInfo.rejectDialog.reason').then(res => {
                    let noteObj = $getNoteObj($form.values)
                    let values = {
                      flowRemark: $self.query('reason').take().value,
                      companyId: $form.values.companyId,
                      ...noteObj
                    }
                    $queryEngine.request.save(values, { query: { '*':{} }, action: 'reject' }).then((res) => {
                      done()
                      $message.success($t('bidMod.toRefuseSuccess'))
                      $back($bus)
                    })
                  }).finally(() => {
                    closeLoading()
                  })
                } else {
                  done()
                }
              }`)
            },
            properties: {
              reason: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.rejectReasonNote'),
                'x-component-props': {
                  type: 'textarea',
                  maxlength: 1000,
                  rows: 5
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('cusEntry.tipMessage.rejectReason')
                }
              }
            }
          }
        }
      }
    }
  }
})
const $nodeList = (userType) => {
  // 通过前显示
  let nodeList1 = [
    {
      code: 'relationSuppliers',
      name: t('cusEntry.vendorMod.relationSuppliers'),
      percentage: 0
    }
  ]
  let nodeList2 = [
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
    }
  ]
  if (['passRegister'].includes(attrs.params.flag)) {
    company = company.concat(nodeList1)
    person = person.concat(nodeList1)
  } else if (['startFileApproval', 'approval'].includes(attrs.params.flag)) {
    company = company.concat(nodeList2)
    person = person.concat(nodeList2)
  } else {
    company = company.concat(nodeList1).concat(nodeList2)
    person = person.concat(nodeList1).concat(nodeList2)
  }
  return userType === 'PERSONAL' ? person : company
}
const $back = ($bus: any) => {
  emitTabRemove(attrs.tabName)
  $bus.$emit('green')
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
const CollapseItemTitle = {
  functional: true,
  render (h) {
    return h('div', {
      style: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }
    }, [
      h('span', {
      }, [t('vendorMod.vendorUserInfo')]),
      h('el-button', {
        props: {
          type: 'text'
        },
        on: {
          click: (event) => {
            event.stopPropagation()
          }
        }
      }, [
        t('cusEntry.common.note')
      ])
    ])
  }
}
/* 获取批注信息 */
const $getNoteValue = (values) => {
  const {
    extRejectAttribute1,
    extRejectAttribute2,
    extRejectAttribute3,
    extRejectAttribute4,
    extRejectAttribute5,
    extRejectAttribute6,
    extRejectAttribute7,
    extRejectAttribute8,
    extRejectAttribute9,
    extRejectAttribute10,
    extRejectAttribute11
  } = values
  let resultString = ''
  let resultMap = new Map()
  for (let i = 1; i < 11; i++) {
    if (values[`extRejectAttribute${i}`]) {
      resultMap.set($noteType.get(`extRejectAttribute${i}`), values[`extRejectAttribute${i}`])
    }
  }
  if (resultMap.size) {
    for (let [key, value] of resultMap) {
      resultString = `${resultString}${key}:${value}\n`
    }
  }
  return resultString
}
/* 批注类型 */
const $noteType = new Map([
  ['extRejectAttribute1', t('vendorMod.companyType')],
  ['extRejectAttribute2', t('vendorMod.enterpriseThreeCertificates')],
  ['extRejectAttribute3', t('vendorMod.companyBaseInfo2')],
  ['extRejectAttribute4', t('vendorMod.contactInfo')],
  ['extRejectAttribute5', t('vendorMod.bankInfo')],
  ['extRejectAttribute6', t('cusEntry.vendorMod.financeReport')],
  ['extRejectAttribute7', t('vendorMod.companySize')],
  ['extRejectAttribute8', t('cusEntry.vendorMod.serviceRange')],
  ['extRejectAttribute9', t('cusEntry.vendorMod.qualificationInformation')],
  ['extRejectAttribute10', t('cusEntry.vendorMod.authInfo')],
  ['extRejectAttribute11', t('vendorMod.sceneAttachmentInfo2')]
])
/* 获取有值得批注 */
const $getNoteObj = (values) => {
  let noteObj = {}
  const {
    extRejectAttribute1,
    extRejectAttribute2,
    extRejectAttribute3,
    extRejectAttribute4,
    extRejectAttribute5,
    extRejectAttribute6,
    extRejectAttribute7,
    extRejectAttribute8,
    extRejectAttribute9,
    extRejectAttribute10,
    extRejectAttribute11
  } = values
  for (let i = 1; i < 11; i++) {
    if (values[`extRejectAttribute${i}`]) {
      noteObj[`extRejectAttribute${i}`] = values[`extRejectAttribute${i}`]
    }
  }
  return noteObj
}
/* 添加导航监听器 */
const $addScrollEvent = ($form: any) => {
  /* 获取导航菜单节点 */
  const navNodes = $nodeList($form.query('state').get('data').userType)
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
  const scrollTop = document.getElementsByClassName('render-form-container')[0].scrollTop
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

const initButtonConfig = ($form: any) => {
  let isView = ['view'].includes(attrs.params.flag)
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = ['startFileApproval'].includes(attrs.params.flag)
    componentInstance.buttonConfigInfo.submit.name = t('cusEntry.supplement20250211.submitFileApprove') // 发起附件审批
    componentInstance.buttonConfigInfo.cancel.view = !isView
    componentInstance.buttonConfigInfo.close.view = isView
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  let isView = ['view'].includes(attrs.params.flag)
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    componentInstance.buttonConfigInfo.save.view = false
    componentInstance.buttonConfigInfo.submit.view = ['startFileApproval'].includes(attrs.params.flag)
    componentInstance.buttonConfigInfo.submit.name = t('cusEntry.supplement20250211.submitFileApprove') // 发起附件审批
    componentInstance.buttonConfigInfo.cancel.view = !isView
    componentInstance.buttonConfigInfo.close.view = isView
    componentInstance.setWorkflowBusinessId($form.values.companyId)
    let formHeaderValue = getHeaderField($form.values)
    componentInstance.setWorkflowBusinessVariables({
      procTitleObj: { companyCode: $form.values.companyCode }
    })
  }, 50)
}
// 发起资质信息附件审批流
const $startApprovalHandle = (type: string, $form: any, $bus: any) => {
  if (type != 'SUBMIT') return
  const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
  componentInstance.setWorkflowBusinessId($form.values.companyId)
  componentInstance.setWorkflowTabDisabled(false)
  let formHeaderValue = getHeaderField($form.values)
  componentInstance.setWorkflowBusinessVariables({
      procTitleObj: { companyCode: $form.values.companyCode }
  })
  componentInstance.handlerAfter(type.toUpperCase(), () => {
    $back($bus)
  })
}

const scope = {
  app,
  t,
  http,
  $attrs: attrs,
  initButtonConfig,
  updateButtonConfig,
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
  $nodeList,
  sceneFileApi,
  $showSunFile,
  CollapseItemTitle,
  $getNoteValue,
  $noteType,
  $getNoteObj,
  $throttle,
  $addScrollEvent,
  $scrollHandler,
  transformMQL,
  relationSuppliersDetail,
  $startApprovalHandle,
  $taxDictClass: createDictClass({ 'country': [] })
}
const components = {
  SrmCommonFile,
  CAddress,
  CCategorySelect,
  FileDynamic,
  CFillProgress,
  newAddress,
  Note
}
</script>

<template>
  <RenderEngine
    schemaKey="vendorProfileDetail"
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
.vendorGreen .companyInfo {
  display: flex;
  padding: 16px;
  width: 100%;
}
.vendorGreen .render-form-container__fixed-footer{
  padding-top:0px
}
.vendorGreen .render-form-container {
  margin-bottom: 50px;
}
.scroll-area {
  position: relative;
}
.info-fill-progress{
  position: fixed !important;
  width: 210px !important;
  top: 104px !important;
  right: 0px !important;
  bottom: 0px !important;
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
</style>
