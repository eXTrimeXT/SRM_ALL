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
import CAddress from 'lib@/components/c-address/index.vue'
import CCategorySelect from 'lib@/components/c-category-select/index.vue'
import { contactInfoList } from './components/contactInfoList'
import { bankInfoList } from './components/bankInfoList'
import { cooInfoList } from './components/cooInfoList'
import { financeInfoList } from './components/financeInfoList'
import { factoryInfoList } from './components/factoryInfoList'
import { vendorSiteInfoList } from './components/vendorSiteInfos'
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
import { FileDynamic } from 'lib@/components/srm-components/file-dynamic'
import CFillProgress from 'lib@/components/c-fill-progress/index.vue'
import vendorInfoChangeDetail from 'modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/edit'
import { personBaseInfo } from './components/personal'
import { serviceRange } from './components/serviceRange'
import { authInfo } from './components/authInfo'
import { specialControls } from './components/specialControls'
import { qualificationInformation } from './components/qualificationInformation'
import { abnormalInfo } from './components/abnormalInfo'
import { sceneFileApi } from 'modb@/basicSetting/api/basicSetting'
import Note from './components/Note'
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

const customUpdateButton = computed(() => (!$disabled && ['SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const viewUpdateButton = computed(() => (!$disabled && !['APPROVED', 'SUPPLIER_SUBMITTED'].includes(workflowStatus.value)))
const disabledUpdateButton = computed(() => ['APPROVING'].includes(workflowStatus.value))
const query = {
  '*': {},
  'bankInfos': { '*': {} },
  'contactInfos': { '*': {} },
  'orgCategorys': { '*': {} },
  'orgInfos': { '*': {} },
  'operationInfo': { '*': {} },
  'fileUploads': { '*': {} },
  'supplierLeaderList': { '*': {} },
  'siteInfos': { '*': {} },
  'npmCompanySizes': { '*': {} },
  'managementAttaches': { '*': {} },
  'cateJournalList': { '*': {}, npmSerciceCustoms: { '*': {} } },
  'npmFinanceReports': { '*': {} },
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
  },
  'operatingLogList': { '*': {} },
  'infoChangeList': { '*': {} }

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
              npmCompanySizes,
              cateJournalList,
              ...other
            } = data
            $form.setValues({
              ...other
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
            $form.query('companySizes').take().value = npmCompanySizes
            $form.values.userInfo = userInfoList
            $form.query('businessDate').take().value = [other.businessStartDate, other.businessEndDate]
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
                extSex,
                businessScope,
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
            setTimeout(() => {
              $addScrollEvent($form)
            }, 1000)
            return data
          }`)
        }
      }
    },
    'x-data': {
      totalServiceRangeList: []
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
            getMdmCode: {
              type: 'void',
              'x-visible': generateCharExpressionByFunction(`({ $form }) => {
                return $form.values.status === 'APPROVED' && !$form.values.companyCode && $attrs.params.flag === 'edit'
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
              'x-content': i18nExpression('common.submit'),
              'x-component': 'Button',
              'x-visible': generateCharExpressionByFunction(`({ $form }) => {
                return $form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit'
              }`),
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
              'x-visible': generateCharExpressionByFunction(`({ $form }) => {
                return $form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit'
              }`),
              'x-content': i18nExpression('purchaseDemand.refuse'),
              'x-component': 'Button',
              // 'x-visible': expression('[\'SUBMITTED\'].includes($form.query(\'state\').get(\'data\').status)'),
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
            }
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
              // 银行信息
              ...bankInfoList,
              // 财务信息
              ...financeInfoList,
              // 公司规模
              ...companySizesList,
              // 服务范围
              ...serviceRange,
              // 资质信息
              ...qualificationInformation,
              // // 认证协议
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
                          readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
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
              },
              // 特殊控制
              ...specialControls,
              // 异常信息
              ...abnormalInfo,
              // 调查表清单
              questSupplier: {
                'x-visible': false,
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
                        }
                      },
                      // 创建时间
                      creationDate: {
                        type: 'string',
                        title: i18nExpression('quest.creationDate'),
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-component-props': {
                          'disabled': expression('$disabled')
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
                          disabled: expression('$disabled'),
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
                        type: 'string',
                        title: i18nExpression('quest.changeApplyDate'),
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-component-props': {
                          'disabled': expression('$disabled')
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
                          'disabled': expression('$disabled')
                        }
                      },
                      // 创建时间
                      creationDate: {
                        type: 'string',
                        title: i18nExpression('quest.creationDate'),
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-component-props': {
                          'disabled': expression('$disabled')
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
                        type: 'string',
                        title: i18nExpression('dataConfMod.operationTime'),
                        'x-render-table-column': {
                          minWidth: 100
                        },
                        'x-read-pretty': expression('$disabled'),
                        'x-component-props': {
                          'disabled': expression('$disabled')
                        }
                      },
                      operation: {
                        type: 'string',
                        title: i18nExpression('contractMod.operationType'),
                        'x-component': 'DictSelect',
                        'x-render-table-column': {
                          minWidth: 150
                        },
                        'x-read-pretty': expression('$disabled'),
                        'x-component-props': {
                          disabled: expression('$disabled'),
                          code: 'OPERATING_TYPE'
                        }
                      },
                      reason: {
                        type: 'string',
                        title: i18nExpression('common.explanationOfReasons'),
                        'x-render-table-column': {
                          minWidth: 100
                        },
                        'x-read-pretty': expression('$disabled'),
                        'x-component-props': {
                          'disabled': expression('$disabled')
                        }
                      },
                      createdFullName: {
                        type: 'string',
                        title: i18nExpression('flowMod.operator'),
                        'x-render-table-column': {
                          minWidth: 100
                        },
                        'x-read-pretty': expression('$disabled'),
                        'x-component-props': {
                          'disabled': expression('$disabled')
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
})
const $nodeList = (userType) => {
  const company = [
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
      code: 'bankInfoList',
      name: t('vendorMod.bankInfo'),
      percentage: 0
    },
    {
      code: 'financeInfo',
      name: t('cusEntry.vendorMod.financeReport'),
      percentage: 0
    },
    {
      code: 'companySizesList',
      name: t('vendorMod.companySize'),
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
    },
    {
      code: 'specialControls',
      name: t('cusEntry.vendorMod.specialControls'),
      percentage: 0
    },
    {
      code: 'abnormalInfo',
      name: t('cusEntry.vendorMod.abnormalInfo'),
      percentage: 0
    },
    // {
    //   code: 'questSupplier',
    //   name: t('quest.questSupplierModule'),
    //   percentage: 0
    // },
    {
      code: 'infoChange',
      name: t('quest.changeRecord'),
      percentage: 0
    },
    {
      code: 'operatingLog',
      name: t('common.operationRecord'),
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
    // {
    //   code: 'authInfo',
    //   name: t('cusEntry.vendorMod.authInfo'),
    //   percentage: 0
    // },
    {
      code: 'fileUploadsList',
      name: t('vendorMod.sceneAttachmentInfo2'),
      percentage: 0
    },
    {
      code: 'specialControls',
      name: t('cusEntry.vendorMod.specialControls'),
      percentage: 0
    },
    {
      code: 'abnormalInfo',
      name: t('cusEntry.vendorMod.abnormalInfo'),
      percentage: 0
    },
    // {
    //   code: 'questSupplier',
    //   name: t('quest.questSupplierModule'),
    //   percentage: 0
    // },
    {
      code: 'infoChange',
      name: t('quest.changeRecord'),
      percentage: 0
    },
    {
      code: 'operatingLog',
      name: t('common.operationRecord'),
      percentage: 0
    }
  ]
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
const scope = {
  app,
  t,
  http,
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
  $nodeList,
  sceneFileApi,
  $showSunFile,
  CollapseItemTitle,
  $getNoteValue,
  $noteType,
  $getNoteObj,
  $throttle,
  $addScrollEvent,
  $scrollHandler
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
.scroll-area {
  position: relative;
}
.info-fill-progress{
  position: fixed;
  width: 210px !important;
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
