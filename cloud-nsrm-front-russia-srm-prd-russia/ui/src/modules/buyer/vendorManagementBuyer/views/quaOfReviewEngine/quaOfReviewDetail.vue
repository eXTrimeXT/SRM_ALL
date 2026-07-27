<script setup lang="ts">

// @ts-ignore
import { RenderEngine } from 'lib@/components/render-engine'

import {
  defineSchemas,
  expression,
  i18nExpression,
  connect,
  mapProps,
  ViewModel,
  generateXindexInOrder,
  queryFieldValueExpression,
  toJS
} from '@meicloud/render-engine'

import { useAttrs } from 'vue-demi'
// @ts-ignore
import { usePageHelper } from 'lib@/components/composables/usePageHelper'
// @ts-ignore
import { accessCommonApi, quaApi, reviewFormStandard } from 'modb@/vendorManagementBuyer/api/vendorManagement'
// @ts-ignore
import BaseInfo from './components/collapseItem/baseInfo'
// @ts-ignore
import SourcingInfo from './components/collapseItem/sourcingInfo'
// @ts-ignore
import OrgCateJournals from './components/collapseItem/orgCateJournals'
// // @ts-ignore
import Finance from './components/collapseItem/finance'
// @ts-ignore
import BankInfo from './components/collapseItem/bankInfo'
// @ts-ignore
import VendorSiteInfos from './components/collapseItem/vendorSiteInfos'
// @ts-ignore
import QuaReviewReason from './components/collapseItem/quaReviewReason'
// @ts-ignore
import Attachment from './components/collapseItem/attachment'
// @ts-ignore
import QuestSupplierModule from './components/collapseItem/questSupplierModule'
// @ts-ignore
import QualificationStandard from './components/dialog/qualificationStandard'
// @ts-ignore
import SourcingList from './components/dialog/sourcingList'
// @ts-ignore
import VendorSiteBatch from './components/vendorSiteBatch'
// @ts-ignore
import { vendorAttributeComApi } from 'modb@/basicSetting/api/basicSetting'
// @ts-ignore
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
// @ts-ignore
import VendorProfileDetailRead from 'modb@/vendorManagementBuyer/views/vendorProfile/vendorProfileDetailRead'
// @ts-ignore
import sourcingApplicationDetail from 'modb@/sourcing/views/sourcingApplicationBuyer/sourcingApplicationDetail'

const IQualificationStandard = connect(QualificationStandard, mapProps({ value: 'data' }))

const { emitTabAdd, emitTabRemove, t: $t, app } = usePageHelper()

let $attrs: any = useAttrs()

const initButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('ReviewForm').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.save.name = app.$t('common.staging')
    componentInstance.buttonConfigInfo.submit.name = app.$t('common.submit')
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
  }, 50)
}

const updateButtonConfig = ($form: any) => {
  setTimeout(() => {
    const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance
    const viewUpdateButton = $form.query('ReviewForm').get('data').viewUpdateButton
    componentInstance.buttonConfigInfo.save.view = viewUpdateButton
    componentInstance.buttonConfigInfo.submit.view = viewUpdateButton
    componentInstance.buttonConfigInfo.cancel.view = viewUpdateButton
    componentInstance.buttonConfigInfo.close.view = !viewUpdateButton
    componentInstance.setWorkflowTabDisabled($form.query('ReviewForm').get('data').orderStatus === 'DRAFT')
  }, 50)
}

const initData = ($form: any) => {
  // 查询维度配置-控制银行信息是否展示
  $getFieldDimConf($form)
  // 设置审批流按钮
  initButtonConfig($form)

  if ($form.query('ReviewForm').get('data').curOpt === 'add') {
    // @ts-ignore
    const { nickname, ceeaDeptId, department } = app.$store.getters.userInfo
    // 设置单据基础信息-创建人、部门
    $form.values.createdFullName = nickname
    $form.values.ceeaDeptId = ceeaDeptId || null
    $form.values.ceeaDeptName = department
    if ($attrs.params.row) {
      // 设置单据基础信息-供应商
      $form.values.vendorId = $attrs.params.row.vendorId
      $form.values.vendorCode = $attrs.params.row.vendorCode
      $form.values.vendorName = $attrs.params.row.vendorName

      // 获取供应商银行信息
      $getFinBankInfo($form)
      // 获取供应商地点信息
      $getSiteList($form, $attrs.params.row.vendorId, false)
      // 获取调查表清单信息
      $getQuestSupplierList($form, $attrs.params.row.vendorId)
    }
    return false
  }

  if (['edit', 'view'].includes($form.query('ReviewForm').get('data').curOpt)) {
    $form.values.reviewFormId = $attrs.params.row.reviewFormId
    // 获取调查表清单信息
    $getQuestSupplierList($form, $attrs.params.row.vendorId)
    return true
  }
}

