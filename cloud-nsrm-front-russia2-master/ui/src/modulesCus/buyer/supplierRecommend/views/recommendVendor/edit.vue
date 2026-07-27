<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        :showTopBtn="urlParams.flag != 'view'"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
      <!-- <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="RCOMMVENDOR"
        :approval-status="form.projectStatus"
        :status-map="statusMap"
        :projectName="form.souName"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      > -->
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <!-- <el-collapse-item title="基础信息" name="1"> -->
            <el-collapse-item :title="$t('common.baseInfo')" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :approval-flag="approvalFlag"
                :is-mobile="isMobile"
              />
            </el-collapse-item>
            <!-- <el-collapse-item title="申请信息" name="2"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250121.applicationInformation')" name="2">
              <ApplyInfo
                ref="applyInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :isAdd="isAdd"
                :isShowFlag.sync="form.publishFlag"
                :approval-flag="approvalFlag"
                :is-mobile="isMobile"
                v-on="$listeners"
              />
            </el-collapse-item>
            <!-- <el-collapse-item title="标的物信息" name="3"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250121.subjectMatterInformation')" name="3">
              <SubjectInfo
                ref="subjectInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :isAdd="isAdd"
              />
            </el-collapse-item>
            <!-- <el-collapse-item title="推荐供应商列表" name="4"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250121.recommendedSupplierList')" name="4">
              <VendorInfo
                ref="vendorInfo"
                :value="recommvendorList"
                :form="form"
                :readonly="disabledFlag"
                :is-mobile="isMobile"
                :approval-flag="approvalFlag"
                :accompany-bid-list="accompanyBidList"
              />
            </el-collapse-item>
            <!-- <el-collapse-item v-if="!isMobile" title="附件信息" name="5"> -->
            <el-collapse-item v-if="!isMobile" :title="$t('bidMod.fileInfo')" name="5">
              <FileInfo ref="fileInfo" :value="recommvendorFileList" :readonly="disabledFlag" />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      <!-- </ApprovalProcess> -->
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import BaseInfo from './components/baseInfo'
import ApplyInfo from './components/applyInfo'
import SubjectInfo from './components/subjectInfo'
import VendorInfo from './components/vendorInfo'
import FileInfo from './components/fileInfo'
import { transformMQL } from 'lib@/utils/util'
import recommendHttp from '../../api'
import ApprovalProcess from 'modc@/components/approval-process'
import { validatePhone, validEmail } from '@/utils/validate'

