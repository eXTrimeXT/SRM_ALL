<template>
  <el-container
    class="questManagementFlow"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => saveDataHandle(type)"
        @submit-direct="(type) => saveDataHandle(type)"
        @confirm="(type, comment) => confirmDataHandle(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <div class="fillInfoSec">
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
            >
              <srm-row :gutter="32">
                <!-- 调查模板类型 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questTemplateTypeName"
                    :label="$t('vendorMod.questTemplateType')"
                  >
                    <el-input
                      v-model="form.questTemplateTypeName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 调查表编码 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questNo"
                    :label="$t('vendorMod.questNo')"
                  >
                    <el-input
                      v-model="form.questNo"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 调查表名称 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questName"
                    :label="$t('vendorMod.questName')"
                  >
                    <el-input
                      v-model="form.questName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!-- 业务组织 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questTemplateOrgName"
                    :label="$t('vendorMod.questTemplateOrgName')"
                  >
                    <el-input
                      v-model="form.questTemplateOrgName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row :gutter="32">
                <!-- 反馈备注 -->
                <srm-col :initCol="2">
                  <el-form-item
                    prop="questFeedback"
                    :label="$t('vendorMod.questFeedback')"
                  >
                    <el-input
                      v-model="form.questFeedback"
                      :disabled="curOpt === 'view' || curRole === 'BUYER'"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 审批状态 -->
                <srm-col :initCol="2">
                  <el-form-item
                    prop="approvalStatus"
                    :label="$t('vendorMod.approvalStatus2')"
                  >
                    <DictSelect
                      v-model="form.approvalStatus"
                      code="QUEST_SUPPLIER_APPROVE_STATUS"
                      filterable
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <div class="fillInfo">
              <renderForm
                v-if="form.questTemplateId"
                ref="renderForm"
                :quest-template-id="form.questTemplateId"
                :quest-sup-id="form.questSupId"
                :disabled="readOnly"
                :com-data="form.groupInfoList"
                :opt-type="curOpt"
              />
            </div>
          </div>
        </div>
        <CToolbar>
          <template #right>
            <el-button
              v-if="curOpt !== 'view'"
              @click="cancelBill"
            >
              {{ $t('vendorMod.relegation.abrogate') }}
            </el-button>
          </template>
        </CToolbar>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import renderForm from 'modb@/vendorManagementBuyer/views/questTemplate/renderForm'
import { adaptDictData, parseTime, findMenuIdByPath } from '@/utils'
import WorkflowCommon from '@/library/mixins/workflow-common'