// 查询维度配置-控制银行信息是否展示
const $getFieldDimConf = ($form: any) => {
  // @ts-ignore
  vendorAttributeComApi.getFieldDim().then(res => {
    let dimObj = {}
    if (res.data.length > 0) {
      res.data.map((item: any) => {
        // @ts-ignore
        if (item.isAudit === 'Y') {
          // @ts-ignore
          dimObj[item.dimCode] = item.isAudit
        }
      })
    }
    $form.query('ReviewForm').get('data').changeDim = dimObj
  })
}

//  获取银行信息---TODO:分页未处理
const $getFinBankInfo = ($form: ViewModel) => {
  // @ts-ignore
  accessCommonApi.getBankJournaByReviewId({ vendorId: $form.values.vendorId }).then(res => {
    if (res.data) {
      $form.values.bankJournals = res.data
    }
  })
}

// 获取供应商地点信息-TODO:分页未处理
const $getSiteList = ($form: ViewModel, vendorId: any, fromQuicksearch: boolean = false) => {
  // @ts-ignore
  app.$http({
    url: '/api-sup/organization/site-journal/listSiteJournal',
    method: 'GET',
    params: { vendorId },
    loading: true
  })
    .then((res: any) => {
      $form.values.siteJournals = $form.values.siteJournals.concat(res.data)
      if (res.data.length === 0 && fromQuicksearch) {
        $form.query('ReviewForm').get('data').isNewVendor = true
      }
    })
}

// 获取调查表清单信息
const $getQuestSupplierList = ($form: ViewModel, vendorId: any) => {
  // @ts-ignore
  app.$http({
    url: '/api-sup/quest/questSupplier/listPageByParm',
    method: 'POST',
    data: {
      companyIdForQuery: vendorId,
      approvalStatusList: 'APPROVED',
      orgCondition: 'Y'
    }
  })
    .then((res: { data: { list: any } }) => {
      $form.values.questSupplierList = res.data.list
    })
}

// 资质审查类型切换
const $getQuaReviewType = (quaReviewType: any, $form: ViewModel) => {
  if (!quaReviewType) {
    return false
  }
  $form.values.ceeaIfVendorAuth = quaReviewType === 'ONETIME_VENDOR' ? 'N' : 'Y'
}

// 供应商名称选择
const $getCompanyObj = (value: any, $form: ViewModel) => {
  $form.values.vendorId = value ? value.companyId : ''
  $form.values.vendorCode = value ? value.companyCode : ''
  $form.values.vendorName = value ? value.companyName : ''

  if (value) {
    // 清除供应商地点信息
    $form.values.siteJournals = []
    // 获取供应商银行信息
    $getFinBankInfo($form)
    // 获取供应商地点信息
    $getSiteList($form, value.companyId, true)
    // 获取调查表清单信息
    $getQuestSupplierList($form, value.companyId)
  }
}
// 打开寻源需求弹窗
const $addSourcingItem = ($form: ViewModel) => {
  // @ts-ignore
  $form.query('sourcingDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    $form.query('ReviewForm').get('data').sourcingDialogInit = true
  })
}

// 设置寻源需求数据
const $setSelectedSourcingData = ($form: ViewModel, $message: any) => {
  const ids = $form.values.reviewRelations.map((item: any) => item.reqHeadId)

  let orgList: any = []
  let categoryList: any = []

  $form.query('ReviewForm').get('data').selectedSourcing.forEach((item: any) => {
    if (!ids.includes(item.reqHeadId)) {
      $form.query('reviewRelations').take((field: any) => field.value.push(item))
    }
    orgList.push({ reqHeadId: item.reqHeadId, organizationName: item.orgName, organizationId: item.orgId, organizationCode: item.orgCode })
    categoryList.push({ reqHeadId: item.reqHeadId, categoryId: item.categoryId, categoryName: item.categoryName, categoryCode: item.categoryCode, thisYearAmount: '', existCountOfCompany: '', supplierCountLimit: '', supplierCountLimitFlag: '' })
  })

  // 设置组织信息
  $organizationSelectHandel(orgList, $form, $message)
  // 获取品类信息
  $addCategorysList(categoryList, $form, $message)
}

