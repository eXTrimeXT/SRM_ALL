<template>
  <el-container>
    <el-main>
      <!-- <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="goBack"
      > -->
      <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="BORROW"
        :approval-status="detailForm.status"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
        @get-flow-node-list="getFlowNodeList"
      >
        <el-collapse v-model="activeNum">
          <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
            <el-form
              ref="detailForm"
              :model="detailForm"
              :disabled="isReadOnly"
            >
              <srm-row>
                <srm-col :init-col="3">
                  <!-- 招标项目编号 -->
                  <el-form-item
                    :label="$t('bidMod.bidingNumCla')"
                    :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: 'change' }]"
                    prop="bidingNum"
                  >
                    <el-input v-model="detailForm.bidingNum" />
                    <!-- <QuickSearch
                      :disable="isReadOnly"
                      :showInput="detailForm.bidingNum"
                      show-key="bidingNum"
                      :scope-data="detailForm"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_npm_sou_project"
                      @close-quicksearch="getBidObj"
                    /> -->
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('purchaseDemand.applicant')"
                    prop="applyUserName"
                  >
                    <el-input v-model="detailForm.applyUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('cusEntry.common.contactInfo')"
                    prop="applyContacts"
                  >
                    <el-input v-model="detailForm.applyContacts" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('bidMod.bidingName')"
                    :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: 'change' }]"
                    prop="bidingName"
                  >
                    <el-input v-model="detailForm.bidingName" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('cusEntry.common.applicantUnit')"
                    prop="orgName"
                  >
                    <el-input v-model="detailForm.orgName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('cusEntry.common.applyDepartment')"
                    prop="departmentName"
                  >
                    <el-input v-model="detailForm.departmentName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-tooltip class="item" effect="dark" :content="$t('cusEntry.biddingSettings.borrowTypeTips')" placement="top">
                    <el-form-item
                      :label="$t('cusEntry.biddingSettings.borrowDataType')"
                      prop="borrowType"
                      :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: 'change' }]"
                    >
                      <el-input v-model="detailForm.borrowType" />
                    </el-form-item>
                  </el-tooltip>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('cusEntry.common.isInvolveQuota')"
                    prop="priceFlag"
                    :rules="[{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }]"
                  >
                    <dict-select v-model="detailForm.priceFlag" code="YES_OR_NO" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    :label="$t('cusEntry.common.useWay')"
                    :rules="[{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }]"
                    prop="useType"
                  >
                    <dict-select v-model="detailForm.useType" code="BORROW_USETYPE" :disabled="isReadOnly" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <el-form-item
                    label="单据状态"
                    prop="status"
                  >
                    <dict-select v-model="detailForm.status" code="BORROW_STATUS" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="detailForm.priceFlag=='Y'" :init-col="3">
                  <!-- 被借阅单位总经理 -->
                  <el-form-item
                    :label="$t('cusEntry.supplement20250205.borrowedUnitGeneralManager')"
                    prop="managerName"
                    :rules="[{ required: true, message: this.$t('common.pleaseSelect'), trigger: 'change' }]"
                  >
                    <QuickSearch
                      :disabled="isReadOnly"
                      :show-input="detailForm.managerName"
                      show-key="username"
                      :scope-data="detailForm"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="1">
                  <el-form-item
                    :label="$t('cusEntry.common.applyUseReason')"
                    :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: 'blur' }]"
                    prop="borrowCause"
                  >
                    <el-input
                      v-model="detailForm.borrowCause"
                      type="textarea"
                      :rows="4"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <el-collapse-item :title="$t('cusEntry.common.attention')" name="2">
            <p>{{ $t('cusEntry.biddingSettings.loanTip1') }}</p>
            <p>{{ $t('cusEntry.biddingSettings.loanTip2') }}</p>
          </el-collapse-item>
          <el-collapse-item v-if="(['APPROVING'].includes(detailForm.status) && inputFlag) || detailForm.status === 'APPROVED'" :title="$t('contractMod.addUploadFile')" name="3">
            <el-button
              v-if="inputFlag"
              type="primary"
              style="margin-bottom: 10px;"
              @click="addUploadOne"
            >
              {{ $t('common.add') }}
            </el-button>
            <el-table
              border
              :data="attachList"
              style="width: 100%"
              max-height="250px"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="60"
              />
              <el-table-column
                align="center"
                prop="attachName"
                :label="$t('bidMod.fileName')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <!-- <el-button type="text" @click="getFile(scope.row)">
                    {{ scope.row.attachName }}
                  </el-button> -->
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.attachId,
                      fileName: scope.row.attachName
                    }"
                    :readonly="!inputFlag"
                    @on-change="({file}) => uploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="inputFlag"
                align="center"
                :label="$t('common.operation')"
                width="120"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteItem(scope.$index,scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </ApprovalProcess>
      <!-- </CWorkflowMulti> -->
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { transformMQL } from 'lib@/utils/util'
import WorkflowCommon from '@/library/mixins/workflow-common'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'LoanApplyDetail',
  components: {
    CToolbar,
    QuickSearch,
    ApprovalProcess
  },
  mixins: [tabTodoWatch, tabTodoMixin, WorkflowCommon],
  data () {
    return {
      isReadOnly: false,
      activeNum: ['1', '2', '3'],
      detailForm: {
        status: 'DRAFT',
        bidingNum: null,
        bidingName: null,
        bidingId: null,
        applyUserName: null,
        applyContacts: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        departmentId: null,
        departmentCode: null,
        departmentName: null,
        borrowType: null,
        priceFlag: null,
        useType: null,
        managerName: null,
        managerCode: null,
        borrowCause: null
      },
      attachList: [],
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'BORROW_APPLICATION', // 功能
        fileType: 'images' // 类型
      },
      inputFlag: false, // 审批流页面字段可编辑标识
      operationPreOptions: {
        pass: this.prePassHandler,
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
    username () {
      return this.$store.getters.userInfo.username || ''
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.detailForm ? this.detailForm.borrowId : null
    },
    workflowTabDisabled () {
      // 拟定状态的单据 流程审批按钮失效
      return ['DRAFT'].includes(this.detailForm.status)
    },
    viewUpdateButton () {
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.detailForm.status) && !this.isReadOnly
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.detailForm.status) &&
        this.workflowParamsInfo.integrationMode === 'Push' &&
        this.$attrs.params.flag === 'approval'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  created () {
    const { nickname, phone, username } = this.$store.getters.userInfo
    const { flag, row } = this.$attrs.params
    this.isReadOnly = ['view', 'approval'].includes(flag)
    if (flag === 'add') {
      this.detailForm.applyUserName = nickname
      this.detailForm.applyContacts = phone
      this.getUserCompany(username)
    } else {
      this.getFormDetail(row.borrowId)
    }

    this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 获取审批流程节点
    getFlowNodeList (flowNodeList = []) {
      const obj = flowNodeList.find(item => item.taskStatus === '1') // 当前节点 taskStatus = '1'
      if (['资料管理员', '区域负责人'].includes(obj?.activityName) && obj?.executor.map(item => item.userId).includes(this.username)) {
        this.inputFlag = true
      }
    },
    // 通过前置处理
    async prePassHandler () {
      if (this.inputFlag) {
        // 调用附件上传接口
        const bol = this.attachList.some(item => !item.attachId) || this.attachList.length == 0
        if (bol) {
          // 请上传附件
          this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips12'))
          return false
        }
        const saveData = transformMQL.save('Borrow', this.attachList, 'saveOrUpdateBorrowAttach')
        await this.$http({
          url: '/api-sou/api-ql/Borrow/saveOrUpdateBorrowAttach',
          method: 'POST',
          data: saveData,
          loading: true
        })
      }
      return true
    },
    // 下一步前置处理
    async preNextStepHandler () {
      let validForm = false
      await this.$refs.detailForm.validate(valid => {
        validForm = valid
      })
      if (!validForm) {
        // 请填写完必填项
        this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseCompleteTheRequiredFields'))
        return false
      }
      // 调用暂存接口
      const saveData = transformMQL.save('Borrow', [this.detailForm], 'saveOrUpdate')
      const response = await this.$http({
        url: '/api-sou/api-ql/Borrow/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      })
      this.detailForm = response.data[0]
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveLoanApply('SAVE')
        break
      case 'submit':
        this.goBack()
        break
      default:
        break
      }
    },
    addUploadOne () {
      this.attachList.push({
        borrowId: this.detailForm.borrowId,
        attachId: null,
        attachName: null
      })
    },
    uploadSuccess (file, row) {
      const { fileId, fileName } = file || {}
      row.attachId = fileId
      row.attachName = fileName
    },
    // 删除
    deleteItem (index, row) {
      this.attachList.splice(index, 1)
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 指定工作流的业务类型，在定义工作流时指定
    getWorkflowBusinessType () {
      return 'BORROW' // 这里之后是和后端对接配置好的流程审批模块
    },
    goBack () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'loanApplyDetail' + this.$attrs.params.row.borrowId)
      } else {
        this.$emit('tab-remove', 'loanApplyDetail')
      }
      this.__setTabTodo('loanApplyList.getQueryData')
    },
    getFile (row) {
      this.$http({
        url: 'api-pj/external/bpm/getFileToCode',
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          let newToken = res.data
          let url = row.attachPath
          let newUrl = url.replace(/token=.*?(&|$)/, 'token=' + newToken + '$1')
          window.open(newUrl, '_blank')
        }
      })
    },
    // 查询用户公司和部门
    getUserCompany (username) {
      this.$http({
        url: `/api-base/pj-anon/user/getHrUserOrgnizationByUsername?username=${username}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { ouOrganization = {}, departmentOrganization = {} } = res.data
          this.detailForm.orgId = ouOrganization.organizationId // 公司
          this.detailForm.orgCode = ouOrganization.organizationCode // 公司
          this.detailForm.orgName = ouOrganization.organizationName // 公司
          this.detailForm.departmentId = departmentOrganization.organizationId // 部门
          this.detailForm.departmentCode = departmentOrganization.organizationCode // 部门
          this.detailForm.departmentName = departmentOrganization.organizationName // 部门
        }
      })
    },
    getBidObj (val, scope) {
      scope.bidingName = val ? val.souName : '' // 招标项目名称
      scope.bidingNum = val ? val.extProjectNo : '' // 招标项目编号
      scope.bidingId = val ? val.projectId : '' // 招标项目id
    },
    getUserObj (val, scope) {
      scope.managerCode = val ? val.username : '' // 总经理工号
      scope.managerName = val ? val.nickname : '' // 总经理姓名
    },
    getFormDetail (borrowId) {
      const searchData = transformMQL.save(
        'Borrow',
        [borrowId],
        'read',
        {
          '*': {},
          'attachList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sou/api-ql/Borrow/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { attachList = [], ...rest } = res.data[0]
          this.detailForm = { ...rest }
          this.attachList = attachList
        }
      })
    },
    saveOrSubmitBill (type) { // 点击 提交审批或保存按钮
      if (type === 'SUBMIT') {
        this.submitLoanApply(type)
      } else if (type === 'SAVE') {
        this.saveLoanApply(type)
      }
    },
    saveLoanApply (type) {
      const saveData = transformMQL.save('Borrow', [this.detailForm], 'saveOrUpdate')
      this.$http({
        url: '/api-sou/api-ql/Borrow/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(async res => {
        this.detailForm = res.data[0]
        this.getFormDetail(this.detailForm.borrowId)
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.success'))
          this.__setTabTodo('loanApplyList.getQueryData')
        } else {
          await this.handlerAfter(type, 'Y', () => {
            this.__setTabTodo('loanApplyList.getQueryData')
          })
        }
      })
    },
    submitLoanApply (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          this.saveLoanApply(type)
        } else {
          return this.$message.warning(this.$t('common.pleasefinishRequired'))
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