export default {
  name: 'QuestManagementFlow',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    FormWrapper,
    TableView,
    renderForm
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      investigationCode: '',
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      approveStatusList: [], // 审批状态
      questTemplateTypeList: [], // 调查模板类型
      orgIdList: [], // 模板所属组织ID
      yesOrNoList: [], // 是否
      form: {
        questSupId: null,
        questNo: null,
        questName: null,
        questTemplateId: null,
        questTemplateType: null,
        questTemplateTypeName: null,
        questTemplateOrgId: null,
        questTemplateOrgCode: null,
        questTemplateOrgName: null,
        questFeedback: '',
        approvalStatus: '',
        groupInfoList: [],
        fieldInfoList: []
      },
      rules: {},
      readOnly: this.$attrs.params.flag == 'view',
      curRole: this.$store.getters.userType,
      curOpt: 'view',
      buttonCustom: {
        prePassData: {
          name: this.$t('dashboard.prePassData'),
          view: true,
          disabled: false
        },
        preRejectedData: {
          name: this.$t('dashboard.preRejectedData'),
          view: true,
          disabled: false
        },
        submit: {
          name: this.$t('purchaseDemand.submitAudit'),
          view: false,
          disabled: false
        }
      }
    }
  },
  computed: {
    viewUpdateButton () {
      return this.curRole === 'BUYER' && !this.readOnly && this.form.approvalStatus === 'PRE_PASS'
    },
    viewPrePassButton () {
      return this.curRole === 'BUYER' && this.form.approvalStatus === 'WRITED'
    },
    viewPreRejectedButton () {
      return this.curRole === 'BUYER' && this.form.approvalStatus === 'WRITED'
    },
    disabledUpdateButton () {
      return this.form.approvalStatus === 'SUBMITTED' || this.form.approvalStatus === 'APPROVED'
    },
    disabledPrePassButton () {
      return (
        this.form.approvalStatus === 'PRE_PASS' || this.form.approvalStatus === 'PRE_REJECTED'
      )
    },
    disabledPreRejectedButton () {
      return this.form.approvalStatus === 'PRE_PASS' || this.form.approvalStatus === 'PRE_REJECTED'
    },
    workflowBusinessId () {
      return this.form.questSupId ? this.form.questSupId : null
    },
    workflowTabDisabled () {
      // DRAFT:拟定;PUBLISH:已发布;WRITING:填写中;WRITED:已填写;SUBMITTED:已提交;REJECTED:已驳回;APPROVED:已审批;ABANDONED:已废弃;WITHDRAW:已撤回;PRE_PASS:预审通过;PRE_REJECTED:预审驳回
      return (
        this.form.approvalStatus === 'DRAFT' ||
        this.form.approvalStatus === 'PUBLISH' ||
        this.form.approvalStatus === 'WRITING' ||
        this.form.approvalStatus === 'WRITED'
      )
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = false

      this.buttonCustom.submit.view = this.viewUpdateButton
    },
    viewPrePassButton (newValue) {
      this.buttonCustom.prePassData.view = newValue
    },
    viewPreRejectedButton (newValue) {
      this.buttonCustom.preRejectedData.view = newValue
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton

      this.buttonCustom.submit.disabled = this.disabledUpdateButton
    },
    disabledPrePassButton () {
      this.buttonCustom.prePassData.disabled = this.disabledPrePassButton
    },
    disabledPreRejectedButton () {
      this.buttonCustom.preRejectedData.disabled = this.disabledPreRejectedButton
    }
  },
  async created () {
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'add') {
      const { phone, nickname, username, ceeaDeptId, department } =
        this.$store.getters.user.userInfo
      this.form.createdFullName = nickname
    }
    const { flag, row, readOnly = false } = this.$attrs.params
    this.investigationCode = row.questNo
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'view' || flag === 'approvalOnly') {
      await this.getSupplierResultDetail(row.questSupId)
    }
    console.log('【integrationMode】', this.workflowParamsInfo.integrationMode)
    // 屏蔽原来的暂存和提交按钮
    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = false
    this.buttonCustom.submit.view = this.viewUpdateButton
    this.buttonCustom.preRejectedData.view = this.viewPreRejectedButton
    this.buttonCustom.prePassData.view = this.viewPrePassButton

    this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton

    this.buttonCustom.submit.disabled = this.disabledUpdateButton
    this.buttonCustom.preRejectedData.disabled = this.disabledPreRejectedButton
    this.buttonCustom.prePassData.disabled = this.disabledPrePassButton

    this.buttonConfigInfo.cancel.view = !this.readOnly
    this.buttonConfigInfo.close.view = this.readOnly
    console.log('created:', this.buttonConfigInfo, this.buttonCustom)
  },
  mounted () {},
  methods: {
    async getWorkflowBusinessType () {
      return 'questResultApprove'
    },
    async getWorkflowBusinessVariables () {
      return {
        formNo: this.investigationCode
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },

    async validateForm () {
      return true
    },

    deleteRowField (index, propArr) {
      propArr.splice(index, 1)
    },
    // 更新审批状态
    updateApprovalStatusDataHandle (type) {
      this.form.approvalStatus = type
      this.$http({
        url: '/api-sup/quest/questSupplier/flow/updateQuestSupplierApprovalStatus',
        method: 'POST',
        data: this.form,
        loading: true
      })
        .then(async (res) => {
          this.$message({
            type: 'success',
            message: this.$t('common.successSubmit')
          })
          await this.getSupplierResultDetail(this.form.questSupId)
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 保存数据操作
    async saveDataHandle (type) {
      console.log('saveDataHandle TYPE:', type)
      // 如果是预审,则直接更新状态
      if (type === 'prePassData') {
        this.updateApprovalStatusDataHandle('PRE_PASS')
      } else if (type === 'preRejectedData') {
        this.updateApprovalStatusDataHandle('PRE_REJECTED')
      }
      await this.getSupplierResultDetail(this.form.questSupId)
      if (type === 'submit' && this.form.approvalStatus === 'PRE_PASS') {
        await this.handlerAfter(type)
      }
    },
    async confirmDataHandle (type) {
      console.log('confirmDataHandle TYPE:', type)
      // 关闭工作流后，弹框确认
    },
    back () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'questManagementFlow')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('questManagementList.getQuerydata')
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'questManagementFlow')
      } else {
        this.$emit('tab-remove', 'questManagementFlow' + row.questSupId)
      }
      this.__setTabTodo('questManagementList.getQuerydata')
    },
    // 通过id查询供应商问卷填写内容
    async getSupplierResultDetail (questSupId) {
      this.$http({
        url: '/api-sup/quest/questResult/getQuestResultDtoByQuestSupId',
        method: 'GET',
        params: { questSupId: questSupId },
        loading: true
      })
        .then((res) => {
          let result = res.data
            console.log(result)
          // 获取单据ID复制到工作流参数中
          // this.workflowParamsInfo.businessId = result.questSupId;
          // this.workflowParamsInfo.tabDisabled = false;
          this.form.questSupId = result.questSupId
          this.form.questNo = result.questNo
          this.form.questName = result.questName
          this.form.questTemplateId = result.questTemplateId
          this.form.questTemplateType = result.questTemplateType
          this.form.questTemplateTypeName = result.questTemplateTypeName
          this.form.questTemplateOrgId = result.questTemplateOrgId
          this.form.questTemplateOrgCode = result.questTemplateOrgCode
          this.form.questTemplateOrgName = result.questTemplateOrgName
          this.form.questFeedback = result.questFeedback
          this.form.approvalStatus = result.approvalStatus
          this.form.groupInfoList = result.groupInfoList
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.fillInfoSec {
  padding: 15px 15px 50px;
  .fillInfo {
    margin-top: 20px;
  }
}
</style>