// 设置组织信息
const $organizationSelectHandel = (orgs: any = [], $form: any, $message: any) => {
  if (orgs.length == 0) {
    $message.warning($t('common.msgSelectOrg'))
    return false
  }

  if ($form.values.orgJournals.length < 1) {
    orgs.forEach((item: any) => {
      let obj = {
        reqHeadId: item.reqHeadId || null,
        orgName: item.organizationName,
        orgId: item.organizationId,
        orgCode: item.organizationCode,
        orgJournalPayPlanList: [{ paymentPeriod: 1 }]
      }
      $form.query('orgJournals').take((field: any) => field.value.push(obj))
      $financeInfoListAdd(item, $form)
    })
  } else {
    orgs.forEach((item: any) => {
      let bol = true
      let reqHeadId = item.reqHeadId || null
      let organizationId = item.organizationId
      $form.values.orgJournals.find((el: any) => {
        if (el.orgId === item.organizationId) {
          bol = false
        }
      })
      if (bol) {
        let obj = {
          reqHeadId: item.reqHeadId || null,
          orgName: item.organizationName,
          orgId: item.organizationId,
          orgCode: item.organizationCode,
          orgJournalPayPlanList: [{ paymentPeriod: 1 }]
        }
        $form.query('orgJournals').take((field: any) => field.value.push(obj))
        $financeInfoListAdd(item, $form)
      } else if (reqHeadId) {
        $form.values.orgJournals.forEach((el: any, index: any) => {
          if (organizationId === el.orgId) {
            $form.values.orgJournals.splice(index, 1, {
              ...el,
              reqHeadId
            })
          }
        })
      }
    })
  }
}

// 获取财务信息
const $financeInfoListAdd = async (item: any, $form: ViewModel) => {
  const ids = $form.values.financeInfoList.map((item: any) => item.financeInfoId)

  let res = await reviewFormStandard.listByCompanyIdAndOrgId({
    companyIdList: [$form.values.vendorId],
    orgId: item.organizationId
  })
  if (res.data.length > 0) {
    res.data.forEach((el: any) => {
      let obj = {
        orgName: item.organizationName,
        orgId: item.organizationId,
        orgCode: item.organizationCode,
        fullPathId: item.fullPathId,
        factoryCode: el.factoryCode,
        clearCurrency: el.clearCurrency,
        paymentMethod: el.paymentMethod,
        paymentTerms: el.paymentTerms,
        enableFlag: el.enableFlag,
        financeInfoId: el.financeInfoId
      }
      if (!ids.includes(el.financeInfoId)) {
        $form.query('financeInfoList').take((field: any) => field.value.push(obj))
      }
    })
  } else {
    let obj = {
      orgName: item.organizationName,
      orgId: item.organizationId,
      orgCode: item.organizationCode,
      fullPathId: item.fullPathId,
      financeInfoId: item.financeInfoId
    }
    if (!ids.includes(item.financeInfoId)) {
      $form.query('financeInfoList').take((field: any) => field.value.push(obj))
    }
  }
}

// 新增财务信息
const $addFinanceInfoRow = ($form: any) => {
  let obj = {
    orgName: '',
    orgId: null,
    orgCode: '',
    factoryCode: '',
    clearCurrency: '',
    paymentMethod: '',
    paymentTerms: ''
  }
  $form.query('financeInfoList').take((field: any) => field.value.push(obj))
}

// 新增附件信息
const $addAttachmentRow = ($form: any) => {
  let obj = {
    templateDesc: null,
    templateFileId: null,
    templateFileName: null,
    fileId: null,
    fileName: null,
    ifRequired: 'N',
    ifValidDate: 'N',
    fileValidDate: null,
    reviewPeople: null,
    vendorAssessor: null,
    reviewDate: null,
    score: null,
    authResult: null,
    remark: null
  }
  $form.query('fileRecords').take((field: any) => field.value.push(obj))
}