export default {
  name: 'RecommendVendorDetail',
  components: {
    BaseInfo,
    ApplyInfo,
    SubjectInfo,
    VendorInfo,
    FileInfo,
    ApprovalProcess
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      colValue: ['1', '2', '3', '4', '5'],
      form: {
        projectId: null,
        extRecommendNo: null,
        extOrgBuName: null,
        extOrgOuName: null,
        extApplicantDepart: null,
        souNo: null,
        souRequirementNo: null,
        souRequirementId: null,
        createdFullName: null,
        creationDate: null,
        lastUpdateDate: null,
        extTechPrincipal: null,
        tel: null,
        projectStatus: 'DRAFT',
        extSouPrincipal: null,
        cancelReason: null,
        sourceFromNo: null,
        applicantNo: null,
        souName: null,
        sourceFromType: null,
        extBudget: null,
        extCategoryName: null,
        extScaleQuantity: null,
        extEarnestAmount: null,
        rcommendType: null,
        publishFlag: null,
        projectRemark: null,
        vendorFlairAdjure: null,
        vendorBizAdjure: null,
        preInviteTenders: null,
        remark: null
      },
      formRules: {
        souName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        extBudget: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        extCategoryName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        extScaleQuantity: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        projectRemark: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        vendorFlairAdjure: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        vendorBizAdjure: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        preInviteTenders: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        addVendorReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      projectId: null,
      recommvendorProjectExtend: [],
      recommvendorFileList: [],
      recommvendorList: [],
      accompanyBidList: [],
      // 扩展表字段
      // sourceFrom 单据来源，BID:招标计划池，SOU:寻源单
      extAttrs: [
        'extRecommendNo', 'recommendedVendorId', 'projectId', 'rcommendType', 'publishFlag',
        'projectRemark', 'vendorFlairAdjure', 'vendorBizAdjure', 'preInviteTenders', 'remark',
        'originalExtRecommendNo', 'originalExtRecommendId', 'addVendorReason', 'sourceFrom',
        'souRequirementId', 'souRequirementNo'
      ],
      originalRecommvendorList: [],
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECT', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    approvalFlag () { // 审批流页面字段展示标识
      return this.urlParams.approvalFlag || false
    },
    isMobile () { // 审批流页面字段展示标识--移动端
      return this.urlParams.isMobile || false
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECT', 'WITHDRAW'].includes(this.form.projectStatus)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.projectStatus) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.projectStatus)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.projectId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.projectId ||
              (['DRAFT'].includes(this.form.projectStatus) && this.urlParams.flag !== 'approve')
    },
    // 是否追加供应商
    isAdd () {
      return this.form.rcommendType === 'ADD'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  created () {
    let { projectId } = this.urlParams.row || {}
    this.projectId = projectId
    if (projectId) {
      if (this.urlParams.flag === 'append') {
        this.getFormDetail('append')
      } else {
        this.getFormDetail()
      }
    }
    this.getButtonConfig()
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'RCOMMVENDOR'
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      const procTitleObj = { extRecommendNo: this.form.extRecommendNo }
      return {
        procTitleObj
      }
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 下一步前置处理
    async preNextStepHandler () {
      if (!await this.validBill()) {
        this.__focus_error__()
        return false
      }
      const { recommvendorList } = this.initParams()
      if (!recommvendorList || !recommvendorList.length) {
        // this.$message.warning('推荐供应商列表不能为空')
        this.$message.warning(this.$t("cusEntry.supplement20250121.theRecommendedSupplierListCannotBeEmpty"))
        return false
      }
      for (let item of recommvendorList) {
        if (!item.linkmanName) {
          // this.$message.warning('推荐供应商列表 - 报名联系人不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.registrationContactPersonCannotBeEmpty"))
          return false
        }
        if (!item.phone) {
          // this.$message.warning('推荐供应商列表 - 报名联系电话不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.registrationContactPhoneNumberCannotBeEmpty"))
          return false
        }
        if (!item.email) {
          // this.$message.warning('推荐供应商列表 - 邮箱不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.emailCannotBeEmpty"))
          return false
        }
        if (!item.extVendorAttr) {
          // this.$message.warning('推荐供应商列表 - 供应商属性不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.supplierAttributeCannotBeEmpty"))
          return false
        }
        if (!item.extIsNewVendor) {
          // this.$message.warning('推荐供应商列表 - 是否新供应商不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.cantTheNewSupplierBeEmpty"))
          return false
        }
        if (item.phone && !validatePhone(item.phone)) {
          // this.$message.warning(`推荐供应商列表 - ${item.vendorName}报名联系电话格式不正确`)
          this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")} ${item.vendorName}${this.$t("cusEntry.supplement20250121.theFormatOfTheRegistrationContactPhoneNumberIsIncorrect")}`)
          return false
        }
        if (item.email && !validEmail(item.email)) {
          // this.$message.warning(`推荐供应商列表 - ${item.vendorName}邮箱不正确`)
          this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")} ${item.vendorName}${this.$t("cusEntry.supplement20250121.incorrectEmailAddress")}`)
          return false
        }
        // GSCP校验
        if (['Open', 'Changed'].includes(item.extGscp)) {
          // this.$message.warning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：有命中，业务流程卡住，不允许提交`)
          this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.thereIsAHitButTheBusinessProcessIsStuck")}`)
          return false
        }
        if (item.extGscp === 'TrueHitNoCooperation') {
          // this.$message.wraning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：真实命中，不建议合作，不允许提交`)
          this.$message.wraning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.trueHitNotRecommendedForCollaboration")}`)
          return false
        }
        if (item.extGscp === 'TrueHitCooperation') {
          // this.$message.wraning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：真实命中，但可以合作，请注意`)
          this.$message.wraning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.trueHitButCanCollaborate")}`)
          return false
        }
      }
      if (this.urlParams.flag === 'append' && !this.form.souNo && recommvendorList.length <= this.originalRecommvendorList.length) {
        // this.$message.warning('至少需要一个追加供应商才可以提交')
        this.$message.warning(this.$t("cusEntry.supplement20250121.atLeastOneAdditionalSupplierIsRequiredToSubmit"))
        return false
      }
      // 调用暂存接口
      let transformParams = transformMQL.save('RecommvendorProject', [this.initParams()], 'save')
      const response = await recommendHttp.save(transformParams)
      this.projectId = this.form.projectId = response.data[0].projectId
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveBill('SAVE')
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = this.disabledFlag
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('RecommendVendorList.getQueryData')
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      let extArr = []; let extObj = {}
      for (let key of this.extAttrs) {
        extObj[key] = this.form[key] ?? null
      }
      extArr = [extObj]
      params.recommvendorProjectExtend = extArr
      params.recommvendorList = JSON.parse(JSON.stringify(this.$refs.vendorInfo.getParams()))
      params.recommvendorList.forEach((item, index) => {
        item.sortIndex = index
        if (item.extVendorAttr) {
          item.extVendorAttr = item.extVendorAttr.join(';')
        }
      })
      params.recommvendorFileList = this.$refs.fileInfo.getParams()
      params.recommvendorFileList.forEach(item => {
        item.fileType = 'RECOMM'
      })
      console.log('params', params)
      return params
    },
    async getFormDetail  (type) {
      let transformParams = transformMQL.save('RecommvendorProject', [this.projectId], 'read',
        {
          '*': {},
          'recommvendorList': {
            '*': {}
          },
          'recommvendorFileList': {
            '*': {}
          },
          'recommvendorProjectExtend': {
            '*': {}
          }
        }
      )
      const response = await recommendHttp.read(transformParams)
      if (response.data.length) {
        const { recommvendorFileList, recommvendorList, recommvendorProjectExtend, accompanyBiddingList, ...rest } = response.data[0]
        this.form = rest
        if (recommvendorProjectExtend.length) {
          this.form = {
            ...rest,
            ...recommvendorProjectExtend[0]
          }
        }
        this.accompanyBidList = accompanyBiddingList
        this.recommvendorFileList = recommvendorFileList
        this.recommvendorList = recommvendorList.map(item => ({
          ...item,
          extVendorAttr: item.extVendorAttr ? item.extVendorAttr.split(';') : []
        }))
        // 记录原始供应商列表，用于追加供应商判断是否有新加的供应商
        this.originalRecommvendorList = JSON.parse(JSON.stringify(recommvendorList))
        if (type === 'append') {
          const { extRecommendNo, projectId } = this.form
          this.form.rcommendType = 'ADD'
          this.form.originalExtRecommendNo = extRecommendNo
          this.form.originalProjectId = projectId
          this.form.projectStatus = 'DRAFT'
          this.projectId = this.form.projectId = null
          this.form.extRecommendNo = null
          this.form.souNo = null
          this.recommvendorList.forEach(item => {
            item.isAppend = true
          })
        }
      }
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        // 其它待校验组件
        resolve(validForm)
      })
    },
    async saveBill (type) {
      if (type === 'SUBMIT') {
        if (!await this.validBill()) {
          this.__focus_error__()
          return false
        }
        const { recommvendorList } = this.initParams()
        if (!recommvendorList || !recommvendorList.length) {
          // this.$message.warning('推荐供应商列表不能为空')
          this.$message.warning(this.$t("cusEntry.supplement20250121.theRecommendedSupplierListCannotBeEmpty"))
          return false
        }
        for (let item of recommvendorList) {
          if (!item.linkmanName) {
            // this.$message.warning('推荐供应商列表 - 报名联系人不能为空')
            this.$message.warning(this.$t("cusEntry.supplement20250121.registrationContactPersonCannotBeEmpty"))
            return false
          }
          if (!item.phone) {
            // this.$message.warning('推荐供应商列表 - 报名联系电话不能为空')
            this.$message.warning(this.$t("cusEntry.supplement20250121.registrationContactPhoneNumberCannotBeEmpty"))
            return false
          }
          if (!item.email) {
            // this.$message.warning('推荐供应商列表 - 邮箱不能为空')
            this.$message.warning(this.$t("cusEntry.supplement20250121.emailCannotBeEmpty"))
            return false
          }
          if (!item.extVendorAttr) {
            // this.$message.warning('推荐供应商列表 - 供应商属性不能为空')
            this.$message.warning(this.$t("cusEntry.supplement20250121.supplierAttributeCannotBeEmpty"))
            return false
          }
          if (!item.extIsNewVendor) {
            // this.$message.warning('推荐供应商列表 - 是否新供应商不能为空')
            this.$message.warning(this.$t("cusEntry.supplement20250121.cantTheNewSupplierBeEmpty"))
            return false
          }
          if (item.phone && !validatePhone(item.phone)) {
            // this.$message.warning(`推荐供应商列表 - ${item.vendorName}报名联系电话格式不正确`)
            this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")} ${item.vendorName}${this.$t("cusEntry.supplement20250121.theFormatOfTheRegistrationContactPhoneNumberIsIncorrect")}`)
            return false
          }
          if (item.email && !validEmail(item.email)) {
            // this.$message.warning(`推荐供应商列表 - ${item.vendorName}邮箱不正确`)
            this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")} ${item.vendorName}${this.$t("cusEntry.supplement20250121.incorrectEmailAddress")}`)
            return false
          }
          // GSCP校验
          if (['Open', 'Changed'].includes(item.extGscp)) {
            // this.$message.warning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：有命中，业务流程卡住，不允许提交`)
            this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.thereIsAHitButTheBusinessProcessIsStuck")}`)
            return false
          }
          if (item.extGscp === 'TrueHitNoCooperation') {
            // this.$message.wraning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：真实命中，不建议合作，不允许提交`)
            this.$message.wraning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.trueHitNotRecommendedForCollaboration")}`)
            return false
          }
          if (item.extGscp === 'TrueHitCooperation') {
            // this.$message.wraning(`推荐供应商列表 -${item.vendorName}供应商GSCP结果为：真实命中，但可以合作，请注意`)
            this.$message.wraning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")}${item.vendorName}${this.$t("cusEntry.supplement20250121.trueHitButCanCollaborate")}`)
            return false
          }
        }
        if (this.urlParams.flag === 'append' && !this.form.souNo && recommvendorList.length <= this.originalRecommvendorList.length) {
          // this.$message.warning('至少需要一个追加供应商才可以提交')
          this.$message.warning(this.$t("cusEntry.supplement20250121.atLeastOneAdditionalSupplierIsRequiredToSubmit"))
          return false
        }
      }
      let transformParams = transformMQL.save('RecommvendorProject', [this.initParams()], type === 'SAVE' ? 'save' : 'submit')
      const response = type === 'SAVE' ? await recommendHttp.save(transformParams) : await recommendHttp.submit(transformParams)
      this.projectId = this.form.projectId = response.data[0].projectId
      this.__setTabTodo('RecommendVendorList.getQueryData')
      await this.getFormDetail()
      this.$message.success(this.$t('common.successSave'))
      if (type === 'SUBMIT') {
        this.handlerAfter(type)
      }
    }
  }
}
</script>
