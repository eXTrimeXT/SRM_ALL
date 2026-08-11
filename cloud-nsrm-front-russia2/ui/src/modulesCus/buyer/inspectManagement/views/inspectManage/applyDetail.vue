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
      <!-- <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="INSPECT_APPLY"
        :approval-status="detailForm.inspectStatus"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      > -->
        <el-form
          ref="detailForm"
          :model="detailForm"
          :disabled="isReadOnly"
        >
          <el-collapse v-model="activeNum">
            <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
              <srm-row>
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingName"
                    :label="$t('bidMod.bidingName')"
                    :rules="[{ required: true, message: this.$t('common.pleaseSelect'), trigger: ['blur', 'change'] }]"
                  >
                    <QuickSearch
                      :disable="isReadOnly"
                      :showInput="detailForm.bidingName"
                      show-key="bidingName"
                      :scope-data="detailForm"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_npm_sou_project"
                      @close-quicksearch="getBidObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 招标单号 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingNum"
                    :label="$t('cusEntry.biddingSettings.bidingNum')"
                  >
                    <el-input v-model="detailForm.bidingNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 招标专家 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingHead"
                    :label="$t('cusEntry.biddingSettings.bidChargePerson')"
                  >
                    <el-input v-model="detailForm.bidingHead" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 出行方式 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="comeType"
                    :label="$t('cusEntry.biddingSettings.travelMode')"
                  >
                    <el-input v-model="detailForm.comeType" />
                  </el-form-item>
                </srm-col>
                <!-- 公司名称 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="orgName"
                    :label="$t('vendorMod.corporateName')"
                  >
                    <el-input v-model="detailForm.orgName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 申请部门 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="departmentName"
                    :label="$t('purchaseDemand.ceeaDepartment')"
                  >
                    <el-input v-model="detailForm.departmentName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 考察单号 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="inspectNum"
                    :label="$t('cusEntry.biddingSettings.inspectBill')"
                  >
                    <el-input v-model="detailForm.inspectNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 招标部是否参加 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingDepartmentFlag"
                    :label="$t('cusEntry.biddingSettings.ifParticipate')"
                  >
                    <dict-select v-model="detailForm.bidingDepartmentFlag" code="YES_OR_NO" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <!-- <el-form-item
                    prop="bidingTechHead"
                    label="技术专家"
                  > -->
                  <el-form-item
                    prop="bidingTechHead"
                    :label="$t('cusEntry.bidSuperviseReport.extTechPrincipal')"
                  >
                    <el-input v-model="detailForm.bidingTechHead" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 拟参加人员 -->
                <srm-col :init-col="1">
                  <el-form-item
                    prop="comment"
                    :label="$t('cusEntry.biddingSettings.intendedPerson')"
                    :rules="[{ required: true, message: this.$t('common.pleaseInput'), trigger: ['blur', 'change'] }]"
                  >
                    <el-input v-model="detailForm.comment" />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="detailForm.inspectStatus==='ABANDON'" :init-col="1">
                  <!-- <el-form-item label="废弃说明"> -->
                  <el-form-item :label="$t('cusEntry.supplement20250121.reasonDesc')">
                    <el-input v-model="detailForm.reasonDesc" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('cusEntry.biddingSettings.inspectionUnit')" name="2">
              <el-button
                type="primary"
                style="margin-bottom: 10px;"
                :disabled="isReadOnly"
                @click="addItem"
              >
                {{ $t('common.add') }}
              </el-button>
              <el-table
                border
                max-height="300px"
                :data="vendorList"
              >
                <el-table-column
                  align="center"
                  type="index"
                  fixed="left"
                  :label="$t('common.sort')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('common.vendorName')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :disable="isReadOnly"
                      :showInput="scope.row.vendorName"
                      show-key="vendorName"
                      :scope-data="scope"
                      :placeholder="$t('common.pleaseSelect')"
                      :pre-query-data="preQueryData"
                      name="scc_npm_inspect_vendor"
                      @close-quicksearch="getVendorObj"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="extVendorLinkMan"
                  :label="$t('vendorMod.vendorContact')"
                  minWidth="150"
                >
                  <!-- <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.extVendorLinkMan"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template> -->
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="extVendorLinkPhone"
                  :label="$t('contractMod.mobileNumber')"
                  minWidth="150"
                >
                  <!-- <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.extVendorLinkPhone"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template> -->
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="inspectAddress"
                  :label="$t('cusEntry.common.location')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.inspectAddress"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="inspectCause"
                  :label="$t('cusEntry.biddingSettings.inspectionReason')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.inspectCause"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="inspectTime"
                  :label="$t('cusEntry.biddingSettings.inspectionTime')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.inspectTime"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="inspectContent"
                  :label="$t('cusEntry.biddingSettings.inspectionContent')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.inspectContent"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  align="center"
                  prop="comprehensiveEvaluation"
                  :label="$t('cusEntry.biddingSettings.comprehensiveOp')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.comprehensiveEvaluation"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column> -->
                <el-table-column
                  align="center"
                  :label="$t('common.operation')"
                  fixed="right"
                  width="80"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="deleteItem(scope.$index,scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- <el-collapse-item title="参与人员" name="3"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250205.participants')" name="3">
              <el-button
                type="primary"
                style="margin-bottom: 10px;"
                :disabled="isReadOnly"
                @click="addRow"
              >
                {{ $t('common.add') }}
              </el-button>
              <el-table
                border
                max-height="300px"
                :data="userList"
              >
                <el-table-column
                  align="center"
                  type="index"
                  fixed="left"
                  :label="$t('common.sort')"
                  width="50"
                />
                <!-- <el-table-column
                  align="center"
                  prop="username"
                  label="姓名"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                > -->
                <el-table-column
                  align="center"
                  prop="username"
                  :label="$t('vendorMod.nickname')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :disable="isReadOnly"
                      :showInput="scope.row.username"
                      show-key="nickname"
                      :scope-data="scope"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj"
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  align="center"
                  prop="jobDesc"
                  label="岗位"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="jobDesc"
                  :label="$t('components.orgPositionSel.position')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="company"
                  label="所属单位"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="company"
                  :label="$t('cusEntry.competition.belongCompany')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="department"
                  label="部门"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="department"
                  :label="$t('cusEntry.centralizedPurchase.department')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="bidFlag"
                  label="是否评标组成员"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                > -->
                <el-table-column
                  align="center"
                  prop="bidFlag"
                  :label="$t('cusEntry.supplement20250205.isBidEvaluationGroupMember')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.bidFlag"
                      code="YES_OR_NO"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  align="center"
                  prop="responsibility"
                  label="负责内容"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                > -->
                <el-table-column
                  align="center"
                  prop="responsibility"
                  :label="$t('cusEntry.supplement20250205.responsibleContent')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.responsibility"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  :label="$t('common.operation')"
                  fixed="right"
                  width="80"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="deleteRow(scope.$index,scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      <!-- </ApprovalProcess> -->
      <!-- </CWorkflowMulti> -->
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button @click="goBack">
          {{ $t('common.backTo') }}
        </el-button>
        <el-button
          v-if="viewUpdateButton"
          type="primary"
          @click="saveOrSubmitBill('SAVE')"
        >
          {{ $t('flowMod.temporaryView') }}
        </el-button>
        <el-button
          v-if="viewUpdateButton"
          type="primary"
          @click="saveOrSubmitBill('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>
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
  name: 'InspectApplyDetail',
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
        inspectStatus: 'DRAFT',
        bidingTechHead: null,
        bidingName: null,
        bidingNum: null,
        bidingHead: null,
        comeType: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        departmentId: null,
        departmentCode: null,
        departmentName: null,
        inspectNum: null,
        bidingDepartmentFlag: null,
        comment: null,
        reasonDesc: null,
        comprehensiveEvaluation: null,
        leaderEvaluation: null
      },
      preQueryData: { 't.project_id': null },
      vendorList: [],
      vendorListDelete: [],
      userList: [],
      userListDelete: [],
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'commonFile', // 功能
        fileType: 'images' // 类型
      },
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPLY_APPROVING', // 已提交
        APPROVED: 'APPLY_APPROVED', // 审批通过
        REJECTED: 'APPLY_REJECTED', // 已驳回
        WITHDRAW: 'APPLY_WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.detailForm ? this.detailForm.inspectId : null
    },
    workflowTabDisabled () {
      // 拟定状态的单据 流程审批按钮失效
      return ['DRAFT'].includes(this.detailForm.inspectStatus)
    },
    viewUpdateButton () {
      return ['DRAFT', 'APPLY_WITHDRAW', 'APPLY_REJECTED'].includes(this.detailForm.inspectStatus) && !this.isReadOnly
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPLY_APPROVING'].includes(this.detailForm.inspectStatus) &&
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
  async created () {
    const { flag, row } = this.$attrs.params
    const { username } = this.$store.getters.userInfo
    this.isReadOnly = ['view', 'approval'].includes(flag)
    if (flag === 'add') {
      this.getUserCompany(username)
    } else {
      this.getFormDetail(row.inspectId)
    }

    // this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    // this.buttonConfigInfo.save.view = this.viewUpdateButton
    // this.buttonConfigInfo.submit.view = this.viewUpdateButton
    // this.buttonConfigInfo.cancel.view = !this.isReadOnly
    // this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let validForm = false
      await this.$refs.detailForm.validate(valid => {
        validForm = valid
      })
      if (!validForm) {
        // this.$message.warning('请填写完必填项')
        this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseCompleteTheRequiredFields'))
        return false
      }
      if (this.vendorList.length == 0) {
        // this.$message.warning('至少维护一条考察单位数据')
        this.$message.warning(this.$t('cusEntry.supplement20250205.maintainAtLeastOneEvaluationUnitData'))
        return false
      }
      let flag = this.vendorList.some(item => !item.vendorName || !item.extVendorLinkMan || !item.extVendorLinkPhone || !item.inspectAddress || !item.inspectCause || !item.inspectTime || !item.inspectContent)
      if (flag) {
        // this.$message.warning('请维护考察单位必填项')
        this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseMaintainEvaluationUnitRequiredFields'))
        return false
      }
      let userFlag = this.userList.some(item => !item.username || !item.bidFlag || !item.responsibility)
      if (userFlag) {
        // this.$message.warning('请维护参与人员必填项')
        this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseMaintainParticipantsRequiredFields'))
        return false
      }
      // 调用暂存接口
      const params = {
        ...this.detailForm,
        userList: [...this.userList, ...this.userListDelete],
        vendorList: [...this.vendorList, ...this.vendorListDelete]
      }
      const saveData = transformMQL.save('Inspect', [params], 'saveOrUpdate')
      const response = await this.$http({
        url: '/api-sou/api-ql/Inspect/saveOrUpdate',
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
        this.saveInspect('SAVE')
        break
      case 'submit':
        this.goBack()
        break
      default:
        break
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 指定工作流的业务类型，在定义工作流时指定
    getWorkflowBusinessType () {
      return 'INSPECT_APPLY' // 这里之后是和后端对接配置好的流程审批模块
    },
    goBack () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'inspectApplyDetail' + this.$attrs.params.row.inspectId)
      } else {
        this.$emit('tab-remove', 'inspectApplyDetail')
      }
      this.__setTabTodo('inspectManageList.getQueryData')
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
    addRow () {
      this.userList.push({
        userId: null,
        username: null,
        jobDesc: null,
        company: null,
        department: null,
        bidFlag: null,
        responsibility: null
      })
    },
    deleteRow (index, row) {
      if (row.inspectUserId) {
        this.userListDelete.push({ '$delete': row.inspectUserId })
      }
      this.userList.splice(index, 1)
    },
    getUserObj (val, scope) {
      scope.row.username = val ? val.nickname : ''
      scope.row.userId = val ? val.userId : ''
      scope.row.jobDesc = val ? val.ceeaJobcodeDescr : ''
      scope.row.company = val ? val.ceeaCompanyDescr : ''
      scope.row.department = val ? val.department : ''
    },
    getBidObj (val, scope) {
      scope.bidingName = val ? val.souName : '' // 招标项目名称
      scope.bidingNum = val ? val.extProjectNo : '' // 招标项目编号
      scope.bidingId = val ? val.projectId : '' // 招标项目id
      scope.bidingHead = val ? val.extSouPrincipal : '' // 招标专家
      scope.bidingTechHead = val ? val.extTechPrincipal : '' // 技术专家
      this.preQueryData = val ? { 't.project_id': val.projectId } : { 't.project_id': null }
    },
    addItem () {
      if (!this.detailForm.bidingId) {
        // this.$message.warning('请先选择项目名称')
        this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseSelectProjectNameFirst'))
        return
      }
      this.vendorList.push({
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        extVendorLinkMan: null,
        extVendorLinkPhone: null,
        inspectAddress: null,
        inspectCause: null,
        inspectTime: null,
        inspectContent: null
      })
    },
    // 删除
    deleteItem (index, row) {
      if (row.inspectVendorId) {
        this.vendorListDelete.push({ '$delete': row.inspectVendorId })
      }
      this.vendorList.splice(index, 1)
    },
    getVendorObj (val, scope) {
      scope.row.vendorId = val ? val.vendorId : ''
      scope.row.vendorCode = val ? val.vendorCode : ''
      scope.row.vendorName = val ? val.vendorName : ''
      scope.row.extVendorLinkMan = val ? val.linkmanName : ''
      scope.row.extVendorLinkPhone = val ? val.phone : ''
    },
    getFormDetail (inspectId) {
      const searchData = transformMQL.save(
        'Inspect',
        [inspectId],
        'read',
        {
          '*': {},
          'userList': { '*': {} },
          'vendorList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sou/api-ql/Inspect/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { vendorList = [], userList = [], ...rest } = res.data[0]
          this.detailForm = { ...rest }
          this.vendorList = vendorList
          this.userList = userList
        }
      })
    },
    saveOrSubmitBill (type) { // 点击 提交审批或保存按钮
      if (type === 'SUBMIT') {
        this.publishInspect(type)
      } else if (type === 'SAVE') {
        this.saveInspect(type)
      }
    },
    saveInspect (type) {
      const params = {
        ...this.detailForm,
        inspectStatus: type === 'SUBMIT' ? 'APPLY_APPROVED' : this.detailForm.inspectStatus,
        userList: [...this.userList, ...this.userListDelete],
        vendorList: [...this.vendorList, ...this.vendorListDelete]
      }
      const saveData = transformMQL.save('Inspect', [params], 'saveOrUpdate')
      this.$http({
        url: '/api-sou/api-ql/Inspect/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(async res => {
        this.detailForm = res.data[0]
        this.getFormDetail(this.detailForm.inspectId)
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.successSave'))
          this.__setTabTodo('inspectManageList.getQueryData')
        } else {
          // await this.handlerAfter(type, 'Y', () => {
          //   this.__setTabTodo('inspectManageList.getQueryData')
          // })
          this.$message.success(this.$t('common.successSubmit'))
          this.goBack()
        }
      })
    },
    publishInspect (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          if (this.vendorList.length == 0) {
            // this.$message.warning('至少维护一条考察单位数据')
            this.$message.warning(this.$t('cusEntry.supplement20250205.maintainAtLeastOneEvaluationUnitData'))
            return
          }
          let flag = this.vendorList.some(item =>
            !item.vendorName || !item.extVendorLinkMan || !item.extVendorLinkPhone || !item.inspectAddress || !item.inspectCause || !item.inspectTime || !item.inspectContent)
          if (flag) {
            // this.$message.warning('请维护考察单位必填项')
            this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseMaintainEvaluationUnitRequiredFields'))
            return
          }
          let userFlag = this.userList.some(item => !item.username || !item.bidFlag || !item.responsibility)
          if (userFlag) {
            // this.$message.warning('请维护参与人员必填项')
            this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseMaintainParticipantsRequiredFields'))
            return
          }
          this.saveInspect(type)
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