// 获取品类信息
const $addCategorysList = (categoryList: any, $form: ViewModel, $message: any) => {
  if ($form.values.cateJournals.length < 1) {
    categoryList.forEach((item: any, index: any) => {
      quaApi.getCategoryInfoById(item.categoryId).then((res: any) => {
        item.existCountOfCompany = res.data.existCountOfCompany
        item.supplierCountLimit = res.data.supplierCountLimit
        item.supplierCountLimitFlag = res.data.supplierCountLimitFlag
        $form.query('cateJournals').take((field: any) => field.value.push(item))
        if (index === 0) {
          let query = { quaReviewType: $form.values.quaReviewType || '', categoryId: item.categoryId }
          $fatchQuaFileConfig(query, $form, $message)
        }
      })
    })
  } else {
    let query = { quaReviewType: $form.values.quaReviewType || '', categoryId: $form.values.cateJournals[0].categoryId }
    $fatchQuaFileConfig(query, $form, $message)

    categoryList.forEach((item: any) => {
      let bol = true
      let reqHeadId = item.reqHeadId || null
      let categoryId = item.categoryId
      $form.values.cateJournals.find((el: any) => {
        if (el.categoryId === item.categoryId) {
          bol = false
        }
      })
      if (bol) {
        quaApi.getCategoryInfoById(item.categoryId).then((res: any) => {
          item.existCountOfCompany = res.data.existCountOfCompany
          item.supplierCountLimit = res.data.supplierCountLimit
          item.supplierCountLimitFlag = res.data.supplierCountLimitFlag
          $form.query('cateJournals').take((field: any) => field.value.push(item))
        })
      } else if (reqHeadId) {
        $form.values.cateJournals.forEach((el: any, index: any) => {
          if (categoryId === el.categoryId) {
            $form.values.cateJournals.splice(index, 1, { ...el, reqHeadId })
          }
        })
      }
    })
  }
}

// 获取附件信息
const $fatchQuaFileConfig = (query: any, $form: ViewModel, $message: any) => {
  quaApi.getTemplateFilesByReviewCreate(query).then((res: any) => {
    if (res.data && Object.keys(res.data).length > 0) {
      if (res.data.entryFileConfigList.length > 0) {
        $form.values.fileRecords = res.data.entryFileConfigList.map((item: any) => ({
          ...item,
          fileId: '',
          fileName: ''
        }))
      } else {
        $form.values.fileRecords = []
      }
    } else {
      $form.values.fileRecords = []
      // 该品类没有配置准入流程，请先去配置品类准入配置！
      $message.error($t('vendorMod.msgConfCate'))
    }
  })
}

// 删除寻源数据
const $delSourcingItem = ($form: ViewModel, $table: any, row: any, rowIndex: any) => {
  $table.remove(rowIndex)

  $form.values.orgJournals.forEach((item: any, index: any) => {
    if (item.reqHeadId === row.reqHeadId) {
      $delOrgJournals($form, item, index)
    }
  })

  $form.values.cateJournals.forEach((item: any, index: any) => {
    if (item.reqHeadId === row.reqHeadId) {
      $delCateJournals($form, index)
    }
  })
}
const $delOrgJournals = ($form: ViewModel, item: any, index: any) => {
  $form.values.financeInfoList.forEach((el: any, n: any) => {
    if (el.orgId === item.orgId) {
      $form.values.financeInfoList.splice(n, 1)
    }
  })

  // $form.values.orgJournals.splice(index, 1)
}

const $delCateJournals = ($form: ViewModel, index: any) => {
  // $form.values.cateJournals.splice(index, 1)
  // 品类清空完后
  if ($form.values.cateJournals.length === 0) {
    $form.values.fileRecords = []
  }
}

// 查看资质标准
const $qualifications = (row: any, $form: any, $message: any) => {
  quaApi.getByCategoryId(row.categoryId).then((res: any) => {
    if (!res.data.reviewFormStandardDimList || res.data.reviewFormStandardDimList.length < 1) {
      $message.error($t('vendorMod.noQualificationStandardForCategory'))
    } else {
      $getList(res, $form)
    }
  })
}

//  获取资质标准数据
const $getList = (res: any, $form: any) => {
  let editableTabs: any = []
  res.data.reviewFormStandardDimList.forEach((item: any, n: any) => {
    const index = String(n + 1)
    let d1 = {
      title: item.dimName,
      name: index,
      content: []
    }
    let attr: any = []
    item.reviewFormStandardDimFieldList.forEach((el: any) => {
      let d2 = {
        standardId: el.standardId,
        standardDimId: el.standardDimId,
        fieldCode: el.fieldCode,
        fieldName: el.fieldName,
        fieldType: el.fieldType,
        fieldContent: el.fieldContent
      }
      attr.push(d2)
    })
    d1.content = attr
    editableTabs.push(d1)
  })

  // @ts-ignore
  $form.query('qualificationStandardDialog').take().setComponentProps({ visible: true })
  setTimeout(() => {
    // eslint-disable-next-line no-return-assign
    $form.query('qualificationStandardDialog.qualificationStandard').take((field: any) => field.value = editableTabs)
  })
}

