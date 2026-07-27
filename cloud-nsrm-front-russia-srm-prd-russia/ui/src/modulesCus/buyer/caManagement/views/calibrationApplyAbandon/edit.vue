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
        business-type="SOU_DCA"
        :approvalStatus="form.status"
        :status-map="statusMap"
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
                :approval-flag="approvalFlag"
                :readonly="true"
              />
            </el-collapse-item>
            <el-collapse-item title="业务信息" name="2">
              <BusinessInfo
                ref="businessInfo"
                :form.sync="form"
                :readonly="true"
                :approval-flag="approvalFlag"
                v-on="$listeners"
              />
            </el-collapse-item>
            <el-collapse-item title="原定标申请附件" name="3">
              <FileDynamic
                ref="originalSceneAttachment"
                v-model="form.originalSceneFiles"
                scene-module-code="SCENE_SOU_CA_ATTACHMENT"
                :business-id="originalCaId"
                :editable="false"
              />
            </el-collapse-item>
            <el-collapse-item title="附件信息" name="4">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.sceneFiles"
                scene-module-code="SCENE_SOU_DCA_ATTACHMENT"
                :business-id="caId"
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
import BusinessInfo from './components/businessInfo'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { transformMQL } from 'lib@/utils/util'
import caHttp from './api'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'CalibrationApplyAbandonDetail',
  components: {
    BaseInfo,
    BusinessInfo,
    FileDynamic,
    ApprovalProcess
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      colValue: ['1', '2', '3', '4'],
      form: {
        caId: null,
        caNo: null,
        originalCaId: null,
        originalCaNo: null,
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
        abandonDesc: null,
        applicantNo: null,
        ifWrite: null,
        approvalNickname: null,
        discardDescription: null,
        caSuppliers: [],
        caSelectionResults: [],
        sceneFiles: [],
        originalSceneFiles: []
      },
      formRules: {
        // extOrgOuName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // souName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // extBudget: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // projectOverviewAndBidScope: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // paymentRequirements: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        // abandonDesc: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
      },
      caId: null,
      originalCaId: null,
      caNegotiateExtendTable: [],
      caNegotiateExtendTableHeader: [],
      winOptions: [],
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
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
      return ['DRAFT', 'REJECTED', 'WITHDRAW', ''].includes(this.form.status) && !['view'].includes(this.urlParams.flag)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.status) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.caId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.caId ||
              (['DRAFT', ''].includes(this.form.status) && this.urlParams.flag !== 'approval')
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
    let { caId, originalCaId } = this.urlParams.row
    this.caId = caId
    this.originalCaId = originalCaId
    if (this.urlParams.flag === 'add' && !caId) {
      this.getOriginalFormDetail('init')
      this.loadFileInfo('sceneAttachment')
    } else {
      this.getFormDetail()
      this.getOriginalFormDetail()
    }
    this.getButtonConfig()
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      if (!await this.validBill()) return false
      // 调用暂存接口
      let { caSuppliers, caSelectionResults, originalSceneFiles, ...rest } = this.form
      let transformParams = transformMQL.save('Dca', [rest], 'save')
      const response = await caHttp.save(transformParams)
      this.caId = this.form.caId = response.data[0].caId
      this.businessId = this.caId
      if (!this.businessId) {
        this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
        return false
      }
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      let { caSuppliers, caSelectionResults, originalSceneFiles, ...rest } = this.form
      let transformParams = transformMQL.save('Dca', [rest], 'save')
      switch (type) {
      case 'save':
        caHttp.save(transformParams).then(res => {
          this.$message.success(this.$t('common.successSave'))
          this.caId = res.data[0].caId
          this.getFormDetail()
          this.__setTabTodo('CalibrationApplyAbandonList.getQueryData')
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
      this.__setTabTodo('CalibrationApplyAbandonList.getQueryData')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'SOU_DCA'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }

      return params
    },
    // 从定标申请过来，从编辑过来
    async getOriginalFormDetail  (type) {
      let transformParams = transformMQL.save('Ca', [this.originalCaId], 'read',
        {
          '*': {},
          'caOrders': {
            '*': {}
          },
          'caSuppliers': {
            '*': {}
          },
          'caSelectionResults': {
            '*': {}
          },
          'sceneFiles': {
            '*': {}
          }
        }
      )
      const response = await caHttp.originalRead(transformParams)
      if (response.data.length) {
        let { caOrders, caSuppliers, caSelectionResults, sceneFiles, caId, caNo, ...rest } = response.data[0]
        if (type === 'init') this.form = rest
        this.form.originalSceneFiles = sceneFiles
        this.form.caSuppliers = caSuppliers
        this.form.caOrders = caOrders
        this.form.caSelectionResults = caSelectionResults
        if (type === 'init') {
          this.form.sceneFiles = []
          this.form.status = 'DRAFT'
          this.form.type = 'DESTORY'
          this.form.originalCaId = caId
          this.form.originalCaNo = caNo
        }
        this.loadFileInfo('originalSceneAttachment')
      }
    },
    async getFormDetail () {
      let transformParams = transformMQL.save('Dca', [this.caId], 'read',
        {
          '*': {},
          // 'caSuppliers': {
          //   '*': {}
          // },
          // 'caSelectionResults': {
          //   '*': {}
          // },
          'sceneFiles': {
            '*': {}
          }
          // 'originalSceneFiles': {
          //   '*': {}
          // }
        }
      )
      const response = await caHttp.read(transformParams)
      if (response.data.length) {
        const { sceneFiles, ...rest } = response.data[0]
        this.form.sceneFiles = sceneFiles
        for (let key in rest) {
          this.form[key] = rest[key]
        }
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
      }
      let { caSuppliers, caSelectionResults, originalSceneFiles, ...rest } = this.form
      let transformParams = transformMQL.save('Dca', [rest], type === 'SAVE' ? 'save' : 'submit')
      const response = type === 'SAVE' ? await caHttp.save(transformParams) : await caHttp.submit(transformParams)
      this.caId = this.form.caId = response.data[0].caId
      await this.getFormDetail()
      this.$message.success(this.$t('common.successSave'))
      if (type === 'SUBMIT') {
        await this.handlerAfter(type)
      }
    }
  }
}
</script>
