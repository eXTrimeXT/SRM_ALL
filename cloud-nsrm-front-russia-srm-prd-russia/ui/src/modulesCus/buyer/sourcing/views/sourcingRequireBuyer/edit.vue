<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
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
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <el-collapse-item title="基础信息" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                @pubConfigChange="pubConfigChange"
              />
            </el-collapse-item>
            <el-collapse-item title="招募内容" name="2">
              <RecruitInfo
                ref="recruitInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                v-on="$listeners"
              />
            </el-collapse-item>
            <el-collapse-item title="对供应商要求" name="3">
              <VendorRequire
                ref="vendorRequire"
                :form.sync="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="报名联系方式" name="4">
              <LinkInfo
                ref="linkInfo"
                :form.sync="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="意向金" name="5">
              <IntentionInfo
                ref="intentionInfo"
                :form.sync="form"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="附件" name="6">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.fileUploads"
                scene-module-code="SCENE_SOU_REQ_ATTACHMENT"
                :business-id="reqHeadId"
                :editable="!disabledFlag"
                :needInit="false"
                fileInfo="fileInfo"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import BaseInfo from './components/baseInfo'
import RecruitInfo from './components/recruitInfo'
import VendorRequire from './components/vendorRequire'
import LinkInfo from './components/linkInfo'
import IntentionInfo from './components/intentionInfo'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { transformMQL } from 'lib@/utils/util'
import souHttp from '../../api'