const $financeDel = ($form: ViewModel, row: any, index: any, $message: any) => {
  let bol = false
  let i = 0
  $form.values.orgJournals.forEach((u: any) => {
    if (u.orgId == row.orgId) {
      bol = true
    }
  })
  $form.values.financeInfoList.forEach((e: any) => {
    if (e.orgId == row.orgId) {
      i++
    }
  })
  if (bol) {
    // 账期与上面组织有关联的时候
    if (i > 1) {
      // 如果自动带出的账期不能修改
      if (row.enableFlag !== 'Y') {
        $form.values.financeInfoList.splice(index, 1)
      } else {
        $message.error($t('vendorMod.msgDel2'))
      }
    } else {
      $message.error($t('vendorMod.msgDel1'))
    }
  } else {
    $form.values.financeInfoList.splice(index, 1)
  }
}

// 查看寻源单据
const $readSourcing = (row: any) => {
  emitTabAdd({
    component: sourcingApplicationDetail,
    params: {
      flag: 'view',
      row,
      showType: 'readOnly',
      activeWorkflowTab: 'sourcingApplicationDetail' + row.reqHeadNo
    },
    title: row.reqHeadNo,
    name: 'sourcingApplicationDetail' + row.reqHeadNo
  })
}

// 银行快查选择
const $getBankObj = (val: any, row: any) => {
  row.branchBankId = val ? val.branchBankId : ''
  row.bankCode = val ? val.bankNum : '' // 银行编号
  row.bankName = val ? val.bankName : '' // 银行名称
  row.unionCode = val ? val.branchBankNum : '' // 分行编号
  row.openingBank = val ? val.branchBankName : '' // 分行名称[开户行名称]
}

// 批量维护供应商地点信息
const $batchSelectCountry = (data: any, $form: ViewModel) => {
  $form.values.siteJournals.forEach((val: any, index: any) => {
    val.country = data.globalCountry
    val.addressDetail = data.globalAddressDetail
    // 选择国外就清理省市区，并且禁用
    if (data.globalCountry !== 'CN') {
      val.province = null
      val.city = null
    }
    if ($form.query('ReviewForm').get('data').isNewVendor) {
      val.vendorSiteCode = data.globalAddress
    }
  })
}

// 地点信息列表-业务实体选择
const $selectHandler = (node: any, row: any) => {
  row.orgId = node ? node.organizationId : null
  row.orgCode = node ? node.organizationCode : null
  row.orgName = node ? node.organizationName : null
  if (node) {
    // @ts-ignore
    app.$http({
      url: '/api-base/organization/organization/get',
      method: 'GET',
      params: { organizationId: node.organizationId },
      loading: true
    }).then((res: any) => {
      if (res.data) {
        row.erpOrgId = res.data.erpOrgId || null
      }
    })
  }
}

