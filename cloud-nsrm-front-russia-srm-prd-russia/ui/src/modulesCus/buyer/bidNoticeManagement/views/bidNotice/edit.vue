<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <!-- <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      > -->
      <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="SOU_TN"
        :approvalStatus="form.status"
        :status-map="statusMap"
        :projectName="form.souName"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <el-collapse-item title="基础信息" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :is-third-party="$attrs.params.isThirdParty"
                :readonly="disabledFlag"
                :approval-flag="approvalFlag"
                v-on="$listeners"
              />
            </el-collapse-item>
            <el-collapse-item title="中/落标信息" name="2">
              <BidResult
                ref="bidResult"
                :value="bidNoticeDetails"
                :form="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="内部通知书" name="3">
              <InnerNotice
                ref="innerNotice"
                :value.sync="bidNoticeInternals"
                :form="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="附件信息" name="4">
              <FileDynamic
                ref="sceneAttachment"
                v-model="sceneFiles"
                scene-module-code="SCENE_SOU_TN_ATTACHMENT"
                :business-id="bidNoticeId"
                :editable="!disabledFlag"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </ApprovalProcess>
      <!-- </CWorkflowMulti> -->
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import BaseInfo from './components/baseInfo'
import BidResult from './components/bidResult'
import InnerNotice from './components/innerNotice'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { transformMQL } from 'lib@/utils/util'
import bidNoticeHttp from './api'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'CalibrationApplyDetail',
  components: {
    BaseInfo,
    BidResult,
    InnerNotice,
    ApprovalProcess,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      colValue: ['1', '2', '3', '4'],
      form: {
        bidNoticeId: null,
        bidNoticeNo: null,
        caId: null,
        caNo: null,
        status: 'DRAFT',
        extOrgBuId: null,
        extOrgBuCode: null,
        extOrgBuName: null,
        extOrgOuId: null,
        extOrgOuCode: null,
        extOrgOuName: null,
        demandDepartmentName: null,
        demandUserName: null,
        createdFullName: null,
        creationDate: null,
        lastUpdateDate: null,
        demandUserNickname: null,
        extProjectNo: null,
        souName: null,
        warrantyPeriod: null,
        extBudget: null,
        timeLimit: null,
        releaseTime: null,
        bidClosingTime: null,
        bidEvaluationEndTime: null,
        priceOpeningTime: null,
        projectOverviewAndBidScope: null,
        paymentRequirements: null,
        remark: null,
        discardReason: null
      },
      formRules: {
        // extOrgOuName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // souName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
      },
      bidNoticeId: null,
      sceneFiles: [],
      bidNoticeDetails: [],
      bidNoticeInternals: [],
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    approvalFlag () { // 审批流页面字段展示标识
      return this.urlParams.approvalFlag || false
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status) && !['view'].includes(this.urlParams.flag)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.status) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.bidNoticeId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.bidNoticeId ||
              (['DRAFT'].includes(this.form.status) && this.urlParams.flag !== 'approval')
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
    this.bidNoticeId = this.urlParams.row.bidNoticeId
    if (this.bidNoticeId) {
      this.getFormDetail()
    } else {
      this.loadFileInfo()
    }
    this.getButtonConfig()
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      if (!await this.validBill()) return false
      const { bidNoticeDetails, bidNoticeInternals } = this.initParams()
      for (let item of bidNoticeDetails) {
        if (!item.isWin) {
          this.$message.warning('中/落标信息-【是否中标】不能为空')
          return false
        }
        if (item.isWin === 'Y' && !item.winAmount) {
          this.$message.warning('中/落标信息-中标供应商【中标金额】不能为空')
          return false
        }
        if (item.isWin === 'Y' && !item.isPerformanceEvaluated) {
          this.$message.warning('中/落标信息-中标供应商【是否履约评价】不能为空')
          return false
        }
        if (item.isWin === 'Y' && item.isPerformanceEvaluated === 'N' && !item.nonPerformanceReason) {
          this.$message.warning('中/落标信息-中标供应商,是否履约评价为否，【不履约评价的原因】不能为空')
          return false
        }
        if (item.isWin === 'Y' && !item.noticeAttachmentId) {
          this.$message.warning('中/落标信息-中标供应商【通知书附件】不能为空')
          return false
        }
        if (item.isWin === 'Y' && (!item.contractSignUnitList || !item.contractSignUnitList.length)) {
          this.$message.warning('中/落标信息-中标供应商【合同签署单位】不能为空')
          return false
        }
      }
      for (let item of bidNoticeInternals) {
        if (!item.attachmentId) {
          this.$message.warning('内部通知书-通知书附件不能为空')
          return false
        }
      }
      // 调用暂存接口
      let transformParams = transformMQL.save('BidNotice', [this.initParams()], 'save')
      const response = await bidNoticeHttp.save(transformParams)
      this.bidNoticeId = this.form.bidNoticeId = response.data[0].bidNoticeId
      this.businessId = this.bidNoticeId
      if (!this.businessId) {
        this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
        return false
      }
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      let transformParams = transformMQL.save('BidNotice', [this.initParams()], 'save')
      switch (type) {
      case 'save':
        bidNoticeHttp.save(transformParams).then(res => {
          this.$message.success(this.$t('common.successSave'))
          this.bidNoticeId = this.form.bidNoticeId = res.data[0].bidNoticeId
          this.getFormDetail()
          this.__setTabTodo('BidNoticeList.getQueryData')
        })
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
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
      this.__setTabTodo('BidNoticeList.getQueryData')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'SOU_TN'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    initParams () { // 参数
      let params = this.form
      params.bidNoticeDetails = this.$refs.bidResult.getParamsData()
      params.bidNoticeInternals = this.bidNoticeInternals
      params.sceneFiles = this.sceneFiles
      return params
    },
    async getFormDetail  () {
      let transformParams = transformMQL.save('BidNotice', [this.bidNoticeId], 'read',
        {
          '*': {},
          'bidNoticeDetails': {
            '*': {}
          },
          'bidNoticeInternals': {
            '*': {}
          },
          'sceneFiles': {
            '*': {}
          }
        }
      )
      const response = await bidNoticeHttp.read(transformParams)
      if (response.data.length) {
        const { bidNoticeDetails, bidNoticeInternals, sceneFiles, ...rest } = response.data[0]
        this.form = rest
        this.bidNoticeDetails = bidNoticeDetails
        this.bidNoticeDetails.forEach(item => {
          item.contractSignUnitList = item.contractSignUnit ? item.contractSignUnit.toString().split(',') : []
          if (item.isWin === 'N') { // 中标为否 默认不履约评价
            item.isPerformanceEvaluated = 'N'
          }
        })
        this.bidNoticeInternals = bidNoticeInternals
        this.sceneFiles = sceneFiles
        this.loadFileInfo()
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
        if (!await this.validBill()) return
        const { bidNoticeDetails, bidNoticeInternals } = this.initParams()
        for (let item of bidNoticeDetails) {
          if (!item.isWin) {
            return this.$message.warning('中/落标信息-【是否中标】不能为空')
          }
          if (item.isWin === 'Y' && !item.winAmount) {
            return this.$message.warning('中/落标信息-中标供应商【中标金额】不能为空')
          }
          if (item.isWin === 'Y' && !item.isPerformanceEvaluated) {
            return this.$message.warning('中/落标信息-中标供应商【是否履约评价】不能为空')
          }
          if (item.isWin === 'Y' && item.isPerformanceEvaluated === 'N' && !item.nonPerformanceReason) {
            return this.$message.warning('中/落标信息-中标供应商,是否履约评价为否，【不履约评价的原因】不能为空')
          }
          if (item.isWin === 'Y' && !item.noticeAttachmentId) {
            return this.$message.warning('中/落标信息-中标供应商【通知书附件】不能为空')
          }
          if (item.isWin === 'Y' && (!item.contractSignUnitList || !item.contractSignUnitList.length)) {
            return this.$message.warning('中/落标信息-中标供应商【合同签署单位】不能为空')
          }
        }
        for (let item of bidNoticeInternals) {
          if (!item.attachmentId) {
            return this.$message.warning('内部通知书-通知书附件不能为空')
          }
        }
      }
      let handleType = type.toLowerCase()
      let transformParams = transformMQL.save('BidNotice', [this.initParams()], handleType)
      const response = await bidNoticeHttp[handleType](transformParams)
      this.bidNoticeId = this.form.bidNoticeId = response.data[0].bidNoticeId
      await this.getFormDetail()
      this.$message.success(this.$t('common.successSave'))
      if (type === 'SUBMIT') {
        await this.handlerAfter(type)
      }
    }
  }
}
</script>