export default {
  name: 'SourcingRequireDetail',
  components: {
    BaseInfo,
    RecruitInfo,
    VendorRequire,
    LinkInfo,
    IntentionInfo,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      colValue: ['1', '2', '3', '4', '5', '6'],
      reqHeadId: null,
      form: {
        reqHeadId: null,
        status: 'DRAFT',
        pubconfigId: null, // 寻源公示模板
        pubconfigName: null,
        organizationId: null, // 寻源公示模板对应板块
        organizationCode: null,
        organizationName: null,
        orgBuId: null,
        orgBuCode: null,
        orgBuName: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        reqDepartment: null,
        reqUserName: null,
        responsibilityUserName: null,
        souPersonUserName: null,
        isPreComm: null,
        createdFullName: null,
        creationDate: null,
        lastUpdateDate: null,
        reasonDesc: null,
        reqHeadNo: null,
        projectName: null,
        publicEndTime: null,
        totalAmountByTenKilo: null,
        categoryName: null,
        requireQuantity: null,
        requirementHeadNo: null,
        requireFrom: null,
        projectScope: null,
        vendorQualReq: null,
        technicalReq: null,
        performanceReq: null,
        projectAddress: null,
        contactName: null,
        phone: null,
        officePhone: null,
        isNeedDeposit: 'Y', // 是否需缴纳意向金
        depositAmount: null,
        bankName: null,
        bankNumber: null,
        bankAccount: null,
        bankAccountName: null,
        fileUploads: []
      },
      formRules: {
        orgBuName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        orgName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        pubconfigName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        projectName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        publicEndTime: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        totalAmountByTenKilo: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        categoryName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        requireQuantity: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        projectScope: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        vendorQualReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        technicalReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        performanceReq: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        isNeedDeposit: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        depositAmount: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'SOURCE_PUBLISH',
        fileType: 'images'
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
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.status) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.reqHeadId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.reqHeadId ||
              (['DRAFT'].includes(this.form.status) && this.urlParams.flag !== 'approve')
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
    this.reqHeadId = this.urlParams.row.reqHeadId
    if (this.reqHeadId) {
      this.getFormDetail()
    } else {
      this.loadFileInfo()
    }
    this.getButtonConfig()
  },
  methods: {
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = this.disabledFlag
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    pubConfigChange (val) {
      const { depositAmount, totalAmountByTenKilo } = this.form
      if (['NB', 'GF', 'MD', 'JG'].includes(val.organizationCode) && totalAmountByTenKilo) {
        this.form.depositAmount = this.depositAmountChange(val.organizationCode, totalAmountByTenKilo)
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('SourcingRequireList.getQueryData')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'souReqHead'
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
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
    },
    depositAmountChange (code, amount) {
      let total = 0
      if (code === 'NB') {
        if (amount > 10 && amount <= 100) {
          total = 200
        } else if (amount > 100 && amount <= 300) {
          total = 400
        } else if (amount > 300 && amount <= 500) {
          total = 590
        } else if (amount > 500 && amount <= 1000) {
          total = 790
        } else if (amount > 1000 && amount <= 3000) {
          total = 990
        } else if (amount > 3000) {
          total = 1200
        }
      }
      if (code === 'GF') {
        if (amount > 10 && amount <= 50) {
          total = 100
        } else if (amount > 50 && amount <= 300) {
          total = 300
        } else if (amount > 300 && amount <= 500) {
          total = 500
        } else if (amount > 500 && amount <= 1000) {
          total = 600
        } else if (amount > 1000 && amount <= 3000) {
          total = 800
        } else if (amount > 3000 && amount <= 10000) {
          total = 1000
        } else if (amount > 10000 && amount <= 30000) {
          total = 1500
        } else if (amount > 30000) {
          total = 2000
        }
      }
      if (code === 'MD') {
        if (amount > 10 && amount <= 100) {
          total = 150
        } else if (amount > 100 && amount <= 300) {
          total = 350
        } else if (amount > 300 && amount <= 500) {
          total = 550
        } else if (amount > 500 && amount <= 1000) {
          total = 750
        } else if (amount > 1000 && amount <= 3000) {
          total = 950
        } else if (amount > 3000) {
          total = 1150
        }
      }
      if (code === 'JG') {
        if (amount > 10 && amount <= 100) {
          total = 180
        } else if (amount > 100 && amount <= 300) {
          total = 380
        } else if (amount > 300 && amount <= 500) {
          total = 580
        } else if (amount > 500 && amount <= 1000) {
          total = 780
        } else if (amount > 1000 && amount <= 3000) {
          total = 980
        } else if (amount > 3000) {
          total = 1180
        }
      }
      return total
    },
    async getFormDetail  () {
      let transformParams = transformMQL.save('SouReqHeadBuyer', [this.reqHeadId], 'read',
        {
          '*': {},
          'souReqApplyList': {
            '*': {}
          },
          'fileUploads': {
            '*': {}
          }
        }
      )
      const response = await souHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        if (!this.form.isNeedDeposit) this.form.isNeedDeposit = 'Y' // 是否需意向金默认开启
        if (this.form.publicEndTime) {
          let publicEndTime = new Date(this.form.publicEndTime)
          publicEndTime.setHours(17, 0, 0)
          this.form.publicEndTime = this.$dayjs(publicEndTime).format('YYYY-MM-DD HH:mm:ss')
        }
        console.log('publicEndTime', this.form.publicEndTime)
        this.loadFileInfo()
      }
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        resolve(validForm)
      })
    },
    async saveBill (type) {
      if (type === 'SUBMIT') {
        if (!await this.validBill()) {
          this.__focus_error__()
          return
        }
        // 公示截止时间是否晚于当前时间
        if (new Date(this.form.publicEndTime) < new Date().getTime()) {
          this.$message.warning(this.$t('cusEntry.tipMessage.publicEndTimeMsg'))
          return false
        }
      }
      let transformParams = transformMQL.save('SouReqHeadBuyer', [this.form], type === 'SAVE' ? 'save' : 'submit')
      const response = type === 'SAVE' ? await souHttp.save(transformParams) : await souHttp.submit(transformParams)
      this.reqHeadId = this.form.reqHeadId = response.data[0].reqHeadId
      await this.getFormDetail()
      this.$message.success(this.$t('common.successSave'))
      if (type === 'SUBMIT') {
        await this.handlerAfter(type)
      }
    }
  }
}
</script>