const $validateForm = ($form: any, $message: any) => {
  // 基础信息
  const baseInfoFlag = $form.values.quaReviewType && $form.values.vendorName && $form.values.reviewExplain && $form.values.ceeaDemandAnalysis && $form.values.ceeaSupAnalysis
  if (!baseInfoFlag) {
    // $message.error($t('vendorMod.pleasefinishRequired'))
    $message.error('请完成资质审查单据必填项')
    return false
  }

  // 组织信息
  let orgJournals = $form.values.orgJournals || []
  if (orgJournals.length == 0) {
    $message.error($t('vendorMod.pleaseAddAnImportOrganization'))
    return false
  }

  // 品类信息校验
  const cateJournals = $form.values.cateJournals || []
  let cateJournalsFlag = true
  for (let item of cateJournals) {
    if (item.supplierCountLimitFlag === 'Y' && item.existCountOfCompany >= item.supplierCountLimit) {
      $message.error($t('vendorMod.suppliersCategoryHasExceededTheUpperLimit'))
      cateJournalsFlag = false
      break
    }
  }
  if (!cateJournalsFlag) {
    return false
  }

  // 财务信息校验
  const financeInfoList = $form.values.financeInfoList || []
  let financeFlag = true
  for (let item of financeInfoList) {
    if (!item.orgId || !item.clearCurrency || !item.paymentMethod || !item.paymentTerms) {
      $message.error('请完善财务信息必填项')
      financeFlag = false
      break
    }
  }
  if (!financeFlag) {
    return false
  }

  // 银行信息校验
  let bankJournals = $form.values.bankJournals || []
  let ceeaEnabled = bankJournals.filter((v: any) => v.ceeaEnabled === 'Y')
  if (!ceeaEnabled.length) {
    // 供应商银行信息至少需要启用一个
    $message.error($t('vendorMod.msgAtLeastVBank'))
    return false
  }
  if (bankJournals.length > 0) {
    for (let item of bankJournals) {
      if (!item.bankCode) {
        // 银行代码是必填项
        $message.error($t('vendorMod.msgBankCodeRequired'))
        return false
      }
    }
  }

  // 地点信息校验 siteJournals
  let siteJournals = $form.values.siteJournals || []
  let enabledFlag = siteJournals.filter((v: any) => v.enabledFlag === 'Y')
  if (!enabledFlag.length) {
    // 供应商地点信息至少需要启用一个
    $message.error($t('vendorMod.msgAtLeastVAddress'))
    return false
  }
  if (siteJournals.length > 0) {
    let buIdArr = siteJournals.map((v: any) => v.orgId + '-' + v.vendorSiteCode)
    let newArr = Array.from(new Set(buIdArr))
    if (buIdArr.length !== newArr.length) {
      $message.error($t('vendorMod.msgVAddressRepeat')) // 供应商地点信息，请选择不重复的业务实体 + 地点名称!
      return false
    }
    for (let item of siteJournals) {
      if (!item.orgId) {
        $message.error($t('vendorMod.msgVOrgRequired')) // 供应商地点下的业务实体是必填项!
        return false
      }
      if (!item.vendorSiteCode) {
        // 供应商地点下的地点名称是必填项!
        $message.error($t('vendorMod.msgVAddressRequired'))
        return false
      }
      if (!item.country) {
        // 供应商地点下的国家是必填项!
        $message.error($t('vendorMod.msgVCountryRequired'))
        return false
      }
      if (!item.addressDetail) {
        // 供应商地点下的详细地址是必填项!
        $message.error($t('vendorMod.msgVAddressDetailRequired'))
        return false
      }
    }
  }
  // 资质审查原因
  let reviewFormExps = $form.values.reviewFormExps || []
  let reviewFormExpsFlag = true
  if (reviewFormExps.length == 0) {
    // '请选择资质审查原因'
    $message.error($t('vendorMod.msgQuaReviewReason'))
    return false
  } else {
    for (let item of reviewFormExps) {
      if (!item.reviewReason) {
        $message.error($t('vendorMod.msgQuaReviewReason'))
        reviewFormExpsFlag = false
        break
      }
    }
  }
  if (!reviewFormExpsFlag) {
    return false
  }

  // 判断附件是否上传
  let fileRecords = $form.values.fileRecords || []
  let fileFlag = true
  for (let item of fileRecords) {
    if (item.ifRequired === 'Y' && !item.fileId) {
      // 设置了必传
      // 请上传
      $message.error($t('vendorMod.msgUpload') + item.templateDesc + $t('vendorMod.msgAttachInfo'))
      fileFlag = false
      break
    }

    if (item.ifValidDate === 'Y' && !item.fileValidDate) {
      // 设置了有效期必填
      $message.error($t('vendorMod.msgMaintain') + item.templateDesc + $t('vendorMod.msgAttachDeadline'))
      fileFlag = false
      break
    }
  }
  if (!fileFlag) {
    return false
  }

  return true
}

const $saveBill = (type: string, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
  const values = $form.values
  if (type === 'SAVE') {
    $submitData(type, values, $form, $queryEngine, $confirm, $message, $bus)
  } else if (type === 'SUBMIT') {
    $form.validate().then(() => {
      if ($validateForm($form, $message)) {
        $submitData(type, values, $form, $queryEngine, $confirm, $message, $bus)
      }
    }).catch((err: any) => {
      console.log(err, 'err')
      $validateForm($form, $message)
    })
  }
}

// 关联供应商信息
const $relationVendorInfo = (arr: any, $form: any) => {
  return arr.map((item: any) => {
    return {
      ...item,
      vendorId: $form.values.vendorId,
      vendorCode: $form.values.vendorCode,
      vendorName: $form.values.vendorName
    }
  })
}

// 处理空字符串报错-暂时前端处理，后面后端会统一处理
const $handleEmptyField = (arr: any, fields: any) => {
  fields.forEach((item: any) => {
    arr.forEach((el: any) => {
      if (el[fields] === '') {
        el[fields] = null
      }
    })
  })
  return arr
}

const $submitData = (type: string, $values: any, $form: any, $queryEngine: any, $confirm: any, $message: any, $bus: any) => {
    const form = toJS($values)
    delete form.questSupplierList

    // 组织关联供应商
    form.orgJournals = $relationVendorInfo(form.orgJournals, $form)
    // 品类关联供应商
    form.cateJournals = $relationVendorInfo(form.cateJournals, $form)
    // 财务关联供应商
    form.financeInfoList = $relationVendorInfo(form.financeInfoList, $form)
    // 处理供应商地点信息空字符串报错
    form.siteJournals = $handleEmptyField(form.siteJournals, ['orgId'])
    // 处理附件信息空字符串报错
    form.fileRecords = $handleEmptyField(form.fileRecords, ['score'])

    $queryEngine.request.save(form, { loading: true }).then((res: any) => {
      if (res.data && res.data.length > 0) {
        $message.success($t('common.successSave'))
        const reviewFormId = res.originalData?.records[0] || ''
        if (type === 'SUBMIT') {
          const componentInstance = $form.query('.SchemaWorkflow').take().componentProps.componentInstance

          componentInstance.setWorkflowBusinessId(reviewFormId)
          componentInstance.setWorkflowTabDisabled(false)
          componentInstance.setWorkflowBusinessVariables({})
          componentInstance.handlerAfter(type.toUpperCase(), () => {
            $cancel($bus)
          })
        } else {
          $bus.$emit('ReviewFormHead')
          $form.values.reviewFormId = reviewFormId
          $queryEngine.request.read()
        }
      }
    })
  }

const $cancel = ($bus: any) => {
  // @ts-ignore
  emitTabRemove($attrs.tabName)
  $bus.$emit('ReviewFormHead')
}

// @ts-ignore
const scope = {
  $attrs,
  VendorProfileDetailRead,
  initData,
  initButtonConfig,
  updateButtonConfig,
  emitTabRemove,
  emitTabAdd,
  $submitData,
  $saveBill,
  $cancel,
  $getQuaReviewType,
  $getCompanyObj,
  $addSourcingItem,
  $setSelectedSourcingData,
  $organizationSelectHandel,
  $addFinanceInfoRow,
  $addAttachmentRow,
  $addCategorysList,
  $qualifications,
  $getBankObj,
  $batchSelectCountry,
  $selectHandler,
  $delSourcingItem,
  $readSourcing,
  $delOrgJournals,
  $delCateJournals,
  $financeDel
}

// @ts-ignore
const schema = defineSchemas({
  ReviewForm: {
    type: 'void',
    'x-decorator': 'QueryEngine',
    'x-component': 'el-container',
    'x-component-props': {
      class: 'flex-container',
      direction: 'vertical'
    },
    'x-data': {
      isReadOnly: false,
      changeDim: null,
      curOpt: 'add',
      orderStatus: 'DRAFT',
      viewUpdateButton: true,
      isNewVendor: false,
      sourcingDialogInit: false,
      selectedSourcing: []
    },
    'x-query-engine': {
      service: 'sup',
      actions: {
        read: {
          immediate: true,
          ready: expression(`() => {
            // 单纯文本只读状态
            $form.readPretty = $attrs.params.flag === 'view'
            $form.query('ReviewForm').get('data').isReadOnly = $attrs.params.isOnlyRead || false
            $form.query('ReviewForm').get('data').curOpt = $attrs.params.flag || 'add'

            return initData($form)

          }`),
          transformRequest: expression(`(data, headers) => {
            data.payload = [$attrs?.params?.row?.reviewFormId || $form.values.reviewFormId || '']
            data.query['*'] = {}

            return data
         }`),
          onSuccess: expression(`(res) => {
          const detailData = res.data[0]

          $form.query('ReviewForm').get('data').orderStatus = detailData.approveStatus
          $form.query('ReviewForm').get('data').viewUpdateButton = ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(detailData.approveStatus) || $attrs.params.flag === 'add'
          updateButtonConfig($form)

          $form.setValues({
            ...detailData
          })
        }`)
        },
        save: {
          // 标记当前 action 需要消费底层储存的级联删除数据
          cascadeDeletion: true
        }
      }
    },
    properties: {
      SchemaWorkflow: {
        type: 'void',
        'x-component': 'SchemaWorkflow',
        'x-component-props': {
          'business-id': expression('$attrs.params.row?.reviewFormId || null'),
          'business-type': 'quaBusReview',
          'button-custom': expression('{}'),
          '@click-handler': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@submit-direct': expression(`(type) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@confirm': expression(`(type, comment) => {
            $saveBill(type, $form, $queryEngine, $confirm, $message, $bus)
          }`),
          '@close-tab': expression(`() => {
            emitTabRemove($attrs.tabName)
          }`),
          '@update-integration-mode': expression(`(integrationMode) => {
            updateButtonConfig($form)
          }`)

        },
        properties: {
          checkVendor: {
            type: 'void',
            title: i18nExpression('vendorMod.checkVendorInfo'), // 查看供应商信息
            'x-component': 'RButton',
            'x-visible': expression('$form.query(\'ReviewForm\').get(\'data\').curOpt !== \'view\'&& !!$form.values.vendorId'),
            'x-component-props': {
              '@click': expression(`(rowIndex) => {
                emitTabAdd({
                  component: VendorProfileDetailRead,
                  params: {
                    flag: 'view',
                    companyId: $form.values.vendorId,
                    tabName: 'vendorProfileDetailRead'
                },
                title: $form.values.vendorName,
                name: 'vendorProfileDetailRead'
                })
               }`)
            },
            'x-slot': 'buttonOne'
          },
          vendorAccessSteps: {
            type: 'void',
            'x-visible': expression('!!$form.values.reviewFormId'),
            'x-component': 'VendorAccessSteps',
            'x-component-props': {
              'access-type': 'qua',
              'approve-status': expression('$form.query(\'ReviewForm\').get(\'data\').orderStatus'),
              style: 'margin-bottom: 30px'

            }
          },
          collapse: {
            type: 'void',
            'x-component': 'Collapse',
            properties: generateXindexInOrder({
              // 资质审查单
              baseInfo: {
                ...BaseInfo
              },
              // 寻源需求单
              sourcingInfo: {
                ...SourcingInfo
              },
              // 引入组织和品类
              orgCateJournals: {
                ...OrgCateJournals
              },
              // // 财务信息
              finance: {
                ...Finance
              },
              // 银行信息
              bankInfo: {
                ...BankInfo
              },
              // 供应商地点信息
              vendorSiteInfos: {
                ...VendorSiteInfos
              },
              // 资质审查原因
              quaReviewReason: {
                ...QuaReviewReason
              },
              // 附件
              attachment: {
                ...Attachment
              },
              // 调查表清单
              questSupplierModule: {
                ...QuestSupplierModule
              }
            })
          }
        }
      }

    }
  },
  // 寻源需求弹窗
  sourcingDialog: {
    type: 'void',
    title: $t('route.sourcingApplicationBuyer'), // 寻源需求列表
    'x-component': 'RDialog',
    'x-component-props': {
      size: 'large',
      footer: true,
      beforeClose: expression(`(done, type) => {
            $form.query('ReviewForm').get('data').sourcingDialogInit = false
            if ( type === 'ok') {
              $setSelectedSourcingData ($form, $message)
            }
            done()
      }`)
    },
    properties: {
      sourcingInfo: {
        type: 'void',
        'x-component': 'SourcingList',
        'x-component-props': {
          init: expression('$form.query(\'ReviewForm\').get(\'data\').sourcingDialogInit'),
          vendorId: queryFieldValueExpression('vendorId'),
          '@getSelections': expression(`(selections) => {
            $form.query('ReviewForm').get('data').selectedSourcing= selections
          }`)
        }
      }

    }
  },
  //  资质标准
  qualificationStandardDialog: {
    type: 'void',
    title: $t('vendorMod.qualificationStandard'),
    'x-component': 'RDialog',
    'x-component-props': {
      footer: true
    },
    properties: {
      qualificationStandard: {
        type: 'array',
        'x-component': 'IQualificationStandard'
      }
    }
  }
})

// @ts-ignore
const components = {
  IQualificationStandard,
  SourcingList,
  VendorSiteBatch,
  VendorAccessSteps,
  BaseInfo,
  SourcingInfo,
  OrgCateJournals,
  Finance,
  BankInfo,
  VendorSiteInfos,
  QuaReviewReason,
  Attachment,
  QuestSupplierModule
}
</script>

<template>
  <RenderEngine
    schemaKey="quaOfReviewDetail"
    :pageAttrs="$attrs"
    :schema="schema"
    :scope="scope"
    :components="components"
  />
</template>
